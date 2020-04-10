package com.example.moviefinder.data.services;

import com.example.moviefinder.data.models.Film;
import com.example.moviefinder.data.models.Films;

import java.util.List;

public interface IWebServer {

    void onFilmsFetched(boolean success, Films films, int errorCode, String errorMessage, List<Film> responseFilm);
}
