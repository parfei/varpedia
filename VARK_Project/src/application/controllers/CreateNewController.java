package application.controllers;

import application.*;
import application.bashwork.BashCommand;
import application.values.PathIs;
import application.values.SceneFXML;
import com.sun.jdi.connect.Transport;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.sun.prism.impl.Disposer.cleanUp;

/**
 * We create a new creation in this class. Name checking is done here. The bulk of the video creation is called from here.
 * We call new instances of the work to be done in a background thread. Can return back to the menu from here.
 */
public class CreateNewController {

    @FXML private TextField textFieldCreationName;
    @FXML private Label errorName;

    @FXML private ListView listViewExistCreation;

    private List<String> _CreationsExisted = new ArrayList<String>();
    private ExecutorService team;
    private String _term;
    private ChangeScene _changeSceneObject=new ChangeScene();
    private int _picNum;
    @FXML private ChoiceBox<String> choiceBox;

    public void initData(String term, int picNum){
        _term = term;
        _picNum = picNum;
    }

    /**
     * This method will add the existing creation to the ListView
     */
    public void initialize() throws IOException {
        ObservableList list=FXCollections.observableArrayList();
        list.addAll("Clouds","Fingers", "Sun", "No music");
        choiceBox.getItems().addAll(list);
        errorName.setVisible(false);

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
     * When the button is pressed, we will return back to the menu.
     * @param event
     * @throws IOException
     */
    @FXML
    public void returnToStart(ActionEvent event) throws IOException {
        _CreationsExisted.clear();
        //_changeSceneObject.changeScene(event, "resources/menu.fxml","Main Menu");
        Main.getController().setTOPVIEW(SceneFXML.MENU.toString());
    }

    private Boolean checkEmptyAudio(){
        File directoryCheck = new File(PathIs.TEMP + "/audioPiece");
        String[] files = directoryCheck.list();
        if (files.length > 0){
            return false;
        } else {
            return true;
        }
    }

    /***
     * When the user hits the create button, the user will be taken back to the menu whilst the work is done in
     * background threads. The method also checks for the validity of the name.
     * @param event
     * @throws IOException
     */
    @FXML
    public void EnterCreation(ActionEvent event) throws Exception {
        if (checkEmptyAudio()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No audio to combine");
            alert.setHeaderText("Go back and make audios ");
            alert.setContentText("Make audio first");
            alert.showAndWait();

            EditTextController controller = (EditTextController) Main.getController().setTOPVIEW(SceneFXML.AUDIO.toString());
            controller.initData(_term);

        } else {
            errorName.setVisible(false);

            Boolean error = false;
            /*try {
                Integer num = Integer.parseInt(textFldImagesNum.getText()); /////Check for picture number error input.
                if (num < 0 || num > 10) {
                    errorImg.setVisible(true);
                    errorImg.setText("Please enter between 0-10");
                    textFldImagesNum.clear();
                    error = true;
                }
            } catch (NumberFormatException e) {
                errorImg.setVisible(true);
                errorImg.setText("Enter a valid input.");
                textFldImagesNum.clear();
                error = true;
            }*/
            try {
                if (_CreationsExisted.contains(textFieldCreationName.getText())) { /////Check for creation name error input.
                    errorName.setText("Duplicated name.");
                    Alert overwrite = new Alert(Alert.AlertType.CONFIRMATION);
                    overwrite.setTitle("Duplicated Name");
                    overwrite.setHeaderText("Would you like to overwrite " + textFieldCreationName.getText() + "?");
                    overwrite.setContentText("OK: overwrite. Cancel: retry your name, or you can choose to go back to the menu.");

                    overwrite.showAndWait();
                    if (overwrite.getResult() == ButtonType.OK) {
                    } else {
                        return;
                    }

                } else if (textFieldCreationName.getText().trim().isEmpty()||textFieldCreationName.getText()==null){
                    errorName.setText("please enter a name");
                } else if (!textFieldCreationName.getText().matches("[a-zA-Z0-9_-]*")) {
                    errorName.setVisible(true);
                    errorName.setText("Enter a-z chara name only");
                    textFieldCreationName.clear();
                    error = true;
                }
            } catch (Exception e) {
                //errorImg.setVisible(true);
                //errorImg.setText("Enter a valid input.");
                textFieldCreationName.clear();
                error = true;
            }

            if (error) { //Return if there is an error; guide user to reenter the fields.
                return;
            }

            //Notify user of the creation wait.
            Alert complete = new Alert(Alert.AlertType.INFORMATION);
            complete.setHeaderText("Creating...");
            complete.setContentText(textFieldCreationName.getText() + " is being created. Please wait, we will notify you.");
            complete.show();

            createDirectories(); //Create necessary directories if they have not existed yet.

            //Send creation work to background thread to create the final creation...
            CreationWork creationWork = null;
            creationWork = new CreationWork(_term, textFieldCreationName.getText(), _picNum, true,choiceBox.getValue());

            team.submit(creationWork);

            creationWork.setOnSucceeded(workerStateEvent -> {
                try {
                    String p = "\"" + PathIs.EXTRA + "/" + _term + "/" + textFieldCreationName.getText() + "/";
                    new BashCommand().bash("touch " + p + "confidence.txt\" " + p + "plays.txt\"");
                    team.submit(new Confidence(textFieldCreationName.getText(), 0));
                    team.submit(new Play(textFieldCreationName.getText()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    cleanUp(); //Clean up audio files after the Creation has been made.
                } catch (Exception e) {
                    e.printStackTrace();
                }

                _CreationsExisted.clear();
                Alert complete1 = new Alert(Alert.AlertType.INFORMATION);
                complete1.setHeaderText("Created");
                complete1.setContentText(textFieldCreationName.getText() + " has been created. You can now view.");
                complete1.show();

                textFieldCreationName.clear();

                /*FlickrWork getImg = new FlickrWork(_term, textFieldCreationName.getText(), textFldImagesNum.getText());
team.submit(getImg);

//When images have been successfully retrieved, send an instance of CreationWrok to the background thread to combine audio, slideshow and video forms into one Creation.
getImg.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
@Override
public void handle(WorkerStateEvent workerStateEvent) {

CreationWork creationWork = null;
try {
creationWork = new CreationWork(_term, textFieldCreationName.getText(), Integer.parseInt(getImg.get()), true,choiceBox.getValue());
//System.out.println("pic: " + getImg.get());
} catch (InterruptedException e) {
e.printStackTrace();
} catch (ExecutionException e) {
e.printStackTrace();
}

team.submit(creationWork);

creationWork.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
@Override
public void handle(WorkerStateEvent workerStateEvent) {
try {
    String p = "\"" + PathIs.EXTRA + "/" + _term + "/" + textFieldCreationName.getText() + "/";
    new BashCommand().bash("touch " + p + "confidence.txt\" " + p + "plays.txt\"");
    team.submit(new Confidence(textFieldCreationName.getText(), 0));
    team.submit(new Play(textFieldCreationName.getText()));
} catch (Exception e) {
    e.printStackTrace();
}
try {
    cleanUp(); //Clean up audio files after the Creation has been made.
} catch (Exception e) {
    e.printStackTrace();
}

_CreationsExisted.clear();
Alert complete = new Alert(Alert.AlertType.INFORMATION);
complete.setHeaderText("Created");
complete.setContentText(textFieldCreationName.getText() + " has been created. You can now view.");
complete.show();

textFieldCreationName.clear();
textFldImagesNum.clear();
}
});
}
});*/
                try {
                    Main.getController().setTOPVIEW(SceneFXML.MENU.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * Create required folders to store the audio, video and pictures for creating the final Creation.
     * @throws IOException
     */
    private void createDirectories() throws Exception {
        new BashCommand().bash("mkdir -p \"" + PathIs.EXTRA + "/" + _term + "/" + textFieldCreationName.getText() + "\"" +
                " ; mkdir -p \"" + PathIs.CREATIONS + "/" + _term + "\""); //create a creations folders
    }

    /**
     * Clean up audio files after creation.
     */
    private void cleanUp() throws Exception {
        String command = "cd \"" + PathCD.getPathInstance().getPath() + "/mydir\" ; rm -rf .temp/audioPiece/* ; cd -";
        new BashCommand().bash(command);
    }

    //String createMusicCommand="ffmpeg -i ./src/music/groovy-music.mp3 -acodec pcm_u8 -ar 16000 ./myaudio/song.wav";
    //String combineBackGroundMusic="sox -m ./myaudio/sound.wav ./myaudio/song.wav ./myaudio/out.wav trim 0 "+seconds

    /*public void moveUp()
        foreach (ListViewItem lvi in audioList.SelectedItems)
        {
            if (lvi.Index > 0)
            {
                int index = lvi.Index - 1;
                sourceListView.Items.RemoveAt(lvi.Index);
                sourceListView.Items.Insert(index, lvi);
            }
        }
    }*/
}


