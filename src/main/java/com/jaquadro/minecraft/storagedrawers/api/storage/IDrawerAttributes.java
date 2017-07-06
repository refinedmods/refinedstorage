package com.jaquadro.minecraft.storagedrawers.api.storage;

import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;

public interface IDrawerAttributes {
    /**
     * Gets whether or not the lock state can be changed for the given lock attribute.
     * If this method returns false, isItemLocked may still return true.
     */
    default boolean canItemLock(LockAttribute attr) {
        return false;
    }

    /**
     * Gets whether or not a drawer or group is locked for the given lock attribute.
     */
    default boolean isItemLocked(LockAttribute attr) {
        return false;
    }

    /**
     * Gets whether or not the drawer has the concealed attribute.
     * The shrouded attribute instructs the drawer to not render its item label.
     */
    default boolean isConcealed() {
        return false;
    }

    /**
     * Gets whether or not the drawer has the sealed attribute.
     * A sealed drawer cannot be interacted with, and when broken will retain all of its items and upgrades.
     */
    default boolean isSealed() {
        return false;
    }

    /**
     * Gets whether or not the drawer has the quantified attribute.
     * The quantified attribute instructs the drawer to render its numerical quantity.
     */
    default boolean isShowingQuantity() {
        return false;
    }

    /**
     * Gets whether or not the drawer has a voiding attribute.
     */
    default boolean isVoid() {
        return false;
    }

    default boolean isUnlimitedStorage() {
        return false;
    }

    default boolean isUnlimitedVending() {
        return false;
    }

    default boolean isDictConvertible() {
        return false;
    }
}
