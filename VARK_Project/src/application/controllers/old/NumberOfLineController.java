package application.controllers.old;
import application.Main;
import application.PathCD;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
    private Label errorText;

    @FXML
    private URL location;
    @FXML
    private ResourceBundle resources;

    /**
     * This method will add the existing creation to the ListView
     */
    public void initialize()
    {
        errorText.setVisible(false);
        String cmd = "cat -n \"" + PathCD.getPathInstance().getPath() + "/mydir/extra/temp.txt\"";
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

        restriction.setText("Between 1 and "+ Integer.toString( _originalText.size()-1) );


    }

    @FXML
    public void backToMain(ActionEvent event)throws IOException{

        _originalText.clear();

        Parent createViewParent = FXMLLoader.load(Main.class.getResource("resources/menu.fxml"));
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
                errorText.setText("Please enter between 1 and " + userNumber); //TODO fix wrapping of text
                errorText.setVisible(true);

            }else{
                errorText.setVisible(false);
                niceLabel.setText("Success.");
                this.snip();
                _originalText.clear();

                Parent createViewParent = FXMLLoader.load(Main.class.getResource("resources/createNew.fxml"));
                Scene createViewScene = new Scene(createViewParent);
                // gets the Stage information
                Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
                window.setTitle("Create Menu");
                window.setScene(createViewScene);
                window.show();

            }
        }
        catch(Exception e){

            errorText.setText("Invalid input. Please enter a valid number.");
            errorText.setVisible(true);
        }
    }

    private void snip(){
        String path = PathCD.getPathInstance().getPath();
        String cmd = "head -"+userNumber+ " \"" + path +"/mydir/extra/temp.txt\" > \"" + path + "/mydir/extra/lines.txt\"";
        ProcessBuilder finalText = new ProcessBuilder("bash","-c",cmd);
        try{
            Process textProcess = finalText.start();
            textProcess.waitFor();

        }catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

                /*String audioCmd = "cat final_vision.txt | text2wave -o sound.wav";
                ProcessBuilder audio = new ProcessBuilder("bash","-c",audioCmd);
                try{
                    Process audioProcess = audio.start();
                    audioProcess.waitFor();

                }catch (IOException ex) {
                    ex.printStackTrace();
                }*/
        // clear the array list
    }

}
