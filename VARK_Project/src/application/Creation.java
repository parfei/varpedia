package application;

public class Creation {
    private String _term;
    private String _name;
    private int _rating;
    private int _plays;
    private String _path;
    private Boolean _fav;

    public Creation(String term, String name){
        _rating = 0;
        _term = term;
        _name = name;
        _path = PathCD.getPathInstance().getPath() + "/mydir/creations/" + term + "/" + name + ".mp4";
    }

    public void favourite(Boolean fav){
        _fav = fav;
        //TODO move favouriting here
    }

    public void incrementPlays(){
        _plays++;
    }

    public void changeRating(int num){
        _rating = num;
    }

    public String getPath(){
        return _path;
    }

    public Boolean isFav(){
        return _fav;
    }

    @Override
    public String toString() {
        return _name;
    }
}
