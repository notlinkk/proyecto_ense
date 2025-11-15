package com.mentory.ense_proyect.exception;

import com.mentory.ense_proyect.model.Module;

public class DuplicatedModuleException extends Throwable  {
    private final Module module;

    public DuplicatedModuleException(Module module) {
        this.module = module;
    }

    public Module getModule() {
        return module;
    }
}
