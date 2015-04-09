package com.dgsoft.erp.total;

import com.dgsoft.common.SearchDateArea;
import com.dgsoft.common.system.RunParam;
import com.dgsoft.common.utils.finance.Account;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.StoreResPriceEntity;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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
            "where  backItem.orderBack.createDate >= :dateFrom " +
            "and backItem.orderBack.createDate <= :searchDateTo and backItem.orderBack.customer.id = :customerId ";


    private static final String ORDER_TEIM_EJBQL = "select orderItem from OrderItem orderItem " +

            "left join fetch orderItem.storeRes storeRes left join fetch storeRes.res res " +
            "left join fetch res.unitGroup unitGroup " +
            "left join fetch orderItem.needRes needRes left join fetch needRes.customerOrder customerOrder " +
            "left join fetch customerOrder.customer customer left join fetch customer.customerArea " +

            "left join fetch customer.customerLevel " +
            "where orderItem.needRes.customerOrder.canceled = false " +
            "and orderItem.needRes.customerOrder.payType <> 'PRICE_CHANGE' " +
            //"and orderItem.status = 'COMPLETED' " +
            //"and orderItem.needRes.customerOrder.allStoreOut = true " +
            "and orderItem.needRes.customerOrder.createDate >= :dateFrom and orderItem.needRes.customerOrder.createDate <= :searchDateTo " +
            "and orderItem.needRes.customerOrder.customer.id = :customerId";

    private static final String PRICE_ORDER_ITEM_EJBQL = "select customerOrder from CustomerOrder customerOrder " +
            "where customerOrder.canceled = false AND customerOrder.payType = 'PRICE_CHANGE' " +
            "and customerOrder.createDate >= :dateFrom and customerOrder.createDate <= :searchDateTo " +
            "and customerOrder.customer.id = :customerId";

    private static final String ACCOUNT_MONETY_EJBQL = "select accountOper from AccountOper accountOper where " +
            "(accountOper.operType in ('DEPOSIT_BACK', 'PROXY_SAVINGS','CUSTOMER_SAVINGS','MONEY_FREE','ORDER_PAY') " +
            " or (accountOper.operType = 'ORDER_BACK' and  accountOper.advanceReceivable = 0 and accountOper.moneySave is null)) and " +
            "accountOper.customer.id =:customerId and accountOper.saleCertificate.date >= :beginDate and " +
            "accountOper.saleCertificate.date <= :endDate order by accountOper.saleCertificate.date";

    private static final String ACCOUNT_CHECKOUT_EJBQL = " select accountCheckout from AccountCheckout accountCheckout where " +
            "accountCheckout.accountCode = :aCode  and accountCheckout.checkout.id = :checkoutId  ";


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

    private BigDecimal accountBeginBalance;

    private BigDecimal accountCloseBalance;

    private List<AccountOper> accountOpers;

    public BigDecimal getAccountBeginBalance() {
        return accountBeginBalance;
    }

    public BigDecimal getAccountCloseBalance() {
        return accountCloseBalance;
    }

    public List<AccountOper> getAccountOpers() {
        return accountOpers;
    }

    public List<CustomerOrder> getPriceOrder(){
        return erpEntityManager.createQuery(PRICE_ORDER_ITEM_EJBQL,CustomerOrder.class).setParameter("dateFrom", searchDateArea.getDateFrom())
                .setParameter("searchDateTo", searchDateArea.getSearchDateTo())
                .setParameter("customerId", getCustomerId()).getResultList();
    }

    public boolean setAccountConfirmDate(int year, int month) {
        try {
            Checkout checkout = erpEntityManager.createQuery("select checkout from Checkout checkout " +
                    "where year(checkout.closeDate) = :year and month(checkout.closeDate) = :month", Checkout.class)
                    .setParameter("year", year).setParameter("month", month).getSingleResult();
            searchDateArea.setDateFrom(checkout.getBeginDate());
            searchDateArea.setDateTo(checkout.getCloseDate());


            AccountCheckout accountCheckout = erpEntityManager.createQuery(ACCOUNT_CHECKOUT_EJBQL, AccountCheckout.class)
                    .setParameter("checkoutId", checkout.getId())
                    .setParameter("aCode", RunParam.instance().getStringParamValue("erp.finance.advance") + getCustomerId()).getSingleResult();

            accountBeginBalance = accountCheckout.getBeginningBalance();

            accountCloseBalance = accountCheckout.getClosingBalance();


            accountCheckout = erpEntityManager.createQuery(ACCOUNT_CHECKOUT_EJBQL, AccountCheckout.class)
                    .setParameter("checkoutId", checkout.getId())
                    .setParameter("aCode", RunParam.instance().getStringParamValue("erp.finance.customerAccount") + getCustomerId()).getSingleResult();


            accountBeginBalance = accountBeginBalance.subtract(accountCheckout.getBeginningBalance());

            accountCloseBalance = accountCloseBalance.subtract(accountCheckout.getClosingBalance());


            accountCheckout = erpEntityManager.createQuery(ACCOUNT_CHECKOUT_EJBQL, AccountCheckout.class)
                    .setParameter("checkoutId", checkout.getId())
                    .setParameter("aCode", RunParam.instance().getStringParamValue("erp.finance.proxyAccount") + getCustomerId()).getSingleResult();


            accountBeginBalance = accountBeginBalance.subtract(accountCheckout.getBeginningBalance());

            accountCloseBalance = accountCloseBalance.subtract(accountCheckout.getClosingBalance());


            accountOpers = erpEntityManager.createQuery(ACCOUNT_MONETY_EJBQL, AccountOper.class)
                    .setParameter("beginDate", checkout.getBeginDate())
                    .setParameter("endDate", checkout.getCloseDate()).setParameter("customerId", getCustomerId()).getResultList();

            return true;
        } catch (NoResultException e) {
            return false;
        }
    }

    private void initResultList() {
        if (resultList == null) {
            resultList = new ArrayList<StoreResPriceEntity>();
            resultList.addAll(
                    erpEntityManager.createQuery(BACK_ITEM_EJBQL, BackItem.class)
                            .setParameter("dateFrom", searchDateArea.getDateFrom())
                            .setParameter("searchDateTo", searchDateArea.getSearchDateTo())
                            .setParameter("customerId", getCustomerId()).getResultList()
            );

            resultList.addAll(
                    erpEntityManager.createQuery(ORDER_TEIM_EJBQL, OrderItem.class)
                            .setParameter("dateFrom", searchDateArea.getDateFrom())
                            .setParameter("searchDateTo", searchDateArea.getSearchDateTo())
                            .setParameter("customerId", getCustomerId()).getResultList()
            );

            Collections.sort(resultList, new SaleBackItemComparator());
        }
    }

    public List<StoreResPriceEntity> getResultList() {
        initResultList();
        return resultList;
    }


    public ResTotalMoney getTotalMoney() {
        Map<String, BigDecimal> saleMoneyMap = new HashMap<String, BigDecimal>();
        Map<String, BigDecimal> backMoneyMap = new HashMap<String, BigDecimal>();
        for (StoreResPriceEntity entity : getResultList()) {
            if (entity instanceof OrderItem) {
                CustomerOrder order = ((OrderItem) entity).getNeedRes().getCustomerOrder();
                saleMoneyMap.put(order.getId(), order.getMoney());
            } else if (entity instanceof BackItem) {
                OrderBack orderBack = ((BackItem) entity).getOrderBack();
                backMoneyMap.put(orderBack.getId(), orderBack.getMoney());
            }
        }
        BigDecimal saleMoney = BigDecimal.ZERO;

        for (BigDecimal money : saleMoneyMap.values()) {
            saleMoney = saleMoney.add(money);
        }

        BigDecimal backMoney = BigDecimal.ZERO;
        for (BigDecimal money : backMoneyMap.values()) {
            backMoney = backMoney.add(money);
        }
        return new ResTotalMoney(saleMoney, backMoney);
    }

    public class ResTotalMoney {

        private BigDecimal saleMoney;

        private BigDecimal backMoney;

        public ResTotalMoney(BigDecimal saleMoney, BigDecimal backMoney) {
            this.saleMoney = saleMoney;
            this.backMoney = backMoney;
        }

        public BigDecimal getBackMoney() {
            return backMoney;
        }


        public BigDecimal getSaleMoney() {
            return saleMoney;
        }

        public BigDecimal getTotalMoney() {
            return saleMoney.subtract(backMoney);
        }
    }

}
