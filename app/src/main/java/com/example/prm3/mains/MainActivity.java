package com.example.prm3.mains;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.core.app.NotificationCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.prm3.R;
import com.example.prm3.connect.Downloader;
import com.example.prm3.models.Article;
import com.example.prm3.view.CustomAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    final static String urlRss = "https://news.google.com/rss?hl=en-US&gl=US&ceid=US:en";
    private static final String CHANNEL_ID = "";
    //  https://news.google.com/news/rss/headlines/section/topic/
    // https://www.tvn24.pl/najnowsze.xml
    // https://news.google.com/rss?hl=en-US&gl=US&ceid=US:en
    FirebaseDatabase db;
    public static DatabaseReference favRef;
    public static DatabaseReference readRef;
    @SuppressLint("StaticFieldLeak")
    public static CustomAdapter favAdapter;
    @SuppressLint("StaticFieldLeak")
    public static CustomAdapter readAdapter;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = findViewById(R.id.lv);
        if(readAdapter == null) {
            favAdapter = new CustomAdapter(getBaseContext(), new ArrayList<Article>());
            readAdapter = new CustomAdapter(getBaseContext(), new ArrayList<Article>());
        }

        com.example.prm3.MainActivity.mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = com.example.prm3.MainActivity.mAuth.getCurrentUser();
        if(currentUser == null){
            Intent login = new Intent(this, com.example.prm3.MainActivity.class);
            startActivity(login);
        }

        db = FirebaseDatabase.getInstance();
        assert currentUser != null;
        favRef = db.getReference("FAVOURITES_" + currentUser.getUid());
        readRef = db.getReference("READ_" + currentUser.getUid());


        BottomNavigationView bnv = findViewById(R.id.bottom_navigation);

        bnv.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.feed:
                                lv.setAdapter(readAdapter);
                                //lv.setAdapter(new CustomAdapter(MainActivity.this, articles));
                                break;
                            case R.id.favourite:
                                lv.setAdapter(favAdapter);
                                break;
                            case R.id.profile:
                                Intent profile = new Intent(getBaseContext(), com.example.prm3.MainActivity.class);
                                startActivity(profile);
                                break;
                        }
                        return false;
                    }
                }
        );

        callAsynchronousTask();
    }

    public void callAsynchronousTask() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            new Downloader(MainActivity.this, urlRss, lv).execute();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 100000); //execute in every 50000 ms
    }

    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("My notification")
            .setContentText("H")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE);

}
