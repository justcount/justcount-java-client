package io.justcount;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.api.core.SettableApiFuture;
import com.google.api.gax.batching.BatchingSettings;
import com.google.api.gax.core.CredentialsProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PubsubMessage;
import org.threeten.bp.Duration;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class PubSubClient {

    public static class Options {

        public Options(String keyFilename) {
            this.keyFilename = keyFilename;
        }

        public String keyFilename;
        public String topic = "justcount";
        public Batching batching = new Batching();

        public static class Batching {
            public Long delayThresholdMilliseconds = 0L;
            public Long elementCountThreshold = 0L;
            public Long requestByteThreshold = 0L;
        }

    }

    private Publisher publisher;
    private Gson gson;
    private Set<Operation> unsentOperations = new HashSet<Operation>();
    private SettableApiFuture<Boolean> closeFuture;

    public PubSubClient(String keyFilename) throws IOException {
        this(new PubSubClient.Options(keyFilename));
    }

    public PubSubClient(PubSubClient.Options options) throws IOException {
        this.gson = new Gson();

        FileReader keyFileReader = new FileReader(options.keyFilename);
        Map<String, Object> retMap = this.gson.fromJson(
                keyFileReader,
                new TypeToken<HashMap<String, Object>>() {}.getType()
        );
        String project = (String) retMap.get("project_id");

        // Topic name
        ProjectTopicName topicName = ProjectTopicName.of(project, options.topic);
        Publisher.Builder builder = Publisher.newBuilder(topicName);

        // Auth
        final GoogleCredentials credentials = GoogleCredentials
                .fromStream(new FileInputStream(options.keyFilename))
                .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));

        builder.setCredentialsProvider(new CredentialsProvider() {
            public Credentials getCredentials() throws IOException {
                return credentials;
            }
        });

        // Batching
        BatchingSettings.Builder batchingSettingsBuilder = BatchingSettings.newBuilder();
        batchingSettingsBuilder.setIsEnabled(true);
        if (options.batching.delayThresholdMilliseconds > 0) {
            batchingSettingsBuilder.setDelayThreshold(Duration.ofMillis(options.batching.delayThresholdMilliseconds));
        }
        if (options.batching.elementCountThreshold > 0) {
            batchingSettingsBuilder.setElementCountThreshold(options.batching.elementCountThreshold);
        }
        if (options.batching.requestByteThreshold > 0) {
            batchingSettingsBuilder.setRequestByteThreshold(options.batching.requestByteThreshold).build();
        }
        builder.setBatchingSettings(batchingSettingsBuilder.build());

        // Build the publisher
        this.publisher = builder.build();
    }

    public synchronized ApiFuture<Boolean> send(final Operation[] operations) {
        final SettableApiFuture<Boolean> result = SettableApiFuture.<Boolean>create();

        if (this.closeFuture != null) {
            result.set(false);
            return result;
        }

        String json = gson.toJson(new Operation.Bulk(operations));
        PubsubMessage message = PubsubMessage.newBuilder()
                .setData(ByteString.copyFromUtf8(json))
                .build();
        ApiFuture<String> messageIdFuture = this.publisher.publish(message);

        final Collection<Operation> operationsColl = Arrays.asList(operations);
        unsentOperations.addAll(operationsColl);

        ApiFutures.addCallback(messageIdFuture, new ApiFutureCallback<String>() {
            public void onFailure(Throwable throwable) {
                synchronized (PubSubClient.this) {
                    result.set(false);
                    unsentOperations.removeAll(operationsColl);
                    PubSubClient.this.onOperationsSent();
                }
            }

            public void onSuccess(String s) {
                synchronized (PubSubClient.this) {
                    result.set(true);
                    unsentOperations.removeAll(operationsColl);
                    PubSubClient.this.onOperationsSent();
                }
            }
        });

        return result;
    }

    private void onOperationsSent() {
        if (this.closeFuture != null && unsentOperations.size() == 0) {
            this.closeFuture.set(true);
        }
    }

    public synchronized ApiFuture<Boolean> close() throws Exception {
        if (this.closeFuture != null) {
            return this.closeFuture;
        }
        this.closeFuture = SettableApiFuture.<Boolean>create();

        this.publisher.shutdown();

        if (this.unsentOperations.size() == 0) {
            this.closeFuture.set(true);
        }

        return this.closeFuture;
    }

}
