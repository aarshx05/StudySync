package com.aarsh.app.studysyncv1;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class home_screen extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EventsAdapter adapter;
    private List<EventItem> eventItems;

    TextView name_t;
    ImageView papers, profile, notes, forum, news;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        eventItems = new ArrayList<>();
        adapter = new EventsAdapter(eventItems);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Load more data when reaching the end of the list
                if (!recyclerView.canScrollVertically(1)) {
                    new FetchEventsTask().execute();
                }
            }
        });

        name_t = findViewById(R.id.name_base);
        papers = findViewById(R.id.imageView);
        profile = findViewById(R.id.imageView1);
        notes = findViewById(R.id.imageView2);
        forum = findViewById(R.id.imageView3);
        news = findViewById(R.id.imageView4);

        getAndSetName();

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent p_intent = new Intent(home_screen.this, UserProfileActivity.class);
                startActivity(p_intent);
            }
        });
        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent n_intent = new Intent(home_screen.this, news_screen.class);
                startActivity(n_intent);
            }
        });

        papers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent p_intent = new Intent(home_screen.this, PapersScreen.class);
                startActivity(p_intent);
            }
        });

        new FetchEventsTask().execute();
    }

    private void getAndSetName() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uni_code = firebaseUser.getUid();

        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(uni_code).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails readDetails = snapshot.getValue(UserDetails.class);
                if (readDetails != null) {
                    String name = firebaseUser.getDisplayName();
                    String fname = "Hi," + name;
                    name_t.setText(fname);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(home_screen.this, "Something Went Wrong!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private class FetchEventsTask extends AsyncTask<Void, Void, List<EventItem>> {
        @Override
        protected List<EventItem> doInBackground(Void... voids) {
            String url = "https://nfsu.ac.in/events";
            List<EventItem> newEvents = new ArrayList<>();

            try {
                Document document = Jsoup.connect(url).get();

                Elements textElements = document.select(".col-md-6.info");
                Elements imageElements = document.select(".thumb.bg-cover");

                for (int i = 0; i < textElements.size(); i++) {
                    String text = textElements.get(i).text();
                    String imageUrl = (i < imageElements.size()) ? imageElements.get(i).select("img").attr("src") : "";
                    newEvents.add(new EventItem(text, imageUrl));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return newEvents;
        }

        @Override
        protected void onPostExecute(List<EventItem> newEvents) {
            eventItems.addAll(newEvents);
            adapter.notifyDataSetChanged();
        }
    }
}
