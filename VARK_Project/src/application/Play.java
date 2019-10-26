package application;

import application.bashwork.ManageFolder;
import javafx.concurrent.Task;

import java.io.FileNotFoundException;

/**
 * the Play class increase and record the number of plays of a creation
 */
public class Play extends Task<Boolean> {
    private String _path;

    public Play(String creation) throws Exception {
        _path = ManageFolder.findPath(creation, false);
    }



    @Override
    protected Boolean call() throws Exception {
        try {
            String current = ManageFolder.readFile(_path + "/plays.txt");
            if (current.equals("")){
                ManageFolder.writeToFile(_path + "/plays.txt", "0");
            } else {
                ManageFolder.writeToFile(_path + "/plays.txt", Integer.toString(Integer.parseInt(current) + 1));
            }
        } catch (FileNotFoundException e){
            ManageFolder.writeToFile(_path + "/plays.txt", "0");
        }
        return true;
    }
}
