package io.github.vladimirmi.popularmovies.data.entity;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import com.squareup.moshi.Json;

import io.github.vladimirmi.popularmovies.data.net.Api;

/**
 * Class that represents a Movie model.
 */

@SuppressWarnings("WeakerAccess")
public class Movie implements Parcelable {

    @Json(name = "id") private int id;
    @Json(name = "vote_average") private double voteAverage;
    @Json(name = "title") private String title;
    @Json(name = "poster_path") private String posterPath;
    @Json(name = "backdrop_path") private String backdropPath;
    @Json(name = "overview") private String overview;
    @Json(name = "release_date") private String releaseDate;

    public Movie(int id, double voteAverage, String title, String posterPath, String backdropPath, String overview, String releaseDate) {
        this.id = id;
        this.voteAverage = voteAverage;
        this.title = title;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.overview = overview;
        this.releaseDate = releaseDate;
    }

    protected Movie(Parcel in) {
        id = in.readInt();
        voteAverage = in.readDouble();
        title = in.readString();
        posterPath = in.readString();
        backdropPath = in.readString();
        overview = in.readString();
        releaseDate = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeDouble(voteAverage);
        dest.writeString(title);
        dest.writeString(posterPath);
        dest.writeString(backdropPath);
        dest.writeString(overview);
        dest.writeString(releaseDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public int getId() {
        return id;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterUrl(Api.ImageSize size) {
        return getImagePath(size, posterPath);
    }

    public String getBackdropUrl(Api.ImageSize size) {
        return getImagePath(size, backdropPath);
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    @SuppressLint("DefaultLocale")
    private String getImagePath(Api.ImageSize size, String path) {
        return String.format(Api.BASE_IMAGE_URL, size.getPath(), path);
    }
}
