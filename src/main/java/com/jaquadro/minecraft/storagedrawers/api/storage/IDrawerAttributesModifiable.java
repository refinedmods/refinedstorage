package com.jaquadro.minecraft.storagedrawers.api.storage;

import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;

public interface IDrawerAttributesModifiable extends IDrawerAttributes {
    /**
     * Sets whether or not the drawer is currently concealed.
     *
     * @return false if the operation is not supported, true otherwise.
     */
    default boolean setIsConcealed(boolean state) {
        return false;
    }

    /**
     * Sets the lock state of a drawer or group for the given lock attribute.
     * If canItemLock returns false, this is a no-op.
     *
     * @return false if the operation is not supported, true otherwise.
     */
    default boolean setItemLocked(LockAttribute attr, boolean isLocked) {
        return false;
    }

    /**
     * Sets whether or not the drawer is currently quantified.
     *
     * @return false if the operation is not supported, true otherwise.
     */
    default boolean setIsShowingQuantity(boolean state) {
        return false;
    }

    /**
     * Sets whether or not the drawer is currently sealed.
     *
     * @return false if the operation is not supported, true otherwise.
     */
    default boolean setIsSealed(boolean state) {
        return false;
    }

    default boolean setIsVoid(boolean state) {
        return false;
    }

    default boolean setIsUnlimitedStorage(boolean state) {
        return false;
    }

    default boolean setIsUnlimitedVending(boolean state) {
        return false;
    }

    default boolean setIsDictConvertible(boolean state) {
        return false;
    }
}
