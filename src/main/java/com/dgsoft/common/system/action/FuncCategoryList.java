package com.dgsoft.common.system.action;

import com.dgsoft.common.system.model.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("funcCategoryList")
public class FuncCategoryList extends EntityQuery<FuncCategory> {

	private static final String EJBQL = "select funcCategory from FuncCategory funcCategory";

	private static final String[] RESTRICTIONS = {
			"lower(funcCategory.id) like lower(concat(#{funcCategoryList.funcCategory.id},'%'))",
			"lower(funcCategory.icon) like lower(concat(#{funcCategoryList.funcCategory.icon},'%'))",
			"lower(funcCategory.memo) like lower(concat(#{funcCategoryList.funcCategory.memo},'%'))",
			"lower(funcCategory.name) like lower(concat(#{funcCategoryList.funcCategory.name},'%'))",};

	private FuncCategory funcCategory = new FuncCategory();

	public FuncCategoryList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public FuncCategory getFuncCategory() {
		return funcCategory;
	}
}
