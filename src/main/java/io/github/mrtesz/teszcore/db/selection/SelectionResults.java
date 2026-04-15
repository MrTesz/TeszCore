package io.github.mrtesz.teszcore.db.selection;

import io.github.mrtesz.teszcore.copyable.Copyable;
import io.github.mrtesz.teszcore.exceptions.DatabaseException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SelectionResults implements Copyable<SelectionResults> {

    private final List<Map<String, Object>> resultsMap;
    private final List<SelectionResult> results;

    public SelectionResults(@NotNull List<Map<String, Object>> results) {
        this.resultsMap = results;
        this.results = results.stream().map(SelectionResult::new).toList();
    }

    public boolean isEmpty() {
        return results.isEmpty();
    }

    /**
     * Method for Handling only one Result
     * @return The first Result of the results list
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
