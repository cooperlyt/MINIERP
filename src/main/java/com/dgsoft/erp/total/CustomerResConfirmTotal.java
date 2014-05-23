package com.dgsoft.erp.total;

import com.dgsoft.common.SearchDateArea;
import com.dgsoft.erp.model.BackItem;
import com.dgsoft.erp.model.CustomerOrder;
import com.dgsoft.erp.model.OrderItem;
import com.dgsoft.erp.model.api.StoreResPriceEntity;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 14-5-23
 * Time: 下午1:11
 */
@Name("customerResConfirmTotal")
public class CustomerResConfirmTotal {

    private String customerId;

    private SearchDateArea searchDateArea = new SearchDateArea(new Date(), new Date());

    private static final String BACK_ITEM_EJBQL = "select backItem from BackItem backItem " +
            "left join fetch backItem.storeRes storeRes left join fetch storeRes.res res " +
            "left join fetch res.unitGroup unitGroup " +
            "left join fetch backItem.dispatch dispatch left join fetch dispatch.stockChange stockChange " +
            "left join fetch backItem.orderBack orderBack left join fetch orderBack.customer customer " +
            "left join fetch customer.customerArea left join fetch customer.customerLevel  " +
            "where backItem.backItemStatus = 'STORE_IN' and backItem.orderBack.moneyComplete = true " +
            "and  backItem.dispatch.stockChange.operDate >= :dateFrom " +
            "and backItem.dispatch.stockChange.operDate <= :searchDateTo and backItem.orderBack.customer.id = :customerId ";


    private static final String ORDER_TEIM_EJBQL = "select orderItem from OrderItem orderItem " +
            "left join fetch orderItem.dispatch dispatch left join fetch dispatch.stockChange stockChange " +
            "left join fetch orderItem.storeRes storeRes left join fetch storeRes.res res " +
            "left join fetch res.unitGroup unitGroup left join fetch dispatch.stockChange " +
            "left join fetch orderItem.needRes needRes left join fetch needRes.customerOrder customerOrder " +
            "left join fetch customerOrder.customer customer left join fetch customer.customerArea " +

            "left join fetch customer.customerLevel " +
            "where orderItem.needRes.customerOrder.canceled = false and orderItem.status = 'COMPLETED' " +
            "and orderItem.needRes.customerOrder.allStoreOut = true " +
            "and orderItem.dispatch.sendTime >= :dateFrom and orderItem.dispatch.sendTime <= :searchDateTo " +
            "and orderItem.dispatch.needRes.customerOrder.customer.id = :customerId";


    @In
    private EntityManager erpEntityManager;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public SearchDateArea getSearchDateArea() {
        return searchDateArea;
    }

    private List<StoreResPriceEntity> resultList;

    private void initResultList(){
        if (resultList == null){
            resultList = new ArrayList<StoreResPriceEntity>();
            resultList.addAll(
                    erpEntityManager.createQuery(BACK_ITEM_EJBQL, BackItem.class)
                            .setParameter("dateFrom", searchDateArea.getDateFrom())
                            .setParameter("searchDateTo", searchDateArea.getSearchDateTo())
                            .setParameter("customerId", getCustomerId()).getResultList());

            resultList.addAll(
                    erpEntityManager.createQuery(ORDER_TEIM_EJBQL, OrderItem.class)
                            .setParameter("dateFrom", searchDateArea.getDateFrom())
                            .setParameter("searchDateTo", searchDateArea.getSearchDateTo())
                            .setParameter("customerId", getCustomerId()).getResultList());

            Collections.sort(resultList, new SaleBackItemComparator());
        }
    }

    public List<StoreResPriceEntity> getResultList() {
        initResultList();
        return resultList;
    }

    public BigDecimal getTotalMoney(){
        Map<String,BigDecimal> resultMap = new HashMap<String, BigDecimal>();
        for(StoreResPriceEntity entity: getResultList()){
            if (entity instanceof OrderItem){
                CustomerOrder order = ((OrderItem)entity).getDispatch().getNeedRes().getCustomerOrder();
                resultMap.put(order.getId(),order.getMoney());
            }
        }
        BigDecimal result = BigDecimal.ZERO;
        for(BigDecimal money: resultMap.values()){
            result = result.add(money);
        }
        return result;
    }

}
