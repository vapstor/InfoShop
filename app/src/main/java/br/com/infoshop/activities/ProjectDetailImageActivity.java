package br.com.infoshop.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import br.com.infoshop.R;

public class ProjectDetailImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail_image);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ImageView imageDetail = findViewById(R.id.image_detail);
            String url = extras.getString("imageURL", "empty_product_image");
            if (url.contains("empty_product_image") || url.isEmpty()) {
                url = "drawable/empty_image_placeholder";
                int productImageId = this.getResources().getIdentifier(url, "drawable", getPackageName());
                Picasso.get().load(productImageId).into(imageDetail);
            } else {
                Picasso.get().load(extras.getString("imageURL")).into(imageDetail);
            }
        } else {
            Toast.makeText(this, "Erro ao carregar imagem", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
