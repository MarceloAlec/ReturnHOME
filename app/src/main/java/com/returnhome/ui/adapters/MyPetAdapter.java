package com.returnhome.ui.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.returnhome.R;
import com.returnhome.models.Pet;
import com.returnhome.providers.PetProvider;
import com.returnhome.models.RHResponse;
import com.returnhome.ui.activities.pet.MapPetReportedMissingActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPetAdapter extends RecyclerView.Adapter<MyPetAdapter.PetViewHolder> {


    //LISTA MASCOTAS SE ENVIAN AL RECYCLERVIEW
    private Button mButtonUpdate;
    private TextInputEditText mTextInputName;
    private TextInputEditText mTextInputBreed;
    private TextInputEditText mTextInputDescription;
    private RadioButton mRadioButtonMalePet;
    private RadioButton mRadioButtonFemalePet;
    private ArrayList<Pet> petArrayList;
    private BottomSheetDialog mBottomSheetDialog;
    LayoutInflater inflater;
    Context context;

    public MyPetAdapter(Context context, ArrayList<Pet> petArrayList){
        this.context=context;
        this.inflater = LayoutInflater.from(context);
        this.petArrayList = petArrayList;
    }

    @NonNull
    @Override
    //VA A INFLAR LA VISTA
    //RELLENA CADA ELEMENTO DEL RECYCLER VIEW CON UNA VISTA(texto, imagen)
    public MyPetAdapter.PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //SE CREA LA VISTA DE CADA ITEM
        //EL ATTACHTOROOT SERA FALSE PARA NO CARGAR  LOS ITEMS EN EL RECYCLER MUY RAPIDO
        //CON EL INFLATER SE PROCEDE A DIBUJAR LA VISTA
        //COMO PARAMETRO TAMBIEN SE INDICA EL DISEÑO QUE TENDRA CADA ITEM DE LA LISTA
        View view = inflater.inflate(R.layout.cardview_my_pets, parent, false);
        //SE ENVIA LA VISTA AL CONSTRUCTOR DE LA CLASE PETSVIEWHOLDER
        MyPetAdapter.PetViewHolder petsViewHolder = new MyPetAdapter.PetViewHolder(view);
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

                        mBottomSheetDialog = new BottomSheetDialog(context);
                        mBottomSheetDialog.setContentView(R.layout.popup_update);
                        mBottomSheetDialog.setCanceledOnTouchOutside(true);

                        initializeComponents();

                        mButtonUpdate.setText(R.string.btn_updatePet);

                        mTextInputName.setText(petArrayList.get(holder.getBindingAdapterPosition()).getName());
                        mTextInputBreed.setText(petArrayList.get(holder.getBindingAdapterPosition()).getBreed());
                        mTextInputDescription.setText(petArrayList.get(holder.getBindingAdapterPosition()).getDescription());
                        if(petArrayList.get(holder.getBindingAdapterPosition()).getGender()=='M'){
                            mRadioButtonMalePet.setChecked(true);
                        }
                        else{
                            mRadioButtonFemalePet.setChecked(true);
                        }

                        mBottomSheetDialog.show();

                        mButtonUpdate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                clickUpdate(holder);
                            }
                        });

                        return true;

                    case R.id.cardview_delete:

                        AlertDialog builder = new AlertDialog.Builder(context).create();
                        builder.setTitle("ReturnHOME");
                        builder.setMessage("Esta seguro que desea eliminar su mascota?");
                        builder.setButton(AlertDialog.BUTTON_POSITIVE, "SI", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deletePet(holder);
                            }
                        });
                        builder.setButton(AlertDialog.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                builder.cancel();
                            }
                        });

                        builder.show();

                    return true;

                    case R.id.cardview_missing_pet:
                        int idPet = petArrayList.get(holder.getBindingAdapterPosition()).getId();
                        String petName = petArrayList.get(holder.getBindingAdapterPosition()).getName();

                        Intent intent = new Intent(context, MapPetReportedMissingActivity.class);
                        intent.putExtra("idPet",idPet);
                        intent.putExtra("pet_name",petName);
                        context.startActivity(intent);


                        return true;

                    case R.id.cardview_found_pet:
                        int idPet1= petArrayList.get(holder.getBindingAdapterPosition()).getId();
                        Pet pet1 = new Pet(idPet1,false);
                        updateStatusMissingPet(pet1);

                        return true;

                    default:
                        return false;
                }
            }
        });
        popupMenuCardView.show();
    }

    private void updateStatusMissingPet(Pet pet){

        PetProvider.updateStatusMissingPet(pet).enqueue(new Callback<RHResponse>() {
            @Override
            public void onResponse(Call<RHResponse> call, Response<RHResponse> response) {

                if(response.isSuccessful()){
                    Toast.makeText(context, "Mascota reportada como encontrada", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(context, "La mascota ya fue reportada como encontrada", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RHResponse> call, Throwable t) {
                Toast.makeText(context, "El reporte ha fallado", Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void deletePet(PetViewHolder holder){
        PetProvider.deletePet(petArrayList.get(holder.getBindingAdapterPosition()).getId()).enqueue(new Callback<RHResponse>() {
            @Override
            public void onResponse(Call<RHResponse> call, Response<RHResponse> response) {
                if (response.isSuccessful()) {
                    petArrayList.remove(holder.getBindingAdapterPosition());
                    notifyItemRemoved(holder.getBindingAdapterPosition());
                }
            }

            @Override
            public void onFailure(Call<RHResponse> call, Throwable t) {
                Toast.makeText(context, "Eliminación fallida", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clickUpdate(PetViewHolder holder) {
        int idPet = petArrayList.get(holder.getBindingAdapterPosition()).getId();
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
        PetProvider.updatePet(pet).enqueue(new Callback<RHResponse>() {
            @Override
            public void onResponse(Call<RHResponse> call, Response<RHResponse> response) {


                if(response.isSuccessful()){
                    petArrayList.get(position).setName(pet.getName());
                    petArrayList.get(position).setBreed(pet.getBreed());
                    petArrayList.get(position).setDescription(pet.getDescription());
                    petArrayList.get(position).setGender(pet.getGender());
                    notifyItemChanged(position);
                    mBottomSheetDialog.dismiss();
                    Toast.makeText(context, "Actualización exitosa", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(context, "No hubo ninguna actualización", Toast.LENGTH_SHORT).show();
                    mBottomSheetDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<RHResponse> call, Throwable t) {
                Toast.makeText(context, "Actualización fallida", Toast.LENGTH_SHORT).show();
                mBottomSheetDialog.dismiss();
            }
        });


    }

    //OBTIENE LOS ELEMENTOS EN EL RECYCLER VIEW
    @Override
    public int getItemCount() {
        return petArrayList.size();
    }

    private void initializeComponents(){
        mButtonUpdate = mBottomSheetDialog.findViewById(R.id.btnUpdateAddPet);
        mTextInputName = mBottomSheetDialog.findViewById(R.id.textInputNamePet);
        mTextInputBreed = mBottomSheetDialog.findViewById(R.id.textInputBreed);
        mTextInputDescription = mBottomSheetDialog.findViewById(R.id.textInputDescription);
        mRadioButtonMalePet = mBottomSheetDialog.findViewById(R.id.radioButtonMalePet);
        mRadioButtonFemalePet = mBottomSheetDialog.findViewById(R.id.radioButtonFemalePet);
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
