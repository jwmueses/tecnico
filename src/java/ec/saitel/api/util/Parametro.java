/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.saitel.api.util;

/**
 *
 * @author Sistemas
 */
public class Parametro 
{
    private static String _ip = "127.0.0.1";      //  127.0.0.1     pruebas = 192.168.217.16     produccion = 192.168.217.21
    private static int _puerto = 5432;
    private static String _db = "db_isp";
    private static String _usuario = "postgres";
    private static String _clave = "Gi%9875.-*5+$)";    //  Gi%9875.-*5+$)      pruebas = A0Lpni2++
    
    private static String docIp = "192.168.217.31";      //  127.0.0.1     pruebas = 192.168.217.16    produccion = 192.168.217.31
    private static int docPpuerto = 5432;
    private static String docDb = "db_isp_documentos";
    private static String docUsuario = "postgres";
    private static String docClave = "Gi%9875.-*5+$)";    //  Gi%9875.-*5+$)      pruebas = A0Lpni2++
    
    private static String _docsElectronicos = "/opt/lampp/htdocs/anexos/fe/";
    private static String _dir = "/opt/lampp/htdocs/anexos/dir/";
    private static String _urlAnexos = "https://138.185.137.120/anexos/";

    public static String getIp() {
        return _ip;
    }

    public static void setIp(String _ip) {
        Parametro._ip = _ip;
    }

    public static int getPuerto() {
        return _puerto;
    }

    public static void setPuerto(int _puerto) {
        Parametro._puerto = _puerto;
    }

    public static String getDb() {
        return _db;
    }

    public static void setDb(String _db) {
        Parametro._db = _db;
    }

    public static String getUsuario() {
        return _usuario;
    }

    public static void setUsuario(String _usuario) {
        Parametro._usuario = _usuario;
    }

    public static String getClave() {
        return _clave;
    }

    public static void setClave(String _clave) {
        Parametro._clave = _clave;
    }

    public static String getDocIp() {
        return docIp;
    }

    public static void setDocIp(String docIp) {
        Parametro.docIp = docIp;
    }

    public static int getDocPpuerto() {
        return docPpuerto;
    }

    public static void setDocPpuerto(int docPpuerto) {
        Parametro.docPpuerto = docPpuerto;
    }

    public static String getDocDb() {
        return docDb;
    }

    public static void setDocDb(String docDb) {
        Parametro.docDb = docDb;
    }

    public static String getDocUsuario() {
        return docUsuario;
    }

    public static void setDocUsuario(String docUsuario) {
        Parametro.docUsuario = docUsuario;
    }

    public static String getDocClave() {
        return docClave;
    }

    public static void setDocClave(String docClave) {
        Parametro.docClave = docClave;
    }

    public static String getDocsElectronicos() {
        return _docsElectronicos;
    }

    public static void setDocsElectronicos(String _docsElectronicos) {
        Parametro._docsElectronicos = _docsElectronicos;
    }

    public static String getDir() {
        return _dir;
    }

    public static void setDir(String _dir) {
        Parametro._dir = _dir;
    }

    public static String getUrlAnexos() {
        return _urlAnexos;
    }

    public static void setUrlAnexos(String _urlAnexos) {
        Parametro._urlAnexos = _urlAnexos;
    }

}
