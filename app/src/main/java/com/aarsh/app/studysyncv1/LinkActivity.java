package com.aarsh.app.studysyncv1;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LinkActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,LinkAdapter.OnNoteListener {
    String catId, dId;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    RecyclerView recyclerView;
    FirebaseDatabase database;
    DatabaseReference reference;

    RecyclerView.LayoutManager manager;
    LinkAdapter adapter;

    ArrayList<LinkModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link);

        Intent intent = getIntent();
        catId = intent.getStringExtra("categoryId");
        dId = intent.getStringExtra("dataId");

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Category").child(catId).child("data").child(dId).child("links");



        drawerLayout = findViewById(R.id.nav_drawer);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar_id);

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_draw_open, R.string.nav_draw_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

         recyclerView=findViewById(R.id.linksRecyclerView);
         recyclerView.setHasFixedSize(true);
         recyclerView.setLayoutManager(new LinearLayoutManager(this));

         list = new ArrayList<>();
         adapter=new LinkAdapter(this,list,this);
         recyclerView.setAdapter(adapter);

         reference.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        LinkModel link=dataSnapshot.getValue(LinkModel.class);
                        list.add(link);
                 }

                 adapter.notifyDataSetChanged();
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });





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
                Intent home_intent = new Intent(LinkActivity.this, home_screen.class);
                startActivity(home_intent);
                break;

            case R.id.nav_profile:
                Intent prof_intent = new Intent(LinkActivity.this, UserProfileActivity.class);
                startActivity(prof_intent);
                break;

            case R.id.nav_news:
                Intent news_intent = new Intent(LinkActivity.this, news_screen.class);
                startActivity(news_intent);
                break;
        }
        return true;
    }

    @Override
    public void onNoteClick(LinkModel linkModel) {
        Log.d("LinkActivity", "Item clicked. Link: " + linkModel.getLink());

        Log.d("LinkActivity", "Item clicked. Link: " + linkModel.getLink());

        // Open link in WebViewActivity
        Intent intent = new Intent(LinkActivity.this, WebViewActivity.class);
        intent.putExtra("link", linkModel.getLink());
        startActivity(intent);
    }


}
