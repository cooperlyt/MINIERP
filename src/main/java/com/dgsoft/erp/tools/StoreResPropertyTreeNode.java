package com.dgsoft.erp.tools;

import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.ResTreeNode;

import javax.swing.tree.TreeNode;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 1/22/14
 * Time: 2:25 PM
 */
public class StoreResPropertyTreeNode implements ResTreeNode {


    public static List<ResTreeNode> genStoreResNodes(Res res, ResTreeFilter filter) {

        if (filter.getCategoryTypes().equals(ResTreeFilter.StoreResAddType.NOT_ADD)) {
            throw new IllegalArgumentException("filter is not set StoreResTreeNode");
        }

        List<ResTreeNode> result = new ArrayList<ResTreeNode>();

        int propertyCount = res.getFormatDefineList().size();

        if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            propertyCount++;
        }

        if (filter.getCategoryTypes().equals(ResTreeFilter.StoreResAddType.LIST_ADD) ||
                (propertyCount <= 1)) {

            for (StoreRes storeRes : res.getStoreResList()) {
                if (storeRes.isEnable() || filter.containDisable()) {
                    result.add(new StoreResTreeNode(storeRes));
                }
            }

        } else {



            Map<FormatDefine,Set<Format>> defines = new HashMap<FormatDefine, Set<Format>>();

            for (StoreRes storeRes: res.getStoreReses()){
               for(Format format: storeRes.getFormats()){
                   Set<Format> containsFormat = defines.get(format.getFormatDefine());
                   if (containsFormat == null){
                       containsFormat = new HashSet<Format>();

                       defines.put(format.getFormatDefine(),containsFormat);
                   }
                   containsFormat.add(format);

               }
            }



            FormatDefine define = null;

            StoreResPropertyTreeNode treeNode = null;

            for (int i = 0; i < propertyCount; i++) {
                if (i == (propertyCount - 1)){
                    if (res.getFormatDefines().size() <= i){
                        define = null;
                    }else{
                        define = res.getFormatDefineList().get(i);
                    }
                    break;
                }
                define = res.getFormatDefineList().get(i);



                if (treeNode == null){
                    treeNode = new StoreResPropertyTreeNode(define);
                    result.add(treeNode);
                }else{

                }





            }

            if (define == null){

            }

        }


        return result;
    }

    public static class StoreResTreeNode implements ResTreeNode {

        private StoreRes storeRes;

        private String title = null;

        public StoreResTreeNode(StoreRes storeRes) {
            this.storeRes = storeRes;
        }

        public StoreResTreeNode(StoreRes storeRes, String title) {
            this.storeRes = storeRes;
            this.title = title;
        }

        @Override
        public Object getData() {
            return storeRes;
        }

        @Override
        public String getNodeType() {
            return null;
        }

        @Override
        public void setTreeFilter(ResTreeFilter treeFilter) {

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
        public TreeNode getParent() {
            return null;
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
            return false;
        }

        @Override
        public Enumeration children() {
            return null;
        }
    }

    public StoreResPropertyTreeNode(FormatDefine define) {
        this.define = define;
    }

    private FormatDefine define;

    private List<ResTreeNode> clildList = new ArrayList<ResTreeNode>();

    public void addChild(ResTreeNode node){
        clildList.add(node);
    }

    @Override
    public Object getData() {
        return define;
    }

    @Override
    public String getNodeType() {
        return "property";
    }

    @Override
    public void setTreeFilter(ResTreeFilter treeFilter) {

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
    public TreeNode getParent() {
        return null;
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
        return false;
    }

    @Override
    public Enumeration children() {
        return null;
    }
}
