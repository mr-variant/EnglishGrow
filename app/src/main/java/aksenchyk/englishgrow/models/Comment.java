package aksenchyk.englishgrow.models;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Comment {

    private @ServerTimestamp Date timestamp;

    private String user_id;
    private String message;


    public Comment() {  }

    public Comment(FirebaseUser user, String message) {
        this.user_id = user.getUid();
        this.message = message;
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
