package com.example.votoapp.SQL;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class sql extends SQLiteOpenHelper {
    private static final String database = "VOTOAPP";
    private static final int VERSION = 2;

    private final String tVotantes = "CREATE TABLE VOTANTES (" +
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
            "IMAGEN TEXT NOT NULL);";

    //Constructor
    public sql(Context context){
        super(context, database, null, VERSION);
    }

    //metodos heredados
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(tVotantes);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        if (newVersion > oldVersion){
            db.execSQL("DROP TABLE IF EXISTS VOTOAPP");
            db.execSQL(tVotantes);
        }
    }
}
