package aksenchyk.englishgrow.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.icu.util.TimeUnit;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aksenchyk.englishgrow.CommentsActivity;
import aksenchyk.englishgrow.LoginActivity;
import aksenchyk.englishgrow.R;
import aksenchyk.englishgrow.models.BlogPost;
import aksenchyk.englishgrow.models.User;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static java.util.concurrent.TimeUnit.DAYS;

public class BlogRecyclerAdapter extends FirestoreAdapter<BlogRecyclerAdapter.ViewHolder> {
    /*
        public interface OnBlogSelectedListener {
            void onBlogSelected(DocumentSnapshot blog);
        }

        private OnBlogSelectedListener mListener;
    */
    public BlogRecyclerAdapter(Query query/*, OnBlogSelectedListener listener*/) {
        super(query);
        // mListener = listener;
    }



    private Context context;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String currentUserId;




    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_blog, parent,false);
        context = parent.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {





        holder.bind(getSnapshot(position)/*, mListener*/, context);
    }









    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textViewBlogDesc)
        ExpandableTextView textViewBlogDesc;

        @BindView(R.id.imageViewBlogPhoto) ImageView imageViewBlogPhoto;
        @BindView(R.id.textViewBlogPostedDate) TextView textViewBlogPostedDate;
        @BindView(R.id.textViewBlogUsername) TextView textViewBlogUsername;
        @BindView(R.id.circleImageViewUserPhoto) CircleImageView circleImageViewUserPhoto;
        @BindView(R.id.imageViewBlogLikeBtn) ImageView imageViewBlogLikeBtn;
        @BindView(R.id.textViewBlogLikeCount) TextView textViewBlogLikeCount;
        @BindView(R.id.textViewBlogCommentsCount) TextView textViewBlogCommentsCount;
        @BindView(R.id.imageViewBlogShare) ImageView imageViewBlogShare;
        @BindView(R.id.imageViewComments) ImageView imageViewComments;
        @BindView(R.id.imageViewBlogMoreBtn) ImageView imageViewBlogMoreBtn;

        private FirebaseFirestore firebaseFirestore;
        private FirebaseAuth firebaseAuth;

        public ViewHolder(View itemView) {
            super(itemView);
            firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseAuth = FirebaseAuth.getInstance();
            ButterKnife.bind(this, itemView);
        }

        public void bind(final DocumentSnapshot snapshot,
                /*final OnBlogSelectedListener listener,*/
                         final Context context) {


            BlogPost blogPost = snapshot.toObject(BlogPost.class);

           // final String blogPostID = blogPost.BlogPostID;

            final String blogPostID = snapshot.getId();
            final String blogUserId = blogPost.getUser_id();
            final String currentUserId = firebaseAuth.getCurrentUser().getUid();


            //Log.d("--->#"," " );
//!!!!
            firebaseFirestore.collection("Users").document(blogUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()) {
                        User user = task.getResult().toObject(User.class);
                        String userName = user.getName();
                        String userImage = user.getImage();

                        setUserData(userName, userImage);

                    }
                }
            });



/*
            String userName = userList.get(position).getName();
            String userImage = userList.get(position).getImage();

            setUserData(userName, userImage);
*/


            String desc_data = blogPost.getDesc();
            setDescText(desc_data);


            String image_url = blogPost.getImage_url();
            String thumbUri = blogPost.getImage_thumb();
            setBlogImage(image_url, thumbUri);


            try {
                long millisecond = blogPost.getTimestamp().getTime();
                //String dateString = DateFormat.format("dd MMM E HH:mm ", new Date(millisecond)).toString();
                String dateString;

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

                setTime(dateString);
            } catch (Exception e) {
                Toast.makeText(context, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }


            //Get Likes Count
            firebaseFirestore.collection("Posts/" + blogPostID + "/Likes").addSnapshotListener( new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    if(documentSnapshots.isEmpty()){
                        updateLikesCount(0);
                    } else {
                        int count = documentSnapshots.size();
                        updateLikesCount(count);
                    }
                }
            });


            //Get Likes
            firebaseFirestore.collection("Posts/" + blogPostID + "/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                    if(documentSnapshot.exists()){
                        imageViewBlogLikeBtn.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_red));
                    } else {
                        imageViewBlogLikeBtn.setImageDrawable(context.getDrawable(R.drawable.ic_favorite));
                    }
                }
            });


            //Get Comment Count
            firebaseFirestore.collection("Posts/" + blogPostID + "/Comment").addSnapshotListener( new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    if(documentSnapshots.isEmpty()){
                        updateCommentsCount(0);
                    } else {
                        int count = documentSnapshots.size();
                        updateCommentsCount(count);
                    }
                }
            });


            imageViewBlogLikeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    firebaseFirestore.collection("Posts/" + blogPostID + "/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.getResult().exists()){
                                firebaseFirestore.collection("Posts/" + blogPostID + "/Likes").document(currentUserId).delete();
                            } else {
                                Map<String, Object> likesMap = new HashMap<>();
                                likesMap.put("timestamp", FieldValue.serverTimestamp());
                                firebaseFirestore.collection("Posts/" + blogPostID + "/Likes").document(currentUserId).set(likesMap);
                            }
                        }
                    });
                }
            });


            imageViewBlogShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get access to the URI for the bitmap
                    Uri bmpUri = getLocalBitmapUri(imageViewBlogPhoto);
                    if (bmpUri != null) {
                        // Construct a ShareIntent with link to image
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_TEXT, getDesc());
                        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                        shareIntent.setType("*/*");
                        // Launch sharing dialog for image
                        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_blog_post)));
                    } else {
                        // ...sharing failed, handle error
                        Toast.makeText(context,context.getText(R.string.error_share),Toast.LENGTH_LONG).show();
                    }
                }
            });


            imageViewComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentComments = new Intent(context, CommentsActivity.class);
                    intentComments.putExtra(CommentsActivity.KEY_BLOG_ID, blogPostID);
                    context.startActivity(intentComments);

                }
            });



            imageViewBlogMoreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(context, v);
                    //popupMenu.inflate(R.menu.popupmenu);

                    if(blogUserId.equals(currentUserId)) {
                        popupMenu.getMenu().add(1, R.id.menuBlogDelete, 2, context.getString(R.string.popup_blog_delete));
                    } else {
                        popupMenu.getMenu().add(1, R.id.menuBlogReport, 1, context.getString(R.string.popup_blog_report));
                    }

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menuBlogReport:
                                    firebaseFirestore.collection("Posts/" + blogPostID + "/Reports").document(blogUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.getResult().exists()){
                                                Toast.makeText(context, context.getString(R.string.popup_blog_report_msg), Toast.LENGTH_LONG).show();
                                            } else {
                                                Map<String, Object> reportsMap = new HashMap<>();
                                                reportsMap.put("timestamp", FieldValue.serverTimestamp());
                                                firebaseFirestore.collection("Posts/" + blogPostID + "/Reports").document(blogUserId).set(reportsMap);
                                            }
                                        }
                                    });
                                    return true;

                                case R.id.menuBlogDelete:

                                    firebaseFirestore.collection("Posts").document(blogPostID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            //TODO
                                            // blogList.remove(position);
                                            // userList.remove(position);

                                        }
                                    });

                                    return true;

                                default:
                                    return false;
                            }
                        }
                    });


                    popupMenu.show();



                }
            });


        }



        public void setDescText(String descText) {
            textViewBlogDesc.setText(descText);
        }

        public void setBlogImage(String downloadUri, String thumbUri){

                imageViewBlogPhoto.setVisibility(View.VISIBLE);

                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.image_placeholder);
                Glide.with(imageViewBlogPhoto.getContext()).applyDefaultRequestOptions(requestOptions).load(downloadUri).thumbnail(
                        Glide.with(imageViewBlogPhoto.getContext()).load(thumbUri)
                ).into(imageViewBlogPhoto);


                if(downloadUri.equals("not_img")) {
                    imageViewBlogPhoto.setVisibility(View.GONE);
                }
        }

        public void setTime(String date) {
            textViewBlogPostedDate.setText(date);
        }

        public void setUserData(String name, String image){
            textViewBlogUsername.setText(name);
            //circleImageViewUserPhoto.setImageDrawable();
            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.default_image);
            Glide.with(circleImageViewUserPhoto.getContext()).applyDefaultRequestOptions(placeholderOption).load(image).into(circleImageViewUserPhoto);
        }


        public void updateLikesCount(int count){
            textViewBlogLikeCount.setText(String.valueOf(count));
        }

        public void updateCommentsCount(int count){
            textViewBlogCommentsCount.setText(String.valueOf(count));
        }

        public String getDesc() {
            return textViewBlogDesc.getText().toString();
        }

        // Returns the URI path to the Bitmap displayed in specified ImageView
        public Uri getLocalBitmapUri(ImageView imageView) {
            // Extract Bitmap from ImageView drawable
            Drawable drawable = imageView.getDrawable();
            Bitmap bmp = null;
            if (drawable instanceof BitmapDrawable){
                bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            } else {
                return null;
            }
            // Store image to default external storage directory
            Uri bmpUri = null;
            try {
                // Use methods on Context to access package-specific directories on external storage.
                // This way, you don't need to request external read/write permission.
                // See https://youtu.be/5xVh-7ywKpE?t=25m25s
                File file =  new File(imageView.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
                FileOutputStream out = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.close();
                // **Warning:** This will fail for API >= 24, use a FileProvider as shown below instead.
                bmpUri = Uri.fromFile(file);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return bmpUri;
        }

    }

}
