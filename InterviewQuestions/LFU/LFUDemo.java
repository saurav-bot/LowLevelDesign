package InterviewQuestions.LFU;

import InterviewQuestions.LFU.service.CacheService;

public class LFUDemo {
    public static void main(String[] args){
        CacheService<String, String> cacheService = new CacheService<>();

        System.out.println(cacheService.get("hello"));
        cacheService.put("hello", "world");
        System.out.println(cacheService.get("hello"));
        cacheService.remove("hello");
        System.out.println(cacheService.get("hello"));

    }
}
