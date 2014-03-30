package com.dgsoft.erp.action;

import com.dgsoft.common.TotalDataGroup;
import com.dgsoft.common.system.DictionaryWord;
import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.action.store.StoreResCountInupt;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.total.StoreResGroupStrategy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.security.Credentials;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 26/03/14
 * Time: 15:05
 */
@Name("quotedPriceHome")
public class QuotedPriceHome extends ErpEntityHome<QuotedPrice> {

    @In(create = true)
    private CustomerHome customerHome;

    @In(create = true)
    private CustomerAreaHome customerAreaHome;

    @In
    private DictionaryWord dictionary;

    @In(create = true)
    private ResHelper resHelper;

    @In
    private FacesMessages facesMessages;

    @In(create = true)
    private StoreResHome storeResHome;

    private PriceItem editingItem;

    @DataModelSelection
    private PriceItem selectedItem;

    @In
    private Credentials credentials;

    @DataModel(value = "quotedPriceItems")
    private List<PriceItem> priceItems;



    public String genPriceItem() {

        if (priceItems == null) {
            priceItems = new ArrayList<PriceItem>();
            if (customerHome.isIdDefined()) {
                List<QuotedPrice> history = getEntityManager().createQuery("select quotedPrice from QuotedPrice quotedPrice where quotedPrice.customer.id = :customerId order by quotedPrice.createDate", QuotedPrice.class)
                        .setParameter("customerId", customerHome.getId()).setFirstResult(0).setMaxResults(1).getResultList();
                if (!history.isEmpty()) {
                    QuotedPrice historyQuoted = history.get(0);
                    facesMessages.addFromResourceBundle(StatusMessage.Severity.INFO, "customerQuotedFind", historyQuoted.getCreateDate(), dictionary.getWordValue(historyQuoted.getCreateEmp()));
                    for (PriceItem priceItem : historyQuoted.getPriceItems()) {
                        priceItems.add(new PriceItem(getInstance(), priceItem.getStoreRes(), priceItem.getCount(), priceItem.getMoney(), priceItem.getResUnit(), priceItem.getMemo()));
                    }
                }
            }

        }
        return "/func/erp/sale/QuotedCreateItem.xhtml";

    }

    @Observer(value = "erp.resLocateSelected", create = false)
    public void generatePriceItemByRes(Res res) {
        editingItem = new PriceItem(res, resHelper.getFormatHistory(res), resHelper.getFloatConvertRateHistory(res), res.getResUnitByOutDefault(), getInstance());
        log.debug("generateStoreInItemByStoreRes complete");
    }

    @Observer(value = "erp.storeResLocateSelected", create = false)
    public void generatePriceItemByStoreRes(StoreRes storeRes) {
        editingItem = new PriceItem(storeRes, resHelper.getFormatHistory(storeRes.getRes()), resHelper.getFloatConvertRateHistory(storeRes.getRes()), storeRes.getRes().getResUnitByOutDefault(), getInstance());

        log.debug("generateStoreInItemByStoreRes complete");
    }

    public void addPriceItem() {
        storeResHome.setRes(editingItem.getRes(), editingItem.getFormats(), editingItem.getFloatConvertRate());
        if (storeResHome.isIdDefined()) {
            for (PriceItem priceItem : priceItems) {
                if (priceItem.getStoreRes().equals(storeResHome.getInstance())) {
                    facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "storeResExistsInList");
                    return;
                }
            }

            editingItem.setStoreRes(storeResHome.getInstance());
            priceItems.add(editingItem);
            editingItem = null;

        } else {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "orderStoreResNotExists");
            return;
        }

    }

    public void cancelAdd() {
        editingItem = null;
    }

    @Override
    public boolean wire() {
        if (!isIdDefined()) {
            getInstance().getPriceItems().clear();
            getInstance().getPriceItems().addAll(priceItems);
            if (!customerHome.isIdDefined()){
                customerHome.getInstance().setCustomerArea(customerAreaHome.getReadyInstance());
            }
            getInstance().setCustomer(customerHome.getReadyInstance());

            getInstance().setCreateEmp(credentials.getUsername());
        }

        return true;
    }

    public void removePriceItme() {
        priceItems.remove(selectedItem);
    }

    public PriceItem getEditingItem() {
        return editingItem;
    }

    public void setEditingItem(PriceItem editingItem) {
        this.editingItem = editingItem;
    }


    public void setCustomerId(String id){
        List<QuotedPrice> history = getEntityManager().createQuery("select quotedPrice from QuotedPrice quotedPrice where quotedPrice.customer.id = :customerId order by quotedPrice.createDate", QuotedPrice.class)
                .setParameter("customerId", id).setFirstResult(0).setMaxResults(1).getResultList();
        if (history.isEmpty()){
            clearInstance();
        }else{
            setId(history.get(0).getId());
        }
    }
    public String getCustomerId(){
        if(isIdDefined()){
            return getInstance().getCustomer().getId();
        } else {
            return null;
        }
    }

    public TotalDataGroup<?, PriceItem> getPriceItemGroup(){
       return TotalDataGroup.allGroupBy(getInstance().getPriceItems(),new StoreResGroupStrategy<PriceItem>());
    }
}
