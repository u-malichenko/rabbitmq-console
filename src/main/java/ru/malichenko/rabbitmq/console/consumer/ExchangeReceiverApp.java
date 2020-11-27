package ru.malichenko.rabbitmq.console.consumer;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class ExchangeReceiverApp {
    private static final String EXCHANGE_NAME = "progExchanger";
    private static final ConnectionFactory factory = new ConnectionFactory();

    public static void main(String[] argv) throws Exception {
//        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

//        Connection connection = factory.newConnection();
//        Channel channel = connection.createChannel();
//        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        Scanner sc = new Scanner(System.in);
        console(sc);

        sc.close();
    }

    public static void console(Scanner sc) throws Exception {
        System.out.print("Input a number 1 - 5: ");
        while (sc.hasNextInt()) {
        int typing = sc.nextInt();
        switch (typing) {
            case 1:
                ChannelNews("*.java");
                break;
            case 2:
                ChannelNews("*.php");
                break;
            case 3:
                ChannelNews("*.piton");
                break;
            case 4:
                ChannelNews("*.c++");
                break;
            case 5:
                ChannelNews("prog.*");
                break;
            default:
                return;
        }
        }
    }

    private static void ChannelNews(String key) throws Exception {
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        String queueName = channel.queueDeclare().getQueue();
        System.out.println(" My queue name: " + queueName);
        channel.queueBind(queueName, EXCHANGE_NAME, key);
        System.out.println(" [*] Waiting for messages");
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });
    }
}
