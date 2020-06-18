package com.example.moviefinder.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.moviefinder.R;
import com.example.moviefinder.adapters.FavoriteAdapter;
import com.example.moviefinder.adapters.FilmAdapterRecycler;
import com.example.moviefinder.data.models.Film;
import com.example.moviefinder.database.FavoriteTableHelper;
import com.example.moviefinder.database.FilmProvider;
import com.example.moviefinder.database.FilmTableHelper;
import com.example.moviefinder.fragments.ConfirmDialogFragment;
import com.example.moviefinder.fragments.ConfirmDialogFragmentListener;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity implements ConfirmDialogFragmentListener, FavoriteAdapter.OnFilmListener {

    Toolbar toolbar;
    private static final int MY_ID = 3;

    ListView listView;
    FavoriteAdapter favoriteAdapter;

    GridLayoutManager manager;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Favorites Movie");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        recyclerView = findViewById(R.id.favorite_recycler);
        favoriteAdapter = new FavoriteAdapter(this,this);

        // Controllo l'orientamento dello schermo
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            // Se è portrait, allora uso 2 colonne
            manager = new GridLayoutManager(this,2);
        }
        else{
            // Se è landscape, allore uso 3 colonne
            manager = new GridLayoutManager(this,3);
        }

        //manager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(favoriteAdapter);

        fetchDatiDatabase();

    }

    @Override
    public void onPositivePressed(long id) {
        //Toast.makeText(this,"Operazione confermata",Toast.LENGTH_LONG).show();
        deleteFilm(id);
    }

    @Override
    public void onNegativePressed() {
        Toast.makeText(this,"Operazione annullata",Toast.LENGTH_LONG).show();
    }

    private void deleteFilm(long filmId) {
        Log.d("PROVA", "ID: " + filmId);
        if (filmId > 0) {
            String whereClause = FavoriteTableHelper._ID + "=?";
            String[] whereArgs = new String[] { String.valueOf(filmId) };
            int deletedRows = getContentResolver().delete(FilmProvider.FAVORITES_URI, whereClause, whereArgs);
            Log.d("PROVA", "deleteFilm: " + deletedRows);
            if (deletedRows > 0) {
                Toast.makeText(FavoriteActivity.this, "Eliminato con successo", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(FavoriteActivity.this, "Errore durante la cancellazione", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(FavoriteActivity.this, "Errore", Toast.LENGTH_SHORT).show();
        }
    }

    // Fetch dei dati nel database
    private void fetchDatiDatabase() {
        // Se il fetch dei dati mi ritorna un errore allora esegua query sul db per recuperare tutti i flim dal db interno
        Cursor mCursor = getContentResolver().query(FilmProvider.FAVORITES_URI, null, null, null,null);
        List<Film> films1 = new ArrayList<>();
        for(mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
            // The Cursor is now set to the right position
            films1.add(new Film(mCursor.getString(mCursor.getColumnIndex(FilmTableHelper.POSTER_PATH))));
        }
        // Setto la recyclerview con i film recuperati dal database
        favoriteAdapter.setFilms(films1);
        favoriteAdapter.notifyDataSetChanged();
        //loadingBar.setVisibility(View.GONE);
    }

    @Override
    public void onLongFilmClick(int position) {
        int idFilm = position + 1;
        Cursor mCursor = getContentResolver().query(FilmProvider.FAVORITES_URI, null, FavoriteTableHelper._ID + " =" + idFilm, null,null);
        mCursor.moveToFirst();
        String titoloFilm = mCursor.getString(mCursor.getColumnIndex(FilmTableHelper.TITLE));

        FragmentManager fragmentManager = getSupportFragmentManager();
        ConfirmDialogFragment dialogFragment = new ConfirmDialogFragment("Rimuovi dai Preferiti",
                "Sei sicuro di volere rimuovere il film " + titoloFilm + " dai preferiti ?",
                position);
        dialogFragment.show(fragmentManager, ConfirmDialogFragment.class.getName());
    }
}
