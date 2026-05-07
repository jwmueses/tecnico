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

public class PlanServicio extends DataBase {

    public PlanServicio(String m, int p, String db, String u, String c) {
        super(m, p, db, u, c);
    }

    public ResultSet getPlan(String id) {
        return this.consulta("SELECT P.*, N.texto from tbl_plan_isp as P inner join tbl_nivel_soporte as N on P.id_nivel_soporte=N.id_nivel_soporte"
                + " WHERE P.id_plan_isp=" + id);
    }
    
    public String getNombrePlan(String id_plan_servicio) {
        String plan = "";
        try {
            ResultSet rs = this.consulta("SELECT plan from vta_plan_servicio where id_plan_servicio=" + id_plan_servicio);
            if (rs.next()) {
                plan = rs.getString(1) != null ? rs.getString(1) : "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return plan;
    }

    public ResultSet getPlanes() {
        return this.consulta("SELECT * from vta_plan_isp order by plan");
    }

    public ResultSet getPlanesTodos() {
        return this.consulta("SELECT distinct id_plan_servicio, plan || ' (' || burst_limit || ')' from vta_sector_plan_servicio order by plan || ' (' || burst_limit || ')'");
    }

    public ResultSet getPlanes(String id_sector) {
        return this.consulta("SELECT id_plan_servicio, plan || ' (' || burst_limit || ')' from vta_sector_plan_servicio where id_sector=" + id_sector + " order by plan || ' (' || burst_limit || ')', burst_limit::int");
    }

    public ResultSet getPlanes(String id_sector, String tipo_cliente_plan) {
        return this.consulta("SELECT id_plan_servicio, plan || ' (' || burst_limit || ')' from vta_sector_plan_servicio where id_sector=" + id_sector + " and tipo_cliente_plan='" + tipo_cliente_plan + "' order by plan || ' (' || burst_limit || ')', burst_limit::int");
    }

    public ResultSet getPlanes(String id_sector, String tipo_cliente_plan, String tipo_instalacion, String idPromocion) {
        return this.consulta("SELECT id_plan_servicio, plan || ' (' || burst_limit || ')' from vta_sector_plan_servicio where id_sector=" + id_sector
                + (idPromocion.compareTo("-1") != 0 && idPromocion.compareTo("-2") != 0 ? " and id_plan_servicio in(select id_plan_servicio from tbl_promocion_plan where id_promocion=" + idPromocion + ") " : "")
                + " and tipo_cliente_plan='" + tipo_cliente_plan 
                + "' order by plan || ' (' || burst_limit || ')', burst_limit::int");
    }

    public ResultSet getPlanesTarifas() {
        return this.consulta("SELECT id_plan_servicio, plan || ' (' || burst_limit || ')' from vta_plan_tarifa order by comparticion, plan || ' (' || burst_limit || ')' desc");
    }

    public ResultSet getPlanesTarifas(String tipo_cliente_plan) {
        return this.consulta("SELECT id_plan_servicio, plan || ' (' || burst_limit || ')' from vta_plan_tarifa where tipo_cliente_plan='" + tipo_cliente_plan + "' order by comparticion, plan || ' (' || burst_limit || ')' desc");
    }

    public ResultSet getPlanDetalle(String id_plan_servicio) {
        return this.consulta("select P.plan, V.burst_limit, T.costo_plan, '1 - ' || P.div_max_limit as comparticion, P.id_nivel_soporte, P.plan || ' ' || V.burst_limit as plan_servicio "
                + "from (tbl_plan_isp as P inner join tbl_plan_servicio as V on P.id_plan_isp=V.id_plan_isp) "
                + "inner join tbl_tarifa as T on T.id_plan_servicio=V.id_plan_servicio "
                + "where P.eliminado=false and V.eliminado=false and V.id_plan_servicio=" + id_plan_servicio);
    }

    public ResultSet getPlanesISP(String id_sector) {
        return this.consulta("SELECT id_plan_servicio, plan || ' (' || burst_limit || ')', id_plan_isp from vta_sector_plan_servicio where id_sector=" + id_sector + " order by plan || ' (' || burst_limit || ')', burst_limit::int");
    }

    public boolean estaDuplicado(String id, String plan) {
        ResultSet res = this.consulta("SELECT * FROM tbl_plan_isp where lower(plan)='" + plan.toLowerCase() + "' and id_plan_isp<>" + id);
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

    public String insertar(String id_producto, String id_producto_anticipo, String plan, String comparticion, String id_nivel_soporte,
            String div_max_limit, String div_burst_threshold, String burst_time, String sim_subida) {
        String pk = this.insert("INSERT INTO tbl_plan_isp(id_producto, id_producto_anticipo, plan, comparticion, id_nivel_soporte, div_max_limit, div_burst_threshold, burst_time, sim_subida) VALUES"
                + "(" + id_producto + ", " + id_producto_anticipo + ", '" + plan + "', " + comparticion + ", " + id_nivel_soporte + ", " + div_max_limit + ", " + div_burst_threshold + ", " + burst_time + ", " + sim_subida + ")");
        return pk;
    }

    public String insertar(String id_producto, String id_producto_anticipo, String plan, String comparticion, String id_nivel_soporte,
            String div_max_limit, String div_burst_threshold, String burst_time, String sim_subida, String tipo_cliente_plan, String ancho_banda) {
        String pk = this.insert("INSERT INTO tbl_plan_isp(id_producto, id_producto_anticipo, plan, comparticion, id_nivel_soporte, div_max_limit, div_burst_threshold, burst_time, sim_subida,tipo_cliente_plan,ancho_banda) VALUES"
                + "(" + id_producto + ", " + id_producto_anticipo + ", '" + plan + "', " + comparticion + ", " + id_nivel_soporte + ", " + div_max_limit + ", " + div_burst_threshold + ", " + burst_time + ", " + sim_subida + ", '" + tipo_cliente_plan + "','" + ancho_banda + "')");
        return pk;
    }

    public boolean actualizar(String id, String id_producto, String id_producto_anticipo, String plan, String comparticion, String id_nivel_soporte,
            String div_max_limit, String div_burst_threshold, String burst_time, String sim_subida) {
        return this.ejecutar("UPDATE tbl_plan_isp SET plan='" + plan + "', comparticion=" + comparticion + ", id_producto=" + id_producto
                + ", id_producto_anticipo=" + id_producto_anticipo + ", div_max_limit=" + div_max_limit
                + ", div_burst_threshold=" + div_burst_threshold + ", burst_time=" + burst_time + ", id_nivel_soporte="
                + id_nivel_soporte + ", sim_subida=" + sim_subida + " WHERE id_plan_isp=" + id);

    }

    public boolean actualizar(String id, String id_producto, String id_producto_anticipo, String plan, String comparticion, String id_nivel_soporte,
            String div_max_limit, String div_burst_threshold, String burst_time, String sim_subida, String tipo_cliente_plan, String ancho_banda) {
        return this.ejecutar("UPDATE tbl_plan_isp SET plan='" + plan + "', comparticion=" + comparticion + ", id_producto=" + id_producto
                + ", id_producto_anticipo=" + id_producto_anticipo + ", div_max_limit=" + div_max_limit
                + ", div_burst_threshold=" + div_burst_threshold + ", burst_time=" + burst_time + ", id_nivel_soporte="
                + id_nivel_soporte + ", sim_subida=" + sim_subida + ",tipo_cliente_plan='" + tipo_cliente_plan + "',ancho_banda='" + ancho_banda + "' WHERE id_plan_isp=" + id);

    }

    public String getSufPlan(String id_plan_servicio) {
        String plan = "suspendidos";
        try {
            ResultSet rs = this.consulta("SELECT substr( plan, 1, 4) || substr( split_part(solo_plan, ' ', 2), 1, 5) || substr( split_part(solo_plan, ' ', 3), 1, 4) || burst_limit::varchar from vta_plan_servicio where id_plan_servicio=" + id_plan_servicio);
            if (rs.next()) {
                plan = rs.getString(1) != null ? rs.getString(1) : "suspendidos";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return plan;
    }

    public String getPool(String id_plan_servicio) {
        String pool = "cort";
        try {
            ResultSet rs = this.consulta("SELECT substr( plan, 1, 4) as pool from vta_plan_servicio where id_plan_servicio=" + id_plan_servicio);
            if (rs.next()) {
                pool = rs.getString("pool") != null ? rs.getString("pool") : "cort";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pool;
    }

    /* VELOCIDES - BURST LIMIT */
    public ResultSet getVelocidad(String id_plan_servicio) {
        return this.consulta("SELECT * from vta_plan_servicio where id_plan_servicio=" + id_plan_servicio);
    }

    public ResultSet getVelocidades() {
        return this.consulta("SELECT * from vta_plan_servicio order by plan, burst_limit::int");
    }

    public ResultSet getVelocidades(String id_plan_isp) {
        return this.consulta("SELECT * from vta_plan_servicio where id_plan_isp=" + id_plan_isp + " order by burst_limit::int");
    }

    public boolean velocidadDuplicada(String id_plan_isp, String id_plan_servicio, String burst_limit) {
        ResultSet res = this.consulta("SELECT * FROM tbl_plan_servicio where burst_limit='" + burst_limit + "' and id_plan_isp=" + id_plan_isp + " and id_plan_servicio<>" + id_plan_servicio + ";");
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

    public String insertarVelocidad(String id_plan_isp, String burst_limit, String login_time) {
        String pk = this.insert("INSERT INTO tbl_plan_servicio(id_plan_isp, burst_limit, login_time) VALUES(" + id_plan_isp + ", '" + burst_limit + "', '" + login_time + "')");
        if (pk.compareTo("-1") != 0) {
            List sql = new ArrayList();
            int anio = Fecha.getAnio();
            int mes = Fecha.getMes();
            sql.add("insert into tbl_tarifa(id_plan_servicio, vigente_desde, vigente_hasta, costo_plan) values(" + pk + ", '" + anio + "-" + mes + "-01', (now() + ('1 year')::interval)::date, 0);");
            try {
                ResultSet rsSecPlanes = this.consulta("SELECT distinct id_sector from vta_sector_plan_servicio where id_plan_isp=" + id_plan_isp);
                while (rsSecPlanes.next()) {
                    sql.add("insert into tbl_sector_plan_servicio(id_sector, id_plan_servicio) values("
                            + (rsSecPlanes.getString("id_sector") != null ? rsSecPlanes.getString("id_sector") : "") + ", " + pk + ");");
                }
                rsSecPlanes.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.transacciones(sql);
        }
        return pk;
    }

    public boolean actualizarVelocidad(String id_plan_servicio, String burst_limit, String login_time) {
        return this.ejecutar("UPDATE tbl_plan_servicio SET burst_limit='" + burst_limit + "', login_time='" + login_time
                + "' WHERE id_plan_servicio=" + id_plan_servicio);
    }

    public String getRadPlanJSON(String id) {
        ResultSet rs = this.consulta("select up_max_limit || 'k / ' || max_limit || 'k' as max_limit, "
                + "up_burst_limit || 'k / ' || burst_limit || 'k' as burst_limit,"
                + "up_burst_threshold || 'k / ' || burst_threshold || 'k' as burst_threshold,"
                + "up_burst_time || ' / ' || burst_time as burst_time,"
                + "up_limit_at || 'k / ' || limit_at || 'k' as limit_at from vta_plan_servicio where id_plan_servicio=" + id);
        return this.getJSON(rs);
    }

    public String getRadPlan(String id) {
        String burst_limit = "0/0";
        String max_limit = "0/0";
        String burst_threshold = "0/0";
        String burst_time = "0/0";
        String priorodad = "8";
        String limit_at = "0/0";
        try {
            ResultSet rs = this.consulta("select up_max_limit || 'k/' || max_limit || 'k' as max_limit, "
                    + "up_burst_limit || 'k/' || burst_limit || 'k' as burst_limit,"
                    + "up_burst_threshold || 'k/' || burst_threshold || 'k' as burst_threshold,"
                    + "up_burst_time || '/' || burst_time as burst_time,"
                    + "comparticion::int,"
                    + "up_limit_at || 'k/' || limit_at || 'k' as limit_at from vta_plan_servicio where id_plan_servicio=" + id);
            if (rs.next()) {
                burst_limit = (rs.getString("burst_limit") != null) ? rs.getString("burst_limit") : "0/0";
                max_limit = (rs.getString("max_limit") != null) ? rs.getString("max_limit") : "0/0";
                burst_threshold = (rs.getString("burst_threshold") != null) ? rs.getString("burst_threshold") : "0/0";
                burst_time = (rs.getString("burst_time") != null) ? rs.getString("burst_time") : "0/0";
                priorodad = (rs.getString("comparticion") != null) ? rs.getString("comparticion") : "8";
                limit_at = (rs.getString("limit_at") != null) ? rs.getString("limit_at") : "0/0";
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (max_limit + " " + burst_limit + " " + burst_threshold + " " + burst_time + " " + priorodad + " " + limit_at);
    }

    /* TARIFAS */
    public ResultSet getTarifaVigente(String id_plan_servicio, String fecha) {
        return this.consulta("SELECT * from tbl_tarifa where id_plan_servicio=" + id_plan_servicio + " and "
                + "'" + fecha + "' between vigente_desde and vigente_hasta");
    }

    public ResultSet getTarifas(String id_plan_servicio) {
        return this.consulta("SELECT *, toDateSQL(vigente_desde) as sql_vigente_desde, toDateSQL(vigente_hasta) as sql_vigente_hasta from tbl_tarifa where id_plan_servicio=" + id_plan_servicio);
    }

    public boolean tarifaDuplicada(String id_plan_servicio, String id_tarifa, String vigente_desde, String vigente_hasta) {
        ResultSet res = this.consulta("SELECT * FROM tbl_tarifa where ('" + vigente_desde + "' between vigente_desde and vigente_hasta or '" + vigente_hasta
                + "' between vigente_desde and vigente_hasta) and id_plan_servicio=" + id_plan_servicio + " and id_tarifa<>" + id_tarifa + ";");
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

    public String insertarTarifa(String id_plan_servicio, String vigente_desde, String vigente_hasta, String costo_plan) {
        return this.insert("insert into tbl_tarifa(id_plan_servicio, vigente_desde, vigente_hasta, costo_plan) values(" + id_plan_servicio
                + ", '" + vigente_desde + "', '" + vigente_hasta + "', " + costo_plan + ");");
    }

    public boolean actualizarTarifa(String id_tarifa, String vigente_desde, String vigente_hasta, String costo_plan) {
        List sql = new ArrayList();
        float costoAnt = 0;
        String id_plan_servicio = "-1";
        try {
            ResultSet rs = this.consulta("select id_plan_servicio, costo_plan from tbl_tarifa where id_tarifa=" + id_tarifa);
            if (rs.next()) {
                costoAnt = rs.getString("costo_plan") != null ? rs.getFloat("costo_plan") : 0;
                id_plan_servicio = rs.getString("id_plan_servicio") != null ? rs.getString("id_plan_servicio") : "-1";
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        sql.add("update tbl_tarifa set vigente_desde='" + vigente_desde + "', vigente_hasta='" + vigente_hasta + "', costo_plan=" + costo_plan + " where id_tarifa=" + id_tarifa);
        if (costoAnt != Float.parseFloat(costo_plan)) {
            sql.add("update tbl_prefactura set recalcular=true where id_instalacion in (select id_instalacion from tbl_instalacion where id_plan_actual=" + id_plan_servicio + ")");
        }
        return this.transacciones(sql);
    }

    public float getCostoPlan(String id_plan_servicio) {
        float costo = 0;
        try {
            ResultSet rs = this.consulta("select costo_plan from tbl_tarifa where id_tarifa=(select id_tarifa from tbl_tarifa where id_plan_servicio=" + id_plan_servicio + ") and id_plan_servicio=" + id_plan_servicio);
            if (rs.next()) {
                costo = rs.getString(1) != null ? rs.getFloat(1) : 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return costo;
    }

    public ResultSet getPrecioPlanSector(String id_plan, String id_sector) {
        return this.consulta("select distinct P.id_producto,t.costo_plan,Z.id_producto as id_producto_sector, "
                + " case when lower(P.plan) like '%alambri%' then Z.costo_instalacion "
                + "	 when lower(P.plan) like '%ibra%' then Z.costo_cambio_domicilio_fibra "
                + "	 when lower(P.plan) like '%pon%' then Z.costo_instalacion_gepon "
                + "	 else Z.costo_instalacion end as valor_instalacion,"
                + " P.plan, "
                + " case when lower(P.plan) like '%alambri%' then 'a' "
                + "	 when lower(P.plan) like '%ibra%' then 'f' "
                + "	 when lower(P.plan) like '%pon%' then 'g' "
                + "	 else 'a' end as tipo_instalacion, "
                + " (select w.costo_router from tbl_sucursal as w where w.id_sucursal=Z.id_sucursal) as costo_router,"
                + " (select w.costo_instalacion from tbl_sucursal as w where w.id_sucursal=Z.id_sucursal) as costo_instalacion,"
                + " (select w.tiempo_permanencia from tbl_sucursal as w where w.id_sucursal=Z.id_sucursal) as tiempo_permanencia"
                + " from tbl_plan_isp as P  "
                + " inner join tbl_plan_servicio as V on P.id_plan_isp=V.id_plan_isp "
                + " inner join tbl_sector_plan_servicio as X on X.id_plan_servicio =V.id_plan_servicio "
                + " inner join tbl_sector as Z on Z.id_sector =X.id_sector  "
                + " inner join tbl_tarifa as T on T.id_plan_servicio=V.id_plan_servicio  "
                + " where P.eliminado=false and V.eliminado=false and (V.tipo_plan is not null and V.tipo_plan<>'')  "
                + " and v.id_plan_servicio ='" + id_plan + "' and x.id_sector ='" + id_sector + "' limit 1;");

    }

    public String getPrecioPlanSectorCampo(String id_plan, String id_sector, String campo) {
        String valor = "";
        try {
            ResultSet rs = this.consulta("select " + campo + " from vta_instalacion_costos where id_plan_servicio ='" + id_plan + "' and id_sector ='" + id_sector + "' limit 1;");
            if (rs.next()) {
                valor = rs.getString(campo) != null ? rs.getString(campo) : "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return valor;
    }
}
