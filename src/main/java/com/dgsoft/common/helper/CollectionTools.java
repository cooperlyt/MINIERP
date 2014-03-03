package com.dgsoft.common.helper;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 2/19/14
 * Time: 1:11 PM
 */
@Name("collectionTools")
@Scope(ScopeType.STATELESS)
public class CollectionTools {

    public <K,V> List<Map.Entry<K,V>> mapToList(Map<K,V> map){

        return new ArrayList(map.entrySet());
    }

    public List setToList(Set set){
       return new ArrayList(set);
    }
}
