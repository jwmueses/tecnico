/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.saitel.api.tecnico.model;

import java.util.List;

/**
 *
 * @author Sistemas
 */
public class OrdenTrabajoModel 
{
    private String idOrdenTrabajo;
    private String numOrden;
    private String solucionado;
    private String conformidad;
    private String usuarioSolucion;
    private String claveUsuario;
    private String recomendacion;
    private String atencion;
    private String capacidadEfectiva;
    private String velocidadCarga;
    private String velocidadDescarga;
    private String tipoTrabajo;
    private String idPreinstalacion;
    private String idSoporte;
    private String idSucursal;
    private String idInstalacion;
    private String fechaInicioVisita;
    private String horaInicioVisita;
    private String idEmpleado;
    private String otProcedente;
    
    private String idBodegaEmpleado;
    private String usuarioKey;
    private String remoteAddr;
    private String receptorAnterior;
    private String macAnterior;
    
    private String equiposRetirados;
    private String equiposUtilizados;
    
    private InstalacionModel instalacion;
    private List<MaterialesModel> ordenTrabajoMaterial;
    private List<ReposicionEquiposModel> reposicionEquipos;

    public String getIdOrdenTrabajo() {
        return idOrdenTrabajo;
    }

    public void setIdOrdenTrabajo(String idOrdenTrabajo) {
        this.idOrdenTrabajo = idOrdenTrabajo;
    }

    public String getNumOrden() {
        return numOrden;
    }

    public void setNumOrden(String numOrden) {
        this.numOrden = numOrden;
    }

    public String getSolucionado() {
        return solucionado;
    }

    public void setSolucionado(String solucionado) {
        this.solucionado = solucionado;
    }

    public String getConformidad() {
        return conformidad;
    }

    public void setConformidad(String conformidad) {
        this.conformidad = conformidad;
    }

    public String getUsuarioSolucion() {
        return usuarioSolucion;
    }

    public void setUsuarioSolucion(String usuarioSolucion) {
        this.usuarioSolucion = usuarioSolucion;
    }

    public String getClaveUsuario() {
        return claveUsuario;
    }

    public void setClaveUsuario(String claveUsuario) {
        this.claveUsuario = claveUsuario;
    }

    public String getRecomendacion() {
        return recomendacion;
    }

    public void setRecomendacion(String recomendacion) {
        this.recomendacion = recomendacion;
    }

    public String getAtencion() {
        return atencion;
    }

    public void setAtencion(String atencion) {
        this.atencion = atencion;
    }

    public String getCapacidadEfectiva() {
        return capacidadEfectiva;
    }

    public void setCapacidadEfectiva(String capacidadEfectiva) {
        this.capacidadEfectiva = capacidadEfectiva;
    }

    public String getVelocidadCarga() {
        return velocidadCarga;
    }

    public void setVelocidadCarga(String velocidadCarga) {
        this.velocidadCarga = velocidadCarga;
    }

    public String getVelocidadDescarga() {
        return velocidadDescarga;
    }

    public void setVelocidadDescarga(String velocidadDescarga) {
        this.velocidadDescarga = velocidadDescarga;
    }

    public String getTipoTrabajo() {
        return tipoTrabajo;
    }

    public void setTipoTrabajo(String tipoTrabajo) {
        this.tipoTrabajo = tipoTrabajo;
    }

    public String getIdPreinstalacion() {
        return idPreinstalacion;
    }

    public void setIdPreinstalacion(String idPreinstalacion) {
        this.idPreinstalacion = idPreinstalacion;
    }

    public String getIdSoporte() {
        return idSoporte;
    }

    public void setIdSoporte(String idSoporte) {
        this.idSoporte = idSoporte;
    }

    public String getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(String idSucursal) {
        this.idSucursal = idSucursal;
    }

    public String getIdInstalacion() {
        return idInstalacion;
    }

    public void setIdInstalacion(String idInstalacion) {
        this.idInstalacion = idInstalacion;
    }

    public String getFechaInicioVisita() {
        return fechaInicioVisita;
    }

    public void setFechaInicioVisita(String fechaInicioVisita) {
        this.fechaInicioVisita = fechaInicioVisita;
    }

    public String getHoraInicioVisita() {
        return horaInicioVisita;
    }

    public void setHoraInicioVisita(String horaInicioVisita) {
        this.horaInicioVisita = horaInicioVisita;
    }

    public String getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(String idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getOtProcedente() {
        return otProcedente;
    }

    public void setOtProcedente(String otProcedente) {
        this.otProcedente = otProcedente;
    }

    public String getIdBodegaEmpleado() {
        return idBodegaEmpleado;
    }

    public void setIdBodegaEmpleado(String idBodegaEmpleado) {
        this.idBodegaEmpleado = idBodegaEmpleado;
    }
    
    public String getUsuarioKey() {
        return usuarioKey;
    }

    public void setUsuarioKey(String usuarioKey) {
        this.usuarioKey = usuarioKey;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public String getReceptorAnterior() {
        return receptorAnterior;
    }

    public void setReceptorAnterior(String receptorAnterior) {
        this.receptorAnterior = receptorAnterior;
    }

    public String getMacAnterior() {
        return macAnterior;
    }

    public void setMacAnterior(String macAnterior) {
        this.macAnterior = macAnterior;
    }

    public String getEquiposRetirados() {
        return equiposRetirados;
    }

    public void setEquiposRetirados(String equiposRetirados) {
        this.equiposRetirados = equiposRetirados;
    }

    public String getEquiposUtilizados() {
        return equiposUtilizados;
    }

    public void setEquiposUtilizados(String equiposUtilizados) {
        this.equiposUtilizados = equiposUtilizados;
    }

    public InstalacionModel getInstalacion() {
        return instalacion;
    }

    public void setInstalacion(InstalacionModel instalacion) {
        this.instalacion = instalacion;
    }

    public List<MaterialesModel> getOrdenTrabajoMaterial() {
        return ordenTrabajoMaterial;
    }

    public void setOrdenTrabajoMaterial(List<MaterialesModel> ordenTrabajoMaterial) {
        this.ordenTrabajoMaterial = ordenTrabajoMaterial;
    }

    public List<ReposicionEquiposModel> getReposicionEquipos() {
        return reposicionEquipos;
    }

    public void setReposicionEquipos(List<ReposicionEquiposModel> reposicionEquipos) {
        this.reposicionEquipos = reposicionEquipos;
    }

    
}
