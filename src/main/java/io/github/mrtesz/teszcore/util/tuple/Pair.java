package io.github.mrtesz.teszcore.util.tuple;

import io.github.mrtesz.teszcore.copyable.Copyable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class Pair<L, R> implements Copyable<Pair<L, R>> {

    L left;
    R right;

    public String toString(@NotNull String format) {
        return String.format(format, this.getLeft(), this.getRight());
    }

    @Override
    public Pair<L, R> copy() {
        return new Pair<>(this.left, this.right);
    }
}
