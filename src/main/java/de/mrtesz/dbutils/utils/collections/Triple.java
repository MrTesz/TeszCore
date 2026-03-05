package de.mrtesz.dbutils.utils.collections;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Triple<L, M, R> {

    public L left;
    public M middle;
    public R right;

    public String toString() {
        return "(" + this.getLeft() + "," + this.getMiddle() + "," + this.getRight() + ")";
    }
    public String toString(String format) {
        return String.format(format, this.getLeft(), this.getMiddle(), this.getRight());
    }
}
