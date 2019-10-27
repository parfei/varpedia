package application.controllers;

import application.*;
import application.bashwork.BashCommand;
import application.values.CreationStep;
import application.values.PathIs;
import application.values.SceneFXML;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * We create a new creation in this class. Name checking is done here. The bulk of the video creation is called from here.
 * We call new instances of the work to be done in a background thread. Can return back to the menu from here.
 */
public class CreateNewController {

    @FXML private TextField textFieldCreationName;
    @FXML private ChoiceBox<String> choiceBox;
    @FXML private ListView listViewExistCreation;
    @FXML private Button createBtn;

    private List<String> _CreationsExisted = new ArrayList<String>();
    private ExecutorService team;
    private String _term;
    private int _picNum;


    public void initData(String term, int picNum){
        _term = term;
        _picNum = picNum;
    }

    /**
     * This method will add the existing creation to the ListView and make a choice box for user to choose music
     */
    public void initialize() throws IOException {
        Main.getController().popupHelper("Let's add some final details to your creation!", false);
        Main.getController().currentCreationStep(CreationStep.FINAL);
        choiceBox.getSelectionModel().selectLast();
        createBtn.setDisable(true);

        ObservableList list=FXCollections.observableArrayList();
        list.addAll("Clouds","Fingers", "Sun", "No music");
        choiceBox.getItems().addAll(list);

        team = Executors.newSingleThreadExecutor();

        //List out all the existing creations!
        String command = "ls -R \"" + PathCD.getPathInstance().getPath() + "/mydir/creations\" " + " | grep .mp4 | cut -f1 -d'.' | sort";
        ProcessBuilder builder1 = new ProcessBuilder("bash", "-c", command);
        try {
            String line;
            Process process = builder1.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            while ((line = reader.readLine()) != null) {
                _CreationsExisted.add(line);
            }
            listViewExistCreation.getItems().addAll(_CreationsExisted);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method checks for validity of text entered, will enable create button if can create.
     */
    @FXML
    public void enteredText(){
        createBtn.setDisable(true);
        try {
            if (textFieldCreationName.getText().trim().isEmpty()||textFieldCreationName.getText()==null){
                Main.getController().popupHelper("Enter a name please!", false);
            } else if (!textFieldCreationName.getText().matches("[a-zA-Z0-9_-]*")) {
                Main.getController().popupHelper("Enter a-z name only!", false);
            } else {
                createBtn.setDisable(false);
            }
        } catch (Exception e) {
            textFieldCreationName.clear();
        }

    }

    /***
     * When the user hits the create button, the user will be taken back to the menu whilst the work is done in
     * background threads. The method also checks for the validity of the name.
     * @throws IOException
     */
    @FXML
    public void enterCreation() throws Exception {
        if (_CreationsExisted.contains(textFieldCreationName.getText())) { /////Check for creation name error input.
            Alert overwrite = new Alert(Alert.AlertType.CONFIRMATION);
            overwrite.setTitle("Duplicated Name");
            overwrite.setHeaderText("Would you like to overwrite " + textFieldCreationName.getText() + "?");
            overwrite.setContentText("OK: overwrite. Cancel: retry your name, or you can choose to go back to the menu.");

            overwrite.showAndWait();
            if (overwrite.getResult() == ButtonType.OK) {
            } else {
                return;
            }
        }

        createDirectories(); //Create necessary directories if they have not existed yet.

        //Send creation work to background thread to create the final creation...
        CreationWork creationWork = null;
        creationWork = new CreationWork(_term, textFieldCreationName.getText(), _picNum, true,choiceBox.getValue());

        team.submit(creationWork);

        creationWork.setOnSucceeded(workerStateEvent -> {
            try {

                // create a confidence.txt file to record user's confidence and create a plays.text file to record user's times of play
                String p = "\"" + PathIs.EXTRA + "/" + _term + "/" + textFieldCreationName.getText() + "/";
                new BashCommand().bash("touch " + p + "confidence.txt\" " + p + "plays.txt\"");

                team.submit(new Confidence(textFieldCreationName.getText(), 0));
                team.submit(new Play(textFieldCreationName.getText()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Main.clear(); //Clean up audio files after the Creation has been made.
            } catch (Exception e) {
                e.printStackTrace();
            }

            _CreationsExisted.clear();

            textFieldCreationName.clear();
            Main.getController().creationInProgress(false); //show user the creation work is done by change the picture
            Main.getController().currentCreationStep(CreationStep.CREATED);
        });
        try {
            Main.getController().popupHelper("Let's go learn your creation, click play button!", false);
            Main.getController().setTOPVIEW(SceneFXML.MENU.toString()); // go to the main menu
            Main.getController().creationInProgress(true); // show the user the creation is still being made
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Create required folders to store the audio, video and pictures for creating the final Creation.
     * @throws IOException
     */
    private void createDirectories() throws Exception {
        new BashCommand().bash("mkdir -p \"" + PathIs.EXTRA + "/" + _term + "/" + textFieldCreationName.getText() + "\""); //create a creations folder.
    }
}


