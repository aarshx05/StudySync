package com.aarsh.app.studysyncv1;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class CategoryTwoViewHolder extends RecyclerView.ViewHolder  {

    public TextView dataName;

    public ImageView img;

    public CategoryTwoViewHolder(@NonNull final View itemView) {
        super(itemView);

        dataName = itemView.findViewById(R.id.data_name);
        img=itemView.findViewById(R.id.imageView);

    }



}
