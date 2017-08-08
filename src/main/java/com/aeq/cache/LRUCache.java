package com.aeq.cache;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


/**
 * Created by gopal on 04/08/17.
 */
public class LRUCache<K, V> implements Map<K, V> {

    private final ConcurrentHashMap<K,Node<K, V>> keyMap;
    private final DoublyLinkedList queue;

    private final Long ttl;



    public LRUCache(Long ttl) {
        this.ttl = ttl;
        keyMap = new ConcurrentHashMap<>();
        queue = new DoublyLinkedList<>();

        Timer timer = new Timer(false);
        timer.schedule(new CleanerTask(), 0,10);     //Trigger every 10 mills - not a better way to do it.

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
                 if(queue.getTail().get() != null) {
                     Node tail = (Node) queue.getTail().get();
                     if(System.currentTimeMillis() - tail.lastAccessed > ttl) {
                         Node toBeTail = tail.previous;
                         remove(tail.key);      //remove from keymap
                         tail = toBeTail;
                         continueCleanup = true;
                         queue.setTail(tail);

                     } else {
                         continueCleanup = false;
                     }
                 }
            } while (continueCleanup);
        }
    }


}
