package aksenchyk.englishgrow.adapters;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import aksenchyk.englishgrow.ChangeCommentActivity;
import aksenchyk.englishgrow.CommentsActivity;
import aksenchyk.englishgrow.NewPostActivity;
import aksenchyk.englishgrow.R;
import aksenchyk.englishgrow.models.Comment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;


public class CommentsRecyclerAdapter extends FirestoreAdapter<CommentsRecyclerAdapter.ViewHolder> {

    private static String currentUserId;
    private String blogPostID;

    public CommentsRecyclerAdapter(Query query, String blogPostID) {
        super(query);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        this.blogPostID = blogPostID;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_comment, parent, false));
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //!!!
        String commentID = getSnapshot(position).getId();
        holder.bind(getSnapshot(position).toObject(Comment.class), commentID, blogPostID);
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


        public void bind(final Comment comment, final String commentID, final String blogPostID) {

            final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

            final String user_id = comment.getUser_id();

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


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Build an AlertDialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);


                    final int arrayId;
                    if(comment.getUser_id().equals(currentUserId)) {
                        arrayId = R.array.alertCommentCurrentUser;
                    } else {
                        arrayId = R.array.alertCommentAnotherUser;
                    }

                    // Set the list of items for alert dialog
                    builder.setItems(arrayId , new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0: //copy
                                    String text = textViewCommentMessage.getText().toString();
                                    ClipData clipData = ClipData.newPlainText("text", text);
                                    ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
                                    clipboardManager.setPrimaryClip(clipData);

                                    Toast.makeText(context, context.getText(R.string.comment_copy), Toast.LENGTH_SHORT).show();
                                    break;

                                case 1: //change

                                    Intent changeCommentIntent = new Intent(context, ChangeCommentActivity.class);
                                    context.startActivity(changeCommentIntent);

                                    break;

                                case  2: //delete

                                    firebaseFirestore.collection("Posts/" + blogPostID + "/Comment").document(commentID).delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(context, context.getText(R.string.comment_delete), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    break;
                            }
                        }
                    });

                    AlertDialog dialog = builder.create();
                    // Display the alert dialog on interface
                    dialog.show();


                }
            });

        }

    }

}