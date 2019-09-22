package application;

import java.io.File;
import java.net.URISyntaxException;

public class PathCD {
    /** Create an instance of the class at the time of class loading */
    private static final PathCD instancePath = new PathCD();
    private String _path;

    /** private constructor to prevent others from instantiating this class */
    private PathCD() {
        try {
            _path = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        _path = _path.substring(0, _path.lastIndexOf("/"));
    }

    /** Provide a global point of access to the instance */
    public static PathCD getPathInstance() {
        return instancePath;
    }
    public String getPath(){
        return _path;
    }

}
