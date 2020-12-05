package dk.softbois.notificationproducer.rabbitmq;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import dk.softbois.notificationproducer.models.Phone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

public class RabbitMQ {

    private final static String EXCHANGE_FLIGHT_UPDATES = "EXCHANGE_FLIGHT_UPDATES";
    private final static String EXCHANGE_GLOBAL_NOTIFICATION = "EXCHANGE_GLOBAL_NOTIFICATION";

    public RabbitMQ(List<Phone> phones) {
        initServers(phones);
    }

    private void initServers(List<Phone> phones) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
//                channel.queueDeclare(QUEUE_NAME, false, false, false, null);
//                channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null, message.getBytes("UTF-8"));
            channel.exchangeDeclare(EXCHANGE_FLIGHT_UPDATES, "direct", true);
            channel.exchangeDeclare(EXCHANGE_GLOBAL_NOTIFICATION, "fanout", true);

            for (var phone : phones) {
                var id = phone.getId();
                channel.queueDeclare(id, true, false, false, null);


                channel.queueBind(id, EXCHANGE_FLIGHT_UPDATES, id);
                channel.queueBind(id, EXCHANGE_GLOBAL_NOTIFICATION, id);


            }

        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendFlightUpdate(List<String> mobileIds, String message) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            for (String mobileId: mobileIds) {
                System.out.println("Publishing...");
                channel.basicPublish(EXCHANGE_FLIGHT_UPDATES, mobileId, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8"));
            }

        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void addQueue(String id) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
//                channel.queueDeclare(QUEUE_NAME, false, false, false, null);
//                channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null, message.getBytes("UTF-8"));
            channel.exchangeDeclare(EXCHANGE_FLIGHT_UPDATES, "direct", true);
            channel.exchangeDeclare(EXCHANGE_GLOBAL_NOTIFICATION, "fanout", true);

            System.out.println(id);
            channel.queueDeclare(id, true, false, false, null);


            channel.queueBind(id, EXCHANGE_FLIGHT_UPDATES, id);
            channel.queueBind(id, EXCHANGE_GLOBAL_NOTIFICATION, id);

        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcastNotification(String message){
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
                channel.basicPublish(EXCHANGE_GLOBAL_NOTIFICATION, "" , MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8"));

        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
