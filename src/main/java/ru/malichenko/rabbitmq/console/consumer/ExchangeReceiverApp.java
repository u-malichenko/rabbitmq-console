package ru.malichenko.rabbitmq.console.consumer;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class ExchangeReceiverApp {
    private static final String EXCHANGE_NAME = "progExchanger";
    private static final ConnectionFactory factory = new ConnectionFactory();
    private static HashMap<String, String> listQueueName = new HashMap<>();

    public static void main(String[] argv) throws Exception {
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        Scanner sc = new Scanner(System.in);
        System.out.print("Input a number 1 - 5(all): ");
        while (sc.hasNextInt()) {
            int typing = sc.nextInt();
            switch (typing) {
                case 1:
                    ChannelNews("*.java", channel);
                    break;
                case 11:
                    unbinding("*.java", channel);
                    break;
                case 2:
                    ChannelNews("*.php", channel);
                    break;
                case 22:
                    unbinding("*.php", channel);
                    break;
                case 3:
                    ChannelNews("*.piton", channel);
                    break;
                case 33:
                    unbinding("*.piton", channel);
                    break;
                case 4:
                    ChannelNews("*.c++", channel);
                    break;
                case 44:
                    unbinding("*.c++", channel);
                    break;
                case 5:
                    ChannelNews("prog.*", channel);
                    break;
                case 55:
                    unbinding("prog.*", channel);
                    break;
                default:
                    return;
            }
        }
        sc.close();
    }

    private static void ChannelNews(String key, Channel channel) throws Exception {
        String queueName = channel.queueDeclare().getQueue();
        listQueueName.put(key, queueName);
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

    private static void unbinding(String key, Channel channel) throws Exception {
        System.out.println(" Unbind queue name: " + listQueueName.get(key));
        if(!listQueueName.containsKey(key)) return;
        channel.queueUnbind(listQueueName.get(key), EXCHANGE_NAME, key);
        listQueueName.remove(key);
    }
}
