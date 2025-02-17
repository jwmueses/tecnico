/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.saitel.api.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author PC-ON
 */
public class Spliter extends DataBase {

    public Spliter(String m, int p, String db, String u, String c) {
        super(m, p, db, u, c);
    }

    public boolean estaDuplicado(String nombre, String sucursal, String sector, String id) {
        ResultSet res = this.consulta("SELECT * FROM tbl_spliter where nombre_spliter='" + nombre + "' and id_sucursal='" + sucursal + "' and id_sector='" + sector + "' and id_spliter<>" + id + ";");
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

    public String SetSpliter(String nombre_spliter, String sucursal, String sector, String direccion, String numero_puertos, String tipo, String id_activo, String codigo_equipo) {
        String pk = this.insert("INSERT INTO tbl_spliter(nombre_spliter, id_sucursal, id_sector, direccion, numero_puertos, tipo_spliter,id_activo,codigo_activo)\n"
                + "VALUES ('" + nombre_spliter + "', '" + sucursal + "', '" + sector + "', '" + direccion + "', '" + numero_puertos + "', "
                + "'" + tipo + "', '" + id_activo + "', '" + codigo_equipo + "');");
        return pk;
    }

    public String SetSpliter(String nombre_spliter, String sucursal, String sector, String direccion, String numero_puertos, String tipo, String id_activo, String codigo_equipo, String id_acoplado) {
        String pk = this.insert("INSERT INTO tbl_spliter(nombre_spliter, id_sucursal, id_sector, direccion, numero_puertos, tipo_spliter,id_activo,codigo_activo,id_acoplado)\n"
                + "VALUES ('" + nombre_spliter + "', '" + sucursal + "', '" + sector + "', '" + direccion + "', '" + numero_puertos + "', "
                + "'" + tipo + "', '" + id_activo + "', '" + codigo_equipo + "', '" + id_acoplado + "');");
        return pk;
    }

    public String SetSpliter(String nombre_spliter, String sucursal, String sector, String direccion, String numero_puertos, String tipo, String id_activo, String codigo_equipo, String id_acoplado, String puerto_conectado, String latitud_gps, String longitud_gps) {
        String pk = this.insert("INSERT INTO tbl_spliter(nombre_spliter, id_sucursal, id_sector, direccion, numero_puertos, tipo_spliter,id_activo,codigo_activo,id_acoplado,puerto_conectado,latitud_gps,longitud_gps)\n"
                + "VALUES ('" + nombre_spliter + "', '" + sucursal + "', '" + sector + "', '" + direccion + "', '" + numero_puertos + "', "
                + "'" + tipo + "', '" + id_activo + "', '" + codigo_equipo + "', '" + id_acoplado + "','" + puerto_conectado + "','" + latitud_gps + "','" + longitud_gps + "');");
        return pk;
    }

    public String SetSpliter(String nombre_spliter, String sucursal, String sector, String direccion, String numero_puertos, String tipo, String id_activo, String codigo_equipo, String id_acoplado, String puerto_conectado, String latitud_gps, String longitud_gps, String id_provincia, String id_canton, String id_parroquia) {
        String pk = this.insert("INSERT INTO tbl_spliter(nombre_spliter, id_sucursal, id_sector, direccion, numero_puertos, tipo_spliter,id_activo,codigo_activo,id_acoplado,puerto_conectado,latitud_gps,longitud_gps,id_provincia,id_canton,id_parroquia) "
                + " VALUES ('" + nombre_spliter + "', '" + sucursal + "', '" + sector + "', '" + direccion + "', '" + numero_puertos + "', "
                + " '" + tipo + "', '" + id_activo + "', '" + codigo_equipo + "', '" + id_acoplado + "','" + puerto_conectado + "','" + latitud_gps + "','" + longitud_gps + "'," + id_provincia + "," + id_canton + "," + id_parroquia + ");");
        return pk;
    }

    public boolean UpdateSpliter(String nombre_spliter, String sucursal, String sector, String direccion, String numero_puertos, String tipo, String id, String id_activo, String codigo_activo) {
        boolean pk = true;
        List sql = new ArrayList();
        sql.add("UPDATE tbl_spliter SET  nombre_spliter='" + nombre_spliter + "', id_sucursal='" + sucursal + "', id_sector='" + sector + "', direccion='" + direccion + "'"
                + ", numero_puertos='" + numero_puertos + "', tipo_spliter='" + tipo + "', id_activo='" + id_activo + "', codigo_activo='" + codigo_activo + "' WHERE id_spliter='" + id + "';");
        pk = this.transacciones(sql);
        return pk;
    }

    public boolean UpdateSpliter(String nombre_spliter, String sucursal, String sector, String direccion, String numero_puertos, String tipo, String id, String id_activo, String codigo_activo, String id_acoplado) {
        boolean pk = true;
        List sql = new ArrayList();
        sql.add("UPDATE tbl_spliter SET  nombre_spliter='" + nombre_spliter + "', id_sucursal='" + sucursal + "', id_sector='" + sector + "', direccion='" + direccion + "'"
                + ", numero_puertos='" + numero_puertos + "', tipo_spliter='" + tipo + "', id_activo='" + id_activo + "', codigo_activo='" + codigo_activo + "', id_acoplado='" + id_acoplado + "' WHERE id_spliter='" + id + "';");
        pk = this.transacciones(sql);
        return pk;
    }

    public boolean UpdateSpliter(String nombre_spliter, String sucursal, String sector, String direccion, String numero_puertos, String tipo, String id, String id_activo, String codigo_activo, String id_acoplado, String puerto_conectado, String latitud_gps, String longitud_gps) {
        boolean pk = true;
        List sql = new ArrayList();
        sql.add("UPDATE tbl_spliter SET  nombre_spliter='" + nombre_spliter + "', id_sucursal='" + sucursal + "', id_sector='" + sector + "', direccion='" + direccion + "'"
                + ", numero_puertos='" + numero_puertos + "', tipo_spliter='" + tipo + "', id_activo='" + id_activo + "', codigo_activo='" + codigo_activo + "', id_acoplado='" + id_acoplado + "',puerto_conectado='" + puerto_conectado + "',latitud_gps='" + latitud_gps + "',longitud_gps='" + longitud_gps + "' WHERE id_spliter='" + id + "';");
        pk = this.transacciones(sql);
        return pk;
    }

    public boolean UpdateSpliter(String nombre_spliter, String sucursal, String sector, String direccion, String numero_puertos, String tipo, String id, String id_activo, String codigo_activo, String id_acoplado, String puerto_conectado, String latitud_gps, String longitud_gps, String id_provincia, String id_canton, String id_parroquia) {
        boolean pk = true;
        List sql = new ArrayList();
        sql.add("UPDATE tbl_spliter SET  nombre_spliter='" + nombre_spliter + "', id_sucursal='" + sucursal + "', id_sector='" + sector + "', direccion='" + direccion + "'"
                + ", numero_puertos='" + numero_puertos + "', tipo_spliter='" + tipo + "', id_activo='" + id_activo + "', codigo_activo='" + codigo_activo + "', id_acoplado='" + id_acoplado + "',puerto_conectado='" + puerto_conectado + "',latitud_gps='" + latitud_gps + "',"
                + "longitud_gps='" + longitud_gps + "',id_provincia=" + id_provincia + ",id_canton=" + id_canton + ",id_parroquia=" + id_parroquia + " WHERE id_spliter='" + id + "';");
        pk = this.transacciones(sql);
        return pk;
    }

    public ResultSet GetSpliter(String id) {
        return this.consulta("select * from vta_spliter where id_spliter='" + id + "';");
    }

    public ResultSet BuscarSpliter(String txt, String tipo) {
        //return this.consulta("select id_spliter,nombre_spliter,numero_puertos,codigo_activo from vta_spliter where tipo_spliter in (" + tipo + ") and (lower(nombre_spliter) like '%" + txt + "%' or lower(codigo_activo) like '%" + txt + "%')");
        return this.consulta("select id_spliter,nombre_spliter,numero_puertos,codigo_activo from vta_spliter where tipo_spliter in (" + tipo + ") and (lower(nombre_spliter) like '%" + txt + "%' or lower(codigo_activo) like '%" + txt + "%')");
    }

    public ResultSet BuscarSpliters(String txt, String tipo) {
        //return this.consulta("select id_spliter,nombre_spliter,numero_puertos,codigo_activo from vta_spliter where tipo_spliter in (" + tipo + ") and (lower(nombre_spliter) like '%" + txt + "%' or lower(codigo_activo) like '%" + txt + "%')");
        return this.consulta("select * from vta_spliter where tipo_spliter in (" + tipo + ") and (lower(nombre_spliter) like '%" + txt.toLowerCase() + "%' or lower(codigo_activo) like '%" + txt.toLowerCase() + "%')");
    }

    public ResultSet BuscarSpliters(String txt, String tipo, int id_sucursal) {
        //return this.consulta("select id_spliter,nombre_spliter,numero_puertos,codigo_activo from vta_spliter where tipo_spliter in (" + tipo + ") and (lower(nombre_spliter) like '%" + txt + "%' or lower(codigo_activo) like '%" + txt + "%')");
        return this.consulta("select * from vta_spliter where tipo_spliter in (" + tipo + ") and (lower(nombre_spliter) like '%" + txt.toLowerCase() + "%' or lower(codigo_activo) like '%" + txt.toLowerCase() + "%') and id_sucursal='" + id_sucursal + "';");
    }

    public String GetNombreSpliter(String id) {
        String nombre = "";
        if( id.compareTo("")!=0 ){
            try {
                ResultSet rs = this.consulta("select nombre_spliter from tbl_spliter where id_spliter::varchar='" + id + "'");
                if (rs.next()) {
                    nombre += (rs.getString("nombre_spliter") != null ? rs.getString("nombre_spliter") : "");
                    rs.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(Spliter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return nombre;
    }
    
    public String GetpuertosSpliter(String id) {
        id = (id.trim().compareTo("") != 0 ? id : "-1");
        String puertos = "";
        try {
            ResultSet rs = this.consulta("select puerto_conectado from tbl_spliter where id_acoplado='" + id + "'"
                    + " union"
                    + " select puerto from tbl_spliter_utilizado where id_spliter='" + id + "' and estado_puerto=true;");
            while (rs.next()) {
                puertos += (rs.getString(1) != null ? rs.getString(1) : "") + ",";
            }
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Spliter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return puertos;
    }

    public ResultSet GetSpliter() {
        return this.consulta("select id_spliter,nombre_spliter from vta_spliter");
    }

    public ResultSet GetSpliterV1() {
        return this.consulta("select id_spliter,nombre_spliter from vta_spliter where tipo_spliter in ('u','s');");
    }

    public ResultSet GetSpliterV1(int id_sucursal, String spliter) {
        return this.consulta("select id_spliter as id_antena_acoplada ,nombre_spliter as antena_acoplada  from vta_spliter where tipo_spliter in ('u','s') and id_sucursal='" + id_sucursal + "' and nombre_spliter like '%" + spliter + "%' limit 10 offset 0;");
    }

    public ResultSet GetSpliter(String tipo, String parm) {
        return this.consulta("select id_spliter,nombre_spliter from vta_spliter where tipo_spliter in (" + tipo + ");");
    }

    public ResultSet GetSpliter(String tipo, String parm, int id_sucursal) {
        String where = "";
        if (id_sucursal != 1) {
            where += " and id_sucursal='" + id_sucursal + "' ";
        }
        return this.consulta("select id_spliter,nombre_spliter from vta_spliter where tipo_spliter in (" + tipo + ") " + where + ";");
    }

    public String GetpuertoSpliter(String id, String id_orden) {
        String puertos = "";
        id = (id.trim().compareTo("") != 0 ? id : "-1");
        id_orden = (id_orden.trim().compareTo("") != 0 ? id_orden : "-1");
        try {
            ResultSet rs = this.consulta("select puerto from tbl_spliter_utilizado where id_spliter='" + id + "' and id_orden_trabajo='" + id_orden + "'");
            while (rs.next()) {
                puertos += (rs.getString("puerto") != null ? rs.getString("puerto") : "");
            }
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Spliter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return puertos;
    }

    public String FrmGetpuertoSpliter(String id) {
        String puertos = "";
        id = (id.trim().compareTo("") != 0 ? id : "-1");
        try {
            ResultSet rs = this.consulta("select puerto_conectado from tbl_spliter where id_spliter='" + id + "';");
            while (rs.next()) {
                puertos += (rs.getString(1) != null ? rs.getString(1) : "0");
            }
            puertos = (puertos.trim().compareTo("") != 0 ? puertos : "0");
            if (rs != null) {
                rs.close();
            }

        } catch (SQLException ex) {
            Logger.getLogger(Spliter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return puertos;
    }

    public String FrmGetpuertoConectados(String id) {
        String puertos = "";
        id = (id.trim().compareTo("") != 0 ? id : "-1");
        try {
            ResultSet rs = this.consulta("select puerto_conectado from tbl_spliter where id_acoplado='" + id + "'"
                    + " union"
                    + " select puerto from tbl_spliter_utilizado where id_spliter='" + id + "' and estado_puerto=true;");
            while (rs.next()) {
                puertos += (rs.getString(1) != null ? rs.getString(1) : "0") + ",";
            }
            puertos = (puertos.trim().compareTo("") != 0 ? puertos : "0");
            if (rs != null) {
                rs.close();
            }

        } catch (SQLException ex) {
            Logger.getLogger(Spliter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return puertos;
    }

    public String FrmGetNumeropuerto(String id) {
        String puertos = "";
        id = (id.trim().compareTo("") != 0 ? id : "-1");
        try {
            ResultSet rs = this.consulta("select numero_puertos from tbl_spliter where id_spliter='" + id + "';");
            while (rs.next()) {
                puertos += (rs.getString(1) != null ? rs.getString(1) : "0");
            }
            puertos = (puertos.trim().compareTo("") != 0 ? puertos : "0");
            if (rs != null) {
                rs.close();
            }

        } catch (SQLException ex) {
            Logger.getLogger(Spliter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return puertos;
    }

    public ResultSet getAntenasSplitter(int id_suc, String txt) {
        String id_sucursal = (id_suc != 1 ? " and id_sucursal='" + id_suc + "' " : " ");
        return this.consulta("SELECT antena_acoplada as id_antena_acoplada, antena_acoplada from vta_antena where  lower(antena_acoplada) like '" + txt.toLowerCase() + "%' "
                + " union all  "
                + " select id_spliter::text as id_antena_acoplada ,nombre_spliter as antena_acoplada  from vta_spliter where tipo_spliter in ('u','s') " + id_sucursal + " and lower(nombre_spliter) like '%" + txt.toLowerCase() + "%' "
                + " limit 10 offset 0");
    }

    public ResultSet GetCordenadasEquipos(String id_sector, String id_parroquia) {
        return this.consulta("select distinct ids,nodo,latitud_gps,longitud_gps,numero_puertos,puertos_utilizados from vta_nodos_antenas_splitter where trim(latitud_gps)<>'' and trim(longitud_gps)<>'' and tipo='s' and identifica='osu' and id_sector='" + id_sector + "' and id_parroquia='" + id_parroquia + "';");
    }

    public ResultSet GetPuertoUsados(String id_spliter) {
        return this.consulta("select * from tbl_spliter_utilizado where id_spliter ='" + id_spliter + "' and estado_puerto=true");
    }

    public ResultSet GetPuertoUsadosSplit(String id_spliter) {
        return this.consulta("select string_agg(puerto::text, ',') from tbl_spliter_utilizado where id_spliter ='" + id_spliter + "' and estado_puerto=true");
    }

    public String[] GetPuertoInstalacion(String id_instalacion) {
        String puertos[] = {"", "", "", "", "", "", "", "", ""};
        try {
            ResultSet rs = this.consulta("select distinct x.id_spliter,x.puerto,x.id_spliter_utilizado,y.nombre_spliter,y.numero_puertos,y.latitud_gps,y.longitud_gps,y.direccion  "
                    + " from tbl_spliter_utilizado as x "
                    + " right join tbl_spliter as y on y.id_spliter=x.id_spliter  "
                    + " where x.id_instalacion ='" + id_instalacion + "' and estado_puerto=true");
            if (rs.next()) {
                puertos[0] = (rs.getString(1) != null ? rs.getString(1) : "");
                puertos[1] = (rs.getString(2) != null ? rs.getString(2) : "");
                puertos[2] = (rs.getString(3) != null ? rs.getString(3) : "");
                puertos[3] = (rs.getString(4) != null ? rs.getString(4) : "");
                puertos[4] = (rs.getString(5) != null ? rs.getString(5) : "");
                puertos[5] = (rs.getString(6) != null ? rs.getString(6) : "");
                puertos[6] = (rs.getString(7) != null ? rs.getString(7) : "");
                puertos[7] = (rs.getString(8) != null ? rs.getString(8) : "");
                rs.close();
            }
        } catch (SQLException ex) {
            System.out.println("" + ex.getLocalizedMessage() + " " + ex.getMessage());
        }
        return puertos;
    }

    public boolean setActualizarPuerto(String id_spliter, String puerto, String id) {

        return this.ejecutar("update tbl_spliter_utilizado set puerto='" + puerto + "' where id_spliter_utilizado='" + id + "';");
    }

    public String setNuevoPuerto(String id_spliter, String puerto, String id) {
        return this.insert("INSERT INTO tbl_spliter_utilizado( id_spliter, puerto , id_instalacion)VALUES( '" + id_spliter.replaceAll("osu", "") + "', '" + puerto + "',  '" + id + "');");
    }

    public String GetPuertosUsadosSplit(String id_spliter) {
        String resultado = "";
        ResultSet rs = this.consulta("select string_agg((puerto::text||';'||txt_estado_servicio), ',') from vta_spliter_puertos where id_spliter='" + id_spliter + "';");
        try {
            if (rs.next()) {
                resultado = (rs.getString(1) != null ? rs.getString(1) : "");
            }
        } catch (Exception e) {
            System.out.println("" + e.getMessage() + " " + e.getLocalizedMessage());
        }
        return resultado;
    }

    public ResultSet GetPuertosUsadosSplitJson(String id_spliter) {
        return this.consulta("select puerto,txt_estado_servicio from vta_spliter_puertos where id_spliter='" + id_spliter + "';");
    }

    public ResultSet GetPuertosUsadosSplitOlt(String id_spliter) {
        return this.consulta("select * from vta_spliter_puertos where id_spliter='" + id_spliter + "';");
    }

    public int getActivoUsado(String codigo_activo, String id_spliter) {
        int contador = 0;
        ResultSet rs = this.consulta(" select count(*) as contador from tbl_spliter where lower(codigo_activo)='" + codigo_activo.toLowerCase() + "'  and id_spliter <>'" + id_spliter + "';");
        try {
            if (rs.next()) {
                contador = (rs.getString(1) != null ? rs.getInt(1) : 0);
            }
        } catch (Exception e) {
            System.out.println("" + e.getMessage() + " " + e.getLocalizedMessage());
        }
        return contador;
    }

}
