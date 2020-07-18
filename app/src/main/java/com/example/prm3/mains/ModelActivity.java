package com.example.prm3.mains;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.prm3.R;
import com.example.prm3.models.Article;
import com.example.prm3.view.PicassoClient;

public class ModelActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.model_layout);

        TextView titleTxt = findViewById(R.id.txtTitle);
        TextView descTxt = findViewById(R.id.txtDescription);
        ImageView img = findViewById(R.id.articleImage);
        TextView urlTxt = findViewById(R.id.txtUrl);

        Bundle bundle = getIntent().getExtras();

        final String title = bundle.getString("title");
        final String imageUrl = bundle.getString("imageUrl");
        final String desc = bundle.getString("desc");
        final String url = bundle.getString("url");


        final Article a = new Article(title,desc,imageUrl,url);

        Switch sw = findViewById(R.id.switchFavourite);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    MainActivity.favRef.child(a.getTitle()).setValue(a);
                    MainActivity.favAdapter.add(a);
                }
            }
        });

        img.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.readRef.child(a.getTitle()).setValue(a);
                MainActivity.readAdapter.remove(a);
                MainActivity.readAdapter.notifyDataSetChanged();
                Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browseIntent);
            }
        });


        titleTxt.setText(title);
        descTxt.setText(desc);
        PicassoClient.downloadImage(this,imageUrl,img);
        urlTxt.setText(url);

    }
}
