package InterviewQuestions.LRU;

import InterviewQuestions.LRU.service.LRUCache;

import java.time.Duration;

public class LRUCacheDemo {
    public static void main(String[] args) {
        LRUCache<String, String> cache = new LRUCache<>();

        cache.putVal("hello", "world");
        cache.putVal("how", "are");
        cache.putVal("are", "you");

        System.out.println(cache.getValue("hello"));
        System.out.println(cache.getValue("how"));
        System.out.println(cache.getValue("are"));

        cache.putVal("yo", "oy");


        System.out.println(cache.getValue("how"));
        System.out.println(cache.getValue("are"));


        cache.putVal("time", "test", Duration.ofMillis(100));
        System.out.println("Before key expires " + cache.getValue("time"));
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("After key expires " + cache.getValue("time"));
//        System.out.println();
//        System.out.println();
    }
}
