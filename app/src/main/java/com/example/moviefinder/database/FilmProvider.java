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
    public static final String BASE_PATH_FAVORITES = "favorites";

    public static final int ALL_FILM = 1;
    public static final int SINGLE_FILM = 0;
    public static final int ALL_FAVORITE = 3;
    public static final int SINGLE_FAVORITE = 2;

    public static final String MIME_TYPE_FILMS = ContentResolver.CURSOR_DIR_BASE_TYPE + "vnd.all_films";
    public static final String MIME_TYPE_FILM = ContentResolver.CURSOR_ITEM_BASE_TYPE + "vnd.single_film";
    public static final String MIME_TYPE_FAVORITES = ContentResolver.CURSOR_DIR_BASE_TYPE + "vnd.all_favorites";
    public static final String MIME_TYPE_FAVORITE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "vnd.single_favorite";

    public static final Uri FILMS_URI = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + AUTORITY
            + "/" + BASE_PATH_FILMS);
    public static final Uri FAVORITES_URI = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + AUTORITY
            + "/" + BASE_PATH_FAVORITES);

    private FilmDB database;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTORITY, BASE_PATH_FILMS, ALL_FILM);
        uriMatcher.addURI(AUTORITY,BASE_PATH_FILMS + "/#", SINGLE_FILM);
        uriMatcher.addURI(AUTORITY, BASE_PATH_FAVORITES, ALL_FAVORITE);
        uriMatcher.addURI(AUTORITY,BASE_PATH_FAVORITES + "/#", SINGLE_FAVORITE);
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
                break;
            case SINGLE_FAVORITE:
                builder.setTables(FavoriteTableHelper.TABLE_NAME);
                builder.appendWhere(FavoriteTableHelper._ID + " = " + uri.getLastPathSegment());
                break;
            case ALL_FAVORITE:
                builder.setTables(FavoriteTableHelper.TABLE_NAME);
                break;
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
            case SINGLE_FAVORITE:
                return MIME_TYPE_FAVORITE;
            case ALL_FAVORITE:
                return MIME_TYPE_FAVORITES;
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
        } else if (uriMatcher.match(uri) == ALL_FAVORITE) {
            SQLiteDatabase db = database.getWritableDatabase();
            long result = db.insert(FavoriteTableHelper.TABLE_NAME, null, values);
            String resultString = ContentResolver.SCHEME_CONTENT + "://" + BASE_PATH_FAVORITES + "/" + result;
            getContext().getContentResolver().notifyChange(uri, null);
            return Uri.parse(resultString);
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
            case SINGLE_FAVORITE:
                table = FavoriteTableHelper.TABLE_NAME;
                query = FavoriteTableHelper._ID + " = " + uri.getLastPathSegment();
                if (selection != null) {
                    query += " AND " + selection;
                }
                break;
            case ALL_FAVORITE:
                table = FavoriteTableHelper.TABLE_NAME;
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
            case SINGLE_FAVORITE:
                table = FavoriteTableHelper.TABLE_NAME;
                query = FavoriteTableHelper._ID + " = " + uri.getLastPathSegment();
                if (selection != null) {
                    query += " AND " + selection;
                }
                break;
            case ALL_FAVORITE:
                table = FavoriteTableHelper.TABLE_NAME;
                query = selection;
                break;
        }
        int deleteRows = db.update(table, values, query, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);

        return deleteRows;
    }
}
