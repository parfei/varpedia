package application.bashwork;

import application.PathCD;
import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class BashCommand{

    public ArrayList<String> bash(String command) throws Exception {
        ArrayList<String> output = new ArrayList<String>();


        ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", command);
        Process process = pb.start();

        if (process.waitFor() == 1) {
            output.add("1");
            return output;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            output.add(line);
        }

        return output;
    }
}
