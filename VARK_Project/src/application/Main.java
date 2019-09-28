package application;

import javafx.application.Application;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.initializeFolder();
        this.writeScheme();
        Parent root = FXMLLoader.load(getClass().getResource("resources/menu.fxml"));
        primaryStage.setTitle("Main Menu");
        primaryStage.setScene(new Scene(root, 450, 300));
        primaryStage.show();

        /*TransportClass.getInstance().setter("apple");
        ExecutorService team = Executors.newSingleThreadExecutor();
        FlickrWork task = new FlickrWork("apple", "1");
        team.submit(task);
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                team.submit(new CreationWork("apple", 1, false, false));
            }
        });*/


    }

    /**
     * This method will create a folder
     */
    private void initializeFolder(){
        String path = PathCD.getPathInstance().getPath();
        try {
            String command = "[ -e \"" + path + "/mydir\" ]"; //check if there is a creations folder.
            ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", command);

            Process folder = pb.start();

            if (folder.waitFor() == 1) {
                String command2 = "mkdir -p \"" + path + "/mydir/extra/audio\" ; mkdir \"" + path + "/mydir/creations\"; "; //create a creations folder.
                ProcessBuilder pb2 = new ProcessBuilder("/bin/bash", "-c", command2);
                pb2.start();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Override //TODO check if it always works, implement for start, check if exits and nothing is blocking some process is running too.
    public void stop() throws Exception { //In case the deletion of such extra files were not successful.
        /*String command = "cd \"" + PathCD.getPathInstance().getPath() + "/mydir\" ; rm -rf extra/* ; cd -"; //Clear files in extra folder.
        ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);
        try {
            Process end = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public void writeScheme() throws IOException {
        FileWriter writer1=new FileWriter("kal.scm");
        writer1.write("(voice_kal_diphone)");
        writer1.close();

        FileWriter writer2=new FileWriter("jdt.scm");
        writer2.write("(voice_akl_nz_jdt_diphone)");
        writer2.close();

        FileWriter writer3=new FileWriter("cw.scm");
        writer3.write("(voice_akl_nz_cw_cg_cg)");
        writer3.close();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
