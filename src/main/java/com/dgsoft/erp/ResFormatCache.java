package com.dgsoft.erp;

import com.dgsoft.common.utils.LRULinkedHashMap;
import com.dgsoft.erp.model.Format;
import com.dgsoft.erp.model.StoreRes;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.contexts.Contexts;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 19/04/14
 * Time: 11:31
 */
@Name("resFormatCache")
@AutoCreate
@Scope(ScopeType.APPLICATION)
@Startup
public class ResFormatCache {

    private final static int CAPACITY = 10000;

    private Map<String, List<Format>> cache = new LRULinkedHashMap<String, List<Format>>(CAPACITY);

    @In
    private EntityManager erpEntityManager;

    public static ResFormatCache instance() {
        if (!Contexts.isEventContextActive()) {
            throw new IllegalStateException("no active event context");
        }
        return (ResFormatCache) Component.getInstance(ResFormatCache.class, ScopeType.APPLICATION, true);
    }

    public List<Format> getFormats(String storeResId) {
        List<Format> result = cache.get(storeResId);
        if (result == null) {
            result = erpEntityManager.createQuery("select format from Format format left join format.formatDefine where format.storeRes.id = :storeResId order by format.formatDefine.priority", Format.class).setParameter("storeResId", storeResId).getResultList();
            cache.put(storeResId, result);
        }

        return result;
    }

    public List<Format> getFormats(StoreRes storeRes) {
        return getFormats(storeRes.getId());
    }

}
