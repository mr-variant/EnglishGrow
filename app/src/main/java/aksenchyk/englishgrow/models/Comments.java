package aksenchyk.englishgrow.models;

import java.util.Date;

public class Comments {

    private String userID;
    private String message;
    private Date timestamp;


    public Comments(){
    }

    public Comments(String message, String userID, Date timestamp) {
        this.message = message;
        this.userID = userID;
        this.timestamp = timestamp;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
