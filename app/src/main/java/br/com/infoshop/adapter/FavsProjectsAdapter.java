package br.com.infoshop.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import br.com.infoshop.R;
import br.com.infoshop.holder.FavsProjectsViewHolder;
import br.com.infoshop.interfaces.IUnfavClick;
import br.com.infoshop.model.Project;
import br.com.infoshop.utils.RoundedTransformation;

import static br.com.infoshop.utils.Constants.MY_LOG_TAG;
import static br.com.infoshop.utils.Util.RSmask;

public class FavsProjectsAdapter extends RecyclerView.Adapter<FavsProjectsViewHolder> {

    private ArrayList<Project> itens;
    private final Context context;
    private final IUnfavClick iUnfavClick;

    public FavsProjectsAdapter(ArrayList<Project> itens, Context context, IUnfavClick iUnfavClick) {
        if (itens == null)
            throw new NullPointerException("Lista de Itens Nula!");
        this.context = context;
        this.itens = itens;
        this.iUnfavClick = iUnfavClick;
    }

    @NonNull
    @Override
    public FavsProjectsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_profile_recycler_fav_project_item_, parent, false);
        return new FavsProjectsViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull FavsProjectsViewHolder holder, int position) {
        Project item = itens.get(position);
        //Titulo
        TextView titulo = holder.tituloDoProjeto;
        titulo.setText(item.getTitulo());

        //Descrição
        TextView descricao = holder.descricaoDoProjeto;
        descricao.setText(item.getDescricao());

        //Preço
        TextView preco = holder.precoItemProduto;
        preco.setText(RSmask(item.getPreco()));

        //Imagem
        ImageView imagemItem = holder.imgProduto;
        String url = "https://www.royalfarma.com.br/uploads/" + item.getImagem();

        //btn unfav
        AppCompatImageButton btnUnfav = holder.unFavButton;
        btnUnfav.setOnClickListener(v -> {
            new AlertDialog.Builder(context, R.style.AlertDialog_Rounded)
                    .setTitle(R.string.excluir)
                    .setMessage(R.string.voce_tem_certeza_que_deseja_desfavoritar_este_projeto)
                    .setNegativeButton(R.string.cancelar, (dialog, which) -> {
//                    adapterProjects.notifyItemChanged(position);
                        notifyItemRemoved(position);
                        notifyItemInserted(position);
                    })
                    .setPositiveButton(R.string.excluir, (dialog, which) -> {
                        this.iUnfavClick.onUnfav(position);
                    }).setOnDismissListener(dialog -> notifyItemChanged(position)).show();

        });


        //LOADER
        final ProgressBar progressView = holder.progressBar;
        progressView.setVisibility(View.VISIBLE);
        imagemItem.setVisibility(View.VISIBLE);

        final Callback loadedCallback = new Callback() {
            @Override
            public void onSuccess() {
                progressView.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                String url = "drawable/empty_product_image";
                int productImageId = context.getResources().getIdentifier(url, "drawable", context.getPackageName());
                Picasso.get().load(productImageId).into(imagemItem);
            }
        };
        imagemItem.setTag(loadedCallback);
        if (item.getImagem() == null || item.getImagem().equals("")) {
            url = "drawable/empty_image_placeholder";
            int productImageId = this.context.getResources().getIdentifier(url, "drawable", context.getPackageName());
            Picasso.get()
                    .load(productImageId)
                    .transform(new RoundedTransformation(15, 0))
                    .fit()
                    .into(imagemItem, loadedCallback);
        } else {
            Log.d(MY_LOG_TAG, "URL: " + url);
            Log.d(MY_LOG_TAG, "Product Image URL DB: " + item.getImagem());
            Picasso.get()
                    .load(url)
                    .transform(new RoundedTransformation(15, 0))
                    .fit()
                    .into(imagemItem, loadedCallback);
        }
        //FIM LOADER
        //fim imagem

    }

    // Clean all elements of the recycler
    public void clear() {
        if (itens != null && getItemCount() > 0) {
            itens.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }

    // Add a list of items -- change to type used
    public void addAll(ArrayList<Project> list) {
        itens.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return itens.get(position).getId();
    }

    public void updateProjectsList(ArrayList<Project> projects) {
        this.itens = projects;
        notifyDataSetChanged();
    }
}
