/**
 * @version 1.0
 * @package FACTURAPYMES.
 * @author Jorge Washington Mueses Cevallos.
 * @copyright Copyright (C) 2010 por Jorge Mueses. Todos los derechos
 * reservados.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL. FACTURAPYMES! es un
 * software de libre distribuciÃ³n, que puede ser copiado y distribuido bajo los
 * tÃ©rminos de la Licencia PÃºblica General GNU, de acuerdo con la publicada
 * por la Free Software Foundation, versiÃ³n 2 de la licencia o cualquier
 * versiÃ³n posterior.
 */
package ec.saitel.api.util;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jorge
 */
public class Empleado extends DataBase {

    public Empleado(String m, int p, String db, String u, String c) {
        super(m, p, db, u, c);
    }

    public ResultSet getEmpleado(int id) {
        return this.consulta("SELECT * FROM vta_empleado where id_empleado=" + id);
    }
    
    public ResultSet getEmpleado(String alias) {
        return this.consulta("SELECT * FROM vta_empleado where alias='" + alias + "'");
    }

    public ResultSet getEmpleadoNombre(String nombre) {
        return this.consulta("SELECT * FROM vta_empleado where UPPER(empleado) like UPPER('%" + nombre + "%') limit 5;");
    }

    public ResultSet getCarnets(String id) {
        return this.consulta("select id_empleado, apellido, nombre, cargo, dni, tipo_sangre, carnet, foto, padre_nombre, carnet_vigencia_ini, carnet_vigencia_fin from vta_empleado where id_empleado in (" + id + ");");
    }
    
    public ResultSet getCumpleaneros() {
        return this.consulta("select id_empleado, alias, S.sucursal, nombre, apellido, date_part('day', fecha_nac)::int as dia, date_part('month', fecha_nac)::int as mes, mensaje \n" +
            "from tbl_empleado as E inner join tbl_sucursal as S on E.id_sucursal = S.id_sucursal \n" +
            "where e.estado and not E.eliminado and generar_rol \n" +
            "	and ( date_part('year', now() ) || '-' || date_part('month', fecha_nac) || '-' || date_part('day', fecha_nac) )::date between ( now()::date - '15 days'::interval)::date and now()::date \n" +
            "order by date_part('month', fecha_nac)::int desc, date_part('day', fecha_nac)::int desc;");
    }

    public ResultSet getFamiliar(String id) {
        return this.consulta("SELECT * FROM tbl_familia f, tbl_parentesco p where f.id_parentesco=p.id_parentesco and id_empleado=" + id + ";");
    }

    public ResultSet getInstruccion(String id) {
        return this.consulta("SELECT * FROM tab_formaciones where id_empleado=" + id + ";");
    }

    public String getId(String dni) {
        String id_empleado = "";
        try {
            ResultSet r = this.consulta("SELECT * FROM tbl_empleado where dni='" + dni + "'");
            if (r.next()) {
                id_empleado = (r.getString("id_empleado") != null) ? r.getString("id_empleado") : "";
                r.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id_empleado;
    }

    public ResultSet getEmpleados() {
        return this.consulta("SELECT id_empleado,empleado FROM vta_empleado where estado=true order by empleado;");
    }

    public ResultSet getEmpleadosATS() {
        return this.consulta("select id_empleado,nombre ||' '|| apellido,dni from tbl_empleado where estado=true and eliminado=false");
    }

    public ResultSet getVendedoresFreeLance() {
        return this.consulta("SELECT id_empleado,empleado FROM vta_empleado where generar_rol=false and lower(cargo) like '%marketing%' order by empleado;");
    }

    public ResultSet getCajero(String cajero) {
        return this.consulta("SELECT E.nombre || ' ' || E.apellido as empleado, S.sucursal FROM tbl_empleado as E inner join tbl_sucursal as S on E.id_sucursal=S.id_sucursal where E.alias='" + cajero + "';");
    }

    public ResultSet getEmpleadosResponsables() {
        return this.consulta("SELECT id_empleado,empleado FROM vta_empleado where id_empleado in (select id_responsable from vta_bodega) order by empleado;");
    }

    public ResultSet getEmpleados(int id_sucursal) {
        return this.consulta("SELECT id_empleado,empleado FROM vta_empleado where estado=true and id_sucursal=" + id_sucursal + " order by empleado;");
    }

    public ResultSet getEmpleados(String id_sucursal, String id_instalacion) {
        return this.consulta("SELECT id_empleado,empleado FROM vta_empleado where id_sucursal=" + id_sucursal + " order by empleado");
    }

    public ResultSet getEmpleados(int idSucursal, String where) {
        return this.consulta("SELECT distinct id_empleado, id_sucursal, dni, empleado, alias, num_horas_devolver, num_horas_devolver_devueltas, monto_horas_devolver, monto_horas_devolver_devueltas FROM vta_empleado WHERE "
                + (where.compareTo("") != 0 ? where + " and " : "") + " estado=true and eliminado=false "
                + (idSucursal > 0 ? " and id_sucursal=" + idSucursal : "")
                + " order by empleado;");
    }

    public ResultSet getEmpleadosIncluirRubrosRoles(int idSucursal, String where) {
        return this.consulta("SELECT distinct id_empleado, id_sucursal, dni, apellido || ' ' || nombre as empleado, alias, num_horas_devolver, num_horas_devolver_devueltas FROM vta_empleado WHERE "
                + (where.compareTo("") != 0 ? where + " and " : "") + " estado=true and eliminado=false "
                + (idSucursal > 0 ? " and id_sucursal=" + idSucursal : "")
                + " order by empleado;");
    }

    public ResultSet getEmpleadosBodega(int id_sucursal) {
        //return this.consulta("SELECT id_empleado,empleado FROM vta_empleado where id_sucursal="+id_sucursal+" and id_empleado in (select id_responsable from tbl_bodega where estado=true) order by empleado;");
        return this.consulta("SELECT id_empleado, nombre || ' ' || apellido as empleado, placa_vehiculo \n"
                + "FROM tbl_empleado \n"
                + "where id_sucursal=" + id_sucursal + " \n"
                + "and id_empleado in (select id_responsable from tbl_bodega where estado=true and id_responsable not in(select id_tecnico_resp from tbl_hoja_ruta where estado_hoja not in ('9') and id_tecnico_resp is not null )) "
                + "and id_rol in(select id_rol from tbl_rol where lower(rol) like '%tecnico%' ) "
                + "and estado=true order by nombre || ' ' || apellido;");
    }

    public ResultSet getEmpleadosBodegaContratado(int id_sucursal) {
        //return this.consulta("SELECT id_empleado,empleado FROM vta_empleado where id_sucursal="+id_sucursal+" and id_empleado in (select id_responsable from tbl_bodega where estado=true) order by empleado;");
        return this.consulta("SELECT id_empleado, nombre || ' ' || apellido as empleado, placa_vehiculo \n"
                + "FROM tbl_empleado \n"
                + "where id_sucursal=" + id_sucursal + " \n"
                + "and id_departamento in( select id_area::text  from tbl_area where lower(area) like '%externo%') "
                + "and estado=true order by nombre || ' ' || apellido;");
    }

    public ResultSet getEmpleadoBodega(String id_tecnico_resp) {
        //return this.consulta("SELECT id_empleado,empleado FROM vta_empleado where id_sucursal="+id_sucursal+" and id_empleado in (select id_responsable from tbl_bodega where estado=true) order by empleado;");
        return this.consulta("SELECT id_empleado, nombre || ' ' || apellido as empleado, placa_vehiculo \n"
                + "FROM tbl_empleado \n"
                + "where id_empleado=" + id_tecnico_resp);
    }

    public ResultSet getEmpleadosBodegaTorre(int id_sucursal) {
        //return this.consulta("SELECT id_empleado,empleado FROM vta_empleado where id_sucursal="+id_sucursal+" and id_empleado in (select id_responsable from tbl_bodega where estado=true) order by empleado;");
        return this.consulta("SELECT id_empleado,empleado FROM vta_empleado where id_sucursal=" + id_sucursal + " and estado=true order by empleado;");
    }

    public ResultSet getEmpleadosBodegaNoDiponibles(int id_sucursal) {
        //return this.consulta("SELECT id_empleado,empleado FROM vta_empleado where id_sucursal="+id_sucursal+" and id_empleado in (select id_responsable from tbl_bodega where estado=true) order by empleado;");
        return this.consulta("SELECT id_empleado,empleado \n"
                + "FROM vta_empleado \n"
                + "where id_sucursal=" + id_sucursal + " \n"
                + "and id_empleado in (select id_responsable from tbl_bodega where estado=true and id_responsable in(select id_tecnico_resp from tbl_hoja_ruta where estado_hoja not in ('9'))) \n"
                + "and id_rol in(select id_rol from tbl_rol where lower(rol) like '%tecnico%' ) "
                + "and estado=true order by empleado;");
    }

    public ResultSet getEmpleadosPersonalizacion(String txt, String tipoMov) {
        String clientes = "";
        if (tipoMov.compareTo("3") == 0 || tipoMov.compareTo("6") == 0 || tipoMov.compareTo("7") == 0) {
            //clientes = " union select ruc as dni, razon_social as empleado from tbl_cliente where id_cliente in(select id_cliente from tbl_instalacion where anulado=false and estado_servicio not in('t','e','r'))  and (lower(ruc) like '"+txt+"%' or lower(razon_social) like '%"+txt+"%')";
            clientes = " union select ruc as dni, razon_social as empleado from tbl_cliente where id_cliente in(select id_cliente from tbl_instalacion where lower(ruc) like '" + txt + "%' or lower(razon_social) like '%" + txt + "%')";
        }
        return this.consulta("SELECT dni,empleado FROM vta_empleado where lower(dni) like '" + txt + "%' or lower(empleado) like '%" + txt + "%' " + clientes + " order by empleado limit 10 offset 0");
    }

    public ResultSet getEmpleadosDepartamentos(int id_sucursal) {
        return this.consulta("SELECT id_empleado,empleado, '/ ' || departamento FROM vta_empleado where id_sucursal=" + id_sucursal + " and estado=true order by departamento,empleado");
    }

    public ResultSet getareas(String estado) {
        return this.consulta("SELECT id_area, area from tbl_area where eliminado=" + estado + " order by area");
    }

    public ResultSet getCargos(String estado) {
        return this.consulta("SELECT id_cargo, cargo from tbl_cargo where eliminado=" + estado + " order by cargo");
    }

    public ResultSet getPlanificadores() {
        return this.consulta("SELECT distinct id_empleado, nombre, apellido from vta_empleado_planificacion where order by nombre, apellido");
    }

    public ResultSet getCargosId(String id_area) {
        return this.consulta("SELECT id_cargo, cargo from tbl_cargo where eliminado=false and id_area=" + id_area + " order by cargo");
    }

    public ResultSet getResponsableCajaChica(int id_sucursal) {
        return this.consulta("SELECT E.* FROM vta_empleado as E inner join tbl_sucursal as S on S.id_responsable_caja_chica=E.id_empleado where S.id_sucursal=" + id_sucursal + " and E.estado=true");
    }

    public ResultSet getEmpleadosActivos() {
        return this.consulta("SELECT dni,empleado FROM vta_empleado WHERE estado=true order by empleado");
    }

    public ResultSet getEmpleadosActivos(int sucursal) {
        return this.consulta("SELECT dni,empleado FROM vta_empleado WHERE estado=true " + (sucursal <= 0 ? " " : " and id_sucursal=" + sucursal + " ") + " order by empleado");
    }

    public ResultSet getEmpleadosDepartamento(int idDepartamento) {
        return this.consulta("SELECT id_empleado,empleado FROM vta_empleado WHERE estado=true and id_departamento='" + idDepartamento + "' order by empleado");
    }

    public ResultSet getEmpleados(int anio, int mes) {
        String fin = anio + "-" + mes + "-" + Fecha.getUltimoDiaMes(anio, mes);
        return this.consulta("SELECT id_empleado,empleado FROM vta_empleado where id_empleado not in (select distinct id_empleado from tbl_rol_pago_detalle where periodo='" + fin + "');");
    }

    public ResultSet getEmpleadosTareas(int idSucursal) {
        return this.consulta("SELECT distinct id_tecnico_resp, nombre || ' ' || apellido as empleado, E.estado FROM tbl_empleado as E inner join tbl_hoja_ruta as HR on E.id_empleado=HR.id_tecnico_resp \n" +
                "where " + (idSucursal >= 1 ? "HR.id_sucursal="+idSucursal : "") + " and id_rol in(select id_rol from tbl_rol where lower(rol) like '%tecnico%' ) order by E.estado desc, nombre || ' ' || apellido");
    }

    public String getNombre(long id) {
        String empleado = "";
        try {
            ResultSet r = this.consulta("SELECT nombre || ' ' || apellido as empleado FROM tbl_empleado where id_empleado=" + id + ";");
            if (r.next()) {
                empleado = (r.getString("empleado") != null) ? r.getString("empleado") : "";
                r.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return empleado;
    }

    public String getMail(String alias) {
        String email = "sistemas@saitel.ec";
        try {
            ResultSet r = this.consulta("SELECT email FROM tbl_empleado where alias='" + alias + "';");
            if (r.next()) {
                email = (r.getString("email") != null) ? r.getString("email") : "sistemas@saitel.ec";
                r.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return email;
    }

    public int getIdEmpleado(String alias) {
        int id = -1;
        try {
            ResultSet r = this.consulta("SELECT id_empleado FROM tbl_empleado where alias='" + alias + "'");
            if (r.next()) {
                id = (r.getString("id_empleado") != null) ? r.getInt("id_empleado") : -1;
                r.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    public int getIdSucursal(String alias) {
        int id = -1;
        try {
            ResultSet r = this.consulta("SELECT id_sucursal FROM tbl_empleado where alias='" + alias + "'");
            if (r.next()) {
                id = (r.getString("id_sucursal") != null) ? r.getInt("id_sucursal") : -1;
                r.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    public int getIdEmpleado(String bodega, String parm) {
        int id = -1;
        String ruc = "";
        try {
            ResultSet r = this.consulta("select b.id_bodega,e.id_empleado,c.id_cliente,c.ruc from tbl_bodega b left join tbl_empleado as e on e.id_empleado=b.id_responsable\n"
                    + "left join tbl_cliente as c on c.id_cliente=b.id_responsable where b.id_bodega='" + bodega + "'");
            if (r.next()) {
                id = (r.getString("id_empleado") != null) ? r.getInt("id_empleado") : -1;
                if (id == -1) {
                    id = (r.getString("id_cliente") != null) ? r.getInt("id_cliente") : -1;
                    ruc = (r.getString("ruc") != null) ? r.getString("ruc") : "";
                    ruc = (ruc.length() <= 10) ? ruc : ruc.substring(0, ruc.length() - 3);
                    String temruc = this.getId(ruc);
                    if (temruc.compareTo("") != 0) {
                        id = Integer.parseInt(temruc);
                    }
                }
                r.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    public String getNombre(String dni) {
        String empleado = "";
        try {
            ResultSet r = this.consulta("SELECT nombre || ' ' || apellido as empleado FROM tbl_empleado where dni='" + dni + "'");
            if (r.next()) {
                empleado = (r.getString("empleado") != null) ? r.getString("empleado") : "";
                r.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return empleado;
    }

    public String getApellidoNombres(String alias) {
        String empleado = "Empleado";
        try {
            ResultSet r = this.consulta("SELECT apellido || ' ' || nombre as empleado FROM tbl_empleado where alias='" + alias + "'");
            if (r.next()) {
                empleado = (r.getString("empleado") != null) ? r.getString("empleado") : "Empleado";
                r.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return empleado;
    }

    public String getNombres(String alias) {
        String empleado = "Empleado";
        try {
            ResultSet r = this.consulta("SELECT nombre || ' ' || apellido as empleado FROM tbl_empleado where alias='" + alias + "'");
            if (r.next()) {
                empleado = (r.getString("empleado") != null) ? r.getString("empleado") : "Empleado";
                r.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return empleado;
    }

    public String getId(String alias, String parm1) {
        String id_empleado = "";
        try {
            ResultSet r = this.consulta("SELECT * FROM tbl_empleado where alias='" + alias + "'");
            if (r.next()) {
                id_empleado = (r.getString("id_empleado") != null) ? r.getString("id_empleado") : "";
                r.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id_empleado;
    }

    public int getDepartamento(String alias) {
        int num = 0;
        try {
            ResultSet rs = this.consulta("select id_departamento from tbl_empleado where alias='" + alias + "'");
            if (rs.next()) {
                num = (rs.getString("id_departamento") != null) ? rs.getInt("id_departamento") : 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    public String getDatosEmpleado(String alias, String campo) {
        String empleado = "Empleado";
        try {
            ResultSet r = this.consulta("SELECT " + campo + " as campo FROM vta_empleado where alias='" + alias + "'");
            if (r.next()) {
                empleado = (r.getString("campo") != null) ? r.getString("campo") : "Empleado";
                r.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return empleado;
    }

    public int getDatoEmpleado(String alias, String campo) {
        int empleado = -1;
        try {
            ResultSet r = this.consulta("SELECT " + campo + " as campo FROM vta_empleado where alias='" + alias + "'");
            if (r.next()) {
                empleado = (r.getString("campo") != null) ? r.getInt("campo") : -1;
                r.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return empleado;
    }

    public String getDatosEmpleado(String campo, String alias, String pam1) {
        String empleado = "Empleado";
        try {
            ResultSet r = this.consulta("SELECT " + campo + " as campo FROM vta_empleado where id_empleado='" + alias + "'");
            if (r.next()) {
                empleado = (r.getString("campo") != null) ? r.getString("campo") : "Empleado";
                r.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return empleado;
    }

    /*public String getEmpleadoJSON()
     {
     ResultSet rs = this.consulta("SELECT id_empleado,ruc,serie_factura,aut_factura,toDateSQL(fecha_cad_factura),direccion,ciudad,telefono,email,contacto " +
     "FROM vta_empleado order by id_empleado;");
     String tbl = this.getJSON(rs);
     try{
     rs.close();
     }catch(Exception e){
     e.printStackTrace();
     }
     return tbl;
     }*/
    public boolean estaDuplicado(String id, String dni) {
        ResultSet res = this.consulta("SELECT * FROM tbl_empleado where dni='" + dni + "' and id_empleado<>" + id + ";");
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

    public double getValorSueldo(int id_empleado) {
        double sueldo = 0;
        try {
            ResultSet r = this.consulta("select S.sueldo from tbl_escala_salarial as S inner join tbl_empleado as E on E.id_cargo = S.id_cargo \n"
                    + "where vigencia_hasta is null and id_empleado=" + id_empleado);
            if (r.next()) {
                sueldo = (r.getString("sueldo") != null) ? r.getDouble("sueldo") : 0;
                r.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sueldo;
    }

    /**
     * Guarda la informacion de la familia.
     *
     * @param id_empleado Clave primaria del empleado.
     * @param nombre Nombres del Familiar.
     * @param direccion Direccion.
     * @param telefono Telefono.
     * @param f_nacimiento Fecha de Nacimiento.
     * @param carnet_conadis Carnet del Conadis.
     * @param discapacidad Discapacidad.
     * @param vive El Familiar vive?
     * @param id_parentesco Clave primaria (id_parentesco) de la tabla
     * (tbl_parentesco).
     * @return (true) Si se guardo correctamente (false) Si tuvo algun error.
     */
    public boolean insertarfamilia(String id_empleado, String nombre, String direccion, String telefono, String f_nacimiento, String carnet_conadis, String discapacidad, String vive, String id_parentesco) {
        f_nacimiento = f_nacimiento.compareTo("") != 0 ? "'" + f_nacimiento + "'" : "NULL";
        return this.ejecutar("INSERT INTO tbl_familia(id_empleado, nombre, direccion, telefono, fecha_nacimiento, carnet_conadis, discapacidad, vive, id_parentesco)"
                + "VALUES(" + id_empleado + ",'" + nombre.toUpperCase() + "','" + direccion.toUpperCase() + "', '" + telefono + "', " + f_nacimiento + ", '" + carnet_conadis + "', '" + discapacidad + "', '" + vive + "', " + id_parentesco + ");");
    }

    public boolean insertarfamilia(String id_empleado, String nombre, String direccion, String telefono, String f_nacimiento, String carnet_conadis, String discapacidad, String vive, String id_parentesco, String carga_familiares) {
        f_nacimiento = f_nacimiento.compareTo("") != 0 ? "'" + f_nacimiento + "'" : "NULL";
        return this.ejecutar("INSERT INTO tbl_familia(id_empleado, nombre, direccion, telefono, fecha_nacimiento, carnet_conadis, discapacidad, vive, id_parentesco,carga_familiar)"
                + "VALUES(" + id_empleado + ",'" + nombre.toUpperCase() + "','" + direccion.toUpperCase() + "', '" + telefono + "', " + f_nacimiento + ", '" + carnet_conadis + "', '" + discapacidad + "', '" + vive + "', " + id_parentesco + ",'" + carga_familiares + "');");
    }

    /**
     * Actualizar La informacion del familiar.
     *
     * @param id_familia Clave primaria (id_familia) de la tabla (tbl_familia).
     * @param nombre Nombre.
     * @param direccion Direccion.
     * @param telefono Telefono.
     * @param f_nacimiento Fecha de Nacimiento.
     * @param carnet_conadis Carnet del Conadis.
     * @param discapacidad Discapacidad.
     * @param vive El familiar Vive?
     * @param id_parentesco Clave primaria (id_parentesco) de la tabla
     * (tbl_parentesco).
     * @return (true) Si se guardo correctamente (false) Si tuvo algun error.
     */
    public boolean actualizarfamilia(String id_familia, String nombre, String direccion, String telefono, String f_nacimiento, String carnet_conadis, String discapacidad, String vive, String id_parentesco) {
        f_nacimiento = f_nacimiento.compareTo("") != 0 ? "'" + f_nacimiento + "'" : "NULL";
        return this.ejecutar("UPDATE tbl_familia SET nombre='" + nombre.toUpperCase() + "', direccion='" + direccion.toUpperCase() + "', telefono='" + telefono + "', fecha_nacimiento=" + f_nacimiento + ", carnet_conadis='" + carnet_conadis + "', discapacidad='" + discapacidad
                + "', vive='" + vive + "', id_parentesco=" + id_parentesco + "  WHERE id_familia=" + id_familia + ";");
    }

    public boolean actualizarfamilia(String id_familia, String nombre, String direccion, String telefono, String f_nacimiento, String carnet_conadis, String discapacidad, String vive, String id_parentesco, String carga_familiares) {
        f_nacimiento = f_nacimiento.compareTo("") != 0 ? "'" + f_nacimiento + "'" : "NULL";
        return this.ejecutar("UPDATE tbl_familia SET nombre='" + nombre.toUpperCase() + "', direccion='" + direccion.toUpperCase() + "', telefono='" + telefono + "', fecha_nacimiento=" + f_nacimiento + ", carnet_conadis='" + carnet_conadis + "', discapacidad='" + discapacidad
                + "', vive='" + vive + "', id_parentesco=" + id_parentesco + ", carga_familiar=" + carga_familiares + "  WHERE id_familia=" + id_familia + ";");
    }

    /**
     * Guarda informacion de la formacion del empleado.
     *
     * @param id_empleado Clave primaria del empleado.
     * @param nombre_institucion Instruccion realizada.
     * @param titulo Titulo obtenido.
     * @param instruccion Clave primaria de la instruccion.
     * @param fecha Fecha que lo realizo.
     * @return (true) Si se guardo correctamente (false) Si tuvo algun error.
     */
    public boolean insertarformacion(String id_empleado, String nombre_institucion, String titulo, String instruccion, String fecha) {
        fecha = fecha.compareTo("") != 0 ? "'" + fecha + "'" : "NULL";
        return this.ejecutar("INSERT INTO tab_formaciones(id_empleado, nombreinstitucion, titulo, instruccion, fecha)"
                + "VALUES(" + id_empleado + ",'" + nombre_institucion.toUpperCase() + "','" + titulo.toUpperCase() + "', '" + instruccion.toUpperCase() + "', " + fecha + ");");
    }

    /**
     * Actualizar la informacion de la formacion del empleado.
     *
     * @param id_formacion Clave primaria (id_formacion) de la tabla
     * (tab_formaciones)
     * @param nombre_institucion Nombre de instruccion.
     * @param titulo Titulo obtenido.
     * @param instruccion Clave primaria de la instruccion.
     * @param fecha Fecha que lo realizo.
     * @return (true) Si se guardo correctamente (false) Si tuvo algun error.
     */
    public boolean actualizarformacion(String id_formacion, String nombre_institucion, String titulo, String instruccion, String fecha) {
        fecha = fecha.compareTo("") != 0 ? "'" + fecha + "'" : "NULL";
        return this.ejecutar("UPDATE tab_formaciones SET nombreinstitucion='" + nombre_institucion.toUpperCase() + "', titulo='" + titulo.toUpperCase() + "', instruccion='" + instruccion.toUpperCase() + "', fecha=" + fecha + " WHERE id_formacion=" + id_formacion + ";");
    }

    /**
     * Elimina los datos de un familiar.
     *
     * @param id_empleado_familia Clave primaria (id_familia) de la tabla
     * (tbl_familia).
     * @return (true) Si se elimino Correctamente (false) Si tuvo algun error.
     */
    public boolean eliminarfamilia(String id_empleado_familia) {
        return this.ejecutar("Delete from tbl_familia where id_familia=" + id_empleado_familia + ";");
    }

    /**
     * Guarda los ids de la induccion recibida.
     *
     * @param id_empleado Clave primaria (id_empleado) de la tabla
     * (tbl_empleado).
     * @param texto ids segun la induccion recibida.
     * @return (true) Si se guardo Correctamente (false) Si tuvo algun error.
     */
    public boolean guardarCarnet(String id_empleado, String texto, String carnet_vigencia_ini, String carnet_vigencia_fin) {
        return this.ejecutar("Update tbl_empleado set carnet='" + texto + "', carnet_vigencia_ini='" + carnet_vigencia_ini + "', carnet_vigencia_fin='" + carnet_vigencia_fin + "' where id_empleado=" + id_empleado + ";");
    }

    /**
     * Para eliminar la formacion.
     *
     * @param id_formacion_empleado Clave primaria (id_formacion) de la tabla
     * (tab_formaciones).
     * @return (true) Si se elimino Correctamente (false) Si tuvo algun error.
     */
    public boolean eliminarformacion(String id_formacion_empleado) {
        return this.ejecutar("Delete from tab_formaciones where id_formacion=" + id_formacion_empleado + ";");
    }

    /**
     * Guarda un nuevo cargo del empleado.
     *
     * @param id_empleado Clave primaria (id_empleado).
     * @param id_cargo Clave primaria del cargo.
     * @param usuario usuario del Cargo.
     * @param fecha_ingreso Fecha en que inicio el cargo.
     * @return (true) Si se Guardo Correctamente (false) Si tuvo algun error.
     */
    public boolean nuevoCargo(String id_empleado, String id_cargo, String usuario, String fecha_ingreso) {
        return this.ejecutar("insert into tbl_cargo_empleado (id_empleado, id_cargo, usuario, fecha_ingreso) values(" + id_empleado + "," + id_cargo + ",'" + usuario + "','" + fecha_ingreso + "');");
    }

    /**
     * Guarda un nuevo cargo del empleado.
     *
     * @param id_empleado Clave primaria (id_empleado).
     * @param id_cargo Clave primaria del cargo.
     * @param usuario usuario del Cargo.
     * @param fecha_ingreso Fecha en que inicio el cargo.
     * @return (true) Si se Guardo Correctamente (false) Si tuvo algun error.
     */
    /*public boolean setRubrosPermanentes(String id_empleado, String id_sucursal)
    {
        return this.ejecutar("insert into tbl_rubro_cont_per(id_rubro_cont, id_empleado, monto, id_sucursal) select id_rubro_cont, "+id_empleado+", valor, " + id_sucursal + 
                " from tbl_rubro_cont where tipo=false");
    }*/
    /**
     * Actualizar informacion del cargo
     *
     * @param id_empleado Clave primaria (id_empleado) de la tabla
     * (tbl_empleado).
     * @param usuario usuario del cargo.
     * @param id_cargo Clave primaria del cargo.
     * @return (true) Si se Actualizo Correctamente (false) Si tuvo algun error.
     */
    public boolean actualizarCargo(String id_empleado, String usuario, String id_cargo) {
        List ejec = new ArrayList();
        ejec.add("update tbl_cargo_empleado set fecha_salida=date 'now()' where id_cargo_empleado=(select max(id_cargo_empleado) from tbl_cargo_empleado where id_empleado=" + id_empleado + ");");
        ejec.add("insert into tbl_cargo_empleado (id_empleado, id_cargo, usuario, fecha_ingreso) values(" + id_empleado + "," + id_cargo + ",'" + usuario + "',date 'now');");
        return this.transacciones(ejec);
    }

    public boolean personalizacionesPendientes(String usuario) {
        try {
            ResultSet rs = this.consulta("select P.* from tbl_activo_personalizacion as P inner join tbl_empleado as E on P.dni_recibe=E.dni "
                    + "where E.alias='" + usuario + "' and P.aceptada=false and anulado=false and P.rechazo_aceptado "
                    + "union "
                    + "select P.* from tbl_activo_personalizacion as P inner join tbl_empleado as E on P.dni_entrega =E.dni "
                    + "where E.alias='" + usuario + "' and P.rechazo_aceptado=false and anulado=false");
            if (this.getFilas(rs) > 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean ordenConsumoPendientes(int idEmpleado) {
        try {
            ResultSet rs = this.consulta("select * from tbl_ordenconsumo where id_empleador='" + idEmpleado + "' and estado_recibido='1' and not eliminado;");
            if (this.getFilas(rs) > 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /*Memos*/
    public int get_numMemo() {
        int num = 31;
        try {
            ResultSet rs = this.consulta("select num_memo from tbl_empleado_memo order by num_memo desc, anio_memo desc");
            if (rs.next()) {
                num = (rs.getString("num_memo") != null) ? rs.getInt("num_memo") : 31;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (num + 1);
    }

    public ResultSet getMemos(String id) {
        return this.consulta("SELECT * FROM vta_empleado_memo where id_empleado_memo=" + id + ";");
    }

    public ResultSet getMemosWhere(String where) {
        return this.consulta("SELECT * FROM vta_empleado_memo " + where + ";");
    }

    ///////////////
    public ResultSet getEmpleados(String wh) {
        return this.consulta("SELECT id_empleado,empleado FROM vta_empleado " + wh + " order by empleado;");
    }
    
    public ResultSet getEmpleadosPorAlias(String wh) {
        return this.consulta("SELECT alias, empleado FROM vta_empleado " + wh + " order by empleado;");
    }

    public String insertar(String id_sucursal, String alias, String tipo_ident, String dni, String nombre, String apellido, String sexo, String fecha_nac, String estado_civil,
            String calle, String numero, String id_provincia, String id_ciudad, String id_parroquia, String sector, String telefono, String movil, String movil_movistar, String email, String nacionalidad,
            String ac_no, String cedula_militar, String licencia_tipo, String placa_vehiculo, String lugar_nacimiento, String tipo_sangre, String carne_conadis, String discapacidad, String enfermedades,
            String fecha_ingreso, String banco, String cuenta, String estado, String id_rol, String id_cargo, String id_departamento, String carnet_iess,
            String generar_rol, String periodo_14_sueldo, String cobra_14_mensual, String cobra_f_r, String id_horario, String fecha_ingreo_trabajo, String cobra_13_mensual, String porcentaje, String tipo_cuenta,
            String tipo_transferencia, String numero_semana, String fecha_salida, String referencias, String etnia, String calle1, String emailpersonal, String barrio, String instrucciones, String dis_porcentaje,
            String distribucionGasto) {
        fecha_nac = fecha_nac.compareTo("") != 0 ? "'" + fecha_nac + "'" : "NULL";
        fecha_salida = fecha_salida.trim().compareTo("") != 0 ? "'" + fecha_salida + "'" : "NULL";
        dis_porcentaje = dis_porcentaje.trim().compareTo("") != 0 ? "'" + dis_porcentaje + "'" : "NULL";
        fecha_ingreo_trabajo = fecha_ingreo_trabajo.compareTo("") != 0 ? fecha_ingreo_trabajo : Fecha.getFecha("ISO");
        String pkEmpleado = this.insert("INSERT INTO tbl_empleado(id_sucursal, alias, tipo_ident, dni, nombre, apellido, sexo, fecha_nac, estado_civil, calle, numero, "
                + "id_provincia, id_ciudad, id_parroquia, sector, telefono, movil, movil_movistar, email, nacionalidad, ac_no, cedula_militar, licencia_tipo, placa_vehiculo, "
                + "lugar_nacimiento, tipo_sangre, carne_conadis, discapacidad, enfermedades, fecha_ingreso, banco, cuenta, estado, id_rol, id_cargo, id_departamento, "
                + "carnet_iess, generar_rol, periodo_14_sueldo, cobra_14_mensual, cobra_f_r, id_horario, fecha_ingreo_trabajo, cobra_13_mensual, semana,tipo_cuenta, "
                + "forma_pago,numero_semana,fecha_salida,referencias,etnia,calle1,emailpersonal,barrio,instrucciones,dis_porcentaje, distribucion_gasto) "
                + "VALUES(" + id_sucursal + ",'" + alias + "',  '" + tipo_ident + "', '" + dni + "', '" + nombre + "', '" + apellido + "', " + sexo + ", " + fecha_nac + ", '" + estado_civil
                + "', '" + calle + "', '" + numero + "', " + id_provincia + ", " + id_ciudad + ", " + id_parroquia + ", '" + sector + "', '" + telefono + "', '" + movil + "', '" + movil_movistar + "', '" + alias
                + "@saitel.ec', '" + nacionalidad + "', '" + ac_no + "', '" + cedula_militar + "', '" + licencia_tipo + "', '" + placa_vehiculo + "', '" + lugar_nacimiento
                + "', '" + tipo_sangre + "', '" + carne_conadis + "', '" + discapacidad + "', '" + enfermedades + "', '" + fecha_ingreso + "', '" + banco
                + "', '" + cuenta + "', " + estado + ", " + id_rol + ", " + id_cargo + ", '" + id_departamento + "', '" + carnet_iess
                + "', " + generar_rol + ", '" + periodo_14_sueldo + "', " + cobra_14_mensual + ", " + cobra_f_r + ", " + id_horario
                + ", '" + fecha_ingreo_trabajo + "', " + cobra_13_mensual + "," + porcentaje + ",'" + tipo_cuenta + "','" + tipo_transferencia
                + "','" + numero_semana + "'," + fecha_salida + ",'" + referencias + "','" + etnia + "','" + calle1 + "','" + emailpersonal + "','" + barrio + "','" + instrucciones
                + "'," + dis_porcentaje + ", " + distribucionGasto + ");");

        if (pkEmpleado.compareTo("-1") != 0) {
            try {
                ResultSet rs = this.consulta("select modalidad from tab_horarios where id_horario=" + id_horario);
                if (rs.next()) {
                    int modalidad = rs.getString("modalidad") != null ? rs.getInt("modalidad") : 1;
                    if (modalidad == 2 || modalidad == 3) {
                        this.ejecutar("insert into tbl_empleado_asistencia_147(usuario, fecha_inicial, fecha_final, id_horario) values('" + alias + "', '" + fecha_ingreo_trabajo + "', '" + fecha_ingreo_trabajo + "'::date + '6 days'::interval, " + id_horario + ")");
                    }

                    rs.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return pkEmpleado;
    }

    public boolean actualizar(String id, String id_sucursal, String alias, String tipo_ident, String dni, String nombre, String apellido, String sexo, String fecha_nac, String estado_civil,
            String calle, String numero, String id_provincia, String id_ciudad, String id_parroquia, String sector, String telefono, String movil, String movil_movistar, String email, String nacionalidad,
            String ac_no, String cedula_militar, String licencia_tipo, String placa_vehiculo, String lugar_nacimiento, String tipo_sangre, String carne_conadis, String discapacidad, String enfermedades,
            String fecha_ingreso, String banco, String cuenta, String estado, String id_rol, String id_cargo, String id_departamento, String carnet_iess,
            String generar_rol, String periodo_14_sueldo, String cobra_14_mensual, String cobra_f_r, String id_horario, String cobra_13_mensual, String porcentaje, String tipo_cuenta,
            String tipo_transferencia, String numero_semana, String fecha_salida, String referencias, String etnia, String calle1, String emailpersonal, String barrio, String instrucciones, String dis_porcentaje,
            String n_timbrado_sistema2, String distribucionGasto, String mensaje) {
        fecha_nac = fecha_nac.compareTo("") != 0 ? "'" + fecha_nac + "'" : "NULL";
        fecha_salida = fecha_salida.trim().compareTo("") != 0 ? "'" + fecha_salida + "'" : "NULL";
        dis_porcentaje = dis_porcentaje.compareTo("") != 0 ? "'" + dis_porcentaje + "'" : "NULL";
        return this.ejecutar("UPDATE tbl_empleado SET id_sucursal=" + id_sucursal + ", alias='" + alias + "', tipo_ident='" + tipo_ident + "', dni='" + dni + "', nombre='" + nombre + "', apellido='" + apellido
                + "', sexo=" + sexo + ", fecha_nac=" + fecha_nac + ", estado_civil='" + estado_civil + "', calle='" + calle + "', numero='" + numero
                + "', id_provincia=" + id_provincia + ", id_ciudad=" + id_ciudad + ", id_parroquia=" + id_parroquia + ", sector='" + sector + "', telefono='" + telefono + "', movil='" + movil + "', movil_movistar='" + movil_movistar + "', email='" + email
                + "', nacionalidad='" + nacionalidad + "', ac_no='" + ac_no + "', cedula_militar='" + cedula_militar + "', licencia_tipo='" + licencia_tipo + "', placa_vehiculo='" + placa_vehiculo + "', lugar_nacimiento='" + lugar_nacimiento + "', "
                + "tipo_sangre='" + tipo_sangre + "', carne_conadis='" + carne_conadis + "', discapacidad='" + discapacidad + "', enfermedades='" + enfermedades + "', fecha_ingreso='" + fecha_ingreso
                + "', banco='" + banco + "', cuenta='" + cuenta + "', estado=" + estado + ", id_rol=" + id_rol + ", id_cargo=" + id_cargo + ", id_departamento='" + id_departamento + "', carnet_iess='" + carnet_iess
                + "', generar_rol=" + generar_rol + ", periodo_14_sueldo='" + periodo_14_sueldo + "', cobra_14_mensual=" + cobra_14_mensual + ", cobra_f_r=" + cobra_f_r + ", id_horario=" + id_horario + ", cobra_13_mensual=" + cobra_13_mensual
                + ", semana=" + porcentaje + ", tipo_cuenta='" + tipo_cuenta + "', forma_pago='" + tipo_transferencia + "', numero_semana='" + numero_semana + "', fecha_salida=" + fecha_salida + ", referencias='" + referencias
                + "', etnia='" + etnia + "', calle1='" + calle1 + "', emailpersonal='" + emailpersonal + "', barrio='" + barrio + "', instrucciones='" + instrucciones + "',dis_porcentaje=" + dis_porcentaje + ", n_timbrado_sistema='" + n_timbrado_sistema2
                + "', distribucion_gasto=" + distribucionGasto + ", mensaje='"+mensaje+"' WHERE id_empleado=" + id);
    }
    
    public boolean actualizar(String id, String id_provincia, String id_ciudad, String id_parroquia, String sector, String barrio, String calle, 
            String calle1, String numero, String telefono, String movil, String movil_movistar, String email, String emailpersonal, 
            String licencia_tipo, String enfermedades) {
        return this.ejecutar("UPDATE tbl_empleado SET calle='" + calle + "', numero='" + numero + "', id_provincia=" + id_provincia + 
                ", id_ciudad=" + id_ciudad + ", id_parroquia=" + id_parroquia + ", sector='" + sector + "', telefono='" + telefono + 
                "', movil='" + movil + "', movil_movistar='" + movil_movistar + "', email='" + email + "', licencia_tipo='" + licencia_tipo + 
                "', enfermedades='" + enfermedades + "',  calle1='" + calle1 + "', emailpersonal='" + emailpersonal + "', barrio='" + barrio + 
                "' WHERE id_empleado=" + id);
    }

    public ResultSet getEmpleadosREDP(String anio) {
        return this.consulta("select distinct"
                + " id_empleado,"
                + " case "
                + " 	WHEN  tipo_ident='2' then 'C'"
                + " 	WHEN  tipo_ident='3' then 'P'"
                + " end as txttipo_documento,"
                + " dni,"
                + " apellido,"
                + " nombre,"
                + " case "
                + " 	WHEN  id_sucursal<10 then '00'||id_sucursal"
                + " 	WHEN  id_sucursal<100 then '0'||id_sucursal"
                + " end as txtestablecimineto,"
                + " case "
                + " 	WHEN  trim(discapacidad)<>'' then '02'"
                + " 	else '01'"
                + " end as txtdiscapacidad,"
                + " case "
                + " 	WHEN  trim(discapacidad)<>'' then dis_porcentaje"
                + " 	else '0'"
                + " end as txtporcentaje,"
                + " num_cargas_rdep"
                + " from vta_rol_pagos where fecha_final between '" + anio + "-01-01' and '" + anio + "-12-30';");
    }

    public ResultSet getDetallesREDP(String id_empleado, String anio) {
        return this.consulta("select sum(salario)as txtsalario,"
                + " sum(horas_extras+horas_suple+comision)as sobresueldo,"
                + " '0.00'as utilidades,"
                + " sum(fondos_reserva)as txtfondoreserva,"
                + " (select sum(dr.monto) from tbl_rol_pagos as r"
                + " inner join tbl_rubro_cont_det as dr on dr.id_rol_pagos=r.id_rol_pagos"
                + " where  r.id_empleado=" + id_empleado + " and (r.fecha_final between '" + anio + "-01-01' and '" + anio + "-12-31') and dr.id_rubro_cont in ("
                + " select tmp.id_rubro_cont from tbl_rubro_cont  as tmp where tmp.movimiento=true and tmp.estado=true))as txtotros,"
                + " sum(aporte_patronal)as txtaportepatronal,"
                + " sum(aporte_iess) as txtaporteiess"
                + " from tbl_rol_pagos"
                + " where  id_empleado=" + id_empleado + " and (fecha_final between '" + anio + "-01-01' and '" + anio + "-12-30');");
    }

    public ResultSet getDetallesREDPEmpleado(String id_empleado, String anio) {
        return this.consulta("select sum(R1.salario + R1.horas_extras + R1.horas_suple + R1.comision) + (select case when sum(dr.monto) > 0 then sum(dr.monto)::numeric(18,2) else 0 end from tbl_rol_pagos as r inner join tbl_rubro_cont_det as dr on dr.id_rol_pagos=r.id_rol_pagos "
                + " where  r.id_empleado='" + id_empleado + "' and (r.fecha_final between '" + anio + "-01-01' and '" + anio + "-12-31') and dr.id_rubro_cont in (60, 6, 7, 8, 9, 23, 25, 26, 32, 38, 44) "
                + " ) as txtsalario, "
                + " (select case when sum(dr.monto) > 0 then sum(dr.monto)::numeric(18,2) else 0 end from tbl_rol_pagos as r inner join tbl_rubro_cont_det as dr on dr.id_rol_pagos=r.id_rol_pagos "
                + " where  r.id_empleado='" + id_empleado + "' and (r.fecha_final between '" + anio + "-01-01' and '" + anio + "-12-31') and dr.id_rubro_cont in (10, 37) "
                + " ) as sobresueldo, "
                + " '0.00'as utilidades, "
                + " (select trcd.monto from tbl_rubro_cont_det trcd where trcd.id_rubro_cont=54 and trcd.id_empleado='" + id_empleado + "' and (trcd.periodo between '" + anio + "-01-01' and '" + anio + "-12-31') and trcd.id_comprobante_egreso is not null order by trcd.id_rubro_cont_det desc limit 1) as txtdecimotercero, "
                + " (select trcd.monto from tbl_rubro_cont_det trcd where trcd.id_rubro_cont=52 and trcd.id_empleado='" + id_empleado + "' and (trcd.periodo between '" + anio + "-01-01' and '" + anio + "-12-31') and trcd.id_comprobante_egreso is not null order by trcd.id_rubro_cont_det desc limit 1) as txtdecimocuarto, "
                + " (select sum(trcd.monto)::numeric(18,2) from tbl_rubro_cont_det trcd where trcd.id_rubro_cont=56 and trcd.id_empleado='" + id_empleado + "' and (trcd.periodo between '" + anio + "-01-01' and '" + anio + "-12-31') and trcd.id_comprobante_egreso is not null) as txtutilidades, "
                + " sum(R1.fondos_reserva)::numeric(18,2) as txtfondoreserva, "
                + " sum(R1.aporte_patronal)::numeric(18,2) as txtaportepatronal, "
                + " sum(R1.aporte_iess)::numeric(18,2) as txtaporteiess "
                + " from tbl_rol_pagos as R1 "
                + " where R1.id_empleado='" + id_empleado + "' and (R1.fecha_final between '" + anio + "-01-01' and '" + anio + "-12-31') and R1.estado='a';");
    }
   
//    public ResultSet getDetallesREDPEmpleado(String id_empleado, String anio) {
//        return this.consulta("select sum(R1.salario)as txtsalario, "
//                + " ((sum(R1.horas_extras+R1.horas_suple+R1.comision))+ "
//                + " (select sum(dr.monto) from tbl_rol_pagos as r "
//                + " inner join tbl_rubro_cont_det as dr on dr.id_rol_pagos=r.id_rol_pagos "
//                + " where  r.id_empleado='" + id_empleado + "' and (r.fecha_final between '" + anio + "-01-01' and '" + anio + "-12-31') and dr.id_rubro_cont in "
//                + " (select unnest(string_to_array( tc.valor,','))::int8 from tbl_configuracion tc where tc.parametro='rubros_cobrar_rol')) "
//                + " )as sobresueldo, "
//                + " '0.00'as utilidades, "
//                + " (select trcd.monto from tbl_rubro_cont_det trcd where trcd.id_rubro_cont=54 and trcd.id_empleado='" + id_empleado + "' and (trcd.periodo between '" + anio + "-01-01' and '" + anio + "-12-31') and trcd.id_comprobante_egreso is not null order by trcd.id_rubro_cont_det desc limit 1) as txtdecimotercero, "
//                + " (select trcd.monto from tbl_rubro_cont_det trcd where trcd.id_rubro_cont=52 and trcd.id_empleado='" + id_empleado + "' and (trcd.periodo between '" + anio + "-01-01' and '" + anio + "-12-31') and trcd.id_comprobante_egreso is not null order by trcd.id_rubro_cont_det desc limit 1) as txtdecimocuarto, "
//                + " (select sum(trcd.monto) from tbl_rubro_cont_det trcd where trcd.id_rubro_cont=56 and trcd.id_empleado='" + id_empleado + "' and (trcd.periodo between '" + anio + "-01-01' and '" + anio + "-12-31') and trcd.id_comprobante_egreso is not null) as txtutilidades, "
//                + " sum(R1.fondos_reserva)as txtfondoreserva, "
//                + " sum(R1.aporte_patronal)as txtaportepatronal, "
//                + " sum(R1.aporte_iess) as txtaporteiess "
//                + " from tbl_rol_pagos as R1 "
//                + " where R1.id_empleado='" + id_empleado + "' and (R1.fecha_final between '" + anio + "-01-01' and '" + anio + "-12-31') and R1.estado='a';");
//    }

    public ResultSet getEncargados(String where) {
        return this.consulta("SELECT id_empleado,empleado,txt_sucursal,departamento,cargo,alias,dni FROM vta_empleado " + where + " order by empleado;");
    }

    public boolean getObligadoFirmar(String alias) {
        boolean firma = false;
        try {
            ResultSet r = this.consulta("SELECT obligado_firmar as campo FROM vta_empleado where alias='" + alias + "'");
            if (r.next()) {
                firma = (r.getString("campo") != null) ? r.getBoolean("campo") : false;
                r.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return firma;
    }

    public boolean setEmpleadoFirma(String id, String firma) {
        return this.ejecutar("update tbl_empleado set obligado_firmar=" + firma + " where id_empleado=" + id);
    }

    public boolean setNumHorasDevolver(String idEmpleado, String nHoras, String montoDevolver) {
        return this.ejecutar("update tbl_empleado set num_horas_devolver=" + nHoras + ", monto_horas_devolver=" + montoDevolver + " where id_empleado=" + idEmpleado);
    }

    public double getHorasDevueltas(String idEmpleado) {
        double num = 0;
        try {
            ResultSet rs = this.consulta("select (case when sum(num_horas::numeric)>0 then sum(num_horas::numeric) else 0 end +  case when sum(num_minutos::numeric)>0 then sum(num_minutos::numeric)/60 else 0 end) \n"
                    + "from tbl_empleado_hora_extra where tipo='d' and estado in('a') and eliminado=false and id_empleado=" + idEmpleado);
            if (rs.next()) {
                num = (rs.getString(1) != null) ? rs.getFloat(1) : 0.0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    public String getCodigoTimbradoActual(String alias) {
        String codigo = "";
        try {
            ResultSet r = this.consulta("SELECT codigo_timbrado from tbl_empleado_asistencia_dispotivo where alias='" + alias + "' and vigente=true and aprobado=true;");
            if (r != null) {
                if (r.next()) {
                    codigo = (r.getString(1) != null) ? r.getString(1) : "";
                    r.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return codigo;
    }

}
