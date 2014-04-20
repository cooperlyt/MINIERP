package com.dgsoft.erp.tools;

import com.dgsoft.common.DataFormat;
import com.dgsoft.erp.ResFormatCache;
import com.dgsoft.erp.action.ResHelper;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.ResTreeNode;
import com.google.common.collect.Iterators;
import org.jboss.seam.log.Logging;

import javax.persistence.Transient;
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

        for (StoreRes storeRes : res.getStoreResList()) {
            if (storeRes.isEnable() || filter.containDisable()) {
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
                for (Format format : ResFormatCache.instance().getFormats(storeRes)) {
                    Set<Format> containsFormat = defines.get(format.getFormatDefine());
                    if (containsFormat == null) {
                        containsFormat = new HashSet<Format>();

                        defines.put(format.getFormatDefine(), containsFormat);
                        //Logging.getLog(StoreResPropertyTreeNode.class).debug("create FormatDefine:" + format.getFormatDefine().getName());
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

                    if (defines.get(define) != null) {
                        //Logging.getLog(StoreResPropertyTreeNode.class).debug("gen root node define:" + define.getName() + "-" + defines.get(define).size());
                        for (Format f : defines.get(define)) {
                            StoreResPropertyTreeNode srptn = new StoreResPropertyTreeNode(f, res);
                            treeNodes.add(srptn);
                            result.add(srptn);
                        }
                    }
                    StoreResPropertyTreeNode srptn = new StoreResPropertyTreeNode(define, res);
                    treeNodes.add(srptn);
                    result.add(srptn);

                } else {
                    List<StoreResPropertyTreeNode> childNodes = new ArrayList<StoreResPropertyTreeNode>();
                    for (StoreResPropertyTreeNode parentNode : treeNodes) {

                        if (defines.get(define) != null) {

                            for (Format f : defines.get(define)) {
                                StoreResPropertyTreeNode newNode = new StoreResPropertyTreeNode(f, parentNode);
                                childNodes.add(newNode);
                                parentNode.addChild(newNode);
                               // Logging.getLog(StoreResPropertyTreeNode.class).debug("add format :" + f.getFormatValue() + "to" + parentNode.getFormatDefine().getName());
                            }

                        }

                        StoreResPropertyTreeNode newNode = new StoreResPropertyTreeNode(define, parentNode);
                        childNodes.add(newNode);
                        parentNode.addChild(newNode);


                    }
                    treeNodes = childNodes;
                }
            }

            for (StoreRes sr : validStoreResList) {
                boolean find = false;
                for (StoreResPropertyTreeNode node : treeNodes) {

                    List<Format> srcParentFormats = new ArrayList<Format>(ResFormatCache.instance().getFormats(sr));

                    if (!sr.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)){
                        if (!srcParentFormats.isEmpty())
                            srcParentFormats.remove(srcParentFormats.size() - 1);
                        //TODO no format
                    }

                    if (ResHelper.instance().sameFormat(node.getFormats().values(), srcParentFormats)) {
                        node.addChild(new StoreResTreeNode(sr, node, (define == null) ? sr.getDisplayFloatRate() : ResHelper.instance().formatDisplayValue(ResFormatCache.instance().getFormatMap(sr).get(define))));
                        find = true;
                        break;
                    }
                }
                if (!find) {
                    Logging.getLog(StoreResPropertyTreeNode.class).warn("storeRes not in Tree:" + sr.getCode());

                   // throw new IllegalArgumentException("storeRes not in tree:" + sr);
                }
            }

            for (ResTreeNode node : result) {
                ((StoreResPropertyTreeNode) node).clean();
            }

            List<ResTreeNode> newResult = new ArrayList<ResTreeNode>();
            for (ResTreeNode node : result) {
                if (node.getChildCount() > 0) {
                    newResult.add(node);
                }
            }

            result = newResult;

        }


        return result;
    }

    public static class StoreResTreeNode implements ResTreeNode {

        private StoreRes storeRes;

        private String title = null;

        private ResTreeNode parent;

        public StoreResTreeNode(StoreRes storeRes, ResTreeNode parent) {
            this.storeRes = storeRes;
            this.parent = parent;
        }

        public StoreResTreeNode(StoreRes storeRes, ResTreeNode parent, String title) {
            this.storeRes = storeRes;
            this.title = title;
            this.parent = parent;
        }

        public String getTitle() {
             return title;
        }

        public String getShowTitle(){
            if (!DataFormat.isEmpty(title)){
                return title;
            }else if (storeRes.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)){
                return storeRes.getDisplayFloatRate() + " " + storeRes.getRes().getUnitGroup().getName();
            }else {
                return storeRes.getRes().getName();
            }
        }

        public StoreRes getStoreRes() {
            return storeRes;
        }

        public void setStoreRes(StoreRes storeRes) {
            this.storeRes = storeRes;
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
        public ResTreeFilter getTreeFilter() {
            return parent.getTreeFilter();
        }

        @Transient
        private Boolean expanded = null;

        @Transient
        @Override
        public boolean isExpanded() {
            if (expanded == null) {
                expanded = getTreeFilter().expandedDefault();
            }
            return expanded;
        }

        @Transient
        @Override
        public void setExpanded(boolean expanded) {
            this.expanded = expanded;
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
        if (format != null)
            result.put(format.getFormatDefine(), format);


        return result;
    }

    public Res getResParent(){
        TreeNode node = getParent();
        while (node != null){
            if (node instanceof Res){
                return (Res)node;
            }
            node = node.getParent();
        }
        return null;
    }

    private Format format;

    private ResTreeNode parent;

    private FormatDefine formatDefine;

    public FormatDefine getFormatDefine() {
        return formatDefine;
    }


    public StoreResPropertyTreeNode(FormatDefine formatDefine, ResTreeNode parent) {
        this.formatDefine = formatDefine;
        this.parent = parent;
    }

    public StoreResPropertyTreeNode(Format format, ResTreeNode parent) {
        this.format = format;
        this.parent = parent;
        this.formatDefine = format.getFormatDefine();
    }


    public void clean() {

        List<ResTreeNode> removeNode = new ArrayList<ResTreeNode>();
        for (ResTreeNode childNode : childList) {
            if (childNode instanceof StoreResPropertyTreeNode) {
                ((StoreResPropertyTreeNode) childNode).clean();
                if (childNode.getChildCount() == 0) {
                    removeNode.add(childNode);
                }
            }

        }
        childList.removeAll(removeNode);
    }

    private List<ResTreeNode> childList = new ArrayList<ResTreeNode>();


    @Transient
    private Boolean expanded = null;

    @Transient
    @Override
    public boolean isExpanded() {
        if (expanded == null) {
            expanded = getTreeFilter().expandedDefault();
        }
        return expanded;
    }

    @Transient
    @Override
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

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
    public ResTreeFilter getTreeFilter() {
        return parent.getTreeFilter();
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
