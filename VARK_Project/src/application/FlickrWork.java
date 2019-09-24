package application;

import javafx.concurrent.Task;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

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

        System.out.println("flickr done");
        return null;
    }

    /**
     * Get the HTML from the page to search images for.
     * CONSIDER PAGES WHICH DON'T HAVE IMAGES FOR IT?
     */
    private void getHTML() throws IOException {
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
        } catch (IOException e) {
            throw new IOException("Images cannot be found. Replacing with a blue background instead."); //TODO implement this.
        }
    }

    private void downloadImgs() throws IOException {
        String link;
        String trueLink;
        InputStream in = null;

        int i = 1;
       for (String imgLine:_html){
           link = "http://" + imgLine.substring(imgLine.indexOf("live.staticflickr.com/"), imgLine.indexOf(".jpg") + 4);
           try {

               //Gets true link instead of the link that redirects you.
               HttpURLConnection con = (HttpURLConnection) new URL(link).openConnection();
               con.setInstanceFollowRedirects(false);
               con.connect();
               trueLink = con.getHeaderField("Location").toString();

               //Download the link using the true link.
               in = new URL(trueLink).openConnection().getInputStream();
               System.out.println(PathCD.getPathInstance().getPath() + "/mydir/extra/img" + i + ".jpg");
               Files.copy(in, Paths.get(PathCD.getPathInstance().getPath() + "/mydir/extra/img" + i + ".jpg"));

           } catch (IOException e) {
               e.printStackTrace();
           }
           System.out.println(link);
           i++;
       }
    }
}
