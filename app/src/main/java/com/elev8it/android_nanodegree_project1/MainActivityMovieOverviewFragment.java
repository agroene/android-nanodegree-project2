package com.elev8it.android_nanodegree_project1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.app.Activity;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Anton Groenewald on 2016/02/02.
 *
 * This is the Fragment View associated with the MainActivity Class for the Popular Movies
 * Application for the Android Nanodegree Project 1.  It contains a grid view of all the movie posters
 * retrieved, sorted either by 'Most Popular' or 'Top Rated'.
 */
public class MainActivityMovieOverviewFragment extends Fragment {

    // The movie array adapter that will be associated with the GridView on the Movie Overview Screen
    protected MovieArrayAdapter mMovieArrayAdapter = null;

    /*
     * Default Constructor
     */
    public MainActivityMovieOverviewFragment() {
    }

    /*
     * This method retrieves the 'sort by' setting for the movie overview screen and launches the async
     * method to call the WebService from the moviedb api to retrieve the list of movies.
     */
    public void getMovies() {
        // Retrieve the stored shared preferences for the application
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Retrieve the 'sort by' setting from the shared preferences.  The default value is the
        // first item configured in the strings.xml file within the pref_sort_list_values string-array
        String currentSortByPref =
                prefs.getString(
                        getString(R.string.pref_sort_by_key),
                        getResources().getStringArray(R.array.pref_sort_list_values)[0]);

        // Instantiate the aSyncTask and execute in another Thread.  Pass the Sort By Preference retrieved
        new getMoviesTask().execute(currentSortByPref);

        // Set the title in the action bar of the fragment to correspond to what was selected in the
        // 'Sort By' preference by retrieving the entry at the index corresponding to the sortByPref
        // index in the string-array pref_sort_list_values
        getActivity().setTitle(
                getResources().getStringArray(
                        R.array.pref_sort_list_entries)[Arrays.asList(
                        getResources().getStringArray(R.array.pref_sort_list_values)).indexOf(
                        currentSortByPref)] + " Movies");
    }

    /**
     * Called when the Fragment is visible to the user.  This is generally
     * tied to {@link Activity#onStart() Activity.onStart} of the containing
     * Activity's lifecycle.
     *
     * This method is overridden so that the GridView can be reset when the fragment is started.  It
     * will then display loading icons until the getMovies method is called to retrieve the list of
     * movies from the moviedb api.
     */
    @Override
    public void onStart() {
        super.onStart();
        // Since we're going to reload the gridview, we want to disable the ability to click on it
        // until the images have reloaded.

        getActivity().findViewById(R.id.gridview_movie_overview).setEnabled(false);
        // Clear the ArrayAdapter associated with the GridView

        mMovieArrayAdapter.clear();
        // The next section of code creates empty movie placeholders that will be inserted into the
        // ArrayAdapter associated with the GridView.  This will cause the GridView to display
        // loading icons until the actual images are retrieved from the webservice call.
        Movie emptyMovieObj = new Movie();
        ArrayList<Movie> emptyMoviesArrayList = new ArrayList<Movie>();

        for (int i = 0; i < 15; i++) {
            emptyMoviesArrayList.add(emptyMovieObj);
        }

        mMovieArrayAdapter.addAll(emptyMoviesArrayList);

        //Call the method that will launch the aSyncTask to call the MovieDB API
        getMovies();
    }

    /**
     * Called to do initial creation of a fragment.  This is called after
     * {@link #onAttach(Activity)} and before
     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * <p/>
     * <p>Note that this can be called while the fragment's activity is
     * still in the process of being created.  As such, you can not rely
     * on things like the activity's content view hierarchy being initialized
     * at this point.  If you want to do work once the activity itself is
     * created, see {@link #onActivityCreated(Bundle)}.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: Add code to retrieve savedInstanceState here later
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        //Create the rootView object by inflating the associated XML layout
        View rootView = inflater.inflate(R.layout.movie_overview_fragment_main, container, false);

        // Generate an ArrayList of empty movie objects to fill the grid with initially.  This is so
        // that the spinner wheel will be displayed within each grid element at startup until the
        // images are loaded successfully
        Movie emptyMovieObj = new Movie();

        ArrayList<Movie> emptyMoviesArrayList = new ArrayList<Movie>();

        for (int i = 0; i < 15; i++) {
            emptyMoviesArrayList.add(emptyMovieObj);
        }

        // Initialise the Adapter for the grid view with the temporary list of empty movie objects
        mMovieArrayAdapter =
                new MovieArrayAdapter(getActivity(), emptyMoviesArrayList);

        // Get the gridview object and set the adapter
        GridView moviesGridView = (GridView)rootView.findViewById(R.id.gridview_movie_overview);
        moviesGridView.setAdapter(mMovieArrayAdapter);

        // Disable the ability to click on elements in the GridView while the movie posters are
        // loading
        moviesGridView.setEnabled(false);

        return rootView;
    }

    /*
     * This is the aSyncTask class that is responsible to retrieve the list of movies from the
     * MovieDB API
     */
    private class getMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        private final String API_KEY_PARAM = "api_key";
        private final String SORT_BY_PARAM = "sort_by";
        private final String JSON_KEY_RESULTS = "results";
        private final String JSON_KEY_ID = "id";
        private final String JSON_KEY_NAME = "title";
        private final String JSON_KEY_POSTER_URL = "poster_path";
        private final String JSON_KEY_DATE = "release_date";
        private final String JSON_KEY_VOTE_AVE = "vote_average";
        private final String JSON_KEY_DESC = "overview";

        /*
         * Parse the JSON string that is returned from theMovieDB API call.
         */
        private ArrayList<Movie> getMovieDataFromJson(String moviesJsonStr) {

            ArrayList<Movie> moviesArrayList = new ArrayList<Movie>();

            try {
                // Convert the raw string into a JSONObject for parsing
                JSONObject movieLstJson = new JSONObject(moviesJsonStr);

                // The API returns a list of movies under the 'results' tag.  Put the results in
                // a JSONArray object for further processing
                JSONArray movieLst = movieLstJson.getJSONArray(JSON_KEY_RESULTS);

                // Iterate through the results list and construct each movie object by extracting
                // the information we require for our application
                for (int i=0; i < movieLst.length(); i++) {
                    JSONObject movieJSON = movieLst.getJSONObject(i);
                    Movie movie =
                            new Movie(
                                    movieJSON.getString(JSON_KEY_ID),
                                    movieJSON.getString(JSON_KEY_NAME),
                                    getString(R.string.tmdb_get_movie_poster_url) +
                                        movieJSON.getString(JSON_KEY_POSTER_URL),
                                    movieJSON.getString(JSON_KEY_DATE),
                                    movieJSON.getString(JSON_KEY_VOTE_AVE),
                                    movieJSON.getString(JSON_KEY_DESC));

                    moviesArrayList.add(movie);
                }
            }
            catch (JSONException jsonex) {
                //TODO: Handle Exception properly
            }

            return moviesArrayList;
        }

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p/>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected ArrayList<Movie> doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String url_sort_type_ext;

            // String that will contain the JSON response to the movies query as a string.
            String moviesJsonStr = null;
            ArrayList<Movie> movieArrayList = null;

            try {
                // Check that the 'sort by' parameter is passed to this method as it required to
                // determine which API call to use
                if (params.length != 1) {
                    throw new ArrayIndexOutOfBoundsException(
                            "Have to specify exactly one parameter in the GetMovies Background Task - " +
                            "sort_by url extension.  You specified " + params.length + " parameter(s).");
                }
                else {
                    url_sort_type_ext = params[0];
                }

                // Construct the URL for the theMovieDB query
                // Using two different URL's from the API depending on the Sort Type selected...
                // For 'most popular' use: https://api.themoviedb.org/3/movie/popular
                // For 'highest rated' use: https://api.themoviedb.org/3/movie/top_rated
                Uri.Builder uriBuilder = new Uri.Builder();
                uriBuilder.encodedPath(getString(R.string.tmdb_get_movies_url) + url_sort_type_ext);

                // Append your API Key as the second parameter to the method below
                uriBuilder.appendQueryParameter(API_KEY_PARAM, getString(R.string.tmdb_api_key));

                // Construct the URL that will be used to get a list of the most popular movies
                URL url = new URL(uriBuilder.build().toString());

                // Create the request to theMovieDB and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String variable
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer rspBuffer = new StringBuffer();

                if (inputStream == null) {

                    // TODO: Deal with error situation
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;

                while ((line = reader.readLine()) != null) {
                    rspBuffer.append(line + "\n");
                }

                if (rspBuffer.length() == 0) {
                    return null;
                }

                moviesJsonStr = rspBuffer.toString();

                // Parse the JSON string and build the movieArrayList from it
                movieArrayList = getMovieDataFromJson(moviesJsonStr);
            }
            catch (MalformedURLException mfurlex) {

                //TODO: Deal with Exception
                return null;
            }
            catch (IOException ioex) {

                //TODO: Deal with Exception
                return null;
            }

            return movieArrayList;
        }

        /**
         * <p>Runs on the UI thread after {@link #doInBackground}. The
         * specified result is the value returned by {@link #doInBackground}.</p>
         * <p/>
         * <p>This method won't be invoked if the task was cancelled.</p>
         *
         * @param movieArrayListFromAPICall The result of the operation computed by {@link #doInBackground}.
         * @see #onPreExecute
         * @see #doInBackground
         * @see #onCancelled(Object)
         */
        @Override
        protected void onPostExecute(ArrayList<Movie> movieArrayListFromAPICall) {

            if (movieArrayListFromAPICall != null) {
                // Clear the ArrayAdapter which contains dummy movie data at this stage
                mMovieArrayAdapter.clear();

                // Add the ArrayList of movies that were retrieved from the API call
                mMovieArrayAdapter.addAll(movieArrayListFromAPICall);
            }
        }
    }
 }
