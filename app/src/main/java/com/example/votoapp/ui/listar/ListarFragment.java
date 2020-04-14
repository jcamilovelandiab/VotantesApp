package com.example.votoapp.ui.listar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.example.votoapp.SQL.SQLite;
import com.example.votoapp.R;
import com.example.votoapp.ui.editar.EditarFragment;

import java.io.File;
import java.util.ArrayList;

public class ListarFragment extends Fragment {

    private ListarViewModel listarViewModel;
    ArrayList<String> reg_activos, reg_finados;
    ArrayList<String> imagenes_activos, imagenes_finados;
    ListView lv_votantes_activos, lv_votantes_finados;
    LinearLayout layout_activos, layout_finados;
    SQLite sqlite;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        listarViewModel =
                ViewModelProviders.of(this).get(ListarViewModel.class);
        View root = inflater.inflate(R.layout.fragment_listar, container, false);

        lv_votantes_activos = root.findViewById(R.id.listar_lv_lista_activos);
        lv_votantes_finados = root.findViewById(R.id.listar_lv_lista_finados);
        layout_activos = root.findViewById(R.id.listar_layout_activos);
        layout_finados = root.findViewById(R.id.listar_layout_finados);

        //base de datos
        sqlite = new SQLite(getContext());
        sqlite.abrir();

        configureListVotantesActivos();
        configureListVotantesFinados();

        return root;
    }

    private void configureListVotantesActivos(){
        Cursor cursorActivos = sqlite.getRegistroVotantesActivos();
        reg_activos = sqlite.getStringVotantesFromCursor(cursorActivos);
        imagenes_activos = sqlite.getImagenes(cursorActivos);
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,reg_activos);
        lv_votantes_activos.setAdapter(adaptador);
        lv_votantes_activos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_votante,null);
                ((TextView)dialogView.findViewById(R.id.dialog_votante_tv_datos)).setText(reg_activos.get(i));
                ImageView iVImagen=dialogView.findViewById(R.id.dialog_votante_iv_picture);
                cargarImagen(imagenes_activos.get(i),iVImagen);
                AlertDialog.Builder dialogo=new AlertDialog.Builder(getContext());
                dialogo.setTitle("Votante Activo");
                dialogo.setView(dialogView);
                dialogo.setPositiveButton("Aceptar",null);
                dialogo.show();
            }
        });
        resizeLayout(layout_activos, reg_activos.size());
    }

    private void configureListVotantesFinados(){
        Cursor cursorFinados = sqlite.getRegistroVotantesFinados();
        reg_finados = sqlite.getStringVotantesFromCursor(cursorFinados);
        imagenes_finados = sqlite.getImagenes(cursorFinados);
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,reg_finados);
        lv_votantes_finados.setAdapter(adaptador);
        lv_votantes_finados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_votante,null);
                ((TextView)dialogView.findViewById(R.id.dialog_votante_tv_datos)).setText(reg_finados.get(i));
                ImageView iVImagen=dialogView.findViewById(R.id.dialog_votante_iv_picture);
                cargarImagen(imagenes_finados.get(i),iVImagen);
                AlertDialog.Builder dialogo=new AlertDialog.Builder(getContext());
                dialogo.setTitle("Votante Finado");
                dialogo.setView(dialogView);
                dialogo.setPositiveButton("Aceptar",null);
                dialogo.show();
            }
        });
        resizeLayout(layout_finados, reg_finados.size());
    }

    private void resizeLayout(final LinearLayout layout, int elements){
        // Gets the layout params that will allow you to resize the layout
        ViewGroup.LayoutParams params = layout.getLayoutParams();
        // Changes the height and width to the specified *pixels*
        int heightDP = 385;
        int heightPixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightDP, getResources().getDisplayMetrics());
        params.height = heightPixels*elements;
        layout.setLayoutParams(params);
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