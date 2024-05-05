package org.smartjobs.core.service;

import org.smartjobs.core.service.auth.levels.AuthLevel;

public interface AuthService {
    String getCurrentUsername();

    AuthLevel userMaxAuthLevel();
}
