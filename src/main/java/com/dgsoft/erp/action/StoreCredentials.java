package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityLoader;
import com.dgsoft.erp.model.Store;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cooper on 11/29/14.
 */
@Name("storeCredentials")
@Scope(ScopeType.SESSION)
@AutoCreate
public class StoreCredentials {

    @In
    private Identity identity;

    @In(create = true)
    private ErpEntityLoader erpEntityLoader;

    private List<Store> viewStores;

    private List<Store> editStores;

    private List<Store> getAllStore(){
       return erpEntityLoader.getEntityManager().createQuery("select store from Store store where store.enable = true",Store.class).getResultList();
    }

    public List<Store> getViewStores() {
        if(viewStores == null){
            if(identity.hasRole("erp.storage.manager") || identity.hasRole("erp.storage.dispatch")){
                viewStores = getAllStore();
            }else if (identity.hasRole("erp.storage.store")){
                viewStores = new ArrayList<Store>();
                for(Store store: getAllStore()){
                    if (identity.hasRole(store.getRole())){
                        viewStores.add(store);
                    }
                }
            }else{
                viewStores = new ArrayList<Store>(0);
            }
        }
        return viewStores;
    }

    public List<String> getSearchViewStoreIds(){
        List<String> result = new ArrayList<String>();
        for(Store store: getViewStores()){
            result.add(store.getId());
        }
        if (result.isEmpty()){
            result.add("-none-");
        }
        return result;
    }

    public List<String> getSearchEditStoreIds(){
        List<String> result = new ArrayList<String>();
        for(Store store: getEditStores()){
            result.add(store.getId());
        }
        if (result.isEmpty()){
            result.add("-none-");
        }
        return result;
    }

    public List<Store> getEditStores() {
        if(editStores == null){
            if(identity.hasRole("erp.storage.manager")){
                editStores = getAllStore();
            }else if (identity.hasRole("erp.storage.store")){
                editStores = new ArrayList<Store>();
                for(Store store: getAllStore()){
                    if (identity.hasRole(store.getRole())){
                        editStores.add(store);
                    }
                }
            }else{
                editStores = new ArrayList<Store>(0);
            }
        }
        return editStores;
    }

}
