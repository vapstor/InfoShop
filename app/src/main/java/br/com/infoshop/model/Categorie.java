package br.com.infoshop.model;

public class Categorie {
    private String title;
    private int id;
    private String backgroundImagePath;

    public Categorie(int id, String title, String backgroundImagePath) {
        this.id = id;
        this.title = title;
        this.backgroundImagePath = backgroundImagePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackgroundImagePath() {
        return backgroundImagePath;
    }

    public void setBackgroundImagePath(String backgroundImagePath) {
        this.backgroundImagePath = backgroundImagePath;
    }
}
