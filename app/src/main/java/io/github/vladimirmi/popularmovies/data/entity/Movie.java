package io.github.vladimirmi.popularmovies.data.entity;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * JavaBean class that represents a Movie model.
 */

public class Movie {

    /**
     * vote_count : 492
     * id : 345938
     * video : false
     * vote_average : 7
     * title : The Shack
     * popularity : 132.041256
     * poster_path : /doAzav9kfdtsoSdw1MDFvjKq3J4.jpg
     * original_language : en
     * original_title : The Shack
     * genre_ids : [18,14]
     * backdrop_path : /un7pVnJC2el8YxdIOg6YrSmBTlO.jpg
     * adult : false
     * overview : After suffering a family tragedy, Mack Phillips spirals into a deep depression causing him to question his innermost beliefs. Facing a crisis of faith, he receives a mysterious letter urging him to an abandoned shack deep in the Oregon wilderness. Despite his doubts, Mack journeys to the shack and encounters an enigmatic trio of strangers led by a woman named Papa. Through this meeting, Mack finds important truths that will transform his understanding of his tragedy and change his life forever.
     * release_date : 2017-03-03
     */

    @Json(name = "vote_count") private int voteCount;
    @Json(name = "id") private int id;
    @Json(name = "video") private boolean video;
    @Json(name = "vote_average") private int voteAverage;
    @Json(name = "title") private String title;
    @Json(name = "popularity") private double popularity;
    @Json(name = "poster_path") private String posterPath;
    @Json(name = "original_language") private String originalLanguage;
    @Json(name = "original_title") private String originalTitle;
    @Json(name = "backdrop_path") private String backdropPath;
    @Json(name = "adult") private boolean adult;
    @Json(name = "overview") private String overview;
    @Json(name = "release_date") private String releaseDate;
    @Json(name = "genre_ids") private List<Integer> genreIds;

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public int getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(int voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public List<Integer> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(List<Integer> genreIds) {
        this.genreIds = genreIds;
    }
}
