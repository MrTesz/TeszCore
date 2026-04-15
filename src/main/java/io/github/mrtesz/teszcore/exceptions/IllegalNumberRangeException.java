package io.github.mrtesz.teszcore.exceptions;

import lombok.Getter;

@Getter
public class IllegalNumberRangeException extends TeszCoreException {

    private final Number num;
    private final Number min;
    private final Number max;

    public IllegalNumberRangeException(Number num, Number min, Number max) {
        super("{num=" + num + ", min=" + min + ", max=" + max + "}");
        this.num = num;
        this.min = min;
        this.max = max;
    }
    public IllegalNumberRangeException(Number num, Number min, Number max, String message) {
        super(message + " {num=" + num + ", min=" + min + ", max=" + max + "}");
        this.num = num;
        this.min = min;
        this.max = max;
    }

    @Override
    public TeszCoreException copy() {
        return new IllegalNumberRangeException(num, min, max, message);
    }
}
