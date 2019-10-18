package application.bashwork;

import application.PathCD;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ManageFolder {

    public static void initializeFolders() throws Exception {
        String path = PathCD.getPathInstance().getPath();

        BashCommand output = new BashCommand();
        ArrayList<String> list = output.bash("[ -e \"" + path + "/mydir\" ]");
        if (list.isEmpty()){
        } else if (list.get(0).equals("1")) {
            output.bash("mkdir -p \"" + path + "/mydir/extra/\" ; mkdir \"" + path + "/mydir/creations/favourites\"; ");
        }
    }

}
