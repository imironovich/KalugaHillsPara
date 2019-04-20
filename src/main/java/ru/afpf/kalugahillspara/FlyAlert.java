package ru.afpf.kalugahillspara;

import java.sql.*;
import java.time.LocalDate;
import java.util.TimerTask;

public class FlyAlert extends TimerTask {
    final String sqlpasswd;
    final String sqlurl;
    final String sqluser;
    final String chatId;
    Bot bot;

    public FlyAlert(String DB_PASSWD, String DB_USER, String SQLURL, String CHAT_ID, String BOT_NAME, String BOT_TOKEN) {
        sqlpasswd = DB_PASSWD;
        sqluser = DB_USER;
        sqlurl = SQLURL;
        chatId = CHAT_ID;

        bot = new Bot(BOT_NAME, BOT_TOKEN);
    }


    public void run() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Unable to load class.");
            e.printStackTrace();
        }

        try (Connection connection = DriverManager.getConnection(sqlurl, sqluser, sqlpasswd)) {
            LocalDate daten = LocalDate.now();


            LocalDate thisDay = daten.plusDays(1);
            Statement stmt = connection.createStatement();
            boolean nigde = true;

            ResultSet result = stmt.executeQuery(
                    "SELECT date, hour FROM weatherIsFlying WHERE date > '" + thisDay + "' AND flying = '" + 1 + "'");

            System.out.println("Желохово:");
            while (result.next()) {
                System.out.println(result.getString(1) + " " + result.getString(2));
                bot.sendMess(Integer.parseInt(chatId), "Желохово: " + result.getString(1) + " " + result.getString(2));
                nigde = false;
            }

            ResultSet resultV = stmt.executeQuery(
                    "SELECT date, hour FROM weatherIsFlying WHERE date > '" + thisDay + "' AND flying = '" + 2 + "'");

            System.out.println("Вороново:");
            while (resultV.next()) {
                System.out.println(resultV.getString(1) + " " + resultV.getString(2));
                bot.sendMess(Integer.parseInt(chatId), "Вороново: " + resultV.getString(1) + " " + resultV.getString(2));
                nigde = false;
            }

            if (nigde){ System.out.println("К сожалению летной погоды на калужских горках в ближайшее время не будет :-(");
                bot.sendMess(Integer.parseInt(chatId), "К сожалению летной погоды на калужских горках в ближайшее время не будет :-(");
                }

            } catch(SQLException e){
                e.printStackTrace();
            }
        }

    }

