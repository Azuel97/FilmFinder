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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.moviefinder.R;
import com.example.moviefinder.data.models.Film;
import com.example.moviefinder.data.models.Films;
import com.example.moviefinder.data.models.Movie;
import com.example.moviefinder.data.models.Movies;
import com.example.moviefinder.data.services.IWebServer;
import com.example.moviefinder.data.services.WebService;
import com.example.moviefinder.database.FilmProvider;
import com.example.moviefinder.database.FilmTableHelper;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.List;

public class DetailMovieActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    Toolbar toolbar;
    YouTubePlayerView youTubePlayerView;
    String urlVideo;

    private WebService webService;
    //private IVideoService videoServiceListener;
    private IWebServer webServerListener = new IWebServer() {
        @Override
        public void onFilmsFetched(boolean success, Films films, int errorCode, String errorMessage, List<Film> responseFilm) {
            if (success){
                Log.d("AAAAA", "onVideoFetched: " + responseFilm.get(0).getTitle());
            }else {
                Log.d("AAAAA", "ERRORE");
            }
        }

        @Override
        public void onVideoFetched(boolean success, Movies movies, int errorCode, String errorMessage, List<Movie> responseMovie) {
            if (success){
                if (responseMovie.isEmpty()) {
                    urlVideo = "1Clu22DnUXk";
                } else {
                    urlVideo = responseMovie.get(0).getKey();
                }
            }else {
                Log.d("AAAAA", "ERRORE");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Dettaglio Film");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));


        webService = WebService.getInstance();
        youTubePlayerView = findViewById(R.id.youtube_player_view);
        youTubePlayerView.initialize(getString(R.string.youtube_data_api_key), this);


        // Recupero dall'intent ricevuto dalla mainactivity
        Intent intent = getIntent();
        int id = intent.getExtras().getInt("ID");
        Log.d("BBB", "onCreate: " + id);

        // Eseguo una ricerca tramite l'id all'interno del db per recuperare le informazioni del film
        Cursor cursor = getContentResolver().query(FilmProvider.FILMS_URI, null, FilmTableHelper.FILM_ID + " = " + id, null,null);
        cursor.moveToNext();

        webService.getVideo(webServerListener,cursor.getInt(cursor.getColumnIndex(FilmTableHelper.FILM_ID)));

        TextView textViewTitle = findViewById(R.id.text_Title);
        textViewTitle.setText(cursor.getString(cursor.getColumnIndex(FilmTableHelper.TITLE)));

        TextView textViewDescription = findViewById(R.id.textViewDescription);
        if(cursor.getString(cursor.getColumnIndex(FilmTableHelper.DESCRIPTION)).equals("")) {
            textViewDescription.setText("Nessuna descrizione");
        }else {
            textViewDescription.setText(cursor.getString(cursor.getColumnIndex(FilmTableHelper.DESCRIPTION)));
        }

        ImageView imageView = findViewById(R.id.imageViewDetail);
        // Glide for image
        Glide.with(this)
                .load("https://image.tmdb.org/t/p/w500/"+cursor.getString(cursor.getColumnIndex(FilmTableHelper.BACKDROP_PATH)))
                .placeholder(R.drawable.image_placeholder)
                .into(imageView);

        ImageView imageView1 = findViewById(R.id.imageView1);
        // Glide for image
        Glide.with(this)
                .load("https://image.tmdb.org/t/p/w500/"+cursor.getString(cursor.getColumnIndex(FilmTableHelper.POSTER_PATH)))
                .placeholder(R.drawable.image_placeholder)
                .into(imageView1);

        TextView relaseDate = findViewById(R.id.textViewRelaseDate);
        if(cursor.getString(cursor.getColumnIndex(FilmTableHelper.RELASE_DATE)).equals("")) {
            relaseDate.setText("Nessuna data");
        } else {
            relaseDate.setText(String.format("%s", cursor.getString(cursor.getColumnIndex(FilmTableHelper.RELASE_DATE))));
        }

        TextView voteAverage = findViewById(R.id.textViewVote);
        if (cursor.getString(cursor.getColumnIndex(FilmTableHelper.VOTE_AVERAGE)).equals("")) {
            voteAverage.setText("Nessun voto");
        } else {
            voteAverage.setText(String.format("%s/10", cursor.getString(cursor.getColumnIndex(FilmTableHelper.VOTE_AVERAGE))));
        }
    }

    // Inizializzazione del player di youtube
    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if (!b) {
            if (urlVideo == null) {
                urlVideo = "1Clu22DnUXk";
            }
            youTubePlayer.cueVideo(urlVideo);
        }
    }

    // In caso di errore durante l'inizializzazione del player di youtube
    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(this, "Failed to initialize YouTube Video Player.", Toast.LENGTH_LONG).show();
    }
}
