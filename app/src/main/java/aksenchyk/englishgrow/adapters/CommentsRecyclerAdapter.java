package aksenchyk.englishgrow.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import aksenchyk.englishgrow.R;
import aksenchyk.englishgrow.models.Comment;
import de.hdodenhof.circleimageview.CircleImageView;


public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder> {

    private List<Comment> commentsList;
    private Context context;

    private FirebaseFirestore firebaseFirestore;


    public CommentsRecyclerAdapter(List<Comment> commentsList){
        this.commentsList = commentsList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_comment, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CommentsRecyclerAdapter.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);

        String user_id = commentsList.get(position).getUser_id();
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){

                    String userName = task.getResult().getString("name");
                    String userImage = task.getResult().getString("image");

                    holder.setUserData(userName, userImage);
                } else {
                    //Firebase Exception
                }
            }
        });


        String commentMessage = commentsList.get(position).getMessage();
        holder.setCommentMessage(commentMessage);
    }

    @Override
    public int getItemCount() {
        if(commentsList != null) {
            return commentsList.size();
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView textViewCommentMessage;
        private TextView textViewCommentUsername;
        private CircleImageView circleImageViewCommentUserPhoto;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }


        public void setUserData(String name, String image){
            circleImageViewCommentUserPhoto = mView.findViewById(R.id.circleImageViewCommentUserPhoto);
            textViewCommentUsername = mView.findViewById(R.id.textViewCommentUsername);

            textViewCommentUsername.setText(name);

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.profile_placeholder);

            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(circleImageViewCommentUserPhoto);
        }


        public void setCommentMessage(String message){
            textViewCommentMessage = mView.findViewById(R.id.textViewCommentMessage);
            textViewCommentMessage.setText(message);
        }
    }

}