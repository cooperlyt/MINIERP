package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.ResCategory;
import com.dgsoft.erp.model.StockChange;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 23/04/14
 * Time: 13:01
 */
@Name("resCategoryList")
@Scope(ScopeType.CONVERSATION)
public class ResCategoryList extends ErpEntityQuery<ResCategory> {

    private static final String EJBQL = "select resCategory from ResCategory resCategory where resCategory.enable=true and  resCategory.root = true";

    private static final String[] RESTRICTIONS = {
            "resCategory.type in (#{resCategeroyList.acceptTypes})",
          };

    public ResCategoryList(){
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
        acceptTypes = new ArrayList<ResCategory.ResType>(EnumSet.allOf(ResCategory.ResType.class));
    }

    private List<ResCategory.ResType> acceptTypes;

    public List<ResCategory.ResType> getAcceptTypes() {
        return acceptTypes;
    }

    public List<ResCategory> getSaleResCategorys(){
        acceptTypes = new ArrayList<ResCategory.ResType>(StockChange.StoreChangeType.SELL_OUT.getResTypes());
        return getResultList();
    }
}
