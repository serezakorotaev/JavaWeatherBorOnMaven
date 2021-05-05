package ru.Korotaev.Weatherbot;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

public class WeatherTreatment {
    static String getContent(String city) {
       StringBuffer content = new StringBuffer();
        try{
            Properties properties = new Properties();
            properties.load(new FileInputStream("src\\main\\resources\\properties.properties"));
            String key =String.valueOf(properties.getProperty("key"));

            URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q="+ city + "&appid=" + key);

           URLConnection urlConnection = url.openConnection();
           BufferedReader bufferedReader = new BufferedReader(
                   new InputStreamReader(urlConnection.getInputStream()));
           while (bufferedReader.ready()){
               content.append(bufferedReader.readLine());
           }
           bufferedReader.close();
       } catch (Exception e){
           System.out.println("Error");
       }
        return content.toString();
    }

        public String weatherToday(String message)  {

        String jsonContent = getContent(message);
        if (!jsonContent.isEmpty()) {
            JSONObject object = new JSONObject(jsonContent);
            String weather = String.format("Температура в городе %s = %s градусов",message,Math.round(object.getJSONObject("main").getDouble("temp") - 273) );
            System.out.println(weather);
            return weather;
        }
        return "Нет такого города. Возможно, допущена ошибка";
    }
}
