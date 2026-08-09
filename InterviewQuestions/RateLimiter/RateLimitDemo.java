package InterviewQuestions.RateLimiter;

import InterviewQuestions.RateLimiter.models.RateLimitResult;
import InterviewQuestions.RateLimiter.models.RateLimitRule;
import InterviewQuestions.RateLimiter.models.RequestMetadata;
import InterviewQuestions.RateLimiter.models.RuleType;
import InterviewQuestions.RateLimiter.service.RateLimiterService;
import InterviewQuestions.RateLimiter.strategy.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RateLimitDemo {
    public static void main(String[] args) {
        SlidingWindowCounter slidingWindowCounter = new SlidingWindowCounter( 40000);
        SlidingWindowLogStrategy slidingWindowLogStrategy = new SlidingWindowLogStrategy(10000);
        TokenBucketStrategy tokenBucketStrategy = new TokenBucketStrategy();
        LeakyBucketStrategy leakyBucketStrategy = new LeakyBucketStrategy();
        RateLimiterService rateLimiterService = getRateLimiterService( leakyBucketStrategy);

        ExecutorService executor = Executors.newFixedThreadPool(10);
        for(int i = 0; i < 1; i ++){
            executor.execute(() -> helper(rateLimiterService));
        }
        executor.shutdown();
//        executors.execute();

//        for(int i=0; i < 30; i ++){
//            int finalI = i;
//            Thread t1 = new Thread(() -> {
//
//            });
//            t1.run();
//
//        }
//        rateLimiterService.
    }

    private static RateLimiterService getRateLimiterService(RateLimitStrategy slidingWindowLogStrategy) {
        RateLimiterService rateLimiterService = new RateLimiterService(slidingWindowLogStrategy);
        RateLimitRule global = new RateLimitRule("1", RuleType.GLOBAL, null, 10, 2, 2);
        RateLimitRule user = new RateLimitRule("2", RuleType.USER, null, 10, 5, 5);
        RateLimitRule resource = new RateLimitRule("3", RuleType.RESOURCE, "test", 10, 3, 3);
        RateLimitRule ip = new RateLimitRule("4", RuleType.IP, null, 6, 4, 4);

        rateLimiterService.addRateLimitRules(global);
        rateLimiterService.addRateLimitRules(user);
        rateLimiterService.addRateLimitRules(resource);
        rateLimiterService.addRateLimitRules(ip);
        return rateLimiterService;
    }


    private static void helper(RateLimiterService rateLimiterService) {
        for (int i = 0; i < 50; i ++){
            RequestMetadata metadata = new RequestMetadata("1", "1", "test", 1);
            RateLimitResult result = rateLimiterService.isValid(metadata);
            System.out.println(System.currentTimeMillis() + " " + Thread.currentThread().getName() + " Result " + i + " " + result.isAllowed() + " entity failure: " + result.getRetryAfterSeconds() + " ent: " + result.getViolatedRuleId());
            if (!result.isAllowed()) {
                try {
                    Thread.sleep(result.getRetryAfterSeconds()*1000);
                } catch (Exception ex) {
                    System.out.println("excpetion occurred: " + ex.getMessage());
                }
            }
        }
    }
}
