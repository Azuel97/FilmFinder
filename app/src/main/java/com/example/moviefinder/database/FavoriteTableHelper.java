package com.example.moviefinder.database;

import android.provider.BaseColumns;

public class FavoriteTableHelper implements BaseColumns {

    public static final String TABLE_NAME = "favorites";
    public static final String ID_FILM = "id_film";

    public static final String CREATE = "CREATE TABLE " + TABLE_NAME + " ( " +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
            ID_FILM + " INTEGER ) ";
}
