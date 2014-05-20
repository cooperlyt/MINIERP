package com.dgsoft.erp.business.finance;

import com.dgsoft.common.system.DictionaryWord;
import com.dgsoft.common.system.model.Word;
import com.dgsoft.erp.model.Accounting;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.richfaces.model.SwingTreeNodeImpl;

import javax.persistence.EntityManager;
import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 14-5-20
 * Time: 上午11:44
 */
@Name("account")
public class Account {

    @In
    private DictionaryWord dictionary;

    @In
    private EntityManager erpEntityManager;

    public List<TreeNode> getAccountTree() {
        List<TreeNode> result = new ArrayList<TreeNode>();

        for (Word word : dictionary.getWordList("finance.accountType")) {
            SwingTreeNodeImpl rootNode = new SwingTreeNodeImpl<Word>();
            for (Accounting ac : erpEntityManager.createQuery("SELECT accounting from Accounting accounting " +
                    "where accounting.level = 1 and accounting.accountingType =:type", Accounting.class).
                    setParameter("type", word.getId()).getResultList()) {
                rootNode.addChild(ac);
            }
            result.add(rootNode);
        }

        return result;
    }

}
