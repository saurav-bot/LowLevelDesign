//package InterviewQuestions.RateLimiter;
//
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//public class TokenBucketStrategy implements RateLimitStrategy{
//
////    public boolean isValid(String userId) {
////        return true;
////    }
//    private final Map<RuleType, Map<String, TokenBucket>>  tokenBuckets = new ConcurrentHashMap<>();
//
//    public TokenBucketStrategy() {
//        for(RuleType ruleType: RuleType.values()){
//            tokenBuckets.put(ruleType, new ConcurrentHashMap<>());
//        }
//    }
//
//    public RateLimitResult tryConsume(RequestMetadata requestMetadata) {
////        RateLimitResult rateLimitResult = new RateLimitResult();
////        try {
////            if (requestMetadata.getUserId() != null){
////                if (tokenBuckets.get(RuleType.USER).get(requestMetadata.getUserId()) == null) {
////                    tokenBuckets.get(RuleType.USER).put(requestMetadata.getUserId(), new TokenBucket(requestMetadata.getUserId(), ))
////                }
////            }
////        } catch (Exception ex) {
////            System.out.println("Exception occurred while rate limiting " + ex.getMessage());
////        }
//        return rateLimitResult;
//    }
//}
