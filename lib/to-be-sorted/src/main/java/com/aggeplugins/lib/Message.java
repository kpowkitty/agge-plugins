/**
 * Generic Message to be sent between systems via MessageBus.
 *
 * @param <T> key
 * The Message's key (is generic, behaves as an identifier).
 * @param <U> val
 * The Message's value (usually a data structure or a bool).
 */

package com.aggeplugins.lib;

import com.aggeplugins.lib.*;
import com.aggeplugins.lib.export.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Message<K, V> {
    private K key;
    private V value;
    public Message(K key, V value) {
        //log.info("A Message was created!");
        //log.info("Message key: " + key);
        //log.info("Message value: " + value);
        this.key = key;
        this.value = value;
    }
    public K getKey()
    {
        return this.key;
    }
    public V getValue()
    {   
        return this.value;
    }
}
