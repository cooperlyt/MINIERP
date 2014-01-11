package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpSimpleEntityHome;
import com.dgsoft.erp.model.Factory;
import com.dgsoft.erp.model.ProductGroup;
import com.dgsoft.erp.model.Res;
import org.jboss.seam.annotations.*;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.richfaces.component.UITree;
import org.richfaces.event.TreeSelectionChangeEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cooper on 1/11/14.
 */
@Name("productGroupHome")
public class ProductGroupHome extends ErpSimpleEntityHome<ProductGroup> {

    @In
    private FacesMessages facesMessages;

    @End
    public void selectionChanged(TreeSelectionChangeEvent selectionChangeEvent) {
        List<Object> selection = new ArrayList<Object>(selectionChangeEvent.getNewSelection());
        Object currentSelectionKey = selection.get(0);
        UITree tree = (UITree) selectionChangeEvent.getSource();

        Object storedKey = tree.getRowKey();
        tree.setRowKey(currentSelectionKey);

        Object rowData = tree.getRowData();
        if (rowData instanceof Factory){
            selectFactory = (Factory) rowData;
            clearInstance();
        }else{
            setId(((ProductGroup)rowData).getId());
            selectFactory = null;
        }

        tree.setRowKey(storedKey);
    }

    private ProductGroup parent;

    private Factory selectFactory;

    private String selectProductResId;

    public ProductGroup getParent() {
        return parent;
    }

    public Factory getSelectFactory() {
        return selectFactory;
    }

    public String getSelectProductResId() {
        return selectProductResId;
    }

    public void setSelectFactoryId(String id){
        selectFactory = getEntityManager().find(Factory.class,id);
    }

    public String getSelectFactoryId(){
        if (selectFactory != null){
            return selectFactory.getId();
        }else
            return null;
    }

    public void setSelectProductResId(String selectProductResId) {
        this.selectProductResId = selectProductResId;
    }

    public void deleteProductRes(){
        for (Res res: getInstance().getReses()){
            if (res.getId().equals(selectProductResId)){
                getInstance().getReses().remove(res);

                return;
            }
        }
        facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,"cantDeleteGroupProduct");
    }

    @Observer(value = "erp.resLocateSelected", create = false)
    public void resSelectedListener(Res res){
        getInstance().getReses().add(res);
    }

    @Override
    protected ProductGroup createInstance(){
        return new ProductGroup(true);
    }

    @Override
    @Begin(flushMode = FlushModeType.MANUAL)
    public String createNew(){
        if (isIdDefined()){
            parent = getInstance();
            selectFactory = null;
        }else{
            parent = null;
        }

        clearInstance();

        return super.createNew();
    }

    @Override
    protected boolean wire(){
        if (!isIdDefined()){
            if (parent != null){
                getInstance().setParentGroup(parent);
                getInstance().setFactory(parent.getFactory());
            }else{
                getInstance().setParentGroup(null);
                getInstance().setFactory(selectFactory);
            }

        }
        return true;
    }

    @Override
    protected boolean verifyRemoveAvailable() {
        if (!getInstance().getChildrenGroups().isEmpty() ||
                !getInstance().getProductStoreIns().isEmpty()) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "cantDeleteGroup");
            return false;
        }
        return true;
    }


}
