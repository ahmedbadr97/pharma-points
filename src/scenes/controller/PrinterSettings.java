package scenes.controller;

import exceptions.SystemError;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import main.Main;
import scenes.images.ImageLoader;
import scenes.main.Alerts;

public class PrinterSettings {

    @FXML
    private TextField primaryPrinterTf;

    @FXML
    private Button primPrinterSett_Btn;


    scenes.abstracts.PrinterSettings main_scenes;
    PrinterJob primaryPjob;
    PrinterJob secondaryPjob;
    Printer primaryPrinter;
    public  void ini(scenes.abstracts.PrinterSettings main_scenes)
    {
        this.main_scenes =main_scenes;
        ImageLoader.icoButton(primPrinterSett_Btn,"settings_white.png",15);
        primaryPjob=secondaryPjob=null;
        if(Main.appSettings.getPrinterName()!=null)
        {
            primaryPrinterTf.setText(Main.appSettings.getPrinterName());
        }
    }
    @FXML
    void Open_primPrinterSett(ActionEvent event) {
        primaryPjob= primaryPjob==null ?PrinterJob.createPrinterJob():primaryPjob;
        primaryPjob.showPrintDialog(main_scenes.getStage());
        Printer printer=primaryPjob.getPrinter();
        if(printer==null)
            new Alerts("من فضلك قم باختيار الطبهه",Alert.AlertType.ERROR)
            {
                @Override
                public void alertOnClose() {
                    Open_primPrinterSett(null);
                }
            };
        else {
            primaryPrinterTf.setText(primaryPjob.getPrinter().getName());
            primaryPrinter=primaryPjob.getPrinter();
        }
    }

    @FXML
    void cancel(ActionEvent event) {
        main_scenes.closeStage();
    }
    @FXML
    void save(ActionEvent event) {
        if(primaryPrinter!=null)
        {
            try {
                Main.appSettings.setPrinterName(primaryPrinter.getName());
            } catch (SystemError e) {
                new Alerts(e.getMessage(), Alert.AlertType.ERROR);
            }
        }
        new Alerts("تم حفظ البيانات بنجاح", Alert.AlertType.INFORMATION);
    }
}
