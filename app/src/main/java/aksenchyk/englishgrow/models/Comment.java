package aksenchyk.englishgrow.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Comment {

    private String user_id;
    private String message;


    private @ServerTimestamp Date timestamp;


    public Comment(){
    }

    public Comment(String message, String user_id, Date timestamp) {
        this.message = message;
        this.user_id = user_id;
        this.timestamp = timestamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String userID) {
        this.user_id = userID;
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
