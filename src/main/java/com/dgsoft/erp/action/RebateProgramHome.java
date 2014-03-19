package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpSimpleEntityHome;
import com.dgsoft.erp.model.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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


    private List<OrderItemRebate> orderItemRebateList = null;

    private OrderItemRebate selectOrderItemRebate;

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

    public void storeResCalcModeChange(){
        for (OrderItemRebate rebate : orderItemRebateList) {

        }
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
