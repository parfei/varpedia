package application.controllers;

import application.ChangeScene;
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

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class EditTextController {
    @FXML
    private TextArea textArea;
    @FXML
    private Label askForVoice;
    @FXML
    private ToggleGroup group;
    @FXML
    private Label select;

    @FXML
    private Button create;


    private StringBuffer _stringBuffer = new StringBuffer();

    @FXML
    private RadioButton default_voice;
    @FXML
    private RadioButton male_voice;
    @FXML
    private RadioButton female_voice;
    @FXML
    private Label remindLabel;

    private String _term;


    static final int OUT = 0;
    static final int IN = 1;

    public void initData(String term){
        _term = term;
        System.out.println(_term);
    }
    private ChangeScene _changeSceneObject=new ChangeScene();


    /**
     * this method will add the search result to the text area
     */
    @FXML
    public void initialize() {

        remindLabel.setVisible(false);

        String cmd = "cat \"" + PathCD.getPathInstance().getPath() + "/mydir/extra/temp.txt\"";
        //String cmd="cat temp.txt";
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
        // remove the text in brackets to make it readable
        String textWithoutBrackets = selectedText.replaceAll("[\\[\\](){}']","");

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

            if (default_voice.isSelected()){
                FileWriter writer=new FileWriter("default_voice");
                writer.write("(voice_kal_diphone)"+"\n"+"(SayText" + " "+"\""+selectedText +"\"" + ")") ;
                writer.close();
                String cmd="festival -b default_voice";
                ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
                try {
                    Process process = pb.start();


                } catch (IOException e) {
                    e.printStackTrace();
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
                    // if the male voice can't read the text, switch to default voice
                    if (exitStatus==255){
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("voice changed to default");
                        alert.setHeaderText("switch voice to the default due to limitation of other voice options");
                        alert.setContentText("Sorry for the inconvenience");
                        alert.showAndWait();
                        FileWriter newWriter=new FileWriter("default_voice");
                        newWriter.write("(voice_kal_diphone)"+"\n"+"(SayText" + " "+"\""+selectedText +"\"" + ")") ;
                        newWriter.close();
                        String useDefault="festival -b default_voice";
                        ProcessBuilder pronounce = new ProcessBuilder("bash", "-c", useDefault);
                        pronounce.start();

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
                    // if the female voice can't read the text, switch to default voice
                    if (exitStatus==255){
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("voice changed to default");
                        alert.setHeaderText("switch voice to the default due to limitation of other voice options");
                        alert.setContentText("Sorry for the inconvenience");
                        alert.showAndWait();


                        FileWriter newWriter=new FileWriter("default_voice");
                        newWriter.write("(voice_kal_diphone)"+"\n"+"(SayText" + " "+"\""+selectedText +"\"" + ")") ;
                        newWriter.close();
                        String useDefault="festival -b default_voice";
                        ProcessBuilder pronounce = new ProcessBuilder("bash", "-c", useDefault);
                        pronounce.start();

                    }
                } catch (IOException | InterruptedException ex) {
                    ex.printStackTrace();
                }

            }
            else {
                //do nothing
            }
        }
    }

    /**
     * This method will check exception and go to saveToAudio interface when a save button is clicked
     * @param event
     * @throws IOException
     */
    @FXML
    public void save(ActionEvent event) throws IOException {
        String selectedText=textArea.getSelectedText();
        String saveble = selectedText.replaceAll("[\\[\\](){}']","");
        int numberOfWords = countWords(selectedText);
        if (selectedText== null ||selectedText.isEmpty()) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("No selection");
            error.setHeaderText("please select a chunk");
            error.setContentText("please select a part of text ");
            error.showAndWait();
        }

        else if (numberOfWords > 25) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("select a smaller chunk");
            alert.setHeaderText("too much words");
            alert.setContentText("please select a smaller chunk");
            alert.showAndWait();
        }

        // save the selected text to a file and switch scene to saveToAudio interface
        else {
            FileWriter writer=new FileWriter(PathCD.getPathInstance().getPath() + "/mydir/extra/savedText.txt");
            writer.write(saveble);
            writer.close();



            try {


                FXMLLoader loader = new FXMLLoader(Main.class.getResource("resources/saveToAudio.fxml"));
                Parent createViewParent = loader.load();
                SaveToAudioController controller = loader.getController();

                controller.initData(_term);


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


    /**
     * THis method will remove the saved text and audios when the user want to restart a creation process
     * @param event
     * @throws IOException
     */
    @FXML
    public void backToMain(ActionEvent event) throws IOException {

        String cmd1="rm -rf \""+PathCD.getPathInstance().getPath()+"/mydir/extra/audioPiece\" ; rm -f \""+ PathCD.getPathInstance().getPath() + "/mydir/extra/temp.txt\"; ";
        //String cmd2="rm -r"+ PathCD.getPathInstance().getPath() + "/mydir/extra/temp.txt";

        ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd1);
        try {
            Process process = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        _changeSceneObject.changeScene(event, "resources/menu.fxml","Main Menu");

        /*Parent createViewParent = FXMLLoader.load(Main.class.getResource("resources/menu.fxml"));
        Scene createViewScene = new Scene(createViewParent);
        // gets the Stage information
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setTitle("Main Menu");
        window.setScene(createViewScene);
        window.show();*/
    }

    private static boolean isDirEmpty(final Path directory) throws IOException {
        try(DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory)) {
            return !dirStream.iterator().hasNext();
        }
    }

    /**
     * This method will take the user to the creation of video interface when "create' button is clicked
     * @param event
     * @throws IOException
     */
    @FXML
    public void readyToCombine(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("resources/createNew.fxml"));
        Parent createViewParent=loader.load();
        CreateNewController controller = loader.getController();

        controller.initData(_term);


        Scene createViewScene = new Scene(createViewParent);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(createViewScene);
        // gets the Stage information
        window.setTitle("Creation Menu");
        window.show();
    }

    /**
     * This method will count how many words in users' selected part
     * @param str
     * @return
     */
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




