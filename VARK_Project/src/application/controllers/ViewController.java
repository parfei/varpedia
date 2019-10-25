package application.controllers;
import application.Confidence;
import application.PathCD;
import application.Play;
import application.bashwork.BashCommand;
import application.bashwork.ManageFolder;
import application.listeners.CreationListCell;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.util.Callback;
import javafx.util.Duration;

/**
 * Controller for the view window of the application.
 */
public class ViewController {
    private String _choice;
    private MediaPlayer _player;
    private ExecutorService team = Executors.newSingleThreadExecutor();

    @FXML private ListView stuffCreated;

    @FXML private MediaView view;
    @FXML private ButtonBar playOptions;
    @FXML private Button playButton;
    @FXML private Button muteButton;
    @FXML private CheckBox favOption;
    @FXML private Slider confidence;
    @FXML private Button favBtn;
    @FXML private HBox creationOptions;

    /**
     * This method will add the existing creation to the ListView
     * It will also set the cell factory for stuffcreated listview.
     */
    public void initialize() throws Exception {//TODO concurrency for this??
        team.submit(() -> {
            try {
                setCreations("creations");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        creationOptions.setDisable(true);
        playButton.setDisable(true);
        playOptions.setDisable(true);
    }

    /**
     * Retrieve the user selection of the ListView.
     * @param mouseEvent
     */
    @FXML
    public void getTheSelection(javafx.scene.input.MouseEvent mouseEvent) throws Exception {

        try{
            ObservableList selectedCreation = stuffCreated.getSelectionModel().getSelectedItems();
            _choice = selectedCreation.get(0).toString();
            if (_choice != null){
                playButton.setDisable(false);
                creationOptions.setDisable(false);

                File file = new File(ManageFolder.findPath(_choice, true));
                _player = new MediaPlayer(new Media(file.toURI().toString())); //Set up player to be played.

                //Get confidence rating from file.
                int rate = Integer.parseInt(ManageFolder.readFile(ManageFolder.findPath(_choice, false) + "/confidence.txt"));
                if (rate == 0){
                    confidence.setValue(1);
                } else {
                    confidence.setValue(rate); //Set up confidence for viewing.
                }

                //When the player ends...
                _player.setOnEndOfMedia(() -> {
                    try {
                        team.submit(new Play(_choice));
                        setColourImmediately();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                view.setDisable(false);
                view.setMediaPlayer(_player);

                /*String cmd = "ffplay -autoexit \"" + path "\"";
            System.out.println(cmd);
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
            try {
                Process process = pb.start();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            }else{
            }*/
            }
        }catch (Exception e){
        }
    }

    /**
     * Play the video when the button "Play" is clicked.
     * @param event
     * @throws IOException
     */
    @FXML
    public void playVideo(ActionEvent event)throws IOException{
        if (_choice == null){
            return;
        }

        view.setVisible(true);
        stuffCreated.setDisable(true);
        playOptions.setDisable(false); //Show the video manipulation options.

        if (_player.getStatus().equals(MediaPlayer.Status.PLAYING)) {
            _player.pause();
            stuffCreated.setDisable(false);
            playButton.setText("Play");
        } else {
            _player.play();
            playButton.setText("Pause");
        }
    }

    /**
     * A method that will execute when one of the embedded video player buttons are pressed. Depending on the type of button pressed,
     * a different piece of code will execute.
     * @param event
     */
    @FXML
    public void videoPlay(ActionEvent event) throws Exception { //TODO if user presses something else
        String btnText = ((Button)event.getSource()).getText(); //Get button pressed's text

        if (btnText.equals("<< 10")){ //Regardless if playing status or not...
            _player.play();
            _player.seek(new Duration(_player.getCurrentTime().toMillis() - 10000));
        } else if (btnText.equals("10 >>")){
            _player.play();
            _player.seek(new Duration(_player.getCurrentTime().toMillis() + 10000));
        } else if (btnText.equals("Stop")){
            _player.stop();
            resetPlayer();
        } else if (btnText.equals("Mute")){
            _player.setMute(true);
            muteButton.setText("Unmute");
        } else if (btnText.equals("Unmute")){
            _player.setMute(false);
            muteButton.setText("Mute");
        }
    }

    /**
     * Will execute to delete a video when requested by the user.
     * @param event
     * @throws IOException
     */
    @FXML
    public void deleteVideo(ActionEvent event) throws Exception {
        if(_choice!=null){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Delete");
            alert.setHeaderText("Check again!");
            alert.setContentText("Are you sure to delete this?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                resetPlayer();

                team.submit(new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        String path = ManageFolder.findPath(_choice, true); //finds the relevant creation
                        String command = "rm -f \"" + path + "\"";
                        new BashCommand().bash(command);

                        Platform.runLater(() -> {
                            _choice = null;
                            playButton.setDisable(true);
                            creationOptions.setDisable(true);

                            try {
                                tickFav(); //get the list of creations for currently ticked option.
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        return null;
                    }
                });
            } else if (result.get() == ButtonType.CANCEL){
                return;
            }
        } else {
        }
    }

    @FXML
    public void changeConfidence(javafx.scene.input.MouseEvent mouseEvent) throws Exception {
        int rating = (int) confidence.getValue();
        Confidence change = new Confidence(_choice, rating);
        team.submit(change);

        change.setOnSucceeded(workerStateEvent -> {
            try {
                setColourImmediately();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    @FXML
    public void favourite(ActionEvent event) throws Exception { //TODO implement remove favourites, button changes when this option is ticked.
        if (_choice != null){

            String file = "\"" + ManageFolder.findPath(_choice, true) + "\"";
            String file2 = "\"" + PathCD.getPathInstance().getPath() + "/mydir/creations/favourites/" + _choice + ".mp4\"";

            resetPlayer();
            String command = "mv " + file + " " + file2;

            new BashCommand().bash(command);

        } else {
        }
        tickFav();
    }

    /**
     * Load the creations based on the favourites checkbox- if ticked, load in favourites only. If not ticked, load in all creations.
     * @throws Exception
     */
    @FXML
    public void tickFav() throws Exception {
        if (favOption.isSelected()){
            setCreations("favourites");
        } else {
            setCreations("creations");
        }
    }

    /**
     * If user wants to clear data for plays and confidence rating, then pressing this button resets data saved in the text files.
     * @throws Exception
     */
    @FXML
    public void clearDataForCreation() throws Exception {
        if (_choice != null){
            ManageFolder.writeToFile(ManageFolder.findPath(_choice, false) + "/plays.txt", "0");
            ManageFolder.writeToFile(ManageFolder.findPath(_choice, false) + "/confidence.txt", "0");
            setColourImmediately();
        }
    }

    private void setCreations(String path) throws Exception { //TODO USE THIS!
        ArrayList<String> creations = null;
        try {
            creations = ManageFolder.getCreations(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        stuffCreated.getItems().clear();
        stuffCreated.getItems().setAll(creations);
        stuffCreated.setCellFactory((Callback<ListView<String>, ListCell<String>>) param -> new CreationListCell());
    }

    private void resetPlayer() throws Exception {
        muteButton.setDisable(true);
        playOptions.setDisable(true);
        muteButton.setText("Mute");
        playButton.setText("Play");

        view.setDisable(true);
        _player.stop();
        _player.dispose();

        stuffCreated.setDisable(false);
        tickFav();
    }

    /**
     * Method called everytime a video changes confidence or view count. Check whether item needs reviewing or not.
     * Code inspired from: https://stackoverflow.com/questions/20936101/get-listcell-via-listview
     */
    private void setColourImmediately() throws Exception {
        int index = stuffCreated.getSelectionModel().getSelectedIndex();
        Object[]cells = stuffCreated.lookupAll(".cell").toArray();
        Cell cell = (Cell) cells[index];

        String confidence = ManageFolder.readFile(ManageFolder.findPath(_choice, false) + "/confidence.txt");
        String plays = ManageFolder.readFile(ManageFolder.findPath(_choice, false) + "/plays.txt");

        String style = "-fx-background-color:";

        if (Integer.parseInt(plays) == 0){ //If video has never been played.
            style += "#93D4EE;";
        } else if (Integer.parseInt(confidence) < 3){ //If confidence is below 3
            style += "orange;";
        } else {
            style += "#80B8F0;";
        }

        cell.setStyle(style);
    }

}
