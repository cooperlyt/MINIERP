package com.dgsoft.erp.action;

import com.dgsoft.common.system.DictionaryWord;
import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.model.PriceItem;
import com.dgsoft.erp.model.QuotedPrice;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;

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

    @In
    private DictionaryWord dictionary;

    @In
    private FacesMessages facesMessages;

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
                          priceItem.add(new PriceItem(getInstance(),priceItem.getStoreRes(),priceItem.getCount(),priceItem.getMoney(),priceItem.getResUnit(),priceItem.getMemo()));
                    }
                }
            }

        }
        return "/func/erp/sale/QuotedCreateItem.xhtml";

    }

}
