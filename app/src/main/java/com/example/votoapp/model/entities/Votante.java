package com.example.votoapp.model.entities;

public class Votante {

    Long id;
    String nombres;
    String apellidos;
    String domicilio;
    String fecha_nacimiento;
    Sexo sexo;

    String clave_electoral;
    String CURP;
    int anio_registro;
    int no_version;

    String estado;
    String municipio;
    String seccion;
    String localidad;
    int anio_emision;
    int vigencia;
    boolean finado;
    String path_imagen;

    public Votante() {
    }

    public Votante(String nombres, String apellidos, String fecha_nacimiento, Sexo sexo){
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.fecha_nacimiento = fecha_nacimiento;
        this.sexo=sexo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public String getFecha_nacimiento() {
        return fecha_nacimiento;
    }

    public void setFecha_nacimiento(String fecha_nacimiento) {
        this.fecha_nacimiento = fecha_nacimiento;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public String getClave_electoral() {
        return clave_electoral;
    }

    public void setClave_electoral(String clave_electoral) {
        this.clave_electoral = clave_electoral;
    }

    public String getCURP() {
        return CURP;
    }

    public void setCURP(String CURP) {
        this.CURP = CURP;
    }

    public int getAnio_registro() {
        return anio_registro;
    }

    public void setAnio_registro(int anio_registro) {
        this.anio_registro = anio_registro;
    }

    public int getNo_version() {
        return no_version;
    }

    public void setNo_version(int no_version) {
        this.no_version = no_version;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getSeccion() {
        return seccion;
    }

    public void setSeccion(String seccion) {
        this.seccion = seccion;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public int getAnio_emision() {
        return anio_emision;
    }

    public void setAnio_emision(int anio_emision) {
        this.anio_emision = anio_emision;
    }

    public int getVigencia() {
        return vigencia;
    }

    public void setVigencia(int vigencia) {
        this.vigencia = vigencia;
    }

    public boolean isFinado() {
        return finado;
    }

    public void setFinado(boolean finado) {
        this.finado = finado;
    }

    public String getPath_imagen() {
        return path_imagen;
    }

    @Override
    public String toString() {
        String[] arrDomicilio = this.getDomicilio().split("\\r?\\n");
        assert(arrDomicilio.length==5);
        String d =
                "   Estado: " + estado +"\r\n"+
                "   Municipio: " + municipio +"\r\n"+
                "   CALLE: "+arrDomicilio[0]+"\r\n"+
                "   NÚMERO INTERIOR: "+arrDomicilio[1]+"\r\n"+
                "   NÚMERO EXTERIOR: "+arrDomicilio[2]+"\r\n"+
                "   COLONIA: "+arrDomicilio[3]+"\r\n"+
                "   CÓDIGO POSTAL: "+arrDomicilio[4];

        return  "[ID Votante: " + id + "\r\n"+
                "CURP del Votante: " + CURP + "\r\n"+
                "Nombres: " + nombres + "\r\n"+
                "Apellidos: " + apellidos + "\r\n"+
                "Domicilio:\n" + d + "\r\n"+
                "Fecha de Nacimiento: " + fecha_nacimiento + "\r\n"+
                "Sexo:" + sexo.toString() +"\r\n"+
                "Clave Electoral: " + clave_electoral + "\r\n"+
                "Año de registro: " + anio_registro +"\r\n"+
                "No de version: " + no_version +"\r\n"+
                "Año de emisión: " + anio_emision +"\r\n"+
                "Vigencia: " + vigencia +"\r\n"+
                "Finado: " + finado +"]";
    }

    public void setPath_imagen(String path_imagen) {
        this.path_imagen = path_imagen;
    }
}
