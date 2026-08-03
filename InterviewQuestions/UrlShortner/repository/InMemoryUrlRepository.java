package InterviewQuestions.UrlShortner.repository;

import InterviewQuestions.UrlShortner.entities.UrlDetails;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Optional;


public class InMemoryUrlRepository implements UrlRepository {
    private final Map<String, UrlDetails> shortCodeMap = new ConcurrentHashMap<>();
    private final Map<String, UrlDetails> longUrlMap = new ConcurrentHashMap<>();

    public Optional<UrlDetails> findByShortCode(String shortCode) {
//        System.out.println(shortCodeMap.get(shortCode).toString());
        return Optional.ofNullable(shortCodeMap.get(shortCode));
    }

    public Optional<UrlDetails> findByLongUrl(String longUrl) {
        return Optional.ofNullable(longUrlMap.get(longUrl));
    }

    public synchronized UrlDetails saveIfAbsent(UrlDetails urlDetails){
        UrlDetails existing = longUrlMap.putIfAbsent(urlDetails.getOriginalUrl(), urlDetails);
        if (existing != null){
            return existing;
        }

        UrlDetails shortExisting = shortCodeMap.putIfAbsent(urlDetails.getShortCode(), urlDetails);
        if (shortExisting != null){
            longUrlMap.remove(urlDetails.getOriginalUrl());
            return null;
        }

        return urlDetails;
    }

    public boolean existsByShortCode(String shortCode) {
        return shortCodeMap.get(shortCode) != null;
    }

    public synchronized void deleteByShortCode(String shortCode) {
        UrlDetails urlDetails = shortCodeMap.get(shortCode);
        if (urlDetails == null){
            throw new RuntimeException("Short code does not exists: ");
        }
        shortCodeMap.remove(shortCode);
        longUrlMap.remove(urlDetails.getOriginalUrl());
    }
}
