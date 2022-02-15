package com.returnhome.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.returnhome.R;
import com.returnhome.modelos.Mascota;

import java.util.ArrayList;

public class MissingPetAdapter extends RecyclerView.Adapter<MissingPetAdapter.MissingPetViewHolder>{

    private ArrayList<Mascota> mascotaArrayList;

    LayoutInflater inflater;
    Context context;

    public MissingPetAdapter(Context context, ArrayList<Mascota> mascotaArrayList){
        this.context=context;
        this.inflater = LayoutInflater.from(context);
        this.mascotaArrayList = mascotaArrayList;
    }

    @NonNull
    @Override
    public MissingPetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.cardview_missing_pets, parent, false);
        MissingPetAdapter.MissingPetViewHolder petsViewHolder = new MissingPetAdapter.MissingPetViewHolder(view);
        return petsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MissingPetViewHolder holder, int position) {
        holder.mTextViewNamePet.setText(mascotaArrayList.get(position).getNombre());
        holder.mTextViewBreedPet.setText(mascotaArrayList.get(position).getRaza());
    }

    @Override
    public int getItemCount() {
        return mascotaArrayList.size();
    }


    public class MissingPetViewHolder extends RecyclerView.ViewHolder {

        TextView mTextViewNamePet;
        TextView mTextViewBreedPet;
        ImageView mImageViewPet;

        public MissingPetViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewNamePet = itemView.findViewById(R.id.nameMissingPet);
            mTextViewBreedPet = itemView.findViewById(R.id.breedMissingPet);
            mImageViewPet = itemView.findViewById(R.id.imageViewMissingPet);
        }

    }
}
