/**
 * @enum MessageID
 * Master enumerations of MessageIDs, to have a known ID for senders to sender 
 * by and listeners to listen for.
 */

package com.aggeplugins.MessageBus;

public enum MessageID {
    REQUEST_PATH,
    SEND_PATH,
    INSTRUCTIONS,
    REQUEST_SKILLING,
    DONE_SKILLING,
    ERROR_SKILLING,
    REQUEST_FIGHTING,
    DONE_FIGHTING,
    ERROR_FIGHTING;
}
