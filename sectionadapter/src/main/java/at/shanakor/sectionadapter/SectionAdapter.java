package at.shanakor.sectionadapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * This class serves as the base class for any sectioned adapter.
 * On creation it automatically loads the needed data from the given groupedDataProvider.
 * It can be refreshed via {@link #notifyDataSetChanged} respectively via {@link #notifyDataSetChanged(GroupedDataProvider)} if a new {@link GroupedDataProvider} is wanted.
 */
public abstract class SectionAdapter<K, V> extends BaseAdapter {
    private Class<K> keyType;
    protected LinkedHashMap<K, List<V>> groupedData = null;

    /**
     * An internal enum to determine whether a section, child or child_divider
     * is being requested to inflate.
     */
    protected enum ITEM_VIEW_TYPE {
        SECTION,
        CHILD,
        CHILD_DIVIDER
    }

    private final Context context;
    private final boolean useCustomChildDividers;
    private GroupedDataProvider<K, V> groupedDataProvider;

    protected Object[] data = null;
    private int globalChildPos = 0;

    /**
     * @param context The context of the enclosing activity.
     * @param keyType The class of the key type. Unfortunately there is no other way using java generics.
     * @param groupedDataProvider A provider which will be used to get the data to be loaded.
     * @param useCustomChildDividers Whether or not the adapter should insert a custom divider layout between every value.
     *                               If false {@code bindChildView()} is not going to be called.
     */
    public SectionAdapter(Context context, Class<K> keyType, GroupedDataProvider<K, V> groupedDataProvider, boolean useCustomChildDividers) {
        if(context == null)
            throw new IllegalArgumentException("The parameter 'context' can not be null!");
        if(keyType == null)
            throw new IllegalArgumentException("The parameter 'keyType' can not be null!");
        if(groupedDataProvider == null)
            throw new IllegalArgumentException("The parameter 'groupedDataProvider' can not be null!");

        this.context = context;
        this.keyType = keyType;
        this.useCustomChildDividers = useCustomChildDividers;
        this.groupedDataProvider = groupedDataProvider;

        LinkedHashMap<K, List<V>> groupedData = groupedDataProvider.getData();
        this.data = getAdaptedGroupedData(groupedData);
        this.groupedData = groupedData;
    }

    /**
     * Uses the provided groupedDataProvider to load the new data.
     */
    @Override
    public void notifyDataSetChanged() {
        this.data = getAdaptedGroupedData(groupedDataProvider.getData());

        super.notifyDataSetChanged();
    }

    /**
     * Uses the given groupedDataProvider to load the new data.
     * @param newGroupedDataProvider A new groupedDataProvider.
     */
    public void notifyDataSetChanged(GroupedDataProvider<K, V> newGroupedDataProvider){
        groupedDataProvider = newGroupedDataProvider;

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(data == null)
            return 0;

        return data.length;
    }

    @Override
    public Object getItem(int position) {
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        if(useCustomChildDividers)
            return ITEM_VIEW_TYPE.values().length;
        else
            return ITEM_VIEW_TYPE.values().length - 1;
    }

    /**
     * @return The {@link ITEM_VIEW_TYPE} of the item at the specified position.
     */
    @Override
    public int getItemViewType(int position) {
        return (data[position] == null && useCustomChildDividers) ? ITEM_VIEW_TYPE.CHILD_DIVIDER.ordinal() :
                (data[position].getClass().isAssignableFrom(keyType)) ? ITEM_VIEW_TYPE.SECTION.ordinal() : ITEM_VIEW_TYPE.CHILD.ordinal();
    }

    /**
     * @return Returns false if the item at the given position is a group. (Disables onClickEvents/animations)
     */
    @Override
    public boolean isEnabled(int position) {
        int type = getItemViewType(position);
        return  type != ITEM_VIEW_TYPE.SECTION.ordinal() && type != ITEM_VIEW_TYPE.CHILD_DIVIDER.ordinal();
    }

    /**
     * Implements cell reuse and conveniently calls {@code bindGroupView}, {@code bindChildView} or
     * {@code bindChildDividerView} depending on the requested {@link ITEM_VIEW_TYPE}.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Object object = getItem(position);
        final int type = getItemViewType(position);

        if(convertView == null)
            convertView = inflateViewFor(type, parent);

        if(type == ITEM_VIEW_TYPE.SECTION.ordinal())
            bindGroupView((K) object, convertView, position == 0);
        else if(type == ITEM_VIEW_TYPE.CHILD.ordinal()) {
            PositionWrapper<V> positionWrapper = (PositionWrapper<V>) object;
            bindChildView(positionWrapper.getData(), convertView, position, positionWrapper.getPosition());
        }
        else
            bindChildDividerView();

        return convertView;
    }

    /**
     * @param viewTypeOrdinal The ordinal of the view's {@link ITEM_VIEW_TYPE}
     * @param parent
     * @return A correctly inflated view depending on the given viewTypeOrdinal.
     */
    protected abstract View inflateViewFor(int viewTypeOrdinal, ViewGroup parent);

    /**
     * Binds the given key's data to the given groupView.
     * @param groupView The view to bind the key's values to.
     * @param isFirstGroup Whether the group is the first group inside the sectionAdapter.
     */
    protected abstract void bindGroupView(K key, View groupView, boolean isFirstGroup);

    /**
     * Binds the given values's data to the given childView.
     * @param childView The view to bind the child's values to.
     * @param posInGroup The position inside a group.
     * @param globalChildPos The position of the child in a global context. (To enumerate children across groups).
     */
    protected abstract void bindChildView(V value, View childView, int posInGroup, int globalChildPos);

    /**
     * Binds any data to the childDividerView. Has not yet been implemented.
     */
    protected void bindChildDividerView(){
    }

    //region Collection Helper methods
    /**
     * @return An array of objects where each group is represented by a {@link K} object
     *         each child is represented by a {@link V} object and each childDivider is represented by null.
     *         ChildDividers are only added if the flag was set accordingly.
     */
    private Object[] getAdaptedGroupedData(LinkedHashMap<K, List<V>> groupedData){
        globalChildPos = 0;

        List<Object> adaptedGroupedData = new LinkedList<>();

        Object[] keys = groupedData.keySet().toArray();
        K key;
        for (int i = keys.length - 1; i >= 0; i--) {
            key = (K) keys[i];

            adaptedGroupedData.add(key);
            adaptedGroupedData.addAll(getAdaptedValues(groupedData.get(key)));
        }

        return adaptedGroupedData.toArray(new Object[adaptedGroupedData.size()]);
    }

    /**
     * Adds a null value after every entry, except the first one.
     */
    private List<Object> getAdaptedValues(List<V> values) {
        List<Object> objects = new LinkedList<>();

        int i = 0;
        for (V value : values) {
            if(useCustomChildDividers && i > 0)
                objects.add(null);

            objects.add(new PositionWrapper(value, globalChildPos));
            i++;
            globalChildPos++;
        }

        return objects;
    }
    //endregion

    //region Getter and Setter
    protected Context getContext(){
        return this.context;
    }

    protected GroupedDataProvider<K, V> getGroupedDataProvider() {
        return groupedDataProvider;
    }

    public LinkedHashMap<K, List<V>> getGroupedData() {
        return groupedData;
    }
    //endregion
}
