package com.example.prm3.connect;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ListView;
import android.widget.Toast;

import com.example.prm3.mains.MainActivity;
import com.example.prm3.models.Article;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

class RSSParser extends AsyncTask<Void,Void,Boolean> {
   @SuppressLint("StaticFieldLeak")
   private
   Context c;
   private InputStream is;
   @SuppressLint("StaticFieldLeak")
   private
   ListView lv;

   private ProgressDialog pd;


    RSSParser(Context c, InputStream is, ListView lv) {
        this.c = c;
        this.is = is;
        this.lv = lv;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd=new ProgressDialog(c);
        pd.setTitle("Parse RSS");
        pd.setMessage("Parsing...Please wait");
        pd.show();
    }

    @Override
    protected Boolean doInBackground(Void... params){
        return this.parseRSS();
    }

    @Override
    protected  void onPostExecute(Boolean isParsed){
        super.onPostExecute(isParsed);
        pd.dismiss();
        if(isParsed){
            //BIND
            //lv.setAdapter(new CustomAdapter(c,MainActivity.articles));
            lv.setAdapter(MainActivity.readAdapter);
        } else {
            Toast.makeText(c, "Unable to parse", Toast.LENGTH_SHORT).show();
        }
    }

    private Boolean parseRSS(){
        try{
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(is,null);
            int event = parser.getEventType();

            String tagValue = null;
            boolean isSiteMeta = true;

            //MainActivity.articles.clear();
            Article article = new Article();

            do {
                String tagName = parser.getName();

                switch (event){
                    case XmlPullParser.START_TAG:
                        if (tagName.equalsIgnoreCase("item")){
                            article = new Article();
                            isSiteMeta = false;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        tagValue = parser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if(!isSiteMeta){
                            if(tagName.equalsIgnoreCase("title")){
                                assert tagValue != null;
                                tagValue = tagValue.replaceAll("\\.", "\\,");
                                article.setTitle(tagValue);
                            } else if (tagName.equalsIgnoreCase("description")){
                                assert tagValue != null;
                                String desc = tagValue.substring(tagValue.indexOf(" /> ")+4, tagValue.length());
                                String imageURL = tagValue.substring(tagValue.indexOf("src=")+5,tagValue.indexOf("?")+1);
                               // imageURL += "dstw=1400&dsth=820&quality=100";
                                article.setImageUrl(imageURL);
                                article.setDescription(desc);
                            } else if (tagName.equalsIgnoreCase("link")){
                                article.setUrl(tagValue);
                            }
                        }
                        if(tagName.equalsIgnoreCase("item")){
                            MainActivity.readAdapter.add(article);
                            //MainActivity.articles.add(article);
                            isSiteMeta = true;
                        }
                        break;
                }
                event = parser.next();

            } while (event != XmlPullParser.END_DOCUMENT);

            Query favQuery = MainActivity.favRef;
            favQuery.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Article a = dataSnapshot.getValue(Article.class);
                    MainActivity.favAdapter.add(a);
                }
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                }
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

            final List<Article> copy = new ArrayList<>(MainActivity.readAdapter.getArticles());

            Query readQuery = MainActivity.readRef;
            readQuery.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    Article a = dataSnapshot.getValue(Article.class);
                    for (Article art : copy) {
                        assert a != null;
                        if(a.getTitle().equals(art.getTitle())) MainActivity.readAdapter.remove(art);
                    }
                }
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                }
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

            return true;
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
