package Main;

import Entity.ParserCars;
import Entity.VKBot;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        Thread threadParser = new Thread(new Runnable() {
            @Override
            public void run() {
                ParserCars parsingCars = new ParserCars();
                while(true) {
                    try {
                        parsingCars.parsing();
                    } catch (Exception ex) {
                        System.out.println("Problem in parsing");
                    }
                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        Thread threadVKBot = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    VKBot vkBot = new VKBot();
                } catch (ClientException e) {
                    e.printStackTrace();
                } catch (ApiException e) {
                    e.printStackTrace();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });
        threadParser.start();
        threadVKBot.start();
    }
}
