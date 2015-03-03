package kamcord;

/**
 * Created by taoxia on 3/3/15.
 */
public class Key {
    private String date;
    private String os;
    private String sdk;

    public Key(String date) {
        this(date, null, null);
    }

    public Key(String date, String os) {
        this(date, os, null);
    }

    public Key(String date, String os, String sdk) {
        this.date = date;
        this.os = os;
        this.sdk = sdk;
    }

    public String getKey(){
        return date + (os == null ? "" : os.toLowerCase()) + (sdk == null ? "" : sdk);
    }

    public Key getParentKey(){
        if(os==null && sdk==null) {
            return null;
        }if(sdk==null){
            return new Key(date);
        }else{
            return new Key(date, os);
        }
    }
}
