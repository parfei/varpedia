package application.controllers.old;

import application.Main;
import application.PathCD;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class CombineAudio {
    //combine the audio
    private List<String> _audioExisted = new ArrayList<String>();
    @FXML
    private ListView audioList;


    public void initialize(){

        String command = "ls -tcr " + PathCD.getPathInstance().getPath() + "/mydir/extra/audioPiece" + " | cut -f1 -d'.'\n";
        ProcessBuilder builder = new ProcessBuilder("bash", "-c", command);
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

    }


    public void combineAudioPieces(){
        String combine= "$(cd " + PathCD.getPathInstance().getPath() + "/mydir/extra/audioPiece ; sox $(ls -tcr | grep wav) sound.wav)";
        System.out.println(combine);
        ProcessBuilder pb = new ProcessBuilder("bash", "-c", combine);
        try {
            Process process = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void backToMain(ActionEvent event) throws IOException {
        String cmd1="rm -rf \""+PathCD.getPathInstance().getPath()+"/mydir/audioPiece\" ; rm -f \""+ PathCD.getPathInstance().getPath() + "/mydir/extra/temp.txt\"; ";
        //String cmd2="rm -r"+ PathCD.getPathInstance().getPath() + "/mydir/extra/temp.txt";
        //System.out.println(cmd2);
        ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd1);
        try {
            Process process = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*ProcessBuilder pb2 = new ProcessBuilder("bash", "-c", cmd2);
        try {
            Process process = pb2.start();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        Parent createViewParent = FXMLLoader.load(Main.class.getResource("resources/menu.fxml"));
        Scene createViewScene = new Scene(createViewParent);
        // gets the Stage information
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setTitle("Main Menu");
        window.setScene(createViewScene);
        window.show();
    }

}
