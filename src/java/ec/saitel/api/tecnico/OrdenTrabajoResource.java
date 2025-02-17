/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.saitel.api.tecnico;

//import javax.ws.rs.core.Context;
import ec.saitel.api.tecnico.dao.OrdenTrabajoDao;
import ec.saitel.api.tecnico.model.OrdenTrabajoModel;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author Sistemas
 */
@Path("ordenTrabajo")
public class OrdenTrabajoResource {
    
    private final OrdenTrabajoDao ordenTrabajoDao = new OrdenTrabajoDao();

//    @Context
//    private UriInfo context;

    /**
     * Creates a new instance of OrdenTrabajoResource
     */
    public OrdenTrabajoResource() {
    }

    /**
     * PUT method for updating or creating an instance of OrdenTrabajoResource
     * @param ordenTrabajo representation for the resource
     * @return String 
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response putOrdenTrabajoSolucion(OrdenTrabajoModel ordenTrabajo) 
    {
        if(ordenTrabajo.getIdOrdenTrabajo() != null) {
            String ok = this.ordenTrabajoDao.guardar(ordenTrabajo);
            if( ok.compareTo("ok")==0 ) {
                Response.status(Response.Status.OK).build();
            }
            return Response.ok(ok).build();
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
