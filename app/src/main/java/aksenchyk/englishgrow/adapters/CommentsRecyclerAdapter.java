package aksenchyk.englishgrow.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import aksenchyk.englishgrow.R;
import aksenchyk.englishgrow.models.Comment;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


public class CommentsRecyclerAdapter extends FirestoreAdapter<CommentsRecyclerAdapter.ViewHolder> {

    public CommentsRecyclerAdapter(Query query) {
        super(query);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_comment, parent, false));
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position).toObject(Comment.class));
    }



    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textViewCommentMessage) TextView textViewCommentMessage;
        @BindView(R.id.textViewCommentUsername) TextView textViewCommentUsername;
        @BindView(R.id.circleImageViewCommentUserPhoto) CircleImageView circleImageViewCommentUserPhoto;
        @BindView(R.id.textViewCommentTime) TextView textViewCommentTime;

        private Context context;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            context = itemView.getContext();
        }




        private String setTime(Date date) {

            String dateString = context.getString(R.string.one_minute_ago);

            if (date == null) {
                return dateString;
            }

            try {
                //String dateString = DateFormat.format("dd MMM E HH:mm ", new Date(millisecond)).toString();
                long millisecond = date.getTime();
                Date todayDate = new Date();
                Date blogDate = new Date(millisecond);
                long millisecondAgo = todayDate.getTime() - millisecond;


                if(millisecondAgo < 60000) {
                    dateString = context.getString(R.string.one_minute_ago);
                } else if(millisecondAgo < 3600000) {
                    dateString = millisecondAgo / 60000 + " " + context.getString(R.string.n_minute_ago);
                } else if(millisecondAgo < 7200000) {
                    dateString = context.getString(R.string.one_hour_ago);
                } else if(DateUtils.isToday(millisecond)) { //????
                    dateString = DateFormat.format("'"+ context.getString(R.string.today_at) +"' HH:mm",blogDate).toString();
                } else if(DateUtils.isToday(blogDate.getTime() + DateUtils.DAY_IN_MILLIS)) { //????
                    dateString = DateFormat.format("'"+ context.getString(R.string.yesterday_at) +"' HH:mm", blogDate).toString();
                } else if(DateUtils.isToday(blogDate.getTime() + DateUtils.YEAR_IN_MILLIS)) {
                    dateString = DateFormat.format("d MMMM '"+ context.getString(R.string.in) +"' HH:mm", blogDate).toString();
                } else {
                    dateString = DateFormat.format("d MMM yyyy '"+ context.getString(R.string.in) +"' HH:mm", blogDate).toString();
                }

            } catch (Exception e) {
                Toast.makeText(context, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            return dateString;
        }


        public void bind(Comment comment) {

            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

            String user_id = comment.getUser_id();
            firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){

                        String userName = task.getResult().getString("name");
                        String userImage = task.getResult().getString("image");

                        textViewCommentUsername.setText(userName);
                        RequestOptions placeholderOption = new RequestOptions();
                        placeholderOption.placeholder(R.drawable.profile_placeholder);
                        Glide.with(circleImageViewCommentUserPhoto.getContext())
                                .applyDefaultRequestOptions(placeholderOption)
                                .load(userImage)
                                .into(circleImageViewCommentUserPhoto);

                    } else {
                        //Firebase Exception
                    }
                }
            });


            textViewCommentMessage.setText(comment.getMessage());


            String commentTime = setTime(comment.getTimestamp());
            textViewCommentTime.setText(commentTime);


        }

    }

}