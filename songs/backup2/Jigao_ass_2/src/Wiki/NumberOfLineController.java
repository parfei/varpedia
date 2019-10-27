package Wiki;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
public class NumberOfLineController {
    private List<String > _originalText = new ArrayList<String>();

    private int userNumber;

    @FXML
    private TextField userInput;
    @FXML
    private Label niceLabel;
    @FXML
    private Label restriction;
    @FXML
    private ListView preView;

    @FXML
    private URL location;
    @FXML
    private ResourceBundle resources;


    /**
     * This method will add the existing creation to the ListView
     */
    public void initialize()
    {


        String cmd = "cat -n temp.txt";
        ProcessBuilder pb = new ProcessBuilder("bash","-c",cmd);
        try{
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;


            while ((line = reader.readLine()) != null) {
                _originalText.add(line);
            }
            preView.getItems().addAll(_originalText);

        }catch (IOException ex) {
            ex.printStackTrace();
        }

        restriction.setText("Between 1 and "+_originalText.size() );


    }

    @FXML
    public void backToMain(ActionEvent event)throws IOException{

        _originalText.clear();

        Parent createViewParent = FXMLLoader.load(getClass().getResource("menu.fxml"));
        Scene createViewScene = new Scene(createViewParent);
        // gets the Stage information
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setTitle("Main Menu");
        window.setScene(createViewScene);
        window.show();


    }

    @FXML
    public void generate(ActionEvent event)throws IOException{
        try{
            userNumber = Integer.parseInt(userInput.getText());


            // While enter invalid number
            if(userNumber<1 ||userNumber>_originalText.size() ){
                niceLabel.setText("You have enter an invalid number,please enter again!");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("You make a significant mistake!");
                alert.setContentText("Ooops, you enter a invalid number!!");

                alert.showAndWait();


            }else{
                niceLabel.setText("Success,Please wait for system");



                String cmd = "head -"+userNumber+" temp.txt >final_vision.txt";
                ProcessBuilder finalText = new ProcessBuilder("bash","-c",cmd);
                try{
                    Process textProcess = finalText.start();
                    textProcess.waitFor();

                }catch (IOException ex) {
                    ex.printStackTrace();
                }

                String audioCmd = "cat final_vision.txt | text2wave -o sound.wav";
                ProcessBuilder audio = new ProcessBuilder("bash","-c",audioCmd);
                try{
                    Process audioProcess = audio.start();
                    audioProcess.waitFor();

                }catch (IOException ex) {
                    ex.printStackTrace();
                }
                // clear the array list
                _originalText.clear();

                Parent createViewParent = FXMLLoader.load(getClass().getResource("createNew.fxml"));
                Scene createViewScene = new Scene(createViewParent);
                // gets the Stage information
                Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
                window.setTitle("Create Menu");
                window.setScene(createViewScene);
                window.show();




            }
        }
        catch(Exception e){

            niceLabel.setText("You have enter an invalid input ,please enter number !!!");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("You make a significant mistake!");
            alert.setContentText("Ooops, Please go back and try again!");

            alert.showAndWait();


        }



    }

}
