package aksenchyk.englishgrow.bottom_navigation_fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import aksenchyk.englishgrow.MainActivity;
import aksenchyk.englishgrow.R;
import aksenchyk.englishgrow.adapters.ChatListAdapter;
import aksenchyk.englishgrow.models.Chats;


public class ChatFragment extends Fragment {

    private RecyclerView mChatListRecyclerView;
    private FirebaseFirestore mFirestore;

    private List<Chats> chatsList;
    private ChatListAdapter chatListAdapter;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

/*
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        chatsList = new ArrayList<>();
        chatListAdapter = new ChatListAdapter(chatsList);

        mChatListRecyclerView = (RecyclerView) rootView.findViewById(R.id.chatListRecyclerView);
        mChatListRecyclerView.setHasFixedSize(true);
        mChatListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mChatListRecyclerView.setAdapter(chatListAdapter);

        mFirestore = FirebaseFirestore.getInstance();

        mFirestore.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if(e != null) {
                    Log.d("FireTag","Error" + e.getMessage());
                }

                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                    if(doc.getType() == DocumentChange.Type.ADDED) {
                      //  String name = doc.getDocument().getString("name");
                      //  Toast.makeText(getActivity(),name,Toast.LENGTH_SHORT).show();

                      Chats chats =  doc.getDocument().toObject(Chats.class);
                      chatsList.add(chats);
                      chatListAdapter.notifyDataSetChanged();
                    }

                }


            }
        });*/



        return inflater.inflate(R.layout.fragment_chat, container, false);
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        chatsList = new ArrayList<>();
        chatListAdapter = new ChatListAdapter(chatsList);

        mChatListRecyclerView = (RecyclerView) view.findViewById(R.id.chatListRecyclerView);
        mChatListRecyclerView.setHasFixedSize(true);
        mChatListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mChatListRecyclerView.setAdapter(chatListAdapter);

        mFirestore = FirebaseFirestore.getInstance();

        mFirestore.collection("Chats").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if(e != null) {
                    Log.d("FireTag","Error" + e.getMessage());
                }

                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                    if(doc.getType() == DocumentChange.Type.ADDED) {
                        //  String name = doc.getDocument().getString("name");
                        //  Toast.makeText(getActivity(),name,Toast.LENGTH_SHORT).show();

                        Chats chats =  doc.getDocument().toObject(Chats.class);
                        chatsList.add(chats);
                        chatListAdapter.notifyDataSetChanged();
                    }

                }


            }
        });

    }





}
