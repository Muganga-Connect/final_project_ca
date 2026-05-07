package com.example.mugangaconnect.ui.view;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

final class LucideIcons {
    private static final Map<String, String[]> ICONS = createIcons();

    private LucideIcons() {
    }

    static String[] pathsFor(String iconName) {
        if (iconName == null) {
            return null;
        }
        return ICONS.get(iconName.replace('-', '_'));
    }

    private static Map<String, String[]> createIcons() {
        Map<String, String[]> icons = new HashMap<>();
        icons.put("activity", new String[]{"M22,12h-4l-3,9L9,3l-3,9H2"});
        icons.put("arrow_right", new String[]{"M5,12h14M12,5l7,7-7,7"});
        icons.put("bell", new String[]{"M18,8A6,6 0,0 0,6 8c0,7 -3,9 -3,9h18s-3,-2 -3,-9", "M13.73,21a2,2 0,0 1,-3.46 0"});
        icons.put("bell_ring", new String[]{"M18,8A6,6 0,0 0,6 8c0,7 -3,9 -3,9h18s-3,-2 -3,-9", "M13.73,21a2,2 0,0 1,-3.46 0", "M21,5.5a9.5,9.5 0,0 0,-3.5 -3.5", "M3,5.5A9.5,9.5 0,0 1,6.5 2"});
        icons.put("bot", new String[]{"M12,8V4H8", "M3,12h1M20,12h1M3,18h18M6,8h12a2,2 0,0 1,2 2v6a2,2 0,0 1,-2 2H6a2,2 0,0 1,-2 -2v-6a2,2 0,0 1,2 -2z", "M9,13h.01M15,13h.01"});
        icons.put("brain", new String[]{"M12,5a3,3 0,1 0,-5.997 0.125A4,4 0,0 0,2 9v4a4,4 0,0 0,3.5 3.97A4,4 0,0 0,10 21h4a4,4 0,0 0,4.5 -4.03A4,4 0,0 0,22 13V9a4,4 0,0 0,-4.003 -3.875A3,3 0,1 0,12 5z", "M9.5,10h.01M14.5,10h.01M9.5,15a3.5,3.5 0,0 0,5 0"});
        icons.put("building_2", new String[]{"M6,22V4a2,2 0,0 1,2 -2h8a2,2 0,0 1,2 2v18z", "M6,12H4a2,2 0,0 0,-2 2v6a2,2 0,0 0,2 2h2", "M18,9h2a2,2 0,0 1,2 2v9a2,2 0,0 1,-2 2h-2", "M10,6h4M10,10h4M10,14h4M10,18h4"});
        icons.put("calendar_clock", new String[]{"M8,2v4M16,2v4", "M3,7h18v5H3z", "M3,11v10h7", "M17.5,17.5m-3.5,0a3.5,3.5 0,1 0,7 0a3.5,3.5 0,1 0,-7 0", "M17.5,16v1.5l1,1"});
        icons.put("calendar_days", new String[]{"M8,2v4M16,2v4", "M3,7h18v14H3z", "M3,11h18", "M8,15h.01M12,15h.01M16,15h.01M8,19h.01M12,19h.01M16,19h.01"});
        icons.put("calendar_x", new String[]{"M8,2v4M16,2v4", "M3,7h18v14H3z", "M3,11h18", "M10,16l4,4M14,16l-4,4"});
        icons.put("camera", new String[]{"M23,19a2,2 0,0 1,-2 2H3a2,2 0,0 1,-2 -2V8a2,2 0,0 1,2 -2h4l2,-3h6l2,3h4a2,2 0,0 1,2 2z", "M12,17m-4,0a4,4 0,1 0,8 0a4,4 0,1 0,-8 0"});
        icons.put("check", new String[]{"M20,6L9,17l-5,-5"});
        icons.put("chevron_right", new String[]{"M9,18l6,-6-6,-6"});
        icons.put("circle_check", new String[]{"M12,22m-10,0a10,10 0,1 0,20 0a10,10 0,1 0,-20 0", "M9,12l2,2 4,-4"});
        icons.put("circle_check_big", new String[]{"M21.801,10A10,10 0,1 0,22 12", "M9,12l2,2 4,-4"});
        icons.put("circle_help", new String[]{"M12,22m-10,0a10,10 0,1 0,20 0a10,10 0,1 0,-20 0", "M9.09,9a3,3 0,0 1,5.83 1c0,2 -3,3 -3,3", "M12,17h.01"});
        icons.put("clipboard_list", new String[]{"M9,5H7a2,2 0,0 0,-2 2v12a2,2 0,0 0,2 2h10a2,2 0,0 0,2 -2V7a2,2 0,0 0,-2 -2h-2", "M9,5a2,2 0,0 1,2 -2h2a2,2 0,0 1,2 2v0a2,2 0,0 1,-2 2h-2a2,2 0,0 1,-2 -2z", "M9,12h6M9,16h4"});
        icons.put("droplet", new String[]{"M12,22a7,7 0,0 0,7 -7c0,-2 -1,-3.9 -3,-5.5s-3.5,-4 -4,-6.5c-0.5,2.5 -2,4.9 -4,6.5C6,11.1 5,13 5,15a7,7 0,0 0,7 7z"});
        icons.put("droplets", new String[]{"M7,16.3c2.2,0 4,-1.9 4,-4.2c0,-1.5 -0.7,-2.8 -1.8,-3.8L7,5L4.8,8.3C3.7,9.3 3,10.6 3,12.1C3,14.4 4.8,16.3 7,16.3z", "M17,16.3c2.2,0 4,-1.9 4,-4.2c0,-1.5 -0.7,-2.8 -1.8,-3.8L17,5l-2.2,3.3C13.7,9.3 13,10.6 13,12.1C13,14.4 14.8,16.3 17,16.3z", "M7,19l0,2", "M17,19l0,2"});
        icons.put("eye", new String[]{"M2,12s3,-7 10,-7 10,7 10,7-3,7-10,7-10,-7-10,-7z", "M12,12m-3,0a3,3 0,1 0,6 0a3,3 0,1 0,-6 0"});
        icons.put("file_text", new String[]{"M15,2H6a2,2 0,0 0,-2 2v16a2,2 0,0 0,2 2h12a2,2 0,0 0,2 -2V7z", "M14,2v4a2,2 0,0 0,2 2h4", "M10,9H8M16,13H8M16,17H8"});
        icons.put("fingerprint_pattern", new String[]{"M12,10a2,2 0,0 0,-2 2c0,1.02 0.4,1.94 1.05,2.61", "M14,12a2,2 0,0 0,-2 -2", "M8.5,8.5A5,5 0,0 1,17 12c0,1.5 -0.5,3 -1.5,4", "M7,12a5,5 0,0 1,5 -5", "M5.5,5.5A9,9 0,0 1,21 12c0,2 -0.5,4 -1.5,5.5", "M3,12a9,9 0,0 1,9 -9", "M2,2l20,20"});
        icons.put("heart", new String[]{"M20.84,4.61a5.5,5.5 0,0 0,-7.78 0L12,5.67l-1.06,-1.06a5.5,5.5 0,0 0,-7.78 7.78l1.06,1.06L12,21.23l7.78,-7.78 1.06,-1.06a5.5,5.5 0,0 0,0 -7.78z"});
        icons.put("heart_pulse", new String[]{"M19.5,12.572l-7.5,7.428l-7.5,-7.428a5,5 0,1 1,7.5 -6.566a5,5 0,1 1,7.5 6.566", "M3,12h2l2,-4l2,8l2,-4h2l1,2h3"});
        icons.put("image", new String[]{"M21,3H3a2,2 0,0 0,-2 2v14a2,2 0,0 0,2 2h18a2,2 0,0 0,2 -2V5a2,2 0,0 0,-2 -2z", "M8.5,10m-2.5,0a2.5,2.5 0,1 0,5 0a2.5,2.5 0,1 0,-5 0", "M21,15l-5,-5L5,21"});
        icons.put("layout_dashboard", new String[]{"M3,3h7v9H3zM14,3h7v5h-7zM14,12h7v9h-7zM3,16h7v5H3z"});
        icons.put("lock", new String[]{"M19,11H5a2,2 0,0 0,-2 2v7a2,2 0,0 0,2 2h14a2,2 0,0 0,2 -2v-7a2,2 0,0 0,-2 -2z", "M7,11V7a5,5 0,0 1,10 0v4"});
        icons.put("log_in", new String[]{"M15,3h4a2,2 0,0 1,2 2v14a2,2 0,0 1,-2 2h-4", "M10,17l5,-5-5,-5", "M15,12H3"});
        icons.put("mail", new String[]{"M4,4h16c1.1,0 2,0.9 2,2v12c0,1.1 -0.9,2 -2,2H4c-1.1,0 -2,-0.9 -2,-2V6c0,-1.1 0.9,-2 2,-2z", "M22,6l-10,7L2,6"});
        icons.put("menu", new String[]{"M3,12h18M3,6h18M3,18h18"});
        icons.put("paperclip", new String[]{"M21.44,11.05l-9.19,9.19a6,6 0,0 1,-8.49 -8.49l9.19,-9.19a4,4 0,0 1,5.66 5.66l-9.2,9.19a2,2 0,0 1,-2.83 -2.83l8.49,-8.48"});
        icons.put("pill", new String[]{"M10.5,20.5l10,-10a4.95,4.95 0,1 0,-7 -7l-10,10a4.95,4.95 0,1 0,7 7z", "M8.5,8.5l7,7"});
        icons.put("ruler", new String[]{"M21.3,8.7L8.7,21.3c-1,1 -2.5,1 -3.4,0L2.7,18.7c-1,-1 -1,-2.5 0,-3.4L15.3,2.7c1,-1 2.5,-1 3.4,0l2.6,2.6C22.3,6.3 22.3,7.7 21.3,8.7z", "M7.5,10.5l2,2", "M10.5,7.5l2,2", "M13.5,13.5l2,2"});
        icons.put("search", new String[]{"M11,11m-8,0a8,8 0,1 0,16 0a8,8 0,1 0,-16 0", "M21,21l-4.35,-4.35"});
        icons.put("send", new String[]{"M22,2L11,13", "M22,2L15,22 11,13 2,9l20,-7z"});
        icons.put("shield", new String[]{"M12,22s8,-4 8,-10V5l-8,-3L4,5v7c0,6 8,10 8,10z"});
        icons.put("shield_check", new String[]{"M12,22s8,-4 8,-10V5l-8,-3L4,5v7c0,6 8,10 8,10z", "M9,12l2,2 4,-4"});
        icons.put("siren", new String[]{"M7,18H17V16a5,5 0,0 0,-10 0v2z", "M5,21h14", "M12,9V3", "M4.22,10.22l1.42,1.42", "M18.36,11.64l1.42,-1.42", "M2,18h2M20,18h2"});
        icons.put("sliders_horizontal", new String[]{"M21,6H3M15,12H3M17,18H3", "M21,6m-2,0a2,2 0,1 0,4 0a2,2 0,1 0,-4 0", "M15,12m-2,0a2,2 0,1 0,4 0a2,2 0,1 0,-4 0", "M17,18m-2,0a2,2 0,1 0,4 0a2,2 0,1 0,-4 0"});
        icons.put("smile", new String[]{"M12,22m-10,0a10,10 0,1 0,20 0a10,10 0,1 0,-20 0", "M8,14s1.5,2 4,2 4,-2 4,-2", "M9,9h.01M15,9h.01"});
        icons.put("sparkles", new String[]{"M9.937,15.5A2,2 0,0 0,8.5 14.063l-6.135,-1.582a0.5,0.5 0,0 1,0 -0.962L8.5,9.936A2,2 0,0 0,9.937 8.5l1.582,-6.135a0.5,0.5 0,0 1,0.963 0L14.063,8.5A2,2 0,0 0,15.5 9.937l6.135,1.581a0.5,0.5 0,0 1,0 0.964L15.5,14.063a2,2 0,0 0,-1.437 1.437l-1.582,6.135a0.5,0.5 0,0 1,-0.963 0z", "M20,3v4M22,5h-4", "M4,17v2M5,18H3"});
        icons.put("star", new String[]{"M12,2l3.09,6.26L22,9.27l-5,4.87 1.18,6.88L12,17.77l-6.18,3.25L7,14.14 2,9.27l6.91,-1.01L12,2z"});
        icons.put("user", new String[]{"M20,21v-2a4,4 0,0 0,-4 -4H8a4,4 0,0 0,-4 4v2", "M12,11m-4,0a4,4 0,1 0,8 0a4,4 0,1 0,-8 0"});
        icons.put("user_plus", new String[]{"M16,21v-2a4,4 0,0 0,-4 -4H6a4,4 0,0 0,-4 4v2", "M9,11m-4,0a4,4 0,1 0,8 0a4,4 0,1 0,-8 0", "M19,8v6M22,11h-6"});
        icons.put("user_round", new String[]{"M18,20a6,6 0,0 0,-12 0", "M12,14m-4,0a4,4 0,1 0,8 0a4,4 0,1 0,-8 0"});
        icons.put("weight", new String[]{"M12,3a1,1 0,1 0,2 0a1,1 0,1 0,-2 0", "M6.5,6h11l1,14H5.5L6.5,6z", "M10,12l1.5,1.5L14,10"});
        return Collections.unmodifiableMap(icons);
    }
}
