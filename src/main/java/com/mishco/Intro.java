package com.mishco;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class Intro extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        String fullText = "Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".";
        Parent root = FXMLLoader.load(getClass().getResource("/views/sample.fxml"));
        stage.setTitle(fullText);

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/css/progress.css");
        stage.setScene(scene);

        // Set screen to max
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());

        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}