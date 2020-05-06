package com.example.moviefinder.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.moviefinder.R;
import com.example.moviefinder.adapters.FavoriteAdapter;
import com.example.moviefinder.adapters.FilmAdapterRecycler;
import com.example.moviefinder.data.models.Film;
import com.example.moviefinder.data.models.Films;
import com.example.moviefinder.data.services.IWebServer;
import com.example.moviefinder.data.services.WebService;
import com.example.moviefinder.database.FavoriteTableHelper;
import com.example.moviefinder.database.FilmProvider;
import com.example.moviefinder.database.FilmTableHelper;
import com.example.moviefinder.fragments.ConfirmDialogFragment;
import com.example.moviefinder.fragments.ConfirmDialogFragmentListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FilmAdapterRecycler.OnFilmListener, ConfirmDialogFragmentListener {

    Toolbar toolbar;

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

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Movies");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        webService = WebService.getInstance();

        loadingBar = findViewById(R.id.loading_bar);
        recyclerView = findViewById(R.id.film_recycler);

        adapterRecycler = new FilmAdapterRecycler(this,this);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setAdapter(adapterRecycler);

        loadFilms();
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

    // Gestione del longClick sulla Locandina del film nella lista
    @Override
    public void onLongFilmClick(final int position) {
        int id = position + 1;
        Cursor mCursor = getContentResolver().query(FilmProvider.FILMS_URI, null, FilmTableHelper._ID + " =" + id, null,null);
        mCursor.moveToFirst();
        String titoloFilm = mCursor.getString(mCursor.getColumnIndex(FilmTableHelper.TITLE));

        FragmentManager fragmentManager = getSupportFragmentManager();
        ConfirmDialogFragment dialogFragment = new ConfirmDialogFragment("Aggiungi ai Preferiti",
                "Sei sicuro di volere aggiungere il film " +  titoloFilm + " ai preferiti ?",
                position);
        dialogFragment.show(fragmentManager, ConfirmDialogFragment.class.getName());
    }

    // Salvo i film nel database
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

    // Fetch dei dati nel database
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

    // Click su conferma di aggiungere il film ai preferiti
    @Override
    public void onPositivePressed(long id) {
        Toast.makeText(this,"Operazione confermata",Toast.LENGTH_LONG).show();
        insertUser(id);
    }

    // Click su annula aggiunta del fiml ai preferiti
    @Override
    public void onNegativePressed() {
        Toast.makeText(this,"Operazione annullata",Toast.LENGTH_LONG).show();
    }

    private void insertUser(long id) {
        long idFilm = id + 1;
        // Eseguo una ricerca tramite l'id all'interno del db per recuperare le informazioni del film
        Cursor cursor = getContentResolver().query(FilmProvider.FILMS_URI, null, FilmTableHelper._ID + " = " + idFilm, null,null);
        cursor.moveToNext();

        ContentValues values = new ContentValues();
        values.put(FavoriteTableHelper.TITLE, cursor.getString(cursor.getColumnIndex(FilmTableHelper.TITLE)));
        values.put(FavoriteTableHelper.DESCRIPTION, cursor.getString(cursor.getColumnIndex(FilmTableHelper.DESCRIPTION)));
        values.put(FavoriteTableHelper.POSTER_PATH, cursor.getString(cursor.getColumnIndex(FilmTableHelper.POSTER_PATH)));
        values.put(FavoriteTableHelper.BACKDROP_PATH, cursor.getString(cursor.getColumnIndex(FilmTableHelper.BACKDROP_PATH)));
        getContentResolver().insert(FilmProvider.FAVORITES_URI, values);
    }
}
