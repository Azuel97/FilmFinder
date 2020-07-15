package com.example.moviefinder.data.services;

import com.example.moviefinder.data.models.Movie;
import com.example.moviefinder.data.models.Movies;

import java.util.List;

public interface IVideoService {

    void onVideoFetched(boolean success, Movies movies, int errorCode, String errorMessage, List<Movie> responseMovie);

}
