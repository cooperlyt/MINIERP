package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.business.finance.AccountDateHelper;
import com.dgsoft.erp.model.Batch;
import com.dgsoft.erp.model.Stock;
import com.dgsoft.erp.model.StockChange;
import com.dgsoft.erp.model.StockChangeItem;
import com.dgsoft.erp.model.api.StoreResCountEntity;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.Logging;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/18/13
 * Time: 11:01 AM
 */

@Name("stockChangeHome")
public class StockChangeHome extends ErpEntityHome<StockChange> {

    @In
    private org.jboss.seam.security.Credentials credentials;

    @In
    private FacesMessages facesMessages;


    public boolean validDate(){
        Date checkDate = getEntityManager().createQuery("select max(inventory.checkDate) from Inventory inventory where inventory.store.id = :storeId",Date.class).
                setParameter("storeId",getInstance().getStore().getId()).getSingleResult();
        if ((checkDate != null) && (checkDate.compareTo(getInstance().getOperDate()) > 0)){
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,"StoreChangeDateIsInventorError");
            return false;
        }
        return true;
    }

    @Override
    protected StockChange createInstance(){
        return new StockChange(true);
    }

    @Override
    protected boolean wire() {
        if (!isManaged())
            getInstance().setOperEmp(credentials.getUsername());
        return true;
    }

    public void resStockChange(Collection<StockChangeItem> items) {
        for (StockChangeItem item : items) {
            resStockChange(item);
        }
    }

    public void resStockChange(StockChangeItem item) {
        if (item.getStock() == null) {
            item.setStock(findOrCreactStock(item));
        } else {
            if (getInstance().getOperType().isOut()) {
                item.getStock().setCount(item.getStock().getCount().subtract(item.getMasterCount()));
            } else
                item.getStock().setCount(item.getStock().getCount().add(item.getMasterCount()));
        }
        getInstance().getStockChangeItems().add(item);
    }

    public void resStockChange(StoreResCountEntity inCount) {
        Stock inStock = findOrCreactStock(inCount);
        getInstance().getStockChangeItems().add(new StockChangeItem(getInstance(), inStock,
                inCount.getMasterCount()));

    }

    private Stock findOrCreactStock(StoreResCountEntity inCount) {
        Stock inStock = null;
        for (Stock stock : inCount.getStoreRes().getStocks()) {
            if (stock.getStore().getId().equals(getInstance().getStore().getId())) {
                inStock = stock;
                break;
            }
        }
        if (inStock == null) {
            inStock = new Stock(getInstance().getStore(), inCount.getStoreRes(), BigDecimal.ZERO);
        }

        if (getInstance().getOperType().isOut()) {
            inStock.setCount(inStock.getCount().subtract(inCount.getMasterCount()));
        } else
            inStock.setCount(inStock.getCount().add(inCount.getMasterCount()));
        return inStock;
    }

    @Transactional
    @Override
    public String remove(){
                                //TODO implemete can delete type
        if (getInstance().getOperDate().compareTo(AccountDateHelper.instance().getStoreCloseDate(getInstance().getStore().getId())) < 0){
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,"StoreChangeDateIsClose");
            return null;
        }

        boolean accept = false;
        switch (getInstance().getOperType()){
            case MATERIAL_IN:
                break;
            case MATERIAL_BACK_IN:
                break;
            case MATERIAL_OUT:
                break;
            case SELL_OUT:
                break;
            case SELL_BACK:
                break;
            case PRODUCE_IN:
                getEntityManager().remove(getInstance().getProductStoreIn());
                accept = true;
                break;
            case ALLOCATION_IN:
                break;
            case ALLOCATION_OUT:
                break;
            case ASSEMBLY_IN:
                break;
            case ASSEMBLY_OUT:
                break;
            case SCRAP_OUT:
                break;
            case STORE_CHECK_LOSS:
                break;
            case STORE_CHECK_ADD:
                break;
            case STORE_CHANGE_IN:
            case STORE_CHANGE_OUT:
                getEntityManager().remove(getInstance().getStoreChange());
                accept = true;
                break;
        }

        Logging.getLog(getClass()).debug("remove stockChange:" + getInstance().getOperType() + "-" + accept);
        if (accept) {

            for (StockChangeItem item: getInstance().getStockChangeItems()){
                if (getInstance().getOperType().isOut()){
                    item.getStock().setCount(item.getStock().getCount().add(item.getCount()));
                }else{
                    item.getStock().setCount(item.getStock().getCount().subtract(item.getCount()));
                }
            }

            getEntityManager().flush();
            deletedMessage();
            raiseAfterTransactionSuccessEvent();
            return "removed";
        }else
            throw new IllegalArgumentException("operType storeChange can't remove:" + getInstance().getOperType());

    }

}
