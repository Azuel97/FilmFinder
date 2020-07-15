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

public class FavoriteActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, ConfirmDialogFragmentListener {

    Toolbar toolbar;
    private static final int MY_ID = 3;

    ListView listView;
    FavoriteAdapter favoriteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Film Preferiti");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        listView = findViewById(R.id.listViewFavorite);
        favoriteAdapter = new FavoriteAdapter(this, null);
        listView.setAdapter(favoriteAdapter);
        getSupportLoaderManager().initLoader(MY_ID, null, this);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                ConfirmDialogFragment dialogFragment = new ConfirmDialogFragment("Cancella film preferito",
                        "Sei sicuro di volere eliminare il film ?",
                        id);
                dialogFragment.show(fragmentManager, ConfirmDialogFragment.class.getName());
                return true;
            }
        });

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(this, FilmProvider.FAVORITES_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        favoriteAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        favoriteAdapter.changeCursor(null);
    }

    @Override
    public void onPositivePressed(long id) {
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
            if (deletedRows > 0) {
                Toast.makeText(FavoriteActivity.this,"Film eliminato con successo", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(FavoriteActivity.this,"Qualcosa è andato storto durante la cancellazione", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(FavoriteActivity.this,"ERRORE", Toast.LENGTH_SHORT).show();
        }
    }
}
