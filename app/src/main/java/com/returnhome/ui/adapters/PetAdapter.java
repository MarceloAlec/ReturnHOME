package com.returnhome.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.returnhome.R;
import com.returnhome.models.Pet;
import com.returnhome.providers.PetProvider;
import com.returnhome.ui.activities.LoginActivity;
import com.returnhome.utils.retrofit.ResponseApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder> {


    //LISTA MASCOTAS SE ENVIAN AL RECYCLERVIEW
    private ArrayList<Pet> petArrayList;
    LayoutInflater inflater;
    PetProvider mPetProvider;
    Context context;

    public PetAdapter(Context context, ArrayList<Pet> petArrayList){
        this.context=context;
        this.inflater = LayoutInflater.from(context);
        this.petArrayList = petArrayList;
        mPetProvider = new PetProvider(context);
    }

    @NonNull
    @Override
    //VA A INFLAR LA VISTA
    //RELLENA CADA ELEMENTO DEL RECYCLER VIEW CON UNA VISTA(texto, imagen)
    public PetAdapter.PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //SE CREA LA VISTA DE CADA ITEM
        //EL ATTACHTOROOT SERA FALSE PARA NO CARGAR  LOS ITEMS EN EL RECYCLER MUY RAPIDO
        //CON EL INFLATER SE PROCEDE A DIBUJAR LA VISTA
        //COMO PARAMETRO TAMBIEN SE INDICA EL DISEÑO QUE TENDRA CADA ITEM DE LA LISTA
        View view = inflater.inflate(R.layout.list_pets, parent, false);
        //SE ENVIA LA VISTA AL CONSTRUCTOR DE LA CLASE PETSVIEWHOLDER
        PetAdapter.PetViewHolder petsViewHolder = new PetAdapter.PetViewHolder(view);
        return petsViewHolder;
    }

    //CADA ITEM DEL RECYCLERVIEW HACE USO DE LA VISTA CREADA EN EL VIEWHOLDER
    //DE MANERA QUE TODOS LOS ITEMS COMPARTAN UN MISMO DISEÑO

    //POSITION: POSICION DE CADA ITEM DE LA LISTA

    //VA A BINDEAR A CADA VISTA DEL VIEWHOLDER CON LOS DATOS
    //PARA POBLAR CADA POSICION
    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        //A CADA POSICION DEL RECYCLER TENDRA LA MISMA VISTA CREADA EN ONCREATEVIEWHOLDER
        holder.mTextViewNamePet.setText(petArrayList.get(position).getName());
        holder.mTextViewBreedPet.setText(petArrayList.get(position).getBreed());

        //PARA TENER ACCESO AL CONTEXTO EN EL PETVIEWHOLDER Y LLAMAR A LA FUNCION STARTACTIVITY
        //LOS EVENTOS DE CLIC SE ASIGNARAN EN PETVIEWHOLDER
        holder.mButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                mPetProvider.deletePet(petArrayList.get(holder.getBindingAdapterPosition()).getIdPet()).enqueue(new Callback<ResponseApi>() {
                    @Override
                    public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {
                        if(response.isSuccessful()){
                            petArrayList.remove(holder.getBindingAdapterPosition());
                            notifyItemRemoved(holder.getBindingAdapterPosition());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseApi> call, Throwable t) {
                        Toast.makeText(context, "Eliminación fallida", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });




    }

    //OBTIENE LOS ELEMENTOS EN EL RECYCLER VIEW
    @Override
    public int getItemCount() {
        return petArrayList.size();
    }


    //MANEJA LOS ELEMENTOS QUE CONTIENE LA VISTA DE CADA ITEM DENTRO DEL RECYCLER
    public class PetViewHolder extends RecyclerView.ViewHolder {

        TextView mTextViewNamePet;
        TextView mTextViewBreedPet;
        ImageView mImageViewPet;
        Button mButtonDelete;
        Button mButtonEdit;


        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewNamePet = itemView.findViewById(R.id.namePet);
            mTextViewBreedPet = itemView.findViewById(R.id.breedPet);
            mImageViewPet = itemView.findViewById(R.id.imageView_pet);
            mButtonDelete = itemView.findViewById(R.id.btnDelete);
            mButtonEdit = itemView.findViewById(R.id.btnEdit);


        }


    }


}
