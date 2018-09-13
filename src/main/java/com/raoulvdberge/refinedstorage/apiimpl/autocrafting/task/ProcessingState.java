package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

enum ProcessingState {
    READY,
    EXTRACTED_ALL,
    MACHINE_NONE,
    MACHINE_DOES_NOT_ACCEPT,
    PROCESSED,
    LOCKED
}
