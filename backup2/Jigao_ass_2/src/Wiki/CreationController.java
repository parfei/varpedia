package Wiki;
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

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class CreationController {
    public String _InputFromUser;
    private List<String> _textGot = new ArrayList<String>();
    private List<String> _textFormat = new ArrayList<String>();

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
        Parent createViewParent = FXMLLoader.load(getClass().getResource("menu.fxml"));
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
        public ActionEvent _event;

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


            String command = "wikit " + _InputFromUser + " | sed 's/\\([.]\\) \\([[:upper:]]\\)/\\1\\n\\2/g'";
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);
            try {
                Process process = pb.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;


                while ((line = reader.readLine()) != null) {
                    _textGot.add(line);
                }


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


            if (_textGot.get(0).equals(_InputFromUser + " not found :^(")) {
                Platform.runLater(() -> whatDoYouWant.setText("Your input name is invalid, please enter again"));

                //clear the searched text in the wikipedia
                _textGot.clear();
            } else {
                resultOut = true;
                // get the format of the searchedText
                for (int i = 1; i < _textGot.size(); i++) {
                    _textFormat.add(_textGot.get(i - 1) + "\n");

                }

                //create a temp text file in the working directory

                try {
                    FileWriter writer = new FileWriter("temp.txt");
                    for (String str : _textFormat) {
                        writer.write(str);
                    }
                    writer.close();
                } catch (IOException e) {

                }

                Platform.runLater(() -> {
                    try {

                        Parent createViewParent = FXMLLoader.load(getClass().getResource("numberOfLine.fxml"));
                        Scene createViewScene = new Scene(createViewParent);
                        // gets the Stage information
                        Stage window = (Stage) ((Node) _event.getSource()).getScene().getWindow();
                        window.setTitle("Select Line Menu");
                        window.setScene(createViewScene);
                        window.show();
                    } catch (IOException e) {

                    }

                });

            }


            _textFormat.clear();
            _textGot.clear();
        }
    }
}

