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
public class Documento extends DataBase {

    public Documento(String m, int p, String db, String u, String c) {
        super(m, p, db, u, c);
    }

    public ResultSet getDocumento(String tipo) {
        return this.consulta("SELECT documento FROM tbl_documento where tipo='" + tipo + "';");
    }

    public String getDocumentoTextoId(String tipo) {
        String documento = "";
        ResultSet rs = this.consulta("SELECT documento FROM tbl_documento where id_documento='" + tipo + "';");
        try {
            if (rs.next()) {
                documento = (rs.getString("documento") != null ? rs.getString("documento") : "");
            }
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        }
        return documento;

    }

    public String getDocumentoTextoTipo(String tipo) {
        String documento = "";
        ResultSet rs = this.consulta("SELECT documento FROM tbl_documento where tipo='" + tipo + "';");
        try {
            if (rs.next()) {
                documento = (rs.getString("documento") != null ? rs.getString("documento") : "");
            }
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        }
        return documento;

    }
}
