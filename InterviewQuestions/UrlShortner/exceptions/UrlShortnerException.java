package InterviewQuestions.UrlShortner.exceptions;

public class UrlShortnerException extends RuntimeException {
    public UrlShortnerException(String message) {
        super(message);
    }

    public static class URLNotFoundException extends UrlShortnerException {
        public URLNotFoundException(String message) {
            super("Url does not exists " + message);
        }
    }

    public static class UrlInvalidException extends UrlShortnerException {
        public UrlInvalidException(String message) {
            super("Url is invalid " + message);
        }
    }

    public static class AliasAlreadyExistException extends UrlShortnerException {
        public AliasAlreadyExistException(String message) {
            super("Alias already exists "+ message);
        }
    }

    public static class UrlAlreadyExpiredException extends UrlShortnerException {
        public UrlAlreadyExpiredException(String message) {
            super("Url already expired: " + message);
        }
    }
}
