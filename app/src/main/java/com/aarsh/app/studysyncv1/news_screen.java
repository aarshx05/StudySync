package com.aarsh.app.studysyncv1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class news_screen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    AlertDialog dialog;
    NavigationView navigationView;
    Toolbar toolbar;

    RecyclerView recyclerView;
    EventsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_screen);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventsAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        new FetchEventsTask().execute();

        drawerLayout = findViewById(R.id.nav_drawer);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar_id);

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_draw_open, R.string.nav_draw_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                Intent home_intent = new Intent(news_screen.this, home_screen.class);
                startActivity(home_intent);
                break;

            case R.id.nav_profile:
                Intent prof_intent = new Intent(news_screen.this, UserProfileActivity.class);
                startActivity(prof_intent);
                break;
            case R.id.nav_question_paper:
                Intent qn_intent = new Intent(news_screen.this, PapersScreen.class);
                startActivity(qn_intent);
                break;

        }

        return true;
    }


    private class FetchEventsTask extends AsyncTask<Void, Void, List<EventItem>> {
        @Override
        protected List<EventItem> doInBackground(Void... voids) {
            String url = "https://nfsu.ac.in/events";
            List<EventItem> eventItems = new ArrayList<>();

            try {
                Document document = Jsoup.connect(url).get();

                // Select elements with class "col-md-6 pr0 pl0" for text
                Elements textElements = document.select(".col-md-6.info");
                // Select elements with class "col-md-6 pr0 pl0" for images
                Elements imageElements = document.select(".thumb.bg-cover");

                // Log image URLs
                for (Element imageElement : imageElements) {
                    String imageUrl = imageElement.select("img").attr("src");
                    Log.d("Image URL", imageUrl);
                }

                // Assuming text and image elements have the same size and order
                for (int i = 0; i < textElements.size(); i++) {
                    String text = textElements.get(i).text();
                    String imageUrl = (i < imageElements.size()) ? imageElements.get(i).select("img").attr("src") : "";
                    eventItems.add(new EventItem(text, imageUrl));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return eventItems;
        }

        @Override
        protected void onPostExecute(List<EventItem> eventItems) {
            adapter = new EventsAdapter(eventItems);
            recyclerView.setAdapter(adapter);
        }

    }
}