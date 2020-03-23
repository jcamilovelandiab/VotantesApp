package com.example.votoapp.ui.editar;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.votoapp.SQL.SQLite;
import com.example.votoapp.R;
import com.example.votoapp.model.entities.Generador;
import com.example.votoapp.model.entities.Sexo;
import com.example.votoapp.model.entities.Votante;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.votoapp.Constantes.MAPVIEW_BUNDLE_KEY;

public class EditarFragment extends Fragment implements OnMapReadyCallback {

    private int dia, mes, anio; //fecha de nacimiento
    private static final int REQUEST_TAKE_PHOTO=1;

    private EditText et_buscar_curp, et_buscar_idVotante;
    private EditText et_idVotante, et_no_version, et_nombres, et_apellidos, et_fecha_nacimiento;
    private EditText et_calle, et_numInt, et_numExt, et_colonia, et_codigo_postal; // Domicilio
    private EditText et_clave_electoral, et_CURP, et_anio_registro, et_anio_emision, et_vigencia;
    private Spinner sp_estado, sp_municipio, sp_seccion, sp_localidad, sp_sexo;
    private ImageView iv_foto;

    private String strNombres, strApellidos, strFecha_nacimiento;
    private String strClave_electoral, strCURP;
    private String strEstado, strMunicipio, strSeccion, strLocalidad, strSexo, strFinado;
    private boolean finado;

    private String pathImage="", currentPhotoPath="";

    private Button btn_modificar, btn_limpiar, btn_fecha_nacimiento, btn_buscar, btn_generar_curp;
    private LinearLayout layout_contenedor;
    private TextView tv_finado;

    Uri photoURI;
    SQLite sqLite;
    private MapView mv_map;
    private GoogleMap myMap;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_editar, container, false);

        layout_contenedor = root.findViewById(R.id.editar_layout_contenedor);
        et_buscar_idVotante = root.findViewById(R.id.editar_et_buscar_idVotante);

        // Enlazamos interfaz con el modelo
        sp_estado = root.findViewById(R.id.editar_sp_estado);
        sp_municipio = root.findViewById(R.id.editar_sp_municipio);
        sp_sexo = root.findViewById(R.id.editar_sp_sexo);
        sp_seccion = root.findViewById(R.id.editar_sp_seccion);
        sp_localidad = root.findViewById(R.id.editar_sp_localidad);

        et_idVotante = root.findViewById(R.id.editar_et_idVotante);
        tv_finado = root.findViewById(R.id.editar_tv_finado);
        et_no_version = root.findViewById(R.id.editar_et_no_version);
        et_nombres = root.findViewById(R.id.editar_et_nombres);
        et_apellidos = root.findViewById(R.id.editar_et_apellidos);
        et_fecha_nacimiento = root.findViewById(R.id.editar_et_fecha_nacimiento);

        et_calle = root.findViewById(R.id.editar_et_calle);
        et_numExt = root.findViewById(R.id.editar_et_numExt);
        et_numInt = root.findViewById(R.id.editar_et_numInt);
        et_colonia = root.findViewById(R.id.editar_et_colonia);
        et_codigo_postal = root.findViewById(R.id.editar_et_codigo_postal);

        et_anio_registro = root.findViewById(R.id.editar_et_anio_registro);
        et_clave_electoral = root.findViewById(R.id.editar_et_clave_electoral);
        et_CURP = root.findViewById(R.id.editar_et_curp);
        et_anio_emision = root.findViewById(R.id.editar_et_anio_emision);
        et_anio_emision.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try{
                    et_vigencia.setText(String.valueOf(Integer.parseInt(et_anio_emision.getText().toString())+10));
                }catch(Exception e){
                }
            }
        });


        et_vigencia = root.findViewById(R.id.editar_et_vigencia);

        btn_fecha_nacimiento = root.findViewById(R.id.editar_btn_fecha_nacimiento);
        btn_limpiar = root.findViewById(R.id.editar_btn_limpiar);
        btn_modificar = root.findViewById(R.id.editar_btn_modificar);
        btn_generar_curp = root.findViewById(R.id.editar_btn_generar_curp);
        btn_buscar = root.findViewById(R.id.editar_btn_buscar);

        iv_foto = root.findViewById(R.id.editar_iv_foto);
        mv_map = root.findViewById(R.id.editar_mv_map);
        initGoogleMap(savedInstanceState);

        // Conexión con base de datos
        sqLite = new SQLite(getContext());
        sqLite.abrir();

        configureSpinnerSexo();
        configureFechaNacimiento();
        configureSpinnerEstado();
        configureBtnGenerarCurp();

        configureBtnModificar();
        configureBtnBuscar();
        configureBtnLimpiar();
        configureTomarFoto();

        layout_contenedor.setVisibility(LinearLayout.GONE);

        return root;
    }

    private void configureBtnGenerarCurp(){
        btn_generar_curp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!et_nombres.getText().toString().trim().equals("") &&
                        !et_apellidos.getText().toString().trim().equals("") &&
                        !et_fecha_nacimiento.getText().toString().trim().equals("") &&
                        !strSexo.equals("")){
                    Votante votante = new Votante(et_nombres.getText().toString(),
                            et_apellidos.getText().toString(), et_fecha_nacimiento.getText().toString(), Sexo.valueOf(strSexo));
                    strCURP = Generador.generarCURP(votante);
                    System.out.println(strCURP);
                    et_CURP.setText(strCURP);
                    Toast.makeText(getContext(), "CURP generado éxitosamente!\nSi se cambió la información personal el CURP generado es uno nuevo.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(), "Para generar el CURP debe ingresar primero sus nombres" +
                            ", apellidos, fecha de nacimiento y sexo.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void cargarInformacion(){
        Bundle bundle = this.getActivity().getIntent().getExtras();
        String strCurp = null;
        strCurp = bundle.getString("CURP");
        if(strCurp!=null){
            // consultar la base de datos
        }
    }

    private void configureBtnBuscar(){
        btn_buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String buscarCURP = et_buscar_curp.getText().toString();
                String idVotante = et_buscar_idVotante.getText().toString();
                if(!idVotante.trim().equals("")){
                    int id = Integer.parseInt(idVotante);
                    if (sqLite.getVotantexID(id).getCount() == 1) {
                        Cursor cursor = sqLite.getVotantexID(id);
                        ArrayList<Votante> votantes = sqLite.getVotantesFromCursor(cursor);
                        if(votantes.size()==1){

                            Votante votante = votantes.get(0);
                            et_idVotante.setText(String.valueOf(votante.getId()));
                            et_apellidos.setText(votante.getApellidos());
                            et_nombres.setText(votante.getNombres());
                            et_fecha_nacimiento.setText(votante.getFecha_nacimiento());

                            String[] domicilio = votante.getDomicilio().split("\\r?\\n");
                            assert(domicilio.length==5);
                            et_calle.setText(domicilio[0]);
                            et_numInt.setText(domicilio[1]);
                            et_numExt.setText(domicilio[2]);
                            et_colonia.setText(domicilio[3]);
                            et_codigo_postal.setText(domicilio[4]);

                            et_clave_electoral.setText(votante.getClave_electoral());
                            et_CURP.setText(votante.getCURP());
                            et_anio_emision.setText(String.valueOf(votante.getAnio_emision()));
                            et_vigencia.setText(String.valueOf(votante.getVigencia()));
                            et_anio_registro.setText(String.valueOf(votante.getAnio_registro()));
                            et_no_version.setText(String.valueOf(votante.getNo_version()));

                            layout_contenedor.setVisibility(LinearLayout.VISIBLE);
                            pathImage = votante.getPath_imagen();
                            cargarImagen();

                            int posSexo= buscarPosicion(R.array.sexo,votante.getSexo().toString());
                            if(posSexo!=-1) {
                                sp_sexo.setSelection(posSexo);
                            }

                            int posEstado = buscarPosicion(R.array.estados, votante.getEstado());
                            if(posEstado!=-1){
                                strMunicipio = votante.getMunicipio();
                                strSeccion = votante.getSeccion();
                                strLocalidad = votante.getLocalidad();
                                sp_estado.setSelection(posEstado);
                            }

                            finado = votante.isFinado();
                            tv_finado.setText((finado)?"Finado":"Activo");
                            if(finado){
                                disableEnableControls(false, layout_contenedor);
                            }else{
                                disableEnableControls(true, layout_contenedor);
                                et_idVotante.setEnabled(false);
                                et_no_version.setEnabled(false);
                            }

                        }else{
                            Toast.makeText(getContext(), "El id del votante ingresado no existe", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getContext(), "El id del votante ingresado no existe", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getContext(), "Debe ingresar el ID del votante", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void disableEnableControls(boolean enable, ViewGroup vg){
        for (int i = 0; i < vg.getChildCount(); i++){
            View child = vg.getChildAt(i);
            child.setEnabled(enable);
            if (child instanceof ViewGroup){
                disableEnableControls(enable, (ViewGroup)child);
            }
        }
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
                int municipioArray=-1, seccionArray=-1, localidadArray=-1;
                switch (opEstado){
                    case 1:
                        municipioArray=R.array.mexico;
                        seccionArray=R.array.seccion_mexico;
                        localidadArray=R.array.localidad_mexico;
                        break;
                    case 2:
                        municipioArray = R.array.cdmx;
                        seccionArray=R.array.seccion_cdmx;
                        localidadArray=R.array.localidad_cdmx;
                        break;
                    case 3:
                        municipioArray = R.array.morelos;
                        seccionArray=R.array.seccion_morelos;
                        localidadArray=R.array.localidad_morelos;
                        break;
                    case 4:
                        municipioArray = R.array.guerrero;
                        seccionArray=R.array.seccion_guerrero;
                        localidadArray=R.array.localidad_guerrero;
                        break;
                }
                if(opEstado!=0){
                    strEstado = estadoAdapter.getItem(opEstado).toString();

                    final ArrayAdapter<CharSequence> municipioAdapter =
                            ArrayAdapter.createFromResource(getContext(), municipioArray, android.R.layout.simple_spinner_item);
                    final ArrayAdapter<CharSequence> seccionAdapter =
                            ArrayAdapter.createFromResource(getContext(), seccionArray, android.R.layout.simple_spinner_item);
                    final ArrayAdapter<CharSequence> localidadAdapter =
                            ArrayAdapter.createFromResource(getContext(), localidadArray, android.R.layout.simple_spinner_item);
                    configureSpinnerMunicipio(municipioAdapter, municipioArray);
                    configureSpinnerSeccion(seccionAdapter, seccionArray);
                    configureSpinnerLocalidad(localidadAdapter, localidadArray);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void configureSpinnerMunicipio(final ArrayAdapter<CharSequence> municipiosAdapter, int municipioArray){
        sp_municipio.setAdapter(municipiosAdapter);
        if(strMunicipio!=null){
            int posMunicipio = buscarPosicion(municipioArray,strMunicipio);
            if(posMunicipio!=-1){
                sp_municipio.setSelection(posMunicipio);
                String direccion = strMunicipio.split(" ")[1]+", "+strEstado.split(" ")[2]+", Mexico";
                busqueDireccion(direccion);
            }
        }
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

    private void configureSpinnerSeccion(final ArrayAdapter<CharSequence> seccionAdapter, int seccionArray){
        sp_seccion.setAdapter(seccionAdapter);
        if(strSeccion!=null){
            int posSeccion = buscarPosicion(seccionArray,strSeccion);
            if(posSeccion!=-1){
                sp_seccion.setSelection(posSeccion);
            }
        }
        sp_seccion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int opSeccion = sp_seccion.getSelectedItemPosition();
                if(opSeccion!=0){
                    strSeccion = seccionAdapter.getItem(opSeccion).toString();
                    Toast.makeText(getContext(),"Sección: "+strSeccion, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void configureSpinnerLocalidad(final ArrayAdapter<CharSequence> localidadAdapter, int localidadArray){
        sp_localidad.setAdapter(localidadAdapter);
        if(strLocalidad!=null){
            int posLocalidad = buscarPosicion(localidadArray,strLocalidad);
            if(posLocalidad!=-1){
                sp_localidad.setSelection(posLocalidad);
            }
        }
        sp_localidad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int opLocalidad = sp_localidad.getSelectedItemPosition();
                if(opLocalidad!=0){
                    strLocalidad = localidadAdapter.getItem(opLocalidad).toString();
                    Toast.makeText(getContext(),"Localidad: "+strLocalidad, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void configureSpinnerSexo() {
        final ArrayAdapter<CharSequence> sexoAdapter = ArrayAdapter.createFromResource(
                getContext(), R.array.sexo, android.R.layout.simple_spinner_item);
        sp_sexo.setAdapter(sexoAdapter);
        sp_sexo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int op = sp_sexo.getSelectedItemPosition();
                switch (op){
                    case 1: strSexo = "MASCULINO"; break;
                    case 2: strSexo = "FEMENINO"; break;
                }
            }
            //Fin spinner sexo
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void configureFechaNacimiento(){
        // Configurando la fecha - datepicker
        btn_fecha_nacimiento.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                final Calendar c= Calendar.getInstance();
                dia=c.get(Calendar.DAY_OF_MONTH);
                mes=c.get(Calendar.MONTH);
                anio = c.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear , int dayOfMonth) {
                        //     sFecha=Integer.toString(2019-year);
                        et_fecha_nacimiento.setText(dayOfMonth+"/"+(monthOfYear+1)+"/"+year);
                    }
                },anio,mes,dia);
                datePickerDialog.show();
            }
        });
    }

    private void configureTomarFoto(){
        //Tomar fotografia
        iv_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tomarfoto=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //Se comprueba que se encontro una actividad para genera la foto
                if(tomarfoto.resolveActivity(getActivity().getPackageManager())!=null){
                    //se crea el archivo donde se guardara la imagen
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex){
                        Toast.makeText(getContext(), "Ocurrio un error mientras se generaba el archivo", Toast.LENGTH_SHORT).show();
                    }
                    //se comprueba que la imagen fue creada correctamente
                    if(photoFile != null){
                        photoURI = FileProvider.getUriForFile(getContext(),"com.example.votoapp",photoFile);
                        tomarfoto.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(tomarfoto,REQUEST_TAKE_PHOTO);
                    }
                }
            }
        });
    }

    //Crear archivo de imagen
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void configureBtnModificar(){
        //Guardar registro
        btn_modificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( !et_apellidos.getText().toString().trim().equals("") &&
                        !et_nombres.getText().toString().trim().equals("") &&
                        !et_fecha_nacimiento.getText().toString().trim().equals("") &&
                        sp_sexo.getSelectedItemPosition()!=0 &&

                        sp_estado.getSelectedItemPosition()!=0 &&
                        sp_municipio.getSelectedItemPosition()!=0 &&
                        !et_calle.getText().toString().trim().equals("") &&
                        !et_numInt.getText().toString().trim().equals("") &&
                        !et_numExt.getText().toString().trim().equals("") &&
                        !et_colonia.getText().toString().trim().equals("") &&
                        !et_codigo_postal.getText().toString().trim().equals("") &&

                        !et_anio_registro.toString().trim().equals("") &&
                        !et_clave_electoral.toString().trim().equals("") &&
                        !et_CURP.toString().trim().equals("") &&
                        !et_anio_emision.toString().trim().equals("") &&
                        !et_vigencia.toString().trim().equals("") &&

                        !pathImage.equals("")){

                    String domicilio =
                            et_calle.getText().toString()+"\n"+
                                    et_numInt.getText().toString()+"\n"+
                                    et_numExt.getText().toString()+"\n"+
                                    et_colonia.getText().toString()+"\n"+
                                    et_codigo_postal.getText().toString();

                    //dentro de if
                    Toast.makeText(getContext(), strEstado+" "+strMunicipio+" "+
                            et_apellidos.getText().toString().toUpperCase()+" "+
                            et_nombres.getText().toString().toUpperCase()+" "+
                            strSexo+" "+
                            et_CURP.getText().toString().toUpperCase()+" "+
                            et_clave_electoral.getText().toString().toUpperCase()+" "+
                            et_anio_emision.getText().toString().toUpperCase()+" "+
                            et_vigencia.getText().toString(), Toast.LENGTH_LONG).show();

                    boolean result = sqLite.updateVotantexID(
                            Long.parseLong(et_buscar_idVotante.getText().toString())+0,
                            et_nombres.getText().toString()+"",
                            et_apellidos.getText().toString()+"",
                            et_fecha_nacimiento.getText().toString()+"",
                            strSexo+"",

                            domicilio.toUpperCase()+"",
                            strSeccion+"",
                            strLocalidad+"",
                            strEstado+"",
                            strMunicipio+"",

                            et_clave_electoral.getText().toString()+"",
                            et_CURP.getText().toString()+"",
                            Integer.parseInt(et_anio_registro.getText().toString())+0,
                            Integer.parseInt(et_no_version.getText().toString())+1,

                            Integer.parseInt(et_anio_emision.getText().toString())+0,
                            Integer.parseInt(et_vigencia.getText().toString())+0,
                            finado || false,
                            pathImage+"");
                    //if(result){
                    //    Toast.makeText(getContext(), "No se pudo modificar el registro", Toast.LENGTH_SHORT).show();
                    //}else{
                        Toast.makeText(getContext(), "REGISTRO MODIFICADO", Toast.LENGTH_SHORT).show();
                        limpiarCampos();
                    //}

                }else{
                    Toast.makeText(getContext(),
                            "Error: no puede haber campos vacios",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void configureBtnLimpiar(){
        btn_limpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limpiarCampos();
            }
        });
    }

    private void limpiarCampos(){
        et_apellidos.setText("");
        et_nombres.setText("");
        et_fecha_nacimiento.setText("");

        et_codigo_postal.setText("");
        et_numExt.setText("");
        et_numInt.setText("");
        et_calle.setText("");
        et_colonia.setText("");

        et_anio_registro.setText("");
        et_clave_electoral.setText("");
        et_CURP.setText("");
        et_anio_emision.setText("");
        et_vigencia.setText("");

        sp_sexo.setId(0);
        sp_municipio.setId(0);
        sp_estado.setId(0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK){
            iv_foto.setImageURI(photoURI);
            pathImage=currentPhotoPath;
            Toast.makeText(getContext(), "Foto guardada en "+ pathImage, Toast.LENGTH_SHORT).show();
        }
    }

    public int buscarPosicion(int arreglo,String elemento){
        int posicion=-1;
        elemento = elemento.toUpperCase();
        String tem;
        String elementos[]=getResources().getStringArray(arreglo);
        for (int i=0; i<elementos.length;i++){
            tem=elementos[i].toUpperCase();
            if(tem.equals(elemento)){
                posicion=i;
                break;
            }
        }
        return posicion;
    }

    public void cargarImagen(){
        try{
            File filePhoto=new File(pathImage);
            photoURI = FileProvider.getUriForFile(getContext(),"com.example.votoapp",filePhoto);
            iv_foto.setImageURI(photoURI);
        }catch (Exception ex){
            Toast.makeText(getContext(), "Ocurrio un error al cargar la imagen", Toast.LENGTH_SHORT).show();
            Log.d("Cargar Imagen","Error al cargar imagen "+pathImage+"\nMensaje: "+ex.getMessage()+"\nCausa: "+ex.getCause());
            pathImage="";
        }
    }

    private void initGoogleMap(Bundle savedInstanceState){
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mv_map.onCreate(mapViewBundle);
        mv_map.getMapAsync(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }
        mv_map.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mv_map.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mv_map.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mv_map.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        myMap = map;
        //map.addMarker(new MarkerOptions().position(new LatLng(0,0)).title("Ubicación"));
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);
    }

    @Override
    public void onPause() {
        mv_map.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mv_map.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mv_map.onLowMemory();
    }

    private void busqueDireccion(String direccion){
        List<Address> direcciones = null;
        Geocoder geocoder = new Geocoder(getContext());
        MarkerOptions markerOptions = new MarkerOptions();
        try {
            direcciones = geocoder.getFromLocationName(direccion,5);
            if(direcciones!=null){
                //for(int i=0; i<direcciones.size(); i++){
                myMap.clear();
                Address d = direcciones.get(0);
                LatLng latlng = new LatLng(d.getLatitude(),d.getLongitude());
                markerOptions.position(latlng);
                markerOptions.title("Ubicación del municipio");
                //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                myMap.addMarker(markerOptions);
                myMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                myMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                //}
            }else{
                Toast.makeText(getContext(), "Ubicación no encontrada", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}