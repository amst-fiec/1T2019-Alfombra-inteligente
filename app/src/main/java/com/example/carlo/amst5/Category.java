package com.example.carlo.amst5;

import android.graphics.drawable.Drawable;

public class Category {

    private String categoryId;
    private String title,estado,fecha;
    private Drawable imagen;

    public Category() {
        super();
    }

    public Category( String estado, String fecha, String tanque, Drawable imagen) {
        super();
        this.title = tanque;
        this.estado = estado;
        this.fecha=fecha;
        this.imagen = imagen;

    }


    public String getTitle() {
        return title;
    }

    public String getUbicacion(){
        return fecha;
    }

    public void setTittle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return estado;
    }

    public void setDescription(String description) {
        this.estado = description;
    }

    public Drawable getImage() {
        return imagen;
    }

    public void setImagen(Drawable imagen) {
        this.imagen = imagen;
    }

    public void setUbicacion(String location){
        this.fecha = location;
    }

    public String getCategoryId(){return categoryId;}

    public void setCategoryId(String categoryId){this.categoryId = categoryId;}
}
