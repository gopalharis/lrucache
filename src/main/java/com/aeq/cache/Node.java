package com.aeq.cache;

import java.time.LocalDateTime;

class Node<K, V> {

    protected K key;
    protected V value;
    protected LocalDateTime lastAccessed;
    protected Node<K, V> next;
    protected Node<K, V> previous;

    public Node(K key, V value) {
        this.key = key;
        this.value = value;
        this.lastAccessed = LocalDateTime.now();
    }


}