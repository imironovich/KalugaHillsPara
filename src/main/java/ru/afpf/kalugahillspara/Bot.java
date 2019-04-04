package ru.afpf.kalugahillspara;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


public class Bot extends TelegramLongPollingBot {

  final String bot_name;
  final String bot_token;



    public Bot(String BOT_NAME, String BOT_TOKEN) {

        bot_name = BOT_NAME;
        bot_token = BOT_TOKEN;

    }
    public void onUpdateReceived(Update update) {
        try{
            Message message = update.getMessage();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void sendMess(long chat_id, String text){
        SendMessage sendMess = new SendMessage().setChatId(chat_id).setText(text);
        try {
            execute(sendMess);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    public String getBotUsername() {
        return bot_name;
    }

    public String getBotToken() {
        return bot_token;
    }

}
