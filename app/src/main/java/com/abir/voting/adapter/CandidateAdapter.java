package com.abir.voting.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.abir.voting.R;
import com.abir.voting.model.Candidate;
import com.bumptech.glide.Glide;
import java.util.List;

public class CandidateAdapter extends RecyclerView.Adapter<CandidateAdapter.ViewHolder> {

    private Context mContext;
    private final List<Candidate> candidateModels;
    private OnSelected onSelected;
    int index;

    public CandidateAdapter(List<Candidate> uploads) {
        candidateModels = uploads;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.candidatemodel, parent, false);
        this.mContext = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try {
            holder.name.setText(candidateModels.get(position).getCandidateName());
            if (candidateModels.get(position).getImageURL().equalsIgnoreCase("default")) {
                holder.imageView.setImageResource(R.drawable.defaulimage);
            } else {
                Glide.with(mContext).load(candidateModels.get(position).getImageURL()).into(holder.imageView);
            }

        } catch (Exception e) { //not possible to get null
        }

        if(index==position){
            holder.votingLayout.setBackground(ContextCompat.getDrawable(mContext, R.drawable.hollow_rectangle));
        }else {
            holder.votingLayout.setBackground(ContextCompat.getDrawable(mContext, R.drawable.hollow_rectangle_light));
        }
        holder.votingLayout.setOnClickListener(view -> {
            onSelected.SelectedCandidate(candidateModels.get(position));
            index=position;
            notifyDataSetChanged();
        });

    }

    @Override
    public int getItemCount() {
        return candidateModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ConstraintLayout votingLayout;
        private final TextView name;
        private final ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.h_topjiintxtView);
            imageView = itemView.findViewById(R.id.h_topjiinimgView);
            votingLayout = itemView.findViewById(R.id.votingLayout);
        }

    }


    public void setOnItemClickListener(OnSelected listener) {
        onSelected = listener;
    }

    public interface OnSelected {
        void SelectedCandidate(Candidate candidate);
    }
}