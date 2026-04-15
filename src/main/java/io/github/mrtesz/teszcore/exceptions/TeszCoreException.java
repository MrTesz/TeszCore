package io.github.mrtesz.teszcore.exceptions;

import io.github.mrtesz.teszcore.copyable.Copyable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

@Getter
@NoArgsConstructor
public abstract class TeszCoreException extends RuntimeException implements Copyable<TeszCoreException> {
    protected @Nullable String message = null;
    protected @Nullable Throwable cause = null;

    public TeszCoreException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
        this.message = message;
        this.cause = cause;
    }
    public TeszCoreException(@Nullable Throwable cause) {
        super(cause);
        this.cause = cause;
    }
    public TeszCoreException(@Nullable String message) {
        super(message);
        this.message = message;
    }
}
