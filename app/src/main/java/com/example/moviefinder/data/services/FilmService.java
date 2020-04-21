package com.example.moviefinder.data.services;

import com.example.moviefinder.data.models.Film;
import com.example.moviefinder.data.models.Films;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FilmService {

    // ?api_key=6710ff6073915867f8a6b472ffbd9235&language=it-IT&page=1
    @GET("movie/now_playing")
    Call<Films> getFilms(@Query("api_key") String apiKey, @Query("language") String lang, @Query("page") String page);
}
