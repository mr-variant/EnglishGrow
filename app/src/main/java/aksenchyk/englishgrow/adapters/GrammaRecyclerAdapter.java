package aksenchyk.englishgrow.adapters;

import android.content.res.Resources;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import aksenchyk.englishgrow.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class GrammaRecyclerAdapter extends FirestoreAdapter<GrammaRecyclerAdapter.ViewHolder>  {

    public interface OnGrammaSelectedListener {
        void onGrammaSelected(DocumentSnapshot word);
    }

    private GrammaRecyclerAdapter.OnGrammaSelectedListener mListener;

    public GrammaRecyclerAdapter(Query query, GrammaRecyclerAdapter.OnGrammaSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @Override
    public GrammaRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GrammaRecyclerAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_gramma, parent, false));
    }

    @Override
    public void onBindViewHolder(GrammaRecyclerAdapter.ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.textViewGrammaUnit) TextView textViewGrammaUnit;
        @BindView(R.id.textViewUnitNum) TextView textViewUnitNum;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final GrammaRecyclerAdapter.OnGrammaSelectedListener listener) {



            textViewGrammaUnit.setText(snapshot.get("unit").toString());
            textViewUnitNum.setText("Unit "+ snapshot.get("unitNum").toString());

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onGrammaSelected(snapshot);
                    }
                }
            });


        }

    }



}
