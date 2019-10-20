package Wiki;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.text.html.ListView;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception{
        this.initializeFolder();
        Parent root = FXMLLoader.load(getClass().getResource("menu.fxml"));
        primaryStage.setTitle("Main Menu");
        primaryStage.setScene(new Scene(root, 450, 300));
        primaryStage.show();



    }

    /**
     * This method will create a folder
     */
    private void initializeFolder(){
        //path for the working directory
        Path path = Paths.get("mydir");
        //change path
        if (Files.exists(path)) {

        }
        File dir = new File(path.toString());
        dir.mkdir();



    }


    public static void main(String[] args) {
        launch(args);
    }
}
