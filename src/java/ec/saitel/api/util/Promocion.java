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
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Jorge
 */
public class Promocion extends DataBase {

    public Promocion(String m, int p, String db, String u, String c) {
        super(m, p, db, u, c);
    }

    public ResultSet getPromocion(String id) {
        return this.consulta("SELECT * FROM tbl_promocion where id_promocion=" + id + ";");
    }

    public ResultSet getPromociones() {
        return this.consulta("SELECT * FROM tbl_promocion order by promocion;");
    }
    
    public ResultSet getPublicidades(int idSucursal) {
        return this.consulta("select replace( replace(array_agg(P.id_promocion)::varchar, '{', ''), '}', '') as ids_promocion \n" +
            "FROM tbl_promocion as P inner join tbl_promocion_sucursal as S on P.id_promocion = S.id_promocion \n" +
            "where id_sucursal=" + idSucursal + " and not cerrada and now()::date between fecha_inicio and fecha_termino;");
    }

    public ResultSet getPromocionesInstalaciones(int idSucursal) {
        return this.consulta("SELECT P.id_promocion, P.promocion, P.fecha_creacion, P.fecha_inicio, P.fecha_termino, P.inst_objetivo_es_porcentaje, P.inst_objetivo_a_cumplir, "
                + "P.inst_objetivo_basado_en, P.inst_costo_es_porcentaje, P.inst_costo_valor, P.inst_prepago, P.inst_postpago, P.men_tiempo_de_permanencia_min "
                + "FROM tbl_promocion as P inner join tbl_promocion_sucursal as PS on P.id_promocion=PS.id_promocion and case when fecha_inicio is not null then fecha_inicio else fecha_creacion end <= now() "
                + "where inst_objetivo_a_cumplir > 0 and inst_costo_valor > 0 and P.cerrada=false and PS.id_sucursal=" + idSucursal + " order by promocion");
    }

    public ResultSet getPromocionesPlanes(String idSucursal, char op) {
        String where = "";
        switch (op) {
            case 'i':   //  en instalaciones
                where = " and inst_objetivo_a_cumplir > 0 ";
                break;
            case 'p':   //  en prefacturas
                where = " and men_descuento > 0 ";
                break;
            case 'v':   //  en ventas de productos
                where = " and prod_descuento > 0 ";
                break;
        }
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT PP.id_promocion,id_plan_servicio ");
        sql.append("FROM (tbl_promocion_plan as PP inner join tbl_promocion as P on PP.id_promocion=P.id_promocion) ");
        sql.append("inner join tbl_promocion_sucursal as S on S.id_promocion=P.id_promocion ");
        sql.append("where P.cerrada=false and S.id_sucursal=");
        sql.append(idSucursal);
        sql.append(where);
        sql.append(" order by PP.id_promocion,id_plan_servicio");
        return this.consulta(sql.toString());
    }

    public ResultSet getPromocionesPreFacturas(String idSucursal) {
        return this.consulta("SELECT P.id_promocion, P.promocion, P.fecha_creacion, P.fecha_inicio, P.fecha_termino, P.inst_prepago, P.inst_postpago, "
                + "P.fp_tarjeta_credito, P.fp_tarjeta_debito, P.fp_cuenta_corriente, P.fp_cuenta_ahorros, P.men_descuento, P.men_es_porcentaje, P.men_num_meses "
                + "FROM tbl_promocion as P inner join tbl_promocion_sucursal as PS on P.id_promocion=PS.id_promocion "
                + "where men_descuento > 0 and P.cerrada=false and PS.id_sucursal=" + idSucursal + " order by promocion");
    }

    public ResultSet getPromocionesProductos() {
        return this.consulta("SELECT * FROM tbl_promocion order by promocion;");
    }

    /*public boolean estaDuplicado(String id, String promocion)
    {
        ResultSet res = this.consulta("SELECT * FROM tbl_promocion where promocion='"+promocion+"' and id_promocion<>"+id+";");
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
    public String insertar(String promocion, String cerrada, String fecha_inicio, String hora_inicio, String fecha_termino, String hora_fin,
            String inst_objetivo_es_porcentaje, String inst_objetivo_a_cumplir, String inst_objetivo_basado_en, float inst_base_referencia_total, String inst_costo_es_porcentaje, String inst_costo_valor,
            String inst_prepago, String inst_postpago, String habilitar_financiar_instalacion, String men_num_meses, String men_es_porcentaje, String men_descuento, String men_tiempo_de_permanencia_min, String fp_tarjeta_credito, String fp_tarjeta_debito,
            String fp_cuenta_corriente, String fp_cuenta_ahorros, List sucursales, List planes, String migracion_fibra, String cambio_domicilio, String aumento_megas, List listaDescuentos) {
        String pk = this.insert("insert into tbl_promocion(promocion, fecha_inicio, hora_inicio, fecha_termino, hora_fin, "
                + "inst_objetivo_es_porcentaje, inst_objetivo_a_cumplir, inst_objetivo_basado_en, inst_base_referencia_total, inst_costo_es_porcentaje, inst_costo_valor, "
                + "inst_prepago, inst_postpago, habilitar_financiar_instalacion, men_num_meses, men_es_porcentaje, men_descuento, men_tiempo_de_permanencia_min, fp_tarjeta_credito, fp_tarjeta_debito, "
                + "fp_cuenta_corriente, fp_cuenta_ahorros,migracion_fibra,cambio_domicilio, aumento_megas) "
                + "values('" + promocion + "', " + fecha_inicio + ", '" + hora_inicio + "', " + fecha_termino + ", '" + hora_fin + "', "
                + inst_objetivo_es_porcentaje + ", " + inst_objetivo_a_cumplir + ", " + inst_objetivo_basado_en + ", " + inst_base_referencia_total + ", " + inst_costo_es_porcentaje + ", "
                + inst_costo_valor + ", " + inst_prepago + ", " + inst_postpago + ", " + habilitar_financiar_instalacion + ", " + men_num_meses + ", " + men_es_porcentaje + ", " + men_descuento + ", " + men_tiempo_de_permanencia_min + ", "
                + fp_tarjeta_credito + ", " + fp_tarjeta_debito + ", " + fp_cuenta_corriente + ", " + fp_cuenta_ahorros + "," + migracion_fibra + "," + cambio_domicilio + ", "+aumento_megas+");");
        if (pk.compareTo("-1") != 0) {
            List sql = new ArrayList();
            if (listaDescuentos.size() > 0) {
                Iterator it = listaDescuentos.iterator();
                while (it.hasNext()) {
                    String idPromDescuento = (String) it.next();
                    sql.add("insert into tbl_promocion_descuento_habilitada(id_promocion, id_promocion_descuento) "
                            + "values(" + pk + ", " + idPromDescuento + ");");
                }
            }
            if (sucursales.size() > 0) {
                Iterator it = sucursales.iterator();
                while (it.hasNext()) {
                    String sucursal[] = (String[]) it.next();
                    sql.add("insert into tbl_promocion_sucursal(id_promocion, id_sucursal, id_canton, id_parroqia) "
                            + "values(" + pk + ", " + sucursal[0] + ", " + sucursal[1] + ", " + sucursal[2] + ");");
                }
            }
            if (planes.size() > 0) {
                Iterator it = planes.iterator();
                while (it.hasNext()) {
                    String plan = (String) it.next();
                    sql.add("insert into tbl_promocion_plan(id_promocion, id_plan_servicio) values(" + pk + ", " + plan + ");");
                }
            }
            if (!this.transacciones(sql)) {
                this.ejecutar("delete from tbl_promocion where id_promocion=" + pk);
                pk = "-1";
            }
        }
        return pk;
    }

    public boolean actualizar(String id_promocion, String promocion, String cerrada, String fecha_inicio, String hora_inicio, String fecha_termino, String hora_fin,
            String inst_objetivo_es_porcentaje, String inst_objetivo_a_cumplir, String inst_objetivo_basado_en, float inst_base_referencia_total, String inst_costo_es_porcentaje, String inst_costo_valor,
            String inst_prepago, String inst_postpago, String habilitar_financiar_instalacion, String men_num_meses, String men_es_porcentaje, String men_descuento, String men_tiempo_de_permanencia_min, String fp_tarjeta_credito, String fp_tarjeta_debito,
            String fp_cuenta_corriente, String fp_cuenta_ahorros, List sucursales, List planes, String migracion_fibra, String cambio_domicilio, String aumento_megas, List listaDescuentos) {
        boolean ok = this.ejecutar("update tbl_promocion set promocion='" + promocion + "', fecha_inicio=" + fecha_inicio
                + ", hora_inicio='" + hora_inicio + "', fecha_termino=" + fecha_termino + ", hora_fin='" + hora_fin + "', inst_objetivo_es_porcentaje="
                + inst_objetivo_es_porcentaje + ", inst_objetivo_a_cumplir=" + inst_objetivo_a_cumplir + ", inst_objetivo_basado_en=" + inst_objetivo_basado_en
                + ", inst_base_referencia_total=" + inst_base_referencia_total + ", inst_costo_es_porcentaje=" + inst_costo_es_porcentaje + ", inst_costo_valor=" + inst_costo_valor + ", inst_prepago=" + inst_prepago
                + ", inst_postpago=" + inst_postpago + ", habilitar_financiar_instalacion=" + habilitar_financiar_instalacion + ", men_num_meses=" + men_num_meses
                + ", men_es_porcentaje=" + men_es_porcentaje + ", men_descuento=" + men_descuento + ", men_tiempo_de_permanencia_min=" + men_tiempo_de_permanencia_min
                + ", fp_tarjeta_credito=" + fp_tarjeta_credito + ", fp_tarjeta_debito=" + fp_tarjeta_debito + ", fp_cuenta_corriente=" + fp_cuenta_corriente
                + ", fp_cuenta_ahorros=" + fp_cuenta_ahorros + ", migracion_fibra=" + migracion_fibra + ", cambio_domicilio=" + cambio_domicilio + ", aumento_megas="+aumento_megas+" where id_promocion=" + id_promocion);
        if (ok) {
            List sql = new ArrayList();
            if (listaDescuentos.size() > 0) {
                sql.add("delete from tbl_promocion_descuento_habilitada where id_promocion=" + id_promocion);
                Iterator it = listaDescuentos.iterator();
                while (it.hasNext()) {
                    String idPromDescuento = (String) it.next();
                    sql.add("insert into tbl_promocion_descuento_habilitada(id_promocion, id_promocion_descuento) "
                            + "values(" + id_promocion + ", " + idPromDescuento + ");");
                }
            }
            if (sucursales.size() > 0) {
                sql.add("delete from tbl_promocion_sucursal where id_promocion=" + id_promocion);
                Iterator it = sucursales.iterator();
                while (it.hasNext()) {
                    String sucursal[] = (String[]) it.next();
                    sql.add("insert into tbl_promocion_sucursal(id_promocion, id_sucursal, id_canton, id_parroqia) "
                            + "values(" + id_promocion + ", " + sucursal[0] + ", " + sucursal[1] + ", " + sucursal[2] + ");");
                }
            }
            if (planes.size() > 0) {
                sql.add("delete from tbl_promocion_plan where id_promocion=" + id_promocion);
                Iterator it = planes.iterator();
                while (it.hasNext()) {
                    String plan = (String) it.next();
                    sql.add("insert into tbl_promocion_plan(id_promocion, id_plan_servicio) values(" + id_promocion + ", " + plan + ");");
                }
            }
            this.transacciones(sql);
        }

        return ok;
    }

    public float[] getBaseReferencialTotal() {
        float total[] = {0, 0};
        try {
            ResultSet rs = this.consulta("select count(id_instalacion), sum(costo_instalacion) from tbl_instalacion where estado_servicio in ('a','c','s')");
            if (rs.next()) {
                total[0] = rs.getString(1) != null ? rs.getFloat(1) : 0;
                total[1] = rs.getString(2) != null ? rs.getFloat(2) : 0;
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }

    public boolean setPromocionInstalacion(String id_instalacion, String id_promocion) {
        return this.ejecutar("insert into tbl_instalacion_promocion(id_instalacion, id_promocion) values(" + id_instalacion + ", " + id_promocion + ")");
    }

    public ResultSet getPromocionesInstalacion(String id_instalacion) {
        return this.consulta("select * from vta_instalacion_promocion_contrato where id_instalacion='" + id_instalacion + "';");
    }

    public ResultSet getPromocionPlan(String id_sucursal, String id_sector, String id_plan_servicio, String id_promocion) {
        return this.consulta("select distinct P.promocion, P.inst_costo_es_porcentaje, P.inst_costo_valor, P.inst_prepago, P.inst_postpago, P.men_tiempo_de_permanencia_min from tbl_promocion as p "
                + " inner join tbl_promocion_plan as pp on pp.id_promocion =p.id_promocion  "
                + " inner join tbl_promocion_sucursal as ps on ps.id_promocion =p.id_promocion  "
                + " inner join tbl_sector as s on s.id_sucursal = ps.id_sucursal  "
                + " where ps.id_sucursal ='" + id_sucursal + "' and s.id_sector ='" + id_sector + "' and pp.id_plan_servicio ='" + id_plan_servicio + "' and p.id_promocion ='" + id_promocion + "';");
    }
}
