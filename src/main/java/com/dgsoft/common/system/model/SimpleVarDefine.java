package com.dgsoft.common.system.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 6/18/13
 * Time: 10:28 AM
 */
@Entity
@Table(name = "SIMPLE_VAR_DEFINE", catalog = "DG_SYSTEM")
public class SimpleVarDefine {


    public enum VarType {
        STRING, INTEGER, FLOAT, DOUBLE,
        DATE, DATETIME, BOOLEAN, SHORT_MONEY,
        MONEY, AREA, WORD;
    }

    private String id;
    private String title;
    private int length;
    private VarType type;
    private String description;
    private WordCategory wordCategory;

    public SimpleVarDefine() {
        super();
    }

    @Id
    @Column(name = "ID", unique = true, nullable = false, length = 32)
    @NotNull
    @Size(max = 32)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "TITLE", nullable = false, length = 50)
    @NotNull
    @Size(max = 50)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "LENGTH", nullable = false)
    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    @Column(name = "TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    public VarType getType() {
        return type;
    }

    public void setType(VarType type) {
        this.type = type;
    }

    @Column(name = "DESCRIPTION", nullable = true, length = 200)
    @Size(max = 200)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WORD_TYPE", nullable = true)
    public WordCategory getWordCategory() {
        return wordCategory;
    }

    public void setWordCategory(WordCategory wordCategory) {
        this.wordCategory = wordCategory;
    }
}
