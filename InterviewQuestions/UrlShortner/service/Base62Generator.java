package InterviewQuestions.UrlShortner.service;

public class Base62Generator implements ShortCodeGenerator{

    @Override
    public String generate(long input) {
       return Base62.encode(input);
    }
}
