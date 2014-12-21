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
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
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

    private Map<Res, ResSaleTotalResult> resultData;

    private static final String SALE_DATA_SQL = "select new com.dgsoft.erp.total.data.StoreResSaleTotalData(oi.storeRes,sum(oi.count),avg(oi.money),sum(oi.totalMoney),sum(oi.needCount)) " +
            " from OrderItem oi where oi.needRes.customerOrder.canceled <> true and oi.needRes.customerOrder.createDate >= :beginDate and oi.needRes.customerOrder.createDate <= :endDate group by oi.storeRes";


    private static final String REBATE_DATA_SQL = "select new com.dgsoft.erp.total.data.ResRebateTotalData(rsr.res,sum(rsr.rebateCount),sum(rsr.rebateMoney)) " +
            " from ResSaleRebate rsr where rsr.customerOrder.canceled <> true and rsr.customerOrder.createDate >= :beginDate and rsr.customerOrder.createDate <= :endDate  group by rsr.res";

    private static final String BACK_DATA_SQL = "select new com.dgsoft.erp.total.data.StoreResBackTotalData(bi.storeRes,bi.count,bi.totalMoney) " +
            " from BackItem bi where bi.orderBack.confirmed = true and bi.orderBack.completeDate >= :beginDate and bi.orderBack.completeDate <= :endDate group by bi.storeRes";

    private void total() {
        resultData = new HashMap<Res, ResSaleTotalResult>();

        List<StoreResSaleTotalData> saleTotal = erpEntityLoader.getEntityManager().createQuery(SALE_DATA_SQL, StoreResSaleTotalData.class)
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

    public void export() {
        refresh();
        ExportRender render = new ExcelExportRender(messages.get("ResSaleTotal"));

        render.setNextCellType(ExportRender.Type.HEADER, 0);
        render.cell(0, 0, messages.get("res"));
        render.cell(0, 1, messages.get("StoreRes"));
        render.cell(0, 2, 0, 5, messages.get("SaleCount"));
        render.cell(0, 6, messages.get("NeedCount"));
        render.cell(0, 7, messages.get("needItemAdd"));
        render.cell(0, 8, messages.get("SaleMoney"));
        render.cell(0, 9, 0, 12, messages.get("SaleBackCount"));
        render.cell(0, 13, messages.get("SaleBackMoney"));
        render.cell(0, 14, messages.get("SaleRebateCount"));
        render.cell(0, 15, messages.get("SaleRebateMoney"));
        int row = 1;
        for (ResSaleTotalResult result : getResultList()) {
            int beginRow = row;
            Logging.getLog(getClass()).debug("resyktL:" + result.getSaleAndBacks().size());
            for (SaleAndBack sb : result.getSaleAndBacks()) {

                render.cell(row, 1, ResHelper.instance().generateStoreResTitle(sb.getStoreRes()));
                if (sb.getSale() != null) {
                    render.cell(row, 2, sb.getSale().getResCount().getMasterCount().doubleValue());
                    render.cell(row, 3, sb.getRes().getUnitGroup().getMasterUnit().getName());
                    if (sb.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
                        render.cell(row, 4, sb.getSale().getResCount().getAuxCount().doubleValue());
                        render.cell(row, 5, sb.getRes().getUnitGroup().getFloatAuxiliaryUnit().getName());
                        render.cell(row, 6, sb.getSale().getNeedCount().doubleValue());
                        render.cell(row, 7, sb.getSale().getAddCount().doubleValue());
                    }
                    render.cell(row, 8, sb.getSale().getMoney().doubleValue());
                }
                if (sb.getBack() != null) {

                    render.cell(row, 9, sb.getBack().getResCount().getMasterCount().doubleValue());
                    render.cell(row, 10, sb.getRes().getUnitGroup().getMasterUnit().getName());

                    if (sb.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
                        render.cell(row, 11, sb.getBack().getResCount().getAuxCount().doubleValue());
                        render.cell(row, 12, sb.getRes().getUnitGroup().getFloatAuxiliaryUnit().getName());

                    }


                    render.cell(row, 13, sb.getBack().getMoney().doubleValue());
                }
                row++;

            }

            if (!result.getSaleAndBacks().isEmpty())

            render.cell(beginRow, 0, row - 1, 0, result.getRes().getName());

            render.cell(row, 0, row, 1, result.getSaleAndBacks().isEmpty() ? result.getRes().getName() : messages.get("Total"));
            render.cell(row, 2, result.getSaleCount().getMasterCount().doubleValue());
            render.cell(row, 3, result.getRes().getUnitGroup().getMasterUnit().getName());
            if (result.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
                render.cell(row, 4, result.getSaleCount().getAuxCount().doubleValue());
                render.cell(row, 5, result.getRes().getUnitGroup().getFloatAuxiliaryUnit().getName());
                render.cell(row, 6, result.getNeedCount().doubleValue());
                render.cell(row, 7, result.getAddCount().doubleValue());
            }
            render.cell(row, 8, result.getSaleMoney().doubleValue());

            render.cell(row, 9, result.getBackCount().getMasterCount().doubleValue());
            render.cell(row, 10, result.getRes().getUnitGroup().getMasterUnit().getName());
            if (result.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
                render.cell(row, 11, result.getBackCount().getAuxCount().doubleValue());
                render.cell(row, 12, result.getRes().getUnitGroup().getFloatAuxiliaryUnit().getName());

            }

            render.cell(row, 13, result.getBackMoney().doubleValue());

            if (result.getRebate() != null) {
                render.cell(row, 14, result.getRebate().getCount().doubleValue());
                render.cell(row, 15, result.getRebate().getMoney().doubleValue());
            }

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

        private ResCount saleCount;

        private ResCount backCount;

        private BigDecimal saleMoney;

        private BigDecimal backMoney;

        private BigDecimal needCount;

        private BigDecimal addCount;

        public void totalCount() {
            saleCount = ResTotalCount.ZERO(res);
            backCount = ResTotalCount.ZERO(res);
            saleMoney = BigDecimal.ZERO;
            backMoney = BigDecimal.ZERO;
            needCount = BigDecimal.ZERO;
            addCount = BigDecimal.ZERO;
            for (SaleAndBack saleAndBack : saleAndBacks.values()) {
                if (saleAndBack.getBack() != null) {
                    backCount = backCount.add(saleAndBack.getBack().getResCount());
                    backMoney = backMoney.add(saleAndBack.getBack().getMoney());

                }
                if (saleAndBack.getSale() != null) {
                    saleCount = saleCount.add(saleAndBack.getSale().getResCount());
                    saleMoney = saleMoney.add(saleAndBack.getSale().getMoney());
                    if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
                        needCount = needCount.add(saleAndBack.getSale().getNeedCount());
                        addCount = addCount.add(saleAndBack.getSale().getAddCount());
                    }
                }
            }
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
            if ((needCount == null) && res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT))
                totalCount();
            return needCount;
        }

        public BigDecimal getAddCount() {
            if ((addCount == null) && res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT))
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
    }
}
