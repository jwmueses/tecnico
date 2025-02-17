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

public class Sector extends DataBase {

    public Sector(String m, int p, String db, String u, String c) {
        super(m, p, db, u, c);
    }

    public ResultSet getSector(String id) {
        return this.consulta("SELECT * from tbl_sector WHERE id_sector=" + id + ";");
    }

    public ResultSet getSectorInstalacion(String id) {
        return this.consulta("SELECT S.id_sector,S.sector,S.costo_instalacion,S.id_producto,P.codigo,P.descripcion,P.tiene_iva "
                + "from vta_sector as S inner join tbl_producto as P on S.id_producto=P.id_producto where S.id_sector=" + id + ";");
    }

    public ResultSet getSectores() {
        return this.consulta("SELECT id_sector,sector from vta_sector order by sector;");
    }

    public ResultSet getSectores(int id) {
        return this.consulta("SELECT id_sector,sector from tbl_sector where eliminado = false and id_sucursal=" + id + " order by sector;");
    }

    public ResultSet getSectoresInstalacion() {
//        return this.consulta("SELECT S.id_sector,S.sector, (S.costo_instalacion / (1+(I.porcentaje/100))) as costo_instalacion,S.id_producto,P.codigo,P.descripcion,P.tiene_iva, "
        return this.consulta("SELECT S.id_sector,S.sector, S.costo_instalacion,S.id_producto,P.codigo,P.descripcion,P.tiene_iva, "
                + "S.costo_instalacion_fibra, S.costo_instalacion_antena2, S.costo_instalacion_gepon, SP.id_sucursal, I.porcentaje, I.codigo as codigo_iva "
                + "from ((vta_sector as S inner join tbl_producto as P on S.id_producto=P.id_producto) "
                + "inner join tbl_sucursal_producto as SP on SP.id_producto=P.id_producto) "
                + "inner join tbl_iva as I on I.id_iva=SP.id_iva order by S.sector;");
    }

    public boolean estaDuplicado(String id, String sector) {
        ResultSet res = this.consulta("SELECT * FROM tbl_sector where lower(sector)='" + sector.toLowerCase() + "' and id_sector<>" + id + ";");
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

    public boolean insertar(String id_sucursal, String sector, String costo_instalacion, String costo_instalacion_antena2,
            String costo_instalacion_fibra, String costo_instalacion_gepon, String ids_plan_isp, String id_producto) {
        boolean res = false;
        String pk = this.insert("INSERT INTO tbl_sector(id_sucursal, sector, costo_instalacion, costo_instalacion_antena2, "
                + "costo_instalacion_fibra, costo_instalacion_gepon, id_producto) "
                + "VALUES(" + id_sucursal + ", '" + sector + "', " + costo_instalacion + ", " + costo_instalacion_antena2 + ", " + costo_instalacion_fibra
                + ", " + costo_instalacion_gepon + ", " + id_producto + ");");
        if (pk.compareTo("-1") != 0) {
            this.setPlanes(pk, ids_plan_isp);
            res = true;
        }
        return res;
    }

    public boolean insertar(String id_sucursal, String sector, String costo_instalacion, String costo_instalacion_antena2,
            String costo_instalacion_fibra, String costo_instalacion_gepon, String ids_plan_isp, String id_producto, String costo_cambio_domicilio, String costo_orden_no_procedente,
            String costo_cambio_domicilio_fibra, String costo_migracion_fibra, String costo_migracion_inalambrico) {
        boolean res = false;
        String pk = this.insert("INSERT INTO tbl_sector(id_sucursal, sector, costo_instalacion, costo_instalacion_antena2,"
                + " costo_instalacion_fibra, costo_instalacion_gepon, id_producto,costo_cambio_domicilio,costo_orden_no_procedente,"
                + " costo_cambio_domicilio_fibra,costo_migracion_fibra,costo_migracion_inalambrico)"
                + " VALUES(" + id_sucursal + ", '" + sector + "', " + costo_instalacion + ", " + costo_instalacion_antena2 + ", " + costo_instalacion_fibra
                + " ," + costo_instalacion_gepon + ", " + id_producto + ", " + costo_cambio_domicilio + ", " + costo_orden_no_procedente
                + " ," + costo_cambio_domicilio_fibra + ", " + costo_migracion_fibra + ", " + costo_migracion_inalambrico + ");");
        if (pk.compareTo("-1") != 0) {
            this.setPlanes(pk, ids_plan_isp);
            res = true;
        }
        return res;
    }

    public boolean actualizar(String id, String id_sucursal, String sector, String costo_instalacion, String costo_instalacion_antena2,
            String costo_instalacion_fibra, String costo_instalacion_gepon, String ids_plan_isp, String id_producto) {
        boolean res = this.ejecutar("UPDATE tbl_sector SET id_sucursal=" + id_sucursal + ", sector='" + sector + "', costo_instalacion="
                + costo_instalacion + ", costo_instalacion_antena2=" + costo_instalacion_antena2 + ", costo_instalacion_fibra=" + costo_instalacion_fibra
                + ", costo_instalacion_gepon=" + costo_instalacion_gepon + ", id_producto=" + id_producto + " WHERE id_sector=" + id + ";");
        if (res) {
            this.ejecutar("delete from tbl_sector_plan_servicio where id_sector=" + id);
            this.setPlanes(id, ids_plan_isp);
        }
        return res;
    }

    public boolean actualizar(String id, String id_sucursal, String sector, String costo_instalacion, String costo_instalacion_antena2,
            String costo_instalacion_fibra, String costo_instalacion_gepon, String ids_plan_isp, String id_producto, String costo_cambio_domicilio,
            String costo_orden_no_procedente, String costo_cambio_domicilio_fibra, String costo_migracion_fibra, String costo_migracion_inalambrico) {
        boolean res = this.ejecutar("UPDATE tbl_sector SET id_sucursal=" + id_sucursal + ", sector='" + sector + "', costo_instalacion=" + costo_instalacion + ","
                + " costo_instalacion_antena2=" + costo_instalacion_antena2 + ", costo_instalacion_fibra=" + costo_instalacion_fibra + ","
                + " costo_instalacion_gepon=" + costo_instalacion_gepon + ", id_producto=" + id_producto + ", costo_cambio_domicilio=" + costo_cambio_domicilio + ","
                + " costo_orden_no_procedente=" + costo_orden_no_procedente + ",costo_cambio_domicilio_fibra=" + costo_cambio_domicilio_fibra + ",costo_migracion_fibra=" + costo_migracion_fibra + ","
                + " costo_migracion_inalambrico=" + costo_migracion_inalambrico + " WHERE id_sector=" + id + ";");
        if (res) {
            this.ejecutar("delete from tbl_sector_plan_servicio where id_sector=" + id);
            this.setPlanes(id, ids_plan_isp);
        }
        return res;
    }

    public void setPlanes(String id_sector, String ids_plan_isp) {
        String idsPlanes[] = ids_plan_isp.split(",");
        for (int i = 0; i < idsPlanes.length; i++) {
            this.ejecutar("insert into tbl_sector_plan_servicio(id_sector,id_plan_servicio) select " + id_sector + ", id_plan_servicio from tbl_plan_servicio where id_plan_isp=" + idsPlanes[i] + ";");
        }
    }

    public boolean enSectorPlanes(ResultSet rs, String codigo) {
        boolean res = false;
        try {
            String id_sector = "";
            rs.beforeFirst();
            while (rs.next()) {
                id_sector = rs.getString("id_plan_isp") != null ? rs.getString("id_plan_isp") : "";
                if (id_sector.compareTo(codigo) == 0) {
                    res = true;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            res = false;
        }
        return res;
    }
}
