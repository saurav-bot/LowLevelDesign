package InterviewQuestions.BookMyShow.entities;

import java.util.UUID;
import java.time.Duration;

public class Movie {
    private final String movieId;
    private final String movieName;
    private final String movieDescription;
    private final Duration movieDuration;

    public Movie(String movieName, String movieDescription, Duration movieDuration) {
        this.movieDescription = movieDescription;
        this.movieName = movieName;
        this.movieDuration = movieDuration;
        this.movieId = UUID.randomUUID().toString();
    }

    public String getMovieId() {
        return this.movieId;
    }

    public String getMovieName() {
        return this.movieName;
    }

    public String getMovieDescription() {
        return this.movieDescription;
    }

    public Duration getMovieDuration() {
        return this.movieDuration;
    }
}
