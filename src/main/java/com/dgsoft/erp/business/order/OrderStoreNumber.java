package com.dgsoft.erp.business.order;


import com.dgsoft.erp.model.OutNumber;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;

import java.util.ArrayList;
import java.util.List;

@Name("orderStoreNumber")
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class OrderStoreNumber {

    private OutNumber newNumber = new OutNumber();

    @DataModel("orderStoreOutNumberItems")
    private List<OutNumber> outNumbers = new ArrayList<OutNumber>();

    @DataModelSelection
    private OutNumber selectNumber;

    public List<OutNumber> getOutNumbers() {
        return outNumbers;
    }

    public OutNumber getNewNumber() {
        return newNumber;
    }

    public void setNewNumber(OutNumber newNumber) {
        this.newNumber = newNumber;
    }

    public void addNumber(){
        outNumbers.add(newNumber);
        newNumber = new OutNumber();
    }

    public void removeNumber(){
        outNumbers.remove(selectNumber);
    }
}
