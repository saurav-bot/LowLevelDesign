//package InterviewQuestions.BookMyShow.service;
//
//import InterviewQuestions.BookMyShow.entities.Screen;
//import InterviewQuestions.BookMyShow.entities.Show;
//
//import java.util.List;
//import java.util.concurrent.ConcurrentHashMap;
//
//public class ScreensService {
//    ConcurrentHashMap<String, Screen> screens = new ConcurrentHashMap<>();
//
//    public void setScreens(Screen screen){
//        screens.put(screen.getScreenId(), screen);
//    }
//
//    public Screen getScreen(String screenId) {
//        return this.screens.get(screenId);
//    }
////
////    public List<Show> getShowsOfScreen(String screenId) {
////        return this.screens.get(screenId).getShows();
////    }
//}
