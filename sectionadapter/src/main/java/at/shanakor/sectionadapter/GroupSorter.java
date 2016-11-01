package at.shanakor.sectionadapter;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * An interface that determines how the groups will be sorted.
 */
public interface GroupSorter<K, V, KD>{
    /**
     * @return A Grouper which groups the values by their sortable criteria. Return the same thing that you compare at the comparator.
     */
    Grouper<K, V> getSortableGrouper();

    /**
     * @return A comparator which determines how the groups are going to be sorted.
     * This accepts a type of Map.EntrySet because the data is first grouped and then the groups are sorted.
     */
    Comparator<Map.Entry<K, List<V>>> getComparator();

    /**
     * @return The displayable version of the key using the given key.
     */
    KD getKeyDisplayFromKey(K key);
}
