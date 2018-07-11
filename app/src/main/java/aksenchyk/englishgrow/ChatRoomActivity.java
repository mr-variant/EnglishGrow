package aksenchyk.englishgrow;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aksenchyk.englishgrow.adapters.ChatListAdapter;
import aksenchyk.englishgrow.adapters.MessagesAdapter;
import aksenchyk.englishgrow.models.Chats;
import aksenchyk.englishgrow.models.Messages;
import aksenchyk.englishgrow.models.UserInfo;

public class ChatRoomActivity extends AppCompatActivity implements View.OnClickListener {


    private Toolbar mRoomToolbar;
    private EditText mSendMessageEditText;
    private ImageButton mSendMessageButton;

    private String mRoomName;
    private String mChatId;

    private FirebaseFirestore mFirestore;
    private RecyclerView mChatListRecyclerView;
    private List<Messages> messagesList;
    private MessagesAdapter messagesAdapter;

    private String nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        mRoomToolbar = (Toolbar) findViewById(R.id.roomToolbar);
        mSendMessageEditText = (EditText) findViewById(R.id.sendMessageEditText);
        mSendMessageButton = (ImageButton) findViewById(R.id.sendMessageButton);

        mSendMessageButton.setOnClickListener(this);


        messagesList = new ArrayList<>();
        messagesAdapter = new MessagesAdapter(messagesList);

        mChatListRecyclerView = (RecyclerView) findViewById(R.id.chatRoomListRecyclerView);
        mChatListRecyclerView.setHasFixedSize(true);
        mChatListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mChatListRecyclerView.setAdapter(messagesAdapter);


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

        nickname = UserInfo.getNickname();


        mFirestore.collection("Chats").document(mChatId).collection("Messages").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if(e != null) {
                    Log.d("FireTag","Error" + e.getMessage());
                }

                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                    if(doc.getType() == DocumentChange.Type.ADDED) {
                        //  String name = doc.getDocument().getString("name");
                        //  Toast.makeText(getActivity(),name,Toast.LENGTH_SHORT).show();

                        Messages messages =  doc.getDocument().toObject(Messages.class);
                        messagesList.add(messages);
                        messagesAdapter.notifyDataSetChanged();
                    }

                }

            }
        });


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

                msgMap.put("nickname", nickname);
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
