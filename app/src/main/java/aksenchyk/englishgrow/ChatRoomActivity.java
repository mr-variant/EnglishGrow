package aksenchyk.englishgrow;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatRoomActivity extends AppCompatActivity implements View.OnClickListener {


    private Toolbar mRoomToolbar;
    private EditText mSendMessageEditText;
    private ImageButton mSendMessageButton;

    private String mRoomName;
    private String mChatId;

    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        mRoomToolbar = (Toolbar) findViewById(R.id.roomToolbar);
        mSendMessageEditText = (EditText) findViewById(R.id.sendMessageEditText);
        mSendMessageButton = (ImageButton) findViewById(R.id.sendMessageButton);

        mSendMessageButton.setOnClickListener(this);

        mFirestore = FirebaseFirestore.getInstance();


        mSendMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().isEmpty()) {
                    mSendMessageButton.setVisibility(View.VISIBLE);
                } else  {
                    mSendMessageButton.setVisibility(View.GONE);
                }
            }
        });


        setSupportActionBar(mRoomToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        mRoomName = intent.getStringExtra("roomName");
        mChatId  = intent.getStringExtra("roomId");

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


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.sendMessageButton:
                String message = mSendMessageEditText.getText().toString();

                Map<String,Object> msgMap = new HashMap<>();

                msgMap.put("nickname", "user");
                msgMap.put("msg", message);
                msgMap.put("time", new Date());

                mFirestore.collection("Chats").document(mChatId).collection("Messages").add(msgMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        mSendMessageEditText.setText(null);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMsg = e.getMessage();
                        Toast.makeText(ChatRoomActivity.this, errorMsg , Toast.LENGTH_SHORT).show();
                    }
                });

                break;
        }
    }

}
