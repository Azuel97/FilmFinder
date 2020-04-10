package com.example.moviefinder;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class DetailMovieActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);
        setTitle("Details Movie");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1976D2")));


        Intent intent = getIntent();
        String title = intent.getExtras().getString("Title");
        String image = intent.getExtras().getString("Image");
        String description = intent.getExtras().getString("Description");

        TextView textViewTitle = findViewById(R.id.text_Title);
        textViewTitle.setText(title);

        TextView textViewDescription = findViewById(R.id.textViewDescription);
        textViewDescription.setText(description);

        ImageView imageView = findViewById(R.id.imageViewDetail);
        // Glide for image
        Glide.with(this)
                .load("https://image.tmdb.org/t/p/w500/"+image)
                .into(imageView);
    }
}
