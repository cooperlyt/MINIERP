package com.dgsoft.common;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 6/20/13
 * Time: 1:21 PM
 */
public class OrderBeanComparator implements Comparator<OrderModel> {

    private static OrderBeanComparator instance;

    public static OrderBeanComparator getInstance() {
        if (instance == null) {
            instance = new OrderBeanComparator();
        }
        return instance;
    }

    public static <T extends OrderModel> void up(T target, List<T> collection) {
        Collections.sort(collection, getInstance());
        int index = collection.indexOf(target);
        if (index > 0) {
            int curPriority = target.getPriority();
            target.setPriority(collection.get(index - 1).getPriority());
            collection.get(index - 1).setPriority(curPriority);
        }
        Collections.sort(collection, getInstance());
    }

    public static <T extends OrderModel> void down(T target, List<T> collection) {
        Collections.sort(collection, getInstance());
        int index = collection.indexOf(target);
        if (index >= 0 && index < (collection.size() - 1)) {
            int curPriority = target.getPriority();
            target.setPriority(collection.get(index + 1).getPriority());
            collection.get(index + 1).setPriority(curPriority);
        }
        Collections.sort(collection, getInstance());
    }

    public static <T extends OrderModel> void addToLast(T target, List<T> collection) {
        if (!collection.isEmpty()) {


            Collections.sort(collection, getInstance());
            target.setPriority(collection.get(collection.size() - 1).getPriority() + 1);

        }else{
            target.setPriority(1);
        }

        collection.add(target);
    }

    private OrderBeanComparator() {
        super();
    }

    @Override
    public int compare(OrderModel o1, OrderModel o2) {
        return new Integer(o1.getPriority()).compareTo(o2.getPriority());
    }
}
