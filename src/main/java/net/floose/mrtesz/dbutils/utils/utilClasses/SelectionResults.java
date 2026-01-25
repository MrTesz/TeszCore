package net.floose.mrtesz.dbutils.utils.utilClasses;

import net.floose.mrtesz.dbutils.utils.exceptions.DatabaseException;

import java.util.List;
import java.util.Map;

public class SelectionResults {

    private final List<Map<String, Object>> resultsMap;
    private final List<SelectionResult> results;

    public SelectionResults(List<Map<String, Object>> results) {
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
    public SelectionResult getFirst() throws DatabaseException {
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
}
