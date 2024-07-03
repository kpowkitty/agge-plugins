/**
 * Utilities and macros from Message handling.
 */

package com.aggeplugins.MessageBus;

import com.aggeplugins.MessageBus.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageUtil {
    public Message<MessageID, ?>  msg;
    public MessageBus bus;

    public MessageUtil() {
        this.msg = null; // stay as null until explicitly created
        this.bus = bus.instance();
    }

    public void cleanup()
    {
        if (msg != null) {
            bus.remove(msg.getId());
        }
        msg = null;
        bus.release();
        bus = null;
    }

    public static boolean waitForInstruction(String name)
    {
        return true;
    }

    public void handleInstructions(String name)
    {
    }

    private MessageID handleString(String action, String name)
    {
        try {
            return MessageID.valueOf(action + "_" + name);
        } catch (IllegalArgumentException e) {
            // xxx enum conversion unsuccessful
        }
        return null;
    }

    public static void init()
    {
    }
}
