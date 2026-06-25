package io.github.mrtesz.teszcore.exceptions;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/** Exception thrown in YamlConfig context */
public class YamlConfigException extends TeszCoreException {

    /** IOException, causing the exception */
    private final @Nullable IOException ioEx;

    public YamlConfigException(@Nullable String message, @Nullable IOException ioEx) {
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
