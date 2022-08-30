package utils;

import exceptions.SystemError;
import javafx.scene.control.Alert;
import main.AppSettings;
import main.Main;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;
import scenes.fxml.FMXLResourcesLoader;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.PrinterName;
import java.awt.print.PageFormat;
import java.util.ArrayList;

public class TreasurySummaryPrint {

    public static class TreasurySummary {
        String username;
        String treasuryTotal;
        String creditTotal;
        String fromDatetime;
        String toDateTime;
        String printDate;

        public TreasurySummary(String username, Float treasuryTotal, Float creditTotal, DateTime fromDatetime, DateTime toDateTime, DateTime printDate) {
            this.username = username;
            if (treasuryTotal >= 0)
                this.treasuryTotal = treasuryTotal + " وارد ";
            else
                this.treasuryTotal = treasuryTotal*-1 + " صادر ";

            if (creditTotal >= 0)
                this.creditTotal = creditTotal + " وارد ";
            else
                this.creditTotal = creditTotal*-1 + " صادر ";


            this.fromDatetime = fromDatetime.toString();
            this.toDateTime = toDateTime.toString();
            this.printDate = printDate.toString();
        }

        public String getUsername() {
            return username;
        }

        public String getTreasuryTotal() {
            return treasuryTotal;
        }

        public String getCreditTotal() {
            return creditTotal;
        }

        public String getFromDatetime() {
            return fromDatetime;
        }

        public String getToDateTime() {
            return toDateTime;
        }

        public String getPrintDate() {
            return printDate;
        }
    }


    private TreasurySummary treasurySummary;

    public TreasurySummaryPrint(TreasurySummary treasurySummary) {
        this.treasurySummary = treasurySummary;
    }

    public void print() throws Exception {
        if (AppSettings.getInstance().getPrinterName() == null)
            throw new SystemError(SystemError.ErrorType.printerError);
        ArrayList<TreasurySummary> jasperData = new ArrayList<>();
        jasperData.add(treasurySummary);
        JasperDesign jasperDesign = FMXLResourcesLoader.getJrxml("treasuryData.jrxml");
        JasperReport report = JasperCompileManager.compileReport(jasperDesign);
        JRBeanCollectionDataSource DataSource = new JRBeanCollectionDataSource(jasperData);
        JasperPrint jasperPrint = JasperFillManager.fillReport(report, null, DataSource);
        PrintReportToPrinter(jasperPrint, AppSettings.getInstance().getPrinterName());


    }

    private void PrintReportToPrinter(JasperPrint jp, String printerNameShort) throws Exception {


        JasperPrint jasperPrint = jp;

        java.awt.print.PrinterJob printerJob = java.awt.print.PrinterJob.getPrinterJob();

        PageFormat pageFormat = java.awt.print.PrinterJob.getPrinterJob().defaultPage();
        printerJob.defaultPage(pageFormat);

        int selectedService = 0;

        AttributeSet attributeSet = new HashPrintServiceAttributeSet(new PrinterName(printerNameShort, null));

        PrintService[] printService = PrintServiceLookup.lookupPrintServices(null, attributeSet);


        printerJob.setPrintService(printService[selectedService]);


        JRPrintServiceExporter exporter;
        PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
        // printRequestAttributeSet.add(MediaSizeName.);
        printRequestAttributeSet.add(new Copies(1));

        // these are deprecated
        exporter = new JRPrintServiceExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE, printService[selectedService]);
        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, printService[selectedService].getAttributes());
        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttributeSet);
        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);
        exporter.exportReport();

    }
}