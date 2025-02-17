/**
 * @version 1.0
 * @package FACTURAPYMES.
 * @author Jorge Washington Mueses Cevallos.
 * @copyright Copyright (C) 2010 por Jorge Mueses. Todos los derechos
 * reservados.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL. FACTURAPYMES! es un
 * software de libre distribución, que puede ser copiado y distribuido bajo los
 * términos de la Licencia Pública General GNU, de acuerdo con la publicada por
 * la Free Software Foundation, versión 2 de la licencia o cualquier versión
 * posterior.
 */
package ec.saitel.api.util;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import javax.servlet.http.HttpServletResponse;


/**
 *
 * @author Jorge
 */
public class Pdf extends DataBase {

    DecimalFormat formato = new DecimalFormat("#.00");
    public static File documento = null;
    public static String nom_documento = null;
    String m1;
    int p1;
    String db1;
    String u1;
    String c1;

    String dm1;
    int dp1;
    String ddb1;
    String du1;
    String dc1;

    String _URL_ANEXOS = "";
    String numero_contrato = "";
    String _dir = "";

    public Pdf(String m, int p, String db, String u, String c) {
        super(m, p, db, u, c);
        m1 = m;
        p1 = p;
        db1 = db;
        u1 = u;
        c1 = c;
    }

    public void PdfDocumental(String m, int p, String db, String u, String c, String dir, String anexos) {
        dm1 = m;
        dp1 = p;
        ddb1 = db;
        du1 = u;
        dc1 = c;
        _dir = dir;
        _URL_ANEXOS = anexos;
    }

    public void Contratodocumental(HttpServletResponse response, String usuario, String clave, String id_contrato, String num_contrato, String id_sucursal, boolean imprimir, boolean nuevo, boolean guardar) {
        Archivo archivo = new Archivo(dm1, dp1, ddb1, du1, dc1);
        String tmppdf[][] = null;
        if (nuevo) {
            tmppdf = this.Generarcontrato(response, imprimir, id_contrato);
        } else {
            tmppdf = this.GenerarcontratoAntiguo(response, imprimir, id_contrato);
        }

        try {
            if (tmppdf != null && guardar) {
                archivo.setArchivoDocumentales("tbl_contrato", id_contrato, "contratotexto", this.nom_documento, this.documento, "public", "db_isp");
                archivo.setArchivoDocumentalTexto(tmppdf[0][0], num_contrato, id_sucursal, "tbl_contrato", id_contrato, "contratotexto", "public", "db_isp");
            }
        } catch (Exception e) {
            System.out.println("" + e.getLocalizedMessage() + " " + e.getMessage());
        }
        archivo.cerrar();
    }

    public String[][] Generarcontrato(HttpServletResponse response, boolean oki, String id_contrato) {
        String contrato[][] = null;
        try {
            String firmas[] = null;
            contrato = this.Modelo(id_contrato);
            firmas = this.Firmas(contrato[0][6], contrato[0][7], contrato[0][8]);
            PdfCotrato objPdfCotrato = new PdfCotrato();
            objPdfCotrato.imprimir(response, oki, contrato, firmas);
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        } finally {
            return contrato;
        }
    }

    public String[][] ServiciosAdicionales(String id_instalacion) {
        DataBase objDataBase = new DataBase(m1, p1, db1, u1, c1);
        try {
            return Matriz.ResultSetAMatriz(objDataBase.consulta("select nombre_servicio,detalle_servicio,cantidad,round(valor_servicio,2) from vta_instalacion_servicio where id_instalacion='" + id_instalacion + "';"));
        } catch (Exception e) {
            System.out.println("" + e.getLocalizedMessage() + "  " + e.getMessage());
            return null;
        } finally {
            objDataBase.cerrar();
        }
    }

    public String[][] GenerarcontratoAntiguo(HttpServletResponse response, boolean oki, String id_contrato) {
        String contrato[][] = null;
        try {
            String firmas[] = null;
            contrato = this.ModeloAnterior(id_contrato);
            firmas = this.Firmas(contrato[0][2], contrato[0][3], contrato[0][4]);
            PdfCotratoAntiguo objPdfCotrato = new PdfCotratoAntiguo();
            objPdfCotrato.imprimir(response, oki, contrato, firmas);
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        } finally {
            return contrato;
        }

    }

    public String[][] Modelo(String id_contrato) {
        String[][] tipo_documento = {{"04", "Ruc"}, {"05", "Cedula"}, {"06", "Pasaporte"}};
        String anexos[][] = {
            {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""}
        };
        String tmpcontrato = "";
        String tmpburo = "";
        String tmpAdultoMayor = "";
        String tmpTratamientoDatos = "";
        String id_sucursal = "";
        String num_contrato = "";
        String tipo_documento1 = "04";
        String id_cliente = "";
        String cedula = "";
        String cliente = "";
        String representante = "";
        String id_instalacion = "";
        String direccion = "";
        String parroquia = "";
        String canton = "";
        String ciudad = "";
        String provincia = "";
        String email = "";
        String telefono = "";
        String movil_claro = "";
        String movil_movistar = "";
        String movil = "";
        String edad = "";
        String carne_conadis = "";
        String direccion_instalacion = "";
        String tipo_cliente_instalacion = "c";
        String fecha_actual = Fecha.getAnio() + "-" + Fecha.getMes() + "-" + Fecha.getDia();
        String fecha_contrato = fecha_actual;
        String iva_actual = "";
        String gerente = "Mgtr. Freddy Marlon Rosero Cuaspa";
        String rucSAITEL = "1091728857001";
        
        boolean especial = false;
        float velocidad = 0;
        String costo_instalacion = "0";
        String tiempo_permanencia = "0";
        try {
            Documento objContrato = new Documento(m1, p1, db1, u1, c1);
            ResultSet rsDoc = objContrato.getDocumento("x");
            if (rsDoc.next()) {
                tmpcontrato = rsDoc.getString("documento") != null ? rsDoc.getString("documento") : "";
                rsDoc.close();
            }
            rsDoc = objContrato.getDocumento("w");
            if (rsDoc.next()) {
                anexos[0][1] = rsDoc.getString("documento") != null ? rsDoc.getString("documento") : "";
                rsDoc.close();
            }
            rsDoc = objContrato.getDocumento("v");
            if (rsDoc.next()) {
                anexos[0][2] = rsDoc.getString("documento") != null ? rsDoc.getString("documento") : "";
                rsDoc.close();
            }
            rsDoc = objContrato.getDocumento("q");
            if (rsDoc.next()) {
                tmpburo = rsDoc.getString("documento") != null ? rsDoc.getString("documento") : "";
                rsDoc.close();
            }
            rsDoc = objContrato.getDocumento("am"); //  adulto mayor
            if (rsDoc.next()) {
                tmpAdultoMayor = rsDoc.getString("documento") != null ? rsDoc.getString("documento") : "";
                rsDoc.close();
            }
            rsDoc = objContrato.getDocumento("tData"); //  tratamiento de datos
            if (rsDoc.next()) {
                tmpTratamientoDatos = rsDoc.getString("documento") != null ? rsDoc.getString("documento") : "";
                rsDoc.close();
            }
            objContrato.cerrar();
        } catch (Exception e) {
            System.out.println("no existe contrato");
        }
        
        
        Configuracion conf = new Configuracion(m1, p1, db1, u1, c1);
        try {
            gerente = conf.getValor("rep_nombre");
            rucSAITEL = conf.getValor("ruc");
            iva_actual = conf.getValor("p_iva1");
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        } finally {
            conf.cerrar();
        }
        
        try {
            Contrato objContrato = new Contrato(m1, p1, db1, u1, c1);
            ResultSet res = objContrato.getContrato(id_contrato);
            if (res.next()) {
                id_sucursal = res.getString("id_sucursal") != null ? res.getString("id_sucursal") : "";
                num_contrato = res.getString("num_contrato") != null ? res.getString("num_contrato") : "";
                cedula = res.getString("ruc") != null ? res.getString("ruc") : "";
                cliente = res.getString("razon_social") != null ? res.getString("razon_social") : "";
                representante = res.getString("representante") != null ? res.getString("representante") : "";
                id_instalacion = res.getString("id_instalacion") != null ? res.getString("id_instalacion") : "";
                id_cliente = res.getString("id_cliente") != null ? res.getString("id_cliente") : "";
                fecha_contrato = res.getString("fecha_contrato") != null ? res.getString("fecha_contrato") : "";
                res.close();
            }
            objContrato.cerrar();
        } catch (Exception e) {
            System.out.println("datos del contrato");
        }
        numero_contrato = id_sucursal + " - " + num_contrato;
        Sucursal objSucursal = new Sucursal(m1, p1, db1, u1, c1);
        ciudad = objSucursal.getCiudad(Integer.parseInt(id_sucursal));
        costo_instalacion = objSucursal.getSucursalCampo(id_sucursal, "costo_instalacion");
        double ax_costo_instalacion = Addons.redondear( Float.parseFloat( costo_instalacion ) / (1 + ( Float.parseFloat(iva_actual) / 100) ) );
        costo_instalacion = String.valueOf( ax_costo_instalacion );
        tiempo_permanencia = objSucursal.getSucursalCampo(id_sucursal, "tiempo_permanencia");
        objSucursal.cerrar();
        try {
            Cliente objCliente = new Cliente(m1, p1, db1, u1, c1);
            ResultSet res = objCliente.getCliente(id_cliente);
            if (res.next()) {
                anexos[2][0] = direccion = res.getString("direccion") != null ? res.getString("direccion") : "";
                anexos[2][1] = provincia = res.getString("id_provincia") != null ? res.getString("id_provincia") : "";
                anexos[2][2] = canton = res.getString("id_ciudad") != null ? res.getString("id_ciudad") : "";
                anexos[2][3] = parroquia = res.getString("id_parroquia") != null ? res.getString("id_parroquia") : "";
                anexos[2][4] = email = res.getString("email") != null ? res.getString("email") : "";
                anexos[2][5] = telefono = res.getString("telefono") != null ? res.getString("telefono") : "";
                anexos[2][6] = movil_claro = res.getString("movil_claro") != null ? res.getString("movil_claro") : "";
                anexos[2][7] = movil_movistar = res.getString("movil_movistar") != null ? res.getString("movil_movistar") : "";
                anexos[2][8] = edad = res.getString("edad") != null ? res.getString("edad") : "";
                anexos[2][9] = carne_conadis = res.getString("carne_conadis") != null ? res.getString("carne_conadis") : "";
                anexos[2][10] = tipo_documento1 = res.getString("tipo_documento") != null ? res.getString("tipo_documento") : "04";
                anexos[2][11] = id_sucursal;
                anexos[2][14] = res.getString("observacion") != null ? res.getString("observacion") : "";;
                res.close();
            }
            objCliente.cerrar();
        } catch (Exception e) {
            System.out.println("datos del cliente");
        }
        if (telefono.trim().compareTo("") != 0) {
            movil += telefono + " / ";
        }
        if (movil_claro.trim().compareTo("") != 0) {
            movil += movil_claro + " / ";
        }
        if (movil_movistar.trim().compareTo("") != 0) {
            movil += movil_movistar + " / ";
        }
        if (movil.trim().compareTo("") != 0) {
            movil = movil.substring(0, movil.length() - 2);
        }
        if (edad.trim().compareTo("") == 0) {
            edad = "0";
        }
        if (Integer.parseInt(edad) >= 65 || carne_conadis.trim().compareTo("") != 0) {
            especial = true;
        }
//        String id_sector = "";
        String valor_instalacion_sector = "";
        try {
            Instalacion objInstalacion = new Instalacion(m1, p1, db1, u1, c1);
            ResultSet res = objInstalacion.getInstalacion(id_instalacion);
            if (res.next()) {
                double costoInst = Addons.redondear( Float.parseFloat( res.getString("costo_instalacion") ) / (1 + ( Float.parseFloat(iva_actual) / 100) ) );
                anexos[1][0] = direccion_instalacion = res.getString("direccion_instalacion") != null ? res.getString("direccion_instalacion") : "";
                anexos[1][1] = tipo_cliente_instalacion = res.getString("tipo_cliente_instalacion") != null ? res.getString("tipo_cliente_instalacion") : "";
                anexos[1][2] = res.getString("tipo_instalacion") != null ? res.getString("tipo_instalacion") : "";
                anexos[1][3] = res.getString("plan") != null ? res.getString("plan") : "";
                anexos[1][4] = res.getString("plan_burst_limit") != null ? res.getString("plan_burst_limit") : "0";
                anexos[1][5] = res.getString("txt_comparticion") != null ? res.getString("txt_comparticion") : "";
                anexos[1][6] = String.valueOf( costoInst );
                anexos[1][7] = res.getString("id_plan_actual") != null ? res.getString("id_plan_actual") : "";
                anexos[1][8] = res.getString("txt_convenio_pago") != null ? res.getString("txt_convenio_pago") : "";
                anexos[1][9] = res.getString("forma_pago") != null ? res.getString("forma_pago") : "";
                anexos[1][10] = res.getString("id_provincia") != null ? res.getString("id_provincia") : "";
                anexos[1][11] = res.getString("id_ciudad") != null ? res.getString("id_ciudad") : "";
                anexos[1][12] = res.getString("id_parroquia") != null ? res.getString("id_parroquia") : "";
                anexos[1][13] = res.getString("latitud") != null ? res.getString("latitud") : "";
                anexos[1][14] = res.getString("longitud") != null ? res.getString("longitud") : "";
                valor_instalacion_sector = res.getString("costo_instalacion_facturado") != null ? res.getString("costo_instalacion_facturado") : "0";
                
                valor_instalacion_sector = String.valueOf(Addons.redondear(Double.parseDouble(valor_instalacion_sector) /  (1 + ( Float.parseFloat(iva_actual) / 100) ) ) );
                
//                id_sector = res.getString("id_sector") != null ? res.getString("id_sector") : "";

                velocidad = Float.parseFloat(anexos[1][4]);
                anexos[1][18] = id_instalacion;
                anexos[1][4] = (anexos[1][4].trim().compareTo("") == 0 ? "0" : "" + formato.format(Double.parseDouble(anexos[1][4]) / 1000));
                anexos[1][6] = (anexos[1][6].trim().compareTo("") == 0 ? "0" : "" + Math.round(Double.parseDouble(anexos[1][6])));
                res.close();
            }
            objInstalacion.cerrar();
        } catch (Exception e) {
            System.out.println("datos del ubicacion");
        }
        try {
            Archivo objArchivo = new Archivo(dm1, dp1, ddb1, du1, dc1);
            anexos[1][17] = _URL_ANEXOS + "dir/" + objArchivo.getArchivoDocumental(_dir, "tbl_instalacion", id_instalacion, "imgcroquis");
            objArchivo.cerrar();

        } catch (Exception e) {
            System.out.println("datos del ubicacion");
        }

        try {
            PlanServicio objPlanServicio = new PlanServicio(m1, p1, db1, u1, c1);
            ResultSet res = objPlanServicio.getPlanDetalle(anexos[1][7]);
            if (res.next()) {
                anexos[1][15] = res.getString("costo_plan") != null ? res.getString("costo_plan") : "";
                anexos[1][16] = res.getString("plan") != null ? res.getString("plan") : "";
                res.close();
            }
//            valor_instalacion_sector = objPlanServicio.getPrecioPlanSectorCampo(anexos[1][7], id_sector, "valor_instalacion");
            objPlanServicio.cerrar();
        } catch (Exception e) {
            System.out.println("datos del ubicacion");
        }

        try {
            Ubicacion objUbicacion = new Ubicacion(m1, p1, db1, u1, c1);
            provincia = objUbicacion.getNombre(provincia.compareTo("") != 0 ? provincia : "-1");
            canton = objUbicacion.getNombre(canton.compareTo("") != 0 ? canton : "-1");
            parroquia = objUbicacion.getNombre(parroquia.compareTo("") != 0 ? parroquia : "-1");
            anexos[1][10] = objUbicacion.getNombre(anexos[1][10].compareTo("") != 0 ? anexos[1][10] : "-1");
            anexos[1][11] = objUbicacion.getNombre(anexos[1][11].compareTo("") != 0 ? anexos[1][11] : "-1");
            anexos[1][12] = objUbicacion.getNombre(anexos[1][12].compareTo("") != 0 ? anexos[1][12] : "-1");
            anexos[2][15] = objUbicacion.getNombre(anexos[2][1].compareTo("") != 0 ? anexos[2][1] : "-1");
            anexos[2][16] = objUbicacion.getNombre(anexos[2][2].compareTo("") != 0 ? anexos[2][2] : "-1");
            anexos[2][17] = objUbicacion.getNombre(anexos[2][3].compareTo("") != 0 ? anexos[2][3] : "-1");
            objUbicacion.cerrar();
        } catch (Exception e) {
            System.out.println("datos del ubicacion");
        }
        String porcentaje_promocion = "";
        try {
            Promocion objPromocion = new Promocion(m1, p1, db1, u1, c1);
            ResultSet rs = objPromocion.getPromocionesInstalacion(id_instalacion);
            if (rs.next()) {
                anexos[2][18] = rs.getString(1) != null ? rs.getString(1) : "";
                anexos[2][19] = rs.getString(2) != null ? rs.getString(2) : "";
                anexos[2][20] = rs.getString(4) != null ? rs.getString(4) : "";
                porcentaje_promocion = rs.getString(10) != null ? rs.getString(10) : "";
                rs.close();
            }
            objPromocion.cerrar();
        } catch (Exception e) {
            System.out.println("datos del promociones");
        }
        
        anexos[3][0] = (costo_instalacion.trim().compareTo("") == 0 ? "0" : costo_instalacion);
        anexos[3][1] = (tiempo_permanencia.trim().compareTo("") == 0 ? "0" : tiempo_permanencia);
        anexos[3][2] = (valor_instalacion_sector.trim().compareTo("") == 0 ? "0" : valor_instalacion_sector);
        anexos[3][3] = (iva_actual.trim().compareTo("") == 0 ? "15" : iva_actual);
        anexos[3][4] = (porcentaje_promocion.trim().compareTo("") == 0 ? "0" : porcentaje_promocion);

        String permisos_isp[][] = null;
        try {
            DataBase objDataBase = new DataBase(m1, p1, db1, u1, c1);
            permisos_isp = Matriz.ResultSetAMatriz(objDataBase.consulta("select codigo_permiso,nombre_permiso from tbl_permisos_isp where eliminado=false;"));
            objDataBase.cerrar();
        } catch (Exception e) {
            System.out.println("datos del permisossp");
        }

        boolean tieneDiferenciaCostoInstalacion = (Double.parseDouble(costo_instalacion) - Double.parseDouble(valor_instalacion_sector)) > 0;
        tmpcontrato = tmpcontrato.replaceAll("<<lugar>>", ciudad);
        tmpcontrato = tmpcontrato.replaceAll("<<fecha>>", Fecha.getFechaSolicitud(fecha_contrato));
        tmpcontrato = tmpcontrato.replaceAll("<<rason_social>>", (representante.trim().compareTo("") != 0 ? Cadena.capital(representante) : Cadena.capital(cliente)));
        tmpcontrato = tmpcontrato.replaceAll("<<cedula>>", cedula);
        tmpcontrato = tmpcontrato.replaceAll("<<email>>", email);
        tmpcontrato = tmpcontrato.replaceAll("<<movil>>", movil);
        tmpcontrato = tmpcontrato.replaceAll("<<direccion>>", direccion);
        tmpcontrato = tmpcontrato.replaceAll("<<parroquia>>", parroquia);
        tmpcontrato = tmpcontrato.replaceAll("<<canton>>", canton);
        tmpcontrato = tmpcontrato.replaceAll("<<ciudad>>", canton);
        tmpcontrato = tmpcontrato.replaceAll("<<provincia>>", provincia);
        tmpcontrato = tmpcontrato.replaceAll("<<siedad>>", (especial ? "X" : ""));
        tmpcontrato = tmpcontrato.replaceAll("<<noedad>>", (!especial ? "X" : ""));
        tmpcontrato = tmpcontrato.replaceAll("<<permanenciasi>>", ((anexos[2][20].trim().compareTo("") != 0 || tieneDiferenciaCostoInstalacion) ? "X" : ""));
        tmpcontrato = tmpcontrato.replaceAll("<<permanenciano>>", ((anexos[2][20].trim().compareTo("") == 0) && !tieneDiferenciaCostoInstalacion ? "X" : ""));
        tmpcontrato = tmpcontrato.replaceAll("<<direccion_instalacion>>", direccion_instalacion);
        String permisos_isp1 = "";
        if (permisos_isp != null) {
            for (int i = 0; i < permisos_isp.length; i++) {
                permisos_isp1 += permisos_isp[i][1] + "                  " + (permisos_isp[i][0].compareTo(tipo_cliente_instalacion) == 0 ? "X" : "") + "  \n";
            }
        }

        tmpcontrato = tmpcontrato.replaceAll("<<permisos_isp>>", permisos_isp1);
        tmpburo = tmpburo.replaceAll("<<rason_social>>", (representante.trim().compareTo("") != 0 ? Cadena.capital(representante) : Cadena.capital(cliente)));
        tmpburo = tmpburo.replaceAll("<<cedula>>", cedula);
        tmpburo = tmpburo.replace("<<tipo_documento>>", tipo_documento[Matriz.enMatriz(tipo_documento, tipo_documento1, 0)][1]);
        anexos[2][12] = tipo_documento[Matriz.enMatriz(tipo_documento, tipo_documento1, 0)][1];
        anexos[2][13] = movil;
        anexos[0][0] = tmpcontrato;
        anexos[0][3] = tmpburo;
        anexos[0][4] = ciudad;
        anexos[0][5] = fecha_contrato;
        anexos[0][6] = cedula;
        anexos[0][7] = cliente;
        anexos[0][8] = representante;

        //  si es adulto mayor y tiene el beneficio de descuento 
        if (Integer.parseInt(edad) >= 65 && velocidad <= 20000
                && (anexos[1][3].contains("RESIDENCIAL") || anexos[1][3].contains("HOME"))) {
//            tmpAdultoMayor = tmpAdultoMayor.replaceAll("<<LUGAR>>", ciudad);
//            tmpAdultoMayor = tmpAdultoMayor.replaceAll("<<FECHA>>", Fecha.getFechaSolicitud(fecha_actual));
            tmpAdultoMayor = tmpAdultoMayor.replaceAll("<<CLIENTE>>", (representante.trim().compareTo("") != 0 ? Cadena.capital(representante) : Cadena.capital(cliente)));
            tmpAdultoMayor = tmpAdultoMayor.replaceAll("<<DNI>>", cedula);
            tmpAdultoMayor = tmpAdultoMayor.replaceAll("<<DIRECCION>>", direccion);
            tmpAdultoMayor = tmpAdultoMayor.replaceAll("<<PARROQUIA>>", parroquia);
            tmpAdultoMayor = tmpAdultoMayor.replaceAll("<<CANTON>>", canton);
            tmpAdultoMayor = tmpAdultoMayor.replaceAll("<<PROVINCIA>>", provincia);
            anexos[0][9] = tmpAdultoMayor;
        }
        
        
//  adendum al tratamiento de datos

        tmpTratamientoDatos = tmpTratamientoDatos.replaceAll("<<lugar>>", ciudad);
        tmpTratamientoDatos = tmpTratamientoDatos.replaceAll("<<fecha>>", Fecha.getFechaSolicitud(fecha_actual));
        tmpTratamientoDatos = tmpTratamientoDatos.replaceAll("<<rason_social>>", (representante.trim().compareTo("") != 0 ? Cadena.capital(representante) : Cadena.capital(cliente)));
        tmpTratamientoDatos = tmpTratamientoDatos.replaceAll("<<cedula>>", cedula);
        tmpTratamientoDatos = tmpTratamientoDatos.replaceAll("<<email>>", email);
        tmpTratamientoDatos = tmpTratamientoDatos.replaceAll("<<movil>>", movil);
        tmpTratamientoDatos = tmpTratamientoDatos.replaceAll("<<direccion>>", direccion);
        tmpTratamientoDatos = tmpTratamientoDatos.replaceAll("<<parroquia>>", parroquia);
        tmpTratamientoDatos = tmpTratamientoDatos.replaceAll("<<canton>>", canton);
        tmpTratamientoDatos = tmpTratamientoDatos.replaceAll("<<ciudad>>", canton);
        tmpTratamientoDatos = tmpTratamientoDatos.replaceAll("<<provincia>>", provincia);
        tmpTratamientoDatos = tmpTratamientoDatos.replaceAll("<<fecha_contrato>>", Fecha.getFechaSolicitud(fecha_contrato));
        tmpTratamientoDatos = tmpTratamientoDatos.replaceAll("<<gerente>>", gerente);
        tmpTratamientoDatos = tmpTratamientoDatos.replaceAll("<<ruc_saitel>>", rucSAITEL);
        anexos[0][10] = tmpTratamientoDatos;

        return anexos;
    }

    public String[][] ModeloAnterior(String id_contrato) {

        String anexos[][] = {
            {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""}};
        String num_contrato = "";
        String id_cliente = "";
        String id_sucursal = "";
        String fecha_contrato = "";
        String ruc_representante = "";
        String representante = "";
        String contrato = "";
        String autorizacion = "";

        Documento objDocumento = new Documento(m1, p1, db1, u1, c1);
        try {
            ResultSet rsDoc = objDocumento.getDocumento("c");
            if (rsDoc.next()) {
                contrato = rsDoc.getString("documento") != null ? rsDoc.getString("documento") : "";
                rsDoc.close();
            }

            ResultSet rsAut = objDocumento.getDocumento("2");
            if (rsAut.next()) {
                autorizacion = rsAut.getString("documento") != null ? rsAut.getString("documento") : "";
                rsAut.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            objDocumento.cerrar();
        }

        try {
            Contrato objContrato = new Contrato(m1, p1, db1, u1, c1);
            ResultSet res = objContrato.getContrato(id_contrato);
            if (res.next()) {
                id_sucursal = res.getString("id_sucursal") != null ? res.getString("id_sucursal") : "";
                num_contrato = res.getString("num_contrato") != null ? res.getString("num_contrato") : "";
                ruc_representante = res.getString("ruc_representante") != null ? res.getString("ruc_representante") : "";
                anexos[0][4] = representante = res.getString("representante") != null ? res.getString("representante") : "";
                id_cliente = res.getString("id_cliente") != null ? res.getString("id_cliente") : "";
                anexos[0][5] = fecha_contrato = res.getString("fecha_contrato") != null ? res.getString("fecha_contrato") : "";
                res.close();
            }
            objContrato.cerrar();
        } catch (Exception e) {
            System.out.println("datos del contrato");
        }

        Cliente objCliente = new Cliente(m1, p1, db1, u1, c1);
        ResultSet rsCliente = objCliente.getCliente(id_cliente);
        String cedula = "";
        String tipo_documento = "05";
        String razon_social = "";
        try {
            if (rsCliente.next()) {
                anexos[0][2] = cedula = (rsCliente.getString("ruc") != null) ? rsCliente.getString("ruc") : "";
                tipo_documento = (rsCliente.getString("tipo_documento") != null) ? rsCliente.getString("tipo_documento") : "05";
                anexos[0][3] = razon_social = (rsCliente.getString("razon_social") != null) ? rsCliente.getString("razon_social") : "";
                rsCliente.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            objCliente.cerrar();
        }
        numero_contrato = id_sucursal + " - " + num_contrato;
        Sucursal objSucursal = new Sucursal(m1, p1, db1, u1, c1);
        String ciudad = objSucursal.getCiudad(Integer.parseInt(id_sucursal));
        anexos[0][6] = ciudad;
        objSucursal.cerrar();

        contrato = contrato.replace("<<numero>>", id_sucursal + "-" + num_contrato);
        contrato = contrato.replace("<<fecha_inicio>>", fecha_contrato);
        contrato = contrato.replace("<<ciudad>>", ciudad);

        if (ruc_representante.compareTo("") != 0 && representante.compareTo("") != 0) {
            contrato = contrato.replace("el SUSCRIPTOR", "el/la Sr(a). " + Cadena.capital(representante)
                    + " en calidad de Representante de " + razon_social.toUpperCase() + " con " + (ruc_representante.length() == 13 ? "R.U.C." : "C.I.") + " Nro. " + ruc_representante);
        }

        //autorizacion = autorizacion.replace("<<CIUDAD>>", ciudad);
        //autorizacion = autorizacion.replace("<<FECHA_SOLICITUD>>", Fecha.getFechaSolicitud(Fecha.getFecha("ISO")));
        autorizacion = autorizacion.replace("<<CLIENTE>>", razon_social);
        autorizacion = autorizacion.replace("<<TIPO_DOCUMENTO>>", (tipo_documento.compareTo("04") == 0 ? "RUC" : (tipo_documento.compareTo("05") == 0 ? "cédula" : "pasaporte")));
        autorizacion = autorizacion.replace("<<CEDULA>>", cedula);
        anexos[0][0] = contrato;
        anexos[0][1] = autorizacion;
        return anexos;
    }

    public String[] Firmas(String cedula, String cliente, String representante) {
        String datosFirma[] = {"", "", "", "", "", "", ""};
        Configuracion conf = new Configuracion(m1, p1, db1, u1, c1);
        datosFirma[0] = conf.getValor("rep_cargo").toUpperCase();
        datosFirma[1] = representante.compareTo("") != 0 ? cliente.toUpperCase() : "EL SUSCRIPTOR";
        datosFirma[2] = conf.getValor("rep_nombre");
        datosFirma[3] = representante.compareTo("") != 0 ? Cadena.capital(representante) : Cadena.capital(cliente);
        datosFirma[4] = "R.U.C.: " + conf.getValor("ruc");
        datosFirma[5] = (cedula.length() == 13 ? "R.U.C.: " : "C.I.: ") + cedula;
        conf.cerrar();
        return datosFirma;
    }

    public class PdfCotrato extends PdfPageEventHelper {

        public void onStartPage(PdfWriter writer, Document document) {
            try {
                PdfPTable encabezado = new PdfPTable(2);
                encabezado.setTotalWidth(document.right() - document.left() - 120);
                PdfPCell logoCel = Addons.setLogo(_URL_ANEXOS + "dir/logo.jpg", 200, 60);
                if (logoCel != null) {
                    encabezado.addCell(logoCel);
                } else {
                    encabezado.addCell("");
                }
                encabezado.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_RIGHT, 0));
                encabezado.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_RIGHT, 0));
                encabezado.addCell(Addons.setCeldaPDF("Contrato Nro. " + numero_contrato, Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_RIGHT, 0));
                encabezado.writeSelectedRows(0, -1, 80, document.top() + 80, writer.getDirectContent());

                PdfContentByte cb = writer.getDirectContent();
                cb.setLineWidth(2);
                cb.moveTo(60, document.top() + 10);
                cb.lineTo(document.right() - document.left() - 58, document.top() + 10);
            } catch (Exception e) {
                throw new ExceptionConverter(e);
            }

        }

        public void onEndPage(PdfWriter writer, Document document) {
            try {
                Image img = Addons.setMarcaAgua(_URL_ANEXOS + "dir/logo_agua.jpg", 681, 206);
                if (img != null) {
                    document.add(img);
                }
            } catch (Exception e) {
                throw new ExceptionConverter(e);
            }
        }

        public void imprimir(HttpServletResponse response, boolean oki, String documentos[][], String firmas[]) {
            /* inicio PDF */
            Document document = new Document(PageSize.A4);// paso 1
            document.setMargins(30, 0, 90, 30);
            /*Izquierda, derecha, tope, pie */

            try {
                PdfWriter writer = null;
                if (oki) {
                    try {
                        writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2
                        writer.setPageEvent(new Pdf.PdfCotrato());
                    } catch (Exception e) {
                        System.out.println("" + e.getLocalizedMessage() + " " + e.getMessage());
                    }
                }
                try {
                    documento = new File(_dir + "/" + numero_contrato.trim() + ".pdf");
                    nom_documento = documento.getName();
                    PdfWriter.getInstance(document, new FileOutputStream(documento));
                } catch (Exception e) {
                    System.out.println("" + e.getLocalizedMessage() + " " + e.getMessage());
                }
                document.open(); // paso 3
//                if (oki) {
//                    writer.addJavaScript("this.print(false);", false); // Para enviar a la impresora automáticamente.
//                }
                /* todo el cuerpo del doc es el paso 4 */
                PdfPTable tbl_det = new PdfPTable(1);
                tbl_det.addCell(Addons.setCeldaPDF(documentos[0][0], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                document.add(tbl_det);

                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));

                PdfPTable tbl_firma = new PdfPTable(2);
                tbl_firma.addCell(Addons.setCeldaPDF("__________________________", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_firma.addCell(Addons.setCeldaPDF("__________________________", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));

                for (int i = 0; i < firmas.length; i++) {
                    tbl_firma.addCell(Addons.setCeldaPDF(firmas[i], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                }
                document.add(tbl_firma);

                //anexo depende del proveedor y cliente
                /*modelo de contrato */
                double iva_actual = Double.parseDouble(documentos[3][3]);
                double valor_instalacion = Double.parseDouble(documentos[3][0]);
                valor_instalacion = valor_instalacion + ((valor_instalacion * iva_actual) / 100);
                valor_instalacion = Addons.redondear(valor_instalacion, 2);
                int tiempo_permanencia = Integer.parseInt(documentos[3][1]);
                double costo_instalacion_sector = Double.parseDouble(documentos[3][2]);
                double porcentaje_descuento = Double.parseDouble(documentos[3][4]);
                costo_instalacion_sector = costo_instalacion_sector + ((costo_instalacion_sector * iva_actual) / 100);
                valor_instalacion = (costo_instalacion_sector > valor_instalacion ? costo_instalacion_sector : valor_instalacion);
                String tiempo_permanecia_final = (documentos[2][20].trim().compareTo("") == 0 ? "" + tiempo_permanencia : documentos[2][18]);
                double costo_instalacion_promocion = (valor_instalacion - ((valor_instalacion * porcentaje_descuento) / 100));
                double abono_final = (documentos[2][20].trim().compareTo("") == 0 ? costo_instalacion_sector : costo_instalacion_promocion);
                double valor_penal = (documentos[2][20].trim().compareTo("") == 0 ? (valor_instalacion - costo_instalacion_sector) : valor_instalacion - (costo_instalacion_promocion));
                boolean tieneDiferenciaCostoInstalacion = (valor_instalacion - Addons.redondear(costo_instalacion_sector, 2)) > 0;
                document.newPage();
                if (documentos[1][1].trim().compareTo("c") == 0) {
                    tbl_det = new PdfPTable(1);
                    //tbl_det.addCell(Addons.setCeldaPDF(documentos[0][4] + ", " + Fecha.getFechaSolicitud(documentos[0][5]), Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                    //tbl_det.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    tbl_det.addCell(Addons.setCeldaPDF("SERVICIO DE ACCESO A INTERNET", Font.HELVETICA, 12, Font.BOLD, Element.ALIGN_CENTER, 0));
                    tbl_det.addCell(Addons.setCeldaPDF("ANEXO 1", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    tbl_det.addCell(Addons.setCeldaPDF("INFORMACIÒN DEL PLAN COSTOS Y FORMAS DE PAGO", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    tbl_det.addCell(Addons.setCeldaPDF(documentos[0][4] + ", " + Fecha.getFechaSolicitud(documentos[0][5]), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    tbl_det.addCell(Addons.setCeldaPDF("INFORMACIÒN DEL PLAN", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    tbl_det.addCell(Addons.setCeldaPDF("NOMBRE DEL PLAN: " + documentos[1][3], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    document.add(tbl_det);
                    /////
                    //document.add(new Paragraph(" "));
                    tbl_det = new PdfPTable(1);
                    tbl_det.addCell(Addons.setCeldaPDF("RED DE ACCESO", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    document.add(tbl_det);
                    tbl_det = new PdfPTable(4);
                    String[][] tipo_instalacion = {{"a", "Inalámbrico (antena)"}, {"f", "Fibra (punto a punto)"}};
                    if (Integer.parseInt(documentos[2][11]) == 9) {
                        tipo_instalacion = new String[][]{{"a", "Inalámbrico (antena)"}, {"n", "inalámbrico prepago (antena)"}, {"f", "Fibra (punto a punto)"}};
                    }
                    if (Integer.parseInt(documentos[2][11]) != 5 || Integer.parseInt(documentos[2][11]) != 9 || Integer.parseInt(documentos[2][11]) != 10) {
                        tipo_instalacion = new String[][]{{"a", "Inalámbrico (antena)"}, {"n", "inalámbrico PYMES (antena)"}, {"f", "Fibra (punto a punto)"}, {"g", "Fibra GEPON"}};
                    }
                    for (int i = 0; i < tipo_instalacion.length; i++) {
                        tbl_det.addCell(Addons.setCeldaPDF(tipo_instalacion[i][1], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_det.addCell(Addons.setCeldaPDF((tipo_instalacion[i][0].compareTo(documentos[1][2]) == 0 ? "X" : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    }
                    document.add(tbl_det);
                    ////
                    //document.add(new Paragraph(" "));
                    tbl_det = new PdfPTable(1);
                    tbl_det.addCell(Addons.setCeldaPDF("TIPO DE CUENTA", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    document.add(tbl_det);
                    tbl_det = new PdfPTable(4);
                    String[][] tipo_cuenta = {{"residencial", "Residencial"}, {"corporativo", "Corporativo"}, {"small", "Negocio Small"}, {"otros", "Otros"}};
                    boolean ok = false;
                    for (int i = 0; i < tipo_cuenta.length; i++) {
                        tbl_det.addCell(Addons.setCeldaPDF(tipo_cuenta[i][1], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        if (tipo_cuenta[i][0].indexOf(documentos[1][16].toLowerCase()) >= 0) {
                            tbl_det.addCell(Addons.setCeldaPDF("X", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            ok = true;
                        } else {
                            if (!ok && tipo_cuenta.length - 1 == i) {
                                tbl_det.addCell(Addons.setCeldaPDF("X", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            } else {
                                tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            }
                        }

                    }
                    document.add(tbl_det);
                    /////
                    //document.add(new Paragraph(" "));
                    tbl_det = new PdfPTable(1);
                    tbl_det.addCell(Addons.setCeldaPDF("VELOCIDAD", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    tbl_det.addCell(Addons.setCeldaPDF("Compartición " + documentos[1][5], Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    document.add(tbl_det);
                    tbl_det = new PdfPTable(4);
                    String[] velocidad = {"Comercial de bajada", "Comercial de subida", "Minima efectiva de bajada", "Minima efectiva de subida"};
                    for (int i = 0; i < velocidad.length; i++) {
                        tbl_det.addCell(Addons.setCeldaPDF(velocidad[i], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        tbl_det.addCell(Addons.setCeldaPDF(documentos[1][4] + " Mbps", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    }
                    document.add(tbl_det);
                    ////
                    /////
                    //document.add(new Paragraph(" "));
                    tbl_det = new PdfPTable(1);
                    tbl_det.addCell(Addons.setCeldaPDF("SERVICIOS ADICIONALES QUE OFRECE", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    document.add(tbl_det);
                    tbl_det = new PdfPTable(5);
                    tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("SI", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("NO", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("DESCRIPCIÓN", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("CANT.", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    String servicios_adicionales[][] = ServiciosAdicionales(documentos[1][18]);
                    if (servicios_adicionales != null) {
                        for (int i = 0; i < servicios_adicionales.length; i++) {
                            tbl_det.addCell(Addons.setCeldaPDF(servicios_adicionales[i][0], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            tbl_det.addCell(Addons.setCeldaPDF("X", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            tbl_det.addCell(Addons.setCeldaPDF(servicios_adicionales[i][1], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            tbl_det.addCell(Addons.setCeldaPDF(servicios_adicionales[i][2], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        }

                    }
                    document.add(tbl_det);
                    ////
                    /////
                    //document.add(new Paragraph(" "));

                    tbl_det = new PdfPTable(1);
                    tbl_det.addCell(Addons.setCeldaPDF("PLAZO DEL CONTRATO", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    document.add(tbl_det);
                    tbl_det = new PdfPTable(4);
                    tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("SI", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("NO", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("TIEMPO MESES", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("El contato incluye permanencia mínima", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("" + (documentos[2][20].trim().compareTo("") != 0 || tieneDiferenciaCostoInstalacion ? "X" : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("" + ((documentos[2][20].trim().compareTo("") == 0) && !tieneDiferenciaCostoInstalacion ? "X" : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("" + ((documentos[2][20].trim().compareTo("") == 0) && !tieneDiferenciaCostoInstalacion ? "0" : tiempo_permanecia_final), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));

                    tbl_det.addCell(Addons.setCeldaPDF("Beneficios por permanencia mínima", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("Valor abonado $" + Addons.redondear(abono_final, 2) + " al costo de instalacion", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1, 3, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("Valor a cancelar en caso de no completar el tiempo de permanencia minima $" + Addons.redondear(valor_penal, 2), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1, 3, 2));
                    document.add(tbl_det);
                    ////
                    /////
                    //document.add(new Paragraph(" "));
                    tbl_det = new PdfPTable(1);
                    tbl_det.addCell(Addons.setCeldaPDF("TARIFAS", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    tbl_det.addCell(Addons.setCeldaPDF("COSTOS Y FORMAS DE PAGO", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    document.add(tbl_det);
                    tbl_det = new PdfPTable(2);
                    try {
                        tbl_det.addCell(Addons.setCeldaPDF("Instalación (Valor a pagar por una sola vez)", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("" + valor_instalacion, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("Plazo para activar instalación (Horas ,Dias)", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("Pago mensual más impuestos", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                        tbl_det.addCell(Addons.setCeldaPDF( Addons.redondearDecimales( Double.parseDouble(documentos[1][15]) ), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("Modalidad de pago", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("" + documentos[1][8], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("Formas de pag", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("" + (documentos[1][9].compareTo("") == 0 ? "Ninguna" : (documentos[1][9].compareTo("CTA") == 0 ? "Cuenta Bancaria" : "Tarjeta")), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                    } catch (Exception e) {
                        System.out.println("" + e.getLocalizedMessage() + " " + e.getMessage());
                    }
                    document.add(tbl_det);
                    ///
                    /////
                    //document.add(new Paragraph(" "));
                    tbl_det = new PdfPTable(1);
                    tbl_det.addCell(Addons.setCeldaPDF("OTROS VALORES", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    document.add(tbl_det);
                    tbl_det = new PdfPTable(2);
                    tbl_det.addCell(Addons.setCeldaPDF("SERVICIOS", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("VALOR (USD)", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                    if (servicios_adicionales != null) {
                        double total_servicios = 0;
                        for (int i = 0; i < servicios_adicionales.length; i++) {
                            tbl_det.addCell(Addons.setCeldaPDF(servicios_adicionales[i][0], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                            tbl_det.addCell(Addons.setCeldaPDF(servicios_adicionales[i][3], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                            try {
                                total_servicios += (Double.parseDouble(servicios_adicionales[i][3]) * Double.parseDouble(servicios_adicionales[i][2]));
                            } catch (Exception e) {
                                System.out.println("" + e.getLocalizedMessage() + "   " + e.getMessage());
                            }
                        }
                        tbl_det.addCell(Addons.setCeldaPDF("Total otros Servicios", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("" + formato.format(total_servicios), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                    }

                    document.add(tbl_det);
                    ///
                    /////
                    document.add(new Paragraph(" "));
                    tbl_det = new PdfPTable(2);
                    tbl_det.addCell(Addons.setCeldaPDF("Sitio web para consulta de tarifas", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("https://saitel.ec/planes/escoge-tu-mejor-plan/", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("Sitio web consulta calidad del servicio", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("https://saitel.ec/consulta_calidad/", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                    document.add(tbl_det);
                    ///
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(" "));

                    tbl_firma = new PdfPTable(2);
                    tbl_firma.addCell(Addons.setCeldaPDF("__________________________", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_firma.addCell(Addons.setCeldaPDF("__________________________", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));

                    for (int i = 0; i < firmas.length; i++) {
                        tbl_firma.addCell(Addons.setCeldaPDF(firmas[i], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    }
                    document.add(tbl_firma);
                    ////para proveedor
                } else {
                    tbl_det = new PdfPTable(1);
                    //tbl_det.addCell(Addons.setCeldaPDF(documentos[0][4] + ", " + Fecha.getFechaSolicitud(documentos[0][5]), Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                    //tbl_det.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    tbl_det.addCell(Addons.setCeldaPDF("SERVICIO PORTADOR", Font.HELVETICA, 12, Font.BOLD, Element.ALIGN_CENTER, 0));
                    tbl_det.addCell(Addons.setCeldaPDF("ANEXO 1", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    tbl_det.addCell(Addons.setCeldaPDF("INFORMACIÒN DEL PLAN COSTOS Y FORMAS DE PAGO", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    tbl_det.addCell(Addons.setCeldaPDF(documentos[0][4] + ", " + Fecha.getFechaSolicitud(documentos[0][5]), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    document.add(tbl_det);
                    /////
                    document.add(new Paragraph(" "));
                    tbl_det = new PdfPTable(1);
                    tbl_det.addCell(Addons.setCeldaPDF("PLAZO DEL CONTRATO", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    document.add(tbl_det);
                    tbl_det = new PdfPTable(4);
                    tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("SI", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("NO", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("TIEMPO MESES", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("El contato incluye permanencia minima", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("" + (documentos[2][20].trim().compareTo("") == 0 ? "" : "X"), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("" + (documentos[2][20].trim().compareTo("") == 0 ? "X" : ""), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("" + documentos[2][18], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("Beneficios por permanencia mínima", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("" + documentos[2][19], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1, 3, 3));
                    ////

                    /////
                    //document.add(new Paragraph(" "));
                    tbl_det = new PdfPTable(1);
                    tbl_det.addCell(Addons.setCeldaPDF("ENLACES NACIONALES", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    document.add(tbl_det);
                    tbl_det = new PdfPTable(5);
                    tbl_det.addCell(Addons.setCeldaPDF("N°", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("DESCRIPCIÓN", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("MEDIO TRANSMISIÓN", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("VELOCIDAD DE TRASMISIÓN", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("COMPARTICIÓN", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("1", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("" + documentos[1][3], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("" + (documentos[1][2].trim().compareTo("n") == 0 || documentos[1][2].trim().compareTo("a") == 0 ? "MEDIO INALAMBRICO" : "FIBRA OPTICA"), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("" + documentos[1][4] + "  Mbps", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("" + documentos[1][5], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    document.add(tbl_det);
                    ////

                    /////
                    //document.add(new Paragraph(" "));
                    tbl_det = new PdfPTable(1);
                    tbl_det.addCell(Addons.setCeldaPDF("ENLACES INTERNACIONALES", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    document.add(tbl_det);
                    tbl_det = new PdfPTable(5);
                    tbl_det.addCell(Addons.setCeldaPDF("N°", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("DESCRIPCIÓN", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("MEDIO TRANSMISIÓN", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("CELOCIDAD DE TRASMISIÓN", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("COMPARTICIÓN", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    document.add(tbl_det);
                    ////

                    /////
                    //document.add(new Paragraph(" "));
                    tbl_det = new PdfPTable(1);
                    tbl_det.addCell(Addons.setCeldaPDF("SERVICIOS ADICIONALES QUE OFRECE", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    document.add(tbl_det);
                    tbl_det = new PdfPTable(5);
                    tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("SI", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("NO", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("DESCRIPCIÓN", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("CANT.", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                    String servicios_adicionales[][] = ServiciosAdicionales(documentos[1][18]);
                    if (servicios_adicionales != null) {
                        for (int i = 0; i < servicios_adicionales.length; i++) {
                            tbl_det.addCell(Addons.setCeldaPDF(servicios_adicionales[i][0], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            tbl_det.addCell(Addons.setCeldaPDF("X", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            tbl_det.addCell(Addons.setCeldaPDF(servicios_adicionales[i][1], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                            tbl_det.addCell(Addons.setCeldaPDF(servicios_adicionales[i][2], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                        }

                    }
                    document.add(tbl_det);
                    ////

                    /////
                    //document.add(new Paragraph(" "));
                    tbl_det = new PdfPTable(1);
                    tbl_det.addCell(Addons.setCeldaPDF("TARIFAS", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    tbl_det.addCell(Addons.setCeldaPDF("COSTOS Y FORMAS DE PAGO", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    document.add(tbl_det);
                    try {
                        tbl_det = new PdfPTable(2);
                        tbl_det.addCell(Addons.setCeldaPDF("Instalación (Valor a pagar por una sola vez)", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("" + valor_instalacion, Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("Plazo para activar instalación (Horas ,Dias)", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("Pago mensual más impuestos", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                        tbl_det.addCell(Addons.setCeldaPDF( Addons.redondearDecimales( Double.parseDouble(documentos[1][15]) ), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("Modalidad de pago", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("" + documentos[1][8], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("Formas de pago", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("" + (documentos[1][9].compareTo("") == 0 ? "Ninguna" : (documentos[1][9].compareTo("CTA") == 0 ? "Cuenta Bancaria" : "Tarjeta")), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                    } catch (Exception e) {
                        System.out.println("" + e.getLocalizedMessage() + " " + e.getMessage());
                    }
                    document.add(tbl_det);
                    ///
                    /////
                    //document.add(new Paragraph(" "));
                    tbl_det = new PdfPTable(1);
                    tbl_det.addCell(Addons.setCeldaPDF("OTROS VALORES", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    document.add(tbl_det);
                    tbl_det = new PdfPTable(2);
                    tbl_det.addCell(Addons.setCeldaPDF("SERVICIOS", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("VALOR (USD)", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                    if (servicios_adicionales != null) {
                        double total_servicios = 0;
                        for (int i = 0; i < servicios_adicionales.length; i++) {
                            tbl_det.addCell(Addons.setCeldaPDF(servicios_adicionales[i][0], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                            tbl_det.addCell(Addons.setCeldaPDF(servicios_adicionales[i][3], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                            try {
                                total_servicios += (Double.parseDouble(servicios_adicionales[i][3]) * Double.parseDouble(servicios_adicionales[i][2]));
                            } catch (Exception e) {
                                System.out.println("" + e.getLocalizedMessage() + "   " + e.getMessage());
                            }
                        }
                        tbl_det.addCell(Addons.setCeldaPDF("Total otros Servicios", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                        tbl_det.addCell(Addons.setCeldaPDF("" + formato.format(total_servicios), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                    }
                    document.add(tbl_det);
                    ///
                    /////
                    document.add(new Paragraph(" "));
                    tbl_det = new PdfPTable(2);
                    tbl_det.addCell(Addons.setCeldaPDF("Sitio web para consulta de tarifas", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("https://saitel.ec/planes/escoge-tu-mejor-plan/", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("Sitio web consulta calidad del servicio", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                    tbl_det.addCell(Addons.setCeldaPDF("https://saitel.ec/consulta_calidad/", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 1));
                    document.add(tbl_det);
                    ///
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph(" "));

                    tbl_firma = new PdfPTable(2);
                    tbl_firma.addCell(Addons.setCeldaPDF("__________________________", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    tbl_firma.addCell(Addons.setCeldaPDF("__________________________", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));

                    for (int i = 0; i < firmas.length; i++) {
                        tbl_firma.addCell(Addons.setCeldaPDF(firmas[i], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                    }
                    document.add(tbl_firma);
                }
                document.newPage();
                /////dartos del cliente y de la instalacion
                tbl_det = new PdfPTable(1);
                //tbl_det.addCell(Addons.setCeldaPDF(documentos[0][4] + ", " + Fecha.getFechaSolicitud(documentos[0][5]), Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                //tbl_det.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("DATOS DEL CLIENTE, DATOS DE LA INSTALACIÓN Y CONTACTOS PROVEEDOR", Font.HELVETICA, 12, Font.BOLD, Element.ALIGN_CENTER, 0));
                tbl_det.addCell(Addons.setCeldaPDF("ANEXO 2", Font.HELVETICA, 10, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("INFORMACIÒN DEL ABONADO CLIENTE", Font.HELVETICA, 9, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("ABONADO/CLIENTE: " + (documentos[0][8].trim().compareTo("") != 0 ? Cadena.capital(documentos[0][8]) : Cadena.capital(documentos[0][7])), Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("TIPO DE IDENTIFICACIÓN: " + documentos[2][12] + " N° " + documentos[0][6], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                document.add(tbl_det);
                ///////
                //document.add(new Paragraph(" "));
                tbl_det = new PdfPTable(1);
                tbl_det.addCell(Addons.setCeldaPDF("CONTACTOS DEL CLIENTE", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_JUSTIFIED, 0));
                document.add(tbl_det);
                tbl_det = new PdfPTable(4);
                tbl_det.addCell(Addons.setCeldaPDF("Email", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                tbl_det.addCell(Addons.setCeldaPDF(documentos[2][4], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                tbl_det.addCell(Addons.setCeldaPDF("Telefonos", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                tbl_det.addCell(Addons.setCeldaPDF(documentos[2][13], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
//                tbl_det.addCell(Addons.setCeldaPDF("Dirección: " + documentos[1][0], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1, 3, 2));
//                tbl_det.addCell(Addons.setCeldaPDF("Referencia: ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1, 3, 2));
//                tbl_det.addCell(Addons.setCeldaPDF("Parroquia", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
//                tbl_det.addCell(Addons.setCeldaPDF(documentos[1][12], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
//                tbl_det.addCell(Addons.setCeldaPDF("Cantón", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
//                tbl_det.addCell(Addons.setCeldaPDF(documentos[1][11], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
//                tbl_det.addCell(Addons.setCeldaPDF("Provincia", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
//                tbl_det.addCell(Addons.setCeldaPDF(documentos[1][10], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                /////cambio
                tbl_det.addCell(Addons.setCeldaPDF("Dirección: " + documentos[2][0], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1, 3, 2));
                tbl_det.addCell(Addons.setCeldaPDF("Referencia: ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1, 3, 2));
                tbl_det.addCell(Addons.setCeldaPDF("Parroquia", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                tbl_det.addCell(Addons.setCeldaPDF(documentos[2][17], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                tbl_det.addCell(Addons.setCeldaPDF("Cantón", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                tbl_det.addCell(Addons.setCeldaPDF(documentos[2][16], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                tbl_det.addCell(Addons.setCeldaPDF("Provincia", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                tbl_det.addCell(Addons.setCeldaPDF(documentos[2][15], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                ////cambio
                document.add(tbl_det);
                ////
///////
                // document.add(new Paragraph(" "));
                tbl_det = new PdfPTable(1);
                tbl_det.addCell(Addons.setCeldaPDF("LUGAR DE INSTALACIÓN", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_JUSTIFIED, 0));
                document.add(tbl_det);
                tbl_det = new PdfPTable(4);
//                tbl_det.addCell(Addons.setCeldaPDF("Dirección: " + documentos[2][0], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1, 3, 2));
//                tbl_det.addCell(Addons.setCeldaPDF("Referencia: ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1, 3, 2));
//                tbl_det.addCell(Addons.setCeldaPDF("Parroquia", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
//                tbl_det.addCell(Addons.setCeldaPDF(documentos[2][17], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
//                tbl_det.addCell(Addons.setCeldaPDF("Cantón", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
//                tbl_det.addCell(Addons.setCeldaPDF(documentos[2][16], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
//                tbl_det.addCell(Addons.setCeldaPDF("Provincia", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
//                tbl_det.addCell(Addons.setCeldaPDF(documentos[2][15], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                ///cambio
                tbl_det.addCell(Addons.setCeldaPDF("Dirección: " + documentos[1][0], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1, 3, 2));
                tbl_det.addCell(Addons.setCeldaPDF("Referencia: ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1, 3, 2));
                tbl_det.addCell(Addons.setCeldaPDF("Parroquia", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                tbl_det.addCell(Addons.setCeldaPDF(documentos[1][12], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                tbl_det.addCell(Addons.setCeldaPDF("Cantón", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                tbl_det.addCell(Addons.setCeldaPDF(documentos[1][11], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                tbl_det.addCell(Addons.setCeldaPDF("Provincia", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                tbl_det.addCell(Addons.setCeldaPDF(documentos[1][10], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                //cambio
                document.add(tbl_det);
                ////
                ///////
                // document.add(new Paragraph(" "));
                tbl_det = new PdfPTable(1);
                tbl_det.addCell(Addons.setCeldaPDF("PUNTOS GPS", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("Latitud: " + documentos[1][13], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("Logitud: " + documentos[1][14], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                document.add(tbl_det);
                ////////
                ///////
                document.add(new Paragraph(" "));
                tbl_det = new PdfPTable(1);
                tbl_det.addCell(Addons.setCeldaPDF("CROQUIS", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_JUSTIFIED, 0));
                Image imagen = null;
                try {
                    imagen = Image.getInstance(documentos[1][17]);
                    imagen.scaleAbsolute(300, 300);
                    PdfPCell celdaImg = new PdfPCell(imagen);
                    celdaImg.setBorderWidth(0);
                    celdaImg.setPadding(0);
                    tbl_det.addCell(celdaImg);

                } catch (Exception e) {
                    tbl_det.addCell("");
                }
                document.add(tbl_det);
                ////////
                /////
                document.add(new Paragraph(" "));
                tbl_det = new PdfPTable(1);
                tbl_det.addCell(Addons.setCeldaPDF("FACTURA", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_JUSTIFIED, 0));
                document.add(tbl_det);
                tbl_det = new PdfPTable(4);
                tbl_det.addCell(Addons.setCeldaPDF("FÍSICA", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                tbl_det.addCell(Addons.setCeldaPDF("ELECTRÓNICA", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                tbl_det.addCell(Addons.setCeldaPDF("X", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1));
                tbl_det.addCell(Addons.setCeldaPDF("EMAIL", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1, 3, 2));
                tbl_det.addCell(Addons.setCeldaPDF(documentos[2][4], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 1, 3, 2));
                document.add(tbl_det);
                ////
                ///
                document.add(new Paragraph(" "));
                tbl_det = new PdfPTable(1);
                tbl_det.addCell(Addons.setCeldaPDF("Si su elección es la factura electrónica puede descargarse la misma en el link: ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("https://saitelapp.ec/html/pags/facturaElectronica/index.php", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("CONSULTAS TUS PLANILLAS", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("https://saitelapp.ec:8080/sitio/planilla.jsp", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("Planes y tarifas:", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("Sitio web para consulta de tarifas: https://saitel.ec/html/planes.html", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("CONTACTOS PROVEEDOR", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("Email información SAITEL: info@saitel.ec", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("Email información sucursal:", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("Email soporte SAITEL: soporte@saitel.ec", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("Email soporte sucursal:", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("Teléfonos: 1700(SAITEL)724835, Call Center 1996724835 ", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("HORARIOS DE ATENCIÓN", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("Lunes a Viernes: 08:00 a 18:00", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("Sabados: 09:00 a 13:00", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                document.add(tbl_det);
                /////
                ///
                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));

                tbl_firma = new PdfPTable(2);
                tbl_firma.addCell(Addons.setCeldaPDF("__________________________", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_firma.addCell(Addons.setCeldaPDF("__________________________", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));

                for (int i = 0; i < firmas.length; i++) {
                    tbl_firma.addCell(Addons.setCeldaPDF(firmas[i], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                }
                document.add(tbl_firma);
                ///datos del cliente y la instalacion

                //custodia de equipos y propiedad de saitel
                document.newPage();
                tbl_det = new PdfPTable(1);
                tbl_det.addCell(Addons.setCeldaPDF(documentos[0][4] + ", " + Fecha.getFechaSolicitud(documentos[0][5]), Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                tbl_det.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("CUSTODIA DE EQUIPOS PROPIEDAD DE SAITEL ENTREGADOS AL ABONADO/CLIENTE", Font.HELVETICA, 12, Font.BOLD, Element.ALIGN_CENTER, 0));
                tbl_det.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF(documentos[0][1], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                document.add(tbl_det);

                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));

                tbl_firma = new PdfPTable(2);
                tbl_firma.addCell(Addons.setCeldaPDF("__________________________", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_firma.addCell(Addons.setCeldaPDF("__________________________", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));

                for (int i = 0; i < firmas.length; i++) {
                    tbl_firma.addCell(Addons.setCeldaPDF(firmas[i], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                }
                document.add(tbl_firma);

                //pagina acceso y seguridad de 
                document.newPage();
                tbl_det = new PdfPTable(1);
                tbl_det.addCell(Addons.setCeldaPDF(documentos[0][4] + ", " + Fecha.getFechaSolicitud(documentos[0][5]), Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                tbl_det.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("ACCESO Y SEGURIDAD DE INFORMACION", Font.HELVETICA, 12, Font.BOLD, Element.ALIGN_CENTER, 0));
                tbl_det.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF(documentos[0][2], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                document.add(tbl_det);

                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));

                tbl_firma = new PdfPTable(2);
                tbl_firma.addCell(Addons.setCeldaPDF("__________________________", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_firma.addCell(Addons.setCeldaPDF("__________________________", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));

                for (int i = 0; i < firmas.length; i++) {
                    tbl_firma.addCell(Addons.setCeldaPDF(firmas[i], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                }
                document.add(tbl_firma);
                ///pagina del buro de credito
                document.newPage();

                tbl_det = new PdfPTable(1);
                tbl_det.addCell(Addons.setCeldaPDF(documentos[0][4] + ", " + Fecha.getFechaSolicitud(documentos[0][5]), Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                tbl_det.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("AUTORIZACIÓN CONSULTA BURO DE CRÉDITO", Font.HELVETICA, 12, Font.BOLD, Element.ALIGN_CENTER, 0));
                tbl_det.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF(documentos[0][3], Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                document.add(tbl_det);

                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));

                tbl_firma = new PdfPTable(1);
                tbl_firma.addCell(Addons.setCeldaPDF("Nombres y apellidos:", Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_firma.addCell(Addons.setCeldaPDF("Cédula/Pasaporte:", Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_firma.addCell(Addons.setCeldaPDF("Firma:", Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_LEFT, 0));

                document.add(tbl_firma);

                if (documentos[0][9].compareTo("") != 0) {
                    document.newPage();
                    PdfPTable tbl_AdultoMayor = new PdfPTable(1);
                    tbl_AdultoMayor.addCell(Addons.setCeldaPDF(documentos[0][4] + ", " + Fecha.getFechaSolicitud(documentos[0][5]), Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                    tbl_AdultoMayor.addCell(Addons.setCeldaPDF(documentos[0][9], Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    document.add(tbl_AdultoMayor);
                }
                
                if (documentos[0][10].compareTo("") != 0) {
                    document.newPage();
                    PdfPTable tblTrataDatos = new PdfPTable(1);
                    tblTrataDatos.addCell(Addons.setCeldaPDF(documentos[0][10], Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    document.add(tblTrataDatos);
                }

            } catch (IllegalStateException ie) {
                ie.printStackTrace();
            } catch (DocumentException de) {
                de.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            document.close(); // paso 5
            /* fin PDF */
        }

    }

    public class PdfCotratoAntiguo extends PdfPageEventHelper {

        public void onStartPage(PdfWriter writer, Document document) {
            try {
                PdfPTable encabezado = new PdfPTable(2);
                encabezado.setTotalWidth(document.right() - document.left() - 120);
                PdfPCell logoCel = Addons.setLogo(_URL_ANEXOS + "dir/logo.jpg", 200, 60);
                if (logoCel != null) {
                    encabezado.addCell(logoCel);
                } else {
                    encabezado.addCell("");
                }
                encabezado.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_RIGHT, 0));
                encabezado.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_RIGHT, 0));
                encabezado.addCell(Addons.setCeldaPDF("Contrato Nro. " + numero_contrato, Font.HELVETICA, 9, Font.BOLD, Element.ALIGN_RIGHT, 0));
                encabezado.writeSelectedRows(0, -1, 80, document.top() + 80, writer.getDirectContent());

                PdfContentByte cb = writer.getDirectContent();
                cb.setLineWidth(2);
                cb.moveTo(60, document.top() + 10);
                cb.lineTo(document.right() - document.left() - 58, document.top() + 10);
            } catch (Exception e) {
                throw new ExceptionConverter(e);
            }

        }

        public void onEndPage(PdfWriter writer, Document document) {
            try {
                Image img = Addons.setMarcaAgua(_URL_ANEXOS + "dir/logo_agua.jpg", 681, 206);
                if (img != null) {
                    document.add(img);
                }
            } catch (Exception e) {
                throw new ExceptionConverter(e);
            }
        }

        public void imprimir(HttpServletResponse response, boolean oki, String documentos[][], String firmas[]) {
            /* inicio PDF */
            Document document = new Document(PageSize.A4);// paso 1
            document.setMargins(30, 0, 90, 30);
            /*Izquierda, derecha, tope, pie */

            try {
                PdfWriter writer = null;
                if (oki) {
                    try {
                        writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2
//                        writer.setPageEvent(new Pdf.PdfCotratoAntiguo());
                    } catch (Exception e) {
                        System.out.println("" + e.getLocalizedMessage() + " " + e.getMessage());
                    }
                }
                try {
                    documento = new File(_dir + "/" + numero_contrato.trim() + ".pdf");
                    nom_documento = documento.getName();
                    PdfWriter.getInstance(document, new FileOutputStream(documento));
                } catch (Exception e) {
                    System.out.println("" + e.getLocalizedMessage() + " " + e.getMessage());
                }
                document.open(); // paso 3
//                if (oki) {
//                    writer.addJavaScript("this.print(false);", false); // Para enviar a la impresora automáticamente.
//                }
                /* todo el cuerpo del doc es el paso 4 */
                PdfPTable tbl_det = new PdfPTable(1);
                tbl_det.addCell(Addons.setCeldaPDF(documentos[0][0], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                document.add(tbl_det);

                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));

                PdfPTable tbl_firma = new PdfPTable(2);
                tbl_firma.addCell(Addons.setCeldaPDF("__________________________", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_firma.addCell(Addons.setCeldaPDF("__________________________", Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));

                for (int i = 0; i < firmas.length; i++) {
                    tbl_firma.addCell(Addons.setCeldaPDF(firmas[i], Font.HELVETICA, 8, Font.NORMAL, Element.ALIGN_LEFT, 0));
                }
                document.add(tbl_firma);
                ///pagina del buro de credito
                document.newPage();

                tbl_det = new PdfPTable(1);
                tbl_det.addCell(Addons.setCeldaPDF(documentos[0][6] + ", " + Fecha.getFechaSolicitud(documentos[0][5]), Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_RIGHT, 0));
                tbl_det.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF("AUTORIZACIÓN CONSULTA BURO DE CRÉDITO", Font.HELVETICA, 12, Font.BOLD, Element.ALIGN_CENTER, 0));
                tbl_det.addCell(Addons.setCeldaPDF(" ", Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                tbl_det.addCell(Addons.setCeldaPDF(documentos[0][1], Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                document.add(tbl_det);

                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));

                tbl_firma = new PdfPTable(1);
                tbl_firma.addCell(Addons.setCeldaPDF("Nombres y apellidos:", Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_firma.addCell(Addons.setCeldaPDF("Cédula/Pasaporte:", Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_LEFT, 0));
                tbl_firma.addCell(Addons.setCeldaPDF("Firma:", Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_LEFT, 0));
                document.add(tbl_firma);

            } catch (IllegalStateException ie) {
                ie.printStackTrace();
            } catch (DocumentException de) {
                de.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            document.close(); // paso 5
            /* fin PDF */
        }

    }

    ////////////////////////////certificado
    public void CrearPdfCertificado(HttpServletResponse response, String id, int id_sucursal) {
        PdfCertificado objPdfCertificado = new PdfCertificado();
        objPdfCertificado.imprimir(response, id, id_sucursal);
    }

    public class PdfCertificado extends PdfPageEventHelper {

        public void onStartPage(PdfWriter writer, Document document) {
            try {
                PdfPTable encabezado = new PdfPTable(1);
                encabezado.setTotalWidth(document.right() - document.left() - 120);
                PdfPCell logoCel = Addons.SetLogo(_URL_ANEXOS + "dir/encabezado.jpg", 200, 60, 20);
                if (logoCel != null) {
                    encabezado.addCell(logoCel);
                } else {
                    encabezado.addCell("");
                }
                encabezado.writeSelectedRows(0, -1, 80, document.top() + 80, writer.getDirectContent());
            } catch (Exception e) {
                throw new ExceptionConverter(e);
            }

        }

        public void onEndPage(PdfWriter writer, Document document) {
            try {
                Image pie = Addons.setPiePagina(_URL_ANEXOS + "dir/pie.png", 600, 206);
                //Image fondo = Addons.setMarcaAgua(_URL_ANEXOS + "dir/fondo.png", 420, 180, 360, 80, 350);
                if (pie != null) {
                    document.add(pie);
                }
//                if (fondo != null) {
//                    document.add(fondo);
//                }
            } catch (Exception e) {
                throw new ExceptionConverter(e);
            }
        }

        public void imprimir(HttpServletResponse response, String id, int id_sucursal) {
            Instalacion objInstalacion = new Instalacion(m1, p1, db1, u1, c1);
            OrdenTrabajo objOrdenTrabajo = new OrdenTrabajo(m1, p1, db1, u1, c1);
            try {
                response.setContentType("application/pdf");
                response.setHeader("Pragma", "no-cache");
                response.setHeader("Expires", "Mon, 01 Jan 2001 00:00:01 GMT");
                response.setHeader("Cache-Control", "no-store");
                response.setHeader("Cache-Control", "must-revalidate");
                response.setHeader("Cache-Control", "no-cache");
                String fecha_actual = Fecha.getAnio() + "-" + Fecha.getMes() + "-" + Fecha.getDia();
                Sucursal objSucursal = new Sucursal(m1, p1, db1, u1, c1);
                String ciudad = objSucursal.getCiudad(id_sucursal);
                objSucursal.cerrar();
                ResultSet rs = null;
                Document document = new Document(PageSize.A4);// paso 1
                document.setMargins(0, 0, 100, 200);    //  left, right, top, botton
                PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream()); // paso 2
                writer.setPageEvent(new PdfCertificado());
                document.open(); // paso 3
                writer.addJavaScript("this.print(false);", false); // Para enviar a la impresora automáticamente.
                String documento = "";
                String ruc = "";
                String razon_social = "";
                String email = "";
                String telefono = "";
                String direccion_del_cliente = "";
                String costo = "";
                String nombre_firma = "";
                String txt_estado_servicio = "";
                String ip = "";
                String plan = "";
                String valor_trabajo = "";
                String nombre_certificado = "";
                String id_plan_nuevo = "";
                String plan_nuevo = "";
                String velocidad_bajada = "";
                String velocidad_subida = "";
                String min_velocidad_bajada = "";
                String min_velocidad_subida = "";
                String comparticion = "";
                String costo_migracion_inalambrico_tmp = "";
                String costo_migracion_fibra_tmp = "";
                int men_tiempo_de_permanencia_min = 0;
                String costo_plan = "0.00";
                String modalidad_pago = "";
                String observaciones = "";
                String id_certificados_isp = "";
                String migracion_fibra = "";
                String tipo_instalacion = "";
                String cambio_domicilio = "";
                String id_promocion = "";
                String direccion = "";
                String id_instalacion = "-1";
                String idCliente = "-1";
                rs = objInstalacion.getCertificado(id);
                String tipo_bloqueo = "";
                String modalidad_antigua = "";
                String modalidad_nueva = "";
                String estado_servicio = "a";
                String parroquia_cliente = "EL SAGRARIO";
                String ciudad_cliente = "IBARRA";
                String provincia_cliente = "IMBABURA";
                String renovacion_tiempo_permanencia = "";
                double renovacion_costo_facturado = 0;
                try {
                    if (rs.next()) {
                        idCliente = rs.getString("id_cliente") != null ? rs.getString("id_cliente") : "";
                        id_instalacion = rs.getString("id_instalacion") != null ? rs.getString("id_instalacion") : "";
                        id_promocion = rs.getString("id_promocion") != null ? rs.getString("id_promocion") : "";
                        migracion_fibra = rs.getString("migracion_fibra") != null ? rs.getString("migracion_fibra") : "";
                        cambio_domicilio = rs.getString("cambio_domicilio") != null ? rs.getString("cambio_domicilio") : "";
                        id_certificados_isp = rs.getString("id_certificados_isp") != null ? rs.getString("id_certificados_isp") : "";
                        estado_servicio = rs.getString("estado_servicio") != null ? rs.getString("estado_servicio") : "";
                        if ( ( (migracion_fibra.trim().compareTo("t") == 0 || migracion_fibra.trim().compareTo("") == 0)
                                &&  id_certificados_isp.trim().compareTo("5") == 0)) {
                            documento = rs.getString("documento_1") != null ? rs.getString("documento_1") : "";
                        } else if ( ( (migracion_fibra.trim().compareTo("t") == 0 || migracion_fibra.trim().compareTo("") == 0)
                                &&  id_certificados_isp.trim().compareTo("4") == 0)) {
                            documento = rs.getString("documento") != null ? rs.getString("documento") : "";
                        } else if (cambio_domicilio.trim().compareTo("t") == 0 && cambio_domicilio.trim().compareTo("") != 0
                                && (id_certificados_isp.trim().compareTo("2") == 0 || id_certificados_isp.trim().compareTo("3") == 0)) {
                            documento = rs.getString("documento_1") != null ? rs.getString("documento_1") : "";
                        } else {
                            documento = rs.getString("documento") != null ? rs.getString("documento") : "";
                            if (!objOrdenTrabajo.getCambiosDomicilio(id_instalacion, "'2','14'") && (id_certificados_isp.trim().compareTo("2") == 0 || id_certificados_isp.trim().compareTo("3") == 0)) {
                                documento = rs.getString("documento_2") != null ? rs.getString("documento_2") : "";
                            }
                            if (id_certificados_isp.compareTo("1") == 0 && estado_servicio.compareTo("t") == 0) {
                                documento = rs.getString("documento_1") != null ? rs.getString("documento_1") : "";
                            }
                            if (id_certificados_isp.compareTo("11") == 0) {
                                documento = rs.getString("documento") != null ? rs.getString("documento") : "";
                            }
                        }

                        ruc = rs.getString("ruc") != null ? rs.getString("ruc") : "";
                        direccion = rs.getString("direccion") != null ? rs.getString("direccion") : "";
                        razon_social = rs.getString("razon_social") != null ? rs.getString("razon_social") : "";
                        email = rs.getString("email") != null ? rs.getString("email") : "";
                        telefono = rs.getString("telefono") != null ? rs.getString("telefono") : "";
                        direccion_del_cliente = rs.getString("direccion_del_cliente") != null ? rs.getString("direccion_del_cliente") : "";
                        costo = rs.getString("txtcosto_plan") != null ? rs.getString("txtcosto_plan") : "";
                        nombre_firma = rs.getString("nombre_firma") != null ? rs.getString("nombre_firma") : "";
                        fecha_actual = rs.getString("fecha_creada") != null ? rs.getString("fecha_creada") : "";
                        txt_estado_servicio = rs.getString("txt_estado_servicio") != null ? rs.getString("txt_estado_servicio") : "";
                        ip = rs.getString("ip") != null ? rs.getString("ip") : "";
                        plan = rs.getString("plan") != null ? rs.getString("plan") : "";
                        id_plan_nuevo = rs.getString("id_plan") != null ? rs.getString("id_plan") : "";
                        plan_nuevo = rs.getString("plan_nuevo") != null ? rs.getString("plan_nuevo") : "";
                        
//                        velocidad_bajada = rs.getString("velocidad_bajada") != null ? Addons.redondear( rs.getString("velocidad_bajada") ) : "";
//                        velocidad_subida = rs.getString("velocidad_subida") != null ? Addons.redondear( rs.getString("velocidad_subida") ) : "";
//                        min_velocidad_bajada = rs.getString("min_velocidad_bajada") != null ? Addons.redondear( rs.getString("min_velocidad_bajada") ) : "";
//                        min_velocidad_subida = rs.getString("min_velocidad_subida") != null ? Addons.redondear( rs.getString("min_velocidad_subida") ) : "";
//                        comparticion = rs.getString("comparticion") != null ? rs.getString("comparticion") : "";
                        tipo_instalacion = rs.getString("tipo_instalacion") != null ? rs.getString("tipo_instalacion") : "";
                        costo_migracion_inalambrico_tmp = rs.getString("costo_migracion_inalambrico_tmp") != null ? Addons.redondear(  rs.getString("costo_migracion_inalambrico_tmp") ) : "";
                        costo_migracion_fibra_tmp = rs.getString("costo_migracion_fibra_tmp") != null ? Addons.redondear(  rs.getString("costo_migracion_fibra_tmp") ) : "";
                        men_tiempo_de_permanencia_min = rs.getString("men_tiempo_de_permanencia_min") != null ? rs.getInt("men_tiempo_de_permanencia_min") : 0;
                        costo_plan = rs.getString("costo_plan") != null ? Addons.redondear( rs.getString("costo_plan") ) : "0.00";
                        modalidad_pago = rs.getString("txt_modalidad_pago") != null ? rs.getString("txt_modalidad_pago") : "PREPAGO";
                        
                        valor_trabajo = rs.getString("valor_trabajo") != null ? rs.getString("valor_trabajo") : "";
                        nombre_certificado = rs.getString("nombre_certificado") != null ? rs.getString("nombre_certificado") : "";
                        observaciones = rs.getString("observaciones") != null ? rs.getString("observaciones") : "";
                        tipo_bloqueo = rs.getString("txt_tipo_bloqueo") != null ? rs.getString("txt_tipo_bloqueo") : "";
                        modalidad_antigua = rs.getString("txt_convenio_pago_ant") != null ? rs.getString("txt_convenio_pago_ant") : "";
                        modalidad_nueva = rs.getString("txt_modalidad_pago") != null ? rs.getString("txt_modalidad_pago") : "";
                        
                        renovacion_tiempo_permanencia = rs.getString("renovacion_tiempo_permanencia") != null ? rs.getString("renovacion_tiempo_permanencia") : "";
                        renovacion_costo_facturado = rs.getString("renovacion_costo_facturado") != null ? rs.getDouble("renovacion_costo_facturado") : 0;
                        rs.close();
                        
                        
                        ResultSet rs1 = objInstalacion.consulta("select burst_limit::numeric / 1000 as velocidad_bajada,\n" +
                                "    case when sim_subida > 0 then (burst_limit::numeric / 1000) / sim_subida\n" +
                                "    	else (burst_limit::numeric / 1000) * sim_subida end \n" +
                                "    as velocidad_subida,\n" +
                                "    burst_limit::numeric / 1000 / comparticion as min_velocidad_bajada,\n" +
                                "    case when sim_subida > 0 then (burst_limit::numeric / 1000 / comparticion) / sim_subida\n" +
                                "    	else (burst_limit::numeric / 1000 / comparticion) * sim_subida end \n" +
                                "    as min_velocidad_subida, comparticion::int || ':1' as comparticion "
                                + "from vta_plan_servicio where id_plan_servicio=" + id_plan_nuevo);
                        if(rs1.next()){
                            velocidad_bajada = rs1.getString("velocidad_bajada") != null ? Addons.redondear( rs1.getString("velocidad_bajada") ) : "";
                            velocidad_subida = rs1.getString("velocidad_subida") != null ? Addons.redondear( rs1.getString("velocidad_subida") ) : "";
                            min_velocidad_bajada = rs1.getString("min_velocidad_bajada") != null ? Addons.redondear( rs1.getString("min_velocidad_bajada") ) : "";
                            min_velocidad_subida = rs1.getString("min_velocidad_subida") != null ? Addons.redondear( rs1.getString("min_velocidad_subida") ) : "";
                            comparticion = rs1.getString("comparticion") != null ? rs1.getString("comparticion") : "";
                            rs1.close();
                        }
                        
                        ResultSet rs2 = objInstalacion.consulta("select * from vta_cliente where id_cliente=" + idCliente);
                        if(rs2.next()){
                            parroquia_cliente = rs2.getString("parroquia") != null ? rs2.getString("parroquia") : "";
                            ciudad_cliente = rs2.getString("ciudad") != null ? rs2.getString("ciudad") : "";
                            parroquia_cliente = rs2.getString("parroquia") != null ? rs2.getString("parroquia") : "";
                            provincia_cliente = rs2.getString("provincia") != null ? rs2.getString("provincia") : "";
                            rs2.close();
                        }
                        
                    }
                    
                    
                    
                    
                    PdfPTable tbl_det = new PdfPTable(1);
                    documento = documento.replaceAll("<<ciudad>", ciudad);
                    documento = documento.replaceAll("<<dia>>", String.valueOf( Fecha.getDia() ) );
                    documento = documento.replaceAll("<<mes_texto>>", Fecha.getTxtMes( Fecha.getMes() ) );
                    documento = documento.replaceAll("<<anio>>", String.valueOf( Fecha.getAnio() ) );
                    documento = documento.replaceAll("<<fecha>>", Fecha.getFechaSolicitud(fecha_actual));
                    documento = documento.replaceAll("<<razon_social>>", razon_social);
                    documento = documento.replaceAll("<<cedula>>", ruc);
                    documento = documento.replaceAll("<<email>>", email);
                    documento = documento.replaceAll("<<telefono>>", telefono);
                    documento = documento.replaceAll("<<direccion_cliente>>", direccion_del_cliente);
                    documento = documento.replaceAll("<<parroquia>>", parroquia_cliente);
                    documento = documento.replaceAll("<<canton>>", ciudad_cliente);
                    documento = documento.replaceAll("<<provincia>>", provincia_cliente);
                    
                    documento = documento.replaceAll("<<estado_servicio>>", txt_estado_servicio);
                    documento = documento.replaceAll("<<costo_plan>>", costo);
                    documento = documento.replaceAll("<<ip>>", ip);
                    documento = documento.replaceAll("<<plan>>", plan);
                    documento = documento.replaceAll("<<plan_nuevo>>", plan_nuevo);
                    
                    plan_nuevo = plan_nuevo.toUpperCase();
                    documento = documento.replaceAll("<<marca_residencial>>", (plan_nuevo.contains("RESIDENCIAL") ? "   X" : "" ) );
                    documento = documento.replaceAll("<<marca_small>>", (plan_nuevo.contains("SMALL") ? "   X" : "" ) );
                    documento = documento.replaceAll("<<marca_corporativo>>", (plan_nuevo.contains("CORPORATIVO") ? "   X" : "" ) );
                    documento = documento.replaceAll("<<marca_plan_otros>>", (!plan_nuevo.contains("RESIDENCIAL") && !plan_nuevo.contains("SMALL") && !plan_nuevo.contains("CORPORATIVO") ? "   X" : "" ) );
                    documento = documento.replaceAll("<<velocidad_bajada>>", velocidad_bajada);
                    documento = documento.replaceAll("<<velocidad_subida>>", velocidad_subida);
                    documento = documento.replaceAll("<<min_velocidad_bajada>>", min_velocidad_bajada);
                    documento = documento.replaceAll("<<min_velocidad_subida>>", min_velocidad_subida);
                    documento = documento.replaceAll("<<comparticion>>", comparticion);
                    
                    documento = documento.replaceAll("<<permanencia_min>>", (men_tiempo_de_permanencia_min > 0 ? "SI" : "NO") );
                    documento = documento.replaceAll("<<tiempo_permanencia>>", String.valueOf(men_tiempo_de_permanencia_min) );
                    
                    if( ( (migracion_fibra.trim().compareTo("t") == 0 || migracion_fibra.trim().compareTo("") == 0)
                                && (id_certificados_isp.trim().compareTo("4") == 0 ) || id_certificados_isp.trim().compareTo("5") == 0) ){
                        if(tipo_instalacion.compareTo("f")==0 || tipo_instalacion.compareTo("g")==0) {
                            documento = documento.replaceAll("<<valor_permanencia_min>>", costo_migracion_fibra_tmp);
                            documento = documento.replaceAll("<<costo_migracion>>", costo_migracion_fibra_tmp);
                        } else {
                            documento = documento.replaceAll("<<valor_permanencia_min>>", costo_migracion_inalambrico_tmp);
                            documento = documento.replaceAll("<<costo_migracion>>", costo_migracion_inalambrico_tmp);
                        }
                    }
                    if(id_certificados_isp.trim().compareTo("11") == 0 ){
                        documento = documento.replaceAll("<<renovacion_tiempo_permanencia>>", renovacion_tiempo_permanencia);
                        documento = documento.replaceAll("<<incluye_renovacion_permanencia>>", (renovacion_costo_facturado>0 ? "Si" : "No") );
                        documento = documento.replaceAll("<<renovar_contrato_costo>>", String.valueOf(renovacion_costo_facturado) );
                    }
                    
                    documento = documento.replaceAll("<<costo_plan>>", costo_plan);
                    documento = documento.replaceAll("<<modalidad_pago>>", modalidad_pago);
                    
                    documento = documento.replaceAll("<<valor_trabajo>>", valor_trabajo);
                    documento = documento.replaceAll("<<tipo_trabajo>>", nombre_certificado);
                    documento = documento.replaceAll("<<observaciones>>", direccion + " " + observaciones);
                    documento = documento.replaceAll("<<tipo_bloqueo>>", tipo_bloqueo);
                    documento = documento.replaceAll("<<modalidad_antigua>>", modalidad_antigua);
                    documento = documento.replaceAll("<<modalidad_nueva>>", modalidad_nueva);
                    if ( id_certificados_isp.trim().compareTo("4") != 0 && id_certificados_isp.trim().compareTo("6") != 0 && id_certificados_isp.trim().compareTo("11") != 0 ){
                        tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                        tbl_det.addCell(Addons.setCeldaPDF("" + nombre_certificado.toUpperCase(), Font.HELVETICA, 13, Font.BOLD, Element.ALIGN_CENTER, 0));
                        tbl_det.addCell(Addons.setCeldaPDF("", Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    }
                    tbl_det.addCell(Addons.setCeldaPDF(documento, Font.HELVETICA, 11, Font.NORMAL, Element.ALIGN_JUSTIFIED, 0));
                    document.add(tbl_det);
//                    document.add(new Paragraph(" ")); 
//                    document.add(new Paragraph(" "));
                    if (id_certificados_isp.trim().compareTo("2") == 0 || id_certificados_isp.trim().compareTo("3") == 0 || id_certificados_isp.trim().compareTo("4") == 0 || id_certificados_isp.trim().compareTo("5") == 0 
                            || id_certificados_isp.trim().compareTo("7") == 0 || id_certificados_isp.trim().compareTo("8") == 0 || id_certificados_isp.trim().compareTo("11") == 0) {

                    } else {
                        tbl_det = new PdfPTable(1);
                        Image imagen = null;
                        try {
                            imagen = Image.getInstance(_URL_ANEXOS + "/dir/" + nombre_firma);
                            imagen.scaleAbsolute(220, 180);
                            PdfPCell celdaImg = new PdfPCell(imagen);
                            celdaImg.setBorderWidth(0);
                            celdaImg.setPadding(0);
                            tbl_det.addCell(celdaImg);

                        } catch (Exception e) {
                            tbl_det.addCell("");
                        }
                        document.add(tbl_det);
                    }
                } catch (Exception e) {
                    System.out.println("" + e.getMessage());
                }
                document.close(); // paso 5

            } catch (IllegalStateException ie) {
                ie.printStackTrace();
            } catch (DocumentException de) {
                de.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                objInstalacion.cerrar();
                objOrdenTrabajo.cerrar();
                //archivo.cerrar();
            }

            /* fin PDF */
        }

    }

    
    
    ////////////////////////////certificado
//    public void GenerarTextoAPdf(HttpServletResponse response, String documento, String nombre_firma, String URL_ANEXOS) {
//        TextoAPdf objTextoAPdf = new TextoAPdf();
//        objTextoAPdf.imprimir(response, documento, nombre_firma, URL_ANEXOS);
//    }
//    public void
    
    
}
