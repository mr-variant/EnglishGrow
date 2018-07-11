package aksenchyk.englishgrow.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import aksenchyk.englishgrow.ChatRoomActivity;
import aksenchyk.englishgrow.R;
import aksenchyk.englishgrow.models.Chats;
import aksenchyk.englishgrow.models.Messages;

/**
 * Created by ixvar on 2/25/2018.
 */

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    private List<Messages> messagesList;


    public MessagesAdapter(List<Messages> messagesList) {
        this.messagesList = messagesList;
    }

    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_room_list_item,parent,false);
        return new MessagesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessagesAdapter.ViewHolder holder, int position) {
        holder.nicknameTextView.setText(messagesList.get(position).getNickname());
        holder.messageTextView.setText(messagesList.get(position).getMsg());
        holder.dateTextView.setText(messagesList.get(position).getTime().toString());
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public TextView nicknameTextView;
        public TextView messageTextView;
        public TextView dateTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            nicknameTextView = (TextView) mView.findViewById(R.id.nicknameTextView);
            messageTextView = (TextView) mView.findViewById(R.id.messageTextView);
            dateTextView = (TextView) mView.findViewById(R.id.dateTextView);
        }

    }


}
