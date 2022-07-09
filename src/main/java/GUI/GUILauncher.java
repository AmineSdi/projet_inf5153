package GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import Application.Main;

public class GUILauncher extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800.0, 416.0);
        stage.setTitle("Contacts");
        stage.setScene(scene);
        stage.show();
    }

    public static void launch(String[] args) {
        launch();
    }
}