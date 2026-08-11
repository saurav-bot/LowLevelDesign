package InterviewQuestions.BookMyShow.service;

import InterviewQuestions.BookMyShow.entities.*;

import java.math.BigDecimal;
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
//    private final ConcurrentHashMap<String, Show> shows = new ConcurrentHashMap<>();


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
        theatre.addScreenToTheatre(screen);
        screens.put(screen.getScreenId(), screen);
    }

    public void addSeatToScreen(String screenId, Seat seat) {
        Screen screen = screens.get(screenId);
        if (screen == null) {
            throw new RuntimeException("Screen does not exists");
        }
        screen.addSeatToScreen(seat);
    }



    public List<Theatre> getTheatreByCity(String cityName) {
        return cityVsTheatre.get(cityName);
    }

    public List<Screen> getScreenInTheatre(String theatreId) {
        return theatres.get(theatreId).getScreens();
    }

}
