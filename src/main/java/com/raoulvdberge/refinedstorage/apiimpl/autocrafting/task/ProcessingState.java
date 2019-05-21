package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

enum ProcessingState {
    READY_OR_PROCESSING,
    MACHINE_NONE,
    MACHINE_DOES_NOT_ACCEPT,
    PROCESSED,
    LOCKED
}
