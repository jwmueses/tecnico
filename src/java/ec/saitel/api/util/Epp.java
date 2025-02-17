/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.saitel.api.util;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author PC-ON
 */
public class Epp extends DataBase {

    public Epp(String m, int p, String db, String u, String c) {
        super(m, p, db, u, c);
    }

    public ResultSet getFichaEpp() {
        return this.consulta("select * from vta_ficha_epp;");
    }

    public ResultSet getFichaEpp(String id) {
        return this.consulta("select * from vta_ficha_epp where id_ficha_epp='" + id + "';");
    }

    public ResultSet getFichaRevisionEpp(String id) {
        return this.consulta("select * from vta_ficha_revision_epp where id_ficha_revision_epp='" + id + "';");
    }

    public String getBodegaId(String id) {
        String id_bodega = "-1";
        try {
            ResultSet rs = this.consulta("select id_bodega from tbl_bodega where id_responsable='" + id + "';");
            if (rs.next()) {
                id_bodega = (rs.getString("id_bodega") != null ? rs.getString("id_bodega") : "-1");
            }
        } catch (Exception e) {
            System.out.println("error al obtener la bodega");
        }
        return id_bodega;
    }

    public String getNumeroFichaEpp(String id_sucursal) {
        String numero = "1";
        try {
            ResultSet rs = this.consulta("select case when max(num_ficha_epp)is null then 1 else max(num_ficha_epp)+1 end from tbl_ficha_epp where id_sucursal='" + id_sucursal + "';");
            if (rs.next()) {
                numero = (rs.getString(1) != null ? rs.getString(1) : "1");
            }
        } catch (Exception e) {
            System.out.println("error al obtener la bodega");
        }
        return numero;
    }

    public int getCountFichaEpp(String alias, String id_sucursal) {
        int numero = 0;
        try {
            ResultSet rs = this.consulta("select count(*) from tbl_ficha_epp where usuario='" + alias + "' and id_sucursal='" + id_sucursal + "';");
            if (rs.next()) {
                numero = (rs.getString(1) != null ? rs.getInt(1) : 0);
            }
        } catch (Exception e) {
            System.out.println("error al obtener la bodega");
        }
        return numero;
    }

    public String getFichaHabilitadaEpp(String alias, String id_sucursal) {
        String numero = "";
        try {
            ResultSet rs = this.consulta("select id_ficha_epp from tbl_ficha_epp where usuario='" + alias + "' and id_sucursal='" + id_sucursal + "' and actual=true;");
            if (rs.next()) {
                numero = (rs.getString(1) != null ? rs.getString(1) : "");
            }
        } catch (Exception e) {
            System.out.println("error al obtener la bodega");
        }
        return numero;
    }

    public ResultSet getBodega(String id) {
        return this.consulta("SELECT A.id_activo,A.codigo_activo, A.descripcion FROM tbl_bodega_activo as BA  "
                + " inner join tbl_activo as A on BA.id_activo=A.id_activo  "
                + " where BA.id_bodega='" + id + "' and A.eliminado=false  "
                + " and A.id_activo not in (select id_activo from tbl_activo_perdida)  "
                + " order by A.descripcion asc;");
    }

    public ResultSet getBodegaFichaEpp(String id_bodega, String id) {
        return this.consulta("SELECT A.id_activo,A.codigo_activo, A.descripcion,'s' FROM tbl_ficha_epp_detalle as FE "
                + " inner join tbl_activo as A on A.id_activo=FE.id_activo "
                + " where FE.id_ficha_epp='" + id + "' and FE.tipo='a' "
                + " union "
                + " SELECT A.id_activo,A.codigo_activo, A.descripcion,'n' FROM tbl_bodega_activo as BA "
                + " inner join tbl_activo as A on BA.id_activo=A.id_activo "
                + " where BA.id_bodega='" + id_bodega + "' and A.eliminado=false  "
                + " and A.id_activo not in (select id_activo from tbl_activo_perdida) "
                + " and A.id_activo not in (select id_activo from tbl_ficha_epp_detalle where id_ficha_epp='" + id + "' and tipo='a') "
                + " order by descripcion asc;");
    }

    public ResultSet getSubministro(String id) {
        return this.consulta("select b.id_producto,p.codigo,p.descripcion from tbl_bodsubministro as b "
                + " inner join vta_producto as p on p.id_producto=b.id_producto "
                + " where b.id_empleado='" + id + "' order by p.descripcion asc;");
    }

    public ResultSet getSubministroFichaEpp(String id_empleado, String id) {
        return this.consulta("select b.id_activo,p.codigo,p.descripcion,'s' from tbl_ficha_epp_detalle as b "
                + " inner join vta_producto as p on p.id_producto=b.id_activo "
                + " where id_ficha_epp='" + id + "' and b.tipo='s' "
                + " union "
                + " select b.id_producto,p.codigo,p.descripcion,'n' from tbl_bodsubministro as b "
                + " inner join vta_producto as p on p.id_producto=b.id_producto "
                + " where b.id_empleado='" + id_empleado + "' and "
                + " p.id_producto not in (select id_activo from tbl_ficha_epp_detalle where id_ficha_epp='" + id + "' and tipo='s') "
                + " order by descripcion asc;");
    }

    public ResultSet getDatosRevisionEpp(String alias) {
        return this.consulta("SELECT FE.id_ficha_epp_detalle,A.codigo_activo, A.descripcion,'1','1','b',false,'' FROM tbl_ficha_epp_detalle as FE "
                + " inner join tbl_ficha_epp as F on F.id_ficha_epp=FE.id_ficha_epp "
                + " inner join tbl_activo as A on A.id_activo=FE.id_activo "
                + " where F.usuario='" + alias + "' and FE.tipo='a' "
                + " union "
                + " select b.id_ficha_epp_detalle,p.codigo,p.descripcion,BS.stock,BS.stock,'b',false,'' from tbl_ficha_epp_detalle as b "
                + " inner join vta_ficha_epp as F on F.id_ficha_epp=b.id_ficha_epp "
                + " inner join tbl_producto as p on p.id_producto=b.id_activo "
                + " inner join tbl_bodsubministro as BS on (BS.id_producto=b.id_activo and BS.id_empleado=F.id_empleado) "
                + " where F.usuario='" + alias + "'  and b.tipo='s' "
                + " order by descripcion asc;");
    }

    public ResultSet getDatosRevisionEppId(String id) {
        return this.consulta("select FRED.id_ficha_epp_detalle,A.codigo_activo, A.descripcion,FRED.cantidad_ingresada,FRED.cantidad_revisada,FRED.estadoepp,FRED.estado_revisado,FRED.observacionepp from tbl_ficha_revision_epp_detalle as FRED "
                + " inner join tbl_ficha_epp_detalle as FED on FED.id_ficha_epp_detalle=FRED.id_ficha_epp_detalle "
                + " inner join tbl_ficha_revision_epp as FRE on FRE.id_ficha_revision_epp=FRED.id_ficha_revision_epp "
                + " inner join tbl_activo as A on A.id_activo=FED.id_activo "
                + " where FRE.id_ficha_revision_epp='" + id + "' and FED.tipo='a' "
                + " union "
                + " select FRED.id_ficha_epp_detalle,P.codigo, P.descripcion,FRED.cantidad_ingresada,FRED.cantidad_revisada,FRED.estadoepp,FRED.estado_revisado,FRED.observacionepp from tbl_ficha_revision_epp_detalle as FRED "
                + " inner join tbl_ficha_epp_detalle as FED on FED.id_ficha_epp_detalle=FRED.id_ficha_epp_detalle "
                + " inner join tbl_ficha_revision_epp as FRE on FRE.id_ficha_revision_epp=FRED.id_ficha_revision_epp "
                + " inner join tbl_producto as P on P.id_producto=FED.id_activo "
                + " where FRE.id_ficha_revision_epp='" + id + "' and FED.tipo='s' "
                + " order by descripcion asc;");
    }

    public String getBodegahtml(String id_empleado, String id) {
        StringBuffer html = new StringBuffer();
        ResultSet rs = null;
        String id_bodega = this.getBodegaId(id_empleado);
        try {
            if (id.compareTo("-1") == 0) {
                html.append("<table>");
                html.append("<tr><td align='center' colspan='4'> ACTIVOS </td></tr>");
                html.append("<tr><td>CODIGO</td><td>DESCRIPCION</td><td>REVISAR</td></tr>");
                rs = this.getBodega(id_bodega);
                int i = 0;
                while (rs.next()) {
                    String id_activo = (rs.getString(1) != null ? rs.getString(1) : "-1");
                    String mac = (rs.getString(2) != null ? rs.getString(2) : "");
                    String detalle = (rs.getString(3) != null ? rs.getString(3) : "");
                    html.append("<tr><td><input type='hidden' id='id_activo" + i + "' name='id_activo" + i + "' value='" + id_activo + "' /><input type='hidden' id='tipo" + i + "' name='tipo" + i + "' value='a' />" + mac + "</td><td>" + detalle + "</td><td><input type='checkbox' id='revisar" + i + "' name='revisar" + i + "' value='true' /></td></tr>");
                    i++;
                }
                html.append("<tr><td colspan='4'>&nbsp;</td></tr>");
                html.append("<tr><td colspan='4'> SUMINISTROS </td></tr>");
                html.append("<tr><td colspan='4'>&nbsp;</td></tr>");
                rs = this.getSubministro(id_empleado);
                while (rs.next()) {
                    String id_activo = (rs.getString(1) != null ? rs.getString(1) : "-1");
                    String mac = (rs.getString(2) != null ? rs.getString(2) : "");
                    String detalle = (rs.getString(3) != null ? rs.getString(3) : "");
                    html.append("<tr><td><input type='hidden' id='id_activo" + i + "' name='id_activo" + i + "' value='" + id_activo + "' /><input type='hidden' id='tipo" + i + "' name='tipo" + i + "' value='s' />" + mac + "</td><td>" + detalle + "</td><td><input type='checkbox' id='revisar" + i + "' name='revisar" + i + "' value='true' /></td></tr>");
                    i++;
                }
                html.append("<input type='hidden' id='items' name='items' value='" + i + "' />");
                html.append("</table>");
            } else {
                html.append("<table>");
                html.append("<tr><td align='center' colspan='4'> ACTIVOS </td></tr>");
                html.append("<tr><td>CODIGO</td><td>DESCRIPCION</td><td>REVISAR</td></tr>");
                rs = this.getBodegaFichaEpp(id_bodega, id);
                int i = 0;
                while (rs.next()) {
                    String id_activo = (rs.getString(1) != null ? rs.getString(1) : "-1");
                    String mac = (rs.getString(2) != null ? rs.getString(2) : "");
                    String detalle = (rs.getString(3) != null ? rs.getString(3) : "");
                    String cheked = (rs.getString(4) != null ? rs.getString(4) : "n");
                    html.append("<tr><td><input type='hidden' id='id_activo" + i + "' name='id_activo" + i + "' value='" + id_activo + "' /><input type='hidden' id='tipo" + i + "' name='tipo" + i + "' value='a' />" + mac + "</td><td>" + detalle + "</td><td><input type='checkbox' id='revisar" + i + "' name='revisar" + i + "' value='true' " + (cheked.compareTo("s") == 0 ? "checked" : "") + " /></td></tr>");
                    i++;
                }
                html.append("<tr><td colspan='4'>&nbsp;</td></tr>");
                html.append("<tr><td colspan='4'> SUMINISTROS </td></tr>");
                html.append("<tr><td colspan='4'>&nbsp;</td></tr>");
                rs = this.getSubministroFichaEpp(id_empleado, id);
                while (rs.next()) {
                    String id_activo = (rs.getString(1) != null ? rs.getString(1) : "-1");
                    String mac = (rs.getString(2) != null ? rs.getString(2) : "");
                    String detalle = (rs.getString(3) != null ? rs.getString(3) : "");
                    String cheked = (rs.getString(4) != null ? rs.getString(4) : "n");
                    html.append("<tr><td><input type='hidden' id='id_activo" + i + "' name='id_activo" + i + "' value='" + id_activo + "' /><input type='hidden' id='tipo" + i + "' name='tipo" + i + "' value='s' />" + mac + "</td><td>" + detalle + "</td><td><input type='checkbox' id='revisar" + i + "' name='revisar" + i + "' value='true' " + (cheked.compareTo("s") == 0 ? "checked" : "") + " /></td></tr>");
                    i++;
                }
                html.append("<input type='hidden' id='items' name='items' value='" + i + "' />");
                html.append("</table>");
            }

        } catch (Exception e) {
            System.out.println("eror al cargar el hmtl de la bodega");
        }
        return html.toString();
    }

    

    public String setFichaEpps(String usuario, String id_sucursal, String num_ficha_epp, String usuarioi, String actual) {
        String pk = this.insert("insert into tbl_ficha_epp (usuario,id_sucursal,num_ficha_epp,usuarioi,actual)values('" + usuario + "','" + id_sucursal + "','" + num_ficha_epp + "','" + usuarioi + "','" + actual + "');");
        return pk;
    }

    public String setFichaRevisionEpps(String id_ficha_epp, String usuario, String id_sucursal, String num_ficha_revision_epp, String periodo, String estado) {
        String pk = this.insert("insert into tbl_ficha_revision_epp (id_ficha_epp,usuario,id_sucursal,num_ficha_revision_epp,periodo,estado)values('" + id_ficha_epp + "','" + usuario + "','" + id_sucursal + "','" + num_ficha_revision_epp + "','" + periodo + "','" + estado + "');");
        return pk;
    }

    public String getNumeroFichaRevisionEpp(String id_sucursal) {
        String numero = "1";
        try {
            ResultSet rs = this.consulta("select case when max(num_ficha_revision_epp)is null then 1 else max(num_ficha_revision_epp)+1 end from tbl_ficha_revision_epp where id_sucursal='" + id_sucursal + "';");
            if (rs.next()) {
                numero = (rs.getString(1) != null ? rs.getString(1) : "1");
            }
        } catch (Exception e) {
            System.out.println("error al obtener la bodega");
        }
        return numero;
    }

    public int getCountFichaIngresadas(String alias, String periodo) {
        int numero = 0;
        try {
            ResultSet rs = this.consulta("select count(*) from tbl_ficha_revision_epp where periodo='" + periodo + "' and usuario='" + alias + "' and estado not in ('n') and anulado=false and eliminado=false;");
            if (rs.next()) {
                numero = (rs.getString(1) != null ? rs.getInt(1) : 0);
            }
        } catch (Exception e) {
            System.out.println("error al obtener la fichas ingresadas");
        }
        return numero;
    }

    public ResultSet getPdfFichaRevisionEpp(String id) {
        return this.consulta("select A.codigo_activo, A.descripcion,FRED.cantidad_ingresada,FRED.cantidad_revisada,FRED.txtestadoepp,FRED.txtestado_revisado,FRED.observacionepp from vta_ficha_revision_epp_detalle as FRED "
                + " inner join tbl_ficha_epp_detalle as FED on FED.id_ficha_epp_detalle=FRED.id_ficha_epp_detalle "
                + " inner join tbl_ficha_revision_epp as FRE on FRE.id_ficha_revision_epp=FRED.id_ficha_revision_epp "
                + " inner join tbl_activo as A on A.id_activo=FED.id_activo "
                + " where FRE.id_ficha_revision_epp='" + id + "' and FED.tipo='a' "
                + " union "
                + " select P.codigo, P.descripcion,FRED.cantidad_ingresada,FRED.cantidad_revisada,FRED.txtestadoepp,FRED.txtestado_revisado,FRED.observacionepp from vta_ficha_revision_epp_detalle as FRED "
                + " inner join tbl_ficha_epp_detalle as FED on FED.id_ficha_epp_detalle=FRED.id_ficha_epp_detalle "
                + " inner join tbl_ficha_revision_epp as FRE on FRE.id_ficha_revision_epp=FRED.id_ficha_revision_epp "
                + " inner join tbl_producto as P on P.id_producto=FED.id_activo "
                + " where FRE.id_ficha_revision_epp='" + id + "' and FED.tipo='s' "
                + " order by descripcion asc;");
    }

    public ResultSet getPdfFichaEpp(String id) {
        return this.consulta("SELECT A.codigo_activo, A.descripcion FROM tbl_ficha_epp_detalle as FE "
                + " inner join tbl_activo as A on A.id_activo=FE.id_activo "
                + " where FE.id_ficha_epp='" + id + "' and FE.tipo='a'  "
                + " union "
                + " select p.codigo,p.descripcion from tbl_ficha_epp_detalle as b "
                + " inner join vta_producto as p on p.id_producto=b.id_activo "
                + " where id_ficha_epp='" + id + "' and b.tipo='s';");
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

    public boolean setFichaEpp(String id_empleado, String id_sucursal, String id_activos, String proviene) {
        try {
            List sql = new ArrayList();
            String usuario = this.getDatosEmpleado("alias", id_empleado, "");
            String id = this.getUltimaFichaEpp(usuario, id_sucursal);
            if (id.trim().compareTo("-1") == 0) {
                String num_ficha_epp = this.getNumeroFichaEpp(id_sucursal);
                id = this.setFichaEpps(usuario, id_sucursal, num_ficha_epp, "administrador", "true");
            }
            if (id.trim().compareTo("-1") != 0) {
                String tipo = "a";
                if (proviene.trim().compareTo("suministro") == 0) {
                    tipo = "s";
                }
                String[] vid_activos = id_activos.split(",");
                for (int i = 0; i < vid_activos.length; i++) {
                    sql.add("insert into tbl_ficha_epp_detalle (id_ficha_epp,id_activo,tipo)values('" + id + "','" + vid_activos[i] + "','" + tipo + "');");
                }
                sql.add("update tbl_ficha_epp set actual=false where usuario='" + usuario + "';");
                sql.add("update tbl_ficha_epp set actual='true' where id_ficha_epp='" + id + "';");

            }
            if (this.transacciones(sql)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println("error al generar ficha");
            return false;
        }
    }

    public String getUltimaFichaEpp(String alias, String id_sucursal) {
        String numero = "-1";
        try {
            ResultSet rs = this.consulta("select id_ficha_epp from tbl_ficha_epp where usuario='" + alias + "' and id_sucursal='" + id_sucursal + "' order by id_ficha_epp desc;");
            if (rs.next()) {
                numero = (rs.getString(1) != null ? rs.getString(1) : "-1");
            }
        } catch (Exception e) {
            System.out.println("error al obtener la bodega");
        }
        return numero;
    }
}
