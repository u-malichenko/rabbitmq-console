package ru.malichenko.rabbitmq.console.producer;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.*;
// Домашнее задание:
// 1. Сделайте два консольных приложения (не Спринг):
//   а. IT-блог, который публикует статьи по языкам программирования
//   б. подписчик, которого интересуют статьи по определенным языкам
// 2. Сделайте возможность публиковать "статьи" по темам
// 3. * Сделайте возможность клиентов подписываться и отписываться от статей по темам

public class ExchangeSenderApp {
    private static final String EXCHANGE_NAME = "progExchanger";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        Scanner sc = new Scanner(System.in);
        console(sc, channel);
        System.out.println("Test is done");

        sc.close();
        channel.close();
    }

    public static void console(Scanner sc, Channel channel) throws IOException {
        HashMap<Integer, String> listMessages = new HashMap<>();
        listMessages.put(1, "java|javajavajavajavajavajavajavajava");
        listMessages.put(2, "php|phpphpphpphpphpphpphpphpphpphp");
        listMessages.put(3, "piton|pitonpitonpitonpitonpitonpiton");
        listMessages.put(4, "c++|c++c++c++c++c++c++c++c++");
        System.out.print("Input post or number 1 - 5(exit): ");
        while (true) {
            String typing = sc.nextLine();
            switch (typing) {
                case "1":
                case "2":
                case "3":
                case "4":
                    sender(channel, listMessages.get(Integer.parseInt(typing)));
                    System.out.print("Input post or number 1 - 5(exit): ");
                    break;
                case "5":
                    System.out.println("goodbye");
                    return;
                default:
                    sender(channel, typing);
                    System.out.print("Input post or number 1 - 5(exit): ");
            }
        }
    }

    public static void sender(Channel channel, String s) throws IOException {
        String[] msg = s.split("\\|");
        if(msg.length<2) return;
        channel.basicPublish(EXCHANGE_NAME, "prog." + msg[0], null, msg[1].getBytes("UTF-8"));
        System.out.println(" [x] Sent '" + msg[0] + "'");

    }
}