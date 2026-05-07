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
import java.util.HashMap;
import java.util.Map;

public class Configuracion extends DataBase{
    public Configuracion(String m, int p, String db, String u, String c){
        super(m, p, db, u, c);
    }
    public String getValor(String param)
    {
        String valor = "";
        try{
            ResultSet r = this.consulta("SELECT valor FROM tbl_configuracion where parametro='"+param+"';");
            if(r.next()){
                valor = (r.getString("valor")!=null) ? r.getString("valor") : "";
                r.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return valor;
    }
    
    public String getValor(String matConfiguracion[][], String param)
    {
        String valor = "";
        int pos = Matriz.enMatriz(matConfiguracion, param, 0);
        if ( pos >=0 ){
            valor = matConfiguracion[pos][1];
        }
        return valor;
    }
    
    public boolean setValor(String param, String valor)
    {
        return this.ejecutar("UPDATE tbl_configuracion SET valor='"+valor+"' WHERE parametro='"+param+"';");
    }
    
    public Map<String, String> getParametros(String parametros)
    {
        Map<String, String> hm = new HashMap();
        try{
            ResultSet rs = this.consulta("select parametro, valor from tbl_configuracion where parametro in( " + parametros + " ) order by parametro");
            while(rs.next()){
                String clave = rs.getString("parametro")!=null ? rs.getString("parametro") : "";
                if(clave.compareTo("")!=0) {
                    hm.put(clave, (rs.getString("valor")!=null ? rs.getString("valor") : "") );
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return hm;
    }
    
    public String[][] getParametros2(String parametros)
    {
        ResultSet rs = this.consulta("select parametro, valor from tbl_configuracion where parametro in( " + parametros + " ) order by parametro");
        return Matriz.ResultSetAMatriz(rs);
    }
    
    public String[][] getParametros()
    {
        ResultSet rs = this.consulta("select parametro, valor from tbl_configuracion order by parametro");
        return Matriz.ResultSetAMatriz(rs);
    }
    
}