package InterviewQuestions.UrlShortner.service;

public class Base62 {
    public static final String ALPHABETS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final int BASE = ALPHABETS.length();

    public static String encode(long input) {
        if (input == 0) {
            return String.valueOf(ALPHABETS.charAt((int) input));
        }
        StringBuilder sb = new StringBuilder();
        while (input > 0) {
            sb.append(ALPHABETS.charAt((int) input % BASE));
            input /= BASE;
        }

        return sb.reverse().toString();

    }
}
