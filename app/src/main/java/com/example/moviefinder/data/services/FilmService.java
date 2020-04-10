package com.example.moviefinder.data.services;

import com.example.moviefinder.data.models.Film;
import com.example.moviefinder.data.models.Films;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface FilmService {

    @GET("movie/now_playing?api_key=6710ff6073915867f8a6b472ffbd9235&language=it-IT&page=1")
    Call<Films> getFilms();
}
