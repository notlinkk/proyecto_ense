package com.mentory.ense_proyect.exception;

import com.mentory.ense_proyect.model.entity.Subscription;

public class DuplicatedSubscriptionException extends Exception {
    private final Subscription subscription;

    public DuplicatedSubscriptionException(String message) {
        super(message);
        this.subscription = null;
    }

    public DuplicatedSubscriptionException(Subscription subscription) {
        super("User already has a subscription to this lesson");
        this.subscription = subscription;
    }

    public Subscription getSubscription() {
        return subscription;
    }
}
