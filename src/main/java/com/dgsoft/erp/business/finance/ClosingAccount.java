package com.dgsoft.erp.business.finance;

import com.dgsoft.common.utils.finance.Account;
import com.dgsoft.erp.model.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Events;
import org.jboss.seam.log.Logging;
import org.jboss.seam.security.Credentials;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * Created by cooper on 5/17/14.
 */
@Name("closingAccount")
public class ClosingAccount {

    @In
    private AccountDateHelper accountDateHelper;

    @In
    private EntityManager erpEntityManager;


    @In
    private Credentials credentials;

    @In(create = true)
    private SaleAccountClose saleAccountClose;

    @In(create = true)
    private AccountTitleHelper accountTitleHelper;

    @Transactional
    public void unclosing() {
        Long maxId = erpEntityManager.createQuery("select max(checkout.id) from Checkout checkout", Long.class).getSingleResult();
        if (maxId == null) {
            return;
        }
        erpEntityManager.remove(erpEntityManager.find(Checkout.class, maxId));
        erpEntityManager.flush();

        Events.instance().raiseTransactionSuccessEvent("erp.closingAccount");

    }

    @Transactional
    public void closing() {
        Long maxId = erpEntityManager.createQuery("select max(checkout.id) from Checkout checkout", Long.class).getSingleResult();


        Checkout checkout = new Checkout((maxId == null) ? new Long(1) : maxId + 1, new Date(), credentials.getUsername(), accountDateHelper.getNextBeginDate(), accountDateHelper.getNextCloseDate());
        if (maxId != null) {
            for (AccountCheckout aco : erpEntityManager.find(Checkout.class, maxId).getAccountCheckouts()) {
                checkout.getAccountCheckouts().add(new AccountCheckout(aco.getAccountCode(), checkout, aco.getClosingBalance(), aco.getClosingCount()));
            }
            for (StockAccount sa : erpEntityManager.find(Checkout.class, maxId).getStockAccounts()) {
                checkout.getStockAccounts().add(new StockAccount(sa.getCloseCount(), checkout, sa.getStock()));
            }

        }
        if (!saleAccountClose.canClose(checkout) || !validStockCount()) {
            return;
        }
        closeStockAccount(checkout);

        saleAccountClose.doClose(checkout);


        for (AccountCheckout aco : checkout.getAccountCheckouts()) {

            Account account = accountTitleHelper.getAccountByCode(aco.getAccountCode());
            if (account.getDirection().equals(Account.Direction.CREDIT)) {
                aco.setClosingBalance(aco.getBeginningBalance().add(aco.getCreditMoney()).subtract(aco.getDebitMoney()));
                aco.setClosingCount(aco.getBeginningCount().add(aco.getCreditCount()).subtract(aco.getDebitCount()));
            } else {
                aco.setClosingBalance(aco.getBeginningBalance().add(aco.getDebitMoney()).subtract(aco.getCreditMoney()));
                aco.setClosingCount(aco.getBeginningCount().add(aco.getDebitCount()).subtract(aco.getCreditCount()));
            }

        }

        erpEntityManager.persist(checkout);
        erpEntityManager.flush();

        Events.instance().raiseTransactionSuccessEvent("erp.closingAccount");
    }


    private static final String ALL_STOCK_CHANGE_EJBQL = "select COALESCE(sum(stockChangeItem.count),0) from StockChangeItem stockChangeItem " +
            "where stockChangeItem.stock.id = :stockId " +
            "and stockChangeItem.stockChange.verify = true and stockChangeItem.stockChange.operType in (:types)";

    private static final String STOCK_CHANGE_EJBQL = ALL_STOCK_CHANGE_EJBQL + " and stockChangeItem.stockChange.operDate >= :beginDate " +
            "and stockChangeItem.stockChange.operDate <= :closeDate";

    private boolean validStockCount() {

        if (!accountDateHelper.isFirst()) {

            for (Stock stock : erpEntityManager.createQuery("select stock from Stock stock", Stock.class).getResultList()) {
                BigDecimal currentCount = erpEntityManager.createQuery(ALL_STOCK_CHANGE_EJBQL, BigDecimal.class)
                        .setParameter("stockId", stock.getId()).setParameter("types",
                                new ArrayList<StockChange.StoreChangeType>(StockChange.StoreChangeType.getAllIn())).getSingleResult();

                currentCount = currentCount.subtract(erpEntityManager.createQuery(ALL_STOCK_CHANGE_EJBQL, BigDecimal.class)
                        .setParameter("stockId", stock.getId()).setParameter("types",
                                new ArrayList<StockChange.StoreChangeType>(StockChange.StoreChangeType.getAllOut())).getSingleResult());

                if (stock.getCount().compareTo(currentCount) != 0) {
                    throw new IllegalArgumentException("stock count not equals stockID:" + stock.getId() + "|count:" + stock.getCount() + "|calcCount:" + currentCount);
                }

            }
        }
        return true;
    }

    private void closeStockAccount(Checkout checkout) {

        Map<String, StockAccount> saMap = checkout.getStockAccountMap();


        if (accountDateHelper.isFirst()) {

            for (Stock stock : erpEntityManager.createQuery("select stock from Stock stock", Stock.class).getResultList()) {
                BigDecimal currentCount = erpEntityManager.createQuery(ALL_STOCK_CHANGE_EJBQL, BigDecimal.class)
                        .setParameter("stockId", stock.getId()).setParameter("types",
                                new ArrayList<StockChange.StoreChangeType>(StockChange.StoreChangeType.getAllIn())).getSingleResult();

                currentCount = currentCount.subtract(erpEntityManager.createQuery(ALL_STOCK_CHANGE_EJBQL, BigDecimal.class)
                        .setParameter("stockId", stock.getId()).setParameter("types",
                                new ArrayList<StockChange.StoreChangeType>(StockChange.StoreChangeType.getAllOut())).getSingleResult());

                if (stock.getCount().compareTo(currentCount) != 0) {
                    stock.setCount(currentCount);
                    Logging.getLog(getClass()).warn("change stock change :"+ stock.getId() + "|count:" + stock.getCount() + "|calcCount:" + currentCount);
                }

            }
        }

        for (Stock stock : erpEntityManager.createQuery("select stock from Stock stock", Stock.class).getResultList()) {
            StockAccount sa = saMap.get(stock.getId());
            if (sa == null) {
                sa = new StockAccount(BigDecimal.ZERO, checkout, stock);
                saMap.put(sa.getId(), sa);
                checkout.getStockAccounts().add(sa);
            }

            BigDecimal closeCount = erpEntityManager.createQuery(STOCK_CHANGE_EJBQL, BigDecimal.class)
                    .setParameter("stockId", stock.getId())
                    .setParameter("types", new ArrayList<StockChange.StoreChangeType>(StockChange.StoreChangeType.getAllIn()))
                    .setParameter("beginDate", checkout.getBeginDate())
                    .setParameter("closeDate", checkout.getCloseDate()).getSingleResult();

            closeCount = closeCount.subtract(erpEntityManager.createQuery(STOCK_CHANGE_EJBQL, BigDecimal.class)
                    .setParameter("stockId", stock.getId())
                    .setParameter("types", new ArrayList<StockChange.StoreChangeType>(StockChange.StoreChangeType.getAllOut()))
                    .setParameter("beginDate", checkout.getBeginDate())
                    .setParameter("closeDate", checkout.getCloseDate()).getSingleResult());

            sa.setCloseCount(closeCount);
        }

    }


}
