package com.dgsoft.erp.tools;

import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.ResTreeNode;
import com.google.common.collect.Iterators;

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

        List<StoreRes> validStoreResList = new ArrayList<StoreRes>();

        for (StoreRes storeRes: res.getStoreResList()){
            if (storeRes.isEnable() || filter.containDisable()){
                validStoreResList.add(storeRes);
            }
        }

        if (filter.getCategoryTypes().equals(ResTreeFilter.StoreResAddType.LIST_ADD) ||
                (propertyCount <= 1)) {

            for (StoreRes storeRes : validStoreResList) {
                if (storeRes.isEnable() || filter.containDisable()) {
                    result.add(new StoreResTreeNode(storeRes, res));
                }
            }

        } else {


            Map<FormatDefine, Set<Format>> defines = new HashMap<FormatDefine, Set<Format>>();

            for (StoreRes storeRes : res.getStoreReses()) {
                for (Format format : storeRes.getFormats()) {
                    Set<Format> containsFormat = defines.get(format.getFormatDefine());
                    if (containsFormat == null) {
                        containsFormat = new HashSet<Format>();

                        defines.put(format.getFormatDefine(), containsFormat);
                    }
                    containsFormat.add(format);

                }
            }


            FormatDefine define = null;

            List<StoreResPropertyTreeNode> treeNodes = null;

            for (int i = 0; i < propertyCount; i++) {
                if (i == (propertyCount - 1)) {
                    if (res.getFormatDefines().size() <= i) {
                        define = null;
                    } else {
                        define = res.getFormatDefineList().get(i);
                    }
                    break;
                }
                define = res.getFormatDefineList().get(i);


                if (treeNodes == null) {
                    treeNodes = new ArrayList<StoreResPropertyTreeNode>();
                    for (Format f : defines.get(define)) {
                        StoreResPropertyTreeNode srptn = new StoreResPropertyTreeNode(f, res);
                        treeNodes.add(srptn);
                        result.add(srptn);
                    }
                } else {
                    List<StoreResPropertyTreeNode> childNodes = new ArrayList<StoreResPropertyTreeNode>();
                    for (StoreResPropertyTreeNode parentNode : treeNodes) {
                        for (Format f : defines.get(define)) {
                            StoreResPropertyTreeNode newNode = new StoreResPropertyTreeNode(f, parentNode);
                            childNodes.add(newNode);
                            parentNode.addChild(newNode);
                        }
                    }
                    treeNodes = childNodes;
                }
            }

            for (StoreRes sr : validStoreResList) {
                boolean find = false;
                for (StoreResPropertyTreeNode node : treeNodes) {
                    if (sr.seamFormat(node.getFormats())) {
                        node.addChild(new StoreResTreeNode(sr, node, (define == null) ? sr.getDisplayFloatRate() : define.getName()));
                        find = true;
                        break;
                    }
                }
                if (!find){
                    throw  new IllegalArgumentException("storeRes not in tree:" + sr);
                }
            }

            for (ResTreeNode node: result){
                ((StoreResPropertyTreeNode)node).clean();

            }
        }


        return result;
    }

    public static class StoreResTreeNode implements ResTreeNode {

        private StoreRes storeRes;

        private String title = null;

        private TreeNode parent;

        public StoreResTreeNode(StoreRes storeRes, TreeNode parent) {
            this.storeRes = storeRes;
            this.parent = parent;
        }

        public StoreResTreeNode(StoreRes storeRes, TreeNode parent, String title) {
            this.storeRes = storeRes;
            this.title = title;
            this.parent = parent;
        }

        @Override
        public Object getData() {
            return storeRes;
        }

        @Override
        public String getNodeType() {
            return "storeRes";
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
    }

    public Map<FormatDefine, Format> getFormats() {
        Map<FormatDefine, Format> result;
        if (getParent() instanceof StoreResPropertyTreeNode) {
            result = ((StoreResPropertyTreeNode) getParent()).getFormats();
        } else {
            result = new HashMap<FormatDefine, Format>();
        }
        result.put(format.getFormatDefine(), format);
        return result;
    }

    private Format format;

    private TreeNode parent;

    public StoreResPropertyTreeNode(Format format, TreeNode parent) {
        this.format = format;
        this.parent = parent;
    }


    public void clean(){

        List<ResTreeNode> removeNode = new ArrayList<ResTreeNode>();
        for (ResTreeNode childNode: childList){
            if (childNode instanceof StoreResPropertyTreeNode){
                ((StoreResPropertyTreeNode) childNode).clean();
            }
            if (childNode.getChildCount() == 0){
                removeNode.add(childNode);
            }
        }
        childList.removeAll(removeNode);
    }

    private List<ResTreeNode> childList = new ArrayList<ResTreeNode>();

    public void addChild(ResTreeNode node) {
        childList.add(node);
    }

    @Override
    public Object getData() {
        return format;
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
        return childList.get(childIndex);
    }

    @Override
    public int getChildCount() {
        return childList.size();
    }

    @Override
    public TreeNode getParent() {
        return parent;
    }

    @Override
    public int getIndex(TreeNode node) {
        return childList.indexOf(node);
    }

    @Override
    public boolean getAllowsChildren() {
        return true;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public Enumeration children() {
        return Iterators.asEnumeration(childList.iterator());
    }
}
