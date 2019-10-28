package application.bashwork;

import application.values.PathIs;
import javafx.concurrent.Task;

import java.io.FileWriter;
import java.io.IOException;

public class PreviewHelper extends Task<Integer> {
        private String _voice;
        private String _text;

        public PreviewHelper(String voice, String text){
            _text = text.replaceAll("[\\[\\](){}']",""); // remove the text in brackets to make it readable
            _voice = voice;
        }

        @Override
        protected Integer call() throws Exception {
            FileWriter writer = null;
            String cmd = "";

            if (_voice.equals("default_voice")){
                writer=new FileWriter(PathIs.TEMP + "/" + _voice);
                writer.write("(voice_kal_diphone)"+"\n"+"(SayText" + " "+"\""+_text +"\"" + ")");
                cmd = "festival -b \"" + PathIs.TEMP + "/default_voice\"";

            } else if (_voice.equals("male_voice")){
                writer=new FileWriter(PathIs.TEMP + "/" + _voice);
                writer.write("(voice_akl_nz_jdt_diphone)"+"\n"+"(SayText" + " "+"\""+_text+"\"" + ")");
                cmd = "festival -b \"" + PathIs.TEMP + "/male_voice\"";
            } else if (_voice.equals("female_voice")){
                writer=new FileWriter(PathIs.TEMP + "/female_voice.scm");
                writer.write("(voice_akl_nz_cw_cg_cg)"+"\n"+"(SayText" + " "+"\""+_text+"\"" + ")");
                cmd = "festival -b \"" + PathIs.TEMP + "/female_voice.scm\"";
            }
            writer.close();

            Process process = null;
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
            try {
                process = pb.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return process.waitFor();
        }
}
