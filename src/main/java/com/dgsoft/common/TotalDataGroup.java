package com.dgsoft.common;

import java.util.*;

/**
 * Created by cooper on 3/13/14.
 */
public class TotalDataGroup<K extends Comparable, V> implements Comparable<TotalDataGroup> {


    private TotalDataGroup(K key) {
        super();
        this.key = key;
    }

    private K key;

    private List<TotalDataGroup<? extends Comparable<?>, V>> childGroup;

    private List<V> values = new ArrayList<V>();

    public Map<Object,List<V>> getChildMap(){
        Map<Object,List<V>> result = new HashMap<Object, List<V>>();
        for (TotalDataGroup<? extends Comparable<?>, V> cg: childGroup){
            result.put(cg.getKey(),cg.values);
        }
        return result;
    }

    public boolean isLeaf() {
        return ((childGroup == null) || childGroup.isEmpty());
    }

    public K getKey() {
        return key;
    }


    public List<TotalDataGroup<? extends Comparable<?>, V>> getChildGroup() {
        return childGroup;
    }

    public List<V> getValues() {
        return values;
    }


    //public static        Object... params

    public static <V> TotalDataGroup<?, V> allGroupBy(Collection<V> values,TotalGroupStrategy<?, V>... groupStrategys){

        TotalDataGroup<?, V> result =  new TotalDataGroup<Comparable, V>(null);
        result.values = new ArrayList<V>(values);
        //result.childGroup =  groupBy(values,groupStrategy,groupStrategys);
        for (TotalGroupStrategy<?, V> stragegy : groupStrategys) {
            groupChildBy(result,stragegy);
        }

        return result;
    }

    public static <K extends Comparable<? super K>, V> List<TotalDataGroup<K, V>> groupBy(Collection<V> values,
                                                                                          TotalGroupStrategy<K, V> groupStrategy,
                                                                                          TotalGroupStrategy<?, V>... groupStrategys) {
        List<TotalDataGroup<K, V>> result = groupCollection(values, groupStrategy);


        for (TotalGroupStrategy<?, V> stragegy : groupStrategys) {
            for (TotalDataGroup<K, V> l : result) {
                groupChildBy(l, stragegy);
            }
        }
        return result;
    }

    private static <K extends Comparable<? super K>, V> List<TotalDataGroup<K, V>> groupCollection(Collection<V> values,
                                                                                                   TotalGroupStrategy<K, V> groupStrategy) {
        Map<K, TotalDataGroup<K, V>> result = new HashMap<K, TotalDataGroup<K, V>>();
        for (V value : values) {
            K valueKey = groupStrategy.getKey(value);


            TotalDataGroup<K, V> valueList = result.get(valueKey);
            if (valueList == null) {
                valueList = new TotalDataGroup<K, V>(valueKey);
                result.put(valueKey, valueList);
            }
            valueList.getValues().add(value);
        }

        List<TotalDataGroup<K, V>> listResult = new ArrayList<TotalDataGroup<K, V>>(result.values());
        Collections.sort(listResult);
        for (TotalDataGroup<K, V> group : listResult) {
            group.totalData = groupStrategy.totalGroupData(group.values);
        }
        return listResult;
    }

    private static <K extends Comparable<? super K>, V> void groupChildBy(TotalDataGroup<?, V> values, TotalGroupStrategy<K, V> groupStrategy) {
        if (values.isLeaf()) {
            values.childGroup = new ArrayList<TotalDataGroup<? extends Comparable<?>, V>>();
            for (TotalDataGroup<K, V> c : groupCollection(values.getValues(), groupStrategy)) {
                values.childGroup.add(c);
            }
        } else {
            for (TotalDataGroup<?, V> c : values.getChildGroup()) {
                groupChildBy(c, groupStrategy);
            }
        }
    }

    public static <K, V> void unionData(TotalDataGroup<?, V> data, TotalDataUnionStrategy<K, V> unionStrategy) {

        Map<K, V> result = new HashMap<K, V>();
        for (V v : data.values) {
            K k = unionStrategy.getKey(v);
            V oldV = result.get(k);
            if (oldV == null) {
                result.put(k, v);
            } else {
                result.put(k, unionStrategy.unionData(oldV, v));
            }
        }

        if (!data.isLeaf()) {
            for (TotalDataGroup<?, V> d : data.getChildGroup()) {
                unionData(d, unionStrategy);
            }
        }

        data.values = new ArrayList<V>(result.values());

    }

    public static <V extends Comparable<? super V>> void sort(TotalDataGroup<?, V> data) {
        Collections.sort(data.values);

        if (!data.isLeaf()) {
            for (TotalDataGroup<?, V> d : data.getChildGroup()) {
                sort(d);
            }
        }

    }


    public static <V> void sort(TotalDataGroup<?, V> data, Comparator<? super V> c) {
        Collections.sort(data.values, c);

        if (!data.isLeaf()) {
            for (TotalDataGroup<?, V> d : data.getChildGroup()) {
                sort(d, c);
            }
        }
    }


    @Override
    public int compareTo(TotalDataGroup o) {
        return getKey().compareTo(o.getKey());
    }

    private Object totalData;

    public Object getTotalData() {
        return totalData;
    }
}
