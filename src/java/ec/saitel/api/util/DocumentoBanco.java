/**
* @version 1.0
* @package FACTURAPYMES.
* @author Jorge Washington Mueses Cevallos.
* @copyright Copyright (C) 2010 por Jorge Mueses. Todos los derechos reservados.
* @license http://www.gnu.org/copyleft/gpl.html GNU/GPL.
* FACTURAPYMES! es un software de libre distribución, que puede ser
* copiado y distribuido bajo los términos de la Licencia Pública
* General GNU, de acuerdo con la publicada por la Free Software
* Foundation, versión 2 de la licencia o cualquier versión posterior.
*/

package ec.saitel.api.util;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jorge
 */
public class DocumentoBanco extends DataBase{
    public DocumentoBanco(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }

    public float getDocumentoBanco(String num_doc)
    {
        float saldo = 0;
        try{
            ResultSet res = this.consulta("SELECT monto FROM tbl_documento_banco_tmp where tipo_transaccion='C' and documento='"+num_doc+"' and '"+num_doc+"' not in (select num_documento from tbl_arqueo_caja_documento_cierre)");
            if(res.next()){
                saldo = (res.getString(1)!=null) ? res.getFloat(1) : 0;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return saldo;
    }
    
    public String getDocumentoPCBanco(String num_doc)
    {
        String idBanco = "-0";
        try{
            ResultSet res = this.consulta("SELECT id_plan_cuenta FROM tbl_documento_banco_tmp D inner join tbl_banco B on B.id_banco=D.id_banco where documento='"+num_doc+"' union "
                    + "SELECT id_plan_cuenta FROM tbl_documento_banco D inner join tbl_banco B on B.id_banco=D.id_banco where num_documento='"+num_doc+"'");
            if(res.next()){
                idBanco = (res.getString(1)!=null) ? res.getString(1) : "-0";
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return idBanco;
    }
    
    public String getPkBanco(String idPlanCuenta)
    {
        String idBanco = "-0";
        try{
            ResultSet res = this.consulta("SELECT id_banco FROM tbl_banco where id_plan_cuenta='"+idPlanCuenta+"'");
            if(res.next()){
                idBanco = (res.getString(1)!=null) ? res.getString(1) : "-0";
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return idBanco;
    }
    
    public float getSaldoDocumento(String num_doc)
    {
        float saldo = 0;
        try{
            ResultSet res = this.consulta("SELECT saldo FROM tbl_documento_banco where tipo_transaccion='C' and num_documento='"+num_doc+"' and saldo>0");
            if(res.next()){
                saldo = (res.getString(1)!=null) ? res.getFloat(1) : 0;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return saldo;
    }
    
    public float getSaldoDocumento(String idBanco, String tipo, String num_doc)
    {
        float saldo = 0;
        try{
            ResultSet res = this.consulta("SELECT saldo FROM tbl_documento_banco where id_banco='"+idBanco+"' and tipo_transaccion='"+tipo+"' and num_documento='"+num_doc+"' and saldo>0");
            if(res.next()){
                saldo = (res.getString(1)!=null) ? res.getFloat(1) : 0;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return saldo;
    }

    public float getDebitosBanco(String doc, String cuenta)
    {
        float saldo = 0;

        /*int anio = Fecha.getAnio();
        int mes = Fecha.getMes();
        String ini = anio + "-" + mes + "-01";
        String fin = anio + "-" + mes + "-" + Fecha.getUltimoDiaMes(anio, mes);*/
        try{
            ResultSet res = this.consulta("SELECT valor FROM tbl_documento_banco_debito where item='"+doc+"' and cuenta='"+cuenta+"'");
            if(res.next()){
                saldo = (res.getString(1)!=null) ? res.getFloat(1) : 0;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return saldo;
    }

    public boolean hayDocumento(String num_doc)
    {
        ResultSet res = this.consulta("SELECT * FROM tbl_documento_banco where tipo_transaccion='C' and num_documento='"+num_doc+"' and saldo>=0 ");
        if(this.getFilas(res)>0){
            return true;
        }
        try{
            res.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public ResultSet getPagosDocumentoBanco(String num_doc)
    {
        int anio = Fecha.getAnio();
        return this.consulta("select * from tbl_comprobante_ingreso where num_comp_pago='"+num_doc+"' and fecha_proceso between '"+anio+"-01-01' and '"+anio+"-12-31' order by id_comprobante_ingreso;");
    }
    
    public ResultSet getUtilizacionDocumentoBanco(String num_doc)
    {
        return this.consulta("select cajero, num_comprobante::varchar as numero, fecha_proceso as fecha_emision, total, 'comprobante ingreso' as documento, anulado,'tbl_comprobante_ingreso' as tabla_digital,id_comprobante_ingreso as id_unico from tbl_comprobante_ingreso where num_comp_pago = '"+num_doc+"' " +
"union " +
"select vendedor as cajero, serie_factura || '-' || num_factura as numero, fecha_emision, total, 'Factura venta' as documento, anulado,'tbl_factura_venta' as tabla_digital,id_factura_venta as id_unico from tbl_factura_venta where num_comp_pago = '"+num_doc+"' " +
"union " +
"select A.cajero, A.num_documento::varchar as numero, A.fecha as fecha_emision, C.monto as total, 'Arqueo caja' as documento, A.anulado,'tbl_arqueo_caja_documento_cierre' as tabla_digital,id_arqueo_caja_documento_cierre as id_unico from tbl_arqueo_caja_documento_cierre as C inner join tbl_arqueo_caja as A on A.id_arqueo_caja=C.id_arqueo_caja where C.num_documento='"+num_doc+"' "
        + "order by fecha_emision desc");
    }
    
    public ResultSet getUtilizacionChequeBanco(String numcheque)
    {
        return this.consulta("select * from tbl_comprobante_egreso where num_cheque = '"+numcheque+"'");
    }
    
    public double getUtilizacionDocumentoBanco(String idBanco, String numDocs, String concepto)
    {
        double montoProcesado = 0;
        try{
            StringBuilder sql = new StringBuilder();
            sql.append("with A as( \n");
            if( concepto.toUpperCase().contains("CHEQUE") ) {
                sql.append("select total from tbl_comprobante_ingreso where id_plan_cuenta_banco=(select id_plan_cuenta from tbl_banco where id_banco="+idBanco+") and num_cheque in ('"+numDocs+"') and not anulado \n");
            } else {
                sql.append("select total from tbl_comprobante_ingreso where id_plan_cuenta_banco=(select id_plan_cuenta from tbl_banco where id_banco="+idBanco+") and num_comp_pago in ('"+numDocs+"') and not anulado \n");
            }
            sql.append("union all \n");
            sql.append("select total from tbl_factura_venta where id_banco="+idBanco+" and num_comp_pago in ('"+numDocs+"') and not anulado \n");
            sql.append("union all \n");
            sql.append("select C.monto as total from tbl_arqueo_caja_documento_cierre as C inner join tbl_arqueo_caja as A on A.id_arqueo_caja=C.id_arqueo_caja where C.num_documento in ('"+numDocs+"') and not anulado \n");
            sql.append(") \n");
            sql.append("select sum(total) as total from A");
            
            ResultSet rs = this.consulta(sql.toString());
            if( rs.next() ) {
                montoProcesado = rs.getString("total")!=null ? rs.getDouble("total") : 0;
                rs.close();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return montoProcesado;
    }
    
    public double getUtilizacionDocumentoBancoDebito(String idBanco, String numDocs, String concepto)
    {
        double montoProcesado = 0;
        try{
            StringBuilder sql = new StringBuilder();
            if( concepto.toUpperCase().contains("CHEQUE") ) {
                sql.append("with A as( \n");
                sql.append("select total from tbl_comprobante_egreso where id_banco="+idBanco+" and num_cheque in ('"+numDocs+"') and not anulado \n");
                sql.append(") \n");
                sql.append("select sum(total) as total from A");
            }
            ResultSet rs = this.consulta(sql.toString());
            if( rs.next() ) {
                montoProcesado = rs.getString("total")!=null ? rs.getDouble("total") : 0;
                rs.close();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return montoProcesado;
    }
    
    
    public boolean insertar(String usuario, String num_documento, String monto)
    {
        return this.ejecutar("INSERT INTO tbl_documento_banco(usuario, num_documento, monto, saldo)"
                + " VALUES('"+usuario+"', '"+num_documento+"', "+monto+", "+monto+")");
    }

    public boolean insertar(String usuario, String num_documento, String monto, double saldo)
    {
        return this.ejecutar("INSERT INTO tbl_documento_banco(usuario, num_documento, monto, saldo)"
                + " VALUES('"+usuario+"', '"+num_documento+"', "+monto+", "+saldo+")");
    }
    
    public boolean actualizar(String num_doc, String saldo)
    {
        return this.ejecutar("UPDATE tbl_documento_banco SET saldo=saldo-"+saldo+" WHERE num_documento='"+num_doc+"'");
    }
    
    public boolean insertar(String usuario, String num_documento, String monto, double saldo, String idBanco)
    {
        return this.ejecutar("INSERT INTO tbl_documento_banco(usuario, num_documento, monto, saldo, id_banco)"
                + " VALUES('"+usuario+"', '"+num_documento+"', "+monto+", "+saldo+", '"+idBanco+"')");
    }
    
    public boolean actualizar(String num_doc, String saldo, String idBanco)
    {
        return this.ejecutar("UPDATE tbl_documento_banco SET saldo=saldo-"+saldo+", id_banco='"+idBanco+"' WHERE num_documento='"+num_doc+"' and saldo >= "+saldo);
    }
    
    public boolean actualizarSaldo(String num_doc, String saldo)
    {
        return this.ejecutar("UPDATE tbl_documento_banco SET saldo=saldo+"+saldo+" WHERE num_documento='"+num_doc+"'");
    }
    
    public boolean encerarSaldoDebito(String idBanco, String num_doc)
    {
        return this.ejecutar("UPDATE tbl_documento_banco SET saldo=0 WHERE id_banco='"+idBanco+"' and tipo_transaccion='D' and num_documento='"+num_doc+"'");
    }

    /* DEBITOS */

    public boolean hayCuenta(String item, String cuenta)
    {
        boolean ok = false;
        int anio = Fecha.getAnio();
        int mes = Fecha.getMes();
        String ini = anio + "-" + mes + "-01";
        String fin = anio + "-" + mes + "-" + Fecha.getUltimoDiaMes(anio, mes);
        try{
            ResultSet res = this.consulta("SELECT * FROM tbl_documento_banco_debito where item='"+item+"' and cuenta='"+cuenta+"' and fecha between '"+ini+"' and '"+fin+"'");
            if(this.getFilas(res)>0){
                ok = true;
            }
            res.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return ok;
    }


    /*   TRANSACCIONES   -    MOVIMIENTOS   */


    public ResultSet getMovDocumentoBanco(String id)
    {
        return this.consulta("select * from tbl_documento_banco_tmp where id_documento_banco_tmp="+id);
    }

    public boolean estaDuplicado(String id, String documento)
    {
        ResultSet res = this.consulta("SELECT documento FROM tbl_documento_banco_tmp where lower(documento)='"+documento.toLowerCase()+"' and id_documento_banco_tmp<>"+id
                + " union all "
                + "SELECT num_documento FROM tbl_documento_banco where lower(num_documento)='"+documento.toLowerCase()+"'"
                + " union all "
                + "SELECT num_referencia_medio FROM tbl_documento_banco_cash where lower(num_referencia_medio)='"+documento.toLowerCase()+"'");
        if(this.getFilas(res)>0){
            return true;
        }
        try{
            res.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean insertarMov(String usuario, String idBanco, String fecha, String concepto, String documento, String monto, String de_banco)
    {
        List sql = new ArrayList();
        sql.add("INSERT INTO tbl_documento_banco_tmp(id_banco, fecha, concepto, documento, monto, de_banco)"
                + " VALUES("+idBanco+", '"+fecha+"', '"+concepto+"', '"+documento+"', "+monto+", "+de_banco+")");
        return this.transacciones(sql);
    }

    public boolean actualizarMov(String id, String idBanco, String fecha, String concepto, String documento, String monto)
    {
        return this.ejecutar("update tbl_documento_banco_tmp set id_banco="+idBanco+", fecha='"+fecha+"', concepto='"+concepto+
                "', documento='"+documento+"', monto="+monto+" where id_documento_banco_tmp="+id);
    }

}