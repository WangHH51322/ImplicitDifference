package top.codechap.utils;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

/**
 * @author Qing
 * @date 2020/10/30
 */
public class ExcelInput {
    /**
     * 文件名
     */
    private String fileName ;

    /**
     * 文件绝对路径
     */
    private String absolutePath;

    /**
     * 空构造方法
     */
    public ExcelInput() {}

    /**
     *
     * @param fileName
     */
    public ExcelInput(String fileName) {
        this.fileName = fileName;
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

    private Map<String, List> getData (XSSFWorkbook workbook) {
        int sheetCount = workbook.getNumberOfSheets();
//        System.out.println("sheetCount = " + sheetCount);
        Map<String, List> data = new HashMap<>();
        for (int i = 0; i < sheetCount; i++) {
//            System.out.println("sheetCount = " + i);
            //列数
            int columnCount = workbook.getSheetAt(i).getRow(0).getPhysicalNumberOfCells();
//            System.out.println("columnCount = " + columnCount);
            //行数
            int rowCount = workbook.getSheetAt(i).getLastRowNum() + 1;
//            System.out.println("rowCount = " + rowCount);
            //工作簿名称
            String string = workbook.getSheetName(i);

            List<List<String>> var1 = new LinkedList<>();
            List<String> var2 = new LinkedList<>();
            String var3 = null;

            for (int j = 1; j < rowCount; j++) {    //从excel的第二行开始读取数据
                for (int k = 0; k < columnCount; k++) {
                    //获取单元格的值
                    var3 = String.valueOf(workbook.getSheetAt(i).getRow(j).getCell(k));
                    var2.add(k, var3);
                }
                var1.add(new ArrayList<>(var2));
                var2.clear();
            }
            data.put(string, new ArrayList<>(var1));
            var1.clear();
        }
        return data;
    }

    public Map read () {
        return getData(getWorkbook());
    }

    public void setFileName (String fileName) {
        this.fileName = fileName;
    }

    public void setAbsolutePath (String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getFileName () {
        return this.fileName;
    }

    public String getAbsolutePath () {
        return this.absolutePath;
    }
}
