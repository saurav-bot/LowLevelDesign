package InterviewQuestions.UrlShortner.service;

import java.util.concurrent.atomic.AtomicLong;

public class Base62Generator implements ShortCodeGenerator{
    private final AtomicLong counter;
    public Base62Generator(long seed) {
        this.counter = new AtomicLong(seed);
    }

    public Base62Generator() {
        this.counter = new AtomicLong(1000000);
    }

    @Override
    public String generate(String longUrl) {
        long nextId = counter.incrementAndGet();
        return Base62.encode(nextId);
    }
}
