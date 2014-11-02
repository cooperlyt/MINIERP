package com.dgsoft.erp.action;

import com.dgsoft.common.SetLinkList;
import com.dgsoft.common.helper.ActionExecuteState;
import com.dgsoft.erp.ErpSimpleEntityHome;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.tools.StoreResCondition;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 10/27/13
 * Time: 2:47 PM
 * To change this template use File | Settings | File Templates.
 */
@Name("middleManHome")
public class MiddleManHome extends ErpSimpleEntityHome<MiddleMan> {

    @In
    private FacesMessages facesMessages;

    private SetLinkList<Customer> customers;

    private SetLinkList<SalerPrice> salerPrices;

    private SalerPrice selectSalerPrice;

    public void setResId(String resId) {
        for (SalerPrice rebate : getSalerPrices()) {
            if (rebate.getRes().getId().equals(resId)) {
                selectSalerPrice = rebate;
                return;
            }
        }
        selectSalerPrice = null;
    }

    public String getResId() {
        if (selectSalerPrice == null) {
            return null;
        } else {
            return selectSalerPrice.getRes().getId();
        }
    }


    private boolean containRes(Res res) {
        for (SalerPrice rebate : getSalerPrices()) {
            if (rebate.getRes().equals(res)) {
                return true;
            }
        }
        return false;
    }

    public SetLinkList<SalerPrice> getSalerPrices() {
        if (salerPrices == null) {
            salerPrices = new SetLinkList<SalerPrice>(getInstance().getSalerPrices());
            List<Res> allRes = getEntityManager().createQuery("select res from Res res where res.enable = true and res.resCategory.type = :type", Res.class).setParameter("type", ResCategory.ResType.PRODUCT).getResultList();
            for (Res res : allRes) {
                if (!containRes(res)) {
                    salerPrices.add(new SalerPrice(BigDecimal.ZERO, res.getResUnitByOutDefault(), res, getInstance()));
                }
            }
            Collections.sort(salerPrices, new Comparator<SalerPrice>() {
                @Override
                public int compare(SalerPrice o1, SalerPrice o2) {
                    return o1.getRes().compareTo(o2.getRes());
                }
            });
        }
        return salerPrices;
    }


    public void clearStoreResItem() {
        selectSalerPrice.getSalerStoreResPrices().clear();
    }


    private String selectStoreResId;

    public String getSelectStoreResId() {
        return selectStoreResId;
    }

    public void setSelectStoreResId(String selectStoreResId) {
        this.selectStoreResId = selectStoreResId;
    }

    public void deleteStoreResItem() {
        for (SalerStoreResPrice rebate : selectSalerPrice.getSalerStoreResPrices()) {
            if (rebate.getStoreRes().getId().equals(selectStoreResId)) {
                selectSalerPrice.getSalerStoreResPrices().remove(rebate);
                return;
            }
        }
    }


    @In(required = false)
    private StoreResCondition storeResCondition;

    @In
    private ActionExecuteState actionExecuteState;

    private ResUnit batchResUnit;

    private BigDecimal batchPrice;

    public ResUnit getBatchResUnit() {
        return batchResUnit;
    }

    public void setBatchResUnit(ResUnit batchResUnit) {
        this.batchResUnit = batchResUnit;
    }

    public BigDecimal getBatchPrice() {
        return batchPrice;
    }

    public void setBatchPrice(BigDecimal batchPrice) {
        this.batchPrice = batchPrice;
    }

    public void batchAddStoreRes() {
        for (StoreRes storeRes : storeResCondition.getMatchStoreReses()) {
            boolean exists = false;
            for (SalerStoreResPrice resRebate : selectSalerPrice.getSalerStoreResPrices()) {
                if (resRebate.getStoreRes().getId().equals(storeRes.getId())) {
                    resRebate.setPrice(batchPrice);
                    resRebate.setResUnit(batchResUnit);
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                selectSalerPrice.getSalerStoreResPrices().add(new SalerStoreResPrice(batchPrice, selectSalerPrice, storeRes, batchResUnit));
            }
        }
        actionExecuteState.actionExecute();
    }

    @DataModel("middleManCustomers")
    public SetLinkList<Customer> getCustomers() {
        return customers;
    }

    @DataModelSelection
    private Customer selectCustomer;

    public void removeCustomer() {
        getCustomers().remove(selectCustomer);
        selectCustomer.setMiddleMan(null);
    }

    @In(required = false)
    public CustomerHome customerHome;

    public void addCustomer() {
        if (customerHome.getInstance().getMiddleMan() != null) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "customerHaveMiddleManError", customerHome.getInstance().getMiddleMan().getName());
            return;
        }
        customerHome.getInstance().setMiddleMan(getInstance());
        getCustomers().add(customerHome.getInstance());
    }

    @Override
    protected void initInstance() {
        super.initInstance();
        customers = new SetLinkList<Customer>(getInstance().getCustomers());
        salerPrices = null;
    }

    @Override
    protected MiddleMan createInstance() {
        return new MiddleMan(true);
    }

    @Override
    protected boolean verifyRemoveAvailable() {

        if (getInstance().getCustomers().isEmpty() && getInstance().getMiddleMoneys().isEmpty()) {
            return true;
        } else {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "usageCantDelete");
            return false;
        }
    }

}
