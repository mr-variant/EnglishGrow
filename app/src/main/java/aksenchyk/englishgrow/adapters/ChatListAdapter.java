package aksenchyk.englishgrow.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import aksenchyk.englishgrow.ChatRoomActivity;
import aksenchyk.englishgrow.R;
import aksenchyk.englishgrow.models.Chats;

/**
 * Created by ixvar on 2/17/2018.
 */

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    private List<Chats> chatsList;


    public ChatListAdapter(List<Chats> chatsList) {
        this.chatsList = chatsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chats_list_item,parent,false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.nameText.setText(chatsList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return chatsList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View mView;

        public TextView nameText;
        public CardView roomCardView;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            nameText = (TextView) mView.findViewById(R.id.nameChatTextView);
            roomCardView = (CardView) mView.findViewById(R.id.roomCardView);
            roomCardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                switch (v.getId()) {
                    case R.id.roomCardView:
                        Context context = v.getContext();
                        Intent intentToChatRoom = new Intent(context, ChatRoomActivity.class);
                        intentToChatRoom.putExtra("roomName",chatsList.get(position).getName());
                        intentToChatRoom.putExtra("roomId",chatsList.get(position).chatRoomId);

                        context.startActivity(intentToChatRoom);

                        break;
                }
            }
        }


    }




}
