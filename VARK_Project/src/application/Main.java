package application;

import application.bashwork.BashCommand;
import application.bashwork.ManageFolder;
import application.controllers.MainController;
import application.values.PathIs;
import application.values.SceneFXML;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;

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
        new BashCommand().bash("mkdir -p \"" + PathCD.getPathInstance().getPath() + "/mydir/.temp\"");
        this.writeScheme();

        FXMLLoader loader = new FXMLLoader(Main.class.getResource(SceneFXML.WINDOW.toString()));
        Parent root = loader.load();
        _controller = (MainController) loader.getController();

        primaryStage.setTitle("VARpedia");
        primaryStage.setScene(new Scene(root, 1200, 600));
        primaryStage.setResizable(false);
        primaryStage.show();


        primaryStage.setOnCloseRequest(event -> {
            deleteTemp();
            Platform.exit();
            System.exit(0);
        });
    }

    /**
     * write the scheme file for the use of saving audio
     * @throws IOException
     */

    public static void writeScheme() throws IOException{ //TODO reorganise files
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

    /**
     * In case program terminates unexpectedly, NOT through exit button.
     * @throws Exception
     */
    @Override
    public void stop() throws Exception {
        super.stop();
        deleteTemp();
        Platform.exit();
        System.exit(0);
    }

    /**
     * Helper function to clear .temp folder and stop all threads cleanly.
     */
    public static void clear(){
        try {
            deleteTemp();
            new BashCommand().bash("mkdir -p \"" + PathCD.getPathInstance().getPath() + "/mydir/.temp\"");
            writeScheme();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteTemp(){
        try {
            new BashCommand().bash("cd \"" + PathCD.getPathInstance().getPath() + "/mydir\" ; rm -rf .temp ; cd -"); //Clear files in temp folder.);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
