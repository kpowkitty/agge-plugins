package com.aggeplugins.lib;

import com.aggeplugins.lib.*;
import com.aggeplugins.lib.export.*;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Iterator;

@Slf4j
public class MessageBus {
    private static MessageBus instance;
    private ConcurrentHashMap<String, Message<?, ?>> messages;
    private ScheduledExecutorService scheduler;

    /**
     * MessageBus is a singleton, protected to throw a compiler error if an
     * object tries to create. It can only instantiate itself, or an object can
     * request an instantiation through instance()
     */
    protected MessageBus()
    {
        try {
            this.messages = new ConcurrentHashMap<>();
        } catch (NullPointerException e) {
            log.info("Unable to create messages map!");
        }
        try {
            this.scheduler = Executors.newScheduledThreadPool(1);
        } catch (NullPointerException e) {
            log.info("Unable to create thread pool!");
        }
    }

    /**
     * Get the instance of the MessageBus.
     */
    public static synchronized MessageBus instance()
    {
        log.info("A MessageBus instance was requested");
        if (instance == null) {
            log.info("No MessageBus instance, creating instance...");
            instance = new MessageBus();
        }
        return instance;
    }

    ///**
    // * Register as a sender. Provide a known ID. Allows recievers to query all
    // * Messages from a sender.
    // * @todo Query all Messages, unimplemented.
    // */
    //public synchronized void register(String id)
    //{
    //    log.info("Registered " + id + " on the MessageBus");
    //    this.messages.putIfAbsent(id, new HashMap<>());
    //}

    ///**
    // * Send a Message with a timeout duration. Registers the sender's ID as a 
    // * key and the Message key.
    // */
    //public synchronized void send(String id, Message<?, ?> msg, 
    //                              long timeout, TimeUnit unit) 
    //{
    //    log.info(id + " sent a timed Message on the MessageBus");
    //    register(id);
    //    this.messages.get(id).put(msg.getKey(), msg.getVal());
    //    this.scheduler.schedule(() ->
    //        messages.get(id).remove(msg.getKey()), timeout, unit);
    //}

    //public synchronized void send(Message<?, ?> msg)
    //{
    //    log.info("A Message was sent on the MessageBus!");
    //    msgs.add(msg);
    //}

    ///**
    // * Queries and removes Message (if it exists).
    // * @return TRUE if the Message exists on the MessageBus.
    // */
    //public synchronized boolean recieve(Message<?, ?> msg)
    //{
    //    if (msgs.contains(msg)) {
    //        log.info("Message exists on the MessageBus, removing Message...");
    //        msgs.remove(msg);
    //        return true;
    //    }
    //    log.info("Query unsuccessful! Message does not exist on the MessageBus.");
    //    return false;
    //}

    /**
     * Send a Message with no timeout. Both sender/caller has privledge to 
     * remove.
     */
    public synchronized void send(String id, Message<?, ?> msg)
    {
        log.info(id + " sent a Message on the MessageBus");
        this.messages.put(id, msg);
        //this.messages.get(id).put(msg.getKey(), msg.getVal());
        log.info("Message key: " + msg.getKey());
        log.info("Message value: " + msg.getValue());
    }

    /**
     * Get a Message by querying its sender's ID. Messages are mapped by sender
     * ID, so the ID should be known by the reciever for a valid request.
     */
    @SuppressWarnings("unchecked")
    public synchronized Message<?, ?> get(String id) 
    {
        //log.info("A request for a Message from " + id + " occurred");
        return this.messages.get(id);
    }

    ///**
    // * Get a Message by querying its sender's ID and the Message key. Useful 
    // * when a sender sends multiple Messages, but a reciever only wants a 
    // * specific Message key from the sender.
    // */
    //@SuppressWarnings("unchecked")
    //public synchronized <K, V> Message<K, V> get(String id, K key) 
    //{
    //    log.info("A request for a Message from " + id + " occurred");
    //    Map<Object, Object> myMsgs = this.messages.get(id);
    //    if (myMsgs != null) {
    //        log.info("Message key: " + myMsgs.get(key));
    //        return (Message<K, V>) myMsgs.get(key);
    //    }
    //    return null;
    //}

    /**
     * Remove all Messages from a sender's ID.
     */
    public synchronized void remove(String id)
    {
        log.info("Message from " + id + " removed from the MessageBus");
        this.messages.remove(id);
    }

    /**
     * Clears all Messages from the MessageBus.
     *
     * @warning Will clear ALL Messages. Be careful!
     */
    public synchronized void clear()
    {
        this.messages.clear();
    }

    /**
     * Clears all Messages EXCEPT the MessageIDs provided.
     * Useful for MessageBus adminstrators to control the overall state of the
     * MessageBus, and clear all Messages except what they want to control.
     *
     * @param MessageID id, the MessageID that shouldn't be cleared
     */
    public synchronized void clearExcept(MessageID... id)
    {
        Set<MessageID> idSet = new HashSet<>(Arrays.asList(ids));

        Iterator<Map.Entry<MessageID, Message>> iterator = 
            messages.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<MessageID, Message> entry = iterator.next();
            if (!idSet.contains(entry.getKey())) {
                log.info("Removed: " + entry.getKey());
                iterator.remove();
            }
        } 
    }

    ///**
    // * Remove a specific Message, specified by the sender's ID and the 
    // * Message's key.
    // */
    //public synchronized <K> void remove(String id, K key)
    //{
    //    Map<Object, Object> myMsgs = this.messages.get(id);
    //    if (myMsgs != null) {
    //        log.info("Message from " + id + " with key " + myMsgs.get(key) + " removed from the MessageBus");
    //        myMsgs.remove(key);
    //    }
    //}

    public void shutdown()
    {
        this.scheduler.shutdown();
        try {
            if (!this.scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                this.scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            this.scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
