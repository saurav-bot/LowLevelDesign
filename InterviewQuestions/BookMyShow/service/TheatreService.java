package InterviewQuestions.BookMyShow.service;

import InterviewQuestions.BookMyShow.entities.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class TheatreService {
    private final ConcurrentHashMap<String, Theatre> theatres = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<Theatre>> cityVsTheatre = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, Screen> screens = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Show> shows = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<ShowSeat>> showSeats = new ConcurrentHashMap<>();


    public void createTheatre(Theatre theatre) {
        theatres.put(theatre.getTheatreId(), theatre);
        cityVsTheatre
                .computeIfAbsent(theatre.getAddress().getCity(), k -> new CopyOnWriteArrayList<>())
                .add(theatre);
    }

    public void addScreenToTheatre(String theatreId, Screen screen) {
        Theatre theatre = theatres.get(theatreId);
        if (theatre == null) {
            throw new RuntimeException("Theatre does not exists");
        }
        theatre.getScreens().add(screen);
        screens.put(screen.getScreenId(), screen);
    }

    public void addSeatToScreen(String screenId, Seat seat) {
        Screen screen = screens.get(screenId);
        if (screen == null) {
            throw new RuntimeException("Screen does not exists");
        }
        screen.getSeats().add(seat);
    }

    public void createShow(String theatreId, String screenId, Show show) {
        shows.put(show.getShowId(), show);
        Screen screen = screens.get(screenId);
        for (Seat seat : screen.getSeats()) {
            ShowSeat showSeat = new ShowSeat(show, seat, 100);
            showSeats
                    .computeIfAbsent(show.getShowId(), k -> new CopyOnWriteArrayList<>())
                    .add(showSeat);
        }
    }

    public List<Theatre> getTheatreByCity(String cityName) {
        return cityVsTheatre.get(cityName);
    }

    public List<Screen> getScreenInTheatre(String theatreId) {
        return theatres.get(theatreId).getScreens();
    }

    public Set<Movie> getAllMovieInCity(String city) {
        List<Theatre> theatres = cityVsTheatre.get(city);
        if (theatres == null || theatres.isEmpty()) {
            throw new RuntimeException("No theatres found for this city");
        }
        Set<Movie> movies = new HashSet<>();

        for (Show show : shows.values()) {
            if (show.getTheatre().getAddress().getCity().equalsIgnoreCase(city)) {
                movies.add(show.getMovie());
            }
        }

        return movies;
    }


    public List<ShowSeat> getAvailableSeat(String showId) {
        return showSeats.get(showId);
    }

    public List<Show> getAllShowOfMovieInCity(String city, String movieId) {
        List<Theatre> theatres = cityVsTheatre.get(city);
        if (theatres == null || theatres.isEmpty()) {
            throw new RuntimeException("No theatres found for this city");
        }

        List<Show> showList = new ArrayList<>();

        for (Show show: shows.values()) {
            if (show.getMovie().getMovieId().equalsIgnoreCase(movieId) && show.getTheatre().getAddress().getCity().equalsIgnoreCase(city)) {
                showList.add(show);
            }
        }

        return showList;
    }
}
