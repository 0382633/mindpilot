package com.group5.mindpilot;

import org.junit.Test;
import org.junit.Before;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Chat Adapter Test
 */

public class ChatAdapterTest {

    private List<Message> messageList;
    private ChatAdapter chatAdapter;

    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_BOT = 2;

    @Before
    public void setup() {
        messageList = new ArrayList<>(); // Create initial empty message list
        chatAdapter = new ChatAdapter(messageList);
    }

    @Test
    public void testGetEmptyList() {
        assertEquals(0, chatAdapter.getItemCount()); // Should return 0 as list is not populated
    }

    @Test
    public void testGetPopulatedList() {

        messageList.add(new Message("test1", Message.SENT_BY_USER));
        messageList.add(new Message("test2", Message.SENT_BY_BOT));

        assertEquals(2, chatAdapter.getItemCount()); // Should return two messages due to above ^
    }

    @Test
    public void testGetUserMessage() {
        messageList.add(new Message("test", Message.SENT_BY_USER));

        int viewType = chatAdapter.getItemViewType(0); // Gets first message
        assertEquals(VIEW_TYPE_USER, viewType); // Should return 1 as User ViewType = 1
    }

    @Test
    public void testGetBotMessage() {
        messageList.add(new Message("test", Message.SENT_BY_BOT)); // Gets first message

        int viewType = chatAdapter.getItemViewType(0);
        assertEquals(VIEW_TYPE_BOT, viewType); // Should return 2 as User ViewType = 2
    }

}