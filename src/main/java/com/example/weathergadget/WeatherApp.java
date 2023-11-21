package com.example.weathergadget;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;


public class WeatherApp extends Application {
    public static Stage window;
    @Override
    public void start(Stage stage) throws IOException {
        window = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(WeatherApp.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 140, 105);
        scene.setFill(Color.TRANSPARENT);
        stage.setTitle("Hello!");

        Image logo = new Image(WidgetStageController.localPath + File.separator + "WeatherLogo.png");
        stage.getIcons().add(logo);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}