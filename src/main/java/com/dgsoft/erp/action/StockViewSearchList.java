package com.dgsoft.erp.action;

import com.dgsoft.common.ExcelExportRender;
import com.dgsoft.common.ExportRender;
import com.dgsoft.common.TotalDataGroup;
import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.SaleCount;
import com.dgsoft.erp.model.api.StockView;
import com.dgsoft.erp.total.ResFormatGroupStrategy;
import com.dgsoft.erp.total.data.ResTotalCount;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.Logging;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by cooper on 11/28/14.
 */
@Name("stockViewSearchList")
@Scope(ScopeType.CONVERSATION)
public class StockViewSearchList extends ErpEntityQuery<SaleCount> {

    private static final String EJBQL = "select new com.dgsoft.erp.model.api.SaleCount(orderItem.dispatch.store.id,orderItem.storeRes.id,sum(orderItem.count)) from OrderItem orderItem where orderItem.status = 'DISPATCHED'";

    private static final String[] RESTRICTIONS = {
            "orderItem.dispatch.store.id in (#{stockSearchList.storeIds})",
            "orderItem.storeRes.code = #{storeResCondition.storeResCode}",
            "orderItem.storeRes.res.resCategory.id in (#{storeResCondition.searchResCategoryIds})",
            "orderItem.storeRes.res.id = #{storeResCondition.searchResId}",
            "orderItem.storeRes.floatConversionRate = #{storeResCondition.searchFloatConvertRate}",
            "orderItem.storeRes.id in (#{storeResCondition.matchStoreResIds})"};

    public StockViewSearchList() {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
        setGroupBy(" orderItem.dispatch.store.id, orderItem.storeRes.id ");
    }

    @In(create = true)
    private StockSearchList stockSearchList;

    @In(create = true)
    private FacesContext facesContext;

    @In(create = true)
    private FacesMessages facesMessages;

    @In
    private Map<String, String> messages;

    public void export(){
        ExternalContext externalContext = facesContext.getExternalContext();
        externalContext.responseReset();
        externalContext.setResponseContentType("application/vnd.ms-excel");
        externalContext.setResponseHeader("Content-Disposition", "attachment;filename=export.xls");

        try {
            TotalDataGroup.export(getResultGroup(), new ResTotalCount.CountExportStrategy<StockView, StockView.StockTotalCount>() {
                @Override
                public int wirteData(int row, int beginCol, StockView value, ExportRender render) {
                    int col = beginCol;
                    render.cell(row,col++,value.getStock().getStore().getName());
                    col = outCount(row,col,value.getStockCount(),render);
                    col = outCount(row,col,value.getSaleCount(),render);
                    col = outCount(row,col,value.getCanUseCount(),render);
                    return row + 1;
                }

                @Override
                public int wirteTotal(int row, int beginCol, StockView.StockTotalCount value, TotalDataGroup.GroupKey<?> key, ExportRender render, int childCount) {
                    if (childCount <= 1){
                        return row;
                    }
                    int col = beginCol;
                    if (key instanceof Res){
                        col = col + 3;
                        render.cell(row,beginCol,row,col,messages.get("Total"));
                        col ++;
                    }else if (key instanceof ResFormatGroupStrategy.StoreResFormatKey){
                        if (!((ResFormatGroupStrategy.StoreResFormatKey) key).getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)){
                           return row;
                        }
                        col = col + 2;
                        render.cell(row,beginCol,row,col,messages.get("GroupTotal"));
                        col++;

                    }else if (key instanceof StoreRes){
                        if (stockSearchList.getStoreIds().size() == 1){
                            return row;
                        }
                        render.cell(row,beginCol,row,++col,messages.get("GroupTotal"));
                        col++;
                    }else{
                        return row;
                    }

                    col = outCount(row,col,value.getStockCount(),render);
                    col = outCount(row,col,value.getSaleCount(),render);
                    col = outCount(row,col,value.getCanUseCount(),render);


                    return row + 1;
                }

                @Override
                public void wirteKey(int row, int col, int toRow, int toCol, TotalDataGroup.GroupKey<?> key, ExportRender render) {
                    if (key instanceof Res){
                        render.cell(row,col,toRow,toCol,((Res) key).getName());
                    }else if (key instanceof ResFormatGroupStrategy.StoreResFormatKey){
                        render.cell(row,col,toRow,toCol,((ResFormatGroupStrategy.StoreResFormatKey) key).getFormatTitle());
                    }else if (key instanceof StoreRes){


                       if (((StoreRes) key).getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)){
                           DecimalFormat df = new DecimalFormat(((StoreRes) key).getRes().getUnitGroup().getFloatConvertRateFormat());
                           df.setGroupingUsed(false);
                           df.setRoundingMode(RoundingMode.HALF_UP);
                           render.cell(row,col,toRow,toCol,df.format(((StoreRes) key).getFloatConversionRate()) + ((StoreRes) key).getRes().getUnitGroup().getName());
                       }
                    }else{
                        render.cell(row,col,toRow,toCol,"not Define");
                    }
                }

                @Override
                public int wirteHeader(ExportRender render) {
                    render.cell(0,0,0,3,messages.get("StoreRes"));
                    render.cell(0,4,0,7,messages.get("StockCount"));
                    render.cell(0,8,0,11,messages.get("SaleStockCount"));
                    render.cell(0,12,0,15,messages.get("StockCanUseCount"));
                    return 1;
                }
            },new ExcelExportRender(messages.get("Stock")), externalContext.getResponseOutputStream());
            facesContext.responseComplete();
        } catch (IOException e) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,"ExportIOError");
            Logging.getLog(getClass()).error("export error", e);
        }
    }


    public List<TotalDataGroup<Res,StockView,StockView.StockTotalCount>> resultGroup;

    public List<TotalDataGroup<Res,StockView,StockView.StockTotalCount>> getResultGroup(){

        if (resultGroup == null){
            List<StockView> stockViews = new ArrayList<StockView>();
            stockSearchList.setMaxResults(null);
            for(Stock stock: stockSearchList.getResultList()){
                stockViews.add(new StockView(stock,getStockSaleCount(stock)));
            }

            resultGroup =
                    TotalDataGroup.groupBy(stockViews, new StockView.ResCountGroupStrategy<StockView>(),
                            new StockView.FormatCountGroupStrategy(), new StockView.StoreResCountGroupStrategy());

            for(TotalDataGroup<Res,StockView,StockView.StockTotalCount> group: resultGroup){
                TotalDataGroup.sort(group,new Comparator<StockView>() {
                    @Override
                    public int compare(StockView o1, StockView o2) {
                        return o1.getStock().getStoreRes().compareTo(o2.getStock().getStoreRes());
                    }
                });
            }
        }
        return resultGroup;
    }

    @Override
    public void refresh() {
        super.refresh();
        resultGroup = null;
        resultMap = null;
        StockSearchList searchList = (StockSearchList)Component.getInstance("stockSearchList",false,false);
        if(searchList != null){
            searchList.refresh();
        }
    }

    private List<String> getStoreResIds(List<StoreRes> storeReses) {
        List<String> result = new ArrayList<String>(storeReses.size());
        for (StoreRes storeRes : storeReses) {
            if (storeRes.getId() != null)
                result.add(storeRes.getId());
        }
        return result;
    }

    public List<StockView> searchStockViews(Store store, List<StoreRes> storeReses) {
        List<String> ids = getStoreResIds(storeReses);
        if (ids.isEmpty()) {
            return new ArrayList<StockView>(0);
        }
        return getEntityManager().createQuery("select new com.dgsoft.erp.model.api.StockView(stock,(select sum(orderItem.count) from OrderItem orderItem where orderItem.status = 'DISPATCHED' and orderItem.dispatch.store.id = stock.store.id and orderItem.storeRes.id = stock.storeRes.id)) from Stock stock where stock.count != 0 and stock.store.id = :storeId and stock.storeRes.id in (:storeResIds)", StockView.class).
                setParameter("storeId", store.getId()).setParameter("storeResIds", ids).getResultList();
    }

    public StockView searchStockViews(Store store, StoreRes storeRes) {
        List<StoreRes> storeReses = new ArrayList<StoreRes>(1);
        storeReses.add(storeRes);
        List<StockView> result = searchStockViews(store, storeReses);
        if (result.isEmpty()) {
            return null;
        } else
            return result.get(0);
    }

    public List<StockView> searchStockViews(StoreRes storeRes) {
        List<StoreRes> storeReses = new ArrayList<StoreRes>(1);
        storeReses.add(storeRes);
        return searchStockViews(storeReses);
    }

    public List<StockView> searchStockViews(List<StoreRes> storeReses) {

        List<String> ids = getStoreResIds(storeReses);
        if (ids.isEmpty()) {
            return new ArrayList<StockView>(0);
        }
        return getEntityManager().createQuery("select new com.dgsoft.erp.model.api.StockView(stock,(select sum(orderItem.count) from OrderItem orderItem where orderItem.status = 'DISPATCHED' and orderItem.dispatch.store.id = stock.store.id and orderItem.storeRes.id = stock.storeRes.id)) from Stock stock where stock.count != 0 and stock.storeRes.id in (:storeResIds)", StockView.class).
                setParameter("storeResIds", ids).getResultList();
    }

    public BigDecimal getStockSaleCount(Stock stock){
        Map<String,BigDecimal> resMap = getResultMap().get(stock.getStore().getId());
        if (resMap == null){
            return BigDecimal.ZERO;
        }else{
            BigDecimal count = resMap.get(stock.getStoreRes().getId());
            return (count == null) ? BigDecimal.ZERO : count;
        }
    }

    private Map<String,Map<String,BigDecimal>> resultMap;

    public Map<String, Map<String, BigDecimal>> getResultMap() {
        if (resultMap == null){
            List<SaleCount> resultList = getResultList();
            resultMap = new HashMap<String, Map<String, BigDecimal>>();
            for(SaleCount saleCount: resultList){
                Map<String,BigDecimal> storeResCount = resultMap.get(saleCount.getStoreId());
                if (storeResCount == null){
                    storeResCount = new HashMap<String, BigDecimal>();
                    resultMap.put(saleCount.getStoreId(),storeResCount);
                }
                BigDecimal count = storeResCount.get(saleCount.getStoreResId());
                if (count ==  null){
                    storeResCount.put(saleCount.getStoreResId(),saleCount.getCount());
                }else{
                    storeResCount.put(saleCount.getStoreResId(),count.add(saleCount.getCount()));
                }

            }
        }
        return resultMap;
    }





}
