package at.shanakor.sectionadapter;

/**
 * Wraps an object to attach a position to it.
 */
public class PositionWrapper<T> {
    private final T data;
    private final int position;

    public PositionWrapper(T data, int position) {
        this.data = data;
        this.position = position;
    }

    public T getData() {
        return data;
    }

    public int getPosition() {
        return position;
    }
}
