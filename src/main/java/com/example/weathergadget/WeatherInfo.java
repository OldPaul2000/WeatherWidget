package com.example.weathergadget;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class WeatherInfo {

    private static WeatherInfo weatherInfo;


    private WeatherInfo(){};

    public static WeatherInfo getWeatherInfoInstance(){
        if(weatherInfo == null){
            weatherInfo = new WeatherInfo();
        }
        return weatherInfo;
    }


    private String mainUrl = "https://www.accuweather.com/";
    private Connection mainConnection = Jsoup.connect(mainUrl);
    private Document mainPage;
    private String mainLink;

    private String currentHour;
    private String place;
    private String temperature;
    private String realFeelTemperature;
    private String weatherCondition;
    private String humidity;


    public void connectToMainPage() throws IOException{
        mainPage = mainConnection.get();
        findLocalPlaceLink();
    }

    private void replaceMainLinkLanguage(){
        mainLink = mainLink.replaceFirst("(.*accuweather.com/)(\\w{2,})(/.*)","$1ro$3");
    }

    private void findLocalPlaceLink(){
        //This method will find the specific link for your current location.
        Elements mainPageLinks = mainPage.select("a[href]");
        String currentLocationElementLink = mainPageLinks.get(3).absUrl("href");

        try{
            Document currentLocationPage = Jsoup.connect(currentLocationElementLink).get();
            Elements currentLocationPageLinks = currentLocationPage.select("a[href]");
            for(Element element : currentLocationPageLinks){
                String link = element.absUrl("href");
                if(link.contains("current-weather")){
                    mainLink = link;
                    replaceMainLinkLanguage();
                }
            }
        }
        catch (IOException e){

        }
    }

    public void processData() throws IOException{
        Document doc = Jsoup.connect(mainLink).get();
        Elements elements = doc.select("body");
        String webPageText = "";

        for(Element element : elements){
            webPageText = element.text();
        }

        currentHour = webPageText.replaceAll(".* Vremea curentă (\\d{1,2}:\\d{1,2}).*","$1");

        place = webPageText.replaceAll("Înapoi (.*) \\d+°C.*","$1").replaceAll("(.*) \\d+°C.*","$1");

        temperature = webPageText.replaceAll(".*\\d{1,2}:\\d{1,2} (-?\\d+°)C.*","$1");

        realFeelTemperature = webPageText.replaceFirst("RealFeel Shade™ (.*)","");
        realFeelTemperature = realFeelTemperature.replaceFirst(".* RealFeel® (.*) \\w*","$1");
        realFeelTemperature = realFeelTemperature.replaceAll("(-?\\d+°) .*","$1");

        weatherCondition = webPageText.replaceAll(".*°C (.*) RealFeel","$1");
        weatherCondition = weatherCondition.replaceFirst("\\sRealFeel.*","");

        humidity = webPageText.replaceAll(".* Umiditate (\\d+%) .*","$1");
    }


    public String getCurrentHour(){
        return currentHour;
    }

    public String getPlace(){
        return place;
    }

    public String getTemperature(){
        return temperature;
    }

    public String getRealFeelTemperature(){
        return realFeelTemperature;
    }

    public String getWeatherCondition(){
        return weatherCondition;
    }

    public String getHumidity(){
        return humidity;
    }

}
