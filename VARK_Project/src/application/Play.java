package application;

import application.bashwork.ManageFolder;
import javafx.concurrent.Task;

public class Play extends Task<Boolean> {
    private String _path;

    public Play(String creation) throws Exception {
        _path = ManageFolder.findPath(creation, false);
    }

    @Override
    protected Boolean call() throws Exception {
        String current = ManageFolder.readFile(_path + "/plays.txt");
        ManageFolder.writeToFile(_path + "/plays.txt", Integer.toString(Integer.parseInt(current) + 1));
        return true;
    }
}
