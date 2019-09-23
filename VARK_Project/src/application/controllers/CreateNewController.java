package application.controllers;

import application.*;
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
    private Boolean _overwrite;

    /**
     * This method will add the existing creation to the ListView
     */
    public void initialize() {
        errorName.setVisible(false);
        errorImg.setVisible(false);
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
        if (textFldImagesNum.getText().isEmpty() || textFldImagesNum.getText() == null || textFieldCreationName.getText().isEmpty() || textFieldCreationName.getText() == null){
            errorImg.setVisible(true);
            errorImg.setText("Complete the fields!");
            return;
        }

        Integer num = Integer.parseInt(textFldImagesNum.getText());

        if (_CreationsExisted.contains(textFieldCreationName.getText())) {
            errorName.setText("Duplicated name.");
            Alert overwrite = new Alert(Alert.AlertType.CONFIRMATION);
            overwrite.setTitle("Duplicated Name");
            overwrite.setHeaderText("Would you like to overwrite " + textFieldCreationName.getText() + "?");
            overwrite.setContentText("OK: overwrite. Cancel: retry your name, or you can choose to go back to the menu.");

            overwrite.showAndWait();
            if (overwrite.getResult() == ButtonType.OK){
                _overwrite = true;
            }

        } else if (!textFieldCreationName.getText().matches("[a-zA-Z0-9_-]*")) {
            errorName.setVisible(true);
            errorName.setText("Invalid naming. Please enter again.");
            return;

        } else if ( (!textFldImagesNum.getText().matches("[a-zA-Z0-9_-]*")) || num <= 0 || num > 10 ){
            errorImg.setVisible(true);
            errorImg.setText("Invalid number. Please enter between 1-10");
            return;
        }

        //FlickrWork getImg = new FlickrWork(TransportClass.getInstance().getter(), textFldImagesNum.getCharacters().toString());
        //team.submit(getImg);

        //getImg.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
        //    @Override
        //    public void handle(WorkerStateEvent workerStateEvent) {

                //TODO put in image implementation details.
                CreationWork creationWork = new CreationWork(textFieldCreationName.getText());
                team.submit(creationWork);

                creationWork.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent workerStateEvent) {
                        _CreationsExisted.clear();
                        Alert complete = new Alert(Alert.AlertType.INFORMATION);
                        complete.setHeaderText("Created");
                        complete.setContentText(textFieldCreationName.getText() + " has been created.");
                        complete.show();
                    }
                });

//            }
//        }); //TODO implement overwriting

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


