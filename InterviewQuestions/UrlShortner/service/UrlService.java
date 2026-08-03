package InterviewQuestions.UrlShortner.service;

import InterviewQuestions.UrlShortner.entities.UrlDetails;
import InterviewQuestions.UrlShortner.exceptions.UrlShortnerException;
import InterviewQuestions.UrlShortner.repository.InMemoryUrlRepository;
import InterviewQuestions.UrlShortner.repository.UrlRepository;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

public class UrlService {
    private static final String BASE_URL = "https://short.it/";
    private static final int MAX_COLLISION = 5;

    private final String baseUrl;
    private final UrlRepository urlRepository;
    private final ShortCodeGenerator shortCodeGenerator;

    public UrlService(UrlRepository urlRepository, ShortCodeGenerator shortCodeGenerator, String baseUrl){
        this.urlRepository = urlRepository;
        this.shortCodeGenerator = shortCodeGenerator;
        this.baseUrl = baseUrl;
    }

    public UrlService() {
        this(new InMemoryUrlRepository(), new Base62Generator(), BASE_URL);
    }

    public String shortenUrl(String longUrl) {
        return shortenUrl(longUrl, null, null);
    }

    public String shortenUrl(String longUrl, String alias) {
        return shortenUrl(longUrl, alias, null);
    }

    public String shortenUrl(String longUrl, Duration ttl) {
        return shortenUrl(longUrl, null, ttl);
    }


    public String shortenUrl(String longUrl, String customAlias, Duration ttl) {
        validateUrl(longUrl);

        Instant expiredAt = (ttl != null) ? Instant.now().plus(ttl) : null;

        if (customAlias != null && !customAlias.isBlank()) {
            String sanitizedAlias = customAlias.trim();
            if (urlRepository.existsByShortCode(sanitizedAlias)) {
                throw new UrlShortnerException.AliasAlreadyExistException(sanitizedAlias);
            }
            UrlDetails urlDetails = new UrlDetails(sanitizedAlias, longUrl, expiredAt);
            urlRepository.saveIfAbsent(urlDetails);

            return baseUrl + sanitizedAlias;
        }

        Optional<UrlDetails> existing = urlRepository.findByLongUrl(longUrl);
        if (existing.isPresent() && !existing.get().isExpired()) {
            return baseUrl + existing.get().getShortCode();
        }

        for(int attempt = 0; attempt < MAX_COLLISION; attempt ++){
            String candidateInput = (attempt == 0) ? longUrl : longUrl + "#" + attempt;
            String shortCode = shortCodeGenerator.generate(candidateInput);

            UrlDetails urlDetails = new UrlDetails(shortCode, longUrl, expiredAt);

            UrlDetails saved = urlRepository.saveIfAbsent(urlDetails);

            if (saved != null){
                return baseUrl + saved.getShortCode();
            }
        }

        throw new RuntimeException(("Failed to generate unique short code after retries. Please retry."));

    }

    public String getOriginalUrl(String shortCodeUrl){
        String shortCode = extractShortCode(shortCodeUrl);

        UrlDetails details = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlShortnerException.URLNotFoundException("Url not found for " + shortCode));

        if (details.isExpired()) {
            urlRepository.deleteByShortCode(shortCode);
            throw new UrlShortnerException.UrlAlreadyExpiredException(shortCodeUrl);
        }

        details.incrementClickCount();

        return details.getOriginalUrl();
    }

    private String extractShortCode(String shortCodeUrl) {
        if (shortCodeUrl == null || shortCodeUrl.isBlank()) {
            throw new UrlShortnerException.UrlInvalidException("Short code or URL must not be blank");
        }

        if (shortCodeUrl.contains("/")) {
            return shortCodeUrl.substring(shortCodeUrl.lastIndexOf("/")+1);
        }

        return shortCodeUrl.trim();
    }

    public long getClickCount(String shortCodeUrl) {
        String shortCode = extractShortCode(shortCodeUrl);

        UrlDetails details = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlShortnerException.URLNotFoundException("Url not found for " + shortCode));

        return details.getClickCount();
    }

    private void validateUrl(String url){
        if (url == null || url.isBlank()){
            throw new UrlShortnerException.UrlInvalidException("Url must not be null or empty");
        }
        try {
            URI uri = URI.create(url.trim());
            String scheme = uri.getScheme();
            if(scheme == null || (!scheme.equalsIgnoreCase("http") &&
                    !scheme.equalsIgnoreCase("https"))) {
                throw new UrlShortnerException.UrlInvalidException(url);
            }

            if (uri.getHost() == null){
                throw new UrlShortnerException.UrlInvalidException(url);
            }
        } catch (Exception e) {
            throw new UrlShortnerException.UrlInvalidException(url);
        }
    }

    private String getShortCode(String longUrl) {
        return shortCodeGenerator.generate(longUrl);
    }
}
