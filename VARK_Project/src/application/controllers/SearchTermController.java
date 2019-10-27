package application.controllers;

import application.Main;
import application.PathCD;
import application.bashwork.BashCommand;
import application.values.CreationStep;
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
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * User search a word in this class, if the searching is successful, user can go to the next step, which is selecting text
 * keyboard short cut for search button is enabled by pressing enter.
 */
public class SearchTermController {
    public String _InputFromUser;
    private String _line;


    @FXML private TextField yourKeyWord;

    @FXML private Label whatDoYouWant;
    @FXML private ImageView progress;
    @FXML private Button enterButton;

    /**
     * initialize by making the audioPiece and photos folder
     * @throws Exception
     */
    @FXML

    public void initialize() throws Exception {
        Main.getController().currentCreationStep(CreationStep.SEARCH);

        //Create the necessary folders and files
        String command2 = "mkdir -p \"" + PathIs.TEMP + "/audioPiece\" ; mkdir -p \""+ PathIs.TEMP + "/photos\" ";
        new BashCommand().bash(command2);

        progress.setVisible(false);
        enterButton.setDisable(false);
        yourKeyWord.setDisable(false);
        addKeyBoardShortCut();
    }

    /**
     * The search method will search the key word user entered
     */

    @FXML
    public void search() throws IOException {
        _InputFromUser = yourKeyWord.getText();
        if (_InputFromUser.trim().isEmpty() || _InputFromUser == null) {
            Main.getController().popupHelper("Enter a word that isn't empty!", false);
            yourKeyWord.clear();
        } else {
            progress.setVisible(true);
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
        yourKeyWord.setOnKeyReleased(keyEvent -> {

            if (keyEvent.getCode() == KeyCode.ENTER) {
                try {
                    search();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * create a class doing background search work
     */
    class DoingJob extends Task<Void> {

        @Override
        protected Void call() throws Exception {
            progress.setVisible(true);
            enterButton.setVisible(true);
            yourKeyWord.setVisible(true);

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
            yourKeyWord.setDisable(false);

            if (_line.contains(_InputFromUser + " not found :^(")) {
                Platform.runLater(() -> {
                        whatDoYouWant.setText("Word wasn't found. Enter another one!");
                        //clear the searched text in the wikipedia
                        yourKeyWord.clear();
                });
            } else {

                try {
                    FileWriter tempWriter = new FileWriter(PathIs.EXTRA + "/temp.txt"); // record the search result in temp.txt file
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
                        EditTextController controller = (EditTextController) Main.getController().setTOPVIEW(SceneFXML.AUDIO.toString()); //go to the save audio scene
                        controller.initData(_InputFromUser);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }
}

