package InterviewQuestions.UrlShortner.repository;

import InterviewQuestions.UrlShortner.entities.UrlDetails;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UrlRepository {

    private final Map<String, UrlDetails> shortCodeUrlMap = new ConcurrentHashMap<>();
    private final Map<String, UrlDetails> longUrlMap = new ConcurrentHashMap<>();

    public UrlDetails getUrlDetails(String shortCode) {
        return shortCodeUrlMap.get(shortCode);
    }

    public UrlDetails getUrlDetailsByLongUrl(String longUrl){
        return longUrlMap.get(longUrl);
    }

    public UrlDetails setUrlDetails(String longUrl, String shortCode){
        UrlDetails urlDetails = new UrlDetails(longUrl, shortCode);

        UrlDetails existing = longUrlMap.putIfAbsent(longUrl, urlDetails);
        if (existing != null) {
            return existing;
        }
        shortCodeUrlMap.put(shortCode, urlDetails);

        return urlDetails;
    }
}