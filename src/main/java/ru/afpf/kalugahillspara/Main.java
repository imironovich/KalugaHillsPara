package ru.afpf.kalugahillspara;

import java.io.*;
import java.util.Calendar;
import java.util.Properties;
import java.util.Timer;


public class Main {public static void main(String[] args) throws Exception{

    Timer timer = new Timer();

    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    Properties props = new Properties();
    try(InputStream resourceStream = loader.getResourceAsStream("Bot.properties")) {
        props.load(resourceStream);
    }

    CheckCreateDB dbcre = new CheckCreateDB(props.getProperty("DB_PASSWD"), props.getProperty("DB_USER"), props.getProperty("SQLCREATEURL"));
    dbcre.createDB();

    Calendar dateWindguru = Calendar.getInstance();
    dateWindguru.set(Calendar.HOUR_OF_DAY, 8);
    dateWindguru.set(Calendar.MINUTE, 20);
    dateWindguru.set(Calendar.SECOND, 00);
    dateWindguru.set(Calendar.MILLISECOND, 0);
    WindguruParser wp = new WindguruParser(props.getProperty("DB_PASSWD"), props.getProperty("DB_USER"), props.getProperty("SQLURL"));
    timer.schedule(wp, dateWindguru.getTime(), 1000 * 60 * 60 * 24); //dayli take weather forecast from windguru.cz and put to tavle windGuru

    Calendar dateFlying = Calendar.getInstance();
    dateFlying.set(Calendar.HOUR_OF_DAY, 8);
    dateFlying.set(Calendar.MINUTE, 25);
    dateFlying.set(Calendar.SECOND, 00);
    dateFlying.set(Calendar.MILLISECOND, 0);
    AnalizWeatherForecast awf = new AnalizWeatherForecast(props.getProperty("DB_PASSWD"), props.getProperty("DB_USER"), props.getProperty("SQLURL"));
    timer.schedule(awf, dateFlying.getTime(), 1000 * 60 * 60 * 24); //dayli take forecast from table windGuru and put result in table weatherIsFlying

    Calendar dateFlight = Calendar.getInstance();
    dateFlight.set(Calendar.HOUR_OF_DAY, 8);
    dateFlight.set(Calendar.MINUTE, 30);
    dateFlight.set(Calendar.SECOND, 00);
    dateFlight.set(Calendar.MILLISECOND, 0);
    FlyAlert fa = new FlyAlert(props.getProperty("DB_PASSWD"), props.getProperty("DB_USER"), props.getProperty("SQLURL"), props.getProperty("CHAT_ID"), props.getProperty("BOT_NAME"), props.getProperty("BOT_TOKEN"));
    timer.schedule(fa, dateFlight.getTime(), 1000 * 60 * 60 * 24); //dayli take forecast from table windGuru and put result in table weatherIsFlying
}
}
