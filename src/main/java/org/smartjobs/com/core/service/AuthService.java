package org.smartjobs.com.core.service;

import org.smartjobs.com.core.service.auth.levels.AuthLevel;

public interface AuthService {
    String getCurrentUsername();

    AuthLevel userMaxAuthLevel();
}
