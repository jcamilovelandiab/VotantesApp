package com.example.votoapp.ui.crear;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.votoapp.Constantes.MAPVIEW_BUNDLE_KEY;

public class CrearFragment extends Fragment implements OnMapReadyCallback {

    private int dia=-1, mes=-1, anio=-1; //fecha de nacimiento
    private static final int REQUEST_TAKE_PHOTO=1;

    private EditText et_nombres, et_apellidos, et_fecha_nacimiento;
    private EditText et_calle, et_numInt, et_numExt, et_colonia, et_codigo_postal; // Domicilio
    private EditText et_clave_electoral, et_CURP, et_anio_registro, et_anio_emision, et_vigencia;
    private Spinner sp_estado, sp_municipio, sp_seccion, sp_localidad, sp_sexo;
    private ImageView iv_foto;

    private String strClave_electoral, strCURP;
    private String strEstado, strMunicipio,strSeccion="sin sección", strLocalidad="sin localidad", strSexo;

    private String pathImage="", currentPhotoPath="";
    int dia_nacimiento, mes_nacimiento, anio_nacimiento;

    private Button btn_guardar, btn_limpiar, btn_fecha_nacimiento, btn_generar_curp;
    Uri photoURI;
    SQLite sqLite;
    private MapView mv_map;
    private GoogleMap myMap;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_crear, container, false);

        // Enlazamos interfaz con el modelo
        sp_estado = root.findViewById(R.id.crear_sp_estado);
        sp_municipio = root.findViewById(R.id.crear_sp_municipio);

        sp_sexo = root.findViewById(R.id.crear_sp_sexo);
        sp_seccion = root.findViewById(R.id.crear_sp_seccion);
        sp_localidad = root.findViewById(R.id.crear_sp_localidad);

        et_nombres = root.findViewById(R.id.crear_et_nombres);
        et_apellidos = root.findViewById(R.id.crear_et_apellidos);
        et_fecha_nacimiento = root.findViewById(R.id.crear_et_fecha_nacimiento);

        et_calle = root.findViewById(R.id.crear_et_calle);
        et_numExt = root.findViewById(R.id.crear_et_numExt);
        et_numInt = root.findViewById(R.id.crear_et_numInt);
        et_colonia = root.findViewById(R.id.crear_et_colonia);
        et_codigo_postal = root.findViewById(R.id.crear_et_codigo_postal);

        et_anio_registro = root.findViewById(R.id.crear_et_anio_registro);
        et_clave_electoral = root.findViewById(R.id.crear_et_clave_electoral);
        et_CURP = root.findViewById(R.id.crear_et_curp);
        et_anio_emision = root.findViewById(R.id.crear_et_anio_emision);
        et_vigencia = root.findViewById(R.id.crear_et_vigencia);

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

        btn_fecha_nacimiento = root.findViewById(R.id.crear_btn_fecha_nacimiento);
        btn_limpiar = root.findViewById(R.id.crear_btn_limpiar);
        btn_guardar = root.findViewById(R.id.crear_btn_guardar);
        btn_generar_curp = root.findViewById(R.id.crear_btn_generar_curp);

        iv_foto = root.findViewById(R.id.crear_iv_foto);
        mv_map = root.findViewById(R.id.crear_mv_map);
        initGoogleMap(savedInstanceState);

        // Conexión con base de datos
        sqLite = new SQLite(getContext());
        sqLite.abrir();

        configureSpinnerSexo();
        configureFechaNacimiento();
        configureBtnLimpiar();
        configureTomarFoto();
        configureSpinnerEstado();
        configureBtnGenerarCurp();
        configureBtnGuardar();

        return root;

    }

    private void configureBtnGenerarCurp(){
        btn_generar_curp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!et_nombres.getText().toString().trim().equals("") &&
                   !et_apellidos.getText().toString().trim().equals("") &&
                   !et_fecha_nacimiento.getText().toString().trim().equals("") &&
                    strSexo!=null){
                    Votante votante = new Votante(et_nombres.getText().toString(),
                            et_apellidos.getText().toString(), et_fecha_nacimiento.getText().toString(), Sexo.valueOf(strSexo));
                    strCURP = Generador.generarCURP(votante);
                    System.out.println(strCURP);
                    et_CURP.setText(strCURP);
                    Toast.makeText(getContext(), "CURP generado éxitosamente!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(), "Para generar el CURP debe ingresar primero sus nombres" +
                            ", apellidos, fecha de nacimiento y sexo.", Toast.LENGTH_SHORT).show();
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
                    configureSpinnerMunicipio(municipioAdapter);
                    configureSpinnerSeccion(seccionAdapter);
                    configureSpinnerLocalidad(localidadAdapter);
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
                    String direccion = strMunicipio.split(" ")[1]+", "+strEstado.split(" ")[2]+", Mexico";
                    busqueDireccion(direccion);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void configureSpinnerSeccion(final ArrayAdapter<CharSequence> seccionAdapter){
        sp_seccion.setAdapter(seccionAdapter);
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

    private void configureSpinnerLocalidad(final ArrayAdapter<CharSequence> localidadAdapter){
        sp_localidad.setAdapter(localidadAdapter);
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
                        dia_nacimiento = dayOfMonth; mes_nacimiento=monthOfYear+1;
                        anio_nacimiento = year;
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

    private void configureBtnGuardar(){
        //Guardar registro
        btn_guardar.setOnClickListener(new View.OnClickListener() {
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

                        if((Integer.parseInt(et_anio_emision.getText().toString())-anio_nacimiento)<18){
                            Toast.makeText(getContext(),
                                    "El año de emisión no puede ser menor a los 18 años de la fecha de nacimiento",
                                    Toast.LENGTH_LONG).show();
                            et_anio_emision.requestFocus();
                        }else{
                            String domicilio =
                                    et_calle.getText().toString()+"\n"+
                                    et_numInt.getText().toString()+"\n"+
                                    et_numExt.getText().toString()+"\n"+
                                    et_colonia.getText().toString()+"\n"+
                                    et_codigo_postal.getText().toString();

                            Toast.makeText(getContext(), "MUNICIPIO:"+strEstado+","+strMunicipio+"\r\n"+
                                    et_apellidos.getText().toString().toUpperCase()+" "+et_nombres.getText().toString().toUpperCase()+"\r\n"+
                                    strSexo+" "+
                                    et_CURP.getText().toString().toUpperCase()+" "+
                                    et_clave_electoral.getText().toString().toUpperCase()+" "+
                                    et_anio_emision.getText().toString().toUpperCase()+" "+
                                    et_vigencia.getText().toString(), Toast.LENGTH_LONG).show();

                            if (sqLite.registrarVotante(
                                    et_nombres.getText().toString().toUpperCase()+"",
                                    et_apellidos.getText().toString().toUpperCase()+"",
                                    domicilio.toUpperCase()+"",
                                    et_fecha_nacimiento.getText().toString()+"",
                                    strSexo+"",
                                    et_clave_electoral.getText().toString().toUpperCase()+"",
                                    et_CURP.getText().toString().toUpperCase()+"",
                                    Integer.parseInt(et_anio_registro.getText().toString())+0,
                                    strEstado+"",
                                    strMunicipio+"",
                                    strSeccion+"",
                                    strLocalidad+"",
                                    Integer.parseInt(et_anio_emision.getText().toString())+0,
                                    Integer.parseInt(et_vigencia.getText().toString())+0,
                                    pathImage+""
                            )){//Dentro if agregar registro
                                Toast.makeText(getContext(), "REGISTRO AÑADIDO",Toast.LENGTH_SHORT).show();
                                limpiarCampos();
                            }else{
                                Toast.makeText(getContext(),
                                        "Error: compruebe que los datos sean correctos",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
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
        //Limpiar campos
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

}