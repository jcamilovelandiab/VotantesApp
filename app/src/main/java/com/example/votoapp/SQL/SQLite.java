package com.example.votoapp.SQL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.util.Log;

import com.example.votoapp.model.entities.Sexo;
import com.example.votoapp.model.entities.Votante;

import java.util.ArrayList;
import java.util.Calendar;

public class SQLite {

    private sql sql;
    private SQLiteDatabase db;

    public SQLite(Context context) {
        sql = new sql(context);
    }

    public void abrir() {
        Log.i("SQLite",
                "Se abre conexión a la base de datos" +
                        sql.getDatabaseName());

        db = sql.getWritableDatabase();
        //restart();
    }

    private void restart(){
        db.execSQL("DROP TABLE IF EXISTS VOTANTES");
        System.out.println("Tabla VOTANTES eliminada");

        db.execSQL("CREATE TABLE VOTANTES (" +
                "ID_VOTANTE INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "NOMBRES TEXT NOT NULL," +
                "APELLIDOS TEXT NOT NULL,"+
                "FECHA_NACIMIENTO TEXT NOT NULL," +
                "SEXO TEXT NOT NULL," +

                "CLAVE_ELECTORAL TEXT NOT NULL," +
                "CURP TEXT NOT NULL UNIQUE," +
                "ANIO_REGISTRO INTEGER NOT NULL," +
                "NO_VERSION INTEGER NOT NULL," +

                "ESTADO TEXT NOT NULL," +
                "MUNICIPIO TEXT NOT NULL," +
                "SECCION TEXT NOT NULL,"+
                "LOCALIDAD TEXT NOT NULL,"+
                "DOMICILIO TEXT NOT NULL," +

                "ANIO_EMISION INTEGER NOT NULL," +
                "VIGENCIA INTEGER NOT NULL," +
                "FINADO INTEGER NOT NULL," +
                "IMAGEN TEXT NOT NULL);");
        System.out.println("Nueva tabla de votantes creada");
    }

    public void cerrar() {
        Log.i("SQLite",
                "Se cierra conexión a la base de datos" +
                        sql.getDatabaseName());
        sql.close();
    }

    public boolean registrarVotante(
            String nombres,
            String apellidos,
            String domicilio,
            String fecha_nacimiento,
            String sexo,
            String clave_electoral,
            String CURP,
            int anio_registro,
            //String no_version,
            String estado,
            String municipio,
            String seccion,
            String localidad,
            int anio_emision,
            int vigencia,
            //boolean finado,
            String path_imagen) {

        ContentValues cv = new ContentValues(); //Equivalente a putExtra
        cv.put("NOMBRES", nombres);
        cv.put("APELLIDOS", apellidos);
        cv.put("FECHA_NACIMIENTO", fecha_nacimiento);
        cv.put("SEXO", sexo);

        cv.put("CLAVE_ELECTORAL", clave_electoral);
        cv.put("CURP", CURP);
        cv.put("ANIO_REGISTRO", anio_registro);
        cv.put("NO_VERSION", 1);

        cv.put("ESTADO", estado);
        cv.put("MUNICIPIO", municipio);
        cv.put("SECCION", seccion);
        cv.put("LOCALIDAD", localidad);
        cv.put("DOMICILIO", domicilio);

        cv.put("ANIO_EMISION", anio_emision);
        cv.put("VIGENCIA", vigencia);
        cv.put("FINADO", false);
        cv.put("IMAGEN", path_imagen);
        return (db.insert(
                "VOTANTES",
                null, cv) != -1) ? true : false;
    }

    //Leer base de datos
    public Cursor getRegistroVotantesActivos() {
        return db.rawQuery("SELECT * FROM VOTANTES WHERE FINADO=0", null);
    }

    //Leer base de datos
    public Cursor getRegistroVotantesFinados() {
        return db.rawQuery("SELECT * FROM VOTANTES WHERE FINADO=1", null);
    }

    public Cursor getVotantesxNombre_CURP_estado_municipio(String nombres, String apellidos, String curp, String estado, String municipio){
        String sql = "SELECT * FROM VOTANTES " +
                "WHERE NOMBRES='"+nombres+"' OR APELLIDOS='"+apellidos+"' OR "+
                "CURP='"+curp+"' OR (ESTADO='"+estado+"' AND MUNICIPIO='"+municipio+"');";
        return db.rawQuery(sql, null);
    }

    public ArrayList<String> getStringVotantesFromCursor(Cursor cursor) {
        ArrayList<String> listData = new ArrayList<>();
        Votante votante = new Votante();
        if (cursor.moveToFirst()) {
            do {
                votante.setId(cursor.getLong(cursor.getColumnIndex("ID_VOTANTE")));
                votante.setNombres(cursor.getString(cursor.getColumnIndex("NOMBRES")));
                votante.setApellidos(cursor.getString(cursor.getColumnIndex("APELLIDOS")));
                votante.setFecha_nacimiento(cursor.getString(cursor.getColumnIndex("FECHA_NACIMIENTO")));
                votante.setSexo(Sexo.valueOf(cursor.getString(cursor.getColumnIndex("SEXO"))));

                votante.setClave_electoral(cursor.getString(cursor.getColumnIndex("CLAVE_ELECTORAL")));
                votante.setCURP(cursor.getString(cursor.getColumnIndex("CURP")));
                votante.setAnio_registro(cursor.getInt(cursor.getColumnIndex("ANIO_REGISTRO")));
                votante.setNo_version(cursor.getInt(cursor.getColumnIndex("NO_VERSION")));

                votante.setEstado(cursor.getString(cursor.getColumnIndex("ESTADO")));
                votante.setMunicipio(cursor.getString(cursor.getColumnIndex("MUNICIPIO")));
                votante.setSeccion(cursor.getString(cursor.getColumnIndex("SECCION")));
                votante.setLocalidad(cursor.getString(cursor.getColumnIndex("LOCALIDAD")));
                votante.setDomicilio(cursor.getString(cursor.getColumnIndex("DOMICILIO")));

                votante.setAnio_emision(cursor.getInt(cursor.getColumnIndex("ANIO_EMISION")));
                votante.setVigencia(cursor.getInt(cursor.getColumnIndex("VIGENCIA")));
                votante.setFinado(cursor.getInt(cursor.getColumnIndex("FINADO"))==1?true:false);
                //votante.setPath_imagen(cursor.getString(cursor.getColumnIndex("IMAGEN")));

                listData.add(votante.toString()); //LO AGREGAMOS AL ARRAYLIST

                votante = new Votante();
            } while (cursor.moveToNext()); //MIENTRAS LA CONSULTA TENGA DATOS
        }
        return listData;
    }

    public ArrayList<Votante> getVotantesFromCursor(Cursor cursor) {
        ArrayList<Votante> listData = new ArrayList<>();
        Votante votante = new Votante();
        if (cursor.moveToFirst()) {
            do {
                votante.setId(cursor.getLong(cursor.getColumnIndex("ID_VOTANTE")));
                votante.setNombres(cursor.getString(cursor.getColumnIndex("NOMBRES")));
                votante.setApellidos(cursor.getString(cursor.getColumnIndex("APELLIDOS")));
                votante.setFecha_nacimiento(cursor.getString(cursor.getColumnIndex("FECHA_NACIMIENTO")));
                votante.setSexo(Sexo.valueOf(cursor.getString(cursor.getColumnIndex("SEXO"))));

                votante.setClave_electoral(cursor.getString(cursor.getColumnIndex("CLAVE_ELECTORAL")));
                votante.setCURP(cursor.getString(cursor.getColumnIndex("CURP")));
                votante.setAnio_registro(cursor.getInt(cursor.getColumnIndex("ANIO_REGISTRO")));
                votante.setNo_version(cursor.getInt(cursor.getColumnIndex("NO_VERSION")));

                votante.setEstado(cursor.getString(cursor.getColumnIndex("ESTADO")));
                votante.setMunicipio(cursor.getString(cursor.getColumnIndex("MUNICIPIO")));
                votante.setSeccion(cursor.getString(cursor.getColumnIndex("SECCION")));
                votante.setLocalidad(cursor.getString(cursor.getColumnIndex("LOCALIDAD")));
                votante.setDomicilio(cursor.getString(cursor.getColumnIndex("DOMICILIO")));

                votante.setAnio_emision(cursor.getInt(cursor.getColumnIndex("ANIO_EMISION")));
                votante.setVigencia(cursor.getInt(cursor.getColumnIndex("VIGENCIA")));
                votante.setFinado(cursor.getInt(cursor.getColumnIndex("FINADO"))==1?true:false);
                votante.setPath_imagen(cursor.getString(cursor.getColumnIndex("IMAGEN")));
                listData.add(votante); //LO AGREGAMOS AL ARRAYLIST

                votante = new Votante();
            } while (cursor.moveToNext()); //MIENTRAS LA CONSULTA TENGA DATOS
        }
        return listData;
    }

    public ArrayList<String> getImagenes(Cursor cursor){
        ArrayList<String> listData = new ArrayList<>();
        if (cursor.moveToFirst()){
            do {
                String pathImage = cursor.getString(cursor.getColumnIndex("IMAGEN"));
                listData.add(pathImage);
            }while (cursor.moveToNext());
        }
        return listData;
    }

    public Cursor getVotantexID(int idVotante) {
        return db.rawQuery("SELECT * FROM VOTANTES WHERE ID_VOTANTE="+String.valueOf(idVotante), null);
    }

    public Cursor getVotantexCURP(String curp) {
        return db.rawQuery(
                "SELECT * FROM VOTANTES WHERE CURP=" + curp,
                null);
    }

    public String updateVotantexCURP(
            String viejoCURP,
            String nombres,
            String apellidos,
            String fecha_nacimiento,
            String sexo,

            String domicilio,
            String seccion,
            String localidad,
            String estado,
            String municipio,

            String clave_electoral,
            String nuevoCURP,
            int anio_registro,
            int no_version,



            int anio_emision,
            int vigencia,
            boolean finado,
            String path_imagen) {

        ContentValues cv = new ContentValues(); //Equivalente a putExtra
        cv.put("NOMBRES", nombres);
        cv.put("APELLIDOS", apellidos);
        cv.put("FECHA_NACIMIENTO", fecha_nacimiento);
        cv.put("SEXO", sexo);

        cv.put("CLAVE_ELECTORAL", clave_electoral);
        cv.put("CURP", nuevoCURP);
        cv.put("ANIO_REGISTRO", anio_registro);
        cv.put("NO_VERSION", no_version);

        cv.put("ESTADO", estado);
        cv.put("MUNICIPIO", municipio);
        cv.put("SECCION",seccion);
        cv.put("LOCALIDAD", localidad);
        cv.put("DOMICILIO", domicilio);

        cv.put("ANIO_EMISION", anio_emision);
        cv.put("VIGENCIA", vigencia);
        cv.put("FINADO", finado);
        cv.put("IMAGEN", path_imagen);
        int cant = db.update(
                "VOTANTES",
                cv,
                "CURP=" + viejoCURP,
                null);
        if (cant == 1) {
            return "Votante modificado";
        } else {
            return "Error, no se modifico";
        }
    }

    public boolean updateVotantexID(
            Long idVotante,
            String nombres,
            String apellidos,

            String fecha_nacimiento,
            String sexo,

            String domicilio,
            String seccion,
            String localidad,
            String estado,
            String municipio,

            String clave_electoral,
            String CURP,
            int anio_registro,
            int no_version,

            int anio_emision,
            int vigencia,
            boolean finado,
            String path_imagen) {

        ContentValues cv = new ContentValues(); //Equivalente a putExtra
        cv.put("NOMBRES", nombres);
        cv.put("APELLIDOS", apellidos);
        cv.put("FECHA_NACIMIENTO", fecha_nacimiento);
        cv.put("SEXO", sexo);

        cv.put("CLAVE_ELECTORAL", clave_electoral);
        cv.put("CURP", CURP);
        cv.put("ANIO_REGISTRO", anio_registro);
        cv.put("NO_VERSION", no_version);

        cv.put("ESTADO", estado);
        cv.put("MUNICIPIO", municipio);
        cv.put("SECCION", seccion);
        cv.put("LOCALIDAD", localidad);
        cv.put("DOMICILIO", domicilio);

        cv.put("ANIO_EMISION", anio_emision);
        cv.put("VIGENCIA", vigencia);
        cv.put("FINADO", finado);
        cv.put("IMAGEN", path_imagen);
        int cant = db.update(
                "VOTANTES",
                cv,
                "ID_VOTANTE=" + idVotante,
                null);
        if (cant == 1) {
            return true; //"Votante modificado";
        } else {
            return false; //"Error, no se modifico";
        }
    }

    public void UpdateFinadoxCURP(String curp) {
        ContentValues cv =  new ContentValues();
        cv.put("FINADO","1");
        db.update("VOTANTES",cv, "CURP='"+curp+"'",null);
    }

    public void UpdateFinadoxID(String idVotante) {
        ContentValues cv =  new ContentValues();
        cv.put("FINADO","1");
        db.update("VOTANTES",cv, "ID_VOTANTE="+idVotante,null);
    }

}

