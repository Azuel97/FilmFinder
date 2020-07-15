package com.example.moviefinder.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.moviefinder.R;
import com.example.moviefinder.data.models.Film;
import com.example.moviefinder.database.FavoriteTableHelper;
import com.example.moviefinder.database.FilmProvider;
import com.example.moviefinder.database.FilmTableHelper;

import java.util.ArrayList;
import java.util.List;

public class FavoriteAdapter extends CursorAdapter {


    public FavoriteAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater vInflater = LayoutInflater.from(context);
        View vView = vInflater.inflate(R.layout.film_cell_favorite, null);
        return vView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int idFilm = cursor.getInt(cursor.getColumnIndex(FavoriteTableHelper.ID_FILM));
        // Eseguo una ricerca tramite l'id all'interno del db per recuperare le informazioni del film
        Cursor cursor1 = context.getContentResolver().query(FilmProvider.FILMS_URI, null, FilmTableHelper.FILM_ID + " = " + idFilm, null,null);
        cursor1.moveToNext();

        ImageView imageView = view.findViewById(R.id.imageFilm);
        TextView titolo = view.findViewById(R.id.textViewTitoloPreferito);

        // Glide for image
        Glide.with(context)
                .load("https://image.tmdb.org/t/p/w500/"+cursor1.getString(cursor1.getColumnIndex(FilmTableHelper.POSTER_PATH)))
                .into(imageView);

        titolo.setText(cursor1.getString(cursor1.getColumnIndex(FilmTableHelper.TITLE)));
    }

    /*private Context context;
    private List<Film> films = new ArrayList<>();
    private OnFilmListener mOnFilmListener;

    public FavoriteAdapter(Context context, OnFilmListener onFilmListener) {
        this.context = context;
        this.mOnFilmListener = onFilmListener;
    }

    public void setFilms(List<Film> films)  {
        this.films = films;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(context);
        view = mInflater.inflate(R.layout.film_cell,parent,false);

        return new MyViewHolder(view, mOnFilmListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Film currentFilm = films.get(position);

        // Glide for image
        Glide.with(context)
                .load("https://image.tmdb.org/t/p/w500/"+currentFilm.getPosterPath())
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return films.size();
    }

    // Classe ViewHolder
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        ImageView image;
        OnFilmListener onFilmListener;

        public MyViewHolder(@NonNull View itemView, OnFilmListener onFilmListener) {
            super(itemView);
            this.onFilmListener = onFilmListener;
            image = itemView.findViewById(R.id.imageFilm);

            itemView.setOnLongClickListener(this);
        }


        @Override
        public boolean onLongClick(View v) {
            onFilmListener.onLongFilmClick(getAdapterPosition());
            return true;
        }
    }


    // Interface per il clickListener
    public interface OnFilmListener {
        void onLongFilmClick(int position);
    }*/

}
