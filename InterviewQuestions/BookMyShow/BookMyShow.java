package InterviewQuestions.BookMyShow;

import InterviewQuestions.BookMyShow.entities.*;
import InterviewQuestions.BookMyShow.enums.SeatType;
import InterviewQuestions.BookMyShow.service.BookingService;
import InterviewQuestions.BookMyShow.service.TheatreService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BookMyShow {
    public static void main(String[] args) {
        TheatreService theatreService = new TheatreService();
        BookingService bookingService = new BookingService(theatreService);

        initialize(theatreService, bookingService);
    }

    public static void initialize(TheatreService theatreService, BookingService bookingService) {
        Movie movie1 = new Movie("Bahubali", "war crime", "150");
        Movie movie2 = new Movie("Spiderman", "war crime", "140");
        Movie movie3 = new Movie("Odyssey", "war crime", "180");

        List<Seat> seats = new ArrayList<>();
        for (int i = 0; i < 10; i ++)  {
            Seat seat = new Seat(i, SeatType.REGULAR);
            seats.add(seat);
        }

        for (int i = 10; i < 20; i ++)  {
            Seat seat = new Seat(i, SeatType.PREMIUM);
        }

        Screen screen = new Screen(seats);
        Screen screen1 = new Screen(seats);
        Screen screen3 = new Screen(seats);
        Screen screen4 = new Screen(seats);

        Address address = new Address("Bengaluru", "p 199", null, "Karnatka", "India", "122001");
        Address address1 = new Address("Gurgaon", "h 199", null, "Haryana", "India", "122002");

        Theatre theatre = new Theatre(address, "Ambienece ", new ArrayList<>());
        Theatre theatre1 = new Theatre(address1, "Vega", new ArrayList<>());

        Show show = new Show(movie1, "17:30", "150", screen, theatre);
        Show show1 = new Show(movie2, "19:30", movie2.getMovieDuration(), screen1, theatre);
        Show show2 = new Show(movie3, "7:30", "180", screen3, theatre1);
        Show show3 = new Show(movie1, "17:30", "150", screen4, theatre1);


        theatreService.createTheatre(theatre);
        theatreService.createTheatre(theatre1);

        theatreService.addScreenToTheatre(theatre.getTheatreId(), screen);
        theatreService.addScreenToTheatre(theatre.getTheatreId(), screen1);

        theatreService.addScreenToTheatre(theatre1.getTheatreId(), screen3);
        theatreService.addScreenToTheatre(theatre1.getTheatreId(), screen4);

        theatreService.createShow(theatre.getTheatreId(), screen.getScreenId(), show);
        theatreService.createShow(theatre.getTheatreId(), screen1.getScreenId(), show1);
        theatreService.createShow(theatre1.getTheatreId(), screen3.getScreenId(), show2);
        theatreService.createShow(theatre1.getTheatreId(), screen4.getScreenId(), show3);

        String selectedCity = "Gurgaon";
        Set<Movie> movies = theatreService.getAllMovieInCity(selectedCity);
        System.out.println("Movie in " + selectedCity);
        Movie selectedMovie = null;
        for (Movie movie : movies) {
            selectedMovie = movie;
            System.out.println(movie.getMovieId() + " " + movie.getMovieName());
        }


        List<Show> shows = theatreService.getAllShowOfMovieInCity(selectedCity, selectedMovie.getMovieId());

        System.out.println("Shows available for movie: " + selectedMovie.getMovieName() + " in city: " + selectedCity);

        for (Show s : shows) {
            System.out.println(" " + s.getShowId() + " start time" + s.getShowStartTime() + " Screen " + s.getShowScreen().getScreenId());
        }

        List<ShowSeat> bookedSeat = bookingService.getAvailableSeatForShow(show.getShowId());
        Booking book1 = bookingService.book(show.getTheatre(), show.getShowScreen(), show, bookedSeat);
        System.out.println("Booking: " + book1.getBookingStatus());


        Booking book2 = bookingService.book(show.getTheatre(), show.getShowScreen(), show, bookedSeat);
        System.out.println("Booking: " + book2.getBookingStatus());





    }
}
