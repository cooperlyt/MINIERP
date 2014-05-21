package com.dgsoft.common.utils.finance;

import javax.swing.tree.TreeNode;

/**
 * Created by cooper on 5/17/14.
 */
public interface Account extends TreeNode {

    public enum Direction {
        CREDIT, DBEDIT;

        public Direction reverse(){
            if (this.equals(Direction.CREDIT)){
                return Direction.DBEDIT;
            }else{
                return Direction.CREDIT;
            }
        }
    }

    public abstract String getAccountCode();

    public abstract Direction getDirection();

    public abstract String getName();

    public abstract Account getParent();

    public abstract int getLevel();

    public abstract Account getData();

}
