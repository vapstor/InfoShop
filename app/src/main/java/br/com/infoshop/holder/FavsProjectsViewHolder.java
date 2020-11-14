package br.com.infoshop.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import br.com.infoshop.R;

public class FavsProjectsViewHolder extends RecyclerView.ViewHolder {
    public ProgressBar progressBar;
    public TextView tituloDoProjeto, descricaoDoProjeto, precoItemProduto;
    public AppCompatImageButton unFavButton;
    public ImageView imgProduto;
    public View viewForeground, viewBackground;

    public FavsProjectsViewHolder(View itemView) {
        super(itemView);
        tituloDoProjeto = itemView.findViewById(R.id.project_title);
        descricaoDoProjeto = itemView.findViewById(R.id.project_description);
        precoItemProduto = itemView.findViewById(R.id.project_price);
        imgProduto = itemView.findViewById(R.id.project_image);
        progressBar = itemView.findViewById(R.id.progress_bar_project_image);
        viewBackground = itemView.findViewById(R.id.view_background);
        viewForeground = itemView.findViewById(R.id.view_foreground);
        unFavButton = itemView.findViewById(R.id.btn_unfav_project);
    }
}
