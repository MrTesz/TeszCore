package de.mrtesz.dbutils.utils.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DBUtilsException extends RuntimeException{
    private final String message;
}
