package io.justcount;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface Client {

    CompletableFuture<Void> send(Collection<Operation> operations);

    void close() throws Exception;

}
