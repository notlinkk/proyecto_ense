package com.mentory.ense_proyect.exception;

import com.mentory.ense_proyect.model.entity.Subscription;

public class DuplicatedSubscriptionException extends Throwable {
    private final Subscription subscription;

    public DuplicatedSubscriptionException(Subscription subscription) {
        this.subscription = subscription;
    }

    public Subscription getSubscription() {
        return subscription;
    }
}
