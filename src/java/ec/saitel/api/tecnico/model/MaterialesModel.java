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
public class MaterialesModel 
{
    private String idProducto;
    private String idMaterial;
    private String cantidad;
    private String precioActual;
    private String detalle;
    private String gastoadicional;

    public String getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }

    public String getIdMaterial() {
        return idMaterial;
    }

    public void setIdMaterial(String idMaterial) {
        this.idMaterial = idMaterial;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getPrecioActual() {
        return precioActual;
    }

    public void setPrecioActual(String precioActual) {
        this.precioActual = precioActual;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public String getGastoadicional() {
        return gastoadicional;
    }

    public void setGastoadicional(String gastoadicional) {
        this.gastoadicional = gastoadicional;
    }

}
