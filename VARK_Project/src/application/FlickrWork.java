package application;

import javafx.concurrent.Task;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public class FlickrWork extends Task<String> {
    private String _term;
    private int _num;
    private String _html;

    public FlickrWork(String term, String num){
        _term = term;
        _num = Integer.parseInt(num);
    }

    @Override
    protected String call() throws Exception {
        // https://www.flickr.com/search/?text=_term;
        getHTML();
        downloadImgs();
        return null;
    }

    /**
     * Get the HTML from the page to search images for.
     * CONSIDER PAGES WHICH DON'T HAVE IMAGES FOR IT?
     */
    private void getHTML(){
        try {
            URL url = new URL("https://www.flickr.com/search/?text=" + _term);
            //TODO throw exception if cannot find images for the specific term.
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream()));

            String line = null;
            while((line = reader.readLine()) != null){
                _html += line + "\n";
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void downloadImgs(){
       System.out.println(_html);
    }
}
