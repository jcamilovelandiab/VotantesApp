package com.example.votoapp.model.entities;

import java.util.Random;

public class Generador {
        public static String generarCURP(Votante votante){
        String curp="";
        String[] nombres = votante.getNombres().toUpperCase().split(" "), apellidos=votante.getApellidos().toUpperCase().split(" ");

        assert(apellidos.length>=1);
        assert(nombres.length>=1);

        String primer_nombre = nombres[0].toUpperCase(), segundo_nombre = (nombres.length>=2)?nombres[1].toUpperCase():null;
        String primer_apellido = apellidos[0].toUpperCase(), segundo_apellido= (apellidos.length>=2)?apellidos[1].toUpperCase():"X";

        for(int i=0; i<primer_apellido.length(); i++){
            if(Character.isLetter(primer_apellido.charAt(i))){
                if(curp.length()==0) curp+=primer_apellido.charAt(i); // primera letra, segundo caracter
                if(primer_apellido.charAt(i)=='A' || primer_apellido.charAt(i)=='E' ||
                        primer_apellido.charAt(i)=='I' && primer_apellido.charAt(i)=='O' || primer_apellido.charAt(i)=='U'){
                    curp+=primer_apellido.charAt(i); // primera vocal, segundo caracter
                    break;
                }
            }
        }

        curp+=segundo_apellido.charAt(0); //tercer caracter

        if((primer_nombre.equals("JOSE") || primer_nombre.equals("MARIA"))
                && segundo_nombre!=null){
            curp+=segundo_nombre.charAt(0); // cuarto caracter
        }else{
            curp+=primer_nombre.charAt(0); // cuarto caracter
        }

        String[] fecha_nacimiento = votante.getFecha_nacimiento().split("/");
        assert(fecha_nacimiento.length==3);
        String dia=fecha_nacimiento[0], mes=fecha_nacimiento[1], anio = fecha_nacimiento[2];

        curp+=anio.substring(anio.length()-2,anio.length()); // quinto y sexto caracter
        curp+=String.format("%02d", Integer.parseInt(mes));; // septimo y octavo caracter
        curp+=String.format("%02d", Integer.parseInt(dia)); // noveno y decimo caracter
        curp+=(votante.getSexo().toString().equals("MASCULINO"))?"H":"M"; //onceavo caracter

        curp+="NE"; //doceavo y treceavo caracter es según la RENAPO

        curp+=primer_apellido.charAt(0); // catorceavo, quinceavo y dieciseisavo carácter
        curp+=segundo_apellido.charAt(0);
        curp+=primer_nombre.charAt(0);

        curp+=(Integer.parseInt(anio)<2000)?"O":"A"; // diecisieteavo carácter
        curp+=String.valueOf(new Random().nextInt(10));//dieciochoavo caracter;
            assert(curp.length()==18);
        return curp;
    }


}
