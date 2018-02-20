package aksenchyk.englishgrow;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class ChatRoomActivity extends AppCompatActivity {


    private Toolbar mRoomToolbar;

    private String mRoomName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        mRoomToolbar = (Toolbar) findViewById(R.id.roomToolbar);
        setSupportActionBar(mRoomToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        mRoomName = intent.getStringExtra("roomName");
        mRoomToolbar.setTitle(mRoomName);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
