package io.justcount;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

public class AMQPClient implements Client {

    public static class Options {

        public String host = "127.0.0.1";
        public int port = 5672;
        public String username;
        public String password;
        public String virtualHost = "/";
        public String exchange = "";
        public String routingKey = "justcount";

    }

    private Charset charset;
    private Options options;
    private Channel channel;
    private Connection connection;
    private Gson gson;

    public AMQPClient(Options options) {
        this.charset = Charset.forName("UTF-8");
        this.options = options;
        this.gson = new Gson();
    }

    private void connect() throws IOException, TimeoutException {
        // Already have a connected channel ?
        if (channel != null && channel.isOpen()) return;

        // No connection yet ?
        if (connection == null || !connection.isOpen()) {
            // Close any open things first before reopening any
            close();

            // New connection
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(this.options.host);
            factory.setPort(this.options.port);
            if (this.options.username != null) {
                factory.setUsername(this.options.username);
            }
            if (this.options.password != null) {
                factory.setPassword(this.options.password);
            }
            if (this.options.virtualHost != null) {
                factory.setVirtualHost(this.options.virtualHost);
            }
            connection = factory.newConnection();
        }
        this.channel = connection.createChannel();
        if (this.options.exchange.equals("")) {
            this.channel.queueDeclarePassive(this.options.routingKey);
        }
    }

    // Note: This function is synchronous but conforms to the async Client interface
    public CompletableFuture<Void> send(final Collection<Operation> operations) {
        CompletableFuture<Void> rtn = new CompletableFuture<>();
        try {
            String json = gson.toJson(new Operation.Bulk(operations));
            connect();
            channel.basicPublish(
                    this.options.exchange,
                    this.options.routingKey,
                    true,  // mandatory
                    false, // immediate
                    MessageProperties.MINIMAL_PERSISTENT_BASIC,
                    json.getBytes(this.charset)
            );
            rtn.complete(null);
        } catch (IOException | TimeoutException ex) {
            rtn.completeExceptionally(ex);
        }
        return rtn;
    }

    public void close() throws IOException, TimeoutException {
        if (this.channel != null) {
            if (this.channel.isOpen()) {
                this.channel.close();
            }
            this.channel = null;
        }
        if (this.connection != null) {
            if (this.connection.isOpen()) {
                this.connection.close();
            }
            this.connection = null;
        }
    }

}
