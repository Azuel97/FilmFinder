package com.example.moviefinder.database;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FilmProvider extends ContentProvider {

    public static final String AUTORITY = "com.example.moviefinder.database.ContentProvider";

    public static final String BASE_PATH_FILMS = "films";

    public static final int ALL_FILM = 0;
    public static final int SINGLE_FILM = 1;

    public static final String MIME_TYPE_FILMS = ContentResolver.CURSOR_DIR_BASE_TYPE + "vnd.all_films";
    public static final String MIME_TYPE_FILM = ContentResolver.CURSOR_ITEM_BASE_TYPE + "vnd.single_film";

    public static final Uri FILMS_URI = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + AUTORITY
            + "/" + BASE_PATH_FILMS);

    private FilmDB database;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTORITY, BASE_PATH_FILMS, ALL_FILM);
        uriMatcher.addURI(AUTORITY,BASE_PATH_FILMS + "/#", SINGLE_FILM);
    }


    @Override
    public boolean onCreate() {
        database = new FilmDB(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = database.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        switch (uriMatcher.match(uri)) {
            case SINGLE_FILM:
                builder.setTables(FilmTableHelper.TABLE_NAME);
                builder.appendWhere(FilmTableHelper._ID + " = " + uri.getLastPathSegment());
                break;
            case ALL_FILM:
                builder.setTables(FilmTableHelper.TABLE_NAME);
        }
        Cursor cursor = builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case SINGLE_FILM:
                return MIME_TYPE_FILM;
            case ALL_FILM:
                return MIME_TYPE_FILMS;
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        if (uriMatcher.match(uri) == ALL_FILM) {
            SQLiteDatabase db = database.getWritableDatabase();
            long result = db.insert(FilmTableHelper.TABLE_NAME, null, values);
            String resultSrting = ContentResolver.SCHEME_CONTENT + "://" + BASE_PATH_FILMS + "/" + result;
            getContext().getContentResolver().notifyChange(uri,null);
            return Uri.parse(resultSrting);
        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        String table = "", query = "";
        SQLiteDatabase db = database.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case SINGLE_FILM:
                table = FilmTableHelper.TABLE_NAME;
                query = FilmTableHelper._ID + " = " + uri.getLastPathSegment();
                if (selection != null) {
                    query += " AND " + selection;
                }
                break;
            case ALL_FILM:
                table = FilmTableHelper.TABLE_NAME;
                query = selection;
                break;
        }
        int deleteRows = db.delete(table, query, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);

        return deleteRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        String table = "", query = "";
        SQLiteDatabase db = database.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case SINGLE_FILM:
                table = FilmTableHelper.TABLE_NAME;
                query = FilmTableHelper._ID + " = " + uri.getLastPathSegment();
                if (selection != null) {
                    query += " AND " + selection;
                }
                break;
            case ALL_FILM:
                table = FilmTableHelper.TABLE_NAME;
                query = selection;
                break;
        }
        int deleteRows = db.update(table, values, query, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);

        return deleteRows;
    }
}
