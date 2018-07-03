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
