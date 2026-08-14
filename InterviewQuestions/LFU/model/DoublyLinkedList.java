package InterviewQuestions.LFU.model;

public class DoublyLinkedList<K, V> {
    private final CacheEntry<K, V> head;
    private final CacheEntry<K, V> tail;

    public DoublyLinkedList() {
        this.head = new CacheEntry<>(null, null);
        this.tail = new CacheEntry<>(null, null);
        head.next = tail;
        tail.prev = head;
    }

    public void addLast(CacheEntry<K, V> newNode) {
        newNode.next = tail;
        newNode.prev = tail.prev;
        tail.prev.next = newNode;
        tail.prev = newNode;

    }

    public CacheEntry<K, V> removeFirst(){
        if (isEmpty()){
            return null;
        }
        CacheEntry<K, V> node = head.next;
        remove(node);

        return node;
    }

    public void remove(CacheEntry<K, V> node) {
        if (node.prev == null || node.next == null) {
            return;
        }
        node.next.prev = node.prev;
        node.prev.next = node.next;
    }

    public void moveToLast(CacheEntry<K,V> node) {
        remove(node);
        addLast(node);
    }

    public boolean isEmpty(){
        return head.next == tail;
    }
}
