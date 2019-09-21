package Wiki;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
public class CreateNewController {





        @FXML
        private Label labelMessage;
        @FXML
        private TextField textFieldCreationName;


        @FXML
        private ListView listViewExistCreation;

        @FXML
        private URL location;
        @FXML
        private ResourceBundle resources;


        private List<String > _CreationsExisted = new ArrayList<String>();


    /**
         * This method will add the existing creation to the ListView
         */
        public void initialize()
        {
            String command = "ls"+ " mydir "+ "/ | grep avi | sort | cut -f1 -d'.'\n";
            ProcessBuilder builder = new ProcessBuilder("bash","-c",command);
            try{
                String line;
                Process process = builder.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));


                while ((line = reader.readLine()) != null) {
                    _CreationsExisted.add(line);
                }
                listViewExistCreation.getItems().addAll(_CreationsExisted);

            }catch (IOException e) {
                e.printStackTrace();
            }
        }

        @FXML
        public void returnToStart (ActionEvent event)throws IOException{

            _CreationsExisted.clear();

            Parent createView = FXMLLoader.load(getClass().getResource("menu.fxml"));
            Scene createViewScene = new Scene(createView);
            // gets the Stage information
            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            window.setTitle("Main Menu");
            window.setScene(createViewScene);
            window.show();
        }

        @FXML
        public void EnterCreation(ActionEvent event)throws IOException{

            if(_CreationsExisted.contains(textFieldCreationName.getText())){
                labelMessage.setText("Your name is duplicated, Please enter again!");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("mistake found!");
                alert.setContentText("Please rename or go back to the Main Menu!");

                alert.showAndWait();
            } else if(textFieldCreationName.getText().contains("([\\w||[-]&&[^|]])*")){
                labelMessage.setText("Some characters are not valid in your name, Please try again!");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("mistake found!");
                alert.setContentText("Please rename or go back to the Main Menu!");

                alert.showAndWait();

            }else{
                System.out.println(TransportClass.getInstance().getter());
                String videoCommand= "ffmpeg -f lavfi -i color=c=blue:s=320x240:d=5 -vf \"drawtext=fontfile=myfont.ttf:fontsize=30: fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text='" + TransportClass.getInstance().getter()+ "'\" creation.avi";

                ProcessBuilder video = new ProcessBuilder("bash","-c",videoCommand);
                try{
                    Process videoProcess = video.start();
                    videoProcess.waitFor();

                }catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }

                String combineCommand = "ffmpeg -i creation.avi -i sound.wav -map 0:v -map 1:a "+textFieldCreationName.getText()+".avi 2>/dev/null";
                ProcessBuilder getTogether= new ProcessBuilder("bash","-c",combineCommand);
                try{
                    Process combinedProcess = getTogether.start();
                    combinedProcess.waitFor();

                }catch (IOException | InterruptedException ex) {
                    ex.printStackTrace();
                }
                // clear the array list
                String findPath = System.getProperty("user.dir");
                String finishingPath = findPath+"/mydir";

                File fileGeneralized = new File (textFieldCreationName.getText()+".avi");
                File audio = new File ("sound.wav");
                File theVideo = new File ("creation.avi");
                audio.delete();
                theVideo.delete();


                String changeDirectory = "mv "+textFieldCreationName.getText()+ ".avi " +finishingPath;
                System.out.println(changeDirectory);
                ProcessBuilder changePosition = new ProcessBuilder("bash","-c",changeDirectory);
                try{
                    Process movingProcess = changePosition.start();
                    movingProcess.waitFor();

                }catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }

                fileGeneralized.delete();

                _CreationsExisted.clear();

                Parent menuParent = FXMLLoader.load(getClass().getResource("menu.fxml"));
                Scene createViewScene = new Scene(menuParent);
                // gets the Stage information
                Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
                window.setTitle("Main Menu");
                window.setScene(createViewScene);
                window.show();




            }



        }



    }


