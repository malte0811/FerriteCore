package malte0811.ferritecore.impl;

import it.unimi.dsi.fastutil.chars.CharComparator;
import it.unimi.dsi.fastutil.chars.CharList;
import it.unimi.dsi.fastutil.ints.AbstractIntList;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntList;

public class CharListIntListWrapper extends AbstractIntList {
    public final CharList delegate;

    public CharListIntListWrapper(CharList delegate) {
        this.delegate = delegate;
    }

    @Override
    public int getInt(int index) {
        return charToInt(delegate.getChar(index));
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public void size(int size) {
        delegate.size(size);
    }

    @Override
    public void add(int index, int k) {
        delegate.add(index, intToChar(k));
    }

    @Override
    public boolean add(int k) {
        return delegate.add(intToChar(k));
    }

    @Override
    public int removeInt(int i) {
        return charToInt(delegate.removeChar(i));
    }

    @Override
    public int set(int index, int k) {
        return delegate.set(index, intToChar(k));
    }

    @Override
    public int indexOf(int k) {
        return delegate.indexOf(intToChar(k));
    }

    @Override
    public int lastIndexOf(int k) {
        return delegate.lastIndexOf(intToChar(k));
    }

    @Override
    public boolean rem(int k) {
        return delegate.rem(intToChar(k));
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public IntList subList(int from, int to) {
        return new CharListIntListWrapper(delegate.subList(from, to));
    }

    @Override
    public void sort(IntComparator comparator) {
        delegate.sort(wrapComparator(comparator));
    }

    @Override
    public void unstableSort(IntComparator comparator) {
        delegate.unstableSort(wrapComparator(comparator));
    }

    @Override
    public void removeElements(int from, int to) {
        delegate.removeElements(from, to);
    }

    public static CharComparator wrapComparator(IntComparator comparator) {
        return (k1, k2) -> comparator.compare(charToInt(k1), charToInt(k2));
    }

    // we use \u0000 to represent -1 or the end
    public static char intToChar(int i) {
        if (i > Character.MAX_VALUE || i == 0)
            throw new IllegalArgumentException();
        return i < 0 ? '\u0000' : (char)i;
    }

    public static int charToInt(char c) {
        return c == '\u0000' ? -1 : (int)c;
    }
}
