package com.group5.mindpilot;

import com.google.firebase.Timestamp;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Emotion Log Test
 */

public class EmotionLogTest {

    @Test
    public void testGetters() {

        String expectedEmotion = "😊";
        String expectedUserId = "testUser";
        Timestamp expectedTimestamp = Timestamp.now();

        EmotionLog log = new EmotionLog(expectedEmotion, expectedUserId, expectedTimestamp);

        assertEquals(expectedEmotion, log.getEmotion());
        assertEquals(expectedUserId, log.getUserId());
        assertEquals(expectedTimestamp, log.getTimestamp());
    }

    @Test
    public void testSetters() {

        EmotionLog log = new EmotionLog();
        String newEmotion = "😞";
        String newUserId = "testUser";

        Timestamp initialTimestamp = Timestamp.now();
        Timestamp newTimestamp = new Timestamp(initialTimestamp.getSeconds() + 10, 0);

        log.setEmotion(newEmotion);
        log.setUserId(newUserId);
        log.setTimestamp(newTimestamp);

        assertEquals(newEmotion, log.getEmotion());
        assertEquals(newUserId, log.getUserId());
        assertEquals(newTimestamp, log.getTimestamp());

    }

}