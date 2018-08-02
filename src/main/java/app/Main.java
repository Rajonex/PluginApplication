package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.opencv.core.Core;

public class Main extends Application {


    public static void main(String[] args) {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        nu.pattern.OpenCV.loadShared();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent parent = (Parent) FXMLLoader.load(getClass().getResource("/fxml/MainPane.fxml"));

        Scene scene = new Scene(parent);
        primaryStage.setScene(scene);
//        primaryStage.setTitle("Tytuł");
//        primaryStage.sizeToScene();
        primaryStage.show();
    }
}
