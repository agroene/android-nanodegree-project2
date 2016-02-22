package com.elev8it.android_nanodegree_project1;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Anton Groenewald on 2016/02/02.
 *
 * The Movie class that is used to store the data related to each of the movies retrieved from
 * theMovieDB API.
 */
public class Movie implements Parcelable {

    // The ID of each movie
    protected String id = null;
    // The title of the movie
    protected String name = null;
    // The URL to the poster image of the particular movie
    protected String url = null;
    // The date at which the movie was released
    protected String releaseDate = null;
    // The average vote for the movie as rated by theMovieDB
    protected String voteAverage = null;
    // The synopsis of the movie, describing what it is about
    protected String description = null;

    public Movie() {
    }

    // Constructor where all the necessary attributes are set at instantiation of the object
    public Movie(String id, String name, String url, String date, String votes, String desc) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.releaseDate = date;
        this.voteAverage = votes;
        this.description = desc;
    }

    // Private constructor for use when the object is used as a Parcelable
    private Movie(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.url = in.readString();
        this.releaseDate = in.readString();
        this.voteAverage = in.readString();
        this.description = in.readString();
    }

    // Getter for the movie Id attribute
    public String getId() {
        return id;
    }

    // Setter for the movie ID attribute
    public void setId(String id) {
        this.id = id;
    }

    // Getter for the movie Name attribute
    public String getName() {
        return name;
    }

    // Setter for the movie Name attribute
    public void setName(String name) {
        this.name = name;
    }

    // Getter for the movie Url attribute
    public String getUrl() { return url; }

    // Setter for the movie Url attribute
    public void setUrl(String url) { this.url = url; }

    // Getter for the movie releaseDate attribute
    public String getReleaseDate() {
        return releaseDate;
    }

    // Setter for the movie releaseDate attribute
    public void setReleaseDate(String release_date) {
        this.releaseDate = release_date;
    }

    // Getter for the movie voteAverage attribute
    public String getVoteAverage() {
        return voteAverage;
    }

    // Setter for the movie voteAverage attribute
    public void setVoteAverage(String vote_average) {
        this.voteAverage = vote_average;
    }

    // Getter for the movie Description attribute
    public String getDescription() {
        return description;
    }

    // Setter for the movie Description attribute
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * @return a bitmask indicating the set of special object types marshalled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getId());
        dest.writeString(getName());
        dest.writeString(getUrl());
        dest.writeString(getReleaseDate());
        dest.writeString(getVoteAverage());
        dest.writeString(getDescription());
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        /**
         * Create a new instance of the Parcelable class, instantiating it
         * from the given Parcel whose data had previously been written by
         * {@link Parcelable#writeToParcel Parcelable.writeToParcel()}.
         *
         * @param source The Parcel to read the object's data from.
         * @return Returns a new instance of the Parcelable class.
         */
        @Override
        public Movie createFromParcel(Parcel source) {

            return new Movie(source);
        }

        /**
         * Create a new array of the Parcelable class.
         *
         * @param size Size of the array.
         * @return Returns an array of the Parcelable class, with every entry
         * initialized to null.
         */
        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
