/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.saitel.api.util.firma;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import ec.saitel.api.util.Addons;
import ec.saitel.api.util.Fecha;
import java.io.File;
import java.io.FileOutputStream;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author PC-ON
 */
public class FirmasPdf {

    public FirmasPdf() {

    }

    public static String ModeloFirmasArqueo(HttpServletResponse response, String _dir, String id, String usuario) {
        try {
            response.setContentType("application/pdf");
            File documento = new File(_dir + "/" + usuario + Fecha.getFecha("ISO").replaceAll("-", "") + Fecha.getHora().replaceAll(":", "") + ".pdf");
            Document doc = new Document(PageSize.A4);// paso 1
            try {
                PdfWriter.getInstance(doc, new FileOutputStream(documento));
            } catch (Exception e) {

            }
            doc.open();
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph(" "));
            PdfPTable tbl_firmas = new PdfPTable(3);
            tbl_firmas.addCell(Addons.setCeldaPDF("RESPONSABLE", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
            tbl_firmas.addCell(Addons.setCeldaPDF("VERIFICADO", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
            tbl_firmas.addCell(Addons.setCeldaPDF("AUTORIZADO", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
            doc.add(tbl_firmas);
            doc.close();
            return documento.getName();
        } catch (Exception e) {
            return null;
        }
    }

}
