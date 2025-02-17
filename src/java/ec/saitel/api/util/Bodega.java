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

public class Bodega extends DataBase {

    public Bodega(String m, int p, String db, String u, String c) {
        super(m, p, db, u, c);
    }

    public ResultSet getBodega(String id) {
        return this.consulta("SELECT * from tbl_bodega WHERE id_bodega=" + id);
    }

    public ResultSet getBodegaEmpleado(String id_empleado) {
        return this.consulta("SELECT * FROM tbl_bodega where es_responsable_cliente=false and id_responsable=" + id_empleado);
    }

    public ResultSet getBodegaCliente(String id) {
        return this.consulta("SELECT * \n"
                + "from tbl_bodega_activo as BA inner join tbl_activo as A on BA.id_activo=A.id_activo \n"
                + "where BA.id_bodega in (SELECT id_bodega from vta_bodega WHERE id_responsable=" + id + ") \n"
                + "and A.eliminado=false and A.id_activo not in (select id_activo from tbl_activo_perdida) order by A.codigo_activo");
    }

    public ResultSet getBodegaInstalacion(String id) {
        return this.consulta("SELECT * from tbl_bodega WHERE id_instalacion=" + id);
    }
    
    public ResultSet getBodegas() {
        return this.consulta("SELECT id_bodega, bodega from vta_bodega where es_responsable_cliente=false");
    }

    public ResultSet getBodegasPersonalizacion(String txt) {
        return this.consulta("SELECT id_bodega, bodega, E.dni, E.empleado, es_responsable_cliente from vta_bodega as B inner join vta_empleado as E on B.id_responsable=E.id_empleado "
                + "where lower(bodega) like '%" + txt + "%' or lower(E.dni) like '" + txt + "%' or lower(E.empleado) like '%" + txt + "%' and es_responsable_cliente=false "
                + "union "
                + "select id_bodega, bodega, ruc as dni, razon_social as empleado, es_responsable_cliente from vta_bodega as B inner join tbl_cliente as C on C.id_cliente=B.id_responsable "
                + "where lower(bodega) like '%" + txt + "%' or lower(ruc) like '" + txt + "%' or lower(razon_social) like '%" + txt + "%' and es_responsable_cliente=true "
                + "order by bodega limit 10 offset 0");
    }

    public ResultSet getBodegasPersonalizacion(String txt, boolean obtener_equipo_empleado, String tipo_envio) {
        String consulta = "SELECT id_bodega, bodega, E.dni, E.empleado, es_responsable_cliente from vta_bodega as B inner join vta_empleado as E on B.id_responsable=E.id_empleado "
                + "where lower(bodega) like '%" + txt + "%' or lower(E.dni) like '" + txt + "%' or lower(E.empleado) like '%" + txt + "%' and es_responsable_cliente=false "
                + "union "
                + "select id_bodega, bodega, ruc as dni, razon_social as empleado, es_responsable_cliente from vta_bodega as B inner join tbl_cliente as C on C.id_cliente=B.id_responsable "
                + "where lower(bodega) like '%" + txt + "%' or lower(ruc) like '" + txt + "%' or lower(razon_social) like '%" + txt + "%' and es_responsable_cliente=true "
                + "order by bodega limit 10 offset 0";
        if (!obtener_equipo_empleado && tipo_envio.trim().compareTo("2") == 0) {
            consulta = "select id_bodega, bodega, ruc as dni, razon_social as empleado, es_responsable_cliente from vta_bodega as B inner join tbl_cliente as C on C.id_cliente=B.id_responsable "
                    + "where lower(bodega) like '%" + txt + "%' or lower(ruc) like '" + txt + "%' or lower(razon_social) like '%" + txt + "%' and es_responsable_cliente=true "
                    + "order by bodega limit 10 offset 0";
        }

        return this.consulta(consulta);
    }

    public ResultSet getBodegasPersonalizacion(String txt, boolean obtener_equipo_empleado, String tipo_envio, boolean buscartodaslasbodegas) {
        String bodega_habil = "";
        if (!buscartodaslasbodegas) {
            bodega_habil = " and B.bodega_personalizacion=true ";
        }
        String consulta = "SELECT id_bodega, bodega, E.dni, E.empleado, es_responsable_cliente from vta_bodega as B inner join vta_empleado as E on B.id_responsable=E.id_empleado "
                + "where (lower(bodega) like '%" + txt + "%' or lower(E.dni) like '" + txt + "%' or lower(E.empleado) like '%" + txt + "%' and es_responsable_cliente=false) " + bodega_habil + " "
                + "union "
                + "select id_bodega, bodega, ruc as dni, razon_social as empleado, es_responsable_cliente from vta_bodega as B inner join tbl_cliente as C on C.id_cliente=B.id_responsable "
                + "where (lower(bodega) like '%" + txt + "%' or lower(ruc) like '" + txt + "%' or lower(razon_social) like '%" + txt + "%' and es_responsable_cliente=true) " + bodega_habil 
                + " order by bodega limit 10 offset 0";
        if (!obtener_equipo_empleado && tipo_envio.trim().compareTo("2") == 0) {
            consulta = "select id_bodega, bodega, ruc as dni, razon_social as empleado, es_responsable_cliente from vta_bodega as B inner join tbl_cliente as C on C.id_cliente=B.id_responsable "
                    + "where (lower(bodega) like '%" + txt + "%' or lower(ruc) like '" + txt + "%' or lower(razon_social) like '%" + txt + "%' and es_responsable_cliente=true) "
                    + "order by bodega limit 10 offset 0";
        }

        return this.consulta(consulta);
    }

    public ResultSet getBodegasPersonalizacion(String txt, boolean obtener_equipo_nodo, boolean obtener_equipo_empleado, boolean obtener_equipo_cliente, 
            String tipo_envio, boolean buscartodaslasbodegas, String ids, String entregaORecibe) {
        String bodega_habil = "";
        if (!buscartodaslasbodegas) {
            bodega_habil = " and B.bodega_personalizacion=true ";
        }
        if (ids.trim().compareTo("") != 0) {        //  bodegas blolqueadas
            ids = " and B.id_bodega not in (" + ids + ") ";
        }
        
//        String consulta = "SELECT id_bodega, bodega, E.dni, E.empleado, es_responsable_cliente from vta_bodega as B inner join vta_empleado as E on B.id_responsable=E.id_empleado "
//                + "where (lower(bodega) like '%" + txt + "%' or lower(E.dni) like '" + txt + "%' or lower(E.empleado) like '%" + txt + "%' and es_responsable_cliente=false) " + bodega_habil + " " + ids + ""
//                + "union "
//                + "select id_bodega, bodega, ruc as dni, razon_social as empleado, es_responsable_cliente from vta_bodega as B inner join tbl_cliente as C on C.id_cliente=B.id_responsable "
//                + "where (lower(bodega) like '%" + txt + "%' or lower(ruc) like '" + txt + "%' or lower(razon_social) like '%" + txt + "%' and es_responsable_cliente=true) " + bodega_habil + " "
//                + "order by bodega limit 10 offset 0";
        
        String consulta = "";
        if( obtener_equipo_nodo ){
            consulta += "select id_bodega, bodega, E.dni, E.nombre || ' ' || E.apellido as empleado, es_responsable_cliente "
                + "from tbl_bodega as B inner join tbl_empleado as E on B.id_responsable=E.id_empleado "
                + "where (lower(bodega) like '%" + txt + "%' or lower(E.dni) like '" + txt + "%' or lower(E.nombre) like '%" + txt + "%' or lower(E.apellido) like '%" + txt + "%') and es_responsable_cliente=false " 
                + bodega_habil + ids + " and id_bodega in(select distinct id_bodega from tbl_nodo_bodega) ";
            if(entregaORecibe.compareTo("bodega_recibe")==0) {
                consulta += " and B.estado and not B.eliminado ";
            }
        }
        if( obtener_equipo_empleado ){
            consulta += (consulta.compareTo("") !=0 ? "\n union \n" : "") + "SELECT id_bodega, bodega, E.dni, E.nombre || ' ' || E.apellido as empleado, es_responsable_cliente "
                + "from tbl_bodega as B inner join tbl_empleado as E on B.id_responsable=E.id_empleado "
                + "where (lower(bodega) like '%" + txt + "%' or lower(E.dni) like '" + txt + "%' or lower(E.nombre) like '%" + txt + "%' or lower(E.apellido) like '%" + txt + "%') and es_responsable_cliente=false " 
                + bodega_habil + ids+ " and not B.eliminado and id_bodega not in(select distinct id_bodega from tbl_nodo_bodega) ";
            if(entregaORecibe.compareTo("bodega_recibe")==0) {
                consulta += " and B.estado and not B.eliminado ";
            }
        }
        if( obtener_equipo_cliente ){
            consulta += (consulta.compareTo("") !=0 ? "\n union \n" : "") + "select id_bodega, bodega, ruc as dni, razon_social as empleado, es_responsable_cliente "
                + "from tbl_bodega as B inner join tbl_cliente as C on C.id_cliente=B.id_responsable "
                + "where (lower(bodega) like '%" + txt + "%' or lower(ruc) like '" + txt + "%' or lower(razon_social) like '%" + txt + "%') and es_responsable_cliente=true " + bodega_habil;
            if(entregaORecibe.compareTo("bodega_recibe")==0) {
                consulta += " and B.estado and not B.eliminado ";
            }
        }
        if( consulta.compareTo("") !=0 ){
            consulta += " order by bodega limit 10 offset 0";
        }
        
        if (!obtener_equipo_empleado && tipo_envio.trim().compareTo("2") == 0) {
            consulta = "select id_bodega, bodega, ruc as dni, razon_social as empleado, es_responsable_cliente from vta_bodega as B inner join tbl_cliente as C on C.id_cliente=B.id_responsable "
                    + "where (lower(bodega) like '%" + txt + "%' or lower(ruc) like '" + txt + "%' or lower(razon_social) like '%" + txt + "%') and es_responsable_cliente=true "
                    + (entregaORecibe.compareTo("bodega_recibe")==0 ? " and B.estado and not B.eliminado " : "")
                    + "order by bodega limit 10 offset 0";
        }

        return this.consulta(consulta);
    }

    public ResultSet getBodegasPersonalizacion(String txt, int id_sucursal) {
        
        if( id_sucursal == 0 ){
            return this.consulta("SELECT id_bodega, bodega, E.dni, E.empleado, es_responsable_cliente from vta_bodega as B inner join vta_empleado as E on B.id_responsable=E.id_empleado "
                + "where (lower(bodega) like '%" + txt + "%' or lower(E.dni) like '" + txt + "%' or lower(E.empleado) like '%" + txt + "%') and es_responsable_cliente=false "
                + "union "
                + "select id_bodega, bodega, ruc as dni, razon_social as empleado, es_responsable_cliente from vta_bodega as B inner join tbl_cliente as C on C.id_cliente=B.id_responsable "
                + "where (lower(bodega) like '%" + txt + "%' or lower(ruc) like '" + txt + "%' or lower(razon_social) like '%" + txt + "%') and es_responsable_cliente=true "
                + "order by bodega limit 10 offset 0");
        }
        return this.consulta("SELECT id_bodega, bodega, E.dni, E.empleado, es_responsable_cliente from vta_bodega as B inner join vta_empleado as E on B.id_responsable=E.id_empleado "
                + "where (lower(bodega) like '%" + txt + "%' or lower(E.dni) like '" + txt + "%' or lower(E.empleado) like '%" + txt + "%') and es_responsable_cliente=false and B.id_sucursal='" + id_sucursal + "' "
                + "union "
                + "select id_bodega, bodega, ruc as dni, razon_social as empleado, es_responsable_cliente from vta_bodega as B inner join tbl_cliente as C on C.id_cliente=B.id_responsable "
                + "where (lower(bodega) like '%" + txt + "%' or lower(ruc) like '" + txt + "%' or lower(razon_social) like '%" + txt + "%') and es_responsable_cliente=true and B.id_sucursal='" + id_sucursal + "' "
                + "order by bodega limit 10 offset 0");
    }

    public ResultSet getBodegasPersonalizacion(String txt, String id_sucursal, boolean obtener_equipo_empleado, String tipo_envio) {
        String consulta = "SELECT id_bodega, bodega, E.dni, E.empleado, es_responsable_cliente from vta_bodega as B inner join vta_empleado as E on B.id_responsable=E.id_empleado "
                + "where (lower(bodega) like '%" + txt + "%' or lower(E.dni) like '" + txt + "%' or lower(E.empleado) like '%" + txt + "%') and es_responsable_cliente=false and B.id_sucursal='" + id_sucursal + "' "
                + "union "
                + "select id_bodega, bodega, ruc as dni, razon_social as empleado, es_responsable_cliente from vta_bodega as B inner join tbl_cliente as C on C.id_cliente=B.id_responsable "
                + "where (lower(bodega) like '%" + txt + "%' or lower(ruc) like '" + txt + "%' or lower(razon_social) like '%" + txt + "%') and es_responsable_cliente=true and B.id_sucursal='" + id_sucursal + "' "
                + "order by bodega limit 10 offset 0";
        if (!obtener_equipo_empleado && tipo_envio.trim().compareTo("2") == 0) {
            consulta = "select id_bodega, bodega, ruc as dni, razon_social as empleado, es_responsable_cliente from vta_bodega as B inner join tbl_cliente as C on C.id_cliente=B.id_responsable "
                    + "where (lower(bodega) like '%" + txt + "%' or lower(ruc) like '" + txt + "%' or lower(razon_social) like '%" + txt + "%') and es_responsable_cliente=true and B.id_sucursal='" + id_sucursal + "' "
                    + "order by bodega limit 10 offset 0";
        }
        return this.consulta(consulta);
    }

    public ResultSet getBodegasPersonalizacion(String txt, String id_sucursal, boolean obtener_equipo_empleado, String tipo_envio, boolean buscartodaslasbodegas) {
        String bodega_habil = "";
        if (!buscartodaslasbodegas) {
            bodega_habil = " and B.bodega_personalizacion=true ";
        }
        String consulta = "SELECT id_bodega, bodega, E.dni, E.empleado, es_responsable_cliente from vta_bodega as B inner join vta_empleado as E on B.id_responsable=E.id_empleado "
                + "where (lower(bodega) like '%" + txt + "%' or lower(E.dni) like '" + txt + "%' or lower(E.empleado) like '%" + txt + "%' and es_responsable_cliente=false) and B.id_sucursal='" + id_sucursal + "' " + bodega_habil + " "
                + "union "
                + "select id_bodega, bodega, ruc as dni, razon_social as empleado, es_responsable_cliente from vta_bodega as B inner join tbl_cliente as C on C.id_cliente=B.id_responsable "
                + "where (lower(bodega) like '%" + txt + "%' or lower(ruc) like '" + txt + "%' or lower(razon_social) like '%" + txt + "%' and es_responsable_cliente=true) and B.id_sucursal='" + id_sucursal + "' " + bodega_habil 
                + " order by bodega limit 10 offset 0";
        if (!obtener_equipo_empleado && tipo_envio.trim().compareTo("2") == 0) {
            consulta = "select id_bodega, bodega, ruc as dni, razon_social as empleado, es_responsable_cliente from vta_bodega as B inner join tbl_cliente as C on C.id_cliente=B.id_responsable "
                    + "where (lower(bodega) like '%" + txt + "%' or lower(ruc) like '" + txt + "%' or lower(razon_social) like '%" + txt + "%' and es_responsable_cliente=true) and B.id_sucursal='" + id_sucursal + "' "
                    + "order by bodega limit 10 offset 0";
        }
        return this.consulta(consulta);
    }

    public ResultSet getBodegasPersonalizacion(String txt, String id_sucursal, boolean obtener_equipo_nodo, boolean obtener_equipo_empleado, boolean obtener_equipo_cliente, 
            String tipo_envio, boolean buscartodaslasbodegas, String ids, String entregaORecibe) {
        String bodega_habil = "";
        if (!buscartodaslasbodegas) {
            bodega_habil = " and B.bodega_personalizacion=true ";
        }
        if (ids.trim().compareTo("") != 0) {
            ids = " and B.id_bodega not in (" + ids + ") ";
        }
        
        String consulta = "";
        if( obtener_equipo_nodo ){
            consulta += "select id_bodega, bodega, E.dni, E.nombre || ' ' || E.apellido as empleado, es_responsable_cliente "
                + "from tbl_bodega as B inner join tbl_empleado as E on B.id_responsable=E.id_empleado "
                + "where (lower(bodega) like '%" + txt + "%' or lower(E.dni) like '" + txt + "%' or lower(E.nombre) like '%" + txt + "%' or lower(E.apellido) like '%" + txt + "%') and es_responsable_cliente=false " 
                + bodega_habil + ids + " and B.id_sucursal='" + id_sucursal + "'  and id_bodega in(select distinct id_bodega from tbl_nodo_bodega) ";
            if(entregaORecibe.compareTo("bodega_recibe")==0) {
                consulta += " and B.estado and not B.eliminado ";
            }
        }
        if( obtener_equipo_empleado ){
            consulta += (consulta.compareTo("") !=0 ? "\n union \n" : "") + "SELECT id_bodega, bodega, E.dni, E.nombre || ' ' || E.apellido as empleado, es_responsable_cliente "
                + "from tbl_bodega as B inner join tbl_empleado as E on B.id_responsable=E.id_empleado "
                + "where (lower(bodega) like '%" + txt + "%' or lower(E.dni) like '" + txt + "%' or lower(E.nombre) like '%" + txt + "%' or lower(E.apellido) like '%" + txt + "%') and es_responsable_cliente=false " 
                + bodega_habil + ids+ " and not B.eliminado and B.id_sucursal='" + id_sucursal + "'  and id_bodega not in(select distinct id_bodega from tbl_nodo_bodega) ";
            if(entregaORecibe.compareTo("bodega_recibe")==0) {
                consulta += " and B.estado and not B.eliminado ";
            }
        }
        
        if( obtener_equipo_cliente ){
            consulta += (consulta.compareTo("") !=0 ? "\n union \n" : "") + "select id_bodega, bodega, ruc as dni, razon_social as empleado, es_responsable_cliente "
                + "from tbl_bodega as B inner join tbl_cliente as C on C.id_cliente=B.id_responsable "
                + "where (lower(bodega) like '%" + txt + "%' or lower(ruc) like '" + txt + "%' or lower(razon_social) like '%" + txt + "%') and es_responsable_cliente=true  and B.id_sucursal='" + id_sucursal + "' " + bodega_habil;
            if(entregaORecibe.compareTo("bodega_recibe")==0) {
                consulta += " and B.estado and not B.eliminado ";
            }
        }
                
//        String consulta = "SELECT id_bodega, bodega, E.dni, E.empleado, es_responsable_cliente from vta_bodega as B inner join vta_empleado as E on B.id_responsable=E.id_empleado "
//                + "where (lower(bodega) like '%" + txt + "%' or lower(E.dni) like '" + txt + "%' or lower(E.empleado) like '%" + txt + "%' and es_responsable_cliente=false) and B.id_sucursal='" + id_sucursal + "' " + bodega_habil + " " + ids + " "
//                + "union "
//                + "select id_bodega, bodega, ruc as dni, razon_social as empleado, es_responsable_cliente from vta_bodega as B inner join tbl_cliente as C on C.id_cliente=B.id_responsable "
//                + "where (lower(bodega) like '%" + txt + "%' or lower(ruc) like '" + txt + "%' or lower(razon_social) like '%" + txt + "%' and es_responsable_cliente=true) and B.id_sucursal='" + id_sucursal + "' " + bodega_habil + " "
//                + "order by bodega limit 10 offset 0";
        if (!obtener_equipo_empleado && tipo_envio.trim().compareTo("2") == 0) {
            consulta = "select id_bodega, bodega, ruc as dni, razon_social as empleado, es_responsable_cliente from vta_bodega as B inner join tbl_cliente as C on C.id_cliente=B.id_responsable "
                    + "where (lower(bodega) like '%" + txt + "%' or lower(ruc) like '" + txt + "%' or lower(razon_social) like '%" + txt + "%') and es_responsable_cliente=true and B.id_sucursal='" + id_sucursal + "' "
                    + (entregaORecibe.compareTo("bodega_recibe")==0 ? " and B.estado and not B.eliminado " : "")
                    + "order by bodega limit 10 offset 0";
        }
        return this.consulta(consulta);
    }

    public Bodega() {
    }

    public ResultSet getBodegas(int id_sucursal) {
        return this.consulta("SELECT id_bodega, bodega from vta_bodega where es_responsable_cliente=false and id_sucursal=" + id_sucursal);
    }

    public ResultSet getBodegasResponsables() {
        return this.consulta("SELECT B.id_bodega,B.bodega || ' -> ' || E.empleado FROM vta_bodega as B inner join vta_empleado as E on B.id_responsable=E.id_empleado where B.estado and not B.eliminado order by B.bodega,E.empleado;");
    }

    public ResultSet getBodegasResponsables(int sucursal, String responsable) {
        return this.consulta("SELECT B.id_bodega,B.bodega || ' -> ' || E.empleado FROM vta_bodega as B"
                + " inner join vta_empleado as E on B.id_responsable=E.id_empleado"
                + " where B.id_sucursal='" + sucursal + "' and E.alias='" + responsable + "'"
                + " order by B.bodega,E.empleado;");
    }

    public String[] getBodegasResponsables(int sucursal, String responsable, String parm1) {
        String res[] = new String[]{"", "", "", "", ""};
        try {
            ResultSet rs = this.consulta("SELECT B.id_bodega,B.bodega || ' -> ' || E.empleado FROM vta_bodega as B"
                    + " inner join vta_empleado as E on B.id_responsable=E.id_empleado"
                    + " where B.id_sucursal='" + sucursal + "' and E.alias='" + responsable + "'"
                    + " and not B.eliminado and B.estado order by B.bodega,E.empleado;");
            if (rs.next()) {
                res[0] = rs.getString(1) != null ? rs.getString(1) : "";
                res[1] = rs.getString(2) != null ? rs.getString(2) : "";
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public ResultSet getResponsables(String txt) {
        return this.consulta("SELECT id_empleado, empleado || '(empleado)' as empleado, '-1' as id_instalacion FROM vta_empleado where lower(empleado) like '%" + txt + "%' "
                + "union "
                + "select id_cliente as id_empleado, razon_social || '(cliente)' as empleado, id_instalacion from vta_instalacion where estado_servicio not in ('t') and anulado=false "
                + "and lower(razon_social) like '%" + txt + "%' "
                + "order by empleado limit 10 offset 0");
        //and id_instalacion not in (select id_instalacion from tbl_bodega where id_instalacion is not null)
    }

    public String[] getBodegaResponsableTecnico(String id_empleado) {
        String res[] = new String[]{"", "", "", "", ""};
        try {
            ResultSet rs = this.consulta("SELECT B.id_bodega,B.bodega,B.ubicacion, E.dni, E.empleado FROM vta_bodega as B inner join vta_empleado as E on B.id_responsable=E.id_empleado "
                    + "where B.id_responsable=" + id_empleado + " and es_responsable_cliente=false");
            if (rs.next()) {
                res[0] = rs.getString("id_bodega") != null ? rs.getString("id_bodega") : "";
                res[1] = rs.getString("bodega") != null ? rs.getString("bodega") : "";
                res[2] = rs.getString("ubicacion") != null ? rs.getString("ubicacion") : "";
                res[3] = rs.getString("dni") != null ? rs.getString("dni") : "";
                res[4] = rs.getString("empleado") != null ? rs.getString("empleado") : "";
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public String[] getBodegaResponsableCliente(String id_empleado) {
        String res[] = new String[]{"", "", "", "", ""};
        try {
            ResultSet rs = this.consulta("SELECT B.id_bodega,B.bodega,B.ubicacion, E.ruc, E.razon_social "
                    + "FROM vta_bodega as B inner join vta_cliente as E on B.id_responsable=E.id_cliente "
                    + "where B.id_responsable=" + id_empleado + " and es_responsable_cliente=true");
            if (rs.next()) {
                res[0] = rs.getString("id_bodega") != null ? rs.getString("id_bodega") : "";
                res[1] = rs.getString("bodega") != null ? rs.getString("bodega") : "";
                res[2] = rs.getString("ubicacion") != null ? rs.getString("ubicacion") : "";
                res[3] = rs.getString("ruc") != null ? rs.getString("ruc") : "";
                res[4] = rs.getString("razon_social") != null ? rs.getString("razon_social") : "";
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public String[] getBodegaResponsableCliente(String id_cliente, String id_instalacion) {
        String res[] = new String[]{"", "", "", "", ""};
        try {
            ResultSet rs = this.consulta("SELECT B.id_bodega,B.bodega,B.ubicacion, E.ruc, E.razon_social "
                    + "FROM vta_bodega as B inner join vta_cliente as E on B.id_responsable=E.id_cliente "
                    + "where B.id_responsable=" + id_cliente + " and es_responsable_cliente=true and B.id_instalacion=" + id_instalacion + "");
            if (rs.next()) {
                res[0] = rs.getString("id_bodega") != null ? rs.getString("id_bodega") : "";
                res[1] = rs.getString("bodega") != null ? rs.getString("bodega") : "";
                res[2] = rs.getString("ubicacion") != null ? rs.getString("ubicacion") : "";
                res[3] = rs.getString("ruc") != null ? rs.getString("ruc") : "";
                res[4] = rs.getString("razon_social") != null ? rs.getString("razon_social") : "";
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public String[] getBodegaResponsableCliente(String id_cliente, String id_instalacion, String id_sucursal, String num_instalacion, String razon_social, String direccion_instalacion) {
        String res[] = new String[]{"", "", "", "", ""};
        String sql = "SELECT B.id_bodega,B.bodega,B.ubicacion, E.ruc, E.razon_social "
                + " FROM vta_bodega as B inner join vta_cliente as E on B.id_responsable=E.id_cliente "
                + " where B.id_responsable=" + id_cliente + " and es_responsable_cliente=true and B.id_instalacion=" + id_instalacion + ";";
        try {
            ResultSet rs = this.consulta(sql);
            if (this.getFilas(rs) == 0) {
                ResultSet rs1 = this.consulta("SELECT B.id_bodega,B.bodega,B.ubicacion, E.ruc, E.razon_social "
                        + " FROM vta_bodega as B inner join vta_cliente as E on B.id_responsable=E.id_cliente "
                        + " where es_responsable_cliente=true and B.id_instalacion=" + id_instalacion + ";");
                if (this.getFilas(rs1) == 0) {
                    this.ejecutar("insert into tbl_bodega(id_sucursal, bodega, id_responsable, ubicacion, id_instalacion, es_responsable_cliente) values "
                            + " (" + id_sucursal + ", 'INSTALACION No. " + id_sucursal + "-" + num_instalacion + " " + razon_social + "', " + id_cliente + ", '" + direccion_instalacion + "',"
                            + " " + id_instalacion + ", true);");
                } else {
                    this.ejecutar("update tbl_bodega set id_responsable='" + id_cliente + "' where id_instalacion='" + id_instalacion + "';");
                }
                rs = this.consulta(sql);
            }
            if (rs.next()) {
                res[0] = rs.getString("id_bodega") != null ? rs.getString("id_bodega") : "";
                res[1] = rs.getString("bodega") != null ? rs.getString("bodega") : "";
                res[2] = rs.getString("ubicacion") != null ? rs.getString("ubicacion") : "";
                res[3] = rs.getString("ruc") != null ? rs.getString("ruc") : "";
                res[4] = rs.getString("razon_social") != null ? rs.getString("razon_social") : "";
                rs.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public String getIdBodega(String id_empleado) {
        String id_bodega = "";
        try {
            ResultSet r = this.consulta("SELECT * FROM tbl_bodega where es_responsable_cliente=false and bodega_orden=true and id_responsable=" + id_empleado);
            if (r.next()) {
                id_bodega = (r.getString("id_bodega") != null) ? r.getString("id_bodega") : "";
                r.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id_bodega;
    }
    
    public String getIdBodega(int idSucursal, String idEmpleado) {
        String id_bodega = "";
        try {
            ResultSet r = this.consulta("SELECT * FROM tbl_bodega where es_responsable_cliente=false and bodega_orden=true and id_sucursal="+idSucursal+" and id_responsable=" + idEmpleado);
            if (r.next()) {
                id_bodega = (r.getString("id_bodega") != null) ? r.getString("id_bodega") : "";
                r.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id_bodega;
    }

    public String getIdBodegaHalilitadaEnSucursal(int idSucursal, String idEmpleado) {
        String id_bodega = "";
        try {
            ResultSet r = this.consulta("SELECT * FROM tbl_bodega where es_responsable_cliente=false and bodega_orden=true and id_sucursal="+idSucursal+" and id_responsable=" + idEmpleado);
            if (r.next()) {
                id_bodega = (r.getString("id_bodega") != null) ? r.getString("id_bodega") : "";
                r.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id_bodega;
    }

    ///orden de trabajo nuevo
    public String getIdBodegaCliente(String id_cliente, String id_instalacion) {
        String id_bodega = "";
        try {
            ResultSet r = this.consulta("SELECT * FROM tbl_bodega where es_responsable_cliente=true and id_responsable='" + id_cliente + "' and id_instalacion='" + id_instalacion + "';");
            if (r.next()) {
                id_bodega = (r.getString("id_bodega") != null) ? r.getString("id_bodega") : "";
                r.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id_bodega;
    }

    public ResultSet getBodegaUsuario(String usuario) {
        return this.consulta("SELECT B.*, E.dni, E.nombre || ' ' || E.apellido as empleado FROM tbl_bodega as B inner join tbl_empleado as E on B.id_responsable=E.id_empleado where E.alias='" + usuario + "' and B.eliminado =false and B.bodega_personalizacion=true and B.estado =true;");
    }

    public String getNombre(String id) {
        String bodega1 = "";
        if (id.compareTo("-0") != 0) {
            try {
                ResultSet r = this.consulta("SELECT * FROM tbl_bodega where id_bodega=" + id);
                if (r.next()) {
                    bodega1 = (r.getString("bodega") != null) ? r.getString("bodega") : "";
                    r.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bodega1;
    }

    public String getResponsable(String id) {
        String empleado = "";
        if (id.compareTo("-0") != 0) {
            try {
                ResultSet r = this.consulta("SELECT E.empleado FROM tbl_bodega as B inner join vta_empleado as E on B.id_responsable=E.id_empleado where B.id_bodega=" + id);
                if (r.next()) {
                    empleado = (r.getString("empleado") != null) ? r.getString("empleado") : "";
                    r.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return empleado;
    }

    public boolean estaDuplicado(String id, String bodega) {
        ResultSet res = this.consulta("SELECT * FROM tbl_bodega where lower(bodega)='" + bodega.toLowerCase() + "' and id_bodega<>" + id);
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

    public String insertar(String id_sucursal, String bodega, String id_responsable, String coresponsable,
            String ubicacion, String estado, String id_instalacion, String id_tipoBodega) {
        String es_responsable_cliente = "true";
        if (id_instalacion.compareTo("-0") == 0 || id_instalacion.compareTo("-1") == 0 || id_instalacion.compareTo("0") == 0 || id_instalacion.compareTo("") == 0) {
            es_responsable_cliente = "false";
            id_instalacion = "null";
        }

        String pk = this.insert("INSERT INTO tbl_bodega(id_sucursal,bodega,id_responsable,coresponsable,ubicacion,estado, id_instalacion, es_responsable_cliente, id_bodega_tipo) VALUES("
                + id_sucursal + ", '" + bodega + "', " + id_responsable + ", '" + coresponsable + "', '" + ubicacion + "', " + estado + ", " + id_instalacion + ", " + es_responsable_cliente + ", " + id_tipoBodega + ")");
        if (pk.compareTo("-1") != 0) {
            this.ejecutar("INSERT INTO tbl_estanteria(id_bodega,estanteria,fila,columna,num_nivel) VALUES(" + pk + ", '" + this.getNombreEstanteria(bodega) + "', 1, 1, 1)");
        }
        return pk;
    }

    public String insertar(String id_sucursal, String bodega, String id_responsable, String coresponsable,
            String ubicacion, String estado, String id_instalacion, String id_tipoBodega, String para_stock) {
        String es_responsable_cliente = "true";
        if (id_instalacion.compareTo("-0") == 0 || id_instalacion.compareTo("-1") == 0 || id_instalacion.compareTo("0") == 0 || id_instalacion.compareTo("") == 0) {
            es_responsable_cliente = "false";
            id_instalacion = "null";
        }

        String pk = this.insert("INSERT INTO tbl_bodega(id_sucursal,bodega,id_responsable,coresponsable,ubicacion,estado, id_instalacion, es_responsable_cliente, id_bodega_tipo,para_stock) VALUES("
                + id_sucursal + ", '" + bodega + "', " + id_responsable + ", '" + coresponsable + "', '" + ubicacion + "', " + estado + ", " + id_instalacion + ", " + es_responsable_cliente + ", " + id_tipoBodega + "," + para_stock + ")");
        if (pk.compareTo("-1") != 0) {
            this.ejecutar("INSERT INTO tbl_estanteria(id_bodega,estanteria,fila,columna,num_nivel) VALUES(" + pk + ", '" + this.getNombreEstanteria(bodega) + "', 1, 1, 1)");
        }
        return pk;
    }

    public String insertar(String id_sucursal, String bodega, String id_responsable, String coresponsable,
            String ubicacion, String estado, String id_instalacion, String id_tipoBodega, String para_stock, String bodega_orden, String bodega_personalizacion) {
        String es_responsable_cliente = "true";
        if (id_instalacion.compareTo("-0") == 0 || id_instalacion.compareTo("-1") == 0 || id_instalacion.compareTo("0") == 0 || id_instalacion.compareTo("") == 0) {
            es_responsable_cliente = "false";
            id_instalacion = "null";
        }

        String pk = this.insert("INSERT INTO tbl_bodega(id_sucursal,bodega,id_responsable,coresponsable,ubicacion,estado, id_instalacion, es_responsable_cliente, id_bodega_tipo,para_stock,bodega_orden,bodega_personalizacion) VALUES("
                + id_sucursal + ", '" + bodega + "', " + id_responsable + ", '" + coresponsable + "', '" + ubicacion + "', " + estado + ", " + id_instalacion + ", " + es_responsable_cliente + ", " + id_tipoBodega + "," + para_stock + "," + bodega_orden + "," + bodega_personalizacion + ")");
        if (pk.compareTo("-1") != 0) {
            this.ejecutar("INSERT INTO tbl_estanteria(id_bodega,estanteria,fila,columna,num_nivel) VALUES(" + pk + ", '" + this.getNombreEstanteria(bodega) + "', 1, 1, 1)");
        }
        return pk;
    }

    public String insertar(String id_sucursal, String bodega, String id_responsable, String coresponsable,
            String ubicacion, String estado, String id_instalacion, String id_tipoBodega, String para_stock, String bodega_orden) {
        String es_responsable_cliente = "true";
        if (id_instalacion.compareTo("-0") == 0 || id_instalacion.compareTo("-1") == 0 || id_instalacion.compareTo("0") == 0 || id_instalacion.compareTo("") == 0) {
            es_responsable_cliente = "false";
            id_instalacion = "null";
        }

        String pk = this.insert("INSERT INTO tbl_bodega(id_sucursal,bodega,id_responsable,coresponsable,ubicacion,estado, id_instalacion, es_responsable_cliente, id_bodega_tipo,para_stock,bodega_orden) VALUES("
                + id_sucursal + ", '" + bodega + "', " + id_responsable + ", '" + coresponsable + "', '" + ubicacion + "', " + estado + ", " + id_instalacion + ", " + es_responsable_cliente + ", " + id_tipoBodega + "," + para_stock + "," + bodega_orden + ")");
        if (pk.compareTo("-1") != 0) {
            this.ejecutar("INSERT INTO tbl_estanteria(id_bodega,estanteria,fila,columna,num_nivel) VALUES(" + pk + ", '" + this.getNombreEstanteria(bodega) + "', 1, 1, 1)");
        }
        return pk;
    }

    public boolean actualizar(String id, String id_sucursal, String bodega, String id_responsable, String coresponsable,
            String ubicacion, String estado, String id_tipoBodega, String para_stock, String bodega_orden, String bodega_personalizacion) {
        List sql = new ArrayList();
        if (bodega_orden.trim().compareTo("true") == 0) {
            sql.add("update tbl_bodega set bodega_orden =false where id_responsable ='" + id_responsable + "' and id_sucursal ='" + id_sucursal + "' and es_responsable_cliente =(select t.es_responsable_cliente from tbl_bodega as t where t.id_bodega ='" + id + "');");
        }
        sql.add("UPDATE tbl_bodega SET id_sucursal=" + id_sucursal + ", bodega='" + bodega + "', id_responsable=" + id_responsable + ", "
                + "coresponsable='" + coresponsable + "', ubicacion='" + ubicacion + "', estado=" + estado + ", id_bodega_tipo=" + id_tipoBodega + ",para_stock=" + para_stock + ",bodega_orden=" + bodega_orden + ",bodega_personalizacion=" + bodega_personalizacion + " WHERE id_bodega=" + id);
        return this.transacciones(sql);
    }

    public boolean actualizar(String id, String id_sucursal, String bodega, String id_responsable, String coresponsable,
            String ubicacion, String estado, String id_tipoBodega) {
        return this.ejecutar("UPDATE tbl_bodega SET id_sucursal=" + id_sucursal + ", bodega='" + bodega + "', id_responsable=" + id_responsable + ", "
                + "coresponsable='" + coresponsable + "', ubicacion='" + ubicacion + "', estado=" + estado + ", id_bodega_tipo=" + id_tipoBodega + " WHERE id_bodega=" + id);
    }

    public boolean actualizar(String id, String id_sucursal, String bodega, String id_responsable, String coresponsable,
            String ubicacion, String estado, String id_tipoBodega, String para_stock) {
        return this.ejecutar("UPDATE tbl_bodega SET id_sucursal=" + id_sucursal + ", bodega='" + bodega + "', id_responsable=" + id_responsable + ", "
                + "coresponsable='" + coresponsable + "', ubicacion='" + ubicacion + "', estado=" + estado + ", id_bodega_tipo=" + id_tipoBodega + ",para_stock=" + para_stock + " WHERE id_bodega=" + id);
    }

    public boolean actualizar(String id, String id_sucursal, String bodega, String id_responsable, String coresponsable,
            String ubicacion, String estado, String id_tipoBodega, String para_stock, String bodega_orden) {
        return this.ejecutar("UPDATE tbl_bodega SET id_sucursal=" + id_sucursal + ", bodega='" + bodega + "', id_responsable=" + id_responsable + ", "
                + "coresponsable='" + coresponsable + "', ubicacion='" + ubicacion + "', estado=" + estado + ", id_bodega_tipo=" + id_tipoBodega + ",para_stock=" + para_stock + ",bodega_orden=" + bodega_orden + " WHERE id_bodega=" + id);
    }

    public boolean actualizar(String id, String bodega, String id_responsable) {
        return this.ejecutar("UPDATE tbl_bodega SET bodega='" + bodega + "', id_responsable=" + id_responsable + " WHERE id_instalacion=" + id);
    }

    /*   ESTANTERIAS   */
    public String getNombreEstanteria(String bodega) {
        String estanteria = "EST";
        String vec[] = bodega.split(" ");
        for (int i = 0; i < vec.length; i++) {
            int lim = vec[i].length() > 3 ? 3 : vec[i].length();
            estanteria += vec[i].substring(0, lim);
        }
        return estanteria + "001";
    }

    public ResultSet getEstanteria(String id_estanteria) {
        return this.consulta("SELECT * from tbl_estanteria where id_estanteria=" + id_estanteria);
    }

    public ResultSet getEstanterias(String id_bodega) {
        return this.consulta("SELECT * from tbl_estanteria where id_bodega=" + id_bodega + " and eliminado=false");
    }

    public ResultSet getEstanterias(int id_bodega) {
        return this.consulta("SELECT estanteria, estanteria from tbl_estanteria where id_bodega=" + id_bodega + " and eliminado=false");
    }

    public ResultSet getUbicaciones(int id_bodega) {
        return this.consulta("SELECT estanteria, estanteria from tbl_estanteria where id_bodega=" + id_bodega + " and eliminado=false");
    }

    public String tblEstanterias(String id_bodega) {
        String html = "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0' id='tblPE'>";
        try {
            int i = 0;
            String id_estanteria = "";
            String estanteria = "";
            String fila = "";
            String columna = "";
            String num_nivel = "";
            ResultSet rsEstanterias = this.getEstanterias(id_bodega);
            while (rsEstanterias.next()) {
                id_estanteria = (rsEstanterias.getString("id_estanteria") != null) ? rsEstanterias.getString("id_estanteria") : "";
                estanteria = (rsEstanterias.getString("estanteria") != null) ? rsEstanterias.getString("estanteria") : "";
                fila = (rsEstanterias.getString("fila") != null) ? rsEstanterias.getString("fila") : "";
                columna = (rsEstanterias.getString("columna") != null) ? rsEstanterias.getString("columna") : "";
                num_nivel = (rsEstanterias.getString("num_nivel") != null) ? rsEstanterias.getString("num_nivel") : "";
                html += "<tr id='rTI" + i + "' valign='top' class='jm_filaPar' onclick=\"adm_estanteriaEditar(" + id_estanteria + ");\" style='cursor:pointer' onmouseover=\"this.className='jm_filaSobre'\" onmouseout=\"this.className='jm_filaPar'\">";
                html += "<td width='121' style='cursor:pointer'>" + estanteria + "</td>";
                html += "<td width='81' style='cursor:pointer'>" + fila + "</td>";
                html += "<td width='81' style='cursor:pointer'>" + columna + "</td>";
                html += "<td width='81' style='cursor:pointer'>" + num_nivel + "</td>";
                html += "<td align='center' width='21'><input type='hidden' id='idE" + i + "' value='" + id_estanteria + "' />&nbsp;</td></tr>";
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        html += "</table>";

        return html;
    }

    public boolean estaDuplicadaEstanteria(String id, String estanteria) {
        ResultSet res = this.consulta("SELECT * FROM tbl_estanteria where lower(estanteria)='" + estanteria.toLowerCase() + "' and id_estanteria<>" + id);
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

    public String insertarEstanteria(String id_bodega, String estanteria, String fila, String columna, String num_nivel) {
        return this.insert("INSERT INTO tbl_estanteria(id_bodega,estanteria,fila,columna,num_nivel) VALUES("
                + id_bodega + ", '" + estanteria + "', " + fila + ", " + columna + ", " + num_nivel + ")");
    }

    public boolean actualizarEstanteria(String id, String estanteria, String fila, String columna, String num_nivel) {
        return this.ejecutar("UPDATE tbl_estanteria SET estanteria='" + estanteria + "', fila=" + fila + ", "
                + "columna=" + columna + ", num_nivel=" + num_nivel + " WHERE id_estanteria=" + id);
    }

    /*  ARTICULOS DE LA BODEGA  */
    public ResultSet getArticulos(String id_bodega) {
        return this.consulta("SELECT BA.ubicacion, A.codigo_activo, A.descripcion,A.valor_compra,A.valor_depreciado from tbl_bodega_activo as BA inner join tbl_activo as A on BA.id_activo=A.id_activo "
                + "where BA.id_bodega=" + id_bodega + " and A.eliminado=false and A.id_activo not in (select id_activo from tbl_activo_perdida) order by A.descripcion");
    }

    public ResultSet getArticulosv1(String idBodega) {
        return this.consulta("select tb.ubicacion, A.codigo_activo, A.descripcion, A.valor_compra, A.valor_depreciado,\n" +
            "	 (select tmp.costocompra from tbl_revalorizarac as tmp where tmp.id_revalorizarac= \n" +
            "	 (select max(tmp1.id_revalorizarac) from tbl_revalorizarac as tmp1  where tmp1.costocompra<>tmp1.costodepresiado and tmp1.id_activo=A.id_activo)) \n" +
            "	as costo_revalorizacion, \n" +
            " (select tmp.costodepresiado from tbl_revalorizarac as tmp where tmp.id_revalorizarac= \n" +
            " (select max(tmp1.id_revalorizarac) from tbl_revalorizarac as tmp1  where tmp1.costocompra<>tmp1.costodepresiado and tmp1.id_activo=A.id_activo)) \n" +
            " as valor_depreciado1 \n" +
            " from tbl_bodega tb inner join tbl_bodega_activo as BA on tb.id_bodega = BA.id_bodega\n" +
            " 	inner join tbl_activo as A on BA.id_activo=A.id_activo \n" +
            "where BA.id_bodega="+idBodega+" and A.eliminado=false and A.id_activo not in (select x.id_activo from tbl_activo_perdida as x where x.eliminado=false) \n" +
            "order by tb.bodega, A.codigo_activo;");
    }

    ////subministros
    public ResultSet getArticulossubministro(int idempleado) {
        return this.consulta("select doc.id_producto,oc.fec_recepcion,doc.cant_ordenconsumo,doc.detalle_cantidad,doc.detproducto from tbl_ordenconsumo oc\n"
                + "inner join tbl_detordenconsumo as doc on oc.id_ordenconsumo=doc.id_ordenconsumo\n"
                + "where oc.id_empleador=" + idempleado + " and oc.eliminado=false and oc.id_empleadoe is not null and doc.tipo_producto='r' order by doc.detproducto");
    }

    public ResultSet GeStockSuministro(int sucursal) {
        return this.consulta("select p.codigo,sp.stock_sucursal,p.descripcion from tbl_producto as p"
                + " inner join tbl_sucursal_producto as sp on sp.id_producto=p.id_producto and p.tipo='r'"
                + " and sp.id_sucursal='" + sucursal + "' order by sp.stock_sucursal desc");
    }

    public ResultSet GetBodegasEmpresa(boolean todo) {
        return this.consulta("select id_bodega,id_sucursal,bodega,ubicacion,responsable,id_responsable from vta_bodega " + (todo ? "" : " where es_responsable_cliente=false ") + " order by id_bodega asc;");
    }

    public ResultSet GetBodegasEmpresa(String where) {
        return this.consulta("select id_bodega,id_sucursal,bodega,ubicacion,responsable,id_responsable from vta_bodega "
                + " " + where + " "
                + " order by id_bodega asc;");
    }

    public ResultSet GetBodegasNodo(String id_nodo) {
        return this.consulta("select * from vta_nodo_bodega where id_nodo='" + id_nodo + "';");
    }

    /*Informe de bodega*/
    public ResultSet getArticulosInforme(String id_bodega) {
        return this.consulta("SELECT A.id_activo, A.codigo_activo, A.descripcion "
                + " from tbl_bodega_activo as BA inner join tbl_activo as A on BA.id_activo=A.id_activo "
                + "where BA.id_bodega=" + id_bodega + " and A.eliminado=false and A.id_activo not in (select x.id_activo from tbl_activo_perdida as x where x.eliminado=false) order by A.descripcion");
    }

    public ResultSet getArticulosInforme(String id_bodega, String id_activos) {
        id_activos = (id_activos.compareTo("") != 0 ? " and A.id_activo not in (" + id_activos + ") " : "");
        return this.consulta("SELECT A.id_activo, A.codigo_activo, A.descripcion "
                + " from tbl_bodega_activo as BA inner join tbl_activo as A on BA.id_activo=A.id_activo "
                + "where BA.id_bodega=" + id_bodega + " and A.eliminado=false " + id_activos + " and A.id_activo not in (select x.id_activo from tbl_activo_perdida as x where x.eliminado=false) order by A.descripcion");
    }

    public String setInformeBodega(String id_bodega, String id_empleado, String alias, String num_bodega_informe, String observacion, String id_sucursal) {
        return this.insert("INSERT INTO tbl_bodega_informe(id_bodega, id_empleado, alias, num_bodega_informe, observacion,id_sucursal)VALUES "
                + " ('" + id_bodega + "', '" + id_empleado + "', '" + alias + "', " + num_bodega_informe + ", '" + observacion + "','" + id_sucursal + "');");
    }

    public String getNumBodegaInforme(String id_sucursal) {
        String numero = "1";
        try {
            ResultSet r = this.consulta("select case when max(num_bodega_informe) is null then 1 else max(num_bodega_informe)+1 end as numero from tbl_bodega_informe where id_sucursal ='" + id_sucursal + "';");
            if (r.next()) {
                numero = (r.getString("numero") != null) ? r.getString("numero") : "1";
                r.close();
            }
        } catch (Exception e) {
            System.out.println("" + e.getLocalizedMessage() + " " + e.getMessage());
        }
        return numero;
    }

    public ResultSet getBodegaInforme(String id) {
        return this.consulta("SELECT * from vta_bodega_informe WHERE id_bodega_informe=" + id);
    }

    public ResultSet getBodegaInformes(String id) {
        return this.consulta("SELECT * from vta_bodega_informe WHERE id_bodega_informe in(" + id + ");");
    }

    public ResultSet getBodegaActivoInforme(String id) {
        return this.consulta("SELECT * from vta_bodega_informe_detalle WHERE id_bodega_informe=" + id + " ORDER BY revision desc;");
    }

    public boolean setAceptaInforme(String id) {
        return this.ejecutar("update tbl_bodega_informe set aceptado=true where id_bodega_informe='" + id + "';");
    }

    public String getEstaBodegaActual(String id_activo, String id_bodega) {
        String ok = "";
        try {
            ResultSet rs = this.consulta("select id_bodega,bodega from vta_bodega_activo where id_activo='" + id_activo + "';");
            String id_bodegat = "";
            String bodega = "";
            if (rs.next()) {
                id_bodegat = (rs.getString("id_bodega") != null ? rs.getString("id_bodega") : "");
                bodega = (rs.getString("bodega") != null ? rs.getString("bodega") : "");
            }
            //if (id_bodegat.compareTo(id_bodega) != 0) {
            ok = bodega;
            //}
        } catch (Exception e) {
            System.out.println("" + e.getMessage() + " " + e.getLocalizedMessage());
        }
        return ok;
    }

    public ResultSet getBodegaActivoInformeTipo(String id, String revision, String aceptado) {
        return this.consulta("SELECT * from vta_bodega_informe_detalle WHERE id_bodega_informe=" + id + " and revision in (" + revision + ") and aceptado in (" + aceptado + ") order by aceptado,revision desc;");
    }

    public int getBodegaInformeAceptados(String id, String estado) {
        int numero = 0;
        try {
            ResultSet r = this.consulta("select count(*) as contador from tbl_bodega_informe_detalle where aceptado in (" + estado + ") and id_bodega_informe ='" + id + "';");
            if (r.next()) {
                numero = (r.getString("contador") != null) ? r.getInt("contador") : 0;
                r.close();
            }
        } catch (Exception e) {
            System.out.println("" + e.getLocalizedMessage() + " " + e.getMessage());
        }
        return numero;
    }

    /*fin de informe de bodega*/
    public String getBodegasbloqueadas(String alias) {
        String id = "";
        try {
            ResultSet rs = this.consulta("select id_bodega from tbl_activo_personalizacion_bodega where alias_bloqueo like '%" + alias + "%';");
            while (rs.next()) {
                id += (rs.getString(1) != null ? rs.getString(1) : "-1") + ",";
            }
            if (id.trim().compareTo("") != 0) {
                id = id.substring(0, id.length() - 1);
            }
            rs.close();
        } catch (Exception e) {
            System.out.println("" + e.getLocalizedMessage() + " " + e.getMessage());
        }
        return id;
    }

    public String getNumInformesPendientes(String alias) {
        String msg = "";
        try {
            ResultSet rs = this.consulta("select id_sucursal || '-' || num_bodega_informe as num_informe, fecha_registro from tbl_bodega_informe as b where b.aceptado=false and alias ='" + alias + "' and estado ='a';");
            if (rs.next()) {
                String num_informe = (rs.getString("num_informe") != null ? rs.getString("num_informe") : "");
                String fecha_registro = (rs.getString("fecha_registro") != null ? rs.getString("fecha_registro") : "");
                msg += ". Nro " + num_informe + " de la fecha " + fecha_registro;
            }
        } catch (Exception e) {
            System.out.println("" + e.getMessage() + " " + e.getLocalizedMessage());
        }
        return msg;
    }

    public boolean getEsClienteBodega(String id) {
        boolean ok = true;
        try {
            ResultSet r = this.consulta("select b.es_responsable_cliente from tbl_bodega as b where b.id_bodega="
                    + "(select x.id_bodega_entrega from tbl_activo_personalizacion as x where x.id_activo_personalizacion='" + id + "')");
            if (r.next()) {
                ok = (r.getString(1) != null) ? r.getBoolean(1) : true;
                r.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok;
    }

}
