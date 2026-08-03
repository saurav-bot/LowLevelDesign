package InterviewQuestions.UrlShortner;

import InterviewQuestions.UrlShortner.repository.UrlRepository;
import InterviewQuestions.UrlShortner.service.UrlService;
import InterviewQuestions.UrlShortner.service.UrlShortCodeGenerator;

public class TinyUrlDemo {
    public static void main(String[] args) {
        UrlRepository urlRepository = new UrlRepository();
        UrlShortCodeGenerator urlShortCodeGenerator = UrlShortCodeGenerator.getUrlShortCodeGenerator();
        UrlService urlService = new UrlService(urlRepository, urlShortCodeGenerator);

        String shortCode = "";
        String original = "helloworlds";
        for (int i= 0; i< 10; i++){
            try {
                original += i;
                shortCode = urlService.generateShortCode(original );
                original = urlService.getOriginalUrl(shortCode);

                System.out.println(shortCode + " : " + original);
            } catch (Exception ex){
                System.out.println("Eerror occurred while : "+ex.getMessage());
            }
        }

        try {
            shortCode = urlService.getOriginalUrl("sdfsdfsdfsdf");
        } catch (Exception ex) {
            System.out.println("Exception occurred: "+ex);
        }
    }
}
