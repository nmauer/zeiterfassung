package de.nmauer.utils;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.server.StreamResource;
import de.nmauer.data.entity.timeMapping.WorkingHour;
import de.nmauer.data.service.timeTracking.WorkingHourService;
import net.sf.jasperreports.engine.export.ooxml.XlsxWorkbookHelper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.List;

public class Exporter {

    private WorkingHourService workingHourService;

    public Exporter(WorkingHourService workingHourService) {
        this.workingHourService = workingHourService;
    }

    public void export(int user_id, int month, int year){
        List<WorkingHour> workingHours = workingHourService.getWorkingHourByUserId(user_id, month, year);


        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Stundenübersicht");
        sheet.setColumnWidth(1, 5000);
        sheet.setColumnWidth(2, 5000);
        sheet.setColumnWidth(3, 5000);
        sheet.setColumnWidth(4, 5000);
        sheet.setColumnWidth(5, 5000);

//        CellStyle headerStyle = ((XSSFWorkbook)workbook).createCellStyle();
//        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
//
//        CellStyle rowStyle = ((XSSFWorkbook)workbook).createCellStyle();
//        rowStyle.setFillBackgroundColor(IndexedColors.LIGHT_BLUE.getIndex());

        Row header = sheet.createRow(1);

        Cell headerCell = header.createCell(1);
        headerCell.setCellValue("Datum");
//        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(2);
        headerCell.setCellValue("Begin");
//        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(3);
        headerCell.setCellValue("Ende");
//        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(4);
        headerCell.setCellValue("Stunden");
//        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(5);
        headerCell.setCellValue("Pause");
//        headerCell.setCellStyle(headerStyle);

        int i = 2;
        for(WorkingHour workingHour: workingHours){
            Row dataRow = sheet.createRow(i);

            Cell dataCell = dataRow.createCell(1);
            dataCell.setCellValue(workingHour.getDate());
//            if(i%2==0)
//                dataCell.setCellStyle(rowStyle);

            dataCell = dataRow.createCell(2);
            dataCell.setCellValue(workingHour.getBeginFormatted());
//            if(i%2==0)
//                dataCell.setCellStyle(rowStyle);

            dataCell = dataRow.createCell(3);
            dataCell.setCellValue(workingHour.getEndFormatted());
//            if(i%2==0)
//                dataCell.setCellStyle(rowStyle);

            dataCell = dataRow.createCell(4);
            dataCell.setCellValue(workingHour.getWorkingTimeFormatted());
//            if(i%2==0)
//                dataCell.setCellStyle(rowStyle);

            dataCell = dataRow.createCell(5);
            dataCell.setCellValue(workingHour.getPauseTime());
//            if(i%2==0)
//                dataCell.setCellStyle(rowStyle);

            i++;
        }

        File currDirFile = new File(".");
        String path = currDirFile.getAbsolutePath();
        String fileLocation = path.substring(0, path.length() -1) + "export.xlsx";

        try {
            FileOutputStream outputStream = new FileOutputStream(fileLocation);
            workbook.write(outputStream);
            workbook.close();

            File file = new File(fileLocation);
            StreamResource streamResource = new StreamResource(file.getName(), () -> getStream(file));
            Anchor anchor = new Anchor(streamResource, "Export");
            anchor.getElement().setAttribute("download", true);
            UI.getCurrent().getPage().executeJs("$0.click()", anchor.getElement());
        } catch (IOException e) {
            Notification.show("Es ist ein Fehler aufgetreten. Bitte kontaktieren Sie einen Administrator");
            e.printStackTrace();
        }

    }
    private InputStream getStream(File file) {
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return stream;
    }

}
