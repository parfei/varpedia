package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.initializeFolder();
        Parent root = FXMLLoader.load(getClass().getResource("resources/menu.fxml"));
        primaryStage.setTitle("Main Menu");
        primaryStage.setScene(new Scene(root, 450, 300));
        primaryStage.show();



    }

    /**
     * This method will create a folder
     */
    private void initializeFolder(){
        //path for the working directory
        /*Path path = Paths.get("mydir");
        //change path
        if (Files.exists(path)) {

        }
        File dir = new File(path.toString());
        dir.mkdir();*/

        String path = PathCD.getPathInstance().getPath();
        try {
            String command = "[ -e \"" + path + "/mydir\" ]"; //check if there is a creations folder.
            ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", command); //TODO fix this

            Process folder = pb.start();
            if (folder.waitFor() == 1) {
                String command2 = "mkdir -p \"" + path + "/mydir/extra\" ; mkdir \"" + path + "/mydir/creations\"; mkdir \\\"\" + path + \"/mydir/audioPiece\\\""; //create a creations folder.
                ProcessBuilder pb2 = new ProcessBuilder("/bin/bash", "-c", command2);
                pb2.start();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public static void main(String[] args) {
        launch(args);
    }
}
