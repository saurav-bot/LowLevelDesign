package InterviewQuestions.UrlShortner;

import InterviewQuestions.UrlShortner.service.UrlService;

import java.time.Duration;

public class TinyUrlDemo {
    public static void main(String[] args) {
        UrlService urlService = new UrlService();

        // 1. BASE CASE
        String longUrl = "https://hellow.com/?hi=hello";
        String shortCodeUrl  = urlService.shortenUrl(longUrl);
        System.out.println(shortCodeUrl + " : " + urlService.getOriginalUrl(shortCodeUrl));

        // 2. Custom Alias Case
        String longUrl2 = "https://test2.com/?how=are";
        String customAlias = "railway";
        String customShortUrl = urlService.shortenUrl(longUrl2, customAlias);

        System.out.println(customShortUrl + " : " + urlService.getOriginalUrl(customShortUrl));

        // 3. Custom Alias Already exists
        try {
            String custom = urlService.shortenUrl(longUrl2, customAlias);
        } catch (Exception ex) {
            System.out.println("exception occurred: " + ex.getMessage());
        }

        // 4. Count Case

        urlService.getOriginalUrl(shortCodeUrl);
        urlService.getOriginalUrl(shortCodeUrl);
        System.out.println(shortCodeUrl + " : " + urlService.getClickCount(shortCodeUrl));


        // 5. Expiration test
        String shortcodeUrl = urlService.shortenUrl("https://ycoimbinator.com/?hello=world", Duration.ofMillis(100));
        try {
            Thread.sleep(150);
            System.out.println(urlService.getOriginalUrl(shortcodeUrl));
        } catch (Exception ex) {
            System.out.println("Exception occurred: " + ex.getMessage());
        }
    }
}
