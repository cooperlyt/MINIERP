package com.dgsoft.common;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by cooper on 12/7/14.
 */
public interface ExportRender {

    public enum Type{
        DATA,HEADER,FOOTER;
    }

    public void cell(int row, int col, int toRow, int toCol, String value);
    public void cell(int row, int col, int toRow, int toCol, Date value);
    public void cell(int row, int col, int toRow, int toCol, double value) ;
    public void cell(int row, int col, int toRow, int toCol, Calendar value) ;
    public void cell(int row, int col, int toRow, int toCol, boolean value) ;

    public void cell(int row, int col, String value) ;
    public void cell(int row, int col, Date value) ;
    public void cell(int row, int col, double value);
    public void cell(int row, int col, Calendar value);
    public void cell(int row, int col, boolean value) ;

    //public void setNextRowType(Type type, int level);

    public void setNextCellType(Type type, int level);

    public void setNextRowType(Type type,int level);



    public void write(OutputStream outputStream) throws IOException;

}
