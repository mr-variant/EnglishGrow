package aksenchyk.englishgrow.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import aksenchyk.englishgrow.R;
import aksenchyk.englishgrow.models.BlogPost;
import de.hdodenhof.circleimageview.CircleImageView;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    private List<BlogPost> blogList;
    private Context context;

    public BlogRecyclerAdapter(List<BlogPost> blogList) {
        this.blogList = blogList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_blog, parent,false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String desc_data = blogList.get(position).getDesc();
        holder.setDescText(desc_data);

        String image_url = blogList.get(position).getImage_url();
        holder.setBlogImage(image_url);


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

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDescText(String descText) {
            textViewBlogDesc = mView.findViewById(R.id.textViewBlogDesc);
            textViewBlogDesc.setText(descText);
        }

        public void setBlogImage(String downloadURI) {
            imageViewBlogPhoto = mView.findViewById(R.id.imageViewBlogPhoto);
            Glide.with(context).load(downloadURI).into(imageViewBlogPhoto);
        }

    }


}
