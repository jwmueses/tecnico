/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.saitel.api.util;

import ec.gob.sri.FirmaXadesBes;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author PC-ON
 */
public class Preinstalacion extends DataBase {

    public Preinstalacion(String m, int p, String db, String u, String c) {
        super(m, p, db, u, c);
    }

    public ResultSet GetUsuariosPreinstalacion() {
        return this.consulta("select distinct usuario, usuario from vta_preinstalacion where eliminado=false;");
    }

    public ResultSet GetPreinstalacion(String id) {
        return this.consulta("select * from vta_preinstalacion where id_preinstalacion='" + id + "';");
    }

    /*public String SetAts(String id_orden, String fecha, String sucursal, String tipo_orden, String duracion, String inicio_orden, String final_orden, String lugar,
            String observacion, String vehiculo, String placa, String empleado) {
        String pk = this.insert("INSERT INTO tbl_ats(id_orden_trabajo, fecha_ats, id_sucursal, id_tipo_trabajo, duracion_orden, inicio_orden, final_orden,"
                + " lugar_orden, observaciones_ats, vehiculo_ats, placa, id_empleado)"
                + " VALUES ('" + id_orden + "', '" + fecha + "', '" + sucursal + "', '" + tipo_orden + "', '" + duracion + "', '" + inicio_orden + "', '" + final_orden + "',"
                + " '" + lugar + "', '" + observacion + "', '" + vehiculo + "', '" + placa + "', '" + empleado + "');");
        return pk;
    }

    public boolean Set_equipo_trabajo(String empleados, String id_ats) {
        boolean pk = true;
        if (empleados.trim().compareTo("") != 0) {
            List sql = new ArrayList();
            String empleadosv[] = empleados.split(",");
            for (int i = 0; i < empleadosv.length; i++) {
                sql.add("INSERT INTO tbl_equipo_trabajo(id_empleado, id_ats)VALUES ('" + empleadosv[i] + "', '" + id_ats + "');");
            }
            pk = this.transacciones(sql);
        }
        return pk;
    }*/
    public String GetUltimoContrato(String id_sucursal) {
        String numero = "-1";
        try {
            ResultSet r = this.consulta("select (max(num_contrato)+1)as numero from tbl_contrato where id_sucursal='" + id_sucursal + "'");
            if (r.next()) {
                numero = (r.getString("numero") != null) ? r.getString("numero") : "-1";
                r.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return numero;
    }

    public String getCiudad(String id) {
        String ciudad = "Ibarra";
        try {
            ResultSet r = this.consulta("SELECT ciudad FROM tbl_sucursal where id_sucursal='" + id + "';");
            if (r.next()) {
                ciudad = (r.getString("ciudad") != null) ? r.getString("ciudad") : "Empresa";
                r.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ciudad;
    }

    public String[][] getPuntosEmisionVirtuales() {
        ResultSet rs = this.consulta("SELECT P.id_sucursal, P.id_punto_emision, usuario_caja, fac_num_serie, "
                + "case when max(num_factura)>0 then max(num_factura)+1 else 1 end, direccion_establecimiento "
                + "from tbl_punto_emision as P inner join tbl_factura_venta as F on F.serie_factura=P.fac_num_serie "
                + "where caja_virtual=true "
                + "group by P.id_sucursal, P.id_punto_emision, usuario_caja, fac_num_serie, direccion_establecimiento "
                + "order by id_sucursal");
        return Matriz.ResultSetAMatriz(rs);
    }

    public String[][] getSectoresInstalacion(String id_sucursal, String id_sector) {
        ResultSet rs = this.consulta("SELECT S.id_sector,S.sector,S.costo_instalacion,S.id_producto,P.codigo,P.descripcion,P.tiene_iva,"
                + " S.costo_instalacion_fibra, S.costo_instalacion_antena2, S.costo_instalacion_gepon, SP.id_sucursal, I.porcentaje, I.codigo as codigo_iva"
                + " from ((vta_sector as S inner join tbl_producto as P on S.id_producto=P.id_producto)"
                + " inner join tbl_sucursal_producto as SP on SP.id_producto=P.id_producto)"
                + " inner join tbl_iva as I on I.id_iva=SP.id_iva"
                + " where SP.id_sucursal='" + id_sucursal + "' and S.id_sector='" + id_sector + "'"
                + " order by S.sector;");
        return Matriz.ResultSetAMatriz(rs);
    }

    public String fechahora_actual(String formato) {
        Date date = new Date();
        DateFormat FormatoFecha = new SimpleDateFormat(formato);
        return FormatoFecha.format(date);
    }

    public String addfecha_actual(String formato, String tipo, int valor) {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        DateFormat FormatoFecha = new SimpleDateFormat(formato);
        if (tipo.trim().compareTo("anio") == 0) {
            calendar.add(Calendar.YEAR, valor);
        }
        if (tipo.trim().compareTo("mes") == 0) {
            calendar.add(Calendar.YEAR, valor);
        }
        if (tipo.trim().compareTo("dia") == 0) {
            calendar.add(Calendar.YEAR, valor);
        }
        date = calendar.getTime();
        return FormatoFecha.format(date);
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

    public String solucionarorden(Ats objAts, OrdenTrabajo objOrdenTrabajo,
            int id_sucursal, String id_orden_trabajo, String usuario, String solucionado, String conformidad, String atencion, String recomendacion, String materiales, String cantidades, String set_deviceclave,
            String fechaats, String idsucats, String tipoats, String duracionats, String inicioats, String finalats, String lugarats, String observacionats, String vehiculoats, String placaats, String id_tecnico_resp, String fecha_baja,
            String a_trabajo_alturas, String a_analisis_riesgo, String a_procedimiento, String a_otros, String a_observacion, String b_epp_basico, String b_epp_trabajo, String b_otros, String b_observacion, String preguntas1,
            String preguntas2, String riesgos, String medidas, String empleadosats, String pos, String id_preinstalacion) {
        String r = "Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o "
                + "contáctese con el administrador del sistema para mayor información.";
        if (!objOrdenTrabajo.solucionar(id_sucursal, id_orden_trabajo, usuario, Fecha.getFecha("ISO"), Fecha.getHora(), solucionado,
                conformidad, atencion, recomendacion, materiales, cantidades, set_deviceclave)) {
            r = "msg»" + objOrdenTrabajo.getError();
            //r = "msg»Ha ocurrido un error en el registro de la solución de la orden de trabajo. Por favor contáctese con el administrador del sistema para mayor información";
        } else {
            r = " El ATS NO SE HA GENERADO CONTACTE AL ADMINISTRADOR";
            this.ejecutar("update tbl_preinstalacion set estado='n' where id_preinstalacion='" + id_preinstalacion + "';");
            String pkats = objAts.SetAts(id_orden_trabajo, fechaats, idsucats, tipoats, duracionats, inicioats, finalats, lugarats, observacionats, vehiculoats, placaats, id_tecnico_resp, fecha_baja);
            if (pkats.trim().compareTo("-1") != 0) {
                String pkpermiso_proteccion = objAts.Set_permiso_proteccion(a_trabajo_alturas, a_analisis_riesgo, a_procedimiento, a_otros, a_observacion, b_epp_basico, b_epp_trabajo, b_otros, b_observacion, preguntas1, pkats);
                String pksecuencia = objAts.Set_secuencia_trabajo(preguntas2, riesgos, medidas, pkats);
                boolean pkempleados = objAts.Set_equipo_trabajo(empleadosats, pkats);

                if (pkats.compareTo("-1") != 0 && pkpermiso_proteccion.compareTo("-1") != 0 && pksecuencia.compareTo("-1") != 0 && pkempleados == true) {
                    r = "INCLUIDO ATS EXITOSO";
                }
                r = "fun»_R('rotSol" + pos + "');_R('ppp');^msg»ORDEN DE TRABAJO REGISTRADA SATISFACTORIAMENTE. " + r;
            }
        }

        return r;
    }

    public String[] promociones(String id_promocion) {
        String promocion[] = {"", ""};
        try {
            ResultSet rs = this.consulta("select inst_costo_es_porcentaje,inst_costo_valor from tbl_promocion where id_promocion=" + id_promocion + "");
            if (rs.next()) {
                promocion[0] = (rs.getString("inst_costo_es_porcentaje") != null) ? rs.getString("inst_costo_es_porcentaje") : "";
                promocion[1] = (rs.getString("inst_costo_valor") != null) ? rs.getString("inst_costo_valor") : "";
                rs.close();
            }
            return promocion;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public int promocionsucural(String id_promocion, String id_plan) {
        int promocion = 0;
        try {
            ResultSet rs = this.consulta("select count(*) from tbl_promocion_plan where id_promocion=" + id_promocion + " and id_plan_servicio=" + id_plan + ";");
            if (rs.next()) {
                promocion = (rs.getString(1) != null) ? rs.getInt(1) : 0;
                rs.close();
            }
            return promocion;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public String[] crearinstalacion(String ips, int puertos, String db, String usuario, String clave, String usuarioSolucion, String ipRemota,
            String ruta, String id_sucursal, String id_preinstalacion, String ip, String id_sector1, String id_plan, String id_orden_trabajo,
            String id_instalacionnormal, String cobrarinstalacion, String id_promocion, Archivo archivo, String dips, int dpuertos,
            String ddb, String dusuario, String dclave, HttpServletResponse response, String dir, String anexos) {
        String tmpid_instalacion = "";
        String num_contrato = "";
        String idContrato = "";
        String pk_instalacion[] = {"-1", ""};
        String r = "Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o "
                + "contáctese con el administrador del sistema para mayor información.";
        Instalacion objInstalacion = new Instalacion(ips, puertos, db, usuario, clave);
        PreFactura objPreFactura = new PreFactura(ips, puertos, db, usuario, clave);
        Auditoria auditoria = new Auditoria(ips, puertos, db, usuario, clave);
        FacturaVenta objFacturaVenta = new FacturaVenta(ips, puertos, db, usuario, clave);
        Contrato objContrato = new Contrato(ips, puertos, db, usuario, clave);
        PuntoEmision objPuntoEmision = new PuntoEmision(ips, puertos, db, usuario, clave);
        Iva objIva = new Iva(ips, puertos, db, usuario, clave);
        Producto objProducto = new Producto(ips, puertos, db, usuario, clave);
        FacturaElectronica objFE = new FacturaElectronica();
        List sql = new ArrayList();
        ///datos para procesamiento

        String matPuntosVirtuales[][] = this.getPuntosEmisionVirtuales();
        int p = Matriz.enMatriz(matPuntosVirtuales, id_sucursal, 0);
        try {
            r = "No existe un punto virtual para esta sucursal o existe un error";
            if (p != -1) {
                ///variables para procesar
                String fecha_actual = this.fechahora_actual("yyyy-MM-dd");
                String fecha_actual_mas = this.addfecha_actual("yyyy-MM-dd", "anio", 1);
                DecimalFormat formato = new DecimalFormat("#.00");

                ////datos para el cliente
                ///1. obtengo los datos del cliente de la preintalacion
                String tipo_documento_cliente = "05";
                String id_cliente = "-1";
                String razon_social = "";
                String ruc = "";
                String convenio_pago = "1";
                String id_provincia = "";
                String id_ciudad = "";
                String id_parroquia = "";
                String direccion_instalacion = "";
                String tipo_instalacion = "";
                String telefono = "";
                String email = "";
                String tipo_cliente_instalacion = "c";
                try {
                    ResultSet rscliente = this.GetPreinstalacion(id_preinstalacion);
                    if (rscliente.next()) {
                        id_cliente = rscliente.getString("id_cliente") != null ? rscliente.getString("id_cliente") : "-1";
                        razon_social = rscliente.getString("razon_social") != null ? rscliente.getString("razon_social") : "";
                        ruc = rscliente.getString("ruc") != null ? rscliente.getString("ruc") : "";
                        tipo_documento_cliente = rscliente.getString("tipo_documento") != null ? rscliente.getString("tipo_documento") : "05";
                        convenio_pago = rscliente.getString("tipo_convenio") != null ? rscliente.getString("tipo_convenio") : "1";
                        id_provincia = rscliente.getString("id_provincia") != null ? rscliente.getString("id_provincia") : "";
                        id_ciudad = rscliente.getString("id_ciudad") != null ? rscliente.getString("id_ciudad") : "";
                        id_parroquia = rscliente.getString("id_parroquia") != null ? rscliente.getString("id_parroquia") : "";
                        direccion_instalacion = rscliente.getString("direccion") != null ? rscliente.getString("direccion") : "";
                        tipo_instalacion = rscliente.getString("id_tipoinstalacion") != null ? rscliente.getString("id_tipoinstalacion") : "i";
                        telefono = rscliente.getString("telefono") != null ? rscliente.getString("telefono") : "";
                        email = rscliente.getString("email") != null ? rscliente.getString("email") : "";
                        rscliente.close();
                    }
                } catch (Exception ec) {
                    ec.printStackTrace();
                }
                r = "No existe un cliente en esta preinstalacion para bajar esta orden";
                if (id_cliente.compareTo("-1") != 0) {

                    ///2- generamos el contrato del cliente
                    idContrato = objContrato.getNumeroContrato(id_cliente);
                    if (idContrato.trim().compareTo("-1") == 0) {
                        num_contrato = this.GetUltimoContrato(id_sucursal);
                        String ruc_representante = "";
                        String representante = "";
                        idContrato = objContrato.insertar(num_contrato, id_cliente, id_sucursal, fecha_actual, fecha_actual_mas, ruc_representante, representante, "", "", "");
                        if (idContrato.compareTo("-1") != 0) {
                            //archivo.setArchivoDocumentalTexto(contrato, num_contrato, id_sucursal, "tbl_contrato", idContrato, "contratotexto", "public", "db_isp");
                            auditoria.setRegistro(usuarioSolucion, ipRemota, "INGRESO DEL NUEVO CONTRATO Nro. " + id_sucursal + "-" + num_contrato + " POR PREINSTALACION");
                        }
                    }
                    r = "No existe un contrato para este cliente o existe un error";
                    if (idContrato.compareTo("-1") != 0) {
                        /////
                        String error = "";
                        String id = "-1";
                        boolean ok = true;
                        /////
                        ////datos de la empresa
                        String _DOCS_ELECTRONICOS = ruta;
                        Configuracion conf = new Configuracion(ips, puertos, db, usuario, clave);
                        
                        Map<String, String> params = conf.getParametros("'clave_certificado', 'ambiente', 'ruc', 'razon_social', 'nombre_comercial', 'num_resolucion',"
                                + " 'oblga_contabilidad', 'dir_matriz', 'desc_venta', 'cxc', 'p_iva1', 'email_info', 'numeros_soporte', 'pagina_web'");
        
                        String clave_certificado = params.get("clave_certificado");
                        String ambiente = params.get("ambiente");
                //        String tipoEmision = "1"; // 1=normal    2=Indisponibilidad del sistema
                        String ruc_empresa = params.get("ruc");
                        String razon_social_empresa = params.get("razon_social");
                        String nombre_comercial = params.get("nombre_comercial");
                        String email_info = params.get("email_info");
                        String num_contacto = params.get("numeros_soporte");
                        String sitio_web = params.get("pagina_web");
                        String num_resolucion = params.get("num_resolucion");
                        String oblga_contabilidad = params.get("oblga_contabilidad");
                        String dir_matriz = params.get("dir_matriz");
                        String desc_venta = params.get("desc_venta");
                        String cxc = params.get("cxc");

                        String tipoEmision = "1";
//                        String clave_certificado = conf.getValor("clave_certificado");
//                        String ambiente = conf.getValor("ambiente");
//                        String tipoEmision = conf.getValor("tipo_emision"); // 1=normal    2=Indisponibilidad del sistema
//                        String cxc = conf.getValor("cxc");
//                        String ruc_empresa = conf.getValor("ruc");
//                        String razon_social_empresa = conf.getValor("razon_social");
//                        String nombre_comercial = conf.getValor("nombre_comercial");
//                        String num_resolucion = conf.getValor("num_resolucion");
//                        String oblga_contabilidad = conf.getValor("oblga_contabilidad");
//                        String dir_matriz = conf.getValor("dir_matriz");
//                        String desc_venta = conf.getValor("desc_venta");
                        
                        conf.cerrar();
                        ////
                        /////datos del puto de emision
                        String id_punto_emision = matPuntosVirtuales[p][1];
//                        String id_plan_cuenta_caja = objPuntoEmision.getIdCaja(id_punto_emision);
                        String usuario_emision = matPuntosVirtuales[p][2];
                        String serie_factura = matPuntosVirtuales[p][3];
                        String num_factura = matPuntosVirtuales[p][4];
                        String direccion_sucursal = matPuntosVirtuales[p][5];
                        String num_comp_pago = "";
                        //////recolectamos datos ´para la instalacion 
                        ///datos para la instalacion
                        String id_plan_contratado = id_plan;
                        String num_instalacion = objInstalacion.getNumInstalacion(Integer.parseInt(id_sucursal));
                        String ip_mascara = ip.toUpperCase();
                        String es_instalacion = cobrarinstalacion;
                        String ip_radio = "";
                        String doc_electronico = "0";
                        String set_convenio_tarjeta = "false";
                        String set_convenio_cuenta = "false";
                        String cobrar = "TRUE";
                        String id_sector = id_sector1;
                        String motivo_no_cobrar = "";
//                        String tipo_servicio = "i";
//                        if (tipo_instalacion.compareTo("a") == 0 || tipo_instalacion.compareTo("n") == 0) {
//                            tipo_servicio = "i";
//                        } else if (tipo_instalacion.compareTo("f") == 0 || tipo_instalacion.compareTo("g") == 0) {
//                            tipo_servicio = "f";
//                        }
                        String descuento = "0";
                        String forma_pago = "d";
                        String id_plan_cuenta_banco = "0";
                        String gastos_bancos = "0";
                        ///
                        String matCostoInstalacion[][] = this.getSectoresInstalacion(id_sucursal, id_sector);
                        int pc = Matriz.enMatriz(matCostoInstalacion, id_sector, 0);
                        String costo_instalacion = matCostoInstalacion[pc][2];
                        if (tipo_instalacion.compareTo("f") == 0) {
                            costo_instalacion = matCostoInstalacion[pc][7];
                        } else if (tipo_instalacion.compareTo("n") == 0) {
                            costo_instalacion = matCostoInstalacion[pc][8];
                        } else if (tipo_instalacion.compareTo("g") == 0) {
                            costo_instalacion = matCostoInstalacion[pc][9];
                        }
                        //////////////////

                        double descuento1 = 0;
                        double descPro = 0;
                        double pDescPro = 0;
                        ////promocion
                        try {
                            if (id_promocion.trim().compareTo("") != 0 && id_promocion.trim().compareTo("-0") != 0) {

                                String promocion[] = this.promociones(id_promocion);
                                int tmpexiste = this.promocionsucural(id_promocion, id_plan);
                                String tmpporcentaje = promocion[0];
                                String tmpcosto1 = promocion[1];
                                double tmpcosto = Double.parseDouble(tmpcosto1.trim().compareTo("") != 0 ? tmpcosto1 : "0");
                                if (tmpexiste > 0) {
                                    if (tmpporcentaje.compareTo("t") == 0 && tmpcosto <= 100) {
                                        pDescPro = tmpcosto;
                                    }
                                    if (tmpporcentaje.compareTo("f") == 0 && tmpcosto > 0) {
                                        descPro = tmpcosto;
                                    }
                                    es_instalacion = ((tmpporcentaje.compareTo("t") == 0 && tmpcosto == 100) ? "FALSE" : "TRUE");
                                }
                            }
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                        descuento1 = pDescPro > 0 ? ((Double.parseDouble(costo_instalacion) * pDescPro) / 100) : descPro;
                        descuento1 = this.redondear(descuento1, 2);
                        descuento = Double.toString(descuento1);
                        //////////
                        float iva_general = Float.parseFloat(matCostoInstalacion[pc][11]);
                        double tmptotal = this.redondear(((Float.parseFloat(costo_instalacion) - descuento1) + ((Float.parseFloat(costo_instalacion) - descuento1) * (iva_general / 100))), 2);
                        String total = "" + tmptotal;
                        String matParamAsientoAx[][] = null;
                        if (forma_pago.compareTo("d") == 0) {
                            matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{cxc, total, "0"});
                        }

                        ///para la retencion no existe va todo en blanco
                        String paramRet = "";
                        String ret_num_serie = "";
                        String ret_autorizacion = "";
                        String ret_num_retencion = "0";
                        String ret_fecha_emision = "";
                        String ret_ejercicio_fiscal_mes = "";
                        String ret_ejercicio_fiscal = "NULL";
                        String ret_impuesto_retenido = "0";
                        ////fin de la retencion
                        if (Float.parseFloat(descuento) > 0) {
                            matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{desc_venta, descuento, "0"});
                        }

                        //  productos
                        String ids_productos = "";
                        String descripciones = "";
                        String cantidades_prod = "";
                        String preciosUnitarios = "";
                        String descuentos = "";
                        String subtotales = "";
                        String ivas = "";
                        String pIvas = "";
                        String codigoIvas = "";
                        String paramArtic = "";
                        ///proceso del producto
                        String id_producto = matCostoInstalacion[pc][3];
                        String subt = costo_instalacion;
                        double tmpiva = this.redondear(((Float.parseFloat(costo_instalacion) - descuento1) * (iva_general / 100)), 2);
                        String iva = "" + tmpiva;
                        ids_productos += id_producto + ",";
                        //descripciones += request.getParameter("cpt"+i) + ",";
                        cantidades_prod += "1,";
                        preciosUnitarios += costo_instalacion + ",";
                        descuentos += "" + descuento + ",";
                        String pIva = matCostoInstalacion[pc][11];
                        String codigoIva = matCostoInstalacion[pc][12];
                        subtotales += subt + ",";
                        ivas += iva + ",";
                        pIvas += pIva + ",";
                        codigoIvas += codigoIva + ",";
                        String tipo = "";
                        String id_iva = "2";
                        String id_plan_cuenta_venta = "";
                        String descripcion = "";
                        try {
                            ResultSet rs = objProducto.getProducto(id_producto);
                            if (rs.next()) {
                                tipo = rs.getString("tipo") != null ? rs.getString("tipo") : "";
                                id_iva = rs.getString("id_iva") != null ? rs.getString("id_iva") : "2";
                                id_plan_cuenta_venta = rs.getString("id_plan_cuenta_venta") != null ? rs.getString("id_plan_cuenta_venta") : "";
                                descripcion = rs.getString("descripcion") != null ? rs.getString("descripcion") : "";
                                rs.close();
                            }
                        } catch (Exception e) {
                        }
                        descripciones += descripcion + ",";
                        matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{id_plan_cuenta_venta, "0", subt});
                        String cuenta = tipo.compareTo("s") == 0 ? "id_plan_cuenta_venta_servicio" : "id_plan_cuenta_venta_bien";
                        String id_cuenta_iva = objIva.getIva(id_iva, cuenta);
                        matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{id_cuenta_iva, "0", iva});
                        paramArtic += "['" + id_producto + "', '1', '" + costo_instalacion + "', '" + subt
                                + "', '" + descuento + "', '" + iva + "', '" + total + "', '" + descripcion
                                + "', '1', '" + tipo + "', '" + pIva + "', '" + codigoIva + "'],";
                        if (paramArtic.compareTo("") != 0) {
                            paramArtic = paramArtic.substring(0, paramArtic.length() - 1);
                            ids_productos = ids_productos.substring(0, ids_productos.length() - 1);
                            descripciones = descripciones.substring(0, descripciones.length() - 1);
                            cantidades_prod = cantidades_prod.substring(0, cantidades_prod.length() - 1);
                            preciosUnitarios = preciosUnitarios.substring(0, preciosUnitarios.length() - 1);
                            descuentos = descuentos.substring(0, descuentos.length() - 1);
                            subtotales = subtotales.substring(0, subtotales.length() - 1);
                            ivas = ivas.substring(0, ivas.length() - 1);
                            pIvas = pIvas.substring(0, pIvas.length() - 1);
                            codigoIvas = codigoIvas.substring(0, codigoIvas.length() - 1);
                        }
                        String matParamAsiento[][] = Matriz.suprimirDuplicados(matParamAsientoAx, 0);
                        String paramAsiento = "";
                        for (int i = 0; i < matParamAsiento.length; i++) {
                            paramAsiento += "['" + matParamAsiento[i][0] + "', '" + matParamAsiento[i][1] + "', '" + matParamAsiento[i][2] + "'],";
                        }
                        if (paramAsiento.compareTo("") != 0) {
                            paramAsiento = paramAsiento.substring(0, paramAsiento.length() - 1);
                        }
                        String nombre_archivo = "factura_" + ruc + "_" + serie_factura + "-" + num_factura + ".xml";
                        String subtotal = costo_instalacion;
                        String subtotal_0 = "0";
                        String subtotal_2 = "" + (iva_general == 12 ? costo_instalacion : 0);
                        String subtotal_3 = "" + (iva_general == 14 ? costo_instalacion : 0);
                        double tmpiva_2 = this.redondear((iva_general == 12 ? (Float.parseFloat(costo_instalacion) - descuento1) * (iva_general / 100) : 0), 2);
                        String iva_2 = "" + tmpiva_2;
                        double tmpiva_3 = this.redondear((iva_general == 14 ? (Float.parseFloat(costo_instalacion) - descuento1) * (iva_general / 100) : 0), 2);
                        String iva_3 = "" + tmpiva_3;
                        String id_forma_pago = "99";
                        FormaPago objFormaPago = new FormaPago(ips, puertos, db, usuario, clave);
                        String codigoFormaPago = objFormaPago.getCodigoFormaPago(id_forma_pago);
                        objFormaPago.cerrar();
                        String direccion = direccion_instalacion;
                        String autorizacion = "";
                        String son = total;
                        String observacion = "";
                        String banco = "";
                        String num_cheque = "";

                        ///
                        r = "Este Cliente ya tiene una Instalacion anteriormente";
                        boolean crearfactura = true;
                        if (id_instalacionnormal.trim().compareTo("") != 0 && id_instalacionnormal.trim().compareTo("-1") != 0) {
                            crearfactura = false;
                            pk_instalacion[0] = id_instalacionnormal;
                        }
                        try {
                            if (crearfactura) {
                                r = "IP duplicada para realizar esta instalacion";
                                if (objInstalacion.ipDisponible(id, id_sucursal, ip_mascara)) {
                                    boolean bandera = true;

                                    r = "Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o "
                                            + "contáctese con el administrador del sistema para mayor información.";
                                    String id_factura_id_Instalacion = "-1:-1";
                                    if (id.compareTo("-1") == 0) {
                                        if (es_instalacion.compareTo("TRUE") == 0) {
                                            if (objPreFactura.facturaDuplicada(serie_factura, num_factura)) {
                                                bandera = false;
                                            }
                                        }
                                        if (bandera) {
                                            String xmlFirmado = "";
                                            String estadoDocumento = "";
                                            String certificado = _DOCS_ELECTRONICOS + "certificado.p12";
                                            String rutaSalida = _DOCS_ELECTRONICOS + "firmados";
                                            String claveAcceso = "";
                                            String autorizacionXml = "";
                                            String respuestaAutoriz = "";
                                            if (doc_electronico.compareTo("0") == 0) {
                                                ok = false;
                                                String vecSerie[] = serie_factura.split("-");
                                                claveAcceso = objFE.getClaveAcceso(Fecha.getFecha("SQL"), "01", ruc_empresa, ambiente, vecSerie[0] + vecSerie[1], Cadena.setSecuencial(num_factura), tipoEmision);
//                                                if (tipoEmision.compareTo("2") == 0) {  //   por indisponibilidad del sistema
//                                                    claveAcceso = objClavesSri.getSigClave(Fecha.getFecha("SQL"), "01", ruc_empresa, ambiente, tipoEmision);
//                                                }
                                                String xml = objFE.generarXml(claveAcceso, ambiente, tipoEmision, razon_social_empresa, nombre_comercial, ruc_empresa, email_info, num_contacto, sitio_web, "01", vecSerie[0], vecSerie[1],
                                                        Cadena.setSecuencial(num_factura), dir_matriz, Cadena.setFecha(fecha_actual), direccion_sucursal, num_resolucion, oblga_contabilidad,
                                                        tipo_documento_cliente, razon_social, ruc, subtotal, descuento, subtotal_0, subtotal_2, iva_2, subtotal_3, iva_3, total, codigoFormaPago,
                                                        ids_productos, descripciones, cantidades_prod, preciosUnitarios, descuentos, subtotales, ivas, pIvas, codigoIvas, direccion, email);
                                                String documentoXml = _DOCS_ELECTRONICOS + "generados/" + claveAcceso + ".xml";
                                                objFE.salvar(documentoXml);
                                                error = objFE.getError();
                                                if (error.compareTo("") == 0) {
                                                    estadoDocumento = "g";
                                                    String archivoSalida = claveAcceso + ".xml";
                                                    FirmaXadesBes firmaDigital = new FirmaXadesBes(certificado, clave_certificado, documentoXml, rutaSalida, archivoSalida);
                                                    firmaDigital.execute();
                                                    error = firmaDigital.getError();

                                                    if (error.compareTo("") == 0) {
                                                        estadoDocumento = "f";
                                                        if (tipoEmision.compareTo("1") == 0) {  //   emision normal
                                                            autorizacionXml = this.getStringFromFile(_DOCS_ELECTRONICOS + "firmados/" + claveAcceso + ".xml");
                                                            ok = true;
                                                        } else {
                                                            autorizacionXml = this.getStringFromFile(_DOCS_ELECTRONICOS + "firmados/" + claveAcceso + ".xml");
                                                            ok = true;
                                                        }
                                                    }
                                                }
                                            }
                                            r = "msg»" + error;

                                            if (ok) {
                                                String radusername = ruc + "_" + id_sucursal + "_" + num_instalacion;
                                                String radclave = Cadena.getRandomClave(10);
                                                id_factura_id_Instalacion = objInstalacion.insertar(idContrato, convenio_pago, num_instalacion, id_sucursal, set_convenio_tarjeta, set_convenio_cuenta, id_provincia, id_ciudad, id_parroquia,
                                                        id_sector, tipo_instalacion, costo_instalacion, direccion_instalacion, ip_mascara, ip_radio, id_plan_contratado, es_instalacion, cobrar, motivo_no_cobrar,
                                                        radusername, radclave, Cadena.getRandomClave(10), Integer.parseInt(id_sucursal), Integer.parseInt(id_punto_emision), id_cliente, usuario_emision, serie_factura, num_factura, autorizacion, ruc,
                                                        razon_social, fecha_actual, direccion, telefono, id_forma_pago, forma_pago, banco, num_cheque, num_comp_pago, gastos_bancos, id_plan_cuenta_banco,
                                                        son, observacion, subtotal, subtotal_0, subtotal_2, subtotal_3, descuento, iva_2, iva_3, total, "array[" + paramArtic + "]", ret_num_serie, ret_num_retencion,
                                                        ret_autorizacion, ret_fecha_emision, ret_ejercicio_fiscal_mes, ret_ejercicio_fiscal, ret_impuesto_retenido,
                                                        "array[" + paramRet + "]::varchar[]", "array[" + paramAsiento + "]", xmlFirmado, tipo_cliente_instalacion, "false", "", "", "false", "null");
                                                if (id_factura_id_Instalacion.compareTo("-1:-1") != 0) {
                                                    String vec[] = id_factura_id_Instalacion.split(":");
                                                    String id_factura = vec[0];
                                                    pk_instalacion[0] = vec[1];
                                                    tmpid_instalacion = vec[1];
                                                    objInstalacion.ejecutar("");
                                                    /*nuevo 2018-11-29*/
                                                    sql.add("update tbl_contrato set id_instalacion='" + vec[1] + "' where id_contrato='" + idContrato + "';");
                                                    sql.add("delete from tbl_ips_libres where ips='" + ip.trim() + "' and id_sucursal=" + id_sucursal + ";");
                                                    sql.add("update tbl_orden_trabajo set id_instalacion='" + pk_instalacion[0] + "' where id_orden_trabajo='" + id_orden_trabajo + "';");
                                                    sql.add("update tbl_preinstalacion set id_instalacion='" + pk_instalacion[0] + "',estado='i' where id_preinstalacion='" + id_preinstalacion + "';");
                                                    this.transacciones(sql);

                                                    ///nuevo 2019-03-13
                                                    Pdf Objpdf = new Pdf(ips, puertos, db, usuario, clave);
                                                    Objpdf.PdfDocumental(dips, dpuertos, ddb, dusuario, dclave, dir, anexos);
                                                    Objpdf.Contratodocumental(response, usuario, clave, idContrato, num_contrato, id_sucursal, false, true, true);
                                                    Objpdf.cerrar();
                                                    ///nuevo 2019-03-13

                                                    objFacturaVenta.setDocumentoElectronico(id_factura, estadoDocumento, claveAcceso, autorizacionXml, respuestaAutoriz);
                                                    archivo.setArchivoDocumentalTexto(autorizacionXml, num_factura, "" + id_sucursal, "tbl_factura_venta", id_factura, "documentoxml", "public", "db_isp");
                                                    auditoria.setRegistro(usuarioSolucion, ipRemota, "INGRESO DEL NUEVA INSTALACION Nro. " + id_sucursal + "-" + num_instalacion);
                                                    r = "ok";
                                                } else {
                                                    r = "" + objInstalacion.getError();
                                                }
                                            } else {
                                                r = "Ha ocurrido un error en el proceso de generación de la factura electrónica.";
                                            }
                                        } else {
                                            r = "El número de factura " + serie_factura + "-" + num_factura + " ya ha sido emitido.";
                                        }

                                    }
                                }
                            }
                        } catch (IOException e) {
                            System.out.println("" + e.getMessage());
                        }

                    }
                }
            }
        } finally {
            /*nuevo 2018-11-29*/
            if (tmpid_instalacion.trim().compareTo("") == 0) {
                objInstalacion.ejecutar("delete from tbl_contrato where id_contrato='" + idContrato + "';");
            }
            /*nuevo 2018-11-29*/
            objInstalacion.cerrar();
            objPreFactura.cerrar();
            auditoria.cerrar();
            objFacturaVenta.cerrar();
            objContrato.cerrar();
            objPuntoEmision.cerrar();
            objIva.cerrar();
            objProducto.cerrar();
            pk_instalacion[1] = r;
        }
        return pk_instalacion;

    }

    private String getStringFromFile(String archivo) throws IOException {
        File file = new File(archivo);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        StringBuilder cadXml = new StringBuilder();
        String linea;
        while ((linea = br.readLine()) != null) {
            cadXml.append(linea);
        }
        return cadXml.toString();
    }

    ////////////
    public boolean UpdatePreinstalacion(String id, String id_sucursal, String cobrar, String tipo_convenio, String id_provincia, String id_ciudad, String id_parroquia,
            String id_sector, String tipo_instalacion, String id_plan_contratado, String costo_instalacion, String estado, String pre_direccion) {
        List sql = new ArrayList();
        sql.add("UPDATE tbl_preinstalacion"
                + " SET  id_sucursal='" + id_sucursal + "', id_provincia='" + id_provincia + "', id_ciudad='" + id_ciudad + "', id_parroquia='" + id_parroquia + "', id_sector='" + id_sector + "', id_plan='" + id_plan_contratado + "', id_tipoinstalacion='" + tipo_instalacion + "', cobrar='" + cobrar + "',"
                + " tipo_convenio='" + tipo_convenio + "',estado='" + estado + "',pre_direccion='" + pre_direccion + "'"
                + " WHERE id_preinstalacion='" + id + "';");
        return this.transacciones(sql);
    }

    public String CargarMapa(String latitud, String longitud) {
        String html = "";
        html += "<div id='divmapa' style='position: fixed; width: 1000px; height: 1000px; top: 0; left: 0; font-family:Verdana, Arial, Helvetica, sans-serif; font-size: 12px; font-weight: normal; border: #333333 3px solid; background-color: #FAFAFA; color: #000000; display:none;'>";
        html += "<iframe  src='https://138.185.137.120/antenas/maps/index.php' width='100%' height='500px'></iframe>";
        // html+="<object type=\"text/html\" data=\"http://192.168.88.76/antenas/maps/index.php\" width=\"1000px\" height=\"1000px\"> </object>";
        html += "</div>";
        return html;
    }

    public double redondear(double valorInicial, int numeroDecimales) {
        double parteEntera, resultado;
        resultado = valorInicial;
        parteEntera = Math.floor(resultado);
        resultado = (resultado - parteEntera) * Math.pow(10, numeroDecimales);
        resultado = Math.round(resultado);
        resultado = (resultado / Math.pow(10, numeroDecimales)) + parteEntera;
        return resultado;
    }

}
