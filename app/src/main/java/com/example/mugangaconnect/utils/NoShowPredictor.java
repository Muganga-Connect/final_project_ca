package com.example.mugangaconnect.utils;

import com.example.mugangaconnect.data.model.Appointment;

/**
 * Rule-based no-show predictor.
 * Risk is calculated from the patient's historical missed-appointment ratio.
 *
 *  missed/total < 20%  → LOW
 *  20% – 50%           → MEDIUM
 *  > 50%               → HIGH
 *
 * Minimum 3 appointments required before escalating beyond LOW.
 */
public class NoShowPredictor {

    public static String predict(int missedCount, int totalCount) {
        if (totalCount < 3) return Appointment.RiskLevel.LOW.name();

        float ratio = (float) missedCount / totalCount;

        if (ratio > 0.50f) return Appointment.RiskLevel.HIGH.name();
        if (ratio > 0.20f) return Appointment.RiskLevel.MEDIUM.name();
        return Appointment.RiskLevel.LOW.name();
    }

    /** Returns how many hours before the appointment the reminder should fire. */
    public static int getReminderLeadHours(String riskLevel) {
        switch (riskLevel) {
            case "HIGH":   return 48;   // 2 days before
            case "MEDIUM": return 24;   // 1 day before
            default:       return 3;    // 3 hours before
        }
    }
}
