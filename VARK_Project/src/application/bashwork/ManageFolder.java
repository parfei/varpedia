package application.bashwork;

import application.PathCD;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class ManageFolder {

    public static void initializeFolders() throws Exception {
        String path = PathCD.getPathInstance().getPath();

        BashCommand output = new BashCommand();
        ArrayList<String> list = output.bash("[ -e \"" + path + "/mydir\" ]");
        if (list.get(0).equals("1")){
            output.bash("mkdir -p \"" + path + "/mydir/extra/\" ; mkdir \"" + path + "/mydir/creations/favourites\"; ");
        }
    }
}
