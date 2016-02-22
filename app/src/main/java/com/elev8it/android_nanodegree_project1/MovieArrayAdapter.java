package com.elev8it.android_nanodegree_project1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anton Groenewald on 2016/02/02.
 *
 * The Custom Array Adapter class that is used with the movie overview GridView object.
 */
public class MovieArrayAdapter extends ArrayAdapter<Movie> {

    protected Context mContext;
    protected GridView parentGridView = null;

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param objects  The objects to represent in the ListView.
     */
    public MovieArrayAdapter(Context context, ArrayList<Movie> objects) {
        super(context, 0, objects);
        this.mContext = context;
    }

    /**
     * {@inheritDoc}
     *
     * @param position - The position of the element for which the View is being obtained
     * @param convertView - The View that needs to be customised for the specific element
     * @param parent - The View Object that is using the custom ArrayAdapter (GridView)
     */
    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        // Retrieve the movie object at the current position
        Movie movie = getItem(position);

        // Retrieve the URL of the movie at the current position
        String url = movie.getUrl();

        // If there is no view associated with the element at the current position, inflate the
        // View as configured in the associated layout XML file (image_view_movies.xml)
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.image_view_movies, parent, false);
        }

        // Set the visibility of the ImageView to invisible.  This is so that the user initially sees
        // the progress bar spinner while the image is loading
        final ImageView moviePoster = (ImageView)convertView.findViewById(R.id.movie_poster_img);
        moviePoster.setVisibility(View.INVISIBLE);

        // Make the progress bar visible so that the spinner is displayed to the user while the
        // movie poster images are loading.
        final ProgressBar progressBar = (ProgressBar)convertView.findViewById(R.id.movie_poster_progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        parentGridView = (GridView)parent;

        int width = parentGridView.getColumnWidth();
        Double height = width * 1.5;

        // Load the poster image into the ImageView
        Picasso.with(getContext()).load(url).resize(width, height.intValue()).into(moviePoster, new Callback() {
            @Override
            public void onSuccess() {
                // When the image is finished loading, hide the progress bar from the user and make
                // loaded image visible.
                moviePoster.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);

                // Get a handle to the GridView associated with the ArrayAdapter and enable the
                // item to be clicked on.
                parentGridView.setEnabled(true);


                parentGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        // When the item is clicked on, launch an Intent to the MovieDetailActivity
                        // class.
                        Intent showDetailsIntent = new Intent(mContext, MovieDetailActivity.class);

                        //Retrieve the movie object that was clicked on
                        Movie movie = getItem(position);

                        // Add the associated movie object as a parcelable to be passed  to the intent.
                        showDetailsIntent.putExtra(
                                mContext.getString(R.string.movie_parcelable_key),
                                movie);

                        // Launch the Intent
                        mContext.startActivity(showDetailsIntent);
                    }
                });
            }

            @Override
            public void onError() {
                // TODO: Add proper error handling
            }
        });




//        Picasso.Builder builder = new Picasso.Builder(mContext);
//        builder.listener(new Picasso.Listener()
//        {
//            @Override
//            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception)
//            {
//                exception.printStackTrace();
//            }
//        });
//        builder.build().load(url).into(moviePoster);

        return convertView;
    }

}
