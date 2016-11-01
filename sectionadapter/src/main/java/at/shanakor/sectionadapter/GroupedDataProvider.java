package at.shanakor.sectionadapter;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by nikla on 21.03.2016.
 */
public interface GroupedDataProvider<K, V> {
    /**
     * @return The correctly grouped data. You can use {@link CollectionHelper} for this purpose.
     */
    LinkedHashMap<K, List<V>> getData();
}
