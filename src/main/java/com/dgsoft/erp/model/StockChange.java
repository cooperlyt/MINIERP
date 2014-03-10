package com.dgsoft.erp.model;
// Generated Oct 1, 2013 5:41:32 PM by Hibernate Tools 4.0.0

import com.dgsoft.erp.model.api.StoreResCountGroup;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

/**
 * StockChange generated by hbm2java
 */
@Entity
@Table(name = "STOCK_CHANGE", catalog = "MINI_ERP")
public class StockChange implements java.io.Serializable {

    public enum StoreChangeType {
        MATERIAL_IN(EnumSet.of(ResCategory.ResType.MATERIAL, ResCategory.ResType.OUTER_MATERIAL), false),
        MATERIAL_BACK_IN(EnumSet.of(ResCategory.ResType.MATERIAL, ResCategory.ResType.OUTER_MATERIAL), false),
        MATERIAL_OUT(EnumSet.of(ResCategory.ResType.MATERIAL, ResCategory.ResType.OUTER_MATERIAL), true),
        SELL_OUT(EnumSet.of(ResCategory.ResType.PRODUCT, ResCategory.ResType.FITTING), true),
        SELL_BACK(EnumSet.of(ResCategory.ResType.PRODUCT, ResCategory.ResType.FITTING), false),
        PRODUCE_IN(EnumSet.of(ResCategory.ResType.PRODUCT, ResCategory.ResType.FITTING, ResCategory.ResType.SEMI_PRODUCT, ResCategory.ResType.WORK_IN_PROCESS), false),
        ALLOCATION_IN(EnumSet.allOf(ResCategory.ResType.class), false),
        ALLOCATION_OUT(EnumSet.allOf(ResCategory.ResType.class), true),
        ASSEMBLY_IN(EnumSet.allOf(ResCategory.ResType.class), false),
        ASSEMBLY_OUT(EnumSet.allOf(ResCategory.ResType.class), true),
        SCRAP_OUT(EnumSet.allOf(ResCategory.ResType.class), true),
        STORE_CHECK_LOSS(EnumSet.allOf(ResCategory.ResType.class), true),
        STORE_CHECK_ADD(EnumSet.allOf(ResCategory.ResType.class), false),
        STORE_CHANGE_IN(EnumSet.allOf(ResCategory.ResType.class), false),
        STORE_CHANGE_OUT(EnumSet.allOf(ResCategory.ResType.class), true);

        private EnumSet<ResCategory.ResType> resTypes;

        private boolean out;

        public EnumSet<ResCategory.ResType> getResTypes() {
            return resTypes;
        }


        public boolean isOut() {
            return out;
        }

        private StoreChangeType(EnumSet<ResCategory.ResType> resTypes, boolean out) {
            this.resTypes = resTypes;
            this.out = out;
        }
    }

    private String id;
    private Store store;
    private Date operDate;
    private String operEmp;
    private StoreChangeType operType;
    private String memo;
    private boolean verify;

    private Inventory inventoryAdd;
    private Inventory inventoryLoss;


    private Assembly assemblyForStoreOut;
    private Assembly assemblyForStoreIn;
    private Assembly assemblyForLoseOut;
    private ProductStoreIn productStoreIn;
    private MaterialStoreOut materialStoreOut;
    private Allocation allocationForStoreOut;
    private MaterialStoreIn materialStoreIn;
    private Allocation allocationForStoreIn;

    private ScrapStoreOut scrapStoreOut;
    private ProductBackStoreIn productBackStoreIn;
    private MaterialBackStoreIn materialBackStoreIn;
    private StoreChange storeChange;
    private Dispatch orderDispatch;

    private Set<StockChangeItem> stockChangeItems = new HashSet<StockChangeItem>(0);
    private Set<PrepareStockChange> prepareStockChanges = new HashSet<PrepareStockChange>(0);

    public StockChange() {
    }

    public StockChange(Store store, String operEmp,
                       StoreChangeType operType, boolean verify) {
        this.store = store;
        this.operEmp = operEmp;
        this.operType = operType;
        this.verify = verify;
    }

    public StockChange(Store store, Date operDate, String operEmp,
                       StoreChangeType operType, String memo, boolean verify) {
        this.store = store;
        this.operDate = operDate;
        this.operEmp = operEmp;
        this.operType = operType;
        this.memo = memo;
        this.verify = verify;
    }

    public StockChange(String id, Store store, Date operDate, String operEmp,
                       StoreChangeType operType, String memo, boolean verify) {
        this(store, operDate, operEmp, operType, memo, verify);
        this.id = id;
    }

    @Id
    @Column(name = "ID", unique = true, nullable = false, length = 32)
    @NotNull
    @Size(max = 32)
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "VERIFY", nullable = false)
    public boolean isVerify() {
        return verify;
    }

    public void setVerify(boolean verify) {
        this.verify = verify;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STORE", nullable = false)
    @NotNull
    public Store getStore() {
        return this.store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "OPER_DATE", nullable = false, length = 19)
    @NotNull
    public Date getOperDate() {
        return this.operDate;
    }

    public void setOperDate(Date operDate) {
        this.operDate = operDate;
    }

    @Column(name = "OPER_EMP", nullable = false, length = 32)
    @NotNull
    @Size(max = 32)
    public String getOperEmp() {
        return this.operEmp;
    }

    public void setOperEmp(String operEmp) {
        this.operEmp = operEmp;
    }


    @Enumerated(EnumType.STRING)
    @Column(name = "OPER_TYPE", nullable = false, length = 32)
    @NotNull
    public StoreChangeType getOperType() {
        return this.operType;
    }

    public void setOperType(StoreChangeType operType) {
        this.operType = operType;
    }

    @Column(name = "MEMO", length = 200)
    @Size(max = 200)
    public String getMemo() {
        return this.memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @OneToOne(optional = true, fetch = FetchType.LAZY, mappedBy = "stockChangeByStoreOut")
    public Assembly getAssemblyForStoreOut() {
        return this.assemblyForStoreOut;
    }

    public void setAssemblyForStoreOut(Assembly assemblyForStoreOut) {
        this.assemblyForStoreOut = assemblyForStoreOut;
    }


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "stockChange", orphanRemoval = true, cascade = {CascadeType.ALL})
    public Set<StockChangeItem> getStockChangeItems() {
        return this.stockChangeItems;
    }

    public void setStockChangeItems(Set<StockChangeItem> stockChangeItems) {
        this.stockChangeItems = stockChangeItems;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "stockChange", orphanRemoval = true, cascade = {CascadeType.ALL})
    public Set<PrepareStockChange> getPrepareStockChanges() {
        return prepareStockChanges;
    }

    public void setPrepareStockChanges(Set<PrepareStockChange> prepareStockChanges) {
        this.prepareStockChanges = prepareStockChanges;
    }

    @Transient
    public StoreResCountGroup getChangeDetails(){
        return new StoreResCountGroup(getStockChangeItems());
    }

    @Transient
    public List<PrepareStockChange> getPrepareStockChangeList() {
        List<PrepareStockChange> result = new ArrayList<PrepareStockChange>(getPrepareStockChanges());
        Collections.sort(result, new Comparator<PrepareStockChange>() {
            @Override
            public int compare(PrepareStockChange o1, PrepareStockChange o2) {
                return o1.getStoreRes().compareTo(o2.getStoreRes());
            }
        }
        );
        return result;
    }

    @Transient
    public List<StockChangeItem> getStockChangeItemList() {
        List<StockChangeItem> result = new ArrayList<StockChangeItem>(getStockChangeItems());
        Collections.sort(result, new Comparator<StockChangeItem>() {
            @Override
            public int compare(StockChangeItem o1, StockChangeItem o2) {
                return o1.getStoreRes().compareTo(o2.getStoreRes());
            }
        });
        return result;
    }

    @OneToOne(optional = true, fetch = FetchType.LAZY, mappedBy = "stockChangeByStoreIn")
    public Assembly getAssemblyForStoreIn() {
        return this.assemblyForStoreIn;
    }

    public void setAssemblyForStoreIn(Assembly assemblyForStoreIn) {
        this.assemblyForStoreIn = assemblyForStoreIn;
    }

    @OneToOne(optional = true, fetch = FetchType.LAZY, mappedBy = "stockChangeByLoseOut")
    public Assembly getAssemblyForLoseOut() {
        return assemblyForLoseOut;
    }

    public void setAssemblyForLoseOut(Assembly assemblyForLoseOut) {
        this.assemblyForLoseOut = assemblyForLoseOut;
    }

    @OneToOne(optional = true, fetch = FetchType.LAZY, mappedBy = "stockChange")
    public ProductStoreIn getProductStoreIn() {
        return this.productStoreIn;
    }

    public void setProductStoreIn(ProductStoreIn productStoreIn) {
        this.productStoreIn = productStoreIn;
    }

    @OneToOne(optional = true, fetch = FetchType.LAZY, mappedBy = "stockChange")
    public MaterialStoreOut getMaterialStoreOut() {
        return materialStoreOut;
    }

    public void setMaterialStoreOut(MaterialStoreOut materialStoreOut) {
        this.materialStoreOut = materialStoreOut;
    }

    @OneToOne(optional = true, fetch = FetchType.LAZY, mappedBy = "stockChangeByStoreOut")
    public Allocation getAllocationForStoreOut() {
        return this.allocationForStoreOut;
    }

    public void setAllocationForStoreOut(Allocation allocationForStoreOut) {
        this.allocationForStoreOut = allocationForStoreOut;
    }

    @OneToOne(optional = true, fetch = FetchType.LAZY, mappedBy = "stockChange")
    public MaterialStoreIn getMaterialStoreIn() {
        return this.materialStoreIn;
    }

    public void setMaterialStoreIn(MaterialStoreIn materialStoreIn) {
        this.materialStoreIn = materialStoreIn;
    }

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "stockChangeByStoreIn")
    public Allocation getAllocationForStoreIn() {
        return this.allocationForStoreIn;
    }

    public void setAllocationForStoreIn(Allocation allocationsForStoreIn) {
        this.allocationForStoreIn = allocationsForStoreIn;
    }


    @OneToOne(optional = true, fetch = FetchType.LAZY, mappedBy = "stockChangeAdd")
    public Inventory getInventoryAdd() {
        return this.inventoryAdd;
    }

    public void setInventoryAdd(Inventory inventoryAdd) {
        this.inventoryAdd = inventoryAdd;
    }

    @OneToOne(optional = true, fetch = FetchType.LAZY, mappedBy = "stockChangeLoss")
    public Inventory getInventoryLoss() {
        return this.inventoryLoss;
    }

    public void setInventoryLoss(Inventory inventoryLoss) {
        this.inventoryLoss = inventoryLoss;
    }

    @OneToOne(optional = true, fetch = FetchType.LAZY, mappedBy = "stockChange")
    public ScrapStoreOut getScrapStoreOut() {
        return this.scrapStoreOut;
    }

    public void setScrapStoreOut(ScrapStoreOut scrapStoreOuts) {
        this.scrapStoreOut = scrapStoreOuts;
    }

    @OneToOne(optional = true, fetch = FetchType.LAZY, mappedBy = "stockChange")
    public ProductBackStoreIn getProductBackStoreIn() {
        return this.productBackStoreIn;
    }

    public void setProductBackStoreIn(ProductBackStoreIn productBackStoreIn) {
        this.productBackStoreIn = productBackStoreIn;
    }

    @OneToOne(optional = true, fetch = FetchType.LAZY, mappedBy = "stockChange")
    public MaterialBackStoreIn getMaterialBackStoreIn() {
        return this.materialBackStoreIn;
    }

    public void setMaterialBackStoreIn(
            MaterialBackStoreIn materialBackStoreIn) {
        this.materialBackStoreIn = materialBackStoreIn;
    }

    @OneToOne(optional = true, fetch = FetchType.LAZY, mappedBy = "stockChange")
    public Dispatch getOrderDispatch() {
        return this.orderDispatch;
    }

    public void setOrderDispatch(Dispatch orderStoreOuts) {
        this.orderDispatch = orderStoreOuts;
    }

    @OneToOne(optional = true, fetch = FetchType.LAZY, mappedBy = "stockChange")
    public StoreChange getStoreChange() {
        return storeChange;
    }

    public void setStoreChange(StoreChange storeChange) {
        this.storeChange = storeChange;
    }
}
