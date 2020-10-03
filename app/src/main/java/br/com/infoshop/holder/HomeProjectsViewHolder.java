package br.com.infoshop.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import br.com.infoshop.R;

public class HomeProjectsViewHolder extends RecyclerView.ViewHolder {
    public ProgressBar progressBar;
    public TextView tituloDoProjeto, descricaoDoProjeto, precoItemProduto;
    public ImageView imgProduto;
    public View viewForeground, viewBackground;

    public HomeProjectsViewHolder(View itemView) {
        super(itemView);
        tituloDoProjeto = itemView.findViewById(R.id.project_title);
        descricaoDoProjeto = itemView.findViewById(R.id.project_description);
        precoItemProduto = itemView.findViewById(R.id.project_price);
        imgProduto = itemView.findViewById(R.id.project_image);
        progressBar = itemView.findViewById(R.id.progress_bar_project_image);
        viewBackground = itemView.findViewById(R.id.view_background);
        viewForeground = itemView.findViewById(R.id.view_foreground);
    }
}
