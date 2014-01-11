package com.dgsoft.erp.action;

import com.dgsoft.erp.model.Factory;
import com.dgsoft.erp.model.ProductGroup;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by cooper on 1/11/14.
 */
@Name("productGroupSelect")
public class ProductGroupSelect {

    private Factory factory;

    private ProductGroup productGroup;

    @In(create = true)
    private EntityQuery<Factory> validFactoryList;

    @In(create = true)
    private EntityManager erpEntityManager;

    private String selectGroupId;


    public String getSelectGroupId() {
        return selectGroupId;
    }

    public void setSelectGroupId(String selectGroupId) {
        this.selectGroupId = selectGroupId;
    }

    public void productGroupIdSelected(){
        productGroup = erpEntityManager.find(ProductGroup.class,selectGroupId);
        if (productGroup != null){
            factory = productGroup.getFactory();
        }
    }

    public Factory getFactory() {
        return factory;
    }

    public void setFactory(Factory factory) {
        this.factory = factory;

    }

    public List<Factory> getFactoryTree(){
        if (factory == null){
            return validFactoryList.getResultList();
        }else{
            List<Factory> result = new ArrayList<Factory>(1);
            result.add(factory);
            return result;
        }
    }

    public ProductGroup getProductGroup() {
        return productGroup;
    }

    public void setProductGroup(ProductGroup productGroup) {
        this.productGroup = productGroup;
    }

    public List<ProductGroup> getFactoryProductGroups() {
        List<ProductGroup> result;
        if (factory != null) {

            result = new ArrayList<ProductGroup>(factory.getProductGroups());

        }else {
            result = erpEntityManager.createQuery("select productGroup from ProductGroup productGroup where productGroup.enable = true", ProductGroup.class).getResultList();

        }
        Collections.sort(result, new Comparator<ProductGroup>() {
            @Override
            public int compare(ProductGroup o1, ProductGroup o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });
        return result;
    }
}
