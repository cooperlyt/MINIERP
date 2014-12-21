package com.dgsoft.erp.total;

import com.dgsoft.common.SearchDateArea;
import com.dgsoft.erp.ErpEntityLoader;
import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.total.data.ResRebateTotalData;
import com.dgsoft.erp.total.data.StoreResBackTotalData;
import com.dgsoft.erp.total.data.StoreResSaleTotalData;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import java.util.List;
import java.util.Map;

/**
 * Created by cooper on 12/19/14.
 */
@Name("resSaleTotal")
public class ResSaleTotal {

    @In(create = true)
    private ErpEntityLoader erpEntityLoader;

    @In(create = true)
    private SearchDateArea searchDateArea;


    private Map<String,StoreResSaleTotalData> saleDatas;

    private Map<String,StoreResBackTotalData> backDatas;

    private Map<Res,ResRebateTotalData> rebateDatas;

    private static final String SALE_DATA_SQL = "select new com.dgsoft.erp.total.data.StoreResSaleTotalData(oi.storeRes.id,sum(oi.count),avg(oi.money),sum(oi.totalMoney),sum(oi.needCount)) " +
            " from OrderItem oi where oi.needRes.customerOrder.canceled <> true group by oi.storeRes.id";


    private static final String REBATE_DATA_SQL = "select new com.dgsoft.erp.total.data.ResRebateTotalData(rsr.res.id,sum(rsr.rebateCount),sum(rsr.rebateMoney)) from ResSaleRebate rsr where rsr.customerOrder.canceled <> true group by rsr.res.id";

    private static final String BACK_DATA_SQL = "select new com.dgsoft.erp.total.data.StoreResBackTotalData(bi.storeRes.id,bi.count,bi.totalMoney) from BackItem bi where bi.orderBack.confirmed = true group by bi.storeRes.id";

    private void total(){
        List<StoreResSaleTotalData>  erpEntityLoader.getEntityManager().createQuery(BACK_DATA_SQL)
    }





    public static class ResSaleTotalResult{



    }
}
