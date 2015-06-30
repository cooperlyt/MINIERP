package com.dgsoft.erp.total;

import com.dgsoft.common.*;
import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.model.StockChange;
import com.dgsoft.erp.model.StockChangeItem;
import com.dgsoft.erp.model.UnitGroup;
import com.dgsoft.erp.total.data.ResCount;
import com.dgsoft.erp.total.data.ResTotalCount;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.Logging;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.*;

/**
 * Created by cooper on 4/28/14.
 */
@Name("stockChangeItemTotal")
public class StockChangeItemTotal extends ErpEntityQuery<StockChangeItem> {

    public enum  ResultGroupType{
        GORUP_BY_DAY, GROUP_BY_CHANGE, NO_GROUP;
    }

    private static final String EJBQL = "select stockChangeItem from StockChangeItem stockChangeItem  " +
            "where stockChangeItem.stockChange.verify = true";

    private static final String[] RESTRICTIONS = {
            "stockChangeItem.stockChange.operDate >= #{searchDateArea.dateFrom}",
            "stockChangeItem.stockChange.operDate <= #{searchDateArea.searchDateTo}",
            "stockChangeItem.stockChange.operType in (#{stockChangeTypeCondition.searchTypes})",
            "stockChangeItem.stockChange.store.id = #{stockChangeTypeCondition.storeId}",
            "stockChangeItem.storeRes.res.resCategory.id in (#{storeResCondition.searchResCategoryIds})",
            "stockChangeItem.storeRes.res.id = #{storeResCondition.searchResId}",
            "stockChangeItem.storeRes.floatConversionRate = #{storeResCondition.searchFloatConvertRate}",
            "stockChangeItem.storeRes.id in (#{storeResCondition.matchStoreResIds})"};


    public StockChangeItemTotal() {
        super();
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
        setOrderColumn("stockChangeItem.stockChange.operDate");
    }

    public EnumSet<ResultGroupType> getAllGroupType(){
        return EnumSet.allOf(ResultGroupType.class);
    }

    private ResultGroupType resultGroupBy = ResultGroupType.NO_GROUP;

    public ResultGroupType getResultGroupBy() {
        return resultGroupBy;
    }

    public void setResultGroupBy(ResultGroupType resultGroupBy) {
        this.resultGroupBy = resultGroupBy;
    }

    public TotalDataGroup<?, StockChangeItem,ResCount> getDayResultGroup() {
        return TotalDataGroup.allGroupBy(getResultList(), new TotalGroupStrategy<TotalDataGroup.DateKey, StockChangeItem,ResCount>() {
            @Override
            public TotalDataGroup.DateKey getKey(StockChangeItem stockChangeItem) {
                return new TotalDataGroup.DateKey(DataFormat.halfTime(stockChangeItem.getStockChange().getOperDate()));
            }

            @Override
            public ResCount totalGroupData(Collection<StockChangeItem> datas) {
                return null;
            }
        }, new ResTotalCount.ResCountGroupStrategy<StockChangeItem>(), new ResTotalCount.FormatCountGroupStrategy<StockChangeItem>());
    }

    public TotalDataGroup<?, StockChangeItem,?> getResultGroup() {
        return TotalDataGroup.allGroupBy(getResultList(),
                new ResTotalCount.ResCountGroupStrategy<StockChangeItem>(), new ResTotalCount.FormatCountGroupStrategy<StockChangeItem>());
    }

    public TotalDataGroup<?, StockChangeItem,ResCount> getChangeResultGroup() {
        return TotalDataGroup.allGroupBy(getResultList(), new TotalGroupStrategy<StockChange, StockChangeItem,ResCount>() {
            @Override
            public StockChange getKey(StockChangeItem stockChangeItem) {
                return stockChangeItem.getStockChange();
            }

            @Override
            public ResCount totalGroupData(Collection<StockChangeItem> datas) {
                return null;
            }
        },new ResTotalCount.ResCountGroupStrategy<StockChangeItem>(), new ResTotalCount.FormatCountGroupStrategy<StockChangeItem>());
    }

    @In(create = true)
    private FacesContext facesContext;

    @In
    private Map<String, String> messages;

    @In(create = true)
    private FacesMessages facesMessages;

    public void export() {
        ExportRender render = new ExcelExportRender("");
        render.setNextCellType(ExportRender.Type.HEADER, 1);
        render.cell(0,0, 1,1, messages.get("StoreRes"));

        render.cell(0,2,0,5, messages.get("count"));
        render.cell(1,2,1,3,messages.get("MasterUnitCount"));
        render.cell(1,4,1,5,messages.get("AuxUnitCount"));
        int row = 2;


        for (TotalDataGroup<?, StockChangeItem,?> g1: getResultGroup().getChildGroup()){
            int beginRow = row;
            render.setNextCellType(ExportRender.Type.DATA, 1);
            for (TotalDataGroup<?, StockChangeItem,?> g2: g1.getChildGroup()){
                render.cell(row,1,((ResFormatGroupStrategy.StoreResFormatKey)g2.getKey()).getFormatTitle());

                render.cell(row,2,((ResCount)g2.getTotalData()).getMasterCount().floatValue());
                render.cell(row,3,((ResCount)g2.getTotalData()).getRes().getUnitGroup().getMasterUnit().getName());
                if (((ResCount)g2.getTotalData()).getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)){
                    render.cell(row,4,((ResCount)g2.getTotalData()).getAuxCount().floatValue());
                    render.cell(row,5,((ResCount)g2.getTotalData()).getRes().getUnitGroup().getFloatAuxiliaryUnit().getName());
                }
                row++;
            }
            render.setNextCellType(ExportRender.Type.FOOTER, 1);
            render.cell(row,1,messages.get("Total"));
            render.cell(row,2,((ResCount)g1.getTotalData()).getMasterCount().floatValue());
            render.cell(row,3,((ResCount)g1.getTotalData()).getRes().getUnitGroup().getMasterUnit().getName());
            if (((ResCount)g1.getTotalData()).getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)){
                render.cell(row,4,((ResCount)g1.getTotalData()).getAuxCount().floatValue());
                render.cell(row,5,((ResCount)g1.getTotalData()).getRes().getUnitGroup().getFloatAuxiliaryUnit().getName());
            }

            render.setNextCellType(ExportRender.Type.HEADER,1);
            render.cell(beginRow,0,row,0,((Res)g1.getKey()).getName());
            row ++;


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

    }


}
