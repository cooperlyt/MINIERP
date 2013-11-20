package com.dgsoft.common.system;

import com.dgsoft.common.OrderBeanComparator;
import com.dgsoft.common.system.model.*;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.log.Log;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 8/20/13
 * Time: 7:47 AM
 */
@Name("dictionary")
@Scope(ScopeType.APPLICATION)
@Synchronized
@AutoCreate
@Startup
public class DictionaryWord {

    //private Map<String, List<Word>> wordsCache = new HashMap<String, List<Word>>();

    private Map<String, Word> wordCache;

    private Map<String, WordCategory> wordCategory;

    private Map<String, Employee> employeeMap;

    private Map<Integer, City> cityMap;

    @Create
    @Transactional
    public void load() {
        loadWord();
        loadEmp();
        loadCity();
    }


    public void loadCity() {
        cityMap = new HashMap<Integer, City>();
        List<City> citys = systemEntityLoader.getPersistenceContext().createQuery("select city from City city left join fetch city.province", City.class).getResultList();
        for (City city : citys) {
            cityMap.put(city.getId(), city);
        }
    }

    @Observer({"org.jboss.seam.afterTransactionSuccess.WordCategory", "org.jboss.seam.afterTransactionSuccess.Word"})
    public void loadWord() {
        wordCache = new HashMap<String, Word>();
        wordCategory = new HashMap<String, WordCategory>();
        List<WordCategory> result = systemEntityLoader.getPersistenceContext().createQuery("select wordCategory from WordCategory wordCategory left join fetch wordCategory.words").getResultList();
        for (WordCategory category : result) {
            wordCategory.put(category.getId(), category);
            for (Word word : category.getWords()) {
                wordCache.put(word.getId(), word);
            }
        }
    }

    @Observer("org.jboss.seam.afterTransactionSuccess.Employee")
    public void loadEmp() {
        employeeMap = new HashMap<String, Employee>();
        List<Employee> employees = systemEntityLoader.getPersistenceContext().createQuery("select emp from Employee emp join fetch emp.person").getResultList();
        for (Employee emp : employees) {
            employeeMap.put(emp.getId(), emp);
        }
    }

    @In(create = true)
    private SystemEntityLoader systemEntityLoader;

    public List<Word> getWordList(String categoryId) {
        List<Word> result = new ArrayList<Word>();
        for (Word word : wordCategory.get(categoryId).getWordList()) {
            if (word.isEnable()) {
                result.add(word);
            }
        }
        return result;
    }

    public String getWordCategory(String categoryId) {
        if (categoryId == null || "".equals(categoryId.trim())) {
            return "";
        }

        WordCategory resultCategory = wordCategory.get(categoryId);
        if (resultCategory == null) {
            return "";
        } else
            return resultCategory.getName();
    }


    public Word getWord(String wordId) {
        if (wordId == null || "".equals(wordId.trim()))
            return null;
        return wordCache.get(wordId);
    }

    public String getWordValue(String wordId) {
        if (wordId == null || "".equals(wordId.trim()))
            return "";
        Word word = getWord(wordId);
        if (word != null) {
            return word.getValue();
        } else
            return "";

    }


    public Employee getEmpById(String id) {
        return employeeMap.get(id);
    }

    public String getEmpNameById(String id) {
        Employee emp = employeeMap.get(id);
        if (emp == null) {
            return "invalid";
        } else
            return emp.getPerson().getName();

    }

    public City getCity(Integer code) {
        return cityMap.get(code);
    }

    public String getCityName(Integer code) {
        City city = getCity(code);
        if (city == null) {
            return "";
        } else {
            return city.getProvince().getName() + city.getName();
        }
    }

}
