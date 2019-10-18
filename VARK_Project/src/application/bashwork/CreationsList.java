package application.bashwork;

import application.PathCD;
import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CreationsList extends Task<ArrayList<String>> {
    private String _path;

    public CreationsList(String path){
        _path = path;
    }

    @Override
    protected ArrayList<String> call() throws Exception {
        ArrayList<String> innovation = new ArrayList<String>();

        String cmd = "ls -R"+ " \""+ PathCD.getPathInstance().getPath() + "/mydir/" + _path + "\""+ " | grep .mp4 | cut -f1 -d'.' | sort";
        ProcessBuilder initializing = new ProcessBuilder("bash","-c",cmd);
        try{
            Process process = initializing.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                innovation.add(line);
            }
        }catch (IOException ex) {
            ex.printStackTrace();
        }
        return innovation;
    }
}
