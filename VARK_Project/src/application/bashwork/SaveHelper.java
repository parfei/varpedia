package application.bashwork;

import application.values.PathIs;
import javafx.concurrent.Task;

import java.io.IOException;

public class SaveHelper extends Task<Integer> {
    private String _voice;
    private String _number;
    private String _term;

    public SaveHelper(String voice, String number, String term){
        _term = term;
        _number = number;
        _voice = voice;
    }

    @Override
    protected Integer call() throws Exception {
        String createAudio = "";
//        String path = "\"" + PathIs.EXTRA +"/savedText" + _number + ".txt\"";
        String path = "\"" + PathIs.TEMP +"/savedText" + _number + ".txt\"";
        if (_voice.equals("default_voice")){
            createAudio = "text2wave -o \"" + PathIs.TEMP + "/audioPiece/" + _term + "-"+ _number+ ".wav\" " +
                    path + " -eval \"" + PathIs.TEMP +"/kal.scm\"";
        } else if (_voice.equals("male_voice")){
            createAudio = "text2wave -o \"" + PathIs.TEMP +"/audioPiece/" + _term+ "-"+ _number + ".wav\" " +
                    path + " -eval \"" + PathIs.TEMP + "/jdt.scm\"";
        } else if (_voice.equals("female_voice")){
            createAudio = "text2wave -o \"" + PathIs.TEMP + "/audioPiece/" + _term+ "-"+ _number + ".wav\" " +
                    path + " -eval \"" + PathIs.TEMP + "/cw.scm\"";
        }

        Process process = null;
        ProcessBuilder pb = new ProcessBuilder("bash", "-c", createAudio);
        try {
            process = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return process.waitFor();
    }
}
