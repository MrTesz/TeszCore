package de.mrtesz.teszcore.utils.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TeszCoreException extends RuntimeException {
    protected final String message;
}
