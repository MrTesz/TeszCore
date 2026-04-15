package io.github.mrtesz.teszcore.exceptions;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class YamlConfigException extends TeszCoreException {

    private final IOException ioEx;

    public YamlConfigException(@Nullable String message, IOException ioEx) {
        super(message, ioEx);
        this.ioEx = ioEx;
    }
    public YamlConfigException(@Nullable String message) {
        super(message);
        this.ioEx = null;
    }

    @Override
    public YamlConfigException copy() {
        return new YamlConfigException(message, this.ioEx);
    }
}
