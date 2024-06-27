package com.aggeplugins.MessageBus;

import com.aggeplugins.MessageBus.*;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import java.util.concurrent.CopyOnWriteArrayList;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class MessageBus {
    private static MessageBus instance;
    private ConcurrentHashMap<MessageID, Message<MessageID, ?>> messages;
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
            log.info("Error: Unable to create messages map!");
        }
        try {
            this.scheduler = Executors.newScheduledThreadPool(1);
        } catch (NullPointerException e) {
            log.info("Error: Unable to create thread pool!");
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
   
    /**
    * Send a Message with no timeout. 
    *
    * @param Message<MessageID, ?> msg
    * The message to be sent.
    */   
    public synchronized void send(Message<MessageID, ?> msg)
    {
        log.info("Recieved send request for Message with ID: " + msg.getId());
        this.messages.put(msg.getId(), msg);
    }

    /**
     * Send a Message with a timeout duration. 
     *
     * @param Message<MessageID, ?> msg
     * The Message to be sent.
     *
     * @param long timeout
     * The duration of the timeout.
     *
     * @param TimeUnit unit
     * The unit of the timeout.
     */
    public synchronized void send(Message<MessageID, ?> msg, long timeout, 
                                                             TimeUnit unit)
    {   
        log.info("Recieved send request for Message with ID: " + msg.getId());
        log.info("Timeout: " + timeout + " " + unit);
        this.messages.put(msg.getId(), msg);
        this.scheduler.schedule(() -> messages.remove(msg.getId()),
                                timeout, unit);
    } 

    /**
     * Query if a Message is on the MessageBus.
     *
     * @param MessageID id
     * The ID of the Message to query.
     *
     * @return TRUE if it is, FALSE if it's not
     */
    public synchronized boolean query(MessageID id)
    {
        // will spam because constant query
        //log.info("Recieved query request for Message with ID: " + id);
        if (this.messages.isEmpty())
            return false;
        return this.messages.get(id) != null;
    }

    /**
     * Recieve and remove a Message on the MessageBus.
     * 
     * @param MessageID id
     * The ID of the Message to recieve and remove.
     *
     * @return Message<MessageID, ?> msg
     * The Message to be recieved.
     *
     * @warning Destructive procedure, will remove Message. Advised to query()
     * first.
     */
    @SuppressWarnings("unchecked")
    public synchronized Message<MessageID, ?> recieve(MessageID id)
    {
        log.info("Recieved recieve request for Message with ID: " + id);
        Message<MessageID, ?> msg = this.messages.get(id);
        if (msg != null) {
            this.messages.remove(id);
            return msg;
        }
        return null;
    }

    /**
     * Remove Message with specified MessageID from the MessageBus.
     *
     * @return TRUE if success, FALSE if failure or Message does not exist
     *
     * @warning Destructive procedure, will remove Message.
     */
    public synchronized boolean remove(MessageID id)
    {
        log.info("Recieved remove request for Message with ID: " + id);
        if (this.messages.remove(id) != null)
            return true;
        return false;
    }

    /**
     * Clear all Message(s) on the MessageBus.
     *
     * @return TRUE if success, FALSE if failure or MessageBus is not clear
     *
     * @warning Destructive procedure, will clear ALL Message(s)!
     */
    public synchronized boolean clear()
    {
        log.info("Recieved clear all Messages request");
        this.messages.clear();
        return this.messages.isEmpty();
    }

    /**
     * Shutdown procedure.
     *
     * @remark Make sure to shutdown the MessageBus on its last instance, for a
     * clean shutdown.
     */
    public void shutdown()
    {
        log.info("Recieved shutdown request");
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
