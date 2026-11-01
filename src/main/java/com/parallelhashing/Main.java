
package com.parallelhashing;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        // Load the main interface from the FXML file
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/main-view.fxml")
        );

        Scene scene = new Scene(loader.load(), 1000, 750);

        // Load the application stylesheet
        scene.getStylesheets().add(
                getClass().getResource("/styles/style.css").toExternalForm()
        );

        primaryStage.setTitle("Parallel Hashing Lab");
        primaryStage.setMinWidth(850);
        primaryStage.setMinHeight(650);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
