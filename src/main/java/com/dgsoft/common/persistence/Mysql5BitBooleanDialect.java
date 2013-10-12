package com.dgsoft.common.persistence;

import org.hibernate.dialect.MySQL5Dialect;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 5/3/13
 * Time: 3:37 PM
 */
public class Mysql5BitBooleanDialect extends MySQL5Dialect {


    public Mysql5BitBooleanDialect() {
        super();
        registerColumnType( java.sql.Types.BOOLEAN, "bit" );
    }

}
