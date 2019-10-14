package application.controllers;
import application.Main;
import application.PathCD;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

    @FXML private TableView list;

    @FXML private ListView stuffCreated;
    @FXML private Label errorText;

    @FXML private MediaView view;
    @FXML private ButtonBar playOptions;
    @FXML private Button playButton;
    @FXML private Button deleteButton;
    @FXML private Button muteButton;
    @FXML private Button timeBack;
    @FXML private Button timeForward;

    @FXML private URL location;
    @FXML private ResourceBundle resources;

    /**
     * This method will add the existing creation to the ListView
     */
    public void initialize() //TODO concurrency for this??
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

                File file = new File(findCreation(_choice));
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
    public void deleteVideo(ActionEvent event)throws IOException{

        if(_choice!=null){

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Delete");
            alert.setHeaderText("Check again!");
            alert.setContentText("Are you sure to delete this?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){

                String path = findCreation(_choice); //finds the relevant creation
                resetPlayer();

                String cmd= "rm -f \"" + path + "\"";
                System.out.println(cmd);
                ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
                try {
                    Process process = pb.start();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                innovation.clear();
                stuffCreated.getItems().clear();
                this.initialize();

            } else if (result.get() == ButtonType.CANCEL){
                return;
            }
        } else {
            errorText.setVisible(true);
        }
    }

    private String findCreation(String name){
        String command = "find \"" + PathCD.getPathInstance().getPath() + "/mydir/creations/\"*\"/" + name + ".mp4\"";


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

    @FXML
    public void favourite(ActionEvent event) throws IOException {
        if (_choice != null){
            errorText.setVisible(false);

            String file = "\"" + findCreation(_choice) + "\"";
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


            //File file = new File("\"" + findCreation(_choice) + "\"");
            //File file2 = new File("\"" + PathCD.getPathInstance().getPath() + "/mydir/creations/favourites/" + _choice +".mp4\"");
            //Files.move(Paths.get(file.toURI()), Paths.get(file2.toURI()));
            //Files.move(file.toPath(), new File("\"" + PathCD.getPathInstance().getPath() + "/mydir/creations/favourites/" + _choice +".mp4\"").toPath());
            //file.renameTo(new File("\"" + PathCD.getPathInstance().getPath() + "/mydir/creations/favourites/" + _choice + ".mp4\""));
        } else {
            errorText.setVisible(true);
        }
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
