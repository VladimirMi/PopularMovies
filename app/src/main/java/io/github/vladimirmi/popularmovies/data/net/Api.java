package io.github.vladimirmi.popularmovies.data.net;

@SuppressWarnings("WeakerAccess")
public class Api {

    public final static String BASE_URL = "https://api.themoviedb.org/3/";
    public final static String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/%1$s/%2$s";
    public static final String YOUTUBE = "YouTube";
    public final static String YOUTUBE_VIDEO_URL = "http://www.youtube.com/watch?v=%1$s";
    public final static String YOUTUBE_THUMBNAIL_URL = "http://img.youtube.com/vi/%1$s/0.jpg";
    public final static int CONNECT_TIMEOUT = 5000;
    public final static int READ_TIMEOUT = 5000;
    public final static int WRITE_TIMEOUT = 5000;

    public interface ImageSize {

        String getPath();
    }

    public enum PosterSize implements ImageSize {
        VERY_LOW("w92"),
        LOW("w154"),
        MID("w342"),
        HIGH("w500"),
        VERY_HIGH("w780"),
        ORIGINAL("original");

        final String path;

        PosterSize(String path) {
            this.path = path;
        }

        @Override
        public String getPath() {
            return path;
        }
    }

    public enum BackdropSize implements ImageSize {
        LOW("w300"),
        MID("w780"),
        HIGH("w1280"),
        ORIGINAL("original");

        final String path;

        BackdropSize(String path) {
            this.path = path;
        }

        @Override
        public String getPath() {
            return path;
        }
    }

    private Api() {
    }
}
