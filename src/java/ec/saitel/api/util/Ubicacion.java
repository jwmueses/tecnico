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

/**
 *
 * @author Jorge
 */
public class Ubicacion extends DataBase {

    public Ubicacion(String m, int p, String db, String u, String c) {
        super(m, p, db, u, c);
    }

    public ResultSet getUbicacion(String padre) {
        return this.consulta("SELECT * FROM tbl_ubicacion WHERE id_ubicacion=" + padre + ";");
    }

    public ResultSet getUbicaciones() {
        return this.consulta("SELECT id_ubicacion,ubicacion FROM tbl_ubicacion WHERE id_padre > 0 order by ubicacion;");
    }

    public ResultSet getUbicaciones(String padre) {
        return this.consulta("SELECT id_ubicacion,ubicacion FROM tbl_ubicacion WHERE id_padre=" + padre + " order by ubicacion;");
    }
    
    public ResultSet getProvincias(String idProvincias) {
        return this.consulta("SELECT id_provincia,provincia FROM vta_provincia WHERE id_provincia in(" + idProvincias + ") order by provincia;");
    }
    
    public ResultSet getProvinciasSucursal(String idSucursal) {
        return this.consulta("SELECT id_provincia,provincia FROM vta_provincia WHERE id_provincia in(select regexp_split_to_table(id_provincias, ',')::int8 from tbl_sucursal where id_sucursal=" + idSucursal + ");");
    }

    public boolean estaDuplicado(String id_padre, String nombre) {
        ResultSet res = this.consulta("SELECT * FROM tbl_ubicacion where lower(nombre)='" + nombre.toLowerCase() + "' and id_padre=" + id_padre);
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

    public String getSecuancia(String padre) {
        String num = "";
        try {
            ResultSet res = this.consulta("select max(id_ubicacion)+1 from tbl_ubicacion where id_padre=" + padre);
            if (res.next()) {
                num = (res.getString(1) != null) ? res.getString(1) : "";
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (num.compareTo("") == 0) {
            num = padre + "01";
        }
        return num;
    }

    public boolean insertar(String id_ubicacion, String id_padre, String nombre) {
        return this.ejecutar("INSERT INTO tbl_ubicacion(id_ubicacion,id_padre,ubicacion) VALUES(" + id_ubicacion + ", " + id_padre + ", '" + nombre.toUpperCase() + "');");
    }

    public boolean actualizar(String id, String id_padre, String nombre) {
        return this.ejecutar("update tbl_ubicacion set id_padre=" + id_padre + ", ubicacion='" + nombre.toUpperCase() + "' where id_ubicacion=" + id + ";");
    }

    public String getNombre(String id_ubicacion) {
        String nombre = "";
        try {
            ResultSet res = this.consulta("select ubicacion from tbl_ubicacion where id_ubicacion=" + id_ubicacion);
            if (res.next()) {
                nombre = (res.getString(1) != null) ? res.getString(1) : "";
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nombre;
    }

    public ResultSet getUbicacionSucursal(String id_sucursal) {
        return this.consulta("select u.id_ubicacion,u.ubicacion from tbl_ubicacion_sucursal as us "
                + " inner join tbl_ubicacion as u on u.id_ubicacion =us.id_ubicacion "
                + " where us.id_sucursal ='" + id_sucursal + "' and us.anulado =false and us.eliminado =false;");
    }
}
