package com.elev8it.android_nanodegree_project1;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

/**
 * Created by Anton Groenewald on 2016/02/02.
 *
 * A fragment to display the movie details when a movie poster is selected
 */
public class MovieDetailActivityFragment extends Fragment {

    public MovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        // Obtain the movie object that was sent as a parcelable along with the Intent
        Movie intentMovie =
                getActivity().getIntent().getParcelableExtra(getString(R.string.movie_parcelable_key));

        // Retrieve the Title TextView for the movie and populate it with the movie title
        TextView movieDetailTitle = (TextView)rootView.findViewById(R.id.movie_title_textview);
        movieDetailTitle.setText(intentMovie.getName());

        // Retrieve the Release Date TextView for the movie and populate it with the movie release date
        ((TextView)rootView.findViewById(
                R.id.movie_year_textview)).setText(
                intentMovie.getReleaseDate());

        // Retrieve the Vote Average TextView for the movie and populate it with the movie vote ave
        ((TextView)rootView.findViewById(
                R.id.movie_vote_ave_textview)).setText("Vote Average: " +
                    intentMovie.getVoteAverage());

        // Retrieve the Plot TextView for the movie and populate it with the movie plot description
        ((TextView)rootView.findViewById(
                R.id.movie_plot_textview)).setText(
                    intentMovie.getDescription());

        // Load the ImageView with the relevant movie poster image
        ImageView moviePosterImg = (ImageView)rootView.findViewById(R.id.movie_poster_img_dtls);
        Picasso.with(getActivity()).load(intentMovie.getUrl()).into(moviePosterImg);

        return rootView;
    }
}
