package com.dgsoft.erp.total;

import com.dgsoft.common.*;
import com.dgsoft.erp.ErpEntityLoader;
import com.dgsoft.erp.action.ResHelper;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.total.data.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.Logging;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.persistence.TypedQuery;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

/**
 * Created by cooper on 12/19/14.
 */
@Name("resSaleTotal")
public class ResSaleTotal {

    @In(create = true)
    private ErpEntityLoader erpEntityLoader;

    @In(create = true)
    private SearchDateArea searchDateArea;

    private boolean conationPriceOrder = false;

    public boolean isConationPriceOrder() {
        return conationPriceOrder;
    }

    public void setConationPriceOrder(boolean conationPriceOrder) {
        this.conationPriceOrder = conationPriceOrder;
    }

    private Map<Res, ResSaleTotalResult> resultData;

    private static final String SALE_DATA_SQL_NO_PRICE  = "select new com.dgsoft.erp.total.data.StoreResSaleTotalData(oi.storeRes,sum(oi.count),avg(oi.money),sum(oi.totalMoney),sum(oi.needCount),sum(CASE co.payType WHEN 'PRICE_CHANGE' THEN oi.totalMoney ELSE  (oi.money * oi.saleCount) END)) " +
            " from OrderItem oi left join oi.needRes nr left join nr.customerOrder co where oi.needRes.customerOrder.canceled <> true and oi.needRes.customerOrder.createDate >= :beginDate and oi.needRes.customerOrder.createDate <= :endDate group by oi.storeRes";

    private static final String SALE_DATA_SQL= "select new com.dgsoft.erp.total.data.StoreResSaleTotalData(oi.storeRes,sum(oi.count),avg(oi.money),sum(oi.totalMoney),sum(oi.needCount),sum(oi.money * oi.saleCount)) " +
            " from OrderItem oi where oi.needRes.customerOrder.payType <> 'PRICE_CHANGE' and oi.needRes.customerOrder.canceled <> true and oi.needRes.customerOrder.createDate >= :beginDate and oi.needRes.customerOrder.createDate <= :endDate group by oi.storeRes";


    private static final String REBATE_DATA_SQL = "select new com.dgsoft.erp.total.data.ResRebateTotalData(rsr.res,sum(rsr.rebateCount),sum(rsr.rebateMoney)) " +
            " from ResSaleRebate rsr where rsr.customerOrder.canceled <> true and rsr.customerOrder.createDate >= :beginDate and rsr.customerOrder.createDate <= :endDate  group by rsr.res";

    private static final String BACK_DATA_SQL = "select new com.dgsoft.erp.total.data.StoreResBackTotalData(bi.storeRes,bi.count,bi.totalMoney) " +
            " from BackItem bi where bi.orderBack.confirmed = true and bi.orderBack.completeDate >= :beginDate and bi.orderBack.completeDate <= :endDate group by bi.storeRes";


    public static final String ORDER_REDUCE_SQL ="select sum(orderReduce.money) from OrderReduce orderReduce where orderReduce.customerOrder.canceled <> true and orderReduce.customerOrder.createDate >= :beginDate and orderReduce.customerOrder.createDate <= :endDate";

    public static final String ORDER_REDUCE_SQL_NO_PRICE="select sum(orderReduce.money) from OrderReduce orderReduce where orderReduce.customerOrder.payType <> 'PRICE_CHANGE' and orderReduce.customerOrder.canceled <> true and orderReduce.customerOrder.createDate >= :beginDate and orderReduce.customerOrder.createDate <= :endDate";

    private void total() {
        resultData = new HashMap<Res, ResSaleTotalResult>();

        String saleSQL;
        if (conationPriceOrder) {
            saleSQL = SALE_DATA_SQL;
        } else {
            saleSQL = SALE_DATA_SQL_NO_PRICE;
        }

        List<StoreResSaleTotalData> saleTotal = erpEntityLoader.getEntityManager().createQuery(saleSQL, StoreResSaleTotalData.class)
                .setParameter("beginDate", searchDateArea.getDateFrom())
                .setParameter("endDate", searchDateArea.getSearchDateTo()).getResultList();

        List<ResRebateTotalData> rebateTotal = erpEntityLoader.getEntityManager().createQuery(REBATE_DATA_SQL, ResRebateTotalData.class)
                .setParameter("beginDate", searchDateArea.getDateFrom())
                .setParameter("endDate", searchDateArea.getSearchDateTo()).getResultList();

        List<StoreResBackTotalData> backTotal = erpEntityLoader.getEntityManager().createQuery(BACK_DATA_SQL, StoreResBackTotalData.class)
                .setParameter("beginDate", searchDateArea.getDateFrom())
                .setParameter("endDate", searchDateArea.getSearchDateTo()).getResultList();

        Logging.getLog(getClass()).debug(searchDateArea.getDateFrom().getYear() + ">" + searchDateArea.getSearchDateTo().getYear());

        for (ResRebateTotalData rebate : rebateTotal) {
            resultData.put(rebate.getRes(), new ResSaleTotalResult(rebate));
        }

        for (StoreResSaleTotalData sale : saleTotal) {
            ResSaleTotalResult total = resultData.get(sale.getStoreRes().getRes());
            if (total == null) {
                total = new ResSaleTotalResult(sale.getStoreRes().getRes());
                resultData.put(total.getRes(), total);
            } else {
                total.put(sale);
            }
        }

        for (StoreResBackTotalData back : backTotal) {
            ResSaleTotalResult total = resultData.get(back.getStoreRes().getRes());
            if (total == null) {
                total = new ResSaleTotalResult(back.getStoreRes().getRes());
                resultData.put(total.getRes(), total);
            } else {
                total.put(back);
            }
        }
    }

    protected Map<Res, ResSaleTotalResult> getResultMap() {
        if (resultData == null) {
            total();
        }
        return resultData;
    }

    public List<ResSaleTotalResult> getResultList() {
        return new ArrayList<ResSaleTotalResult>(getResultMap().values());
    }

    public void refresh() {
        resultData = null;
    }


    @In(create = true)
    private FacesContext facesContext;

    @In
    private Map<String, String> messages;

    private void exportSaleAndBackRow(ExportRender render, int beginCol, int row, SaleAndBack sb) {
        int col = beginCol;


        if (sb.getSale() != null) {
            render.cell(row, col++, sb.getSale().getResCount().getMasterCount().doubleValue());
            render.cell(row, col++, sb.getRes().getUnitGroup().getMasterUnit().getName());
            if (sb.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
                render.cell(row, col++, sb.getSale().getResCount().getAuxCount().doubleValue());
                render.cell(row, col++, sb.getRes().getUnitGroup().getFloatAuxiliaryUnit().getName());
                render.cell(row, col++, sb.getSale().getNeedCount().doubleValue());

            } else {
                col = col + 3;
            }
            render.cell(row, col++, sb.getSale().getMoney().doubleValue());

            if (sb.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
                render.cell(row, col, sb.getSale().getNeedMoney().doubleValue());
            }
            col++;
            if (sb.getCalcSaleAvgMoney() != null){
                render.cell(row,col,sb.getCalcSaleAvgMoney().doubleValue());
            }
            col++;
        } else {
            col = col + 8;
        }
        if (sb.getBack() != null) {

            render.cell(row, col++, sb.getBack().getResCount().getMasterCount().doubleValue());
            render.cell(row, col++, sb.getRes().getUnitGroup().getMasterUnit().getName());

            if (sb.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
                render.cell(row, col++, sb.getBack().getResCount().getAuxCount().doubleValue());
                render.cell(row, col++, sb.getRes().getUnitGroup().getFloatAuxiliaryUnit().getName());

            } else {
                col = col + 2;
            }


            render.cell(row, col, sb.getBack().getMoney().doubleValue());
        }
    }

    public void export() {

        BigDecimal totalSaleMoney = BigDecimal.ZERO;
        BigDecimal totalBackMoney = BigDecimal.ZERO;
        BigDecimal totalRebateMoney = BigDecimal.ZERO;
        BigDecimal totalNeedMoney = BigDecimal.ZERO;

        refresh();
        ExportRender render = new ExcelExportRender(messages.get("ResSaleTotal"));

        render.setNextCellType(ExportRender.Type.HEADER, 0);
        render.cell(0, 0, 0, 2, messages.get("StoreRes"));

        render.cell(0, 3, 0, 6, messages.get("SaleCount"));
        render.cell(0, 7, messages.get("NeedCount"));

        render.cell(0, 8, messages.get("RealSaleMoney"));

        render.cell(0, 9, messages.get("NeedSaleMoney"));

        render.cell(0,10,messages.get("AVGSaleMoney"));

        render.cell(0, 11, 0, 14, messages.get("SaleBackCount"));
        render.cell(0, 15, messages.get("SaleBackMoney"));
        render.cell(0, 16, messages.get("SaleRebateCount"));
        render.cell(0, 17, messages.get("SaleRebateMoney"));
        int row = 1;
        for (ResSaleTotalResult result : getResultList()) {
            int beginRow = row;
            Logging.getLog(getClass()).debug("resyktL:" + result.getSaleAndBacks().size());

            for (TotalDataGroup<ResFormatGroupStrategy.StoreResFormatKey, SaleAndBack, SaleAndBackTotalData> total : result.getSaleAndBacksGroups()) {

                if (total.getKey().getRes().getCode().equals("FR") || total.getKey().getRes().getCode().equals("FD")) {
                    int sbBeginRow = row;
                    for (SaleAndBack sb : total.getValues()) {
                        DecimalFormat df = new DecimalFormat(total.getKey().getRes().getUnitGroup().getFloatConvertRateFormat());
                        df.setGroupingUsed(false);
                        df.setRoundingMode(RoundingMode.HALF_UP);
                        render.cell(row, 2, df.format(sb.getStoreRes().getFloatConversionRate()) + total.getKey().getRes().getUnitGroup().getName());
                        exportSaleAndBackRow(render, 3, row++, sb);
                    }
                    if (sbBeginRow != row) {
                        render.cell(sbBeginRow, 1, row - 1, 1, total.getKey().getFormatTitle());
                    }
                } else {
                    render.cell(row, 1, row, 2, total.getKey().getFormatTitle());


                    render.cell(row, 3, total.getTotalData().getSaleCount().getMasterCount().doubleValue());
                    render.cell(row, 4, total.getKey().getRes().getUnitGroup().getMasterUnit().getName());
                    if (total.getKey().getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
                        render.cell(row, 5, total.getTotalData().getSaleCount().getAuxCount().doubleValue());
                        render.cell(row, 6, total.getKey().getRes().getUnitGroup().getFloatAuxiliaryUnit().getName());
                    }
                        render.cell(row, 7, total.getTotalData().getNeedCount().doubleValue());


                    render.cell(row, 8, total.getTotalData().getSaleMoney().doubleValue());


                        render.cell(row, 9, total.getTotalData().getNeedMoney().doubleValue());


                    if (total.getTotalData().getCalcAvgMoney() != null){
                        render.cell(row,10,total.getTotalData().getCalcAvgMoney().doubleValue());
                    }


                    render.cell(row, 11, total.getTotalData().getBackCount().getMasterCount().doubleValue());
                    render.cell(row, 12, total.getKey().getRes().getUnitGroup().getMasterUnit().getName());

                    if (total.getKey().getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
                        render.cell(row, 13, total.getTotalData().getBackCount().getAuxCount().doubleValue());
                        render.cell(row, 14, total.getKey().getRes().getUnitGroup().getFloatAuxiliaryUnit().getName());

                    }

                    render.cell(row, 15, total.getTotalData().getBackMoney().doubleValue());
                    row++;

                }

            }


            if (!result.getSaleAndBacks().isEmpty())

                render.cell(beginRow, 0, row - 1, 0, result.getRes().getName());

            render.cell(row, 0, row, 2, result.getSaleAndBacks().isEmpty() ? result.getRes().getName() : messages.get("Total"));
            render.cell(row, 3, result.getSaleCount().getMasterCount().doubleValue());
            render.cell(row, 4, result.getRes().getUnitGroup().getMasterUnit().getName());
            if (result.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
                render.cell(row, 5, result.getSaleCount().getAuxCount().doubleValue());
                render.cell(row, 6, result.getRes().getUnitGroup().getFloatAuxiliaryUnit().getName());
            }
                render.cell(row, 7, result.getNeedCount().doubleValue());


            render.cell(row, 8, result.getSaleMoney().doubleValue());

            totalSaleMoney = totalSaleMoney.add(result.getSaleMoney());
            totalNeedMoney = totalNeedMoney.add(result.getNeedMoney());

            render.cell(row, 9, result.getNeedMoney().doubleValue());



            render.cell(row, 11, result.getBackCount().getMasterCount().doubleValue());
            render.cell(row, 12, result.getRes().getUnitGroup().getMasterUnit().getName());
            if (result.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
                render.cell(row, 13, result.getBackCount().getAuxCount().doubleValue());
                render.cell(row, 14, result.getRes().getUnitGroup().getFloatAuxiliaryUnit().getName());

            }

            render.cell(row, 15, result.getBackMoney().doubleValue());


            totalBackMoney = totalBackMoney.add(result.getBackMoney());


            if (result.getRebate() != null) {
                render.cell(row, 16, result.getRebate().getCount().doubleValue());
                render.cell(row, 17, result.getRebate().getMoney().doubleValue());
                totalRebateMoney = totalRebateMoney.add(result.getRebate().getMoney());
            }

            row++;

        }


        render.cell(row , 0, row , 2, messages.get("StoteResSaleTotal"));
        render.cell(row , 8, totalSaleMoney.doubleValue());
        render.cell(row , 9, totalNeedMoney.doubleValue());
        render.cell(row , 15, totalBackMoney.doubleValue());
        render.cell(row , 17, totalRebateMoney.doubleValue());

        TypedQuery<BigDecimal> query;

        if (!conationPriceOrder){
            query = erpEntityLoader.getEntityManager().createQuery(ORDER_REDUCE_SQL, BigDecimal.class);

        }else{
            query = erpEntityLoader.getEntityManager().createQuery(ORDER_REDUCE_SQL_NO_PRICE, BigDecimal.class);
        }

        BigDecimal orderReduce = query.setParameter("beginDate", searchDateArea.getDateFrom())
                .setParameter("endDate", searchDateArea.getSearchDateTo()).getSingleResult();

        if (orderReduce == null) {orderReduce = BigDecimal.ZERO;}

        row++;

        render.cell(row,0,row ,2, messages.get("OrderChangePriceMoney"));

        render.cell(row, 8, orderReduce.doubleValue());
        //render.cell(row, 9, orderReduce.doubleValue());




        row ++;
        render.cell(row, 0, row, 2, searchDateArea.getDisplay());

        NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.CHINA);
        currency.setGroupingUsed(true);
        currency.setMinimumFractionDigits(2);
        currency.setMaximumFractionDigits(2);

        render.cell(row, 3, row, 17, currency.format(totalSaleMoney) + "(货品总价) - " +
                currency.format(totalRebateMoney) + "(返点金额) - " +
                currency.format(orderReduce) + "(调价减免) = " +
                currency.format(totalSaleMoney.subtract(totalRebateMoney).subtract(orderReduce)) + "(订单金额) - " +
                        currency.format(totalBackMoney) + "(退货金额) = " +
                        currency.format(totalSaleMoney.subtract(totalRebateMoney).subtract(orderReduce).subtract(totalBackMoney)));

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

    @In(create = true)
    private FacesMessages facesMessages;


    public static class ResSaleTotalResult {

        private Res res;

        private ResRebateTotalData rebate;

        private Map<StoreRes, SaleAndBack> saleAndBacks = new HashMap<StoreRes, SaleAndBack>();

        public ResSaleTotalResult(ResRebateTotalData rebate) {
            this.rebate = rebate;
            this.res = rebate.getRes();
        }

        public ResSaleTotalResult(Res res) {
            this.res = res;
        }

        public Res getRes() {
            return res;
        }

        public void put(StoreResSaleTotalData data) {
            SaleAndBack saleAndBack = saleAndBacks.get(data.getStoreRes());
            if (saleAndBack == null) {
                saleAndBack = new SaleAndBack(data);
                saleAndBacks.put(data.getStoreRes(), saleAndBack);
            } else {
                saleAndBack.setSale(data);
            }
        }

        public void put(StoreResBackTotalData data) {
            SaleAndBack saleAndBack = saleAndBacks.get(data.getStoreRes());
            if (saleAndBack == null) {
                saleAndBack = new SaleAndBack(data);
                saleAndBacks.put(data.getStoreRes(), saleAndBack);
            } else {
                saleAndBack.setBack(data);
            }
        }

        public ResRebateTotalData getRebate() {
            return rebate;
        }

        public List<SaleAndBack> getSaleAndBacks() {

            List<SaleAndBack> result = new ArrayList<SaleAndBack>(saleAndBacks.values());
            Collections.sort(result, new Comparator<SaleAndBack>() {
                @Override
                public int compare(SaleAndBack o1, SaleAndBack o2) {
                    return o1.getStoreRes().compareTo(o2.getStoreRes());
                }
            });
            return result;
        }

        public List<TotalDataGroup<ResFormatGroupStrategy.StoreResFormatKey, SaleAndBack, SaleAndBackTotalData>> getSaleAndBacksGroups() {
            return TotalDataGroup.groupBy(getSaleAndBacks(), new TotalGroupStrategy<ResFormatGroupStrategy.StoreResFormatKey, SaleAndBack, SaleAndBackTotalData>() {
                @Override
                public ResFormatGroupStrategy.StoreResFormatKey getKey(SaleAndBack saleAndBack) {
                    return new ResFormatGroupStrategy.StoreResFormatKey(saleAndBack.getStoreRes());
                }

                @Override
                public SaleAndBackTotalData totalGroupData(Collection<SaleAndBack> datas) {
                    SaleAndBackTotalData result = new SaleAndBackTotalData();
                    for (SaleAndBack item : datas) {
                        result.put(item);
                    }
                    return result;
                }
            });
        }

        private ResCount saleCount;

        private ResCount backCount;

        private BigDecimal saleMoney;

        private BigDecimal backMoney;

        private BigDecimal needCount;

        private BigDecimal addCount;

        private BigDecimal needMoney;

        public void totalCount() {
            saleCount = ResTotalCount.ZERO(res);
            backCount = ResTotalCount.ZERO(res);
            saleMoney = BigDecimal.ZERO;
            backMoney = BigDecimal.ZERO;
            needCount = BigDecimal.ZERO;
            addCount = BigDecimal.ZERO;
            needMoney = BigDecimal.ZERO;
            for (SaleAndBack saleAndBack : saleAndBacks.values()) {
                if (saleAndBack.getBack() != null) {
                    backCount = backCount.add(saleAndBack.getBack().getResCount());
                    backMoney = backMoney.add(saleAndBack.getBack().getMoney());

                }
                if (saleAndBack.getSale() != null) {
                    saleCount = saleCount.add(saleAndBack.getSale().getResCount());
                    saleMoney = saleMoney.add(saleAndBack.getSale().getMoney());

                        needCount = needCount.add(saleAndBack.getSale().getNeedCount());
                        addCount = addCount.add(saleAndBack.getSale().getAddCount());
                        needMoney = needMoney.add(saleAndBack.getSale().getNeedMoney());

                }
            }
        }

        public BigDecimal getNeedMoney() {
            if (needMoney == null)
                totalCount();
            return needMoney;
        }

        public BigDecimal getSaleMoney() {
            if (saleMoney == null)
                totalCount();
            return saleMoney;
        }

        public BigDecimal getBackMoney() {
            if (backMoney == null)
                totalCount();
            return backMoney;
        }

        public BigDecimal getNeedCount() {
            if ((needCount == null))
                totalCount();
            return needCount;
        }

        public BigDecimal getAddCount() {
            if ((addCount == null))
                totalCount();
            return addCount;
        }

        public ResCount getSaleCount() {
            if (saleCount == null)
                totalCount();
            return saleCount;
        }

        public ResCount getBackCount() {
            if (backCount == null)
                totalCount();
            return backCount;
        }
    }

    public static class SaleAndBack {


        private StoreResBackTotalData back;

        private StoreResSaleTotalData sale;

        public SaleAndBack(StoreResBackTotalData back) {
            this.back = back;
        }

        public SaleAndBack(StoreResSaleTotalData sale) {
            this.sale = sale;
        }

        public StoreRes getStoreRes() {
            if (sale != null) {
                return sale.getStoreRes();
            } else {
                return back.getStoreRes();
            }
        }

        public Res getRes() {
            return getStoreRes().getRes();
        }

        public StoreResBackTotalData getBack() {
            return back;
        }

        public StoreResSaleTotalData getSale() {
            return sale;
        }

        public void setBack(StoreResBackTotalData back) {
            this.back = back;
        }

        public void setSale(StoreResSaleTotalData sale) {
            this.sale = sale;
        }

        public BigDecimal getCalcSaleAvgMoney(){
            if (sale != null){
            if (getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)){
                return getSale().getNeedMoney().divide(getSale().getResCount().getAuxCount(),2,BigDecimal.ROUND_HALF_UP);
            }else{
                return getSale().getMoney().divide(getSale().getResCount().getMasterCount(),2,BigDecimal.ROUND_HALF_UP);
            }}
            else return null;
        }
    }

    public static class SaleAndBackTotalData implements TotalDataGroup.GroupTotalData {

        private ResCount saleCount;

        private ResCount backCount;

        private BigDecimal saleMoney;

        private BigDecimal backMoney;

        private BigDecimal needCount;

        private BigDecimal addCount;

        private BigDecimal needMoney;

        public SaleAndBackTotalData() {

            saleCount = ResTotalCount.ZERO;
            backCount = ResTotalCount.ZERO;
            saleMoney = BigDecimal.ZERO;
            backMoney = BigDecimal.ZERO;
            needCount = BigDecimal.ZERO;
            addCount = BigDecimal.ZERO;
            needMoney = BigDecimal.ZERO;
        }


        public BigDecimal getCalcAvgMoney(){
            if (saleCount != null){
            if (saleCount.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)){
                return saleMoney.divide(saleCount.getAuxCount(), 2, BigDecimal.ROUND_HALF_UP);
            }else{
                return saleMoney.divide(saleCount.getMasterCount(), 2, BigDecimal.ROUND_HALF_UP);
            }}
            return null;
        }



        public void put(SaleAndBack saleAndBack) {
            if (saleAndBack.getBack() != null) {
                backCount = backCount.add(saleAndBack.getBack().getResCount());
                backMoney = backMoney.add(saleAndBack.getBack().getMoney());

            }
            if (saleAndBack.getSale() != null) {
                saleCount = saleCount.add(saleAndBack.getSale().getResCount());
                saleMoney = saleMoney.add(saleAndBack.getSale().getMoney());

                    needCount = needCount.add(saleAndBack.getSale().getNeedCount());
                    addCount = addCount.add(saleAndBack.getSale().getAddCount());
                    needMoney = needMoney.add(saleAndBack.getSale().getNeedMoney());


            }
        }


        public ResCount getSaleCount() {
            return saleCount;
        }


        public ResCount getBackCount() {
            return backCount;
        }


        public BigDecimal getSaleMoney() {
            return saleMoney;
        }


        public BigDecimal getBackMoney() {
            return backMoney;
        }


        public BigDecimal getNeedCount() {
            return needCount;
        }


        public BigDecimal getAddCount() {
            return addCount;
        }


        public BigDecimal getNeedMoney() {
            return needMoney;
        }


    }
}
