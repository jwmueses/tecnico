/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.saitel.api.seg.dao;

/**
 *
 * @author Sistemas
 */

import io.jsonwebtoken.SignatureAlgorithm;
import ec.saitel.api.seg.model.Usuario;
import ec.saitel.api.util.DataBase;
import io.jsonwebtoken.Jwts;
import static io.jsonwebtoken.security.Keys.secretKeyFor;
import java.sql.ResultSet;
import java.util.Date;

/**
 *
 * @author Jorge
 */
public class UsuarioDAO extends DataBase 
{
    private final long EXPIRACION_EN_MINUTOS = 1800;
    
    public Usuario getUsuarioJWT(String jwt)
    {
        Usuario usuario = new Usuario();
        
        try( ResultSet rs = this.consulta("select * from tbl_usuario where jwt='" + jwt + "'") ) {
            if(rs.next()){
                usuario.setAlias(rs.getString("alias")!=null ? rs.getString("alias") : "");
                usuario.setIdPerfil(rs.getString("id_rol")!=null ? rs.getInt("id_rol") : 1);
                usuario.setEliminado(rs.getString("eliminado")!=null ? rs.getBoolean("eliminado") : false);
                usuario.setEstado(rs.getString("estado")!=null ? rs.getBoolean("estado") : true);
                usuario.setCambiarClave(rs.getString("cambiar_clave")!=null ? rs.getBoolean("cambiar_clave") : true);
                usuario.setJwt(rs.getString("jwt")!=null ? rs.getString("jwt") : "");
                rs.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return usuario;
    }
    
    public String generarJWT(Usuario usuario)
    {
        long tiempo = System.currentTimeMillis();
        long tiempoAExpirar = tiempo + (EXPIRACION_EN_MINUTOS * 60000);
        Date dateAExpirar = new Date(tiempoAExpirar);
        
        String jwt = Jwts.builder()                     // (1)
            .header()                                   // (2) optional
                .add("alg", "HS512")
                .add("typ", "JWT")
                .and()

            .subject( usuario.getAlias())                             // (3) JSON Claims, or
            .claim( "perfil", usuario.getIdPerfil() )
            .expiration(dateAExpirar)
            //.content(aByteArray, "text/plain")        //     any byte[] content, with media type

            .signWith( secretKeyFor(SignatureAlgorithm.HS512) )                      // (4) if signing, or
            //.encryptWith(key, keyAlg, encryptionAlg)  //     if encrypting

            .compact();                                 // (5)
        return jwt;
    }
    
}

