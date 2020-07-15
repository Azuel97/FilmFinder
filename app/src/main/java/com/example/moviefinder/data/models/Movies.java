package com.example.moviefinder.data.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Movies {

    @SerializedName("id")
    private int id;
    @SerializedName("results")
    private List<Movie> results;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Movie> getResults() {
        return results;
    }

    public void setResults(List<Movie> results) {
        this.results = results;
    }
}

