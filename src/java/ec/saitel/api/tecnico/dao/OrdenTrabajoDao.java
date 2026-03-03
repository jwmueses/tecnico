/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.saitel.api.tecnico.dao;

import ec.gob.sri.FirmaXadesBes;
import ec.saitel.api.tecnico.model.MaterialesModel;
import ec.saitel.api.tecnico.model.OrdenTrabajoModel;
import ec.saitel.api.tecnico.model.ReposicionEquiposModel;
import ec.saitel.api.util.Activo;
import ec.saitel.api.util.Addons;
import ec.saitel.api.util.Archivo;
import ec.saitel.api.util.Auditoria;
import ec.saitel.api.util.Bodega;
import ec.saitel.api.util.Cadena;
import ec.saitel.api.util.Cliente;
import ec.saitel.api.util.Configuracion;
import ec.saitel.api.util.DataBase;
import ec.saitel.api.util.Documento;
import ec.saitel.api.util.FacturaElectronica;
import ec.saitel.api.util.FacturaVenta;
import ec.saitel.api.util.Fecha;
import ec.saitel.api.util.FormaPago;
import ec.saitel.api.util.Instalacion;
import ec.saitel.api.util.Matriz;
import ec.saitel.api.util.Mikrotik;
import ec.saitel.api.util.OrdenTrabajo;
import ec.saitel.api.util.Parametro;
import ec.saitel.api.util.PdfDocumental;
import ec.saitel.api.util.PreFactura;
import ec.saitel.api.util.Producto;
import ec.saitel.api.util.Promocion;
import ec.saitel.api.util.Sucursal;
import ec.saitel.api.util.Utiles;
import ec.saitel.api.util.firma.CordenadasXY;
import ec.saitel.api.util.firma.Firmas;
import ec.saitel.api.util.firma.Utils;
import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author Sistemas
 */
public class OrdenTrabajoDao 
{
    public String guardar(OrdenTrabajoModel ordenTrabajo)
    {
        
        String r = "Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o contáctese con el administrador del sistema para mayor información.";

        try {
            
            Parametro.setUsuario( ordenTrabajo.getUsuarioSolucion() );
            Parametro.setClave( ordenTrabajo.getClaveUsuario() );
    //        String _DOCS_ELECTRONICOS = Parametro.getDocsElectronicos();
            int id_sucursal = Integer.parseInt( ordenTrabajo.getIdSucursal() );
            int id_empleado = Integer.parseInt( ordenTrabajo.getIdEmpleado() );
            String firma_ordenes = "false";
            boolean obligado_firmar = true;

    //      PARA LA PERSONALIZACIONE DE ACTIVOS
            /*Configuracion conf = new Configuracion(Parametro._ip, Parametro._puerto, Parametro._db, usuario, clave);
             String ruc_empresa = conf.getValor("ruc");
             String empresa = conf.getValor("razon_social");
             conf.cerrar();*/
            String observacion = "";
            String auxR = "";
            Documento objDocumento = new Documento( Parametro.getIp(), Parametro.getPuerto(), Parametro.getDb(), Parametro.getUsuario(), Parametro.getClave() );
            try {
                ResultSet rsDoc = objDocumento.getDocumento("p");
                if (rsDoc.next()) {
                    observacion = rsDoc.getString("documento") != null ? rsDoc.getString("documento") : "";
                    rsDoc.close();
                }
            } catch (Exception e) {
                auxR = e.getMessage();
                e.printStackTrace();
            } finally {
                objDocumento.cerrar();
            }

            
            
            r = "NO SE HA PODIDO OBTENER EL TEXTO DE ACEPTACION DE LA PERSONALIZACION. " + auxR;
            if( observacion.compareTo("")!=0 ) {
                
                Archivo archivo = new Archivo( Parametro.getDocIp(), Parametro.getDocPpuerto(), Parametro.getDocDb(), Parametro.getDocUsuario(), Parametro.getDocClave() );
                Activo objActivo = new Activo(Parametro.getIp(), Parametro.getPuerto(), Parametro.getDb(), Parametro.getUsuario(), Parametro.getClave());
                long num_documento = objActivo.getNunDocumento();

                Bodega objBodega = new Bodega(Parametro.getIp(), Parametro.getPuerto(), Parametro.getDb(), Parametro.getUsuario(), Parametro.getClave());

                Auditoria auditoria = new Auditoria(Parametro.getIp(), Parametro.getPuerto(), Parametro.getDb(), Parametro.getUsuario(), Parametro.getClave());

                Instalacion objInstalacion = new Instalacion(Parametro.getIp(), Parametro.getPuerto(), Parametro.getDb(), Parametro.getUsuario(), Parametro.getClave());

                Producto objProducto = new Producto(Parametro.getIp(), Parametro.getPuerto(), Parametro.getDb(), Parametro.getUsuario(), Parametro.getClave());
                
                Promocion objPromocion = new Promocion(Parametro.getIp(), Parametro.getPuerto(), Parametro.getDb(), Parametro.getUsuario(), Parametro.getClave());

                PreFactura objPrefactura = new PreFactura(Parametro.getIp(), Parametro.getPuerto(), Parametro.getDb(), Parametro.getUsuario(), Parametro.getClave());
                OrdenTrabajo objOrdenTrabajo = new OrdenTrabajo(Parametro.getIp(), Parametro.getPuerto(), Parametro.getDb(), Parametro.getUsuario(), Parametro.getClave());

        //        Ats objAts = new Ats(Parametro.getIp(), Parametro.getPuerto(), Parametro.getDb(), Parametro.getUsuario(), Parametro.getClave());
        //        Preinstalacion objPreinstalacion = new Preinstalacion(Parametro.getIp(), Parametro.getPuerto(), Parametro.getDb(), Parametro.getUsuario(), Parametro.getClave());

                Configuracion conf = new Configuracion(Parametro.getIp(), Parametro.getPuerto(), Parametro.getDb(), Parametro.getUsuario(), Parametro.getClave());
                String modoSincronizacionMikrotiks = conf.getValor("modoSincronizacionMikrotiks");
        //        String idPCcostoVentas = conf.getValor("costo_ventas");


                try {
        //            String pos = request.getParameter("pos"); // posicion de la orden de trabjao
                    String id_tecnico_resp = ordenTrabajo.getIdEmpleado();
                    String bodega_movil[] = objBodega.getBodegaResponsableTecnico(id_tecnico_resp);

                    String puesta_tierra = ordenTrabajo.getInstalacion().getPuestaTierra();
        //            String numero_orden = ordenTrabajo.getNumOrden();
                    String id_orden_trabajo = ordenTrabajo.getIdOrdenTrabajo();
                    String id_instalacion = ordenTrabajo.getIdInstalacion();
                    String tipo_trabajo = ordenTrabajo.getTipoTrabajo();
                    String id_cliente = ordenTrabajo.getInstalacion().getIdCliente();
                    String ruc = ordenTrabajo.getInstalacion().getRuc();
                    String razon_social = ordenTrabajo.getInstalacion().getRazonSocial();
                    String solucionado = ordenTrabajo.getSolucionado();
                    String conformidad = "NULL";
                    String atencion = "NULL";
                    String recomendacion = ordenTrabajo.getRecomendacion();
                    String id_sector = ordenTrabajo.getInstalacion().getIdSector();
                    String ip = ordenTrabajo.getInstalacion().getIp();
                    String tipo_instalacion = ordenTrabajo.getInstalacion().getTipoInstalacion();
                    String id_plan_actual = ordenTrabajo.getInstalacion().getIdPlanEstablecido();
                    String direccion = ordenTrabajo.getInstalacion().getDireccionInstalacion();
                    String mac_ant = ordenTrabajo.getMacAnterior().toUpperCase();
                    String receptor_ant = ordenTrabajo.getReceptorAnterior();
                    String equiposRetirados = ordenTrabajo.getEquiposRetirados();
                    String equiposUtilizados = ordenTrabajo.getEquiposUtilizados();

                    String mac_nuevo = ordenTrabajo.getInstalacion().getMac().toUpperCase();
                    String receptor_nuevo = ordenTrabajo.getInstalacion().getReceptor();
                    String set_deviceclave = ordenTrabajo.getInstalacion().getSetDeviceClave();
                    String capacidad_efectiva = ordenTrabajo.getCapacidadEfectiva();
                    String porcentaje_senal = ordenTrabajo.getInstalacion().getPorcentajeSenal();
                    porcentaje_senal = (porcentaje_senal.trim().compareTo("") != 0 ? porcentaje_senal : "0");
                    String antena_acoplada = ordenTrabajo.getInstalacion().getAntenaAcoplada();
        //            String lat_h = request.getParameter("lat_h").compareTo("")!=0 ? request.getParameter("lat_h") : "0";
        //            String lat_m = request.getParameter("lat_m").compareTo("")!=0 ? request.getParameter("lat_m") : "0";
        //            String lat_s = request.getParameter("lat_s").compareTo("")!=0 ? request.getParameter("lat_s") : "0";
        //            String lat_o = request.getParameter("lat_o");
        //            String lon_h = request.getParameter("lon_h").compareTo("")!=0 ? request.getParameter("lon_h") : "0";
        //            String lon_m = request.getParameter("lon_m").compareTo("")!=0 ? request.getParameter("lon_m") : "0";
        //            String lon_s = request.getParameter("lon_s").compareTo("")!=0 ? request.getParameter("lon_s") : "0";
        //            String lon_o = request.getParameter("lon_o");
                    String altura = ordenTrabajo.getInstalacion().getAltura();
                    String altura_antena = ordenTrabajo.getInstalacion().getAlturaAntena();
                    String num_instalacion = ordenTrabajo.getInstalacion().getNumInstalacion();
                    String latitud = ordenTrabajo.getInstalacion().getLatitud();
                    String longitud = ordenTrabajo.getInstalacion().getLongitud();
        //            String ax_latitud_gps = String.valueOf( Integer.parseInt(lat_h) + ( Float.parseFloat(lat_m) / 60.0) + ( Float.parseFloat(lat_s) / 3600.0) ); 
        //            String ax_longitud_gps =  "-" + ( Integer.parseInt(lon_h) + ( Float.parseFloat(lon_m) / 60.0) + ( Float.parseFloat(lon_s) / 3600.0) ); 
                    String longitud_gps = ordenTrabajo.getInstalacion().getLatitudGps();
                    String latitud_gps = ordenTrabajo.getInstalacion().getLongitudGps();
                    String idproductos = "";
                    String materiales = "";
                    String cantidades = "";
                    String vel_carga = ordenTrabajo.getVelocidadCarga();
                    vel_carga = (vel_carga.trim().compareTo("") != 0 ? vel_carga : "0");
                    String vel_descarga = ordenTrabajo.getVelocidadDescarga();
                    vel_descarga = (vel_descarga.trim().compareTo("") != 0 ? vel_descarga : "0");
                    String costos_sector[][] = objOrdenTrabajo.CostosSector(id_sector);
        //            String id_preinstalacion = request.getParameter("id_preinstalacion");
        //            String pre_hecha = (request.getParameter("pre_hecha") != null ? request.getParameter("pre_hecha") : "true");
        //            String cobrar = request.getParameter("cobrar");
        //            cobrar = cobrar.trim().toUpperCase();
        //            String id_promocion = request.getParameter("id_promocion");
                    String procedente = ordenTrabajo.getOtProcedente();
                    String id_bodega_empleado = ordenTrabajo.getIdBodegaEmpleado();

                    String firmar_documentos = "si";
                    String usuario_key = ordenTrabajo.getUsuarioKey();
        //            // cuando es PRE-instalacion
        //            if (tipo_trabajo.trim().compareTo("20") == 0 && id_preinstalacion.trim().compareTo("-1") != 0 && id_preinstalacion.trim().compareTo("") != 0) {
        //                if (pre_hecha.trim().compareTo("true") == 0) {
        //                    String pk_instalacion[] = objPreinstalacion.crearinstalacion(Parametro.getIp(), Parametro.getPuerto(), Parametro.getDb(), Parametro.getUsuario(), Parametro.getClave(), ordenTrabajo.getUsuarioSolucion(), ordenTrabajo.getRemoteAddr(),
        //                            _DOCS_ELECTRONICOS, id_sucursal, id_preinstalacion, ip, id_sector, id_plan_actual, id_orden_trabajo,
        //                            id_instalacion, cobrar, id_promocion, archivo, Parametro.getDocIp(), Parametro.getDocIp(), Parametro.getDocDb(), Parametro.getDocUsuario(), Parametro.getDocClave(), response, Parametro.getDir(), Parametro.getUrlAnexos() );
        //                    if (pk_instalacion[0].trim().compareTo("") != 0 && pk_instalacion[0].trim().compareTo("-1") != 0) {
        //                        id_instalacion = pk_instalacion[0];
        //                        tipo_trabajo = "3";
        //                    }
        //                    r = "msg»" + pk_instalacion[1];
        //                } else {
        //                    if (!objOrdenTrabajo.solucionar(id_sucursal, id_orden_trabajo, Parametro.getUsuario(), Fecha.getFecha("ISO"), Fecha.getHora(), solucionado,
        //                            conformidad, atencion, recomendacion, materiales, cantidades, set_deviceclave)) {
        //                        r = "msg»" + objOrdenTrabajo.getError();
        //                    } else {
        //                        objInstalacion.ejecutar("update tbl_preinstalacion set estado='n' where id_preinstalacion='" + id_preinstalacion + "';");
        //                        r = "msg»ORDEN DE TRABAJO REGISTRADA SATISFACTORIAMENTE. ";
        //                    }
        //                }
        //
        //            }
        
                    r = "NO SE HA PODIDO CARGAR INFORMACION DE LA INSTALACION";
                    if (id_instalacion.trim().compareTo("") != 0 && id_instalacion.trim().compareTo("-1") != 0) {
                        
                        ////////obtener bodega de la instalacion al cliente
                        r = "No se pudo encontrar una bodega del cliente, contacte con el Jefe de sucursal";
                        String bodega_cliente[] = objBodega.getBodegaResponsableCliente(id_cliente, id_instalacion, "" + id_sucursal, num_instalacion, razon_social, direccion);
                        if (bodega_cliente[0].trim().compareTo("") != 0) {
                            boolean ok = false;
                            boolean guardado = true;
                            String msg = "";
        //                    int topem = Integer.parseInt(request.getParameter("topem"));
                            String precioactual = "";
                            String adicionales = "";
                            String periodo = objPrefactura.getUltimoPeriodo();
                            String listaidnuevos = "";
                            String listacostonuevos = "";
                            String listadescripcionnuevos = "";
                            String listatiposrubros = "";
                            String listacantidadesrubros = "";

                            if( ordenTrabajo.getOrdenTrabajoMaterial() != null ) {

                                Iterator it = ordenTrabajo.getOrdenTrabajoMaterial().iterator();
                                while( it.hasNext() ) {
                                    MaterialesModel materialesModel = (MaterialesModel)it.next();
                                    if ( Double.parseDouble( materialesModel.getCantidad() ) > 0 ) {
                                        idproductos += materialesModel.getIdProducto() + ",";
                                        materiales += materialesModel.getIdMaterial() + ",";
                                        cantidades += materialesModel.getCantidad() + ",";
                                        precioactual += materialesModel.getPrecioActual() + ",";
                                        adicionales += materialesModel.getGastoadicional() + ",";
                                        if ( Double.parseDouble( materialesModel.getGastoadicional() ) > 0) {
                                            listaidnuevos += materialesModel.getIdProducto() + ",";
                                            listadescripcionnuevos += materialesModel.getDetalle();
                                            listacostonuevos += ((Double.parseDouble(materialesModel.getGastoadicional())) * Double.parseDouble(materialesModel.getPrecioActual())) + ",";
                                            listatiposrubros += "p" + ",";
                                            listacantidadesrubros += materialesModel.getGastoadicional() + ",";
                                        }
                                    }
                                }
                            }

                            if (materiales.compareTo("") != 0) {
                                idproductos = idproductos.substring(0, idproductos.length() - 1);
                                materiales = materiales.substring(0, materiales.length() - 1);
                                cantidades = cantidades.substring(0, cantidades.length() - 1);
                                precioactual = precioactual.substring(0, precioactual.length() - 1);
                            }

                            //String macs_nuevas = mac_nuevo.compareTo("")!=0 ? mac_nuevo+"," : "";
                            String idsActivos = mac_nuevo.compareTo("") != 0 ? objActivo.getIdActivo(mac_nuevo) + "," : "";
                            //String macs_retiradas = mac_ant.compareTo("")!=0 ? mac_ant+"," : "";
                            String idsActivosRet = mac_ant.compareTo("") != 0 ? objActivo.getIdActivo(mac_ant) + "," : "";

                            String axEquiposRetirados[] = equiposRetirados.split(",");
                            for(int x=0; x<axEquiposRetirados.length; x++){
                                idsActivosRet += objActivo.getIdActivo( axEquiposRetirados[x] ) + ",";
                            }

                            String axEquiposUtilizados[] = equiposUtilizados.split(",");
                            for(int x=0; x<axEquiposUtilizados.length; x++){
                                idsActivos += objActivo.getIdActivo( axEquiposUtilizados[x] ) + ",";
                            }

        //                    String codigo_activo = "";

                            ////variables para reposiciones
        //                    String idsActivos1 = "";
        //                    String idsActivosRet1 = "";
        //                    String idsMacs = "";

                            if( ordenTrabajo.getReposicionEquipos() != null ) {

                                Iterator it = ordenTrabajo.getReposicionEquipos().iterator();
                                while( it.hasNext() ) {
                                    ReposicionEquiposModel reposicionEquipos = (ReposicionEquiposModel)it.next();
                                    if (reposicionEquipos.getIdActivoNuevo()!= null) {
                                        idsActivos += reposicionEquipos.getIdActivoNuevo() + ",";
                                    }

                                    if (reposicionEquipos.getIdActivoActual() != null) {
                                        idsActivosRet += reposicionEquipos.getIdActivoActual() + ",";
                                    }

                                    /// reposicion de equipos por daño o perdida
                                    if (reposicionEquipos.getTipoReposicion() != null) {
                                        String estadorepo = reposicionEquipos.getTipoReposicion();
                                        ////reposicion por remplazo
                                        if (estadorepo.trim().compareTo("0") == 0) {
                                            idsActivos += reposicionEquipos.getIdActivoNuevo() + ",";
                                            idsActivosRet += reposicionEquipos.getIdActivoActual() + ",";

                                        } ////reposicion de perdida de equipos y cobro nuevo
                                        else {
        //                                    codigo_activo = request.getParameter("macacf" + i);
        //                                    String codigo_activo_nuevo = request.getParameter("macnuf" + i);
        //                                    idsActivos1 += reposicionEquipos.getIdActivoNuevo() + ",";
                                            try {
                                                ///bajas de activos por perdidad de equipos 
                                                ResultSet rs = objActivo.getActivo( reposicionEquipos.getIdActivoActual() );
                                                if (rs.next()) {
                                                    String motivo = "PERDIDA DE EQUIPO EN ORDEN DE TRABAJO DE CLIENTE";
                                                    String idactivo = (rs.getString("id_activo") != null ? rs.getString("id_activo") : "");
                                                    String descripcion = (rs.getString("descripcion") != null ? rs.getString("descripcion") : "");
                                                    String compra = (rs.getString("valor_compra") != null ? rs.getString("valor_compra") : "");
                                                    String depreciacion = (rs.getString("valor_depreciado") != null ? rs.getString("valor_depreciado") : "");
                                                    Double perdida = Double.parseDouble(compra) - Double.parseDouble(depreciacion);
                                                    if (objActivo.darVaja(id_sucursal, idactivo, ordenTrabajo.getUsuarioSolucion(), String.valueOf(perdida), motivo)) {
                                                        auditoria.setRegistro( ordenTrabajo.getUsuarioSolucion(), ordenTrabajo.getRemoteAddr(), "BAJA DEL ACTIVO: " + descripcion + " " + motivo);
                                                    }
                                                }
                                                rs.close();

                                                ///rubos de activos obtener informacion
                                                ResultSet rsn = objActivo.getActivo( reposicionEquipos.getIdActivoNuevo() );
                                                if (rsn.next()) {
                                                    listaidnuevos += (rsn.getString("id_activo") != null ? rsn.getString("id_activo") : "") + ",";
                                                    listadescripcionnuevos += (rsn.getString("descripcion") != null ? rsn.getString("descripcion") : "") + ",";
                                                    listacostonuevos += (rsn.getString("valor_compra") != null ? rsn.getString("valor_compra") : "") + ",";
                                                    listatiposrubros += "1" + ",";
                                                    listacantidadesrubros += "1" + ",";
                                                }

                                                rsn.close();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                }

                            }   

                            String id_personalizacion[] = null;
                            if (guardado) {
                                PdfDocumental ObjPdfDocumental = new PdfDocumental();
                                ObjPdfDocumental.setConexion( Parametro.getIp(), Parametro.getPuerto(), Parametro.getDb(), Parametro.getUsuario(), Parametro.getClave(), 
                                                    Parametro.getDocIp(), Parametro.getDocPpuerto(), Parametro.getDocDb(), Parametro.getDocUsuario(), Parametro.getDocClave()
                                                );
                                boolean oka = false;
                                if (firmar_documentos.trim().compareTo("si") == 0 && obligado_firmar && firma_ordenes.compareTo("true") == 0) {
                                    String oki = ObjPdfDocumental.ValidarKeys( Parametro.getDir(), id_empleado, usuario_key);
                                    if (oki.compareTo("") == 0) {
                                        oka = true;
                                    } else {
                                        r = oki;
                                    }
                                } else if ((firmar_documentos.trim().compareTo("no") == 0 || !obligado_firmar || firma_ordenes.compareTo("false") == 0)) {
                                    oka = true;
                                }
                                if (oka) {
                                    if (idsActivos.compareTo("") != 0) {
                                        idsActivos = idsActivos.substring(0, idsActivos.length() - 1);
                                        idsActivos = Matriz.QuitarRepetidos(idsActivos, ",");
                                    }
                                    if (idsActivosRet.compareTo("") != 0) {
                                        idsActivosRet = idsActivosRet.substring(0, idsActivosRet.length() - 1);
                                        idsActivosRet = Matriz.QuitarRepetidos(idsActivosRet, ",");
                                        idsActivosRet = idsActivosRet != null ? idsActivosRet : "";
                                    }
                                    if (tipo_trabajo.compareTo("3") == 0) {     //  instalaciones
                                        String resultado = objActivo.validar_personalizacion(idsActivos, idsActivosRet, tipo_trabajo, bodega_cliente[0], id_instalacion, "" + id_sucursal, id_bodega_empleado, ordenTrabajo.getRemoteAddr(), observacion, id_orden_trabajo);
                                        id_personalizacion = resultado.split(";");
                                        msg = id_personalizacion[1];
                                        if (id_personalizacion[2].compareTo("-1") != 0) {
                                            auditoria.setRegistro( ordenTrabajo.getUsuarioSolucion(), ordenTrabajo.getRemoteAddr(), "REGISTRO DE DOCUMENTO DE PERSONALIZACION DE ACTIVOS ID: " + id_personalizacion[2]);
                                            String fecha_instalacion = ordenTrabajo.getInstalacion().getFechaInstalacion();
                                            String estado_instalacion = ordenTrabajo.getInstalacion().getEstadoInstalacion();
        //                                    if (id_preinstalacion.trim().compareTo("-1") != 0 && id_preinstalacion.trim().compareTo("") != 0) {
        //                                        fecha_instalacion = Fecha.getAnio() + "-" + Fecha.getMes() + "-" + Fecha.getDia();
        //                                        estado_instalacion = "i";
        //                                    }
                                            if (!objInstalacion.insertarPostInstalacion(id_instalacion, id_sucursal, fecha_instalacion, receptor_nuevo, mac_nuevo, idsActivos,
                                                    id_personalizacion[2], porcentaje_senal, antena_acoplada, tipo_instalacion, id_plan_actual, "true", conformidad, atencion,
                                                    estado_instalacion, latitud, longitud, altura, altura_antena, latitud_gps, longitud_gps)) {
                                                objActivo.aceptar_anular_personalizacion("" + id_sucursal, "error de orden", ordenTrabajo.getRemoteAddr(), observacion, "anular", id_personalizacion[2]);
                                            } else {
                                                ok = true;
                                            }

                                        }

                                    } else if (tipo_trabajo.compareTo("4") == 0) {      //desinstalacion
                                        FacturaVenta objFactura = new FacturaVenta(Parametro.getIp(), Parametro.getPuerto(), Parametro.getDb(), Parametro.getUsuario(), Parametro.getClave());
                                        boolean sin_deuda = objFactura.instalacionSinDeuda(id_instalacion);
                                        String estado_servicio = "t";
                                        if (idsActivosRet.compareTo("") != 0) {
                                            estado_servicio = "e";
                                            if (sin_deuda) {
                                                estado_servicio = "t";
                                            }
                                        } else {
                                            if (sin_deuda) {
                                                estado_servicio = "d";
                                            } else {
                                                estado_servicio = "r";
                                            }
                                        }
                                        String resultado = objActivo.validar_personalizacion(idsActivos, idsActivosRet, tipo_trabajo, bodega_cliente[0], id_instalacion, "" + id_sucursal, id_bodega_empleado, ordenTrabajo.getRemoteAddr(), observacion, id_orden_trabajo);
                                        id_personalizacion = resultado.split(";");
                                        msg = id_personalizacion[1];
                                        if (id_personalizacion[2].compareTo("-1") != 0) {
                                            objInstalacion.desInstalar(id_instalacion, id_cliente, recomendacion, estado_servicio, idsActivosRet);
                                            ok = true;
                                            auditoria.setRegistro( ordenTrabajo.getUsuarioSolucion(), ordenTrabajo.getRemoteAddr(), "REGISTRO DE DOCUMENTO DE PERSONALIZACION DE ACTIVOS Nro.: " + num_documento);
                                            num_documento++;
                                        }

                                    } else {    //    revision general y demas

                                        ok = false;
                                        msg = "Error: empleado no dispone de una bodega para personalizaciones (utilizada para ordenes de trabajo), contáctese con el jefe de sucursal, para que le asigne una bodega.";
                                        if (id_bodega_empleado.compareTo("") != 0) {

                                            String resultado = objActivo.validar_personalizacion(idsActivos, idsActivosRet, tipo_trabajo, bodega_cliente[0], id_instalacion, "" + id_sucursal, id_bodega_empleado, ordenTrabajo.getRemoteAddr(), observacion, id_orden_trabajo);
                                            id_personalizacion = resultado.split(";");
                                            msg = id_personalizacion[1];
                                            if (id_personalizacion[3].compareTo("-1") != 0 && id_personalizacion[2].compareTo("-1") != 0) {
                                                ok = true;
                                                if (objInstalacion.setactualizar(id_instalacion, id_orden_trabajo, id_sector, direccion, ip, mac_ant, idsActivosRet, receptor_ant, mac_nuevo,
                                                        idsActivos, receptor_nuevo, porcentaje_senal, antena_acoplada, latitud, longitud, altura, altura_antena, latitud_gps, longitud_gps)) {
                                                    ok = true;
                                                    r = "Actualización de datos del registro de instalación y personalización del equipo al cliente realizada con éxito.";
                                                } else {
                                                    ok = false;
                                                    msg = "Error en actualizacion de datos de instalacion " + objInstalacion.getError();
                                                    if (id_personalizacion[2].compareTo("-1") != 0) {
                                                        objActivo.aceptar_anular_personalizacion("" + id_sucursal, "error de orden", ordenTrabajo.getRemoteAddr(), observacion, "anular", id_personalizacion[2]);
                                                    }
                                                    if (id_personalizacion[3].compareTo("-1") != 0) {
                                                        objActivo.aceptar_anular_personalizacion("" + id_sucursal, "error de orden", ordenTrabajo.getRemoteAddr(), observacion, "anular", id_personalizacion[3]);
                                                    }
                                                }
                                            }

                                        }

                                    }
                                    if (ok) {
                                        /* if (!objOrdenTrabajo.solucionar(id_sucursal, id_orden_trabajo, Parametro._usuario, Fecha.getFecha("ISO"), Fecha.getHora(), solucionado,
                                            conformidad, atencion, recomendacion, materiales, cantidades, set_deviceclave)) {
                                        r = "msg»" + objOrdenTrabajo.getError();
                                    } else {*/
                                        List sql = new ArrayList();
                                        /*actualizar informacion en la orden de la capacidad efectiva  velocidades de carga , ordenes no procedente*/
                                        sql.add("UPDATE tbl_orden_trabajo SET capacidad_efectiva='" + capacidad_efectiva + "',velocidad_carga='" + vel_carga + "',velocidad_descarga='" + vel_descarga + "',ot_procedente='" + procedente + "' "
                                                + " WHERE id_orden_trabajo='" + id_orden_trabajo + "';");
                                        sql.add("update tbl_soporte set procedente='" + procedente + "' where id_soporte=(select tmp.id_soporte from tbl_orden_trabajo as tmp where tmp.id_orden_trabajo='" + id_orden_trabajo + "');");
                                        /* fin actualizar informacion en la orden de la capacidad efectiva  velocidades de carga , ordenes no procedente*/
         /*actualizar informacion sisq proviene desde un certificado*/
                                        String id_instalacion_certificado = objOrdenTrabajo.getInstalacionCertificado(id_orden_trabajo);
                                        int croquis = archivo.ExisteArchivo("tbl_instalacion_croquis", id_instalacion_certificado, "imgcroquisnuevo");
                                        if (croquis > 0) {
                                            archivo.ejecutar("update tbl_documentos  "
                                                    + " set documento=(select t1.documento from tbl_documentos as t1 where t1.tabla='tbl_instalacion_croquis' and t1.campo_tabla='imgcroquisnuevo' and t1.id_tabla='" + id_instalacion_certificado + "'), "
                                                    + " nombre_documento=(select t1.nombre_documento from tbl_documentos as t1 where t1.tabla='tbl_instalacion_croquis' and t1.campo_tabla='imgcroquisnuevo' and t1.id_tabla='" + id_instalacion_certificado + "') "
                                                    + " where tabla='tbl_instalacion' and campo_tabla='imgcroquis' and id_tabla='" + id_instalacion + "'");
                                        }
                                        sql.add("UPDATE tbl_instalacion  as i "
                                                + " SET id_provincia=c.id_provincia,id_ciudad=c.id_canton,id_parroquia=c.id_parroquia,id_sector=c.id_sector, "
                                                + " id_plan_actual=c.id_plan,direccion_instalacion=substring(c.direccion,0,199),convenio_pago=c.modalidad_pago,tipo_instalacion=c.tipo_instalacion "
                                                + " FROM tbl_instalacion_certificado as c "
                                                + " WHERE i.id_instalacion = c.id_instalacion and c.id_instalacion_certificado='" + id_instalacion_certificado + "' and c.fecha_creada>='2019-09-24';");
                                        /*fin actualizar informacion sisq proviene desde un certificado*/
         /*inserttar rubros adicionales de cambios de domicio orden no procedente o migraciones*/
                                        String[] anticipos = objOrdenTrabajo.getAnticipoOrdenTrabajo(id_orden_trabajo);

                                        //  cambios de domicilio
                                        if ((tipo_trabajo.trim().compareTo("2") == 0 || tipo_trabajo.trim().compareTo("14") == 0) && procedente.trim().compareTo("true") == 0) {
        //                                    if ( objOrdenTrabajo.getCambiosDomicilio(id_instalacion, "'2','14'", id_orden_trabajo) ) {
//                                            if ( objPromocion.cobrarCambioDomicilio(id_instalacion) ) {
                                                if ( objInstalacion.numCertificadosCambioDomicilioEnAnio(id_instalacion, "2,3") > 1 ) {
                                                    int indice = (tipo_trabajo.trim().compareTo("2") == 0 ? 0 : 1);
                                                    if (this.SetFactura(id_instalacion, costos_sector, anticipos, indice)[0].compareTo("-1") == 0) {
                                                        sql.add("INSERT INTO tbl_prefactura_rubro( id_sucursal, id_rubro,id_instalacion, rubro,periodo, monto,tiporubro)VALUES ('" + id_sucursal + "','" + costos_sector[indice][0] + "' ,'" + id_instalacion + "', '" + costos_sector[indice][1] + "','" + periodo + "'::date + '1 month'::interval, '" + costos_sector[indice][2] + "','p');");
                                                    }
                                                }
//                                            }
                                        }

                                        //  migraciones desde 2024 todas son gratis
                                        /*if ((tipo_trabajo.trim().compareTo("13") == 0 || tipo_trabajo.trim().compareTo("22") == 0) && procedente.trim().compareTo("true") == 0) {
//                                            if ( !objOrdenTrabajo.getMgracionGratuita(id_orden_trabajo) && objInstalacion.numCertificadosEnAnio(id_instalacion, "4,5") > 1 ) {
                                            if ( objInstalacion.numCertificadosEnAnio(id_instalacion, "4,5,6") > 1 ) {
//                                                if ( objPromocion.cobrarMigracion(id_instalacion) ) {
                                                    int indice = (tipo_trabajo.trim().compareTo("13") == 0 ? 3 : 2);
                                                    if (this.SetFactura(id_instalacion, costos_sector, anticipos, indice)[0].compareTo("-1") == 0) {
                                                        sql.add("INSERT INTO tbl_prefactura_rubro( id_sucursal, id_rubro,id_instalacion, rubro,periodo, monto,tiporubro)VALUES ('" + id_sucursal + "','" + costos_sector[indice][0] + "' ,'" + id_instalacion + "', '" + costos_sector[indice][1] + "','" + periodo + "'::date + '1 month'::interval, '" + costos_sector[indice][2] + "','p');");
                                                    }
//                                                }
                                            }
                                        }*/

                                        if (procedente.trim().compareTo("false") == 0 && tipo_trabajo.trim().compareTo("3") != 0 && tipo_trabajo.trim().compareTo("4") != 0) {
                                            int indice = 4;
                                            if (this.SetFactura(id_instalacion, costos_sector, anticipos, indice)[0].compareTo("-1") == 0) {
                                                sql.add("INSERT INTO tbl_prefactura_rubro( id_sucursal, id_rubro,id_instalacion, rubro,periodo, monto,tiporubro)VALUES ('" + id_sucursal + "','" + costos_sector[indice][0] + "' ,'" + id_instalacion + "', '" + costos_sector[indice][1] + "','" + periodo + "'::date + '1 month'::interval, '" + costos_sector[indice][2] + "','p');");
                                            }
                                        }
                                        /*inserttar rubros adicionales de cambios de domicio orden no procedente o migraciones*/

                                        ///encrustado
                                        ////kardex y asiento contable
                                        if (!materiales.equals("")) {
                                            sql = objOrdenTrabajo.setactualizarconsumible(id_orden_trabajo, materiales, adicionales, sql);
                                            String detalle = "CONSUMO DE MATERIAL INSTALADO EN ORDEN DE TRABAJO CLIENTE DOCUMENTO N°. ";
                                            sql = objOrdenTrabajo.kardexdeconsumoproducto(idproductos, cantidades, precioactual, id_sucursal, detalle, id_orden_trabajo, id_tecnico_resp, id_cliente, "Ordenes de trabajo cliente", sql);
                                        }
                                        ////agregar los rubos nuevos
                                        if (listaidnuevos.compareTo("") != 0) {
                                            listaidnuevos = listaidnuevos.substring(0, listaidnuevos.length() - 1);
                                            listacostonuevos = listacostonuevos.substring(0, listacostonuevos.length() - 1);
                                            listadescripcionnuevos = listadescripcionnuevos.substring(0, listadescripcionnuevos.length() - 1);
                                            listatiposrubros = listatiposrubros.substring(0, listatiposrubros.length() - 1);
                                            listacantidadesrubros = listacantidadesrubros.substring(0, listacantidadesrubros.length() - 1);
                                            sql = objActivo.agregarRubroprefactura(id_sucursal, id_instalacion, listaidnuevos, listacostonuevos, listadescripcionnuevos, periodo, listatiposrubros, listacantidadesrubros, sql);
                                        }
                                        if (!objOrdenTrabajo.solucionar(id_sucursal, id_orden_trabajo, ordenTrabajo.getUsuarioSolucion(), Fecha.getFecha("ISO"), Fecha.getHora(), solucionado,
                                                conformidad, atencion, recomendacion, materiales, cantidades, set_deviceclave, sql)) {
                                            if (id_personalizacion[2].compareTo("-1") != 0) {
                                                objActivo.aceptar_anular_personalizacion("" + id_sucursal, "error de orden", ordenTrabajo.getRemoteAddr(), observacion, "anular", id_personalizacion[2]);
                                            }
                                            if (id_personalizacion[3].compareTo("-1") != 0) {
                                                objActivo.aceptar_anular_personalizacion("" + id_sucursal, "error de orden", ordenTrabajo.getRemoteAddr(), observacion, "anular", id_personalizacion[3]);
                                            }
                                            r = objOrdenTrabajo.getError();

                                        } else {



        //  el asiento de salida de suministros se lo hace al entregar al tecnico y enla venta se realiza el mismo asiento. Se reasliza solo el asiento de la venta por eso no se debe realizar ninguna reversion                                    

        //                                    //  reversar suministro adicional, ya que se contabiliza nuevamente en la emision de la factura
        //                                    if(listacantidadesrubros.compareTo("")!=0) {
        //                                        
        //                                        ComprobanteDiario objComprobanteDiario = new ComprobanteDiario(Parametro.getIp(), Parametro.getPuerto(), Parametro.getDb(), Parametro.getUsuario(), Parametro.getClave());
        //                                        
        //                                        String detalle;
        //                                        String idProducto[] = listaidnuevos.split(",");
        //                                        String descripcionProducto[] = listadescripcionnuevos.split(",");
        //                                        String montos[] = listacostonuevos.split(",");
        //                                        for (int i = 0; i < montos.length; i++) {
        //                                            try{
        //                                                detalle = "REVERSION DE TRASPASO DE CONSUMO DE SUMINISTROS " + descripcionProducto[i] + " POR EXCESO EN ORDEN DE TRABAJO " + numero_orden;
        //                                                String param = "['"+objProducto.getIdPCcompras( idProducto[i] )+"','"+montos[i]+"','0'],";
        //                                                param += "['"+idPCcostoVentas+"','0','"+montos[i]+"']";
        //                                                ResultSet res = objComprobanteDiario.consulta("select proc_comprobanteDiario("+id_sucursal+", "+objComprobanteDiario.getNumComprobante()+", now()::date, '"+detalle+"', "+montos[i]+", array["+param+"]);");
        ////                                                if(res.next()){
        ////                                                    num = (res.getString(1)!=null) ? res.getInt(1) : -1;
        ////                                                    res.close();
        ////                                                }
        //                                            }catch(Exception e){
        //                                                e.printStackTrace();
        //                                            }
        //                                        }
        //                                        objComprobanteDiario.cerrar();
        //                                    }

                                            File archivo_entrada = new File(Parametro.getDir(), "eordentrabajo" + Fecha.getFechaHora() + ".pdf");
                                            String oki = ObjPdfDocumental.PdfOrdenTrabajoCliente(id_orden_trabajo, archivo_entrada);
                                            if (oki != null && firmar_documentos.trim().compareTo("si") == 0 && obligado_firmar && firma_ordenes.compareTo("true") == 0) {
                                                try{
                                                    List Keys = ObjPdfDocumental.getKeys();
                                                    Properties parametros = ObjPdfDocumental.getPropiedadesPdf(id_sucursal);
                                                    CordenadasXY cordenadasXY = new CordenadasXY();
                                                    List cordenadas = cordenadasXY.CordenadasXY(archivo_entrada, "_firma_responsable_");
                                                    File archivo_salida = new File(Parametro.getDir(), "sordentrabajo" + Fecha.getFechaHora() + ".pdf");
                                                    File archivo_firma = Firmas.ValidarFirma(archivo_entrada.getAbsolutePath(), archivo_salida.getAbsolutePath(), true, cordenadas, Keys, parametros);
                                                    if (archivo_firma != null) {
                                                        ok = archivo.setArchivoDocumental("tbl_orden_trabajo", id_orden_trabajo, "documento_digital", archivo_firma.getName(), archivo_firma.getAbsolutePath(), "public", "db_isp");
                                                    }
                                                } catch(Exception e){
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                File archivo_firma = new File(Parametro.getDir(), oki);
                                                ok = archivo.setArchivoDocumental("tbl_orden_trabajo", id_orden_trabajo, "documento_digital", archivo_firma.getName(), archivo_firma.getAbsolutePath(), "public", "db_isp");
                                            }

                                            if( modoSincronizacionMikrotiks.compareTo("apis") == 0 ) {
                                                Mikrotik objMikrotik = new Mikrotik(Parametro.getIp(), Parametro.getPuerto(), Parametro.getDb(), Parametro.getUsuario(), Parametro.getClave());
                                                ResultSet rsInfoIncta = objMikrotik.getInfoInstalacion(id_instalacion);
                                                objMikrotik.conectar( this.getIdSucursal(rsInfoIncta), ip );
                                                objMikrotik.actualizarInstalacionEnServidor(id_instalacion);
                                                objMikrotik.MikrotikCerrar();
                                            }

                                            r = "ok";
                                        }
                                    } else {
                                        r = objActivo.getError() + " - " + objInstalacion.getError() + " " + msg;
                                    }
                                    objActivo.ejecutar("update tbl_instalacion set puesta_tierra=" + puesta_tierra + " where id_instalacion=" + id_instalacion + " ");
                                } else {
        //                            r += "^fun»_('btnRegSol').disabled=false";
                                }
                            } else {
                                r = "LA BODEGA DEL CLIENTE TIENE UN LIMITE EN SU BODEGA.";
                            }
                        }
                    }


                } finally {
                    conf.cerrar();
                    auditoria.cerrar();
                    objInstalacion.cerrar();
                    objOrdenTrabajo.cerrar();
                    objActivo.cerrar();
                    objBodega.cerrar();
                    objProducto.cerrar();
        //            objAts.cerrar();
        //            objPreinstalacion.cerrar();
                    archivo.cerrar();
                    objPrefactura.cerrar();
                }
                
            }
        
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    
        return r;
    }
    
    
    private String getIdSucursal(ResultSet rs) 
    {
        String id = "-1";
        try {
            if(rs.next()){
                id = rs.getString("id_sucursal")!=null ? rs.getString("id_sucursal") : "-1";
                rs.close();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    public String[] getEmpleado(String id_orden_trabajo) 
    {
        String res[] = new String[]{"", "", "", ""};
        DataBase objDataBase = new DataBase( Parametro.getIp(), Parametro.getPuerto(), Parametro.getDb(), Parametro.getUsuario(), Parametro.getClave() );
        try {
            ResultSet rs = objDataBase.consulta("select e.id_empleado,dni, nombre || ' ' || apellido as empleado, I.direccion_instalacion "
                    + "from (tbl_empleado as E inner join tbl_orden_trabajo as OT on E.id_empleado=OT.id_empleado) "
                    + "inner join tbl_instalacion as I on I.id_instalacion=OT.id_instalacion "
                    + "where OT.id_orden_trabajo=" + id_orden_trabajo);
            if (rs.next()) {
                res[0] = rs.getString("dni") != null ? rs.getString("dni") : "";
                res[1] = rs.getString("empleado") != null ? rs.getString("empleado") : "";
                res[2] = rs.getString("direccion_instalacion") != null ? rs.getString("direccion_instalacion") : "";
                res[3] = rs.getString("id_empleado") != null ? rs.getString("id_empleado") : "";
                res.clone();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            objDataBase.cerrar();
        }
        return res;
    }

    public String[] SetFactura(String id_instalacion, String rubros[][], String[] anticipo, int indice) 
    {
        String respuesta[] = {"-1", ""};
        String msg = "";
        if (anticipo[0].trim().compareTo("") != 0 && indice >= 0) {
            FacturaVenta ObjFacturaVenta = new FacturaVenta( Parametro.getIp(), Parametro.getPuerto(), Parametro.getDb(), Parametro.getUsuario(), Parametro.getClave() );
            try {
                String tipo_documento_cliente = "";
                String direccion = "";
                String email = "";
                String telefono = "";
                String id_sucursal = "";
                String razon_social = "";
                String ruc = "";
                String id_cliente = "";
                String id_factura_venta = "-1";
                String ip = "";
                /*datos de la instalacion */
                Instalacion ObjInstalacion = new Instalacion( Parametro.getIp(), Parametro.getPuerto(), Parametro.getDb(), Parametro.getUsuario(), Parametro.getClave() );
                try {
                    ResultSet rsInstalacion = ObjInstalacion.getInstalacion(id_instalacion);
                    if (rsInstalacion.next()) {
                        tipo_documento_cliente = rsInstalacion.getString("tipo_documento") != null ? rsInstalacion.getString("tipo_documento") : "05";
                        direccion = rsInstalacion.getString("direccion") != null ? rsInstalacion.getString("direccion") : "";
                        email = rsInstalacion.getString("email") != null ? rsInstalacion.getString("email") : "";
                        telefono = rsInstalacion.getString("telefono") != null ? rsInstalacion.getString("telefono") : "";
                        id_sucursal = rsInstalacion.getString("id_sucursal") != null ? rsInstalacion.getString("id_sucursal") : "";
                        razon_social = rsInstalacion.getString("razon_social") != null ? rsInstalacion.getString("razon_social") : "";
                        ruc = rsInstalacion.getString("ruc") != null ? rsInstalacion.getString("ruc") : "";
                        id_cliente = rsInstalacion.getString("id_cliente") != null ? rsInstalacion.getString("id_cliente") : "";
                        ip = rsInstalacion.getString("ip") != null ? rsInstalacion.getString("ip") : "";
                        rsInstalacion.close();
                    }
                } catch (Exception e) {
                    System.out.println("Error portal obtner datos del cliente :" + e.getMessage());
                } finally {
                    ObjInstalacion.cerrar();
                }
                ////datos de la cuenta de anticio del cliente
                String id_plan_cuenta_anticipo = "232";
                Cliente ObjCliente = new Cliente( Parametro.getIp(), Parametro.getPuerto(), Parametro.getDb(), Parametro.getUsuario(), Parametro.getClave() );
                try {
                    ResultSet rsCliente = ObjCliente.getCliente(id_cliente);
                    if (rsCliente.next()) {
                        tipo_documento_cliente = rsCliente.getString("tipo_documento") != null ? rsCliente.getString("tipo_documento") : "06";
                        id_plan_cuenta_anticipo = rsCliente.getString("id_plan_cuenta_anticipo") != null ? rsCliente.getString("id_plan_cuenta_anticipo") : "232";
                        rsCliente.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    ObjCliente.cerrar();
                }
                /**/
                msg = "no se encontro un ip validad para escribir";
                if (ip.compareTo("") != 0) {
                    /* fin datos de la instalacion  */
                    String matPuntosVirtuales[][] = ObjFacturaVenta.getPuntosEmisionVirtuales();
                    msg = "No se ha encontrado un punto de emisiona factura";
                    int p = Matriz.enMatriz(matPuntosVirtuales, id_sucursal, 0);
                    if (p != -1) {
                        /*obtenemos todos los productos */
                        String productos[][] = null;
                        try {
                            ResultSet rsDetalleFactura = ObjFacturaVenta.consulta("select SP.id_sucursal, P.id_producto, P.codigo, P.descripcion, SP.stock_sucursal, P.precio_costo, I.porcentaje, "
                                    + "max(case when tipo='s' then P.precio_venta_servicio else round((P.precio_costo + (P.precio_costo * PP.utilidad / 100)), 4) end) as precio_venta,"
                                    + "SP.descuento, tipo, round((P.precio_costo + (P.precio_costo * P.utilidad_min / 100)), 4), I.codigo as codigo_iva, P.id_plan_cuenta_venta, P.id_iva, id_plan_cuenta_venta_servicio, id_plan_cuenta_venta_bien  "
                                    + "FROM ((vta_producto as P inner join tbl_sucursal_producto as SP on P.id_producto=SP.id_producto) "
                                    + "inner join tbl_iva as I on I.id_iva=SP.id_iva) "
                                    + "inner join tbl_producto_precio as PP on P.id_producto=PP.id_producto "
                                    + "group by SP.id_sucursal, P.id_producto, P.codigo, P.descripcion, SP.stock_sucursal, P.precio_costo, I.porcentaje, case when tiene_iva then '~' else '' end, "
                                    + "SP.descuento, tipo, round((P.precio_costo + (P.precio_costo * P.utilidad_min / 100)), 4), I.codigo, P.id_plan_cuenta_venta, P.id_iva, id_plan_cuenta_venta_servicio, id_plan_cuenta_venta_bien order by id_sucursal, id_producto");
                            productos = Matriz.ResultSetAMatriz(rsDetalleFactura);
                        } catch (Exception e) {
                            System.out.println("Error portal obtener productos de sucursal :" + e.getMessage());
                        }
                        /*validamos si existe costos y productos */
                        msg = "No se ha encontrado datos de costos y productos";
                        if (rubros != null && productos != null) {
                            /*Datos de facturar*/
                            String idPuntoEmision = matPuntosVirtuales[p][1];
                            String usuario = matPuntosVirtuales[p][2];
                            String serie_factura = matPuntosVirtuales[p][3];
                            String num_factura = matPuntosVirtuales[p][4];
                            String direccion_sucursal = matPuntosVirtuales[p][5];
                            String autorizacion = "1119999999";
                            String desc_venta = "121";
                            String ambiente = "1";      // 1=pruebas    2=produccion
                            String tipoEmision = "1"; // 1=normal    2=Indisponibilidad del sistema
                            String clave_certificado = "";
                            String ruc_empresa = "1091728857001";
                            String razon_social_empresa = "SOLUCIONES AVANZADAS INFORMATICAS Y TELECOMUNICACIONES SAITEL";
                            String nombre_comercial = "SAITEL";
                            String email_info = "";
                            String numeros_soporte = "";
                            String pagina_web = "";
                            String num_resolucion = "";
                            String oblga_contabilidad = "SI";
                            String dir_matriz = "JOSE JOAQUIN DE OLMEDO 4-63 Y JUAN GRIJALVA";
                            String iva_vigente = "12";
                            String id_forma_pago = "98";
                            String cxc = "";
                            try {
                                ResultSet r = ObjFacturaVenta.consulta("SELECT * FROM tbl_configuracion order by parametro;");
                                while (r.next()) {
                                    String parametro = r.getString("parametro") != null ? r.getString("parametro") : "";
                                    if (parametro.compareTo("desc_venta") == 0) {
                                        desc_venta = r.getString("valor") != null ? r.getString("valor") : "121";
                                    }
                                    if (parametro.compareTo("ambiente") == 0) {
                                        ambiente = r.getString("valor") != null ? r.getString("valor") : "2";
                                    }
                                    if (parametro.compareTo("email_info") == 0) {
                                        email_info = r.getString("valor") != null ? r.getString("valor") : "1";
                                    }
                                    if (parametro.compareTo("numeros_soporte") == 0) {
                                        numeros_soporte = r.getString("valor") != null ? r.getString("valor") : "1";
                                    }
                                    if (parametro.compareTo("pagina_web") == 0) {
                                        pagina_web = r.getString("valor") != null ? r.getString("valor") : "1";
                                    }
                                    if (parametro.compareTo("clave_certificado") == 0) {
                                        clave_certificado = r.getString("valor") != null ? r.getString("valor") : "";
                                    }
                                    if (parametro.compareTo("ruc") == 0) {
                                        ruc_empresa = r.getString("valor") != null ? r.getString("valor") : "1091728857001";
                                    }
                                    if (parametro.compareTo("razon_social") == 0) {
                                        razon_social_empresa = r.getString("valor") != null ? r.getString("valor") : "SOLUCIONES AVANZADAS INFORMATICAS Y TELECOMUNICACIONES SAITEL";
                                    }
                                    if (parametro.compareTo("nombre_comercial") == 0) {
                                        nombre_comercial = r.getString("valor") != null ? r.getString("valor") : "SAITEL";
                                    }
                                    if (parametro.compareTo("num_resolucion") == 0) {
                                        num_resolucion = r.getString("valor") != null ? r.getString("valor") : "";
                                    }
                                    if (parametro.compareTo("oblga_contabilidad") == 0) {
                                        oblga_contabilidad = r.getString("valor") != null ? r.getString("valor") : "SI";
                                    }
                                    if (parametro.compareTo("p_iva1") == 0) {
                                        iva_vigente = r.getString("valor") != null ? r.getString("valor") : "12";
                                    }
                                    if (parametro.compareTo("cxc") == 0) {
                                        cxc = r.getString("valor") != null ? r.getString("valor") : "12";
                                    }
                                }
                                r.close();
                            } catch (Exception e) {
                                System.out.println("Error portal obtener datos de configuracion :" + e.getMessage());
                            }
                            
                            Sucursal objSucursal = new Sucursal( Parametro.getIp(), Parametro.getPuerto(), Parametro.getDb(), Parametro.getUsuario(), Parametro.getClave() );
                            String ubicacionNombreComercial[] = objSucursal.getDireccionDePuntoEmision(String.valueOf(idPuntoEmision));
                            nombre_comercial = ubicacionNombreComercial[1].compareTo("")!=0 ? ubicacionNombreComercial[1] : nombre_comercial;
                            email_info = ubicacionNombreComercial[2].compareTo("")!=0 ? ubicacionNombreComercial[2] : email_info;
                            numeros_soporte = ubicacionNombreComercial[3].compareTo("")!=0 ? ubicacionNombreComercial[3] : numeros_soporte;
                            pagina_web = ubicacionNombreComercial[4].compareTo("")!=0 ? ubicacionNombreComercial[4] : pagina_web;
                            objSucursal.cerrar();
                            
                            double total_anticipo = Double.parseDouble(anticipo[1]);
                            double total_rubro = Double.parseDouble(rubros[indice][2]);    
                            total_rubro = total_rubro + (total_rubro * (Double.parseDouble(iva_vigente) / 100));
                            msg = "El monto del anticipo no cubre el monto del rubro";
                            if (total_anticipo >= total_rubro) {
                                String idCliAnt = "";
                                String monto_vajar = "";
                                double abonos = 0;
                                double axUltimoAbono = 0;
                                String axIdCliAnt = "";
                                String axMontoVajar = "";
                                try {
                                    String anticipo_id[] = anticipo[0].split(",");
                                    String anticipo_valor[] = anticipo[2].split(";");
                                    for (int i = 0; i < anticipo_id.length; i++) {
                                        if (Double.parseDouble(anticipo_valor[i]) >= total_rubro) {    //  si el anticipo cubre el total de la factura 
                                            idCliAnt = anticipo_id[i];
                                            monto_vajar = "" + total_rubro;
                                            break;
                                        } else {        //  si el anticipo no cubre el monto
                                            abonos += Double.parseDouble(anticipo_valor[i]);
                                            axUltimoAbono = Double.parseDouble(anticipo_valor[i]);
                                            axIdCliAnt += anticipo_id[i] + ",";
                                            axMontoVajar += anticipo_valor[i] + ",";
                                            if (abonos >= total_rubro) {     //  si el o los abonos cubren el total se salta al pago
                                                break;
                                            }
                                        }
                                    }
                                    if (axIdCliAnt.compareTo("") != 0 && abonos > 0) {
                                        idCliAnt = axIdCliAnt.substring(0, axIdCliAnt.length() - 1);
                                        monto_vajar = axMontoVajar = axMontoVajar.substring(0, axMontoVajar.length() - 1);
                                        if (abonos == total_rubro) {       //  si los abonos cubre el total de la factura se vaja la factura completa
                                            monto_vajar = axMontoVajar;
                                        } else if (abonos > total_rubro) {     // se cubre mÃ¡s del total de la factura se vaja la factura completa
                                            monto_vajar = axMontoVajar.substring(0, axMontoVajar.lastIndexOf(",")) + "," + Addons.redondear(total_rubro - (abonos - axUltimoAbono)) + ","; //    el ultimo abono debe ser 
                                        }
                                    }
                                } catch (Exception e) {
                                    System.out.println("error en antificpos");
                                }
                                String matParamAsientoAx[][] = null;
                                String paramArtic = "";
                                String ids_productos = "";
                                String descripciones = "";
                                String cantidades = "";
                                String preciosUnitarios = "";
                                String descuentos = "";
                                String subtotales = "";
                                String ivas = "";
                                String pIvas = "";
                                String codigoIvas = "";
                                double iva_actual = Double.parseDouble(iva_vigente);
                                double total = 0;
                                double subtotal_final = 0;
                                double total_final = 0;
                                double descuento_final = 0;
                                double iva_final = 0;
                                /*calculo para la instalacion y costo de mes actual*/
                                int cantidad = 1;
                                double costo_instalacion = 0;
                                double descuento = 0;
                                double iva_calculado = 0;
                                double subtotal = 0;
                                int pos_rubro = Matriz.enMatrizBuscar(productos, new String[]{id_sucursal, rubros[indice][3]}, new int[]{0, 1});
                                msg = "no se encontro posicion del rubro con su respectivo producto";
                                if (pos_rubro != -1) {
                                    costo_instalacion = Double.parseDouble(rubros[indice][2]);
                                    descuento = 0;
                                    iva_calculado = (costo_instalacion - descuento) * iva_actual / 100;
                                    cantidad = 1;
                                    subtotal = costo_instalacion * cantidad;
                                    ids_productos += productos[pos_rubro][1] + ",";
                                    descripciones += productos[pos_rubro][3] + ",";
                                    cantidades += cantidad + ",";
                                    preciosUnitarios += Addons.redondearDecimales(costo_instalacion) + ",";
                                    descuentos += Addons.redondearDecimales(descuento) + ",";
                                    subtotales += Addons.redondearDecimales(subtotal) + ",";
                                    ivas += Addons.redondearDecimales(iva_calculado) + ",";
                                    pIvas += productos[pos_rubro][6] + ",";
                                    codigoIvas += productos[pos_rubro][11] + ",";
                                    total = (costo_instalacion - descuento + iva_calculado);
                                    subtotal_final += subtotal;
                                    descuento_final += descuento;
                                    iva_final += iva_calculado;
                                    total_final += total;
                                    matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{productos[pos_rubro][12], "0", Addons.redondearDecimales(costo_instalacion)});
                                    matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{productos[pos_rubro][14], "0", Addons.redondearDecimales(iva_calculado)});
                                    if (descuento > 0) {
                                        matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{desc_venta, Addons.redondearDecimales(descuento), "0"});
                                    }
                                    paramArtic += "['" + productos[pos_rubro][1] + "', '" + cantidad + "', '" + Addons.redondearDecimales(costo_instalacion) + "', '" + Addons.redondearDecimales(subtotal)
                                            + "', '" + Addons.redondearDecimales(descuento) + "', '" + Addons.redondearDecimales(iva_calculado) + "', '" + Addons.redondearDecimales(total) + "', '" + productos[pos_rubro][3]
                                            + "', '" + Addons.redondearDecimales(costo_instalacion) + "', '" + productos[pos_rubro][9] + "', '" + productos[pos_rubro][6] + "', '" + productos[pos_rubro][11] + "','p','-1'],";

                                    if (id_forma_pago.compareTo("99") == 0) {
                                        matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{cxc, Addons.redondearDecimales(total_final), "0"});
                                    } else if (id_forma_pago.compareTo("98") == 0) {
                                        matParamAsientoAx = Matriz.poner(matParamAsientoAx, new String[]{id_plan_cuenta_anticipo, Addons.redondearDecimales(total_final), "0"});
                                    }
                                    if (paramArtic.compareTo("") != 0) {
                                        paramArtic = paramArtic.substring(0, paramArtic.length() - 1);
                                        ids_productos = ids_productos.substring(0, ids_productos.length() - 1);
                                        descripciones = descripciones.substring(0, descripciones.length() - 1);
                                        cantidades = cantidades.substring(0, cantidades.length() - 1);
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
                                    /* crear el xml y otros datos  */
                                    String ret_num_serie = "";
                                    String ret_autorizacion = "";
                                    String ret_num_retencion = "0";
                                    String ret_fecha_emision = "";
                                    String ret_ejercicio_fiscal_mes = "";
                                    String ret_ejercicio_fiscal = "NULL";
                                    String ret_impuesto_retenido = "0";
                                    String paramRet = "";
                                    String codigoFormaPago = "";
                                    String forma_pago = "";
                                    String num_cheque = "";
                                    String banco = "";
                                    String num_comp_pago = "";
                                    String gastos_bancos = "0";
                                    String id_plan_cuenta_banco = "0";
                                    String son = "";
                                    String observacion = "Ingresos por Anticipo Factura Nro. " + serie_factura + " - " + num_factura;
                                    FormaPago objFormaPago = new FormaPago( Parametro.getIp(), Parametro.getPuerto(), Parametro.getDb(), Parametro.getUsuario(), Parametro.getClave() );
                                    try {
                                        codigoFormaPago = objFormaPago.getCodigoFormaPago(id_forma_pago);
                                        forma_pago = objFormaPago.getCodigoInternoFormaPago(id_forma_pago);
                                    } finally {
                                        objFormaPago.cerrar();
                                    }
                                    String xmlFirmado = "";
                                    String estadoDocumento = "";
                                    String certificado = Parametro.getDocsElectronicos() + "certificado.p12";
                                    String rutaSalida = Parametro.getDocsElectronicos() + "firmados";
                                    String claveAcceso = "";
                                    String autorizacionXml = "";
                                    String respuestaAutoriz = "";
                                    String doc_electronico = "0";
                                    String subtotal_0 = "0";
                                    String subtotal_2 = String.valueOf(subtotal_final);
                                    String subtotal_3 = "0";
                                    String iva_3 = "0";
                                    FacturaElectronica objFE = new FacturaElectronica();
                                    boolean ok = true;
                                    String error = "";
                                    String fecha_emision = Fecha.getFecha("ISO");
                                    try {
                                        if (doc_electronico.compareTo("0") == 0) {
                                            ok = false;
                                            String vecSerie[] = serie_factura.split("-");
                                            claveAcceso = objFE.getClaveAcceso(Fecha.getFecha("ISO"), "01", ruc_empresa, ambiente, vecSerie[0] + vecSerie[1], Cadena.setSecuencial(num_factura), tipoEmision);
//                                            if (tipoEmision.compareTo("2") == 0) {
//                                                claveAcceso = objClavesSri.getSigClave(Fecha.getFecha("SQL"), "01", ruc_empresa, ambiente, tipoEmision);
//                                            }
                                            objFE.generarXml(claveAcceso, ambiente, tipoEmision, razon_social_empresa, nombre_comercial, ruc_empresa, email_info, numeros_soporte, pagina_web, "01", vecSerie[0], vecSerie[1],
                                                    Cadena.setSecuencial(num_factura), dir_matriz, Cadena.setFecha(fecha_emision), direccion_sucursal, num_resolucion, oblga_contabilidad,
                                                    tipo_documento_cliente, razon_social, ruc, Addons.redondearDecimales(subtotal_final), Addons.redondearDecimales(descuento_final), subtotal_0, subtotal_2, Addons.redondearDecimales(iva_final), subtotal_3, iva_3, Addons.redondearDecimales(total_final), codigoFormaPago,
                                                    ids_productos, descripciones, cantidades, preciosUnitarios, descuentos, subtotales, ivas, pIvas, codigoIvas, direccion, email);
                                            String documentoXml = Parametro.getDocsElectronicos() + "generados/" + claveAcceso + ".xml";
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
                                                        autorizacionXml = Utils.getStringFromFile(Parametro.getDocsElectronicos() + "firmados/" + claveAcceso + ".xml");
                                                        ok = true;
                                                    } else {
                                                        autorizacionXml = Utils.getStringFromFile(Parametro.getDocsElectronicos() + "firmados/" + claveAcceso + ".xml");
                                                        ok = true;
                                                    }
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                        System.out.println("Error portal tratando de generar y fimrar xml :" + e.getMessage());
                                        msg = e.getMessage();
                                    }
                                    
                                    msg = error;
                                    if (ok) {
                                        msg = "Ha ocurrido un error inesperado, por favor, vuelva a intentarlo más tarde o "
                                                + "contáctese con el administrador del sistema para mayor información.";
                                        String paramAnticipo = Utiles.ConcatenarValoresAnticipo(idCliAnt, monto_vajar);
                                        id_factura_venta = ObjFacturaVenta.insertar(id_instalacion, id_sucursal, idPuntoEmision, id_cliente,
                                                usuario, serie_factura, num_factura, autorizacion, ruc, razon_social, fecha_emision, direccion, telefono, id_forma_pago,
                                                forma_pago, banco, num_cheque, num_comp_pago, gastos_bancos, id_plan_cuenta_banco, son, observacion, Addons.redondearDecimales(subtotal_final),
                                                subtotal_0, subtotal_2, subtotal_3, Addons.redondearDecimales(descuento_final), Addons.redondearDecimales(iva_final), iva_3, Addons.redondearDecimales(total_final), "array[" + paramArtic + "]", ret_num_serie, ret_num_retencion,
                                                ret_autorizacion, ret_fecha_emision, ret_ejercicio_fiscal_mes, ret_ejercicio_fiscal, ret_impuesto_retenido, "array[" + paramRet + "]::varchar[]",
                                                "array[" + paramAsiento + "]", xmlFirmado, estadoDocumento, claveAcceso, autorizacionXml, respuestaAutoriz, ip, id_plan_cuenta_anticipo, paramAnticipo);
                                        if (id_factura_venta.compareTo("-1") != 0) {
                                            respuesta[0] = id_factura_venta;
                                            respuesta[1] = "";
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Error en portal" + e.getMessage());
            } finally {
                ObjFacturaVenta.cerrar();
            }

        } else {
            msg = "El Cliente no tiene ningun anticipo para esta orden de trabajo.";
        }
        if (respuesta[0].compareTo("-1") == 0) {
            respuesta[1] = msg;
        }
        return respuesta;
    }
    
}
