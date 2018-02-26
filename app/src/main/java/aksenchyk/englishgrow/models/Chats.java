package aksenchyk.englishgrow.models;

import aksenchyk.englishgrow.adapters.ChatRoomId;

/**
 * Created by ixvar on 2/17/2018.
 */

public class Chats extends ChatRoomId{

    private String name;

    public Chats() {

    }

    public Chats(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
