package com.example.onlinekonobar.Models;

import java.util.Map;

public class CafeCategory {
    String description;
    String image;
    String name;
    Map<String, Drink> cafeDrinksCategory;

    public CafeCategory() {}

    public CafeCategory(String description, String image, String name, Map<String, Drink> cafeDrinksCategory) {
        this.description = description;
        this.image = image;
        this.name = name;
        this.cafeDrinksCategory = cafeDrinksCategory;
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

    public Map<String, Drink> getCafeDrinksCategory() {
        return cafeDrinksCategory;
    }

    public void setCafeDrinksCategory(Map<String, Drink> cafeDrinksCategory) {
        this.cafeDrinksCategory = cafeDrinksCategory;
    }
}
