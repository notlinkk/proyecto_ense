package com.mentory.ense_proyect.exception;

import com.mentory.ense_proyect.model.entity.Ability;

public class DuplicatedAbilityException extends Throwable  {
    private final Ability ability;

    public DuplicatedAbilityException(Ability ability) {
        this.ability = ability;
    }

    public Ability getAbility() {
        return ability;
    }

}
