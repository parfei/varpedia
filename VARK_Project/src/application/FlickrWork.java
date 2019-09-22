package application;

import javafx.concurrent.Task;

public class FlickrWork extends Task<String> {
    private String _term;
    private int _num;

    public FlickrWork(String term, String num){
        _term = term;
        _num = Integer.parseInt(num);
    }

    @Override
    protected String call() throws Exception {
        return null;
    }
}
