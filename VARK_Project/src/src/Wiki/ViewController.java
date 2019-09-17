package Wiki;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ViewController {
    private List <String > innovation = new ArrayList<String>();

    private String _choice;
    @FXML
    private ListView stuffCreated;
    @FXML
    private Button playButton;
    @FXML
    private Button deleteButton;

    // create an object of CreateController to use the back to main menu method
    private CreationController creation = new CreationController();



    @FXML
    private URL location;
    @FXML
    private ResourceBundle resources;

    /**
     * This method will add the existing creation to the ListView
     */
    public void initialize()
    {
        String cmd = "ls"+ " mydir "+ "/ | grep avi | sort | cut -f1 -d'.'\n";
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
        try{
            ObservableList selectedCreation = stuffCreated.getSelectionModel().getSelectedItems();
            _choice = selectedCreation.get(0).toString();
        }catch (Exception e){

        }





    }

    @FXML
    public void playVideo(ActionEvent event)throws IOException{

        if(_choice!=null) {


            String cmd = "ffplay -autoexit"+" mydir/" +_choice+".avi";
            System.out.println(cmd);
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
            try {
                Process process = pb.start();


            } catch (IOException ex) {
                ex.printStackTrace();
            }


        }else{

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

                String cmd= "rm" +" mydir/"+_choice+".avi";
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

            }

        }

    }
}
