package com.example.votoapp.ui.consultar;

import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.votoapp.R;
import com.example.votoapp.SQL.SQLite;
import com.example.votoapp.ui.crear.CrearViewModel;

import java.io.File;
import java.util.ArrayList;

public class ConsultarFragment extends Fragment {

    Button btn_consultar;
    EditText et_nombres, et_apellidos, et_curp;

    Spinner sp_estado, sp_municipio;
    String strEstado, strMunicipio;
    ArrayList<String> reg, imagenes;
    ListView lv_votantes;

    private SQLite sqlite;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_consultar, container, false);

        et_nombres = root.findViewById(R.id.consultar_et_nombres);
        et_apellidos = root.findViewById(R.id.consultar_et_apellidos);
        et_curp = root.findViewById(R.id.consultar_et_curp);

        sp_estado = root.findViewById(R.id.consultar_sp_estado);
        sp_municipio = root.findViewById(R.id.consultar_sp_municipio);

        btn_consultar = root.findViewById(R.id.consultar_btn_consultar);
        lv_votantes = root.findViewById(R.id.consultar_lv_lista_votantes);

        configureSpinnerEstado();
        configureBtnConsultar();

        //base de datos
        sqlite = new SQLite(getContext());
        sqlite.abrir();

        et_nombres.setText("");
        et_apellidos.setText("");
        et_curp.setText("");
        strEstado = ""; strMunicipio="";

        return root;
    }

    private void listeVotantes(Cursor cursor){
        reg = sqlite.getStringVotantesFromCursor(cursor);
        imagenes = sqlite.getImagenes(cursor);
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,reg);
        lv_votantes.setAdapter(adaptador);
        lv_votantes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_votante,null);
                ((TextView)dialogView.findViewById(R.id.dialog_votante_tv_datos)).setText(reg.get(i));
                ImageView iVImagen=dialogView.findViewById(R.id.dialog_votante_iv_picture);
                cargarImagen(imagenes.get(i),iVImagen);
                AlertDialog.Builder dialogo=new AlertDialog.Builder(getContext());
                dialogo.setTitle("Votante");
                dialogo.setView(dialogView);
                dialogo.setPositiveButton("Aceptar",null);
                dialogo.show();
            }
        });
    }

    private void configureBtnConsultar(){
        btn_consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombres="", apellidos="", curp="";
                nombres = et_nombres.getText().toString().toUpperCase();
                apellidos = et_apellidos.getText().toString().toUpperCase();
                curp = et_curp.getText().toString().toUpperCase();
                if(sp_municipio.getSelectedItemPosition()==0 &&
                        nombres.trim().equals("") && apellidos.trim().equals("") && curp.trim().equals("")){
                    Toast.makeText(getContext(),"Para consultar debe llenar al menos un campo",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(),"Buscando registro...",Toast.LENGTH_SHORT).show();

                    if(nombres.equals("")) nombres = "NN";
                    if(apellidos.equals("")) apellidos= "NN";
                    if(curp.equals("")) curp="NN";
                    if(strEstado.equals("")) strEstado="NN";
                    if(strMunicipio.equals("")) strMunicipio="NN";

                    Cursor cursor = sqlite.getVotantesxNombre_CURP_estado_municipio(nombres, apellidos,
                            curp.toUpperCase(),strEstado, strMunicipio);
                    listeVotantes(cursor);
                }
            }
        });
    }

    private void configureSpinnerEstado(){
        final ArrayAdapter<CharSequence> estadoAdapter = ArrayAdapter.createFromResource(
                getContext(), R.array.estados,
                android.R.layout.simple_spinner_item
        );
        sp_estado.setAdapter(estadoAdapter);
        sp_estado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int opEstado = sp_estado.getSelectedItemPosition();
                int municipioArray=-1;
                switch (opEstado){
                    case 1: municipioArray=R.array.mexico; break;
                    case 2: municipioArray = R.array.cdmx; break;
                    case 3: municipioArray = R.array.morelos; break;
                    case 4: municipioArray = R.array.guerrero; break;
                }
                if(opEstado!=0){
                    strEstado = estadoAdapter.getItem(opEstado).toString();
                    final ArrayAdapter<CharSequence> municipiosAdapter =
                            ArrayAdapter.createFromResource(getContext(), municipioArray, android.R.layout.simple_spinner_item);
                    configureSpinnerMunicipio(municipiosAdapter);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void configureSpinnerMunicipio(final ArrayAdapter<CharSequence> municipiosAdapter){
        sp_municipio.setAdapter(municipiosAdapter);
        sp_municipio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int opMunicipio = sp_municipio.getSelectedItemPosition();
                if(opMunicipio!=0){
                    strMunicipio = municipiosAdapter.getItem(opMunicipio).toString();
                    Toast.makeText(getContext(),"Estado: "+strEstado+"\nMunicipio: "+strMunicipio, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
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
