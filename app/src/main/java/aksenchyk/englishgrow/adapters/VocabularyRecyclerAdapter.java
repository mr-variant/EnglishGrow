package aksenchyk.englishgrow.adapters;

import android.content.Intent;
import android.content.res.Resources;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import aksenchyk.englishgrow.R;
import aksenchyk.englishgrow.WordInfoActivity;
import aksenchyk.englishgrow.models.Vocabulary;
import butterknife.BindView;
import butterknife.ButterKnife;

public class VocabularyRecyclerAdapter extends FirestoreAdapter<VocabularyRecyclerAdapter.ViewHolder> {

    public interface OnVocabularySelectedListener {
        void onVocabularySelected(DocumentSnapshot word);
    }

    private OnVocabularySelectedListener mListener;
    private TextToSpeech mTTS;

    public VocabularyRecyclerAdapter(Query query, TextToSpeech mTTS, OnVocabularySelectedListener listener) {
        super(query);
        this.mTTS = mTTS;
        mListener = listener;
    }

    @Override
    public VocabularyRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VocabularyRecyclerAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_vocabulary, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mTTS, mListener);
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.textViewItemWord) TextView textViewItemWord;
        @BindView(R.id.textViewItemTransl) TextView textViewItemTransl;
        @BindView(R.id.imageViewItemStatus) ImageView imageViewItemStatus;
        @BindView(R.id.imageViewItemSpeech) ImageView imageViewItemSpeech;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final TextToSpeech tts,
                         final OnVocabularySelectedListener listener) {

            final Vocabulary vocabulary = snapshot.toObject(Vocabulary.class);
            Resources resources = itemView.getResources();

            textViewItemWord.setText(snapshot.getId());
            textViewItemTransl.setText(vocabulary.getTranslation());

            switch (vocabulary.getStatus()) {
                case Vocabulary.FIELD_STATUS_AGAIN:
                    imageViewItemStatus.setImageResource(R.drawable.cyrcle_again);
                break;

                case Vocabulary.FIELD_STATUS_EASY:
                    imageViewItemStatus.setImageResource(R.drawable.cyrcle_easy);
                    break;

                case Vocabulary.FIELD_STATUS_GOOD:
                    imageViewItemStatus.setImageResource(R.drawable.cyrcle_good);
                    break;

                case Vocabulary.FIELD_STATUS_HARD:
                    imageViewItemStatus.setImageResource(R.drawable.cyrcle_hard);
                    break;
            }

            imageViewItemSpeech.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String text = snapshot.getId();
                    tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                }
            });

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onVocabularySelected(snapshot);
                    }
                }
            });


        }

    }



}
