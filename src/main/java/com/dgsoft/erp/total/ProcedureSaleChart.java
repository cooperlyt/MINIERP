package com.dgsoft.erp.total;

import com.dgsoft.common.SearchDateArea;
import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.model.ResUnit;
import com.dgsoft.erp.total.data.AreaResSaleGroupData;
import com.dgsoft.erp.total.data.ProcedureSaleData;
import com.dgsoft.erp.total.data.ResSaleRebateTotalData;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Logging;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cooper on 7/25/14.
 */
@Name("procedureSaleChart")
public class ProcedureSaleChart {


    @In(create = true)
    protected EntityManager erpEntityManager;

    @In(create = true)
    private SearchDateArea searchDateArea;

    private boolean onlyShip = true;

    public boolean isOnlyShip() {
        return onlyShip;
    }

    public void setOnlyShip(boolean onlyShip) {
        this.onlyShip = onlyShip;
    }


    private boolean useSaleArea = true;

    public boolean isUseSaleArea() {
        return useSaleArea;
    }

    public void setUseSaleArea(boolean useSaleArea) {
        this.useSaleArea = useSaleArea;
    }


    private List<ProcedureSaleData> totalResult;

    private void putBack(List<ProcedureSaleData> base, List<ProcedureSaleData> back) {
        for (ProcedureSaleData bpsd : back) {
            boolean found = false;
            for (ProcedureSaleData psd : base) {
                if (psd.getAreaName().equals(bpsd.getAreaName()) && psd.getResId().equals(bpsd.getResId()) && psd.getSaleUnitId().equals(bpsd.getSaleUnitId())) {
                    found = true;
                    psd.setBackCount(psd.getBackCount().add(bpsd.getSaleCount()));
                    psd.setBackMoney(psd.getBackMoney().add(bpsd.getSalePrice()));
                    break;
                }
            }
            if (!found) {
                ProcedureSaleData backpsd = new ProcedureSaleData(bpsd.getAreaName(), bpsd.getResId(), BigDecimal.ZERO, BigDecimal.ZERO, bpsd.getSaleUnitId());
                backpsd.setBackCount(bpsd.getSaleCount());
                backpsd.setBackMoney(bpsd.getSalePrice());
                base.add(backpsd);
            }
        }
    }

    private void putRebate(List<ProcedureSaleData> base, List<ResSaleRebateTotalData> rebates) {
        for (ResSaleRebateTotalData rt : rebates) {
            for (ProcedureSaleData psd : base) {
                if (rt.getAreaName().equals(psd.getAreaName()) && rt.getResId().equals(psd.getResId()) && rt.getResUnitId().equals(psd.getSaleUnitId())) {
                    psd.setRebateMoney(psd.getRebateMoney().add(rt.getMoney()));
                    psd.setRebateCount(psd.getRebateCount().add(rt.getCount()));
                    break;
                }
            }
        }
    }

    private void initResult() {
        if (totalResult == null) {

            List<ProcedureSaleData> subList;

            String condition = " and orderItem.status <> 'REMOVED' ";
            if (onlyShip) {
                condition += " and orderItem.needRes.customerOrder.allStoreOut = true " + searchDateArea.genConditionSQL("orderItem.needRes.customerOrder.allShipDate", true);

            } else {
                condition += searchDateArea.genConditionSQL("orderItem.needRes.customerOrder.createDate", true);

            }


            if (isUseSaleArea()) {

                subList = searchDateArea.setQueryParam(erpEntityManager.createQuery(
                        "select new com.dgsoft.erp.total.data.ProcedureSaleData(orderItem.needRes.customerOrder.customer.customerArea.name," +
                                "orderItem.storeRes.res.id,sum(orderItem.totalMoney),sum(orderItem.saleCount),orderItem.resUnit.id ) from OrderItem orderItem " +
                                "where orderItem.needRes.customerOrder.canceled = false and " +
                                "  orderItem.storeRes.res.resCategory.type = 'PRODUCT' " + condition +
                                " group by orderItem.needRes.customerOrder.customer.customerArea.name,orderItem.storeRes.res.id,orderItem.resUnit.id", ProcedureSaleData.class
                )).getResultList();
            } else {

                subList = searchDateArea.setQueryParam(erpEntityManager.createQuery(
                        "select new com.dgsoft.erp.total.data.ProcedureSaleData(orderItem.needRes.customerOrder.customer.provinceCode," +
                                "orderItem.storeRes.res.id,sum(orderItem.totalMoney),sum(orderItem.saleCount),orderItem.resUnit.id ) from OrderItem orderItem " +
                                "where orderItem.needRes.customerOrder.canceled = false and " +
                                " orderItem.storeRes.res.resCategory.type = 'PRODUCT' " + condition +
                                " group by orderItem.needRes.customerOrder.customer.provinceCode,orderItem.storeRes.res.id,orderItem.resUnit.id", ProcedureSaleData.class
                )).getResultList();
            }


            String rebateCondition;
            if (onlyShip) {
                rebateCondition = " and resSaleRebate.customerOrder.allStoreOut = true " + searchDateArea.genConditionSQL("resSaleRebate.customerOrder.allShipDate", true);

            } else {
                rebateCondition = searchDateArea.genConditionSQL("resSaleRebate.customerOrder.createDate", true);
            }

            if (isUseSaleArea()) {
                putRebate(subList, searchDateArea.setQueryParam(erpEntityManager.createQuery(
                        "select new com.dgsoft.erp.total.data.ResSaleRebateTotalData(resSaleRebate.customerOrder.customer.customerArea.name," +
                                "resSaleRebate.res.id,resSaleRebate.resUnit.id,sum(resSaleRebate.rebateCount),sum(resSaleRebate.rebateMoney)) from ResSaleRebate resSaleRebate " +
                                "where resSaleRebate.customerOrder.canceled = false and " +
                                "  resSaleRebate.res.resCategory.type = 'PRODUCT' " + rebateCondition +
                                " group by resSaleRebate.customerOrder.customer.customerArea.name,resSaleRebate.res.id,resSaleRebate.resUnit.id", ResSaleRebateTotalData.class
                )).getResultList());
            } else {
                putRebate(subList, searchDateArea.setQueryParam(erpEntityManager.createQuery(
                        "select new com.dgsoft.erp.total.data.ResSaleRebateTotalData(resSaleRebate.customerOrder.customer.provinceCode," +
                                "resSaleRebate.res.id,resSaleRebate.resUnit.id,sum(resSaleRebate.rebateCount),sum(resSaleRebate.rebateMoney)) from ResSaleRebate resSaleRebate " +
                                "where resSaleRebate.customerOrder.canceled = false and " +
                                "  resSaleRebate.res.resCategory.type = 'PRODUCT' " + rebateCondition +
                                " group by resSaleRebate.customerOrder.customer.provinceCode,resSaleRebate.res.id,resSaleRebate.resUnit.id", ResSaleRebateTotalData.class
                )).getResultList());
            }


            String backCondition;
            if (onlyShip) {
                backCondition = " and backItem.orderBack.confirmed = true " + searchDateArea.genConditionSQL("backItem.orderBack.completeDate", true);

            } else {
                backCondition = searchDateArea.genConditionSQL("backItem.orderBack.createDate", true);

            }

            if (isUseSaleArea()) {

                putBack(subList, searchDateArea.setQueryParam(erpEntityManager.createQuery(
                        "select new com.dgsoft.erp.total.data.ProcedureSaleData(backItem.orderBack.customer.customerArea.name," +
                                "backItem.storeRes.res.id,sum(backItem.totalMoney),sum(backItem.saleCount) ,backItem.resUnit.id) from BackItem backItem " +
                                "where backItem.storeRes.res.resCategory.type = 'PRODUCT' " + backCondition +
                                " group by backItem.orderBack.customer.customerArea.name,backItem.storeRes.res.id,backItem.resUnit.id", ProcedureSaleData.class
                )).getResultList());

            } else {
                putBack(subList, searchDateArea.setQueryParam(erpEntityManager.createQuery(
                        "select new com.dgsoft.erp.total.data.ProcedureSaleData(backItem.orderBack.customer.provinceCode," +
                                "backItem.storeRes.res.id,sum(backItem.totalMoney),sum(backItem.saleCount),backItem.resUnit.id ) from BackItem backItem " +
                                "where backItem.storeRes.res.resCategory.type = 'PRODUCT' " + backCondition +
                                " group by backItem.orderBack.customer.provinceCode,backItem.storeRes.res.id,backItem.resUnit.id", ProcedureSaleData.class
                )).getResultList());
            }


            Logging.getLog(this.getClass()).debug(searchDateArea.getDateFrom() + "-" + searchDateArea.getSearchDateTo() + "|sql return count:" + subList.size());

            for (ProcedureSaleData data : subList) {
                data.setResName(erpEntityManager.find(Res.class, data.getResId()).getName());
                data.setSaleUnitName(erpEntityManager.find(ResUnit.class, data.getSaleUnitId()).getName());
            }

            totalResult = subList;

        }
    }

    public List<ProcedureSaleData> getTotalResult() {
        initResult();
        return totalResult;
    }

    public ProcedureSaleData getTotalData() {
        ProcedureSaleData result = new ProcedureSaleData("", "", BigDecimal.ZERO, BigDecimal.ZERO, "");
        for (ProcedureSaleData sub : getTotalResult()) {
            result.setSalePrice(result.getSalePrice().add(sub.getSalePrice()));
            result.setRebateMoney(result.getRebateMoney().add(sub.getRebateMoney()));
            result.setBackMoney(result.getBackMoney().add(sub.getBackMoney()));
        }
        return result;
    }

    public List<ProcedcureKey> getGroupTotalData() {
        List<ProcedcureKey> result = new ArrayList<ProcedcureKey>();
        for (ProcedureSaleData data : getTotalResult()) {
            ProcedcureKey foundKey = null;
            for (ProcedcureKey key : result) {
                if (key.getResId().equals(data.getResId()) && key.getUnitId().equals(data.getSaleUnitId())) {
                    foundKey = key;
                    break;
                }
            }
            if (foundKey == null) {
                foundKey = new ProcedcureKey(data.getResId(), data.getResName(), data.getSaleUnitId(), data.getSaleUnitName());
                result.add(foundKey);
            }
            foundKey.getDatas().add(data);

        }
        return result;

    }


    public boolean isTotaled() {
        return totalResult != null;
    }

    public void refresh() {
        totalResult = null;
    }

    public static class ProcedcureKey {

        private String resId;

        private String resName;

        private String unitId;

        private String unitName;


        private List<ProcedureSaleData> datas = new ArrayList<ProcedureSaleData>();

        public ProcedcureKey(String resId, String resName, String unitId, String unitName) {
            this.resId = resId;
            this.resName = resName;
            this.unitId = unitId;
            this.unitName = unitName;
        }

        public String getResId() {
            return resId;
        }

        public void setResId(String resId) {
            this.resId = resId;
        }

        public String getResName() {
            return resName;
        }

        public void setResName(String resName) {
            this.resName = resName;
        }

        public String getUnitId() {
            return unitId;
        }

        public void setUnitId(String unitId) {
            this.unitId = unitId;
        }

        public String getUnitName() {
            return unitName;
        }

        public void setUnitName(String unitName) {
            this.unitName = unitName;
        }

        public List<ProcedureSaleData> getDatas() {
            return datas;
        }


        public ProcedureSaleData getTotalData() {
            ProcedureSaleData result = new ProcedureSaleData("", resId, BigDecimal.ZERO, BigDecimal.ZERO, unitId);
            for (ProcedureSaleData sub : getDatas()) {
                result.setSaleCount(result.getSaleCount().add(sub.getSaleCount()));
                result.setSalePrice(result.getSalePrice().add(sub.getSalePrice()));
                result.setRebateCount(result.getRebateCount().add(sub.getRebateCount()));
                result.setRebateMoney(result.getRebateMoney().add(sub.getRebateMoney()));
                result.setBackCount(result.getBackCount().add(sub.getBackCount()));
                result.setBackMoney(result.getBackMoney().add(sub.getBackMoney()));
            }
            return result;
        }
    }

}
