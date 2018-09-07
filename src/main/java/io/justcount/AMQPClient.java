package io.justcount;
import com.google.api.core.ApiFuture;
import com.google.gson.Gson;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AMQPClient {
    public static class Options {
        public String host = "127.0.0.1";
        public int port = 5672;
        public String username;
        public String password;
        public String virtualHost;
        public String queue = "restats";
    }

    private Options options;
    private Channel channel;
    private Connection connection;
    private Gson gson;

    public AMQPClient(Options options) {
        this.options = options;
        this.gson = new Gson();
    }

    private void connect() throws IOException, TimeoutException {
        // Already have a connected channel ?
        if (null != channel && channel.isOpen()) return;

        Connection conn;
        // No connection yet ?
        if (null == connection || !connection.isOpen()) {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(this.options.host);
            factory.setPort(this.options.port);
            if (this.options.username != null) factory.setUsername(this.options.username);
            if (this.options.password != null) factory.setPassword(this.options.password);
            if (this.options.virtualHost != null) factory.setVirtualHost(this.options.virtualHost);
            connection = factory.newConnection();
        }
        conn = connection;
        this.channel = conn.createChannel();
        this.channel.queueDeclarePassive(this.options.queue);
    }

    public void send(final Collection<Operation> operations) throws IOException, TimeoutException {
        String json = gson.toJson(new Operation.Bulk(operations));
        connect();
        channel.basicPublish("", this.options.queue, null, json.getBytes("UTF-8"));
    }

    public void close() throws IOException, TimeoutException {
        if (null != this.channel) {
            Connection conn = this.channel.getConnection();
            this.channel.close();
            if (null != conn) conn.close();
        }
    }
}