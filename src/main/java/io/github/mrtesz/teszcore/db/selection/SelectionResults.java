package io.github.mrtesz.teszcore.db.selection;

import io.github.mrtesz.teszcore.copyable.Copyable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/** Class for simple handling results of a selection **/
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SelectionResults implements Copyable<SelectionResults> {

    private final List<Map<String, Object>> resultsMap;
    private final List<SelectionResult> results;

    /** Execution results as a List of Maps representing the selected rows **/
    public SelectionResults(@NotNull List<Map<String, Object>> results) {
        this.resultsMap = results;
        this.results = results.stream().map(SelectionResult::new).toList();
    }

    public boolean isEmpty() {
        return results.isEmpty();
    }

    /**
     * Get the first result of all results (useful when you are sure you have one result - e.g. selection with unique key selection)
     * @return the first result
     */
    public SelectionResult getFirst() {
        if (results.isEmpty())
            return null;

        return results.getFirst();
    }

    public List<SelectionResult> asList() {
        return results;
    }

    public List<Map<String, Object>> asMap() {
        return resultsMap;
    }

    @Override
    public SelectionResults copy() {
        return new SelectionResults(this.resultsMap, this.results);
    }
}
