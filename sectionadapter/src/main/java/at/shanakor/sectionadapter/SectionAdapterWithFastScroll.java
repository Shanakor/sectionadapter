package at.shanakor.sectionadapter;

import android.content.Context;
import android.widget.SectionIndexer;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * This class serves as the base class for any sectioned adapter using FastScroll.
 * On creation it automatically loads the needed data from the given groupedDataProvider.
 * It can be refreshed via {@link #notifyDataSetChanged} respectively via {@link #notifyDataSetChanged(GroupedDataProvider)} if a new {@link GroupedDataProvider} is wanted.
 *
 * @see SectionAdapter
 */
public abstract class SectionAdapterWithFastScroll<K, V> extends SectionAdapter<K,V> implements SectionIndexer {

    /**
     * @param context The context of the enclosing activity.
     * @param keyType The class of the key type. Unfortunately there is no other way using java generics.
     * @param groupedDataProvider A provider which will be used to get the data to be loaded.
     * @param useCustomChildDividers Whether or not the adapter should insert a custom divider layout between every value.
     *                               If false {@code bindChildView()} is not going to be called.
     */
    public SectionAdapterWithFastScroll(Context context, Class<K> keyType, GroupedDataProvider<K, V> groupedDataProvider, boolean useCustomChildDividers) {
        super(context, keyType, groupedDataProvider, useCustomChildDividers);
    }

    //region SectionIndexer
    @Override
    public Object[] getSections() {
        return groupedData.keySet().toArray(new Object[groupedData.keySet().size()]);
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        Collection<List<V>> groupedValues = groupedData.values();

        int sectionCount = 0;
        int pos = 0;
        for (List<V> values : groupedValues) {
            if(sectionCount >= sectionIndex)
                break;

            pos += values.size() + 1;
            sectionCount++;
        }

        return pos;
    }

    @Override
    public int getSectionForPosition(int position) {
        Collection<List<V>> groupedValues = groupedData.values();

        int sectionIndex = 0;
        int childSum = 0;
        for (List<V> values : groupedValues) {
            childSum += values.size()+1;

            if(position <= childSum)
                return sectionIndex;

            sectionIndex++;
        }

        return 0;
    }
    //endregion
}
