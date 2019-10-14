package application.controllers;
import application.ChangeScene;
import application.Main;
import application.PathCD;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;


public class CreationController {
    public String _InputFromUser;
    private String _line;

    @FXML
    private TextField yourKeyWord;


    @FXML
    private Label whatDoYouWant;
    @FXML
    private ProgressBar progress;
    @FXML
    private Button enterButton;
    @FXML
    private Button goingBack;
    private ChangeScene _changeSceneObject=new ChangeScene();

    @FXML
    public void initialize() throws IOException {
        String path = PathCD.getPathInstance().getPath();
        String command2 = "mkdir -p \"" + path + "/mydir/extra/audioPiece\"";
        ProcessBuilder pb2 = new ProcessBuilder("/bin/bash", "-c", command2);
        pb2.start();

        progress.setVisible(false);
        enterButton.setDisable(false);
        goingBack.setDisable(false);
        yourKeyWord.setDisable(false);
    }


    @FXML
    public void backToMain(ActionEvent event) throws IOException {
        //_changeSceneObject.changeScene(event, "resources/menu.fxml", "Main Menu");
        Main.getController().setBOTTOMVIEW("resources/DefaultBottom.fxml");
    }

    /**
     * This will check if term is searchable
     */
    @FXML
    public void search(ActionEvent event) throws IOException {

        _InputFromUser = yourKeyWord.getText();
        if (_InputFromUser.trim().isEmpty() || _InputFromUser == null) {
            whatDoYouWant.setText("Invalid input, please enter again");
            yourKeyWord.clear();
        } else {

            //TransportClass transport = TransportClass.getInstance();
            //transport.setter(_InputFromUser);

            DoingJob doingJob = new DoingJob(event);
            Thread thread = new Thread(doingJob);
            thread.start();
        }
    }

    /**
     * create a class doing multithreading
     */
    class DoingJob extends Task<Void> {
        private boolean resultOut;
        private ActionEvent _event;

        public DoingJob(ActionEvent event) {
            _event = event;
        }

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

                //_line = _line.replace(". ", ".\n");

            } catch (
                    IOException ex) {
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
                        whatDoYouWant.setText("Your input name is invalid, please enter again");
                        //clear the searched text in the wikipedia
                        yourKeyWord.clear();
                });
            } else {
                //resultOut = true;

                // get the format of the searchedText
                //String command = "echo -e \"" + _line + "\" > " + PathCD.getPathInstance().getPath() + "/mydir/extra/temp.txt";
               String path = PathCD.getPathInstance().getPath() + "/mydir/extra";

                try {
                    FileWriter tempWriter = new FileWriter(path + "/temp.txt");
                    tempWriter.write(_line);
                    tempWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                Platform.runLater(() -> {
                    try {
                        String command = "cd \"" + PathCD.getPathInstance().getPath() + "/mydir\" ; rm -rf extra/audioPiece/* ; cd -"; //Clear files in extra folder.
                        ProcessBuilder pb2 = new ProcessBuilder("bash", "-c", command);
                        try {
                            Process end = pb2.start();
                            end.waitFor();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        EditTextController controller = (EditTextController) Main.getController().setBOTTOMVIEW("resources/EditText.fxml");
                        controller.initData(_InputFromUser);

                    } catch (IOException e) {
                        e.printStackTrace();//TODO error?
                    }

                });

            }
        }
    }
}

