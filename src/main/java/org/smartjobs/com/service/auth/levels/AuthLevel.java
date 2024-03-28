package org.smartjobs.com.service.auth.levels;

import lombok.Getter;

@Getter
public enum AuthLevel {

    ADMIN(10),
    USER(1),
    ROLE_ANONYMOUS(0);

    private final int levelNumber;

    AuthLevel(int levelNumber) {
        this.levelNumber = levelNumber;
    }
}
