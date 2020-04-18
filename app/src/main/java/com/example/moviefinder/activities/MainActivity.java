package com.example.moviefinder.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.moviefinder.R;
import com.example.moviefinder.adapters.FilmAdapterRecycler;
import com.example.moviefinder.data.models.Film;
import com.example.moviefinder.data.models.Films;
import com.example.moviefinder.data.services.IWebServer;
import com.example.moviefinder.data.services.WebService;
import com.example.moviefinder.database.FilmTableHelper;

import java.util.List;

public class MainActivity extends AppCompatActivity implements FilmAdapterRecycler.OnFilmListener {

    private ProgressBar loadingBar;

    private RecyclerView recyclerView;
    private FilmAdapterRecycler adapterRecycler;

    private WebService webService;
    private IWebServer webServerListener = new IWebServer() {
        @Override
        public void onFilmsFetched(boolean success, Films films, int errorCode, String errorMessage, List<Film> responseFilm) {
            if (success) {
                adapterRecycler.setFilms(responseFilm);
                adapterRecycler.notifyDataSetChanged();
                loadingBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this,"E' andato tutto bene", Toast.LENGTH_SHORT).show();
            } else {
                loadingBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this,"Qualcosa è andato storto : " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Movies");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1976D2")));

        webService = WebService.getInstance();

        loadingBar = findViewById(R.id.loading_bar);
        recyclerView = findViewById(R.id.film_recycler);

        adapterRecycler = new FilmAdapterRecycler(this,this);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setAdapter(adapterRecycler);


        loadTodos();

    }

    private void loadTodos() {
        loadingBar.setVisibility(View.VISIBLE);
        webService.getFilms(webServerListener);
    }

    @Override
    public void onFilmCLick(int position) {
        Log.d("PROVA", "Film : " + position);

       /* Intent intent = new Intent(this,DetailMovieActivity.class);
        intent.putExtra("Title", film.getTitle());
        intent.putExtra("Image", film.getBackdropPath());
        intent.putExtra("Description",film.getOverview());
        startActivity(intent);*/
    }
}
