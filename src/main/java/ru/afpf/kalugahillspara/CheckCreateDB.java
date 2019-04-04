package ru.afpf.kalugahillspara;

import java.sql.*;

public class CheckCreateDB {

    final String sqlpasswd;
    final String sqlurl;
    final String sqluser;

    CheckCreateDB(String DB_PASSWD, String DB_USER, String SQLCREATEURL){
        sqlpasswd = DB_PASSWD;
        sqluser = DB_USER;
        sqlurl = SQLCREATEURL;
    }




    public void createDB() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Unable to load class.");
            e.printStackTrace();
        }
        try (Connection connection = DriverManager.getConnection(sqlurl, sqluser, sqlpasswd)) {

            Statement stmt = connection.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS WindGuru");
            System.out.println("WindGuru database has successfully been created");

            stmt.executeUpdate( "create table if not exists WindGuru.windGuru(date date not null, hour int  not null, windSpeedInt int  null, windDirInt int null, windBoostInt int null, rainInt int  null, weekdayInt int null, primary key (date, hour));");
            System.out.println("windguru table has successfully been created");

            stmt.executeUpdate("create table if not exists WindGuru.weatherIsFlying" +
                    "(" +
                    "  date   date not null," +
                    "  hour   int  not null," +
                    "  flying int  null," +
                    "  primary key (date, hour)" +
                    ");");
            System.out.println("weatherIsFlying table has successfully been created");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS WindGuru.users (userID int not null , PRIMARY KEY (userID))");
            System.out.println("users table has successfully been created");

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}

