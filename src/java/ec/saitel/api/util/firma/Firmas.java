/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.saitel.api.util.firma;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImage;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfIndirectObject;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSigLockDictionary;
import com.itextpdf.text.pdf.PdfSigLockDictionary.LockPermissions;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.security.BouncyCastleDigest;
import com.itextpdf.text.pdf.security.ExternalDigest;
import com.itextpdf.text.pdf.security.ExternalSignature;
import com.itextpdf.text.pdf.security.MakeSignature;
import com.itextpdf.text.pdf.security.MakeSignature.CryptoStandard;
import com.itextpdf.text.pdf.security.PrivateKeySignature;
import ec.saitel.api.util.Fecha;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFMergerUtility;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 *
 * @author PC-ON
 */
public class Firmas {
    
    public static String msg = "";
    public static File archivo = null;

    public Firmas() {

    }

    public static String FirmarPdf(String origen, String salida, String certificado, String clave, int x, int y, int ancho, int largo) throws IOException, DocumentException, KeyStoreException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, GeneralSecurityException {
        ///pdf original
        try {
            File fpdfOrigen = new File(origen);
            //nombre del pdf firmado
            File fpdfDestino = new File(salida);
            //certificado en formato p12 o pfx (debe contener llave privada, publica y certificado)
            File fContenedorp12 = new File(certificado);
            //clave del p12 o pfx
            String Contenedorp12clave = clave;
            //Se agrega bouncyCastle al provider de java, si no se realiza, arroja un error
            Provider p = new BouncyCastleProvider();

            Security.addProvider(p);

            //Se instancia un keystore de tipo pkcs12 para leer el contenedor p12 o pfx
            KeyStore ks = KeyStore.getInstance("pkcs12");
            //Se entrega la ruta y la clave del p12 o pfx

            ks.load(new FileInputStream(fContenedorp12.getAbsolutePath()), Contenedorp12clave.toCharArray());

            //Se obtiene el nombre del certificado
            String alias = (String) ks.aliases().nextElement();
            //Se obtiene la llave privada
            PrivateKey pk = (PrivateKey) ks.getKey(alias, Contenedorp12clave.toCharArray());
            //Se obtiene la cadena de certificados en base al nombre del certificado
            Certificate[] chain = ks.getCertificateChain(alias);
            //Se indica el origen del pdf a firmar
            PdfReader reader = new PdfReader(fpdfOrigen.getAbsolutePath());
            //Se indica el destino del pdf firmado
            PdfStamper stamper = PdfStamper.createSignature(reader, new FileOutputStream(fpdfDestino.getAbsolutePath()), '\0');
            //Se indican alguno detalles de la forma en que se firmara
            PdfSignatureAppearance appearance = stamper.getSignatureAppearance();

            X509Certificate mycert = (X509Certificate) ks.getCertificate(alias);
            String Certificado[] = mycert.getIssuerDN().toString().split(",");
            ///obetnemos datos del certificado
            String Firmado = "FIRMADO POR=" + alias.toUpperCase() + "\n";
            for (int i = 0; i < Certificado.length; i++) {
                Firmado += (Certificado[i].trim() + "\n");
            }
            Firmado += "FECHA Y HORA=" + Fecha.getFecha("ISO") + " " + Fecha.getHora() + "\n";
            //////agregramos a unlayout los datos del certificado
            Font font = new Font(Font.FontFamily.TIMES_ROMAN, 5, Font.NORMAL);
            appearance.setLayer2Font(font);
            appearance.setLayer2Text(Firmado);
            ////abucamos la firma
            appearance.setVisibleSignature(new Rectangle(x, y, ancho + x, largo + y), 1, null);
            ExternalSignature es = new PrivateKeySignature(pk, "SHA1", "BC");
            ExternalDigest digest = new BouncyCastleDigest();

            //Se genera la firma y se almacena el pdf como se indico en las lineas anteriores
            MakeSignature.signDetached(appearance, digest, es, chain, null, null, null, 0, CryptoStandard.CMS);

            //Se cierran las instancias para liberar espacio
            stamper.close();
            reader.close();
            return fpdfDestino.getName();
        } catch (Exception e) {
            return "";
        }
    }

    public static String SIGNING_REASON = "signingReason";
    public static String SIGNING_LOCATION = "signingLocation";
    public static String SIGN_TIME = "signTime";
    public static String LAST_PAGE = "0";
    public static String FONT_SIZE = "3";

    public static String FirmarPdf(String origen, String salida, String certificado, String clave, Properties xParams) {

        try {
            Provider p = new BouncyCastleProvider();

            Security.addProvider(p);
            KeyStore ks = KeyStore.getInstance("pkcs12");
            //Se entrega la ruta y la clave del p12 o pfx
            File fContenedorp12 = new File(certificado);
            ks.load(new FileInputStream(fContenedorp12.getAbsolutePath()), clave.toCharArray());

            //Se obtiene el nombre del certificado
            String alias = (String) ks.aliases().nextElement();
            //Se obtiene la llave privada
            PrivateKey key = (PrivateKey) ks.getKey(alias, clave.toCharArray());
            //Se obtiene la cadena de certificados en base al nombre del certificado
            Certificate[] certChain = ks.getCertificateChain(alias);

            Properties extraParams = xParams != null ? xParams : new Properties();
            // Motivo de la firma
            String reason = extraParams.getProperty(SIGNING_REASON);
            // Lugar de realizacion de la firma
            String location = extraParams.getProperty(SIGNING_LOCATION);
            // Fecha y hora de la firma, en formato ISO-8601
            String signTime = extraParams.getProperty(SIGN_TIME);

            float fontSize = 3;
            if (extraParams.getProperty(FONT_SIZE) == null) {
                fontSize = 3;
            } else {
                fontSize = Float.parseFloat(extraParams.getProperty(FONT_SIZE).trim());
            }

            // Tamaño espaciado
            float fontLeading = fontSize;

            File fpdfOrigen = new File(origen);
            File fpdfDestino = new File(salida);
            PdfReader pdfReader = new PdfReader(fpdfOrigen.getAbsolutePath());
            PdfStamper stp = PdfStamper.createSignature(pdfReader, new FileOutputStream(fpdfDestino.getAbsolutePath()), '\0', null, true);
            PdfSignatureAppearance sap = stp.getSignatureAppearance();
            ////////////////////

            PdfSigLockDictionary lockDic = null;
            lockDic = new PdfSigLockDictionary(LockPermissions.NO_CHANGES_ALLOWED);
            stp.getWriter().addToBody(lockDic).getIndirectReference();

            ////////////////
            // Pagina donde situar la firma visible
            int page = 0;
            if (extraParams.getProperty(LAST_PAGE) == null) {
                page = 0;
            }
            if (extraParams.getProperty(LAST_PAGE).compareTo("-1") == 0) {
                page = pdfReader.getNumberOfPages();
            } else {
                page = Integer.parseInt(extraParams.getProperty(LAST_PAGE).trim());
            }
            sap.setAcro6Layers(true);

            // Razon de firma
            if (reason != null) {
                sap.setReason(reason);
            }

            // Localizacion en donde se produce la firma
            if (location != null) {
                sap.setLocation(location);
            }

            // Fecha y hora de la firma
            try {
                GregorianCalendar calendar = new GregorianCalendar();
                if (signTime != null) {
                    Date date = Utils.getSignTime(signTime);
                    calendar.setTime(date);
                }
                sap.setSignDate(calendar);
            } catch (Exception e) {

            }
            if (page == 0 || page < 0 || page > pdfReader.getNumberOfPages()) {
                page = pdfReader.getNumberOfPages();
            }
            Rectangle signaturePositionOnPage = getSignaturePositionOnPage(extraParams);
            if (signaturePositionOnPage != null) {
                ////////////
                int firmas = 0;
                AcroFields af = stp.getAcroFields();
                ArrayList<String> names = af.getSignatureNames();
                firmas = names.size();
                String newFieldName = "firma" + "_" + (firmas + 1);

                //////
                sap.setVisibleSignature(signaturePositionOnPage, page, newFieldName);

                X509Certificate x509Certificate = (X509Certificate) certChain[0];
                String informacionCertificado = x509Certificate.getSubjectDN().getName();
                String nombreFirmante = "";
                try {
                    nombreFirmante = (informacionCertificado.substring(informacionCertificado.lastIndexOf("CN=") + 3,
                            informacionCertificado.indexOf(","))).toUpperCase();
                } catch (Exception e) {
                    System.out.println("Otro modelo de certificado");
                    try {
                        nombreFirmante = (informacionCertificado.substring(informacionCertificado.lastIndexOf("CN=") + 3,
                                informacionCertificado.length())).toUpperCase();
                    } catch (Exception ex) {
                        System.out.println("Error general de versiones" + ex.getLocalizedMessage() + " " + e.getMessage());
                    }
                }
                // Creating the appearance for layer 0
                PdfTemplate pdfTemplate = sap.getLayer(0);
                float width = pdfTemplate.getBoundingBox().getWidth();
                float height = pdfTemplate.getBoundingBox().getHeight();
                pdfTemplate.rectangle(0, 0, width, height);

                // Creating the appearance for layer 2
                // Nombre Firmante
                PdfTemplate pdfTemplate1 = sap.getLayer(2);
                Font font1 = new Font(Font.FontFamily.TIMES_ROMAN, fontSize + (fontSize / 2), Font.NORMAL);
                Paragraph paragraph1 = new Paragraph(nombreFirmante.trim(), font1);
                paragraph1.setAlignment(Paragraph.ALIGN_RIGHT);
                ColumnText columnText1 = new ColumnText(pdfTemplate1);
                columnText1.setSimpleColumn(0, 0, (width / 2) - 1, height);
                columnText1.addElement(paragraph1);
                columnText1.go();
                // Segunda Columna
                PdfTemplate pdfTemplate2 = sap.getLayer(2);
                Font font2 = new Font(Font.FontFamily.TIMES_ROMAN, fontSize, Font.NORMAL);
                Paragraph paragraph2 = new Paragraph(fontLeading, "Nombre de reconocimiento "
                        + informacionCertificado.trim() + "\nRazón: " + reason + "\nFecha: " + Fecha.getFecha("iso") + " " + Fecha.getHora(), font2);
                paragraph2.setAlignment(Paragraph.ALIGN_LEFT);
                ColumnText columnText2 = new ColumnText(pdfTemplate2);
                columnText2.setSimpleColumn((width / 2) + 1, 0, width, height);
                columnText2.addElement(paragraph2);
                columnText2.go();

                //sap.setCrypto(key, certChain, null, PdfSignatureAppearance.WINCER_SIGNED);
                ExternalSignature es = new PrivateKeySignature(key, "SHA1", "BC");
                ExternalDigest digest = new BouncyCastleDigest();

                //Se genera la firma y se almacena el pdf como se indico en las lineas anteriores
                MakeSignature.signDetached(sap, digest, es, certChain, null, null, null, 0, CryptoStandard.CMS);
                stp.close();
                pdfReader.close();
                return fpdfDestino.getName();
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    private static Rectangle getSignaturePositionOnPage(Properties extraParams) {
        return PdfUtil.getPositionOnPage(extraParams);
    }

//    public static String FirmarPdf(String origen, String salida, String firma, int x, int y, int w, int h, int pagina) {
//
//        File fpdfOrigen = new File(origen);
//        File fpdfDestino = new File(salida);
//
//        try {
//            PdfReader pdfReader = new PdfReader(fpdfOrigen.getAbsolutePath());
//
//            PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(fpdfDestino.getAbsolutePath()));
//            try {
//                Image imagen = Image.getInstance(firma);
//
//                // for(int i=1; i<= pdfReader.getNumberOfPages(); i++){
//                PdfContentByte content = pdfStamper.getUnderContent(pagina);
//                imagen.scaleAbsolute(w, h);
//                imagen.setAbsolutePosition(x, y);
//
//                content.addImage(imagen);
//            } catch (Exception e) {
//
//            }
//            // }
//            pdfStamper.close();
//            pdfReader.close();
//            return fpdfDestino.getName();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//
//    }
    public static String FirmarPdf(String origen, String salida, String firma, int x, int y, int w, int h, int pagina) {

        File fpdfOrigen = new File(origen);
        File fpdfDestino = new File(salida);
        try {
            PdfReader reader = new PdfReader(fpdfOrigen.getAbsolutePath());
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(fpdfDestino.getAbsolutePath()));
            try {
                int firmas = 0;
                AcroFields af = stamper.getAcroFields();
                ArrayList<String> names = af.getSignatureNames();
                firmas = names.size();
                /////////////////////
                Image imagen = Image.getInstance(firma);
                imagen.scaleAbsolute(w, h);
                imagen.setAbsolutePosition(x, y);
                PdfImage stream = new PdfImage(imagen, "", null);
                stream.put(new PdfName("firmaimg_" + (firmas + 1)), new PdfName("firmaimg_" + (firmas + 1)));
                PdfIndirectObject ref = stamper.getWriter().addToBody(stream);
                imagen.setDirectReference(ref.getIndirectReference());
                PdfContentByte over = stamper.getUnderContent(1);
                over.addImage(imagen);
            } catch (Exception e) {
                System.out.println("no firma");
            }
            stamper.close();
            reader.close();
            return fpdfDestino.getName();
        } catch (Exception e) {
            System.out.println("error al unir pdfs");
            return null;
        }

    }

    public static String UnirPDF(List<InputStream> streamOfPDFFiles, String salida, boolean paginate) {
        Document document = new Document();
        try {
            List<InputStream> pdfs = streamOfPDFFiles;
            List<PdfReader> readers = new ArrayList<PdfReader>();
            int totalPages = 0;
            Iterator<InputStream> iteratorPDFs = pdfs.iterator();

            while (iteratorPDFs.hasNext()) {
                InputStream pdf = iteratorPDFs.next();
                PdfReader pdfReader = new PdfReader(pdf);
                readers.add(pdfReader);
                totalPages += pdfReader.getNumberOfPages();
            }
            File fpdfDestino = new File(salida);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fpdfDestino.getAbsolutePath()));

            document.open();
            PdfContentByte cb = writer.getDirectContent();

            PdfImportedPage page;
            int currentPageNumber = 0;
            int pageOfCurrentReaderPDF = 0;
            Iterator<PdfReader> iteratorPDFReader = readers.iterator();

            while (iteratorPDFReader.hasNext()) {
                PdfReader pdfReader = iteratorPDFReader.next();

                while (pageOfCurrentReaderPDF < pdfReader.getNumberOfPages()) {

                    Rectangle rectangle = pdfReader.getPageSizeWithRotation(1);
                    document.setPageSize(rectangle);
                    document.newPage();

                    pageOfCurrentReaderPDF++;
                    currentPageNumber++;
                    page = writer.getImportedPage(pdfReader,
                            pageOfCurrentReaderPDF);
                    switch (rectangle.getRotation()) {
                        case 0:
                            cb.addTemplate(page, 1f, 0, 0, 1f, 0, 0);
                            break;
                        case 90:
                            cb.addTemplate(page, 0, -1f, 1f, 0, 0, pdfReader
                                    .getPageSizeWithRotation(1).getHeight());
                            break;
                        case 180:
                            cb.addTemplate(page, -1f, 0, 0, -1f, 0, 0);
                            break;
                        case 270:
                            cb.addTemplate(page, 0, 1.0F, -1.0F, 0, pdfReader
                                    .getPageSizeWithRotation(1).getWidth(), 0);
                            break;
                        default:
                            break;
                    }
                    if (paginate) {
                        cb.beginText();
                        cb.getPdfDocument().getPageSize();
                        cb.endText();
                    }
                }
                pageOfCurrentReaderPDF = 0;
            }
            document.close();
            return fpdfDestino.getName();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void UnirPDF(List<InputStream> list, OutputStream outputStream) {
        try {
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            document.open();
            PdfContentByte cb = writer.getDirectContent();

            for (InputStream in : list) {
                PdfReader reader = new PdfReader(in);
                for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                    document.newPage();
                    //import the page from source pdf
                    PdfImportedPage page = writer.getImportedPage(reader, i);
                    //add the page to the destination pdf
                    cb.addTemplate(page, 0, 0);
                }
            }

            outputStream.flush();
            document.close();
            outputStream.close();
        } catch (Exception e) {

        }
    }

    public static String UnirPDF(List<InputStream> list, String salida) {
        File fpdfDestino = new File(salida);
        try {
            Document document = new Document();
            PdfCopy copy = new PdfCopy(document, new FileOutputStream(fpdfDestino.getAbsolutePath()));
            document.open();
            for (InputStream in : list) {
                PdfReader reader = new PdfReader(in);
                copy.addDocument(reader);
                copy.freeReader(reader);
                reader.close();
            }
            document.close();
            return fpdfDestino.getName();
        } catch (Exception e) {
            System.out.println("error al unir pdfs");
            return null;
        }
    }

    public static String UnirPDFS(List<String> list, String salida) {
        try {

            PDFMergerUtility PDFmerger = new PDFMergerUtility();
            PDFmerger.setDestinationFileName(salida);
            for (String pdfFile : list) {
                File file = new File(pdfFile);
                PDDocument doc = PDDocument.load(file);
                PDFmerger.addSource(file);
                doc.close();
            }
//            PDFmerger.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
            PDFmerger.mergeDocuments();
            return salida;
        } catch (Exception e) {
            System.out.println("error al unir pdfs");
            return null;
        }
    }

    public static String UNIRPDFS(List<String> list, String dir, String salida) {
        try {
            RandomAccessFile archivo = new RandomAccessFile(dir + salida, "rw");
            archivo.write(ConvertirPdfs(list));
            archivo.close();
            return salida;
        } catch (Exception e) {
            System.out.println("error al unir pdfs");
            return null;
        }
    }

    public static byte[] ConvertirPdfs(List<String> pdfs) {

        List<byte[]> documentos = new ArrayList<>();
        for (String pdf : pdfs) {
            FileInputStream archivoIS = null;
            try {
                File archivo = new File(pdf);
                archivoIS = new FileInputStream(archivo);
                byte buffer[] = new byte[(int) archivo.length()];
                archivoIS.read(buffer);
                documentos.add(buffer);
            } catch (FileNotFoundException ex) {
                System.out.println("no se pueden econtrar los archivo");
            } catch (IOException ex) {
                System.out.println("error fatal ");
            } finally {
                try {
                    archivoIS.close();
                } catch (IOException ex) {
                    System.out.println("error al cerrar archivo streams");
                }
            }
        }
        List<InputStream> inputStreams = new ArrayList<>();
        for (byte[] content : documentos) {
            inputStreams.add(new ByteArrayInputStream(content));
        }
        ByteArrayOutputStream mergedStream = new ByteArrayOutputStream();
        CombinarPdfs(inputStreams, mergedStream);
        byte[] outputData = mergedStream.toByteArray();
        return outputData;
    }

    public static void CombinarPdfs(final List<InputStream> streamOfPDFFiles, final OutputStream outputStream) {
        try {
            PDFMergerUtility mergePdf = new PDFMergerUtility();
            for (InputStream inputDoc : streamOfPDFFiles) {
                mergePdf.addSource(inputDoc);
            }
            mergePdf.setDestinationStream(outputStream);
//            mergePdf.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
            mergePdf.mergeDocuments();
        } catch (Exception e) {
            System.out.println("error en concatenar" + e.getMessage());
        }
    }

    public static File ValidarFirma(String Origen, String Salida, boolean visible, List Cordenadas, List Keys, Properties Parametros) {
        if (visible) {
            if (!Cordenadas.isEmpty()) {
                if (Cordenadas.size() > 0) {
                    return FirmarPdfVisible(Origen, Salida, Cordenadas, Keys, Parametros);
                } else {
                    return FirmarPdfNovisible(Origen, Salida, Cordenadas, Keys, Parametros);
                }
            } else {
                //msg = "No se puede encontrar cordenadas de firmante en el documento";
                return FirmarPdfNovisible(Origen, Salida, Cordenadas, Keys, Parametros);
            }
        } else {
            return FirmarPdfNovisible(Origen, Salida, Cordenadas, Keys, Parametros);
        }
    }

    public static File ValidarFirmaModelo(String Origen, String Salida, boolean visible, List Cordenadas, List Keys, Properties Parametros) {
        if (visible) {
            if (!Cordenadas.isEmpty()) {
                if (Cordenadas.size() > 0) {
                    return FirmarPdfVisibleModelo(Origen, Salida, Cordenadas, Keys, Parametros);
                } else {
                    return FirmarPdfNovisible(Origen, Salida, Cordenadas, Keys, Parametros);
                }
            } else {
                //msg = "No se puede encontrar cordenadas de firmante en el documento";
                return FirmarPdfNovisible(Origen, Salida, Cordenadas, Keys, Parametros);
            }
        } else {
            return FirmarPdfNovisible(Origen, Salida, Cordenadas, Keys, Parametros);
        }
    }

    public static List ValidarCertificado(String Certificado, String Clave) {
        try {
            File fContenedorp12 = new File(Certificado);
            if (fContenedorp12.exists()) {
                List keys = new ArrayList();
                Provider p = new BouncyCastleProvider();
                Security.addProvider(p);
                KeyStore ks = KeyStore.getInstance("pkcs12");
                ks.load(new FileInputStream(fContenedorp12.getAbsolutePath()), Clave.toCharArray());
                String alias = (String) ks.aliases().nextElement();
                PrivateKey key = (PrivateKey) ks.getKey(alias, Clave.toCharArray());
                Certificate[] certChain = ks.getCertificateChain(alias);
                keys.add(key);
                keys.add(certChain);
                return keys;
            } else {
                msg = "No existe el archivo de firma.";
                return null;
            }

        } catch (Exception e) {
            msg = e.getMessage();
            return null;
        }
    }

    public static String FirmaCaducidad(String dia, List Keys) {
        String fecha = "";
        try {
            DateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
            Date actual = new Date();
            Certificate[] certChain = (Certificate[]) Keys.get(1);
            X509Certificate x509Certificate = (X509Certificate) certChain[0];
            Date Caducidad = x509Certificate.getNotAfter();
            Date notificacionc = Fecha.RestarTiempos(Caducidad, dia, "", "");
            boolean ok = Fecha.EsMenorFecha(notificacionc, actual);
            if (ok) {
                fecha = "Este certificado esta proximamente a caducar. Fecha de caducidad : " + formato.format(Caducidad);
                if (actual.after(Caducidad)) {
                    fecha = "0";
                }
            }
        } catch (Exception e) {
            System.out.println("" + e.getLocalizedMessage() + " " + e.getMessage());
        }
        return fecha;
    }

    public static File FirmarPdfVisible(String Origen, String Salida, List Cordenadas, List Keys, Properties Parametros) {
        try {
            PrivateKey key = (PrivateKey) Keys.get(0);
            Certificate[] certChain = (Certificate[]) Keys.get(1);
            // Motivo de la firma
            String razon = "Firmado Digitalmete";
            String ubicacion = "Ibarra";
            String fecha_hora = Fecha.getFecha("ISO") + "T" + Fecha.getHora() + "-05:00";
            float tamano_letra = 3;
            if (Parametros != null) {
                Parametros = Parametros != null ? Parametros : new Properties();
                razon = (Parametros.getProperty("razon") != null ? Parametros.getProperty("razon") : razon);
                ubicacion = (Parametros.getProperty("ubicacion") != null ? Parametros.getProperty("ubicacion") : ubicacion);
                fecha_hora = (Parametros.getProperty("fecha_hora") != null ? Parametros.getProperty("fecha_hora") : fecha_hora);
                tamano_letra = (Parametros.getProperty("tamano_letra") != null ? Float.parseFloat(Parametros.getProperty("tamano_letra").trim()) : tamano_letra);

            }
            File PdfOrigen = new File(Origen);
            File PdfSalida = new File(Salida);
            boolean ok = false;
            for (int i = 0; i < Cordenadas.size(); i++) {
                String condenadaxy[] = Cordenadas.get(i).toString().split(":");
                if (condenadaxy != null) {
                    int movery = (condenadaxy.length > 5 ? 0 : 60);
                    int moverx = (condenadaxy.length > 5 ? 20 : 30);
                    PdfReader pdfReader = new PdfReader(PdfOrigen.getAbsolutePath());
                    String outfil = PdfSalida.getAbsolutePath();
                    FileOutputStream arcoput = new FileOutputStream(outfil);
                    PdfStamper stp = PdfStamper.createSignature(pdfReader, arcoput, '\0', null, true);
                    double x_pagina = Double.parseDouble(condenadaxy[3]);
                    double y_pagina = Double.parseDouble(condenadaxy[4]);
                    double x_puntero = Double.parseDouble(condenadaxy[0]);
                    double y_puntero = Double.parseDouble(condenadaxy[1]);
                    int pagina = Integer.parseInt(condenadaxy[2]);
                    float tamano_principal = tamano_letra;
                    PdfSignatureAppearance sap = stp.getSignatureAppearance();
                    double x_tamano = pdfReader.getPageSize(pagina).getWidth();
                    double y_tamano = pdfReader.getPageSize(pagina).getHeight();
                    int x = (int) Math.round((x_puntero * x_tamano) / x_pagina);
                    int y = (int) Math.round((y_puntero * y_tamano) / y_pagina);
                    Parametros.setProperty(PdfUtil.positionOnPageLowerLeftX, "" + (x - moverx));
                    Parametros.setProperty(PdfUtil.positionOnPageLowerLeftY, "" + ((y_tamano - (y - movery))));
                    PdfSigLockDictionary lockDic = null;
                    lockDic = new PdfSigLockDictionary(LockPermissions.NO_CHANGES_ALLOWED);
                    stp.getWriter().addToBody(lockDic).getIndirectReference();
                    if (razon != null) {
                        if (razon.trim().compareTo("") != 0) {
                            sap.setReason(razon);
                        }
                    }
                    if (ubicacion != null) {
                        if (ubicacion.trim().compareTo("") != 0) {
                            sap.setLocation(ubicacion);
                        }
                    }
                    if (ubicacion != null) {
                        if (ubicacion.trim().compareTo("") != 0) {
                            sap.setLocation(ubicacion);
                        }
                    }
                    try {
                        GregorianCalendar calendar = new GregorianCalendar();
                        if (fecha_hora != null) {
                            if (fecha_hora.trim().compareTo("") != 0) {
                                Date date = Utils.getSignTime(fecha_hora);
                                calendar.setTime(date);
                            }
                        }
                        sap.setSignDate(calendar);
                    } catch (Exception e) {
                        System.out.println("error" + e.getMessage());
                    }
                    if (pagina == 0 || pagina < 0 || pagina > pdfReader.getNumberOfPages()) {
                        pagina = pdfReader.getNumberOfPages();
                    }
                    Rectangle signaturePositionOnPage = getSignaturePositionOnPage(Parametros);
                    if (signaturePositionOnPage != null) {
                        sap.setVisibleSignature(signaturePositionOnPage, pagina, null);
                        X509Certificate x509Certificate = (X509Certificate) certChain[0];
                        String informacionCertificado = x509Certificate.getSubjectDN().getName();
                        String nombreFirmante = "";
                        try {
                            nombreFirmante = (informacionCertificado.substring(informacionCertificado.lastIndexOf("CN=") + 3,
                                    informacionCertificado.indexOf(","))).toUpperCase();
                        } catch (Exception e) {
                            System.out.println("Otro modelo de certificado");
                            try {
                                nombreFirmante = (informacionCertificado.substring(informacionCertificado.lastIndexOf("CN=") + 3,
                                        informacionCertificado.length())).toUpperCase();
                            } catch (Exception ex) {
                                System.out.println("Error general de versiones" + ex.getLocalizedMessage() + " " + e.getMessage());
                            }
                        }
                        // Creating the appearance for layer 0
                        PdfTemplate pdfTemplate = sap.getLayer(0);
                        float width = pdfTemplate.getBoundingBox().getWidth();
                        float height = pdfTemplate.getBoundingBox().getHeight();
                        pdfTemplate.rectangle(0, 0, width, height);

                        // Creating the appearance for layer 2
                        // Nombre Firmante
                        PdfTemplate pdfTemplate1 = sap.getLayer(2);
                        Font font1 = new Font(Font.FontFamily.TIMES_ROMAN, tamano_letra + (tamano_letra / 2), Font.NORMAL);
                        Paragraph paragraph1 = new Paragraph(nombreFirmante.trim(), font1);
                        paragraph1.setAlignment(Paragraph.ALIGN_RIGHT);
                        ColumnText columnText1 = new ColumnText(pdfTemplate1);
                        columnText1.setSimpleColumn(0, 0, (width / 2) - 1, height);
                        columnText1.addElement(paragraph1);
                        columnText1.go();
                        // Segunda Columna
                        PdfTemplate pdfTemplate2 = sap.getLayer(2);
                        Font font2 = new Font(Font.FontFamily.TIMES_ROMAN, tamano_letra, Font.NORMAL);
                        Paragraph paragraph2 = new Paragraph(tamano_principal, informacionCertificado.trim() + "\n FECHA: " + Fecha.getFecha("iso") + " " + Fecha.getHora(), font2);
                        paragraph2.setAlignment(Paragraph.ALIGN_LEFT);
                        ColumnText columnText2 = new ColumnText(pdfTemplate2);
                        columnText2.setSimpleColumn((width / 2) + 1, 0, width, height);
                        columnText2.addElement(paragraph2);
                        columnText2.go();

                        ExternalSignature es = new PrivateKeySignature(key, "SHA1", "BC");
                        ExternalDigest digest = new BouncyCastleDigest();

                        //Se genera la firma y se almacena el pdf como se indico en las lineas anteriores
                        try {
                            MakeSignature.signDetached(sap, digest, es, certChain, null, null, null, 0, CryptoStandard.CMS);
                            ok = true;
                            stp.close();
                            pdfReader.close();
                            File tmpfile = PdfOrigen;
                            PdfOrigen = PdfSalida;
                            PdfSalida = tmpfile;
                        } catch (Exception e) {
                            ok = false;
                            msg = "error el intentar estampar firma en el archivo " + e.getMessage();
                            break;
                        }
                    } else {
                        ok = false;
                        msg = "error el intentar firmar en una cordenada";
                        break;
                    }
                } else {
                    ok = false;
                    msg = "error el intentar convertir cordenadas entreagas para firma";
                    break;
                }
            }
            if (ok) {
                return PdfOrigen;
            } else {
                return null;
            }
        } catch (Exception e) {
            msg = e.getMessage();
            return null;
        }
    }

    public static File FirmarPdfVisibleModelo(String Origen, String Salida, List Cordenadas, List Keys, Properties Parametros) {
        try {
            PrivateKey key = (PrivateKey) Keys.get(0);
            Certificate[] certChain = (Certificate[]) Keys.get(1);
            // Motivo de la firma
            String razon = "Firmado Digitalmete";
            String ubicacion = "Ibarra";
            String fecha_hora = Fecha.getFecha("ISO");
            float tamano_letra = 3;
            float ancho_final = 150;
            float largo_final = 50;
            if (Parametros != null) {
                Parametros = Parametros != null ? Parametros : new Properties();
                razon = (Parametros.getProperty("razon") != null ? Parametros.getProperty("razon") : razon);
                ubicacion = (Parametros.getProperty("ubicacion") != null ? Parametros.getProperty("ubicacion") : ubicacion);
                fecha_hora = (Parametros.getProperty("fecha_hora") != null ? Parametros.getProperty("fecha_hora") : fecha_hora);
                tamano_letra = (Parametros.getProperty("tamano_letra") != null ? Float.parseFloat(Parametros.getProperty("tamano_letra").trim()) : tamano_letra);
                ancho_final = (Parametros.getProperty("ancho_final") != null ? Float.parseFloat(Parametros.getProperty("ancho_final").trim()) : ancho_final);
                largo_final = (Parametros.getProperty("largo_final") != null ? Float.parseFloat(Parametros.getProperty("largo_final").trim()) : largo_final);

            }
            File PdfOrigen = new File(Origen);
            File PdfSalida = new File(Salida);
            boolean ok = false;
            for (int i = 0; i < Cordenadas.size(); i++) {
                String condenadaxy[] = Cordenadas.get(i).toString().split(":");
                if (condenadaxy != null) {
                    int movery = (condenadaxy.length > 5 ? 0 : 60);
                    int moverx = (condenadaxy.length > 5 ? 20 : 30);
                    PdfReader pdfReader = new PdfReader(PdfOrigen.getAbsolutePath());
                    PdfStamper stp = PdfStamper.createSignature(pdfReader, new FileOutputStream(PdfSalida.getAbsolutePath()), '\0', null, true);
                    double x_pagina = Double.parseDouble(condenadaxy[3]);
                    double y_pagina = Double.parseDouble(condenadaxy[4]);
                    double x_puntero = Double.parseDouble(condenadaxy[0]);
                    double y_puntero = Double.parseDouble(condenadaxy[1]);
                    int pagina = Integer.parseInt(condenadaxy[2]);
                    float tamano_principal = tamano_letra;
                    PdfSignatureAppearance sap = stp.getSignatureAppearance();
                    double x_tamano = pdfReader.getPageSize(pagina).getWidth();
                    double y_tamano = pdfReader.getPageSize(pagina).getHeight();
                    int x = (int) Math.round((x_puntero * x_tamano) / x_pagina);
                    int y = (int) Math.round((y_puntero * y_tamano) / y_pagina);
                    Parametros.setProperty(PdfUtil.positionOnPageLowerLeftX, "" + ((x + 10) - moverx));
                    Parametros.setProperty(PdfUtil.positionOnPageLowerLeftY, "" + (((y_tamano - 17) - (y - movery))));
                    Parametros.setProperty(PdfUtil.positionOnPageUpperRightX, "" + ancho_final);
                    Parametros.setProperty(PdfUtil.positionOnPageUpperRightY, "" + largo_final);
                    PdfSigLockDictionary lockDic = null;
                    lockDic = new PdfSigLockDictionary(LockPermissions.NO_CHANGES_ALLOWED);
                    stp.getWriter().addToBody(lockDic).getIndirectReference();
                    if (razon != null) {
                        if (razon.trim().compareTo("") != 0) {
                            sap.setReason(razon);
                        }
                    }
                    if (ubicacion != null) {
                        if (ubicacion.trim().compareTo("") != 0) {
                            sap.setLocation(ubicacion);
                        }
                    }
                    if (ubicacion != null) {
                        if (ubicacion.trim().compareTo("") != 0) {
                            sap.setLocation(ubicacion);
                        }
                    }
                    try {
                        GregorianCalendar calendar = new GregorianCalendar();
                        if (fecha_hora != null) {
                            if (fecha_hora.trim().compareTo("") != 0) {
                                Date date = Utils.getSignTime(fecha_hora);
                                calendar.setTime(date);
                            }
                        }
                        sap.setSignDate(calendar);
                    } catch (Exception e) {
                        System.out.println("error" + e.getMessage());
                    }
                    if (pagina == 0 || pagina < 0 || pagina > pdfReader.getNumberOfPages()) {
                        pagina = pdfReader.getNumberOfPages();
                    }
                    Rectangle signaturePositionOnPage = getSignaturePositionOnPage(Parametros);
                    if (signaturePositionOnPage != null) {
                        sap.setVisibleSignature(signaturePositionOnPage, pagina, null);
                        X509Certificate x509Certificate = (X509Certificate) certChain[0];
                        String informacionCertificado = x509Certificate.getSubjectDN().getName();
                        String nombreFirmante = "";
                        try {
                            nombreFirmante = (informacionCertificado.substring(informacionCertificado.lastIndexOf("CN=") + 3,
                                    informacionCertificado.indexOf(","))).toUpperCase();
                        } catch (Exception e) {
                            System.out.println("Otro modelo de certificado");
                            try {
                                nombreFirmante = (informacionCertificado.substring(informacionCertificado.lastIndexOf("CN=") + 3,
                                        informacionCertificado.length())).toUpperCase();
                            } catch (Exception ex) {
                                System.out.println("Error general de versiones" + ex.getLocalizedMessage() + " " + e.getMessage());
                            }
                        }
                        // Creating the appearance for layer 0
                        PdfTemplate pdfTemplate = sap.getLayer(0);
                        float width = pdfTemplate.getBoundingBox().getWidth();
                        float height = pdfTemplate.getBoundingBox().getHeight();
                        pdfTemplate.rectangle(0, 0, width, height);

                        // Creating the appearance for layer 2
                        // Nombre Firmante
                        PdfTemplate pdfTemplate1 = sap.getLayer(2);
                        Font font1 = new Font(Font.FontFamily.TIMES_ROMAN, tamano_letra + (tamano_letra / 2), Font.NORMAL);
                        Paragraph paragraph1 = new Paragraph(nombreFirmante.trim(), font1);
                        paragraph1.setAlignment(Paragraph.ALIGN_RIGHT);
                        ColumnText columnText1 = new ColumnText(pdfTemplate1);
                        columnText1.setSimpleColumn(0, 0, (width / 2) - 1, height);
                        columnText1.addElement(paragraph1);
                        columnText1.go();
                        // Segunda Columna
                        PdfTemplate pdfTemplate2 = sap.getLayer(2);
                        Font font2 = new Font(Font.FontFamily.TIMES_ROMAN, tamano_letra, Font.NORMAL);
                        Paragraph paragraph2 = new Paragraph(tamano_principal, informacionCertificado.trim() + "\n FECHA: " + Fecha.getFecha("iso") + " " + Fecha.getHora(), font2);
                        paragraph2.setAlignment(Paragraph.ALIGN_LEFT);
                        ColumnText columnText2 = new ColumnText(pdfTemplate2);
                        columnText2.setSimpleColumn((width / 2) + 1, 0, width, height);
                        columnText2.addElement(paragraph2);
                        columnText2.go();

                        ExternalSignature es = new PrivateKeySignature(key, "SHA1", "BC");
                        ExternalDigest digest = new BouncyCastleDigest();

                        //Se genera la firma y se almacena el pdf como se indico en las lineas anteriores
                        try {
                            MakeSignature.signDetached(sap, digest, es, certChain, null, null, null, 0, CryptoStandard.CMS);
                            ok = true;
                            stp.close();
                            pdfReader.close();
                            File tmpfile = PdfOrigen;
                            PdfOrigen = PdfSalida;
                            PdfSalida = tmpfile;
                        } catch (Exception e) {
                            ok = false;
                            msg = "error el intentar estampar firma en el archivo " + e.getMessage();
                            break;
                        }
                    } else {
                        ok = false;
                        msg = "error el intentar firmar en una cordenada";
                        break;
                    }
                } else {
                    ok = false;
                    msg = "error el intentar convertir cordenadas entreagas para firma";
                    break;
                }
            }
            if (ok) {
                return PdfOrigen;
            } else {
                return null;
            }
        } catch (Exception e) {
            msg = e.getMessage();
            return null;
        }
    }

    public static File FirmarPdfNovisible(String Origen, String Salida, List Cordenadas, List Keys, Properties Parametros) {
        try {
            PrivateKey key = (PrivateKey) Keys.get(0);
            Certificate[] certChain = (Certificate[]) Keys.get(1);
            // Motivo de la firma
            String razon = "Firmado Digitalmete";
            String ubicacion = "Ibarra";
            String fecha_hora = Fecha.getFecha("ISO");
            float tamano_letra = 3;
            if (Parametros != null) {
                Parametros = Parametros != null ? Parametros : new Properties();
                razon = (Parametros.getProperty("razon") != null ? Parametros.getProperty("razon") : razon);
                ubicacion = (Parametros.getProperty("ubicacion") != null ? Parametros.getProperty("ubicacion") : ubicacion);
                fecha_hora = (Parametros.getProperty("fecha_hora") != null ? Parametros.getProperty("fecha_hora") : fecha_hora);
                tamano_letra = (Parametros.getProperty("tamano_letra") != null ? Float.parseFloat(Parametros.getProperty("tamano_letra").trim()) : tamano_letra);

            }
            File PdfOrigen = new File(Origen);
            File PdfSalida = new File(Salida);
            boolean ok = false;
//            for (int i = 0; i < Cordenadas.size(); i++) {
//                String condenadaxy[] = Cordenadas.get(i).toString().split(":");
//                if (condenadaxy != null) {
            PdfReader pdfReader = new PdfReader(PdfOrigen.getAbsolutePath());
            PdfStamper stp = PdfStamper.createSignature(pdfReader, new FileOutputStream(PdfSalida.getAbsolutePath()), '\0', null, true);
            PdfSignatureAppearance sap = stp.getSignatureAppearance();
            PdfSigLockDictionary lockDic = null;
            lockDic = new PdfSigLockDictionary(LockPermissions.NO_CHANGES_ALLOWED);
            stp.getWriter().addToBody(lockDic).getIndirectReference();
            if (razon != null) {
                if (razon.trim().compareTo("") != 0) {
                    sap.setReason(razon);
                }
            }
            if (ubicacion != null) {
                if (ubicacion.trim().compareTo("") != 0) {
                    sap.setLocation(ubicacion);
                }
            }
            if (ubicacion != null) {
                if (ubicacion.trim().compareTo("") != 0) {
                    sap.setLocation(ubicacion);
                }
            }
            try {
                GregorianCalendar calendar = new GregorianCalendar();
                if (fecha_hora != null) {
                    if (fecha_hora.trim().compareTo("") != 0) {
                        Date date = Utils.getSignTime(fecha_hora);
                        calendar.setTime(date);
                    }
                }
                sap.setSignDate(calendar);
            } catch (Exception e) {
                System.out.println("error" + e.getMessage());
            }
            ExternalSignature es = new PrivateKeySignature(key, "SHA1", "BC");
            ExternalDigest digest = new BouncyCastleDigest();
            //Se genera la firma y se almacena el pdf como se indico en las lineas anteriores
            try {
                MakeSignature.signDetached(sap, digest, es, certChain, null, null, null, 0, CryptoStandard.CMS);
                ok = true;
                stp.close();
                pdfReader.close();
                File tmpfile = PdfOrigen;
                PdfOrigen = PdfSalida;
                PdfSalida = tmpfile;
            } catch (Exception e) {
                ok = false;
                msg = "error el intentar estampar firma en el archivo " + e.getMessage();
//                        break;
            }
//                } else {
//                    ok = false;
//                    msg = "error el intentar convertir cordenadas entreagas para firma";
//                    break;
//                }
//            }
            if (ok) {
                return PdfOrigen;
            } else {
                return null;
            }
        } catch (Exception e) {
            msg = e.getMessage();
            return null;
        }
    }
}
