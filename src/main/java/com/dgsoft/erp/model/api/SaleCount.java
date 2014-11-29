package com.dgsoft.erp.model.api;

import java.math.BigDecimal;

/**
 * Created by cooper on 11/29/14.
 */
public class SaleCount {

        private String storeId;
        private String storeResId;
        private BigDecimal count;

        public SaleCount(String storeId, String storeResId, BigDecimal count) {
            this.storeId = storeId;
            this.storeResId = storeResId;
            this.count = count;
        }



        public String getStoreResId() {
            return storeResId;
        }

        public BigDecimal getCount() {
            return count;
        }

        public String getStoreId() {
            return storeId;
        }
}
