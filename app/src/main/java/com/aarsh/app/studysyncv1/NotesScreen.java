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

import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NotesScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    AlertDialog dialog;
    NavigationView navigationView;
    Toolbar toolbar;
    RecyclerView recyclerView;
    FirebaseDatabase database;
    DatabaseReference reference;

    FirebaseRecyclerAdapter<Notes, NotesViewHolder> adapter;
    FirebaseRecyclerAdapter<NotesTwo, NotesTwoViewHolder> adapter2;
    RecyclerView.LayoutManager manager;

    Button btn_add_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_screen);


        drawerLayout = findViewById(R.id.nav_drawer);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar_id);

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_draw_open, R.string.nav_draw_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //firebase init
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Notes");

        manager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(manager);

        FirebaseRecyclerOptions<Notes> options = new FirebaseRecyclerOptions.Builder<Notes>()
                .setQuery(reference,Notes.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Notes, NotesViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull NotesViewHolder categoryViewHolder, int i, @NonNull final Notes category) {

                categoryViewHolder.notesName.setText(category.getNotesName());
                FirebaseRecyclerOptions<NotesTwo> options2 = new FirebaseRecyclerOptions.Builder<NotesTwo>()
                        .setQuery(reference.child(category.getNotesId()).child("data"),NotesTwo.class)
                        .build();

                adapter2 = new FirebaseRecyclerAdapter<NotesTwo, NotesTwoViewHolder>(options2) {
                    @Override
                    protected void onBindViewHolder(@NonNull NotesTwoViewHolder categoryTwoViewHolder, int i, @NonNull final NotesTwo categoryTwo) {

                        categoryTwoViewHolder.dataName.setText(categoryTwo.getDataName());
                        categoryTwoViewHolder.img.setImageResource(R.drawable.year_card);

                        categoryTwoViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Open LinkActivity and pass categoryId and dataId
                                Intent intent = new Intent(NotesScreen.this, LinkActivity.class);
                                intent.putExtra("categoryId", category.getNotesId());
                                intent.putExtra("dataId", categoryTwo.getDataId());
                                intent.putExtra("refer","Notes");
                                startActivity(intent);

                            }
                        });



                    }

                    @NonNull
                    @Override
                    public NotesTwoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View v2 = LayoutInflater.from(getBaseContext())
                                .inflate(R.layout.item_view_two,parent,false);
                        return new NotesTwoViewHolder(v2);
                    }
                };
                adapter2.startListening();
                adapter2.notifyDataSetChanged();
                categoryViewHolder.notes_recyclerView.setAdapter(adapter2);
            }

            @NonNull
            @Override
            public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v1 = LayoutInflater.from(getBaseContext())
                        .inflate(R.layout.item_view_one,parent,false);
                return new NotesViewHolder(v1);
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
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
                Intent home_intent = new Intent(NotesScreen.this, home_screen.class);
                startActivity(home_intent);
                break;

            case R.id.nav_profile:
                Intent prof_intent = new Intent(NotesScreen.this, UserProfileActivity.class);
                startActivity(prof_intent);
                break;

            case R.id.nav_news:
                Intent news_intent = new Intent(NotesScreen.this, news_screen.class);
                startActivity(news_intent);
                break;

        }

        return true;
    }
}
