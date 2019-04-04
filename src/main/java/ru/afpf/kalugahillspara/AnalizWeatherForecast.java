package ru.afpf.kalugahillspara;


import java.sql.*;
import java.util.*;
import java.time.LocalDate;

public class AnalizWeatherForecast extends TimerTask {
    final String sqlpasswd;
    final String sqlurl;
    final String sqluser;
    public AnalizWeatherForecast(String DB_PASSWD, String DB_USER, String SQLURL) {
        sqlpasswd = DB_PASSWD;
        sqluser = DB_USER;
        sqlurl = SQLURL;
    }
        @Override
        public void run() {


            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                System.out.println("Unable to load class.");
                e.printStackTrace();
            }


            String sqlFlying = "REPLACE INTO weatherIsFlying (date, hour, flying) Values (?,?,?)";

            try (Connection connection = DriverManager.getConnection(sqlurl, sqluser, sqlpasswd)) {
                LocalDate daten = LocalDate.now();

                for (int i = 0; i < 9; i++) {
                    LocalDate thisDay = daten.plusDays(i);
                    for (int j = 0; j < 8; j++) {

                        int hours3 = (j * 3);
                        if (8 < hours3 & hours3 < 19) {
                            Statement stmt = connection.createStatement();
                            ResultSet rersult = stmt.executeQuery(
                                    "SELECT windSpeedInt, windDirInt, windBoostInt, rainInt FROM windGuru WHERE date = '" + thisDay + "' AND hour = '" + hours3 + "'");

                            if (rersult.next()) {
                                if (rersult.getInt("rainInt") == 0) {
                                    if ((rersult.getInt("windSpeedInt") > 4 && (rersult.getInt("windBoostInt") < 9)) && (200 > rersult.getInt("windDirInt")) && (rersult.getInt("windDirInt") > 160)) {
                                        System.out.println(
                                                thisDay + " " + hours3 + " " + rersult.getInt("rainInt") + " " + rersult.getInt("windSpeedInt") + " " + rersult.getInt("windSpeedInt") + " " + rersult.getInt("windBoostInt") + " " + rersult.getInt("windDirInt"));
                                        PreparedStatement preparedStatement = connection.prepareStatement(sqlFlying);
                                        preparedStatement.setObject(1, thisDay);
                                        preparedStatement.setInt(2, hours3);
                                        preparedStatement.setInt(3, 1); //ЖЕлохово
                                        preparedStatement.addBatch();
                                        preparedStatement.executeBatch();
                                    }
                                    if ((rersult.getInt("windSpeedInt") > 5 && (rersult.getInt("windBoostInt") < 11)) && (290 > rersult.getInt("windDirInt")) && (rersult.getInt("windDirInt") > 250)) {
                                        System.out.println(
                                                thisDay + " " + hours3 + " " + rersult.getInt("rainInt") + " " + rersult.getInt("windSpeedInt") + " " + rersult.getInt("windSpeedInt") + " " + rersult.getInt("windBoostInt") + " " + rersult.getInt("windDirInt"));
                                        PreparedStatement preparedStatement = connection.prepareStatement(sqlFlying);
                                        preparedStatement.setObject(1, thisDay);
                                        preparedStatement.setInt(2, hours3);
                                        preparedStatement.setInt(3, 2); //Вороново
                                        preparedStatement.addBatch();
                                        preparedStatement.executeBatch();
                                    }
                                } else {
                                    PreparedStatement preparedStatement = connection.prepareStatement(sqlFlying);
                                    preparedStatement.setObject(1, thisDay);
                                    preparedStatement.setInt(2, hours3);
                                    preparedStatement.setInt(3, 0);
                                    preparedStatement.addBatch();
                                    preparedStatement.executeBatch();
                                }
                            }
                        }
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();

            }

        }

}

