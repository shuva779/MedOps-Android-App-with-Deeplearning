package com.istiaksaif.medops.Utils;

import java.util.HashMap;

public class Constants {
    public static final String KEY_PREFERENCE_NAME = "videoMeeting";

    public static final String REMOTE_MSG_AUTH = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";
    private static final String fcmServerKey = "AAAA19dyi8w:APA91bH-pSLyGfgbXcT0H4i8mZbOy-XUo7_R3ivw0fNEnv-xLnY6vuoZZhUHc5SRQeikTuHArY0H6o1rJj8tB7jHZE02EwQwzSCjEvAKlM-4UfsTEVNsXH-z0ALnqSbVsqN0TtFfg4w1";

    public static final String REMOTE_MSG_TYPE = "type";
    public static final String REMOTE_MSG_MEETING_TYPE = "meetingType";
    public static final String REMOTE_MSG_INVITATION = "invitation";
    public static final String REMOTE_MSG_INVITER_TOKEN = "inviterToken";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REG_IDS = "registration_ids";

    public static HashMap<String,String > getRemoteMessageHeader(){
        HashMap<String, String> header = new HashMap<>();
        header.put(Constants.REMOTE_MSG_AUTH,
                "key="+Constants.fcmServerKey);
        header.put(Constants.REMOTE_MSG_CONTENT_TYPE,"application/json");
        return header;
    }
}
