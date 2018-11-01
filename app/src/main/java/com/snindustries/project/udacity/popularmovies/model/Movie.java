package com.snindustries.project.udacity.popularmovies.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.snindustries.project.udacity.popularmovies.database.MovieTypeConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Model for response of Get Movie
 * <p>
 * https://developers.themoviedb.org/3/movies/get-popular-movies
 * and
 * https://developers.themoviedb.org/3/movies/get-top-rated-movies
 *
 * @author shaaz noormohammad
 * (c) October 1, 2018
 */
@Entity
public class Movie implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @ColumnInfo
    @SerializedName("adult")
    private Boolean adult;

    @ColumnInfo
    @SerializedName("backdrop_path")
    private String backdropPath;

    @ColumnInfo
    @Nullable
    @SerializedName("favorite")
    private Boolean favorite;

    @SerializedName("genre_ids")
    @TypeConverters(MovieTypeConverter.class)
    private List<Integer> genreIds = null;

    @PrimaryKey
    @SerializedName("id")
    private Integer id;

    @SerializedName("original_language")
    private String originalLanguage;

    @SerializedName("original_title")
    private String originalTitle;

    @SerializedName("overview")
    private String overview;

    @SerializedName("popularity")
    private Double popularity;

    @ColumnInfo
    @Nullable
    private Integer popularityOrder;

    @SerializedName("poster_path")
    private String posterPath;
    @Nullable
    private Integer ratingOrder;
    @SerializedName("release_date")
    private String releaseDate;
    @SerializedName("title")
    private String title;
    @SerializedName("video")
    private Boolean video;
    @SerializedName("vote_average")
    private Double voteAverage;
    @SerializedName("vote_count")
    private Integer voteCount;

    public Movie() {
        //default null object
    }

    @Ignore
    private Movie(Parcel source) {
        adult = source.readByte() == 1;
        backdropPath = source.readString();
        genreIds = new ArrayList<>();
        source.readList(genreIds, null);
        id = source.readInt();
        originalLanguage = source.readString();
        originalTitle = source.readString();
        overview = source.readString();
        popularity = source.readDouble();
        posterPath = source.readString();
        releaseDate = source.readString();
        title = source.readString();
        video = source.readByte() == 1;
        voteAverage = source.readDouble();
        voteCount = source.readInt();
        adult = source.readByte() == 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Boolean getAdult() {
        return adult;
    }

    public void setAdult(Boolean adult) {
        this.adult = adult;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

    public List<Integer> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(List<Integer> genreIds) {
        this.genreIds = genreIds;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    @Nullable
    public Integer getPopularityOrder() {
        return popularityOrder;
    }

    public void setPopularityOrder(@Nullable Integer popularityOrder) {
        this.popularityOrder = popularityOrder;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    @Nullable
    public Integer getRatingOrder() {
        return ratingOrder;
    }

    public void setRatingOrder(@Nullable Integer ratingOrder) {
        this.ratingOrder = ratingOrder;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getVideo() {
        return video;
    }

    public void setVideo(Boolean video) {
        this.video = video;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    private void writeBoolean(Parcel dest, Boolean favorite) {
        dest.writeByte((byte) (favorite != null && favorite ? 1 : 0));//default false
    }

    @Ignore
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        writeBoolean(dest, adult);
        dest.writeString(backdropPath);
        dest.writeList(genreIds);
        dest.writeInt(id);
        dest.writeString(originalLanguage);
        dest.writeString(originalTitle);
        dest.writeString(overview);
        dest.writeDouble(popularity);
        dest.writeString(posterPath);
        dest.writeString(releaseDate);
        dest.writeString(title);
        writeBoolean(dest, video);
        dest.writeDouble(voteAverage);
        dest.writeInt(voteCount);
        writeBoolean(dest, favorite);
    }
}
