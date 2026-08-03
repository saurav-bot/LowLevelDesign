package InterviewQuestions.UrlShortner.repository;

import InterviewQuestions.UrlShortner.entities.UrlDetails;

import java.util.Optional;


public interface UrlRepository {
    Optional<UrlDetails> findByShortCode(String shortCode);
    Optional<UrlDetails> findByLongUrl(String longUrl);
    UrlDetails saveIfAbsent(UrlDetails urlDetails);
    boolean existsByShortCode(String shortCode);
    void deleteByShortCode(String shortCode);
}