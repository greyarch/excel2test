package io.testx.excel2test.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.format.CellDateFormatter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 *
 * @author gantcho
 */
public class TestScriptFactory {

    private FormulaEvaluator evaluator;
    private final String stepdefPackage;

    public TestScriptFactory(String stepdefPackage) {
        this.stepdefPackage = stepdefPackage;
    }

    public TestScript createFromExcelSheet(InputStream source, String sheetName) throws IOException, InvalidFormatException {
        TestScript result = new TestScript();
        Workbook wb = WorkbookFactory.create(source);
        evaluator = wb.getCreationHelper().createFormulaEvaluator();
        Sheet sheet = wb.getSheet(sheetName);
        for (Row row : sheet) {
            String firstCellValue = getCellValue(row.getCell(0));

            if (StringUtils.isNotEmpty(firstCellValue)) {
                result.addStep(createTestStep(firstCellValue, row.getRowNum(), sheet));
            }
        }
        return result;
    }

    private TestScriptStep createTestStep(String keyword, int row, Sheet sheet) throws IOException {
        TestScriptStep step = new TestScriptStep(removeComment(keyword));
        if (sheet.getRow(row - 1) != null) { //if row has never been used then skip the FOR loop
            for (Cell cell : sheet.getRow(row - 1)) { //traverse the argument names row
                String argName = getCellValue(cell);
                if (StringUtils.isNotEmpty(argName)) {
                    String arg = getCellValue(sheet.getRow(row), cell.getColumnIndex());
                    if (!arg.equalsIgnoreCase("<leeg>")) {
                        step.addArgument(argName, arg);
                    }
                }
            }
            step.addMetaItem("Row", Integer.toString(row + 1));
            step.addMetaItem("Full name", keyword);
            step.addMetaItem("Comment", getComment(keyword));
        }
        return step;
    }

    private String getCellValue(Row row, int index) {
        return getCellValue(row.getCell(index));
    }

    private String getCellValue(Cell cell) {
        if (cell != null) {
            evaluator.evaluateInCell(cell);
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_STRING:
                    return cell.getRichStringCellValue().getString();
                case Cell.CELL_TYPE_NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        Date date = HSSFDateUtil.getJavaDate(cell.getNumericCellValue());
                        String dateFmt = cell.getCellStyle().getDataFormatString().replaceAll("/", "-"); //bug in PoI
                        return new CellDateFormatter(dateFmt).format(date);
                    } else {
                        return Double.toString(cell.getNumericCellValue());
                    }
                case Cell.CELL_TYPE_BOOLEAN:
                    return Boolean.toString(cell.getBooleanCellValue());
                case Cell.CELL_TYPE_FORMULA: //should never happen
                    return "UNHANDLED FORMULA FOUND, SHOULD NOT HAPPEN!";
                default:
                    return "";
            }
        }
        return "";
    }

    private String removeComment(String keyword) {
        if (keyword.contains("[")) {
            return keyword.substring(0, keyword.indexOf("[")).trim();
        } else {
            return keyword;
        }
    }

    private String getComment(String keyword) {
        if (keyword.contains("[")) {
            int start = keyword.indexOf("[");
            if (keyword.contains("]") && keyword.lastIndexOf("]") > start) {
                return keyword.substring(start + 1, keyword.lastIndexOf("]")).trim();
            } else {
                return "";
            }
        } else {
            return "";
        }
    }
}
