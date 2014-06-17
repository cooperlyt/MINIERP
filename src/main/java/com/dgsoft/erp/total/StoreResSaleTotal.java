package com.dgsoft.erp.total;

import com.dgsoft.common.SearchDateArea;
import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.action.ResHelper;
import com.dgsoft.erp.model.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by cooper on 6/17/14.
 */
@Name("storeResSaleTotal")
public class StoreResSaleTotal extends ErpEntityQuery<OrderItem> {

    private static final String EJBQL = "select orderItem from OrderItem orderItem left join fetch orderItem.storeRes storeRes " +
            "left join fetch storeRes.res res left join fetch res.unitGroup unitGroup " +
            "where orderItem.status = 'COMPLETED' and orderItem.needRes.customerOrder.allStoreOut = true";


    private static final String[] RESTRICTIONS = {
            "orderItem.needRes.customerOrder.allShipDate >= #{searchDateArea.dateFrom}",
            "orderItem.needRes.customerOrder.allShipDate <= #{searchDateArea.searchDateTo}",
            "orderItem.storeRes.res.resCategory.id in (#{storeResCondition.searchResCategoryIds})",
            "orderItem.storeRes.res.id = #{storeResCondition.searchResId}",
            "orderItem.storeRes.floatConversionRate = #{storeResCondition.searchFloatConvertRate}",
            "orderItem.storeRes.id in (#{storeResCondition.matchStoreResIds})"};


    public StoreResSaleTotal() {
        super();
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
    }

    private List<ResSaleRebate> totalResRebate;

    @In(create = true)
    private SearchDateArea searchDateArea;

    private void initTotalResRebate() {
        if (totalResRebate == null) {
            initTotalResult();
            totalResRebate = new ArrayList<ResSaleRebate>();
            Set<String> resIds = new HashSet<String>();
            for (TotalItem totalItem : getTotalResult()) {
                resIds.add(totalItem.getRes().getId());
            }

            if (resIds.isEmpty()){
                return;
            }

            List<ResSaleRebate> resSaleRebates = getEntityManager().createQuery("select resSaleRebate from ResSaleRebate  resSaleRebate " +
                            "where resSaleRebate.customerOrder.allStoreOut = true and resSaleRebate.res.id in (:resIds) " +
                            "and resSaleRebate.customerOrder.allShipDate >= :fromDate and resSaleRebate.customerOrder.allShipDate <= :endDate",
                    ResSaleRebate.class).setParameter("resIds", new ArrayList<String>(resIds))
                    .setParameter("fromDate", searchDateArea.getDateFrom()).setParameter("endDate",searchDateArea.getSearchDateTo()).getResultList();

            for(ResSaleRebate resSaleRebate: resSaleRebates){
                boolean find = false;
                for(ResSaleRebate totalResSaleRebate: totalResRebate){
                    if (totalResSaleRebate.getRes().equals(resSaleRebate.getRes())){
                        find = true;
                        totalResSaleRebate.setRebateMoney(totalResSaleRebate.getRebateMoney().add(resSaleRebate.getRebateMoney()));
                        break;
                    }
                }
                if (!find){
                    totalResRebate.add(new ResSaleRebate(resSaleRebate.getRes(),resSaleRebate.getRebateMoney()));
                }

            }


        }
    }

    public List<ResSaleRebate> getTotalResRebate(){
        if (isAnyParameterDirty()) {
            refresh();
        }
        initTotalResRebate();
        return totalResRebate;
    }

    public BigDecimal getTotalRebateMoney(){
        BigDecimal result = BigDecimal.ZERO;
        for (ResSaleRebate rebate: getTotalResRebate()){
            result = result.add(rebate.getRebateMoney());
        }
        return result;
    }

    public int getTotalRebateCount(){
        return getTotalResRebate().size();
    }

    private List<TotalItem> totalResult;

    private void initTotalResult() {

        if (totalResult == null) {
            getResultList();
            totalResult = new ArrayList<TotalItem>();
            for (OrderItem item : getResultList()) {
                boolean find = false;
                for (TotalItem totalItem : totalResult) {
                    if (totalItem.isSameItem(item)) {
                        find = true;
                        totalItem.addCount(item.getUseUnitCount(), item.getTotalMoney(), item.getNeedCount(), item.getNeedMoney());
                        break;
                    }
                }
                if (!find) {
                    totalResult.add(new TotalItem(item.getStoreRes().getRes(), item.getFormats(), item.getUseUnit(),
                            item.getUseUnitCount(), item.getTotalMoney(), item.getNeedCount(), item.getNeedMoney()));
                }
            }
            Collections.sort(totalResult, new Comparator<TotalItem>() {
                @Override
                public int compare(TotalItem o1, TotalItem o2) {
                    return o1.getRes().compareTo(o2.getRes());
                }
            });
        }
    }

    public BigDecimal getTotalSaleMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (TotalItem item : getTotalResult()) {
            result = result.add(item.getSaleMoney());
        }
        return result;
    }

    public BigDecimal getTotalNeedMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (TotalItem item : getTotalResult()) {
            if (item.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
                result = result.add(item.getNeedMoney());
            }
        }
        return result;
    }


    public int getTotalItemCount() {
        return getTotalResult().size();
    }


    public List<TotalItem> getTotalResult() {
        if (isAnyParameterDirty()) {
            refresh();
        }
        initTotalResult();
        return totalResult;
    }

    @Override
    public void refresh() {
        super.refresh();
        totalResult = null;
        totalResRebate = null;
    }


    public class TotalItem {


        private BigDecimal count = BigDecimal.ZERO;

        private BigDecimal saleMoney = BigDecimal.ZERO;

        private Res res;

        private List<Format> formats;

        private ResUnit resUnit;

        private BigDecimal needCount = BigDecimal.ZERO;

        private BigDecimal needMoney = BigDecimal.ZERO;

        public TotalItem(Res res, List<Format> formats, ResUnit resUnit, BigDecimal count, BigDecimal saleMoney, BigDecimal needCount, BigDecimal needMoney) {
            super();
            this.res = res;
            this.formats = formats;
            this.resUnit = resUnit;
            addCount(count, saleMoney, needCount, needMoney);
        }

        public void addCount(BigDecimal count, BigDecimal saleMoney, BigDecimal needCount, BigDecimal needMoney) {
            this.count = this.count.add(count);
            this.saleMoney = this.saleMoney.add(saleMoney);
            if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
                if (needCount == null) {
                    this.needCount = this.needCount.add(count);
                } else
                    this.needCount = this.needCount.add(needCount);
                if (needMoney == null) {
                    this.needMoney = this.needMoney.add(saleMoney);
                } else
                    this.needMoney = this.needMoney.add(needMoney);
            }
        }

        public String getFormatTitle() {
            return ResHelper.instance().getFormatsTitle(formats, false);
        }

        public boolean isSameItem(OrderItem orderItem) {
            return orderItem.getStoreRes().getRes().getId().equals(this.res.getId()) &&
                    orderItem.getUseUnit().equals(resUnit) &&
                    ResHelper.instance().sameFormat(orderItem.getStoreRes().getFormats(), this.formats);
        }

        public BigDecimal getAvgPrice() {
            if ((count.compareTo(BigDecimal.ZERO) == 0) || (saleMoney.compareTo(BigDecimal.ZERO) == 0)) {
                return BigDecimal.ZERO;
            }
            return saleMoney.divide(count,
                    Currency.getInstance(Locale.CHINA).getDefaultFractionDigits(), BigDecimal.ROUND_HALF_UP);
        }

        public BigDecimal getCount() {
            return count;
        }


        public BigDecimal getSaleMoney() {
            return saleMoney;
        }


        public ResUnit getResUnit() {
            return resUnit;
        }


        public Res getRes() {
            return res;
        }


        public List<Format> getFormats() {
            return formats;
        }


        public BigDecimal getNeedCount() {
            return needCount;
        }


        public BigDecimal getNeedMoney() {
            return needMoney;
        }


    }

}
