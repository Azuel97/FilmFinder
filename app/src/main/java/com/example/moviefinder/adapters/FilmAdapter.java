package com.example.moviefinder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.moviefinder.R;
import com.example.moviefinder.data.models.Film;

import java.util.ArrayList;
import java.util.List;

public class FilmAdapter extends BaseAdapter {

    private Context context;
    private List<Film> films = new ArrayList<>();

    public FilmAdapter(Context context) {
        this.context = context;
    }

    public void setFilms(List<Film> films) {
        this.films = films;
    }

    @Override
    public int getCount() {
        return films.size();
    }

    @Override
    public Object getItem(int position) {
        if (position >= films.size())
            return null;
        return films.get(position);
    }

    @Override
    public long getItemId(int position) {
        if (position >= films.size())
            return 0;
        return films.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.film_cell, parent, false);
        }

        final Film currentFilm = films.get(position);

        ImageView imageView = convertView.findViewById(R.id.imageFilm);

        // Glide for image
        Glide.with(context)
                .load("https://image.tmdb.org/t/p/w500/"+currentFilm.getPosterPath())
                .into(imageView);

        return convertView;
    }
}
