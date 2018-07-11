package aksenchyk.englishgrow.models;

import java.util.Date;

/**
 * Created by ixvar on 2/25/2018.
 */

public class Messages {

    private String msg;
    private String nickname;
    private Date time;

    public Messages() {

    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

}
