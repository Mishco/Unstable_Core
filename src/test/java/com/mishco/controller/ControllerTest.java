package com.mishco.controller;

import javafx.application.Application;
import javafx.stage.Stage;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;


class ControllerTest extends Application {

    private static Controller instance;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // noop
    }


    @BeforeClass
    public static void initJFX() {
        Thread t = new Thread("JavaFX Init Thread") {
            public void run() {
                Application.launch(ControllerTest.class, new String[0]);
                instance = new Controller();
                //instance.initialize();
            }
        };
        t.setDaemon(true);
        t.start();
    }

    @Test
    public void testJavafxApp() {
//        instance.initialize();

    }
}