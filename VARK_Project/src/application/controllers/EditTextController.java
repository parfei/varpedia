package application.controllers;

import application.Main;
import application.PathCD;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class EditTextController {
    @FXML
    private TextArea textArea;

    private StringBuffer _stringBuffer = new StringBuffer();

    static final int OUT = 0;
    static final int IN = 1;
    @FXML
    public void initialize() {

        String cmd = "cat \"" + PathCD.getPathInstance().getPath() + "/mydir/extra/temp.txt\"";
        ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
        try {
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;


            while ((line = reader.readLine()) != null) {
                _stringBuffer.append(line);
            }
            textArea.setText(_stringBuffer.toString());

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
    @FXML
    public void preview() { //TODO PREVIEW FOR DIFFERENT VOICES!!
        String selectedText = textArea.getSelectedText(); //TODO can't search "man job" for some reason
        //System.out.println(selectedText);
        int numberOfWords = countWords(selectedText);
        if (numberOfWords > 25) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("select a smaller chunk");
            alert.setHeaderText("too much words");
            alert.setContentText("please select a smaller chunk");
            alert.showAndWait();

        } else {
            String cmd = "echo " + selectedText + " | festival --tts"; //TODO can't pronounce when there is bracket
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
            try {
                Process process = pb.start();
            } catch (IOException ex) {
                ex.printStackTrace();
            }


        }
    }
    @FXML
    public void save(ActionEvent event) throws IOException {
        String selectedText=textArea.getSelectedText();

        if (selectedText== null ||selectedText.isEmpty()) { //TOdo text may be comma, full stop
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("No selection");
            error.setHeaderText("please select a trunk");
            error.setContentText("please select a trunk");
            error.showAndWait();
        }

        //System.out.println(selectedText);
        int numberOfWords = countWords(selectedText);
        if (numberOfWords > 25) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("select a smaller chunk");
            alert.setHeaderText("too much words");
            alert.setContentText("please select a smaller chunk");
            alert.showAndWait();
        }
        else {
            FileWriter writer=new FileWriter("savedText.txt");
            writer.write(selectedText);
            writer.close();



            /*String cmd="echo "+textArea.getSelectedText() + " > temp1.txt";
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
            try {
                Process process = pb.start();
            } catch (IOException ex) {
                ex.printStackTrace();
            }*/

            try {

                Parent createViewParent = FXMLLoader.load(Main.class.getResource("resources/saveToAudio.fxml"));
                Scene createViewScene = new Scene(createViewParent);
                // gets the Stage information
                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                window.setTitle("Save to Audio Menu");
                window.setScene(createViewScene);
                window.show();
            } catch (IOException e) {
                e.printStackTrace();

            }


        }
    }

    public void backToMain(ActionEvent event) throws IOException {

        String cmd1="rm -rf \""+PathCD.getPathInstance().getPath()+"/mydir/extra/audio\" ; rm -f \""+ PathCD.getPathInstance().getPath() + "/mydir/extra/temp.txt\"; ";
        //String cmd2="rm -r"+ PathCD.getPathInstance().getPath() + "/mydir/extra/temp.txt";

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

    public void readyToCombine(ActionEvent event) throws IOException {



        Parent createViewParent = FXMLLoader.load(Main.class.getResource("resources/createNew.fxml"));
        Scene createViewScene = new Scene(createViewParent);
        // gets the Stage information
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setTitle("CombineAudio menu");
        window.setScene(createViewScene);
        window.show();


    }

    static int countWords(String str)
    {
        int state = OUT;
        int wc = 0;  // word count
        int i = 0;

        // Scan all characters one by one
        while (i < str.length())
        {
            // If next character is a separator, set the
            // state as OUT
            if (str.charAt(i) == ' ' || str.charAt(i) == '\n'
                    || str.charAt(i) == '\t')
                state = OUT;


                // If next character is not a word separator
                // and state is OUT, then set the state as IN
                // and increment word count
            else if (state == OUT)
            {
                state = IN;
                ++wc;
            }

            // Move to next character
            ++i;
        }
        return wc;
    }


}




