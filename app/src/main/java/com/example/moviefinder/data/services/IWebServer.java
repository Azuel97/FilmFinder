package com.example.moviefinder.data.services;

import com.example.moviefinder.data.models.Film;
import com.example.moviefinder.data.models.Films;
import com.example.moviefinder.data.models.Movie;
import com.example.moviefinder.data.models.Movies;

import java.util.List;

public interface IWebServer {

    void onFilmsFetched(boolean success, Films films, int errorCode, String errorMessage, List<Film> responseFilm);

    //void onVideoFetched(boolean success, Movies movies, int errorCode, String errorMessage, List<Movie> responseMovie);
}
