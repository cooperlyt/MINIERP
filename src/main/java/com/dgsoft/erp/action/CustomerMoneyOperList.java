package com.dgsoft.erp.action;

import com.dgsoft.common.DataFormat;
import com.dgsoft.common.ExcelExportRender;
import com.dgsoft.common.ExportRender;
import com.dgsoft.common.system.DictionaryWord;
import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.AccountOper;
import com.dgsoft.erp.model.MoneySave;
import com.dgsoft.erp.model.api.PayType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.Logging;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by cooper on 5/12/14.
 */
@Name("customerMoneyOperList")
public class CustomerMoneyOperList extends ErpEntityQuery<AccountOper> {

    private static final String EJBQL = "select accountOper from AccountOper accountOper " +
            "left join fetch accountOper.customer left join fetch accountOper.moneySave moneySave " +
            "left join fetch moneySave.bankAccount left join fetch accountOper.saleCertificate  ";

    private static final String[] RESTRICTIONS = {
            "accountOper.operDate >= #{searchDateArea.dateFrom}",
            "accountOper.operDate <= #{searchDateArea.searchDateTo}",
            "accountOper.operType in (#{customerMoneyCondition.searchAccountOperTypes})",

            "accountOper.customer.customerArea.id = #{customerSearchCondition.customerAreaId}",
            "accountOper.customer.customerLevel.priority >= #{customerSearchCondition.levelFrom}",
            "accountOper.customer.customerLevel.priority <= #{customerSearchCondition.levelTo}",
            "lower(accountOper.customer.name) like lower(concat(#{customerSearchCondition.name},'%'))",
            "accountOper.customer.type = #{customerSearchCondition.type}",
            "accountOper.customer.provinceCode = #{customerSearchCondition.provinceCode}"};


    public CustomerMoneyOperList() {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
        setMaxResults(25);
        setOrderColumn("accountOper.createDate");
    }

    @In
    private Map<String, String> messages;

    @In
    private DictionaryWord dictionary;

    @In(create = true)
    private FacesContext facesContext;

    @In(create = true)
    private FacesMessages facesMessages;

    public void export(){
        ExportRender render = new ExcelExportRender("");
        render.cell(0,0, messages.get("customer_field_name"));
        render.cell(0,1,messages.get("accountOper_field_operDate"));
        render.cell(0,2,messages.get("accountOper_field_operType"));

        render.cell(0,3,messages.get("customer_field_ac"));
        render.cell(0,4,messages.get("customer_field_pac"));
        render.cell(0,5,messages.get("orderFee_field_payType"));
        render.cell(0,6,messages.get("orderFee_field_checkNumber"));
        render.cell(0,7,messages.get("customerPayMoney"));
        render.cell(0,8,messages.get("accountOper_field_fee"));
        render.cell(0,9,messages.get("BankAccount_finel_Bank"));
        render.cell(0,10,messages.get("orderFee_field_bankNumber"));

        render.cell(0,11,messages.get("field_memo"));

        setMaxResults(null);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        int row = 1;
        for(AccountOper oper: getResultList()){

            render.cell(row,0,oper.getCustomer().getName());


            render.cell(row, 1, df.format(oper.getOperDate()) );
            render.cell(row,2,messages.get(oper.getOperType().name()));
            render.cell(row,3,oper.getAccountsReceivable().doubleValue());
            render.cell(row,4,oper.getProxcAccountsReceiveable().doubleValue());
            if (oper.getMoneySave() != null){
                if (PayType.CASH.equals(oper.getMoneySave().getPayType())){

                    render.cell(row,5,oper.getMoneySave().isUseCheck() ? messages.get("CASH_CHECK") : messages.get("CASH"));

                }else{
                    render.cell(row,5,oper.getMoneySave().isUseCheck() ? messages.get("BANK_CHECK") : messages.get("BANK_TRANSFER"));

                }
                if (oper.getMoneySave().isUseCheck())
                    render.cell(row,6,oper.getMoneySave().getCheckNumber());

                render.cell(row,7,oper.getMoneySave().getMoney().doubleValue());
                render.cell(row,8,oper.getMoneySave().getRemitFee().doubleValue());

                if (oper.getMoneySave().getBankAccount() != null) {
                    render.cell(row, 9, dictionary.getWordValue(oper.getMoneySave().getBankAccount().getBank()));
                    render.cell(row, 10, oper.getMoneySave().getBankAccount().getNumber());
                }



            }
            render.cell(row,11,oper.getDescription());



            row++;
        }

        ExternalContext externalContext = facesContext.getExternalContext();
        externalContext.responseReset();
        externalContext.setResponseContentType("application/vnd.ms-excel");
        externalContext.setResponseHeader("Content-Disposition", "attachment;filename=export.xls");
        try {
            render.write(externalContext.getResponseOutputStream());
            facesContext.responseComplete();
        } catch (IOException e) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "ExportIOError");
            Logging.getLog(getClass()).error("export error", e);
        }

        setMaxResults(25);

    }

}
