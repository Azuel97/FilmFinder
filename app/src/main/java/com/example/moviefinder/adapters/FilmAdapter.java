package com.example.moviefinder.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.moviefinder.DetailMovieActivity;
import com.example.moviefinder.MainActivity;
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

        //TextView filmTitle = convertView.findViewById(R.id.textViewTitle);
        //TextView relaseDate = convertView.findViewById(R.id.textViewRelaseDate);
        ImageView imageView = convertView.findViewById(R.id.imageFilm);

        /*
        GridView gridView = convertView.findViewById(R.id.film_grid);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(context,"Single click", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, DetailMovieActivity.class);
                intent.putExtra("Title",currentFilm.getTitle());
                context.startActivity(intent);
            }
        }); */

        //filmTitle.setText(currentFilm.getTitle());
        //relaseDate.setText(currentFilm.getReleaseDate());

        // Glide for image
        Glide.with(context)
                .load("https://image.tmdb.org/t/p/w500/"+currentFilm.getPosterPath())
                .into(imageView);

        return convertView;
    }
}
