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

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jorge
 */
public class Instalacion extends DataBase {

    public Instalacion(String m, int p, String db, String u, String c) {
        super(m, p, db, u, c);
    }

    public ResultSet getInstalacion(String id) {
        return this.consulta("SELECT * FROM vta_instalacion where id_instalacion=" + id);
    }

    public ResultSet getInstalaciones(String idSucursal, int anio, int mes) {
        String fecha_ini = anio + "-" + mes + "-01";
        String fecha_fin = anio + "-" + mes + "-" + Fecha.getUltimoDiaMes(anio, mes);
        return this.consulta("SELECT distinct id_instalacion, id_sucursal || '-' ||  num_instalacion, ruc, razon_social, toDateSQL(fecha_instalacion), ip, txt_estado_servicio FROM vta_instalacion where id_sucursal=" + idSucursal + " and id_instalacion not in "
                + "(select id_instalacion from tbl_prefactura where periodo between '" + fecha_ini + "' and '" + fecha_fin + "') and anulado=false and estado_servicio in ('a','s','c') order by razon_social;");
    }

    public ResultSet getInstalaciones(String idSucursal, int anio, int mes, String tb) {
        String fecha_ini = anio + "-" + mes + "-01";
        String fecha_fin = anio + "-" + mes + "-" + Fecha.getUltimoDiaMes(anio, mes);
        return this.consulta("SELECT distinct id_instalacion, id_sucursal || '-' ||  num_instalacion, ruc, razon_social, toDateSQL(fecha_instalacion), ip, txt_estado_servicio FROM vta_instalacion where id_sucursal=" + idSucursal + " and id_instalacion not in "
                + "(select id_instalacion from tbl_prefactura where periodo between '" + fecha_ini + "' and '" + fecha_fin + "') and anulado=false and estado_servicio in ('a','s','c') and (lower(razon_social) like '" + tb.toLowerCase() + "%' or ruc like '" + tb.toLowerCase() + "%') order by razon_social;");
    }

    public ResultSet getInstalaciones(int idSucursal, String where) {
        return this.consulta("SELECT distinct id_instalacion, id_sucursal || '-' ||  num_instalacion, ruc, razon_social, plan, ip, plan, direccion_instalacion "
                + "FROM vta_instalacion where " + where + " and id_sucursal=" + idSucursal + " and anulado=false;");
    }

    public ResultSet getInstalaciones(String idCliente) {
        return this.consulta("SELECT distinct id_instalacion, id_sucursal || '-' ||  num_instalacion, ruc, razon_social, plan, ip, plan, direccion_instalacion, estado_servicio, fecha_desinstalacion "
                + "FROM vta_instalacion where id_cliente=" + idCliente + " and anulado=false;");
    }

    public ResultSet getInstalaciones() {
        return this.consulta("SELECT * FROM tbl_instalacion where anulado=false");
    }
    
    public ResultSet getInstalacionesEstado(String idCliente) {
        return this.consulta("SELECT fecha_instalacion, ip, direccion_instalacion, txt_estado_servicio, estado_servicio, "
                + "case estado_servicio when 'c' then false when 'd' then false when 'e' then false when 'n' then false else true end as habilitar "
                + "FROM vta_instalacion where anulado=false and id_cliente="+idCliente);
    }
    
    public String[] getRucCliente(String id) {
        String res[] = new String[]{"", ""};
        try {
            ResultSet rs = this.consulta("SELECT ruc, razon_social from vta_instalacion where id_instalacion=" + id);
            if(rs.next()){
                res[0] = rs.getString("ruc") != null ? rs.getString("ruc") : "";
                res[1] = rs.getString("razon_social") != null ? rs.getString("razon_social") : "";
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public String getInstalacionJSON(String id) {
        ResultSet rs = this.consulta("SELECT id_instalacion, ruc, razon_social, sector, ciudad, direccion, telefono, direccion_instalacion, "
                + "plan, txt_comparticion, ip, receptor, antena_acoplada, txt_estado_servicio, id_sucursal, movil_claro, movil_movistar, deviceclave, "
                + "set_deviceclave, ip_radio, tarjeta_credito_caduca, email, txt_tipo_instalacion, puesta_tierra,tipo_instalacion FROM vta_instalacion where id_instalacion=" + id);
        String tbl = this.getJSON(rs);
        try {
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tbl;
    }

    public String getInstalacionesJSON() {
        ResultSet rs = this.consulta("SELECT id_instalacion, ruc, razon_social, sector, ciudad, direccion, telefono, direccion_instalacion, "
                + "plan, txt_comparticion, ip, receptor, antena_acoplada, txt_estado_servicio, id_sucursal FROM vta_instalacion where anulado=false");
        String tbl = this.getJSON(rs);
        try {
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tbl;
    }

    /*public String getParroquia(String id_parroquia)
    {
        String parroquia = "";
        try{
            ResultSet rs = this.consulta("SELECT * FROM tbl_ubicacion where id_ubicacion="+id_parroquia);
            if(rs.next()){
                parroquia = rs.getString("id_parroquia")!=null ? rs.getString("id_parroquia") : "";
                rs.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return parroquia;
    }
    /*public String getInstalacionesJSON(String w)
    {
        ResultSet rs = this.consulta("SELECT id_instalacion, ruc, razon_social, sector, ciudad, direccion, telefono, direccion_instalacion, "
                + "plan, txt_comparticion, ip, receptor, antena_acoplada, txt_estado_servicio, id_sucursal FROM vta_soporte " + w);
        String tbl = this.getJSON(rs);
        try{
            rs.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return tbl;
    }*/
    public boolean aplicaDescuento50(String idCliente) {
        boolean ok = false;
        try {
            ResultSet rs = this.consulta("select date_part('year'::text, age(now(), fecha_desinstalacion::timestamp with time zone)) AS tiempo_desinstalacion "
                    + "from tbl_instalacion "
                    + "where id_cliente=" + idCliente + " and estado_servicio = 't' "
                    + "and id_cliente not in (select I.id_cliente from tbl_instalacion as I where I.id_cliente=" + idCliente + " and I.estado_servicio in ('a', 'c', 's', 'n') ) "
                    + "order by fecha_desinstalacion desc");
            if (rs.next()) {
                int tiempo_desinstalacion = rs.getString("tiempo_desinstalacion") != null ? rs.getInt("tiempo_desinstalacion") : 0;
                ok = tiempo_desinstalacion > 0;
                rs.close();
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok;
    }

    public boolean aplicaDescuento100(String idCliente) {
        boolean ok = false;
        try {
            ResultSet rs = this.consulta("select date_part('year'::text, age(now(), fecha_desinstalacion::timestamp with time zone)) AS tiempo_desinstalacion "
                    + "from tbl_instalacion "
                    + "where id_cliente=" + idCliente + " and estado_servicio = 't' "
                    + "and id_cliente not in (select I.id_cliente from tbl_instalacion as I where I.id_cliente=" + idCliente + " and I.estado_servicio in ('a', 'c', 's', 'n') ) "
                    + "order by fecha_desinstalacion desc");
            if (rs.next()) {
                int tiempo_desinstalacion = rs.getString("tiempo_desinstalacion") != null ? rs.getInt("tiempo_desinstalacion") : -1;
                ok = tiempo_desinstalacion == 0;
                rs.close();
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok;
    }

//    public boolean ipDisponible(String id, String id_sucursal, String ip) {
//        ResultSet res = this.consulta("select distinct ip from tbl_instalacion where upper(ip::varchar) = '" + ip + "' "
//                + "and estado_servicio in ('p', 'a','s','c','r','d','n') and anulado=false "
//                + "and id_sucursal=" + id_sucursal + " and id_instalacion<>" + id);
//        if (this.getFilas(res) > 0) {
//            return false;
//        }
//        if (ip.indexOf("255") >= 0) {
//            return false;
//        }
//        try {
//            res.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return true;
//    }
    public boolean ipDisponible(String id, String id_sucursal, String ip) {
        ResultSet res = this.consulta("select distinct ip from tbl_instalacion where upper(ip::varchar) = '" + ip + "' "
                + "and estado_servicio in ('p', 'a','s','c','r','d','n') and anulado=false "
                + "and id_sucursal=" + id_sucursal + " and id_instalacion<>" + id);
        if (this.getFilas(res) > 0) {
            return false;
        }
//        String cuartoOcteto = ip.replaceAll("[0-9]+.[0-9]+.[0-9]+.", "").replaceAll("/[0-9]+", "");
//        if (Integer.parseInt(cuartoOcteto) == 255) {
//            return false;
//        }
        try {
            res.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public List getIpsDisponibles(int id_sucursal) {
        ResultSet rsIps = this.consulta("select distinct ip from tbl_instalacion where ip::varchar like '192.168.%' and anulado=false "
                + "and id_sucursal=" + id_sucursal + " order by ip");

        ResultSet rsIpsEstado = this.consulta("select distinct ip, estado_servicio from tbl_instalacion "
                + "where ip::varchar like '192.168.%' and anulado=false and id_sucursal=" + id_sucursal + " order by ip");
        String matIps[][] = Matriz.ResultSetAMatriz(rsIpsEstado);
        List<String> ips = new ArrayList();
        try {
            rsIps.beforeFirst();
            int i = 0;
            while (rsIps.next()) {
                String ip = rsIps.getString("ip") != null ? rsIps.getString("ip") : "";
                if (!this.enMatriz(matIps, ip)) {
                    ips.add(ip);
                    if (i > 30) {
                        break;
                    }
                    i++;
                }
            }
            rsIps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ips;
    }

    public boolean enMatriz(String mat[][], String ip) {
        if (mat != null) {
            for (int i = 0; i < mat.length; i++) {
                if (mat[i][0].compareTo(ip) == 0) {
                    if (mat[i][1].compareTo("p") == 0 || mat[i][1].compareTo("a") == 0 || mat[i][1].compareTo("s") == 0
                            || mat[i][1].compareTo("c") == 0 || mat[i][1].compareTo("r") == 0 || mat[i][1].compareTo("d") == 0
                            || mat[i][1].compareTo("n") == 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public String getRazonSocial(String id) {
        String razon_social = "";
        try {
            ResultSet rs = this.consulta("SELECT razon_social FROM vta_instalacion where id_instalacion=" + id);
            if (rs.next()) {
                razon_social = rs.getString("razon_social") != null ? rs.getString("razon_social") : "";
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return razon_social;
    }

    public String getMailInstalacion(String id) {
        String mail = "";
        try {
            ResultSet rs = this.consulta("SELECT email FROM vta_instalacion where id_instalacion=" + id + ";");
            if (rs.next()) {
                mail = rs.getString("email") != null ? rs.getString("email") : "";
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mail;
    }

    public ResultSet getAntenasAcopladas() {
        return this.consulta("SELECT distinct antena_acoplada, antena_acoplada FROM tbl_instalacion order by antena_acoplada");
    }

    public boolean hayPreFacturasGeneradas(int anio, int mes) {
        String fecha_ini = anio + "-" + mes + "-01";
        String fecha_fin = anio + "-" + mes + "-" + Fecha.getUltimoDiaMes(anio, mes);
        try {
            ResultSet res = this.consulta("SELECT count(id_prefactura) FROM tbl_prefactura WHERE fecha_prefactura between '" + fecha_ini + "' and '" + fecha_fin + "';");
            if (res.next()) {
                String num = (res.getString(1) != null) ? res.getString(1) : "0";
                if (Long.parseLong(num) > 0) {
                    return true;
                }
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getAnioMinInstalacion() {
        int anio = Fecha.getAnio();
        try {
            ResultSet res = this.consulta("select min(date_part('year', fecha_registro)) from tbl_instalacion;");
            if (res.next()) {
                anio = (res.getString(1) != null) ? res.getInt(1) : anio;
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return anio;
    }

    public ResultSet getMateriales(String id) {
        return this.consulta("SELECT * FROM vta_instalacion_material where id_instalacion=" + id + ";");
    }

    public String getNumInstalacionJSON() {
        ResultSet rs = this.consulta("select S.id_sucursal, case when max(I.num_instalacion)>0 then max(I.num_instalacion)+1 else 1 end "
                + "from tbl_sucursal as S left outer join tbl_instalacion as I on S.id_sucursal=I.id_sucursal group by S.id_sucursal;");
        String tbl = this.getJSON(rs);
        try {
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tbl;
    }

    public String getNumInstalacion(int idSuc) {
        String r = "1";
        try {
            ResultSet rs = this.consulta("select case when max(num_instalacion)>0 then max(num_instalacion)+1 else 1 end from tbl_instalacion where id_sucursal=" + idSuc);
            if (rs.next()) {
                r = rs.getString(1) != null ? rs.getString(1) : "1";
                rs.close();
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return r;
    }

    public String getNumOrdenServicio(int idSuc) {
        String r = "1";
        try {
            ResultSet rs = this.consulta("select case when max(num_orden_servicio)>0 then max(num_orden_servicio)+1 else 1 end from tbl_instalacion where id_sucursal=" + idSuc);
            if (rs.next()) {
                r = rs.getString(1) != null ? rs.getString(1) : "1";
                rs.close();
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return r;
    }
    
    public ResultSet getInstalacionesTVGOMAX(String where)
    {
        return this.consulta("select id_instalacion, txt_sucursal, ruc, razon_social, plan, fecha_suscripcion from vta_instalacion where lower(plan) like '%iptv%' and estado_servicio <> 't' " + 
                where + " order by txt_sucursal, razon_social limit 15");
    }
    
//    public String getIPsJSON() {
//        ResultSet rs = this.consulta("select S.id_sucursal, S.ip_inicio,  max( regexp_replace(inet_out(ip + 1)::varchar, '(\\.)|(/\\d*)', '', 'g')::int8 )"
//                + " from tbl_sucursal as S left outer join tbl_instalacion as I on S.id_sucursal=I.id_sucursal group by S.id_sucursal, S.ip_inicio;");
//        String tbl = this.getJSON(rs);
//        try {
//            if (rs != null) {
//                rs.close();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return tbl;
//    }

    public String getSigIpSucursal(int id_sucursal) {
        String ip = "";
        try {
            ResultSet rs = this.consulta("select S.id_sucursal, S.ip_inicio, int8ToInet( max( replace(inet_out(ip + 1)::varchar, '.', '')::int8 )) as ip "
                    + "from tbl_sucursal as S left outer join tbl_instalacion as I on S.id_sucursal=I.id_sucursal where S.id_sucursal=" + id_sucursal
                    + " group by S.id_sucursal, S.ip_inicio;");
            if (rs.next()) {
                ip = rs.getString("ip") != null ? rs.getString("ip") : "";
                String vecIp[] = ip.replace(".", ":").split(":");
                if (vecIp[3].compareTo("255") == 0) {
                    ip = vecIp[0] + "." + vecIp[1] + "." + (Integer.parseInt(vecIp[2]) + 1) + ".2";
                }
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return ip;
    }

    public String concatenarValores(String id_articulos, String cantidades, String precios_unitarios,
            String subtotales, String descuentos, String ivas, String totales) {
        String param = "";
        String vecArti[] = id_articulos.split(",");
        String vecCant[] = cantidades.split(",");
        String vecPU[] = precios_unitarios.split(",");
        String vecSubt[] = subtotales.split(",");
        String vecDes[] = descuentos.split(",");
        String vecIva[] = ivas.split(",");
        String vecTot[] = totales.split(",");
        for (int i = 0; i < vecArti.length; i++) {
            param += "['" + vecArti[i] + "','" + vecCant[i] + "','" + vecPU[i] + "','" + vecSubt[i] + "','" + vecDes[i] + "','" + vecIva[i] + "','" + vecTot[i] + "', ''],";
        }
        param = param.substring(0, param.length() - 1);
        return "array[" + param + "]";
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

    public String insertar(String id_contrato, String convenio_pago, String num_instalacion, String id_sucursal, String set_convenio_tarjeta, String id_provincia, String id_ciudad, String id_parroquia,
            String id_sector, String tipo_instalacion, String costo_instalacion, String direccion_instalacion, String ip, String ip_radio, String id_plan_contratado, String es_instalacion,
            String cobrar, String motivo_no_cobrar, String radusername, String radclave, String deviceclave,
            int id_sucursal_venta, int id_punto_emision, String id_cliente, String usuario, String serie_factura, String num_factura, String autorizacion,
            String ruc, String razon_social, String fecha_emision, String direccion, String telefono, String id_forma_pago, String forma_pago, String banco, String num_cheque,
            String num_comp_pago, String gastos_bancos, String id_plan_cuenta_banco, String son, String observacion, String subtotal, String subtotal_0, String subtotal_2,
            String subtotal_3, String descuento, String iva_2, String iva_3, String total, String paramArtic, String ret_num_serie, String ret_num_retencion,
            String ret_autorizacion, String ret_fecha_emision, String ret_ejercicio_fiscal_mes, String ret_ejercicio_fiscal, String ret_impuesto_retenido,
            String paramRet, String paramAsiento, String xmlFirmado, String tipo_cliente_instalacion) {

        String id_factura_id_Instalacion = "-1:-1";
        Connection con = this.getConexion();
        try {
            String num_orden_servicio = this.getNumOrdenServicio(id_sucursal_venta);
            con.setAutoCommit(false);
            Statement st = con.createStatement();
            ip_radio = ip_radio.compareTo("") != 0 ? "'" + ip_radio + "'" : "NULL";
            String sql = "INSERT INTO tbl_instalacion(num_instalacion, convenio_pago, num_orden_servicio, id_sucursal, id_cliente, id_provincia, id_ciudad, id_parroquia, id_sector, tipo_instalacion, costo_instalacion, direccion_instalacion, "
                    + "id_contrato, fecha_instalacion, ip, ip_radio, id_plan_contratado, id_plan_establecido, id_plan_actual, es_instalacion, cobrar, motivo_no_cobrar, estado_instalacion, radusername, radclave, deviceclave, alias, set_convenio_tarjeta,tipo_cliente_instalacion) "
                    + "VALUES(" + num_instalacion + ", '" + convenio_pago + "', " + num_orden_servicio + ", " + id_sucursal + ", " + id_cliente + ", " + id_provincia + ", " + id_ciudad + ", " + id_parroquia + ", " + id_sector + ", '" + tipo_instalacion + "', " + costo_instalacion + ", '" + direccion_instalacion + "', " + id_contrato
                    + ", NULL, '" + ip + "', " + ip_radio + ", " + id_plan_contratado + ", " + id_plan_contratado + ", " + id_plan_contratado + ", " + es_instalacion + ", " + cobrar + ", '" + motivo_no_cobrar + "', 'e', '" + radusername + "', '" + radclave + "', '" + deviceclave + "', '" + usuario + "', " + set_convenio_tarjeta + ",'" + tipo_cliente_instalacion + "');";
            if (st.executeUpdate(this.decodificarURI(sql), Statement.RETURN_GENERATED_KEYS) > 0) {
                String pk = "-1";
                ResultSet rs = st.getGeneratedKeys();
                if (rs.next()) {
                    pk = rs.getString(1) != null ? rs.getString(1) : "-1";
                    rs.close();
                }

                boolean ok = true;
                if (es_instalacion.compareTo("TRUE") == 0) {
                    ret_fecha_emision = ret_fecha_emision.compareTo("") != 0 ? "'" + ret_fecha_emision + "'" : "NULL";
                    ResultSet res = this.consulta("select facturaVenta(" + id_sucursal + ", " + id_punto_emision + ", " + id_cliente + ", '" + usuario + "', '" + serie_factura
                            + "', " + num_factura + ", '" + autorizacion + "', '" + ruc + "', '" + razon_social + "', '" + fecha_emision + "', '" + direccion
                            + "', '" + telefono + "', '" + id_forma_pago + "', '" + forma_pago + "', '" + banco + "', '" + num_cheque + "', '" + num_comp_pago + "', " + gastos_bancos
                            + ", " + id_plan_cuenta_banco + ", '" + son + "', '" + observacion + "', " + subtotal + ", " + subtotal_0 + ", " + subtotal_2 + ", " + subtotal_3 + ", " + descuento
                            + ", " + iva_2 + ", " + iva_3 + ", " + total + ", " + paramArtic + ", '" + ret_num_serie + "', '" + ret_num_retencion + "', '" + ret_autorizacion + "', " + ret_fecha_emision
                            + ", '" + ret_ejercicio_fiscal_mes + "', " + ret_ejercicio_fiscal + ", " + ret_impuesto_retenido + ", " + paramRet + ", " + paramAsiento + ", '" + xmlFirmado + "');"); // 40 param
                    String resFactVent = "-1";
                    if (res.next()) {
                        resFactVent = (res.getString(1) != null) ? res.getString(1) : "-1";
                        res.close();
                    }

                    if (resFactVent.compareTo("-1") != 0) {
                        id_factura_id_Instalacion = resFactVent + ":" + pk;
                        String vec[] = resFactVent.split(":");
                        st.executeUpdate("UPDATE tbl_instalacion SET id_factura_venta=" + vec[0] + " WHERE id_instalacion=" + pk + ";");
                        st.executeUpdate("UPDATE tbl_factura_venta SET id_instalacion=" + pk + ", ip='" + ip + "' WHERE id_factura_venta=" + vec[0] + ";");

                    } else {
                        id_factura_id_Instalacion = "-1:-1";
                        ok = false;
                        this.ejecutar("delete from tbl_instalacion where id_instalacion=" + pk);
                    }
                } else {
                    id_factura_id_Instalacion = "-1:" + pk;
                }

                if (ok) {
                    sql = "insert into tbl_bodega(id_sucursal, bodega, id_responsable, ubicacion, id_instalacion, es_responsable_cliente) values("
                            + id_sucursal + ", 'INSTALACION No. " + id_sucursal + "-" + num_instalacion + " " + razon_social + "', " + id_cliente + ", '" + direccion_instalacion
                            + "', " + pk + ", true);";
                    if (st.executeUpdate(this.decodificarURI(sql), Statement.RETURN_GENERATED_KEYS) > 0) {
                        ResultSet rs1 = st.getGeneratedKeys();
                        if (rs1.next()) {
                            String pkBod = rs1.getString(1) != null ? rs1.getString(1) : "-1";
                            st.executeUpdate("insert into tbl_estanteria(id_bodega, estanteria) values (" + pkBod + ", 'EST-" + pk + "');");
                            rs1.close();
                        }
                    }
                    con.commit();
                } else {
                    con.rollback();
                }

            }
            st.close();
        } catch (Exception e) {
            this.setError(e.getMessage());
            e.printStackTrace();
            id_factura_id_Instalacion = "-1:-1";
            try {
                con.rollback();
            } catch (Exception se) {
                se.printStackTrace();
            }
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(Instalacion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return id_factura_id_Instalacion;
    }

    public String insertar(String id_contrato, String convenio_pago, String num_instalacion, String id_sucursal, String set_convenio_tarjeta, String set_convenio_cuenta, String id_provincia,
            String id_ciudad, String id_parroquia, String id_sector, String tipo_instalacion, String costo_instalacion, String direccion_instalacion, String ip, String ip_radio,
            String id_plan_contratado, String es_instalacion, String cobrar, String motivo_no_cobrar, String radusername, String radclave, String deviceclave,
            int id_sucursal_venta, int id_punto_emision, String id_cliente, String usuario, String serie_factura, String num_factura, String autorizacion,
            String ruc, String razon_social, String fecha_emision, String direccion, String telefono, String id_forma_pago, String forma_pago, String banco, String num_cheque,
            String num_comp_pago, String gastos_bancos, String id_plan_cuenta_banco, String son, String observacion, String subtotal, String subtotal_0, String subtotal_2,
            String subtotal_3, String descuento, String iva_2, String iva_3, String total, String paramArtic, String ret_num_serie, String ret_num_retencion,
            String ret_autorizacion, String ret_fecha_emision, String ret_ejercicio_fiscal_mes, String ret_ejercicio_fiscal, String ret_impuesto_retenido,
            String paramRet, String paramAsiento, String xmlFirmado, String tipo_cliente_instalacion, String factura_credito, String barrio, String nombreBoucher, 
            String es_canje, String fecha_fin_canje) {

        String id_factura_id_Instalacion = "-1:-1";
        fecha_fin_canje = fecha_fin_canje != null ? (fecha_fin_canje.compareTo("") != 0 ? "'" + fecha_fin_canje + "'" : "null") : "null";
        Connection con = this.getConexion();
        try {
            String num_orden_servicio = this.getNumOrdenServicio(id_sucursal_venta);
            con.setAutoCommit(false);
            Statement st = con.createStatement();
            ip_radio = ip_radio.compareTo("") != 0 ? "'" + ip_radio + "'" : "NULL";
            String estado_solicitud_no_cobrar = "0";
            String incluir_en_comision = "true";
            if( motivo_no_cobrar.compareTo("") != 0 ) {
                estado_solicitud_no_cobrar = "s";   //  solicitado
                incluir_en_comision = "false";
            }
            String sql = "INSERT INTO tbl_instalacion(num_instalacion, convenio_pago, num_orden_servicio, id_sucursal, id_cliente, id_provincia, id_ciudad, id_parroquia, id_sector, tipo_instalacion, costo_instalacion, direccion_instalacion, "
                    + "id_contrato, fecha_instalacion, ip, ip_radio, id_plan_contratado, id_plan_establecido, id_plan_actual, es_instalacion, cobrar, motivo_no_cobrar, estado_instalacion, radusername, radclave, deviceclave, alias, "
                    + "set_convenio_tarjeta, set_convenio_cuenta, tipo_cliente_instalacion,factura_credito, costo_instalacion_facturado, barrio, estado_solicitud_no_cobrar, incluir_en_comision, es_canje, fecha_fin_canje) "
                    + "VALUES(" + num_instalacion + ", '" + convenio_pago + "', " + num_orden_servicio + ", " + id_sucursal + ", " + id_cliente + ", " + id_provincia + ", " + id_ciudad + ", " + id_parroquia + ", " + id_sector + ", '" + tipo_instalacion + "', " + costo_instalacion + ", '" + direccion_instalacion + "', " + id_contrato
                    + ", NULL, '" + ip + "', " + ip_radio + ", " + id_plan_contratado + ", " + id_plan_contratado + ", " + id_plan_contratado + ", " + es_instalacion + ", " + cobrar + ", '" + motivo_no_cobrar + "', 'e', '" + radusername + "', '" + radclave + "', '" + deviceclave + "', '" + usuario + "', " + set_convenio_tarjeta
                    + ", " + set_convenio_cuenta + ", '" + tipo_cliente_instalacion + "'," + factura_credito + "," + total + ",'" + barrio + "', '"+estado_solicitud_no_cobrar+"', "+incluir_en_comision+", '"+es_canje+"', "+fecha_fin_canje+");";
            if (st.executeUpdate(this.decodificarURI(sql), Statement.RETURN_GENERATED_KEYS) > 0) {
                String pk = "-1";
                ResultSet rs = st.getGeneratedKeys();
                if (rs.next()) {
                    pk = rs.getString(1) != null ? rs.getString(1) : "-1";
                    rs.close();
                }

                if (set_convenio_cuenta.compareTo("true") == 0) {
                    String idRubro = "1";
                    String rubro = "COMERCIALIZACION";
                    String monto = "0.6";
                    try {
                        ResultSet res = this.consulta("SELECT * FROM tbl_rubro where lower(rubro) like '%comercializaci%' order by id_rubro");
                        if (res.next()) {
                            idRubro = res.getString("id_rubro") != null ? res.getString("id_rubro") : "1";
                            rubro = res.getString("rubro") != null ? res.getString("rubro") : "COMERCIALIZACION";
                            monto = res.getString("valor") != null ? res.getString("valor") : "0.6";
                            res.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    st.executeUpdate("insert into tbl_rubro_instalacion(id_rubro, id_instalacion, id_sucursal, monto) "
                            + "select " + idRubro + ", id_instalacion, id_sucursal, " + monto + " from tbl_instalacion where id_instalacion=" + pk
                            + " and id_instalacion not in (select id_instalacion from tbl_rubro_instalacion where id_rubro=" + idRubro + " and id_instalacion=" + pk + ");");
                    st.executeUpdate("insert into tbl_prefactura_rubro(id_sucursal, id_rubro, id_instalacion, rubro, periodo, monto) "
                            + "select I.id_sucursal, " + idRubro + ", I.id_instalacion, '" + rubro + "', P.periodo, " + monto
                            + " from tbl_instalacion as I inner join tbl_prefactura as P on P.id_instalacion=I.id_instalacion where fecha_emision is null and I.id_instalacion=" + pk + ";");
                    st.executeUpdate("update tbl_instalacion set factura_credito=true,set_convenio_cuenta=true where id_instalacion=" + pk + ";");
                }

                boolean ok = true;
                if (es_instalacion.compareTo("TRUE") == 0 && Double.parseDouble(total) > 0) {
                    ret_fecha_emision = ret_fecha_emision.compareTo("") != 0 ? "'" + ret_fecha_emision + "'" : "NULL";
                    ResultSet res = this.consulta("select facturaVenta(" + id_sucursal + ", " + id_punto_emision + ", " + id_cliente + ", '" + usuario + "', '" + serie_factura
                            + "', " + num_factura + ", '" + autorizacion + "', '" + ruc + "', '" + razon_social + "', '" + fecha_emision + "', '" + direccion
                            + "', '" + telefono + "', '" + id_forma_pago + "', '" + forma_pago + "', '" + banco + "', '" + num_cheque + "', '" + num_comp_pago + "', " + gastos_bancos
                            + ", " + id_plan_cuenta_banco + ", '" + son + "', '" + observacion + "', " + subtotal + ", " + subtotal_0 + ", " + subtotal_2 + ", " + subtotal_3 + ", " + descuento
                            + ", " + iva_2 + ", " + iva_3 + ", " + total + ", " + paramArtic + ", '" + ret_num_serie + "', '" + ret_num_retencion + "', '" + ret_autorizacion + "', " + ret_fecha_emision
                            + ", '" + ret_ejercicio_fiscal_mes + "', " + ret_ejercicio_fiscal + ", " + ret_impuesto_retenido + ", " + paramRet + ", " + paramAsiento + ", '" + xmlFirmado + "');"); // 40 param
                    String resFactVent = "-1";
                    if (res.next()) {
                        resFactVent = (res.getString(1) != null) ? res.getString(1) : "-1";
                        res.close();
                    }

                    if (resFactVent.compareTo("-1") != 0) {
                        id_factura_id_Instalacion = resFactVent + ":" + pk;
                        String vec[] = resFactVent.split(":");
                        
                        String boucher = (forma_pago.compareTo("j")==0 && nombreBoucher.compareTo("")!=0) ? ", nombre_boucher='"+nombreBoucher+"', conciliado_boucher=false" : "";
                        
                        st.executeUpdate("UPDATE tbl_instalacion SET id_factura_venta=" + vec[0] + " WHERE id_instalacion=" + pk + ";");
                        st.executeUpdate("UPDATE tbl_factura_venta SET id_instalacion=" + pk + ", ip='" + ip + "' "+boucher+" WHERE id_factura_venta=" + vec[0] + ";");

                    } else {
                        id_factura_id_Instalacion = "-1:-1";
                        ok = false;
                        this.ejecutar("delete from tbl_instalacion where id_instalacion=" + pk);
                    }
                } else {
                    id_factura_id_Instalacion = "-1:" + pk;
                }

                if (ok) {
                    sql = "insert into tbl_bodega(id_sucursal, bodega, id_responsable, ubicacion, id_instalacion, es_responsable_cliente) values("
                            + id_sucursal + ", 'INSTALACION No. " + id_sucursal + "-" + num_instalacion + " " + razon_social + "', " + id_cliente + ", '" + direccion_instalacion
                            + "', " + pk + ", true);";
                    if (st.executeUpdate(this.decodificarURI(sql), Statement.RETURN_GENERATED_KEYS) > 0) {
                        ResultSet rs1 = st.getGeneratedKeys();
                        if (rs1.next()) {
                            String pkBod = rs1.getString(1) != null ? rs1.getString(1) : "-1";
                            st.executeUpdate("insert into tbl_estanteria(id_bodega, estanteria) values (" + pkBod + ", 'EST-" + pk + "');");
                            rs1.close();
                        }
                    }
                    con.commit();
                } else {
                    con.rollback();
                }

            }
            st.close();
        } catch (Exception e) {
            this.setError(e.getMessage());
            e.printStackTrace();
            id_factura_id_Instalacion = "-1:-1";
            try {
                con.rollback();
            } catch (Exception se) {
                se.printStackTrace();
            }
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(Instalacion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return id_factura_id_Instalacion;
    }

    public boolean tieneDeuda(String id, String id_cliente, String estado) {
        if (estado.compareTo("a") == 0) {
            try {
                ResultSet res = this.consulta("SELECT * FROM vta_prefactura where getFechaSuspension(fecha_prefactura) <= now()::date and fecha_emision is null and id_instalacion=" + id + ";");
                if (this.getFilas(res) > 0) {
                    return true;
                } else {
                    res = this.consulta("SELECT * FROM vta_cliente where id_cliente=" + id_cliente + " and credito>0;");
                    if (this.getFilas(res) > 0) {
                        return true;
                    }
                }
                res.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean hayRadUserName(String id, String radUserName) {
        ResultSet res = this.consulta("SELECT * FROM tbl_instalacion where radusername='" + radUserName + "' and estado_servicio not in ('t','e') and anulado=false and id_instalacion<>" + id + ";");
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

    public boolean setRadusername(String id_instalacion, String radusername) {
        return this.ejecutar("update tbl_instalacion set radusername='" + radusername + "' where id_instalacion=" + id_instalacion);
    }

    public boolean actualizar(String id_instalacion, String id_sector) {
        return this.ejecutar("update tbl_instalacion set id_sector=" + id_sector + " where id_instalacion=" + id_instalacion);
    }

    public boolean setUbicaciones(String id_instalacion, String id_provincia, String id_ciudad, String id_parroquia, String id_sector) {
        return this.ejecutar("update tbl_instalacion set id_provincia=" + id_provincia + ", id_ciudad=" + id_ciudad + ", id_parroquia=" + id_parroquia + ", id_sector=" + id_sector + " where id_instalacion=" + id_instalacion);
    }

    public boolean actualizar(String id_instalacion, String id_provincia, String id_ciudad, String id_parroquia,
            String id_sector, String direccion_instalacion, String ip, String cobrar, String motivo_no_cobrar, String set_deviceclave,
            String tipo_instalacion, String ip_radio, String convenio_pago) {
        ip_radio = ip_radio.compareTo("") != 0 ? "'" + ip_radio + "'" : "null";
        return this.ejecutar("update tbl_instalacion set id_provincia=" + id_provincia + ", id_ciudad=" + id_ciudad
                + ", id_parroquia=" + id_parroquia + ", id_sector=" + id_sector + ", direccion_instalacion='" + direccion_instalacion
                + "', ip='" + ip + "', cobrar=" + cobrar + ", motivo_no_cobrar='" + motivo_no_cobrar + "', set_deviceclave=" + set_deviceclave
                + ", tipo_instalacion='" + tipo_instalacion + "', ip_radio=" + ip_radio + ", convenio_pago='" + convenio_pago
                + "' where id_instalacion=" + id_instalacion);
    }

    public boolean actualizar(String id_instalacion, String id_provincia, String id_ciudad, String id_parroquia,
            String id_sector, String direccion_instalacion, String ip, String cobrar, String motivo_no_cobrar, String set_deviceclave,
            String tipo_instalacion, String ip_radio, String convenio_pago, String tipo_servicio, String tipo_tecnologia, String id_provinciab,
            String id_cantonb, String id_parroquiab, String direccionb) {
        ip_radio = ip_radio.compareTo("") != 0 ? "'" + ip_radio + "'" : "null";
        return this.ejecutar("update tbl_instalacion set id_provincia=" + id_provincia + ", id_ciudad=" + id_ciudad
                + ", id_parroquia=" + id_parroquia + ", id_sector=" + id_sector + ", direccion_instalacion='" + direccion_instalacion
                + "', ip='" + ip + "', cobrar=" + cobrar + ", motivo_no_cobrar='" + motivo_no_cobrar + "', set_deviceclave=" + set_deviceclave
                + ", tipo_instalacion='" + tipo_instalacion + "', ip_radio=" + ip_radio + ", convenio_pago='" + convenio_pago + "',"
                + " tipo_servicio='" + tipo_servicio + "',tipo_tecnologia='" + tipo_tecnologia + "',id_provinciab=" + id_provinciab + ","
                + " id_cantonb=" + id_cantonb + ",id_parroquiab=" + id_parroquiab + ",direccionb='" + direccionb + "' where id_instalacion=" + id_instalacion);
    }

    public boolean actualizar(String id_instalacion, String id_sucursal, String id_provincia, String id_ciudad, String id_parroquia,
            String id_sector, String direccion_instalacion, String ip, String cobrar, String motivo_no_cobrar, String estado_servicio,
            String set_deviceclave, String tipo_instalacion, String ip_radio, String convenio_pago) {
        ip_radio = ip_radio.compareTo("") != 0 ? "'" + ip_radio + "'" : "null";
        return this.ejecutar("update tbl_instalacion set id_sucursal=" + id_sucursal + ", id_provincia=" + id_provincia + ", id_ciudad=" + id_ciudad
                + ", id_parroquia=" + id_parroquia + ", id_sector=" + id_sector + ", direccion_instalacion='" + direccion_instalacion + "', ip='" + ip
                + "', cobrar=" + cobrar + ", motivo_no_cobrar='" + motivo_no_cobrar + "', estado_servicio='" + estado_servicio
                + "', set_deviceclave=" + set_deviceclave + ", tipo_instalacion='" + tipo_instalacion + "', ip_radio=" + ip_radio + ", convenio_pago='" + convenio_pago + "' where id_instalacion=" + id_instalacion);
    }

    public boolean actualizar(String id_instalacion, String id_sucursal, String id_provincia, String id_ciudad, String id_parroquia,
            String id_sector, String direccion_instalacion, String ip, String cobrar, String motivo_no_cobrar, String estado_servicio,
            String set_deviceclave, String tipo_instalacion, String ip_radio, String convenio_pago, String tipo_servicio, String tipo_tecnologia, String id_provinciab,
            String id_cantonb, String id_parroquiab, String direccionb) {
        ip_radio = ip_radio.compareTo("") != 0 ? "'" + ip_radio + "'" : "null";
        return this.ejecutar("update tbl_instalacion set id_sucursal=" + id_sucursal + ", id_provincia=" + id_provincia + ", id_ciudad=" + id_ciudad
                + ", id_parroquia=" + id_parroquia + ", id_sector=" + id_sector + ", direccion_instalacion='" + direccion_instalacion + "', ip='" + ip
                + "', cobrar=" + cobrar + ", motivo_no_cobrar='" + motivo_no_cobrar + "', estado_servicio='" + estado_servicio
                + "', set_deviceclave=" + set_deviceclave + ", tipo_instalacion='" + tipo_instalacion + "', ip_radio=" + ip_radio + ", convenio_pago='" + convenio_pago + "',"
                + " tipo_servicio='" + tipo_servicio + "',tipo_tecnologia='" + tipo_tecnologia + "',id_provinciab=" + id_provinciab + ","
                + " id_cantonb=" + id_cantonb + ",id_parroquiab=" + id_parroquiab + ",direccionb='" + direccionb + "'  where id_instalacion=" + id_instalacion);
    }

    public boolean actualizar(String id_instalacion, String id_sucursal, String id_provincia, String id_ciudad, String id_parroquia,
            String id_sector, String direccion_instalacion, String ip, String cobrar, String motivo_no_cobrar, String estado_servicio,
            String set_deviceclave, String tipo_instalacion, String ip_radio, String convenio_pago, String id_plan_actual) {
        ip_radio = ip_radio.compareTo("") != 0 ? "'" + ip_radio + "'" : "null";
        return this.ejecutar("update tbl_instalacion set id_sucursal=" + id_sucursal + ", id_provincia=" + id_provincia + ", id_ciudad=" + id_ciudad
                + ", id_parroquia=" + id_parroquia + ", id_sector=" + id_sector + ", direccion_instalacion='" + direccion_instalacion + "', ip='" + ip
                + "', cobrar=" + cobrar + ", motivo_no_cobrar='" + motivo_no_cobrar + "', estado_servicio='" + estado_servicio
                + "', set_deviceclave=" + set_deviceclave + ", tipo_instalacion='" + tipo_instalacion + "', ip_radio=" + ip_radio + ", convenio_pago='" + convenio_pago
                + "', id_plan_contratado=" + id_plan_actual + ", id_plan_establecido=" + id_plan_actual + ", id_plan_actual=" + id_plan_actual + " where id_instalacion=" + id_instalacion);
    }

    public boolean actualizar(String id_instalacion, String id_sucursal, String id_provincia, String id_ciudad, String id_parroquia,
            String id_sector, String direccion_instalacion, String ip, String cobrar, String motivo_no_cobrar, String estado_servicio,
            String set_deviceclave, String tipo_instalacion, String ip_radio, String convenio_pago, String id_plan_actual, String tipo_servicio, String tipo_tecnologia, String id_provinciab,
            String id_cantonb, String id_parroquiab, String direccionb) {
        ip_radio = ip_radio.compareTo("") != 0 ? "'" + ip_radio + "'" : "null";
        return this.ejecutar("update tbl_instalacion set id_sucursal=" + id_sucursal + ", id_provincia=" + id_provincia + ", id_ciudad=" + id_ciudad
                + ", id_parroquia=" + id_parroquia + ", id_sector=" + id_sector + ", direccion_instalacion='" + direccion_instalacion + "', ip='" + ip
                + "', cobrar=" + cobrar + ", motivo_no_cobrar='" + motivo_no_cobrar + "', estado_servicio='" + estado_servicio
                + "', set_deviceclave=" + set_deviceclave + ", tipo_instalacion='" + tipo_instalacion + "', ip_radio=" + ip_radio + ", convenio_pago='" + convenio_pago
                + "', id_plan_contratado=" + id_plan_actual + ", id_plan_establecido=" + id_plan_actual + ", id_plan_actual=" + id_plan_actual + ","
                + " tipo_servicio='" + tipo_servicio + "',tipo_tecnologia='" + tipo_tecnologia + "',id_provinciab=" + id_provinciab + ","
                + " id_cantonb=" + id_cantonb + ",id_parroquiab=" + id_parroquiab + ",direccionb='" + direccionb + "' where id_instalacion=" + id_instalacion);
    }

    public boolean actualizar(String id_instalacion, String id_orden_trabajo, String id_sector, String direccion_instalacion, String ip, String mac_ant, String idsActivosRet,
            String receptor_ant, String mac_nuevo, String idsActivos, String receptor_nuevo, String porcentaje_senal, String antena_acoplada, String latitud, String longitud, String altura, String altura_antena) {
        String set = "";
        if (id_sector.compareTo("") != 0 && id_sector.compareTo("-0") != 0) {
            set += "id_sector=" + id_sector;
        }
        if (direccion_instalacion.compareTo("") != 0) {
            set += (set.compareTo("") != 0 ? "," : "") + "direccion_instalacion='" + direccion_instalacion + "'";
        }
        if (ip.compareTo("") != 0) {
            set += (set.compareTo("") != 0 ? "," : "") + "ip='" + ip + "'";
        }
        /*if(id_plan_actual.compareTo("")!=0 && id_plan_actual.compareTo("-0")!=0){
            set += (set.compareTo("")!=0?",":"") + "id_plan_actual="+id_plan_actual;
        }*/
        if (mac_nuevo.compareTo("") != 0) {
            set += (set.compareTo("") != 0 ? "," : "") + "mac='" + mac_nuevo + "'";
        }
        if (receptor_nuevo.compareTo("") != 0) {
            set += (set.compareTo("") != 0 ? "," : "") + "receptor='" + receptor_nuevo + "'";
        }
        if (porcentaje_senal.compareTo("") != 0) {
            set += (set.compareTo("") != 0 ? "," : "") + "porcentaje_senal=" + porcentaje_senal;
        }
        if (antena_acoplada.compareTo("") != 0) {
            set += (set.compareTo("") != 0 ? "," : "") + "antena_acoplada='" + antena_acoplada + "'";
        }
        if (latitud.compareTo("") != 0) {
            set += (set.compareTo("") != 0 ? "," : "") + "latitud='" + latitud + "'";
        }
        if (longitud.compareTo("") != 0) {
            set += (set.compareTo("") != 0 ? "," : "") + "longitud='" + longitud + "'";
        }
        if (altura.compareTo("") != 0) {
            set += (set.compareTo("") != 0 ? "," : "") + "altura='" + altura + "'";
        }
        if (altura_antena.compareTo("") != 0) {
            set += (set.compareTo("") != 0 ? "," : "") + "altura_antena='" + altura_antena + "'";
        }
        List sql = new ArrayList();
        sql.add("insert into tbl_ot_solucion(id_instalacion,id_orden_trabajo,porcentaje_senal_ant, porcentaje_senal_act, ip_ant, ip_act, mac_ant, mac_act, direccion_ant, direccion_act, receptor_ant, receptor_act, id_sector_ant, id_sector_act, antena_acoplada_ant, antena_acoplada_act, latitud_ant, latitud_act, longitud_ant, longitud_act, altura_ant, altura_act, altura_antena_ant, altura_antena_act) "
                + "select id_instalacion, " + id_orden_trabajo + ", porcentaje_senal, " + porcentaje_senal + ", ip, '" + ip + "', '" + mac_ant + "', '" + mac_nuevo + "', direccion_instalacion, '" + direccion_instalacion + "', '" + receptor_ant + "', '" + receptor_nuevo + "', id_sector, '" + id_sector + "', antena_acoplada, '" + antena_acoplada + "', latitud, '" + latitud + "', longitud, '" + longitud + "', altura, '" + altura + "', altura_antena, '" + altura_antena + "' from tbl_instalacion where id_instalacion=" + id_instalacion + ";");
        if (idsActivosRet.compareTo("") != 0) {
            String codigo_ant[] = idsActivosRet.split(",");
            for (int i = 0; i < codigo_ant.length; i++) {
                sql.add("DELETE FROM tbl_instalacion_activo WHERE id_activo=" + codigo_ant[i] + ";");
            }
            String codigo[] = idsActivos.split(",");
            for (int i = 0; i < codigo.length; i++) {
                sql.add("insert into tbl_instalacion_activo(id_instalacion, id_activo) values(" + id_instalacion + ", " + codigo[i] + ");");
            }
        }
        if (set.compareTo("") != 0) {
            sql.add("update tbl_instalacion set " + set + " where id_instalacion=" + id_instalacion + ";");
        }
        return this.transacciones(sql);
    }

    public boolean setactualizar(String id_instalacion, String id_orden_trabajo, String id_sector, String direccion_instalacion, String ip, String mac_ant, String idsActivosRet,
            String receptor_ant, String mac_nuevo, String idsActivos, String receptor_nuevo, String porcentaje_senal, String antena_acoplada, String latitud, String longitud, String altura, String altura_antena, String latitud_gps, String longitud_gps) {
        String set = "";
        if (id_sector.compareTo("") != 0 && id_sector.compareTo("-0") != 0) {
            set += "id_sector=" + id_sector;
        }
        if (direccion_instalacion.compareTo("") != 0) {
            set += (set.compareTo("") != 0 ? "," : "") + "direccion_instalacion='" + direccion_instalacion + "'";
        }
        if (ip.compareTo("") != 0) {
            set += (set.compareTo("") != 0 ? "," : "") + "ip='" + ip + "'";
        }
        /*if(id_plan_actual.compareTo("")!=0 && id_plan_actual.compareTo("-0")!=0){
            set += (set.compareTo("")!=0?",":"") + "id_plan_actual="+id_plan_actual;
        }*/
        if (mac_nuevo.compareTo("") != 0) {
            set += (set.compareTo("") != 0 ? "," : "") + "mac='" + mac_nuevo + "'";
        }
        if (receptor_nuevo.compareTo("") != 0) {
            set += (set.compareTo("") != 0 ? "," : "") + "receptor='" + receptor_nuevo + "'";
        }
        if (porcentaje_senal.compareTo("") != 0) {
            set += (set.compareTo("") != 0 ? "," : "") + "porcentaje_senal=" + porcentaje_senal;
        }
        if (antena_acoplada.compareTo("") != 0) {
            set += (set.compareTo("") != 0 ? "," : "") + "antena_acoplada='" + antena_acoplada + "'";
        }
        if (latitud.compareTo("") != 0) {
            set += (set.compareTo("") != 0 ? "," : "") + "latitud='" + latitud + "'";
        }
        if (longitud.compareTo("") != 0) {
            set += (set.compareTo("") != 0 ? "," : "") + "longitud='" + longitud + "'";
        }
        if (altura.compareTo("") != 0) {
            set += (set.compareTo("") != 0 ? "," : "") + "altura='" + altura + "'";
        }
        if (altura_antena.compareTo("") != 0) {
            set += (set.compareTo("") != 0 ? "," : "") + "altura_antena='" + altura_antena + "'";
        }
        if (latitud_gps.compareTo("") != 0) {
            set += (set.compareTo("") != 0 ? "," : "") + "latitud_gps='" + latitud_gps + "'";
        }
        if (longitud_gps.compareTo("") != 0) {
            set += (set.compareTo("") != 0 ? "," : "") + "longitud_gps='" + longitud_gps + "'";
        }
        List sql = new ArrayList();
        sql.add("insert into tbl_ot_solucion(id_instalacion,id_orden_trabajo,porcentaje_senal_ant, porcentaje_senal_act, ip_ant, ip_act, mac_ant, mac_act, direccion_ant, direccion_act, receptor_ant, receptor_act, id_sector_ant, id_sector_act, antena_acoplada_ant, antena_acoplada_act, latitud_ant, latitud_act, longitud_ant, longitud_act, altura_ant, altura_act, altura_antena_ant, altura_antena_act) "
                + "select id_instalacion, " + id_orden_trabajo + ", porcentaje_senal, " + porcentaje_senal + ", ip, '" + ip + "', '" + mac_ant + "', '" + mac_nuevo + "', direccion_instalacion, '" + direccion_instalacion + "', '" + receptor_ant + "', '" + receptor_nuevo + "', id_sector, '" + id_sector + "', antena_acoplada, '" + antena_acoplada + "', latitud, '" + latitud + "', longitud, '" + longitud + "', altura, '" + altura + "', altura_antena, '" + altura_antena + "' from tbl_instalacion where id_instalacion=" + id_instalacion + ";");
        if (idsActivosRet != null) {
            if (idsActivosRet.compareTo("") != 0) {
                String codigo_ant[] = idsActivosRet.split(",");
                for (int i = 0; i < codigo_ant.length; i++) {
                    sql.add("DELETE FROM tbl_instalacion_activo WHERE id_activo=" + codigo_ant[i] + ";");
                }
                if(idsActivos.compareTo("")!=0) {
                    String codigo[] = idsActivos.split(",");
                    for (int i = 0; i < codigo.length; i++) {
                        sql.add("insert into tbl_instalacion_activo(id_instalacion, id_activo) values(" + id_instalacion + ", " + codigo[i] + ");");
                    }
                }
            }
        }
        if (set.compareTo("") != 0) {
            sql.add("update tbl_instalacion set " + set + " where id_instalacion=" + id_instalacion + ";");
        }
        return this.transacciones(sql);
    }

    public boolean actualizar(String id_instalacion, String id_sector, String direccion_instalacion, String ip, String id_plan_establecido, String mac,
            String receptor, String porcentaje_senal, String antena_acoplada, String latitud, String longitud, String altura, String altura_antena,
            String fecha_instalacion, String conformidad_velocidad, String conformidad_instalacion, String conformidad_atencion, String estado_instalacion) {
        return this.ejecutar("update tbl_instalacion set id_sector=" + id_sector + ", direccion_instalacion='" + direccion_instalacion + "', ip='" + ip + "', id_plan_establecido=" + id_plan_establecido + ", id_plan_actual=" + id_plan_establecido + ", mac='" + mac
                + "', receptor='" + receptor + "', porcentaje_senal=" + porcentaje_senal + ", antena_acoplada='" + antena_acoplada + "', latitud='" + latitud + "', longitud='" + longitud + "', altura='" + altura
                + "', altura_antena='" + altura_antena + "', fecha_instalacion='" + fecha_instalacion + "', conformidad_velocidad=" + conformidad_velocidad + ", conformidad_instalacion='" + conformidad_instalacion + "', conformidad_atencion='" + conformidad_atencion
                + "', estado_instalacion='" + estado_instalacion + "' where id_instalacion=" + id_instalacion);

        /*return this.ejecutar("update tbl_instalacion set id_sector="+id_sector+", direccion_instalacion='"+direccion_instalacion+"', ip='"+ip+"', id_plan_establecido="+id_plan_establecido+", id_plan_actual="+id_plan_establecido+", mac='"+mac+
                "', receptor='"+receptor+"', porcentaje_senal="+porcentaje_senal+", antena_acoplada='"+antena_acoplada+"', latitud='"+latitud+"', longitud='"+longitud+"', altura='"+altura+
                "', altura_antena='"+altura_antena+"', fecha_instalacion='"+fecha_instalacion+"', conformidad_velocidad="+conformidad_velocidad+", conformidad_instalacion='"+conformidad_instalacion+"', conformidad_atencion='"+conformidad_atencion+
                "', estado_instalacion='"+estado_instalacion+"' where id_instalacion="+id_instalacion);*/
    }

    ///2019-08-05
    public boolean actualizar(String id_instalacion, String id_sucursal, String id_provincia, String id_ciudad, String id_parroquia,
            String id_sector, String direccion_instalacion, String ip, String cobrar, String motivo_no_cobrar, String estado_servicio,
            String set_deviceclave, String tipo_instalacion, String ip_radio, String convenio_pago, String id_plan_actual, 
            boolean factura_credito, String barrio,String es_canje, String fecha_fin_canje) 
    {
        ip_radio = ip_radio.compareTo("") != 0 ? "'" + ip_radio + "'" : "null";
        try {
            fecha_fin_canje = fecha_fin_canje != null ? (fecha_fin_canje.compareTo("") != 0 ? "'" + fecha_fin_canje + "'" : "null") : "null";
        } catch(Exception e) {
            e.printStackTrace();
        }
        return this.ejecutar("update tbl_instalacion set id_sucursal=" + id_sucursal + ", id_provincia=" + id_provincia + ", id_ciudad=" + id_ciudad
                + ", id_parroquia=" + id_parroquia + ", id_sector=" + id_sector + ", direccion_instalacion='" + direccion_instalacion + "', ip='" + ip
                + "', cobrar=" + cobrar + ", motivo_no_cobrar='" + motivo_no_cobrar + "', estado_servicio='" + estado_servicio
                + "', set_deviceclave=" + set_deviceclave + ", tipo_instalacion='" + tipo_instalacion + "', ip_radio=" + ip_radio + ", convenio_pago='" + convenio_pago
                + "', id_plan_contratado=" + id_plan_actual + ", id_plan_establecido=" + id_plan_actual + ", id_plan_actual=" + id_plan_actual + ",factura_credito=" + factura_credito 
                + ",barrio='" + barrio + "',es_canje='" + es_canje + "', fecha_fin_canje="+fecha_fin_canje+" where id_instalacion=" + id_instalacion);
    }

    public boolean actualizar(String id_instalacion, String id_sucursal, String id_provincia, String id_ciudad, String id_parroquia,
            String id_sector, String direccion_instalacion, String ip, String cobrar, String motivo_no_cobrar, String estado_servicio,
            String set_deviceclave, String tipo_instalacion, String ip_radio, String convenio_pago, boolean factura_credito, String barrio,
            String es_canje, String fecha_fin_canje) {
        ip_radio = ip_radio.compareTo("") != 0 ? "'" + ip_radio + "'" : "null";
        fecha_fin_canje = fecha_fin_canje != null ? (fecha_fin_canje.compareTo("") != 0 ? "'" + fecha_fin_canje + "'" : "null") : "null";
        return this.ejecutar("update tbl_instalacion set id_sucursal=" + id_sucursal + ", id_provincia=" + id_provincia + ", id_ciudad=" + id_ciudad
                + ", id_parroquia=" + id_parroquia + ", id_sector=" + id_sector + ", direccion_instalacion='" + direccion_instalacion + "', ip='" + ip
                + "', cobrar=" + cobrar + ", motivo_no_cobrar='" + motivo_no_cobrar + "', estado_servicio='" + estado_servicio
                + "', set_deviceclave=" + set_deviceclave + ", tipo_instalacion='" + tipo_instalacion + "', ip_radio=" + ip_radio + ", convenio_pago='" + convenio_pago 
                + "',factura_credito=" + factura_credito + ",barrio='" + barrio + "',es_canje='" + es_canje + "', fecha_fin_canje="+fecha_fin_canje+" where id_instalacion=" + id_instalacion);
    }

    public boolean actualizar(String id_instalacion, String id_provincia, String id_ciudad, String id_parroquia,
            String id_sector, String direccion_instalacion, String ip, String cobrar, String motivo_no_cobrar, String set_deviceclave,
            String tipo_instalacion, String ip_radio, String convenio_pago, boolean factura_credito, String barrio,String es_canje, String fecha_fin_canje) {
        ip_radio = ip_radio.compareTo("") != 0 ? "'" + ip_radio + "'" : "null";
        fecha_fin_canje = fecha_fin_canje != null ? (fecha_fin_canje.compareTo("") != 0 ? "'" + fecha_fin_canje + "'" : "null") : "null";
        return this.ejecutar("update tbl_instalacion set id_provincia=" + id_provincia + ", id_ciudad=" + id_ciudad
                + ", id_parroquia=" + id_parroquia + ", id_sector=" + id_sector + ", direccion_instalacion='" + direccion_instalacion
                + "', ip='" + ip + "', cobrar=" + cobrar + ", motivo_no_cobrar='" + motivo_no_cobrar + "', set_deviceclave=" + set_deviceclave
                + ", tipo_instalacion='" + tipo_instalacion + "', ip_radio=" + ip_radio + ", convenio_pago='" + convenio_pago + "'"
                + ", factura_credito=" + factura_credito + ",barrio='" + barrio + "',es_canje='" + es_canje + "', fecha_fin_canje="+fecha_fin_canje+" where id_instalacion=" + id_instalacion);
    }

    public boolean actualizar(String id_instalacion, String id_sucursal, String id_provincia, String id_ciudad, String id_parroquia,
            String id_sector, String direccion_instalacion, String ip, String cobrar, String motivo_no_cobrar, String estado_servicio,
            String set_deviceclave, String tipo_instalacion, String ip_radio, String convenio_pago, String id_plan_actual, String tipo_servicio, String tipo_tecnologia, String id_provinciab,
            String id_cantonb, String id_parroquiab, String direccionb, boolean factura_credito) {
        ip_radio = ip_radio.compareTo("") != 0 ? "'" + ip_radio + "'" : "null";
        return this.ejecutar("update tbl_instalacion set id_sucursal=" + id_sucursal + ", id_provincia=" + id_provincia + ", id_ciudad=" + id_ciudad
                + ", id_parroquia=" + id_parroquia + ", id_sector=" + id_sector + ", direccion_instalacion='" + direccion_instalacion + "', ip='" + ip
                + "', cobrar=" + cobrar + ", motivo_no_cobrar='" + motivo_no_cobrar + "', estado_servicio='" + estado_servicio
                + "', set_deviceclave=" + set_deviceclave + ", tipo_instalacion='" + tipo_instalacion + "', ip_radio=" + ip_radio + ", convenio_pago='" + convenio_pago
                + "', id_plan_contratado=" + id_plan_actual + ", id_plan_establecido=" + id_plan_actual + ", id_plan_actual=" + id_plan_actual + ","
                + " tipo_servicio='" + tipo_servicio + "',tipo_tecnologia='" + tipo_tecnologia + "',id_provinciab=" + id_provinciab + ","
                + " id_cantonb=" + id_cantonb + ",id_parroquiab=" + id_parroquiab + ",direccionb='" + direccionb + "',factura_credito=" + factura_credito + " where id_instalacion=" + id_instalacion);
    }

    public boolean actualizar(String id_instalacion, String id_sucursal, String id_provincia, String id_ciudad, String id_parroquia,
            String id_sector, String direccion_instalacion, String ip, String cobrar, String motivo_no_cobrar, String estado_servicio,
            String set_deviceclave, String tipo_instalacion, String ip_radio, String convenio_pago, String tipo_servicio, String tipo_tecnologia, String id_provinciab,
            String id_cantonb, String id_parroquiab, String direccionb, boolean factura_credito) {
        ip_radio = ip_radio.compareTo("") != 0 ? "'" + ip_radio + "'" : "null";
        return this.ejecutar("update tbl_instalacion set id_sucursal=" + id_sucursal + ", id_provincia=" + id_provincia + ", id_ciudad=" + id_ciudad
                + ", id_parroquia=" + id_parroquia + ", id_sector=" + id_sector + ", direccion_instalacion='" + direccion_instalacion + "', ip='" + ip
                + "', cobrar=" + cobrar + ", motivo_no_cobrar='" + motivo_no_cobrar + "', estado_servicio='" + estado_servicio
                + "', set_deviceclave=" + set_deviceclave + ", tipo_instalacion='" + tipo_instalacion + "', ip_radio=" + ip_radio + ", convenio_pago='" + convenio_pago + "',"
                + " tipo_servicio='" + tipo_servicio + "',tipo_tecnologia='" + tipo_tecnologia + "',id_provinciab=" + id_provinciab + ","
                + " id_cantonb=" + id_cantonb + ",id_parroquiab=" + id_parroquiab + ",direccionb='" + direccionb + "',factura_credito=" + factura_credito + "  where id_instalacion=" + id_instalacion);
    }

    public boolean actualizar(String id_instalacion, String id_provincia, String id_ciudad, String id_parroquia,
            String id_sector, String direccion_instalacion, String ip, String cobrar, String motivo_no_cobrar, String set_deviceclave,
            String tipo_instalacion, String ip_radio, String convenio_pago, String tipo_servicio, String tipo_tecnologia, String id_provinciab,
            String id_cantonb, String id_parroquiab, String direccionb, boolean factura_credito) {
        ip_radio = ip_radio.compareTo("") != 0 ? "'" + ip_radio + "'" : "null";
        return this.ejecutar("update tbl_instalacion set id_provincia=" + id_provincia + ", id_ciudad=" + id_ciudad
                + ", id_parroquia=" + id_parroquia + ", id_sector=" + id_sector + ", direccion_instalacion='" + direccion_instalacion
                + "', ip='" + ip + "', cobrar=" + cobrar + ", motivo_no_cobrar='" + motivo_no_cobrar + "', set_deviceclave=" + set_deviceclave
                + ", tipo_instalacion='" + tipo_instalacion + "', ip_radio=" + ip_radio + ", convenio_pago='" + convenio_pago + "',"
                + " tipo_servicio='" + tipo_servicio + "',tipo_tecnologia='" + tipo_tecnologia + "',id_provinciab=" + id_provinciab + ","
                + " id_cantonb=" + id_cantonb + ",id_parroquiab=" + id_parroquiab + ",direccionb='" + direccionb + "',factura_credito=" + factura_credito + " where id_instalacion=" + id_instalacion);
    }

    ///2019-08-05
    public boolean setEquipo(String id_instalacion, String mac, String receptor) {
        return this.ejecutar("update tbl_instalacion set mac='" + mac + "', receptor='" + receptor + "' where id_instalacion=" + id_instalacion);
    }

    public boolean anular(String id) {
        boolean ok = false;
        try {
            ResultSet res = this.consulta("select proc_instalacionAnular(" + id + ");");
            if (res.next()) {
                ok = (res.getString(1) != null) ? res.getBoolean(1) : false;
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok;
    }

    public void actualizarEstadoInstalacion(String idInstalacion) 
    {
        this.consulta("select proc_robot("+idInstalacion+");");
    }

    /* POST INSTALACION */
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

    public String concatenarValores(String id_articulos, String cantidades, String precios_unitarios,
            String subtotales) {
        String param = "";
        String vecArti[] = id_articulos.split(",");
        String vecCant[] = cantidades.split(",");
        String vecPU[] = precios_unitarios.split(",");
        String vecSubt[] = subtotales.split(",");
        for (int i = 0; i < vecArti.length; i++) {
            param += "['" + vecArti[i] + "','" + vecCant[i] + "','" + vecPU[i] + "','" + vecSubt[i] + "'],";
        }
        param = param.substring(0, param.length() - 1);
        return "array[" + param + "]";
    }

    public boolean insertarPostInstalacion(String id_instalacion, int id_sucursal, String fecha_instalacion, String receptor, String mac, String codsMateriales,
            String id_personalizacion, String porcentaje_senal, String antena_acoplada, String tipo_instalacion, String id_plan_establecido,
            String conformidad_velocidad, String conformidad_instalacion, String conformidad_atencion, String estado_instalacion,
            //float costo_materiales, String id_productos, String cantidades, String precios_unitarios, String subtotales,
            String latitud, String longitud, String altura, String altura_antena, String latitud_gps, String longitud_gps) {
        /*String num = "-1";
        try{
            String paramProductos = this.concatenarValores(id_productos, cantidades, precios_unitarios, subtotales);
            ResultSet res = this.consulta("select proc_postInstalacion("+id_instalacion+", "+id_sucursal+", '"+fecha_instalacion+"', '"+receptor+"', '"+mac+
                    "', "+id_personalizacion+", "+porcentaje_senal+", '"+antena_acoplada+"', "+id_plan_establecido+", '"+conformidad_velocidad+"', '"+
                    conformidad_instalacion+"', '"+conformidad_atencion+"', '"+estado_instalacion+"', "+costo_materiales+", "+paramProductos+
                    ", '"+latitud+"', '"+longitud+"', '"+altura+"', '"+altura_antena+"');");
            if(res.next()){
                num = (res.getString(1)!=null) ? res.getString(1) : "-1";
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return num;*/
        List sql = new ArrayList();
        String tipoInstalacion = tipo_instalacion.compareTo("-0")!=0 ? ", tipo_instalacion='"+tipo_instalacion+"'" : "";
        String planes = id_plan_establecido.compareTo("-0")!=0 ? ",id_plan_establecido="+id_plan_establecido+",id_plan_actual="+id_plan_establecido : "";
//        String planes = "";
        receptor = (receptor.length() > 39 ? receptor.substring(0, 39) : receptor);
        sql.add("update tbl_instalacion set fecha_instalacion='" + fecha_instalacion + "', receptor='" + receptor + "', mac='" + mac + "', id_activo_personalizacion=" + id_personalizacion + ","
                + "porcentaje_senal=" + porcentaje_senal + ", antena_acoplada='" + antena_acoplada + "' "+tipoInstalacion+" " + planes
//                + ",conformidad_velocidad=" + conformidad_velocidad + ", conformidad_instalacion='" + conformidad_instalacion + "', conformidad_atencion='" + conformidad_atencion + "'"
                + ", estado_instalacion='" + estado_instalacion + "', ingreso_datos=now()::date, estado_servicio='a', latitud='" + latitud + "', longitud='" + longitud + "',"
                + "altura='" + altura + "', altura_antena='" + altura_antena + "',latitud_gps='" + latitud_gps + "',longitud_gps='" + longitud_gps + "' where id_instalacion=" + id_instalacion);
        try {
            int convenioPago = 0;
            ResultSet rsInst = this.consulta("select convenio_pago from tbl_instalacion where id_instalacion=" + id_instalacion);
            if(rsInst.next()){
                convenioPago = rsInst.getString("convenio_pago")!=null ? rsInst.getInt("convenio_pago") : 0;
            }
            
            int anioActual = Fecha.getAnio();
            int mesActual = Fecha.getMes();
            int anioInst = Fecha.datePart("anio", fecha_instalacion);
            int mesInst = Fecha.datePart("mes", fecha_instalacion);
            int numDias = Fecha.getUltimoDiaMes(anioInst, mesInst) - Fecha.datePart("dia", fecha_instalacion);
            if (anioInst != anioActual) {
                mesInst = 0;
            }
            if( convenioPago==1 ) {  //  modalidad postpago
                if (mesInst < mesActual) { // si los meses son iguales se carga en la generación de la prefactura
                    sql.add("insert into tbl_prefactura_dias_conexion(id_instalacion, periodo, num_dias) values(" + id_instalacion + ", (select date_trunc('month', ('" + fecha_instalacion + "'::date + '1 month'::interval)::date )::date), " + numDias + ");");
                }
            }else{  //  modalidad prepago
                if (mesInst < mesActual) { // si los meses son iguales se carga en la generación de la prefactura
                    sql.add("insert into tbl_prefactura_dias_conexion(id_instalacion, periodo, num_dias) values(" + id_instalacion + ", (select date_trunc('month', ('" + fecha_instalacion + "'::date + '2 month'::interval)::date )::date), " + numDias + ");");
                }
            }
        } catch (Exception e) {
            System.out.println("error al producir formateo de ruta");
        }
        /*String codigo[] = codsMateriales.split(",");
        for(int i=0; i<codigo.length; i++){
            if(codigo[i].compareTo("")!=0){
                sql.add("insert into tbl_instalacion_activo(id_instalacion, id_activo) values("+id_instalacion+", "+codigo[i]+");");
            }
        }*/
        return this.transacciones(sql);
    }

    public boolean actualizarPostInstalacion(String id_instalacion, String fecha_instalacion, String receptor, String mac,
            String id_personalizacion, String porcentaje_senal, String antena_acoplada, String id_plan_establecido,
            String conformidad_velocidad, String conformidad_instalacion, String conformidad_atencion, String estado_instalacion,
            //float costo_materiales, String id_productos, String cantidades, String precios_unitarios, String subtotales,
            String latitud, String longitud, String altura, String altura_antena, String latitud_gps, String longitud_gps) {
        /*boolean ok = false;
        try{
            String paramProductos = this.concatenarValores(id_productos, cantidades, precios_unitarios, subtotales);
            ResultSet res = this.consulta("select proc_editarPostInstalacion("+id_instalacion+", '"+fecha_instalacion+"', '"+receptor+"', '"+mac+
                    "', "+id_personalizacion+", "+porcentaje_senal+", '"+antena_acoplada+"', "+id_plan_establecido+", '"+conformidad_velocidad+"', '"+
                    conformidad_instalacion+"', '"+conformidad_atencion+"', '"+estado_instalacion+"', "+costo_materiales+", "+paramProductos+
                    ", '"+latitud+"', '"+longitud+"', '"+altura+"', '"+altura_antena+"');");
            if(res.next()){
                ok = (res.getString(1)!=null) ? res.getBoolean(1) : false;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ok;*/
        return this.ejecutar("update tbl_instalacion set receptor='" + receptor + "', mac='" + mac + "', id_activo_personalizacion=" + id_personalizacion + ","
                + "porcentaje_senal=" + porcentaje_senal + ", antena_acoplada='" + antena_acoplada + "',"
                + "conformidad_velocidad=" + conformidad_velocidad + ", conformidad_instalacion='" + conformidad_instalacion + "',"
                + "conformidad_atencion='" + conformidad_atencion + "', estado_instalacion='" + estado_instalacion + "', "
                + "latitud='" + latitud + "', longitud='" + longitud + "',"
                + "altura='" + altura + "', altura_antena='" + altura_antena + "',latitud_gps='" + latitud_gps + "',longitud_gps='" + longitud_gps + "' where id_instalacion=" + id_instalacion);
    }

    public boolean setAntenaAcoplada(String id, String antena) {
        return this.ejecutar("update tbl_instalacion set antena_acoplada='" + antena + "' where id_instalacion=" + id);
    }

    public boolean setConvenioTarjeta(String id, String setConvenio) {
        return this.ejecutar("update tbl_instalacion set set_convenio_tarjeta=" + setConvenio + ",factura_credito=" + setConvenio + " where id_instalacion=" + id);
    }
    
    public boolean setSuscripcionTVGOMAX(String id) {
        String fecha = Fecha.getFecha("ISO");
        try {
            ResultSet rs = this.consulta("select case when (fecha_suscripcion + '1 month'::interval - '1 day'::interval)::date >= now()::date then (fecha_suscripcion + '1 month'::interval)::date else now()::date end from tbl_instalacion where id_instalacion="+id);
            if (rs.next()) {
                fecha = rs.getString(1) != null ? rs.getString(1) : fecha;
                rs.close();
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.ejecutar("update tbl_instalacion set fecha_suscripcion='"+fecha+"' where id_instalacion=" + id);
    }

    public boolean setConvenioCuenta(String id, String setConvenio) {

        List sql = new ArrayList();
        boolean ok = false;

        if (setConvenio.compareTo("true") == 0) {
            String idRubro = "1";
            String rubro = "COMERCIALIZACION";
            String monto = "0.6";
            try {
                ResultSet res = this.consulta("SELECT * FROM tbl_rubro where lower(rubro) like '%comercializaci%' order by id_rubro");
                if (res.next()) {
                    idRubro = res.getString("id_rubro") != null ? res.getString("id_rubro") : "1";
                    rubro = res.getString("rubro") != null ? res.getString("rubro") : "COMERCIALIZACION";
                    monto = res.getString("valor") != null ? res.getString("valor") : "0.6";
                    res.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            sql.add("insert into tbl_rubro_instalacion(id_rubro, id_instalacion, id_sucursal, monto) "
                    + "select " + idRubro + ", id_instalacion, id_sucursal, " + monto + " from tbl_instalacion where id_instalacion=" + id
                    + " and id_instalacion not in (select id_instalacion from tbl_rubro_instalacion where id_rubro=" + idRubro + " and id_instalacion=" + id + ");");
            sql.add("insert into tbl_prefactura_rubro(id_sucursal, id_rubro, id_instalacion, rubro, periodo, monto) "
                    + "select I.id_sucursal, " + idRubro + ", I.id_instalacion, '" + rubro + "', P.periodo, " + monto
                    + " from tbl_instalacion as I inner join tbl_prefactura as P on P.id_instalacion=I.id_instalacion where fecha_emision is null and I.id_instalacion=" + id + ";");
            sql.add("update tbl_instalacion set factura_credito=true,set_convenio_cuenta=true where id_instalacion=" + id + ";");

        } else if (setConvenio.compareTo("false") == 0) {

            sql.add("delete from tbl_prefactura_rubro where id_rubro=1 and id_instalacion=" + id + " and periodo in (select periodo from vta_prefactura where id_instalacion=" + id + ");");
            sql.add("delete from tbl_rubro_instalacion where id_rubro=1 and id_instalacion=" + id + ";");
            sql.add("update tbl_instalacion set factura_credito=false,set_convenio_cuenta=false where id_instalacion=" + id + ";");

        }
        sql.add("update tbl_prefactura set recalcular=true where fecha_emision is null and id_instalacion=" + id + ";");
        ok = this.transacciones(sql);
        return ok;
//        return this.ejecutar("update tbl_instalacion set set_convenio_cuenta=" + setConvenio + ",factura_credito=" + setConvenio + " where id_instalacion=" + id);
    }

    public boolean setConvenioTarjetaTodosServicios(String idCliente, String setConvenio) {
        return this.ejecutar("update tbl_instalacion set set_convenio_tarjeta=" + setConvenio + ",factura_credito=" + setConvenio + " where id_cliente=" + idCliente);
    }

    public boolean setAntenaMacReceptor(String id, String antena, String mac, String receptor) {
        return this.ejecutar("update tbl_instalacion set antena_acoplada='" + antena + "', mac='" + mac + "', receptor='" + receptor + "' where id_instalacion=" + id);
    }

    /*  CAMBIOS DE RAZON SOCIAL  */
    public ResultSet getCambioCliente(String id) {
        return this.consulta("SELECT * FROM vta_instalacion_cambio_cliente where id_instalacion_cliente=" + id + ";");
    }

    public ResultSet getCambioClientes(String id) {
        return this.consulta("SELECT * FROM vta_instalacion_cambio_cliente where id_instalacion=" + id + ";");
    }

    /*public boolean enConflictoCambioCliente(String id_cambio_cliente, String id_instalacion, String fecha_cambio)
    {
        ResultSet res = this.consulta("SELECT * FROM tbl_instalacion_cliente where '"+fecha_cambio+"' between fecha and (fecha + (1 month)::interval)::date "
                + "and eliminado=false and id_instalacion="+id_instalacion+" and id_instalacion_cliente<>"+id_cambio_cliente+";");
        if(this.getFilas(res)>0){
            return true;
        }
        try{
            res.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }*/
    public String insertarCambioCliente(String id_instalacion, String id_cliente, String fecha_cambio, String radusername, String usuario) {
        String id = "-1";
        try {
            ResultSet res = this.consulta("SELECT id_cliente, id_contrato, id_sucursal, num_instalacion, razon_social, direccion_instalacion FROM vta_instalacion where id_instalacion=" + id_instalacion + "");
            if (res.next()) {
                String id_cliente_actual = res.getString("id_cliente") != null ? res.getString("id_cliente") : "";
                String id_contrato = res.getString("id_contrato") != null ? res.getString("id_contrato") : "";
                String id_sucursal = res.getString("id_sucursal") != null ? res.getString("id_sucursal") : "";
                String num_instalacion = res.getString("num_instalacion") != null ? res.getString("num_instalacion") : "";
                String razon_social = res.getString("razon_social") != null ? res.getString("razon_social") : "";
                String direccion_instalacion = res.getString("direccion_instalacion") != null ? res.getString("direccion_instalacion") : "";
                if (id_cliente_actual.compareTo("") != 0) {

                    id = this.insert("insert into tbl_instalacion_cliente(id_instalacion, id_cliente, fecha, usuario) values(" + id_instalacion + ", " + id_cliente_actual + ", '" + fecha_cambio + "', '" + usuario + "');");
                    if (id.compareTo("-1") != 0) {
                        if (!this.ejecutar("update tbl_instalacion set id_cliente=" + id_cliente + ", radusername='" + radusername + "' where id_instalacion=" + id_instalacion)) {
                            this.ejecutar("delete from tbl_instalacion_cliente where id_instalacion_cliente=" + id);
                        } else {
                            this.ejecutar("update tbl_bodega set bodega='INSTALACION No. " + id_sucursal + "-" + num_instalacion + " " + razon_social + "', id_responsable=" + id_cliente + ", ubicacion='" + direccion_instalacion + "' where id_instalacion=" + id_instalacion);
                        }
                    }

                    try {
                        ResultSet rs = this.consulta("select * from tbl_instalacion where id_cliente=" + id_cliente_actual + " and estado_servicio not in ('r','t') and anulado=false");
                        if (this.getFilas(rs) == 0) {
                            this.ejecutar("update tbl_contrato set terminado=true where id_contrato=" + id_contrato);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    /*public boolean actualizarCambioCliente(String id_instalacion, String id_cliente)
    {
        return this.ejecutar("update tbl_instalacion set id_cliente="+id_cliente+" where id_instalacion="+id_instalacion+";");
    }*/
 /*  CAMBIOS DE PLAN  */
    public ResultSet getCambioPlan(String id) {
        return this.consulta("SELECT * FROM vta_instalacion_cambio_plan where id_instalacion_cambio_plan=" + id + ";");
    }

    public ResultSet getCambioPlanes(String id) {
        return this.consulta("SELECT * FROM vta_instalacion_cambio_plan where id_instalacion=" + id + " order by id_instalacion_cambio_plan desc");
    }

    public boolean enConflictoCambioPlan(String id_cambio_plan, String id_instalacion, String fecha_cambio) {
        ResultSet res = this.consulta("SELECT * FROM tbl_instalacion_cambio_plan where date_trunc('month', '" + fecha_cambio + "'::date)::date = date_trunc('month', fecha_cambio)::date "
                + "and eliminado=false and id_instalacion=" + id_instalacion + " and id_instalacion_cambio_plan<>" + id_cambio_plan + ";");
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

    public String insertarCambioPlan(String id_instalacion, String id_plan_servicio, String fecha_cambio) {
        String pk = this.insert("insert into tbl_instalacion_cambio_plan(id_instalacion, id_plan_servicio, fecha_cambio) "
                + "values(" + id_instalacion + ", " + id_plan_servicio + ", '" + fecha_cambio + "');");
        if (pk.compareTo("-1") != 0) {
            this.ejecutar("update tbl_instalacion_cambio_plan set vigente=false where vigente=true and id_instalacion=" + id_instalacion);
            if (!this.ejecutar("update tbl_instalacion set id_plan_actual=" + id_plan_servicio + " where id_instalacion=" + id_instalacion)) {
                this.ejecutar("delete from tbl_instalacion_cambio_plan where id_instalacion_cambio_plan=" + pk);
                pk = "-1";
            }
        }
        return pk;
    }

    public boolean actualizarCambioPlan(String id_instalacion_cambio_plan, String id_instalacion, String id_plan_servicio, String fecha_cambio) {
        List sql = new ArrayList();
        sql.add("update tbl_instalacion_cambio_plan set id_plan_servicio=" + id_plan_servicio + " where id_instalacion_cambio_plan=" + id_instalacion_cambio_plan + ";");
        try {
            ResultSet rs = this.consulta("select max(id_instalacion_cambio_plan) from tbl_instalacion_cambio_plan "
                    + "where id_instalacion=" + id_instalacion + " and eliminado=false");
            if (rs.next()) {
                String id_icp = rs.getString(1) != null ? rs.getString(1) : "";
                if (id_icp.compareTo(id_instalacion_cambio_plan) == 0) {
                    sql.add("update tbl_instalacion set id_plan_actual=" + id_plan_servicio + " where id_instalacion=" + id_instalacion);
                }
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.transacciones(sql);
    }

    /*  SUSPENCIONES  */
    public ResultSet getSuspension(String id) {
        return this.consulta("SELECT * FROM vta_instalacion_suspension where id_instalacion_suspension=" + id );
    }
    
    public ResultSet getInstalacionSuspension(String id) {
        return this.consulta("select S.*, I.id_sucursal, I.ip  from tbl_instalacion_suspension as S inner join tbl_instalacion as I on S.id_instalacion =I.id_instalacion where S.id_instalacion_suspension=" + id);
    }

    public ResultSet getSuspensiones(String id) {
        return this.consulta("SELECT *, case when tipo='t' then fecha_inicio >= date_trunc('month', now()::date) else fecha_termino > now()::date end as eliminar, "
                + "case when tipo='d' then (fecha_solicitud + '15 day'::interval)::date else fecha_solicitud end as fecha_solicitud_anular FROM vta_instalacion_suspension where id_instalacion=" + id );
    }
    
    public int numSuspensionesEnElAnio(String idInstalacion) 
    {
        int num=0;
        try {
            ResultSet res = this.consulta("with A as(\n" +
                "	select id_instalacion, (fecha_instalacion + ( (date_part('year', now()) - date_part('year', fecha_instalacion) )::int || ' years')::interval)::date as fecha_corte \n" +
                "	from tbl_instalacion where id_instalacion = " + idInstalacion + " \n" +
                "), \n" +
                "B as( \n" +
                "	select id_instalacion, fecha_corte, case when fecha_corte < now()::date then (fecha_corte + '1 year'::interval)::date else (fecha_corte - '1 year'::interval)::date end as inicio_o_fin from A \n" +
                "), \n" +
                "C as ( \n" +
                "	select id_instalacion, case when fecha_corte > inicio_o_fin then inicio_o_fin else fecha_corte end as inicio, case when fecha_corte > inicio_o_fin then fecha_corte else inicio_o_fin end as fin from B \n" +
                ") \n" +
                "select count(S.id_instalacion) from tbl_instalacion_suspension as S inner join C on S.id_instalacion = C.id_instalacion " +
                "where S.id_instalacion = " + idInstalacion + " and S.tipo = 't' and not S.eliminado and fecha_solicitud between C.inicio and (C.fin - '1 day'::interval)::date;");
            
            if (res.next() ){
                num = res.getString(1) != null ? res.getInt(1) : 0;
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    public boolean enConflictoSuspension(String id_suspension, String id_instalacion, String fecha_inicio) {
        ResultSet res = this.consulta("SELECT * FROM tbl_instalacion_suspension where '" + fecha_inicio + "' between fecha_inicio and fecha_termino and eliminado=false and id_instalacion=" + id_instalacion + " and id_instalacion_suspension<>" + id_suspension + ";");
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

    public boolean enConflictoSuspension(String id_suspension, String id_instalacion, String fecha_inicio, String fecha_termino) {
        ResultSet res = this.consulta("SELECT * FROM tbl_instalacion_suspension where ('" + fecha_inicio + "' between fecha_inicio and fecha_termino or '" + fecha_termino
                + "' between fecha_inicio and fecha_termino) and eliminado=false and id_instalacion=" + id_instalacion + " and id_instalacion_suspension<>" + id_suspension + ";");
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

    public boolean haySuspencion(String id_instalacion, String fecha_inicio, String fecha_termino) {
        try {
            ResultSet res = this.consulta("SELECT * FROM tbl_instalacion_suspension where ('" + fecha_inicio + "' between fecha_inicio and fecha_termino or '" + fecha_termino
                    + "' between fecha_inicio and fecha_termino) and eliminado=false and id_instalacion=" + id_instalacion);
            if (this.getFilas(res) > 0) {
                return true;
            }
            res.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String insertarSuspension(String id_instalacion, String usuario, String tipo, String fecha_inicio, String fecha_termino, int tiempo, String obserSuspDefinitiva) {
        if (tipo.compareTo("d") == 0) {
            return this.insert("insert into tbl_instalacion_suspension(id_instalacion, usuario_solicitud, tipo, fecha_inicio, fecha_termino, tiempo, observacion_orden_trabajo) "
                    + "values(" + id_instalacion + ", '" + usuario + "', '" + tipo + "', '" + fecha_inicio + "', ('" + fecha_inicio + "':: date + '15 days'::interval)::date, " + tiempo + ", '" + obserSuspDefinitiva + "');");
        } else {
            return this.insert("insert into tbl_instalacion_suspension(id_instalacion, usuario_solicitud, tipo, fecha_inicio, fecha_termino, tiempo, observacion_orden_trabajo) "
                    + "values(" + id_instalacion + ", '" + usuario + "', '" + tipo + "', '" + fecha_inicio + "', '" + fecha_termino + "', " + tiempo + ", '" + obserSuspDefinitiva + "');");
        }
    }

    public String insertarSuspension(String id_instalacion, String usuario, String tipo, String fecha_inicio, String fecha_termino, int tiempo, String obserSuspDefinitiva, String proveedor_cambia, String motivo_cancelacion) {
        if (tipo.compareTo("d") == 0) {
            return this.insert("insert into tbl_instalacion_suspension(id_instalacion, usuario_solicitud, tipo, fecha_inicio, fecha_termino, tiempo, observacion_orden_trabajo,proveedor_cambia,motivo_cancelacion) "
                    + "values(" + id_instalacion + ", '" + usuario + "', '" + tipo + "', '" + fecha_inicio + "', ('" + fecha_inicio + "':: date + '15 days'::interval)::date, " + tiempo + ", '" + obserSuspDefinitiva + "', '" + proveedor_cambia + "', '" + motivo_cancelacion + "');");
        } else {
            return this.insert("insert into tbl_instalacion_suspension(id_instalacion, usuario_solicitud, tipo, fecha_inicio, fecha_termino, tiempo, observacion_orden_trabajo) "
                    + "values(" + id_instalacion + ", '" + usuario + "', '" + tipo + "', '" + fecha_inicio + "', '" + fecha_termino + "', " + tiempo + ", '" + obserSuspDefinitiva + "');");
        }
    }

    public boolean haySuspensionEnPrefactura(String idSuspension) {
        ResultSet res = this.consulta("SELECT tbl_prefactura FROM tbl_prefactura where fecha_emision is not null and id_instalacion_suspension = " + idSuspension );
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
    
    public boolean anularSuspension(String idInstalacion, String id_suspension, String usuario, String fecha_reactivacion, int num_meses) {
        List sql = new ArrayList();
        int anio = Fecha.getAnio();
        int mes = Fecha.getMes();
        String fecha1 = anio + "-" + mes + "-01";
//        if (num_meses == 0) {
        //  verifico si no hay prefactura en el periodo de reactivacion
        try {
//                ResultSet rs = this.consulta("SELECT distinct id_instalacion FROM tbl_instalacion where id_instalacion="+idInstalacion+" and id_instalacion not in (select id_instalacion from tbl_prefactura where periodo between '" +
//                    fecha1 + "' and '" + anio + "-" + mes + "-" + Fecha.getUltimoDiaMes(anio, mes) + "' id_instalacion="+idInstalacion+") " +
//                    "and id_instalacion not in (select distinct id_instalacion from tbl_anticipo_internet where '"+fecha1+"' between fecha_ini and fecha_fin and eliminado=false and id_instalacion="+idInstalacion+") " +
//                    "and id_instalacion not in (select distinct id_instalacion from tbl_instalacion_suspension where '"+fecha1+"' between fecha_inicio and fecha_termino and eliminado=false and id_instalacion="+idInstalacion+") " +
//                    "and id_instalacion not in (select distinct id_instalacion from tbl_prefactura where fecha_emision is null and id_instalacion="+idInstalacion+") " +
//                    "and id_instalacion not in (select distinct id_instalacion from tbl_factura_venta where deuda>0 and id_instalacion is not null and anulado=false and id_instalacion="+idInstalacion+")");

            long idPrefactura = -1;
//                int diasRestantes = Fecha.getUltimoDiaMes(anio, mes) - Fecha.getDia();
            int diasRestantes = 30 - Fecha.getDia();
            int diasConexion = 30;
            ResultSet rs = this.consulta("SELECT id_prefactura, dias_conexion FROM tbl_prefactura where fecha_emision is null and id_instalacion=" + idInstalacion
                    + " and periodo between '" + fecha1 + "' and '" + anio + "-" + mes + "-" + Fecha.getUltimoDiaMes(anio, mes) + "'");
            if (this.getFilas(rs) > 0) {
                if (rs.next()) {
                    idPrefactura = rs.getString("id_prefactura") != null ? rs.getLong("id_prefactura") : -1;
                    diasConexion = rs.getString("dias_conexion") != null ? rs.getInt("dias_conexion") : 30;
                    if (diasConexion < 30) {
                        diasConexion += diasRestantes;
                    }
                    rs.close();
                }
            } else {
                ResultSet rsPreFactura = this.consulta("select proc_generarprefactura(" + anio + ", " + mes + ", " + idInstalacion + ");");
                if (rsPreFactura.next()) {
                    idPrefactura = rsPreFactura.getString(1) != null ? rsPreFactura.getLong(1) : -1;
                    rsPreFactura.close();
                }
            }

            if (idPrefactura != -1) {
                this.ejecutar("update tbl_prefactura set dias_conexion=" + diasConexion + " where id_prefactura=" + idPrefactura);
//                this.consulta("select proc_calcularPreFactura(" + idPrefactura + ", false);");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        sql.add("update tbl_instalacion_suspension set reac_usuario_solicitud='" + usuario + "', reac_fecha_reactivacion='" + fecha_reactivacion
                + "', reac_fecha_solicitud=now(), eliminado=true where id_instalacion_suspension=" + id_suspension);
//        } else {
//            num_meses--;
//            sql.add( "update tbl_instalacion_suspension set reac_usuario_solicitud='" + usuario + "', reac_fecha_reactivacion='" + fecha_reactivacion
//                    + "', reac_fecha_solicitud=now(), fecha_termino = ('" + Fecha.getAnio() + "-" + Fecha.getMes() + "-01'::date - '1 day'::interval) "
//                    + " where id_instalacion_suspension=" + id_suspension );
//        }
        return this.transacciones(sql);
    }

    public boolean actualizarSuspension(String id_suspension, String fecha_termino, int tiempo) {
        return this.ejecutar("update tbl_instalacion_suspension set fecha_termino='" + fecha_termino
                + "', tiempo=" + tiempo + " where id_instalacion_suspension=" + id_suspension + ";");
    }
    
    public boolean actualizarSuspension(String id_suspension, String tipo, String fecha_inicio, String fecha_termino, int tiempo) {
        return this.ejecutar("update tbl_instalacion_suspension set fecha_inicio='" + fecha_inicio + "', fecha_termino='" + fecha_termino
                + "', tiempo=" + tiempo + ", tipo='" + tipo + "' where id_instalacion_suspension=" + id_suspension + ";");
    }

    
/*  servidores  */
    
    public ResultSet getSuspensionesServidor() {
        return this.consulta("SELECT distinct razon_social, ip::varchar, txt_sucursal, id_sucursal, estado_servicio FROM vta_instalacion "
                + "where estado_servicio in ('p', 's', 'c', 'r', 'd', 'n') order by id_sucursal");
    }

    public ResultSet getActivacionesServidor() {
        return this.consulta("SELECT distinct razon_social, ip::varchar, txt_sucursal, id_sucursal, estado_servicio, lower(plan) as plan FROM vta_instalacion "
                + "where estado_servicio='a' order by id_sucursal");
    }

    public ResultSet getActivacionesServidor(String idSuc) {
        return this.consulta("SELECT distinct razon_social, ip::varchar, txt_sucursal, id_sucursal, estado_servicio, lower(plan) as plan FROM vta_instalacion "
                + "where estado_servicio in ('p', 'a') and id_sucursal=" + idSuc + " order by ip, id_sucursal");
    }

    public ResultSet getColasServidorSucursal(String idSuc) {
        return this.consulta("SELECT distinct razon_social || ' ' || id_instalacion as razon_social, ip::varchar, P.*, case P.comparticion when 1 then 2 when 3 then 3 when 8 then 8 else 8 end as prioridad, I.plan "
                + "FROM vta_instalacion as I inner join vta_plan_servicio as P on I.id_plan_actual=P.id_plan_servicio "
                + "where estado_servicio in ('p', 'a') and I.id_sucursal=" + idSuc + " order by ip;");
    }

    public ResultSet getColasServidor(String subred, String id_sucursal) {
        String ip = "127.0.0.1";
        //String mascara = "24";
        if (subred.contains("/") && subred.length() > 0) {
            String vecRed[] = subred.split("/");
            ip = vecRed[0].trim();
            //mascara = vecRed[1].trim();
        } else if (!subred.contains("/") && subred.length() > 0) {
            ip = subred.trim();
        }
        String octetos[] = ip.replace(".", ";").split(";");
        String ipRed = "";
        for (int i = 0; i < octetos.length - 1; i++) {
            ipRed += octetos[i] + ".";
        }
        if (ipRed.compareTo("") == 0) {
            ipRed = "127.0.0.";
        }

        return this.consulta("SELECT distinct razon_social || ' ' || id_instalacion as razon_social, ip, P.*, "
                + "case P.comparticion when 1 then 2 when 3 then 3 when 8 then 8 else 8 end as prioridad, I.plan "
                + "FROM vta_instalacion as I inner join vta_plan_servicio as P on I.id_plan_actual=P.id_plan_servicio "
                + "where estado_servicio in ('p', 'a') and ip::varchar like '" + ipRed + "%' and I.id_sucursal=" + id_sucursal + " order by ip;");
    }

    
    /*  DES - INSTALACION    */
    public String getNumDesInstalacion(String idSuc) {
        String num = "1";
        try {
            ResultSet rs = this.consulta("select case when max(num_instalacion)>0 then max(num_instalacion)+1 else 1 end "
                    + "from tbl_instalacion where id_sucursal=" + idSuc + ";");
            if (rs.next()) {
                num = rs.getString(1) != null ? rs.getString(1) : "1";
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    public boolean setNoInstalacion(String id_instalacion, String fecha_visita_instalacion, String motivo_no_instalacion) {
        ResultSet rs = this.consulta("SELECT * FROM tbl_instalacion where id_instalacion=" + id_instalacion + " and fecha_visita_instalacion is not null");
        try {
            if (this.getFilas(rs) == 0) {
                return this.ejecutar("update tbl_instalacion set fecha_visita_instalacion='" + fecha_visita_instalacion + "', fecha_no_instalacion=now(), motivo_no_instalacion='" + motivo_no_instalacion + "' where id_instalacion=" + id_instalacion);
            } else {
                return this.ejecutar("update tbl_instalacion set fecha_visita_instalacion='" + fecha_visita_instalacion + "', motivo_no_instalacion='" + motivo_no_instalacion + "' where id_instalacion=" + id_instalacion);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean porRetirar(String id_instalacion, String generar_orden_trab, String id_cliente, String num_desinstalacion, String fecha_desinstalacion, String motivo_desinstalacion) {
        boolean ok = false;
        ok = this.ejecutar("UPDATE tbl_instalacion SET num_desinstalacion=" + num_desinstalacion + ", generar_orden_trab=" + generar_orden_trab + ", fecha_desinstalacion='" + fecha_desinstalacion
                + "', motivo_desinstalacion='" + motivo_desinstalacion + "', estado_servicio='r', fecha_estado_r=now()::date WHERE id_instalacion=" + id_instalacion);
        if (ok) {
            this.ejecutar("UPDATE tbl_orden_trabajo SET fecha_solucion=now(), recomendacion='POR DES-INSTALACION DESDE ADMINISTRACION', estado='9' WHERE estado='1' and id_instalacion=" + id_instalacion);
            try {
                ResultSet rs = this.consulta("select * from tbl_instalacion where id_cliente=" + id_cliente + " and estado_servicio not in ('r','e','t') and anulado=false");
                if (this.getFilas(rs) == 0) {
                    ResultSet rsContrato = this.consulta("select id_contrato from tbl_instalacion where id_instalacion=" + id_instalacion);
                    if (rsContrato.next()) {
                        String id_contrato = rsContrato.getString("id_contrato") != null ? rsContrato.getString("id_contrato") : "";
                        if (id_contrato.compareTo("") != 0) {
                            this.ejecutar("update tbl_contrato set terminado=true where id_contrato=" + id_contrato);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ok;
    }

    public boolean desInstalar(String id_instalacion, String id_cliente, String observacion_terminado, String estado, String codsMateriales) {
        List sql = new ArrayList();

        //estado = estado.compareTo("r") == 0 ? "null" : estado;
        sql.add("UPDATE tbl_instalacion SET observacion_terminado='" + observacion_terminado + "', estado_servicio='" + estado + "', fecha_terminado=now()::date,fecha_desinstalacion=now()::date WHERE id_instalacion=" + id_instalacion);
        sql.add("UPDATE tbl_orden_trabajo SET fecha_solucion=now(), recomendacion='POR DES-INSTALACION DESDE ADMINISTRACION', estado='9' WHERE fecha_solucion is null and id_instalacion=" + id_instalacion + ";");
        String codigo[] = codsMateriales.split(",");
        for (int i = 0; i < codigo.length; i++) {
            if (codigo[i].compareTo("") != 0) {
                sql.add("DELETE FROM tbl_instalacion_activo WHERE id_activo=" + codigo[i] + ";");
            }
        }

        if (estado.compareTo("e") != 0) {
            try {
                ResultSet rs = this.consulta("select id_instalacion from tbl_instalacion where id_cliente=" + id_cliente + " and estado_servicio in('p','a','s','c')");
                if (this.getFilas(rs) == 0) {
                    this.setConvenioCuenta(id_instalacion, "false");
                    this.setConvenioTarjeta(id_instalacion, "false");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this.transacciones(sql);
    }

    public boolean desInstalarFibra(String id_instalacion, String observacion_terminado, String estado, String codsMateriales, String spliter) {
        List sql = new ArrayList();

        //estado = estado.compareTo("r") == 0 ? "null" : "'" + estado + "'";
        sql.add("UPDATE tbl_instalacion SET observacion_terminado='" + observacion_terminado + "', estado_servicio='" + estado + "', fecha_terminado=now()::date WHERE id_instalacion=" + id_instalacion);
        sql.add("UPDATE tbl_orden_trabajo SET fecha_solucion=now(), recomendacion='POR DES-INSTALACION DESDE ADMINISTRACION', estado='9' WHERE fecha_solucion is null and id_instalacion=" + id_instalacion + ";");
        //sql.add("update tbl_spliter_utilizado as su set estado_puerto=false"
        //         + " from tbl_spliter as s where s.id_spliter=su.id_spliter  and su.id_instalacion='" + id_instalacion + "' and s.tipo_spliter='s';");
        sql.add("delete from tbl_spliter_utilizado where id_instalacion='" + id_instalacion + "';");
        String codigo[] = codsMateriales.split(",");
        for (int i = 0; i < codigo.length; i++) {
            if (codigo[i].compareTo("") != 0) {
                sql.add("DELETE FROM tbl_instalacion_activo WHERE id_activo=" + codigo[i] + ";");
            }
        }
        return this.transacciones(sql);
    }

    //  COMISIONES
    public ResultSet getInstalacionesComision(String id_instalacion_comision) {
        return this.consulta("select I.id_plan_actual, I.razon_social, I.plan, I.fecha_registro, I.fecha_instalacion, "
                + "I.ingreso_datos, F.serie_factura || '-' || F.num_factura as num_factura, F.deuda,I.ruc "
                + "FROM (vta_instalacion as I inner join tbl_instalacion_comision as C on C.id_sucursal=I.id_sucursal) "
                + "left outer join tbl_factura_venta as F on F.id_factura_venta=I.id_factura_venta "
                + "where c.id_instalacion_comision=" + id_instalacion_comision + " and I.anulado=false and incluir_en_comision=true "
                + "and I.cobrar=true and I.ingreso_datos between C.fecha_inicio and C.fecha_termino "
                + "and I.id_instalacion in(select id_instalacion from tbl_orden_trabajo where tipo_trabajo='3' and ot_procedente and estado='9') "
                + "order by I.ingreso_datos asc;");
    }

//    public boolean setComision(String id_instalacion_comision)
//    {
//        return this.ejecutar("u");
//    }
    public ResultSet getInstalacionesComisionFreeLance(String id_instalacion_comision) {
        return this.consulta("select I.id_plan_actual, I.razon_social, I.plan, I.fecha_registro, I.fecha_instalacion, "
                + "I.ingreso_datos, F.serie_factura || '-' || F.num_factura as num_factura, F.deuda "
                + "FROM (vta_instalacion as I inner join tbl_instalacion_comision_bono as C on C.alias=I.alias) "
                + "left outer join tbl_factura_venta as F on F.id_factura_venta=I.id_factura_venta "
                + "where c.id_instalacion_comision_bono=" + id_instalacion_comision + " and I.anulado=false and I.cobrar=true and "
                + "I.ingreso_datos between C.fecha_inicio and C.fecha_termino "
                + "order by I.fecha_registro;");
    }

    //  FREE RADIUS
    public String getUsrFreeRadius(String id_plan_servicio) {
        String usuarios = "";
        try {
            ResultSet rs = this.consulta("select radusername from tbl_instalacion where estado_servicio not in ('t','e') and id_plan_actual in (" + id_plan_servicio + ")");
            while (rs.next()) {
                usuarios += (rs.getString("radusername") != null ? "'" + rs.getString("radusername") + "'," : "");
            }
            if (usuarios.compareTo("") != 0) {
                usuarios = usuarios.substring(0, usuarios.length() - 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    public String getUsuariosFreeRadius(String id_instalaciones) {
        String usuarios = "";
        try {
            ResultSet rs = this.consulta("select radusername from tbl_instalacion where id_instalacion in (" + id_instalaciones + ")");
            while (rs.next()) {
                usuarios += (rs.getString("radusername") != null ? "'" + rs.getString("radusername") + "'," : "");
            }
            if (usuarios.compareTo("") != 0) {
                usuarios = usuarios.substring(0, usuarios.length() - 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    public ResultSet getUsuariosNoRegistrados() {
        return this.consulta("SELECT * from tbl_instalacion as I where estado_servicio not in ('t', 'e') and "
                + "radusername not in (select username from radusergroup);");
    }

    public boolean setDeviceClave(String id_instalacion) {
        return this.ejecutar("update tbl_instalacion set set_deviceclave = not set_deviceclave where id_instalacion = " + id_instalacion);
    }
    
    public boolean setConvenioNoPrefacturar(String idInstalacion, String aceptacion, String motivo, String esCanje, String fechaFinCanje) 
    {
        if(aceptacion.compareTo("si")==0) {
            fechaFinCanje = fechaFinCanje.compareTo("") != 0 ? "'" + fechaFinCanje + "'" : "null";
            esCanje = esCanje.toLowerCase().compareTo("on")==0 || esCanje.toLowerCase().compareTo("true")==0 || esCanje.toLowerCase().compareTo("t")==0 
                    ? "true" 
                    : "false";
            return this.ejecutar("update tbl_instalacion set estado_solicitud_no_cobrar='a', cobrar=false, fecha_fin_canje="+fechaFinCanje+", es_canje="+esCanje+", motivo_no_cobrar='"+motivo+"' where id_instalacion = " + idInstalacion);
        }
        return this.ejecutar("update tbl_instalacion set estado_solicitud_no_cobrar='r', cobrar=true, fecha_fin_canje=null, es_canje=false, motivo_no_cobrar='"+motivo+"', incluir_en_comision=true where id_instalacion = " + idInstalacion);
    }

    public String insertarCambioPlan(String id_instalacion, String id_plan_servicio, String fecha_cambio, String idtipoins, String usuario) {

        String pk = this.insert("insert into tbl_instalacion_cambio_plan(id_instalacion, id_plan_servicio, fecha_cambio,tipo_instalacioncp, usuario) "
                + "values(" + id_instalacion + ", " + id_plan_servicio + ", '" + fecha_cambio + "', '" + idtipoins + "', '" + usuario + "');");
        if (pk.compareTo("-1") != 0) {
            this.ejecutar("update tbl_instalacion_cambio_plan set vigente=false where vigente=true and id_instalacion=" + id_instalacion);
            if (!this.ejecutar("update tbl_instalacion set id_plan_actual=" + id_plan_servicio + ",tipo_instalacion='" + idtipoins + "' where id_instalacion=" + id_instalacion)) {
                this.ejecutar("delete from tbl_instalacion_cambio_plan where id_instalacion_cambio_plan=" + pk);
                pk = "-1";
            }
        }
        return pk;
    }

    public String[] InsertarCambioPlan(String id_instalacion, String id_plan_servicio, String fecha_cambio, String idtipoins, String usuario, String id_sucursal, String id_plan_servicio_anterior) {
        String pk[] = {"-1", "", ""};
        try {
            boolean ok = false;
            String plan[] = this.DatosPlanNuevo(id_plan_servicio, id_sucursal);
            int anio = Fecha.getAnio();
            int mes = Fecha.getMes();
            int dia = 1;
            String periodo = anio + "-" + mes + "-" + dia;
            SimpleDateFormat formatear_fecha = new SimpleDateFormat("yyyy-MM-dd");
            Date fecha_periodo = formatear_fecha.parse(periodo.trim().toString());
            Date periodo_cambio = formatear_fecha.parse(fecha_cambio.trim().toString());
            double costo_restante = 0;
            
            if (fecha_periodo.compareTo(periodo_cambio) == 0) {
                
                String prefactura[] = this.UltimaPrefatura(id_instalacion);
                
                ////si no hay prefactura actualizo el cambi0o de plan normalmente
                if (prefactura[0].trim().compareTo("") == 0) {
                    pk[0] = this.insert("insert into tbl_instalacion_cambio_plan(id_instalacion, id_plan_servicio, fecha_cambio,tipo_instalacioncp, usuario, plan_pendiente, id_plan_servicio_ant) "
                            + "values(" + id_instalacion + ", " + id_plan_servicio + ", '" + fecha_cambio + "', '" + idtipoins + "', '" + usuario + "', false, "+id_plan_servicio_anterior+");");

                ///caso contrario realizamos las siguientes acciones
                } else {
                    Date fecha_prefactura = formatear_fecha.parse(prefactura[1].trim().toString());
                    plan[1] = (plan[1].trim().compareTo("") != 0 ? plan[1] : "0");
                    prefactura[4] = (prefactura[1].trim().compareTo("") != 0 ? prefactura[4] : "0");
                    double costo_nuevo = Double.parseDouble(plan[1]);
                    double costo_actual = Double.parseDouble(prefactura[4]);
                    /////si es del mismo periodo y no esta echo el pago actualizo el plan normalmente
                    if (fecha_prefactura.compareTo(fecha_periodo) == 0 && prefactura[2].trim().compareTo("") == 0) {
                        pk[0] = this.insert("insert into tbl_instalacion_cambio_plan(id_instalacion, id_plan_servicio, fecha_cambio,tipo_instalacioncp, usuario, plan_pendiente, id_plan_servicio_ant) "
                                + "values(" + id_instalacion + ", " + id_plan_servicio + ", '" + fecha_cambio + "', '" + idtipoins + "', '" + usuario + "', false, "+id_plan_servicio_anterior+");");
                    } else if (fecha_prefactura.compareTo(fecha_periodo) == 0 && prefactura[2].trim().compareTo("") != 0) {
                        pk[0] = this.insert("insert into tbl_instalacion_cambio_plan(id_instalacion, id_plan_servicio, fecha_cambio,tipo_instalacioncp, usuario, plan_pendiente, id_plan_servicio_ant) "
                                + "values(" + id_instalacion + ", " + id_plan_servicio + ", '" + fecha_cambio + "', '" + idtipoins + "', '" + usuario + "', false, "+id_plan_servicio_anterior+");");
                        if (costo_nuevo > costo_actual) {
//                            costo_restante = costo_nuevo - costo_actual;
                            int dias = Fecha.getDia();
                            if (dias < 30) {
                                double valor_plan_actual = (costo_actual / 30) * dias;
                                double valor_plan_nuevo = (costo_nuevo / 30) * (30 - dias);
                                costo_restante = (valor_plan_actual + valor_plan_nuevo) - costo_actual;
                                costo_restante = Addons.redondear(costo_restante, 2);
                                if (costo_restante > 0) {
                                    this.insert("INSERT INTO tbl_prefactura_rubro( id_sucursal, id_rubro,id_instalacion, rubro,periodo, monto,tiporubro)VALUES ('" + id_sucursal + "','15' ,'" + id_instalacion + "', 'CAMBIO DE PLAN','" + periodo + "'::date + '1 month'::interval, '" + costo_restante + "','p');");
                                } else {
                                    costo_restante = 0;
                                }
                            }
                        }
                    } else if (fecha_prefactura.before(fecha_periodo) && prefactura[2].trim().compareTo("") != 0) {
                        pk[0] = this.insert("insert into tbl_instalacion_cambio_plan(id_instalacion, id_plan_servicio, fecha_cambio,tipo_instalacioncp, usuario, plan_pendiente, id_plan_servicio_ant) "
                                + "values(" + id_instalacion + ", " + id_plan_servicio + ", '" + fecha_cambio + "', '" + idtipoins + "', '" + usuario + "', false, "+id_plan_servicio_anterior+");");
                    } else {
                        pk[0] = this.insert("insert into tbl_instalacion_cambio_plan(id_instalacion, id_plan_servicio, fecha_cambio,tipo_instalacioncp, usuario,plan_pendiente, id_plan_servicio_ant) "
                                + "values(" + id_instalacion + ", " + id_plan_servicio + ", '" + fecha_cambio + "', '" + idtipoins + "', '" + usuario + "', true, "+id_plan_servicio_anterior+");");
                        ok = true;
                    }

                }
            } else {
                pk[0] = this.insert("insert into tbl_instalacion_cambio_plan(id_instalacion, id_plan_servicio, fecha_cambio,tipo_instalacioncp, usuario, plan_pendiente, id_plan_servicio_ant) "
                        + "values(" + id_instalacion + ", " + id_plan_servicio + ", '" + fecha_cambio + "', '" + idtipoins + "', '" + usuario + "', true, "+id_plan_servicio_anterior+");");
                ok = true;
            }
            if (pk[0].compareTo("-1") != 0) {
                if (!ok) {
                    this.ejecutar("update tbl_instalacion_cambio_plan set vigente=false where vigente=true and id_instalacion=" + id_instalacion);
                    if (!this.ejecutar("update tbl_instalacion set id_plan_actual=" + id_plan_servicio + ",tipo_instalacion='" + idtipoins + "' where id_instalacion=" + id_instalacion)) {
                        this.ejecutar("delete from tbl_instalacion_cambio_plan where id_instalacion_cambio_plan=" + pk[0]);
                        pk[0] = "-1";
                    }
                }
                pk[1] = "" + costo_restante;
            }

        } catch (Exception e) {
            System.out.println("error al guardar el plan");
        }
        return pk;
    }

    public String[] UltimaPrefatura(String id_instalacion) {
        String resultado[] = {"", "", "", "", ""};
        ResultSet rs = this.consulta("select p.id_prefactura,p.periodo,p.fecha_emision,i.id_plan_actual, "
                + " (select t.costo_plan from vta_plan_tarifa as t where t.id_plan_servicio=i.id_plan_actual and t.id_sucursal=i.id_sucursal)as costo_actual "
                + " from tbl_prefactura as p "
                + " inner join tbl_instalacion as i on i.id_instalacion=p.id_instalacion "
                + " where p.id_prefactura=(select max(t1.id_prefactura) from tbl_prefactura as t1 where t1.id_instalacion='" + id_instalacion + "');");
        try {
            if (rs.next()) {
                resultado[0] = (rs.getString("id_prefactura") != null ? rs.getString("id_prefactura") : "");
                resultado[1] = (rs.getString("periodo") != null ? rs.getString("periodo") : "");
                resultado[2] = (rs.getString("fecha_emision") != null ? rs.getString("fecha_emision") : "");
                resultado[3] = (rs.getString("id_plan_actual") != null ? rs.getString("id_plan_actual") : "");
                resultado[4] = (rs.getString("costo_actual") != null ? rs.getString("costo_actual") : "");
            }
        } catch (Exception e) {
            System.out.println("Error al obtener la ultima prefactura");
        }
        return resultado;
    }

    public String[] DatosPlanNuevo(String id_plan_actual, String id_sucursal) {
        String resultado[] = {"", ""};
        ResultSet rs = this.consulta("select t.id_plan_servicio,t.costo_plan from vta_plan_tarifa as t where t.id_plan_servicio='" + id_plan_actual + "' and t.id_sucursal='" + id_sucursal + "';");
        try {
            if (rs.next()) {
                resultado[0] = (rs.getString("id_plan_servicio") != null ? rs.getString("id_plan_servicio") : "");
                resultado[1] = (rs.getString("costo_plan") != null ? rs.getString("costo_plan") : "");
            }
        } catch (Exception e) {
            System.out.println("Error al obtener datos del plan nuevo");
        }
        return resultado;
    }

    public boolean actualizarCambioPlan(String id_instalacion_cambio_plan, String id_instalacion, String id_plan_servicio, String fecha_cambio, String idtipoins) {
        List sql = new ArrayList();
        sql.add("update tbl_instalacion_cambio_plan set id_plan_servicio=" + id_plan_servicio + ",tipo_instalacioncp='" + idtipoins + "' where id_instalacion_cambio_plan=" + id_instalacion_cambio_plan + ";");
        try {
            ResultSet rs = this.consulta("select max(id_instalacion_cambio_plan) from tbl_instalacion_cambio_plan "
                    + "where id_instalacion=" + id_instalacion + " and eliminado=false");
            if (rs.next()) {
                String id_icp = rs.getString(1) != null ? rs.getString(1) : "";
                if (id_icp.compareTo(id_instalacion_cambio_plan) == 0) {
                    sql.add("update tbl_instalacion set id_plan_actual=" + id_plan_servicio + ",tipo_instalacion='" + idtipoins + "' where id_instalacion=" + id_instalacion);
                }
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.transacciones(sql);
    }

    public String[] ActualizarCambioPlan(String id_instalacion_cambio_plan, String id_instalacion, String id_plan_servicio, String fecha_cambio, String idtipoins, String id_sucursal) {
        List sql = new ArrayList();
        String pk[] = {"", "", ""};
        boolean ok = false;
        String plan[] = this.DatosPlanNuevo(id_plan_servicio, id_sucursal);
        int anio = Fecha.getAnio();
        int mes = Fecha.getMes();
        int dia = 1;
        String periodo = anio + "-" + mes + "-" + dia;
        SimpleDateFormat formatear_fecha = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date fecha_periodo = formatear_fecha.parse(periodo.trim().toString());
            Date periodo_cambio = formatear_fecha.parse(fecha_cambio.trim().toString());
            double costo_restante = 0;
            if (fecha_periodo.compareTo(periodo_cambio) == 0) {
                String prefactura[] = this.UltimaPrefatura(id_instalacion);
                if (prefactura[0].trim().compareTo("") == 0) {
                    sql.add("update tbl_instalacion_cambio_plan set id_plan_servicio=" + id_plan_servicio + ",tipo_instalacioncp='" + idtipoins + "',plan_pendiente=false where id_instalacion_cambio_plan=" + id_instalacion_cambio_plan + ";");
                } else {
                    Date fecha_prefactura = formatear_fecha.parse(prefactura[1].trim().toString());
                    plan[1] = (plan[1].trim().compareTo("") != 0 ? plan[1] : "0");
                    prefactura[4] = (prefactura[1].trim().compareTo("") != 0 ? prefactura[4] : "0");
                    double costo_nuevo = Double.parseDouble(plan[1]);
                    double costo_actual = Double.parseDouble(prefactura[4]);
                    /////si es del mismo periodo y no esta echo el pago actualizo el plan normalmente
                    if (fecha_prefactura.compareTo(fecha_periodo) == 0 && prefactura[2].trim().compareTo("") == 0) {
                        sql.add("update tbl_instalacion_cambio_plan set id_plan_servicio=" + id_plan_servicio + ",tipo_instalacioncp='" + idtipoins + "',plan_pendiente=false where id_instalacion_cambio_plan=" + id_instalacion_cambio_plan + ";");
                    } else if (fecha_prefactura.compareTo(fecha_periodo) == 0 && prefactura[2].trim().compareTo("") != 0) {
                        sql.add("update tbl_instalacion_cambio_plan set id_plan_servicio=" + id_plan_servicio + ",tipo_instalacioncp='" + idtipoins + "',plan_pendiente=false where id_instalacion_cambio_plan=" + id_instalacion_cambio_plan + ";");
                        if (costo_nuevo > costo_actual) {
                            costo_restante = costo_nuevo - costo_actual;
                            sql.add("INSERT INTO tbl_prefactura_rubro( id_sucursal, id_rubro,id_instalacion, rubro,periodo, monto,tiporubro)VALUES ('" + id_sucursal + "','15' ,'" + id_instalacion + "', 'CAMBIO DE PLAN','" + periodo + "'::date + '1 month'::interval, '" + costo_restante + "','p');");
                        }
                    } else if (fecha_prefactura.before(fecha_periodo) && prefactura[2].trim().compareTo("") != 0) {
                        sql.add("update tbl_instalacion_cambio_plan set id_plan_servicio=" + id_plan_servicio + ",tipo_instalacioncp='" + idtipoins + "',plan_pendiente=false where id_instalacion_cambio_plan=" + id_instalacion_cambio_plan + ";");
                    } else {
                        sql.add("update tbl_instalacion_cambio_plan set id_plan_servicio=" + id_plan_servicio + ",tipo_instalacioncp='" + idtipoins + "',plan_pendiente=true where id_instalacion_cambio_plan=" + id_instalacion_cambio_plan + ";");
                        ok = true;
                    }
                }
            } else {
                sql.add("update tbl_instalacion_cambio_plan set id_plan_servicio=" + id_plan_servicio + ",tipo_instalacioncp='" + idtipoins + "',plan_pendiente=true where id_instalacion_cambio_plan=" + id_instalacion_cambio_plan + ";");
                ok = true;
            }
            if (!ok) {
                ResultSet rs = this.consulta("select max(id_instalacion_cambio_plan) from tbl_instalacion_cambio_plan "
                        + "where id_instalacion=" + id_instalacion + " and eliminado=false");
                if (rs.next()) {
                    String id_icp = rs.getString(1) != null ? rs.getString(1) : "";
                    if (id_icp.compareTo(id_instalacion_cambio_plan) == 0) {
                        sql.add("update tbl_instalacion set id_plan_actual=" + id_plan_servicio + ",tipo_instalacion='" + idtipoins + "' where id_instalacion=" + id_instalacion);
                    }
                    rs.close();
                }
            }
            if (this.transacciones(sql)) {
                pk[0] = "ok";
                pk[1] = "" + costo_restante;
                pk[2] = "";
                return pk;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public String[] hayPreFacturasGeneradas(int anio, int mes, String id_instalacion) {
        String fecha_ini = anio + "-" + mes + "-01";
        String fecha_fin = anio + "-" + mes + "-" + Fecha.getUltimoDiaMes(anio, mes);
        String pkprefactura[] = new String[3];
        pkprefactura[0] = "-1";
        try {
            ResultSet res = this.consulta("SELECT id_prefactura,periodo,fecha_emision FROM tbl_prefactura WHERE fecha_prefactura between '" + fecha_ini + "' and '" + fecha_fin + "' and id_instalacion='" + id_instalacion + "' and fecha_emision is null;");
            if (res.next()) {
                pkprefactura[0] = (res.getString(1) != null) ? res.getString(1) : "-1";
                pkprefactura[1] = (res.getString(2) != null) ? res.getString(2) : "";
                pkprefactura[1] = (res.getString(3) != null) ? res.getString(3) : "";
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pkprefactura;
    }
    
//    public boolean cobrarPermanenciaMinima(String id_instalacion) 
//    {
//        boolean ok = false;
//        try {
//            ResultSet res = this.consulta("SELECT  FROM vta_instalacion_promocion_contrato WHERE  id_instalacion=" + id_instalacion);
//            if (res.next()) {
//                ok = (res.getString(1) != null) ? res.getBoolean(1) : false;
//                res.close();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return ok;
//    }

    
    public int getDiasAgregar( String id_instalacion, String estado_cliente, String convenio_pago, String id_sucursal) 
    {
        int diasagregar = 0;
        try {
            SimpleDateFormat formatear_fecha = new SimpleDateFormat("yyyy-MM-dd");
            Date fecha_prefactura;
            Date fecha_periodo;
            int anioprefactura = Fecha.getAnio();
            int mesprefactura = Fecha.getMes();
            int diaActual = Fecha.getDia();
            int numDiasActivo = diaActual;
            
            String periodo = anioprefactura + "-" + mesprefactura + "-01";
//            boolean sin_deuda = objPrefactura.instalacionSinDeuda(id_instalacion);
            int ultimodia = Fecha.getUltimoDiaMes(anioprefactura, mesprefactura);
            ///validar prefacturas anterior y periodo actual
            ////prefactura periodo anterior
            String ultimaprefactura[] = this.UltimaPreFacturasGenerada(id_instalacion, ultimodia, ultimodia);
//            boolean estado_instalacion = this.EstadosInstalacionCobro(id_instalacion, estado_cliente);
//            boolean estado_instalacion = this.EstadosInstalacionCobro(id_instalacion);
            
            ///prefactura periodo actual
            ////ultima prefactura es difectenta a -1
            if (ultimaprefactura[0].trim().compareTo("-1") != 0) {
                ///la prefactura es del periodo actual y no esta emitida con ffecha
                fecha_prefactura = formatear_fecha.parse(ultimaprefactura[1].trim().toString());
                fecha_periodo = formatear_fecha.parse(periodo.trim().toString());
                
                //  con prefactura actual pendiente
                if (fecha_prefactura.compareTo(fecha_periodo) == 0 && ultimaprefactura[2].trim().compareTo("") == 0) {
                    
                    // prepago
                    if( convenio_pago.compareTo("0")==0 ) { 
                        if( estado_cliente.trim().compareTo("a") == 0 ) {
                            diasagregar = numDiasActivo;
                        } 
                        // cortado quitos
                        if( estado_cliente.trim().compareTo("c") == 0 && (id_sucursal.compareTo("7")==0 || id_sucursal.compareTo("11")==0) ) {
                            diasagregar = 15;
                        } 
                        // cortado demas sucursales
                        if( estado_cliente.trim().compareTo("c") == 0 && id_sucursal.compareTo("7")!=0 && id_sucursal.compareTo("11")!=0 ) {
                            diasagregar = 5;
                        }
                    }
                    
                    // postpago
                    if( convenio_pago.compareTo("1")==0 ) { 
                        diasagregar = numDiasActivo;
                    }
                    
                //caso contrario si no cuadran los periodos es un periodo mas antes y tiene q pagar y generamos una nueva con periodo actual    
                } else if (fecha_prefactura.compareTo(fecha_periodo) == 0 && ultimaprefactura[2].trim().compareTo("") != 0) {
                    ////se modifica el dia de suspencion + 15 dias 
                    if (diaActual > 15) {

                            ///obtenemos los datos de la prefactura nueva con periodo actual 
                            String hayprefactura[] = this.hayPreFacturasGeneradas(anioprefactura, mesprefactura, id_instalacion);
                            ///validamos la prefactura si todo es correcto
                            if (hayprefactura[0].trim().compareTo("-1") != 0) {
                                
                                // prepago
//                                if( convenio_pago.compareTo("0")==0 ) { 
//                                    diasagregar = diasadional;
//                                }

                                // postpago
                                if( convenio_pago.compareTo("1")==0 ) { 
                                    if( estado_cliente.trim().compareTo("a") == 0 ) {
                                        diasagregar = numDiasActivo;
                                    }
                                    // cortado quitos
                                    if( estado_cliente.trim().compareTo("c") == 0 && (id_sucursal.compareTo("7")==0 || id_sucursal.compareTo("11")==0) ) {
                                        diasagregar = 15;
                                    } 
                                    // cortado demas sucursales
                                    if( estado_cliente.trim().compareTo("c") == 0 && id_sucursal.compareTo("7")!=0 && id_sucursal.compareTo("11")!=0 ) {
                                        diasagregar = 5;
                                    }
                                }

                            }

                    // cuando no hay que cobrar
//                    } else {
                    }
                    
                } else {    // si no hay le generamos la nueva prefactura
                    
                        ///obtenemos los datos de la prefactura nueva con periodo actual 
                        String hayprefactura[] = this.hayPreFacturasGeneradas(anioprefactura, mesprefactura, id_instalacion);
                        ///validamos la prefactura si todo es correcto
                        if (hayprefactura[0].trim().compareTo("-1") != 0) {
                            
                            // prepago
//                            if( convenio_pago.compareTo("0")==0 ) { 
//                                diasagregar = diasadional;
//                            }

                            // postpago
                            if( convenio_pago.compareTo("1")==0 ) { 
                                if( estado_cliente.trim().compareTo("a") == 0 ) {
                                    diasagregar = numDiasActivo;
                                } 
                                // cortado quitos
                                if( estado_cliente.trim().compareTo("c") == 0 && (id_sucursal.compareTo("7")==0 || id_sucursal.compareTo("11")==0) ) {
                                    diasagregar = 15;
                                } 
                                // cortado demas sucursales
                                if( estado_cliente.trim().compareTo("c") == 0 && id_sucursal.compareTo("7")!=0 && id_sucursal.compareTo("11")!=0 ) {
                                    diasagregar = 5;
                                }
                            }
                    
                        }
                    
                }
                
            } else {  //    si no hay datos de prefacturas anteriores
                
                    ///obtenemos los datos de la prefactura nueva con periodo actual 
                    String hayprefactura[] = this.hayPreFacturasGeneradas(anioprefactura, mesprefactura, id_instalacion);
                    ///validamos la prefactura si todo es correcto
                    if (hayprefactura[0].trim().compareTo("-1") != 0) {
                        
                        // prepago
                        if( convenio_pago.compareTo("0")==0 ) { 
                            if( estado_cliente.trim().compareTo("a") == 0 ) {
                                diasagregar = numDiasActivo;
                            } 
                            // cortado quitos
                            if( estado_cliente.trim().compareTo("c") == 0 && (id_sucursal.compareTo("7")==0 || id_sucursal.compareTo("11")==0) ) {
                                diasagregar = 15;
                            } 
                            // cortado demas sucursales
                            if( estado_cliente.trim().compareTo("c") == 0 && id_sucursal.compareTo("7")!=0 && id_sucursal.compareTo("11")!=0 ) {
                                diasagregar = 5;
                            }
                        }

                        // postpago
                        if( convenio_pago.compareTo("1")==0 ) { 
                            diasagregar = numDiasActivo;
                        }
                            
                    }
                
            }
            
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        }
        return diasagregar;
    }
    
    public String SuspencionTemporal(PreFactura objPrefactura, String id_instalacion, String id_suspencion, int tiempo, String estado_cliente, int diasadional, String convenio_pago, String id_sucursal) {
        boolean ok = false;
        String resultado = "";
        String okprefactura = "-1";
        try {
            SimpleDateFormat formatear_fecha = new SimpleDateFormat("yyyy-MM-dd");
            Date fecha_prefactura;
            Date fecha_periodo;
            int anioprefactura = Fecha.getAnio();
            int mesprefactura = Fecha.getMes();
            int diaActual = Fecha.getDia();
            int numDiasActivo = diaActual;
            int diasagregar = 0;
            String periodo = anioprefactura + "-" + mesprefactura + "-01";
//            boolean sin_deuda = objPrefactura.instalacionSinDeuda(id_instalacion);
            int ultimodia = Fecha.getUltimoDiaMes(anioprefactura, mesprefactura);
            String detalle_suspencion = diasadional == 15 ? " MAS DIAS DE SUSPENCIÓN DEFINITIVAS" : "";
            ///validar prefacturas anterior y periodo actual
            ////prefactura periodo anterior
            String ultimaprefactura[] = this.UltimaPreFacturasGenerada(id_instalacion, ultimodia, ultimodia);
//            boolean estado_instalacion = this.EstadosInstalacionCobro(id_instalacion, estado_cliente);
//            boolean estado_instalacion = this.EstadosInstalacionCobro(id_instalacion);
            
            ///prefactura periodo actual
            ////ultima prefactura es difectenta a -1
            if (ultimaprefactura[0].trim().compareTo("-1") != 0) {
                ///la prefactura es del periodo actual y no esta emitida con ffecha
                fecha_prefactura = formatear_fecha.parse(ultimaprefactura[1].trim().toString());
                fecha_periodo = formatear_fecha.parse(periodo.trim().toString());
                
                //  con prefactura actual pendiente
                if (fecha_prefactura.compareTo(fecha_periodo) == 0 && ultimaprefactura[2].trim().compareTo("") == 0) {
                    
                    // prepago
                    if( convenio_pago.compareTo("0")==0 ) { 
                        if( estado_cliente.trim().compareTo("a") == 0 ) {
                            diasagregar = numDiasActivo + diasadional;
                        } 
                        // cortado quitos
                        if( estado_cliente.trim().compareTo("c") == 0 && (id_sucursal.compareTo("7")==0 || id_sucursal.compareTo("11")==0) ) {
                            diasagregar = 15 + diasadional;
                        } 
                        // cortado demas sucursales
                        if( estado_cliente.trim().compareTo("c") == 0 && id_sucursal.compareTo("7")!=0 && id_sucursal.compareTo("11")!=0 ) {
                            diasagregar = 5 + diasadional;
                        }
                    }
                    
                    // postpago
                    if( convenio_pago.compareTo("1")==0 ) { 
                        if( estado_cliente.trim().compareTo("a") == 0 ) {
                            diasagregar = numDiasActivo + diasadional;
                        } 
                        // cortado quitos
                        if( estado_cliente.trim().compareTo("c") == 0 && (id_sucursal.compareTo("7")==0 || id_sucursal.compareTo("11")==0) ) {
                            diasagregar = 15 + diasadional;
                        } 
                        // cortado demas sucursales
                        if( estado_cliente.trim().compareTo("c") == 0 && id_sucursal.compareTo("7")!=0 && id_sucursal.compareTo("11")!=0 ) {
                            diasagregar = 5 + diasadional;
                        }
                    }
//                    diasagregar = ( (estado_cliente.trim().compareTo("c") == 0 || estado_cliente.trim().compareTo("s") == 0) && convenio_pago.compareTo("1")==0) ? diasadional : numDiasActivo + diasadional;
//                    diasagregar = (estado_cliente.trim().compareTo("a") == 0 && estado_instalacion == false ? diasadional : diasagregar);
                    ok = this.PrefacturaSuspencionTemporal(ultimaprefactura[0], (diasagregar), false, id_suspencion, tiempo, id_instalacion, detalle_suspencion);
                    okprefactura = ultimaprefactura[0];
                    
                //caso prefactura actual pagada generamos una nueva con periodo actual    
                } else if (fecha_prefactura.compareTo(fecha_periodo) == 0 && ultimaprefactura[2].trim().compareTo("") != 0) {
                    ////se modifica el dia de suspencion + 15 dias 
                    if (diaActual > 15) {

                        if (objPrefactura.generarPreFacturaPorSuspension(Integer.toString(anioprefactura), Integer.toString(mesprefactura), id_instalacion)) {
                            ///obtenemos los datos de la prefactura nueva con periodo actual 
                            String hayprefactura[] = this.hayPreFacturasGeneradas(anioprefactura, mesprefactura, id_instalacion);
                            ///validamos la prefactura si todo es correcto
                            if (hayprefactura[0].trim().compareTo("-1") != 0) {
                                
                                // prepago
                                if( convenio_pago.compareTo("0")==0 ) {     
                                    //  se cobra solo el proporcional
                                    diasagregar = (diaActual + 15) - Fecha.getUltimoDiaMes( Fecha.getAnio(), Fecha.getMes() );
                                }

                                // postpago
                                if( convenio_pago.compareTo("1")==0 ) { 
                                    if( estado_cliente.trim().compareTo("a") == 0 ) {
                                        diasagregar = numDiasActivo + diasadional;
                                    }
                                    // cortado quitos
                                    if( estado_cliente.trim().compareTo("c") == 0 && (id_sucursal.compareTo("7")==0 || id_sucursal.compareTo("11")==0) ) {
                                        diasagregar = 15 + diasadional;
                                    } 
                                    // cortado demas sucursales
                                    if( estado_cliente.trim().compareTo("c") == 0 && id_sucursal.compareTo("7")!=0 && id_sucursal.compareTo("11")!=0 ) {
                                        diasagregar = 5 + diasadional;
                                    }
                                }

                                ////adicionamos solo 15 dias a la nueva prefactura
//                                diasagregar = ( (estado_cliente.trim().compareTo("c") == 0 || estado_cliente.trim().compareTo("s") == 0) && convenio_pago.compareTo("1")==0) ? diasadional : diasadional - (30 - (numDiasActivo > 30 ? 30 : numDiasActivo));
//                                diasagregar = (estado_cliente.trim().compareTo("a") == 0 && estado_instalacion == false ? diasadional : diasagregar);
                                
                                ok = this.PrefacturaSuspencionTemporal(hayprefactura[0], (diasagregar), false, id_suspencion, tiempo, id_instalacion, detalle_suspencion);
                                okprefactura = hayprefactura[0];
                                if(!ok){
                                    okprefactura = "-1";
                                }
                                
                            }

                        }
                    // cuando no hay que cobrar
//                    } else {
                    }
                    
                } else {    // si no hay le generamos la nueva prefactura
                    
                    if (objPrefactura.generarPreFacturaPorSuspension(Integer.toString(anioprefactura), Integer.toString(mesprefactura), id_instalacion)) {
                        ///obtenemos los datos de la prefactura nueva con periodo actual 
                        String hayprefactura[] = this.hayPreFacturasGeneradas(anioprefactura, mesprefactura, id_instalacion);
                        ///validamos la prefactura si todo es correcto
                        if (hayprefactura[0].trim().compareTo("-1") != 0) {
                            
                            // prepago
                            if( convenio_pago.compareTo("0")==0 ) { 
                                diasagregar = diasadional;
                            }

                            // postpago
                            if( convenio_pago.compareTo("1")==0 ) { 
                                if( estado_cliente.trim().compareTo("a") == 0 ) {
                                    diasagregar = numDiasActivo + diasadional;
                                } 
                                // cortado quitos
                                if( estado_cliente.trim().compareTo("c") == 0 && (id_sucursal.compareTo("7")==0 || id_sucursal.compareTo("11")==0) ) {
                                    if( Fecha.getDifNumMeses( periodo, Fecha.getFecha("ISO") ) <= 1 && diaActual <= 15 ) {
                                        diasagregar = 15 + 15;
                                    }
                                    if( Fecha.getDifNumMeses( periodo, Fecha.getFecha("ISO") ) <= 1 && diaActual > 15 ) {
                                        diasagregar = 15 + 15;
                                    }
                                } 
                                // cortado demas sucursales
                                if( estado_cliente.trim().compareTo("c") == 0 && id_sucursal.compareTo("7")!=0 && id_sucursal.compareTo("11")!=0 ) {
                                    if( Fecha.getDifNumMeses( periodo, Fecha.getFecha("ISO") ) <= 1 && diaActual <= 5 ) {
                                        diasagregar = 5 + 15;
                                    }
                                    if( Fecha.getDifNumMeses( periodo, Fecha.getFecha("ISO") ) <= 1 && diaActual > 5 ) {
                                        diasagregar = 5 + 15;
                                    }
                                }
                            }
                    
                            ////adicionamos solo 15 dias a la nueva prefactura
//                            diasagregar = ( (estado_cliente.trim().compareTo("c") == 0 || estado_cliente.trim().compareTo("s") == 0) && convenio_pago.compareTo("1")==0) ? diasadional : numDiasActivo + diasadional;
//                            diasagregar = (estado_cliente.trim().compareTo("a") == 0 && estado_instalacion == false ? diasadional : diasagregar);
                            ok = this.PrefacturaSuspencionTemporal(hayprefactura[0], (diasagregar), false, id_suspencion, tiempo, id_instalacion, detalle_suspencion);
                            okprefactura = hayprefactura[0];
                        }
                    }
                    
                }
                
            } else {  //    si no hay datos de prefacturas anteriores
                
                ///validamos la prefactura
                if (objPrefactura.generarPreFacturaPorSuspension(Integer.toString(anioprefactura), Integer.toString(mesprefactura), id_instalacion)) {
                    ///obtenemos los datos de la prefactura nueva con periodo actual 
                    String hayprefactura[] = this.hayPreFacturasGeneradas(anioprefactura, mesprefactura, id_instalacion);
                    ///validamos la prefactura si todo es correcto
                    if (hayprefactura[0].trim().compareTo("-1") != 0) {
                        
                        // prepago
                        if( convenio_pago.compareTo("0")==0 ) { 
                            if( estado_cliente.trim().compareTo("a") == 0 ) {
                                diasagregar = numDiasActivo + diasadional;
                            } 
                            // cortado quitos
                            if( estado_cliente.trim().compareTo("c") == 0 && (id_sucursal.compareTo("7")==0 || id_sucursal.compareTo("11")==0) ) {
                                diasagregar = 15 + diasadional;
                            } 
                            // cortado demas sucursales
                            if( estado_cliente.trim().compareTo("c") == 0 && id_sucursal.compareTo("7")!=0 && id_sucursal.compareTo("11")!=0 ) {
                                diasagregar = 5 + diasadional;
                            }
                        }

                        // postpago
                        if( convenio_pago.compareTo("1")==0 ) { 
                            diasagregar = numDiasActivo + diasadional;
                        }
                            
                        ////adicionamos solo 15 dias a la nueva prefactura
//                        diasagregar = ( (estado_cliente.trim().compareTo("c") == 0 || estado_cliente.trim().compareTo("s") == 0) && convenio_pago.compareTo("1")==0) ? diasadional : numDiasActivo + diasadional;
//                        diasagregar = (estado_cliente.trim().compareTo("a") == 0 && estado_instalacion == false ? diasadional : diasagregar);
                        ok = this.PrefacturaSuspencionTemporal(hayprefactura[0], (diasagregar), false, id_suspencion, tiempo, id_instalacion, detalle_suspencion);
                        okprefactura = hayprefactura[0];
                    }
                }
                
            }
            
            resultado = String.valueOf(ok);
            if (okprefactura.trim().compareTo("-1") == 0 || okprefactura.trim().compareTo("") == 0) {
                resultado = this.getPromocionCompleta(id_instalacion, okprefactura);
//                if (sql1.trim().compareTo("") != 0) {
//                    int anioprefactura1 = Fecha.getAnio();
//                    int mesprefactura1 = Fecha.getMes();
//                    long pk = objPrefactura.generarPreFacturaRubro(Integer.toString(anioprefactura1), Integer.toString(mesprefactura1), id_instalacion);
//                    if (pk > 0) {
//                        this.PrefacturaSuspencionTemporal("" + pk, 0, false, id_suspencion, tiempo, id_instalacion, "");
//                    }

//                }
            }
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        } finally {
            objPrefactura.cerrar();
        }
        return resultado;
    }

    public boolean PrefacturaSuspencionTemporal(String id_prefactura, int diasnuevo, boolean anular, String id_suspencion, int tiempo, String id_instalacion, String adicional) {
        List sql = new ArrayList();
        //actualizar los dias de la prefactura y recalcular
        if (!anular && diasnuevo > 0) {
            /////cobrar instalacion
            String sql1 = this.getPromocionCompleta(id_instalacion, id_prefactura);
            if (sql1.trim().compareTo("") != 0) {
                sql.add(sql1);
            }
            ////fin de cobrar la instalacion
            sql.add("update tbl_prefactura set dias_conexion='" + diasnuevo + "',recalcular=true,detalle_suspencion='" + adicional + "' where id_prefactura='" + id_prefactura + "';");
            sql.add("update tbl_instalacion_suspension set id_prefactura='" + id_prefactura + "' where id_instalacion_suspension='" + id_suspencion + "';");
            if (tiempo == -2) {
                sql.add("update tbl_instalacion set estado_servicio='r' where id_instalacion='" + id_instalacion + "';");
            }
            return transacciones(sql);
//            if (transacciones(sql)) {
//                this.consulta("select proc_calcularPreFactura(" + id_prefactura + ", false);");
//                return true;
//            }
        }
        if (diasnuevo == 0) {
            this.ejecutar("delete from tbl_prefactura where id_prefactura=" + id_prefactura);
        }
        return false;
    }

    public String[] UltimaPreFacturasGenerada(String id_instalacion, int anio, int mes) {
        String pkprefactura[] = new String[3];
        pkprefactura[0] = "-1";
        try {
            ResultSet res = this.consulta("select id_prefactura,periodo,fecha_emision from tbl_prefactura"
                    + " where id_prefactura=(select max(id_prefactura) from tbl_prefactura where id_instalacion='" + id_instalacion + "');");
            if (res.next()) {
                pkprefactura[0] = (res.getString(1) != null) ? res.getString(1) : "-1";
                pkprefactura[1] = (res.getString(2) != null) ? res.getString(2) : "";
                pkprefactura[2] = (res.getString(3) != null) ? res.getString(3) : "";
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pkprefactura;
    }

    public String[] PrefacturaSuspencion(String id_suspencion) {
        String pkprefactura[] = new String[4];
        pkprefactura[0] = "-1";
        try {
            ResultSet res = this.consulta("select p.dias_conexion,p.fecha_emision,p.periodo,p.id_instalacion from tbl_prefactura p where"
                    + " id_prefactura=(select s.id_prefactura from tbl_instalacion_suspension s where s.id_instalacion_suspension='" + id_suspencion + "');");
            if (res.next()) {
                pkprefactura[0] = (res.getString(1) != null) ? res.getString(1) : "-1";
                pkprefactura[1] = (res.getString(2) != null) ? res.getString(2) : "";
                pkprefactura[2] = (res.getString(3) != null) ? res.getString(3) : "";
                pkprefactura[3] = (res.getString(4) != null) ? res.getString(4) : "-1";
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pkprefactura;
    }

    public int PrefacturaPeriodo(String periodo, String id_instalacion) {
        int contador = 0;
        try {
            ResultSet res = this.consulta("select count(*)as contador from tbl_prefactura where periodo='" + periodo + "' and id_instalacion='" + id_instalacion + "'");
            if (res.next()) {
                contador = (res.getString(1) != null) ? res.getInt(1) : 0;
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contador;
    }

    public boolean SuspencionTemporalEliminar(PreFactura objPrefactura, String clave) {
        boolean resultado = false;
        try {
            SimpleDateFormat formatear_fecha = new SimpleDateFormat("yyyy-MM-dd");
            Date fecha_prefactura;
            Date fecha_periodo;
            ////
            int anioprefactura = Fecha.getAnio();
            int mesprefactura = Fecha.getMes();
            String periodo = anioprefactura + "-" + mesprefactura + "-01";
            ///
            String periodo1 = objPrefactura.getUltimoPeriodo();
            String Prefactura[] = this.PrefacturaSuspencion(clave);
            String tipo = this.TipoSuspencionEliminar(clave);
            //
            if (tipo.trim().compareTo("d") == 0) {
                if (Prefactura[0].trim().compareTo("-1") != 0 && Prefactura[1].trim().compareTo("") != 0) {
                    fecha_prefactura = formatear_fecha.parse(Prefactura[2].trim().toString());
                    fecha_periodo = formatear_fecha.parse(periodo.trim().toString());
                    int dias = Integer.parseInt(Prefactura[0]);
                    //
                    if (fecha_periodo.compareTo(fecha_prefactura) == 0) {
                        ////verificamos si ese periodo de prefactra tiene dos prefacturas iguales
                        if (this.PrefacturaPeriodo(Prefactura[2], Prefactura[3]) > 1) {
                            ///si existe daremos dias de descuento 
                            this.ejecutar("insert into tbl_prefactura_dias_conexion (id_instalacion,periodo,num_dias)values('" + Prefactura[3] + "','" + periodo1 + "','" + (dias - 30) + "');");
                            resultado = true;
                        }///si no existe mas de dos prefacturas del mismo periodo verificaremos los dias
                        else {
                            ///si hay mas de 30 dias daremos dias de descuento
                            this.ejecutar("insert into tbl_prefactura_dias_conexion (id_instalacion,periodo,num_dias)values('" + Prefactura[3] + "','" + periodo1 + "','" + (30 - dias) + "');");
                            resultado = true;
                        }
                    } ////si el periodo de quitar suspencion es menor al de la prefactura
                    else if (fecha_periodo.before(fecha_prefactura) == true) {
                        ///si existe daremos dias de descuento 
                        this.ejecutar("insert into tbl_prefactura_dias_conexion (id_instalacion,periodo,num_dias)values('" + Prefactura[3] + "','" + periodo1 + "','" + (dias) + "');");
                        resultado = true;
                    } ////si la actual periodo de suspeion es mayor y diferencia de meses1
                    else if (fecha_periodo.before(fecha_prefactura) == false && ((fecha_periodo.getMonth() + 1) - (fecha_prefactura.getMonth() + 1)) == 1) {
                        ///si hay mas de 30 dias daremos dias de descuento
                        this.ejecutar("insert into tbl_prefactura_dias_conexion (id_instalacion,periodo,num_dias)values('" + Prefactura[3] + "','" + periodo1 + "','" + (30 - dias) + "');");
                        resultado = true;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        } finally {
            objPrefactura.cerrar();
        }
        return resultado;
    }

    public String TipoSuspencionEliminar(String clave) {
        String tipo = "t";
        try {
            ResultSet res = this.consulta("select tipo from tbl_instalacion_suspension where id_instalacion_suspension='" + clave + "';");
            if (res.next()) {
                tipo = (res.getString(1) != null) ? res.getString(1) : "t";
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tipo;
    }

    public boolean EstadosInstalacionCobro(String id_instalacion, String estado_cliente) {
        boolean estado = true;
        String estado_ant = "";
        String estado_act = "";
        String fecha_realizada = "";
        try {
            ResultSet res = this.consulta("select estado_ant,estado_act,(extract(days from (now() -  fecha)))::int8 as dias_activos from tbl_instalacion_historial_estado "
                    + " where id_instalacion='" + id_instalacion + "'"
                    + " and id_instalacion_historial_estado=(select max(id_instalacion_historial_estado) from tbl_instalacion_historial_estado where id_instalacion='" + id_instalacion + "');");
            if (res.next()) {
                estado_ant = (res.getString(1) != null) ? res.getString(1) : "";
                estado_act = (res.getString(2) != null) ? res.getString(2) : "";
                fecha_realizada = (res.getString(3) != null) ? res.getString(3) : "";
                res.close();
            }
            if (estado_ant.trim().compareTo("") != 0 && estado_act.trim().compareTo("") != 0 && fecha_realizada.trim().compareTo("") != 0) {
                if ((estado_ant.trim().compareTo("c") == 0 || estado_ant.trim().compareTo("s") == 0) && estado_act.trim().compareTo("a") == 0) {
                    int dias_activo = Integer.parseInt(fecha_realizada);
                    estado = false;
                    if (dias_activo > 2) {
                        estado = true;
                    }
                } else if (estado_ant.trim().compareTo("a") == 0 && estado_act.trim().compareTo("a") == 0) {
                    estado = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return estado;
    }

    public boolean EstadosInstalacionCobro(String id_instalacion) {
        boolean estado = true;
        String estado_ant = "";
        String estado_act = "";
        String fecha_realizada = "";
        try {
            ResultSet res = this.consulta("select estado_ant,estado_act,(extract(days from (now() -  fecha)))::int8 as dias_activos from tbl_instalacion_suspension_estado where id_instalacion ='" + id_instalacion + "';");
            if (res.next()) {
                estado_ant = (res.getString(1) != null) ? res.getString(1) : "";
                estado_act = (res.getString(2) != null) ? res.getString(2) : "";
                fecha_realizada = (res.getString(3) != null) ? res.getString(3) : "";
                res.close();
            }
            if (estado_ant.trim().compareTo("") != 0 && estado_act.trim().compareTo("") != 0 && fecha_realizada.trim().compareTo("") != 0) {
                if ((estado_ant.trim().compareTo("c") == 0 || estado_ant.trim().compareTo("s") == 0) && estado_act.trim().compareTo("a") == 0) {
                    int dias_activo = Integer.parseInt(fecha_realizada);
                    estado = false;
                    if (dias_activo > 2) {
                        estado = true;
                    }
                } else if (estado_ant.trim().compareTo("a") == 0 && estado_act.trim().compareTo("a") == 0) {
                    estado = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return estado;
    }

    public List getIpsDisponible(int id_sucursal) {

        List<String> ips = new ArrayList();
        try {
            ResultSet rsIps = this.consulta("select ips from tbl_ips_libres where ips::varchar like '192.168.%' and id_sucursal=" + id_sucursal + " limit 20;");
            if (rsIps != null) {
                while (rsIps.next()) {
                    String ip = rsIps.getString("ips") != null ? rsIps.getString("ips") : "";
                    ips.add(ip);
                }
                rsIps.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ips;
    }

    public List getIpsDisponible(int id_sucursal, String inicio) {

        List<String> ips = new ArrayList();
        try {
            ResultSet rsIps = this.consulta("select ips from tbl_ips_libres where ips::varchar like '" + inicio + "%' and id_sucursal=" + id_sucursal + " limit 20;");
            if (rsIps != null) {
                while (rsIps.next()) {
                    String ip = rsIps.getString("ips") != null ? rsIps.getString("ips") : "";
                    ips.add(ip);
                }
                rsIps.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ips;
    }

    public String insertar(String sql) {
        String pk = "-1";
        Connection con = this.getConexion();
        try {
            con.setAutoCommit(false);
            Statement st = con.createStatement();
            int r = st.executeUpdate(this.decodificarURI(sql), Statement.RETURN_GENERATED_KEYS);
            if (r > 0) {
                ResultSet rs = st.getGeneratedKeys();
                if (rs.next()) {
                    pk = rs.getString(1) != null ? rs.getString(1) : "-1";
                    rs.close();
                }
            }
            con.commit();
            st.close();
        } catch (Exception e) {
            this.setError(e.getMessage());
            e.printStackTrace();
            pk = "-1";
            try {
                con.rollback();
            } catch (Exception se) {
                se.printStackTrace();
            }
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(Instalacion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return pk;
    }

    public boolean enConflictoNuevoServicio(String id_instalacion, String id_servicio, String id_instalacion_servicio) {
        ResultSet res = this.consulta("select * from vta_instalacion_servicio where id_instalacion='" + id_instalacion + "' and id_servicio_instalacion='" + id_servicio + "' and id_instalacion_servicio<>'" + id_instalacion_servicio + "';");
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

    public String insertarNuevoServicio(String id_instalacion, String id_servicio, String fecha_servicio, String cantidad) {
        String pk = this.insert("insert into tbl_instalacion_servicio(id_instalacion, id_servicio_instalacion, fecha_servicio,cantidad) "
                + "values('" + id_instalacion + "', " + id_servicio + ", '" + fecha_servicio + "','" + cantidad + "');");
        return pk;
    }

    public boolean actualizarNuevoServicio(String id) {
        return this.ejecutar("");
    }

    public boolean eliminarNuevoServicio(String id) {
        return this.ejecutar("update tbl_instalacion_servicio set eliminado=true,fecha_servicio_anulacion=now()::date where id_instalacion_servicio='" + id + "';");
    }

    public ResultSet getCertificados() {
        return this.consulta("select id_certificados_isp,nombre_certificado from tbl_certificados_isp where eliminado=false;");
    }

    public ResultSet getCertificados(String id_instalacion) {
        return this.consulta("select * from vta_instalacion_certificado where id_instalacion='" + id_instalacion + "';");
    }

    public ResultSet getCertificado(String id_instalacion_certificado) {
        return this.consulta("select * from vta_instalacion_certificado where id_instalacion_certificado='" + id_instalacion_certificado + "';");
    }

    public boolean enConflictoNuevoCertificado(String id_instalacion, String id_certificados_isp, String id_instalacion_certificado) {
        ResultSet res = this.consulta("select * from vta_instalacion_certificado where id_instalacion='" + id_instalacion + "' and id_certificados_isp='" + id_certificados_isp + "' and id_instalacion_certificado<>'" + id_instalacion_certificado + "';");
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
    
    public int numCertificadosEnAnio(String id_instalacion, String id_certificados_isp) 
    {
        int num=0;
        try {
            ResultSet res = this.consulta("with I as(\n" +
                "	select id_instalacion, ( '2024-' || date_part('month', fecha_instalacion) || '-' || date_part('day', fecha_instalacion) )::date as fecha_instalacion \n" +
                "	from tbl_instalacion where id_instalacion = " + id_instalacion + 
                "), \n" +
                "I1 as ( \n" +
                "	select I.id_instalacion, case when I.fecha_instalacion>=now()::date then (I.fecha_instalacion - '1 year'::interval)::date else I.fecha_instalacion end as fecha_instalacion \n" +
                "	from I \n" +
                ") \n" +
                "select count(C.id_instalacion) from tbl_instalacion_certificado as C inner join I1 on C.id_instalacion =I1.id_instalacion \n" +
                "where not C.eliminado and C.id_instalacion = " + id_instalacion + " and C.id_certificados_isp in(" + id_certificados_isp + ") and I1.fecha_instalacion <= now()::date;");
            if ( res.next() ) {
                num = res.getString(1)!=null ? res.getInt(1) : 0;
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    public String insertarNuevoCertificado(String id_instalacion, String id_certificados_isp, String fecha_creada, String usuario_creado) {
        String pk = this.insert("insert into tbl_instalacion_certificado(id_instalacion, id_certificados_isp, fecha_creada,usuario_creado) "
                + "values('" + id_instalacion + "', " + id_certificados_isp + ", '" + fecha_creada + "', '" + usuario_creado + "');");
        return pk;
    }

    public String insertarNuevoCertificado(String id_instalacion, String id_certificados_isp, String fecha_creada, String usuario_creado, String observaciones, String tipo_instalacion, String id_plan,
            String documento_indice, String pais, String id_provincia, String id_canton, String id_parroquia, String id_sector, String direccion, String cliente_actualizado, String instalacion_actualizado, String modalidad_pago,
            String id_promocion, String id_provincia_cliente, String id_canto_cliente, String direccion_cliente, String tipo_bloqueo, String caconvenio_pago) {
        String pk = this.insert("insert into tbl_instalacion_certificado(id_instalacion, id_certificados_isp, fecha_creada, usuario_creado,observaciones, tipo_instalacion, id_plan, "
                + " documento_indice, pais, id_provincia, id_canton, id_parroquia, id_sector, direccion, cliente_actualizado, instalacion_actualizado, modalidad_pago, "
                + " id_promocion, id_provincia_cliente, id_canto_cliente, direccion_cliente,tipo_bloqueo,modalidad_pago_ant) "
                + " values('" + id_instalacion + "', " + id_certificados_isp + ", '" + fecha_creada + "', '" + usuario_creado + "','" + observaciones + "',"
                + " '" + tipo_instalacion + "','" + id_plan + "','" + documento_indice + "','" + pais + "','" + id_provincia + "','" + id_canton + "','" + id_parroquia + "',"
                + " '" + id_sector + "','" + direccion + "','" + cliente_actualizado + "','" + instalacion_actualizado + "','" + modalidad_pago + "','" + id_promocion + "',"
                + " '" + id_provincia_cliente + "','" + id_canto_cliente + "','" + direccion_cliente + "','" + tipo_bloqueo + "','" + caconvenio_pago + "');");
        return pk;
    }
    
    public String insertarNuevoCertificadoRenovacion(String id_instalacion, String id_certificados_isp, String fecha_creada, String usuario_creado, String observaciones) {
        String pk = this.insert("insert into tbl_instalacion_certificado(id_instalacion, id_certificados_isp, fecha_creada, usuario_creado, observaciones, id_plan, tipo_instalacion, "
                + "pais, id_provincia, id_canton, id_parroquia, id_sector, direccion, cliente_actualizado, instalacion_actualizado, modalidad_pago, id_provincia_cliente, id_canto_cliente, direccion_cliente) "
                + "select '" + id_instalacion + "', " + id_certificados_isp + ", '" + fecha_creada + "', '" + usuario_creado + "','" + observaciones + "', I.id_plan_actual, I.tipo_instalacion, "
                + "'ECUADOR', I.id_provincia, I.id_ciudad, I.id_parroquia, I.id_sector, I.direccion_instalacion, true, true, I.convenio_pago, C.id_provincia, C.id_ciudad, C.direccion "
                + "from tbl_instalacion as I inner join tbl_cliente as C on I.id_cliente=C.id_cliente where id_instalacion = " + id_instalacion);
        if(pk.compareTo("-1")!=0){
            if( !this.ejecutar("update tbl_instalacion set renovacion_tiempo_permanencia=(select valor::int from tbl_configuracion where parametro='renovar_contrato_permanencia'), "
                    + "renovacion_costo_facturado=(select valor::numeric from tbl_configuracion where parametro='renovar_contrato_costo') where id_instalacion=" + id_instalacion) ) {
                this.ejecutar("delete from tbl_instalacion_certificado where id_instalacion_certificado = " + pk);
                pk = "-1";
            }
        }
        return pk;
    }

    public boolean actualizarNuevoCertificado(String id) {
        return this.ejecutar("");
    }

    public boolean eliminarNuevoCertificado(String id, String usuario) {
        return this.ejecutar("update tbl_instalacion_certificado set eliminado=true,fecha_anulada=now()::date,usuario_anulado='" + usuario + "' where id_instalacion_certificado='" + id + "';");
    }

    public ResultSet getEstados() {
        return this.consulta("select * from vta_estados_marketing;");
    }

    public boolean getClienteEdadConadis(String id_cliente) {
        ResultSet res = this.consulta("select * from vta_instalacion where id_cliente='" + id_cliente + "' and (edad>=65 or trim(carne_conadis)<>'') and estado_servicio not in ('t');");
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

    public ResultSet getPromocionInstalacion(String id_instalacion) {
        return this.consulta("select * from vta_instalacion_promocion_contrato where id_instalacion='" + id_instalacion + "';");
    }

    public String[] getPromocionTiempo(String id_instalacion) {
        String tiempos[] = {"", "", "", "n", "n"};
        boolean promocion = false;
        try {
            ResultSet rs = this.consulta("select men_tiempo_de_permanencia_min,tiempo_trascurridotmp,promociontmp from vta_instalacion_promocion_contrato where id_instalacion='" + id_instalacion + "';");
            if (rs.next()) {
                tiempos[0] = (rs.getString(1) != null ? rs.getString(1) : "");
                tiempos[1] = (rs.getString(2) != null ? rs.getString(2) : "");
                tiempos[2] = (rs.getString(3) != null ? rs.getString(3) : "");
                promocion = true;
                rs.close();
            }
            if (promocion) {
                tiempos[0] = (tiempos[0].trim().compareTo("") != 0 ? tiempos[0] : "0");
                tiempos[1] = (tiempos[1].trim().compareTo("") != 0 ? tiempos[1] : "0");
                double uno = Double.parseDouble(tiempos[0]);
                double dos = Double.parseDouble(tiempos[1]);
                tiempos[3] = "s";
                if (dos > uno) {
                    tiempos[4] = "s";
                }
            }
        } catch (Exception e) {

        }
        return tiempos;
    }

    public ResultSet getInstalacionesCliente(String id_cliente) {
        return this.consulta("select i.id_instalacion,(i.ip||' '||i.txt_estado_servicio)as instalacion from vta_instalacion as i where i.id_cliente='" + id_cliente + "';");
    }

    public boolean GetCumplioPromocionInstalacion(String id_instalacion) {
        ResultSet res = this.consulta("select  case when tiempo_trascurridotmp>=men_tiempo_de_permanencia_min then 'si' else 'no' end as tiempo_cumplido from vta_instalacion_promocion_contrato where id_instalacion='" + id_instalacion + "';");
        try {
            if (this.getFilas(res) > 0) {
                if (res.next()) {
                    String ok = (res.getString("tiempo_cumplido") != null ? res.getString("tiempo_cumplido") : "");
                    if (ok.trim().compareTo("no") == 0) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println("error al optener la promocion" + e.getMessage());
            return false;
        }

    }
    
    public boolean GetCumplioRenovacionContrato(String id_instalacion) 
    {
        boolean ok = true;
        try {
            ResultSet res = this.consulta("select (fecha_creada + (renovacion_tiempo_permanencia || ' month')::interval)::date > now()::date \n" +
                            "from tbl_instalacion_certificado as C inner join tbl_instalacion as I on C.id_instalacion = I.id_instalacion \n" +
                            "where id_certificados_isp = 11 and not C.eliminado and C.id_instalacion =" + id_instalacion + "  order by fecha_creada desc limit 1;");
            if (res.next()) {
                ok = (res.getString(1) != null ? res.getBoolean(1) : true);
                res.close();
            }
        } catch (Exception e) {
            System.out.println("error al obtener fecha de renovacion de contrato. " + e.getMessage());
        }
        return ok;
    }

    public boolean GetCumplioPromocionMigracion(String id_instalacion) {
        ResultSet res = this.consulta("select  case when tiempo_trascurridotmp>=men_tiempo_de_permanencia_min then 'si' else 'no' end as tiempo_cumplido from vta_certificado_promocion_contrato where id_instalacion='" + id_instalacion + "' and migracion_fibra=true and id_promocion<>-1 and id_certificados_isp1 in (4,5);");
        try {
            if (this.getFilas(res) > 0) {
                if (res.next()) {
                    String ok = (res.getString("tiempo_cumplido") != null ? res.getString("tiempo_cumplido") : "");
                    if (ok.trim().compareTo("no") == 0) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println("error al optener la promocion" + e.getMessage());
            return false;
        }

    }

    public boolean GetCumplioPromocionDomicilio(String id_instalacion) {
        ResultSet res = this.consulta("select  case when tiempo_trascurridotmp>=men_tiempo_de_permanencia_min then 'si' else 'no' end as tiempo_cumplido from vta_certificado_promocion_contrato where id_instalacion='" + id_instalacion + "' and cambio_domicilio=true and id_promocion<>-1 and id_certificados_isp1 in (2,3);");
        try {
            if (this.getFilas(res) > 0) {
                if (res.next()) {
                    String ok = (res.getString("tiempo_cumplido") != null ? res.getString("tiempo_cumplido") : "");
                    if (ok.trim().compareTo("no") == 0) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println("error al optener la promocion" + e.getMessage());
            return false;
        }

    }

    public ResultSet getPromocionesInstalaciones(int idSucursal) {
        return this.consulta("SELECT P.id_promocion, P.promocion, P.fecha_creacion, P.fecha_inicio, P.fecha_termino, P.inst_objetivo_es_porcentaje, P.inst_objetivo_a_cumplir, "
                + "P.inst_objetivo_basado_en, P.inst_costo_es_porcentaje, P.inst_costo_valor, P.inst_prepago, P.inst_postpago, P.men_tiempo_de_permanencia_min "
                + "FROM tbl_promocion as P inner join tbl_promocion_sucursal as PS on P.id_promocion=PS.id_promocion and case when fecha_inicio is not null then fecha_inicio else fecha_creacion end <= now() "
                + "where inst_objetivo_a_cumplir > 0 and P.cerrada=false and PS.id_sucursal=" + idSucursal + " order by promocion");
    }

    public String getPromocionCompleta(String id_instalacion, String id_prefactura) {
        String sql = "";
        if (this.GetCumplioPromocionInstalacion(id_instalacion)) {
            sql += "INSERT INTO tbl_prefactura_rubro( id_sucursal, id_rubro,id_instalacion, rubro,periodo, monto,tiporubro) "
                    + " select tmp.id_sucursal,25,tmp.id_instalacion,'Servicio de Instalacion',tmp.periodo,(select tmp1.costo_instalacion_cobro from  vta_instalacion_promocion_contrato as tmp1 where tmp1.id_instalacion=tmp.id_instalacion limit 1),'p' from vta_prefactura as tmp where tmp.id_prefactura='" + id_prefactura + "';";
        }
        //if (sql.trim().compareTo("") == 0) {
//            if (this.GetCumplioRenovacionContrato(id_instalacion)) {
//                sql += "INSERT INTO tbl_prefactura_rubro( id_sucursal, id_rubro, id_instalacion, rubro, periodo, monto, tiporubro) "
//                        + " select tmp.id_sucursal,25,tmp.id_instalacion,'Instalacion de equipos',tmp.periodo, renovacion_costo_facturado / 1.12 ,'p' from vta_prefactura as tmp where tmp.id_prefactura='" + id_prefactura + "';";
//            }
//            if (sql.trim().compareTo("") == 0) {
                if (this.GetCumplioPromocionMigracion(id_instalacion)) {
                    sql += "INSERT INTO tbl_prefactura_rubro( id_sucursal, id_rubro,id_instalacion, rubro,periodo, monto,tiporubro) "
                            + " select tmp.id_sucursal,19,tmp.id_instalacion,'Servicio de Migracion',tmp.periodo,(select tmp1.costo_instalacion_cobro from  vta_certificado_promocion_contrato as tmp1 where tmp1.id_instalacion=tmp.id_instalacion order by id_certificados_isp1 desc limit 1),'p' from vta_prefactura as tmp where tmp.id_prefactura='" + id_prefactura + "';";
                }
//                if (sql.trim().compareTo("") == 0) {
                    if (this.GetCumplioPromocionDomicilio(id_instalacion)) {
                        sql += "INSERT INTO tbl_prefactura_rubro( id_sucursal, id_rubro,id_instalacion, rubro,periodo, monto,tiporubro) "
                                + " select tmp.id_sucursal,16,tmp.id_instalacion,'Servicio de Cambio de domicilio',tmp.periodo,(select tmp1.costo_instalacion_cobro from  vta_certificado_promocion_contrato as tmp1 where tmp1.id_instalacion=tmp.id_instalacion order by id_certificados_isp1 desc limit 1),'p' from vta_prefactura as tmp where tmp.id_prefactura='" + id_prefactura + "';";
                    }
//                }
//            }
       // }
        return sql;
    }
    
    public String getPromocionCompleta(String idInstalacion) {
        String sql = "";
        if (this.GetCumplioPromocionInstalacion(idInstalacion)) {
            sql += "INSERT INTO tbl_prefactura_rubro( id_sucursal, id_rubro,id_instalacion, rubro,periodo, monto, tiporubro)  \n" +
                    "select id_sucursal, 25, id_instalacion, 'Servicio de Instalacion', date_trunc('month', now())::date, costo_instalacion_cobro, 'p' \n" +
                    "from  vta_instalacion_promocion_contrato where id_instalacion="+idInstalacion+" limit 1";
        }
        //if (sql.trim().compareTo("") == 0) {
//            if (this.GetCumplioRenovacionContrato(id_instalacion)) {
//                sql += "INSERT INTO tbl_prefactura_rubro( id_sucursal, id_rubro, id_instalacion, rubro, periodo, monto, tiporubro) "
//                        + " select tmp.id_sucursal,25,tmp.id_instalacion,'Instalacion de equipos',tmp.periodo, renovacion_costo_facturado / 1.12 ,'p' from vta_prefactura as tmp where tmp.id_prefactura='" + id_prefactura + "';";
//            }
//            if (sql.trim().compareTo("") == 0) {
                if (this.GetCumplioPromocionMigracion(idInstalacion)) {
                    sql += "INSERT INTO tbl_prefactura_rubro( id_sucursal, id_rubro,id_instalacion, rubro,periodo, monto,tiporubro) \n" +
                            "select id_sucursal, 19, id_instalacion, 'Servicio de Migracion', date_trunc('month', now())::date, costo_instalacion_cobro, 'p' \n" +
                            "from vta_certificado_promocion_contrato where id_instalacion="+idInstalacion+" order by id_certificados_isp1 desc limit 1";
                }
//                if (sql.trim().compareTo("") == 0) {
                    if (this.GetCumplioPromocionDomicilio(idInstalacion)) {
                        sql += "INSERT INTO tbl_prefactura_rubro( id_sucursal, id_rubro,id_instalacion, rubro,periodo, monto,tiporubro) \n" +
                                "select id_sucursal, 16, id_instalacion, 'Servicio de Cambio de domicilio', date_trunc('month', now())::date, costo_instalacion_cobro, 'p'\n" +
                                "from vta_certificado_promocion_contrato  where id_instalacion="+idInstalacion+" order by id_certificados_isp1 desc limit 1";
                    }
//                }
//            }
       // }
        return sql;
    }

    public ResultSet getInstalacionIptv(String wh) {
        wh = wh.toLowerCase();
        return this.consulta("select id_instalacion,ruc,razon_social,ip,direccion_instalacion from vta_instalacion where (lower(ruc) like '%" + wh + "%' or lower(razon_social) like '%" + wh + "%')");
    }

    public ResultSet getInstalacionRevision(String id) {
        return this.consulta("select * from vta_instalacion_aprobacion where id_instalacion='" + id + "';");
    }

    public boolean setRevisionInstalacion(String id, String estado, String observaciones, String usuario, String id_instalacion) {
        List sql = new ArrayList();
        sql.add("update tbl_instalacion_aprobacion set usuario='" + usuario + "',estado='" + estado + "',fecha_aprueba=now(),hora_aprueba=now(),observaciones='" + observaciones + "' where id_instalacion='" + id + "';");
        if (estado.trim().compareTo("n") == 0) {
            sql.add("update tbl_instalacion set estado_servicio ='t' where id_instalacion ='" + id_instalacion + "';");
            sql.add("update tbl_soporte set estado ='s',fecha_solucion =now(),hora_solucion =now(),alias_solucion ='" + usuario + "',diagnostico ='No existe cobertura' where id_instalacion ='" + id_instalacion + "';");
            sql.add("update tbl_orden_trabajo set estado ='9',usuario_solucion ='" + usuario + "',recomendacion ='No existe cobertura' ,fecha_solucion =now(),hora_solucion =now()  where id_instalacion ='" + id_instalacion + "';");
            sql.add("update tbl_contrato set terminado =true,anulado =true where id_instalacion ='" + id_instalacion + "';");
        }
        return this.transacciones(sql);
    }

    public String getIdPromocionInstalacion(String id_instalacion) {
        String id_promocion = "";
        try {
            ResultSet rs = this.consulta("select * from tbl_instalacion_promocion where id_instalacion ='" + id_instalacion + "';");
            if (rs.next()) {
                id_promocion = rs.getString("id_promocion") != null ? rs.getString("id_promocion") : "";
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id_promocion;
    }

    public ResultSet getInstalacionesCertificadoCliente(String id_cliente) {
        return this.consulta("select (i.id_instalacion||';i'),(i.ip||' '||i.txt_estado_servicio)as instalacion from vta_instalacion as i where i.id_cliente ='" + id_cliente + "' ");
    }
    
//    public ResultSet getInstalacionesCertificadoCliente(String id_cliente) {
//        return this.consulta("select (ic.id_instalacion_certificado||';c'),(ic.nombre_certificado||' '||ic.fecha_creada||' '||coalesce(ic.hora_creacion::text,'')) from vta_instalacion_certificado as ic where  ic.id_cliente='" + id_cliente + "' and (ic.estado_orden is not null) "
//                + " union all "
//                + " select (i.id_instalacion||';i'),(i.ip||' '||i.txt_estado_servicio)as instalacion from vta_instalacion as i where i.id_cliente ='" + id_cliente + "' ");
//    }

    public boolean actualizar(String id, String campo, String valor, String campo1, String valor1) {
        return this.ejecutar("UPDATE tbl_instalacion SET " + campo + "='" + valor + "'," + campo1 + "='" + valor1 + "' WHERE id_instalacion=" + id + ";");
    }

    public boolean actualizarCobro(String id, String valor) {
        return this.ejecutar("UPDATE tbl_instalacion SET cobrar='" + valor + "',motivo_no_cobrar =null WHERE id_instalacion=" + id + ";");
    }

    public ResultSet getInstalacionesContratosExternos(String idSucursal, String texto) {
        
        return this.consulta("select id_instalacion, txt_sucursal, razon_social, ip from vta_instalacion where " + (idSucursal.compareTo("-0")!=0 ? "id_sucursal="+idSucursal+" and " : "") + 
                " (lower(razon_social) like '%" + texto.toLowerCase() + "%' or ip::varchar like '" + texto + "%') order by txt_sucursal,razon_social limit 20");
    }
    
    
}
