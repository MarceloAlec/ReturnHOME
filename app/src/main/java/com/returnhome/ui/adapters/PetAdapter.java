package com.returnhome.ui.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.returnhome.R;
import com.returnhome.models.Pet;
import com.returnhome.providers.PetProvider;
import com.returnhome.utils.retrofit.ResponseApi;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder> {


    //LISTA MASCOTAS SE ENVIAN AL RECYCLERVIEW
    private Button mButtonUpdate;
    private TextInputEditText mTextInputName;
    private TextInputEditText mTextInputBreed;
    private TextInputEditText mTextInputDescription;
    private RadioButton mRadioButtonMalePet;
    private RadioButton mRadioButtonFemalePet;
    private ArrayList<Pet> petArrayList;
    private DialogPlus mDialogPlus;
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

        holder.mButtonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu(v, holder);
            }
        });


    }

    private void showMenu(View v, PetViewHolder holder ) {
        PopupMenu popupMenuCardView = new PopupMenu(v.getContext(), v);
        popupMenuCardView.inflate(R.menu.menu_cardview);

        popupMenuCardView.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.cardview_edit:

                        mDialogPlus = DialogPlus.newDialog(context)
                                .setContentHolder(new ViewHolder(R.layout.popup_update))


                                .create();

                        View view = mDialogPlus.getHolderView();
                        mButtonUpdate = view.findViewById(R.id.btnUpdate);
                        mTextInputName = view.findViewById(R.id.textInputNamePet);
                        mTextInputBreed = view.findViewById(R.id.textInputBreed);
                        mTextInputDescription = view.findViewById(R.id.textInputDescription);
                        mRadioButtonMalePet = view.findViewById(R.id.radioButtonMalePet);
                        mRadioButtonFemalePet = view.findViewById(R.id.radioButtonFemalePet);

                        mTextInputName.setText(petArrayList.get(holder.getBindingAdapterPosition()).getName());
                        mTextInputBreed.setText(petArrayList.get(holder.getBindingAdapterPosition()).getBreed());
                        mTextInputDescription.setText(petArrayList.get(holder.getBindingAdapterPosition()).getDescription());
                        if(petArrayList.get(holder.getBindingAdapterPosition()).getGender()=='M'){
                            mRadioButtonMalePet.setChecked(true);
                        }
                        else{
                            mRadioButtonFemalePet.setChecked(true);
                        }

                        mDialogPlus.show();

                        mButtonUpdate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                clickUpdate(holder);
                            }
                        });

                        return true;

                    case R.id.cardview_delete:

                        mPetProvider.deletePet(petArrayList.get(holder.getBindingAdapterPosition()).getIdPet()).enqueue(new Callback<ResponseApi>() {
                            @Override
                            public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {
                                if (response.isSuccessful()) {
                                    petArrayList.remove(holder.getBindingAdapterPosition());
                                    notifyItemRemoved(holder.getBindingAdapterPosition());
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseApi> call, Throwable t) {
                                Toast.makeText(context, "Eliminación fallida", Toast.LENGTH_SHORT).show();
                            }
                        });
                    return true;

                    default:
                        return false;
                }
            }
        });
        popupMenuCardView.show();
    }

    private void clickUpdate(PetViewHolder holder) {
        int idPet = petArrayList.get(holder.getBindingAdapterPosition()).getIdPet();
        String name = mTextInputName.getText().toString();
        String breed = mTextInputBreed.getText().toString();
        String description = mTextInputDescription.getText().toString();
        char gender = ((mRadioButtonMalePet.isChecked() ? 'M' : 'F'));
        int position = holder.getBindingAdapterPosition();


        if(!name.isEmpty() && !breed.isEmpty()){
                //DATOS INGRESADOS CORRECTAMENTE
            updatePet(new Pet(idPet,name,breed,gender,description), position);

        }
        else{
            Toast.makeText(context, "Ingrese todos los campos", Toast.LENGTH_SHORT).show();

        }

    }

    private void updatePet(Pet pet, int position) {
        mPetProvider.updatePet(pet).enqueue(new Callback<ResponseApi>() {
            @Override
            public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {


                if(response.isSuccessful()){
                    petArrayList.get(position).setName(pet.getName());
                    petArrayList.get(position).setBreed(pet.getBreed());
                    petArrayList.get(position).setDescription(pet.getDescription());
                    petArrayList.get(position).setGender(pet.getGender());
                    notifyItemChanged(position);
                    mDialogPlus.dismiss();

                }
                else{
                    Toast.makeText(context, "No se pudo procesar la actualización", Toast.LENGTH_SHORT).show();
                    mDialogPlus.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseApi> call, Throwable t) {
                Toast.makeText(context, "Actualización fallida", Toast.LENGTH_SHORT).show();
                mDialogPlus.dismiss();
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
        AppCompatImageButton mButtonMenu;


        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewNamePet = itemView.findViewById(R.id.namePet);
            mTextViewBreedPet = itemView.findViewById(R.id.breedPet);
            mImageViewPet = itemView.findViewById(R.id.imageView_pet);
            mButtonMenu = itemView.findViewById(R.id.imageButton_menu);

        }


    }


}
