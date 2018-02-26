package aksenchyk.englishgrow.adapters;

import android.support.annotation.NonNull;

/**
 * Created by ixvar on 2/24/2018.
 */

public class ChatRoomId {

    public String chatRoomId;

    public <T extends ChatRoomId> T withId(@NonNull final String id) {

        this.chatRoomId = id;
        return (T) this;
    }

}







