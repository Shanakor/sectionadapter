package at.shanakor.sectionadapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nikla on 21.03.2016.
 */
public abstract class CollectionHelper {
    /**
     * Groups the given values by their keys.
     * @param values A list of values.
     * @param grouper A {@link Grouper} to determine by what property the values should be grouped.
     * @return The correctly grouped data.
     */
    public static <K, V> LinkedHashMap<K, List<V>> groupKeysByValue(List<V> values, Grouper<K, V> grouper){
        if(values == null)
            throw new IllegalArgumentException("The parameter 'values' can not be null!");
        if(grouper == null)
            throw new IllegalArgumentException("The parameter 'grouper' can not be null!");

        LinkedHashMap<K, List<V>> hashMap = new LinkedHashMap<>();

        K key;
        for (V value : values) {
            key = grouper.getGroupFrom(value);

            if (!hashMap.containsKey(key)) {
                List<V> list = new ArrayList<>();
                list.add(value);

                hashMap.put(key, list);
            }
            else
                hashMap.get(key).add(value);
        }

        return hashMap;
    }

    /**
     * Groups the given values by their keys and sorts the values using the given comparator.
     * @param values A list of values.
     * @param grouper A {@link Grouper} to determine by what property the values should be grouped.
     * @param valueComparator A comparator which determines how the values are going to be sorted. (Sorting only appears group internal)
     * @return The correctly grouped and sorted data.
     */
    public static <K, V> LinkedHashMap<K, List<V>> groupKeysByValue(List<V> values, Grouper<K, V> grouper, Comparator<V> valueComparator){
        if(valueComparator == null)
            throw new IllegalArgumentException("The parameter 'valueComparator' can not be null!");

        Collections.sort(values, valueComparator);
        return groupKeysByValue(values, grouper);
    }

    /**
     * Groups the given values by their keys, sorts the values using the given comparator (group internal) and sorts the groups using the given comparator.
     * @param values A list of values.
     * @param valueGrouper A {@link Grouper} to determine by what property the values should be grouped.
     * @param valueComparator A comparator which determines how the values are going to be sorted. (Sorting only appears group internal)
     * @param groupSorter An interface to determine how to sort by groups.
     * @return The correctly grouped and sorted data.
     */
    public static <K, V, KD> LinkedHashMap<KD, List<V>> groupKeysByValue(List<V> values, Grouper<K, V> valueGrouper, Comparator<V> valueComparator, GroupSorter<K, V, KD> groupSorter){
        if(groupSorter == null)
            throw new IllegalArgumentException("The parameter 'groupSorter' can not be null!");

        Collections.sort(values, valueComparator);

        LinkedHashMap<K, List<V>> groupedDataBySortingCriteria = groupKeysByValue(values, groupSorter.getSortableGrouper());
        LinkedHashMap<K, List<V>> sortedGroupedDataBySortingCriteria = sortGroupedDataByKeys(groupedDataBySortingCriteria, groupSorter.getComparator());
        return replaceKeysWithDisplayable(sortedGroupedDataBySortingCriteria, groupSorter);
    }

    private static <KD, V, K> LinkedHashMap<KD, List<V>> replaceKeysWithDisplayable(LinkedHashMap<K, List<V>> groupedDataBySortingCriteria, GroupSorter<K, V, KD> groupSorter) {
        List<Map.Entry<K, List<V>>> entries = new ArrayList<>(groupedDataBySortingCriteria.entrySet());

        LinkedHashMap<KD, List<V>> displayableMap = new LinkedHashMap<>();
        for (Map.Entry<K, List<V>> entry : entries) {
            displayableMap.put(groupSorter.getKeyDisplayFromKey(entry.getKey()), entry.getValue());
        }

        return displayableMap;
    }


    public static <K, V> LinkedHashMap<K, List<V>> sortGroupedDataByKeys(LinkedHashMap<K, List<V>> groupedData, Comparator<Map.Entry<K, List<V>>> groupComparator) {
        List<Map.Entry<K, List<V>>> entries = new ArrayList<>(groupedData.entrySet());

        Collections.sort(entries, groupComparator);

        LinkedHashMap<K, List<V>> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<K, List<V>> entry : entries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
}
