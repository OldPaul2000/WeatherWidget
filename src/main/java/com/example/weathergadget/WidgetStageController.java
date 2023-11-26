package com.example.weathergadget;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.jsoup.helper.ValidationException;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class WidgetStageController {

    public Stage window = WeatherApp.window;
    private StagePositions stagePositions = new StagePositions();

    @FXML
    public AnchorPane background;

    @FXML
    public Button closeButton;

    @FXML
    public Button moveWindowButton;

    @FXML
    public Button minimizeButton;
    private final double BUTTONS_LAYOUT_Y = 5;
    
    @FXML
    public Label currentHour = new Label();

    @FXML
    public Label place = new Label();

    @FXML
    public Label temperature = new Label();

    @FXML
    public Label realFeelTemperature = new Label();

    @FXML
    public Label weatherCondition = new Label();

    @FXML
    public Label humidity = new Label();


    public static Path localPath = FileSystems.getDefault().getPath("").toAbsolutePath();

    private WeatherInfo weatherInfoInstance = WeatherInfo.getWeatherInfoInstance();

    private Thread processData;

    private final String NO_CONNECTION_MESSAGE = "No connection";
    private final String COMIC_SANS_MS_FONT = "Comic Sans MS";

    public void initialize(){
        try{
            weatherInfoInstance.connectToMainPage();
        }
        catch (IOException connectionException){
            displayNoConnection();
        }


        initializeWindowPosition();

        setInfoLabelsLayout();
        setLabelsFontStyle();

        setCloseButtonLayoutAndDimensions();
        setCloseButtonProperties();
        setCloseButtonStyle();

        setMoveWindowButtonLayoutAndDimensions();
        setMoveWindowButtonProperties();
        setMoveWindowButtonStyle();

        setMinimizeButtonLayoutAndDimensions();
        setMinimizeButtonProperties();
        setMinimizeButtonStyle();


        setBackgroundStyle();


        processData = new Thread(() -> {
            while (true){
                boolean hasConnectedSuccesfully = true;
                try {
                    weatherInfoInstance.connectToMainPage();
                }
                catch (ValidationException | IOException connectionException){
                    hasConnectedSuccesfully = false;
                    Platform.runLater(this::displayNoConnection);
                }
                finally {
                    try{
                        weatherInfoInstance.processData();
                    }
                    catch (ValidationException | IOException connectionException){

                    }
                }
                if(hasConnectedSuccesfully){
                    Platform.runLater(() -> {
                        updateLabelsInfo(weatherInfoInstance.getCurrentHour(),
                                weatherInfoInstance.getPlace(),
                                weatherInfoInstance.getTemperature(),
                                weatherInfoInstance.getRealFeelTemperature(),
                                weatherInfoInstance.getWeatherCondition(),
                                weatherInfoInstance.getHumidity()
                        );
                    });
                }

                try {
                    Thread.sleep(15000);
                }
                catch (InterruptedException e){

                }
            }
        });

        processData.start();

        addNodesToScene();
    }

    private void displayNoConnection(){
        currentHour.setText("");
        place.setText("");
        temperature.setText("");
        realFeelTemperature.setText("");
        weatherCondition.setText("");
        humidity.setText(NO_CONNECTION_MESSAGE);
    }

    private void initializeWindowPosition(){
        stagePositions.initializeClassFieldValues();
        window.setX(stagePositions.getXPosition());
        window.setY(stagePositions.getYPosition());
    }

    private void setInfoLabelsLayout(){
        currentHour.setLayoutX(7);
        currentHour.setLayoutY(3);
        place.setLayoutX(5);
        place.setLayoutY(23);
        temperature.setLayoutX(5);
        temperature.setLayoutY(38);
        realFeelTemperature.setLayoutX(5);
        realFeelTemperature.setLayoutY(53);
        weatherCondition.setLayoutX(5);
        weatherCondition.setLayoutY(68);
        humidity.setLayoutX(5);
        humidity.setLayoutY(83);
    }

    private void updateLabelsInfo(String hourInput, String placeInput, String temperatureInput, String realFeelTemperatureInput, String weatherConditionInput, String humidityInput){
        currentHour.setText(hourInput);
        place.setText(placeInput);
        temperature.setText("Temperatura:" + temperatureInput);
        realFeelTemperature.setText("Se simte ca:" + realFeelTemperatureInput);
        weatherCondition.setText(weatherConditionInput);
        humidity.setText("Umiditate:" + humidityInput);
    }

    private void setCloseButtonLayoutAndDimensions(){
        closeButton = new Button();
        closeButton.setLayoutX(120);
        closeButton.setLayoutY(BUTTONS_LAYOUT_Y);
        closeButton.setMinWidth(13);
        closeButton.setMinHeight(13);
        closeButton.setPrefWidth(13);
        closeButton.setPrefHeight(13);
    }

    private void setCloseButtonProperties(){
        closeButton.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getButton().toString().equals("PRIMARY")){
                System.exit(0);
            }
        });
    }

    private void setCloseButtonStyle(){
        Insets insets = new Insets(0,0,0,-1);
        closeButton.setPadding(insets);
        closeButton.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT,null,null)));
        Image closeIconImage = new Image(localPath + File.separator + "CloseIcon.png");
        ImageView buttonXSign = new ImageView(closeIconImage);
        buttonXSign.setFitHeight(8);
        buttonXSign.setFitWidth(8);
        closeButton.setGraphic(buttonXSign);
    }

    private void setMoveWindowButtonLayoutAndDimensions(){
        moveWindowButton = new Button();
        moveWindowButton.setLayoutX(closeButton.getLayoutX() - closeButton.getPrefWidth());
        moveWindowButton.setLayoutY(BUTTONS_LAYOUT_Y);
        moveWindowButton.setMinWidth(13);
        moveWindowButton.setMinHeight(13);
        moveWindowButton.setPrefWidth(13);
        moveWindowButton.setPrefHeight(13);
    }

    private void setMoveWindowButtonProperties(){
        moveWindowButton.setOnMouseDragged(mouseEvent -> {
            if(mouseEvent.getButton().toString().equals("PRIMARY")){
                double mouseXPosition = mouseEvent.getScreenX() - moveWindowButton.getLayoutX() - moveWindowButton.getPrefWidth() / 2;
                double mouseYPosition = mouseEvent.getScreenY() - moveWindowButton.getLayoutY() - moveWindowButton.getPrefHeight() / 2;
                window.setX(mouseXPosition);
                window.setY(mouseYPosition);
            }
        });

        moveWindowButton.setOnMouseReleased(mouseEvent -> {
            stagePositions.writePositionsToFile(window.getX(), window.getY());
        });
    }

    private void setMoveWindowButtonStyle(){
        Insets insets = new Insets(0,0,0,-1);
        moveWindowButton.setPadding(insets);
        moveWindowButton.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT,null,null)));
        Image moveIconImage = new Image(localPath + File.separator + "MoveIcon.png");
        ImageView buttonMoveSign = new ImageView(moveIconImage);
        buttonMoveSign.setFitHeight(8);
        buttonMoveSign.setFitWidth(8);
        moveWindowButton.setGraphic(buttonMoveSign);
    }

    private void setMinimizeButtonLayoutAndDimensions(){
        minimizeButton = new Button();
        minimizeButton.setLayoutX(moveWindowButton.getLayoutX() - moveWindowButton.getPrefWidth());
        minimizeButton.setLayoutY(BUTTONS_LAYOUT_Y);
        minimizeButton.setMinWidth(13);
        minimizeButton.setMinHeight(13);
        minimizeButton.setPrefWidth(13);
        minimizeButton.setPrefHeight(13);
    }

    private void setMinimizeButtonProperties(){
        minimizeButton.setOnMouseClicked(mouseEvent -> {
            window.setIconified(true);
        });
    }

    private void setMinimizeButtonStyle(){
        Insets insets = new Insets(0,0,0,0);
        minimizeButton.setPadding(insets);
        minimizeButton.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT,null,null)));
        Image closeIconImage = new Image(localPath + File.separator + "MinimizeIcon.png");
        ImageView buttonMinimizeSign = new ImageView(closeIconImage);
        buttonMinimizeSign.setFitHeight(8);
        buttonMinimizeSign.setFitWidth(8);
        minimizeButton.setGraphic(buttonMinimizeSign);
    }



    private void setLabelsFontStyle(){
        currentHour.setFont(Font.font(COMIC_SANS_MS_FONT, FontWeight.BOLD,13));
        place.setFont(Font.font(COMIC_SANS_MS_FONT, FontWeight.BOLD,12));
        temperature.setFont(Font.font(COMIC_SANS_MS_FONT, FontWeight.BOLD,12));
        realFeelTemperature.setFont(Font.font(COMIC_SANS_MS_FONT, FontWeight.BOLD,12));
        weatherCondition.setFont(Font.font(COMIC_SANS_MS_FONT, FontWeight.BOLD,12));
        humidity.setFont(Font.font(COMIC_SANS_MS_FONT, FontWeight.BOLD,12));
    }
    
    private void setBackgroundStyle(){
        background.setPrefWidth(140);
        background.setPrefHeight(105);
        CornerRadii radii = new CornerRadii(15,15,15,15,false);
        background.setBackground(new Background(new BackgroundFill(Color.valueOf("FFB24A"),radii,null)));
    }

    private void addNodesToScene(){
        background.getChildren().add(currentHour);
        background.getChildren().add(place);
        background.getChildren().add(temperature);
        background.getChildren().add(realFeelTemperature);
        background.getChildren().add(weatherCondition);
        background.getChildren().add(humidity);
        background.getChildren().add(closeButton);
        background.getChildren().add(moveWindowButton);
        background.getChildren().add(minimizeButton);
    }

}