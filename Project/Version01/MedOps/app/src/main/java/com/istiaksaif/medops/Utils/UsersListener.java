package com.istiaksaif.medops.Utils;

import com.istiaksaif.medops.Model.User;

public interface UsersListener {
    void initiateVideoMeeting(User user);
    void initiateAudioMeeting(User user);
}
