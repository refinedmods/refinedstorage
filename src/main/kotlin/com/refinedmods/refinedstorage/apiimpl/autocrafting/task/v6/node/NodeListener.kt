package com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.node

interface NodeListener {
    fun onAllDone(node: Node)
    fun onSingleDone(node: Node)
}