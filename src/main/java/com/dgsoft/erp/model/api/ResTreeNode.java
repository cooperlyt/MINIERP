package com.dgsoft.erp.model.api;

import com.dgsoft.erp.tools.ResTreeFilter;

import javax.swing.tree.TreeNode;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 1/22/14
 * Time: 10:53 AM
 */
public interface ResTreeNode extends TreeNode {

    public Object getData();

    public String getNodeType();

    public void setTreeFilter(ResTreeFilter treeFilter);
}
