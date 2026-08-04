package InterviewQuestions.UrlShortner.repository;

import InterviewQuestions.UrlShortner.entities.UrlDetails;
import InterviewQuestions.UrlShortner.exceptions.UrlShortnerException;

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

    public UrlDetails saveCustomAlias(UrlDetails urlDetails){
        UrlDetails existing = shortCodeMap.putIfAbsent(urlDetails.getShortCode(), urlDetails);
        if (existing != null){
            return null;
        }
        longUrlMap.put(urlDetails.getOriginalUrl(), urlDetails);

        return urlDetails;
    }

    public UrlDetails saveIfAbsent(UrlDetails urlDetails){
        UrlDetails existing = longUrlMap.putIfAbsent(urlDetails.getOriginalUrl(), urlDetails);
        if (existing != null){
            return existing;
        }

        UrlDetails shortExisting = shortCodeMap.putIfAbsent(urlDetails.getShortCode(), urlDetails);
        if (shortExisting != null){
            longUrlMap.remove(urlDetails.getOriginalUrl(), urlDetails);
            return null;
        }

        return urlDetails;
    }

    public boolean existsByShortCode(String shortCode) {
        return shortCodeMap.get(shortCode) != null;
    }

    public void deleteByShortCode(String shortCode) {
        UrlDetails removed = shortCodeMap.remove(shortCode);
        if (removed == null){
            throw new UrlShortnerException.UrlInvalidException("Short code does not exists: ");
        }

        // Used key, value to remove since multiple shortcode can be used to remove same longUrl
        longUrlMap.remove(removed.getOriginalUrl(), removed);
    }
}
