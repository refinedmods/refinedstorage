package com.raoulvdberge.refinedstorage.integration.oc;

import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import li.cil.oc.api.Network;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.AbstractManagedEnvironment;

public class EnvironmentNetwork extends AbstractManagedEnvironment {
    protected final INetworkNode node;

    public EnvironmentNetwork(INetworkNode node) {
        this.node = node;

        setNode(Network.newNode(this, Visibility.Network).withComponent("refinedstorage", Visibility.Network).create());
    }
}

