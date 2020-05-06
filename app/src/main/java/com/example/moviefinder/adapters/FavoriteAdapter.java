package com.example.moviefinder.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.moviefinder.R;
import com.example.moviefinder.data.models.Film;
import com.example.moviefinder.database.FavoriteTableHelper;

import java.util.ArrayList;
import java.util.List;

public class FavoriteAdapter extends CursorAdapter{

    public FavoriteAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater vInflater = LayoutInflater.from(context);
        View vView = vInflater.inflate(R.layout.film_cell, null);
        return vView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imageView = view.findViewById(R.id.imageFilm);

        // Glide for image
        Glide.with(context)
                .load("https://image.tmdb.org/t/p/w500/"+cursor.getString(cursor.getColumnIndex(FavoriteTableHelper.POSTER_PATH)))
                .into(imageView);
    }

}
