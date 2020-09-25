package com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.node;

public interface NodeListener {
    void onAllDone(Node node);

    void onSingleDone(Node node);
}
