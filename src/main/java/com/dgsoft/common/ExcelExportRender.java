package com.dgsoft.common;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.jboss.seam.log.Logging;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cooper on 12/7/14.
 */
public class ExcelExportRender implements ExportRender {

    private Workbook workbook;

    private Sheet sheet;

    private Type type = Type.DATA;

    private Type rowType = Type.DATA;

    private int rowLevel = 0;

    private int level = 0;

    private Map<Type,Map<Integer,CellStyle>> cellStyles = new HashMap<Type, Map<Integer, CellStyle>>();

    public ExcelExportRender(String title) {
        workbook = new HSSFWorkbook();
        sheet = workbook.createSheet("title");
    }

    private CellStyle getCellStyle(){
        return getStyle(type,level);
    }

    public CellStyle getRowStyle(){
        return getStyle(rowType,rowLevel);
    }

    private CellStyle getStyle(Type type,int level){

        CellStyle result = null;
        Map<Integer,CellStyle> levelStyles = cellStyles.get(type);
        if (levelStyles != null){
            result = levelStyles.get(level);
        }else{
            levelStyles = new HashMap<Integer,CellStyle>();
            cellStyles.put(type,levelStyles);
        }
        if (result == null) {
            result =workbook.createCellStyle();
            if (!type.equals(Type.DATA)) {
                result.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                result.setVerticalAlignment(HSSFCellStyle.ALIGN_CENTER);
                Font font = workbook.createFont();
                //font.setBoldweight((short) (10 - level));

                if (type.equals(Type.FOOTER)){
                    //Logging.getLog(getClass()).debug("setBackground level:" + level);
                    switch (level) {
                        case 0:
                            font.setColor(HSSFColor.GREY_50_PERCENT.index);
                            //result.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
                            break;
                        case 1:
                            font.setColor(HSSFColor.GREY_40_PERCENT.index);
                            //result.setFillBackgroundColor(HSSFColor.GREY_40_PERCENT.index);
                            break;
                        default:
                            font.setColor(HSSFColor.GREY_25_PERCENT.index);
                            //result.setFillBackgroundColor(HSSFColor.GREY_25_PERCENT.index);
                            break;

                    }
                    //result.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                }
                result.setFont(font);
            }


            levelStyles.put(level,result);
        }
        return result;

    }

    private Row getRow(int index) {
        Row result = sheet.getRow(index);
        if (result == null) {
            result = sheet.createRow(index);
            result.setRowStyle(getRowStyle());
        }
        return result;
    }

    @Override
    public void cell(int row, int col, int toRow, int toCol, String value) {
        cell(row,col,value);
        sheet.addMergedRegion(new CellRangeAddress(row,toRow,col,toCol));
    }

    @Override
    public void cell(int row, int col, int toRow, int toCol, Date value) {
        cell(row,col,value);
        sheet.addMergedRegion(new CellRangeAddress(row,toRow,col,toCol));
    }

    @Override
    public void cell(int row, int col, int toRow, int toCol, double value) {
        cell(row,col,value);
        sheet.addMergedRegion(new CellRangeAddress(row,toRow,col,toCol));
    }

    @Override
    public void cell(int row, int col, int toRow, int toCol, Calendar value) {
        cell(row,col,value);
        sheet.addMergedRegion(new CellRangeAddress(row,toRow,col,toCol));
    }

    @Override
    public void cell(int row, int col, int toRow, int toCol, boolean value) {
        cell(row, col, value);
        sheet.addMergedRegion(new CellRangeAddress(row, toRow, col, toCol));
    }


    @Override
    public void cell(int row, int col, String value) {
        Cell cell = getRow(row).createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(getCellStyle());
    }

    @Override
    public void cell(int row, int col, Date value) {
        Cell cell = getRow(row).createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(getCellStyle());
    }

    @Override
    public void cell(int row, int col, double value) {
        Cell cell = getRow(row).createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(getCellStyle());
    }

    @Override
    public void cell(int row, int col, Calendar value) {
        Cell cell = getRow(row).createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(getCellStyle());
    }

    @Override
    public void cell(int row, int col, boolean value) {
        Cell cell = getRow(row).createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(getCellStyle());
    }

    @Override
    public void setNextCellType(Type type, int level) {
        this.type = type;
        this.level = level;
    }

    @Override
    public void setNextRowType(Type type, int level) {
        this.rowLevel = level;
        this.rowType = type;
    }


    @Override
    public void write(OutputStream outputStream) throws IOException {
        workbook.write(outputStream);
    }

}
