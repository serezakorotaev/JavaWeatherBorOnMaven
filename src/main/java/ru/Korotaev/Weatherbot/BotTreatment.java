package ru.Korotaev.Weatherbot;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.messages.*;
import com.vk.api.sdk.queries.messages.MessagesGetLongPollHistoryQuery;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class BotTreatment {
    public static void main(String[] args) throws IOException, ClientException, ApiException {
        TransportClient transportClient = new HttpTransportClient();
        VkApiClient vk = new VkApiClient(transportClient); //через transportClient передаем запросы
        Random random = new Random();

        int groupId;
        String accessToken;
        Properties properties = new Properties();
        properties.load(new FileInputStream("src\\main\\resources\\properties.properties"));
        groupId = Integer.parseInt(properties.getProperty("group_id"));
        accessToken = String.valueOf(properties.getProperty("access_token"));

        GroupActor groupActor = new GroupActor(groupId,accessToken);
        Integer ts = vk.messages().getLongPollServer(groupActor).execute().getTs();//обновление этой переменной чтобы сообщения были не одними и теми же

        Keyboard keyboard = new Keyboard();
        List<List<KeyboardButton>> allKey = new ArrayList<>();
        List<KeyboardButton> line1 = new ArrayList<>();
        //вторая линия кнопок
        List<KeyboardButton> line2 = new ArrayList<>();


        line1.add(new KeyboardButton().setAction(new KeyboardButtonAction().setLabel("Привет").setType(KeyboardButtonActionType.TEXT)).setColor(KeyboardButtonColor.POSITIVE));
        line1.add(new KeyboardButton().setAction(new KeyboardButtonAction().setLabel("Кто я?").setType(KeyboardButtonActionType.TEXT)).setColor(KeyboardButtonColor.POSITIVE));

        line2.add(new KeyboardButton().setAction(new KeyboardButtonAction().setLabel("Погода").setType(KeyboardButtonActionType.TEXT)).setColor(KeyboardButtonColor.POSITIVE));


        allKey.add(line1);
        allKey.add(line2);



        keyboard.setButtons(allKey);
        while (true){
            MessagesGetLongPollHistoryQuery historyQuery = vk.messages().getLongPollHistory(groupActor).ts(ts);
            List<Message> messages = historyQuery.execute().getMessages().getItems();
            if (!messages.isEmpty()){
                for (Message message : messages) {
                    System.out.println(message.toString());
                    try {
                        if (message.getText().equals("Привет")) {
                            vk.messages().send(groupActor).message("Привет!").userId(message.getFromId()).randomId(random.nextInt(10000)).execute();
                        } else if (message.getText().equals("Кнопки")) {
                            vk.messages().send(groupActor).message("А вот и они").userId(message.getFromId()).randomId(random.nextInt(10000)).keyboard(keyboard).execute();
                        } else if (message.getText().equals("Кто я?")) {
                            vk.messages().send(groupActor).message("Ты хороший человек!").userId(message.getFromId()).randomId(random.nextInt(10000)).execute();
                        }else if (message.getText().equals("Погода")) {
                            vk.messages().send(groupActor).message("Хочешь узнать погоду? набери свой город и узнаешь температуру на данный момент").userId(message.getFromId()).randomId(random.nextInt(10000)).execute();
                        }else{
                            WeatherTreatment weatherTreatment = new WeatherTreatment();
                            vk.messages().send(groupActor).message(weatherTreatment.weatherToday(message.getText())).userId(message.getFromId()).randomId(random.nextInt(10000)).execute();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            ts = vk.messages().getLongPollServer(groupActor).execute().getTs();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
