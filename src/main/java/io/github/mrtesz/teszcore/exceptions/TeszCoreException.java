package io.github.mrtesz.teszcore.exceptions;

import io.github.mrtesz.teszcore.copyable.Copyable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class TeszCoreException extends RuntimeException implements Copyable<TeszCoreException> {
    protected final String message;
}
