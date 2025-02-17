/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.saitel.api.util;

import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class DataBase {

    private Connection con = null;
    private Statement st = null;
    private String error = "";
    
    private String ip = "192.168.217.21"; 
    private int puerto = 5432;
    private String db = "db_isp";
    private String usuario = "postgres";
    private String clave = "Gi%9875.-*5+$)";

    public DataBase() 
    {
        try {
            Class.forName("org.postgresql.Driver");
            this.con = DriverManager.getConnection("jdbc:postgresql://" + this.ip + ":" + this.puerto + "/" + this.db, this.usuario, this.clave);
        } catch (ClassNotFoundException e) {
            System.out.println("Error: " + e.getMessage() + ". El driver no puede ser cargado.");
        } catch (Exception ex) {
            System.out.println("Error " + ex.getMessage() + ". Al conectarse a la base de datos.");
        }
    }

    /**
     * Constructor de la clase DataBase que crea una conexi�n a una base de
     * datos SqlServer2k5.
     *
     * @param m. IP de la maquina del servisor de base de datos SqlServer2k5.
     * @param p. Puerto de escucha del servidor de base de datos.
     * @param db. Nombre de la base de datos a conectarse.
     * @param u. Nombre del usuario de la base de datos.
     * @param c. Contrase�a del usuario de la base de datos.
     */
    public DataBase(String m, int p, String db, String u, String c) 
    {
        this.ip = m; 
        this.puerto = p;
        this.db = db;
        this.usuario = u;
        this.clave = c;
        try {
            Class.forName("org.postgresql.Driver");
            this.con = DriverManager.getConnection("jdbc:postgresql://" + m + ":" + p + "/" + db, u, c);
        } catch (ClassNotFoundException nfe) {
            this.error = nfe.getMessage();
            System.out.println("Error: " + nfe.getMessage() + ". El driver no puede ser cargado.");
        } catch (Exception exp) {
            this.error = exp.getMessage();
            System.out.println("Error " + exp.getMessage() + ". Al conectarse a la base de datos.");
        }
    }

    public Connection getConexion() {
        return this.con;
    }

    public String getIp() {
        return ip;
    }

    public int getPuerto() {
        return puerto;
    }

    public String getDb() {
        return db;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getClave() {
        return clave;
    }

    /**
     * Funci�n que ejecuta una instrucci�n SELECT en el servidor de Base de
     * datos.
     *
     * @param cad. Cadena SQL - SELECT.
     * @return Retorna una objeto ResulSet(juego de registros).
     */
    public ResultSet consulta(String cad) {
        ResultSet r = null;
        try {
            Statement st = this.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String sql = this.decodificarURI(cad);
            r = st.executeQuery(sql);
        } catch (Exception e) {
            this.error = e.getMessage();
        }
        return r;
    }
    
    public ResultSet consulta(String cad, boolean decodificar) {
        ResultSet r = null;
        try {
            Statement st = this.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            if (decodificar) {
                cad = this.decodificarURI(cad);
            }
            r = st.executeQuery(cad);
        } catch (Exception e) {
            this.error = e.getMessage();
        }
        return r;
    }

    /**
     * Ejecuta un grupo de transacciones contenidas en un ArrayList, manteniendo
     * las propiedades ACID.
     *
     * @param tr. Un ArrayList de instrucciones SQL.
     * @return true o falase seg�n si se han realizado todas las transaccciones
     * con exito o no.
     */
    public boolean transacciones(List tr) {
        String sql = "";
        try {
            this.con.setAutoCommit(false);
            if (!tr.isEmpty()) {
                Statement st = this.con.createStatement();
                Iterator it = tr.iterator();
                while (it.hasNext()) {
                    sql = (String) it.next();
                    if (sql.toLowerCase().indexOf("select") == 0) {
                        st.executeQuery(sql);
                    } else {
                        st.executeUpdate(this.decodificarURI(sql));
                    }
                }
                this.con.commit();
                st.close();
            }
            return true;
        } catch (SQLException ex) {
            this.error = ex.getMessage();
            try {
                this.con.rollback();
                System.out.println("Error de lote de transacciones.");
            } catch (SQLException se) {
                this.error = se.getMessage();
            }
        } catch (Exception e) {
            this.error = e.getMessage();
            try {
                this.con.rollback();
                System.out.println("Error de lote de transacciones.");
            } catch (SQLException se) {
                this.error = se.getMessage();
            }
        } finally {
            try {
                this.con.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    /**
     * Funci�n que ejecuta una instrucci�n INSERT, UPDATE o DELETE en el
     * servidor de Base de datos.
     *
     * @param cad. Cadena SQL - INSERT, UPDATE o DELETE.
     * @return Retorna verdadero o false seg�n si se ejecut� o no la
     * instrucci�n.
     */
    public boolean ejecutar(String sql) {
        int r = -1;
        try {
            Statement st = this.con.createStatement();
            r = st.executeUpdate(this.decodificarURI(sql));
            st.close();
            if (r > 0) {
                return true;
            }
        } catch (Exception e) {
            this.error = e.getMessage();
            return false;
        }
        return true;
    }
    
    /**
     * Funci�n que ejecuta una instrucci�n INSERT, UPDATE o DELETE en el
     * servidor de Base de datos.
     *
     * @param tr. Un ArrayList de instrucciones SQL.
     * @return Retorna verdadero o false seg�n si se ejecut� o no la
     * instrucci�n.
     */
    public String ejecutar(List tr) {
        String res = "";
        try {
            if (!tr.isEmpty()) {
                Statement st = this.con.createStatement();
                Iterator it = tr.iterator();
                while (it.hasNext()) {
                    try {
                        String sql = (String) it.next();
                        st.executeUpdate(this.decodificarURI(sql));
                    } catch (Exception ex) {
                        res += ex.getMessage();
                    }
                }
                st.close();
            } else {
                res = "Lista vacia";
            }
        } catch (Exception e) {
            this.error = e.getMessage();
        }
        return res;
    }

    /**
     * Funci�n que ejecuta una instrucci�n INSERT en el servidor para tablas con
     * claves autogeneradas de Base de datos.
     *
     * @param cad. Cadena SQL - INSERT.
     * @return Retorna la clave primaria generada e por el comando insert.
     */
    public String insert(String sql) {
        String pk = "-1";
        if (sql.toLowerCase().indexOf("insert") == 0) {
            try {
                Statement st = this.con.createStatement();
                int r = st.executeUpdate(this.decodificarURI(sql), Statement.RETURN_GENERATED_KEYS);
                if (r > 0) {
                    ResultSet rs = st.getGeneratedKeys();
                    if (rs.next()) {
                        pk = rs.getString(1) != null ? rs.getString(1) : "-1";
                        rs.close();
                    }
                }
                st.close();
            } catch (Exception e) {
                pk = "-1";
                this.error = e.getMessage();
            }
        }
        return pk;
    }

    public String InsertUpdateReturnJson(String cad) {
        String json = "{tbl:[";
        try {
            ResultSet r = null;
            Statement st = this.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String sql = this.decodificarURI(cad);
            r = st.executeQuery(sql);
            json = this.getJSON(r);
        } catch (Exception e) {
            this.error = e.getMessage();
        }
        return json;
    }

    /**
     * Funci�n que codifica un objeto ResultSet(juego de registros) en una
     * cadena JSON.
     *
     * @param r. Un objeto Resultset(juego de registros) que contiene el
     * resultado. de una seltencia SELECT.
     * @return
     */
    public String getJSON(ResultSet r) {
        StringBuilder jsonRes = new StringBuilder();
        StringBuilder json = new StringBuilder();
        json.append("{tbl:[");
        try {
            r.beforeFirst();
            ResultSetMetaData mdata = r.getMetaData();
            int col = mdata.getColumnCount();
            int i = 0;
            while (r.next()) {
                json.append("{");
                i = 0;
                StringBuilder jsonCol = new StringBuilder();
                for (int j = 1; j <= col; j++) {
                    jsonCol.append( i );
                    jsonCol.append(":\"");
                    jsonCol.append(((r.getString(j) != null) ? r.getString(j).replace('"', '~').replace("\n", ". ").replace("\t", " ").replace("\r", ". ") : "") );
                    jsonCol.append("\",");
                    i++;
                }
                json.append( jsonCol.substring(0, jsonCol.length() - 1) );
                json.append("},");
            }
            jsonRes.append( json.toString().substring(0, json.length() - 1) );
            jsonRes.append("]}");
            r.beforeFirst();
        } catch (Exception e) {
            this.error = e.getMessage();
        }
        return jsonRes.toString();
    }

    /**
     * Función que retorna los datos de una tabla filtrada en formato JSON.
     *
     * @param t nombre de la tabla
     * @param c nombre de los campos
     * @param w sentencia SQL WHERE
     * @return una cadena formateada a JSON.
     */
    public String getTablaJSON(String t, String c, String w) {
        String tbl_json = "{tbl:[";
        try {
            ResultSet tbl = this.consulta("SELECT " + c + " FROM " + t + " " + w + ";");
            tbl_json = this.getJSON(tbl);
            tbl.close();
        } catch (Exception e) {
            this.error = e.getMessage();
        }
        return tbl_json;
    }

    /**
     * Funci�n que codifica una consulta SELECT paginada en una cadena en
     * formato JSON.
     *
     * @param t. Nombre de la tabla para la consulta.
     * @param c. Nombre de los campos de la tabla.
     * @param w. Clausula WHERE para la consulta.
     * @param p. El n�mero de la p�gina a retornar.
     * @param fxp. El n�mero de registros por p�gina.
     * @return Una cadena codificada en formato JSON y paginada.
     */
    public String paginar(String t, String c, String w, int p, int fxp) {
        ResultSet r = null;
        String json = "{tbl:[";
        long numPags = 0;
        try {
            Statement st = this.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            /*ResultSet rPag = st.executeQuery(this.decodificarURI("SELECT * FROM " + t +" "+ w));
            numPags = (this.getFilas(rPag)-1)/fxp;*/
            ResultSet rPag = st.executeQuery(this.decodificarURI("SELECT count(*) FROM " + t + " " + w.replaceAll("order by .*", "")));
            if (rPag.next()) {
                numPags = ((rPag.getString(1) != null ? rPag.getLong(1) : 1) - 1) / fxp;
                rPag.close();
            }
            rPag.close();
            if (p > numPags) {
                p = 0;
            }
            r = st.executeQuery(this.decodificarURI("SELECT " + c + " FROM " + t + " " + w + " LIMIT " + fxp + " OFFSET " + (fxp * p) + ";"));
            json = this.getJSON(r);
            st.close();
        } catch (Exception e) {
            this.error = e.getMessage();
        }
        return numPags + "|" + json;
    }
    
    
    /**
     * Funci�n que codifica una consulta SELECT paginada en una cadena en
     * formato JSON.
     *
     * @param t. Nombre de la tabla para la consulta.
     * @param c. Nombre de los campos de la tabla.
     * @param w. Clausula WHERE para la consulta.
     * @param p. El n�mero de la p�gina a retornar.
     * @param fxp. El n�mero de registros por p�gina.
     * @return Una cadena codificada en formato JSON y paginada.
     */
    public String paginarFiltoXCampos(String t, String c, String w, int p, int fxp) {
        ResultSet r = null;
        String json = "{tbl:[";
        long numPags = 0;
        try {
            Statement st = this.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            /*ResultSet rPag = st.executeQuery(this.decodificarURI("SELECT * FROM " + t +" "+ w));
            numPags = (this.getFilas(rPag)-1)/fxp;*/
            ResultSet rPag = st.executeQuery(this.decodificarURI("SELECT count(" + c.replace(",", "||") + ") FROM " + t + " " + w.replaceAll("order by .*", "")));
            if (rPag.next()) {
                numPags = ((rPag.getString(1) != null ? rPag.getLong(1) : 1) - 1) / fxp;
                rPag.close();
            }
            rPag.close();
            if (p > numPags) {
                p = 0;
            }
            r = st.executeQuery(this.decodificarURI("SELECT " + c + " FROM " + t + " " + w + " LIMIT " + fxp + " OFFSET " + (fxp * p) + ";"));
            json = this.getJSON(r);
            st.close();
        } catch (Exception e) {
            this.error = e.getMessage();
        }
        return numPags + "|" + json;
    }

    /**
     * Funci�n que codifica una consulta SELECT paginada en una cadena en
     * formato JSON.
     *
     * @param t. Nombre de la tabla para la consulta.
     * @param c. Nombre de los campos de la tabla.
     * @param w. Clausula WHERE para la consulta.
     * @param p. El n�mero de la p�gina a retornar.
     * @param fxp. El n�mero de registros por p�gina.
     * @return Una cadena codificada en formato JSON y paginada.
     */
    public String paginacion(String t, String c, String w, int p, int fxp) {
        String json = "{tbl:[";
        int tope = (p + 1) * fxp;
        String dist = " ";
        if (c.toLowerCase().indexOf("distinct") >= 0) {
            c = c.replace("distinct", "");
            dist = "distinct ";
        }
        String SQL = "SELECT " + dist + c + " FROM " + t + " " + w + " LIMIT " + fxp;
        int numPags = 0;
        try {
            Statement st = this.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rPag = st.executeQuery(this.decodificarURI("SELECT " + dist + c + " FROM " + t + " " + w));
            numPags = (this.getFilas(rPag) - 1) / fxp;
            rPag.close();
            ResultSet r = st.executeQuery(this.decodificarURI(SQL));
            ResultSetMetaData mdata = r.getMetaData();
            int ini = tope - fxp;
            int i = 0;
            while (i < ini) {
                r.next();
                i++;
            }
            while (r.next()) {
                json += "{";
                i = 0;
                for (int j = 1; j <= mdata.getColumnCount(); j++) {
                    String dato = r.getString(j) != null ? r.getString(j) : "";
                    json += i + ":\"" + ((r.getString(j) != null) ? dato.replace("\"", "~").replace("''", "~") : "") + "\",";
                    i++;
                }
                json = json.substring(0, json.length() - 1);
                json += "},";
            }
            json = json.substring(0, json.length() - 1);
            json += "]}";
            r.close();
            st.close();
            this.error = "";
        } catch (Exception e) {
            this.error = e.getMessage();
        }
        return numPags + "|" + json;
    }

    /**
     * Funci�n que calcula el n�mero de filas de un juego de registros.
     *
     * @param rs. Un objeto Resultset(juego de registros) que contiene el
     * resultado.
     * @return el n�mero de filas de un juego de registros.
     */
    public int getFilas(ResultSet rs) {
        int cont = 0;
        try {
            rs.last();
            cont = rs.getRow();
            rs.beforeFirst();
        } catch (Exception e) {
            this.error = e.getMessage();
        }
        return cont;
    }

    /**
     * Funci�n que calcula el n�mero de columnas de un juego de registros.
     *
     * @param rs. Un objeto Resultset(juego de registros) que contiene el
     * resultado.
     * @return el n�mero de columnas de un juego de registros.
     */
    public int getColumnas(ResultSet rs) {
        int cont = 0;
        try {
            ResultSetMetaData mdata = rs.getMetaData();
            cont = mdata.getColumnCount();
        } catch (Exception e) {
            this.error = e.getMessage();
        }
        return cont;
    }

    public String getDato(ResultSet res, String campo) {
        String dato = "";
        try {
            res.beforeFirst();
            if (res.next()) {
                dato = res.getString(campo) != null ? res.getString(campo) : "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dato;
    }

    /**
     * Cierra una conexi�n abierta a una base de datos SqlServer2k5.
     */
    public void cerrar() {
        try {
            this.con.close();
        } catch (Exception ec) {
            System.out.println(ec.getMessage());
        }
    }

    /**
     * Funci�n que decodifica una cadena previamente codificada en formato
     * propietario.
     *
     * @param cad cadena a decodificar.
     * @return una cadena decodificada.
     */
    public String decodificarURI(String cad) {
        cad = cad.replace("_^4;", "á");
        cad = cad.replace("_^5;", "é");
        cad = cad.replace("_^6;", "í");
        cad = cad.replace("_^7;", "ó");
        cad = cad.replace("_^8;", "ú");
        cad = cad.replace("_^9;", "Á");
        cad = cad.replace("_^10;", "É");
        cad = cad.replace("_^11;", "Í");
        cad = cad.replace("_^12;", "Ó");
        cad = cad.replace("_^13;", "Ú");
        cad = cad.replace("_^14;", "Ñ");
        cad = cad.replace("_^15;", "ñ");

        cad = cad.replace("_^0;", "&");
        cad = cad.replace("_^1;", "+");
        cad = cad.replace("_^2;", "%");
        cad = cad.replace("_^3;", "''");
        cad = cad.replace("\\", "/");
        cad = cad.replace("^", "/");
        cad = cad.replace("ￂﾠ ", "");
        cad = cad.replace("ﾀﾑ", "");
        cad = cad.replace("￢", " ");
        cad = cad.replace("\n", " ");
        cad = cad.replace("\r", " ");
        return cad;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError() {
        return this.error;
    }

    
    
    
    
    
    ////////////////////    BLOQUE DE TRANSACCIONES EXTERNO /////////////////////////////////////////////////
    
    
    
    
    
    /**
 * Ejecuta un grupo de transacciones contenidas en un ArrayList,
 * manteniendo las propiedades ACID.
 * @param tr. Un ArrayList de instrucciones SQL.
 * @return true o falase seg�n si se han realizado todas las transaccciones con exito o no.
 */    
    public boolean IniciarTransacciones()
    {
        try{
            this.con.setAutoCommit(false);
            this.con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            this.st = this.con.createStatement();
            return true;
        }catch (SQLException ex) {
            this.error = ex.getMessage();
            ex.printStackTrace();
        }

        return false;
    } 

    /**
 * Funci�n que ejecuta una instrucci�n SELECT en el servidor de Base de datos.
 * @param cad. Cadena SQL - SELECT.
 * @return Retorna una objeto ResulSet(juego de registros).
 */
    public ResultSet consultaTransaccion(String cad) 
    { 
        ResultSet r = null;
        try{
            r = this.st.executeQuery(this.decodificarURI(cad)); 
            this.st.clearBatch();
        }catch(Exception e){  
            this.error = e.getMessage();
            e.printStackTrace();
        }     
        return r;
    } 
    
    /**
 * Funci�n que ejecuta una instrucci�n INSERT, UPDATE o DELETE en el servidor 
 * de Base de datos.
 * @param cad. Cadena SQL - INSERT, UPDATE o DELETE.
 * @return Retorna verdadero o false seg�n si se ejecut� o no la instrucci�n.
 */       
    public boolean ejecutarTransaccion(String sql)
    { 
        try{
            int r = this.st.executeUpdate(this.decodificarURI(sql));
            if(r>0){
                this.st.clearBatch();
                return true;
            }            
        }catch(Exception e){  
            this.error = e.getMessage();
            e.printStackTrace();
        }     
        return false;
    } 
    
/**
 * Funci�n que ejecuta una instrucci�n INSERT en el servidor para tablas con claves autogeneradas
 * de Base de datos.
 * @param cad. Cadena SQL - INSERT.
 * @return Retorna la clave primaria generada e por el comando insert.
 */       
    public String insertarTransaccion(String sql)
    { 
        String pk = "-1";
        if(sql.toLowerCase().indexOf("insert") == 0 ){
            try{
                int r = this.st.executeUpdate(this.decodificarURI(sql), Statement.RETURN_GENERATED_KEYS);
                if(r>0){
                    ResultSet rs = st.getGeneratedKeys();
                    if(rs.next()){
                        pk = rs.getString(1) != null ? rs.getString(1) : "-1";
                        rs.close();
                    }
                }
            }catch(Exception e){
                this.error = e.getMessage();
                e.printStackTrace();
            }
        }
        return pk;
    } 
    
    public boolean ConfirmarTransacciones()
    {
        try{
            this.con.commit();
            st.close();
            return true;
        }catch (SQLException ex) {
            this.error = ex.getMessage();
            try {
                this.con.rollback();
                System.out.println("Error en el commit de transacciones.");
            }catch (SQLException se) {
                this.error = se.getMessage();
            }
        }finally{
            try{
                this.con.setAutoCommit(true);
                this.con.setTransactionIsolation(Connection.TRANSACTION_NONE);
            }catch(SQLException e){}
        }
        return false;
    }
    
    public boolean CancelarTransacciones()
    {
        try{
            this.con.rollback();
            st.close();
            return true;
        }catch (SQLException ex) {
            this.error = ex.getMessage();
        }finally{
            try{
                this.con.setAutoCommit(true);
                this.con.setTransactionIsolation(Connection.TRANSACTION_NONE);
            }catch(SQLException e){}
        }
        return false;
    }
    
}
