package com.example.prm3.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.prm3.R;
import com.example.prm3.models.Article;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends BaseAdapter {
    Context c;
    ArrayList<Article> articles;


    public CustomAdapter(Context c, ArrayList<Article> articles) {
        this.c = c;
        this.articles = articles;
    }

    public void clear(){
        articles.clear();
    }

    public void add(Article a){
        articles.add(a);
    }

    public void remove(Article a) { articles.remove(a); }

    public void removeAll(List<Article> a) { articles.remove(a);}

    @Override
    public int getCount() {
        return articles.size();
    }

    @Override
    public Object getItem(int position) {
        return articles.get(position);
    }

    public ArrayList<Article> getArticles() {
        return articles;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = LayoutInflater.from(c).inflate(R.layout.model, parent, false);
        }
        TextView titleTxt = convertView.findViewById(R.id.txtTitle);
        TextView descTxt = convertView.findViewById(R.id.txtDescription);
        ImageView img = convertView.findViewById(R.id.articleImage);
        TextView urlTxt = convertView.findViewById(R.id.txtUrl);

        final Article article = (Article) this.getItem(position);



        final String title = article.getTitle();
        final String imageUrl = article.getImageUrl();
        final String desc = article.getDescription();
        final String url = article.getUrl();

        titleTxt.setText(title);
        descTxt.setText(desc);
        PicassoClient.downloadImage(c,imageUrl,img);
        urlTxt.setText(url);

        convertView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {

                Intent selectedNews = new Intent(c, com.example.prm3.mains.ModelActivity.class);
                selectedNews.putExtra("title", title);
                selectedNews.putExtra("desc", desc);
                selectedNews.putExtra("imageUrl", imageUrl);
                selectedNews.putExtra("url", url);
                selectedNews.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                c.startActivity(selectedNews);

            }
        });



        return convertView;
    }
}
