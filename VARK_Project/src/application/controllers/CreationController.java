package application.controllers;

import application.Main;
import application.PathCD;
import application.bashwork.BashCommand;
import application.values.PathIs;
import application.values.SceneFXML;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;


public class CreationController {
    public String _InputFromUser;
    private String _line;


    @FXML private TextField yourKeyWord;

    @FXML private Label whatDoYouWant;
    @FXML private ProgressBar progress;
    @FXML private Button enterButton;
    @FXML private Button goingBack;




    public CreationController() throws IOException {



    }

    @FXML
    public void initialize() throws Exception {
        String command2 = "mkdir -p \"" + PathIs.TEMP + "/audioPiece\" ; mkdir -p \""+ PathIs.TEMP + "/photos\" ; mkdir -p \""+ PathIs.EXTRA + "/saveTextFolder\" ";
        new BashCommand().bash(command2);

        progress.setVisible(false);
        enterButton.setDisable(false);
        goingBack.setDisable(false);
        yourKeyWord.setDisable(false);
        addKeyBoardShortCut();






    }
    public Scene getScene(){
        System.out.println(enterButton.getScene());
        return enterButton.getScene();

    }









    @FXML
    public void backToMain() throws IOException {
        //_changeSceneObject.changeScene(event, "resources/menu.fxml", "Main Menu");
        Main.getController().setTOPVIEW(SceneFXML.MENU.toString());
    }


    /**
     * This will check if term is searchable
     */



    @FXML
    public void search() throws IOException {
        System.out.println(enterButton.getScene());
        _InputFromUser = yourKeyWord.getText();
        if (_InputFromUser.trim().isEmpty() || _InputFromUser == null) {
            whatDoYouWant.setText("Invalid input, please enter again");
            yourKeyWord.clear();
        } else {
            DoingJob doingJob = new DoingJob();
            Thread thread = new Thread(doingJob);
            thread.start();
        }
    }

    /**
     * add enter keyboard short cut for search
     */
    @FXML
    public void addKeyBoardShortCut() {
        yourKeyWord.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {

                if (keyEvent.getCode() == KeyCode.ENTER) {
                    try {
                        search();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });



    }




    /**
     * create a class doing multithreading
     */
    class DoingJob extends Task<Void> {
        private boolean resultOut;

        @Override
        protected Void call() throws Exception {
            progress.setVisible(true);
            enterButton.setVisible(true);
            goingBack.setVisible(true);
            yourKeyWord.setVisible(true);

            Platform.runLater(() -> {
                progress.progressProperty().bind(this.progressProperty());
            });

            String command = "wikit " + _InputFromUser;
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);
            try {
                Process process = pb.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                _line = reader.readLine();


            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void done() {
            progress.setVisible(false);
            enterButton.setDisable(false);
            goingBack.setDisable(false);
            yourKeyWord.setDisable(false);


            if (_line.contains(_InputFromUser + " not found :^(")) {
                Platform.runLater(() -> {
                        whatDoYouWant.setText("Your input name is invalid, please enter again"); //TODO timeout result
                        //clear the searched text in the wikipedia
                        yourKeyWord.clear();
                });
            } else {

                try {
                    FileWriter tempWriter = new FileWriter(PathIs.EXTRA + "/temp.txt");
                    tempWriter.write(_line);
                    tempWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Platform.runLater(() -> {
                    try {
                        String command = "cd \"" + PathCD.getPathInstance().getPath() + "/mydir\" ; rm -rf .temp/audioPiece/* ; cd -"; //Clear files in temp folder.
                        ProcessBuilder pb2 = new ProcessBuilder("bash", "-c", command);
                        try {
                            Process end = pb2.start();
                            end.waitFor();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        EditTextController controller = (EditTextController) Main.getController().setTOPVIEW(SceneFXML.AUDIO.toString());
                        controller.initData(_InputFromUser);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }
}

