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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jorge
 */
public class OrdenTrabajo extends DataBase {

    public OrdenTrabajo(String m, int p, String db, String u, String c) {
        super(m, p, db, u, c);
    }

    public ResultSet getOrdenTrabajo(String id) {
        return this.consulta("SELECT * FROM vta_orden_trabajo where id_orden_trabajo=" + id
                + " union all "
                + " SELECT * FROM vta_orden_trabajo_preinstalacion where id_orden_trabajo=" + id);
    }

    public ResultSet getOrdenTrabajo(String id, String version) {
        return this.consulta("SELECT O.*, (S.id_orden_trabajo_solicitud is not null and S.estado='g') as solicitud_pendiente FROM vta_orden_trabajo as O left join tbl_orden_trabajo_solicitud as S on O.id_orden_trabajo = S.id_orden_trabajo where O.id_orden_trabajo=" + id
                + " union all "
                + " SELECT O.*, (S.id_orden_trabajo_solicitud is not null and S.estado='g') as solicitud_pendiente FROM vta_orden_trabajo_preinstalacion as O left join tbl_orden_trabajo_solicitud as S on O.id_orden_trabajo = S.id_orden_trabajo where O.id_orden_trabajo=" + id);
    }

    public String getOrdenTrabajo(int sucursal, String numero) {
        String r = "-1";
        try {
            ResultSet rs = this.consulta("SELECT id_orden_trabajo FROM vta_orden_trabajo where id_sucursal='" + sucursal + "' and num_orden='" + numero + "'");
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

    public ResultSet getOrdenTrabajoInfra(String id) {
        return this.consulta("SELECT * FROM vta_orden_trabajo_infra where id_orden_trabajo=" + id);
    }

    public ResultSet getOrdenesTrabajo(String id) {
        return this.consulta("SELECT * FROM vta_orden_trabajo where estado<>'9' and id_hoja_ruta=" + id + ""
                + " union"
                + " SELECT * FROM vta_orden_trabajo_preinstalacion where id_hoja_ruta=" + id + " order by prioridad desc");
    }

    /*public ResultSet getOrdenesTrabajo(String id) {
        return this.consulta("SELECT * FROM vta_orden_trabajo where id_hoja_ruta=" + id + " order by prioridad desc");
    }*/
    public String[] getOrdenesTrabajoPrefactura(int id) {
        String r[] = new String[]{"No debe", ""};
        try {
            ResultSet rs = this.consulta("SELECT 'Debe del período: '||getaniomes(periodo)||' Subtotal: '||subtotal||' Iva: '||iva_2||' Total: '||total, total\n"
                    + "FROM tbl_prefactura \n"
                    + "where id_instalacion=" + id + " and fecha_emision is null\n"
                    + "union\n"
                    + "SELECT replace ( replace( replace(array_agg(descripcion_mas)::varchar, '{', ''), '}', ''), '\"','') ||' Saldo Pendiente: '||v.deuda, v.deuda as total\n"
                    + "FROM tbl_factura_venta v\n"
                    + "join tbl_factura_venta_detalle p on p.id_factura_venta=v.id_factura_venta\n"
                    + "where v.id_instalacion=" + id + " and deuda>0\n"
                    + "group by v.deuda;");
            if (rs.next()) {
                r[0] = rs.getString(1) != null ? rs.getString(1) : "1";
                r[1] = rs.getString("total") != null ? rs.getString("total") : "0";
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return r;
    }

    public ResultSet getMaterialesBodega(String id) {
        return this.consulta("select codigo_activo, descripcion from (tbl_activo as A inner join tbl_bodega_activo as BA on A.id_activo=BA.id_activo) "
                + "inner join tbl_bodega as B on B.id_bodega=BA.id_bodega where id_instalacion=" + id);
        /*
         String materiales = "";
         try{
         ResultSet rs = this.consulta("select codigo_activo, descripcion from (tbl_activo as A inner join tbl_bodega_activo as BA on A.id_activo=BA.id_activo) " +
         "inner join tbl_bodega as B on B.id_bodega=BA.id_bodega where id_instalacion="+id);
         while(rs.next()){
         String codigoActivo = rs.getString("codigo_activo")!=null ? rs.getString("codigo_activo") : "";
         String descripcion = rs.getString("descripcion")!=null ? rs.getString("descripcion") : "";
                
         rs.close();
         }
         rs.close();
         }catch(Exception e){
         e.printStackTrace();
         }
         return materiales;*/
    }
    
    public ResultSet getMaterialesOrden(String id) {
        return this.consulta("select OM.*, M.id_plan_cuenta_gasto, P.id_producto, P.codigo, P.descripcion \n" +
            "from tbl_orden_trabajo_material as OM \n" +
            "	inner join tbl_material as M on OM.id_material = M.id_material \n" +
            "	inner join tbl_producto as P on M.id_producto = P.id_producto \n" +
            "where id_orden_trabajo = " + id);
    }

    public String getNumOrden(int idSuc) {
        String r = "1";
        try {
            ResultSet rs = this.consulta("select case when max(num_orden)>0 then max(num_orden)+1 else 1 end from tbl_orden_trabajo where id_sucursal=" + idSuc);
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

    public String insertar(String id_instalacion, int id_sucursal_sesion, String num_orden, String tipo_trabajo,
            String usuario_reporte, String fecha_cliente, String hora_cliente, String diagnostico_tecnico) {
        String usuario_cliente = "";
        if (fecha_cliente.compareTo("") != 0) {
            usuario_cliente = usuario_reporte;
        }
        fecha_cliente = fecha_cliente.compareTo("") != 0 ? "'" + fecha_cliente + "'" : "NULL";
        hora_cliente = hora_cliente.compareTo("") != 0 ? "'" + hora_cliente + "'" : "NULL";
        return this.insert("INSERT INTO tbl_orden_trabajo(id_instalacion, id_sucursal, num_orden, tipo_trabajo, usuario_reporte, fecha_reporte, hora_reporte, usuario_cliente, fecha_cliente, hora_cliente, diagnostico_tecnico, estado, tipo) "
                + "VALUES(" + id_instalacion + ", " + id_sucursal_sesion + ", " + num_orden + ", '" + tipo_trabajo + "', '" + usuario_reporte + "', now()::date, now()::time, '" + usuario_cliente + "', " + fecha_cliente + ", " + hora_cliente + ", '" + diagnostico_tecnico + "', '1', 'c');");
        /*if(pk.compareTo("-1")!=0){
         this.ejecutar("update tbl_instalacion set estado_servicio='c' where id_instalacion="+id_instalacion);
         }
         return pk;*/
    }

    public String InsertOrdenCertificado(String id_instalacion, int id_sucursal_sesion, String num_orden, String tipo_trabajo,
            String usuario_reporte, String fecha_cliente, String hora_cliente, String diagnostico_tecnico, String id_instalacion_certificado, String observacion) {
        String usuario_cliente = "";
        if (fecha_cliente.compareTo("") != 0) {
            usuario_cliente = usuario_reporte;
        }
        fecha_cliente = fecha_cliente.compareTo("") != 0 ? "'" + fecha_cliente + "'" : "NULL";
        hora_cliente = hora_cliente.compareTo("") != 0 ? "'" + hora_cliente + "'" : "NULL";
        return this.insert("INSERT INTO tbl_orden_trabajo(id_instalacion, id_sucursal, num_orden, tipo_trabajo, usuario_reporte, fecha_reporte, hora_reporte, usuario_cliente, fecha_cliente, hora_cliente, diagnostico_tecnico, estado, tipo,id_instalacion_certificado) "
                + "VALUES(" + id_instalacion + ", " + id_sucursal_sesion + ", " + num_orden + ", '" + tipo_trabajo + "', '" + usuario_reporte + "', now()::date, now()::time, '" + usuario_cliente + "', " + fecha_cliente + ", " + hora_cliente + ", '" + diagnostico_tecnico + " '||now()||' " + observacion + "', '1', 'c','" + id_instalacion_certificado + "');");
    }

    public String insertar(String id_instalacion, int id_sucursal_sesion, String num_orden, String tipo_trabajo,
            String usuario_reporte, String fecha_cliente, String hora_cliente, String diagnostico_tecnico, String tipo_servicio) {
        String usuario_cliente = "";
        if (fecha_cliente.compareTo("") != 0) {
            usuario_cliente = usuario_reporte;
        }
        fecha_cliente = fecha_cliente.compareTo("") != 0 ? "'" + fecha_cliente + "'" : "NULL";
        hora_cliente = hora_cliente.compareTo("") != 0 ? "'" + hora_cliente + "'" : "NULL";
        return this.insert("INSERT INTO tbl_orden_trabajo(id_instalacion, id_sucursal, num_orden, tipo_trabajo, usuario_reporte, fecha_reporte, hora_reporte, usuario_cliente, fecha_cliente, hora_cliente, diagnostico_tecnico, estado, tipo,tipo_servicio) "
                + "VALUES(" + id_instalacion + ", " + id_sucursal_sesion + ", " + num_orden + ", '" + tipo_trabajo + "', '" + usuario_reporte + "', now()::date, now()::time, '" + usuario_cliente + "', " + fecha_cliente + ", " + hora_cliente + ", '" + diagnostico_tecnico + "', '1', 'c','" + tipo_servicio + "');");
        /*if(pk.compareTo("-1")!=0){
         this.ejecutar("update tbl_instalacion set estado_servicio='c' where id_instalacion="+id_instalacion);
         }
         return pk;*/
    }

    public int getIdSucursal(String idInstalacion) {
        int r = 1;
        try {
            ResultSet rs = this.consulta("select id_sucursal from tbl_instalacion where id_instalacion=" + idInstalacion);
            if (rs.next()) {
                r = rs.getString(1) != null ? rs.getInt(1) : 1;
                rs.close();
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return r;
    }

    public String insertar(String id_instalacion, int id_sucursal_sesion, String num_orden, String tipo_trabajo,
            String usuario_reporte, String fecha_cliente, String hora_cliente, String diagnostico_tecnico, String tipo_servicio, String id_soporte) {
        String usuario_cliente = "";
        if (fecha_cliente.compareTo("") != 0) {
            usuario_cliente = usuario_reporte;
        }
        fecha_cliente = fecha_cliente.compareTo("") != 0 ? "'" + fecha_cliente + "'" : "NULL";
        hora_cliente = hora_cliente.compareTo("") != 0 ? "'" + hora_cliente + "'" : "NULL";
        return this.insert("INSERT INTO tbl_orden_trabajo(id_instalacion, id_sucursal, num_orden, tipo_trabajo, usuario_reporte, fecha_reporte, hora_reporte, usuario_cliente, fecha_cliente, hora_cliente, diagnostico_tecnico, estado, tipo,tipo_servicio,id_soporte) "
                + "VALUES(" + id_instalacion + ", " + id_sucursal_sesion + ", " + num_orden + ", '" + tipo_trabajo + "', '" + usuario_reporte + "', now()::date, now()::time, '" + usuario_cliente + "', " + fecha_cliente + ", " + hora_cliente + ", '" + diagnostico_tecnico + "', '1', 'c','" + tipo_servicio + "','" + id_soporte + "');");
        /*if(pk.compareTo("-1")!=0){
         this.ejecutar("update tbl_instalacion set estado_servicio='c' where id_instalacion="+id_instalacion);
         }
         return pk;*/
    }

//    public boolean actualizar(String id, String tipo_trabajo, String usuario_cliente, String fecha_cliente, String hora_cliente,
//            String diagnostico_tecnico, String estado, String cambio_estado) {
//        fecha_cliente = fecha_cliente.compareTo("") != 0 ? "'" + fecha_cliente + "'" : "NULL";
//        hora_cliente = hora_cliente.compareTo("") != 0 ? "'" + hora_cliente + "'" : "NULL";
//        return this.ejecutar("UPDATE tbl_orden_trabajo SET tipo_trabajo='" + tipo_trabajo + "', usuario_cliente='" + usuario_cliente + "', fecha_cliente=" + fecha_cliente
//                + ", hora_cliente=" + hora_cliente + ", diagnostico_tecnico = diagnostico_tecnico || '" + diagnostico_tecnico + "', estado='" + estado + "', cambio_estado='' WHERE id_orden_trabajo=" + id);
//    }
    public boolean actualizar(String id, String tipo_trabajo, String usuario_cliente, String fecha_cliente, String hora_cliente,
            String diagnostico_tecnico, String estado, String cambio_estado) {
        fecha_cliente = fecha_cliente.compareTo("") != 0 ? "'" + fecha_cliente + "'" : "NULL";
        hora_cliente = hora_cliente.compareTo("") != 0 ? "'" + hora_cliente + "'" : "NULL";
        String estadoPendiente = estado.compareTo("3") == 0 ? ", usuario_pendiente='" + usuario_cliente + "', fecha_pendiente=now()::date, hora_pendiente=now()::time, motivo_pendiente='" + diagnostico_tecnico + "' " : "";
        return this.ejecutar("UPDATE tbl_orden_trabajo SET tipo_trabajo='" + tipo_trabajo + "', usuario_cliente='" + usuario_cliente + "', fecha_cliente=" + fecha_cliente
                + ", hora_cliente=" + hora_cliente + ", diagnostico_tecnico = coalesce(diagnostico_tecnico,'') || '" + diagnostico_tecnico + "', estado='" + estado + "',  cambio_estado='' " + estadoPendiente + " WHERE id_orden_trabajo=" + id);
    }

    public boolean setEstadoOrden(String ids_ordenes, String estado, String usuario, String motivo) {
        String set = "";
        if (estado.compareTo("1") == 0) {
            set = ", id_hoja_ruta=null, motivo_pendiente='" + motivo + "'";
        }
        if (estado.compareTo("3") == 0) {
            set = ", usuario_pendiente='" + usuario + "', fecha_pendiente=now(), hora_pendiente=now(), id_hoja_ruta=null, motivo_pendiente='" + motivo + "'";
        }
        return this.ejecutar("UPDATE tbl_orden_trabajo SET estado='" + estado + "' " + set + " WHERE id_orden_trabajo=" + ids_ordenes);
    }

    public boolean setEstadoOrdenes(String ids_ordenes, String estado) {
        return this.ejecutar("UPDATE tbl_orden_trabajo SET estado='" + estado + "' WHERE id_orden_trabajo in (" + ids_ordenes + ")");
    }

    public String tareas(String usuario, String id_tecnico_resp, String observacion, String fecha_realizacion, String hora_realizacion, String ids_ordenes) {
        String id_hoja_ruta = this.insert("insert into tbl_hoja_ruta(usuario, id_tecnico_resp, observacion) values('" + usuario + "', " + id_tecnico_resp + ", '" + observacion + "')");
        if (id_hoja_ruta.compareTo("-1") != 0) {
            List sql = new ArrayList();
            String vec_ids_ordenes[] = ids_ordenes.split(",");
            String usuario_realizacion = ""; // sale del tecnico responsable
            try {
                ResultSet rs = this.consulta("select alias from tbl_empleado where id_empleado=" + id_tecnico_resp);
                if (rs.next()) {
                    usuario_realizacion = rs.getString("alias") != null ? rs.getString("alias") : "";
                    rs.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (int i = 0; i < vec_ids_ordenes.length; i++) {
                sql.add("UPDATE tbl_orden_trabajo SET usuario_realizacion='" + usuario_realizacion + "', fecha_realizacion='" + fecha_realizacion
                        + "', hora_realizacion='" + hora_realizacion + "', id_empleado=" + id_tecnico_resp + ", id_hoja_ruta=" + id_hoja_ruta + " WHERE id_orden_trabajo=" + vec_ids_ordenes[i]);
            }
            if (!this.transacciones(sql)) {
                this.ejecutar("delete from tbl_hoja_ruta where id_hoja_ruta=" + id_hoja_ruta);
                id_hoja_ruta = "-1";
            }
        }
        return id_hoja_ruta;
    }

    public boolean impresion(String id) {
        return this.ejecutar("UPDATE tbl_orden_trabajo SET impresion=impresion+1 WHERE id_orden_trabajo=" + id);
    }

    public boolean impresiones(String id) {
        return this.ejecutar("UPDATE tbl_orden_trabajo SET impresion=impresion+1 WHERE id_hoja_ruta=" + id);
    }

    public String concatenarValores(String id_materiales, String cantidades) {
        String param = "";
        String vecMat[] = id_materiales.split(",");
        String vecCan[] = cantidades.split(",");
        for (int i = 0; i < vecMat.length; i++) {
            param += "['" + vecMat[i] + "','" + vecCan[i] + "'],";
        }
        param = param.substring(0, param.length() - 1);
        return "array[" + param + "]";
    }

    public boolean noSolucionar(String id_ordenes) {
        List sql = new ArrayList();
        sql.add("update tbl_instalacion set estado_servicio='p' where estado_servicio='a' and fecha_instalacion is null and id_instalacion in (select id_instalacion from tbl_orden_trabajo where id_orden_trabajo in (" + id_ordenes + "));");
        sql.add("update tbl_orden_trabajo set id_empleado=null, id_hoja_ruta=null, usuario_realizacion=null, fecha_realizacion=null, hora_realizacion=null, estado='1' where id_orden_trabajo in (" + id_ordenes + ")");
        sql.add("update tbl_hoja_ruta set estado_hoja='9' where id_hoja_ruta in (select id_hoja_ruta from tbl_orden_trabajo where id_orden_trabajo not in (" + id_ordenes + ") and estado<>'9' and estado<>'3')");
        return this.transacciones(sql);
    }

    public boolean solucionar(int id_sucursal, String id, String usuario_solucion, String fecha_solucion, String hora_solucion,
            String solucionado, String conformidad, String atencion, String recomendacion, String materiales, String cantidades, String set_deviceclave) {
        /*List sql = new ArrayList();
         sql.add("UPDATE tbl_orden_trabajo SET usuario_solucion='"+usuario_solucion+"', fecha_solucion='"+fecha_solucion+"', hora_solucion='"+
         hora_solucion+"', solucionado="+solucionado+", conformidad='"+conformidad+"', atencion='"+atencion+"', recomendacion='"+recomendacion+"'"
         + ", estado='3' WHERE id_orden_trabajo="+id);
         return this.transacciones(sql);*/
        boolean ok = false;
        try {
            String paramMateriales = this.concatenarValores(materiales, cantidades);
            ResultSet res = this.consulta("select proc_orden_trabajo_solucion(" + id_sucursal + ", " + id + ", '" + usuario_solucion + "', '" + fecha_solucion + "', '" + hora_solucion + "', " + solucionado
                    + ", '" + conformidad + "', '" + atencion + "', '" + recomendacion + "', " + paramMateriales + ", " + set_deviceclave + ")");
            if (res.next()) {
                ok = (res.getString(1) != null) ? res.getBoolean(1) : false;
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok;
    }

    public boolean solucionar(int id_sucursal, String id, String usuario_solucion, String fecha_solucion, String hora_solucion,
            String solucionado, String conformidad, String atencion, String recomendacion, String materiales, String cantidades, String set_deviceclave, List sql) {

        boolean ok = false;
        Connection con = this.getConexion();
        try {
            con.setAutoCommit(false);
            String paramMateriales = this.concatenarValores(materiales, cantidades);
            ResultSet res = this.consulta("select proc_orden_trabajo_solucion(" + id_sucursal + ", " + id + ", '" + usuario_solucion + "', '" + fecha_solucion + "', '" + hora_solucion + "', " + solucionado
                    + ", " + conformidad + ", " + atencion + ", '" + recomendacion + "', " + paramMateriales + ", " + set_deviceclave + ")");
            if (res.next()) {
                ok = (res.getString(1) != null) ? res.getBoolean(1) : false;
                res.close();
            }
            if (ok) {
                Statement st = con.createStatement();
                if (!sql.isEmpty()) {
                    Iterator it = sql.iterator();
                    while (it.hasNext()) {
                        st.executeUpdate("" + it.next());
                    }
                }
                st.close();
                con.commit();
            } else {
                con.rollback();
            }

        } catch (Exception e) {
            e.printStackTrace();
            ok = false;
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(Instalacion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return ok;
    }

    public boolean solucionar(String id, String capacidad_efectiva) {
        List sql = new ArrayList();
        sql.add("UPDATE tbl_orden_trabajo SET capacidad_efectiva='" + capacidad_efectiva + "' WHERE id_orden_trabajo=" + id);
        //sql.add("update tbl_instalacion set estado_servicio='a' where id_instalacion="+id_instalacion);
        return this.transacciones(sql);
    }

    public boolean solucionar(String id, String capacidad_efectiva, String vel_carga, String vel_descarga) {
        List sql = new ArrayList();
        sql.add("UPDATE tbl_orden_trabajo SET capacidad_efectiva='" + capacidad_efectiva + "',"
                + " velocidad_carga='" + vel_carga + "',velocidad_descarga='" + vel_descarga + "'"
                + " WHERE id_orden_trabajo=" + id);
        //sql.add("update tbl_instalacion set estado_servicio='a' where id_instalacion="+id_instalacion);
        return this.transacciones(sql);
    }

    /*   temporalmente compatibilidad con version anterior   */
    public boolean solucionar(String id, String id_instalacion, String fecha_solucion, String materiales, String recomendacion) {
        fecha_solucion = fecha_solucion.compareTo("") != 0 ? fecha_solucion : Fecha.getFecha("ISO");
        List sql = new ArrayList();
        sql.add("UPDATE tbl_orden_trabajo SET fecha_solucion='" + fecha_solucion + "', materiales='" + materiales + "', recomendacion='" + recomendacion + "'"
                + ", estado='9' WHERE id_orden_trabajo=" + id);
        //sql.add("update tbl_instalacion set estado_servicio='a' where id_instalacion="+id_instalacion);
        return this.transacciones(sql);
    }

    public boolean solucionarProcedente(String id, String fecha_solucion, String recomendacion, String estado) {
        fecha_solucion = fecha_solucion.compareTo("") != 0 ? fecha_solucion : Fecha.getFecha("ISO");
        List sql = new ArrayList();
        sql.add("UPDATE tbl_orden_trabajo SET fecha_solucion='" + fecha_solucion + "', recomendacion='" + recomendacion + "'"
                + ", estado='" + estado + "' WHERE id_orden_trabajo=" + id);
        //sql.add("update tbl_instalacion set estado_servicio='a' where id_instalacion="+id_instalacion);
        return this.transacciones(sql);
    }

    public boolean solucionarProcedente(String id, String fecha_solucion, String recomendacion, String estado, String usuario, String hora) {
        fecha_solucion = fecha_solucion.compareTo("") != 0 ? fecha_solucion : Fecha.getFecha("ISO");
        List sql = new ArrayList();
        sql.add("UPDATE tbl_orden_trabajo SET fecha_solucion='" + fecha_solucion + "', recomendacion='" + recomendacion + "'"
                + ", estado='" + estado + "',usuario_solucion='" + usuario + "',hora_solucion='" + hora + "' WHERE id_orden_trabajo=" + id);
        //sql.add("update tbl_instalacion set estado_servicio='a' where id_instalacion="+id_instalacion);
        return this.transacciones(sql);
    }

    /*Encuestas Arcotel    */
    public ResultSet getPreguntasArcotel() {
        return this.consulta("select id_encuesta_arcotel, pregunta_empresa from tbl_encuesta_arcotel");
    }

    /*  tipos de ordenes de trabajo */
    public ResultSet getTipos() {
        return this.consulta("SELECT id_orden_trabajo_tipo, orden_trabajo FROM tbl_orden_trabajo_tipo order by prioridad");
    }

    public ResultSet getTipos(String fecha_instalacion) {
        if (fecha_instalacion.compareTo("") == 0) {
            return this.consulta("SELECT id_orden_trabajo_tipo, orden_trabajo FROM tbl_orden_trabajo_tipo where lower(orden_trabajo) like 'instalac%'");
        }
        return this.consulta("SELECT id_orden_trabajo_tipo, orden_trabajo FROM tbl_orden_trabajo_tipo order by prioridad");
    }

    public ResultSet getTiposTareas(int id_padre, int id_sucursal) {
        return this.consulta("SELECT * FROM vta_tarea where id_tarea_padre=" + id_padre + " and id_sucursal in (0, " + id_sucursal + ") order by " + (id_padre == 1 ? "id_tarea" : "tarea"));
    }

    public ResultSet getTiposTareas(int id_padre, int id_sucursal, String id_nodo) {
        return this.consulta("SELECT * FROM vta_tarea where id_tarea_padre=" + id_padre + " and id_sucursal in (0, " + id_sucursal + ") and id_nodo in (0, " + id_nodo + ") order by " + (id_padre == 1 ? "id_tarea" : "tarea"));
    }

    public ResultSet getTiposTareas(int id_padre, int id_sucursal, String id_nodo, String id_tipo_nodo) {
        String w = "and id_nodo in (0, " + id_nodo + ") and tipo_nodo in ('X','N')";
        if (id_tipo_nodo.compareTo("O") == 0) {
            w = "and id_nodo in (0, " + id_nodo + ") and tipo_nodo in ('X','O')";
        }
        return this.consulta("SELECT * FROM vta_tarea where id_tarea_padre=" + id_padre + " and id_sucursal in (0, " + id_sucursal + ") " + w + "  order by " + (id_padre == 1 ? "id_tarea" : "tarea"));
    }

    public ResultSet getContTiposTareas() {
        return this.consulta("SELECT id_tarea_padre, count(id_tarea_padre) FROM vta_tarea where id_tarea_padre>0 group by id_tarea_padre order by id_tarea_padre");
    }

    public ResultSet getTareasAsignadas(String id) {
        return this.consulta("SELECT * FROM tbl_orden_trabajo_tarea where id_orden_trabajo=" + id + " order by id_tarea");
    }

    public ResultSet getTareasOrden(String id) {
        return this.consulta("SELECT O.*, T.tarea FROM (tbl_orden_trabajo_tarea as O inner join vta_tarea as T on T.id_tarea=O.id_tarea) "
                + "where O.id_orden_trabajo=" + id + " order by T.id_tarea");
    }

    public String insertarInfraestructura(int id_sucursal_sesion, String num_orden, String id_nodo, String usuario_reporte, String fecha_cliente,
            String hora_cliente, String diagnostico_tecnico, String id_empleado, String ids_tareas, String idsEmpleados) {
        String usuario_cliente = "";
        if (fecha_cliente.compareTo("") != 0) {
            usuario_cliente = usuario_reporte;
        }
        fecha_cliente = fecha_cliente.compareTo("") != 0 ? "'" + fecha_cliente + "'" : "NULL";
        hora_cliente = hora_cliente.compareTo("") != 0 ? "'" + hora_cliente + "'" : "NULL";
        String pk = this.insert("INSERT INTO tbl_orden_trabajo(id_instalacion, id_sucursal, num_orden, id_nodo, tipo_trabajo, usuario_reporte, fecha_reporte, hora_reporte, usuario_cliente, fecha_cliente, hora_cliente, diagnostico_tecnico, id_empleado, estado, tipo) "
                + "VALUES(null, " + id_sucursal_sesion + ", " + num_orden + ", " + id_nodo + ",'9', '" + usuario_reporte + "', now()::date, now()::time, '" + usuario_cliente + "', " + fecha_cliente + ", " + hora_cliente + ", '" + diagnostico_tecnico + "', " + id_empleado + ", '1', 'i');");
        if (pk.compareTo("-1") != 0) {
            String tarea[] = ids_tareas.split(",");
            for (int i = 0; i < tarea.length; i++) {
                this.ejecutar("insert into tbl_orden_trabajo_tarea(id_orden_trabajo, id_tarea) values(" + pk + ", " + tarea[i] + ")");
            }
        }
        if (idsEmpleados.compareTo("") != 0) {
            String vecIdEmpleado[] = idsEmpleados.split(",");
            for (int i = 0; i < vecIdEmpleado.length; i++) {
                this.ejecutar("insert into tbl_orden_trabajo_integrantes(id_orden_trabajo, id_empleado) values(" + pk + ", " + vecIdEmpleado[i] + ");");
            }
        }
        return pk;
    }

    public String insertarInfraestructura(int id_sucursal_sesion, String num_orden, String id_nodo, String usuario_reporte, String fecha_cliente,
            String hora_cliente, String diagnostico_tecnico, String id_empleado, String ids_tareas, String idsEmpleados, String tipo_nodo_infra) {
        String usuario_cliente = "";
        if (fecha_cliente.compareTo("") != 0) {
            usuario_cliente = usuario_reporte;
        }
        fecha_cliente = fecha_cliente.compareTo("") != 0 ? "'" + fecha_cliente + "'" : "NULL";
        hora_cliente = hora_cliente.compareTo("") != 0 ? "'" + hora_cliente + "'" : "NULL";
        String pk = this.insert("INSERT INTO tbl_orden_trabajo(id_instalacion, id_sucursal, num_orden, id_nodo, tipo_trabajo, usuario_reporte, fecha_reporte, hora_reporte, usuario_cliente, fecha_cliente, hora_cliente, diagnostico_tecnico, id_empleado, estado, tipo,tipo_nodo_infra) "
                + "VALUES(null, " + id_sucursal_sesion + ", " + num_orden + ", " + id_nodo + ",'9', '" + usuario_reporte + "', now()::date, now()::time, '" + usuario_cliente + "', " + fecha_cliente + ", " + hora_cliente + ", '" + diagnostico_tecnico + "', " + id_empleado + ", '1', 'i','" + tipo_nodo_infra + "');");
        if (pk.compareTo("-1") != 0) {
            String tarea[] = ids_tareas.split(",");
            for (int i = 0; i < tarea.length; i++) {
                this.ejecutar("insert into tbl_orden_trabajo_tarea(id_orden_trabajo, id_tarea) values(" + pk + ", " + tarea[i] + ")");
            }
        }
        if (idsEmpleados.compareTo("") != 0) {
            String vecIdEmpleado[] = idsEmpleados.split(",");
            for (int i = 0; i < vecIdEmpleado.length; i++) {
                this.ejecutar("insert into tbl_orden_trabajo_integrantes(id_orden_trabajo, id_empleado) values(" + pk + ", " + vecIdEmpleado[i] + ");");
            }
        }
        return pk;
    }

    public boolean actualizarInfraestructura(String id_orden_trabajo, String id_nodo, String fecha_cliente, String hora_cliente, String diagnostico_tecnico,
            String id_empleado, String ids_tareas, String idsEmpleados, String tipo_nodo_infra) {
        fecha_cliente = fecha_cliente.compareTo("") != 0 ? "'" + fecha_cliente + "'" : "NULL";
        hora_cliente = hora_cliente.compareTo("") != 0 ? "'" + hora_cliente + "'" : "NULL";
        List sql = new ArrayList();
        sql.add("update tbl_orden_trabajo set id_nodo=" + id_nodo + ", fecha_cliente=" + fecha_cliente + ", hora_cliente=" + hora_cliente
                + ", diagnostico_tecnico='" + diagnostico_tecnico + "', id_empleado=" + id_empleado + ",tipo_nodo_infra='" + tipo_nodo_infra + "' where id_orden_trabajo=" + id_orden_trabajo);

        if (ids_tareas.compareTo("") != 0) {
            sql.add("delete from tbl_orden_trabajo_tarea where id_orden_trabajo=" + id_orden_trabajo);
            String tarea[] = ids_tareas.split(",");
            for (int i = 0; i < tarea.length; i++) {
                sql.add("insert into tbl_orden_trabajo_tarea(id_orden_trabajo, id_tarea) values(" + id_orden_trabajo + ", " + tarea[i] + ")");
            }
        }
        if (idsEmpleados.compareTo("") != 0) {
            String vecIdEmpleado[] = idsEmpleados.split(",");
            sql.add("delete from tbl_orden_trabajo_integrantes where id_orden_trabajo=" + id_orden_trabajo);
            for (int i = 0; i < vecIdEmpleado.length; i++) {
                sql.add("insert into tbl_orden_trabajo_integrantes(id_orden_trabajo, id_empleado) values(" + id_orden_trabajo + ", " + vecIdEmpleado[i] + ");");
            }
        }
        return this.transacciones(sql);
    }

    public String concatenarValores(String id_tareas, String solucionados, String observaciones, String macs_nuevas, String macs_retiradas) {
        String param = "";
        String vecCods[] = id_tareas.split(";");
        String vecSol[] = solucionados.split(";");
        String vecObs[] = observaciones.split(";");
        String vecMacN[] = macs_nuevas.split(";");
        String vecMacR[] = macs_retiradas.split(";");
        for (int i = 0; i < vecCods.length; i++) {
            param += "['" + vecCods[i] + "','" + vecSol[i] + "','" + vecObs[i] + "','" + vecMacN[i].trim() + "','" + vecMacR[i].trim() + "'],";
        }
        param = param.substring(0, param.length() - 1);
        return "array[" + param + "]";
    }

    public String solucionarInfraestructura(String id_orden_trabajo, String usuario_solucion, String fecha_solucion,
            String id_tareas, String solucionados, String observaciones, String macs_nuevas, String macs_retiradas) {
        String res = "";
        try {
            String macs = this.concatenarValores(id_tareas, solucionados, observaciones, macs_nuevas, macs_retiradas);
            ResultSet rs = this.consulta("select proc_solucionarOrdenTrabajoInfra(" + id_orden_trabajo
                    + ", '" + usuario_solucion + "', '" + fecha_solucion + "', " + macs + ")");
            if (rs.next()) {
                res = rs.getString(1) != null ? rs.getString(1) : "";
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            res = e.getMessage();
        }
        return res;
    }
    
    public String[] getIdPersonalizacionesInfraestructura(String idOrden)
    {
        String vecIdPersonalizacion[] = null;
        try {
            ResultSet rs = this.consulta("SELECT id_personalizacion FROM tbl_orden_trabajo where id_orden_trabajo=" + idOrden);
            if(rs.next()){
                String axIdPersonalizacion = rs.getString("id_personalizacion")!=null ? rs.getString("id_personalizacion") : "";
                if(axIdPersonalizacion.compareTo("")!=0){
                    vecIdPersonalizacion = axIdPersonalizacion.split(",");
                }
                rs.close();
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return vecIdPersonalizacion;
    }

    ////// registro en kardex y aasiento contable
    public ResultSet kardexdeconsumoproducto(String codigoslista, String cantidadlista, String preciolista, int idsucursal, String detalle, String comprobante) {
        String codigos[] = codigoslista.split(",");
        String cantidades[] = cantidadlista.split(",");
        String precios[] = preciolista.split(",");
        double preciostotales = 0;
        double cantidad = 0;
        Connection con = this.getConexion();
        try {
            con.setAutoCommit(false);
            Statement st = con.createStatement();
            for (int i = 0; i < codigos.length; i++) {
                preciostotales = Double.parseDouble(cantidades[i]) * Double.parseDouble(precios[i]);
                st.executeUpdate("UPDATE tbl_kardex SET ultima_edicion_salida=false WHERE ultima_edicion_salida=true and id_producto='" + codigos[i] + "';");
                st.executeUpdate("INSERT INTO tbl_kardex(id_producto,id_factura,fecha,detalle,cantidad,costo_unitario,costo_total,es_entrada) \n"
                        + "VALUES('" + codigos[i] + "',0, now(), ('" + detalle + " . " + comprobante + "'),'" + cantidades[i] + "', \n"
                        + "'" + precios[i] + "', '" + preciostotales + "', false);");
                cantidad = this.getcantidadproducto(idsucursal, codigos[i], cantidades[i]);
                st.executeUpdate("INSERT INTO tbl_kardex_sucursal(id_sucursal, id_producto, id_factura, fecha, detalle, cantidad, costo_unitario, costo_total, saldo_cantidad, es_entrada) \n"
                        + "values('" + idsucursal + "', '" + codigos[i] + "', 0, now(), '" + detalle + " . " + comprobante + "', \n"
                        + "'" + cantidades[i] + "', '" + precios[i] + "', '" + preciostotales + "', '" + cantidad + "', false);");
                st.executeUpdate("UPDATE tbl_sucursal_producto SET stock_sucursal=stock_sucursal-'" + cantidades[i] + "' WHERE id_sucursal='" + idsucursal + "' and id_producto = '" + codigos[i] + "';");
            }
            con.commit();
        } catch (Exception e) {
            e.printStackTrace();

            try {
                con.rollback();
            } catch (Exception se) {
                se.printStackTrace();
            }
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return this.consulta("");
    }

    public ResultSet kardexdeconsumoproducto(String codigoslista, String cantidadlista, String preciolista, int idsucursal, String detalle, String comprobante, String idempleado, String idcliente, String modulo) {
        String codigos[] = codigoslista.split(",");
        String cantidades[] = cantidadlista.split(",");
        String precios[] = preciolista.split(",");
        Connection con = this.getConexion();
        try {
            con.setAutoCommit(false);
            Statement st = con.createStatement();
            for (int i = 0; i < codigos.length; i++) {
                st.executeUpdate("update tbl_bodsubministro set stock=stock-" + cantidades[i] + " where id_producto='" + codigos[i] + "' and id_sucursal='" + idsucursal + "' and id_empleado='" + idempleado + "';");
                st.executeUpdate("INSERT INTO tbl_movsubmnistro(id_movsubministro, detalle, id_sucursal, fecha, id_usuarioe,id_usuarior, id_producto, cantidad, tipomovimento,costo,cantidadfinal,modulo,idbodsubministro,cantidad_stock)VALUES (" + this.idtblmovsubministro() + ",'" + detalle + " " + comprobante + "','" + idsucursal + "', CURRENT_DATE, '" + idempleado + "','" + idcliente + "', '" + codigos[i] + "','" + cantidades[i] + "',3,'" + precios[i] + "',(select stock from tbl_bodsubministro where id_producto='" + codigos[i] + "' and id_sucursal='" + idsucursal + "' and id_empleado='" + idempleado + "')::numeric,'" + modulo + "',(select idbodsubministro from tbl_bodsubministro where id_producto='" + codigos[i] + "' and id_sucursal='" + idsucursal + "' and id_empleado='" + idempleado + "')::integer,(select entradas from tbl_bodsubministro where id_producto='" + codigos[i] + "' and id_sucursal='" + idsucursal + "' and id_empleado='" + idempleado + "')::numeric);");
            }
            con.commit();
        } catch (Exception e) {
            e.printStackTrace();

            try {
                con.rollback();
            } catch (Exception se) {
                se.printStackTrace();
            }
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return this.consulta("");
    }

    public List kardexdeconsumoproducto(String codigoslista, String cantidadlista, String preciolista, int idsucursal, String detalle, String comprobante, String idempleado, String idcliente, String modulo, List sql) {
        String codigos[] = codigoslista.split(",");
        String cantidades[] = cantidadlista.split(",");
        String precios[] = preciolista.split(",");
        try {
            for (int i = 0; i < codigos.length; i++) {
                sql.add("update tbl_bodsubministro set stock=stock-" + cantidades[i] + " where id_producto='" + codigos[i] + "' and id_sucursal='" + idsucursal + "' and id_empleado='" + idempleado + "';");
                sql.add("INSERT INTO tbl_movsubmnistro(id_movsubministro, detalle, id_sucursal, fecha, id_usuarioe,id_usuarior, id_producto, cantidad, tipomovimento,costo,cantidadfinal,modulo,idbodsubministro,cantidad_stock)VALUES (" 
                        + this.idtblmovsubministro() + ",'" + detalle + " " + comprobante + "','" + idsucursal + "', CURRENT_DATE, '" + idempleado + "','" + idcliente + "', '" + codigos[i] + "','" + cantidades[i] + "',3,'" + precios[i] 
                        + "',(select stock from tbl_bodsubministro where id_producto='" + codigos[i] + "' and id_sucursal='" + idsucursal + "' and id_empleado='" + idempleado + "')::numeric,'" + modulo 
                        + "',(select idbodsubministro from tbl_bodsubministro where id_producto='" + codigos[i] + "' and id_sucursal='" + idsucursal + "' and id_empleado='" + idempleado + "')::integer,"
                        + "(select entradas from tbl_bodsubministro where id_producto='" + codigos[i] + "' and id_sucursal='" + idsucursal + "' and id_empleado='" + idempleado + "')::numeric);");
            }
        } catch (Exception e) {
            System.out.println("erorr al cargar los movimientos de los sumministros " + e.getMessage());
        }
        return sql;
    }

    public int idtblmovsubministro() {
        int id = 1;
        try {

            ResultSet rs = this.consulta("select max(id_movsubministro) from tbl_movsubmnistro");
            if (rs.next()) {
                id = (rs.getString("max") != null ? rs.getInt("max") : 1);
                id++;
                rs.close();
            }

        } catch (SQLException ex) {
            Logger.getLogger(OrdenTrabajo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return id;
    }

    public double getcantidadproducto(int sucursal, String codigo, String cantidades) {
        String contador = "";
        double cantidad = 0;
        try {
            ResultSet res = this.consulta("select saldo_cantidad from tbl_kardex_sucursal where id_producto='" + codigo + "' and id_kardex_sucursal = (select max(id_kardex_sucursal) from tbl_kardex_sucursal where id_sucursal='" + sucursal + "' and id_producto='" + codigo + "')");
            if (res.next()) {
                contador = res.getString("saldo_cantidad");
            }

            if (contador != "") {
                cantidad = Double.parseDouble(contador) - Double.parseDouble(cantidades);
            } else {
                cantidad = cantidad - Integer.parseInt(cantidades);
            }
            res.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cantidad;
    }

    public String asientocontable(int idsucursal, String fechatraspaso, String observacion, String totales, String parametros) {
        String num = "-1:-1:-1";
        try {
            ResultSet res = this.consulta("select setComprobanteDiario(" + idsucursal + ",'" + fechatraspaso + "', '" + observacion + "'," + totales + "," + parametros + ");");
            if (res.next()) {
                num = (res.getString(1) != null) ? res.getString(1) : "-1:-1:-1";
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    public ResultSet setactualizarconsumible(String idordentrabajo, String idmateriales, String gastosasicionales) {
        String codigos[] = idmateriales.split(",");
        String cantidades[] = gastosasicionales.split(",");
        Connection con = this.getConexion();
        try {
            con.setAutoCommit(false);
            Statement st = con.createStatement();
            for (int i = 0; i < codigos.length; i++) {
                st.executeUpdate("UPDATE tbl_orden_trabajo_material SET  gastoadicional='" + cantidades[i] + "' WHERE id_orden_trabajo='" + idordentrabajo + "' and id_material='" + codigos[i] + "';");
            }
            con.commit();
        } catch (Exception e) {
            e.printStackTrace();

            try {
                con.rollback();
            } catch (Exception se) {
                se.printStackTrace();
            }
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return this.consulta("");
    }

    public List setactualizarconsumible(String idordentrabajo, String idmateriales, String gastosasicionales, List sql) {
        String codigos[] = idmateriales.split(",");
        String cantidades[] = gastosasicionales.split(",");
        try {
            for (int i = 0; i < codigos.length; i++) {
                sql.add("UPDATE tbl_orden_trabajo_material SET  gastoadicional='" + cantidades[i] + "' WHERE id_orden_trabajo='" + idordentrabajo + "' and id_material='" + codigos[i] + "';");
            }
        } catch (Exception e) {
            System.out.println("error al cargar nuevos gastos adicionales");
        }
        return sql;
    }

    public ResultSet setinsertarsubministrosInfra(String idordentrabajo, String idmateriales, String gastosasicionales, String adicional) {
        String codigos[] = idmateriales.split(",");
        String cantidades[] = gastosasicionales.split(",");
        String cantidadesadicional[] = adicional.split(",");
        Connection con = this.getConexion();
        try {
            con.setAutoCommit(false);
            Statement st = con.createStatement();
            for (int i = 0; i < codigos.length; i++) {
                st.executeUpdate("INSERT INTO tbl_orden_trabajo_material( id_orden_trabajo, id_material, cantidad,gastoadicional)VALUES ('" + idordentrabajo + "', '" + codigos[i] + "', '" + cantidades[i] + "','" + cantidadesadicional[i] + "');");
            }
            con.commit();
        } catch (Exception e) {
            e.printStackTrace();

            try {
                con.rollback();
            } catch (Exception se) {
                se.printStackTrace();
            }
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return this.consulta("");
    }

    public ResultSet getStockSubministro(String idproducto, String idsucursal, String idempleado) {
        ResultSet rs = null;
        rs = this.consulta("select stock from tbl_bodsubministro where id_sucursal='" + idsucursal + "' and id_empleado='" + idempleado + "' and id_producto='" + idproducto + "'");
        return rs;
    }

    public ResultSet detalleordeninfra(String id) {
        return this.consulta("select distinct OTM.*, M.descripcion from tbl_orden_trabajo_material as OTM inner join vta_material as M on OTM.id_material = M.id_material where id_orden_trabajo=" + id );
    }

    ///
    public ResultSet SpliterUtilizado(String id) {
        return this.consulta("SELECT s.id_spliter,s.nombre_spliter,s.direccion,s.sucursal,s.sector,s.codigo_activo,u.puerto,u.id_orden_trabajo,s.tipo_spliter,s.numero_puertos FROM vta_spliter s\n"
                + "inner join tbl_spliter_utilizado u on u.id_spliter=s.id_spliter where u.id_orden_trabajo='" + id + "' order by u.id_spliter_utilizado asc");
    }

    public String[] OltUtilizado(String id) {
        String datos[] = new String[4];
        try {
            ResultSet rs = this.consulta("SELECT s.id_spliter,s.nombre_spliter,s.puerto_conectado,s.tipo_spliter FROM vta_spliter s"
                    + " where id_spliter=(select id_acoplado from tbl_spliter where id_spliter='" + id + "')");
            if (rs != null) {
                if (rs.next()) {
                    datos[0] = (rs.getString(1) != null ? rs.getString(1) : "");
                    datos[1] = (rs.getString(2) != null ? rs.getString(2) : "");
                    datos[2] = (rs.getString(3) != null ? rs.getString(3) : "");
                    datos[3] = (rs.getString(4) != null ? rs.getString(4) : "");
                }
            }
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        }
        return datos;
    }

    public String getNumOrdenAnterior(String id) {
        String r = "1";
        try {
            ResultSet rs = this.consulta("select tipo_trabajo from tbl_orden_trabajo where id_orden_trabajo='" + id + "'");
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

    public boolean PendientesOrden(String id, String capacidad_efectiva, String infoolt, String numero_hilos, String detalle_anterior) {
        List sql = new ArrayList();
        sql.add("UPDATE tbl_orden_trabajo SET capacidad_efectiva='" + capacidad_efectiva + "', estado='9',"
                + "info_olt='" + infoolt + "',numero_hilos='" + numero_hilos + "',detalle_anterior='" + detalle_anterior + "'"
                + " WHERE id_orden_trabajo=" + id);
        return this.transacciones(sql);
    }

    public boolean PendientesOrden(String id, String capacidad_efectiva, String infoolt, String numero_hilos, String detalle_anterior, String vel_carga, String vel_descarga) {
        List sql = new ArrayList();
        sql.add("UPDATE tbl_orden_trabajo SET capacidad_efectiva='" + capacidad_efectiva + "', estado='9',"
                + " info_olt='" + infoolt + "',numero_hilos='" + numero_hilos + "',detalle_anterior='" + detalle_anterior + "',"
                + " velocidad_carga='" + vel_carga + "',velocidad_descarga='" + vel_descarga + "'"
                + " WHERE id_orden_trabajo=" + id);
        return this.transacciones(sql);
    }

    public String insertar(String id_instalacion, int id_sucursal_sesion, String num_orden, String tipo_trabajo,
            String usuario_reporte, String fecha_cliente, String hora_cliente, String diagnostico_tecnico, int id_orden_secuencia, String tipo_servicio) {
        String usuario_cliente = "";
        if (fecha_cliente.compareTo("") != 0) {
            usuario_cliente = usuario_reporte;
        }
        fecha_cliente = fecha_cliente.compareTo("") != 0 ? "'" + fecha_cliente + "'" : "NULL";
        hora_cliente = hora_cliente.compareTo("") != 0 ? "'" + hora_cliente + "'" : "NULL";
        return this.insert("INSERT INTO tbl_orden_trabajo(id_instalacion, id_sucursal, num_orden, tipo_trabajo, usuario_reporte, fecha_reporte, hora_reporte, usuario_cliente, fecha_cliente, hora_cliente, diagnostico_tecnico, estado, tipo,id_orden_trabajo_secuencia,tipo_servicio) "
                + "VALUES(" + id_instalacion + ", " + id_sucursal_sesion + ", " + num_orden + ", '" + tipo_trabajo + "', '" + usuario_reporte + "', now()::date, now()::time, '" + usuario_cliente + "', " + fecha_cliente + ", " + hora_cliente + ", '" + diagnostico_tecnico + "', '1', 'c', '" + id_orden_secuencia + "', '" + tipo_servicio + "');");
        /*if(pk.compareTo("-1")!=0){
         this.ejecutar("update tbl_instalacion set estado_servicio='c' where id_instalacion="+id_instalacion);
         }
         return pk;*/
    }

    public boolean PendientesOrden(String id, String capacidad_efectiva, String vel_carga, String vel_descarga) {
        List sql = new ArrayList();
        sql.add("UPDATE tbl_orden_trabajo SET capacidad_efectiva='" + capacidad_efectiva + "', estado='9',"
                + " velocidad_carga='" + vel_carga + "',velocidad_descarga='" + vel_descarga + "'"
                + " WHERE id_orden_trabajo=" + id);
        return this.transacciones(sql);
    }

    public boolean PendientesOrden(String id, String capacidad_efectiva) {
        List sql = new ArrayList();
        sql.add("UPDATE tbl_orden_trabajo SET capacidad_efectiva='" + capacidad_efectiva + "', estado='9' WHERE id_orden_trabajo=" + id);
        return this.transacciones(sql);
    }

    public boolean UnirHojaRuta(String usuario, String id_empleado, String id_hoja_ruta, String id_orden_trabajo) {
        return this.ejecutar("update tbl_orden_trabajo set fecha_realizacion=now(), hora_realizacion=now(), estado='2',usuario_realizacion='" + usuario + "',id_empleado='" + id_empleado + "',id_hoja_ruta='" + id_hoja_ruta + "' where id_orden_trabajo='" + id_orden_trabajo + "'");
    }

    public String[][] CostosSector(String id_sector) {
        String costos[][] = {{"-1", "", "0", ""}, {"-1", "", "0", ""}, {"-1", "", "0", ""}, {"-1", "", "0", ""}, {"-1", "", "0", ""}};
        String rubros[][] = {{"-1", "", ""}, {"-1", "", ""}, {"-1", "", ""}};
        try {
            String pIva = "15";
            ResultSet rsConf = this.consulta("SELECT valor FROM tbl_configuracion where parametro='p_iva1'");
            if(rsConf.next()){
                pIva = rsConf.getString("valor")!=null ? rsConf.getString("valor") : "15";
            }
            
            ResultSet rs = this.consulta("select (costo_cambio_domicilio / (1+("+pIva+"/100::float)) )::numeric(8,2), (costo_cambio_domicilio_fibra / (1+("+pIva+"/100::float)) )::numeric(8,2), "
                    + "(costo_migracion_inalambrico / (1+("+pIva+"/100::float)) )::numeric(8,2), (costo_migracion_fibra / (1+("+pIva+"/100::float)) )::numeric(8,2), (costo_orden_no_procedente / (1+("+pIva+"/100::float)) )::numeric(8,2) "
                    + "from tbl_sector where id_sector='" + id_sector + "';");
            if (rs.next()) {
                costos[0][2] = rs.getString(1) != null ? rs.getString(1) : "0";
                costos[1][2] = rs.getString(2) != null ? rs.getString(2) : "0";
                costos[2][2] = rs.getString(3) != null ? rs.getString(3) : "0";
                costos[3][2] = rs.getString(4) != null ? rs.getString(4) : "0";
                costos[4][2] = rs.getString(5) != null ? rs.getString(5) : "0";
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ResultSet rs = this.consulta("select id_rubro,rubro,id_producto from tbl_rubro where id_rubro in (16,17,19) order by id_rubro asc");
            int i = 0;
            while (rs.next()) {
                rubros[i][0] = rs.getString(1) != null ? rs.getString(1) : "-1";
                rubros[i][1] = rs.getString(2) != null ? rs.getString(2) : "";
                rubros[i][2] = rs.getString(3) != null ? rs.getString(3) : "";
                i++;
            }
            ///costos de cambios de domicilio
            costos[0][0] = rubros[0][0];
            costos[0][1] = rubros[0][1];
            costos[0][3] = rubros[0][2];
            costos[1][0] = rubros[0][0];
            costos[1][1] = rubros[0][1];
            costos[1][3] = rubros[0][2];
            ///costos de cambios de domicilio
            costos[4][0] = rubros[1][0];
            costos[4][1] = rubros[1][1];
            costos[4][3] = rubros[1][2];
            ///costos de cambios de domicilio
            costos[2][0] = rubros[2][0];
            costos[2][1] = rubros[2][1];
            costos[2][3] = rubros[2][2];
            costos[3][0] = rubros[2][0];
            costos[3][1] = rubros[2][1];
            costos[3][3] = rubros[2][2];

            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return costos;
    }

    public boolean getCambiosDomicilio(String id_instalacion, String tipo_trabajo, String id_orden_trabajo) {
        int i = 0;
        try {
            ResultSet rs = this.consulta("select count(*) from tbl_orden_trabajo as o "
                    + " inner join vta_instalacion_parametros as ip on ip.id_instalacion=o.id_instalacion "
                    + " where o.tipo_trabajo in (" + tipo_trabajo + ") and o.estado in ('9') and o.ot_procedente=true and o.id_instalacion='" + id_instalacion + "' and o.id_orden_trabajo<>'" + id_orden_trabajo + "' "
                    + " and o.fecha_reporte between ip.fecha_inicio and ip.fecha_fin;");
            if (rs.next()) {
                i = rs.getString(1) != null ? rs.getInt(1) : 0;
                rs.close();
            }
            if (i <= 0) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public boolean getCambiosDomicilio(String id_instalacion, String tipo_trabajo) {
        int i = 0;
        try {
            ResultSet rs = this.consulta("select count(*) from tbl_orden_trabajo as o "
                    + " inner join vta_instalacion_parametros as ip on ip.id_instalacion=o.id_instalacion "
                    + " where o.tipo_trabajo in (" + tipo_trabajo + ") and o.estado in ('9') and o.ot_procedente=true and o.id_instalacion='" + id_instalacion + "' "
                    + " and o.fecha_reporte between ip.fecha_inicio and ip.fecha_fin;");
            if (rs.next()) {
                i = rs.getString(1) != null ? rs.getInt(1) : 0;
                rs.close();
            }
            if (i <= 0) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public boolean getCambiosDomicilio(String id_instalacion) {
        int i = 0;
        try {
            ResultSet rs = this.consulta("select count(*) from tbl_orden_trabajo as o "
                    + " inner join vta_instalacion_parametros as ip on ip.id_instalacion=o.id_instalacion "
                    + " where o.tipo_trabajo='2' and o.estado in ('9') and o.ot_procedente=true and o.id_instalacion='" + id_instalacion + "' "
                    + " and o.fecha_reporte between ip.fecha_inicio and ip.fecha_fin;");
            if (rs.next()) {
                i = rs.getString(1) != null ? rs.getInt(1) : 0;
                rs.close();
            }
            if (i <= 0) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public String[] getNuevoCambioPlan(String id_orden_trabajo) {
        String plan[] = {"", ""};
        try {
            ResultSet rs = this.consulta("select tipo_instalacion,id_plan from tbl_instalacion_certificado where id_instalacion_certificado=\n"
                    + "(select o.id_instalacion_certificado from tbl_orden_trabajo as o where o.id_orden_trabajo='" + id_orden_trabajo + "') and id_certificados_isp not in (1,2,3);");
            if (rs.next()) {
                plan[0] = rs.getString(1) != null ? rs.getString(1) : "";
                plan[1] = rs.getString(2) != null ? rs.getString(2) : "";
                rs.close();
            }
            return plan;
        } catch (Exception e) {
            e.printStackTrace();
            return plan;
        }
    }

    public String getInstalacionCertificado(String id_orden_trabajo) {
        String r = "-1";
        try {
            ResultSet rs = this.consulta("select id_instalacion_certificado from tbl_orden_trabajo  where id_orden_trabajo='" + id_orden_trabajo + "';");
            if (rs.next()) {
                r = rs.getString(1) != null ? rs.getString(1) : "-1";
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return r;
    }

    public boolean getMgracionGratuita(String id_orden_trabajo) {
        boolean ok = false;
        try {
            ResultSet rs = this.consulta("select p.migracion_fibra from tbl_orden_trabajo as o "
                    + " inner join tbl_instalacion_certificado as ic on ic.id_instalacion_certificado=o.id_instalacion_certificado "
                    + " inner join tbl_promocion as p on p.id_promocion=ic.id_promocion "
                    + " where id_orden_trabajo='" + id_orden_trabajo + "';");
            if (rs.next()) {
                ok = rs.getString(1) != null ? rs.getBoolean(1) : false;
                rs.close();
            }
            return ok;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public ResultSet getnombresEquipos() {
        return this.consulta("select distinct descripcion2 from tbl_activo where eliminado=false;");
    }

    public ResultSet getEquipoOrden(String id) {
        return this.consulta("select * from tbl_activo_verificar where activo='" + id + "';");
    }

    public String setEquipoOrden(String id, String grupo, String estado) {
        return this.insert("insert into tbl_activo_verificar (activo,grupo,estado)values('" + id + "','" + grupo + "','" + estado + "')");
    }

    public boolean EliminarEquipoOrden(String id) {
        return this.ejecutar("delete from tbl_activo_verificar where activo='" + id + "';");
    }

    public boolean UpdateEquipoOrden(String id, String grupo, String estado) {
        return this.ejecutar("update tbl_activo_verificar set activo='" + id + "',grupo='" + grupo + "',estado='" + estado + "' where activo='" + id + "';");
    }

    public boolean estaDuplicadoEquipo(String id, String grupo) {
        ResultSet res = this.consulta("SELECT * FROM tbl_activo_verificar where activo<>'" + id + "' and grupo='" + grupo + "';");;
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

    public long getIdGuiaRemision(String id) {
        long r = -1;
        try {
            ResultSet rs = this.consulta("select id_guia_remision from tbl_orden_trabajo where id_orden_trabajo=" + id);
            if (rs.next()) {
                r = rs.getString(1) != null ? rs.getLong(1) : -1;
                rs.close();
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return r;
    }

    public ResultSet getOrdenTrabajoYGuiaRemision(String id) {
        return this.consulta("SELECT O.*, E.dni, E.tipo_dni, E.nombre || ' ' || E.apellido as responsable "
                + "FROM (tbl_orden_trabajo as O inner join tbl_empleado as E on E.id_empleado=O.id_empleado) "
                + "left outer join tbl_guia_remision as G on O.id_guia_remision=G.id_guia_remision where O.id_orden_trabajo=" + id);
    }

    public ResultSet getOrdenTrabajoCorresponsables(String id) {
        return this.consulta("SELECT O.*, E.dni, E.tipo_dni, E.nombre || ' ' || E.apellido as responsable "
                + "FROM (tbl_orden_trabajo_integrantes as O inner join tbl_empleado as E on E.id_empleado=O.id_empleado) "
                + "where O.id_orden_trabajo=" + id);
    }

    public ResultSet getNodoItinerante(String idOrdenTrabajo) {
        return this.consulta("select N.* from tbl_nodo as N inner join tbl_orden_trabajo as O on O.id_nodo=N.id_nodo where O.id_orden_trabajo=" + idOrdenTrabajo);
    }

    public String[] getAnticipoOrdenTrabajo(String id_orden_trabajo) {
        String resultado[] = {"", "", ""};
        try {
            double total = 0;
            ResultSet rs = this.consulta("select id_cliente_anticipo,monto,saldo from tbl_cliente_anticipo as ca  "
                    + " inner join tbl_orden_trabajo as o on o.id_instalacion_certificado=ca.id_instalacion "
                    + " where o.id_orden_trabajo='" + id_orden_trabajo + "' and saldo>0;");
            while (rs.next()) {
                resultado[0] = (rs.getString("id_cliente_anticipo") != null ? rs.getString("id_cliente_anticipo") : "") + ",";
                resultado[2] = (rs.getString("saldo") != null ? rs.getString("saldo") : "") + ";";
                total += (rs.getString("saldo") != null ? rs.getDouble("saldo") : 0);
            }
            if (resultado[0].trim().compareTo("") != 0) {
                resultado[0] = resultado[0].substring(0, resultado[0].length() - 1);
                resultado[2] = resultado[2].substring(0, resultado[2].length() - 1);
                resultado[1] = "" + total;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultado;
    }

    public String getNumHojaRuta(String id_hoja_ruta) {
        String r = "";
        try {
            ResultSet rs = this.consulta("select (id_sucursal ||'-'|| num_hoja_ruta)as numero_hoja from vta_hoja_ruta where id_hoja_ruta =" + (id_hoja_ruta.compareTo("")!=0 ? id_hoja_ruta : "-1") + ";");
            if (rs != null) {
                if (rs.next()) {
                    r = rs.getString(1) != null ? rs.getString(1) : "";
                    rs.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return r;
    }

    
    public String getSecOrdenTrabajoSolicitud() 
    {
        String r = "";
        try {
            ResultSet rs = this.consulta("select case when max(num_solicitud)>0 then max(num_solicitud) + 1 else 1 end from tbl_orden_trabajo_solicitud");
            if (rs != null) {
                if (rs.next()) {
                    r = rs.getString(1) != null ? rs.getString(1) : "";
                    rs.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return r;
    }
    
    public String ordenTrabajoConSolicitud(String idOT) 
    {
        String idOTS = "-1";
        try {
            ResultSet res = this.consulta("SELECT id_orden_trabajo_solicitud FROM tbl_orden_trabajo_solicitud where id_orden_trabajo = " + idOT);
            if(res.next()) {
                idOTS = res.getString("id_orden_trabajo_solicitud")!=null ? res.getString("id_orden_trabajo_solicitud") : "-1";
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return idOTS;
    }
    
    public String setOrdenTrabajoSolicitudAdd(String numSolicitud, String id_instalacion, String id_orden_trabajo, String id_empleado_solicitud, String id_empleado_dirigido_a, String equipos)
    {
        return this.insert("insert into tbl_orden_trabajo_solicitud(num_solicitud, id_instalacion, id_orden_trabajo, id_empleado_solicitud, id_empleado_dirigido_a, equipos) "
                + "values("+numSolicitud+", "+id_instalacion+", "+id_orden_trabajo+", "+id_empleado_solicitud+", "+id_empleado_dirigido_a+", '"+equipos+"')");
    }
    
    public boolean setOrdenTrabajoSolicitudAddActualizacion(String idOTSolicitud, String numSolicitud, String id_empleado_solicitud, String id_empleado_dirigido_a, String equipos)
    {
        return this.ejecutar("update tbl_orden_trabajo_solicitud set estado='g', num_solicitud="+numSolicitud+", id_empleado_solicitud="+id_empleado_solicitud+", id_empleado_dirigido_a="
                +id_empleado_dirigido_a+", equipos='"+equipos+"' where id_orden_trabajo_solicitud = "+idOTSolicitud);
    }

    public ResultSet getOrdenTrabajoSolicitud(String id) {
        return this.consulta("select S.*, E.nombre || ' ' || E.apellido as solicitante, (select nombre || ' ' || apellido as dirigido_a from tbl_empleado where id_empleado=S.id_empleado_dirigido_a) " +
                ", O.id_instalacion, O.id_sucursal, O.tipo_trabajo, O.id_empleado, I.id_cliente, I.num_instalacion, I.direccion_instalacion, C.ruc, C.razon_social " + 
                "from tbl_orden_trabajo_solicitud as S inner join tbl_empleado as E on S.id_empleado_solicitud = E.id_empleado " +
                "inner join tbl_orden_trabajo as O on S.id_orden_trabajo = O.id_orden_trabajo " + 
                "inner join tbl_instalacion as I on I.id_instalacion = O.id_instalacion " +
                "inner join tbl_cliente as C on I.id_cliente = C.id_cliente " + 
                "where id_orden_trabajo_solicitud=" + id);
    }
    
    public ResultSet getEquiposOrdenTrabajoSolicitud(String id) {
        return this.consulta("select id_activo, codigo_activo, descripcion from tbl_activo where codigo_activo in(select trim( regexp_split_to_table(equipos, ',') ) from tbl_orden_trabajo_solicitud where id_orden_trabajo_solicitud="+id+");");
    }
    
    public boolean setOrdenTrabajoSolicitudAceptacion(String idS, String idP)
    {
        return this.ejecutar("update tbl_orden_trabajo_solicitud set estado='a', id_personalizacion="+idP+" where id_orden_trabajo_solicitud=" + idS);
    }
    
    public boolean setOrdenTrabajoSolicitudRechazo(String idS, String motivo)
    {
        return this.ejecutar("update tbl_orden_trabajo_solicitud set estado='r', motivo_rechazo = case when motivo_rechazo is null then '"+motivo+"' else motivo_rechazo || '"+motivo+". ' end where id_orden_trabajo_solicitud=" + idS);
    }
    
    
    //  grupos de equipos permitidos en instalaciones
    
    public String insertarGrupo(String grupo, String num_equipos_instalacion) 
    {
        return this.insert("INSERT INTO tbl_grupo_equipo_ot(grupo, num_equipos_instalacion) VALUES('" + grupo + "', " + num_equipos_instalacion + ")");
    }
    
    public boolean actualizarGrupo(String id, String grupo, String num_equipos_instalacion) 
    {
        return this.ejecutar("update tbl_grupo_equipo_ot set grupo='" + grupo + "', num_equipos_instalacion=" + num_equipos_instalacion + " where id_grupo_equipo_ot=" + id);
    }
    
    public ResultSet getGrupoEquipo(String id) 
    {
        return this.consulta("select * from tbl_grupo_equipo_ot where id_grupo_equipo_ot=" + id);
    }
    
    public ResultSet getGrupoEquipos() 
    {
//        return this.consulta("select * from vta_grupo_equipos");
        return this.consulta("select id_grupo_equipo_ot, grupo from tbl_grupo_equipo_ot");
    }
        
}
