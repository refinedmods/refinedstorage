package com.raoulvdberge.refinedstorage.apiimpl.autocrafting;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternChain;

public class CraftingPatternChainList implements Iterable<CraftingPatternChainList.CraftingPatternChain> {
    LinkedList<CraftingPatternChain> innerChain = new LinkedList<>();

    public void add(ICraftingPattern pattern) {
        int i = 0;
        while (i < innerChain.size() && !innerChain.get(i).add(pattern)) {
            i++;
        }
        if (i == innerChain.size()) {
            innerChain.add(new CraftingPatternChain(pattern));
        }
    }

    public void addAll(Collection<ICraftingPattern> patterns) {
        patterns.forEach(this::add);
    }

    public List<ICraftingPattern> asList() {
        return innerChain.stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Override
    public Iterator<CraftingPatternChain> iterator() {
        return innerChain.iterator();
    }

    public void clear() {
        innerChain.clear();
    }

    public static class CraftingPatternChain implements ICraftingPatternChain {
        private LinkedList<ICraftingPattern> innerList;
        private ICraftingPattern prototype;

        public CraftingPatternChain(ICraftingPattern prototype) {
            this.prototype = prototype;
            this.innerList = new LinkedList<>();
            this.innerList.add(prototype);
        }

        public ICraftingPattern cycle() {
            ICraftingPattern front = innerList.poll();
            innerList.addLast(front);
            return front;
        }

        public ICraftingPattern getPrototype() {
            return prototype;
        }

        @Override
        public int size() {
            return innerList.size();
        }

        @Override
        public boolean isEmpty() {
            return innerList.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return innerList.contains(o);
        }

        @Override
        public Iterator<ICraftingPattern> iterator() {
            return innerList.iterator();
        }

        @Override
        public Object[] toArray() {
            return innerList.toArray();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return innerList.toArray(a);
        }

        @Override
        public boolean add(ICraftingPattern pattern) {
            return isValidForChain(pattern) && innerList.add(pattern);
        }

        @Override
        public boolean remove(Object o) {
            return innerList.remove(o);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return innerList.containsAll(c);
        }

        @Override
        public boolean addAll(Collection<? extends ICraftingPattern> c) {
            c.removeIf(p -> !isValidForChain(p));

            return innerList.addAll(c);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return innerList.removeAll(c);
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return innerList.retainAll(c);
        }

        @Override
        public void clear() {
            innerList.clear();
        }
    }
}
