package org.apache.kafka.common.utils;

import java.util.AbstractCollection;
import java.util.AbstractSequentialList;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Set;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ImplicitLinkedHashCollection<E extends ImplicitLinkedHashCollection.Element> extends AbstractCollection<E> {
    private static final int HEAD_INDEX = -1;

    public static final int INVALID_INDEX = -2;

    private static final int MIN_NONEMPTY_CAPACITY = 5;

    private static final Element[] EMPTY_ELEMENTS = new Element[0];

    private Element head;

    Element[] elements;

    private int size;

    public static interface Element {
        int prev();

        void setPrev(int param1Int);

        int next();

        void setNext(int param1Int);
    }

    private static class HeadElement implements Element {
        static final HeadElement EMPTY = new HeadElement();

        private int prev = -1;

        private int next = -1;

        public int prev() {
            return this.prev;
        }

        public void setPrev(int prev) {
            this.prev = prev;
        }

        public int next() {
            return this.next;
        }

        public void setNext(int next) {
            this.next = next;
        }

        private HeadElement() {}
    }

    private static Element indexToElement(Element head, Element[] elements, int index) {
        if (index == -1)
            return head;
        return elements[index];
    }

    private static void addToListTail(Element head, Element[] elements, int elementIdx) {
        int oldTailIdx = head.prev();
        Element element = indexToElement(head, elements, elementIdx);
        Element oldTail = indexToElement(head, elements, oldTailIdx);
        head.setPrev(elementIdx);
        oldTail.setNext(elementIdx);
        element.setPrev(oldTailIdx);
        element.setNext(-1);
    }

    private static void removeFromList(Element head, Element[] elements, int elementIdx) {
        Element element = indexToElement(head, elements, elementIdx);
        elements[elementIdx] = null;
        int prevIdx = element.prev();
        int nextIdx = element.next();
        Element prev = indexToElement(head, elements, prevIdx);
        Element next = indexToElement(head, elements, nextIdx);
        prev.setNext(nextIdx);
        next.setPrev(prevIdx);
        element.setNext(-2);
        element.setPrev(-2);
    }

    private class ImplicitLinkedHashCollectionIterator implements ListIterator<E> {
        private int cursor = 0;

        private ImplicitLinkedHashCollection.Element cur = ImplicitLinkedHashCollection.this.head;

        private int lastReturnedSlot = -2;

        ImplicitLinkedHashCollectionIterator(int index) {
            for (int i = 0; i < index; i++) {
                this.cur = ImplicitLinkedHashCollection.indexToElement(ImplicitLinkedHashCollection.this.head, ImplicitLinkedHashCollection.this.elements, this.cur.next());
                this.cursor++;
            }
        }

        public boolean hasNext() {
            return (this.cursor != ImplicitLinkedHashCollection.this.size);
        }

        public boolean hasPrevious() {
            return (this.cursor != 0);
        }

        public E next() {
            if (this.cursor == ImplicitLinkedHashCollection.this.size)
                throw new NoSuchElementException();
            this.lastReturnedSlot = this.cur.next();
            this.cur = ImplicitLinkedHashCollection.indexToElement(ImplicitLinkedHashCollection.this.head, ImplicitLinkedHashCollection.this.elements, this.cur.next());
            this.cursor++;
            return (E)this.cur;
        }

        public E previous() {
            if (this.cursor == 0)
                throw new NoSuchElementException();
            ImplicitLinkedHashCollection.Element element = this.cur;
            this.cur = ImplicitLinkedHashCollection.indexToElement(ImplicitLinkedHashCollection.this.head, ImplicitLinkedHashCollection.this.elements, this.cur.prev());
            this.lastReturnedSlot = this.cur.next();
            this.cursor--;
            return (E)element;
        }

        public int nextIndex() {
            return this.cursor;
        }

        public int previousIndex() {
            return this.cursor - 1;
        }

        public void remove() {
            if (this.lastReturnedSlot == -2)
                throw new IllegalStateException();
            if (this.cur == ImplicitLinkedHashCollection.indexToElement(ImplicitLinkedHashCollection.this.head, ImplicitLinkedHashCollection.this.elements, this.lastReturnedSlot)) {
                this.cursor--;
                this.cur = ImplicitLinkedHashCollection.indexToElement(ImplicitLinkedHashCollection.this.head, ImplicitLinkedHashCollection.this.elements, this.cur.prev());
            }
            ImplicitLinkedHashCollection.this.removeElementAtSlot(this.lastReturnedSlot);
            this.lastReturnedSlot = -2;
        }

        public void set(E e) {
            throw new UnsupportedOperationException();
        }

        public void add(E e) {
            throw new UnsupportedOperationException();
        }
    }

    private class ImplicitLinkedHashCollectionListView extends AbstractSequentialList<E> {
        private ImplicitLinkedHashCollectionListView() {}

        public ListIterator<E> listIterator(int index) {
            if (index < 0 || index > ImplicitLinkedHashCollection.this.size)
                throw new IndexOutOfBoundsException();
            return ImplicitLinkedHashCollection.this.listIterator(index);
        }

        public int size() {
            return ImplicitLinkedHashCollection.this.size;
        }
    }

    private class ImplicitLinkedHashCollectionSetView extends AbstractSet<E> {
        private ImplicitLinkedHashCollectionSetView() {}

        public Iterator<E> iterator() {
            return ImplicitLinkedHashCollection.this.iterator();
        }

        public int size() {
            return ImplicitLinkedHashCollection.this.size;
        }

        public boolean add(E newElement) {
            return ImplicitLinkedHashCollection.this.add(newElement);
        }

        public boolean remove(Object key) {
            return ImplicitLinkedHashCollection.this.remove(key);
        }

        public boolean contains(Object key) {
            return ImplicitLinkedHashCollection.this.contains(key);
        }

        public void clear() {
            ImplicitLinkedHashCollection.this.clear();
        }
    }

    public final Iterator<E> iterator() {
        return listIterator(0);
    }

    private ListIterator<E> listIterator(int index) {
        return new ImplicitLinkedHashCollectionIterator(index);
    }

    final int slot(Element[] curElements, Object e) {
        return (e.hashCode() & Integer.MAX_VALUE) % curElements.length;
    }

    private final int findIndexOfEqualElement(Object key) {
        if (key == null || this.size == 0)
            return -2;
        int slot = slot(this.elements, key);
        for (int seen = 0; seen < this.elements.length; seen++) {
            Element element = this.elements[slot];
            if (element == null)
                return -2;
            if (key.equals(element))
                return slot;
            slot = (slot + 1) % this.elements.length;
        }
        return -2;
    }

    public final E find(E key) {
        int index = findIndexOfEqualElement(key);
        if (index == -2)
            return null;
        return (E)this.elements[index];
    }

    public final int size() {
        return this.size;
    }

    public final boolean contains(Object key) {
        return (findIndexOfEqualElement(key) != -2);
    }

    private static int calculateCapacity(int expectedNumElements) {
        int newCapacity = 2 * expectedNumElements + 1;
        if (newCapacity < 5)
            return 5;
        return newCapacity;
    }

    public final boolean add(E newElement) {
        if (newElement == null)
            return false;
        if (this.size + 1 >= this.elements.length / 2)
            changeCapacity(calculateCapacity(this.elements.length));
        int slot = addInternal((Element)newElement, this.elements);
        if (slot >= 0) {
            addToListTail(this.head, this.elements, slot);
            this.size++;
            return true;
        }
        return false;
    }

    public final void mustAdd(E newElement) {
        if (!add(newElement))
            throw new RuntimeException("Unable to add " + newElement);
    }

    int addInternal(Element newElement, Element[] addElements) {
        int slot = slot(addElements, newElement);
        for (int seen = 0; seen < addElements.length; seen++) {
            Element element = addElements[slot];
            if (element == null) {
                addElements[slot] = newElement;
                return slot;
            }
            if (element.equals(newElement))
                return -2;
            slot = (slot + 1) % addElements.length;
        }
        throw new RuntimeException("Not enough hash table slots to add a new element.");
    }

    private void changeCapacity(int newCapacity) {
        Element[] newElements = new Element[newCapacity];
        HeadElement newHead = new HeadElement();
        int oldSize = this.size;
        for (Iterator<E> iter = iterator(); iter.hasNext(); ) {
            Element element = (Element)iter.next();
            iter.remove();
            int newSlot = addInternal(element, newElements);
            addToListTail(newHead, newElements, newSlot);
        }
        this.elements = newElements;
        this.head = newHead;
        this.size = oldSize;
    }

    public final boolean remove(Object key) {
        int slot = findElementToRemove(key);
        if (slot == -2)
            return false;
        removeElementAtSlot(slot);
        return true;
    }

    int findElementToRemove(Object key) {
        return findIndexOfEqualElement(key);
    }

    private boolean removeElementAtSlot(int slot) {
        this.size--;
        removeFromList(this.head, this.elements, slot);
        slot = (slot + 1) % this.elements.length;
        int endSlot = slot;
        for (int seen = 0; seen < this.elements.length; seen++) {
            Element element = this.elements[endSlot];
            if (element == null)
                break;
            endSlot = (endSlot + 1) % this.elements.length;
        }
        while (slot != endSlot) {
            reseat(slot);
            slot = (slot + 1) % this.elements.length;
        }
        return true;
    }

    private void reseat(int prevSlot) {
        Element element = this.elements[prevSlot];
        int newSlot = slot(this.elements, element);
        for (int seen = 0; seen < this.elements.length; seen++) {
            Element e = this.elements[newSlot];
            if (e == null || e == element)
                break;
            newSlot = (newSlot + 1) % this.elements.length;
        }
        if (newSlot == prevSlot)
            return;
        Element prev = indexToElement(this.head, this.elements, element.prev());
        prev.setNext(newSlot);
        Element next = indexToElement(this.head, this.elements, element.next());
        next.setPrev(newSlot);
        this.elements[prevSlot] = null;
        this.elements[newSlot] = element;
    }

    public ImplicitLinkedHashCollection() {
        this(0);
    }

    public ImplicitLinkedHashCollection(int expectedNumElements) {
        clear(expectedNumElements);
    }

    public ImplicitLinkedHashCollection(Iterator<E> iter) {
        clear(0);
        while (iter.hasNext())
            mustAdd(iter.next());
    }

    public final void clear() {
        clear(this.elements.length);
    }

    public final void clear(int expectedNumElements) {
        if (expectedNumElements == 0) {
            this.head = HeadElement.EMPTY;
            this.elements = EMPTY_ELEMENTS;
            this.size = 0;
        } else {
            this.head = new HeadElement();
            this.elements = new Element[calculateCapacity(expectedNumElements)];
            this.size = 0;
        }
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof ImplicitLinkedHashCollection))
            return false;
        ImplicitLinkedHashCollection<?> ilhs = (ImplicitLinkedHashCollection)o;
        return valuesList().equals(ilhs.valuesList());
    }

    public int hashCode() {
        return valuesList().hashCode();
    }

    final int numSlots() {
        return this.elements.length;
    }

    public List<E> valuesList() {
        return new ImplicitLinkedHashCollectionListView();
    }

    public Set<E> valuesSet() {
        return new ImplicitLinkedHashCollectionSetView();
    }
}
