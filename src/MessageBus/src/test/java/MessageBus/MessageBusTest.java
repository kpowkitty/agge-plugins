///**
// * Completely taken from ChatGPT.
// */
//
//import com.aggeplugins.MessageBus;
//import com.aggeplugins.MessageBus.Message;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//
//import static org.junit.Assert.*;
//
//public class MessageBusTest {
//
//    private MessageBus messageBus;
//
//    @Before
//    public void setUp() {
//        messageBus = MessageBus.instance();
//    }
//
//    @After
//    public void tearDown() {
//        messageBus.shutdown();
//    }
//
//    @Test
//    public void testMessageSendingAndRetrieval() {
//        // Register sender
//        messageBus.register("sender1");
//
//        // Send message with timeout
//        Message<Integer, String> msg1 = new Message<>(1, "Hello");
//        messageBus.send("sender1", msg1, 5, TimeUnit.SECONDS);
//
//        // Retrieve all messages from sender1
//        Map<Object, Object> messagesFromSender1 = messageBus.get("sender1");
//        assertNotNull(messagesFromSender1);
//        assertEquals(1, messagesFromSender1.size());
//        assertTrue(messagesFromSender1.containsKey(1));
//        assertEquals("Hello", messagesFromSender1.get(1));
//
//        // Retrieve specific message from sender1
//        Message<Integer, String> retrievedMsg1 = messageBus.get("sender1", 1);
//        assertNotNull(retrievedMsg1);
//        assertEquals(1, (int) retrievedMsg1.getKey());
//        assertEquals("Hello", retrievedMsg1.getVal());
//
//        // Send another message without timeout
//        Message<Integer, String> msg2 = new Message<>(2, "World");
//        messageBus.send("sender1", msg2);
//
//        // Retrieve all messages again and check size
//        messagesFromSender1 = messageBus.get("sender1");
//        assertNotNull(messagesFromSender1);
//        assertEquals(2, messagesFromSender1.size());
//        assertTrue(messagesFromSender1.containsKey(1));
//        assertTrue(messagesFromSender1.containsKey(2));
//
//        // Remove message with key 1 from sender1
//        messageBus.remove("sender1", 1);
//        assertNull(messageBus.get("sender1", 1));
//
//        // Remove all messages from sender1
//        messageBus.remove("sender1");
//        assertNull(messageBus.get("sender1"));
//    }
//
//    @Test
//    public void testMessageBusShutdown() {
//        // Register sender
//        messageBus.register("sender2");
//
//        // Send message without timeout
//        Message<Integer, String> msg = new Message<>(1, "Test");
//        messageBus.send("sender2", msg);
//
//        // Shutdown MessageBus
//        messageBus.shutdown();
//
//        // Attempt to retrieve messages after shutdown
//        assertNull(messageBus.get("sender2"));
//    }
//}
