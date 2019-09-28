package application.controllers;

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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class EditTextController {
    @FXML
    private TextArea textArea;
    @FXML
    private Label askForVoice;
    @FXML
    private ToggleGroup group;
    @FXML
    private Label select;

    private StringBuffer _stringBuffer = new StringBuffer();

    @FXML
    private RadioButton default_voice;
    @FXML
    private RadioButton male_voice;
    @FXML
    private RadioButton female_voice;

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
    public void preview() throws IOException {
        String selectedText = textArea.getSelectedText();
        String textWithoutBrackets = selectedText.replaceAll("[\\[\\](){}']","");//TODO can't search "man job" for some reason
        //System.out.println(selectedText);
        RadioButton selectedRadioButton = (RadioButton) group .getSelectedToggle();


        int numberOfWords = countWords(selectedText);
        if (numberOfWords==0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No chunk selected");
            alert.setHeaderText("Please select a chunk");
            alert.setContentText("select a chunk so we can carry on");
            alert.showAndWait();
        }
        else if (numberOfWords > 25) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("select a smaller chunk");
            alert.setHeaderText("too many words");
            alert.setContentText("please select a smaller chunk");
            alert.showAndWait();

        } else if (selectedRadioButton==null) {
            askForVoice.setText("SELECT A VOICE PLEASE");
            return;
        }else{
           // String textWithoutBrackets = selectedText.replaceAll("[\\[\\](){}']","");
            if (default_voice.isSelected()){
                FileWriter writer=new FileWriter("default_voice");
                writer.write("(voice_kal_diphone)"+"\n"+"(SayText" + " "+"\""+selectedText +"\"" + ")") ;
                writer.close();
                String cmd="festival -b default_voice";
                ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
                try {
                    Process process = pb.start();
                    int exitStatus=process.waitFor();
                    if (exitStatus==255){
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Text selected can't be read");
                        alert.setHeaderText("Make sure the text is readable");
                        alert.setContentText("Sorry, the speaker can't read your selected text");
                        alert.showAndWait();
                    }
                } catch (IOException | InterruptedException ex) {
                    ex.printStackTrace();
                }

            }
            else if (male_voice.isSelected()){
                FileWriter writer=new FileWriter("male_voice");
                writer.write("(voice_akl_nz_jdt_diphone)"+"\n"+"(SayText" + " "+"\""+textWithoutBrackets+"\"" + ")") ;
                writer.close();
                String cmd="festival -b male_voice";
                ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
                try {
                    Process process = pb.start();
                    int exitStatus=process.waitFor();
                    if (exitStatus==255){
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Text selected can't be read");
                        alert.setHeaderText("Make sure the text is readable");
                        alert.setContentText("Sorry, the speaker can't read your selected text or uncommon word");
                        alert.showAndWait();
                    }
                } catch (IOException | InterruptedException ex) {
                    ex.printStackTrace();
                }






            }
            else if (female_voice.isSelected()){
                FileWriter writer=new FileWriter("female_voice.scm");
                writer.write("(voice_akl_nz_cw_cg_cg)"+"\n"+"(SayText" + " "+"\""+textWithoutBrackets+"\"" + ")") ;
                writer.close();
                String cmd="festival -b female_voice.scm";
                ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
                try {
                    Process process = pb.start();
                    int exitStatus=process.waitFor();
                    if (exitStatus==255){
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Text selected can't be read");
                        alert.setHeaderText("Make sure the text is readable");
                        alert.setContentText("Sorry, the speaker can't read your selected text or uncommon word");
                        alert.showAndWait();
                    }
                } catch (IOException | InterruptedException ex) {
                    ex.printStackTrace();
                }

            }
            else {
                //do nothing
            }
           /* String cmd = "echo " + textWithoutBrackets + " | festival --tts"; //TODO can't pronounce when there is bracket
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
            try {
                Process process = pb.start();
            } catch (IOException ex) {
                ex.printStackTrace();
            }*/


        }
    }
    @FXML
    public void save(ActionEvent event) throws IOException {
        String selectedText=textArea.getSelectedText();
        String saveble = selectedText.replaceAll("[\\[\\](){}']","");

        if (selectedText== null ||selectedText.isEmpty()) { //TOdo text may be comma, full stop
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("No selection");
            error.setHeaderText("please select a chunk");
            error.setContentText("please select a part of text ");
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
            writer.write(saveble);
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
        //String command = "cd \"" + PathCD.getPathInstance().getPath() + "/mydir\" ; rm -rf extra/* ; cd -";
        //String command2 = "mkdir -p \"" + path + "/mydir/extra\" ; mkdir \"" + path + "/mydir/creations\"; ";
        String cmd1="rm -rf \""+PathCD.getPathInstance().getPath()+"/mydir/extra/audioPiece\" ; rm -f \""+ PathCD.getPathInstance().getPath() + "/mydir/extra/temp.txt\"; ";
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

    public void readyToCombine(ActionEvent event) throws IOException {



        Parent createViewParent = FXMLLoader.load(Main.class.getResource("resources/CombineAudio.fxml"));
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




