package top.codechap.utils;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.Map;

/**
 * @author CodeChap
 * @date 2021-06-05 16:59
 * @description ExcelOutPut
 */
@Data
@NoArgsConstructor
public class ExcelOutput {

    private String fileName;
    private double[][] twoData;
    Map<String,double[][]> inputData;

    public boolean fileExist(){
        boolean flag = false;
        File file = new File(fileName);
        flag = file.exists();
        return flag;
    }

    private XSSFWorkbook createXSSFWorkbook() {
        XSSFWorkbook workbook =  null;
        BufferedOutputStream outputStream = null;
        try {
            File fileXlsxPath = new File(fileName);
            outputStream = new BufferedOutputStream(new FileOutputStream(fileXlsxPath));
            workbook = new XSSFWorkbook();
            workbook.createSheet("系数矩阵");
            workbook.createSheet("b");
            workbook.createSheet("Qn");
            workbook.createSheet("Qout");
            workbook.createSheet("Hn");
            workbook.createSheet("Hout");
            workbook.write(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(outputStream!=null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return workbook;
    }

    private XSSFWorkbook getWorkbook () {
        XSSFWorkbook workbook = null;
        File file = new File(fileName);
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(file);
            workbook = new XSSFWorkbook(inputStream);
        } catch (FileNotFoundException e1) {
            System.out.println("找不到文件！");
        } catch (IOException e2) {
            System.out.println("文件打开出错！");
        }
        return workbook;
    }

    public void writeTwoData2Excel() {
        SXSSFWorkbook sxssfWorkbook;
        BufferedOutputStream outputStream = null;
        try {
            XSSFWorkbook xssfWorkbook = createXSSFWorkbook();
            sxssfWorkbook = new SXSSFWorkbook(xssfWorkbook, 100);

            for (Map.Entry<String,double[][]> data : inputData.entrySet()) {
                SXSSFSheet sheet = sxssfWorkbook.getSheet(data.getKey());
                double[][] value = data.getValue();
                for (int i = 0; i < value.length; i++) {
                    SXSSFRow row = sheet.createRow(i);
                    for (int j = 0; j < value[i].length; j++) {
                        row.createCell(j).setCellValue(value[i][j]);
                    }
                }
            }
            outputStream = new BufferedOutputStream(new FileOutputStream(fileName));
            sxssfWorkbook.write(outputStream);
            outputStream.flush();
            sxssfWorkbook.dispose();// 释放workbook所占用的所有windows资源
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
