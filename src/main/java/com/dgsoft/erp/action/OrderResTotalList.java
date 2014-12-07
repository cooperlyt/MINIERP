package com.dgsoft.erp.action;

import com.dgsoft.common.DataFormat;
import com.dgsoft.common.ExcelExportRender;
import com.dgsoft.common.ExportRender;
import com.dgsoft.common.TotalDataGroup;
import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.CustomerOrder;
import com.dgsoft.erp.model.OrderItem;
import com.dgsoft.erp.model.UnitGroup;
import com.dgsoft.erp.total.ResFormatGroupStrategy;
import com.dgsoft.erp.total.data.OrderItemTotal;
import com.dgsoft.erp.total.data.ResTotalCount;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.Logging;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

/**
 * Created by cooper on 12/2/14.
 */
@Name("orderResTotalList")
public class OrderResTotalList extends ErpEntityQuery<CustomerOrder> {

    private static final String EJBQL = "select customerOrder from CustomerOrder customerOrder left join fetch customerOrder.customer customer left join fetch customer.customerArea customerArea where customerOrder.canceled <> true";

    private static final String[] RESTRICTIONS = {
            "customerOrder.customer.id = #{customerHome.instance.id}",
            "customerOrder.createDate >= #{customerStoresTotalConditions.searchDateArea.dateFrom}",
            "customerOrder.createDate <= #{customerStoresTotalConditions.searchDateArea.searchDateTo}",
            "customerOrder.allStoreOut = #{customerStoresTotalConditions.storeChangeCondition}"};


    public OrderResTotalList() {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
        setMaxResults(20);
    }


    public Number getTotalRebate() {
        return getResultTotalSum("customerOrder.totalRebateMoney");
    }

    public Number getTotalMoney() {
        return getResultTotalSum("customerOrder.money");
    }

    @In(create = true)
    private FacesContext facesContext;

    @In
    private Map<String, String> messages;

    public void export() {

        ExportRender render = new ExcelExportRender(messages.get("customerResContacts"));
        setMaxResults(null);
        render.setNextCellType(ExportRender.Type.HEADER, 0);
        render.cell(0, 0, messages.get("CustomerOrder"));
        render.cell(0, 1, 0, 3, messages.get("StoreRes"));
        render.cell(0, 4, messages.get("orderItemUnitPrice"));
        render.cell(0, 5, 0, 8, messages.get("count"));
        render.cell(0, 9, messages.get("needConvertRate"));
        render.cell(0, 10, messages.get("NeedCount"));
        render.cell(0, 11, messages.get("needItemAdd"));
        render.cell(0, 12, messages.get("rebate"));
        render.cell(0, 13, messages.get("Money"));
        int row = 1;
        for (CustomerOrder order : getResultList()) {
            int beginRow = row;

            render.cell(row, 1, row, 11, order.getId() + "[" + messages.get(order.getPayType().name()) + "]");
            render.cell(row, 12, order.getTotalRebateMoney().doubleValue());
            render.cell(row, 13, order.getMoney().doubleValue());
            row++;
            row = TotalDataGroup.export(row, 1, 1, order.getItemTotalGroup(), new ResTotalCount.CountExportStrategy<OrderItem, OrderItemTotal>() {
                @Override
                public int wirteData(int row, int beginCol, OrderItem value, ExportRender render) {
                    int col = beginCol;
                    if (value.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)){
                        DecimalFormat df = new DecimalFormat(value.getStoreRes().getRes().getUnitGroup().getFloatConvertRateFormat());
                        df.setGroupingUsed(false);
                        df.setRoundingMode(RoundingMode.HALF_UP);
                        render.cell(row,col,df.format(value.getStoreRes().getFloatConversionRate()) + value.getRes().getUnitGroup().getName());

                    }
                    col++;
                    render.cell(row,col++,value.getMoney().doubleValue());
                    col = outCount(row,col,value.getStoreResCount(),render);
                    if (value.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
                        render.cell(row, col++, value.getNeedConvertRate().doubleValue());
                        render.cell(row, col++, value.getNeedCount().doubleValue());
                        render.cell(row, col++, value.getNeedAddCount().doubleValue());
                    }else{
                        col = col + 3;
                    }
                    if (value.getRebate().compareTo(new BigDecimal("100")) != 0){
                        DecimalFormat df = new DecimalFormat("#0.######");
                        df.setGroupingUsed(false);
                        df.setRoundingMode(RoundingMode.HALF_UP);
                        render.cell(row,col,"%" + df.format(value.getRebate()));
                    }
                    col++;
                    render.cell(row,col++,value.getTotalMoney().doubleValue());

                    return row + 1;
                }

                @Override
                public int wirteTotal(int beginRow, int beginCol, OrderItemTotal value, TotalDataGroup.GroupKey<?> key, ExportRender render, int childCount) {

                    int col = beginCol;
                    int row = beginRow ;
                    if (key instanceof OrderItemTotal.OrderItemResKey){
                        OrderItemTotal.OrderItemResKey resKey = (OrderItemTotal.OrderItemResKey) key;
                        if ((resKey.getResSaleRebate() != null) && (resKey.getResSaleRebate().getRebateMoney().compareTo(BigDecimal.ZERO) != 0)){
                            render.cell(row,beginCol + 1,row,col + 2,messages.get("orderSaleRebate"));
                            render.cell(row,7,resKey.getResSaleRebate().getRebateCount().doubleValue());
                            render.cell(row,12,resKey.getResSaleRebate().getRebateMoney().doubleValue());
                            row ++;
                        }
                        if (childCount <= 1){
                            return row;
                        }
                        render.cell(row,beginCol,row,col + 2,messages.get("GroupTotal"));
                        col = col + 3;
                    }else if(key instanceof ResFormatGroupStrategy.StoreResFormatKey){
                        if (childCount <= 1){
                            return row;
                        }
                        render.cell(row,beginCol,row,col + 1,messages.get("GroupTotal"));
                        col ++;
                    }

                    outCount(row,5,value.getResCount(),render);
                    render.cell(row,13,value.getMoney().doubleValue());

                    row++;
                    return row;
                }

                @Override
                public void wirteKey(int row, int col, int toRow, int toCol, TotalDataGroup.GroupKey<?> key, ExportRender render) {
                     if (key instanceof OrderItemTotal.OrderItemResKey){
                         OrderItemTotal.OrderItemResKey resKey = (OrderItemTotal.OrderItemResKey) key;
                         if ((resKey.getResSaleRebate() != null) && (resKey.getResSaleRebate().getRebateMoney().compareTo(BigDecimal.ZERO) != 0)){
                             render.cell(row,col,toRow + 1,toCol,resKey.getRes().getName() + "[" + resKey.getResUnit().getName() + "]");
                         }else
                            render.cell(row,col,toRow,toCol,resKey.getRes().getName() + "[" + resKey.getResUnit().getName() + "]");
                     }else if(key instanceof ResFormatGroupStrategy.StoreResFormatKey){
                          render.cell(row,col,toRow,toCol,((ResFormatGroupStrategy.StoreResFormatKey) key).getFormatTitle());
                     }
                }

                @Override
                public int wirteHeader(ExportRender render) {
                    return 0;
                }
            }, render);
            Calendar calendar = Calendar.getInstance(Locale.CHINA);
            calendar.setTime(order.getCreateDate());
            render.cell(beginRow, 0, row - 1, 0, calendar);

        }

        render.cell(row,12,getTotalRebate().doubleValue());

        render.cell(row,13,getTotalMoney().doubleValue());
        setMaxResults(25);

        ExternalContext externalContext = facesContext.getExternalContext();
        externalContext.responseReset();
        externalContext.setResponseContentType("application/vnd.ms-excel");
        externalContext.setResponseHeader("Content-Disposition", "attachment;filename=export.xls");
        try {
            render.write(externalContext.getResponseOutputStream());
            facesContext.responseComplete();
        } catch (IOException e) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,"ExportIOError");
            Logging.getLog(getClass()).error("export error", e);
        }

    }

    @In(create = true)
    private FacesMessages facesMessages;

}
