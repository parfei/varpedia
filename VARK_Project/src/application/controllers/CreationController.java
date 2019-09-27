package application.controllers;
import application.Main;
import application.PathCD;
import application.TransportClass;
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
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


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

    @FXML
    public void initialize() {

        progress.setVisible(false);
        enterButton.setDisable(false);
        goingBack.setDisable(false);
        yourKeyWord.setDisable(false);
    }


    @FXML
    public void backToMain(ActionEvent event) throws IOException {
        Parent createViewParent = FXMLLoader.load(Main.class.getResource("resources/menu.fxml"));
        Scene createViewScene = new Scene(createViewParent);
        // gets the Stage information
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setTitle("Main Menu");
        window.setScene(createViewScene);
        window.show();


    }

    /**
     * This will check if term is searchable
     */
    @FXML
    public void search(ActionEvent event) throws IOException {
        _InputFromUser = yourKeyWord.getText();
        if (_InputFromUser.trim().isEmpty() || _InputFromUser == null) {
            whatDoYouWant.setText("Invalid input, please enter again");
            yourKeyWord.clear(); //TODO check
        } else {
            TransportClass transport = TransportClass.getInstance();
            transport.setter(_InputFromUser);

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
            progress.setVisible(true); //TODO move this out of the task inner class.
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

                //_line = _line.replace(". ", "\n");

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
                String command = "echo -e \"" + _line + "\" &> \"" + PathCD.getPathInstance().getPath() + "/mydir/extra/temp.txt\"";

                System.out.println(command);

                ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);
                try {
                    Process process = pb.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Platform.runLater(() -> {
                    try {

                        Parent createViewParent = FXMLLoader.load(Main.class.getResource("resources/EditText.fxml"));
                        Scene createViewScene = new Scene(createViewParent);
                        // gets the Stage information
                        Stage window = (Stage) ((Node) _event.getSource()).getScene().getWindow();
                        window.setTitle("Edit text Menu");
                        window.setScene(createViewScene);
                        window.show();
                    } catch (IOException e) {
                        e.printStackTrace();

                    }

                });

            }
        }
    }
}

