package com.dgsoft.erp.model;

import com.dgsoft.common.utils.finance.CertificateItem;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 14-5-14
 * Time: 上午11:16
 */
@Entity
@Table(name = "SALE_PREPARED", catalog = "MINI_ERP")
public class SaleCertificate implements Serializable {

    private String id;
    private String word;
    private int code;
    private Date date;
    private String preparedEmp;
    private String approvedEmp;
    private String checkedEmp;
    private String cashier;
    private String memo;
    private Integer vouchersCount;

    private Set<AccountOper> accountOpers = new HashSet<AccountOper>(0);
    private Set<MoneySave> moneySaves = new HashSet<MoneySave>(0);


    public SaleCertificate() {
    }

    public SaleCertificate(String word, int code, Date date, String preparedEmp) {
        this.word = word;
        this.code = code;
        this.date = date;
        this.preparedEmp = preparedEmp;
    }

    public SaleCertificate(String word, int code, Date date, String preparedEmp,
                           String approvedEmp, String checkedEmp, String cashier, String memo, Integer vouchersCount) {
        this.word = word;
        this.code = code;
        this.date = date;
        this.preparedEmp = preparedEmp;
        this.approvedEmp = approvedEmp;
        this.checkedEmp = checkedEmp;
        this.cashier = cashier;
        this.memo = memo;
        this.vouchersCount = vouchersCount;
    }

    @Id
    @Column(name = "ID", unique = true, nullable = false, length = 32)
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid.hex")
    @NotNull
    @Size(max = 32)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "_WORD", nullable = false, length = 10)
    @NotNull
    @Size(max = 10)
    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    @Column(name = "_CODE", nullable = false)
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CERTIFICATE_DATE", nullable = false, length = 19)
    @NotNull
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Column(name = "PREPARED_EMP", nullable = true, length = 32)
    @Size(max = 32)
    public String getPreparedEmp() {
        return preparedEmp;
    }

    public void setPreparedEmp(String preparedEmp) {
        this.preparedEmp = preparedEmp;
    }

    @Column(name = "APPROVED_EMP", nullable = true, length = 32)
    @Size(max = 32)
    public String getApprovedEmp() {
        return approvedEmp;
    }

    public void setApprovedEmp(String approvedEmp) {
        this.approvedEmp = approvedEmp;
    }

    @Column(name = "CHECKED_EMP", nullable = true, length = 32)
    @Size(max = 32)
    public String getCheckedEmp() {
        return checkedEmp;
    }

    public void setCheckedEmp(String checkedEmp) {
        this.checkedEmp = checkedEmp;
    }

    @Column(name = "CASHIER", nullable = true, length = 32)
    @Size(max = 32)
    public String getCashier() {
        return cashier;
    }

    public void setCashier(String cashier) {
        this.cashier = cashier;
    }

    @Column(name = "MEMO", nullable = true, length = 200)
    @Size(max = 200)
    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @Column(name = "VOUCHERS_COUNT", nullable = true)
    public Integer getVouchersCount() {
        return vouchersCount;
    }

    public void setVouchersCount(Integer vouchersCount) {
        this.vouchersCount = vouchersCount;
    }

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "saleCertificate")
    public Set<AccountOper> getAccountOpers() {
        return accountOpers;
    }

    public void setAccountOpers(Set<AccountOper> accountOpers) {
        this.accountOpers = accountOpers;
    }

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "saleCertificate")
    public Set<MoneySave> getMoneySaves() {
        return moneySaves;
    }

    public void setMoneySaves(Set<MoneySave> moneySaves) {
        this.moneySaves = moneySaves;
    }

    @Transient
    public List<CertificateItem> getCertificateItems(){
        if (getAccountOpers().isEmpty()){
            throw new IllegalArgumentException("getAccountOpers() is empty");
        }
        List<CertificateItem> result = new ArrayList<CertificateItem>();
        for (MoneySave moneySave: getMoneySaves()){
            result.addAll(moneySave.getCertificateItems());
        }
        for (AccountOper accountOper: getAccountOpers()){
            result.addAll(accountOper.getCertificateItems());
        }
        return result;

    }
}
