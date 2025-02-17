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
public class Contrato extends DataBase {

    public Contrato(String m, int p, String db, String u, String c) {
        super(m, p, db, u, c);
    }

    public ResultSet getContratoCliente(String idCliente) {
        return this.consulta("SELECT * FROM vta_contrato where id_cliente=" + idCliente + ";");
    }

    public ResultSet getContrato(String id_contrato) {
        return this.consulta("SELECT * FROM vta_contrato where id_contrato=" + id_contrato + ";");
    }

    public String getNumContratoSec(int idSucursal) {
        String numCont = "1";
        try {
            ResultSet rs = this.consulta("select case when max(num_contrato)>0 then max(num_contrato)+1 else 1 end "
                + "from tbl_contrato where id_sucursal = " + idSucursal);
            if(rs.next()){
                numCont = rs.getString(1)!=null ? rs.getString(1) : "1";
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return numCont;
    }
    
    public String getNumContratosJSON() {
        ResultSet rs = this.consulta("select S.id_sucursal, case when max(C.num_contrato)>0 then max(C.num_contrato)+1 else 1 end "
                + "from tbl_sucursal as S left outer join tbl_contrato as C on S.id_sucursal=C.id_sucursal group by S.id_sucursal;");
        String tbl = this.getJSON(rs);
        try {
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tbl;
    }

    public String insertar(String num_contrato, String id_cliente, String id_sucursal, String fecha_contrato, String fecha_termino,
            String ruc_representante, String representante, String contrato, String autorizacion) {
        return this.insert("INSERT INTO tbl_contrato(num_contrato, id_cliente, id_sucursal, fecha_contrato, fecha_termino, ruc_representante, representante, contrato, autorizacion) "
                + "VALUES(" + num_contrato + ", " + id_cliente + ", " + id_sucursal + ", '" + fecha_contrato + "', '" + fecha_termino + "', '" + ruc_representante + "', '" + representante + "', '" + contrato + "', '" + autorizacion + "');");
    }

    public String insertar(String num_contrato, String id_cliente, String id_sucursal, String fecha_contrato, String fecha_termino,
            String ruc_representante, String representante, String contrato, String autorizacion, String id_instalacion) {
        id_instalacion = (id_instalacion.trim().compareTo("") == 0 ? "NULL" : "'" + id_instalacion + "'");
        return this.insert("INSERT INTO tbl_contrato(num_contrato, id_cliente, id_sucursal, fecha_contrato, fecha_termino, ruc_representante, representante, contrato, autorizacion,id_instalacion) "
                + "VALUES(" + num_contrato + ", " + id_cliente + ", " + id_sucursal + ", '" + fecha_contrato + "'::date, '" + fecha_termino + "', '" + ruc_representante + "', '" + representante + "', '" + contrato + "', '" + autorizacion + "', " + id_instalacion + ");");
    }
    
    public boolean actualizar(String id, String num_contrato, String id_cliente, String id_sucursal, String fecha_contrato, String fecha_termino,
            String representante, String ruc_representante, String contrato, String autorizacion) {
        return this.ejecutar("UPDATE tbl_contrato SET num_contrato=" + num_contrato + ", id_cliente=" + id_cliente + ", id_sucursal=" + id_sucursal
                + ", fecha_contrato='" + fecha_contrato + "', fecha_termino='" + fecha_termino + "', ruc_representante='" + ruc_representante
                + "', representante='" + representante + "', contrato='" + contrato + "', autorizacion='" + autorizacion + "' WHERE id_contrato=" + id + ";");
    }

    public boolean actualizar(String id, String num_contrato, String id_cliente, String id_sucursal, String fecha_contrato, String fecha_termino,
            String representante, String ruc_representante, String contrato, String autorizacion, String id_instalacion) {
        return this.ejecutar("UPDATE tbl_contrato SET num_contrato=" + num_contrato + ", id_cliente=" + id_cliente + ", id_sucursal=" + id_sucursal
                + ", fecha_contrato='" + fecha_contrato + "', fecha_termino='" + fecha_termino + "', ruc_representante='" + ruc_representante
                + "', representante='" + representante + "', contrato='" + contrato + "', autorizacion='" + autorizacion + "', id_instalacion='" + id_instalacion + "' WHERE id_contrato=" + id + ";");
    }

    public boolean anular(String id, String anular) {
        return this.ejecutar("UPDATE tbl_contrato SET anulado="+anular+" WHERE id_contrato=" + id);
    }

    public String getNumeroContrato(String id_cliente) {
        String id_contrato = "-1";
        ResultSet rs = this.consulta("SELECT id_contrato FROM vta_contrato where id_cliente=" + id_cliente + ";");

        try {
            if (rs.next()) {
                id_contrato = (rs.getString("id_contrato") != null ? rs.getString("id_contrato") : "-1");
                rs.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return id_contrato;
    }

    ///////////////////
    public ResultSet getContratoInstalacion(String id_instalacion) {
        return this.consulta("SELECT * FROM vta_contrato where id_instalacion=" + id_instalacion + ";");
    }

    public String getNumContrato(String id_instalacion) {
        String num_contrato = "-1";
        ResultSet rs = this.consulta("SELECT num_contrato FROM vta_contrato where id_instalacion=" + id_instalacion + ";");

        try {
            if (rs.next()) {
                num_contrato = (rs.getString("num_contrato") != null ? rs.getString("num_contrato") : "-1");
                rs.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return num_contrato;
    }

    public String getNumeContrato(String id_contrato) {
        String num_contrato = "-1";
        ResultSet rs = this.consulta("SELECT num_contrato FROM vta_contrato where id_contrato=" + id_contrato + ";");

        try {
            if (rs.next()) {
                num_contrato = (rs.getString("num_contrato") != null ? rs.getString("num_contrato") : "-1");
                rs.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return num_contrato;
    }

    public String getFechaContrato(String id_contrato) {
        String num_contrato = "";
        ResultSet rs = this.consulta("SELECT fecha_contrato FROM vta_contrato where id_contrato=" + id_contrato + ";");

        try {
            if (rs.next()) {
                num_contrato = (rs.getString("fecha_contrato") != null ? rs.getString("fecha_contrato") : "");
                rs.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return num_contrato;
    }
    
    public boolean contratoAntiguo(String idInstalacion) {
        boolean antiguo = false;
        ResultSet rs = this.consulta("select I.fecha_instalacion < '2016-12-31' and C.fecha_contrato<'2016-12-31' \n" +
            "from tbl_instalacion as I left join tbl_contrato as C on I.id_contrato = C.id_contrato \n" +
            "where I.id_instalacion=" + idInstalacion );
        try {
            if (rs.next()) {
                antiguo = (rs.getString(1) != null ? rs.getBoolean(1) : false);
                rs.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return antiguo;
    }

    //////////////////////////  BENEFICIOS EN RENOVACIONES  ////////////////////////////////////////////////////////////////////////////////
    
    public ResultSet getBeneficiosRenovacion() {
        return this.consulta("SELECT id_contrato_beneficio, beneficio, costo_beneficio FROM tbl_contrato_beneficio where estado order by id_contrato_beneficio");
    }
    
    public String[] renovar(String num_contrato, String id_cliente, String id_sucursal, String fecha_contrato, String ruc_representante, String representante, 
            String id_instalacion, String id_certificados_isp, String fecha_creada, String usuario_creado, String observaciones) {
        
        String ids[] = new String[]{"-1","-1"};
        
        ids[0] = this.insert("INSERT INTO tbl_contrato(num_contrato, id_cliente, id_sucursal, fecha_contrato, fecha_termino, ruc_representante, representante, id_instalacion) "
                + "VALUES(" + num_contrato + ", " + id_cliente + ", " + id_sucursal + ", '" + fecha_contrato + "'::date, ('" + fecha_contrato + "'::date + '1 year'::interval)::date, '" + ruc_representante + "', '" + representante + "', " + id_instalacion + ");");
        if (ids[0].compareTo("-1") != 0) {
            
            ids[1] = this.insert("insert into tbl_instalacion_certificado(id_instalacion, id_certificados_isp, fecha_creada, usuario_creado, observaciones, id_plan, tipo_instalacion, "
                + "pais, id_provincia, id_canton, id_parroquia, id_sector, direccion, cliente_actualizado, instalacion_actualizado, modalidad_pago, id_provincia_cliente, id_canto_cliente, direccion_cliente) "
                + "select '" + id_instalacion + "', " + id_certificados_isp + ", '" + fecha_creada + "', '" + usuario_creado + "','" + observaciones + "', I.id_plan_actual, I.tipo_instalacion, "
                + "'ECUADOR', I.id_provincia, I.id_ciudad, I.id_parroquia, I.id_sector, I.direccion_instalacion, true, true, I.convenio_pago, C.id_provincia, C.id_ciudad, C.direccion "
                + "from tbl_instalacion as I inner join tbl_cliente as C on I.id_cliente=C.id_cliente where id_instalacion = " + id_instalacion);
            if(ids[1].compareTo("-1")!=0){

                if( !this.ejecutar("update tbl_instalacion set costo_instalacion_facturado=89, tiempo_permanencia_contrato=0, renovacion_tiempo_permanencia=(select valor::int from tbl_configuracion where parametro='renovar_contrato_permanencia'), "
                        + "renovacion_costo_facturado=(select valor::int from tbl_configuracion where parametro='renovar_contrato_costo') where id_instalacion=" + id_instalacion)) {
                    
                    this.ejecutar("delete from tbl_contrato where id_contrato = " + ids[0]);
                    this.ejecutar("delete from tbl_instalacion_certificado where id_instalacion_certificado = " + ids[1]);
                    ids[0] = "-1";
                    ids[1] = "-1";
                }
                
            } else {
                this.ejecutar("delete from tbl_contrato where id_contrato = " + ids[0]);
                ids[0] = "-1";
            }
        }
        return ids;
    }
    
    public boolean renovacionCertificado(String id_instalacion) {
        
        return this.ejecutar("update tbl_instalacion set renovacion_tiempo_permanencia=(select valor::int from tbl_configuracion where parametro='renovar_contrato_permanencia'), "
                    + "renovacion_costo_facturado=(select valor::int from tbl_configuracion where parametro='renovar_contrato_costo') where id_instalacion=" + id_instalacion);
               
    }
    
}
