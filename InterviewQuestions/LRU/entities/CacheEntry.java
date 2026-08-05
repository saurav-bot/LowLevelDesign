package InterviewQuestions.LRU.entities;

public class CacheEntry<K,V> {
    K key;
    V val;
    CacheEntry<K, V> prev;
    CacheEntry<K, V> next;

    public CacheEntry(K key, V val){
        this.key = key;
        this.val = val;
    }

    public K getKey(){
        return this.key;
    }

    public V getVal() {
        return this.val;
    }

    public void setVal(V val){
        this.val = val;
    }

}
