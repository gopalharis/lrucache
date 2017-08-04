package com.aeq.cache;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by gopal on 04/08/17.
 */
public class DoublyLinkedList<K, V> {

    private AtomicReference<Node<K, V>> head = new AtomicReference<>();
    private AtomicReference<Node<K, V>> tail = new AtomicReference<>();

    public Node<K, V> add(K key, V value) {
        Node<K, V> node = new Node<>(key, value);

        if(head.get() == null) {
            head.set(node);
        } else {
            node.next = head.get();
            head.get().previous = node;
            head.set(node);
        }

        if(tail.get()==null) {
            tail.set(head.get());
        }

        return node;
    }

    public void removeElement(Node node) {
        if(node==head.get()) head.set(head.get().next);
        else if(node==tail.get()) tail.set(tail.get().previous);
        else if(node.previous != null) node.previous.next = node.next;
        else if(node.next != null) node.next.previous = node.previous;
        node.next = null;
        node.previous = null;
    }

    public void updateAfterAccess(Node node) {
        removeElement(node);
        node.lastAccessed = LocalDateTime.now();
        node.next = head.get();
        if(head != null) head.get().previous = node;
        head.set(node);
    }


    public AtomicReference<Node<K,V>> getTail() {
        return tail;
    }

    public void setTail(AtomicReference<Node<K, V>> tail) {
        this.tail = tail;
    }
}
