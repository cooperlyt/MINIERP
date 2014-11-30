package com.dgsoft.common;


import org.jboss.seam.log.Logging;

import java.util.*;

/**
 * Created by cooper on 11/30/14.
 */
public class TotalDataLazyList<K extends TotalDataGroup.GroupKey, V, T extends TotalDataGroup.GroupTotalData> implements List<TotalDataGroup<K,V,T>> {


    private List<TotalDataGroup<K,V,T>> resultList = new ArrayList<TotalDataGroup<K,V,T>>();

    private Collection<V> datas;

    private TotalGroupStrategy<K, V, T> topStrategy;

    private  TotalGroupStrategy<?, V, ?>[] subStrategys;

    public TotalDataLazyList(Collection<V> values,
                             TotalGroupStrategy<K, V, T> groupStrategy,
                             TotalGroupStrategy<?, V, ?>... groupStrategys) {
        this.datas = values;
        this.topStrategy = groupStrategy;
        this.subStrategys = groupStrategys;
    }

    protected List<TotalDataGroup<K,V,T>> getResultList(){
        if (resultList == null){
            resultList = TotalDataGroup.groupBy(datas,topStrategy,subStrategys);
        }
        return resultList;
    }

    @Override
    public int size() {
        Logging.getLog(getClass()).debug("22");
        return getResultList().size();
    }

    @Override
    public boolean isEmpty() {
        Logging.getLog(getClass()).debug("21");
        return getResultList().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        Logging.getLog(getClass()).debug("20");
        return getResultList().contains(o);
    }

    @Override
    public Iterator<TotalDataGroup<K, V, T>> iterator() {
        Logging.getLog(getClass()).debug("19");
        return getResultList().iterator();
    }

    @Override
    public Object[] toArray() {
        Logging.getLog(getClass()).debug("18");
        return getResultList().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        Logging.getLog(getClass()).debug("17");
        return getResultList().toArray(a);
    }

    @Override
    public boolean add(TotalDataGroup<K, V, T> kvtTotalDataGroup) {
        Logging.getLog(getClass()).debug("16");
        return getResultList().add(kvtTotalDataGroup);
    }

    @Override
    public boolean remove(Object o) {
        Logging.getLog(getClass()).debug("15");
        return getResultList().remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        Logging.getLog(getClass()).debug("14");
        return getResultList().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends TotalDataGroup<K, V, T>> c) {
        Logging.getLog(getClass()).debug("13");
        return getResultList().addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends TotalDataGroup<K, V, T>> c) {
        Logging.getLog(getClass()).debug("12");
        return getResultList().addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        Logging.getLog(getClass()).debug("11");
        return getResultList().removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        Logging.getLog(getClass()).debug("10");
        return getResultList().retainAll(c);
    }

    @Override
    public void clear() {
        Logging.getLog(getClass()).debug("9");
        getResultList().clear();
    }

    @Override
    public TotalDataGroup<K, V, T> get(int index) {
        Logging.getLog(getClass()).debug("8");
        return getResultList().get(index);
    }

    @Override
    public TotalDataGroup<K, V, T> set(int index, TotalDataGroup<K, V, T> element) {
        Logging.getLog(getClass()).debug("7");
        return getResultList().set(index,element);
    }

    @Override
    public void add(int index, TotalDataGroup<K, V, T> element) {
        Logging.getLog(getClass()).debug("6");
        getResultList().add(index,element);
    }

    @Override
    public TotalDataGroup<K, V, T> remove(int index) {
        Logging.getLog(getClass()).debug("5");
        return getResultList().remove(index);
    }

    @Override
    public int indexOf(Object o) {
        Logging.getLog(getClass()).debug("4");
        return getResultList().indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        Logging.getLog(getClass()).debug("3");
        return getResultList().lastIndexOf(o);
    }

    @Override
    public ListIterator<TotalDataGroup<K, V, T>> listIterator() {
        Logging.getLog(getClass()).debug("1");
        return getResultList().listIterator();
    }

    @Override
    public ListIterator<TotalDataGroup<K, V, T>> listIterator(int index) {
        Logging.getLog(getClass()).debug("2");
        return getResultList().listIterator(index);
    }

    @Override
    public List<TotalDataGroup<K, V, T>> subList(int fromIndex, int toIndex) {
        Logging.getLog(getClass()).debug("call subList");
        return getResultList().subList(fromIndex,toIndex);
    }
}
