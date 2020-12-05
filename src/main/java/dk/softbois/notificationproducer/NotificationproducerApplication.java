package dk.softbois.notificationproducer;

import dk.softbois.notificationproducer.rabbitmq.RabbitMQ;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NotificationproducerApplication {

    public static void main(String[] args) {

        SpringApplication.run(NotificationproducerApplication.class, args);
    }

}
