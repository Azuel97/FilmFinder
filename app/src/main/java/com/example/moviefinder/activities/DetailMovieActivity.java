package com.example.moviefinder.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.moviefinder.R;
import com.example.moviefinder.database.FilmProvider;
import com.example.moviefinder.database.FilmTableHelper;

public class DetailMovieActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);
        setTitle("Details Movie");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1976D2")));

        // Recupero dall'intent ricevuto dalla mainactivity
        Intent intent = getIntent();
        int id = intent.getExtras().getInt("ID");

        // Eseguo una ricerca tramite l'id all'interno del db per recuperare le informazioni del film
        Cursor cursor = getContentResolver().query(FilmProvider.FILMS_URI, null, FilmTableHelper._ID + " = " + id, null,null);
        cursor.moveToNext();

        TextView textViewTitle = findViewById(R.id.text_Title);
        textViewTitle.setText(cursor.getString(cursor.getColumnIndex(FilmTableHelper.TITLE)));

        TextView textViewDescription = findViewById(R.id.textViewDescription);
        textViewDescription.setText(cursor.getString(cursor.getColumnIndex(FilmTableHelper.DESCRIPTION)));

        ImageView imageView = findViewById(R.id.imageViewDetail);
        // Glide for image
        Glide.with(this)
                .load("https://image.tmdb.org/t/p/w500/"+cursor.getString(cursor.getColumnIndex(FilmTableHelper.BACKDROP_PATH)))
                .placeholder(new ColorDrawable(Color.BLUE))
                .into(imageView);
    }
}
