```
PubSubClient.Options clientOptions = new PubSubClient.Options("/path/to/pubsub-pubconsumer.json");
PubSubClient client = new PubSubClient(clientOptions);
Operation[] operations = {
        Operation.Builder.AddInt(Metric.from("testRealm:testBackend:testMetric"), 1)
                .setParam("testParam", "testParamValue")
                .build(),
};
ApiFuture<Boolean> sendFuture = client.send(operations);
ApiFutures.addCallback(sendFuture, new ApiFutureCallback<Boolean>() {
    public void onFailure(Throwable throwable) {
        System.out.println("Send error "+throwable.toString());
    }

    public void onSuccess(Boolean aBoolean) {
        System.out.println("Sent success: " + (aBoolean ? "true" : "false"));
    }
});
ApiFuture<Boolean> closeFuture = client.close();
ApiFutures.addCallback(closeFuture, new ApiFutureCallback<Boolean>() {
    public void onFailure(Throwable throwable) {
        System.out.println("Close error "+throwable.toString());
    }

    public void onSuccess(Boolean aBoolean) {
        System.out.println("Close success: " + (aBoolean ? "true" : "false"));
    }
});
closeFuture.get();
```

To authenticate with Application Default Credentials (e.g. the GCE VM service account) instead of a key file,
omit the key file and set the project holding the topic:

```
PubSubClient.Options clientOptions = new PubSubClient.Options();
clientOptions.project = "my-gcp-project";
PubSubClient client = new PubSubClient(clientOptions);
```

When a key file is given, `project` is optional and defaults to the key file's `project_id`.
