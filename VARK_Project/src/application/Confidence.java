package application;

import application.bashwork.ManageFolder;
import javafx.concurrent.Task;

import java.io.File;

/**
 * The Confidence class record the confidence of user
 */
public class Confidence extends Task<Boolean> {
    private int _rating;
    private String _rateFilePath;

    public Confidence(String creation, int rating) throws Exception {
        _rating = rating;
        _rateFilePath = ManageFolder.findPath(creation, false);

    }

    @Override
    protected Boolean call() throws Exception {
        ManageFolder.writeToFile(_rateFilePath + "/confidence.txt", Integer.toString(_rating));
        return true;
    }
}
