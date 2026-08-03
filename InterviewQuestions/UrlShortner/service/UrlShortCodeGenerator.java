package InterviewQuestions.UrlShortner.service;

import java.util.concurrent.atomic.AtomicLong;

public class UrlShortCodeGenerator {

    private final AtomicLong shortCode = new AtomicLong(1000000);
    private final ShortCodeGenerator shortCodeGenerator = new Base62Generator();


    private UrlShortCodeGenerator() {
    }

    public static class Holder{
        private final static UrlShortCodeGenerator INSTANCE = new UrlShortCodeGenerator();
    }

    public static UrlShortCodeGenerator getUrlShortCodeGenerator(){
        return Holder.INSTANCE;
    }

    public String getNextShortCode(){
        long number = shortCode.incrementAndGet();
        return shortCodeGenerator.generate(number);
    }

}
