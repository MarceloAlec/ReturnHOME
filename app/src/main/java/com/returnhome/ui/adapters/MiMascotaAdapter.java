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
import com.returnhome.controllers.MascotaController;
import com.returnhome.models.Mascota;
import com.returnhome.models.RHRespuesta;
import com.returnhome.ui.activities.mascota.MapaNotificacionMascotaDesaparecidaActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MiMascotaAdapter extends RecyclerView.Adapter<MiMascotaAdapter.MiMascotaViewHolder> {


    //LISTA MASCOTAS SE ENVIAN AL RECYCLERVIEW
    private Button mButtonAgregarActualizarMascota;
    private TextInputEditText mTextInputNombre;
    private TextInputEditText mTextInputRaza;
    private TextInputEditText mTextInputDescripcion;
    private RadioButton mRadioButtonGeneroMacho;
    private RadioButton mRadioButtonGeneroFemenino;
    private ArrayList<Mascota> mascotaArrayList;
    private BottomSheetDialog mBottomSheetDialog;
    LayoutInflater inflater;
    Context context;

    public MiMascotaAdapter(Context context, ArrayList<Mascota> mascotaArrayList){
        this.context=context;
        this.inflater = LayoutInflater.from(context);
        this.mascotaArrayList = mascotaArrayList;
    }

    @NonNull
    @Override
    //VA A INFLAR LA VISTA
    //RELLENA CADA ELEMENTO DEL RECYCLER VIEW CON UNA VISTA(texto, imagen)
    public MiMascotaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //SE CREA LA VISTA DE CADA ITEM
        //EL ATTACHTOROOT SERA FALSE PARA NO CARGAR  LOS ITEMS EN EL RECYCLER MUY RAPIDO
        //CON EL INFLATER SE PROCEDE A DIBUJAR LA VISTA
        //COMO PARAMETRO TAMBIEN SE INDICA EL DISEÑO QUE TENDRA CADA ITEM DE LA LISTA
        View view = inflater.inflate(R.layout.cardview_mis_mascotas, parent, false);
        //SE ENVIA LA VISTA AL CONSTRUCTOR DE LA CLASE PETSVIEWHOLDER
        MiMascotaViewHolder mascotasViewHolder = new MiMascotaViewHolder(view);
        return mascotasViewHolder;
    }

    //CADA ITEM DEL RECYCLERVIEW HACE USO DE LA VISTA CREADA EN EL VIEWHOLDER
    //DE MANERA QUE TODOS LOS ITEMS COMPARTAN UN MISMO DISEÑO

    //POSITION: POSICION DE CADA ITEM DE LA LISTA

    //VA A BINDEAR A CADA VISTA DEL VIEWHOLDER CON LOS DATOS
    //PARA POBLAR CADA POSICION
    @Override
    public void onBindViewHolder(@NonNull MiMascotaViewHolder holder, int position) {
        //A CADA POSICION DEL RECYCLER TENDRA LA MISMA VISTA CREADA EN ONCREATEVIEWHOLDER
        holder.mTextViewNombreMascota.setText(mascotaArrayList.get(position).getNombre());
        holder.mTextViewRaza.setText(mascotaArrayList.get(position).getRaza());

        holder.mButtonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu(v, holder);
            }
        });




    }

    private void showMenu(View v, MiMascotaViewHolder holder ) {
        PopupMenu popupMenuCardView = new PopupMenu(v.getContext(), v);
        popupMenuCardView.inflate(R.menu.menu_cardview);

        popupMenuCardView.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.cardview_editar:

                        mBottomSheetDialog = new BottomSheetDialog(context);
                        mBottomSheetDialog.setContentView(R.layout.popup_agregar_actualizar);
                        mBottomSheetDialog.setCanceledOnTouchOutside(true);

                        inicializarComponentes();

                        mButtonAgregarActualizarMascota.setText("Actualizar");

                        mTextInputNombre.setText(mascotaArrayList.get(holder.getBindingAdapterPosition()).getNombre());
                        mTextInputRaza.setText(mascotaArrayList.get(holder.getBindingAdapterPosition()).getRaza());
                        mTextInputDescripcion.setText(mascotaArrayList.get(holder.getBindingAdapterPosition()).getDescripcion());
                        if(mascotaArrayList.get(holder.getBindingAdapterPosition()).getGenero()=='M'){
                            mRadioButtonGeneroMacho.setChecked(true);
                        }
                        else{
                            mRadioButtonGeneroFemenino.setChecked(true);
                        }

                        mBottomSheetDialog.show();

                        mButtonAgregarActualizarMascota.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                clicActualizar(holder);
                            }
                        });

                        return true;

                    case R.id.cardview_eliminar:

                        AlertDialog builder = new AlertDialog.Builder(context).create();
                        builder.setTitle("ReturnHOME");
                        builder.setMessage("Esta seguro que desea eliminar su mascota?");
                        builder.setButton(AlertDialog.BUTTON_POSITIVE, "SI", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                eliminarMascota(holder);
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

                    case R.id.cardview_mascota_desaparecida:
                        int idMascota = mascotaArrayList.get(holder.getBindingAdapterPosition()).getIdMascota();
                        String nombreMascota = mascotaArrayList.get(holder.getBindingAdapterPosition()).getNombre();

                        Intent intent = new Intent(context, MapaNotificacionMascotaDesaparecidaActivity.class);
                        intent.putExtra("idMascota",idMascota);
                        intent.putExtra("nombreMascota",nombreMascota);
                        context.startActivity(intent);

                        return true;

                    case R.id.cardview_mascota_encontrada:
                        int idMascota1= mascotaArrayList.get(holder.getBindingAdapterPosition()).getIdMascota();
                        Mascota mascota1 = new Mascota(idMascota1,false);
                        actualizarEstadoMascotaDesaparecida(mascota1);

                        return true;

                    default:
                        return false;
                }
            }
        });
        popupMenuCardView.show();
    }

    private void actualizarEstadoMascotaDesaparecida(Mascota mascota){

        MascotaController.actualizarMascotaDesaparecida(mascota).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if(response.isSuccessful()){
                    Toast.makeText(context, "Mascota reportada como encontrada", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(context, "La mascota no se encuentra desaparecida", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Se ha producido un error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void eliminarMascota(MiMascotaViewHolder holder){
        MascotaController.eliminar(mascotaArrayList.get(holder.getBindingAdapterPosition()).getIdMascota()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    mascotaArrayList.remove(holder.getBindingAdapterPosition());
                    notifyItemRemoved(holder.getBindingAdapterPosition());
                }
                else{
                    Toast.makeText(context, "No se pudo eliminar la mascota", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Eliminación fallida", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clicActualizar(MiMascotaViewHolder holder) {
        int idMascota = mascotaArrayList.get(holder.getBindingAdapterPosition()).getIdMascota();
        String nombre = mTextInputNombre.getText().toString();
        String raza = mTextInputRaza.getText().toString();
        String descripcion = mTextInputDescripcion.getText().toString();
        char genero = ((mRadioButtonGeneroMacho.isChecked() ? 'M' : 'F'));
        int posicion = holder.getBindingAdapterPosition();

        if(!nombre.isEmpty() && !raza.isEmpty()){
                //DATOS INGRESADOS CORRECTAMENTE
            actualizarMascota(new Mascota(idMascota,nombre,raza,genero,descripcion), posicion);
        }
        else{
            Toast.makeText(context, "Ingrese todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarMascota(Mascota mascota, int posicion) {
        MascotaController.actualizar(mascota).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if(response.isSuccessful()){
                    mascotaArrayList.get(posicion).setNombre(mascota.getNombre());
                    mascotaArrayList.get(posicion).setRaza(mascota.getRaza());
                    mascotaArrayList.get(posicion).setDescripcion(mascota.getDescripcion());
                    mascotaArrayList.get(posicion).setGenero(mascota.getGenero());
                    notifyItemChanged(posicion);
                    mBottomSheetDialog.dismiss();
                    Toast.makeText(context, "Actualización exitosa", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(context, "No hubo ninguna actualización", Toast.LENGTH_SHORT).show();
                    mBottomSheetDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Actualización fallida", Toast.LENGTH_SHORT).show();
                mBottomSheetDialog.dismiss();
            }
        });


    }

    //OBTIENE LOS ELEMENTOS EN EL RECYCLER VIEW
    @Override
    public int getItemCount() {
        return mascotaArrayList.size();
    }

    private void inicializarComponentes(){
        mButtonAgregarActualizarMascota = mBottomSheetDialog.findViewById(R.id.btnAgregarActualizarMascota);
        mTextInputNombre = mBottomSheetDialog.findViewById(R.id.textInputNombreMascota);
        mTextInputRaza = mBottomSheetDialog.findViewById(R.id.textInputRaza);
        mTextInputDescripcion = mBottomSheetDialog.findViewById(R.id.textInputDescripcion);
        mRadioButtonGeneroMacho = mBottomSheetDialog.findViewById(R.id.radioButtonGeneroMacho);
        mRadioButtonGeneroFemenino = mBottomSheetDialog.findViewById(R.id.radioButtonGeneroHembra);
    }


    //MANEJA LOS ELEMENTOS QUE CONTIENE LA VISTA DE CADA ITEM DENTRO DEL RECYCLER
    public class MiMascotaViewHolder extends RecyclerView.ViewHolder {

        TextView mTextViewNombreMascota;
        TextView mTextViewRaza;
        ImageView mImageViewMascota;
        AppCompatImageButton mButtonMenu;

        public MiMascotaViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewNombreMascota = itemView.findViewById(R.id.textViewNombreMascota);
            mTextViewRaza = itemView.findViewById(R.id.textViewRaza);
            mImageViewMascota = itemView.findViewById(R.id.imageView_pet);
            mButtonMenu = itemView.findViewById(R.id.btnMiMascota);
        }
    }
}
