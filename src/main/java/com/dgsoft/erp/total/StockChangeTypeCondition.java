package com.dgsoft.erp.total;

import com.dgsoft.erp.model.StockChange;
import org.jboss.seam.annotations.Name;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cooper on 4/28/14.
 */
@Name("stockChangeTypeCondition")
public class StockChangeTypeCondition {

    private boolean storeIn = false;


    private boolean materialIn = false;
    private boolean materialBackIn = false;
    private boolean sellBack = false;
    private boolean produceIn = false;
    private boolean allocationIn = false;
    private boolean assemblyIn = false;
    private boolean storeCheckAdd = false;
    private boolean storeChangeIn = false;

    private boolean materialOut = true;
    private boolean sellOut = true;
    private boolean allocationOut = true;
    private boolean assemblyOut = true;
    private boolean scrapOut = true;
    private boolean storeCheckLoss = true;
    private boolean storeChangeOut = true;

    public List<StockChange.StoreChangeType> getSearchTypes() {
        List<StockChange.StoreChangeType> result = new ArrayList<StockChange.StoreChangeType>();
        if (storeIn) {
            if (materialIn) {
                result.add(StockChange.StoreChangeType.MATERIAL_IN);
            }
            if (materialBackIn)
                result.add(StockChange.StoreChangeType.MATERIAL_BACK_IN);
            if (sellBack)
                result.add(StockChange.StoreChangeType.SELL_BACK);
            if (produceIn)
                result.add(StockChange.StoreChangeType.PRODUCE_IN);
            if (allocationIn)
                result.add(StockChange.StoreChangeType.ALLOCATION_IN);
            if (assemblyIn)
                result.add(StockChange.StoreChangeType.ASSEMBLY_IN);
            if (storeCheckAdd)
                result.add(StockChange.StoreChangeType.STORE_CHECK_ADD);
            if (storeChangeIn)
                result.add(StockChange.StoreChangeType.STORE_CHANGE_IN);
            if (result.isEmpty()) {
                result.addAll(StockChange.StoreChangeType.getAllIn());
            }

        } else {
            if (materialOut)
                result.add(StockChange.StoreChangeType.MATERIAL_OUT);
            if (sellOut)
                result.add(StockChange.StoreChangeType.SELL_OUT);
            if (allocationOut)
                result.add(StockChange.StoreChangeType.ALLOCATION_OUT);
            if (assemblyOut)
                result.add(StockChange.StoreChangeType.ASSEMBLY_OUT);
            if (scrapOut)
                result.add(StockChange.StoreChangeType.SCRAP_OUT);
            if (storeCheckLoss)
                result.add(StockChange.StoreChangeType.STORE_CHECK_LOSS);
            if (storeChangeOut)
                result.add(StockChange.StoreChangeType.STORE_CHANGE_OUT);
            if (result.isEmpty()) {
                result.addAll(StockChange.StoreChangeType.getAllOut());
            }
        }
        return result;
    }

    public void typeChangeListener() {

        materialIn = true;
        materialBackIn = true;
        sellBack = true;
        produceIn = true;
        allocationIn = true;
        assemblyIn = true;
        storeCheckAdd = true;
        storeChangeIn = true;
        materialOut = true;
        sellOut = true;
        allocationOut = true;
        assemblyOut = true;
        scrapOut = true;
        storeCheckLoss = true;
        storeChangeOut = true;

    }


    public boolean isStoreIn() {
        return storeIn;
    }

    public void setStoreIn(boolean storeIn) {
        this.storeIn = storeIn;
    }

    public boolean isMaterialIn() {
        return materialIn;
    }

    public void setMaterialIn(boolean materialIn) {
        this.materialIn = materialIn;
    }

    public boolean isMaterialBackIn() {
        return materialBackIn;
    }

    public void setMaterialBackIn(boolean materialBackIn) {
        this.materialBackIn = materialBackIn;
    }

    public boolean isSellBack() {
        return sellBack;
    }

    public void setSellBack(boolean sellBack) {
        this.sellBack = sellBack;
    }

    public boolean isProduceIn() {
        return produceIn;
    }

    public void setProduceIn(boolean produceIn) {
        this.produceIn = produceIn;
    }

    public boolean isAllocationIn() {
        return allocationIn;
    }

    public void setAllocationIn(boolean allocationIn) {
        this.allocationIn = allocationIn;
    }

    public boolean isAssemblyIn() {
        return assemblyIn;
    }

    public void setAssemblyIn(boolean assemblyIn) {
        this.assemblyIn = assemblyIn;
    }

    public boolean isStoreCheckAdd() {
        return storeCheckAdd;
    }

    public void setStoreCheckAdd(boolean storeCheckAdd) {
        this.storeCheckAdd = storeCheckAdd;
    }

    public boolean isStoreChangeIn() {
        return storeChangeIn;
    }

    public void setStoreChangeIn(boolean storeChangeIn) {
        this.storeChangeIn = storeChangeIn;
    }

    public boolean isMaterialOut() {
        return materialOut;
    }

    public void setMaterialOut(boolean materialOut) {
        this.materialOut = materialOut;
    }

    public boolean isSellOut() {
        return sellOut;
    }

    public void setSellOut(boolean sellOut) {
        this.sellOut = sellOut;
    }

    public boolean isAllocationOut() {
        return allocationOut;
    }

    public void setAllocationOut(boolean allocationOut) {
        this.allocationOut = allocationOut;
    }

    public boolean isAssemblyOut() {
        return assemblyOut;
    }

    public void setAssemblyOut(boolean assemblyOut) {
        this.assemblyOut = assemblyOut;
    }

    public boolean isScrapOut() {
        return scrapOut;
    }

    public void setScrapOut(boolean scrapOut) {
        this.scrapOut = scrapOut;
    }

    public boolean isStoreCheckLoss() {
        return storeCheckLoss;
    }

    public void setStoreCheckLoss(boolean storeCheckLoss) {
        this.storeCheckLoss = storeCheckLoss;
    }

    public boolean isStoreChangeOut() {
        return storeChangeOut;
    }

    public void setStoreChangeOut(boolean storeChangeOut) {
        this.storeChangeOut = storeChangeOut;
    }
}
