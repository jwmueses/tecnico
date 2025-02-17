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

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Traspaso extends DataBase {

    public Traspaso(String m, int p, String db, String u, String c) {
        super(m, p, db, u, c);
    }

    public int getNumTraspaso() {
        int num = 1;
        try {
            ResultSet res = this.consulta("SELECT max(num_traspaso) FROM tbl_traspaso;");
            if (res.next()) {
                num = (res.getString(1) != null) ? res.getInt(1) : 0;
                num++;
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    public ResultSet getTraspaso(String id) {
        return this.consulta("SELECT * FROM vta_traspaso where id_traspaso=" + id + ";");
    }

    /*public ResultSet getTraspasoDetalle(String id) {
        return this.consulta("select T.*, TD.id_traspaso_detalle, TD.id_producto, TD.cant_enviada, TD.cant_recibida,TD.tipo_producto, P.codigo, P.descripcion "
                + "from (tbl_traspaso as T inner join tbl_traspaso_detalle as TD on T.id_traspaso=TD.id_traspaso) "
                + "inner join tbl_producto as P on P.id_producto=TD.id_producto "
                + "where T.id_traspaso=" + id + ";");
    }*/
 /* public ResultSet getTraspasoDetalle(String id) {
        return this.consulta("select T.*, TD.id_traspaso_detalle, TD.id_producto, TD.cant_enviada, TD.cant_recibida,TD.tipo_producto, P.codigo, P.descripcion,P.precio_costo,IV.porcentaje "
                + "from (tbl_traspaso as T inner join tbl_traspaso_detalle as TD on T.id_traspaso=TD.id_traspaso) "
                + "inner join tbl_producto as P on P.id_producto=TD.id_producto "
                + "inner join tbl_iva as IV on IV.id_iva=P.id_iva "
                + "where T.id_traspaso=" + id + ";");
    }*/
    public ResultSet getTraspasoDetalle(String id) {
        return this.consulta("select T.*, TD.id_traspaso_detalle, TD.id_producto, TD.cant_enviada, TD.cant_recibida,TD.tipo_producto, P.codigo, P.descripcion,P.precio_costo,IV.porcentaje,FC.id_factura_compra,FC.numero_factura,FC.de_activo"
                + " from (tbl_traspaso as T inner join tbl_traspaso_detalle as TD on T.id_traspaso=TD.id_traspaso)"
                + " inner join tbl_producto as P on P.id_producto=TD.id_producto"
                + " inner join tbl_iva as IV on IV.id_iva=P.id_iva"
                + " left join vta_factura_compra as FC on FC.id_factura_compra=TD.id_factura_compra"
                + " where T.id_traspaso=" + id + ";");
    }

    public String concatenarValores(String productos, String cantidades) {
        String param = "";
        String vecProductos[] = productos.split(",");
        String vecCantidades[] = cantidades.split(",");
        for (int i = 0; i < vecProductos.length; i++) {
            param += "['" + vecProductos[i] + "','" + vecCantidades[i] + "'],";
        }
        param = param.substring(0, param.length() - 1);
        return "array[" + param + "]";
    }

    public String concatenarValores(String productos, String cantidades, String tipos) {
        String param = "";
        String vecProductos[] = productos.split(",");
        String vecCantidades[] = cantidades.split(",");
        String tiposve[] = tipos.split(",");
        for (int i = 0; i < vecProductos.length; i++) {
            param += "['" + vecProductos[i] + "','" + vecCantidades[i] + "','" + tiposve[i] + "'],";
        }
        param = param.substring(0, param.length() - 1);
        return "array[" + param + "]";
    }

    public String concatenarValores(String productos, String cantidades, String tipos, String facturas) {
        String param = "";
        String vecProductos[] = productos.split(",");
        String vecCantidades[] = cantidades.split(",");
        String tiposve[] = tipos.split(",");
        String facturasve[] = facturas.split(",");
        for (int i = 0; i < vecProductos.length; i++) {
            param += "['" + vecProductos[i] + "','" + vecCantidades[i] + "','" + tiposve[i] + "','" + facturasve[i] + "'],";
        }
        param = param.substring(0, param.length() - 1);
        return "array[" + param + "]";
    }

    public int insertar(String num_traspaso, String origen, String usuario_origen, String fecha_envio, String recepcion, String productos, String cantidades) {
        int num = -1;
        try {
            String param = this.concatenarValores(productos, cantidades);
            ResultSet res = this.consulta("select proc_traspaso(" + num_traspaso + ", " + origen + ", '" + usuario_origen + "', '" + fecha_envio + "', " + recepcion + ", " + param + ");");
            if (res.next()) {
                num = (res.getString(1) != null) ? res.getInt(1) : -1;
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    public int insertar(String num_traspaso, String origen, String usuario_origen, String fecha_envio, String recepcion, String productos, String cantidades, String tipos) {
        int num = -1;
        try {
            String param = this.concatenarValores(productos, cantidades, tipos);
            ResultSet res = this.consulta("select proc_traspaso(" + num_traspaso + ", " + origen + ", '" + usuario_origen + "', '" + fecha_envio + "', " + recepcion + ", " + param + ");");
            if (res.next()) {
                num = (res.getString(1) != null) ? res.getInt(1) : -1;
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    public int insertar(String num_traspaso, String origen, String usuario_origen, String fecha_envio, String recepcion, String productos, String cantidades, String tipos, String facturas) {
        int num = -1;
        try {
            String param = this.concatenarValores(productos, cantidades, tipos, facturas);
            ResultSet res = this.consulta("select proc_traspaso(" + num_traspaso + ", " + origen + ", '" + usuario_origen + "', '" + fecha_envio + "', " + recepcion + ", " + param + ");");
            if (res.next()) {
                num = (res.getString(1) != null) ? res.getInt(1) : -1;
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    public boolean actualizar(String id, String num_traspaso, String origen, String usuario_origen, String fecha_envio, String recepcion, String productos, String cantidades) {
        boolean ok = false;
        try {
            String param = this.concatenarValores(productos, cantidades);
            ResultSet res = this.consulta("select proc_editarTraspaso(" + id + ", " + num_traspaso + ", " + origen + ", '" + usuario_origen + "', '" + fecha_envio + "', " + recepcion + ", " + param + ");");
            if (res.next()) {
                ok = (res.getString(1) != null) ? res.getBoolean(1) : false;
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok;
    }

    public boolean actualizar(String id, String productos, String cantidades, String tipos, String facturas) {
        String[] productosve = productos.split(",");
        String[] cantidadesve = cantidades.split(",");
        String[] tiposve = tipos.split(",");
        String[] facturasve = facturas.split(",");
        List sql = new ArrayList();
        sql.add("delete from tbl_traspaso_detalle where id_traspaso='" + id + "';");
        for (int i = 0; i < productosve.length; i++) {
            sql.add("insert into tbl_traspaso_detalle(id_traspaso, id_producto, cant_enviada,tipo_producto,id_factura_compra) values('" + id + "','" + productosve[i] + "', '" + cantidadesve[i] + "','" + tiposve[i] + "','" + facturasve[i] + "');");
        }
        return this.transacciones(sql);
    }

    public boolean recibir(String id, String usuario_recepcion, String fecha_recepcion, String productos, String cantidades) {
        boolean ok = false;
        try {
            String param = this.concatenarValores(productos, cantidades);
            ResultSet res = this.consulta("select proc_recibirTraspaso(" + id + ", '" + usuario_recepcion + "', '" + fecha_recepcion + "', " + param + ");");
            if (res.next()) {
                ok = (res.getString(1) != null) ? res.getBoolean(1) : false;
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok;
    }

    public String[] ObtenerPrecios(String id_factura, String id_producto, boolean tipo) {
        String[] precios = new String[6];
        try {
            String consulta = "";
            String datos = "";
            if (tipo) {
                consulta = "select (FCD.cantidad ||';'|| FCD.p_u ||';'||FCD.p_st||';'||FCD.iva||';'||FCD.total||';'||FCD.descuento)as detalle from tbl_factura_compra_activo_detalle FCD"
                        + " where FCD.id_factura_compra='" + id_factura + "' and FCD.codigos_series='" + id_producto + "';";
            } else {
                consulta = "select (FCD.cantidad ||';'|| FCD.p_u ||';'||FCD.p_st||';'||FCD.iva||';'||FCD.total||';'||FCD.descuento)as detalle from tbl_factura_compra_detalle FCD"
                        + " where FCD.id_factura_compra='" + id_factura + "' and FCD.id_producto='" + id_producto + "';";
            }
            ResultSet res = this.consulta(consulta);
            if (res.next()) {
                datos = (res.getString(1) != null) ? res.getString(1) : "";
                res.close();
            }
            if (datos.trim().compareTo("") != 0) {
                precios = datos.split(";");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return precios;
    }

    public ResultSet getUsuariosTraspaso() {
        return this.consulta("SELECT DISTINCT unnest(array[usuario_origen,usuario_recepcion]) AS usuario,unnest(array[usuario_origen,usuario_recepcion]) AS detalle FROM vta_traspaso_caja "
                + " where (usuario_origen is not null and usuario_recepcion is not null) and (trim(usuario_origen) <>'' and trim(usuario_recepcion) <>'') "
                + " order by  unnest(array[usuario_origen,usuario_recepcion]) asc;");
    }

    public ResultSet getUsuariosCaja(String id_sucursal) {
        return this.consulta("select usuario_caja as uno,usuario_caja as dos  "
                + " from tbl_punto_emision as p "
                + " inner join tbl_empleado as e on e.alias=p.usuario_caja where (trim(usuario_caja)<>'' and trim(usuario_caja)<>'-0') "
                + " and p.id_sucursal='" + id_sucursal + "';");
    }

    public String insertarcaja(String num_traspaso, String id_suc_origen, String usuario_origen, String id_suc_recepcion, String usuario_recepcion, String alias, String productos, String cantidades, String tipoproducto, String idfacturas, String bodega_principal) {
        List sql = new ArrayList();
        try {
            String pk = this.insert("INSERT INTO tbl_traspaso(num_traspaso, fecha_documento, id_suc_origen, usuario_origen,id_suc_recepcion,usuario_recepcion, fecha_envio,tipo_traspaso, alias,bodega_principal)"
                    + " VALUES ('" + num_traspaso + "', now()::date, '" + id_suc_origen + "', '" + usuario_origen + "', '" + id_suc_recepcion + "', '" + usuario_recepcion + "', now()::date, '1', '" + alias + "','" + bodega_principal + "');");
            if (pk.trim().compareTo("-1") != 0) {
                String vproductos[] = productos.split(",");
                String vcantidades[] = cantidades.split(",");
                String vtipoproducto[] = tipoproducto.split(",");
                String vidfacturas[] = idfacturas.split(",");
                for (int i = 0; i < vproductos.length; i++) {
                    sql.add("INSERT INTO tbl_traspaso_detalle(id_traspaso, id_producto, cant_enviada, tipo_producto, id_factura_compra)"
                            + " VALUES ('" + pk + "', '" + vproductos[i] + "', '" + vcantidades[i] + "','" + vtipoproducto[i] + "', " + vidfacturas[i] + ");");
                    sql.add("INSERT INTO tbl_bodega_caja_tmp(id_producto, id_sucursal, id_traspaso, cantidad,bodega_principal)"
                            + " VALUES ('" + vproductos[i] + "', '" + id_suc_origen + "', '" + pk + "', '" + vcantidades[i] + "', '" + bodega_principal + "');");
                }
                if (this.transacciones(sql)) {
                    return pk;
                } else {
                    this.ejecutar("delete from tbl_traspaso where id_traspaso='" + pk + "'");
                    return "-1";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "-1";
        }
        return "-1";
    }

    public boolean Recibecaja(String id_traspaso, String id_suc_origen, String usuario_origen, String id_suc_recepcion, String usuario_recepcion, String id_empleado, String productos, String cantidades) {
//        List sql = new ArrayList();
//        try {
//            String pk = "-1";
//            String vproductos[] = productos.split(",");
//            String vcantidades[] = cantidades.split(",");
//            sql.add("update tbl_traspaso set id_suc_recepcion='" + id_sucursal + "',usuario_recepcion='" + alias + "',fecha_recepcion=now()::date,estado='r' where id_traspaso='" + id + "';");
//            for (int i = 0; i < vproductos.length; i++) {
//                pk = existe_bodega_caja(vproductos[i], id_sucursal, alias, id_empleado);
//                if (pk.trim().compareTo("-1") == 0) {
//                    sql.add("INSERT INTO tbl_bodega_caja(id_empleado, alias, id_producto, stock_anterior, stock_actual, id_sucursal) "
//                            + " VALUES ('" + id_empleado + "', '" + alias + "', '" + vproductos[i] + "', '" + vcantidades[i] + "', '" + vcantidades[i] + "', '" + id_sucursal + "');");
//                    sql.add("INSERT INTO tbl_kardex_sucursal(id_sucursal, id_producto, id_factura, fecha, detalle, cantidad, costo_unitario, costo_total, es_entrada, saldo_cantidad, saldo_costo_unitario, saldo_costo_total, precio_unitario_ajuste, ultima_edicion_entrada, ultima_edicion_salida, documento) "
//                            + " VALUES ('" + id_sucursal + "', '" + vproductos[i] + "','" + id + "' , now()::date, 'TRASPASO DE MERCADERIA A CAJA " + alias + "', '" + vcantidades[i] + "', "
//                            + " (select (case when (tmp2.costo_unitario)>0 then tmp2.costo_unitario else 0 end) from tbl_kardex_sucursal as tmp2 where tmp2.id_kardex_sucursal=(SELECT MAX(tmp.id_kardex_sucursal) FROM tbl_kardex_sucursal as tmp where tmp.id_producto='" + vproductos[i] + "' and tmp.id_sucursal='" + id_sucursal + "')), "
//                            + " (select (case when (tmp2.costo_unitario)>0 then tmp2.costo_unitario*" + vcantidades[i] + " else 0 end) from tbl_kardex_sucursal as tmp2 where tmp2.id_kardex_sucursal=(SELECT MAX(tmp.id_kardex_sucursal) FROM tbl_kardex_sucursal as tmp where tmp.id_producto='" + vproductos[i] + "' and tmp.id_sucursal='" + id_sucursal + "')), "
//                            + " FALSE, "
//                            + " (select (case when (tmp2.saldo_cantidad)>0 then tmp2.saldo_cantidad else 0 end) from tbl_kardex_sucursal as tmp2 where tmp2.id_kardex_sucursal=(SELECT MAX(tmp.id_kardex_sucursal) FROM tbl_kardex_sucursal as tmp where tmp.id_producto='" + vproductos[i] + "' and tmp.id_sucursal='" + id_sucursal + "')), "
//                            + " 0, 0, 0, TRUE, TRUE, 't');");
//                } else {
//                    sql.add("update tbl_bodega_caja set stock_anterior=stock_actual,stock_actual=stock_actual+" + vcantidades[i] + " where id_bodega_caja='" + pk + "'");
//                    sql.add("INSERT INTO tbl_kardex_sucursal(id_sucursal, id_producto, id_factura, fecha, detalle, cantidad, costo_unitario, costo_total, es_entrada, saldo_cantidad, saldo_costo_unitario, saldo_costo_total, precio_unitario_ajuste, ultima_edicion_entrada, ultima_edicion_salida, documento, transpaso_caja, id_bodega_caja) "
//                            + " VALUES ('" + id_sucursal + "', '" + vproductos[i] + "','" + id + "' , now()::date, 'TRASPASO DE MERCADERIA A CAJA " + alias + "', '" + vcantidades[i] + "', "
//                            + " (select (case when (tmp2.costo_unitario)>0 then tmp2.costo_unitario else 0 end) from tbl_kardex_sucursal as tmp2 where tmp2.id_kardex_sucursal=(SELECT MAX(tmp.id_kardex_sucursal) FROM tbl_kardex_sucursal as tmp where tmp.id_producto='" + vproductos[i] + "' and tmp.id_sucursal='" + id_sucursal + "')), "
//                            + " (select (case when (tmp2.costo_unitario)>0 then tmp2.costo_unitario*" + vcantidades[i] + " else 0 end) from tbl_kardex_sucursal as tmp2 where tmp2.id_kardex_sucursal=(SELECT MAX(tmp.id_kardex_sucursal) FROM tbl_kardex_sucursal as tmp where tmp.id_producto='" + vproductos[i] + "' and tmp.id_sucursal='" + id_sucursal + "')), "
//                            + " FALSE, "
//                            + " (select (case when (tmp2.saldo_cantidad)>0 then tmp2.saldo_cantidad else 0 end) from tbl_kardex_sucursal as tmp2 where tmp2.id_kardex_sucursal=(SELECT MAX(tmp.id_kardex_sucursal) FROM tbl_kardex_sucursal as tmp where tmp.id_producto='" + vproductos[i] + "' and tmp.id_sucursal='" + id_sucursal + "')), "
//                            + " 0, 0, 0, TRUE, TRUE, 't', true, '" + pk + "' );");
//                }
//                sql.add("UPDATE tbl_bodega_caja_tmp SET aceptada=true "
//                        + " WHERE id_producto='" + vproductos[i] + "' and id_sucursal='" + id_sucursal + "' and id_traspaso='" + id + "';");
//                sql.add("update tbl_traspaso_detalle set cant_recibida='" + vcantidades[i] + "' where id_producto='" + vproductos[i] + "' and id_traspaso='" + id + "';");
//            }
//            return this.transacciones(sql);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
        boolean ok = false;
        try {
            String param = this.concatenarValores(productos, cantidades);
            ResultSet res = this.consulta("select proc_recibirtraspasocaja('" + id_traspaso + "','" + id_suc_origen + "','" + usuario_origen + "','" + id_suc_recepcion + "','" + usuario_recepcion + "','" + id_empleado + "', " + param + ");");
            if (res.next()) {
                ok = (res.getString(1) != null) ? res.getBoolean(1) : false;
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok;
    }

    public ResultSet getTraspasoDetalleCaja(String id) {
//        return this.consulta("select T.*, TD.id_traspaso_detalle, TD.id_producto, TD.cant_enviada, TD.cant_recibida,TD.tipo_producto, P.codigo, P.descripcion,P.precio_costo,IV.porcentaje,FC.id_factura_compra,FC.numero_factura,FC.de_activo "
//                + ",((select tmp2.stock_sucursal from tbl_sucursal_producto as tmp2 where tmp2.id_producto=P.id_producto and tmp2.id_sucursal=T.id_suc_origen)-(select (case when sum(tmp1.cantidad)>0 then sum(tmp1.cantidad) else 0 end) from tbl_bodega_caja_tmp as tmp1 where tmp1.id_producto=P.id_producto and tmp1.id_sucursal=T.id_suc_origen and aceptada=false and tmp1.id_traspaso<>T.id_traspaso and tmp1.bodega_principal='s'))as total"
//                + " from (vta_traspaso_caja as T inner join tbl_traspaso_detalle as TD on T.id_traspaso=TD.id_traspaso)"
//                + " inner join tbl_producto as P on P.id_producto=TD.id_producto"
//                + " inner join tbl_iva as IV on IV.id_iva=P.id_iva"
//                + " left join vta_factura_compra as FC on FC.id_factura_compra=TD.id_factura_compra"
//                + " where T.id_traspaso=" + id + ";");
        return this.consulta("select T.*, TD.id_traspaso_detalle, TD.id_producto, TD.cant_enviada, TD.cant_recibida,TD.tipo_producto, P.codigo, P.descripcion,P.precio_costo,IV.porcentaje,FC.id_factura_compra,FC.numero_factura,FC.de_activo "
                + ",((select tmp2.stock_sucursal from tbl_sucursal_producto as tmp2 where tmp2.id_producto=P.id_producto and tmp2.id_sucursal=T.id_suc_origen)-stock_usado(P.id_producto,T.id_suc_origen,null,T.id_traspaso::text,null,null,null))as total"
                + " from (vta_traspaso_caja as T inner join tbl_traspaso_detalle as TD on T.id_traspaso=TD.id_traspaso)"
                + " inner join tbl_producto as P on P.id_producto=TD.id_producto"
                + " inner join tbl_iva as IV on IV.id_iva=P.id_iva"
                + " left join vta_factura_compra as FC on FC.id_factura_compra=TD.id_factura_compra"
                + " where T.id_traspaso=" + id + ";");
    }

    public boolean actualizarCaja(String id, String productos, String cantidades, String tipos, String facturas, String id_suc_origen, String bodega_principal) {
        String[] productosve = productos.split(",");
        String[] cantidadesve = cantidades.split(",");
        String[] tiposve = tipos.split(",");
        String[] facturasve = facturas.split(",");
        List sql = new ArrayList();
        sql.add("delete from tbl_traspaso_detalle where id_traspaso='" + id + "';");
        sql.add("delete from tbl_bodega_caja_tmp where id_traspaso='" + id + "';");
        for (int i = 0; i < productosve.length; i++) {
            sql.add("insert into tbl_traspaso_detalle(id_traspaso, id_producto, cant_enviada,tipo_producto,id_factura_compra) values('" + id + "','" + productosve[i] + "', '" + cantidadesve[i] + "','" + tiposve[i] + "'," + facturasve[i] + ");");
            sql.add("INSERT INTO tbl_bodega_caja_tmp(id_producto, id_sucursal, id_traspaso, cantidad,bodega_principal)"
                    + " VALUES ('" + productosve[i] + "', '" + id_suc_origen + "', '" + id + "', '" + cantidadesve[i] + "', '" + bodega_principal + "');");
        }
        return this.transacciones(sql);
    }

    public String existe_bodega_caja(String id_producto, String id_sucursal, String alias, String id_empleado) {
        String pk = "-1";
        ResultSet rs = this.consulta("select id_bodega_caja from tbl_bodega_caja where id_empleado='" + id_empleado + "' and alias='" + alias + "' and id_producto='" + id_producto + "' and id_sucursal='" + id_sucursal + "';");
        try {
            if (rs.next()) {
                pk = (rs.getString(1) != null ? rs.getString(1) : "-1");
            }
        } catch (Exception e) {
            System.out.println("" + e);
        } finally {
            return pk;
        }
    }

    public boolean existe_stock(String id_producto, String id_sucursal, String alias, String id_empleado, int cantidad) {
        int stock = 0;
        boolean ok = false;
        ResultSet rs = this.consulta("select (case when (stock_actual)>0 then stock_actual else 0 end)stock_actual from tbl_bodega_caja where id_empleado='" + id_empleado + "' and alias='" + alias + "' and id_producto='" + id_producto + "' and id_sucursal='" + id_sucursal + "';");
        try {
            if (rs.next()) {
                stock = (rs.getString(1) != null ? rs.getInt(1) : 0);
            }
            if (stock >= cantidad) {
                ok = true;
            }
        } catch (Exception e) {
            System.out.println("" + e);
            return false;
        } finally {
            return ok;
        }
    }

    public ResultSet getTraspasoCaja(String id) {
        return this.consulta("SELECT * FROM vta_traspaso_caja where id_traspaso=" + id + ";");
    }

    public boolean rechazaCaja(String id, String id_sucursal, String alias) {
        List sql = new ArrayList();
        sql.add("update tbl_traspaso set estado='n',id_suc_recepcion='" + id_sucursal + "',usuario_recepcion='" + alias + "',fecha_recepcion=now()::date where id_traspaso='" + id + "';");
        sql.add("UPDATE tbl_bodega_caja_tmp SET aceptada=true WHERE  id_traspaso='" + id + "';");
        return this.transacciones(sql);
    }

    public boolean anuladoCaja(String id, String productos, String cantidades, String id_sucursal, String alias, String id_empleado) {
        boolean ok = false;
        try {

            List sql = new ArrayList();
            String[] productosve = productos.split(",");
            String[] cantidadesve = cantidades.split(",");
            sql.add("update tbl_traspaso set estado='a' where id_traspaso='" + id + "';");
            sql.add("UPDATE tbl_bodega_caja_tmp SET aceptada=true WHERE  id_traspaso='" + id + "';");
            String pk = "-1";
            for (int i = 0; i < productosve.length; i++) {
                if (!existe_stock(productosve[i], id_sucursal, alias, id_empleado, Integer.parseInt(cantidadesve[i]))) {
                    return false;
                } else {
                    pk = existe_bodega_caja(productosve[i], id_sucursal, alias, id_empleado);
                    sql.add("update tbl_bodega_caja set stock_actual=stock_actual-" + cantidadesve[i] + " where id_bodega_caja='" + pk + "'");
                    sql.add("INSERT INTO tbl_kardex_sucursal(id_sucursal, id_producto, id_factura, fecha, detalle, cantidad, costo_unitario, costo_total, es_entrada, saldo_cantidad, saldo_costo_unitario, saldo_costo_total, precio_unitario_ajuste, ultima_edicion_entrada, ultima_edicion_salida, documento, transpaso_caja, id_bodega_caja) "
                            + " VALUES ('" + id_sucursal + "', '" + productosve[i] + "','" + id + "' , now()::date, 'ANULACION TRASPASO DE MERCADERIA A CAJA " + alias + "', '" + cantidadesve[i] + "', "
                            + " (select (case when (tmp2.costo_unitario)>0 then tmp2.costo_unitario else 0 end) from tbl_kardex_sucursal as tmp2 where tmp2.id_kardex_sucursal=(SELECT MAX(tmp.id_kardex_sucursal) FROM tbl_kardex_sucursal as tmp where tmp.id_producto='" + productosve[i] + "' and tmp.id_sucursal='" + id_sucursal + "')), "
                            + " (select (case when (tmp2.costo_unitario)>0 then tmp2.costo_unitario*" + cantidadesve[i] + " else 0 end) from tbl_kardex_sucursal as tmp2 where tmp2.id_kardex_sucursal=(SELECT MAX(tmp.id_kardex_sucursal) FROM tbl_kardex_sucursal as tmp where tmp.id_producto='" + productosve[i] + "' and tmp.id_sucursal='" + id_sucursal + "')), "
                            + " TRUE, "
                            + " (select (case when (tmp2.saldo_cantidad)>0 then tmp2.saldo_cantidad else 0 end) from tbl_kardex_sucursal as tmp2 where tmp2.id_kardex_sucursal=(SELECT MAX(tmp.id_kardex_sucursal) FROM tbl_kardex_sucursal as tmp where tmp.id_producto='" + productosve[i] + "' and tmp.id_sucursal='" + id_sucursal + "')), "
                            + " 0, 0, 0, TRUE, TRUE, 't', true, '" + pk + "' );");
                }
            }
            if (this.transacciones(sql)) {
                ok = true;
            }

        } catch (Exception e) {
            return false;
        } finally {
            return ok;
        }
    }

    public boolean setEstadoTraspaso(String id, String estado) {
        return this.ejecutar("update tbl_traspaso set aceptado='" + estado + "' where id_traspaso='" + id + "';");
    }

    public int getDocumentosSinFirmaTraspaso(String usuario) {
        int numero = 0;
        try {
            ResultSet res = this.consulta("select count(*)as conteo from tbl_traspaso as t where t.aceptado=false and t.estado ='r' and t.eliminado=false and t.fecha_recepcion is not null and t.usuario_origen ='" + usuario + "'");
            if (res.next()) {
                numero = (res.getString(1) != null ? res.getInt(1) : 0);
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return numero;
    }
}
