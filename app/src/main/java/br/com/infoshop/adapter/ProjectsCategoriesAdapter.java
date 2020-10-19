package br.com.infoshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;

import br.com.infoshop.R;
import br.com.infoshop.model.Categorie;

public class ProjectsCategoriesAdapter extends BaseAdapter {
    private final Context context;
    private ArrayList<Categorie> categories;

    public ProjectsCategoriesAdapter(ArrayList<Categorie> categories, Context context) {
        this.categories = categories;
        this.context = context;
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Categorie getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return categories.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Categorie categorie = categories.get(position);

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.layout_grid_view_categorie_item, null);
        }

        //Titulo
        TextView titulo = convertView.findViewById(R.id.title_item_categorie);
        titulo.setText(categorie.getTitle());

        //Imagem
        ConstraintLayout root = convertView.findViewById(R.id.container_image_item_categorie);
        String bg = categorie.getBackgroundImagePath();
        if (bg != null && !bg.equals("")) {
            root.setBackground(ResourcesCompat.getDrawable(context.getResources(), Integer.parseInt(bg), null));
        }


//        convertView.setOnClickListener(v -> {
//            NavHostFragment navHostFragment = (NavHostFragment) ((FragmentActivity) context).getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
//            if (navHostFragment != null) {
//                Bundle bundle = new Bundle();
//                bundle.putInt("idCategoria", categorie.getId());
//                navHostFragment.getNavController().navigate(R.id.action_navigation_projects_categories_to_navigation_projects, bundle);
//            } else {
//                Log.e(MY_LOG_TAG, "Nav Host Fragment Null !");
//            }
//        });

        return convertView;
    }

    public void updateList(ArrayList<Categorie> updatedList) {
        this.categories = updatedList;
        notifyDataSetChanged();
    }
}
