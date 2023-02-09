package org.apache.kafka.common.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ImplicitLinkedHashMultiCollection<E extends ImplicitLinkedHashCollection.Element> extends ImplicitLinkedHashCollection<E> {
    public ImplicitLinkedHashMultiCollection() {
        super(0);
    }

    public ImplicitLinkedHashMultiCollection(int expectedNumElements) {
        super(expectedNumElements);
    }

    public ImplicitLinkedHashMultiCollection(Iterator<E> iter) {
        super(iter);
    }

    int addInternal(ImplicitLinkedHashCollection.Element newElement, ImplicitLinkedHashCollection.Element[] addElements) {
        int slot = slot(addElements, newElement);
        for (int seen = 0; seen < addElements.length; seen++) {
            ImplicitLinkedHashCollection.Element element = addElements[slot];
            if (element == null) {
                addElements[slot] = newElement;
                return slot;
            }
            if (element == newElement)
                return -2;
            slot = (slot + 1) % addElements.length;
        }
        throw new RuntimeException("Not enough hash table slots to add a new element.");
    }

    int findElementToRemove(Object key) {
        if (key == null || size() == 0)
            return -2;
        int slot = slot(this.elements, key);
        int bestSlot = -2;
        for (int seen = 0; seen < this.elements.length; seen++) {
            ImplicitLinkedHashCollection.Element element = this.elements[slot];
            if (element == null)
                return bestSlot;
            if (key == element)
                return slot;
            if (key.equals(element))
                bestSlot = slot;
            slot = (slot + 1) % this.elements.length;
        }
        return -2;
    }

    @SuppressWarnings({"unchecked"})
    public final List<E> findAll(E key) {
        if (key == null || size() == 0)
            return Collections.emptyList();
        ArrayList<E> results = new ArrayList<>();
        int slot = slot(this.elements, key);
        for (int seen = 0; seen < this.elements.length; seen++) {
            ImplicitLinkedHashCollection.Element element = this.elements[slot];
            if (element == null)
                break;
            if (key.equals(element)) {
                ImplicitLinkedHashCollection.Element element1 = this.elements[slot];
                results.add((E)element1);
            }
            slot = (slot + 1) % this.elements.length;
        }
        return results;
    }
}
