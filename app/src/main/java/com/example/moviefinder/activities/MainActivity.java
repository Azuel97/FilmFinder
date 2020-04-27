package com.example.moviefinder.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
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
import com.example.moviefinder.database.FilmProvider;
import com.example.moviefinder.database.FilmTableHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FilmAdapterRecycler.OnFilmListener {

    private ProgressBar loadingBar;

    private RecyclerView recyclerView;
    private FilmAdapterRecycler adapterRecycler;

    private WebService webService;
    private IWebServer webServerListener = new IWebServer() {
        @Override
        public void onFilmsFetched(boolean success, Films films, int errorCode, String errorMessage, List<Film> responseFilm) {
            // Se il fetch dei dati va a buon fine
            if (success) {
                // Eseguo query, che ritorna l'intera tabella del db
                Cursor cursor = getContentResolver().query(FilmProvider.FILMS_URI, null, null, null,null);
                // Se ritorna 0, allora è vuota, quindi deve popolare il db
                if (cursor.getCount() == 0) {
                    Log.d("PROVA", "SAVE DB");
                    saveDataOnDB(responseFilm);
                    fetchDatiDatabase();
                } else {
                    Log.d("PROVA", "READ DB");
                    fetchDatiDatabase();
                }
                // Toast.makeText(MainActivity.this,"E' andato tutto bene", Toast.LENGTH_SHORT).show();
            } else {
                fetchDatiDatabase();
                Toast.makeText(MainActivity.this,"OFFLINE : " + errorMessage, Toast.LENGTH_SHORT).show();
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

        loadFilms();
    }

    // Carico i film
    private void loadFilms() {
        loadingBar.setVisibility(View.VISIBLE);
        webService.getFilms(webServerListener,1);
    }

    // Gestione del click sulla locandina del film nella lista
    @Override
    public void onFilmCLick(int position) {
        Intent intent = new Intent(this,DetailMovieActivity.class);
        intent.putExtra("ID", position+1);
        startActivity(intent);
    }

    private void saveDataOnDB(List<Film> responseFilm) {
        ContentValues values = new ContentValues();
        for (int i = 0; i < responseFilm.size(); i++) {
            values.put(FilmTableHelper.TITLE, responseFilm.get(i).getTitle());
            values.put(FilmTableHelper.DESCRIPTION, responseFilm.get(i).getOverview());
            values.put(FilmTableHelper.POSTER_PATH, responseFilm.get(i).getPosterPath());
            values.put(FilmTableHelper.BACKDROP_PATH, responseFilm.get(i).getBackdropPath());
            getContentResolver().insert(FilmProvider.FILMS_URI, values);
        }
    }

    private void fetchDatiDatabase() {
        // Se il fetch dei dati mi ritorna un errore allora esegua query sul db per recuperare tutti i flim dal db interno
        Cursor mCursor = getContentResolver().query(FilmProvider.FILMS_URI, null, null, null,null);
        List<Film> films1 = new ArrayList<>();
        for(mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
            // The Cursor is now set to the right position
            films1.add(new Film(mCursor.getString(mCursor.getColumnIndex(FilmTableHelper.POSTER_PATH))));
        }
        // Setto la recyclerview con i film recuperati dal database
        adapterRecycler.setFilms(films1);
        adapterRecycler.notifyDataSetChanged();
        loadingBar.setVisibility(View.GONE);
    }

}
