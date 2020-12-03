package ru.malichenko.rabbitmq.console.consumer;

import com.rabbitmq.client.*;

import java.util.Scanner;

public class ExchangeReceiverApp {
    private static final String EXCHANGE_NAME = "news_exchanger";
    private static final String HELP = "/help\n" +
            "/subscribe <THEME> - subscribe\n" +
            "/unsubscribe <THEME> - unsubscribe from theme\n" +
            "/exit - close app";

    interface Callback {
        void run();
    }

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        String queueName = channel.queueDeclare().getQueue();
        String defaultRoutingKey = ".java";
        channel.queueBind(queueName, EXCHANGE_NAME, defaultRoutingKey);

        listenCommand(channel, queueName, () -> System.exit(0));

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] New article: %n%s" + message);
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });
    }

    private static void listenCommand(Channel channel, String queueName, Callback callback) {
        Scanner reader = new Scanner(System.in);
        new Thread(() -> {
            try {
                boolean isRun = true;
                System.out.println(HELP);
                while (isRun) {
                    String command = reader.nextLine();
                    String[] splitCommand = command.split("\\s+", 2);
                    switch (splitCommand[0]) {
                        case "/help":
                            System.out.println(HELP);
                            break;
                        case "/subscribe":
                            channel.queueBind(queueName, EXCHANGE_NAME, "#." + splitCommand[1]);
                            System.out.println(" [x] You subscribed to theme " + splitCommand[1]);
                            break;
                        case "/unsubscribe":
                            channel.queueUnbind(queueName, EXCHANGE_NAME, "#." + splitCommand[1]);
                            System.out.println(" [x] You unsubscribed to theme " + splitCommand[1]);
                            break;
                        case "/exit":
                            isRun = false;
                            break;
                    }
                }
                callback.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
