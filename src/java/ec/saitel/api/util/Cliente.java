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
public class Cliente extends DataBase {

    public Cliente(String m, int p, String db, String u, String c) {
        super(m, p, db, u, c);
    }

    public ResultSet getClientes() {
        return this.consulta("SELECT id_cliente,razon_social FROM vta_cliente order by razon_social;");
    }

    /*public ResultSet getClientes(String txt)
    {
        return this.consulta("SELECT id_cliente,ruc,razon_social,direccion,telefono FROM vta_cliente WHERE ruc = '"+txt+"';");
    }*/
    public ResultSet getCliente(String id) {
        return this.consulta("SELECT *, date_part('year',  age(now(), fecha_nacimiento)) as edad FROM tbl_cliente where id_cliente=" + id);
    }

    public ResultSet getClientePorRuc(String ruc) {
        return this.consulta("SELECT *, date_part('year',  age(now(), fecha_nacimiento)) as edad FROM tbl_cliente where upper(ruc)='" + ruc.toUpperCase() + "'");
    }

    public ResultSet getIdCliente(String estab, String ruc) {
        return this.consulta("SELECT * FROM vta_cliente WHERE establecimiento='" + estab + "' and upper(ruc)='" + ruc.toUpperCase() + "';");
    }

    public ResultSet getIdCliente(String estab, String ruc, String contratocliint) {
        return this.consulta("SELECT * FROM vta_cliente WHERE establecimiento='" + estab + "' and upper(ruc)='" + ruc.toUpperCase() + "' " + (contratocliint.trim().compareTo("si") == 0 ? " " : " and id_cliente in (select id_cliente from tbl_contrato where anulado=false) ") + ";");
    }

    public ResultSet getConsumidorFinal() {
        return this.consulta("SELECT * FROM tbl_cliente where ruc='9999999999999';");
    }

    public String getClienteJSON(String txt) {
        /*ResultSet rs = this.consulta("SELECT id_cliente,ruc,razon_social,direccion,telefono FROM vta_cliente WHERE ruc like '"+txt+"%' or lower(razon_social) like '%"+txt.toLowerCase()+"%' order by razon_social;");*/
        ResultSet rs = this.consulta("SELECT id_cliente,ruc,razon_social,direccion,telefono "
                + "FROM tbl_cliente WHERE ruc='" + txt + "'");
        String tbl = this.getJSON(rs);
        try {
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tbl;
    }

    public String getIdPlanCuentaAnticipo(String id_cliente) {
        String id = "";
        try {
            ResultSet rs = this.consulta("SELECT id_plan_cuenta_anticipo FROM vta_cliente WHERE id_cliente=" + id_cliente);
            if (rs.next()) {
                id = rs.getString("id_plan_cuenta_anticipo") != null ? rs.getString("id_plan_cuenta_anticipo") : "";
                rs.close();
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }
    
    public String getEMail(String id_cliente) {
        String id = "";
        try {
            ResultSet rs = this.consulta("SELECT email FROM tbl_cliente WHERE id_cliente=" + id_cliente);
            if (rs.next()) {
                id = rs.getString("email") != null ? rs.getString("email") : "";
                rs.close();
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    public String getClienteAllJSON(String estab, String ruc) {
        ResultSet rs = this.consulta("SELECT id_cliente,ruc,razon_social,ciudad,telefono,direccion FROM vta_cliente WHERE establecimiento='" + estab + "' and ruc='" + ruc + "';");
        String tbl = this.getJSON(rs);
        try {
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tbl;
    }

    public String getClienteInternetJSON(String estab, String ruc) {
        ResultSet rs = this.consulta("SELECT id_cliente,ruc,razon_social,ciudad,telefono,direccion FROM vta_cliente WHERE establecimiento='" + estab + "' and ruc='" + ruc + "' and id_cliente in "
                + "(select id_cliente from tbl_contrato where terminado=false and anulado=false);");
        String tbl = this.getJSON(rs);
        try {
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tbl;
    }

    public String getClienteInternetJSONS(String estab, String ruc) {
        ResultSet rs = this.consulta("SELECT id_cliente,ruc,razon_social,ciudad,telefono,direccion FROM vta_cliente WHERE establecimiento='" + estab + "' and ruc='" + ruc + "';");
        String tbl = this.getJSON(rs);
        try {
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tbl;
    }

    public boolean estaDuplicado(String id, String establecimiento, String ruc) {
        ResultSet res = this.consulta("SELECT * FROM tbl_cliente where establecimiento='" + establecimiento + "' and ruc='" + ruc + "' and id_cliente<>" + id);
        if (this.getFilas(res) > 0) {
            return true;
        }
        try {
            res.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String validarConadis3Edad(String id, String ruc, String carneConadis) 
    {
        String ok = "ok";
        
        try {
            String sql = "select (select count(*) as num_3_edad from vta_cliente where fecha_nacimiento is not null and ruc ='"+ruc+"' and id_cliente<>"+id+"),\n";
            if( carneConadis.compareTo("") > 0 ) {
                sql += "(select count(*) as num_conadis from tbl_cliente where carne_conadis<>'' and ruc ='"+ruc+"' and id_cliente<>"+id+"),\n" +
                    "(SELECT count(*) as conadis_registrado FROM tbl_cliente where carne_conadis='"+carneConadis+"' and id_cliente<>"+id+")";
            } else {
                sql += "0 as num_conadis, 0 as conadis_registrado";
            }
                
            ResultSet res = this.consulta(sql);
            if( res.next() ) {
                int num3Edad = res.getString("num_3_edad")!=null ? res.getInt("num_3_edad") : 0;
                int numConadis = res.getString("num_conadis")!=null ? res.getInt("num_conadis") : 0;
                int conadisRegistrado = res.getString("conadis_registrado")!=null ? res.getInt("conadis_registrado") : 0;
                
                if( numConadis > 0) {
                    ok = "El cliente con DNI "+ruc+" ya tiene registrado un número de carné CONADIS en otro registro";
                } else if( conadisRegistrado > 0) {
                            ok = "Número de carné CONADIS ya se encuentra registrado";
                } else if( num3Edad > 0) {
                            ok = "El cliente con DNI "+ruc+" ya tiene registrada la fecha de nacimiento en otro registro";
                }
                
                res.close();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok;
    }

    /*public int getIdClienteNuevo()
    {
        int r = -1;
        try{
            ResultSet res = this.consulta("SELECT max(id_cliente) FROM tbl_cliente");
            if(res.next()){
                r = res.getString(1)!=null ? res.getInt(1) : -1;
                res.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return r;
    }*/
    public boolean insertar(String ruc, String raz, String dir, String tel) {
        return this.ejecutar("INSERT INTO tbl_cliente(ruc, razon_social, direccion, telefono) VALUES('" + ruc + "', '" + raz + "', '" + dir + "', '" + tel + "');");
    }

    public boolean actualizar(String id, String ruc, String raz, String dir, String tel) {
        return this.ejecutar("UPDATE tbl_cliente SET ruc='" + ruc + "', razon_social='" + raz + "', direccion='" + dir + "', telefono='" + tel + "' WHERE id_cliente=" + id + ";");
    }

    public boolean actualizar(String id, String campo, String movil) {
        return this.ejecutar("UPDATE tbl_cliente SET " + campo + "='" + movil + "' WHERE id_cliente=" + id + ";");
    }

    public boolean actualizar(String id, String antena) {
        return this.ejecutar("UPDATE tbl_instalacion SET antena_acoplada='" + antena + "' WHERE id_instalacion=" + id + ";");
    }

    public String insertar(int id_sucursal, String establecimiento, String tipo_documento, String ruc, String raz, String id_plan_cuenta, String id_plan_cuenta_anticipo,
            String dir, String pai, String pro, String ciu, String parr,
            String tel, String tel_cl, String tel_mo, String fax, String mail, String web, String con, String obs, String tipo_doc_debito, String documento, String cliente_debito,
            String forma_pago, String tipo_cuenta, String tipo_tarjeta_credito, String tarjeta_credito_caduca, String num_cuenta) {
//        fecha_nacimiento = fecha_nacimiento.compareTo("") != 0 ? "'" + fecha_nacimiento + "'" : "NULL";
        tarjeta_credito_caduca = tarjeta_credito_caduca.compareTo("") != 0 ? "'" + tarjeta_credito_caduca + "'" : "NULL";
        return this.insert("INSERT INTO tbl_cliente(id_sucursal, establecimiento, tipo_documento, ruc, razon_social, id_plan_cuenta, id_plan_cuenta_anticipo, "
                + "direccion, pais, id_provincia, id_ciudad, id_parroquia, telefono, movil_claro, movil_movistar, fax, email, web, contacto, observacion, "
                + "tipo_doc_debito, documento, cliente_debito, forma_pago, tipo_cuenta, tipo_tarjeta_credito, tarjeta_credito_caduca, num_cuenta) "
                + "VALUES(" + id_sucursal + ", '" + establecimiento + "', '" + tipo_documento + "', '" + ruc + "', '" + raz + "', " + id_plan_cuenta
                + ", " + id_plan_cuenta_anticipo + ", '" + dir + "', '" + pai + "', " + pro + ", " + ciu + ", '" + parr + "', '" + tel + "', '" + tel_cl + "', '" + tel_mo + "', '" + fax + "', '" + mail + "', '" + web
                + "', '" + con + "', '" + obs + "', '" + tipo_doc_debito + "', '" + documento + "', '" + cliente_debito + "', '" + forma_pago + "', '" + tipo_cuenta
                + "', '" + tipo_tarjeta_credito + "', " + tarjeta_credito_caduca + ", '" + num_cuenta + "');");
    }

    public boolean actualizar(String id, String establecimiento, String tipo_documento, String ruc, String id_plan_cuenta, String id_plan_cuenta_anticipo,
            String dir, String pai, String pro, String ciu, String parr,
            String tel, String tel_cl, String tel_mo, String fax, String mail, String web, String con, String obs, String tipo_doc_debito, String documento, String cliente_debito,
            String forma_pago, String tipo_cuenta, String tipo_tarjeta_credito, String tarjeta_credito_caduca, String num_cuenta) {
//        fecha_nacimiento = fecha_nacimiento.compareTo("") != 0 ? "'" + fecha_nacimiento + "'" : "NULL";
        tarjeta_credito_caduca = tarjeta_credito_caduca.compareTo("") != 0 ? "'" + tarjeta_credito_caduca + "'" : "NULL";
        return this.ejecutar("UPDATE tbl_cliente SET establecimiento='" + establecimiento + "', tipo_documento='" + tipo_documento + "', ruc='" + ruc
                + "', id_plan_cuenta=" + id_plan_cuenta + ", id_plan_cuenta_anticipo=" + id_plan_cuenta_anticipo + ", direccion='" + dir
                + "', pais='" + pai + "', id_provincia=" + pro + ", id_ciudad=" + ciu + ", id_parroquia='" + parr + "', telefono='" + tel + "', movil_claro='" + tel_cl
                + "', movil_movistar='" + tel_mo + "', fax='" + fax + "', email='" + mail + "', web='" + web + "', contacto='" + con + "', observacion='" + obs
                + "', tipo_doc_debito='" + tipo_doc_debito + "', documento='" + documento + "', cliente_debito='" + cliente_debito + "', forma_pago='" + forma_pago
                + "', tipo_cuenta='" + tipo_cuenta + "', tipo_tarjeta_credito='" + tipo_tarjeta_credito + "', tarjeta_credito_caduca=" + tarjeta_credito_caduca
                + ", num_cuenta='" + num_cuenta + "' WHERE id_cliente=" + id + ";");
    }

    public boolean setConvenio(String id_num_cuenta, String tipo_doc_debito, String documento, String cliente_debito, String forma_pago, String tipo_cuenta,
            String tipo_tarjeta_credito, String tarjeta_credito_caduca, String num_cuenta) {
        tarjeta_credito_caduca = tarjeta_credito_caduca.compareTo("") != 0 ? "'" + tarjeta_credito_caduca + "'" : "NULL";
        return this.ejecutar("UPDATE tbl_cliente SET tipo_doc_debito='" + tipo_doc_debito + "', documento='" + documento + "', cliente_debito='" + cliente_debito + "', forma_pago='" + forma_pago
                + "', tipo_cuenta='" + tipo_cuenta + "', tipo_tarjeta_credito='" + tipo_tarjeta_credito + "', tarjeta_credito_caduca=" + tarjeta_credito_caduca
                + ", num_cuenta='" + num_cuenta + "' WHERE num_cuenta='" + id_num_cuenta + "';");
    }

    public boolean setRazonSocial(String idCliente, String raz) {
        return this.ejecutar("UPDATE tbl_cliente SET razon_social='" + raz + "' WHERE id_cliente=" + idCliente);
    }
    
    public boolean setFechaNacimiento(String idCliente, String fecha_nacimiento) {
        fecha_nacimiento = fecha_nacimiento.compareTo("") != 0 ? "'" + fecha_nacimiento + "'" : "NULL";
        return this.ejecutar("UPDATE tbl_cliente SET fecha_nacimiento = "+fecha_nacimiento+" WHERE id_cliente=" + idCliente);
    }
    
    public boolean setConadis(String idCliente, String carne_conadis) {
        return this.ejecutar("UPDATE tbl_cliente SET carne_conadis='" + carne_conadis + "' WHERE id_cliente=" + idCliente);
    }
    
    public boolean setTarjetaCancelada(String id_num_cuenta) {
        return this.ejecutar("insert into tbl_cliente_tarjeta_cancelada(id_cliente, tipo_doc_debito, documento, cliente_debito, forma_pago, tipo_cuenta, "
                + "tipo_tarjeta_credito, tarjeta_credito_caduca, num_cuenta) "
                + "select id_cliente, tipo_doc_debito, documento, cliente_debito, forma_pago, tipo_cuenta, tipo_tarjeta_credito, tarjeta_credito_caduca, num_cuenta "
                + "from tbl_cliente where num_cuenta='" + id_num_cuenta + "';");
    }

    public String getIdClientes(String estab, String ruc) {
        String id = "-1";
        try {
            ResultSet rs = this.consulta("SELECT id_cliente FROM vta_cliente WHERE WHERE establecimiento='" + estab + "' and ruc='" + ruc + "';");
            if (rs.next()) {
                id = rs.getString("id_cliente") != null ? rs.getString("id_cliente") : "-1";
                rs.close();
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    public int getClienteFrelancePendiente(String ruc) {
        int id = 0;
        try {
            ResultSet rs = this.consulta("select count(*)as conteo from tbl_cliente_freelance where ruc='" + ruc + "' and estado ='1';");
            if (rs.next()) {
                id = rs.getString("conteo") != null ? rs.getInt("conteo") : 0;
                rs.close();
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    
    
    
    ///////////////////////////////////////////////////////////////////  CERTIFICADOS    ///////////////////////////////////////////////////
    
    
    public ResultSet getCertificados(String idCliente) {
        return this.consulta("SELECT id_cliente_certificado,nombre_certificado,usuario_emision, fecha_emision || ' ' || hora_emision as fecha FROM vta_cliente_certificado where id_cliente="+idCliente+" order by fecha_emision;");
    }
    
    public ResultSet getCertificado(String idCertificado) {
        return this.consulta("SELECT * FROM vta_cliente_certificado where id_cliente_certificado="+idCertificado);
    }
    
    public String setCertificado(String idCliente, String idCertificado, String usuario) {
        return this.insert("INSERT INTO tbl_cliente_certificado(id_cliente, id_certificado_isp, usuario_emision) VALUES(" + idCliente + ", " + idCertificado + ", '" + usuario + "');");
    }
    
}
