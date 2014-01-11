package com.dgsoft.erp.action;

import com.dgsoft.common.system.action.RoleHome;
import com.dgsoft.erp.ErpSimpleEntityHome;
import com.dgsoft.erp.model.Factory;
import com.dgsoft.erp.model.ProductGroup;
import com.dgsoft.erp.model.Res;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.security.Identity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by cooper on 1/11/14.
 */
@Name("factoryHome")
public class FactoryHome extends ErpSimpleEntityHome<Factory> {

    @In
    private FacesMessages facesMessages;

    @In
    private Identity identity;

    @In(create = true)
    private RoleHome roleHome;

    private boolean autoGenerateRole;

    public boolean isAutoGenerateRole() {
        return autoGenerateRole;
    }

    public void setAutoGenerateRole(boolean autoGenerateRole) {
        this.autoGenerateRole = autoGenerateRole;
    }


    @Override
    @Begin(flushMode = FlushModeType.MANUAL)
    public String createNew(){

        autoGenerateRole = true;
        return super.createNew();
    }

    @Override
    protected Factory createInstance(){
        return new Factory(true);
    }

    @Override
    protected boolean wire(){
        if (!isManaged() && autoGenerateRole){
            String role = "factory." + getInstance().getId();
            roleHome.clearInstance();
            roleHome.getInstance().setId(role);
            roleHome.getInstance().setName(getInstance().getName());
            roleHome.getInstance().setPriority(1000);
            roleHome.persist();
            getInstance().setFactoryRole(role);
        }
        return true;
    }

    @Override
    protected boolean verifyRemoveAvailable() {
        if (!getInstance().getProductGroups().isEmpty()) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "cantDeleteFactory");
            return false;
        }
        return true;
    }

    public List<Res> getOutProductList(){
        Set<Res> result = new HashSet<Res>();
        for (ProductGroup pg: getInstance().getRootProductGroupList()){
            result.addAll(pg.getProducts());
        }
        return new ArrayList<Res>(result);
    }

    @org.jboss.seam.annotations.Factory(value = "myFactory",scope = ScopeType.CONVERSATION)
    public List<Factory> getMyFactory(){
        List<Factory> result = new ArrayList<Factory>();
        for (Factory factory: getEntityManager().createQuery("select factory from Factory factory where factory.enable = true",Factory.class).getResultList()){

            if (identity.hasRole("erp.produce.manager") || identity.hasRole(factory.getFactoryRole())){
                result.add(factory);
            }
        }
        return result;
    }

}
