package InterviewQuestions.BookMyShow.service;

import InterviewQuestions.BookMyShow.entities.*;
import InterviewQuestions.BookMyShow.enums.SeatType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ShowService {
    private final ConcurrentHashMap<String, Show> shows = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<ShowSeat>> showSeats = new ConcurrentHashMap<>();

    public void createShow(Show show) {
        shows.put(show.getShowId(), show);
        for (Seat seat : show.getShowScreen().getSeats()) {
            ShowSeat showSeat = new ShowSeat(show, seat, getPriceForSeat(seat));
            showSeats
                    .computeIfAbsent(show.getShowId(), k -> new CopyOnWriteArrayList<>())
                    .add(showSeat);
        }
    }

    private BigDecimal getPriceForSeat(Seat seat) {
        return seat.getSeatType() == SeatType.PREMIUM
                ? BigDecimal.valueOf(300)
                : BigDecimal.valueOf(100);
    }

    public List<Movie> getAllMovieInCity(String city) {

        Map<String, Movie> movieMap = new HashMap<>();

        for (Show show : shows.values()) {
            if (show.getTheatre().getAddress().getCity().equalsIgnoreCase(city)) {
                movieMap.put(show.getMovie().getMovieId(), show.getMovie());
            }
        }

        return movieMap.values().stream().toList();
    }


    public List<ShowSeat> getAllSeatOfShow(String showId) {
        return showSeats.getOrDefault(showId, new ArrayList<>());
    }

    public List<Show> getAllShowOfMovieInCity(String city, String movieId) {
        List<Show> showList = new ArrayList<>();

        for (Show show: shows.values()) {
            if (show.getMovie().getMovieId().equalsIgnoreCase(movieId)
                    && show.getTheatre().getAddress().getCity().equalsIgnoreCase(city)
                    && show.getShowStartTime().isAfter(LocalDateTime.now())) {
                showList.add(show);
            }
        }

        return showList;
    }
}
