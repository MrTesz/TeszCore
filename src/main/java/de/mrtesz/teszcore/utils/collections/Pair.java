package de.mrtesz.teszcore.utils.collections;

import de.mrtesz.teszcore.utils.copyable.Copyable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Pair<L, R> implements Copyable<Pair<L, R>> {

    L left;
    R right;

    public String toString() {
        return "Pair{" + this.getLeft() + "," + this.getRight() + "}";
    }
    public String toString(String format) {
        return String.format(format, this.getLeft(), this.getRight());
    }

    @Override
    public Pair<L, R> copy() {
        return new Pair<>(this.left, this.right);
    }
}
