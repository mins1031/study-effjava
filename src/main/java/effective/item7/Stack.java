package effective.item7;

import java.util.Arrays;
import java.util.EmptyStackException;

public class Stack {
    private Object[] elements;
    private int size;
    private static final int DEFAULT_CAPACITY = 16;

    public Stack() {
        this.elements = new Object[DEFAULT_CAPACITY];
    }

    public void push(Object o) {
        ensureCapacity();
        this.elements[size++] = o;
    }

    public Object push() {
        if (size == 0) {
            throw new EmptyStackException();
        }

        return this.elements[--size];
    }

    private void ensureCapacity() {
        if (elements.length == size) {
            elements = Arrays.copyOf(elements, 2 * size + 1);
        }
    }


}
