module com.example.weathergadget {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jsoup;


    opens com.example.weathergadget to javafx.fxml;
    exports com.example.weathergadget;
}