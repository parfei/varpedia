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
        String path = PathCD.getPathInstance().getPath(); //TODO alphabetical order

        String cmd = "ls"+ " \""+ path + "/mydir/creations\""+ " | sort | cut -f1 -d'.'\n";
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

    @FXML
    public void getTheSelection(javafx.scene.input.MouseEvent mouseEvent) {
        errorText.setVisible(false);
        try{
            ObservableList selectedCreation = stuffCreated.getSelectionModel().getSelectedItems();
            _choice = selectedCreation.get(0).toString();
        }catch (Exception e){
        }
    }

    @FXML
    public void playVideo(ActionEvent event)throws IOException{ //TODO fix mediaplayer
        view.setVisible(true);

        if(_choice!=null) {

            File file = new File(PathCD.getPathInstance().getPath() + "/mydir/creations/" + _choice + ".mp4");
            _player = new MediaPlayer(new Media(file.toURI().toString()));
            _player.setAutoPlay(true);

            _player.setOnEndOfMedia(new Runnable() {
                @Override
                public void run() {
                    playOptions.setVisible(false);
                    view.setVisible(false);
                }
            });

            view.setMediaPlayer(_player);
            _player.play();
            view.setVisible(true);

            playOptions.setVisible(true);

            /*String cmd = "ffplay -autoexit \""+ PathCD.getPathInstance().getPath() + "/mydir/creations/" +_choice+".mp4\"";
            System.out.println(cmd);
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
            try {
                Process process = pb.start();
            } catch (IOException ex) {
                ex.printStackTrace();
            }*/

        }else{
            errorText.setVisible(true);
            errorText.setText("Select something to play."); //If user has not selected anything, prompt them.
        }
    }

    @FXML
    public void videoPlay(ActionEvent event){
        if (_player.getStatus().equals(MediaPlayer.Status.PLAYING)  ){
            String btn = ((Button)event.getSource()).getText();
            if (btn.equals("Pause")){
                _player.pause();
            } else if (btn.equals("Stop")){
                _player.stop();
                view.setVisible(false);
            } else if (btn.equals("<< 10")){
                _player.seek(new Duration(_player.getCurrentTime().toMillis() + 10000));
            } else if (btn.equals("10 >>")){
                _player.seek(new Duration(_player.getCurrentTime().toMillis() - 10000));
            } else{
                //TODO implement resume functionality, mute functionality
            }
        }else{
            errorText.setVisible(true);
            errorText.setText("There is nothing playing at the moment.");
        }
    }

    @FXML
    public void deleteVideo(ActionEvent event)throws IOException{

        if(_choice!=null){

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Delete");
            alert.setHeaderText("Check again!");
            alert.setContentText("Are you sure to delete this?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){

                String cmd= "rm \"" + PathCD.getPathInstance().getPath() + "mydir/creations/"+_choice+".mp4\"";
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
