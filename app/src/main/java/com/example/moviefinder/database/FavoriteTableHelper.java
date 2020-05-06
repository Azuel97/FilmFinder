package com.example.moviefinder.database;

import android.provider.BaseColumns;

public class FavoriteTableHelper implements BaseColumns {

    public static final String TABLE_NAME = "favorites";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String POSTER_PATH = "poster_path";
    public static final String BACKDROP_PATH = "backdrop_path";

    public static final String CREATE = "CREATE TABLE " + TABLE_NAME + " ( " +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
            TITLE + " TEXT , " +
            DESCRIPTION + " TEXT , " +
            POSTER_PATH + " TEXT , " +
            BACKDROP_PATH + " TEXT ) ";
}
