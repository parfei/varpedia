package application;

import application.bashwork.BashCommand;
import application.bashwork.ManageFolder;
import application.values.PathIs;
import application.values.SceneFXML;
import application.controllers.MainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;

/**
 * This is the entry point of the application.
 */
public class Main extends Application {
    private static MainController _controller;

    /**
     * Sets the stage
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        ManageFolder.initializeFolders(); //Create necessary folders.
        this.writeScheme();

        FXMLLoader loader = new FXMLLoader(Main.class.getResource(SceneFXML.WINDOW.toString()));
        Parent root = loader.load();
        _controller = (MainController) loader.getController();

        primaryStage.setTitle("VARpedia");
        primaryStage.setScene(new Scene(root, 1200, 600));
        primaryStage.setResizable(false);
        primaryStage.show();

        /*TransportClass.getInstance().setter("apple"); //testing
        ExecutorService team = Executors.newSingleThreadExecutor();
        FlickrWork task = new FlickrWork("apple", "1");
        team.submit(task);
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                team.submit(new CreationWork("apple", 1, false, false));
            }
        });*/ //TESTING FLICKR
        /*Executors.newSingleThreadExecutor().submit(new Confidence("apple3", 2));
        Executors.newSingleThreadExecutor().submit(new Play("apple3"));*/ //TESTING CONFIDENCE & PLAYS TXT WRITING AND READING

        //On close request, close processes, threads and children threads. Also clear out .temp folder when exiting application.
        primaryStage.setOnCloseRequest(event -> {
            try {
                new BashCommand().bash("cd \"" + PathCD.getPathInstance().getPath() + "/mydir\" ; rm -rf .temp ; cd -"); //Clear files in temp folder.);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Platform.exit();
            System.exit(0);
        });
    }

    public void writeScheme() throws IOException{ //TODO reorganise files
        FileWriter writer1=new FileWriter(PathIs.TEMP + "/kal.scm");
        writer1.write("(voice_kal_diphone)");
        writer1.close();

        FileWriter writer2=new FileWriter(PathIs.TEMP + "/jdt.scm");
        writer2.write("(voice_akl_nz_jdt_diphone)");
        writer2.close();

        FileWriter writer3=new FileWriter(PathIs.TEMP + "/cw.scm");
        writer3.write("(voice_akl_nz_cw_cg_cg)");
        writer3.close();
    }

    public static MainController getController(){
        return _controller;
    }

    public static void main(String[] args) {
        launch(args);
    }

}
