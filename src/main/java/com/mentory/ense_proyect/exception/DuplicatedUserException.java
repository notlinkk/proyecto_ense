package com.mentory.ense_proyect.exception;

import com.mentory.ense_proyect.model.entity.User;

public class DuplicatedUserException extends Throwable {
    private final User user;

    public DuplicatedUserException(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
