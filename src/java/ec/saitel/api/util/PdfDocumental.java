/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.saitel.api.util;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import ec.saitel.api.util.firma.CordenadasXY;
import ec.saitel.api.util.firma.Firmas;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Properties;


/**
 *
 * @author wilso
 */
public class PdfDocumental {

    private String _ip = Parametro.getIp();
    private int _puerto = Parametro.getPuerto();
    private String _db = Parametro.getDb();
    private String _usuario = Parametro.getUsuario();
    private String _clave = Parametro.getClave();
    
    private String _ipdocumental = Parametro.getDocIp();
    private int _puertodocumental = Parametro.getDocPpuerto();
    private String _dbdocumental = Parametro.getDocDb();
    private String _usuariodocumental = Parametro.getDocUsuario();
    private String _clavedocumental = Parametro.getDocClave();
    
    private List Keys = null;
    private String _URL_ANEXOS = Parametro.getUrlAnexos();
    private String _dir = Parametro.getDir();
    private String msgc = "";

    public void setConexion(String ip, int puerto, String db, String usuario, String clave, String ipdocumental, int puertodocumental, String dbdocumental, String usuariodocumental, String clavedocumental) {
        this._ip = ip;
        this._puerto = puerto;
        this._db = db;
        this._usuario = usuario;
        this._clave = clave;
        this._ipdocumental = ipdocumental;
        this._puertodocumental = puertodocumental;
        this._dbdocumental = dbdocumental;
        this._usuariodocumental = usuariodocumental;
        this._clavedocumental = clavedocumental;
    }

    public void setConexionTransaccional(String ip, int puerto, String db, String usuario, String clave) {
        this._ip = ip;
        this._puerto = puerto;
        this._db = db;
        this._usuario = usuario;
        this._clave = clave;
    }

    public void setUDirAnexos(String url_anexos, String dir) {
        this._URL_ANEXOS = url_anexos;
        this._dir = dir;
    }

    public void setConexionDocumental(String ipdocumental, int puertodocumental, String dbdocumental, String usuariodocumental, String clavedocumental) {
        this._ipdocumental = ipdocumental;
        this._puertodocumental = puertodocumental;
        this._dbdocumental = dbdocumental;
        this._usuariodocumental = usuariodocumental;
        this._clavedocumental = clavedocumental;
    }

    public String ValidarKeys(String _dir, int id_empleado, String usuario_key) {
        String msg = "";
        Archivo ObjArchivo = new Archivo(_ipdocumental, _puertodocumental, _dbdocumental, _usuariodocumental, _clavedocumental);
        Configuracion ObjConfiguracion = new Configuracion(_ip, _puerto, _db, _usuario, _clave);
        try {
            String dias = ObjConfiguracion.getValor("dias_notificar_certificado");
            String ruta_certificado = ObjArchivo.getArchivoDocumental(_dir, "tbl_empleado_firma", "" + id_empleado, "firma_digital");
            if (ruta_certificado.trim().compareTo("") != 0) {
                File archivo_key = new File(_dir, ruta_certificado);
                Keys = Firmas.ValidarCertificado(archivo_key.getAbsolutePath(), usuario_key);
                if (Keys == null) {
                    msg = "msg»Estimado Usted ha ingresado un clave invalida";
                } else {
                    msgc = Firmas.FirmaCaducidad(dias, Keys);
                    if (msgc.compareTo("0") == 0) {
//                        ObjConfiguracion.ejecutar("update tbl_empleado set obligado_firmar =false where id_empleado ='" + id_empleado + "';");
                        ObjArchivo.ejecutar("DELETE FROM tbl_documentos WHERE tabla='tbl_empleado_firma' and campo_tabla='firma_digital' and id_tabla='" + id_empleado + "';");
                    }
                    System.out.println("caduca " + msgc);
                }
            } else {
                msg = "msg»Estimado no tiene adjuntado ningina firma digital";
            }
//            ObjConfiguracion.ejecutar("update tbl_empleado set clave_crt='" + usuario_key + "' where id_empleado ='" + id_empleado + "';");
        } catch (Exception e) {
            msg = e.getMessage();
        } finally {
            ObjArchivo.cerrar();
            ObjConfiguracion.cerrar();
        }
        return msg;
    }

    public List getKeys() {
        return Keys;
    }

    public String getmsgc() {
        return msgc;
    }

    public Properties getPropiedadesPdf(int id_sucursal) {
        Properties parametros = new Properties();
        Sucursal ObjSucursal = new Sucursal(this._ip, this._puerto, this._db, this._usuario, this._clave);
        try {
            parametros.setProperty("ubicacion", ObjSucursal.getCiudad(id_sucursal));
            parametros.setProperty("razon", "FIRMADO DIGITALMENTE");
            parametros.setProperty("tamano_letra", "4.5");
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        } finally {
            ObjSucursal.cerrar();
        }
        return parametros;
    }

    public Properties getPropiedadesPdfSalida(int id_sucursal) {
        Properties parametros = new Properties();
        Sucursal ObjSucursal = new Sucursal(this._ip, this._puerto, this._db, this._usuario, this._clave);
        try {
            parametros.setProperty("ubicacion", ObjSucursal.getCiudad(id_sucursal));
            parametros.setProperty("razon", "FIRMADO DIGITALMENTE");
            parametros.setProperty("tamano_letra", "2");
            parametros.setProperty("ancho_final", "60");
            parametros.setProperty("largo_final", "20");
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        } finally {
            ObjSucursal.cerrar();
        }
        return parametros;
    }

    public boolean firmarArchivoPdf(Archivo ObjArchivo, String rutaDir, File archPdf, int id_sucursal, boolean obligado_firmar, String firmar_documentos, 
            int id_empleado, String usuario_key, String palabraClave, String tabla, String idRegTabla)
    {
        try {
            if ( firmar_documentos.trim().compareTo("si") == 0 && obligado_firmar) {
                String oki = this.ValidarKeys(rutaDir, id_empleado, usuario_key);
                if (oki.compareTo("") == 0) {
                    java.util.List Keys = this.getKeys();
                    Properties parametros = this.getPropiedadesPdf(id_sucursal);
                    CordenadasXY cordenadasXY = new CordenadasXY();
                    java.util.List cordenadas = cordenadasXY.CordenadasXY(archPdf, palabraClave);
                    String archFirmado = "firmado_" + archPdf.getName();
                    File archivo_salida = new File(rutaDir, archFirmado);
                    File archivo_firma = Firmas.ValidarFirma(archPdf.getAbsolutePath(), archivo_salida.getAbsolutePath(), true, cordenadas, Keys, parametros);
                    if (archivo_firma != null) {
                        if (ObjArchivo.setArchivoDocumental(tabla, idRegTabla, "documento_digital", archivo_firma.getName(), archivo_firma.getAbsolutePath(), "public", "db_isp")) {
                            return true;
                        }
                    }
                } 
            } else if (firmar_documentos.trim().compareTo("no") == 0 || !obligado_firmar) {
                File archivo_firma = new File( archPdf.getAbsolutePath() );
                if (ObjArchivo.setArchivoDocumental(tabla, idRegTabla, "documento_digital", archivo_firma.getName(), archivo_firma.getAbsolutePath(), "public", "db_isp")) {
                    return true;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    
    
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    
    
    
    public String PdfPersonalizacion(String id, File documento, String es_responsable_cliente, String tipo_movimento, String entregaUnTercero, String mCorresponsables[][]) {
        String resultado = null;
        Archivo archivo = new Archivo(this._ip, this._puerto, this._db, this._usuario, this._clave);
        byte[] logo = archivo.getArchivo(1);
        archivo.cerrar();
        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, this._usuario, this._clave);
        String ruc = conf.getValor("ruc");
        String razon_social = conf.getValor("razon_social");
        String dir_matriz = conf.getValor("dir_matriz");
        conf.cerrar();
        Activo objActivo = new Activo(this._ip, this._puerto, this._db, this._usuario, this._clave);
        ResultSet cab = objActivo.getDocumento(id);
        boolean aceptada = false;
        ResultSet det = null;
        try {
            if (cab.next()) {
                aceptada = cab.getString("aceptada") != null ? cab.getBoolean("aceptada") : false;
            }
            cab.beforeFirst();
            if (!aceptada) {
                det = objActivo.getDocumentoDetalleTmp(id);
            } else {
                det = objActivo.getDocumentoDetalle(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        objActivo.cerrar();
        /* inicio PDF */
        Document document = new Document(PageSize.A4);// paso 1
        document.setMargins(0, 0, 50, 30);
        /*Izquierda, derecha, tope, pie */

        try {
            PdfWriter.getInstance(document, new FileOutputStream(documento));
            //writer.setPageEvent(new Movimiento());

            document.open(); // paso 3

            /* todo el cuerpo del doc es el paso 4 */
            PdfPTable encabezado = new PdfPTable(new float[]{80f, 400f});
            PdfPTable tbl_encab = new PdfPTable(1);
            Image imagelogo = null;
            try {
                imagelogo = Image.getInstance(logo);
                imagelogo.scaleAbsolute(70, 70);
                PdfPCell celdaImg = new PdfPCell(imagelogo);
                celdaImg.setBorderWidth(0);
                celdaImg.setPadding(0);
                encabezado.addCell(celdaImg);
            } catch (Exception e) {
                encabezado.addCell(e.getMessage());
            }
            tbl_encab.addCell(Addons.setCeldaPDF(razon_social, Font.HELVETICA, 13, Font.BOLD, Element.ALIGN_CENTER, 0));
            tbl_encab.addCell(Addons.setCeldaPDF(dir_matriz, Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_CENTER, 0));
            if (ruc.compareTo("") != 0) {
                tbl_encab.addCell(Addons.setCeldaPDF("RUC: " + ruc, Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_CENTER, 0));
            }
            tbl_encab.addCell(Addons.setCeldaPDF("FORMULARIO DE MOVIMIENTO DE ACTIVOS", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));

            encabezado.addCell(Addons.setCeldaPDF(tbl_encab, Element.ALIGN_CENTER, 0));

            encabezado.addCell(Addons.setFilaBlanco(2, 4));

            document.add(encabezado);

            String observacion = "";
            String persona_entrega = "";
            String persona_recibe = "";
            float gestion_envio = 0;
            String gestion_envio1 = "0";
            try {
                if (cab.next()) {
                    observacion = (cab.getString("observacion") != null) ? cab.getString("observacion") : "";
                    persona_entrega = (cab.getString("persona_entrega") != null) ? cab.getString("persona_entrega") : "";
                    persona_recibe = (cab.getString("persona_recibe") != null) ? cab.getString("persona_recibe") : "";
                    gestion_envio1 = (cab.getString("gestion_envio") != null) ? cab.getString("gestion_envio") : "";
                    gestion_envio1 = gestion_envio1.trim().compareTo("") == 0 ? "0" : gestion_envio1;
                    gestion_envio = Float.parseFloat(gestion_envio1);
                    PdfPTable tbl_cab = new PdfPTable(4);
                    tbl_cab.addCell(Addons.setCeldaPDF("Fecha: " + ((cab.getString("fecha") != null) ? cab.getString("fecha") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0, 3, 2));
                    tbl_cab.addCell(Addons.setCeldaPDF("Nro. " + ((cab.getString("num_documento") != null) ? cab.getString("num_documento") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0, 3, 2));

                    tbl_cab.addCell(Addons.setCeldaPDF("Tipo de movimiento (Traslado): " + ((cab.getString("txt_tipo_movimiento") != null) ? cab.getString("txt_tipo_movimiento") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0, 3, 4));

                    tbl_cab.addCell(Addons.setFilaBlanco(4, 10));
                    /* colspan, alto */

                    tbl_cab.addCell(Addons.setCeldaPDF("ENTREGA", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_CENTER, 1, 3, 2));
                    tbl_cab.addCell(Addons.setCeldaPDF("RECIBE", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_CENTER, 1, 3, 2));

                    String tipo = (cab.getString("tipo_movimiento") != null) ? cab.getString("tipo_movimiento") : "";
                    if (tipo.compareTo("2") != 0) {
                        tbl_cab.addCell(Addons.setCeldaPDF("Bodega: ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_cab.addCell(Addons.setCeldaPDF((cab.getString("bodega_entrega") != null) ? cab.getString("bodega_entrega") : "", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_cab.addCell(Addons.setCeldaPDF("Bodega: ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_cab.addCell(Addons.setCeldaPDF((cab.getString("bodega_recibe") != null) ? cab.getString("bodega_recibe") : "", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_cab.addCell(Addons.setCeldaPDF("Ubicación: ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_cab.addCell(Addons.setCeldaPDF((cab.getString("ubicacion_entrega") != null) ? cab.getString("ubicacion_entrega") : "", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_cab.addCell(Addons.setCeldaPDF("Ubicación: ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_cab.addCell(Addons.setCeldaPDF((cab.getString("ubicacion_recibe") != null) ? cab.getString("ubicacion_recibe") : "", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    }

                    tbl_cab.addCell(Addons.setCeldaPDF("Documento de Identidad: ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_cab.addCell(Addons.setCeldaPDF((cab.getString("dni_entrega") != null) ? cab.getString("dni_entrega") : "", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_cab.addCell(Addons.setCeldaPDF("Documento de Identidad: ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_cab.addCell(Addons.setCeldaPDF((cab.getString("dni_recibe") != null) ? cab.getString("dni_recibe") : "", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_cab.addCell(Addons.setCeldaPDF("Responsable actual: ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_cab.addCell(Addons.setCeldaPDF(persona_entrega, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_cab.addCell(Addons.setCeldaPDF("Nuevo Responsable: ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_cab.addCell(Addons.setCeldaPDF(persona_recibe, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    document.add(tbl_cab);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            document.add(new Paragraph(" "));

            try {
                Double total = 0.0;
                Double precio_tot = 0.0;
                Double tot_sub = 0.0;
                Double tot_iva = 0.0;
                Double valor_dep = 0.0;
                Double total_dep = 0.0;
                PdfPTable tbl_det = new PdfPTable(new float[]{20f, 30f, 10f, 10f, 10f, 10f});
                tbl_det.addCell(Addons.setCeldaPDF("INFORMACIÓN BÁSICA DE LOS ACTIVOS FIJOS", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 0, 3, 6));
                tbl_det.addCell(Addons.setCeldaPDF("CÓDIGO", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_CENTER, 1));
                tbl_det.addCell(Addons.setCeldaPDF("DESCRIPCIÓN", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_CENTER, 1));
                tbl_det.addCell(Addons.setCeldaPDF("SUBTOTAL", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_CENTER, 1));
                tbl_det.addCell(Addons.setCeldaPDF("IVA", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_CENTER, 1));
                tbl_det.addCell(Addons.setCeldaPDF("PRECIO TOTAL", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_CENTER, 1));
                tbl_det.addCell(Addons.setCeldaPDF("COSTO REAL", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_CENTER, 1));
                while (det.next()) {
                    tbl_det.addCell(Addons.setCeldaPDF((det.getString("codigo_activo") != null) ? det.getString("codigo_activo") : "", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((det.getString("descripcion") != null) ? det.getString("descripcion") : "", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((det.getString("valor_compra") != null) ? det.getString("valor_compra") : "", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF((det.getString("iva") != null) ? Addons.redondear(det.getString("iva")) : "", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));

                    precio_tot = Double.parseDouble(det.getString("valor_compra")) + Double.parseDouble(det.getString("iva"));
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(Addons.redondear(precio_tot)), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));

                    valor_dep = Double.parseDouble(det.getString("valor_compra")) - Double.parseDouble(det.getString("valor_depreciado"));
                    tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(Addons.redondear(valor_dep)), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 1));

                    total_dep += valor_dep;
                    tot_sub += Double.parseDouble(det.getString("valor_compra"));
                    tot_iva += Double.parseDouble(det.getString("iva"));
                    total += Double.parseDouble(det.getString("valor_compra")) + Double.parseDouble(det.getString("iva"));
                }
                tbl_det.addCell(Addons.setCeldaPDF("GESTION DE ENVIO", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1, 3, 2));
                tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(Addons.redondear(gestion_envio)), Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_RIGHT, 1));
                tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(Addons.redondear(gestion_envio * 0.12)), Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_RIGHT, 1));
                tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(Addons.redondear(gestion_envio) + (gestion_envio * 0.12)), Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_RIGHT, 1));
                tbl_det.addCell(Addons.setCeldaPDF("0.0", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_RIGHT, 1));

                tot_sub += gestion_envio;
                tot_iva += gestion_envio * 0.12;
                total += gestion_envio + (gestion_envio * 0.12);

                tbl_det.addCell(Addons.setCeldaPDF("TOTAL", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1, 3, 2));
                tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(Addons.redondear(tot_sub)), Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_RIGHT, 1));
                tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(Addons.redondear(tot_iva)), Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_RIGHT, 1));
                tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(Addons.redondear(total)), Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_RIGHT, 1));
                tbl_det.addCell(Addons.setCeldaPDF(String.valueOf(Addons.redondear(total_dep)), Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_RIGHT, 1));
                document.add(tbl_det);
            } catch (Exception e) {
                e.printStackTrace();
            }

            document.add(new Paragraph(" "));

            PdfPTable tbl_obs = new PdfPTable(1);
            tbl_obs.addCell(Addons.setCeldaPDF("CLÁUSULA DE COMPROMISO", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 0));
            tbl_obs.addCell(Addons.setCeldaPDF(observacion, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
            document.add(tbl_obs);

            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            PdfPTable tbl_firmas = new PdfPTable(2);

            tbl_firmas.addCell(Addons.setCeldaPDF("  ENTREGA  ", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 0));
            tbl_firmas.addCell(Addons.setCeldaPDF("  RECIBE", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 0));            
            tbl_firmas.addCell(Addons.setCeldaPDFOculta("  _firma_entrega_  ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
            tbl_firmas.addCell(Addons.setCeldaPDFOculta("  _firma_recibe_  ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
            tbl_firmas.addCell(Addons.setCeldaPDF("  " + (entregaUnTercero.compareTo("")!=0 ? entregaUnTercero : persona_entrega) + "  ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
            tbl_firmas.addCell(Addons.setCeldaPDF("  " + persona_recibe + "  ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
            
            if ( mCorresponsables != null ) {
                
                for(int i=0; i<mCorresponsables.length; i=i+2 ) {
                    
                    //  salto de espacio para firmas
                    tbl_firmas.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 0, 35, 2));

                    tbl_firmas.addCell(Addons.setCeldaPDF("  AUTORIZA  ", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 0));
                        
                    //  saber si el total de los responsables es par
                    if( mCorresponsables.length % 2 == 0 || i+1 < mCorresponsables.length ) { 
                        tbl_firmas.addCell(Addons.setCeldaPDF("  AUTORIZA  ", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 0));
                    } else {    //  completo con un espacio en blanco
                        tbl_firmas.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 0));
                    }
                    
                    tbl_firmas.addCell(Addons.setCeldaPDFOculta("  _firma_"+mCorresponsables[i][1]+"_  ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
                    
                    //  saber si el total de los responsables es par
                    if( mCorresponsables.length % 2 == 0 || i+1 < mCorresponsables.length) { 
                        tbl_firmas.addCell(Addons.setCeldaPDFOculta("  _firma_" + mCorresponsables[i+1][1] + "_  ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    } else {    //  completo con un espacio en blanco
                        tbl_firmas.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 0));
                    }
                    
                    tbl_firmas.addCell(Addons.setCeldaPDF("  " + mCorresponsables[i][2] + "  ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    
                    //  saber si el total de los responsables es par
                    if( mCorresponsables.length % 2 == 0 || i+1 < mCorresponsables.length) { 
                        tbl_firmas.addCell(Addons.setCeldaPDF("  " + mCorresponsables[i+1][2] + "  ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    } else {    //  completo con un espacio en blanco
                        tbl_firmas.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 0));
                    }
                    
                }
            }
            document.add(tbl_firmas);
//            PdfPTable tbl_cerrar = new PdfPTable(1);
//            tbl_cerrar.addCell(Addons.setCeldaPDFOculta("  .  ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
//            document.add(tbl_cerrar);
            resultado = documento.getAbsolutePath();
        } catch (IllegalStateException ie) {
            ie.printStackTrace();
        } catch (DocumentException de) {
            de.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        document.close(); // paso 5
        /* fin PDF */
        return resultado;
    }


    public String PdfOrdenConsumo(String id, File documento) {
        String resultado = null;
        Document document = new Document(PageSize.A4);// paso 1
        document.setMargins(0, 0, 50, 30);
        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, this._usuario, this._clave);
        String titulo = conf.getValor("razon_social");
        conf.cerrar();
        DataBase ObjDataBase = new DataBase(_ip, _puerto, _db, _usuario, _clave);
        try {
            DecimalFormat df = new DecimalFormat("####.##");
            PdfWriter.getInstance(document, new FileOutputStream(documento));
            document.open();
            PdfPTable tbl_cab = new PdfPTable(1);
            tbl_cab.addCell(Addons.setCeldaPDF(titulo, Font.HELVETICA, 14, Font.NORMAL, Element.ALIGN_CENTER, 0));
            tbl_cab.addCell(Addons.setCeldaPDF("ACTA ENTREGA RECEPCION", Font.HELVETICA, 14, Font.NORMAL, Element.ALIGN_CENTER, 0));
            ResultSet rs = ObjDataBase.consulta("select id_ordenconsumo,fec_ordenconsumo,sucursalr,nombresr,sucursale,nombrese,"
                    + " fec_peticion,fec_recepcion,txt_estado_recibido,id_sucursalr from vta_ordenesdeconsumo"
                    + " where id_ordenconsumo=" + id + ";");
            String id3 = "-1";
            double suma3 = 0;
            if (rs.next()) {
                id3 = (rs.getString(1) != null ? rs.getString(1) : "-1");
                tbl_cab.addCell(Addons.setCeldaPDF("No.   " + ((rs.getString(1) != null && rs.getString(10) != null) ? rs.getString(10) + " - " + rs.getString(1) : ""), Font.HELVETICA, 12, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                document.add(tbl_cab);
                PdfPTable tbl_encab = new PdfPTable(2);
                tbl_encab.addCell(Addons.setCeldaPDF("PEDIDO", Font.HELVETICA, 11, Font.BOLD, Element.ALIGN_LEFT, 0));
                tbl_encab.addCell(Addons.setCeldaPDF("ENVIA", Font.HELVETICA, 11, Font.BOLD, Element.ALIGN_LEFT, 0));
                tbl_encab.addCell(Addons.setCeldaPDF("Sucursal: " + ((rs.getString(3) != null) ? rs.getString(3) : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_encab.addCell(Addons.setCeldaPDF("Sucursal: " + ((rs.getString(5) != null) ? rs.getString(5) : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_encab.addCell(Addons.setCeldaPDF("Usuario: " + ((rs.getString(4) != null) ? rs.getString(4) : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_encab.addCell(Addons.setCeldaPDF("Usuario: " + ((rs.getString(6) != null) ? rs.getString(6) : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_encab.addCell(Addons.setCeldaPDF("Fecha: " + ((rs.getString(7) != null) ? Fecha.SQLaISO(rs.getString(7)) : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_encab.addCell(Addons.setCeldaPDF("Fecha: " + ((rs.getString(8) != null) ? Fecha.SQLaISO(rs.getString(8)) : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
//                tbl_encab.addCell(Addons.setCeldaPDF("Estado: " + ((rs.getString(9) != null) ? rs.getString(9) : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
//                tbl_encab.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 11, Font.BOLD, Element.ALIGN_LEFT, 0));
                document.add(tbl_encab);
                String[] cabTabla = new String[]{"CODIGO - DESCRIPCIÓN", "CANT. REQUERIDA", "P. UNITARIO", "TIPO", "P. TOTAL"};
                float[] anchoTabla = new float[]{145f, 55f, 55f, 55f, 55f};
                document.add(Addons.setCabeceraTabla(cabTabla, anchoTabla));
                PdfPTable tbl_det = new PdfPTable(anchoTabla);
                ResultSet rsdetalle = ObjDataBase.consulta("select (idcodigo ||' '|| detproducto)as detalle,cant_ordenconsumo,detalle_cantidad,"
                        + " case est_traspasoe"
                        + " 	when '1' then 'ENTREGADO'"
                        + " 	when '0' then 'NO ENTREGADO'"
                        + " 	else 'Desconocido'"
                        + " end as txtestado,"
                        + " case tipo_producto"
                        + " 	when '1' then 'ACTIVO'"
                        + " 	when 'p' then 'PRODUCTO'"
                        + " 	when 'r' then 'SUMINISTRO'"
                        + " 	else 'Desconocido'"
                        + " end as txtestado"
                        + " from tbl_detordenconsumo where id_ordenconsumo=" + id3 + "");
                while (rsdetalle.next()) {
                    tbl_det.addCell(Addons.setCeldaPDF(((rsdetalle.getString(1) != null) ? rsdetalle.getString(1) : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF(((rsdetalle.getString(2) != null) ? rsdetalle.getString(2) : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF(((rsdetalle.getString(3) != null) ? rsdetalle.getString(3) : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
//                    tbl_det.addCell(Addons.setCeldaPDF(((rsdetalle.getString(4) != null) ? rsdetalle.getString(4) : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF(((rsdetalle.getString(5) != null) ? rsdetalle.getString(5) : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF(((rsdetalle.getString(2) != null && rsdetalle.getString(3) != null) ? "" + (df.format(rsdetalle.getDouble(2) * rsdetalle.getDouble(3))) : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    suma3 += (rsdetalle.getString(2) != null && rsdetalle.getString(3) != null) ? rsdetalle.getDouble(2) * rsdetalle.getDouble(3) : 0;
                }
                tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
//                tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                tbl_det.addCell(Addons.setCeldaPDF("" + df.format(suma3), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));

                rsdetalle.close();
                document.add(tbl_det);
            }
            rs.close();
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            PdfPTable tbl_firmas = new PdfPTable(2);
            tbl_firmas.addCell(Addons.setCeldaPDF("ENTREGADO", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
            tbl_firmas.addCell(Addons.setCeldaPDF("RECIBIDO", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
            tbl_firmas.addCell(Addons.setCeldaPDFOculta("  _firma_autoriza_  ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
            tbl_firmas.addCell(Addons.setCeldaPDFOculta("  _firma_recibe_  ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
            document.add(tbl_firmas);
            PdfPTable tbl_cerrar = new PdfPTable(1);
            tbl_cerrar.addCell(Addons.setCeldaPDFOculta("  .  ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
            document.add(tbl_cerrar);
            document.close();
            resultado = documento.getAbsolutePath();

        } catch (Exception e) {
            System.out.println("" + e.getMessage());
            resultado = null;
        } finally {
            ObjDataBase.cerrar();
        }
        return resultado;
    }

    public String PdfTranspaso(String id, File documento) {
        String resultado = null;
        Document document = new Document(PageSize.A4);// paso 1
        document.setMargins(0, 0, 50, 30);
        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, this._usuario, this._clave);
        String titulo = conf.getValor("razon_social");
        conf.cerrar();
        Traspaso objTraspaso = new Traspaso(_ip, _puerto, _db, _usuario, _clave);
        try {
            DecimalFormat df = new DecimalFormat("####.##");
            PdfWriter.getInstance(document, new FileOutputStream(documento));
            document.open();
            ResultSet rsTraspaso = objTraspaso.getTraspasoCaja(id);
            ResultSet rsTraspasoDetalle = objTraspaso.getTraspasoDetalleCaja(id);
            String[] cabTabla = new String[]{"Codigo", "Descripcion", "Cant. Enviada", "Cant. Recibida", "Precio Unitario", "Subtotal", "Iva", "Precio Total"};
            float[] anchoTabla = new float[]{55f, 120f, 40f, 40f, 40f, 40f, 40f, 40f};
            if (rsTraspaso.next()) {
                PdfPTable tbl_cab = new PdfPTable(1);
                tbl_cab.addCell(Addons.setCeldaPDF(titulo, Font.HELVETICA, 14, Font.NORMAL, Element.ALIGN_CENTER, 0));
                tbl_cab.addCell(Addons.setCeldaPDF("ORDEN DE TRASPASO DE MERCADERIA", Font.HELVETICA, 14, Font.NORMAL, Element.ALIGN_CENTER, 0));
                tbl_cab.addCell(Addons.setCeldaPDF("No.   " + ((rsTraspaso.getString("num_traspaso") != null) ? rsTraspaso.getString("num_traspaso") : ""), Font.HELVETICA, 12, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                document.add(tbl_cab);

                PdfPTable tbl_encab = new PdfPTable(2);

                tbl_encab.addCell(Addons.setCeldaPDF("ORIGEN", Font.HELVETICA, 11, Font.BOLD, Element.ALIGN_LEFT, 0));
                tbl_encab.addCell(Addons.setCeldaPDF("RECEPCION", Font.HELVETICA, 11, Font.BOLD, Element.ALIGN_LEFT, 0));

                tbl_encab.addCell(Addons.setCeldaPDF("Sucursal: " + ((rsTraspaso.getString("origen") != null) ? rsTraspaso.getString("origen") : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_encab.addCell(Addons.setCeldaPDF("Sucursal: " + ((rsTraspaso.getString("recepcion") != null) ? rsTraspaso.getString("recepcion") : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));

                tbl_encab.addCell(Addons.setCeldaPDF("Usuario: " + ((rsTraspaso.getString("usuario_origen") != null) ? rsTraspaso.getString("usuario_origen") : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_encab.addCell(Addons.setCeldaPDF("Usuario: " + ((rsTraspaso.getString("usuario_recepcion") != null) ? rsTraspaso.getString("usuario_recepcion") : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));

                tbl_encab.addCell(Addons.setCeldaPDF("Fecha: " + ((rsTraspaso.getString("fecha_envio") != null) ? Fecha.SQLaISO(rsTraspaso.getString("fecha_envio")) : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_encab.addCell(Addons.setCeldaPDF("Fecha: " + ((rsTraspaso.getString("fecha_recepcion") != null) ? Fecha.SQLaISO(rsTraspaso.getString("fecha_recepcion")) : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));

                document.add(tbl_encab);

                document.add(Addons.setCabeceraTabla(cabTabla, anchoTabla));

                PdfPTable tbl_det = new PdfPTable(anchoTabla);

                ////2018-04-25
                boolean activo;
                double iva_actual = 0.12;
                double cantidad_traspaso = 0.0;
                double p_u = 0.0;
                double p_st = 0.0;
                double iva = 0.0;
                double total = 0.0;
                double sumap_u = 0.0;
                double sumap_st = 0.0;
                double sumaiva = 0.0;
                double sumatotal = 0.0;
                ////*/****
                try {
                    while (rsTraspasoDetalle.next()) {
                        tbl_det.addCell(Addons.setCeldaPDF(((rsTraspasoDetalle.getString("codigo") != null) ? rsTraspasoDetalle.getString("codigo") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_det.addCell(Addons.setCeldaPDF(((rsTraspasoDetalle.getString("descripcion") != null) ? rsTraspasoDetalle.getString("descripcion") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_det.addCell(Addons.setCeldaPDF(((rsTraspasoDetalle.getString("cant_enviada") != null) ? rsTraspasoDetalle.getString("cant_enviada") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_det.addCell(Addons.setCeldaPDF(((rsTraspasoDetalle.getString("cant_recibida") != null) ? rsTraspasoDetalle.getString("cant_recibida") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        cantidad_traspaso = (rsTraspasoDetalle.getString("cant_enviada") != null ? (Double.parseDouble(rsTraspasoDetalle.getString("cant_enviada"))) : 0.0);
                        ///
                        activo = ((rsTraspasoDetalle.getString("de_activo") != null) ? rsTraspasoDetalle.getBoolean("de_activo") : true);
                        String id_factura = ((rsTraspasoDetalle.getString("id_factura_compra") != null) ? rsTraspasoDetalle.getString("id_factura_compra") : "-1");
                        String id_producto = ((rsTraspasoDetalle.getString("id_producto") != null) ? rsTraspasoDetalle.getString("id_producto") : "-1");
                        String[] precios = objTraspaso.ObtenerPrecios(id_factura, id_producto, activo);
                        p_u = (precios[1] != null ? (Double.parseDouble(precios[1].toString())) : 0.0);
                        p_st = (precios[1] != null ? (Double.parseDouble(precios[1].toString()) * cantidad_traspaso) : 0.0);
                        iva = (precios[1] != null ? (p_st * iva_actual) : 0.0);
                        total = (precios[1] != null ? (p_st + iva) : 0.0);
                        sumap_u += p_u;
                        sumap_st += p_st;
                        sumaiva += iva;
                        sumatotal += total;

                        tbl_det.addCell(Addons.setCeldaPDF("" + p_u, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("" + df.format(p_st), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("" + df.format(iva), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("" + df.format(total), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        /////*****/
                    }

                    rsTraspasoDetalle.close();
                    /**
                     *
                     */
                    tbl_det.addCell(Addons.setCeldaPDF("TOTAL", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("" + df.format(sumap_u), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("" + df.format(sumap_st), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("" + df.format(sumaiva), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("" + df.format(sumatotal), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    ////*****////
                } catch (Exception e) {
                    e.printStackTrace();
                }
                document.add(tbl_det);
            }

            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            PdfPTable tbl_firmas = new PdfPTable(2);
            tbl_firmas.addCell(Addons.setCeldaPDF("AUTORIZADO", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
            tbl_firmas.addCell(Addons.setCeldaPDF("RECIBIDO", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
            tbl_firmas.addCell(Addons.setCeldaPDFOculta("  _firma_autoriza_  ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
            tbl_firmas.addCell(Addons.setCeldaPDFOculta("  _firma_recibe_  ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
            document.add(tbl_firmas);
            PdfPTable tbl_cerrar = new PdfPTable(1);
            tbl_cerrar.addCell(Addons.setCeldaPDFOculta("  .  ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
            document.add(tbl_cerrar);

            rsTraspaso.close();

            document.close(); // paso 5
            resultado = documento.getAbsolutePath();

        } catch (Exception e) {
            System.out.println("" + e.getMessage());
            resultado = null;
        } finally {
            objTraspaso.cerrar();
        }
        return resultado;
    }

    public String PdfPedido(String id, File documento) {
        String resultado = null;
        Document document = new Document(PageSize.A4);// paso 1
        document.setMargins(0, 0, 50, 30);
        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, this._usuario, this._clave);
        String titulo = conf.getValor("razon_social");
        conf.cerrar();
        DataBase ObjDatabase = new DataBase(_ip, _puerto, _db, _usuario, _clave);
        try {
            DecimalFormat df = new DecimalFormat("####.##");
            PdfWriter.getInstance(document, new FileOutputStream(documento));
            document.open();
            PdfPTable tbl_cab = new PdfPTable(1);
            tbl_cab.addCell(Addons.setCeldaPDF(titulo, Font.HELVETICA, 14, Font.NORMAL, Element.ALIGN_CENTER, 0));
            tbl_cab.addCell(Addons.setCeldaPDF("ORDEN DE REQUISICIÓN DE MERCADERIA", Font.HELVETICA, 14, Font.NORMAL, Element.ALIGN_CENTER, 0));
            ResultSet rs = ObjDatabase.consulta("select r.id_requesicion,r.id_sucursal,r.fec_requesicion,(r.nombre || ' '|| r.apellido )as empleado,r.txt_est_requesicion,s.sucursal from vta_requesiciones r "
                    + " inner join tbl_sucursal s on s.id_sucursal=r.id_sucursal "
                    + " where id_requesicion=" + id + ";");
            String id1 = "-1";
            if (rs.next()) {
                id1 = (rs.getString(1) != null ? rs.getString(1) : "-1");
                tbl_cab.addCell(Addons.setCeldaPDF("No.   " + ((rs.getString(1) != null && rs.getString(2) != null) ? rs.getString(2) + " - " + rs.getString(1) : ""), Font.HELVETICA, 12, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                document.add(tbl_cab);
                PdfPTable tbl_encab = new PdfPTable(2);
                tbl_encab.addCell(Addons.setCeldaPDF("ORIGEN", Font.HELVETICA, 11, Font.BOLD, Element.ALIGN_LEFT, 0));
                tbl_encab.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 11, Font.BOLD, Element.ALIGN_LEFT, 0));
                tbl_encab.addCell(Addons.setCeldaPDF("Sucursal: " + ((rs.getString(6) != null) ? rs.getString(6) : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_encab.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 11, Font.BOLD, Element.ALIGN_LEFT, 0));
                tbl_encab.addCell(Addons.setCeldaPDF("Usuario: " + ((rs.getString(4) != null) ? rs.getString(4) : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_encab.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 11, Font.BOLD, Element.ALIGN_LEFT, 0));
                tbl_encab.addCell(Addons.setCeldaPDF("Fecha: " + ((rs.getString(3) != null) ? Fecha.SQLaISO(rs.getString(3)) : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_encab.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 11, Font.BOLD, Element.ALIGN_LEFT, 0));
                tbl_encab.addCell(Addons.setCeldaPDF("Estado: " + ((rs.getString(5) != null) ? rs.getString(5) : ""), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_encab.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 11, Font.BOLD, Element.ALIGN_LEFT, 0));
                document.add(tbl_encab);
                String[] cabTabla = new String[]{"CODIGO - DESCRIPCIÓN", "CANT. PEDIDA", "CANT. REVISADA", "CANT. APROBADA", "TIPO", "ESTADO", "PROVEEDOR"};
                float[] anchoTabla = new float[]{145f, 50f, 50f, 50f, 47f, 53f, 70f};
                document.add(Addons.setCabeceraTabla(cabTabla, anchoTabla, 8));
                PdfPTable tbl_det = new PdfPTable(anchoTabla);
                ResultSet rsdetalle = ObjDatabase.consulta("select d.detalle_producto,d.cant_requesicion,"
                        + " case d.tip_producto"
                        + " 	when '1' then 'ACTIVO'"
                        + " 	when 'p' then 'PRODUCTO'"
                        + " 	when 'r' then 'SUMINISTRO'"
                        + " 	else 'DESCONOCIDO'"
                        + " end as txttipo,"
                        + " case d.estadostock"
                        + " 	when '1' then 'CONFIRMADO'"
                        + " 	when '-2' then 'PENDIENTE'"
                        + " 	when '0' then 'NO REQUERIDO'"
                        + " 	else 'SIN CONFIRMAR'"
                        + " end as txtestado, "
                        + " cant_pedida, "
                        + " cant_revisada, "
                        + " (select xy.razon_social from vta_proveedor as xy where xy.id_proveedor =d.id_proveedor)as proveedor "
                        + " from tbl_des_requesicion d"
                        + " where d.id_requesicion=" + id1 + "");
                while (rsdetalle.next()) {
                    double cant_pedida = ((rsdetalle.getString(5) != null) ? rsdetalle.getDouble(5) : 0);
                    double cant_aprobada = ((rsdetalle.getString(2) != null) ? rsdetalle.getDouble(2) : 0);
                    double cant_revisada = ((rsdetalle.getString(6) != null) ? rsdetalle.getDouble(6) : 0);
                    tbl_det.addCell(Addons.setCeldaPDF(((rsdetalle.getString(1) != null) ? rsdetalle.getString(1) : ""), Font.HELVETICA, 7, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("" + Addons.redondear(cant_pedida, 2), Font.HELVETICA, 7, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("" + Addons.redondear(cant_revisada, 2), Font.HELVETICA, 7, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("" + Addons.redondear(cant_aprobada, 2), Font.HELVETICA, 7, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF(((rsdetalle.getString(3) != null) ? rsdetalle.getString(3) : ""), Font.HELVETICA, 7, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF(((rsdetalle.getString(4) != null) ? rsdetalle.getString(4) : ""), Font.HELVETICA, 7, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF(((rsdetalle.getString(7) != null) ? rsdetalle.getString(7) : ""), Font.HELVETICA, 7, Font.NORMAL, Element.ALIGN_LEFT, 1));

                }
                rsdetalle.close();
                document.add(tbl_det);
            }
            rs.close();
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            PdfPTable tbl_firmas = new PdfPTable(2);
            tbl_firmas.addCell(Addons.setCeldaPDF("AUTORIZADO", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
            tbl_firmas.addCell(Addons.setCeldaPDF("RECIBIDO", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
            tbl_firmas.addCell(Addons.setCeldaPDFOculta("  _firma_autoriza_  ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
            tbl_firmas.addCell(Addons.setCeldaPDFOculta("  _firma_recibe_  ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
            document.add(tbl_firmas);
            PdfPTable tbl_cerrar = new PdfPTable(1);
            tbl_cerrar.addCell(Addons.setCeldaPDFOculta("  .  ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
            document.add(tbl_cerrar);

            document.close(); // paso 5
            resultado = documento.getAbsolutePath();

        } catch (Exception e) {
            System.out.println("" + e.getMessage());
            resultado = null;
        } finally {
            ObjDatabase.cerrar();
        }
        return resultado;
    }

    public String PdfOrdenTrabajoCliente(String id, File documento) {
        /* inicio PDF */
        String resultado = "";
        Document document = new Document(PageSize.A4.rotate());// paso 1
        document.setMargins(-90, -90, 50, 0);
        /*Izquierda, derecha, tope, pie */

        OrdenTrabajo objOrdenTrabajo = new OrdenTrabajo(_ip, _puerto, _db, _usuario, _clave);
        Sector objSector = new Sector(_ip, _puerto, _db, _usuario, _clave);
        PlanServicio objPlanServicio = new PlanServicio(_ip, _puerto, _db, _usuario, _clave);
        FacturaVenta objFacturaVenta = new FacturaVenta(_ip, _puerto, _db, _usuario, _clave);
        Archivo archivo = new Archivo(_ipdocumental, _puertodocumental, _dbdocumental, _usuariodocumental, _clavedocumental);
        Spliter objSplitter = new Spliter(_ip, _puerto, _db, _usuario, _clave);
        Archivo oarchivo = new Archivo(_ip, _puerto, _db, _usuario, _clave);
        String logo = this._URL_ANEXOS + "dir/" + oarchivo.getArchivo(this._dir, 1);
        oarchivo.cerrar();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(documento)); // paso 2
            document.open(); // paso 3
            /* todo el cuerpo del doc es el paso 4 */
            int i = 0;
            String id_instalacion_certificado = "-1";
            String coresponsables = "";

//  ORDENES DE TRABAJO  /////////////////////////////////////////////////////////////////////////////////////////////////////
            String sector = "";
            String plan = "";

            int tipo_trabajo = 1;
            document.setPageSize(PageSize.A4);
            document.setMargins(0, 0, 30, 0);
            /*Izquierda, derecha, tope, pie */
            ResultSet RS = objOrdenTrabajo.getOrdenTrabajo(id);
            String id_sector = "";
            String id_plan_contratado = "";
            String radusername = "";
            String radclave = "";
            String deviceclave = "";
            String latitud = "";
            String longitud = "";
            String puesta_tierra = "";
            String id_instalacionqr = "";
            String puerto_instalacion[] = {"", "", "", "", "", "", "", "", ""};
            String id_instalacion_final = "-1";
            String tipo_instalacion_final = "a";
            String latitud_gps = "";
            String longitud_gps = "";
            while (RS.next()) {
                id_instalacion_final = RS.getString("id_instalacion") != null ? RS.getString("id_instalacion") : "-1";
                tipo_instalacion_final = RS.getString("tipo_instalacion") != null ? RS.getString("tipo_instalacion") : "a";
                String id_cliente = RS.getString("id_cliente") != null ? RS.getString("id_cliente") : "-1";
                tipo_trabajo = RS.getString("tipo_trabajo") != null ? RS.getInt("tipo_trabajo") : 1;
                latitud = (RS.getString("latitud") != null) ? RS.getString("latitud") : "";
                longitud = (RS.getString("longitud") != null) ? RS.getString("longitud") : "";
                puesta_tierra = (RS.getBoolean("puesta_tierra") == true) ? "x" : "";
                id_instalacion_certificado = (RS.getString("id_instalacion_certificado") != null ? RS.getString("id_instalacion_certificado") : "-1");
                id_instalacionqr = (RS.getString("contrapartida_pichincha") != null ? RS.getString("contrapartida_pichincha") : "-1");
                latitud_gps = (RS.getString("latitud_gps") != null) ? RS.getString("latitud_gps") : "";
                longitud_gps = (RS.getString("longitud_gps") != null) ? RS.getString("longitud_gps") : "";
                document.newPage();

                PdfPTable tbl_titulo = new PdfPTable(new float[]{20f, 80f});
                try {
                    tbl_titulo.addCell(Addons.setLogo(logo, 70, 70));
                } catch (Exception e) {
                    tbl_titulo.addCell("");
                }

                tbl_titulo.addCell(Addons.setCeldaPDF("\nORDEN DE TRABAJO DE INTERNET INALAMBRICO \n"
                        + (RS.getString("txt_tipo_trabajo") != null ? RS.getString("txt_tipo_trabajo").toUpperCase() : ""), Font.HELVETICA, 14, Font.BOLD, Element.ALIGN_CENTER, 0));
                document.add(tbl_titulo);

                document.add(new Paragraph(" "));

                radusername = RS.getString("radusername") != null ? RS.getString("radusername") : "";
                radclave = RS.getString("radclave") != null ? RS.getString("radclave") : "";
                deviceclave = RS.getString("deviceclave") != null ? RS.getString("deviceclave") : "";

                id_sector = RS.getString("id_sector") != null ? RS.getString("id_sector") : "-1";
                try {

                    ResultSet rsContrato = objSector.getSector(id_sector);
                    if (rsContrato.next()) {
                        sector = rsContrato.getString("sector") != null ? rsContrato.getString("sector") : "";
                        rsContrato.close();
                    }
                } catch (Exception ec) {
                    ec.printStackTrace();
                }

                id_plan_contratado = RS.getString("id_plan_contratado") != null ? RS.getString("id_plan_contratado") : "-1";
                puerto_instalacion = objSplitter.GetPuertoInstalacion(id_instalacion_final);
                try {
                    ResultSet rsPlanServicio = objPlanServicio.getVelocidad(id_plan_contratado);
                    if (rsPlanServicio.next()) {
                        plan = rsPlanServicio.getString("plan") != null ? rsPlanServicio.getString("plan") : "";
                        rsPlanServicio.close();
                    }
                } catch (Exception ep) {
                    ep.printStackTrace();
                }

                switch (tipo_trabajo) {

                    case 3:
                        PdfPTable tbl_det1 = new PdfPTable(new float[]{35f, 65f, 35f, 65f});
                        tbl_det1.addCell(Addons.setCeldaPDF("No." + (RS.getString("id_sucursal") != null ? RS.getString("id_sucursal") : "") + "-" + (RS.getString("num_orden") != null ? RS.getString("num_orden") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0, 3, 4));

                        tbl_det1.addCell(Addons.setCeldaPDF("RESPONSABLE", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det1.addCell(Addons.setCeldaPDF((RS.getString("responsable") != null ? RS.getString("responsable") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_det1.addCell(Addons.setCeldaPDF("CEDULA", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det1.addCell(Addons.setCeldaPDF((RS.getString("ruc") != null ? RS.getString("ruc") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        if (coresponsables.trim().compareTo("") != 0) {
                            tbl_det1.addCell(Addons.setCeldaPDF("CORRESPONSABLE", Font.HELVETICA, 7, Font.BOLD, Element.ALIGN_LEFT, 1));
                            tbl_det1.addCell(Addons.setCeldaPDF(coresponsables, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1, 3, 3));

                        }
                        tbl_det1.addCell(Addons.setCeldaPDF("CLIENTE", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det1.addCell(Addons.setCeldaPDF((RS.getString("razon_social") != null ? RS.getString("razon_social") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_det1.addCell(Addons.setCeldaPDF("TELEFONOS:", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det1.addCell(Addons.setCeldaPDF((RS.getString("telefono") != null ? RS.getString("telefono") : "")
                                + "    Claro:" + (RS.getString("movil_claro") != null ? RS.getString("movil_claro") : "")
                                + "    Movistar:" + (RS.getString("movil_movistar") != null ? RS.getString("movil_movistar") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_det1.addCell(Addons.setCeldaPDF("CIUDAD", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det1.addCell(Addons.setCeldaPDF((RS.getString("ciudad") != null ? RS.getString("ciudad") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_det1.addCell(Addons.setCeldaPDF("DIRECCION DE INSTALACION", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det1.addCell(Addons.setCeldaPDF((RS.getString("direccion_instalacion") != null ? RS.getString("direccion_instalacion") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1, 3, 3));

                        tbl_det1.addCell(Addons.setCeldaPDF("FECHA DE ORDEN DE INSTALACION", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det1.addCell(Addons.setCeldaPDF((RS.getString("fecha_registro") != null ? Fecha.SQLaISO(RS.getString("fecha_registro")) : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_det1.addCell(Addons.setCeldaPDF("FECHA DE INSTALACION REAL", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det1.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_det1.addCell(Addons.setCeldaPDF("COSTO DE INSTALACION", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det1.addCell(Addons.setCeldaPDF((RS.getString("costo_instalacion") != null ? RS.getString("costo_instalacion") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_det1.addCell(Addons.setCeldaPDF("SECTOR", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det1.addCell(Addons.setCeldaPDF(sector, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_det1.addCell(Addons.setCeldaPDF("DIRECCION IP", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det1.addCell(Addons.setCeldaPDF((RS.getString("ip") != null ? RS.getString("ip") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_det1.addCell(Addons.setCeldaPDF("DIRECCION MAC", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det1.addCell(Addons.setCeldaPDF((RS.getString("mac") != null ? RS.getString("mac") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_det1.addCell(Addons.setCeldaPDF("CLAVE DISPOSITIVO", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det1.addCell(Addons.setCeldaPDF(deviceclave, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_det1.addCell(Addons.setCeldaPDF("RECEPTOR DE SEÑAL", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det1.addCell(Addons.setCeldaPDF((RS.getString("receptor") != null ? RS.getString("receptor") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_det1.addCell(Addons.setCeldaPDF("PORCENTAJE DE SEÑAL", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det1.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_det1.addCell(Addons.setCeldaPDF("ANTENA DE TRASMISION ACOPLADA", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det1.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_det1.addCell(Addons.setCeldaPDF("PLAN CONTRATADO", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det1.addCell(Addons.setCeldaPDF(plan, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_det1.addCell(Addons.setCeldaPDF("COMPARTICION", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det1.addCell(Addons.setCeldaPDF((RS.getString("comparticion") != null ? RS.getString("comparticion") : "") + " - 1", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_det1.addCell(Addons.setCeldaPDF("PLAN ESTABLECIDO", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det1.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1, 3, 3));

                        tbl_det1.addCell(Addons.setCeldaPDF("OBSERVACIONES", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det1.addCell(Addons.setCeldaPDF((RS.getString("diagnostico_tecnico") != null ? RS.getString("diagnostico_tecnico") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1, 3, 3));

                        tbl_det1.addCell(Addons.setCeldaPDF("UBICACION GEOGRAFICA LATITUD", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det1.addCell(Addons.setCeldaPDF("_____ °  _____ '  _____ ''  N  S\n" + latitud, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_det1.addCell(Addons.setCeldaPDF("UBICACION GEOGRAFICA LONGITUD", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det1.addCell(Addons.setCeldaPDF("_____ °  _____ '  _____ ''  E  O\n" + longitud, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_det1.addCell(Addons.setCeldaPDF("LATITUD DECIMAL", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det1.addCell(Addons.setCeldaPDF("" + latitud_gps, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_det1.addCell(Addons.setCeldaPDF("LONGITUD DECIMAL", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det1.addCell(Addons.setCeldaPDF("" + longitud_gps, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_det1.addCell(Addons.setCeldaPDF("ALTURA DE LA ESTRUCTURA s.n.m.", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det1.addCell(Addons.setCeldaPDF("_________ (m)", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_det1.addCell(Addons.setCeldaPDF("ALTURA DE LA ESTRUCTURA (BASE-CIMA)", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det1.addCell(Addons.setCeldaPDF("_________ (m)", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_det1.addCell(Addons.setCeldaPDF("CONFORMIDAD DE VELOCIDAD ESTABLECIDA", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det1.addCell(Addons.setCeldaPDF(" ___ SI \n ___ NO\n ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_det1.addCell(Addons.setCeldaPDF("CONFORMIDAD DE LA ADECUADA INSTALACION", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det1.addCell(Addons.setCeldaPDF(" ___ Excelente \n ___ Buena \n ___ Mala\n ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_det1.addCell(Addons.setCeldaPDF("CONFORMIDAD DE LA ATENCION RECIBIDA", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det1.addCell(Addons.setCeldaPDF(" ___ Excelente \n ___ Buena \n ___ Mala\n ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_det1.addCell(Addons.setCeldaPDF("ESTADO DE LA INSTALACION", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det1.addCell(Addons.setCeldaPDF(" ___ Emitido \n ___ Instalado\n ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_det1.addCell(Addons.setCeldaPDF("EL CLIENTE TIENE CONEXIÓN A TIERRA?: [ " + puesta_tierra + " ]", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1, 3, 4));

                        tbl_det1.addCell(Addons.setCeldaPDF("DESCRIPCION DE MATERIALES UTILIZADOS", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1, 3, 4));
                        tbl_det1.addCell(Addons.setCeldaPDF("\n \n \n \n \n ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1, 3, 4));
                        document.add(tbl_det1);
                        try {
//                                if (puerto_instalacion[1].compareTo("") != 0 || puerto_instalacion[3].compareTo("") != 0) {
                            PdfPTable tbl_det2 = new PdfPTable(new float[]{40f, 40f, 40f, 40f, 40f, 40f, 40f, 40f});
                            tbl_det2.addCell(Addons.setCeldaPDF("EQUIPO ACOPLADO ", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                            tbl_det2.addCell(Addons.setCeldaPDF(puerto_instalacion[3], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            tbl_det2.addCell(Addons.setCeldaPDF("PUERTO CONECTADO", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                            tbl_det2.addCell(Addons.setCeldaPDF(puerto_instalacion[1], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            tbl_det2.addCell(Addons.setCeldaPDF("LATITUD ", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                            tbl_det2.addCell(Addons.setCeldaPDF(puerto_instalacion[5], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            tbl_det2.addCell(Addons.setCeldaPDF("LONGITUD", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                            tbl_det2.addCell(Addons.setCeldaPDF(puerto_instalacion[6], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            document.add(tbl_det2);
//                                }
                        } catch (Exception e) {
                            System.out.println("" + e.getMessage() + " " + e.getLocalizedMessage());
                        }

                        break;

                    case 4: //  desinstalacion

                        String valor = RS.getString("id_instalacion") != null ? RS.getString("id_instalacion") : "-1"; //id instalacion para comparacion
                        String devuelto[] = objOrdenTrabajo.getOrdenesTrabajoPrefactura(Integer.parseInt(valor));
                        ResultSet rsEquiposBodega = objOrdenTrabajo.getMaterialesBodega(valor);

                        PdfPTable tbl_det = new PdfPTable(new float[]{45f, 60f});
                        tbl_det.addCell(Addons.setCeldaPDF("NUMERO", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det.addCell(Addons.setCeldaPDF((RS.getString("id_sucursal") != null ? RS.getString("id_sucursal") : "") + "-" + (RS.getString("num_orden") != null ? RS.getString("num_orden") : ""), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_det.addCell(Addons.setCeldaPDF("RESPONSABLE", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det.addCell(Addons.setCeldaPDF((RS.getString("responsable") != null ? RS.getString("responsable") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        if (coresponsables.trim().compareTo("") != 0) {
                            tbl_det.addCell(Addons.setCeldaPDF("CORRESPONSABLE", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                            tbl_det.addCell(Addons.setCeldaPDF(coresponsables, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                        }

                        tbl_det.addCell(Addons.setCeldaPDF("ESTADO DEL SERVICIO", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det.addCell(Addons.setCeldaPDF((RS.getString("txt_estado_servicio") != null ? RS.getString("txt_estado_servicio") : ""), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_det.addCell(Addons.setCeldaPDF("CEDULA", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det.addCell(Addons.setCeldaPDF((RS.getString("ruc") != null ? RS.getString("ruc") : ""), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_det.addCell(Addons.setCeldaPDF("CLIENTE", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det.addCell(Addons.setCeldaPDF((RS.getString("razon_social") != null ? RS.getString("razon_social") : ""), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_det.addCell(Addons.setCeldaPDF("TELEFONOS:", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det.addCell(Addons.setCeldaPDF((RS.getString("telefono") != null ? RS.getString("telefono") : "")
                                + "    Claro:" + (RS.getString("movil_claro") != null ? RS.getString("movil_claro") : "")
                                + "    Movistar:" + (RS.getString("movil_movistar") != null ? RS.getString("movil_movistar") : ""), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_det.addCell(Addons.setCeldaPDF("DIRECCION DEL CLIENTE", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det.addCell(Addons.setCeldaPDF((RS.getString("direccion") != null ? RS.getString("direccion") : ""), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("LATITUD / LONGITUD", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det.addCell(Addons.setCeldaPDF(latitud + " / " + longitud, Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1, 3, 3));
                        tbl_det.addCell(Addons.setCeldaPDF("LATITUD DECIMAL / LONGITUD DECIMAL", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det.addCell(Addons.setCeldaPDF(latitud_gps + " / " + longitud_gps, Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1, 3, 3));

                        tbl_det.addCell(Addons.setCeldaPDF("CIUDAD DE INSTALACION", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det.addCell(Addons.setCeldaPDF((RS.getString("ciudad") != null ? RS.getString("ciudad") : ""), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_det.addCell(Addons.setCeldaPDF("DIRECCION DE INSTALACION", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det.addCell(Addons.setCeldaPDF((RS.getString("direccion_instalacion") != null ? RS.getString("direccion_instalacion") : ""), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_det.addCell(Addons.setCeldaPDF("FECHA DE DES-INSTALACION", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det.addCell(Addons.setCeldaPDF((RS.getString("fecha_desinstalacion") != null ? RS.getString("fecha_desinstalacion") : ""), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_det.addCell(Addons.setCeldaPDF("SECTOR", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det.addCell(Addons.setCeldaPDF(sector, Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_det.addCell(Addons.setCeldaPDF("DIRECCION IP", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det.addCell(Addons.setCeldaPDF((RS.getString("ip") != null ? RS.getString("ip") : ""), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_det.addCell(Addons.setCeldaPDF("DIRECCION MAC", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det.addCell(Addons.setCeldaPDF((RS.getString("mac") != null ? RS.getString("mac") : ""), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_det.addCell(Addons.setCeldaPDF("RECEPTOR DE SEÑAL", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det.addCell(Addons.setCeldaPDF((RS.getString("receptor") != null ? RS.getString("receptor") : ""), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_det.addCell(Addons.setCeldaPDF("PORCENTAJE DE SEÑAL", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det.addCell(Addons.setCeldaPDF((RS.getString("porcentaje_senal") != null ? RS.getString("porcentaje_senal") : ""), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_det.addCell(Addons.setCeldaPDF("ANTENA DE TRASMISION ACOPLADA", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det.addCell(Addons.setCeldaPDF((RS.getString("antena_acoplada") != null ? RS.getString("antena_acoplada") : ""), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_det.addCell(Addons.setCeldaPDF("PLAN ESTABLECIDO", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det.addCell(Addons.setCeldaPDF(plan, Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_det.addCell(Addons.setCeldaPDF("NIVEL DE COMPARTICION", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det.addCell(Addons.setCeldaPDF((RS.getString("comparticion") != null ? RS.getString("comparticion") : "") + " - 1", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_det.addCell(Addons.setCeldaPDF("PREFACTURA", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det.addCell(Addons.setCeldaPDF(devuelto[0], Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_det.addCell(Addons.setCeldaPDF("OBSERVACIONES", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det.addCell(Addons.setCeldaPDF((RS.getString("diagnostico_tecnico") != null ? RS.getString("diagnostico_tecnico") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1, 3, 3));

                        try {
                            ResultSet rsAnticipos = objFacturaVenta.getAnticipos(id_cliente);

                            String montos = "";
                            double sumMontos = 0;
                            while (rsAnticipos.next()) {
                                //String num_comp_pago = rsAnticipos.getString("num_comp_pago")!=null ? rsAnticipos.getString("num_comp_pago") : "";
                                double monto = rsAnticipos.getString("monto") != null ? rsAnticipos.getDouble("monto") : 0;
                                sumMontos += monto;
                                if (monto > 0) {
                                    montos += "$" + String.valueOf(monto) + ", ";
                                }
                            }
                            if (sumMontos > 0) {
                                montos = montos.substring(0, montos.length() - 2);
                                tbl_det.addCell(Addons.setCeldaPDF("ABONOS", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                                tbl_det.addCell(Addons.setCeldaPDF(montos, Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));

                                double saldo = Addons.redondear(Double.parseDouble(devuelto[1]) - sumMontos);
                                saldo = Math.abs(saldo);
                                tbl_det.addCell(Addons.setCeldaPDF("SALDO", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1));
                                tbl_det.addCell(Addons.setCeldaPDF("$" + String.valueOf(saldo), Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            }
                            rsAnticipos.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            tbl_det.addCell(Addons.setCeldaPDF("EQUIPOS DE LA BODEGA", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_LEFT, 1, 3, 2));
                            while (rsEquiposBodega.next()) {
                                String codigoActivo = rsEquiposBodega.getString("codigo_activo") != null ? rsEquiposBodega.getString("codigo_activo") : "";
                                String descripcion = rsEquiposBodega.getString("descripcion") != null ? rsEquiposBodega.getString("descripcion") : "";
                                tbl_det.addCell(Addons.setCeldaPDF(codigoActivo, Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));
                                tbl_det.addCell(Addons.setCeldaPDF(descripcion, Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            }
                            rsEquiposBodega.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        document.add(tbl_det);

                        break;

                    default:

                        PdfPTable tbl_det2 = new PdfPTable(new float[]{35f, 65f, 35f, 65f});

                        tbl_det2.addCell(Addons.setCeldaPDF("No." + (RS.getString("id_sucursal") != null ? RS.getString("id_sucursal") : "") + "-" + (RS.getString("num_orden") != null ? RS.getString("num_orden") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_RIGHT, 0, 3, 4));

                        tbl_det2.addCell(Addons.setCeldaPDF("CEDULA", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det2.addCell(Addons.setCeldaPDF((RS.getString("ruc") != null ? RS.getString("ruc") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_det2.addCell(Addons.setCeldaPDF("CLIENTE", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det2.addCell(Addons.setCeldaPDF((RS.getString("razon_social") != null ? RS.getString("razon_social") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_det2.addCell(Addons.setCeldaPDF("TELEFONOS", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det2.addCell(Addons.setCeldaPDF((RS.getString("telefono") != null ? RS.getString("telefono") : "")
                                + "    Claro:" + (RS.getString("movil_claro") != null ? RS.getString("movil_claro") : "")
                                + "    Movistar:" + (RS.getString("movil_movistar") != null ? RS.getString("movil_movistar") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_det2.addCell(Addons.setCeldaPDF("CIUDAD", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det2.addCell(Addons.setCeldaPDF((RS.getString("ciudad") != null ? RS.getString("ciudad") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_det2.addCell(Addons.setCeldaPDF("DIRECCION DE INSTALACION", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det2.addCell(Addons.setCeldaPDF((RS.getString("direccion_instalacion") != null ? RS.getString("direccion_instalacion") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1, 3, 3));

                        tbl_det2.addCell(Addons.setCeldaPDF("SECTOR", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det2.addCell(Addons.setCeldaPDF((RS.getString("sector") != null ? RS.getString("sector") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_det2.addCell(Addons.setCeldaPDF("ESTADO DEL SERVICIO", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det2.addCell(Addons.setCeldaPDF((RS.getString("txt_estado_servicio") != null ? RS.getString("txt_estado_servicio") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_det2.addCell(Addons.setCeldaPDF("PLAN ACTUAL", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det2.addCell(Addons.setCeldaPDF((RS.getString("plan") != null ? RS.getString("plan") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_det2.addCell(Addons.setCeldaPDF("COMPARTICION", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det2.addCell(Addons.setCeldaPDF((RS.getString("txt_comparticion") != null ? RS.getString("txt_comparticion") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_det2.addCell(Addons.setCeldaPDF("DIRECCION IP\nRECEPTOR DE SEÑAL", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det2.addCell(Addons.setCeldaPDF((RS.getString("ip") != null ? RS.getString("ip") : "") + "\n" + (RS.getString("receptor") != null ? RS.getString("receptor") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_det2.addCell(Addons.setCeldaPDF("CLAVE DISPOSITIVO", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det2.addCell(Addons.setCeldaPDF(deviceclave, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_det2.addCell(Addons.setCeldaPDF("FECHA DE REPORTE", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det2.addCell(Addons.setCeldaPDF((RS.getString("fecha_reporte") != null ? Fecha.SQLaISO(RS.getString("fecha_reporte")) : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_det2.addCell(Addons.setCeldaPDF("FECHA DE REALIZACION", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det2.addCell(Addons.setCeldaPDF((RS.getString("fecha_realizacion") != null ? Fecha.SQLaISO(RS.getString("fecha_realizacion")) : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_det2.addCell(Addons.setCeldaPDF("ANTENA ACOPLADA", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det2.addCell(Addons.setCeldaPDF((RS.getString("antena_acoplada") != null ? RS.getString("antena_acoplada") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_det2.addCell(Addons.setCeldaPDF("RESPONSABLE", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det2.addCell(Addons.setCeldaPDF((RS.getString("responsable") != null ? RS.getString("responsable") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        if (coresponsables.trim().compareTo("") != 0) {
                            tbl_det2.addCell(Addons.setCeldaPDF("CORRESPONSABLE", Font.HELVETICA, 7, Font.BOLD, Element.ALIGN_LEFT, 1));
                            tbl_det2.addCell(Addons.setCeldaPDF(coresponsables, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1, 3, 3));
                        }

                        tbl_det2.addCell(Addons.setCeldaPDF("DIAGNOSTICO TECNICO", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det2.addCell(Addons.setCeldaPDF((RS.getString("diagnostico_tecnico") != null ? RS.getString("diagnostico_tecnico") : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1, 3, 3));

                        tbl_det2.addCell(Addons.setCeldaPDF("UBICACION GEOGRAFICA LATITUD", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det2.addCell(Addons.setCeldaPDF("_____ °  _____ '  _____ ''    N    S\n" + latitud, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_det2.addCell(Addons.setCeldaPDF("UBICACION GEOGRAFICA LONGITUD", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det2.addCell(Addons.setCeldaPDF("_____ °  _____ '  _____ ''    E    O\n" + longitud, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_det2.addCell(Addons.setCeldaPDF("LATITUD DECIMAL", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det2.addCell(Addons.setCeldaPDF("" + latitud_gps, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_det2.addCell(Addons.setCeldaPDF("LONGITUD DECIMAL", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det2.addCell(Addons.setCeldaPDF("" + longitud_gps, Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_det2.addCell(Addons.setCeldaPDF("ALTURA DE LA ESTRUCTURA s.n.m.", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det2.addCell(Addons.setCeldaPDF("_________ (m)", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_det2.addCell(Addons.setCeldaPDF("ALTURA DE LA ESTRUCTURA (BASE-CIMA)", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det2.addCell(Addons.setCeldaPDF("_________ (m)", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_det2.addCell(Addons.setCeldaPDF("DESCRIPCION DE MATERIALES UTILIZADOS", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1, 3, 4));
                        tbl_det2.addCell(Addons.setCeldaPDF("\n \n \n \n \n \n \n ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1, 3, 4));

                        tbl_det2.addCell(Addons.setCeldaPDF("OBSERVACIONES Y RECOMENDACIONES", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1, 3, 4));
                        tbl_det2.addCell(Addons.setCeldaPDF("\n \n \n \n \n ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1, 3, 4));

                        tbl_det2.addCell(Addons.setCeldaPDF("INCONVENIENTE SOLUCIONADO", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det2.addCell(Addons.setCeldaPDF(" ___ SI \n ___ NO\n ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_det2.addCell(Addons.setCeldaPDF("CONFORME CON EL TRABAJO REALIZADO", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det2.addCell(Addons.setCeldaPDF(" ___ SI \n ___ NO\n ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));

                        tbl_det2.addCell(Addons.setCeldaPDF("CONFORME CON LA ATENCION RECIBIDA", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                        tbl_det2.addCell(Addons.setCeldaPDF(" ___ SI \n ___ NO\n ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1, 3, 3));
                        document.add(tbl_det2);
                        try {
//                                if (puerto_instalacion[1].compareTo("") != 0 || puerto_instalacion[3].compareTo("") != 0) {
                            PdfPTable tbl_det3 = new PdfPTable(new float[]{40f, 40f, 40f, 40f, 40f, 40f, 40f, 40f});
                            tbl_det3.addCell(Addons.setCeldaPDF("EQUIPO ACOPLADO ", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                            tbl_det3.addCell(Addons.setCeldaPDF(puerto_instalacion[3], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            tbl_det3.addCell(Addons.setCeldaPDF("PUERTO CONECTADO", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                            tbl_det3.addCell(Addons.setCeldaPDF(puerto_instalacion[1], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            tbl_det3.addCell(Addons.setCeldaPDF("LATITUD ", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                            tbl_det3.addCell(Addons.setCeldaPDF(puerto_instalacion[5], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            tbl_det3.addCell(Addons.setCeldaPDF("LONGITUD", Font.HELVETICA, 8, Font.BOLD, Element.ALIGN_LEFT, 1));
                            tbl_det3.addCell(Addons.setCeldaPDF(puerto_instalacion[6], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            document.add(tbl_det3);
//                                }
                        } catch (Exception e) {
                            System.out.println("" + e.getMessage() + " " + e.getLocalizedMessage());
                        }

                        document.add(new Paragraph(" "));
                        document.add(new Paragraph(" "));
                        document.add(new Paragraph(" "));

                        PdfPCell celdaImg = null;
                        try {
                            ResultSet rsdocumento = archivo.getArchivoDocumentales("tbl_orden_trabajo", id, "firma_orden");
                            while (rsdocumento.next()) {
                                byte[] url_imagen = (rsdocumento.getString(1) != null ? rsdocumento.getBytes(1) : null);
                                Image imagen = Image.getInstance(url_imagen);
                                imagen.scaleAbsolute(90, 90);
                                celdaImg = new PdfPCell(imagen);
                            }
                            if (rsdocumento != null) {
                                rsdocumento.close();
                            }

                        } catch (Exception e) {
                            System.out.println("" + e.getLocalizedMessage() + " " + e.getMessage());
                        }
                        PdfPTable tbl_firmas = new PdfPTable(2);
                        tbl_firmas.addCell(Addons.setCeldaPDF("  RESPONSABLE  ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
                        if (celdaImg != null) {
                            tbl_firmas.addCell(celdaImg);
                        } else {
                            tbl_firmas.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
                        }
                        tbl_firmas.addCell(Addons.setCeldaPDFOculta("  ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
                        tbl_firmas.addCell(Addons.setCeldaPDF( "CEDULA / RUC" + RS.getString("ruc"), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        
                        tbl_firmas.addCell(Addons.setCeldaPDFOculta("  ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
                        tbl_firmas.addCell(Addons.setCeldaPDF(RS.getString("razon_social"), Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                        
                        tbl_firmas.addCell(Addons.setCeldaPDFOculta("  _firma_responsable_  ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
                        tbl_firmas.addCell(Addons.setCeldaPDFOculta("  _firma_cliente_  ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
                        document.add(tbl_firmas);
                        PdfPTable tbl_cerrar = new PdfPTable(1);
                        tbl_cerrar.addCell(Addons.setCeldaPDFOculta("  .  ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
                        document.add(tbl_cerrar);

                }

            }
        } catch (IllegalStateException ie) {
            ie.printStackTrace();
        } catch (DocumentException de) {
            de.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            objSector.cerrar();
            objPlanServicio.cerrar();
            objFacturaVenta.cerrar();
            archivo.cerrar();
            objSplitter.cerrar();
        }
        resultado = documento.getName();
        document.close();
        return resultado;
    }

    
    public String PdfSolicitudAddEquipos(int id_empleado_sesion, String idOT, String numSolicitud, String macsEquipos, File documento) 
    {
        OrdenTrabajo objOrdenTrabajo = new OrdenTrabajo(_ip, _puerto, _db, _usuario, _clave);
        Sucursal objSucursal = new Sucursal(_ip, _puerto, _db, _usuario, _clave);
        Empleado objEmpleado = new Empleado(_ip, _puerto, _db, _usuario, _clave);
        Activo objActivo = new Activo(_ip, _puerto, _db, _usuario, _clave);
        
        String resultado = null;
        String[] cabTabla = null;
        float[] anchoTabla = null;
        String fecha_actual = Fecha.getAnio() + "-" + Fecha.getMes() + "-" + Fecha.getDia();
        try {
//            String id_instalacion = "1";
            String razon_social = "";
            String ip = "";
//            String idEmpleadoSolicitud = "-1";
            String empleado = "";
            String dni = "";
            String ciudad = "";
//            String alias_revision = "";
//            String fecha_revision = "";
            
            try {
                ResultSet rsOrdenTrabajo = objOrdenTrabajo.getOrdenTrabajo(idOT);
                if (rsOrdenTrabajo.next()) {
//                    id_instalacion = (rsOrdenTrabajo.getString("id_instalacion") != null) ? rsOrdenTrabajo.getString("id_instalacion") : "";
                    razon_social = (rsOrdenTrabajo.getString("razon_social") != null) ? rsOrdenTrabajo.getString("razon_social") : "";
                    ip = (rsOrdenTrabajo.getString("ip") != null) ? rsOrdenTrabajo.getString("ip") : "";
                    String idSucursal = (rsOrdenTrabajo.getString("id_sucursal") != null) ? rsOrdenTrabajo.getString("id_sucursal") : "";
//                    idEmpleadoSolicitud = (rsOrdenTrabajo.getString("id_empleado") != null) ? rsOrdenTrabajo.getString("id_empleado") : "";
                    ciudad = objSucursal.getCiudad(Integer.parseInt(idSucursal));
                    rsOrdenTrabajo.close();
                }
            } catch (Exception e) {
                System.out.println("" + e.getMessage() + "  " + e.getLocalizedMessage());
            }
            
            try {
                ResultSet rsEmpleado = objEmpleado.getEmpleado(id_empleado_sesion);
                if (rsEmpleado.next()) {
                    empleado = (rsEmpleado.getString("nombre") != null ? rsEmpleado.getString("nombre") : "") +
                                (rsEmpleado.getString("apellido") != null ? " " + rsEmpleado.getString("apellido") : "");
                    dni = (rsEmpleado.getString("dni") != null) ? rsEmpleado.getString("dni") : "";
                    rsEmpleado.close();
                }
            } catch (Exception e) {
                System.out.println("" + e.getMessage() + "  " + e.getLocalizedMessage());
            }
            
            
            
            /* inicio PDF */
            Document document = new Document(PageSize.A4);// paso 1
            document.setMargins(0, 0, 70, 80);
            /*Izquierda, derecha, tope, pie */
            try {
                PdfWriter.getInstance(document, new FileOutputStream(documento));
                document.open(); // paso 3

                PdfPTable tbl_encab = new PdfPTable(1);
//                tbl_encab.addCell(Addons.setCeldaPDF("", Font.TIMES_ROMAN, 12, Font.NORMAL, Element.ALIGN_CENTER, 0));
                tbl_encab.addCell(Addons.setCeldaPDF("Solicitud Para Agregar Equipos Adicionales En Instalación", Font.TIMES_ROMAN, 14, Font.NORMAL, Element.ALIGN_CENTER, 0));
                
                
                tbl_encab.addCell(Addons.setCeldaPDF("Nro. " + numSolicitud, Font.TIMES_ROMAN, 14, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                
                tbl_encab.addCell(Addons.setCeldaPDF(" ", Font.TIMES_ROMAN, 14, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                
//                tbl_encab.addCell(Addons.setCeldaPDF("", Font.TIMES_ROMAN, 12, Font.NORMAL, Element.ALIGN_CENTER, 0));
                tbl_encab.addCell(Addons.setCeldaPDF(ciudad + ", " + Fecha.getFechaSolicitud(fecha_actual), Font.TIMES_ROMAN, 12, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                
                document.add(tbl_encab);
                
                document.add(new Paragraph(" "));
                
                tbl_encab = new PdfPTable(1);
                
                
                tbl_encab.addCell(Addons.setCeldaPDF("Yo " + empleado + " con cédula de ciudadanía " + dni +
                        " solicito a quien corresponda autorizar la personalización de los equipos listados en la presente, en la instalación del cliente " + razon_social + " (IP. "+ip+")", 
                        Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                
                tbl_encab.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_encab.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_LEFT, 0));
                document.add(tbl_encab);
                
                document.add(new Paragraph(" "));
                
                cabTabla = new String[]{"N°", "CODIGO ACTIVO", "DETALLE"};
                anchoTabla = new float[]{5f, 30f, 60f};
                document.add(Addons.setCabeceraTabla(cabTabla, anchoTabla));
                PdfPTable tbl_cuerpo = new PdfPTable(anchoTabla);
                
                try {
                    ResultSet rs = objActivo.getActivoDescripcion("where codigo_activo in('"+macsEquipos.replace(",", "','")+"')");
                    int i = 1;
                    while (rs.next()) {
                        String codigo_activo = (rs.getString("codigo_activo") != null ? rs.getString("codigo_activo") : "");
                        String descripcion = (rs.getString("descripcion") != null ? rs.getString("descripcion") : "");
                        tbl_cuerpo.addCell(Addons.setCeldaPDF("" + i, Font.TIMES_ROMAN, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_cuerpo.addCell(Addons.setCeldaPDF(codigo_activo, Font.TIMES_ROMAN, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_cuerpo.addCell(Addons.setCeldaPDF(descripcion, Font.TIMES_ROMAN, 10, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        i++;
                    }
                } catch (Exception e) {
                    System.out.println("" + e.getMessage() + "  " + e.getLocalizedMessage());
                }
                document.add(tbl_cuerpo);

                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));

                PdfPTable tbl_firmas = new PdfPTable(2);
                tbl_firmas.addCell(Addons.setCeldaPDF("  RESPONSABLE  ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
                tbl_firmas.addCell(Addons.setCeldaPDF("  AUTORIZADO   ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
                tbl_firmas.addCell(Addons.setCeldaPDFOculta("  _firma_responsable_  ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
                tbl_firmas.addCell(Addons.setCeldaPDFOculta("  _firma_autorizado_  ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
                document.add(tbl_firmas);
                PdfPTable tbl_cerrar = new PdfPTable(1);
                tbl_cerrar.addCell(Addons.setCeldaPDFOculta("  .  ", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_CENTER, 0));
                document.add(tbl_cerrar);
                resultado = documento.getName();
            } catch (IllegalStateException ie) {
                ie.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            document.close(); // paso 5
            /* fin PDF */
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        } finally {
            objActivo.cerrar();
            objSucursal.cerrar();
            objEmpleado.cerrar();
            objOrdenTrabajo.cerrar();
        }
        return resultado;
    }
    
}
