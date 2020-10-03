package br.com.infoshop.model;

public class Project {
    private int id;
    private String title, description, imagePath;
    private double price;

    public Project(int id, String title, String description, double price, String imagePath) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.imagePath = imagePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int ItemId) {
        this.id = ItemId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
