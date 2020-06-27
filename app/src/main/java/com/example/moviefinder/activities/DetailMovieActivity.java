package com.example.moviefinder.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.moviefinder.R;
import com.example.moviefinder.database.FilmProvider;
import com.example.moviefinder.database.FilmTableHelper;

public class DetailMovieActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Details Movie");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



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
                .placeholder(new ColorDrawable(Color.rgb(192, 196, 193)))
                .into(imageView);

        ImageView imageView1 = findViewById(R.id.imageView1);
        // Glide for image
        Glide.with(this)
                .load("https://image.tmdb.org/t/p/w500/"+cursor.getString(cursor.getColumnIndex(FilmTableHelper.POSTER_PATH)))
                .placeholder(new ColorDrawable(Color.rgb(192, 196, 193)))
                .into(imageView1);

        TextView relaseDate = findViewById(R.id.textViewRelaseDate);
        //relaseDate.setText(cursor.getString(cursor.getColumnIndex(FilmTableHelper.RELASE_DATE)));
        relaseDate.setText(String.format("%s",cursor.getString(cursor.getColumnIndex(FilmTableHelper.RELASE_DATE))));

        TextView voteAverage = findViewById(R.id.textViewVote);
        //voteAverage.setText(cursor.getString(cursor.getColumnIndex(FilmTableHelper.VOTE_AVERAGE)));
        voteAverage.setText(String.format("%s/10",cursor.getString(cursor.getColumnIndex(FilmTableHelper.VOTE_AVERAGE))));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favorite:
                Toast.makeText(this,"Favoriti",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this,FavoriteActivity.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
