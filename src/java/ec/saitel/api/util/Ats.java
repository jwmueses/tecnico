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
public class Ats extends DataBase {

    public Ats(String m, int p, String db, String u, String c) {
        super(m, p, db, u, c);
    }

    public ResultSet GetAts(String id) {
        return this.consulta("select * from vta_ats where id_ats='" + id + "';");
    }

    public ResultSet Get_equipo_trabajo(String id) {
        return this.consulta("select * from tbl_equipo_trabajo where id_ats='" + id + "';");
    }

    public ResultSet Get_permiso_proteccion(String id) {
        return this.consulta("select * from tbl_permiso_proteccion where id_ats='" + id + "';");
    }

    public ResultSet Get_secuencia_trabajo(String id) {
        return this.consulta("select * from tbl_secuencia_trabajo where id_ats='" + id + "';");
    }

    public String SetAts(String id_orden, String fecha, String sucursal, String tipo_orden, String duracion, String inicio_orden, String final_orden, String lugar,
            String observacion, String vehiculo, String placa, String empleado) {
        String pk = this.insert("INSERT INTO tbl_ats(id_orden_trabajo, fecha_ats, id_sucursal, id_tipo_trabajo, duracion_orden, inicio_orden, final_orden,"
                + " lugar_orden, observaciones_ats, vehiculo_ats, placa, id_empleado)"
                + " VALUES ('" + id_orden + "', '" + fecha + "', '" + sucursal + "', '" + tipo_orden + "', '" + duracion + "', '" + inicio_orden + "', '" + final_orden + "',"
                + " '" + lugar + "', '" + observacion + "', '" + vehiculo + "', '" + placa + "', '" + empleado + "');");
        return pk;
    }

    public String SetAts(String id_orden, String fecha, String sucursal, String tipo_orden, String duracion, String inicio_orden, String final_orden, String lugar,
            String observacion, String vehiculo, String placa, String empleado, String fecha_baja) {
        String pk = this.insert("INSERT INTO tbl_ats(id_orden_trabajo, fecha_ats, id_sucursal, id_tipo_trabajo, duracion_orden, inicio_orden, final_orden,"
                + " lugar_orden, observaciones_ats, vehiculo_ats, placa, id_empleado,fecha_baja)"
                + " VALUES ('" + id_orden + "', '" + fecha + "', '" + sucursal + "', '" + tipo_orden + "', '" + duracion + "', '" + inicio_orden + "', '" + final_orden + "',"
                + " '" + lugar + "', '" + observacion + "', '" + vehiculo + "', '" + placa + "', '" + empleado + "', '" + fecha_baja + "');");
        return pk;
    }

    public String Set_permiso_proteccion(String tabrajo_alturas, String analisis_riesgo, String procedimiento, String a_otro, String a_observacion, String epp_basico,
            String epp_trabajo, String b_otros, String b_observacion, String preguntas, String id_ats) {
        String preguntasv[] = preguntas.split(",");
        String pk = this.insert("INSERT INTO tbl_permiso_proteccion(a_trabajo_alturas, a_analisis_riesgo, a_procedimiento, a_otro, a_observacion,"
                + "b_epp_basico, b_epp_trabajo, b_otros, b_observacion, c_el_personal, c_es_necesario, c_este_trabajo, c_existe_alguna, c_observacion,"
                + "d_genera_residuosr, d_genera_residuosp, id_ats)"
                + "VALUES ('" + tabrajo_alturas + "', '" + analisis_riesgo + "', '" + procedimiento + "', '" + a_otro + "','" + a_observacion + "', '" + epp_basico + "',"
                + " '" + epp_trabajo + "', '" + b_otros + "', '" + b_observacion + "', '" + preguntasv[0] + "', '" + preguntasv[1] + "','" + preguntasv[2] + "',"
                + " '" + preguntasv[3] + "', '" + preguntasv[4] + "', '" + preguntasv[5] + "','" + preguntasv[6] + "', '" + id_ats + "');");
        return pk;
    }

    public String Set_secuencia_trabajo(String preguntas, String riesgos, String medidas, String id_ats) {
        String cadenasql = this.unircadenasql(preguntas, riesgos, medidas);
        String pk = this.insert("INSERT INTO tbl_secuencia_trabajo(revision, riesgo_1, medidas_1, chequeo, riesgo_2, medidas_2, movilizacion, riesgo_3,"
                + "medidas_3, inpeccion, riesgo_4, medidas_4, ascenso, riesgo_5, medidas_5, ubicacion, riesgo_6, medidas_6, acometida, riesgo_7, medidas_7,"
                + "entrega, riesgo_8, medidas_8, recogida, riesgo_9, medidas_9, orden, riesgo_10, medidas_10, id_ats)"
                + "VALUES (" + cadenasql + ", '" + id_ats + "');");
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
    }

    public String unircadenasql(String datos, String riesgos, String medidas) {
        String datosv[] = datos.split(",");
        String datosv1[] = riesgos.split(";");
        String datosv2[] = medidas.split(";");
        String cadena = "";
        for (int i = 0; i < datosv.length; i++) {
            cadena += "'" + datosv[i] + "','" + datosv1[i] + "','" + datosv2[i] + "',";
        }
        cadena = (cadena.trim().compareTo("") != 0 ? cadena.substring(0, cadena.length() - 1) : "");
        return cadena;
    }

    public boolean UpdateAts(String duracion, String inicio_orden, String final_orden, String lugar,
            String observacion, String vehiculo, String placa, String id_ats, String ats_valido) {
        boolean pk = true;
        List sql = new ArrayList();
        sql.add("UPDATE tbl_ats SET duracion_orden='" + duracion + "', inicio_orden='" + inicio_orden + "', final_orden='" + final_orden + "',"
                + "lugar_orden='" + lugar + "', observaciones_ats='" + observacion + "', vehiculo_ats='" + vehiculo + "', placa='" + placa + "', ats_valido='" + ats_valido + "'"
                + "WHERE id_ats='" + id_ats + "';");
        pk = this.transacciones(sql);
        return pk;
    }

    public boolean UpdateAtsestado(String estado, String id_ats) {
        boolean pk = true;
        List sql = new ArrayList();
        sql.add("UPDATE tbl_ats SET ats_valido='" + estado + "'"
                + "WHERE id_ats='" + id_ats + "';");
        pk = this.transacciones(sql);
        return pk;
    }

    public String Update_secuencia_trabajo(String preguntas, String riesgos, String medidas, String id_ats) {
        String cadenasql = this.unircadenasql(preguntas, riesgos, medidas);
        this.consulta("delete from tbl_secuencia_trabajo where id_ats='" + id_ats + "'");
        String pk = this.insert("INSERT INTO tbl_secuencia_trabajo(revision, riesgo_1, medidas_1, chequeo, riesgo_2, medidas_2, movilizacion, riesgo_3,"
                + "medidas_3, inpeccion, riesgo_4, medidas_4, ascenso, riesgo_5, medidas_5, ubicacion, riesgo_6, medidas_6, acometida, riesgo_7, medidas_7,"
                + "entrega, riesgo_8, medidas_8, recogida, riesgo_9, medidas_9, orden, riesgo_10, medidas_10, id_ats)"
                + "VALUES (" + cadenasql + ", '" + id_ats + "');");
        return pk;
    }

    public boolean Update_equipo_trabajo(String empleados, String id_ats) {
        boolean pk = true;
        if (empleados.trim().compareTo("") != 0) {
            List sql = new ArrayList();
            sql.add("delete from tbl_equipo_trabajo where id_ats='" + id_ats + "'");
            String empleadosv[] = empleados.split(",");
            for (int i = 0; i < empleadosv.length; i++) {
                sql.add("INSERT INTO tbl_equipo_trabajo(id_empleado, id_ats)VALUES ('" + empleadosv[i] + "', '" + id_ats + "');");
            }
            pk = this.transacciones(sql);
        }
        return pk;
    }

    public String Update_permiso_proteccion(String tabrajo_alturas, String analisis_riesgo, String procedimiento, String a_otro, String a_observacion, String epp_basico,
            String epp_trabajo, String b_otros, String b_observacion, String preguntas, String id_ats) {
        this.consulta("delete from tbl_permiso_proteccion where id_ats='" + id_ats + "'");
        String preguntasv[] = preguntas.split(",");
        String pk = this.insert("INSERT INTO tbl_permiso_proteccion(a_trabajo_alturas, a_analisis_riesgo, a_procedimiento, a_otro, a_observacion,"
                + "b_epp_basico, b_epp_trabajo, b_otros, b_observacion, c_el_personal, c_es_necesario, c_este_trabajo, c_existe_alguna, c_observacion,"
                + "d_genera_residuosr, d_genera_residuosp, id_ats)"
                + "VALUES ('" + tabrajo_alturas + "', '" + analisis_riesgo + "', '" + procedimiento + "', '" + a_otro + "','" + a_observacion + "', '" + epp_basico + "',"
                + " '" + epp_trabajo + "', '" + b_otros + "', '" + b_observacion + "', '" + preguntasv[0] + "', '" + preguntasv[1] + "','" + preguntasv[2] + "',"
                + " '" + preguntasv[3] + "', '" + preguntasv[4] + "', '" + preguntasv[5] + "','" + preguntasv[6] + "', '" + id_ats + "');");
        return pk;
    }

//    public String formularioats(int _altBody, String fecha_ats, String id_sucursal_matriz, String id_tipo_trabajo, String hora_actual, String id_empleado, ResultSet rsSucursales, ResultSet rsOrdenTrabajo) {
//        String html = "";
//        html += "<div id='formularioATS' style='width: 700px; height: " + _altBody + "px; top: 0; left: 0; font-family:Verdana, Arial, Helvetica, sans-serif; font-size: 12px; font-weight: normal;  color: #000000; display:none;'>";
//        html += "<table width='700' cellpadding='0' cellspacing='0'>"
//                + "<tr><td colspan='3'></td><td colspan='3'></td><td colspan='3'><span id='axMsP'></span></td></tr>"
//                + "<tr><td align='center' colspan='10'><b>ANÁLISIS DE &nbsp;&nbsp; TRABAJO &nbsp;&nbsp;  SEGURO  (ATS) </b></td>"
//                + "</tr>"
//                + "<tr><td>.</td></tr>"
//                + "<tr><td>.</td></tr>"
//                + "<tr><td>Fecha de Baja dd/mm/yyyy: <span class='marca'>*</span></td>"
//                + "<td colspan=2'>"
//                + "<input type='date' id='fechabaja' name='fechabaja' onkeypress=\"_evaluar(event, '0123456789/');\" value='' size='10' required >"
//                + "<input type='hidden' id='txtfechaats' name='txtfechaats' value='" + fecha_ats + "' size='15' readonly />"
//                + "</td>"
//                + "<td>Sucursal : <span class='marca'>*</span></td>"
//                + "<td colspan='2'>"
//                + "" + DatosDinamicos.combo(rsSucursales, "idsucats", id_sucursal_matriz, "", " EMPRESA ", 140) + ""
//                + "</td>"
//                + "<td>Trabajo a Realizar: <span class='marca'>*</span></td>"
//                + "<td colspan='2'>"
//                + "" + DatosDinamicos.combo(rsOrdenTrabajo, "tipoats", id_tipo_trabajo, "", " TODOS ", 140) + ""
//                + "</td></tr>"
//                + "<tr><td colspan='8'><br></td></tr>"
//                + "<tr><td>Duracion Estimada (Minutos) : <span class='marca'>*</span></td>"
//                + "<td colspan=2'>"
//                + "<input type='text' id='txtduracionats' name='txtduracionats' value='' size='15' onkeypress=\"_evaluar(event, '0123456789');\" required />"
//                + "</td>"
//                + "<td>Desde las : <span class='marca'>*</span></td>"
//                + "<td colspan='2'>"
//                + "<input type='time' id='txtinicioats' name='txtinicioats' value='" + hora_actual + "' size='15' max='23:59:00' min='00:01:00' required/>"
//                + "</td>"
//                + "<td>Hasta las : <span class='marca'>*</span></td>"
//                + "<td colspan='2'>"
//                + "<input type='time' id='txtfinalats' name='txtfinalats' value='" + hora_actual + "' size='15' max='23:59:00' min='00:01:00' required />"
//                + "</td></tr>"
//                + "<tr><td colspan='8'><br></td></tr>"
//                + "<tr><td>Lugar de trabajo : <span class='marca'>*</span></td>"
//                + "<td colspan=8'>"
//                + "<input type='text' id='txtlugarats' name='txtlugarats' value='' size='85' required/>"
//                + "</td></tr>"
//                + "<tr><td colspan='8'><br></td></tr>"
//                + "<tr><td>Observaciones : <span class='marca'>*</span></td>"
//                + "<td colspan=8'>"
//                + "<input type='text' id='txtobservacionats' name='txtobservacionats' value='' size='85' />"
//                + "</td></tr>"
//                + "<tr><td colspan='8'><br></td></tr>"
//                + "<tr><td>Vehiculo : <span class='marca'>*</span></td>"
//                + "<td colspan=3'>"
//                + "<input type='text' id='txtvehiculoats' name='txtvehiculoats' value='' size='30' required />"
//                + "</td>"
//                + "<td>Placa : <span class='marca'>*</span></td>"
//                + "<td colspan='3'>"
//                + "<input type='text' id='txtplacaats' name='txtplacaats' class='may' value='' size='30' required/>"
//                + "</td></tr>"
//                + "<tr><td colspan='8'><br></td></tr>"
//                + "<tr><td colspan='8'><hr></td></tr>"
//                + "<tr><td colspan='8'><br></td></tr>"
//                + "<tr><td colspan=4'><b>PERMISOS ADICIONALES ANTES DE INICIAR EL TRABAJO</b></td>"
//                + "<td colspan=4'><b>EQUIPO DE PROTECCIÓN PERSONAL</b></td></tr>"
//                + "<tr><td colspan='8'><br></td></tr>"
//                + "<tr><td colspan=2'>TRABAJOS EL ALTURAS</td>"
//                + "<td colspan=2'><input type='checkbox' id='txta_trabajo_alturas' name='txta_trabajo_alturas'></td>"
//                + "<td colspan=2'>EPP BASICO</td>"
//                + "<td colspan=2'><input type='checkbox' id='txtb_epp_basico' name='txtb_epp_basico'></tr>"
//                + "<tr><td colspan='8'><br></td></tr>"
//                + "<tr><td colspan=2'>ANÁLISI DE RIESGO OPERAACIONAL</td>"
//                + "<td colspan=2'><input type='checkbox' id='txta_analisis_riesgo' name='txta_analisis_riesgo'></td>"
//                + "<td colspan=2'>EPP TRABAJO EN ALTURAS</td>"
//                + "<td colspan=2'><input type='checkbox' id='txtb_epp_trabajo' name='txtb_epp_trabajo'></tr>"
//                + "<tr><td colspan='8'><br></td></tr>"
//                + "<tr><td colspan=2'>PROCEDIMINETO</td>"
//                + "<td colspan=2'><input type='checkbox' id='txta_procedimiento' name='txta_procedimiento'></td>"
//                + "<td colspan=2'>OTROS</td>"
//                + "<td colspan=2'><input type='checkbox' id='txtb_otros' name='txtb_otros'></tr>"
//                + "<tr><td colspan='8'><br></td></tr>"
//                + "<tr><td colspan=2'>OTROS</td>"
//                + "<td colspan=2'><input type='checkbox' id='txta_otros' name='txta_otros'></td>"
//                + "<td colspan=4'><input type='text' id='txtb_observacion' name='txtb_observacion' value='' size='30'/></td></tr>"
//                + "<tr><td colspan='8'><br></td></tr>"
//                + "<tr><td colspan=4'><input type='text' id='txta_observacion' name='txta_observacion' value='' size='30'/></td>"
//                + "<td colspan=4'>-</td></tr>"
//                + "<tr><td colspan='8'><br></td></tr>"
//                + "<tr><td colspan='8'><hr></td></tr>"
//                + "<tr><td colspan='8'><br></td></tr>"
//                + "<tr><td colspan='6'><b>NOTA: Si usted marco uno o más casilleros en las preguntas adicionales está conciente que:</b></td>"
//                + "<td colspan='1'><b>SI</b></td><td colspan='1'><b>NO</b></td></tr>"
//                + "<tr><td colspan='8'><br></td></tr>"
//                + "<tr><td colspan='6'>El personal que va ha realizar el trabajo ha recibido un entrenamiento adicional por parte de SAITEL</td>"
//                + "<td colspan='1'> <input type='radio' name='p1s' id='p1s' value='1'></td><td colspan='1'><input type='radio' name='p1s' id='p1s' value='0' checked ></td></tr>"
//                + "<tr><td colspan='8'><br></td></tr>"
//                + "<tr><td colspan='6'>Es necesario el uso de una escalera telescópica para realizar este  trabajo,  de longitud mayor a 6m.</td>"
//                + "<td colspan='1'> <input type='radio' name='p2s' id='p2s' value='1'></td><td colspan='1'><input type='radio' name='p2s' id='p2s' value='0' checked ></td></tr>"
//                + "<tr><td colspan='8'><br></td></tr>"
//                + "<tr><td colspan='6'>Este trabajo se lo tiene que realizar al menos con dos personas</td>"
//                + "<td colspan='1'> <input type='radio' name='p3s' id='p3s' value='1'></td><td colspan='1'><input type='radio' name='p3s' id='p3s' value='0' checked ></td></tr>"
//                + "<tr><td colspan='8'><br></td></tr>"
//                + "<tr><td colspan='6'>Existe alguna Condición Subestandar encontrada al momento,  para NO realizar este trabajo (especifique).</td>"
//                + "<td colspan='1'> <input type='radio' name='p4s' id='p4s' value='1'></td><td colspan='1'><input type='radio' name='p4s' id='p4s' value='0' checked ></td></tr>"
//                + "<tr><td colspan='8'><br></td></tr>"
//                + "<tr><td colspan='8'><input type='text' name='txtc_bservacion' id='txtc_bservacion' value='' size='100'/></td></tr>"
//                + "<tr><td colspan='8'><br></td></tr>"
//                + "<tr><td colspan='8'><hr></td></tr>"
//                + "<tr><td colspan='8'><br></td></tr>"
//                + "<tr><td colspan='3'><b>SECUENCIA DEL TRABAJO    (QUE HACE)</b></td>"
//                + "<td colspan='1'>SI&nbsp;&nbsp;&nbsp;NO</td>"
//                + "<td colspan='3'><b>RIESGO Y/O PELIGRO INVOLUCRADO (NÚMERO)</b></td>"
//                + "<td colspan='2'><b>MEDIDAS PREVENTIVAS (NÚMERO)</b></td></tr>"
//                + "<tr><td colspan='8'><br></td></tr>"
//                + "<tr><td colspan='3'><b>Revisión de los EPP´s, equipos, herramientas, materiales</b></td>"
//                + "<td colspan='1'><input type='radio' name='txtseciencia1si' id='txtseciencia1si' value='1' onClick=\"_('txtsecuencia1ri1').setAttribute('required', 'true');_('txtsecuencia1me1').setAttribute('required', 'true');\">&nbsp;&nbsp;&nbsp;<input type='radio' name='txtseciencia1si' id='txtseciencia1no' value='0' checked onClick=\"_('txtsecuencia1ri1').removeAttribute('required');_('txtsecuencia1me1').removeAttribute('required');\"></td>"
//                + "<td colspan='3'><input type='text' id='txtsecuencia1ri1' name='txtsecuencia1ri1' onkeypress=\"_evaluar(event, '0123456789,');\" onkeyup=\"habilitaropcionats('txtseciencia1si', 'txtseciencia1no','txtsecuencia1ri1','txtsecuencia1me1', 'txtsecuencia1ri1');\" value='' onclick=\"riesgos_ats('divriesgos','txtsecuencia1ri1',this.value,'txtseciencia1si','txtseciencia1no','txtsecuencia1me1');\"/></td>"
//                + "<td colspan='2'><input type='text' id='txtsecuencia1me1' name='txtsecuencia1me1' onkeypress=\"_evaluar(event, '0123456789,');\" onkeyup=\"habilitaropcionats('txtseciencia1si', 'txtseciencia1no','txtsecuencia1ri1','txtsecuencia1me1', 'txtsecuencia1me1');\" value='' onclick=\"medidas_ats('divriesgos','txtsecuencia1me1',this.value,'txtseciencia1si','txtseciencia1no','txtsecuencia1ri1');\"/></td></tr>"
//                + "<tr><td colspan='8'><br></td></tr>"
//                + "<tr><td colspan='3'><b>Chequeo  vehicular</b></td>"
//                + "<td colspan='1'><input type='radio' name='txtseciencia2si' id='txtseciencia2si' value='1' onClick=\"_('txtsecuencia1ri2').setAttribute('required', 'true');_('txtsecuencia1me2').setAttribute('required', 'true');\">&nbsp;&nbsp;&nbsp;<input type='radio' name='txtseciencia2si' id='txtseciencia2no' value='0' checked onClick=\"_('txtsecuencia1ri2').removeAttribute('required');_('txtsecuencia1me2').removeAttribute('required');\"></td>"
//                + "<td colspan='3'><input type='text' id='txtsecuencia1ri2' name='txtsecuencia1ri2' onkeypress=\"_evaluar(event, '0123456789,');\" onkeyup=\"habilitaropcionats('txtseciencia2si', 'txtseciencia2no','txtsecuencia1ri2','txtsecuencia1me2', 'txtsecuencia1ri2');\" value='' onclick=\"riesgos_ats('divriesgos','txtsecuencia1ri2',this.value,'txtseciencia2si','txtseciencia2no','txtsecuencia1me2');\"/></td>"
//                + "<td colspan='2'><input type='text' id='txtsecuencia1me2' name='txtsecuencia1me2' onkeypress=\"_evaluar(event, '0123456789,');\" onkeyup=\"habilitaropcionats('txtseciencia2si', 'txtseciencia2no','txtsecuencia1ri2','txtsecuencia1me2', 'txtsecuencia1me2');\" value='' onclick=\"medidas_ats('divriesgos','txtsecuencia1me2',this.value,'txtseciencia2si','txtseciencia2no','txtsecuencia1ri2');\"/></td></tr>"
//                + "<tr><td colspan='8'><br></td></tr>"
//                + "<tr><td colspan='3'><b>Movilización al sitio de trabajo</b></td>"
//                + "<td colspan='1'><input type='radio' name='txtseciencia3si' id='txtseciencia3si' value='1' onClick=\"_('txtsecuencia1ri3').setAttribute('required', 'true');_('txtsecuencia1me3').setAttribute('required', 'true');\">&nbsp;&nbsp;&nbsp;<input type='radio' name='txtseciencia3si' id='txtseciencia3no' value='0' checked onClick=\"_('txtsecuencia1ri3').removeAttribute('required');_('txtsecuencia1me3').removeAttribute('required');\"></td>"
//                + "<td colspan='3'><input type='text' id='txtsecuencia1ri3' name='txtsecuencia1ri3' onkeypress=\"_evaluar(event, '0123456789,');\" onkeyup=\"habilitaropcionats('txtseciencia3si', 'txtseciencia3no','txtsecuencia1ri3','txtsecuencia1me3', 'txtsecuencia1ri3');\" value='' onclick=\"riesgos_ats('divriesgos','txtsecuencia1ri3',this.value,'txtseciencia3si','txtseciencia3no','txtsecuencia1me3');\"/></td>"
//                + "<td colspan='2'><input type='text' id='txtsecuencia1me3' name='txtsecuencia1me3' onkeypress=\"_evaluar(event, '0123456789,');\" onkeyup=\"habilitaropcionats('txtseciencia3si', 'txtseciencia3no','txtsecuencia1ri3','txtsecuencia1me3', 'txtsecuencia1me3');\" value='' onclick=\"medidas_ats('divriesgos','txtsecuencia1me3',this.value,'txtseciencia3si','txtseciencia3no','txtsecuencia1ri3');\"/></td></tr>"
//                + "<tr><td colspan='8'><br></td></tr>"
//                + "<tr><td colspan='3'><b>Inspección del sitio</b></td>"
//                + "<td colspan='1'><input type='radio' name='txtseciencia4si' id='txtseciencia4si' value='1' onClick=\"_('txtsecuencia1ri4').setAttribute('required', 'true');_('txtsecuencia1me4').setAttribute('required', 'true');\">&nbsp;&nbsp;&nbsp;<input type='radio' name='txtseciencia4si' id='txtseciencia4no' value='0' checked onClick=\"_('txtsecuencia1ri4').removeAttribute('required');_('txtsecuencia1me4').removeAttribute('required');\"></td>"
//                + "<td colspan='3'><input type='text' id='txtsecuencia1ri4' name='txtsecuencia1ri4' onkeypress=\"_evaluar(event, '0123456789,');\" onkeyup=\"habilitaropcionats('txtseciencia4si', 'txtseciencia4no','txtsecuencia1ri4','txtsecuencia1me4', 'txtsecuencia1ri4');\" value='' onclick=\"riesgos_ats('divriesgos','txtsecuencia1ri4',this.value,'txtseciencia4si','txtseciencia4no','txtsecuencia1me4');\"/></td>"
//                + "<td colspan='2'><input type='text' id='txtsecuencia1me4' name='txtsecuencia1me4' onkeypress=\"_evaluar(event, '0123456789,');\" onkeyup=\"habilitaropcionats('txtseciencia4si', 'txtseciencia4no','txtsecuencia1ri4','txtsecuencia1me4', 'txtsecuencia1me4');\" value='' onclick=\"medidas_ats('divriesgos','txtsecuencia1me4',this.value,'txtseciencia4si','txtseciencia4no','txtsecuencia1ri4');\"/></td></tr>"
//                + "<tr><td colspan='8'><br></td></tr>"
//                + "<tr><td colspan='3'><b>Ascenso / Descenso / Torre / Escalera</b></td>"
//                + "<td colspan='1'><input type='radio' name='txtseciencia5si' id='txtseciencia5si' value='1' onClick=\"_('txtsecuencia1ri5').setAttribute('required', 'true');_('txtsecuencia1me5').setAttribute('required', 'true');\">&nbsp;&nbsp;&nbsp;<input type='radio' name='txtseciencia5si' id='txtseciencia5no' value='0' checked onClick=\"_('txtsecuencia1ri5').removeAttribute('required');_('txtsecuencia1me5').removeAttribute('required');\"></td>"
//                + "<td colspan='3'><input type='text' id='txtsecuencia1ri5' name='txtsecuencia1ri5' onkeypress=\"_evaluar(event, '0123456789,');\" onkeyup=\"habilitaropcionats('txtseciencia5si', 'txtseciencia5no','txtsecuencia1ri5','txtsecuencia1me5', 'txtsecuencia1ri5');\" value='' onclick=\"riesgos_ats('divriesgos','txtsecuencia1ri5',this.value,'txtseciencia5si','txtseciencia5no','txtsecuencia1me5');\"/></td>"
//                + "<td colspan='2'><input type='text' id='txtsecuencia1me5' name='txtsecuencia1me5' onkeypress=\"_evaluar(event, '0123456789,');\" onkeyup=\"habilitaropcionats('txtseciencia5si', 'txtseciencia5no','txtsecuencia1ri5','txtsecuencia1me5', 'txtsecuencia1me5');\" value='' onclick=\"medidas_ats('divriesgos','txtsecuencia1me5',this.value,'txtseciencia5si','txtseciencia5no','txtsecuencia1ri5');\"/></td></tr>"
//                + "<tr><td colspan='8'><br></td></tr>"
//                + "<tr><td colspan='3'><b>Ubicación e instalación de la antena/Trabajo en infraestructura</b></td>"
//                + "<td colspan='1'><input type='radio' name='txtseciencia6si' id='txtseciencia6si' value='1' onClick=\"_('txtsecuencia1ri6').setAttribute('required', 'true');_('txtsecuencia1me6').setAttribute('required', 'true');\">&nbsp;&nbsp;&nbsp;<input type='radio' name='txtseciencia6si' id='txtseciencia6no' value='0' checked onClick=\"_('txtsecuencia1ri6').removeAttribute('required');_('txtsecuencia1me6').removeAttribute('required');\"></td>"
//                + "<td colspan='3'><input type='text' id='txtsecuencia1ri6' name='txtsecuencia1ri6' onkeypress=\"_evaluar(event, '0123456789,');\" onkeyup=\"habilitaropcionats('txtseciencia6si', 'txtseciencia6no','txtsecuencia1ri6','txtsecuencia1me6', 'txtsecuencia1ri6');\" value='' onclick=\"riesgos_ats('divriesgos','txtsecuencia1ri6',this.value,'txtseciencia6si','txtseciencia6no','txtsecuencia1me6');\"/></td>"
//                + "<td colspan='2'><input type='text' id='txtsecuencia1me6' name='txtsecuencia1me6' onkeypress=\"_evaluar(event, '0123456789,');\" onkeyup=\"habilitaropcionats('txtseciencia6si', 'txtseciencia6no','txtsecuencia1ri6','txtsecuencia1me6', 'txtsecuencia1me6');\" value='' onclick=\"medidas_ats('divriesgos','txtsecuencia1me6',this.value,'txtseciencia6si','txtseciencia6no','txtsecuencia1ri6');\"/></td></tr>"
//                + "<tr><td colspan='8'><br></td></tr>"
//                + "<tr><td colspan='3'><b>Acometida y cableado al punto de distribución</b></td>"
//                + "<td colspan='1'><input type='radio' name='txtseciencia7si' id='txtseciencia7si' value='1' onClick=\"_('txtsecuencia1ri7').setAttribute('required', 'true');_('txtsecuencia1me7').setAttribute('required', 'true');\">&nbsp;&nbsp;&nbsp;<input type='radio' name='txtseciencia7si' id='txtseciencia7no' value='0' checked onClick=\"_('txtsecuencia1ri7').removeAttribute('required');_('txtsecuencia1me7').removeAttribute('required');\"></td>"
//                + "<td colspan='3'><input type='text' id='txtsecuencia1ri7' name='txtsecuencia1ri7' onkeypress=\"_evaluar(event, '0123456789,');\" onkeyup=\"habilitaropcionats('txtseciencia7si', 'txtseciencia7no','txtsecuencia1ri7','txtsecuencia1me7', 'txtsecuencia1ri7');\" value='' onclick=\"riesgos_ats('divriesgos','txtsecuencia1ri7',this.value,'txtseciencia7si','txtseciencia7no','txtsecuencia1me7');\"/></td>"
//                + "<td colspan='2'><input type='text' id='txtsecuencia1me7' name='txtsecuencia1me7' onkeypress=\"_evaluar(event, '0123456789,');\" onkeyup=\"habilitaropcionats('txtseciencia7si', 'txtseciencia7no','txtsecuencia1ri7','txtsecuencia1me7', 'txtsecuencia1me7');\" value='' onclick=\"medidas_ats('divriesgos','txtsecuencia1me7',this.value,'txtseciencia7si','txtseciencia7no','txtsecuencia1ri7');\"/></td></tr>"
//                + "<tr><td colspan='8'><br></td></tr>"
//                + "<tr><td colspan='3'><b>Entrega del servicio de internet al cliente</b></td>"
//                + "<td colspan='1'><input type='radio' name='txtseciencia8si' id='txtseciencia8si' value='1' onClick=\"_('txtsecuencia1ri8').setAttribute('required', 'true');_('txtsecuencia1me8').setAttribute('required', 'true');\">&nbsp;&nbsp;&nbsp;<input type='radio' name='txtseciencia8si' id='txtseciencia8no' value='0' checked onClick=\"_('txtsecuencia1ri8').removeAttribute('required');_('txtsecuencia1me8').removeAttribute('required');\"></td>"
//                + "<td colspan='3'><input type='text' id='txtsecuencia1ri8' name='txtsecuencia1ri8' onkeypress=\"_evaluar(event, '0123456789,');\" onkeyup=\"habilitaropcionats('txtseciencia8si', 'txtseciencia8no','txtsecuencia1ri8','txtsecuencia1me8', 'txtsecuencia1ri8');\" value='' onclick=\"riesgos_ats('divriesgos','txtsecuencia1ri8',this.value,'txtseciencia8si','txtseciencia8no','txtsecuencia1me8');\"/></td>"
//                + "<td colspan='2'><input type='text' id='txtsecuencia1me8' name='txtsecuencia1me8' onkeypress=\"_evaluar(event, '0123456789,');\" onkeyup=\"habilitaropcionats('txtseciencia8si', 'txtseciencia8no','txtsecuencia1ri8','txtsecuencia1me8', 'txtsecuencia1me8');\" value='' onclick=\"medidas_ats('divriesgos','txtsecuencia1me8',this.value,'txtseciencia8si','txtseciencia8no','txtsecuencia1ri8');\"/></td></tr>"
//                + "<tr><td colspan='8'><br></td></tr>"
//                + "<tr><td colspan='3'><b>Recogida de equipos, herraminetas, materiales y desechos</b></td>"
//                + "<td colspan='1'><input type='radio' name='txtseciencia9si' id='txtseciencia9si' value='1' onClick=\"_('txtsecuencia1ri9').setAttribute('required', 'true');_('txtsecuencia1me9').setAttribute('required', 'true');\">&nbsp;&nbsp;&nbsp;<input type='radio' name='txtseciencia9si' id='txtseciencia9no' value='0' checked onClick=\"_('txtsecuencia1ri9').removeAttribute('required');_('txtsecuencia1me9').removeAttribute('required');\"></td>"
//                + "<td colspan='3'><input type='text' id='txtsecuencia1ri9' name='txtsecuencia1ri9' onkeypress=\"_evaluar(event, '0123456789,');\" onkeyup=\"habilitaropcionats('txtseciencia9si', 'txtseciencia9no','txtsecuencia1ri9','txtsecuencia1me9', 'txtsecuencia1ri9');\" value='' onclick=\"riesgos_ats('divriesgos','txtsecuencia1ri9',this.value,'txtseciencia9si','txtseciencia9no','txtsecuencia1me9');\"/></td>"
//                + "<td colspan='2'><input type='text' id='txtsecuencia1me9' name='txtsecuencia1me9' onkeypress=\"_evaluar(event, '0123456789,');\" onkeyup=\"habilitaropcionats('txtseciencia9si', 'txtseciencia9no','txtsecuencia1ri9','txtsecuencia1me9', 'txtsecuencia1me9');\" value='' onclick=\"medidas_ats('divriesgos','txtsecuencia1me9',this.value,'txtseciencia9si','txtseciencia9no','txtsecuencia1ri9');\"/></td></tr>"
//                + "<tr><td colspan='8'><br></td></tr>"
//                + "<tr><td colspan='3'><b>Orden y limpieza del lugar de trabajo</b></td>"
//                + "<td colspan='1'><input type='radio' name='txtseciencia10si' id='txtseciencia10si' value='1' onClick=\"_('txtsecuencia1ri10').setAttribute('required', 'true');_('txtsecuencia1me10').setAttribute('required', 'true');\">&nbsp;&nbsp;&nbsp;<input type='radio' name='txtseciencia10si' id='txtseciencia10no' value='0' checked onClick=\"_('txtsecuencia1ri10').removeAttribute('required');_('txtsecuencia1me10').removeAttribute('required');\"></td>"
//                + "<td colspan='3'><input type='text' id='txtsecuencia1ri10' name='txtsecuencia1ri10' onkeypress=\"_evaluar(event, '0123456789,');\" onkeyup=\"habilitaropcionats('txtseciencia10si', 'txtseciencia10no','txtsecuencia1ri10','txtsecuencia1me10', 'txtsecuencia1ri10');\" value='' onclick=\"riesgos_ats('divriesgos','txtsecuencia1ri10',this.value,'txtseciencia10si','txtseciencia10no','txtsecuencia1me10');\"/></td>"
//                + "<td colspan='2'><input type='text' id='txtsecuencia1me10' name='txtsecuencia1me10' onkeypress=\"_evaluar(event, '0123456789,');\" onkeyup=\"habilitaropcionats('txtseciencia10si', 'txtseciencia10no','txtsecuencia1ri10','txtsecuencia1me10', 'txtsecuencia1me10');\" value='' onclick=\"medidas_ats('divriesgos','txtsecuencia1me10',this.value,'txtseciencia10si','txtseciencia10no','txtsecuencia1ri10');\"/></td></tr>"
//                + "<tr><td colspan='8'><br></td></tr>"
//                + "<tr><td colspan='8'><hr></td></tr>"
//                + "<tr><td colspan='8'><br></td></tr>"
//                + "<tr><td colspan='4' align='center'><b>GESTIÓN AMBIENTAL/MANEJO DE DESECHOS</b></td>"
//                + "<td colspan='4' align='center'>SI&nbsp;&nbsp;&nbsp;NO</td></tr>"
//                + "<tr><td colspan='8'><br></td></tr>"
//                + "<tr><td colspan='4' align='center'><b>1.¿Generas reciduos reciclables?</b></td>"
//                + "<td colspan='4' align='center'><input type='radio' name='txtresiduos1' id='txtresiduos1' value='1'>&nbsp;&nbsp;&nbsp;<input type='radio' name='txtresiduos1' id='txtresiduos1' value='0' checked ></td></tr>"
//                + "<tr><td colspan='8'><br></td></tr>"
//                + "<tr><td colspan='4' align='center'><b>2.¿Generas reciduos  peligrosos?</b></td>"
//                + "<td colspan='4' align='center'><input type='radio' name='txtresiduos2' id='txtresiduos2' value='1'>&nbsp;&nbsp;&nbsp;<input type='radio' name='txtresiduos2' id='txtresiduos2' value='0' checked ></td></tr>"
//                + "<tr><td colspan='8'><br></td></tr>"
//                + "<tr><td colspan='8'><hr></td></tr>"
//                + "<tr><td colspan='8'><br></td></tr>";
//
//        String dniats = this.getDatosEmpleado("dni", id_empleado, "");
//        String nombresats = this.getDatosEmpleado("nombre", id_empleado, "");
//        String apellidosats = this.getDatosEmpleado("apellido", id_empleado, "");
//        html += "<tr><td colspan='8' align='center'><b>EQUIPO DE TRABAJO</b></td></tr>"
//                + "<tr><td colspan='8'><br></td></tr>"
//                + "<tr><td colspan='4' align='center'><b>DNI ENCARGADO</b></td><td colspan='4' align='center'><b>EMPLEADO</b></td></tr>"
//                + "<tr><td colspan='8'><br></td></tr>"
//                + "<tr><td colspan='4' align='center'>" + dniats + "</td><td colspan='4' align='center'>" + nombresats + " " + apellidosats + "</td></tr>"
//                + "<tr><td colspan='8'><br></td></tr>"
//                + "<tr><td colspan='8' align='center'>";
//
//        ////
//        html += "<input type='hidden' id='idats' name='idats' value='-1' /><input type='hidden' id='contempleadoats' name='contempleadoats' value='0' /><input type='text' id='txtempleadosb' name='txtempleadosb' onkeypress=\"addempleadoATS(this.value,'axempleado','divempleados','txtempleadosb','tblDempleado','rmempleado');\" value='' size='40' />";
//        html += "<div id='divempleados' style='position: fixed; width: 350px; height: 290px; top: 0; left: 0; font-family:Verdana, Arial, Helvetica, sans-serif; font-size: 12px; font-weight: normal; border: #333333 3px solid; background-color: #FAFAFA; color: #000000; display:none;'></div>";
//        html += "<table cellpadding='0' cellspacing='0' width='330'>"
//                + "<TH class='jm_TH' width='150'>Empleados</TH>"
//                + "<TH class='jm_TH' width='150'>DNI</TH>"
//                + "<TH class='jm_TH' width='30'></th></table>"
//                + "<DIV id='sll0empleado' style='overflow:auto;width:350px;height:150px;'>"
//                + "<TABLE class='jm_tabla' cellspacing='1' cellpadding='0'><tbody id='tblDempleado'>"
//                + "</tbody></table></div>";
//        ///
//
//        html += "</td></tr>"
//                + "<tr><td colspan='8'><br></td></tr>"
//                + "<tr><td colspan='8' align='center'><input type='button' value='Terminar de Llenar ATS' onclick=\"ocultarVentana_form('formularioATS','Esta Seguro de Terminar el ATS','s','idats','1');\" /></td></tr>"
//                + "</table>";
//
//        html += "<div id='divriesgos' style='position: fixed; width: 350px; height: 310px; top: 0; left: 0; font-family:Verdana, Arial, Helvetica, sans-serif; font-size: 12px; font-weight: normal; border: #333333 3px solid; background-color: #FAFAFA; color: #000000; display:none;'></div>";
//
//        ResultSet rsempleados = this.getEmpleadosATS();
//        String jsonempleados = this.getJSON(rsempleados);
//
//        html += "<div style=\"display:none\" id='axempleado'>" + jsonempleados + "</div>";
//
//        ////
//        html += "</div>";
//        return html;
//    }

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

    public ResultSet getEmpleadosATS() {
        return this.consulta("select id_empleado,nombre ||' '|| apellido,dni from tbl_empleado where estado=true and eliminado=false");
    }

    ////////////
    public boolean UpdateAtsestado(String estado, String id_ats, String observaciones) {
        boolean pk = true;
        List sql = new ArrayList();
        sql.add("UPDATE tbl_ats SET ats_valido='" + estado + "',observaciones='" + observaciones + "'"
                + "WHERE id_ats='" + id_ats + "';");
        pk = this.transacciones(sql);
        return pk;
    }

    public ResultSet retornoSql(String wh) {
        return this.consulta(wh);
    }
}
