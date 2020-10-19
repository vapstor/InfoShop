package br.com.infoshop.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Project implements Parcelable {
    private int id;
    private int id_categoria;
    private String titulo, descricao, imagem;
    private double preco;

    public Project() {    }

    public Project(int id, int id_categoria, String titulo, String descricao, double preco, String imagem) {
        this.id = id;
        this.id_categoria = id_categoria;
        this.titulo = titulo;
        this.descricao = descricao;
        this.preco = preco;
        this.imagem = imagem;
    }

    public int getId() {
        return id;
    }

    public void setId(int ItemId) {
        this.id = ItemId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }


    public int getId_categoria() {
        return id_categoria;
    }

    public void setId_categoria(int id_categoria) {
        this.id_categoria = id_categoria;
    }


    /**
     * PARCELABLE
     **/

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(id_categoria);
        dest.writeString(titulo);
        dest.writeString(descricao);
        dest.writeDouble(preco);
        dest.writeString(imagem);
    }

    public static final Creator<Project> CREATOR = new Creator<Project>() {
        @Override
        public Project createFromParcel(Parcel in) {
            return new Project(in);
        }

        @Override
        public Project[] newArray(int size) {
            return new Project[size];
        }
    };

    private Project(Parcel in) {
        id = in.readInt();
        id_categoria = in.readInt();
        titulo = in.readString();
        descricao = in.readString();
        preco = in.readDouble();
        imagem = in.readString();
    }
}
