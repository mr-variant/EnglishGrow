package aksenchyk.englishgrow.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aksenchyk.englishgrow.R;
import aksenchyk.englishgrow.models.BlogPost;
import de.hdodenhof.circleimageview.CircleImageView;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    private List<BlogPost> blogList;
    private Context context;

    private FirebaseFirestore firebaseFirestore;

    private FirebaseAuth firebaseAuth;

    public BlogRecyclerAdapter(List<BlogPost> blogList) {
        this.blogList = blogList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_blog, parent,false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final String blogPostID = blogList.get(position).BlogPostID;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();



        String user_id = blogList.get(position).getUser_id();


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

        String desc_data = blogList.get(position).getDesc();
        holder.setDescText(desc_data);


        String image_url = blogList.get(position).getImage_url();
        String thumbUri = blogList.get(position).getImage_thumb();
        holder.setBlogImage(image_url, thumbUri);


        try {
            long millisecond = blogList.get(position).getTimestamp().getTime();
            String dateString = DateFormat.format("dd MMM E hh:mm a", new Date(millisecond)).toString();
            holder.setTime(dateString);
        } catch (Exception e) {
            Toast.makeText(context, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }


        //Get Likes Count
        firebaseFirestore.collection("Posts/" + blogPostID + "/Likes").addSnapshotListener( new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if(documentSnapshots.isEmpty()){
                    holder.updateLikesCount(0);
                } else {
                    int count = documentSnapshots.size();
                    holder.updateLikesCount(count);
                }
            }
        });


        //Get Likes
        firebaseFirestore.collection("Posts/" + blogPostID + "/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    holder.imageViewBlogLikeBtn.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_red));
                } else {
                    holder.imageViewBlogLikeBtn.setImageDrawable(context.getDrawable(R.drawable.ic_favorite));
                }
            }
        });


        holder.imageViewBlogLikeBtn.setOnClickListener(new View.OnClickListener() {
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

    }

    @Override
    public int getItemCount() {
        return blogList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView textViewBlogDesc;
        private ImageView imageViewBlogPhoto;
        private TextView textViewBlogPostedDate;
        private TextView textViewBlogUsername;
        private CircleImageView circleImageViewUserPhoto;
        private ImageView imageViewBlogLikeBtn;
        private TextView textViewBlogLikeCount;



        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            imageViewBlogLikeBtn = mView.findViewById(R.id.imageViewBlogLikeBtn);

        }

        public void setDescText(String descText) {
            textViewBlogDesc = mView.findViewById(R.id.textViewBlogDesc);
            textViewBlogDesc.setText(descText);
        }



        public void setBlogImage(String downloadUri, String thumbUri){
            imageViewBlogPhoto = mView.findViewById(R.id.imageViewBlogPhoto);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.image_placeholder);

            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri).thumbnail(
                    Glide.with(context).load(thumbUri)
            ).into(imageViewBlogPhoto);
        }


        public void setTime(String date) {
            textViewBlogPostedDate = mView.findViewById(R.id.textViewBlogPostedDate);
            textViewBlogPostedDate.setText(date);
        }


        public void setUserData(String name, String image){
            circleImageViewUserPhoto = mView.findViewById(R.id.circleImageViewUserPhoto);
            textViewBlogUsername = mView.findViewById(R.id.textViewBlogUsername);

            textViewBlogUsername.setText(name);

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.profile_placeholder);

            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(circleImageViewUserPhoto);
        }


        public void updateLikesCount(int count){
            textViewBlogLikeCount = mView.findViewById(R.id.textViewBlogLikeCount);
            textViewBlogLikeCount.setText(String.valueOf(count));
        }

    }

}
