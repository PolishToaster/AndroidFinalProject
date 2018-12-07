package com.algonquin.androidfinalproject.Movies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.algonquin.androidfinalproject.Movies.MovieActivity;
import com.algonquin.androidfinalproject.R;

/* Said Zaripov*/
public class MovieFragmentMain extends Fragment {

    MovieActivity parent = new MovieActivity();
    private Button searchMovieBtn, favouritesMovieBtn;
    private EditText enteredMovieTitle;

    public MovieFragmentMain(){}

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        View screen = inflater.inflate(R.layout.movie_fragment_main, container, false);

        searchMovieBtn = screen.findViewById(R.id.queryMovieBtn);
        enteredMovieTitle = screen.findViewById(R.id.movieToQueryEt);
        favouritesMovieBtn = screen.findViewById(R.id.mainMovieFavButton);

        searchMovieBtn.setOnClickListener((v -> {
            ((MovieActivity)getActivity()).searchForAMovie(enteredMovieTitle.getText().toString());
        }));

        favouritesMovieBtn.setOnClickListener((v -> {
            ((MovieActivity)getActivity()).toMovieFavourites();
        }));

        return screen;
    }
}