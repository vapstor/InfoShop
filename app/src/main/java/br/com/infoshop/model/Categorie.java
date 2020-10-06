package br.com.infoshop.model;

public class Categorie {
    private String title;
    private int id;
    private int backgroundImagePath;

    public Categorie(int id, String title, int backgroundImagePath) {
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

    public int getBackgroundImagePath() {
        return backgroundImagePath;
    }

    public void setBackgroundImagePath(int backgroundImagePath) {
        this.backgroundImagePath = backgroundImagePath;
    }
}
