package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpSimpleEntityHome;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.tools.ResTreeFilter;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 19/03/14
 * Time: 15:18
 */
@Name("rebateProgramHome")
public class RebateProgramHome extends ErpSimpleEntityHome<RebateProgram> {

    @In
    private FacesMessages facesMessages;

    @org.jboss.seam.annotations.Factory(value = "orderRebateCalcModes", scope = ScopeType.SESSION)
    public RebateProgram.OrderRebateMode[] getOrderRebateCalcModes(){
        return RebateProgram.OrderRebateMode.values();
    }

    @Factory(value = "orderItemRebateCalcModes", scope = ScopeType.SESSION)
    public OrderItemRebate.ItemRebateModel[] getOrderItemRebateCalcModes(){
        return OrderItemRebate.ItemRebateModel.values();
    }

    private List<OrderItemRebate> orderItemRebateList = null;

    private OrderItemRebate selectOrderItemRebate;

    private String selectStoreResId;

    public List<OrderItemRebate> getOrderItemRebateList() {
        return orderItemRebateList;
    }

    public void setResId(String resId) {
        for (OrderItemRebate rebate : orderItemRebateList) {
            if (rebate.getRes().getId().equals(resId)) {
                selectOrderItemRebate = rebate;
            }
        }
        selectOrderItemRebate = null;
    }

    public String getResId() {
        if (selectOrderItemRebate == null) {
            return null;
        } else {
            return selectOrderItemRebate.getRes().getId();
        }
    }

    public String getSelectStoreResId() {
        return selectStoreResId;
    }

    public void setSelectStoreResId(String selectStoreResId) {
        this.selectStoreResId = selectStoreResId;
    }

    private boolean containRes(Res res) {
        for (OrderItemRebate rebate : orderItemRebateList) {
            if (rebate.getRes().equals(res)) {
                return true;
            }
        }
        return false;
    }

    private void initOrderItemRebate() {
        if (orderItemRebateList == null) {
            orderItemRebateList = new ArrayList<OrderItemRebate>(getInstance().getOrderItemRebates());
            List<Res> allRes = getEntityManager().createQuery("select res from Res res where res.enable = true and res.resCategory.type = :type", Res.class).setParameter("type", ResCategory.ResType.PRODUCT).getResultList();
            for (Res res : allRes) {
                if (!containRes(res)) {
                    orderItemRebateList.add(new OrderItemRebate(getInstance(), OrderItemRebate.ItemRebateModel.NO_CALC, res));
                }
            }
            Collections.sort(orderItemRebateList);
        }
    }

    public List<Res> getSelectItemResTree(){
        List<Res> result = new ArrayList<Res>();
        if (selectOrderItemRebate != null){
            result.add(selectOrderItemRebate.getRes());
            selectOrderItemRebate.getRes().setResTreeFilter(new ResTreeFilter() {
                @Override
                public StoreResAddType storesAddType() {
                    return StoreResAddType.PROPERTY_ADD;
                }

                @Override
                public boolean containDisable() {
                    return false;
                }

                @Override
                public EnumSet<ResCategory.ResType> getCategoryTypes() {
                    return EnumSet.allOf(ResCategory.ResType.class);
                }

                @Override
                public boolean isAddRes() {
                    return true;
                }

                @Override
                public boolean expandedDefault() {
                    return true;
                }
            });
        }
        return result;


    }


    public void clearStoreResItem(){
        selectOrderItemRebate.getStoreResRebates().clear();
    }

    public void deleteStoreResItem(){
        for(StoreResRebate rebate: selectOrderItemRebate.getStoreResRebates()){
            if  (rebate.getStoreRes().getId().equals(selectStoreResId)){
                selectOrderItemRebate.getStoreResRebates().remove(rebate);
                return;
            }
        }

    }

    @Override
    protected RebateProgram createInstance() {
        return new RebateProgram(RebateProgram.OrderRebateMode.NOT_CALC, true);
    }


    @Override
    protected void initInstance() {
        super.initInstance();
        initOrderItemRebate();
    }

    @Override
    protected boolean verifyRemoveAvailable() {
        if (!getInstance().getMiddleMans().isEmpty()) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "RebateProgram_cant_delete");
            return false;
        }
        return true;
    }

    @Override
    protected boolean wire() {
        getInstance().getOrderItemRebates().clear();
        if (getInstance().isCalcItem()) {

            getInstance().getOrderItemRebates().clear();

            for (OrderItemRebate rebate : orderItemRebateList) {
                if (!rebate.getMode().equals(OrderItemRebate.ItemRebateModel.NO_CALC)) {
                    getInstance().getOrderItemRebates().add(rebate);
                }
            }

        }
        return true;
    }


}
