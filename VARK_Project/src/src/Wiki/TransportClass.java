package Wiki;

public class TransportClass {
    /** private constructor to prevent others from instantiating this class */
    private TransportClass() {}

    /** Create an instance of the class at the time of class loading */
    private static final TransportClass instance = new TransportClass();

    private String contain;
    /** Provide a global point of access to the instance */
    public static TransportClass getInstance() {
        return instance;
    }
    public void setter(String string) {
        contain = string;
    }
    public String getter(){
        return contain;
    }
}
