package InterviewQuestions.LRU.entities;

public class DoublyLinkedList<K, V> {
    private final CacheEntry<K, V> head;
    private final CacheEntry<K, V> tail;

    public DoublyLinkedList() {
        this.head = new CacheEntry<>(null, null);
        this.tail = new CacheEntry<>(null, null);
        head.next = tail;
        tail.prev =  head;
    }

    public void addLast(CacheEntry<K, V> node) {
        node.prev = tail.prev;
        node.next = tail;
        tail.prev.next = node;
        tail.prev = node;
    }

    public void removeNode(CacheEntry<K, V> node){
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    public void moveToLast(CacheEntry<K, V> node){
        removeNode(node);
        addLast(node);
    }

    public CacheEntry<K, V> removeFirst() {
        if (head.next == tail) {
            return null;
        }
        CacheEntry<K, V> first = head.next;
        removeNode(first);

        return first;
    }
}
