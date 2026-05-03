package com.example.mugangaconnect.utils;

import org.junit.Test;
import static org.junit.Assert.*;

public class ReminderLogicTest {

    @Test
    public void testNoShowPrediction() {
        // LOW risk: 0/10 missed
        assertEquals("LOW", NoShowPredictor.predict(0, 10));
        
        // LOW risk: less than 3 total appointments
        assertEquals("LOW", NoShowPredictor.predict(2, 2));

        // MEDIUM risk: 3/10 missed (30%)
        assertEquals("MEDIUM", NoShowPredictor.predict(3, 10));

        // HIGH risk: 6/10 missed (60%)
        assertEquals("HIGH", NoShowPredictor.predict(6, 10));
    }

    @Test
    public void testLeadHours() {
        assertEquals(48, NoShowPredictor.getReminderLeadHours("HIGH"));
        assertEquals(24, NoShowPredictor.getReminderLeadHours("MEDIUM"));
        assertEquals(3, NoShowPredictor.getReminderLeadHours("LOW"));
    }

    @Test
    public void testTimestampConversion() {
        // Known timestamp for 2025-01-01 10:00:00 UTC
        // Since it uses Locale.getDefault(), this might vary on environment, 
        // but we can test consistency.
        
        long t1 = ReminderManager.getTimestamp("2025-01-01", "10:00");
        long t2 = ReminderManager.getTimestamp("2025-01-01", "11:00");
        
        assertTrue(t2 > t1);
        assertEquals(3600000L, t2 - t1); // 1 hour difference
    }
}
