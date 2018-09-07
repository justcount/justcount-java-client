package io.justcount;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.Collection;
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
        this.channel.queueDeclarePassive(this.options.queue);
    }

    public void send(final Collection<Operation> operations) throws IOException, TimeoutException {
        String json = gson.toJson(new Operation.Bulk(operations));
        connect();
        channel.basicPublish(
                "", // exchange
                this.options.queue,
                true,  // mandatory
                false, // immediate
                MessageProperties.MINIMAL_PERSISTENT_BASIC,
                json.getBytes("UTF-8")
        );
    }

    public void close() throws IOException, TimeoutException {
        if (null != this.channel) {
            this.channel.close();
            this.channel = null;
        }
        if (null != this.connection) {
            this.connection.close();
            this.connection = null;
        }
    }

}
