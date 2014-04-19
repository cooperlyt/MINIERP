package com.dgsoft.common.utils;

import java.util.LinkedHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 19/04/14
 * Time: 11:26
 */
public class LRULinkedHashMap<K, V> extends LinkedHashMap<K, V> {

    private final int maxCapacity;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private final Lock lock = new ReentrantLock();

    public LRULinkedHashMap(int maxCapacity)
    {
        super(maxCapacity, DEFAULT_LOAD_FACTOR, true);
        this.maxCapacity = maxCapacity;
    }

    @Override
    protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest)
    {
        return size() > maxCapacity;
    }

    @Override
    public V get(Object key)
    {
        try {
            lock.lock();
            return super.get(key);
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public V put(K key, V value)
    {
        try {
            lock.lock();
            return super.put(key, value);
        }
        finally {
            lock.unlock();
        }
    }

}
