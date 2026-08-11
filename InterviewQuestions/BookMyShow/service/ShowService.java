//package InterviewQuestions.BookMyShow.service;
//
//import InterviewQuestions.BookMyShow.entities.Movie;
//import InterviewQuestions.BookMyShow.entities.Seat;
//import InterviewQuestions.BookMyShow.entities.Show;
//
//import java.util.List;
//import java.util.concurrent.ConcurrentHashMap;
//
//public class ShowService {
//    private final ConcurrentHashMap<String, Show> shows = new ConcurrentHashMap<>();
//
//    public void createShow(Show show) {
//        this.shows.put(show.getShowId(), show);
//    }
//
//    public Show getShowById(String showId) {
//        return this.shows.get(showId);
//    }
//
//    public List<Seat> getSeatsOfShow(String showId) {
//        return this.shows.get(showId).getSeatList();
//    }
//
//    public Movie getMovieOfShow(String showId) {
//        return this.shows.get(showId).getMovie();
//    }
//
//}
