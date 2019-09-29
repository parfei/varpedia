package application.controllers;
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

        //text2wave -o some.wav some.txt -eval slow.scm

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
                        FXMLLoader loader = new FXMLLoader(Main.class.getResource("resources/EditText.fxml"));
                        Parent createViewParent = loader.load();
                        EditTextController controller = loader.getController();
                        controller.initData(_InputFromUser);

                        Scene createViewScene = new Scene(createViewParent);
                        // gets the Stage information
                        Stage window = (Stage) ((Node) _event.getSource()).getScene().getWindow();
                        window.show();
                        window.setTitle("Edit text Menu");
                        window.setScene(createViewScene);
                    } catch (IOException e) { //TODO error?
                    }

                });

            }
        }
    }
}

