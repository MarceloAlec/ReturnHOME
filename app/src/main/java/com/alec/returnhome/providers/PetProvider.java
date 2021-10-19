package com.alec.returnhome.providers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alec.returnhome.R;
import com.alec.returnhome.models.Pet;
import com.alec.returnhome.retrofit.IPetApi;
import com.alec.returnhome.retrofit.RetrofitClient;

import java.util.ArrayList;

import retrofit2.Call;


public class PetProvider extends RecyclerView.Adapter<PetProvider.PetsViewHolder> {


    private Context context;
    //RAIZ DE LA URL QUE FORMA PARTE DE LA PETICION
    private String baseUrl = "http://192.168.0.3:82/api.returnhome.com/v1/";
    //LISTA MASCOTAS SE ENVIAN AL RECYCLERVIEW
    private ArrayList<Pet> petArrayList;
    LayoutInflater inflater;

    public PetProvider(Context context){
        this.context = context;
    }

    public PetProvider(Context context, ArrayList<Pet> petArrayList){
        this.inflater = LayoutInflater.from(context);
        this.petArrayList = petArrayList;

    }

    public Call<String> getPets(int idClient){

        return RetrofitClient.getClient(baseUrl).create(IPetApi.class).read(idClient);
    }


    @NonNull
    @Override
    //VA A INFLAR LA VISTA
    //RELLENA CADA ELEMENTO DEL RECYCLER VIEW CON UNA VISTA(texto, imagen)
    public PetsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //SE CREA LA VISTA DE CADA ITEM
        //EL ATTACHTOROOT SERA FALSE PARA NO CARGAR  LOS ITEMS EN EL RECYCLER MUY RAPIDO
        //CON EL INFLATER SE PROCEDE A DIBUJAR LA VISTA
        //COMO PARAMETRO TAMBIEN SE INDICA EL DISEÑO QUE TENDRA CADA ITEM DE LA LISTA
        View view = inflater.inflate(R.layout.list_pets, parent, false);
        //SE ENVIA LA VISTA AL CONSTRUCTOR DE LA CLASE PETSVIEWHOLDER
        PetsViewHolder petsViewHolder = new PetsViewHolder(view);
        return petsViewHolder;
    }

    //CADA ITEM DEL RECYCLERVIEW HACE USO DE LA VISTA CREADA EN EL VIEWHOLDER
    //DE MANERA QUE TODOS LOS ITEMS COMPARTAN UN MISMO DISEÑO

    //POSITION: POSICION DE CADA ITEM DE LA LISTA

    //VA A BINDEAR A CADA VISTA DEL VIEWHOLDER CON LOS DATOS
    //PARA POBLAR CADA POSICION
    @Override
    public void onBindViewHolder(@NonNull PetsViewHolder holder, int position) {
        //A CADA POSICION DEL RECYCLER TENDRA LA MISMA VISTA CREADA EN ONCREATEVIEWHOLDER
        String namePet = petArrayList.get(position).getName();
        String breedPet = petArrayList.get(position).getBreed();
        String genderPet = String.valueOf(petArrayList.get(position).getGender());
        String descriptionPet = petArrayList.get(position).getDescription();
        holder.mTextViewNamePet.setText(namePet);
        holder.mTextViewBreedPet.setText(breedPet);
        holder.mTextViewGenderPet.setText(genderPet);
        holder.mTextViewDescriptionPet.setText(descriptionPet);
    }


    //OBTIENE LOS ELEMENTOS EN EL RECYCLER VIEW
    @Override
    public int getItemCount() {
        return petArrayList.size();
    }

    //MANEJA LOS ELEMENTOS QUE CONTIENE LA VISTA DE CADA ITEM DENTRO DEL RECYCLER
    public class PetsViewHolder extends RecyclerView.ViewHolder{

        TextView mTextViewNamePet;
        TextView mTextViewBreedPet;
        TextView mTextViewGenderPet;
        TextView mTextViewDescriptionPet;
        ImageView mImageViewPet;
        Button mButtonDelete;

        public PetsViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewNamePet = itemView.findViewById(R.id.namePet);
            mTextViewBreedPet = itemView.findViewById(R.id.breedPet);
            mTextViewGenderPet = itemView.findViewById(R.id.genderPet);
            mTextViewDescriptionPet = itemView.findViewById(R.id.descriptionPet);
            mImageViewPet = itemView.findViewById(R.id.imageView_pet);
            mButtonDelete = itemView.findViewById(R.id.btnDelete);

            mButtonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("CLICK");
                }
            });

        }
    }
}
