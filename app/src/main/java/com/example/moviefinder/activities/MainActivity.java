package com.example.moviefinder.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.moviefinder.R;
import com.example.moviefinder.adapters.FavoriteAdapter;
import com.example.moviefinder.adapters.FilmAdapterRecycler;
import com.example.moviefinder.data.models.Film;
import com.example.moviefinder.data.models.Films;
import com.example.moviefinder.data.models.Movie;
import com.example.moviefinder.data.models.Movies;
import com.example.moviefinder.data.services.IVideoService;
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

    Boolean isScrolling = false;
    int currentItems, totalItems, scrollOutItems;
    GridLayoutManager manager;
    int page = 1;

    Toolbar toolbar;
    ConstraintLayout overlay;

    private ProgressBar loadingBar;

    private RecyclerView recyclerView;
    private FilmAdapterRecycler adapterRecycler;

    private WebService webService;
    //private IVideoService videoServiceListener;
    private IWebServer webServerListener = new IWebServer() {
        @Override
        public void onFilmsFetched(boolean success, Films films, int errorCode, String errorMessage, List<Film> responseFilm) {
            // Se il fetch dei dati va a buon fine
            if (success) {
                 Log.d("PROVA", "onFilmsFetched: " + films.getPage());
                saveDataOnDB(responseFilm);
                fetchDatiDatabase();

                /*// Eseguo query, che ritorna l'intera tabella del db
                Cursor cursor = getContentResolver().query(FilmProvider.FILMS_URI, null, null, null,null);
                // Se ritorna 0, allora è vuota, quindi deve popolare il db
                if (cursor.getCount() == 0) {
                    Log.d("PROVA", "SAVE DB");
                    saveDataOnDB(responseFilm);
                    fetchDatiDatabase();
                } else {
                    Log.d("PROVA", "READ DB");

                    // TODO: da modificare per visualizzare correttamente
                    saveDataOnDB(responseFilm);

                    fetchDatiDatabase();
                }*/
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
        toolbar.setTitle("Movies Finder ");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        webService = WebService.getInstance();
        //searchVideo();

        loadingBar = findViewById(R.id.loading_bar);
        recyclerView = findViewById(R.id.film_recycler);

        overlay = findViewById(R.id.mainActivityOverlayView);

        adapterRecycler = new FilmAdapterRecycler(this,this);

        // Controllo l'orientamento dello schermo
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            // Se è portrait, allora uso 2 colonne
            manager = new GridLayoutManager(this,2);
        }
        else{
            // Se è landscape, allore uso 3 colonne
            manager = new GridLayoutManager(this,3);
        }

        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapterRecycler);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = manager.getChildCount();
                totalItems = manager.getItemCount();
                scrollOutItems = manager.findFirstVisibleItemPosition();

                if (isScrolling && (currentItems + scrollOutItems ==  totalItems)) {
                    isScrolling = false;
                    loadFilmsScrool();
                }
            }
        });

        loadFilms();
    }

    /*private void searchVideo() {
        videoServiceListener = new IVideoService() {
            @Override
            public void onVideoFetched(boolean success, Movies movies, int errorCode, String errorMessage, List<Movie> responseMovie) {
                if (success){
                    Log.d("AAAAA", "onVideoFetched: " + responseMovie.get(0).getName());
                }else {
                    Log.d("AAAAA", "ERRORE");
                }
            }
        };
        webService.getVideo(videoServiceListener,22);
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        MenuItem mSearch = menu.findItem(R.id.search);
        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setQueryHint("Search");

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Cursor mCursor = getContentResolver().query(FilmProvider.FILMS_URI, null, FilmTableHelper.TITLE + "=?", new String[]{String.valueOf(query)}, null);
                mCursor.moveToFirst();
                if (mCursor.getCount() == 0) {
                    Toast.makeText(MainActivity.this,"Film sconosciuto", Toast.LENGTH_SHORT).show();
                    fetchDatiDatabase();
                } else {
                    Log.d("PROVA", "onQueryTextSubmit: " + mCursor.getString(mCursor.getColumnIndex(FilmTableHelper.TITLE)));
                    List<Film> films1 = new ArrayList<>();
                    for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
                        // The Cursor is now set to the right position
                        films1.add(new Film(mCursor.getString(mCursor.getColumnIndex(FilmTableHelper.POSTER_PATH))));
                    }
                    // Setto la recyclerview con i film recuperati dal database
                    adapterRecycler.setFilms(films1);
                    adapterRecycler.notifyDataSetChanged();
                    loadingBar.setVisibility(View.GONE);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

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
        //loadingBar.setVisibility(View.VISIBLE);
        overlay.setVisibility(View.VISIBLE);
        webService.getFilms(webServerListener,page);
        page++;
    }

    // Carico i film
    private void loadFilmsScrool() {
        //loadingBar.setVisibility(View.VISIBLE);
        overlay.setVisibility(View.VISIBLE);
        Toast.makeText(MainActivity.this,"Caricamento film...", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    webService.getFilms(webServerListener,page);
                    adapterRecycler.notifyDataSetChanged();
                    page++;
                }
            },2000);

    }

    // Gestione del click sulla locandina del film nella lista
    @Override
    public void onFilmCLick(int position, ImageView image) {
        Intent intent = new Intent(this,DetailMovieActivity.class);
        intent.putExtra("ID", position+1);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, image,"example_transition");
        startActivity(intent,options.toBundle());
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
            values.put(FilmTableHelper.VOTE_AVERAGE, responseFilm.get(i).getVoteAverage());
            values.put(FilmTableHelper.RELASE_DATE, responseFilm.get(i).getReleaseDate());
            values.put(FilmTableHelper.FILM_ID, responseFilm.get(i).getId());
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
        //loadingBar.setVisibility(View.GONE);
        overlay.setVisibility(View.GONE);
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
