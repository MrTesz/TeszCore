package io.github.mrtesz.teszcore.collections;

import io.github.mrtesz.teszcore.copyable.Copyable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Quadruple<L, ML, MR, R> implements Copyable<Quadruple<L, ML, MR, R>> {

    private L left;
    private ML midLeft;
    private MR midRight;
    private R right;

    @Override
    public Quadruple<L, ML, MR, R> copy() {
        return new Quadruple<>();
    }
}
