package InterviewQuestions.LRU;

import InterviewQuestions.LRU.service.LRUCache;

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
//        System.out.println();
//        System.out.println();
    }
}
