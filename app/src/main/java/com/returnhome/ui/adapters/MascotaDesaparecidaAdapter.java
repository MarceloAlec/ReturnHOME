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
import com.returnhome.models.Mascota;

import java.util.ArrayList;

public class MascotaDesaparecidaAdapter extends RecyclerView.Adapter<MascotaDesaparecidaAdapter.MascotaDesaparecidaViewHolder>{

    private ArrayList<Mascota> mascotaArrayList;

    LayoutInflater inflater;
    Context context;

    public MascotaDesaparecidaAdapter(Context context, ArrayList<Mascota> mascotaArrayList){
        this.context=context;
        this.inflater = LayoutInflater.from(context);
        this.mascotaArrayList = mascotaArrayList;
    }

    @NonNull
    @Override
    public MascotaDesaparecidaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.cardview_missing_pets, parent, false);
        MascotaDesaparecidaViewHolder petsViewHolder = new MascotaDesaparecidaViewHolder(view);
        return petsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MascotaDesaparecidaViewHolder holder, int position) {
        holder.mTextViewNamePet.setText(mascotaArrayList.get(position).getNombre());
        holder.mTextViewBreedPet.setText(mascotaArrayList.get(position).getRaza());
    }

    @Override
    public int getItemCount() {
        return mascotaArrayList.size();
    }


    public class MascotaDesaparecidaViewHolder extends RecyclerView.ViewHolder {

        TextView mTextViewNamePet;
        TextView mTextViewBreedPet;
        ImageView mImageViewPet;

        public MascotaDesaparecidaViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewNamePet = itemView.findViewById(R.id.nameMissingPet);
            mTextViewBreedPet = itemView.findViewById(R.id.breedMissingPet);
            mImageViewPet = itemView.findViewById(R.id.imageViewMissingPet);
        }

    }
}
