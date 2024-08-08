package com.aarsh.app.studysyncv1;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



public class NotesViewHolder extends RecyclerView.ViewHolder {

    public TextView notesName;
    public RecyclerView notes_recyclerView;
    public RecyclerView.LayoutManager manager;


    public NotesViewHolder(@NonNull View itemView) {
        super(itemView);

        manager = new LinearLayoutManager(itemView.getContext(),LinearLayoutManager.HORIZONTAL,false);
        notesName = itemView.findViewById(R.id.category_name);
        notes_recyclerView = itemView.findViewById(R.id.recyclerView);
        notes_recyclerView.setLayoutManager(manager);
    }
}