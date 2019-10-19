package application.controllers;
import application.PathCD;
import application.bashwork.BashCommand;
import application.bashwork.ManageFolder;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.util.Duration;

/**
 * Controller for the view window of the application.
 */
public class ViewController {
    private String _choice;
    private MediaPlayer _player;
    private ExecutorService team = Executors.newSingleThreadExecutor();

    @FXML private ListView stuffCreated;
    @FXML private Label errorText;
    @FXML private MediaView view;
    @FXML private ButtonBar playOptions;
    @FXML private Button playButton;
    @FXML private Button muteButton;
    @FXML private CheckBox favOption;
    @FXML private Slider confidence;

    /**
     * This method will add the existing creation to the ListView
     */
    public void initialize() throws Exception {//TODO concurrency for this??
        getCreations("creations");
        errorText.setVisible(false);
        playButton.setDisable(true);
        playOptions.setDisable(true);
        muteButton.setDisable(true);
    }

    /**
     * When click this button, it will go back to the main menu
     * @param event
     * @throws IOException
     */
    /*@FXML
    public void backToMain(ActionEvent event) throws IOException {
        creation.backToMain(event);
    }*/

    /**
     * Retrieve the user selection of the ListView.
     * @param mouseEvent
     */
    @FXML
    public void getTheSelection(javafx.scene.input.MouseEvent mouseEvent) {
        try{
            ObservableList selectedCreation = stuffCreated.getSelectionModel().getSelectedItems();
            _choice = selectedCreation.get(0).toString();
            if (_choice != null){
                playButton.setDisable(false);

                File file = new File(ManageFolder.findPath(_choice, true));
                _player = new MediaPlayer(new Media(file.toURI().toString()));
                //_player.setAutoPlay(true);

                _player.setOnEndOfMedia(new Runnable() { //When the player ends...
                    @Override
                    public void run() {
                        resetPlayer();
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
            errorText.setVisible(true);
            return;
        }

        stuffCreated.setDisable(true);

        errorText.setVisible(false);
        muteButton.setDisable(false);
        playOptions.setDisable(false); //Show the video manipulation options.

        if (_player.getStatus().equals(MediaPlayer.Status.PLAYING)) {
            _player.pause();
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
    public void videoPlay(ActionEvent event){ //TODO if user presses something else
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
            muteButton.setText("!Mute");
        } else if (btnText.equals("!Mute")){
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

                String path = ManageFolder.findPath(_choice, true); //finds the relevant creation

                String cmd= "rm -f \"" + path + "\""; //TODO check...
                System.out.println(cmd);
                ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
                try {
                    Process process = pb.start();
                    process.waitFor();
                } catch (IOException | InterruptedException ex) {
                    ex.printStackTrace();
                }
                tickFav(new ActionEvent());

            } else if (result.get() == ButtonType.CANCEL){
                return;
            }
        } else {
            errorText.setVisible(true);
        }
    }

    @FXML
    public void changeConfidence(ActionEvent event) throws IOException {
        int rating = (int) confidence.getValue();

    }

    @FXML
    public void favourite(ActionEvent event) throws Exception { //TODO implement remove favourites, button changes when this option is ticked.
        if (_choice != null){
            errorText.setVisible(false);

            String file = "\"" + ManageFolder.findPath(_choice, true) + "\"";
            String file2 = "\"" + PathCD.getPathInstance().getPath() + "/mydir/creations/favourites/" + _choice + ".mp4\"";

            resetPlayer();
            String command = "mv " + file + " " + file2;

            System.out.println(command);

            ProcessBuilder move = new ProcessBuilder("bash", "-c", command);
            try {
                Process process = move.start();
                process.waitFor();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            errorText.setVisible(true);
        }
    }

    @FXML
    public void tickFav(ActionEvent event) throws Exception {
        List<String> list = null;
        if (favOption.isSelected()){
            getCreations("favourites");
        } else {
            getCreations("creations");
        }
    }

    private void getCreations(String path) throws Exception { //TODO USE THIS!
        team.submit(new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                BashCommand list = new BashCommand();
                ArrayList<String> creations = list.bash("ls -R"+ " \""+ PathCD.getPathInstance().getPath() + "/mydir/creations" + path + "\""+ " | grep .mp4 | cut -f1 -d'.' | sort");
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        stuffCreated.getItems().clear();
                        stuffCreated.getItems().setAll(creations);
                    }
                });
                return null;
            }
        });
    }

    private void resetPlayer(){
        muteButton.setDisable(true);
        playOptions.setDisable(true);
        muteButton.setText("Mute");
        playButton.setText("Play");
        _choice = null;
        view.setDisable(true);
        _player.dispose();

        stuffCreated.setDisable(false);
    }

}
