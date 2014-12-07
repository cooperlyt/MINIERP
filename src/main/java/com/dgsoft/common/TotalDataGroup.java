package com.dgsoft.common;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.*;

/**
 * Created by cooper on 3/13/14.
 */
public class TotalDataGroup<K extends TotalDataGroup.GroupKey, V, T extends TotalDataGroup.GroupTotalData> implements Comparable<TotalDataGroup> {


    private TotalDataGroup(K key) {
        super();
        this.key = key;
    }

    private K key;

    private boolean expanded = false;

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    private List<TotalDataGroup<? extends GroupKey, V, T>> childGroup;

    private List<V> values = new ArrayList<V>();

    public Map<Object, List<V>> getChildMap() {
        Map<Object, List<V>> result = new HashMap<Object, List<V>>();
        for (TotalDataGroup<? extends GroupKey, V, ? extends GroupTotalData> cg : childGroup) {
            result.put(cg.getKey(), cg.values);
        }
        return result;
    }

    public boolean isLeaf() {
        return ((childGroup == null) || childGroup.isEmpty());
    }

    public K getKey() {
        return key;
    }


    public List<TotalDataGroup<? extends GroupKey, V, T>> getChildGroup() {
        return childGroup;
    }

    public List<V> getValues() {
        return values;
    }


    //public static        Object... params

    @Deprecated
    public static <V, T extends GroupTotalData> TotalDataGroup<?, V, T> allGroupBy(Collection<? extends V> values, TotalGroupStrategy<?, V, T>... groupStrategys) {

        TotalDataGroup<?, V, T> result = new TotalDataGroup<GroupKey, V, T>(null);
        result.values = new ArrayList<V>(values);
        //result.childGroup =  groupBy(values,groupStrategy,groupStrategys);
        for (TotalGroupStrategy<?, V, T> stragegy : groupStrategys) {
            groupChildBy(result, stragegy);
        }

        return result;
    }

    public static <K extends GroupKey, V, T extends GroupTotalData> List<TotalDataGroup<K, V, T>> groupBy(Collection<V> values,
                                                                                                          TotalGroupStrategy<K, V, T> groupStrategy,
                                                                                                          TotalGroupStrategy<? extends GroupKey, V, T>... groupStrategys) {
        List<TotalDataGroup<K, V, T>> result = groupCollection(values, groupStrategy);


        for (TotalGroupStrategy<? extends GroupKey, V, T> stragegy : groupStrategys) {
            for (TotalDataGroup<K, V, T> l : result) {
                groupChildBy(l, stragegy);
            }
        }
        return result;
    }

    private static <K extends GroupKey, V, T extends GroupTotalData> List<TotalDataGroup<K, V, T>> groupCollection(Collection<V> values,
                                                                                                                   TotalGroupStrategy<K, V, T> groupStrategy) {
        Map<K, TotalDataGroup<K, V, T>> result = new HashMap<K, TotalDataGroup<K, V, T>>();
        for (V value : values) {
            K valueKey = groupStrategy.getKey(value);


            TotalDataGroup<K, V, T> valueList = result.get(valueKey);
            if (valueList == null) {
                valueList = new TotalDataGroup<K, V, T>(valueKey);
                result.put(valueKey, valueList);
            }
            valueList.getValues().add(value);
        }

        List<TotalDataGroup<K, V, T>> listResult = new ArrayList<TotalDataGroup<K, V, T>>(result.values());
        Collections.sort(listResult);
        for (TotalDataGroup<K, V, T> group : listResult) {
            group.totalData = groupStrategy.totalGroupData(group.values);
        }
        return listResult;
    }

    private static <K extends TotalDataGroup.GroupKey, V, T extends TotalDataGroup.GroupTotalData> void groupChildBy(TotalDataGroup<?, V, T> values, TotalGroupStrategy<K, V, T> groupStrategy) {
        if (values.isLeaf()) {
            values.childGroup = new ArrayList<TotalDataGroup<? extends TotalDataGroup.GroupKey, V, T>>();
            for (TotalDataGroup<K, V, T> c : groupCollection(values.getValues(), groupStrategy)) {
                values.childGroup.add(c);
            }
        } else {
            for (TotalDataGroup<? extends TotalDataGroup.GroupKey, V, T> c : values.getChildGroup()) {
                groupChildBy(c, groupStrategy);
            }
        }
    }

    public static <K, V> Collection<V> unionData(Collection<? extends V> data, TotalDataUnionStrategy<K, V> unionStrategy) {
        Map<K, V> result = new HashMap<K, V>();
        for (V v : data) {
            K k = unionStrategy.getKey(v);
            V oldV = result.get(k);
            if (oldV == null) {
                result.put(k, v);
            } else {
                result.put(k, unionStrategy.unionData(v, oldV));
            }
        }
        return result.values();
    }

    public static <K, V, DK extends GroupKey, DT extends GroupTotalData> void unionGroupData(Collection<TotalDataGroup<DK, V, DT>> datas, TotalDataUnionStrategy<K, V> unionStrategy) {
        for (TotalDataGroup<DK, V, DT> group : datas) {
            unionData(group, unionStrategy);
        }
    }

    public static <K, V> void unionData(TotalDataGroup<?, V, ?> data, TotalDataUnionStrategy<K, V> unionStrategy) {

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
            for (TotalDataGroup<?, V, ?> d : data.getChildGroup()) {
                unionData(d, unionStrategy);
            }
        }

        data.values = new ArrayList<V>(result.values());

    }

    public static <V extends Comparable<? super V>> void sort(TotalDataGroup<?, V, ?> data) {
        Collections.sort(data.values);

        if (!data.isLeaf()) {
            for (TotalDataGroup<?, V, ?> d : data.getChildGroup()) {
                sort(d);
            }
        }
    }

    public static <V> void sort(TotalDataGroup<?, V, ?> data, Comparator<? super V> c) {
        Collections.sort(data.values, c);

        if (!data.isLeaf()) {
            for (TotalDataGroup<?, V, ?> d : data.getChildGroup()) {
                sort(d, c);
            }
        }
    }

    public interface TotalDataExportStrategy<V, T extends GroupTotalData> {

        public int wirteData(int row, int beginCol, V value, ExportRender render);

        public int wirteTotal(int row, int beginCol, T value, GroupKey<?> key, ExportRender render, int childCount);

        public void wirteKey(int row, int col, int toRow, int toCol, GroupKey<?> key, ExportRender render);

        public int wirteHeader(ExportRender render);

    }


    public static <K extends GroupKey, V, T extends GroupTotalData> int export(int beginRow, int beginCol, int level, Collection<TotalDataGroup<K, V, T>> datas,
                                                                               TotalDataExportStrategy<V, T> strategy,
                                                                               ExportRender render) {
        int row = beginRow;
        for (TotalDataGroup<K, V, T> data : datas) {
            row = export(row, beginCol, level, data, strategy, render);
        }
        return row;
    }

    public static <K extends GroupKey, V, T extends GroupTotalData> void export(Collection<TotalDataGroup<K, V, T>> datas,
                                                                                TotalDataExportStrategy<V, T> strategy,
                                                                                ExportRender render, OutputStream outputStream) throws IOException {

        render.setNextCellType(ExportRender.Type.HEADER, 0);
        int row = strategy.wirteHeader(render);
        for (TotalDataGroup<K, V, T> data : datas) {
            row = export(row, 0, 0, data, strategy, render);
        }

        render.write(outputStream);
    }

    private static <V, T extends GroupTotalData> int export(int beginRow, int beginCol, int level,
                                                            TotalDataGroup<? extends GroupKey, V, T> data,
                                                            TotalDataExportStrategy<V, T> strategy, ExportRender render) {

        //render.setNextRowType(ExportRender.Type.DATA, level);
        render.setNextCellType(ExportRender.Type.DATA, level);
        int row = beginRow;
        //int col = beginCol;
        int childCount;
        if (data.isLeaf()) {
            childCount = data.values.size();
            for (V v : data.values) {
                render.setNextCellType(ExportRender.Type.DATA, level);
                row = strategy.wirteData(row, beginCol + 1, v, render);
            }
        } else {
            childCount = data.getChildGroup().size();
            for (TotalDataGroup<? extends GroupKey, V, T> child : data.getChildGroup()) {
                row = export(row, beginCol + 1, level + 1, child, strategy, render);
            }
        }
        render.setNextCellType(ExportRender.Type.HEADER, level);
        strategy.wirteKey(beginRow, beginCol, row - 1, beginCol, data.getKey(), render);
        // render.setNextRowType(ExportRender.Type.FOOTER, level);
        render.setNextCellType(ExportRender.Type.FOOTER, level);
        row = strategy.wirteTotal(row, beginCol, data.getTotalData(), data.getKey(), render, childCount);

        return row;

    }

    @Override
    public int compareTo(TotalDataGroup o) {
        return getKey().getKeyData().compareTo(o.getKey().getKeyData());
    }

    private T totalData;

    public T getTotalData() {
        return totalData;
    }


    public interface GroupTotalData {

    }

    public interface GroupKey<T extends Comparable<? super T>> {

        public T getKeyData();

    }

    public static abstract class GroupKeyHelper<T extends Comparable<? super T>> implements GroupKey<T> {


        @Override
        public boolean equals(Object other) {

            if (other == null) {
                return false;
            }
            if (other == this) {
                return true;
            }

            if (!(other instanceof GroupKeyHelper)) {
                return false;
            }

            GroupKeyHelper otherKey = (GroupKeyHelper) other;

            if ((otherKey.getKeyData() != null) && (getKeyData() != null)) {
                return getKeyData().equals(otherKey.getKeyData());
            }

            return false;
        }

        @Override
        public int hashCode() {
            return 17 * 37 + ((getKeyData() != null) ? getKeyData().hashCode() : super.hashCode());
        }


    }

    public static class DateKey extends GroupKeyHelper<Date> implements Serializable {

        private Date date = new Date();

        public DateKey(Date date) {
            this.date = date;
        }

        @Override
        public Date getKeyData() {
            return date;
        }


    }
}
