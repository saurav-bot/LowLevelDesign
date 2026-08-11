//package InterviewQuestions.BookMyShow.service;
//
//import InterviewQuestions.BookMyShow.entities.Movie;
//
//import java.util.List;
//import java.util.concurrent.ConcurrentHashMap;
//
//public class MovieService {
//    ConcurrentHashMap<String, Movie> nameVsMovie = new ConcurrentHashMap<>();
//
//    public void createMovie(Movie movie) {
//        Movie movie1 = nameVsMovie.putIfAbsent(movie.getMovieName(), movie);
//        if (movie1 != null){
//            throw new RuntimeException("Movie already exists " + movie1.getMovieName());
//        }
//    }
//
//    public Movie getMovieByName(String name) {
//        // should we raise error if movie does not exists
//        return nameVsMovie.get(name);
//    }
//
////    public List<Movie> getMoviesByCity(String city) {
////
////    }
//}
