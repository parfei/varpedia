package application;

import application.bashwork.BashCommand;
import application.bashwork.ManageFolder;
import application.values.SceneFXML;
import application.controllers.MainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This is the entry point of the application.
 */
public class Main extends Application {
    private static MainController _controller;
    private static CreationsList _list;

    /**
     * Sets the stage
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        this.initializeFolder();
        this.writeScheme();
        _list = new CreationsList();

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
        });*/

    }

    public static CreationsList getCreationsList(){
        return _list;
    }

    /**
     * Creates necessary folders.
     */
    private void initializeFolder() throws Exception {
        ManageFolder.initializeFolders();
        /*try {
            String command = "[ -e \"" + path + "/mydir\" ]"; //check if there is a creations folder.
            ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", command);

            Process folder = pb.start();

            if (folder.waitFor() == 1) {
                String command2 = "mkdir -p \"" + path + "/mydir/extra/\" ; mkdir -p \"" + path + "/mydir/creations/favourites\"; "; //create a creations folder.
                ProcessBuilder pb2 = new ProcessBuilder("/bin/bash", "-c", command2);
                pb2.start();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    /**
     * Run this process to clean files when the application finishes
     * @throws Exception
     */
    @Override
    public void stop() throws Exception { //In case the deletion of such extra files were not successful.
        new BashCommand().bash("cd \"" + PathCD.getPathInstance().getPath() + "/mydir\" ; rm -rf extra/audioPiece/* ; cd -"); //Clear files in extra folder.);
        Platform.exit();
    }

    public void writeScheme() throws IOException{ //TODO reorganise files
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

    public static MainController getController(){
        return _controller;
    }

    public static void main(String[] args) {
        launch(args);
    }

}
