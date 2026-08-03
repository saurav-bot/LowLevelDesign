package InterviewQuestions.UrlShortner.service;

import InterviewQuestions.UrlShortner.entities.UrlDetails;
import InterviewQuestions.UrlShortner.repository.UrlRepository;

import java.util.Objects;

public class UrlService {

    private final UrlRepository urlRepository;
    private final UrlShortCodeGenerator urlShortCodeGenerator;

    public UrlService(UrlRepository urlRepository, UrlShortCodeGenerator urlShortCodeGenerator){
        this.urlRepository = urlRepository;
        this.urlShortCodeGenerator = urlShortCodeGenerator;
    }

    public String generateShortCode(String longUrl) {

        UrlDetails urlDetails = urlRepository.getUrlDetailsByLongUrl(longUrl);
        if (Objects.nonNull(urlDetails)) {
            return urlDetails.shortCode();
        }

        String shortCode = getShortCode(longUrl);

        UrlDetails urlDetails1 = urlRepository.setUrlDetails(longUrl, shortCode);
        return urlDetails1.shortCode();

    }

    public String getOriginalUrl(String shortcode){
        UrlDetails urlDetails = urlRepository.getUrlDetails(shortcode);
        if (Objects.isNull(urlDetails)){
            throw new RuntimeException("Invalid shortCode");
        }
        return urlDetails.url();
    }

    private String getShortCode(String longUrl) {
        return urlShortCodeGenerator.getNextShortCode();
    }
}
