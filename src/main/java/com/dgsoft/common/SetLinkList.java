package com.dgsoft.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by cooper on 4/6/14.
 */
public class SetLinkList<E> extends ArrayList<E> {

    private Set<E> linkSet;

    public SetLinkList(Set<E> set) {
        super(set);
        linkSet = set;
    }

    @Override
    public E set(int index, E element) {
        E result = super.set(index, element);
        linkSet.remove(result);
        linkSet.add(element);
        return result;
    }

    @Override
    public boolean add(E e) {
        if (linkSet.add(e)) {
            return super.add(e);
        } else return false;
    }

    @Override
    public void add(int index, E element) {
        if (linkSet.add(element)) {
            super.add(index, element);
        }
    }


    @Override
    public boolean remove(Object o) {
        linkSet.remove(o);
        return super.remove(o);
    }

    @Override
    public E remove(int index) {
        E result = super.remove(index);
        linkSet.remove(result);
        return result;
    }

    @Override
    public void clear() {
        linkSet.clear();
        super.clear();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        Set<? extends E> uc = new HashSet<E>(c);

        int oldSize = linkSet.size();

        if (linkSet.addAll(uc)) {
            if ((linkSet.size() - oldSize) == uc.size()) {
                super.addAll(uc);
            } else {
                for (E nc : uc) {
                    if (!contains(nc)) {
                        add(nc);
                    }
                }
            }

            return true;
        } else return false;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c){
        Set<? extends E> uc = new HashSet<E>(c);

        int oldSize = linkSet.size();

        if (linkSet.addAll(uc)) {
            if ((linkSet.size() - oldSize) == uc.size()) {
                super.addAll(index,uc);
            } else {
                int i = index;
                for (E nc : uc) {
                    if (!contains(nc)) {
                        add(i,nc);
                        i++;
                    }
                }
            }

            return true;
        } else return false;
    }

    @Override
    public boolean retainAll(Collection<?> c){
        if (linkSet.retainAll(c)){
            return super.retainAll(c);
        }else return false;
    }

}
