package com.aarsh.app.studysyncv1;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.ArrayList;

public class LinkAdapter extends RecyclerView.Adapter<LinkAdapter.MyViewHolder> {


    Context context;
    OnNoteListener mOnNoteListener;

    public LinkAdapter(Context context, ArrayList<LinkModel> list,OnNoteListener mOnNoteListener) {
        this.context = context;
        this.list = list;
        this.mOnNoteListener=mOnNoteListener;
    }

    ArrayList<LinkModel> list;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View v=LayoutInflater.from(context).inflate(R.layout.item_link,parent,false);
       return new MyViewHolder(v,mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            LinkModel linkModel=list.get(position);
            holder.filename.setText(linkModel.getLinkName());
        holder.setLinkModel(linkModel);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView filename;
        private OnNoteListener onNoteListener;
        private LinkModel linkModel;

        public MyViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);

            filename = itemView.findViewById(R.id.linkNameTextView);
            this.onNoteListener = onNoteListener;
            itemView.setOnClickListener(this);
        }

        public void setLinkModel(LinkModel linkModel) {
            this.linkModel = linkModel;
        }

        @Override
        public void onClick(View view) {
            onNoteListener.onNoteClick(linkModel);
        }
    }
public interface OnNoteListener
{
    void onNoteClick(LinkModel linkModel);
}


}
