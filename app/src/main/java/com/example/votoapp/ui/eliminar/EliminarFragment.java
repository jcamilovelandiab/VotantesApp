package com.example.votoapp.ui.eliminar;

import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.votoapp.SQL.SQLite;
import com.example.votoapp.R;
import com.example.votoapp.model.entities.Votante;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class EliminarFragment extends Fragment {

    Button btn_eliminar;
    EditText et_idVotante;
    private SQLite sqlite;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_eliminar, container, false);

        btn_eliminar = (Button) root.findViewById(R.id.eliminar_btn_eliminar);
        et_idVotante = (EditText) root.findViewById(R.id.eliminar_et_idVotante);

        configureBtnEliminar();

        //base de datos
        sqlite = new SQLite(getContext());
        sqlite.abrir();

        return root;
    }

    private void configureBtnEliminar(){
        btn_eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sqlite=new SQLite(getContext());
                sqlite.abrir();

                if(et_idVotante.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), "Ingrese el CURP del votante", Toast.LENGTH_SHORT).show();
                } else {
                    if (sqlite.getVotantexID(Integer.parseInt(et_idVotante.getText().toString())).getCount() == 1) {

                        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getContext());

                        Cursor cursor = sqlite.getVotantexID(Integer.parseInt(et_idVotante.getText().toString()));
                        ArrayList<Votante> votantes = sqlite.getVotantesFromCursor(cursor);
                        assert(votantes.size()==1);

                        View dialogView=LayoutInflater.from(getContext()).inflate(R.layout.dialog_votante,null);
                        ((TextView)dialogView.findViewById(R.id.dialog_votante_tv_datos)).setText("¿ Desea eliminar el registro?.\nTenga en cuenta que ésta acción no se podrá deshacer!\n\n" +
                                votantes.get(0).toString());

                        ImageView image=dialogView.findViewById(R.id.dialog_votante_iv_picture);
                        cargarImagen(votantes.get(0).getPath_imagen(),image);

                        dialogo1.setTitle("Importante");
                        dialogo1.setView(dialogView);
                        dialogo1.setCancelable(false);
                        dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogo1, int id) {
                                aceptar();
                            }
                        });
                        dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogo1, int id) {
                                Toast.makeText(getContext(), "Cancelado", Toast.LENGTH_SHORT).show();
                            }
                        });
                        dialogo1.show();
                    }else{
                        Toast.makeText(getContext(), "Error: No existe un votante con esa id" +
                                "", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void aceptar(){
        sqlite.UpdateFinadoxID(et_idVotante.getText().toString());
        Toast.makeText(getContext(), "Registro Eliminado", Toast.LENGTH_SHORT).show();
    }

    //cargar imagen
    public void cargarImagen(String imagen, ImageView iv){
        try{
            File filePhoto=new File(imagen);
            Uri uriPhoto = FileProvider.getUriForFile(getContext(),"com.example.votoapp",filePhoto);
            iv.setImageURI(uriPhoto);
        }catch (Exception ex){
            Toast.makeText(getContext(), "Ocurrio un error al cargar la imagen", Toast.LENGTH_SHORT).show();
            Log.d("Cargar Imagen","Error al cargar imagen: "+imagen+"\nMensaje: "+ex.getMessage()+"\nCausa: "+ex.getCause());
        }
    }

}