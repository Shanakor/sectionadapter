package at.shanakor.sectionadapter;

/**
 * An interface that determines by what your values are going to be grouped.
 */
public interface Grouper<K, V> {
    /**
     * @return The key which you want your values to be grouped by.
     */
    K getGroupFrom(V value);
}
