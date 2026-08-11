package InterviewQuestions.BookMyShow.entities;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public class Show {
    private final String showId;
    private final Movie movie;
    private final LocalDateTime showStartTime;
    private final Duration showDuration;
    private final LocalDateTime showEndTime;
    private final Screen screen;
    private final Theatre theatre;

    public Show(Movie movie, LocalDateTime showStartTime, Duration showDurationInMinute, Screen screen, Theatre theatre) {
        this.movie = movie;
        this.screen = screen;
        this.showStartTime = showStartTime;
        this.showDuration = showDurationInMinute;
        this.showEndTime = showStartTime.plusMinutes(showDurationInMinute.toMinutes());
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

    public LocalDateTime getShowStartTime() {
        return this.showStartTime;
    }

    public Duration getShowDuration() {
        return this.showDuration;
    }

    public Screen getShowScreen() {
        return this.screen;
    }


}
