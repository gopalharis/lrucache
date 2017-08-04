package com.aeq.cache;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


/**
 * Created by gopal on 04/08/17.
 */
public class LRUCache<K, V> implements Map<K, V> {

    private ConcurrentHashMap<K,Node<K, V>> keyMap = new ConcurrentHashMap();
    private DoublyLinkedList queue = new DoublyLinkedList();

    private Duration ttl;

    private Timer timer = new Timer(false);

    public LRUCache(Duration ttl) {
        this.ttl = ttl;
        CleanerTask cleanerTask = new CleanerTask();
        timer.schedule(cleanerTask, 0,10);     //Trigger every 10 mills - not a better way to do it.
    }




    @Override
    public V put(K key, V value) {
        Node node =  queue.add(key, value);
        this.keyMap.put(key, node);
        return value;
    }



    @Override
    public V get(Object key) {
        Node<K, V> itemFound = keyMap.get(key);
        if(itemFound != null) {     //Latest Accessed item to be made as head.
            queue.updateAfterAccess(itemFound);
            return itemFound.value;
        } else {
            return null;
        }

    }

    @Override
    public int size() {
        return keyMap.size();
    }

    @Override
    public boolean isEmpty() {
        return keyMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return keyMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return keyMap.containsValue(value);     //TODO: fix it later. iterate over key-set and get each node's value and compare
    }



    @Override
    public V remove(Object key) {
        Node<K, V> node =  keyMap.get(key);
        if(node != null) {
            keyMap.remove(key);
            queue.removeElement(node);
        }
        return (node != null) ? node.value : null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        //TODO: implement later.
    }

    @Override
    public void clear() {

    }

    @Override
    public Set<K> keySet() {
        return this.keyMap.keySet();
    }

    @Override
    public Collection<V> values() {
        return this.keyMap.values().stream().map(v -> v.value).collect(Collectors.toList());
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        //TODO: implement later;
        return null;
    }

    public class CleanerTask extends TimerTask {

        @Override
        public void run() {
            boolean continueCleanup = false;
            do {

                AtomicReference<Node> tail = queue.getTail();

                 if(tail.get() != null) {

                     if(Duration.between(tail.get().lastAccessed, LocalDateTime.now()).toMillis() > ttl.toMillis()) {
                         Node toBeTail = tail.get().previous;
                         remove(tail.get().key);      //remove from keymap
                         tail.set(toBeTail);
                         continueCleanup = true;
                         queue.setTail(tail);

                     } else {
                         continueCleanup = false;
                     }
                 }
            } while (continueCleanup);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.timer.purge();
    }
}
