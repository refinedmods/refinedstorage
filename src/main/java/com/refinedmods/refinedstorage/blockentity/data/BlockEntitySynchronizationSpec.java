package com.refinedmods.refinedstorage.blockentity.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class BlockEntitySynchronizationSpec {
    private final List<BlockEntitySynchronizationParameter> parameters;
    private final List<BlockEntitySynchronizationParameter> watchedParameters;

    public BlockEntitySynchronizationSpec(List<BlockEntitySynchronizationParameter> parameters, List<BlockEntitySynchronizationParameter> watchedParameters) {
        this.parameters = Collections.unmodifiableList(parameters);
        this.watchedParameters = Collections.unmodifiableList(watchedParameters);
    }

    public List<BlockEntitySynchronizationParameter> getParameters() {
        return parameters;
    }

    public List<BlockEntitySynchronizationParameter> getWatchedParameters() {
        return watchedParameters;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<BlockEntitySynchronizationParameter> parameters = new ArrayList<>();
        private final List<BlockEntitySynchronizationParameter> watchedParameters = new ArrayList<>();

        public Builder addParameter(BlockEntitySynchronizationParameter parameter) {
            parameters.add(parameter);
            return this;
        }

        public Builder addWatchedParameter(BlockEntitySynchronizationParameter parameter) {
            addParameter(parameter);
            watchedParameters.add(parameter);
            return this;
        }

        public BlockEntitySynchronizationSpec build() {
            return new BlockEntitySynchronizationSpec(parameters, watchedParameters);
        }
    }
}
