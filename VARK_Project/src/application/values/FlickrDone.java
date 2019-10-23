package application.values;

import application.controllers.EditPicturesController;

public class FlickrDone {
    private static Boolean _done = false;
    private static EditPicturesController _listener;

    public static void isDone(Boolean v){
        _done = v;
        if (_listener != null){
            _listener.setGrid();
        }
    }

    public static Boolean checkDone(){
        return _done;
    }

    public static void addListener(EditPicturesController controller){
        _listener = controller;
    }

    public static void reset(){
        _done = false;
        _listener = null;
    }
}
