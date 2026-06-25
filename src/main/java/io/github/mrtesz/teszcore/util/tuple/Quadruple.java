package io.github.mrtesz.teszcore.util.tuple;

import io.github.mrtesz.teszcore.copyable.Copyable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

/** Utility tuple with 4 values **/
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Quadruple<L, ML, MR, R> implements Copyable<Quadruple<L, ML, MR, R>> {

    /** first value **/
    private L left;
    /** second value **/
    private ML midLeft;
    /** third value **/
    private MR midRight;
    /** fourth value **/
    private R right;

    public String toString(@NotNull String format) {
        return String.format(format, this.getLeft(), this.getMidLeft(), this.getMidRight(), this.getRight());
    }

    @Override
    public Quadruple<L, ML, MR, R> copy() {
        return new Quadruple<>();
    }
}
