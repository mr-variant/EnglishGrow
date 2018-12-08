package aksenchyk.englishgrow.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Date;

import aksenchyk.englishgrow.R;
import aksenchyk.englishgrow.WordInfoActivity;
import aksenchyk.englishgrow.models.Dictionary;
import aksenchyk.englishgrow.models.Vocabulary;
import butterknife.BindView;
import butterknife.ButterKnife;

public class WordSetRecyclerAdapter extends FirestoreAdapter<WordSetRecyclerAdapter.ViewHolder> {


    public WordSetRecyclerAdapter(Query query) {
        super(query);
    }

    @Override
    public WordSetRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WordSetRecyclerAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_word, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position));
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.textViewWordSet) TextView textViewWordSet;
        @BindView(R.id.textViewTranscriptWordSet) TextView textViewTranscriptWordSet;
        @BindView(R.id.textViewTranslWordSet) TextView textViewTranslWordSet;

        @BindView(R.id.imageViewAddNewWordSet) ImageView imageViewAddNewWordSet;
        @BindView(R.id.imageViewWordSet) ImageView imageViewWordSet;


        private FirebaseFirestore firebaseFirestore;
        private FirebaseAuth firebaseAuth;

        private String userID;



        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseAuth = FirebaseAuth.getInstance();
            userID = firebaseAuth.getCurrentUser().getUid();
        }

        public void bind(final DocumentSnapshot snapshot) {

            final String wordId = snapshot.getId();

            final Vocabulary vocabulary = snapshot.toObject(Vocabulary.class);
            vocabulary.setStatus(Vocabulary.FIELD_STATUS_AGAIN);
            vocabulary.setDateRepeat(new Date());
            vocabulary.setTrainingTranslationWord(false);
            vocabulary.setTrainingWordTranslation(false);

            textViewWordSet.setText(wordId);
            textViewTranscriptWordSet.setText(snapshot.get("transcription").toString());
            textViewTranslWordSet.setText(snapshot.get("translation").toString());

            String imageWord = snapshot.get("image").toString();

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.default_word_img);
            Glide.with(imageViewWordSet.getContext()).applyDefaultRequestOptions(requestOptions).load(imageWord).into(imageViewWordSet);


            firebaseFirestore.collection("Users").document(userID).collection("Vocabulary").document(wordId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.getResult().exists()) {
                        imageViewAddNewWordSet.setClickable(false);
                        imageViewAddNewWordSet.setImageDrawable(imageViewAddNewWordSet.getContext().getDrawable(R.drawable.ic_check_yellow));
                    }
                }
            });

            imageViewAddNewWordSet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    firebaseFirestore.collection("Users").document(userID).collection("Vocabulary").document(wordId)
                            .set(vocabulary)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    imageViewAddNewWordSet.setClickable(false);
                                    imageViewAddNewWordSet.setImageDrawable(imageViewAddNewWordSet.getContext().getDrawable(R.drawable.ic_check_yellow));
                                }
                            });
                }
            });




        }

    }



}

