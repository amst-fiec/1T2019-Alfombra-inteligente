package com.example.carlo.amst5;

import android.graphics.drawable.Drawable;

public class Category {

    private String categoryId;
    private String title,description,ubicacion;
    private Drawable imagen;

    public Category() {
        super();
    }

    public Category(String categoryId, String title, String description, String location, Drawable imagen) {
        super();
        this.title = title;
        this.description = description;
        this.ubicacion=location;
        this.imagen = imagen;
        this.categoryId = categoryId;
    }


    public String getTitle() {
        return title;
    }

    public String getUbicacion(){
        return ubicacion;
    }

    public void setTittle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Drawable getImage() {
        return imagen;
    }

    public void setImagen(Drawable imagen) {
        this.imagen = imagen;
    }

    public void setUbicacion(String location){
        this.ubicacion = location;
    }

    public String getCategoryId(){return categoryId;}

    public void setCategoryId(String categoryId){this.categoryId = categoryId;}
}
