package application.controllers;

import application.*;
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

/**
 * We create a new creation in this class. Name checking is done here. The bulk of the video creation is called from here.
 * We call new instances of the work to be done in a background thread. Can return back to the menu from here.
 */
public class CreateNewController {


    private String _choice;
    private List<String> _audioExisted = new ArrayList<String>();

    @FXML
    private ListView audioList;
    @FXML
    private Label labelMessage;
    @FXML
    private TextField textFieldCreationName;
    @FXML
    private TextField textFldImagesNum;
    @FXML
    private Label errorName;
    @FXML
    private Label errorImg;

    @FXML
    private ListView listViewExistCreation;
    @FXML
    private Label remindLabel;

    @FXML
    private Button playButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button enterCreationButton;

    @FXML
    private URL location;
    @FXML
    private ResourceBundle resources;

    private List<String> _CreationsExisted = new ArrayList<String>();
    private ExecutorService team;
    private String _term;
    private ChangeScene _changeSceneObject=new ChangeScene();
    @FXML
    private ChoiceBox<String> choiceBox;




    public void initData(String term){
        _term = term;
    }

    /**
     * This method will add the existing creation to the ListView
     */
    public void initialize() throws IOException {



        ObservableList list=FXCollections.observableArrayList();
        list.addAll("Yes","No");
        choiceBox.getItems().addAll(list);
        playButton.setDisable(true);
        deleteButton.setDisable(true);
        errorName.setVisible(false);
        errorImg.setVisible(false);

        team = Executors.newSingleThreadExecutor();

        String listAudio = "ls \"" + PathCD.getPathInstance().getPath() + "/mydir/extra/audioPiece\"" + " | cut -f1 -d'.'\n";
        ProcessBuilder builder = new ProcessBuilder("bash", "-c", listAudio);
        try {
            String line;
            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));


            while ((line = reader.readLine()) != null) {
                _audioExisted.add(line);
            }
            audioList.getItems().addAll(_audioExisted);

        } catch (IOException e) {
            e.printStackTrace();
        }

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



    /***
     * When the user hits the create button, the user will be taken back to the menu whilst the work is done in
     * background threads. The method also checks for the validity of the name.
     * @param event
     * @throws IOException
     */
    @FXML
    public void EnterCreation(ActionEvent event) throws IOException {
        if (choiceBox.getValue()==null){
            remindLabel.setText("DO YOU WANT TO INCLUDE MUSIC?");
        }

        else if (_audioExisted.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No audio to combine");
            alert.setHeaderText("Go back and make audios ");
            alert.setContentText("Make audio first");
            alert.showAndWait();

            EditTextController controller = (EditTextController) Main.getController().setTOPVIEW(SceneFXML.AUDIO.toString());
            controller.initData(_term);

        }
        else {

            errorImg.setVisible(false);
            errorName.setVisible(false);

            Boolean error = false;

            try {
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
            }

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
                errorImg.setVisible(true);
                errorImg.setText("Enter a valid input.");
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

                //Send an instance of FlickrWork to the background thread to retrieve the images from Flickr.
                FlickrWork getImg = new FlickrWork(_term, textFieldCreationName.getText(), textFldImagesNum.getText());
                team.submit(getImg);

                //When images have been successfully retrieved, send an instance of CreationWrok to the background thread to
                //combine audio, slideshow and video forms into one Creation.
                getImg.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent workerStateEvent) {

                        CreationWork creationWork = null;
                        try {
                            creationWork = new CreationWork(_term, textFieldCreationName.getText(), Integer.parseInt(getImg.get()), true);
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

                                cleanUp(); //Clean up audio files after the Creation has been made.

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
                });

            Main.getController().setTOPVIEW(SceneFXML.MENU.toString());
        }
    }

    /**
     * Create required folders to store the audio, video and pictures for creating the final Creation.
     * @throws IOException
     */
    private void createDirectories() throws IOException {
        String path = PathCD.getPathInstance().getPath();
        //System.out.println("Creating directories for: " +_term);
        String command2 = "mkdir -p \"" + path + "/mydir/extra/" + _term + "/" + textFieldCreationName.getText() + "\"" +
                " ; mkdir -p \"" + path + "/mydir/creations/" + _term + "\""; //create a creations folders
        ProcessBuilder pb2 = new ProcessBuilder("/bin/bash", "-c", command2);
        pb2.start();
    }

    /**
     * Clean up audio files after creation.
     */
    private void cleanUp() {

        //String command = "cd \"" + PathCD.getPathInstance().getPath() + "/mydir\" ; rm -rf extra/" + TransportClass.getInstance().getter() + "/" + textFieldCreationName.getCharacters().toString() +
        //"/* ; cd -"; //Clear files in extra folder.

        String command = "cd \"" + PathCD.getPathInstance().getPath() + "/mydir\" ; rm -rf extra/audioPiece/* ; cd -";

        ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);
        try {
            Process end = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the selection of the audio list.
     */
    public void getTheSelection(){
        try{
            ObservableList selectedCreation = audioList.getSelectionModel().getSelectedItems();
            _choice = selectedCreation.get(0).toString();
        }catch (Exception e){
        }
        if (_choice!=null){
            playButton.setDisable(false);
            deleteButton.setDisable(false);
        }
    }



    @FXML
    public void playAudio(ActionEvent event){
        String wavFile = findAudio(_choice);
        // String musicFile = "StayTheNight.mp3";     // For example

        Media sound = new Media(new File(wavFile).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }
    @FXML
    public void deleteAudio(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Delete");
        alert.setHeaderText("Check again!");
        alert.setContentText("Are you sure to delete this?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            System.out.println(findAudio(_choice));
            String path = findAudio(_choice); //finds the relevant creation

            String cmd = "rm -f \"" + path + "\"";
            System.out.println(cmd);
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
            try {
                Process process = pb.start();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            _audioExisted.clear();
            audioList.getItems().clear();
            this.initialize();
        }else{
            return;
        }
    }

    private String findAudio(String name){
        String command = "find \"" + PathCD.getPathInstance().getPath() + "/mydir/extra/audioPiece/" + name + ".wav\"";
        System.out.println(command);
        ProcessBuilder find = new ProcessBuilder("bash", "-c", command);
        try {
            Process process = find.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*public int getIndexOfItemInListView(){



        return 1;
    }

    /*public void moveUp(){
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


