package InterviewQuestions.LFU.model;

public class FrequencyNode<K, V> {
    protected FrequencyNode<K, V> next;
    protected FrequencyNode<K, V> prev;
    protected DoublyLinkedList<K,V> dll;
    protected Long freqCount;


    public FrequencyNode(Long freqCount) {
        this.next = null;
        this.prev = null;
        this.dll = new DoublyLinkedList<>();
        this.freqCount = freqCount;
    }

    public DoublyLinkedList<K, V> getDll() {
        return dll;
    }

    public void setNext(FrequencyNode<K, V> next) {
        this.next = next;
    }

    public void setPrev(FrequencyNode<K, V> prev) {
        this.prev = prev;
    }

    public FrequencyNode<K, V> getNext() {
        return this.next;
    }

    public FrequencyNode<K, V> getPrev() {
        return this.prev;
    }

    public Long getFreqCount() {
        return this.freqCount;
    }
}
