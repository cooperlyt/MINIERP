package com.dgsoft.erp.total;

import com.dgsoft.common.*;
import com.dgsoft.erp.action.ResHelper;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.StoreResPriceEntity;
import com.dgsoft.erp.total.data.ResPriceTotal;
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
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 15/04/14
 * Time: 14:16
 */
@Name("customerResContactsTotal")
public class CustomerResContactsTotal {

    @In(create = true)
    private BackResContactsTotal backResContactsTotal;

    @In(create = true)
    private OrderResContactsTotal orderResContactsTotal;

    @In(create = true)
    private CustomerResCondition customerResCondition;

    private boolean onlyModel = false;

    private boolean onlyStoreOut = true;

    private boolean groupByDay = true;

    public boolean isGroupByDay() {
        return groupByDay;
    }

    public void setGroupByDay(boolean groupByDay) {
        this.groupByDay = groupByDay;
    }

    public boolean isOnlyStoreOut() {
        return onlyStoreOut;
    }

    public void setOnlyStoreOut(boolean onlyStoreOut) {
        this.onlyStoreOut = onlyStoreOut;
    }

    public boolean isOnlyModel() {
        return onlyModel;
    }

    public void setOnlyModel(boolean onlyModel) {
        this.onlyModel = onlyModel;
    }

    public ResPriceTotal getTotalPrice(){
//        BigDecimal result = BigDecimal.ZERO;
//        for (StoreResPriceEntity item: getResultList()){
//            result = result.add(item.getTotalMoney());
//        }
        return ResPriceTotal.total(getResultList());
    }


    public List<StoreResPriceEntity> getResultList() {
        final List<StoreResPriceEntity> result = new ArrayList<StoreResPriceEntity>();
        if (!onlyModel) {

            if (customerResCondition.isContainStoreOut()) {
                result.addAll(orderResContactsTotal.getResultList());
            }
            if (customerResCondition.isContainResBack()) {
                result.addAll(backResContactsTotal.getResultList());
            }
        }else{
            if (onlyStoreOut){
                customerResCondition.setContainStoreOut(true);
                customerResCondition.setContainResBack(false);
                result.addAll(orderResContactsTotal.getResultList());
            }else{
                customerResCondition.setContainStoreOut(false);
                customerResCondition.setContainResBack(true);
                result.addAll(backResContactsTotal.getResultList());
            }
        }
        Collections.sort(result, new SaleBackItemComparator());

        return result;
    }

    @Deprecated
    public TotalDataGroup<?, StoreResPriceEntity,ResPriceTotal> getCustomerResultGroup() {
        return TotalDataGroup.allGroupBy(getResultList(), new CustomerGroupStrategy(),
                new ResPriceTotal.ResMoneyGroupStrategy<StoreResPriceEntity>(),
                new ResPriceTotal.FormatMoneyGroupStrategy<StoreResPriceEntity>());
    }


    public List<TotalDataGroup<Customer,StoreResPriceEntity,TotalDataGroup.SingleTotalData<BigDecimal>>> getCustomerResultGroups(){
        if(customerResCondition.isContainStoreOut() && customerResCondition.isContainResBack()){

            return TotalDataGroup.groupBy(getResultList(),
                    new TotalGroupStrategy<Customer, StoreResPriceEntity, TotalDataGroup.SingleTotalData<BigDecimal>>() {
                        @Override
                        public Customer getKey(StoreResPriceEntity storeResPriceEntity) {
                            if (storeResPriceEntity instanceof OrderItem) {
                                return ((OrderItem) storeResPriceEntity).getNeedRes().getCustomerOrder().getCustomer();
                            } else if (storeResPriceEntity instanceof BackItem) {
                                return ((BackItem) storeResPriceEntity).getOrderBack().getCustomer();
                            } else
                                return null;
                        }

                        @Override
                        public TotalDataGroup.SingleTotalData totalGroupData(Collection<StoreResPriceEntity> datas) {
                            return null;
                        }
                    }
                    ,

                    new TotalGroupStrategy<TotalDataGroup.StringKey, StoreResPriceEntity, TotalDataGroup.SingleTotalData<BigDecimal>>() {
                        @Override
                        public TotalDataGroup.StringKey getKey(StoreResPriceEntity storeResPriceEntity) {
                            if ((storeResPriceEntity instanceof OrderItem) &&
                                    ((OrderItem)storeResPriceEntity).isFree()) {
                                return new TotalDataGroup.StringKey("free");

                            }
                            return new TotalDataGroup.StringKey(storeResPriceEntity.getType()) ;

                        }

                        @Override
                        public TotalDataGroup.SingleTotalData<BigDecimal> totalGroupData(Collection<StoreResPriceEntity> datas) {
                            return new TotalDataGroup.SingleTotalData<BigDecimal>(totalMoney(datas)) ;
                        }
                    }
                    );
        }else{
            return TotalDataGroup.groupBy(getResultList(),
                    new TotalGroupStrategy<Customer, StoreResPriceEntity, TotalDataGroup.SingleTotalData<BigDecimal>>() {
                        @Override
                        public Customer getKey(StoreResPriceEntity storeResPriceEntity) {
                            if (storeResPriceEntity instanceof OrderItem) {
                                return ((OrderItem) storeResPriceEntity).getNeedRes().getCustomerOrder().getCustomer();
                            } else if (storeResPriceEntity instanceof BackItem) {
                                return ((BackItem) storeResPriceEntity).getOrderBack().getCustomer();
                            } else
                                return null;
                        }

                        @Override
                        public TotalDataGroup.SingleTotalData<BigDecimal> totalGroupData(Collection<StoreResPriceEntity> datas) {
                            return new TotalDataGroup.SingleTotalData<BigDecimal>(totalMoney(datas)) ;
                        }
                    }

            );
        }

    }

    @In(create = true)
    private FacesContext facesContext;


    @In
    private Map<String, String> messages;

    public void export() {
        ExternalContext externalContext = facesContext.getExternalContext();
        externalContext.responseReset();
        externalContext.setResponseContentType("application/vnd.ms-excel");
        externalContext.setResponseHeader("Content-Disposition", "attachment;filename=export.xls");

        try {
            TotalDataGroup.export(getCustomerResultGroups(), new ResTotalCount.CountExportStrategy<StoreResPriceEntity,TotalDataGroup.SingleTotalData<BigDecimal>>() {


                @Override
                public int wirteData(int row, int beginCol, StoreResPriceEntity value, ExportRender render) {
                    int col = beginCol;

                    DateFormat df = new SimpleDateFormat(messages.get("displayDatePattern"));

                    if (value instanceof OrderItem){
                        render.cell(row,col++,df.format(((OrderItem) value).getNeedRes().getCustomerOrder().getCreateDate()));
                    }else{
                        render.cell(row,col++,df.format(((BackItem) value).getDispatch().getStockChange().getOperDate()));
                    }


                    render.cell(row,col++, ResHelper.instance().generateStoreResTitle(value.getStoreRes(),true));

                    col = outCount(row, col, value.getStoreResCount(), render);

                    render.cell(row,col++,value.getMoney().doubleValue());

                    render.cell(row,col++,value.getUseUnit().getName());

                    render.cell(row,col++,value.getRebate().doubleValue());

                    render.cell(row,col++,value.getTotalMoney().doubleValue());

                    if(value.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT) && (value instanceof OrderItem)){
                        render.cell(row,col++,((OrderItem) value).getNeedConvertRate().doubleValue());
                        render.cell(row,col++ ,((OrderItem) value).getNeedAddCount().doubleValue());

                    }else{
                        col = col + 2;
                    }

                    render.cell(row,col,value.getMemo());



                    return row + 1;
                }

                @Override
                public int wirteTotal(int row, int beginCol, TotalDataGroup.SingleTotalData<BigDecimal> value,
                                      TotalDataGroup.GroupKey<?> key, ExportRender render, int childCount) {

                    return row ;
                }

                @Override
                public void wirteKey(int row, int col, int toRow, int toCol, TotalDataGroup.GroupKey<?> key, ExportRender render) {
                    String title;

                    if (key instanceof Customer){
                        title = ((Customer) key).getName();
                    }else if (key instanceof TotalDataGroup.StringKey){
                        title = messages.get(((TotalDataGroup.StringKey) key).getKeyData());
                    }else{
                        title = "Not Difine";
                    }
                    render.cell(row, col, toRow, toCol, title);
                }

                @Override
                public int wirteHeader(ExportRender render) {

                    render.cell(0,0,0,2,messages.get("SaleTime"));
                    render.cell(0,3,messages.get("StoreRes"));
                    render.cell(0,4,0,7,messages.get("count"));
                    render.cell(0,8,0,9,messages.get("orderItemUnitPrice"));
                    render.cell(0,10,messages.get("rebate"));

                    render.cell(0,11,messages.get("orderItemPrice"));

                    render.cell(0,12,messages.get("needConvertRate"));
                    render.cell(0,13,messages.get("needItemAdd"));
                    render.cell(0,14,messages.get("field_memo"));
                    return 1;
                }

            }, new ExcelExportRender(messages.get("Total")), externalContext.getResponseOutputStream());
            facesContext.responseComplete();
        } catch (IOException e) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,"ExportIOError");
            Logging.getLog(getClass()).error("export error", e);
        }
    }

    @In(create = true)
    private FacesMessages facesMessages;

    private BigDecimal totalMoney(Collection<StoreResPriceEntity> datas){
        BigDecimal result = BigDecimal.ZERO;
        for (StoreResPriceEntity item: datas){
            if (! item.isFree())
            result = result.add(item.getTotalMoney());
        }
        return result;
    }

    public TotalDataGroup<?, StoreResPriceEntity,ResPriceTotal> getDayResultGroup() {
        return TotalDataGroup.allGroupBy(getResultList(), new TotalGroupStrategy<TotalDataGroup.DateKey, StoreResPriceEntity,ResPriceTotal>() {
            @Override
            public TotalDataGroup.DateKey getKey(StoreResPriceEntity storeResPriceEntity) {
                if (storeResPriceEntity instanceof OrderItem) {
                    return new TotalDataGroup.DateKey(DataFormat.halfTime(((OrderItem) storeResPriceEntity).getDispatch().getStockChange().getOperDate()));
                } else if (storeResPriceEntity instanceof BackItem) {
                    return new TotalDataGroup.DateKey(DataFormat.halfTime(((BackItem) storeResPriceEntity).getDispatch().getStockChange().getOperDate()));
                } else
                    return null;
            }

            @Override
            public ResPriceTotal totalGroupData(Collection<StoreResPriceEntity> datas) {
                return ResPriceTotal.total(datas);
            }
        }, new CustomerGroupStrategy(), new ResPriceTotal.ResMoneyGroupStrategy<StoreResPriceEntity>(), new ResPriceTotal.FormatMoneyGroupStrategy<StoreResPriceEntity>());
    }


    private static class CustomerGroupStrategy implements TotalGroupStrategy<Customer, StoreResPriceEntity,ResPriceTotal> {
        @Override
        public Customer getKey(StoreResPriceEntity storeResPriceEntity) {
            if (storeResPriceEntity instanceof OrderItem) {
                return ((OrderItem) storeResPriceEntity).getNeedRes().getCustomerOrder().getCustomer();
            } else if (storeResPriceEntity instanceof BackItem) {
                return ((BackItem) storeResPriceEntity).getOrderBack().getCustomer();
            } else
                return null;
        }

        @Override
        public ResPriceTotal totalGroupData(Collection<StoreResPriceEntity> datas) {
            return ResPriceTotal.total(datas);
        }
    }



}
