import javax.print.*;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.HashAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaTray;
import javax.print.attribute.standard.PrinterName;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.io.Console;
import java.util.HashMap;
import java.util.Map;


public class Main {

    public static void main(String args[]) {

        // get default printer
        PrintService defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();

        // suggest the use of the default printer
        System.out.println("Write the printer name or press enter for [" + defaultPrintService.getName() + "]");

        Console console = System.console();

        // read from the console the name of the printer
        String printerName = console.readLine();

        // if there is no input, use the default printer
        if (printerName == null || printerName.equals("")) {
            printerName = defaultPrintService.getName();
        }


        // the printer is selected
        AttributeSet aset = new HashAttributeSet();
        aset.add(new PrinterName(printerName, null));

        // selection of all print services
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, aset);
        // we store all the tray in a hashmap
        Map<Integer, Media> trayMap = new HashMap<Integer, Media>(10);

        // we chose something compatible with the printable interface
        DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;

        for (PrintService service : services) {
            System.out.println(service);

            // we retrieve all the supported attributes of type Media
            // we can receive MediaTray, MediaSizeName, ...
            Object o = service.getSupportedAttributeValues(Media.class, flavor, null);
            if (o != null && o.getClass().isArray()) {
                for (Media media : (Media[]) o) {
                    // we collect the MediaTray available
                    if (media instanceof MediaTray) {
                        System.out.println(media.getValue() + " : " + media + " - " + media.getClass().getName());
                        trayMap.put(media.getValue(), media);
                    }
                }
            }
        }

        System.out.println("Select tray target id : ");
        String mediaId = console.readLine();

        MediaTray selectedTray = (MediaTray) trayMap.get(Integer.valueOf(mediaId));
        System.out.println("Selected tray : " + selectedTray.toString());

        System.out.println("Do you want to print a test page? [y/n]");
        String printPage = console.readLine();
        if (printPage.equalsIgnoreCase("Y")) {

            PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
            attributes.add(selectedTray);

            DocPrintJob job = services[0].createPrintJob();
            try {
                System.out.println("Trying to print an empty page on : " + selectedTray.toString());

                Doc doc = new SimpleDoc(new PrintableDemo(), DocFlavor.SERVICE_FORMATTED.PRINTABLE, null);
                // print
                job.print(doc, attributes);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    static class PrintableDemo implements Printable {

        @Override
        public int print(Graphics pg, PageFormat pf, int pageNum) {
            // we print an empty page
            if (pageNum >= 1)
                return Printable.NO_SUCH_PAGE;
            pg.drawString("", 10, 10);
            return Printable.PAGE_EXISTS;
        }
    }


}
