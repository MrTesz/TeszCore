package de.mrtesz.dbutils.utils.collections;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Pair<L, R> {

    L left;
    R right;

    public String toString() {
        return "Pair{" + this.getLeft() + "," + this.getRight() + "}";
    }
    public String toString(String format) {
        return String.format(format, this.getLeft(), this.getRight());
    }


}
