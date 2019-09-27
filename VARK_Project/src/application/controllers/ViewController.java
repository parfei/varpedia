package application.controllers;
import application.Main;
import application.PathCD;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.util.Duration;

/**
 * Controller for the view window of the application.
 */
public class ViewController {
    private List <String > innovation = new ArrayList<String>();
    private String _choice;
    private MediaPlayer _player;

    // create an object of CreateController to use to go back to main menu method
    private CreationController creation = new CreationController();

    @FXML
    private ListView stuffCreated;

    @FXML
    private ButtonBar playOptions;
    @FXML
    private Button playButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Text errorText;

    @FXML
    private MediaView view;
    @FXML
    private Button pauseButton;
    @FXML
    private Button timeBack;
    @FXML
    private Button timeForward;

    @FXML
    private URL location;
    @FXML
    private ResourceBundle resources;

    /**
     * This method will add the existing creation to the ListView
     */
    public void initialize()
    {
        String path = PathCD.getPathInstance().getPath();

        String cmd = "ls -R"+ " \""+ path + "/mydir/creations\""+ " | grep .mp4 | cut -f1 -d'.' | sort";
        ProcessBuilder initializing = new ProcessBuilder("bash","-c",cmd);
        try{
            Process process = initializing.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                innovation.add(line);
            }
            stuffCreated.getItems().addAll(innovation);
        }catch (IOException ex) {
            ex.printStackTrace();
        }

        playOptions.managedProperty().bind(playOptions.visibleProperty());
        playOptions.setVisible(false);
    }

    /**
     * When click this button, it will go back to the main menu
     * @param event
     * @throws IOException
     */
    @FXML
    public void backToMain(ActionEvent event) throws IOException {
        creation.backToMain(event);
    }

    /**
     * Retrieve the user selection of the ListView.
     * @param mouseEvent
     */
    @FXML
    public void getTheSelection(javafx.scene.input.MouseEvent mouseEvent) {
        errorText.setVisible(false);
        try{
            ObservableList selectedCreation = stuffCreated.getSelectionModel().getSelectedItems();
            _choice = selectedCreation.get(0).toString();
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
        view.setVisible(true);

        if(_choice!=null) {

            File file = new File(PathCD.getPathInstance().getPath() + "/mydir/creations/" + _choice + ".mp4"); //TODO FIX
            _player = new MediaPlayer(new Media(file.toURI().toString()));
            _player.setAutoPlay(true);

            _player.setOnEndOfMedia(new Runnable() {
                @Override
                public void run() {
                    playOptions.setVisible(false);
                    view.setVisible(false);
                    pauseButton.setText("Pause"); //TODO do similarly for mute button
                }
            });

            view.setMediaPlayer(_player);
            _player.play();
            view.setVisible(true);

            playOptions.setVisible(true);

        }else{
            errorText.setVisible(true);
            errorText.setText("Select something to play."); //If user has not selected anything, prompt them.
        }
    }

    /**
     * A method that will execute when one of the embedded video player buttons are pressed. Depending on the type of button pressed,
     * a different piece of code will execute.
     * @param event
     */
    @FXML
    public void videoPlay(ActionEvent event){
        Button btn = (Button)event.getSource();
        String btnText = btn.getText();
        if (_player.getStatus().equals(MediaPlayer.Status.PLAYING)){
            if (btnText.equals("Pause")){
                _player.pause();
                btn.setText("Resume");
            } else if (btnText.equals("Stop")){
                _player.stop();
                view.setVisible(false);
            } else if (btnText.equals("<< 10")){
                _player.seek(new Duration(_player.getCurrentTime().toMillis() + 10000));
            } else if (btnText.equals("10 >>")){
                _player.seek(new Duration(_player.getCurrentTime().toMillis() - 10000));
            } else if (btnText.equals("Mute")){
                _player.setMute(true);
                btn.setText("Unmute");
            } else if (btnText.equals("Unmute")){
                _player.setMute(false);
                btn.setText("Mute");
            }
        }else{
            if (btnText.equals("Resume")){
                _player.play();
                btn.setText("Pause");
            } else if (btnText.equals("Stop")) {
                _player.stop();
                view.setVisible(false);
            }else {
                errorText.setVisible(true);
                errorText.setText("There is nothing playing at the moment.");
            }
        }
    }

    /**
     * Will execute to delete a video when requested by the user.
     * @param event
     * @throws IOException
     */
    @FXML
    public void deleteVideo(ActionEvent event)throws IOException{

        if(_choice!=null){

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Delete");
            alert.setHeaderText("Check again!");
            alert.setContentText("Are you sure to delete this?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){

                String cmd= "rm \"" + PathCD.getPathInstance().getPath() + "/mydir/creations/"+_choice+".mp4\""; //TODO FIX
                ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
                try {
                    Process process = pb.start();

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                innovation.clear();
                stuffCreated.getItems().clear();
                this.initialize();

            } else {
                errorText.setVisible(true);
                errorText.setText("Select something to delete.");
            }
        }
    }

}
