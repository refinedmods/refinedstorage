package com.refinedmods.refinedstorage.screen

interface IScreenInfoProvider {
    val visibleRows: Int
    val rows: Int
    val currentOffset: Int
    val searchFieldText: String?
    val topHeight: Int
    val bottomHeight: Int
    val yPlayerInventory: Int
}