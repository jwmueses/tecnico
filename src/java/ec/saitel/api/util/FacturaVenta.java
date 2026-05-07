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

import ec.gob.sri.FirmaXadesBes;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

/**
 *
 * @author Jorge
 */
public class FacturaVenta extends DataBase {

    private String _ip = null;
    private int _puerto = 5432;
    private String _db = null;
    private String _usuario = null;
    private String _clave = null;

    public FacturaVenta(String m, int p, String db, String u, String c) {
        super(m, p, db, u, c);
        _ip = m;
        _puerto = p;
        _db = db;
        _usuario = u;
        _clave = c;
    }

    public ResultSet getFactura(String id) {
        return this.consulta("SELECT * FROM vta_factura_venta_retencion where id_factura_venta=" + id + ";");
    }

    public ResultSet getFacturasFirmadas() {
        return this.consulta("SELECT * FROM vta_factura_venta_all where estado_documento='f' and anulado=false and fecha_emision = '2017-12-04';");
    }

    public ResultSet getFacturaDetalleAggFirmadas(String id, String id_sucursal) {
        return this.consulta("select distinct replace( replace(array_agg(P.id_producto)::varchar, '{', ''), '}', '') as ids_productos,\n"
                + "replace( replace(array_agg(descripcion_mas)::varchar, '{', ''), '}', '') as descripciones, \n"
                + "replace( replace(array_agg(cantidad)::varchar, '{', ''), '}', '') as cantidades_prod,\n"
                + "replace( replace(array_agg(p_u)::varchar, '{', ''), '}', '') as preciosUnitarios,\n"
                + "replace( replace(array_agg(D.descuento)::varchar, '{', ''), '}', '') as descuentos,\n"
                + "replace( replace(array_agg(D.p_st)::varchar, '{', ''), '}', '') as subtotales,\n"
                + "replace( replace(array_agg(D.iva)::varchar, '{', ''), '}', '') as ivas,\n"
                + "replace( replace(array_agg(porcentaje)::varchar, '{', ''), '}', '') as pIvas,\n"
                + "replace( replace(array_agg(I.codigo)::varchar, '{', ''), '}', '')  as codigoIvas\n"
                + "FROM ((vta_producto as P inner join tbl_sucursal_producto as SP on P.id_producto=SP.id_producto) \n"
                + "inner join tbl_iva as I on I.id_iva=SP.id_iva) \n"
                + "inner join tbl_factura_venta_detalle as D on D.id_producto=P.id_producto \n"
                + "where D.id_factura_venta=574814 and SP.id_sucursal=3");
    }

    public long getNumFactura(int id_punto_emision) {
        long num = 0;
        //long num_ini = 0;
        try {
            ResultSet res = this.consulta("SELECT max(num_factura) FROM tbl_factura_venta WHERE id_punto_emision=" + id_punto_emision);
            if (res.next()) {
                num = (res.getString(1) != null) ? res.getInt(1) : 0;
                if (num == 0) {
                    ResultSet res1 = this.consulta("SELECT num_fact_inicial FROM tbl_punto_emision WHERE id_punto_emision=" + id_punto_emision);
                    if (res1.next()) {
                        num = (res1.getString(1) != null) ? res1.getLong(1) : 0;
                        res1.close();
                    }
                }
                res.close();
            }
            /*ResultSet res2 = this.consulta("SELECT fac_sec_desde FROM tbl_punto_emision WHERE id_punto_emision="+id_punto_emision);
            if(res2.next()){
                num_ini = (res2.getString(1)!=null) ? res2.getLong(1) : 0;
                res2.close();
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        num++;
        return num;
    }

    public ResultSet getFacturasPendientes(String idC) {
        return this.consulta("SELECT * FROM tbl_factura_venta where id_cliente=" + idC + " and deuda>0 and anulado=false");
    }

//    public ResultSet getFacturasPendientesInstalacion(String id) {
//        return this.consulta("SELECT * FROM vta_factura_venta_all where id_instalacion=" + id + " and deuda>0 and pago_diferirido_meses>0 and anulado=false");
//    }
    public ResultSet getFacturaPendienteTarjetaCredito(String num_cuenta) {
        return this.consulta("SELECT * FROM vta_factura_venta_all where num_cuenta='" + num_cuenta + "' and deuda>0 and anulado=false");
    }
    
    public ResultSet getFacturaPendienteDebitoAustro(String idFacturaVenta) {
        return this.consulta("SELECT * FROM vta_factura_venta_all where id_factura_venta='" + idFacturaVenta + "' and deuda>0 and anulado=false");
    }

    public boolean deudaPendiente(String idC) {
        boolean ok = false;
        try {
            ResultSet rs = this.consulta("SELECT * FROM tbl_factura_venta where id_cliente=" + idC + " and deuda>0 and anulado=false");
            if (this.getFilas(rs) > 0) {
                ok = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok;
    }
    
    public boolean instalacionSinDeuda(String idIns) {
        boolean ok = false;
        try {
            ResultSet rs = this.consulta("SELECT id_instalacion FROM tbl_prefactura where id_instalacion=" + idIns + " and fecha_emision is null "
                    + "union "
                    + "select id_instalacion from tbl_factura_venta where id_instalacion=" + idIns + " and deuda>0 and anulado=false");
            if (this.getFilas(rs) == 0) {
                ok = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok;
    }

    public ResultSet getNumsFacturasPendientes(String idC) {
        return this.consulta("SELECT id_factura_venta, serie_factura || '-' || num_factura FROM vta_factura_venta_all "
                + "where id_cliente=" + idC + " and id_factura_venta not in(select id_factura_venta from tbl_retencion_venta) and deuda>0 and anulado=false");
    }

    public ResultSet getFacturaDetalle(String id) {
        return this.consulta("select * from vta_factura_venta_detalle where id_factura_venta=" + id);
    }

    public ResultSet getFacturaDetalleEdicion(String id, String id_sucursal) {
        return this.consulta("select distinct P.id_producto, P.codigo, D.descripcion_mas, SP.stock_sucursal+D.cantidad as stock_sucursal, "
                + "P.precio_costo, I.porcentaje as porcentaje_iva, I.codigo as codigo_iva, "
                + "D.id_factura_venta_detalle, D.cantidad, D.p_u, D.p_st, D.descuento, D.iva, D.total, D.id_lista_precio, D.p_descuento, SP.descuento, "
                + "case when tipo='s' then '~' else '' end as de_servicio, round((P.precio_costo + (P.precio_costo * P.utilidad_min / 100)), 4) as costo_minimo, "
                + "round( (D.total-D.p_st+D.descuento)*100/(D.p_st-D.descuento)) as p_iv_aplicado, D.cod_iva, D.p_iva::int "
                + "FROM ((vta_producto as P inner join tbl_sucursal_producto as SP on P.id_producto=SP.id_producto) "
                + "inner join tbl_iva as I on I.id_iva=SP.id_iva) "
                + "inner join tbl_factura_venta_detalle as D on D.id_producto=P.id_producto "
                + "where D.id_factura_venta=" + id + " and SP.id_sucursal=" + id_sucursal);
    }

    public ResultSet getFacturaDetalleActivo(String id) {
        return this.consulta("select * from vta_factura_venta_detalle_activo where id_factura_venta=" + id);
    }

    public ResultSet getFacturaDetalleActivoEdicion(String id) {
        return this.consulta("SELECT id_factura_venta_detalle_activo, A.id_activo, codigo_activo, D.descripcion_mas, valor_compra, valor_depreciado, "
                + "round(valor_compra-valor_depreciado, 2) as precio_costo, case when tiene_iva then '~' else '' end, D.cantidad, "
                + "D.p_u, D.p_st, D.p_descuento, d.descuento, D.iva, D.total, D.p_iva, round( (D.total-D.p_st+D.descuento)*100/(D.p_st-D.descuento)) as p_iv_aplicado, D.cod_iva "
                + "FROM (tbl_activo as A left outer join tbl_bodega_activo as BA on A.id_activo=BA.id_activo) "
                + "inner join tbl_factura_venta_detalle_activo as D on D.id_activo=A.id_activo "
                + "where D.id_factura_venta=" + id);
    }

    public ResultSet getCobro(String idFC) {
        return this.consulta("SELECT * FROM tbl_factura_venta_cobro where id_factura_venta_cobro=" + idFC);
    }

    public ResultSet getCobroComprobante(String idFC) {
        return this.consulta("SELECT VC.*, C.fecha_en_efectivo, C.num_cheque, C.banco, C.num_comp_pago, C.gastos_bancos, C.id_plan_cuenta_banco "
                + "FROM tbl_factura_venta_cobro as VC inner join tbl_comprobante_ingreso as C on VC.id_comprobante_ingreso=C.id_comprobante_ingreso "
                + "where VC.id_factura_venta=" + idFC);
    }

    public String getEMail(String idF) {
        String id = "";
        try {
            ResultSet rs = this.consulta("SELECT email FROM vta_factura_venta WHERE id_factura_venta=" + idF);
            if (rs.next()) {
                id = rs.getString("email") != null ? rs.getString("email") : "";
                rs.close();
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }
    
    public ResultSet getPagos(String idD) {
        return this.consulta("SELECT * FROM vta_factura_venta_pago where id_comprobante_ingreso=" + idD + " and factura_anulada=false and pago_anulado=false");
    }

    public static String[][] getCuentasDistintas(String mat[][]) {
        String distintos[][] = null;
        int i = 0;
        int pos = -1;
        while (mat[i][1] != null) {
            pos = Matriz.enMatriz(distintos, mat[i][8], 1);
            if (pos == -1) {
                distintos = Matriz.poner(mat, new String[]{mat[i][8], "0", mat[i][3]});
            } else {
                distintos[pos][2] = String.valueOf(Double.parseDouble(distintos[pos][2]) + Double.parseDouble(mat[i][3]));
            }
            i++;
        }
        return distintos;
    }

    public String verificarStock(int id_sucursal, String id_productos, String cantidades) {
        ResultSet rs = null;
        String codigo = "";
        int cantidad = 1;
        String vecProd[] = id_productos.split(",");
        String vecCant[] = cantidades.split(",");
        for (int i = 0; i < vecProd.length; i++) {
            try {
                rs = this.consulta("select P.codigo, SP.stock_sucursal - " + vecCant[i] + " "
                        + "from vta_producto as P inner join tbl_sucursal_producto as SP on P.id_producto=SP.id_producto "
                        + "where SP.id_sucursal=" + id_sucursal + " and SP.id_producto=" + vecProd[i]);
                if (rs.next()) {
                    cantidad = rs.getString(2) != null ? rs.getInt(2) : 1;
                    if (cantidad < 0) {
                        codigo = rs.getString("codigo") != null ? rs.getString("codigo") : "";
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return codigo;
    }

    public boolean facturaDuplicada(String serie, String numero) {
        ResultSet res = this.consulta("SELECT * FROM tbl_factura_venta where serie_factura='" + serie + "' and num_factura=" + numero + " and anulado=false");
        if (this.getFilas(res) > 0) {
            return true;
        }
        try {
            res.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String concatenarValores(String id_articulos, String descripciones, String cantidades, String precios_costo, String precios_unitarios,
            String subtotales, String descuentos, String ivas, String totales) {
        String param = "";
        String vecArti[] = id_articulos.split(",");
        String vecCant[] = cantidades.split(",");
        String vecPC[] = precios_costo.split(",");
        String vecPU[] = precios_unitarios.split(",");
        String vecSubt[] = subtotales.split(",");
        String vecDes[] = descuentos.split(",");
        String vecIva[] = ivas.split(",");
        String vecTot[] = totales.split(",");
        String vecDescrip[] = descripciones.split(",");
        for (int i = 0; i < vecArti.length; i++) {
            param += "['" + vecArti[i] + "','" + vecCant[i] + "','" + vecPU[i] + "','" + vecSubt[i] + "','" + vecDes[i] + "','" + vecIva[i] + "','" + vecTot[i] + "','" + vecDescrip[i] + "','" + vecPC[i] + "'],";
        }
        param = param.substring(0, param.length() - 1);
        return "array[" + param + "]";
    }

    public String concatenarValores(String idCliAnts, String montos_vajar) {
        String param = "";
        String idCliAnt[] = idCliAnts.split(",");
        String monto_vajar[] = montos_vajar.split(",");
        for (int i = 0; i < idCliAnt.length; i++) {
            param += "['" + idCliAnt[i] + "','" + monto_vajar[i] + "'],";
        }
        param = param.substring(0, param.length() - 1);
        return "array[" + param + "]";
    }

    public String insertar(int id_sucursal, int id_punto_emision, String id_cliente, String vendedor, String serie_factura, String num_factura, String autorizacion,
            String ruc, String razon_social, String fecha_emision, String direccion, String telefono, String id_forma_pago, String forma_pago, String banco,
            String num_cheque, String num_comp_pago, String gastos_bancos, String id_plan_cuenta_banco, String son, String observacion,
            String subtotal, String subtotal_0, String subtotal_2, String subtotal_3, String descuento, String iva_2, String iva_3, String total, String paramArtic,
            String ret_num_serie, String ret_num_retencion, String ret_autorizacion, String ret_fecha_emision, String ret_ejercicio_fiscal_mes,
            String ret_ejercicio_fiscal, String ret_impuesto_retenido, String paramRet, String paramAsiento, String xmlFirmado,
            String id_plan_cuenta_anticipo, String idCliAnt, String monto_vajar, String nombreBoucher) {
        String num = "-1";
        try {
            ret_fecha_emision = ret_fecha_emision.compareTo("") != 0 ? "'" + ret_fecha_emision + "'" : "NULL";
            String paramMontosVajar = this.concatenarValores(idCliAnt, monto_vajar);
            ResultSet res = this.consulta("select facturaVenta(" + id_sucursal + ", " + id_punto_emision + ", " + id_cliente + ", '" + vendedor + "', '" + serie_factura
                    + "', " + num_factura + ", '" + autorizacion + "', '" + ruc + "', '" + razon_social + "', '" + fecha_emision + "', '" + direccion
                    + "', '" + telefono + "', '" + id_forma_pago + "', '" + forma_pago + "', '" + banco + "', '" + num_cheque + "', '" + num_comp_pago + "', " + gastos_bancos
                    + ", " + id_plan_cuenta_banco + ", '" + son + "', '" + observacion + "', " + subtotal + ", " + subtotal_0 + ", " + subtotal_2 + ", " + subtotal_3 + ", " + descuento
                    + ", " + iva_2 + ", " + iva_3 + ", " + total + ", " + paramArtic + ", '" + ret_num_serie + "', '" + ret_num_retencion + "', '" + ret_autorizacion + "', " + ret_fecha_emision
                    + ", '" + ret_ejercicio_fiscal_mes + "', " + ret_ejercicio_fiscal + ", " + ret_impuesto_retenido + ", " + paramRet + ", " + paramAsiento + ", '" + xmlFirmado
                    + "', " + id_plan_cuenta_anticipo + ", " + paramMontosVajar + ", '"+nombreBoucher+"');"); // 43 param
            if (res.next()) {
                num = (res.getString(1) != null) ? res.getString(1) : "-1";
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    public boolean actualizar(String id, int id_sucursal, int id_punto_emision, String id_cliente, String vendedor, String serie_factura, String num_factura, String autorizacion,
            String ruc, String razon_social, String fecha_emision, String direccion, String telefono, String forma_pago, String banco,
            String num_cheque, String num_comp_pago, String gastos_bancos, String id_plan_cuenta_banco, String son, String observacion,
            String subtotal, String subtotal_0, String subtotal_2, String subtotal_3, String descuento, String iva_2, String iva_3, String total, String paramArtic,
            String ret_num_serie, String ret_num_retencion, String ret_autorizacion, String ret_fecha_emision, String ret_ejercicio_fiscal_mes,
            String ret_ejercicio_fiscal, String ret_impuesto_retenido, String paramRet, String paramAsiento, String xmlFirmado) {
        boolean ok = false;
        try {
            ret_fecha_emision = ret_fecha_emision.compareTo("") != 0 ? "'" + ret_fecha_emision + "'" : "NULL";
            ResultSet res = this.consulta("select facturaVentaActualizar(" + id + ", " + id_sucursal + ", " + id_punto_emision + ", " + id_cliente + ", '" + vendedor + "', '" + serie_factura
                    + "', " + num_factura + ", '" + autorizacion + "', '" + ruc + "', '" + razon_social + "', '" + fecha_emision + "', '" + direccion
                    + "', '" + telefono + "', '" + forma_pago + "', '" + banco + "', '" + num_cheque + "', '" + num_comp_pago + "', " + gastos_bancos
                    + ", " + id_plan_cuenta_banco + ", '" + son + "', '" + observacion + "', " + subtotal + ", " + subtotal_0 + ", " + subtotal_2 + ", " + subtotal_3 + ", " + descuento
                    + ", " + iva_2 + ", " + iva_3 + ", " + total + ", " + paramArtic + ", '" + ret_num_serie + "', '" + ret_num_retencion + "', '" + ret_autorizacion + "', " + ret_fecha_emision
                    + ", '" + ret_ejercicio_fiscal_mes + "', " + ret_ejercicio_fiscal + ", " + ret_impuesto_retenido + ", " + paramRet + ", " + paramAsiento + ", '" + xmlFirmado + "');");
            if (res.next()) {
                ok = (res.getString(1) != null) ? res.getBoolean(1) : false;
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok;
    }

    
    public boolean conciliarBoucher(String id, String idCompIngr, int idSucursal, String usuario, String fechaEfectivo, String num_doc_banco, String monto)
    {
        List sql = new ArrayList();
        String param = this.concatenarValores(idCompIngr, fechaEfectivo, monto);
        
        sql.add("update tbl_comprobante_ingreso set documento_rechazado=num_comp_pago where id_comprobante_ingreso=" + idCompIngr);
        sql.add("update tbl_comprobante_ingreso set num_comp_pago='" + num_doc_banco + "', documento_rechazado_motivo=null where id_comprobante_ingreso=" + idCompIngr);
        sql.add("update tbl_factura_venta set conciliado_boucher=true, usuario_concilia_boucher='"+usuario+"' where id_factura_venta=" + id);
        sql.add("select proc_comprobanteIngresoEfectivizar(" + idSucursal + ", " + param + ");");
        return this.transacciones(sql);
    }
    
    public boolean contabilizar(String usuario) {
        boolean ok = false;
        try {
            ResultSet res = this.consulta("select contabilizarVentas('" + usuario + "')");
            if (res.next()) {
                ok = (res.getString(1) != null) ? res.getBoolean(1) : false;
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok;
    }

    public boolean contabilizarFactura(String idFactura) {
        boolean ok = false;
        try {
            ResultSet res = this.consulta("select contabilizarFactura(" + idFactura + ")");
            if (res.next()) {
                ok = (res.getString(1) != null) ? res.getBoolean(1) : false;
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok;
    }

//    public boolean estaContabilizado(String usuario) {
//        boolean ok = false;
//        try {
//            ResultSet res = this.consulta("select count(*) from tbl_factura_venta where contabilizado=false and vendedor='" + usuario + "'");
//            if (res.next()) {
//                int count = res.getString(1) != null ? res.getInt(1) : 0;
//                if (count == 0) {
//                    ok = true;
//                }
//                res.close();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return ok;
//    }
    
    public double getTotalFactsNoContabilizadas(String usuario) {
        double total = 0;
        try {
            ResultSet res = this.consulta("select sum(total) from tbl_factura_venta where contabilizado=false and anulado=false and vendedor='" + usuario + "'");
            if (res.next()) {
                total = res.getString(1) != null ? res.getDouble(1) : 0;
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }

    public boolean procesarXmlSri(String DOCS_ELECTRONICOS, String claveAcceso) {
        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, this._usuario, this._clave);
        String claveCertificado = conf.getValor("clave_certificado");
        //String rutaArchivo = DOCS_ELECTRONICOS + "generados/";
        try {
            ResultSet rs = this.consulta("select id_factura_venta, documento_xml from tbl_factura_venta where estado_documento='p' and clave_acceso='" + claveAcceso + "'");
            if (rs.next()) {
                String id_factura_venta = rs.getString("id_factura_venta") != null ? rs.getString("id_factura_venta") : "";
                String cadenaXml = rs.getString("documento_xml") != null ? rs.getString("documento_xml") : "";
                //if(this.guardarXml(rutaArchivo + claveAcceso + ".xml", cadenaXml)){
                return this.firmarXml(id_factura_venta, DOCS_ELECTRONICOS, claveAcceso, cadenaXml, claveCertificado);
                //}
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean procesarXmlSri(String DOCS_ELECTRONICOS, String claveAcceso, Archivo archivo) {
        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, this._usuario, this._clave);
        String claveCertificado = conf.getValor("clave_certificado");
        //String rutaArchivo = DOCS_ELECTRONICOS + "generados/";
        try {
            ResultSet rs = this.consulta("select id_factura_venta, documento_xml,num_factura,id_sucursal from tbl_factura_venta where estado_documento='p' and clave_acceso='" + claveAcceso + "'");
            if (rs.next()) {
                String id_factura_venta = rs.getString("id_factura_venta") != null ? rs.getString("id_factura_venta") : "";
                String cadenaXml = rs.getString("documento_xml") != null ? rs.getString("documento_xml") : "";
                String num_factura = rs.getString("num_factura") != null ? rs.getString("num_factura") : "";
                String id_sucursal = rs.getString("id_sucursal") != null ? rs.getString("id_sucursal") : "";
                //if(this.guardarXml(rutaArchivo + claveAcceso + ".xml", cadenaXml)){
                return this.firmarXml(id_factura_venta, DOCS_ELECTRONICOS, claveAcceso, cadenaXml, claveCertificado, archivo, num_factura, id_sucursal);
                //}
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            archivo.cerrar();
        }
        return false;
    }

    public boolean procesarXmlSriTodos(String DOCS_ELECTRONICOS) {
        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, this._usuario, this._clave);
        String claveCertificado = conf.getValor("clave_certificado");
        //String rutaArchivo = DOCS_ELECTRONICOS + "generados/";
        try {
            ResultSet rs = this.consulta("select id_factura_venta, clave_acceso, documento_xml from tbl_factura_venta where estado_documento='p' order by fecha_emision");
            while (rs.next()) {
                String id_factura_venta = rs.getString("id_factura_venta") != null ? rs.getString("id_factura_venta") : "";
                String claveAcceso = rs.getString("clave_acceso") != null ? rs.getString("clave_acceso") : "";
                String cadenaXml = rs.getString("documento_xml") != null ? rs.getString("documento_xml") : "";
                //this.guardarXml(rutaArchivo + claveAcceso + ".xml", cadenaXml);
                this.firmarXml(id_factura_venta, DOCS_ELECTRONICOS, claveAcceso, cadenaXml, claveCertificado);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean guardarXml(String rutaArchivo, String cadenaXml) {
        try {
            FileWriter fw = new FileWriter(rutaArchivo);
            PrintWriter writer = new PrintWriter(fw);
            writer.print(cadenaXml);
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean firmarXml(String idFactura, String DOCS_ELECTRONICOS, String claveAcceso, String cadenaXml, String claveCertificado) {
        String certificado = DOCS_ELECTRONICOS + "certificado.p12";
        String rutaSalida = DOCS_ELECTRONICOS + "firmados";
        String rutaXml = DOCS_ELECTRONICOS + "generados/" + claveAcceso + ".xml";
        if (this.guardarXml(rutaXml, cadenaXml)) {
            String estadoDocumento = "g";
            String archivoSalida = claveAcceso + ".xml";
            FirmaXadesBes firmaDigital = new FirmaXadesBes(certificado, claveCertificado, rutaXml, rutaSalida, archivoSalida);
            firmaDigital.execute();

            if (firmaDigital.getError().compareTo("") == 0) {
                try {
                    estadoDocumento = "f";
                    String autorizacionXml = this.getStringFromFile(DOCS_ELECTRONICOS + "firmados/" + claveAcceso + ".xml");
                    return this.ejecutar("update tbl_factura_venta set estado_documento='" + estadoDocumento + "', documento_xml='" + autorizacionXml + "' where id_factura_venta=" + idFactura);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private boolean firmarXml(String idFactura, String DOCS_ELECTRONICOS, String claveAcceso, String cadenaXml, String claveCertificado, Archivo archivo, String num_factura, String id_sucursal) {
        String certificado = DOCS_ELECTRONICOS + "certificado.p12";
        String rutaSalida = DOCS_ELECTRONICOS + "firmados";
        String rutaXml = DOCS_ELECTRONICOS + "generados/" + claveAcceso + ".xml";
        try {
            if (this.guardarXml(rutaXml, cadenaXml)) {
                String estadoDocumento = "g";
                String archivoSalida = claveAcceso + ".xml";
                FirmaXadesBes firmaDigital = new FirmaXadesBes(certificado, claveCertificado, rutaXml, rutaSalida, archivoSalida);
                firmaDigital.execute();

                if (firmaDigital.getError().compareTo("") == 0) {
                    try {
                        estadoDocumento = "f";
                        String autorizacionXml = this.getStringFromFile(DOCS_ELECTRONICOS + "firmados/" + claveAcceso + ".xml");
                        archivo.setArchivoDocumentalTexto(autorizacionXml, num_factura, "" + id_sucursal, "tbl_factura_venta", idFactura, "documentoxml", "public", "db_isp");
                        return this.ejecutar("update tbl_factura_venta set estado_documento='" + estadoDocumento + "', documento_xml='" + autorizacionXml + "' where id_factura_venta=" + idFactura);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        } finally {
            archivo.cerrar();
        }
        return false;
    }

    /* miselanea de funsiones */
    private String getStringFromFile(String archivo) throws IOException {
        File file = new File(archivo);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        StringBuilder cadXml = new StringBuilder();
        //String cadXml = "";
        String linea;
        while ((linea = br.readLine()) != null) {
            cadXml.append(linea);
            //cadXml += linea;
        }
        return cadXml.toString();
    }

    public boolean setEstadoDocumento(String id_factura_venta, String num_factura, String estado, String claveAcceso, String autorizacionXml, String mensaje) {
        return this.ejecutar("update tbl_factura_venta set num_factura=" + num_factura + ", estado_documento='" + estado
                + "', clave_acceso='" + claveAcceso + "', documento_xml='" + autorizacionXml + "', mensaje='"
                + mensaje.replace("|", ".").replace("\n", " ").replace("\r", " ") + "' where id_factura_venta=" + id_factura_venta);
    }

    public boolean setEstadoDocumento(String id_factura_venta, String num_factura, String estado, String autorizacionXml, String mensaje) {
        return this.ejecutar("update tbl_factura_venta set num_factura=" + num_factura + ", estado_documento='" + estado
                + "', documento_xml='" + autorizacionXml + "', mensaje='" + mensaje.replace("|", ".").replace("\n", " ").replace("\r", " ")
                + "' where id_factura_venta=" + id_factura_venta);
    }

    public boolean setEstadoDocumento(String id_factura_venta, String estado, String autorizacionXml, String mensaje) {
        return this.ejecutar("update tbl_factura_venta set estado_documento='" + estado + "', documento_xml='" + autorizacionXml
                + "', mensaje='" + mensaje.replace("|", ".").replace("\n", " ").replace("\r", " ") + "' where id_factura_venta=" + id_factura_venta);
    }

    public boolean setClaveDocumento(String id_factura_venta, String estado, String claveAcceso) {
        return this.ejecutar("update tbl_factura_venta set estado_documento='" + estado + "', clave_acceso='" + claveAcceso
                + "' where id_factura_venta=" + id_factura_venta);
    }

    public boolean setClaveDocumento(String id_factura_venta, String estado, String claveAcceso, String autorizacionXml, String num_factura) {
        return this.ejecutar("update tbl_factura_venta set num_factura=" + num_factura + ", estado_documento='" + estado + "', clave_acceso='" + claveAcceso
                + "', documento_xml='" + autorizacionXml + "' where id_factura_venta=" + id_factura_venta);
    }

    public boolean setDocumentoElectronico(String id_factura_venta, String estado, String claveAcceso, String autorizacionXml, String mensaje) {
        return this.ejecutar("update tbl_factura_venta set estado_documento='" + estado + "', clave_acceso='" + claveAcceso
                + "', documento_xml='" + autorizacionXml + "', mensaje='" + mensaje.replace("|", ".").replace("\n", " ").replace("\r", " ")
                + "' where id_factura_venta=" + id_factura_venta);
    }
    
    public boolean setDocumentoElectronico(String id_factura_venta, String estado, String claveAcceso, String autorizacionXml, String mensaje, String idBanco) {
        return this.ejecutar("update tbl_factura_venta set estado_documento='" + estado + "', clave_acceso='" + claveAcceso
                + "', documento_xml='" + autorizacionXml + "', mensaje='" + mensaje.replace("|", ".").replace("\n", " ").replace("\r", " ")
                + "', id_banco="+idBanco+" where id_factura_venta=" + id_factura_venta);
    }

    public boolean fechaAnulacion(String idFactura) {
        boolean ok = false;
        try {
            ResultSet res = this.consulta("select proc_fechaAnulacion(" + idFactura + ");");
            if (res.next()) {
                ok = (res.getString(1) != null) ? res.getBoolean(1) : false;
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok;
    }

    public boolean claveDuplicada(String clave_acceso) {
        try {
            ResultSet res = this.consulta("SELECT * FROM tbl_factura_venta where clave_acceso='" + clave_acceso + "';");
            if (this.getFilas(res) > 0) {
                return true;
            }
            res.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getClaveAcceso(String id_factura_venta) {
        String num = "0";
        try {
            ResultSet res = this.consulta("SELECT clave_acceso FROM tbl_factura_venta WHERE id_factura_venta=" + id_factura_venta);
            if (res.next()) {
                num = (res.getString(1) != null) ? res.getString(1) : "0";
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    public boolean setClaveAcceso(String id_factura_venta, String claveAcceso) {
        return this.ejecutar("update tbl_factura_venta set clave_acceso='" + claveAcceso + "' where id_factura_venta=" + id_factura_venta);
    }

    public boolean anular(String idFactura) {
        boolean ok = false;
        try {
            ResultSet res = this.consulta("select proc_anularFacturaVenta(" + idFactura + ");");
            if (res.next()) {
                ok = (res.getString(1) != null) ? res.getBoolean(1) : false;
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok;
    }

//    public boolean anular(String idFactura, String por_edicion) {
//        boolean ok = false;
//        try {
//            ResultSet res = this.consulta("select proc_anularFacturaVenta(" + idFactura + ", " + por_edicion + ");");
//            if (res.next()) {
//                ok = (res.getString(1) != null) ? res.getBoolean(1) : false;
//                res.close();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return ok;
//    }

    public boolean getDeActivo(String id_factura_venta) {
        boolean de_activo = false;
        try {
            ResultSet res = this.consulta("SELECT de_activo FROM tbl_factura_venta WHERE id_factura_venta=" + id_factura_venta);
            if (res.next()) {
                de_activo = (res.getString(1) != null) ? res.getBoolean(1) : false;
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return de_activo;
    }

    /*  ANTICIPOS  */
    public boolean registrarAnticipo(String id_cliente, String id_factura_venta, String usuario, String monto, String saldo) {
        return this.ejecutar("insert into tbl_cliente_anticipo(id_cliente, id_factura_venta, alias, monto, saldo) "
                + "values(" + id_cliente + ", " + id_factura_venta + ", '" + usuario + "', " + monto + ", " + saldo + ")");
    }

//    public ResultSet getAnticipo(String id_cliente) {
//        return this.consulta("select * from tbl_cliente_anticipo where saldo>0 and id_cliente=" + id_cliente);
//    }
    
    public ResultSet getAnticipos(String id_cliente) {
        return this.consulta("select * from vta_cliente_anticipo where saldo>0 and id_cliente=" + id_cliente);
    }

    public ResultSet getAnticiposLiquidados(String id_factura) {
        return this.consulta("select num_comprobante, monto, monto_pago from vta_cliente_anticipo as A inner join tbl_cliente_anticipo_liquidado as AL on AL.id_cliente_anticipo=A.id_cliente_anticipo where AL.id_factura_venta=" + id_factura);
    }

    /*public boolean setLiquidacionSaldoAnticipo(String id_sucursal, String id_cliente_anticipo, String id_factura_venta, String saldo)
    {
        boolean ok = false;
        if(Float.parseFloat(saldo)>0){
            //List sql = new ArrayList();
            //sql.add("update tbl_cliente_anticipo set saldo=saldo-"+saldo+" where id_cliente_anticipo="+id_cliente_anticipo);
            //sql.add("insert into tbl_cliente_anticipo_consumo(id_cliente_anticipo, id_factura_venta, monto) values("+id_cliente_anticipo+", "+id_factura_venta+", "+saldo+");");
            //return this.transacciones(sql);
            try{
                ResultSet rs = this.consulta("select proc_liquidar_anticipo("+id_sucursal+", "+id_cliente_anticipo+", "+id_factura_venta+", "+saldo+");");
                if(rs.next()){
                    long id_comprobante_diario = rs.getString(1)!=null ? rs.getLong(1) : -1;
                    if(id_comprobante_diario != -1){
                        ok=true;
                    }
                    rs.close();
                }
            }catch(Exception e){
                this.setError(e.getMessage());
            }
        }
        return ok;
    }*/
 /*public double[] getAnticiposSaldos(String id_cliente)
    {
        double valores[] = new double[]{0,0,0};
        try{
            ResultSet rs = this.consulta("select sum(subtotal) as subtotal, sum(iva) as iva, sum(total) as total "
                    + "from tbl_abono where id_prefactura="+id_prefactura);
            if(rs.next()){
                valores[0] = rs.getString("subtotal")!=null ? rs.getDouble("subtotal") : 0;
                valores[1] = rs.getString("iva")!=null ? rs.getDouble("iva") : 0;
                valores[2] = rs.getString("total")!=null ? rs.getDouble("total") : 0;
                rs.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return valores;
    }*/

 /* RETENCIONES  */
    public ResultSet getRetencion(String id_factura) {
        return this.consulta("select * from tbl_retencion_venta where anulado=false and id_factura_venta=" + id_factura + ";");
    }

    public ResultSet getRetenciones(int id_punto_emision, String desde, String hora) {
        return this.consulta("SELECT R.*, C.tipo_pago FROM tbl_retencion_venta as R inner join tbl_factura_venta_cobro as C on C.id_factura_venta=R.id_factura_venta "
                + "where R.id_punto_emision=" + id_punto_emision + " and (fecha || ' ' || hora)::timestamp >= '" + desde + " " + hora + "'::timestamp and C.anulado=false and R.anulado=false and tipo_pago='e'");
    }

    public ResultSet getFacturaRetencion(String id_retencion) {
        return this.consulta("select * from vta_factura_venta_retencion where id_retencion_venta=" + id_retencion + ";");
    }

    public ResultSet getRetencionDetalle(String id_retencion) {
        return this.consulta("select TR.*, R.id_retencion_venta_detalle, R.id_retencion_venta, R.base_imponible, R.valor_retenido, BI.campo_valor "
                + "from (tbl_retencion_venta_detalle as R inner join tbl_tabla_impuesto as TR on TR.id_tabla_impuesto=R.id_tabla_impuesto) "
                + "inner join tbl_retencion_base_imponible as BI on BI.codigo=R.codigo "
                + "where R.id_retencion_venta=" + id_retencion + ";");
    }

    public String concatenarValores(String id_retenciones, String bases_imponibles, String valores_retenidos) {
        String param = "";
        String vecRet[] = id_retenciones.split(",");
        String vecBI[] = bases_imponibles.split(",");
        String vecVal[] = valores_retenidos.split(",");
        for (int i = 0; i < vecRet.length; i++) {
            param += "['" + vecRet[i] + "','" + vecBI[i] + "','" + vecVal[i] + "'],";
        }
        param = param.substring(0, param.length() - 1);
        return "array[" + param + "]";
    }

    public boolean insertarRetencion(String id_factura, int id_sucursal, int id_punto_emision, String ret_num_serie, String ret_num_retencion, String ret_autorizacion, String ret_fecha_emision,
            String ret_ejercicio_fiscal, String ret_ejercicio_fiscal_mes, String ret_impuesto_retenido, String id_retenciones, String bases_imponibles, String valores_retenidos) {
        boolean ok = false;
        try {
            String paramRetencion = this.concatenarValores(id_retenciones, bases_imponibles, valores_retenidos);
            ResultSet res = this.consulta("select proc_retencionVenta(" + id_factura + ", " + id_sucursal + ", " + id_punto_emision + ", '"
                    + ret_num_serie + "', '" + ret_num_retencion + "', '" + ret_autorizacion + "', '" + ret_fecha_emision + "', "
                    + ret_ejercicio_fiscal + ", '"+ret_ejercicio_fiscal_mes+"', " + ret_impuesto_retenido + ", " + paramRetencion + ");");  // 11 param
            if (res.next()) {
                ok = (res.getString(1) != null) ? res.getBoolean(1) : false;
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok;
    }

    public String getFechaPlazoRetenciones(String fecha_emision, String dia_cortes_creditos) {
        int anio = Fecha.datePart("anio", fecha_emision);
        int mes = Fecha.datePart("mes", fecha_emision);
        int dia = Fecha.datePart("dia", fecha_emision);
        int dia_mes = Fecha.getUltimoDiaMes(anio, mes);
        if (dia > (dia_mes - 7)) {
            mes++;
            if (mes > 12) {
                mes = 1;
                anio++;
            }
            dia_mes = Integer.parseInt(dia_cortes_creditos);
            if (dia_mes == 32) {
                dia_mes = Fecha.getUltimoDiaMes(anio, mes);
            }
        }
        return anio + "-" + mes + "-" + dia_mes;
    }

    /*
    public boolean actualizarRetencion(String id_retencion_venta, String id_factura, String ret_num_serie, String ret_num_retencion, String ret_autorizacion,
            String ret_fecha_emision, String ret_ejercicio_fiscal, String ret_impuesto_retenido, String id_retenciones, String bases_imponibles, String valores_retenidos)
    {
        boolean ok = false;
        try{
            String paramRetencion = this.concatenarValores(id_retenciones, bases_imponibles, valores_retenidos);
            ResultSet res = this.consulta("select proc_editarRetencionVenta("+id_retencion_venta+", "+id_factura+", '"+ret_num_serie+"', '"+ret_num_retencion+
                "', '"+ret_autorizacion+"', '"+ret_fecha_emision+"', "+ret_ejercicio_fiscal+", "+ret_impuesto_retenido+", "+paramRetencion+");");
            if(res.next()){
                ok = (res.getString(1)!=null) ? res.getBoolean(1) : false;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ok;
    }*/
    public boolean anularRetencion(String id_retencion) {
        boolean ok = false;
        try {
            ResultSet res = this.consulta("select proc_anularRetencionRecibida(" + id_retencion + ");");
            if (res.next()) {
                ok = (res.getString(1) != null) ? res.getBoolean(1) : false;
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok;
    }

    public boolean conciliar(String ids) {
        return this.ejecutar("update tbl_factura_venta set conciliado=true, conciliado_fecha=now() where id_factura_venta in(" + ids + ")");
    }

    public String NotificacionPrefactura(String id_instalacion) {
        String msg = "";
        try {
            ResultSet rs = this.consulta("select distinct * from vta_prefactura_notificaciones where id_instalacion='" + id_instalacion + "';");
            if (rs.next()) {
                msg = (rs.getString("comentario") != null ? rs.getString("comentario") : "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msg;
    }

    public String[][] getPuntosEmisionVirtuales() {
        ResultSet rs = this.consulta("SELECT P.id_sucursal, P.id_punto_emision, usuario_caja, fac_num_serie, "
                + "case when max(num_factura)>0 then max(num_factura)+1 else 1 end, direccion_establecimiento "
                + "from tbl_punto_emision as P inner join tbl_factura_venta as F on F.serie_factura=P.fac_num_serie "
                + "where caja_virtual=true "
                + "group by P.id_sucursal, P.id_punto_emision, usuario_caja, fac_num_serie, direccion_establecimiento "
                + "order by id_sucursal");
        return Matriz.ResultSetAMatriz(rs);
    }

    public String insertar(String id_instalacion, String id_sucursal, String id_punto_emision, String id_cliente, String usuario, String serie_factura, String num_factura, String autorizacion,
            String ruc, String razon_social, String fecha_emision, String direccion, String telefono, String id_forma_pago, String forma_pago, String banco, String num_cheque, String num_comp_pago, String gastos_bancos,
            String id_plan_cuenta_banco, String son, String observacion, String subtotal, String subtotal_0, String subtotal_2, String subtotal_3, String descuento, String iva_2, String iva_3, String total, String paramArtic, String ret_num_serie,
            String ret_num_retencion, String ret_autorizacion, String ret_fecha_emision, String ret_ejercicio_fiscal_mes, String ret_ejercicio_fiscal, String ret_impuesto_retenido, String paramRet, String paramAsiento, String xmlFirmado,
            String estadoDocumento, String claveAcceso, String autorizacionXml, String respuestaAutoriz) {
        String ins_fac = "-1;-1";
        Connection con = this.getConexion();
        try {
            String id_factura_venta = "-1";
            con.setAutoCommit(false);
            Statement st = con.createStatement();
            boolean ok = true;
            double total_final1 = Double.parseDouble(total);
            String es_instalacion = "TRUE";
            String ip = "NULL";
            if (es_instalacion.compareTo("TRUE") == 0 && total_final1 > 0) {
                ret_fecha_emision = ret_fecha_emision.compareTo("") != 0 ? "'" + ret_fecha_emision + "'" : "NULL";
                String sql = "select facturaVenta(" + id_sucursal + ", " + id_punto_emision + ", " + id_cliente + ", '" + usuario + "', '" + serie_factura
                        + "', " + num_factura + ", '" + autorizacion + "', '" + ruc + "', '" + razon_social + "', '" + fecha_emision + "', '" + direccion
                        + "', '" + telefono + "', '" + id_forma_pago + "', '" + forma_pago + "', '" + banco + "', '" + num_cheque + "', '" + num_comp_pago + "', " + gastos_bancos
                        + ", " + id_plan_cuenta_banco + ", '" + son + "', '" + observacion + "', " + subtotal + ", " + subtotal_0 + ", " + subtotal_2 + ", " + subtotal_3 + ", " + descuento
                        + ", " + iva_2 + ", " + iva_3 + ", " + total + ", " + paramArtic + ", '" + ret_num_serie + "', '" + ret_num_retencion + "', '" + ret_autorizacion + "', " + ret_fecha_emision
                        + ", '" + ret_ejercicio_fiscal_mes + "', " + ret_ejercicio_fiscal + ", " + ret_impuesto_retenido + ", " + paramRet + ", " + paramAsiento + ", '" + xmlFirmado + "');"; // 40 param
                ResultSet rs = this.consulta(sql);
                if (rs.next()) {
                    id_factura_venta = rs.getString(1) != null ? rs.getString(1) : "-1";
                    rs.close();
                }
                if (id_factura_venta.compareTo("-1") != 0) {
                    st.executeUpdate("UPDATE tbl_instalacion SET id_factura_venta=" + id_factura_venta + " WHERE id_instalacion=" + id_instalacion + ";");
                    st.executeUpdate("UPDATE tbl_factura_venta SET id_instalacion=" + id_instalacion + ", ip=" + ip + " WHERE id_factura_venta=" + id_factura_venta + ";");
                    st.executeUpdate("update tbl_factura_venta set estado_documento='" + estadoDocumento + "', clave_acceso='" + claveAcceso + "', documento_xml='" + autorizacionXml + "', mensaje='" + respuestaAutoriz.replace("|", ".").replace("\n", " ").replace("\r", " ") + "' where id_factura_venta=" + id_factura_venta);

                }/*error en la creacion de la factura*/ else {
                    ok = false;
                }

            }
            if (ok) {
                ins_fac = id_instalacion + ";" + id_factura_venta;
                con.commit();
            } else {
                con.rollback();
            }
        } catch (Exception e) {
            System.out.println("Portal: " + e.getMessage());
            try {
                con.rollback();
            } catch (Exception ex) {
                System.out.println("Portal: " + ex.getMessage());
            }

        } finally {
            try {
                con.setAutoCommit(true);
            } catch (Exception e) {
                System.out.println("Portal: " + e.getMessage());
            }
        }
        return ins_fac;
    }

    public String insertar(String id_instalacion, String id_sucursal, String id_punto_emision, String id_cliente, String usuario, String serie_factura, String num_factura, String autorizacion,
            String ruc, String razon_social, String fecha_emision, String direccion, String telefono, String id_forma_pago, String forma_pago, String banco, String num_cheque, String num_comp_pago, String gastos_bancos,
            String id_plan_cuenta_banco, String son, String observacion, String subtotal, String subtotal_0, String subtotal_2, String subtotal_3, String descuento, String iva_2, String iva_3, String total, String paramArtic, String ret_num_serie,
            String ret_num_retencion, String ret_autorizacion, String ret_fecha_emision, String ret_ejercicio_fiscal_mes, String ret_ejercicio_fiscal, String ret_impuesto_retenido, String paramRet, String paramAsiento, String xmlFirmado,
            String estadoDocumento, String claveAcceso, String autorizacionXml, String respuestaAutoriz, String ip, String id_plan_cuenta_anticipo, String paramAnticipo) {
        String ins_fac = "-1";
        Connection con = this.getConexion();
        try {
            String id_factura_venta = "-1";
            con.setAutoCommit(false);
            Statement st = con.createStatement();
            boolean ok = true;
            double total_final1 = Double.parseDouble(total);
            String es_instalacion = "TRUE";
            if (es_instalacion.compareTo("TRUE") == 0 && total_final1 > 0) {
                ret_fecha_emision = ret_fecha_emision.compareTo("") != 0 ? "'" + ret_fecha_emision + "'" : "NULL";
                String sql = "select facturaVenta(" + id_sucursal + ", " + id_punto_emision + ", " + id_cliente + ", '" + usuario + "', '" + serie_factura
                        + "', " + num_factura + ", '" + autorizacion + "', '" + ruc + "', '" + razon_social + "', '" + fecha_emision + "', '" + direccion
                        + "', '" + telefono + "', '" + id_forma_pago + "', '" + forma_pago + "', '" + banco + "', '" + num_cheque + "', '" + num_comp_pago + "', " + gastos_bancos
                        + ", " + id_plan_cuenta_banco + ", '" + son + "', '" + observacion + "', " + subtotal + ", " + subtotal_0 + ", " + subtotal_2 + ", " + subtotal_3 + ", " + descuento
                        + ", " + iva_2 + ", " + iva_3 + ", " + total + ", " + paramArtic + ", '" + ret_num_serie + "', '" + ret_num_retencion + "', '" + ret_autorizacion + "', " + ret_fecha_emision
                        + ", '" + ret_ejercicio_fiscal_mes + "', " + ret_ejercicio_fiscal + ", " + ret_impuesto_retenido + ", " + paramRet + ", " + paramAsiento + ", '" + xmlFirmado 
                        + "', " + id_plan_cuenta_anticipo + ", " + paramAnticipo + ", 0, '');"; //  44 param
                ResultSet rs = this.consulta(sql);
                if (rs.next()) {
                    id_factura_venta = rs.getString(1) != null ? rs.getString(1) : "-1";
                    rs.close();
                }
                if (id_factura_venta.compareTo("-1") != 0) {
                    st.executeUpdate("UPDATE tbl_factura_venta SET id_instalacion='" + id_instalacion + "', ip='" + ip + "' WHERE id_factura_venta='" + id_factura_venta + "';");
                    st.executeUpdate("update tbl_factura_venta set estado_documento='" + estadoDocumento + "', clave_acceso='" + claveAcceso + "', documento_xml='" + autorizacionXml + "', mensaje='" + respuestaAutoriz.replace("|", ".").replace("\n", " ").replace("\r", " ") + "' where id_factura_venta='" + id_factura_venta + "';");

                }/*error en la creacion de la factura*/ else {
                    ok = false;
                }

            }
            if (ok) {
                ins_fac = id_factura_venta;
                con.commit();
            } else {
                con.rollback();
            }
        } catch (Exception e) {
            System.out.println("orden de trabajo: " + e.getMessage());
            try {
                con.rollback();
            } catch (Exception ex) {
                System.out.println("orden de trabajo: " + ex.getMessage());
            }

        } finally {
            try {
                con.setAutoCommit(true);
            } catch (Exception e) {
                System.out.println("orden de trabajo: " + e.getMessage());
            }
        }
        return ins_fac;
    }

    public String[] getContrapartidaPichincha(String id_instalacion) {
        String res[] = {"",""};
        try {
            ResultSet rs = this.consulta("select * from tbl_instalacion where id_instalacion='" + id_instalacion + "';");
            if (rs.next()) {
                res[0] = (rs.getString("contrapartida_pichincha") != null ? rs.getString("contrapartida_pichincha") : "");
                res[1] = (rs.getString("convenio_pago") != null ? rs.getString("convenio_pago") : "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
    
    public String[][] getIdSucursalesIdInstalacionesIps(String idFacturas) {
        ResultSet rs = this.consulta("SELECT id_sucursal, id_instalacion, ip from tbl_factura_venta where id_factura_venta in("+idFacturas+") order by id_sucursal, id_instalacion");
        return Matriz.ResultSetAMatriz(rs);
    }
    
    
//    public String facturarACredito(String _DOCS_ELECTRONICOS, String requestIP, String ipdocumental, int puertodocumental, String dbdocumental, String usuariodocumental, String clavedocumental,
//            int id_sucursal, String id_punto_emision, String idInstalacion, String subtotal, String subtotal_0, String subtotal_2, String subtotal_3, String descuento, 
//            String iva_2, String iva_3, String total, String descipcion)
//    {
//        
//        String idFactura = "-1";
//        String serie_factura = "";
//        String autorizacion = "1111111111";
//        String fecha_emision = Fecha.getFecha("ISO");
//        String id_cliente = "";
//        String ruc = "";
//        String razon_social = "";
//        String telefono = "";
//        String direccion = "";
//        String email = "";
//        String forma_pago = "d";
//        String id_forma_pago = "99";
//        String banco = "";
//        String num_cheque = "";
//        String num_comp_pago = "";
//        String gastos_bancos = "0";
//        String id_plan_cuenta_banco = "0";
//        String son = NumeroALetras.Convertir(total, false);
//        String tipo_documento_cliente = "06";
//        String id_plan_cuenta_anticipo = "232";
//        String nombreBoucher = "";
//        String idBanco = "0";
//        String id_producto = "";
//
//        /*String bloqueo_libros = "";
//        while(true){
//            bloqueo_libros = conf.getValor("bloqueo_libros");
//            if(bloqueo_libros.compareTo("false")==0) {
//                conf.setValor("bloqueo_libros", "true");
//                break;
//            }
//            try{
//                Thread.sleep(500);
//            }catch(InterruptedException e){
//                e.printStackTrace();
//            }
//        }*/
//        Configuracion conf = new Configuracion(this._ip, this._puerto, this._db, this._usuario, this._clave);
//        
//        Map<String, String> params = conf.getParametros("'clave_certificado', 'ambiente', 'ruc', 'razon_social', 'nombre_comercial', 'num_resolucion',"
//                + " 'oblga_contabilidad', 'dir_matriz', 'desc_venta', 'cxc', 'p_iva1', 'email_info', 'numeros_soporte', 'pagina_web'");
//        
//        String clave_certificado = params.get("clave_certificado");
//        String ambiente = params.get("ambiente");
////        String tipoEmision = "1"; // 1=normal    2=Indisponibilidad del sistema
//        String ruc_empresa = params.get("ruc");
//        String razon_social_empresa = params.get("razon_social");
//        String nombre_comercial = params.get("nombre_comercial");
//        String email_info = params.get("email_info");
//        String num_contacto = params.get("numeros_soporte");
//        String sitio_web = params.get("pagina_web");
//        String num_resolucion = params.get("num_resolucion");
//        String oblga_contabilidad = params.get("oblga_contabilidad");
//        String dir_matriz = params.get("dir_matriz");
//        String desc_venta = params.get("desc_venta");
//        String cxc = params.get("cxc");
////        String cuenta_propina = params.get("propina");
//        String pIva = params.get("p_iva1");
//        
//        String tipoEmision = "1";
//        
////        String clave_certificado = conf.getValor("clave_certificado");
////        String ambiente = conf.getValor("ambiente");
////        String tipoEmision = conf.getValor("tipo_emision"); // 1=normal    2=Indisponibilidad del sistema
////        String cxc = conf.getValor("cxc");
////        String ruc_empresa = conf.getValor("ruc");
////        String razon_social_empresa = conf.getValor("razon_social");
////        String nombre_comercial = conf.getValor("nombre_comercial");
////        String num_resolucion = conf.getValor("num_resolucion");
////        String oblga_contabilidad = conf.getValor("oblga_contabilidad");
////        String dir_matriz = conf.getValor("dir_matriz");
////        String desc_venta = conf.getValor("desc_venta");
////        String pIva = conf.getValor("p_iva1");
//        conf.cerrar();
//
//        Sucursal objSucursal = new Sucursal(this._ip, this._puerto, this._db, this._usuario, this._clave);
//        String ubicacionNombreComercial[] = objSucursal.getDireccionDePuntoEmision(String.valueOf(id_punto_emision));
//        String direccion_sucursal = ubicacionNombreComercial[0];
//        nombre_comercial = ubicacionNombreComercial[1].compareTo("")!=0 ? ubicacionNombreComercial[1] : nombre_comercial;
//        email_info = ubicacionNombreComercial[2].compareTo("")!=0 ? ubicacionNombreComercial[2] : email_info;
//        num_contacto = ubicacionNombreComercial[3].compareTo("")!=0 ? ubicacionNombreComercial[3] : num_contacto;
//        sitio_web = ubicacionNombreComercial[4].compareTo("")!=0 ? ubicacionNombreComercial[4] : sitio_web;
//        objSucursal.cerrar();
//        
//        
//        PuntoEmision objPuntoEmision = new PuntoEmision(this._ip, this._puerto, this._db, this._usuario, this._clave);
//        serie_factura = "";
//        try{
//            ResultSet rsCliente = objPuntoEmision.getPuntoEmision(id_punto_emision);
//            if(rsCliente.next()){
//                serie_factura = rsCliente.getString("fac_num_serie")!=null ? rsCliente.getString("fac_num_serie") : "";
//                rsCliente.close();
//            }
//        }catch(Exception e){
//            e.printStackTrace();
//        }finally{
//            objPuntoEmision.cerrar();
//        }
//        
//        
//        Instalacion objInstalacion = new Instalacion(this._ip, this._puerto, this._db, this._usuario, this._clave);
//        try {
//            ResultSet rsInstalacion = objInstalacion.getInstalacion(idInstalacion);
//            if (rsInstalacion.next()) {
//                id_cliente = (rsInstalacion.getString("id_cliente") != null) ? rsInstalacion.getString("id_cliente") : "-1";
//                ruc = (rsInstalacion.getString("ruc") != null) ? rsInstalacion.getString("ruc") : "";
//                razon_social = (rsInstalacion.getString("razon_social") != null) ? rsInstalacion.getString("razon_social") : "";
//                direccion = (rsInstalacion.getString("direccion") != null) ? rsInstalacion.getString("direccion") : "";
//                telefono = (rsInstalacion.getString("telefono") != null) ? rsInstalacion.getString("telefono") : "";
//                email = (rsInstalacion.getString("email") != null) ? rsInstalacion.getString("email") : "";
//                tipo_documento_cliente = rsInstalacion.getString("tipo_documento") != null ? rsInstalacion.getString("tipo_documento") : "06";
//                id_plan_cuenta_anticipo = rsInstalacion.getString("id_plan_cuenta_anticipo") != null ? rsInstalacion.getString("id_plan_cuenta_anticipo") : "232";
//                id_producto = (rsInstalacion.getString("id_producto") != null) ? rsInstalacion.getString("id_producto") : "";
//                rsInstalacion.close();
//            }
//        }catch(Exception e){
//            e.printStackTrace();
//        }finally{
//            objInstalacion.cerrar();
//        }
//        
//        
//        FormaPago objFormaPago = new FormaPago(this._ip, this._puerto, this._db, this._usuario, this._clave);
//        String codigoFormaPago = objFormaPago.getCodigoFormaPago(id_forma_pago);
//        objFormaPago.cerrar();
//        
//        
//
//        FacturaVenta objFacturaVenta = new FacturaVenta(this._ip, this._puerto, this._db, this._usuario, this._clave);
//        long num_factura = objFacturaVenta.getNumFactura( Integer.parseInt(id_punto_emision) );
//        
//        
//        
//        Producto objProducto = new Producto(this._ip, this._puerto, this._db, this._usuario, this._clave);
//
//        Iva objIva = new Iva(this._ip, this._puerto, this._db, this._usuario, this._clave);
//        String codigoIva = objIva.getCodigoIva(pIva);
//        
//        FacturaElectronica objFE = new FacturaElectronica();
//
//        
//        descuento = (descuento.compareTo("") != 0) ? descuento : "0";
//        subtotal_0 = (subtotal_0.compareTo("") != 0) ? subtotal_0 : "0";
//        iva_2 = (iva_2.compareTo("") != 0) ? iva_2 : "0";
//        subtotal_2 = (Float.parseFloat(subtotal_2) > 0) ? subtotal_2 : (Float.parseFloat(iva_2) > 0 ? subtotal : "0");
//
//        iva_3 = (iva_3.compareTo("") != 0) ? iva_3 : "0";
//        subtotal_3 = (Float.parseFloat(subtotal_3) > 0) ? subtotal_3 : (Float.parseFloat(iva_3) > 0 ? subtotal : "0");
//
//        gastos_bancos = (gastos_bancos.compareTo("") != 0) ? gastos_bancos : "0";
////        float ax_total = Float.parseFloat(total);
//
//
//        String ret_num_serie = "";
//        String ret_num_retencion = "0";
//        String ret_autorizacion = "";
//        String ret_fecha_emision = "";
//        String ret_impuesto_retenido = "0";
//        String ret_ejercicio_fiscal_mes = "";
//        String ret_ejercicio_fiscal = "NULL";
//
//        String paramRet = "";
////        String codigos = "";
////        String codsRetencion = "";
////        String basesImponibles = "";
////        String porcentajesRet = "";
////        String valoresRet = "";
////        String sustentos = "";
//
//        String matParamAsientoAx[][] = null;
//        matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{cxc, total, "0"});
//        if (Float.parseFloat(descuento) > 0) {
//            matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{desc_venta, descuento, "0"});
//        }
//
//        //  productos
//        String ids_productos = "";
//        String descripciones = "";
//        String cantidades_prod = "";
//        String preciosUnitarios = "";
//        String descuentos = "";
//        float sumDescuentos = 0;
//        String subtotales = "";
//        String ivas = "";
//        String pIvas = "";
//        String codigoIvas = "";
//        String paramArtic = "";
////        for (int i = 0; i <= tope; i++) {
////            if (request.getParameter("idPr" + i) != null) {
////                String id_producto = request.getParameter("idPr" + i);
//                String subt = subtotal;
//                String iva = iva_2;
//                if( iva_2.compareTo("0") == 0 && iva_3.compareTo("0") != 0 ){
//                    iva = String.valueOf( Float.parseFloat(iva_3) );
//                }
//
//                ids_productos += id_producto + ",";
//                descripciones += descipcion + ",";
//                cantidades_prod += "1,";
//                if( subtotal_2.compareTo("0") != 0 && subtotal_3.compareTo("0") == 0 ){
//                    preciosUnitarios += subtotal_2 + ",";
//                }
//                if( subtotal_2.compareTo("0") == 0 && subtotal_3.compareTo("0") != 0 ){
//                    preciosUnitarios += subtotal_3 + ",";
//                }
//                
//                descuentos += "0,";
////                sumDescuentos += Float.parseFloat(request.getParameter("d" + i));
////                String pIva = request.getParameter("p_iva" + i);
////                String codigoIva = request.getParameter("cod_iva" + i);
//                subtotales += subt + ",";
//                ivas += iva + ",";
//                pIvas += pIva + ",";
//                codigoIvas += codigoIva + ",";
//
//                String tipo = "";
//                String id_iva = "2";
//                String id_plan_cuenta_venta = "";
//                try {
//                    ResultSet rs = objProducto.getProducto(id_producto);
//                    if (rs.next()) {
//                        tipo = rs.getString("tipo") != null ? rs.getString("tipo") : "";
//                        id_iva = rs.getString("id_iva") != null ? rs.getString("id_iva") : "2";
//                        id_plan_cuenta_venta = rs.getString("id_plan_cuenta_venta") != null ? rs.getString("id_plan_cuenta_venta") : "";
//                        rs.close();
//                    }
//                } catch (Exception e) {
//                }
//
//                matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{id_plan_cuenta_venta, "0", subt});
//
//                String cuenta = tipo.compareTo("s") == 0 ? "id_plan_cuenta_venta_servicio" : "id_plan_cuenta_venta_bien";
//                String id_cuenta_iva = objIva.getIva(id_iva, cuenta);
//                matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{id_cuenta_iva, "0", iva});
//
//                paramArtic += "['" + id_producto + "', '1', '" + subt + "', '" + subt
//                        + "', '0', '" + iva + "', '" + total + "', '" + descipcion
//                        + "', '" + subt + "', '" + tipo + "', '" + pIva + "', '" + codigoIva + "', 'p','-1'],";
////            }
////        }
//        
//        
//        if (paramArtic.compareTo("") != 0) {
//            paramArtic = paramArtic.substring(0, paramArtic.length() - 1);
//
//            ids_productos = ids_productos.substring(0, ids_productos.length() - 1);
//            descripciones = descripciones.substring(0, descripciones.length() - 1);
//            cantidades_prod = cantidades_prod.substring(0, cantidades_prod.length() - 1);
//            preciosUnitarios = preciosUnitarios.substring(0, preciosUnitarios.length() - 1);
//            descuentos = descuentos.substring(0, descuentos.length() - 1);
//            subtotales = subtotales.substring(0, subtotales.length() - 1);
//            ivas = ivas.substring(0, ivas.length() - 1);
//            pIvas = pIvas.substring(0, pIvas.length() - 1);
//            codigoIvas = codigoIvas.substring(0, codigoIvas.length() - 1);
//        }
//        
//        String observacion = "Emisión de la factura por servicios de Internet Nro. "+serie_factura+"-" + num_factura;
//
//        String matParamAsiento[][] = Matriz.suprimirDuplicados(matParamAsientoAx, 0);
//        String paramAsiento = "";
//        for (int i = 0; i < matParamAsiento.length; i++) {
//            paramAsiento += "['" + matParamAsiento[i][0] + "', '" + matParamAsiento[i][1] + "', '" + matParamAsiento[i][2] + "'],";
//        }
//        if (paramAsiento.compareTo("") != 0) {
//            paramAsiento = paramAsiento.substring(0, paramAsiento.length() - 1);
//        }
//
//        //  POR ANTICIPOS
//        String idCliAnt = "";
//        String monto_vajar = "";
//
//
//
//
//        boolean ok = true;
//        String xmlFirmado = "";
//        String estadoDocumento = "";
//        String certificado = _DOCS_ELECTRONICOS + "certificado.p12";
//        String rutaSalida = _DOCS_ELECTRONICOS + "firmados";
//        String claveAcceso = "";
//        String autorizacionXml = "";
//        String respuestaAutoriz = "";
//        //String nombre_archivo = "factura_" + ruc + "_" + serie_factura + "-" + num_factura + ".xml";
//
//        
//
//        if (sumDescuentos == Float.parseFloat(descuento)) {
//
//            String prod_sin_stock = objFacturaVenta.verificarStock(id_sucursal, ids_productos, cantidades_prod);
//            prod_sin_stock = "";
//            if (prod_sin_stock.compareTo("") == 0) {
//
//                if (!objFacturaVenta.facturaDuplicada(serie_factura, String.valueOf(num_factura) ) ) {
//
//                    ok = false;
//                    String vecSerie[] = serie_factura.split("-");
//
//                    claveAcceso = objFE.getClaveAcceso(Cadena.setFecha(fecha_emision), "01", ruc_empresa, ambiente, vecSerie[0] + vecSerie[1], Cadena.setSecuencial( String.valueOf(num_factura) ), tipoEmision);
//
//                    objFE.generarXml(claveAcceso, ambiente, tipoEmision, razon_social_empresa, nombre_comercial, ruc_empresa, email_info, num_contacto, sitio_web, "01", vecSerie[0], vecSerie[1],
//                            Cadena.setSecuencial( String.valueOf(num_factura) ), dir_matriz, Cadena.setFecha(fecha_emision), direccion_sucursal, num_resolucion, oblga_contabilidad,
//                            tipo_documento_cliente, razon_social, ruc, subtotal, descuento, subtotal_0, subtotal_2, iva_2, subtotal_3, iva_3, total, codigoFormaPago,
//                            ids_productos, descripciones, cantidades_prod, preciosUnitarios, descuentos, subtotales, ivas, pIvas, codigoIvas, direccion, email);
//                    String documentoXml = _DOCS_ELECTRONICOS + "generados/" + claveAcceso + ".xml";
//                    objFE.salvar(documentoXml);
//                    String error = objFE.getError();
//
//                    if (error.compareTo("") == 0) {
//                        estadoDocumento = "g";
//                        String archivoSalida = claveAcceso + ".xml";
//                        FirmaXadesBes firmaDigital = new FirmaXadesBes(certificado, clave_certificado, documentoXml, rutaSalida, archivoSalida);
//                        firmaDigital.execute();
//                        error = firmaDigital.getError();
//
//                        if (error.compareTo("") == 0) {
//                            estadoDocumento = "f";
//                            try{
//                                autorizacionXml = this.getStringFromFile(_DOCS_ELECTRONICOS + "firmados/" + claveAcceso + ".xml");
//                                ok = true;
//                            } catch( Exception e){
//                                e.printStackTrace();
//                            }
//                        }
//                    }
////                    r = "fun»_('btnVenEn').disabled=false;^msg»" + error;
//                    this.setError(error);
//
//                    if (ok) {
//                        idFactura = this.insertar( id_sucursal, Integer.parseInt(id_punto_emision), id_cliente, this._usuario, serie_factura, String.valueOf(num_factura), autorizacion, ruc,
//                                razon_social, fecha_emision, direccion, telefono, id_forma_pago, forma_pago, banco, num_cheque, num_comp_pago, gastos_bancos, id_plan_cuenta_banco,
//                                son, observacion, subtotal, subtotal_0, subtotal_2, subtotal_3, descuento, iva_2, iva_3, total, "array[" + paramArtic + "]", ret_num_serie, ret_num_retencion,
//                                ret_autorizacion, ret_fecha_emision, ret_ejercicio_fiscal_mes, ret_ejercicio_fiscal, ret_impuesto_retenido,
//                                "array[" + paramRet + "]::varchar[]", "array[" + paramAsiento + "]", xmlFirmado, id_plan_cuenta_anticipo, idCliAnt, monto_vajar, nombreBoucher);
//                        if (idFactura.compareTo("-1") != 0) {
//                            //String vecFactComp[] = id_factura_.split(":");
//                            //String id_factura = vecFactComp[0];
//                            /*if(respuestaAutoriz.equals("RECHAZADO") || respuestaAutoriz.equals("NO AUTORIZADO")){
//                                objFacturaVenta.anular(id_factura);
//                            }*/
//                            objFacturaVenta.setDocumentoElectronico(idFactura, estadoDocumento, claveAcceso, autorizacionXml, respuestaAutoriz, idBanco);
//
//                            Archivo archivo = new Archivo(ipdocumental, puertodocumental, dbdocumental, usuariodocumental, clavedocumental);
//                            archivo.setArchivoDocumentalTexto(autorizacionXml, String.valueOf(num_factura), "" + id_sucursal, "tbl_factura_venta", idFactura, "documentoxml", "public", "db_isp");
//                            archivo.cerrar();
//
//
////                                        if(factIpTv.compareTo("si")==0 && idInstalacion.compareTo("undefined") != 0){
////                                            Instalacion objInstalacion = new Instalacion(this._ip, this._puerto, this._db, usuario, clave);
////                                            objInstalacion.setSuscripcionTVGOMAX(idInstalacion);
////                                            objInstalacion.cerrar();
////                                        }
//
//
//                            Auditoria auditoria = new Auditoria(this._ip, this._puerto, this._db, this._usuario, this._clave);
//                            auditoria.setRegistro(this._usuario, requestIP, "EMISION DE LA FACTURA DE VENTA: " + serie_factura + "-" + num_factura);
//                            auditoria.cerrar();
//
//                            return idFactura;
//
////                                r = "err»0^vta»cmp^fun»fac_buscarVenta();imprimir('pdfFacturaVenta?id=" + id_factura_ + "');";
//
//                        } else {
//                            this.setError(objFacturaVenta.getError() );
//                        }
//                        
//                    }
//
//                } else {
////                        r = "fun»_('btnVenEn').disabled=false;^msg»El número de factura " + serie_factura + "-" + num_factura + " ya ha sido emitido.";
//                    this.setError("El número de factura " + serie_factura + "-" + num_factura + " ya ha sido emitido.");
//                }
//
//            } else {
////                r = "fun»_('btnVenEn').disabled=false;^msg»Stock insuficiente. El stock del producto de código: " + prod_sin_stock + " ha disminuido por una venta realizada desde otra caja ubicada en la sucursal.";
//                this.setError("Stock insuficiente. El stock del producto de código: " + prod_sin_stock + " ha disminuido por una venta realizada desde otra caja ubicada en la sucursal.");
//            }
//            
//        } else {
////            r = "fun»_('btnVenEn').disabled=false;^msg»La sumatoria de descuentos del detalle " + sumDescuentos + " es diferente del descuento en el resumen " + descuento;
//            this.setError("La sumatoria de descuentos del detalle " + sumDescuentos + " es diferente del descuento en el resumen " + descuento);
//        }
//        
//        objProducto.cerrar();
//        objIva.cerrar();
//
//        return "-1";
//    }
//    
}
