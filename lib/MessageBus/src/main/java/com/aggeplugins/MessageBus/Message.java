/**
 * Generic Message to be sent between systems via MessageBus.
 *
 * @param MessageID id
 * The Message's id, registered in the master ID enumeration.
 * @see MessageID.java
 * @param <T> data
 * The Message's data, to store associated with Message(s).
 */

package com.aggeplugins.MessageBus;

import com.aggeplugins.MessageBus.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Message<MessageID, T> {
    private MessageID id;
    private T data;
    public Message(MessageID id, T data) {
        log.info("Message created with ID: " + id);
        this.id = id;
        this.data = data;
    }
    public MessageID getId()
    {
        return this.id;
    }
    public T getData()
    {   
        return this.data;
    }
}
