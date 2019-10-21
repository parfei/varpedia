package application.values;

public class FlickrDone {
    private static Boolean _done;

    public static void isDone(Boolean v){
        _done = v;
    }

    public static Boolean checkDone(){
        return _done;
    }
}
