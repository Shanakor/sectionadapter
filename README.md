# Section Adapter
Provides a SectionAdapter for Android. The sections are neither clickable nor expandable.
Additionally provides a SectionAdapter which uses FastScroll. 

## Implementation of a custom Section Adapter

In this example a simple contacts adapter is shown.
The sections consist of the first letter of a contact's name. The children consist of the contact's name.

#### 1. Extend the given class _SectionAdapterWithFastScroll_
```java
public class SelectContactsSectionAdapter extends SectionAdapterWithFastScroll<String, Contact>
```
We use a Strings as keys and Contact entities as childValues. (The contact entity is just a simple class which provides a getter for the name).

#### 2. Provide a constructor for the base class.
```java
public SelectContactsSectionAdapter(Context context, GroupedDataProvider<String, Contact> groupedDataProvider) {
        super(context, String.class, groupedDataProvider, false);
}
```
* The groupedDataProvider is a simple interface for defining how to get data (an example will be shown later).
* To the superclass we also have to pass the class of the key (because casting in java gets a bit weird using generics).
* The 'false' flag indicates that we do not want to use customChildDividers. (You can use a custom layout for childDividers if you want to build a more sophisticated layout. You just have to override __initChildDividerView__)

#### 3. Implement all needed methods for your layout.
```java
@Override
    protected View inflateViewFor(int viewTypeOrdinal) {
        return LayoutInflater.from(getContext()).inflate(
                typeOrdinal == ITEM_VIEW_TYPE.SECTION.ordinal() ? R.layout.list_group_select_contacts : android.R.layout.simple_list_item_multiple_choice, null);
    }

    @Override
    protected void bindGroupView(String key, View groupView, boolean isFirstGroup) {
        TextView textViewGroupHeader = (TextView) groupView.findViewById(R.id.textView_contact_short_name);
        textViewGroupHeader.setText(key);
    }

    @Override
    protected void bindChildView(Contact value, View childView, int posInGroup, int globalValuePos) {
        TextView textViewContactName = (TextView) childView.findViewById(android.R.id.text1);
        textViewContactName.setText(value.getName());
    }
```
* Inside __inflateViewFor__ you can specify which custom layout is inflated. For this purpose you can use the provided enum __ITEM_VIEW_TYPE__
* Inside __bindGroupView__, __bindChildView__ and if needed __bindChildView__ you can specify how the data is going to be displayed

## Usage of a Section Adapter

#### 1. Initialize the ListView
```java
        selectContactsListView = (ListView) this.findViewById(R.id.listView_selectSmsContacts);
        selectContactsListView.setFastScrollEnabled(true);
        selectContactsListView.setScrollingCacheEnabled(true);
```
__setFastScrollEnabled__ and __setScrollingCacheEnabled__ is only needed when using a __SectionAdapterWithFastScroll__.

#### 2. Initialize the adapter
```java
        selectContactsSectionAdapter = new SelectContactsSectionAdapter(this, new GroupedDataProvider<String, Contact>() {
            @Override
            public LinkedHashMap<String, List<Contact>> getData() {
                return CollectionHelper.groupKeysByValue(ContactsDataProvider.loadAllContacts(SelectContactsActivity.this), new Grouper<String, Contact>() {
                    @Override
                    public String getGroupFrom(Contact value) {
                        return String.valueOf(value.getName().charAt(0));
                    }
                }, new Comparator<Contact>() {
                    @Override
                    public int compare(Contact lhs, Contact rhs) {
                        return lhs.getName().compareTo(rhs.getName());
                    }
                });
            }
        });
```
There is a lot going on in this code:
* __GroupedDataProvider__ specifies how the adapter is going to get its data. 
* For this purpose you can use the given __CollectionHelper__ which helps you to group your data.
* The __CollectionHelper__ uses a Grouper (how to get a key from your values) and in this case a Comparator for sorting the values by by name.

_Note that the ContactDataProvider is not contained in the library but rather written by myself to ease retrieving contact data from the system._

#### 3. Set the adapter to the ListView.
```java
        selectContactsListView.setAdapter(selectContactsSectionAdapter);
```

If you have any questions, feature ideas or improvement tips feel free to contact me at _Niklas.Ram@gmail.com_.
