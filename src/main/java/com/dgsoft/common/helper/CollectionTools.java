package com.dgsoft.common.helper;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 2/19/14
 * Time: 1:11 PM
 */
@Name("collectionTools")
@Scope(ScopeType.STATELESS)
public class CollectionTools {


    public <K extends Comparable<K>, V> List<Map.Entry<K, V>> mapToList(Map<K, V> map) {
        if (map == null) {
            return null;
        }
        List<Map.Entry<K, V>> result = new ArrayList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(result, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });
        return result;
    }

    public <E extends Comparable<E>> List<E> setToList(Set<E> set) {
        if (set == null) {
            return null;
        }
        return new ArrayList<E>(set);
    }

    public List<String> strToList(String s){
        if ((s == null) || s.trim().equals("")){
            return null;
        }
        String replace = s.replace("[","").replace("]","");
        return new ArrayList<String>(Arrays.asList(replace.split(",")));
    }


    public static CollectionTools instance() {
        if (!Contexts.isEventContextActive()) {
            throw new IllegalStateException("no active event context");
        }
        return (CollectionTools) Component.getInstance(CollectionTools.class, ScopeType.STATELESS, true);
    }

}
