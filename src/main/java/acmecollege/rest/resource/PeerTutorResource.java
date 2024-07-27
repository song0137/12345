/********************************************************************************************************2*4*w*
 * File:  PeerTutorRegistrationResource.java
 * Course materials CST 8277
 * 
  * Updated by:  Group 6
 *   041058923, Xiao, Song (as from ACSIS)
 *   041058416, Chang, Lijun (as from ACSIS)
 *   041044400, Cao, Dandan (as from ACSIS)
 *   040874986, Zeng, Ruxu (as from ACSIS)
 *   
 *   Last modified on: 2024-07-22
 * 
 */
package acmecollege.rest.resource;

import static acmecollege.utility.MyConstants.ADMIN_ROLE;
import static acmecollege.utility.MyConstants.PEER_TUTOR_SUBRESOURCE_NAME ;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_PATH;
import static acmecollege.utility.MyConstants.USER_ROLE;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import acmecollege.ejb.ACMECollegeService;
import acmecollege.entity.PeerTutor;

@Path(PEER_TUTOR_SUBRESOURCE_NAME )
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)

public class PeerTutorResource {
	private static final Logger LOG = LogManager.getLogger();

	@EJB
	protected ACMECollegeService service;

	@Inject
	protected SecurityContext sc;
	
	@GET
    @RolesAllowed({ADMIN_ROLE})
    public Response getAllPeerTutors() {
        LOG.debug("Retrieving all peer tutors ...");
        List<PeerTutor> peerTutors = service.getAllPeerTutors();
        LOG.debug("Peer tutors found = {}", peerTutors);
        return Response.ok(peerTutors).build();
    }
	
	@GET
	@RolesAllowed({ ADMIN_ROLE, USER_ROLE })
	@Path(RESOURCE_PATH_ID_PATH)
	public Response getPeerTutorById(@PathParam(RESOURCE_PATH_ID_ELEMENT)int peerTutorId) {
		LOG.debug("Retrieving specific peer tutor with id = {}\", peerTutorId");
		
		PeerTutor peerTutor = service.getPeerTutorById(peerTutorId);
		if (peerTutor == null)
			return Response.status(Status.NOT_FOUND).build();
		LOG.debug("Peer tutor found = {}", peerTutor);
		return Response.ok(peerTutor).build();
	}

	@POST
	@RolesAllowed({ ADMIN_ROLE })
	public Response addPeerTutor(PeerTutor newPeerTutor) {
		LOG.debug("Adding a new peer tutor = {}", newPeerTutor);
        Response response = null;
        service.persistPeerTutor(newPeerTutor);
        response = Response.ok(newPeerTutor).build();
        return response;
    }

		

	@DELETE
	@RolesAllowed({ ADMIN_ROLE })
	@Path(RESOURCE_PATH_ID_PATH)
	public Response deletePeerTutor(@PathParam(RESOURCE_PATH_ID_ELEMENT) int peerTutorId) {
		LOG.debug("Deleting a peer tutor with id = {}", peerTutorId);
		Response response = null;
		service.deletePeerTutorById(peerTutorId);
		response = Response.ok("Deleted a peer tutor with id: " + peerTutorId).build();
		return response;
	}

}
