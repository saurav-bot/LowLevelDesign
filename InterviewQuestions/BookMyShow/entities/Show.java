package InterviewQuestions.BookMyShow.entities;

import java.util.List;
import java.util.UUID;

public class Show {
    private final String showId;
    private final Movie movie;
    private final String showStartTime;
    private final String showDuration;
    private final Screen screen;
    private final Theatre theatre;

    public Show(Movie movie, String showStartTime, String showDuration, Screen screen, Theatre theatre) {
        this.movie = movie;
        this.screen = screen;
        this.showStartTime = showStartTime;
        this.showDuration = showDuration;
        this.showId = UUID.randomUUID().toString();
        this.theatre = theatre;
    }

    public Theatre getTheatre() {
        return this.theatre;
    }

    public String getShowId() {
        return this.showId;
    }

    public Movie getMovie() {
        return this.movie;
    }

    public String getShowStartTime() {
        return this.showStartTime;
    }

    public String getShowDuration() {
        return this.showDuration;
    }

    public Screen getShowScreen() {
        return this.screen;
    }


}
