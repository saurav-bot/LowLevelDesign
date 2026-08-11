package InterviewQuestions.BookMyShow;

import InterviewQuestions.BookMyShow.entities.*;
import InterviewQuestions.BookMyShow.enums.SeatType;
import InterviewQuestions.BookMyShow.service.BookingService;
import InterviewQuestions.BookMyShow.service.ShowService;
import InterviewQuestions.BookMyShow.service.TheatreService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BookMyShow {
    public static void main(String[] args) {
        TheatreService theatreService = new TheatreService();
        ShowService showService = new ShowService();

        BookingService bookingService = new BookingService(showService);

        initialize(theatreService, showService);

        bookingFlowDemo(theatreService, bookingService, showService);
    }

    public static void bookingFlowDemo(TheatreService theatreService, BookingService bookingService, ShowService showService) {

        String selectedCity = "Gurgaon";
        List<Movie> movies = showService.getAllMovieInCity(selectedCity);
        System.out.println("Movie in " + selectedCity);
        Movie selectedMovie = null;
        for (Movie movie : movies) {
            selectedMovie = movie;
            System.out.println(movie.getMovieId() + " " + movie.getMovieName());
        }


        List<Show> shows = showService.getAllShowOfMovieInCity(selectedCity, selectedMovie.getMovieId());

        System.out.println("Shows available for movie: " + selectedMovie.getMovieName() + " in city: " + selectedCity);

        for (Show s : shows) {
            System.out.println("start time " + s.getShowStartTime() + " Screen " + s.getShowScreen().getScreenId());
        }

        Show show = shows.get(0);
        List<ShowSeat> bookedSeat = bookingService.getAvailableSeatForShow(show.getShowId());
        User saurav = new User("saurav");
        Booking book1 = bookingService.book(show, bookedSeat, saurav);
        System.out.println("Booking: " + book1.getBookingStatus());

//        ShowSeat t = bookedSeat.get(0);
//        bookedSeat = new ArrayList<>(bookingService.getAvailableSeatForShow(show.getShowId()));
//        bookedSeat.add(t);

        Booking book2 = bookingService.book(show, bookedSeat, saurav);
        System.out.println("Booking: " + book2.getBookingStatus());


    }

    public static void initialize(TheatreService theatreService, ShowService showService) {
        Movie movie1 = new Movie("Bahubali", "war crime", Duration.ofMinutes(150));
        Movie movie2 = new Movie("Spiderman", "war crime", Duration.ofMinutes(140));
        Movie movie3 = new Movie("Odyssey", "war crime", Duration.ofMinutes(130));



        Screen screen = new Screen(new ArrayList<>());
        Screen screen1 = new Screen(new ArrayList<>());
        Screen screen3 = new Screen(new ArrayList<>());
        Screen screen4 = new Screen(new ArrayList<>());

        List<Screen> screens = List.of(screen, screen1, screen3, screen4);

        for (Screen scn : screens) {
            for (int i = 0; i < 10; i ++)  {
                Seat seat = new Seat(i, SeatType.REGULAR);
                scn.addSeatToScreen(seat);
            }

            for (int i = 10; i < 20; i ++)  {
                Seat seat = new Seat(i, SeatType.PREMIUM);
                scn.addSeatToScreen(seat);
            }
        }

        Address address = new Address("Bengaluru", "p 199", null, "Karnatka", "India", "122001");
        Address address1 = new Address("Gurgaon", "h 199", null, "Haryana", "India", "122002");

        Theatre theatre = new Theatre(address, "Ambienece ", new ArrayList<>());
        Theatre theatre1 = new Theatre(address1, "Vega", new ArrayList<>());

        Show show = new Show(movie1, LocalDateTime.of(2026, 8, 11, 17, 30), movie1.getMovieDuration(), screen, theatre);
        Show show1 = new Show(movie2, LocalDateTime.of(2026, 8, 11, 19, 30), movie2.getMovieDuration(), screen1, theatre);
        Show show2 = new Show(movie3, LocalDateTime.of(2026, 8, 11, 7, 30), movie3.getMovieDuration(), screen3, theatre1);
        Show show3 = new Show(movie1, LocalDateTime.of(2026, 8, 12, 8, 30), movie1.getMovieDuration(), screen4, theatre1);


        theatreService.createTheatre(theatre);
        theatreService.createTheatre(theatre1);

        theatreService.addScreenToTheatre(theatre.getTheatreId(), screen);
        theatreService.addScreenToTheatre(theatre.getTheatreId(), screen1);

        theatreService.addScreenToTheatre(theatre1.getTheatreId(), screen3);
        theatreService.addScreenToTheatre(theatre1.getTheatreId(), screen4);

        showService.createShow(show);
        showService.createShow(show1);
        showService.createShow(show2);
        showService.createShow(show3);
    }
}
