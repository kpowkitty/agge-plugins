package com.aggeplugins.MessageBus;

import com.aggeplugins.MessageBus.*;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Iterator;

@Slf4j
public class MessageBus {
    private static MessageBus instance;
    private ConcurrentHashMap<MessageID, Message<MessageID, ?>> messages;
    private ScheduledExecutorService scheduler;
    private static final AtomicInteger referenceCount = new AtomicInteger(0);

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
        referenceCount.incrementAndGet();
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
        //log.info("Amount of Messages on the MessageBus: " + messages.size());
        return this.messages.get(id) != null;
    }

    /**
     * Get the amount of Messages on the MessageBus.
     * Useful for debugging.
     *
     * @return int size, the amount of Messages on the MessageBus.
     */
    public synchronized int size()
    {
        return this.messages.size();
    }

    /**
     * Get a Message if it's on the MessageBus.
     *
     * @param Message id
     * The ID of the Message to get.
     *
     * @return Message<MessageID, ?> msg
     * The Message to get, or null if the Message is not on the MessageBus
     * (advised to query() first)
     *
     * @remark Does not remove!
     */
    public synchronized Message<MessageID, ?> get(MessageID id)
    {
        return this.messages.get(id);
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
     * Clears all Messages EXCEPT the MessageIDs provided.
     * Useful for MessageBus adminstrators to control the overall state of the
     * MessageBus, and clear all Messages except what they want to control.
     *
     * @param MessageID id, the MessageID that shouldn't be cleared
     */
    public synchronized void clearExcept(MessageID... ids) 
    {
        Set<MessageID> idSet = new HashSet<>(Arrays.asList(ids));

        Iterator<Map.Entry<MessageID, Message<MessageID, ?>>> iterator = 
            messages.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<MessageID, Message<MessageID, ?>> entry = iterator.next();
            if (!idSet.contains(entry.getKey())) {
                log.info("Removed: " + entry.getKey());
                iterator.remove();
            }
        }
    }

    /**
     * Shutdown procedure.
     *
     * @remark Make sure to shutdown the MessageBus on its last instance, for a
     * clean shutdown.
     *
     * @warning Dangerous procedure, can lose Messages!
     */
    public synchronized void shutdown()
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
        if (messages != null) {
            messages.clear();
        }
        log.info("MessageBus resources have been cleaned up.");
    }

    // Release the instance of the MessageBus.
    public synchronized void release() 
    {
        if (referenceCount.decrementAndGet() == 0) {
            log.info("No more references to MessageBus, cleaning up...");
            instance.shutdown();
            instance = null;
        }
    }
}
