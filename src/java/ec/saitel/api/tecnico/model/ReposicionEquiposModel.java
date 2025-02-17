/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.saitel.api.tecnico.model;

/**
 *
 * @author Sistemas
 */
public class ReposicionEquiposModel 
{
    private String tipoReposicion;
    private String idActivoActual;
    private String equipoActual;
    private String idActivoNuevo;
    private String equipoNuevo;

    public String getTipoReposicion() {
        return tipoReposicion;
    }

    public void setTipoReposicion(String tipoReposicion) {
        this.tipoReposicion = tipoReposicion;
    }

    public String getIdActivoActual() {
        return idActivoActual;
    }

    public void setIdActivoActual(String idActivoActual) {
        this.idActivoActual = idActivoActual;
    }

    public String getEquipoActual() {
        return equipoActual;
    }

    public void setEquipoActual(String equipoActual) {
        this.equipoActual = equipoActual;
    }

    public String getIdActivoNuevo() {
        return idActivoNuevo;
    }

    public void setIdActivoNuevo(String idActivoNuevo) {
        this.idActivoNuevo = idActivoNuevo;
    }

    public String getEquipoNuevo() {
        return equipoNuevo;
    }

    public void setEquipoNuevo(String equipoNuevo) {
        this.equipoNuevo = equipoNuevo;
    }
    
}
