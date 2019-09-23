package application;

import javafx.concurrent.Task;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class FlickrWork extends Task<String> {
    private String _term;
    private int _num;
    private ArrayList<String> _html = new ArrayList<String>();

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
            int counter = 0;
            while((line = reader.readLine()) != null){
                if (counter == _num){
                    break;
                } else if (line.contains("url(//live.staticflickr.com")){
                    _html.add(line);
                    counter++;
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void downloadImgs(){
        String link;
        int i = 1;
       for (String imgLine:_html){
           link = "http://" + imgLine.substring(imgLine.indexOf("live.staticflickr.com/"), imgLine.indexOf(".jpg") + 4);
           System.out.println(link);
           try {

               //Gets true link instead of the link that redirects you.
               HttpURLConnection con = (HttpURLConnection) new URL(link).openConnection();
               con.setInstanceFollowRedirects(false);
               con.connect();
               String link2 = con.getHeaderField("Location").toString();

               InputStream in = new URL(link2).openConnection().getInputStream();
               Files.copy(in, Paths.get(PathCD.getPathInstance().getPath() + "/mydir/extra/picture_" + i + ".jpg"));

           } catch (IOException e) {
               e.printStackTrace();
           }
           i++;
       }
    }
}
