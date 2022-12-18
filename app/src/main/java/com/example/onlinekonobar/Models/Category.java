package com.example.onlinekonobar.Models;

public class Category {
    String description;
    String image;
    String name;

    public Category() {}

    public Category(String description, String image, String name) {
        this.description = description;
        this.image = image;
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
