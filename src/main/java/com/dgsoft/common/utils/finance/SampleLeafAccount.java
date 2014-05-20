package com.dgsoft.common.utils.finance;

import javax.swing.tree.TreeNode;
import java.util.Enumeration;

/**
 * Created by cooper on 5/17/14.
 */
public class SampleLeafAccount implements Account{


    private Direction direction;
    private String name;
    private Account parent;
    private int level;
    private String accountCode;

    public SampleLeafAccount(Direction direction, String name, Account parent, int level, String accountCode) {
        this.direction = direction;
        this.name = name;
        this.parent = parent;
        this.level = level;
        this.accountCode = accountCode;
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public TreeNode getChildAt(int childIndex) {
        return null;
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public Account getParent() {
        return parent;
    }

    @Override
    public int getIndex(TreeNode node) {
        return 0;
    }

    @Override
    public boolean getAllowsChildren() {
        return false;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public Enumeration children() {
        return null;
    }

    @Override
    public int getLevel() {
        return level;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }
}
