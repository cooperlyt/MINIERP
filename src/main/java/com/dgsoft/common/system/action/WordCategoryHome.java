package com.dgsoft.common.system.action;

import com.dgsoft.common.OrderBean;
import com.dgsoft.common.OrderBeanComparator;
import com.dgsoft.common.system.SystemEntityHome;
import com.dgsoft.common.system.model.Word;
import com.dgsoft.common.system.model.WordCategory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;

import javax.faces.event.ValueChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-6-19
 * Time: 上午10:16
 * To change this template use File | Settings | File Templates.
 */
@Name("wordCategoryHome")
public class WordCategoryHome extends SystemEntityHome<WordCategory> {

    @In
    private FacesMessages facesMessages;

    private String filterName;


    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public void verifyIdAvailableaa(ValueChangeEvent e) {
        String name = (String) e.getNewValue();
        if (!isNameAvailable(name)) {
            log.info("add confirm message");
            facesMessages.addToControlFromResourceBundle(e.getComponent().getId(), StatusMessage.Severity.ERROR, "fieldConflict", name);
        }
    }

    @Override
    protected boolean verifyPersistAvailable(){
        String name = this.getInstance().getName();
        if (!isNameAvailable(name)){
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,"fieldConflict", name);
            return false;
        }else
            return true;

    }

    public boolean isNameAvailable(String name) {
        return getEntityManager().createQuery("select w from WordCategory w where w.name = ?1").setParameter(1, name).getResultList().size() == 0;
    }

    @DataModel(value = "wordsDataModel",scope = ScopeType.PAGE)
    private List<Word> wordList = new ArrayList<Word>();

    @DataModelSelection
    private Word selectedWord;

    private boolean flag =false;

    private String wordValueFilter;

    private String wordKeyFiler;

    public String getWordKeyFiler() {
        return wordKeyFiler;
    }

    public void setWordKeyFiler(String wordKeyFiler) {
        this.wordKeyFiler = wordKeyFiler;
    }

    public String getWordValueFilter() {
        return wordValueFilter;
    }

    public void setWordValueFilter(String wordValueFilter) {
        this.wordValueFilter = wordValueFilter;
    }

    public Word getSelectedWord() {
        return selectedWord;
    }

    public void setSelectedWord(Word selectedWord) {
        this.selectedWord = selectedWord;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
    public void wordCategorySelected(){
           wordList.clear();
           wordList.addAll(getInstance().getWordList());
         Collections.sort(wordList,OrderBeanComparator.getInstance());


    }

   public void updateFlag(){
       flag = false;
   }

    public void createWord(){
        this.flag = true;
        selectedWord = new Word();
        selectedWord.setWordCategory(getInstance());
        selectedWord.setEnable(true);

        int priority=1;
        for(Word word:wordList){
            if(word.getPriority()>=priority){
                priority = word.getPriority()+1;
            }


        }
        selectedWord.setPriority(priority);
    }

    public void joinNewWord(){
        getInstance().getWords().add(selectedWord);
        this.update();
        wordCategorySelected();


    }


    public void upWord(){
        OrderBeanComparator.up(selectedWord,wordList);
    }
    public void downWord(){
        OrderBeanComparator.down(selectedWord,wordList);
    }
}
