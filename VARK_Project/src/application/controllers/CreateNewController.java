package application.controllers;

import application.FlickrWork;
import application.Main;
import application.PathCD;
import application.TransportClass;
import com.sun.jdi.connect.Transport;
import javafx.application.Platform;
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
import javafx.stage.Stage;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreateNewController {

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
    private URL location;
    @FXML
    private ResourceBundle resources;

    private List<String> _CreationsExisted = new ArrayList<String>();
    private ExecutorService team;

    /**
     * This method will add the existing creation to the ListView
     */
    public void initialize() {
        team = Executors.newSingleThreadExecutor();

        String command = "ls \"" + PathCD.getPathInstance().getPath() + "/mydir/creations\" " + " | sort | cut -f1 -d'.'\n";
        ProcessBuilder builder = new ProcessBuilder("bash", "-c", command);
        try {
            String line;
            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));


            while ((line = reader.readLine()) != null) {
                _CreationsExisted.add(line);
            }
            listViewExistCreation.getItems().addAll(_CreationsExisted);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void returnToStart(ActionEvent event) throws IOException {

        _CreationsExisted.clear();

        Parent createView = FXMLLoader.load(Main.class.getResource("resources/menu.fxml"));
        Scene createViewScene = new Scene(createView);
        // gets the Stage information
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setTitle("Main Menu");
        window.setScene(createViewScene);
        window.show();
    }

    @FXML
    public void EnterCreation(ActionEvent event) throws IOException {

        if (_CreationsExisted.contains(textFieldCreationName.getText())) {
            errorName.setText("Duplicated name.");
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Duplicated Name");
            alert.setHeaderText("Would you like to overwrite " + textFieldCreationName.getText() + "?");
            //TODO implement overwrite functionality
            //alert.setContentText("Please rename or go back to the Main Menu!");

            alert.showAndWait();

        } else if (!textFieldCreationName.getText().matches("[a-zA-Z0-9_-]*")) { //TODO check regex
            labelMessage.setText("Some characters are not valid in your name, Please try again!");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("mistake found!");
            alert.setContentText("Please rename or go back to the Main Menu!");

            alert.showAndWait();

        } else {
            FlickrWork getImg = new FlickrWork(TransportClass.getInstance().getter(), textFldImagesNum.getCharacters().toString());
            team.submit(getImg);

            getImg.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent workerStateEvent) {

                    //TODO put in image implementation details.

                    team.submit(new Task<Integer>() {

                        @Override
                        protected Integer call() throws Exception {
                            String term = TransportClass.getInstance().getter();
                            String path = PathCD.getPathInstance().getPath();
                            String soundCommand = "cat \"" + path + "/mydir/extra/lines.txt\" | text2wave -o \"" + path + "/mydir/extra/sound.wav\"";

                            ProcessBuilder sound = new ProcessBuilder("bash", "-c", soundCommand);
                            try {
                                Process sod = sound.start();
                                sod.waitFor();

                                //video
                                String videoCommand = "duration=`soxi -D \"" + path + "/mydir/extra/sound.wav\"` ; ffmpeg -f lavfi -i color=c=blue:s=320x240:d=\"$duration\" "
                                        + "-vf \"drawtext=fontfile=:fontsize=30:fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text=" + "'" + term + "'" + "\" \"" + path + "/mydir/extra/video.mp4\"";

                                ProcessBuilder video = new ProcessBuilder("bash", "-c", videoCommand);
                                Process vid = video.start();
                                vid.waitFor();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            String combineCommand = "ffmpeg -i \"" + path + "/mydir/extra/sound.wav\" -i \"" + path + "/mydir/extra/video.mp4\" -c:v copy -c:a aac -strict experimental \"" + path + "/mydir/creations/" + textFieldCreationName.getText() + ".mp4\" 2>/dev/null";
                            ProcessBuilder getTogether = new ProcessBuilder("bash", "-c", combineCommand);

                            try {
                                Process combine = getTogether.start();
                                combine.waitFor();

                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }

                            Platform.runLater(() -> {
                                _CreationsExisted.clear();
                                Alert complete = new Alert(Alert.AlertType.INFORMATION);
                                complete.setHeaderText("Created");
                                complete.setContentText(textFieldCreationName.getText() + " has been created.");
                                complete.show();

                                //TODO CHECK WORKING DELETION
                                String command = "cd \"" + PathCD.getPathInstance().getPath() + "/mydir\" ; rm -rf extra/* ; cd -"; //Clear files in extra folder.
                                ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);
                                try {
                                    Process end = pb.start();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });

                            return null;
                        }
                    });
                }
            });

            getImg.setOnCancelled(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent workerStateEvent) {
                    errorImg.setText("Invalid number. Please enter between 1-10"); //TODO clear text
                }
            });

            Parent menuParent = null;
            try {
                menuParent = FXMLLoader.load(Main.class.getResource("resources/menu.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Scene createViewScene = new Scene(menuParent);
            // gets the Stage information
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setTitle("Main Menu");
            window.setScene(createViewScene);
            window.show();
        }

    }

    @FXML
    public void getImages(ActionEvent event) {

    }
}


