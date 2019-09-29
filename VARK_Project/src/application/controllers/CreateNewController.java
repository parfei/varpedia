package application.controllers;

import application.*;
import com.sun.jdi.connect.Transport;
import javafx.application.Platform;
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
import javafx.stage.Stage;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private URL location;
    @FXML
    private ResourceBundle resources;

    private List<String> _CreationsExisted = new ArrayList<String>();
    private ExecutorService team;
    private String _term;


    public void initData(String term){
        _term = term;
    }

    /**
     * This method will add the existing creation to the ListView
     */
    public void initialize() throws IOException {


        errorName.setVisible(false);
        errorImg.setVisible(false);
        team = Executors.newSingleThreadExecutor();

        String listAudio = "ls -tcr " + PathCD.getPathInstance().getPath() + "/mydir/extra/audioPiece" + " | cut -f1 -d'.'\n";
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
        if (_audioExisted.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No audio to combine");
            alert.setHeaderText("Go back and make audios ");
            alert.setContentText("Make audio first");
            alert.showAndWait();
            Parent createView = FXMLLoader.load(Main.class.getResource("resources/EditText.fxml"));
            Scene createViewScene = new Scene(createView);
            // gets the Stage information
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setTitle("Edit Text Menu");
            window.setScene(createViewScene);
            window.show();

        }
        if (textFldImagesNum.getText().isEmpty() || textFldImagesNum.getText() == null || textFieldCreationName.getText().isEmpty() || textFieldCreationName.getText() == null) {
            errorImg.setVisible(true);
            errorImg.setText("Complete the fields!");
            return;
        }
        errorImg.setVisible(false);
        errorName.setVisible(false);

        Boolean error = false;

        try {
            Integer num = Integer.parseInt(textFldImagesNum.getText());
            if ( num <= 0 || num > 10 ){
                errorImg.setVisible(true);
                errorImg.setText("Please enter between 1-10");
                error = true;
            }
        } catch (NumberFormatException e){
            errorImg.setVisible(true);
            errorImg.setText("Enter a valid input.");
            error = true;
        }

        try {
            if (_CreationsExisted.contains(textFieldCreationName.getText())) {
                errorName.setText("Duplicated name.");
                Alert overwrite = new Alert(Alert.AlertType.CONFIRMATION);
                overwrite.setTitle("Duplicated Name");
                overwrite.setHeaderText("Would you like to overwrite " + textFieldCreationName.getText() + "?");
                overwrite.setContentText("OK: overwrite. Cancel: retry your name, or you can choose to go back to the menu.");

                overwrite.showAndWait();
                if (overwrite.getResult() == ButtonType.OK) {
                }else{
                    return;
                }
            } else if (!textFieldCreationName.getText().matches("[a-zA-Z0-9_-]*")) {
                errorName.setVisible(true);
                errorName.setText("Enter a-z chara name only");
                error = true;
            }
        } catch (Exception e){
            errorImg.setVisible(true);
            errorImg.setText("Enter a valid input.");
            error = true;
        }

        if (error){
            textFieldCreationName.clear();
            textFldImagesNum.clear();
            return;
        }

        createDirectories();

        FlickrWork getImg = new FlickrWork(_term, textFieldCreationName.getCharacters().toString(), textFldImagesNum.getCharacters().toString());
        team.submit(getImg);

        getImg.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {

                CreationWork creationWork = null;
                try {
                    creationWork = new CreationWork(_term, textFieldCreationName.getText(), Integer.parseInt(getImg.get()), true);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                System.out.println("pic: " + Integer.parseInt(textFldImagesNum.getText()));
                team.submit(creationWork);

                creationWork.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent workerStateEvent) {

                        cleanUp();

                        _CreationsExisted.clear();
                        Alert complete = new Alert(Alert.AlertType.INFORMATION);
                        complete.setHeaderText("Created");
                        complete.setContentText(textFieldCreationName.getText() + " has been created.");
                        complete.show();
                    }
                });

            }
        });

        textFieldCreationName.clear();
        textFldImagesNum.clear();

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

    private void createDirectories() throws IOException {
        String path = PathCD.getPathInstance().getPath();
        System.out.println("Creating directories for: " +_term);
        String command2 = "mkdir -p \"" + path + "/mydir/extra/" + _term + "/" + textFieldCreationName.getCharacters().toString() + "\"" +
                " ; mkdir -p \"" + path + "/mydir/creations/" + _term + "\""; //create a creations folders
        ProcessBuilder pb2 = new ProcessBuilder("/bin/bash", "-c", command2);
        pb2.start();
    }

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
    public void getTheSelection(){
        try{
            ObservableList selectedCreation = audioList.getSelectionModel().getSelectedItems();
            _choice = selectedCreation.get(0).toString();
        }catch (Exception e){
        }
    }


}


