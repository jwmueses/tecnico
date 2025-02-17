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
public class ArqueoCaja extends DataBase {

    public ArqueoCaja(String m, int p, String db, String u, String c) {
        super(m, p, db, u, c);
    }

    public ResultSet getArqueoCaja(String id) {
        return this.consulta("SELECT * FROM tbl_arqueo_caja where id_arqueo_caja=" + id);
    }

    public ResultSet getArqueoCajaEfectivo(String id) {
        return this.consulta("SELECT * FROM tbl_arqueo_caja_efectivo where id_arqueo_caja=" + id);
    }

    public ResultSet getArqueoCajaCheque(String id) {
        return this.consulta("SELECT * FROM tbl_arqueo_caja_cheque where id_arqueo_caja=" + id);
    }

    public ResultSet getArqueoCajaOtros(String id) {
        return this.consulta("SELECT * FROM tbl_arqueo_caja_otro where id_arqueo_caja=" + id);
    }

    public ResultSet getArqueoCajaRetenciones(String id) {
        return this.consulta("SELECT * FROM tbl_arqueo_caja_retencion where id_arqueo_caja=" + id);
    }

    public String getArqueoCajaDocsCierres(String id) {
        String docs = "";
        try {
            ResultSet rs = this.consulta("SELECT array_to_string(array_agg(num_documento), ',') FROM tbl_arqueo_caja_documento_cierre where id_arqueo_caja=" + id);
            if (rs.next()) {
                docs = rs.getString(1) != null ? rs.getString(1) : "";
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return docs;
    }

    public long getNumArqueo() {
        int num = 1;
        try {
            ResultSet res = this.consulta("SELECT max(num_documento) FROM tbl_arqueo_caja");
            if (res.next()) {
                num = (res.getString(1) != null) ? res.getInt(1) : 0;
                num++;
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    public boolean arqueoPendiente(String usuario) {
        try {
            ResultSet res = this.consulta("SELECT * FROM tbl_arqueo_caja WHERE cajero='" + usuario + "' and cerrado=false and anulado=false");
            if (this.getFilas(res) > 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean cajaContabilizada(String usuario) {
        try {
            ResultSet res = this.consulta("select count(*) as num from tbl_factura_venta where contabilizado=false and anulado=false and vendedor='" + usuario + "' and fecha_emision < now()::date");
            if (res.next()) {
                long numFacturas = res.getString(1) != null ? res.getLong(1) : 0;
                if (numFacturas == 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public double getSaldoCaja(String id_plan_cuenta) {
        double num = 0;
        try {
            ResultSet res = this.consulta("SELECT saldo_deudor from tbl_libro_diario_mayor where id_libro_diario_mayor="
                    + "(select max(id_libro_diario_mayor) from tbl_libro_diario_mayor where id_plan_cuenta=" + id_plan_cuenta + ")");
            if (res.next()) {
                num = (res.getString(1) != null) ? res.getDouble(1) : 0;
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    public String getFechaUltimoArqueo(String cajero) {
        String fecha = Fecha.getFecha("ISO");
        try {
            ResultSet res = this.consulta("SELECT case when max(fecha) is not null then max(fecha) else now()::date end from tbl_arqueo_caja where cajero='" + cajero + "' and anulado=false");
            if (res.next()) {
                fecha = (res.getString(1) != null) ? res.getString(1) : fecha;
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fecha;
    }

    public String getHoraUltimoArqueo(String cajero) {
        String hora = Fecha.getHora();
        try {
            ResultSet res = this.consulta("SELECT case when hora is not null then hora else '00:00:00.999'::time end from tbl_arqueo_caja "
                    + "where cajero='" + cajero + "' and id_arqueo_caja=(select max(id_arqueo_caja) from tbl_arqueo_caja where cajero='" + cajero + "')");
            if (res.next()) {
                hora = (res.getString(1) != null) ? res.getString(1) : hora;
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hora;
    }

    public String concatenarValores(String param1, String param2) {
        String param = "";
        String vecParam1[] = param1.split(",");
        String vecParam2[] = param2.split(",");
        for (int i = 0; i < vecParam1.length; i++) {
            param += "['" + vecParam1[i] + "','" + vecParam2[i] + "'],";
        }
        param = param.substring(0, param.length() - 1);
        return "array[" + param + "]";
    }

    public String concatenarValores(String param1, String param2, String param3) {
        String param = "";
        String vecParam1[] = param1.split(",");
        String vecParam2[] = param2.split(",");
        String vecParam3[] = param3.split(",");
        for (int i = 0; i < vecParam1.length; i++) {
            param += "['" + vecParam1[i] + "','" + vecParam2[i] + "','" + vecParam3[i] + "'],";
        }
        param = param.substring(0, param.length() - 1);
        return "array[" + param + "]";
    }

    public String concatenarValores(String param1, String param2, String param3, String param4) {
        String param = "";
        String vecParam1[] = param1.split(",");
        String vecParam2[] = param2.split(",");
        String vecParam3[] = param3.split(",");
        String vecParam4[] = param4.split(",");
        for (int i = 0; i < vecParam1.length; i++) {
            param += "['" + vecParam1[i] + "','" + vecParam2[i] + "','" + vecParam3[i] + "','" + vecParam4[i] + "'],";
        }
        param = param.substring(0, param.length() - 1);
        return "array[" + param + "]";
    }

    public String insertar(int id_sucursal, int id_punto_emision, String usuario, long num_documento, String fecha, String hora, String saldo_caja,
            String valor_efectivo, String num_cheques, String valor_cheques, String num_comp_pagos, String valor_comp_pagos,
            String num_retenciones, String valor_retenciones, String tipo_pago_facturas,
            String total_caja, String diferencia, String tipo_diferencia, String denominaciones, String cantidades,
            String totales, String bancos, String num_cheque, String valores, String efectivizado, 
            String num_factura_comprobante, String forma_pago, String num_comps_pagos,
            String valores_comps_pagos, String nums_retenciones, String valores_retenciones,
            String num_comprobante, String fecha_proceso, String descripcion, String id_plan_cuenta0, String id_plan_cuenta1) {
        String num = "-1:-1";
        try {
            String paramEfectivo = this.concatenarValores(denominaciones, cantidades, totales);
            String paramCheques = this.concatenarValores(bancos, num_cheque, valores, efectivizado);
            String paramOtros = this.concatenarValores(num_factura_comprobante, forma_pago, num_comps_pagos, valores_comps_pagos);
            String paramRets = this.concatenarValores(nums_retenciones, valores_retenciones, tipo_pago_facturas);
            double deb_hab = Addons.redondear(Math.abs(Float.parseFloat(diferencia)));
            ResultSet res = this.consulta("select proc_arqueoCaja(" + id_sucursal + ", " + id_punto_emision + ", '" + usuario + "', " + num_documento + ", '" + fecha + "', '"
                    + hora + "', " + saldo_caja + ", " + valor_efectivo + ", " + num_cheques + ", " + valor_cheques + ", " + num_comp_pagos + ", " + valor_comp_pagos
                    + ", " + num_retenciones + ", " + valor_retenciones + ", " + total_caja + ", " + diferencia + ", '" + tipo_diferencia + "', " + paramEfectivo
                    + ", " + paramCheques + ", " + paramOtros + ", " + paramRets + ", " + num_comprobante + ", '" + fecha_proceso + "', '" + descripcion
                    + "', array[['" + id_plan_cuenta0 + "', '" + deb_hab + "', '0'],['" + id_plan_cuenta1 + "', '0', '" + deb_hab + "']])");
            if (res.next()) {
                num = (res.getString(1) != null) ? res.getString(1) : "-1:-1";
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    public String cerrar(String id_arqueo_caja, int id_sucursal, String usuario, String fecha_proceso, String detalle, String crrPC, String crrDebe, 
                    String crrHaber, String paramDocumentos, String sumDebe) {
        String num = "-1";
        try {
            String param = this.concatenarValores(crrPC, crrDebe, crrHaber);
            ResultSet res = this.consulta("select proc_arqueoCajaCerrar(" + id_arqueo_caja + ", " + id_sucursal + ", '" + usuario
                    + "', '" + fecha_proceso + "', '" + detalle + "', " + param + ", " + paramDocumentos + ", " + sumDebe + ")");
            if (res.next()) {
                num = (res.getString(1) != null) ? res.getString(1) : "-1";
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    public boolean anular(String id, String comentario_anulado) {
        boolean ok = false;
        try {
            ResultSet res = this.consulta("select proc_anularArqueo(" + id + ", '" + comentario_anulado + "');");
            if (res.next()) {
                ok = (res.getString(1) != null) ? res.getBoolean(1) : false;
                res.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok;
    }

    public boolean ArqueoEstado(String id, String usuario, String estado) {
        return this.ejecutar("update tbl_arqueo_caja set estado_arqueo='" + estado + "',usuario_aprovacion='" + usuario + "' where id_arqueo_caja='" + id + "';");
    }

    public boolean setEstado(String id, String estado) {
        return this.ejecutar("update tbl_arqueo_caja set estado_arqueo='" + estado + "' where id_arqueo_caja='" + id + "';");
    }
}
