/********************************************************************************************************2*4*w*
 * File:  ACMEColegeService.java
 * Course materials CST 8277
 *
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
import static acmecollege.utility.MyConstants.PEER_TUTOR_REGISTRATION_RESOURCE_NAME ;
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
import acmecollege.entity.PeerTutorRegistration;

@Path(PEER_TUTOR_REGISTRATION_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PeerTutorRegistrationResource {
	private static final Logger LOG = LogManager.getLogger();

	@EJB
	protected ACMECollegeService service;

	@Inject
	protected SecurityContext sc;
	
	@GET
    @RolesAllowed({ADMIN_ROLE})
    public Response getAllPeerTutorRegistrations() {
        LOG.debug("Retrieving all peer tutor registrations ...");
        List<PeerTutorRegistration> peerTutorRegistrations = service.getAllRegistration();
        LOG.debug("Peer tutors Registrations found = {}", peerTutorRegistrations);
        return Response.ok(peerTutorRegistrations).build();
    }
	
	@GET
	@RolesAllowed({ ADMIN_ROLE, USER_ROLE })
	@Path(RESOURCE_PATH_ID_PATH)
	public Response getPeerTutorRegistrationById(@PathParam("studentId") int studentId, @PathParam("courseId") int courseId) {
		LOG.debug("Retrieving specific peer tutor registration with course id = {}, student id = {}", courseId, studentId);
		
		PeerTutorRegistration peerTutorRegistration = service.getPeerTutorRegistrationById(studentId, courseId);
		if (peerTutorRegistration == null)
			return Response.status(Status.NOT_FOUND).build();
		LOG.debug("Peer tutor registration found = {}", peerTutorRegistration);
		return Response.ok(peerTutorRegistration).build();
	}



	@POST
	@RolesAllowed({ ADMIN_ROLE })
	public Response addPeerTutorRegistration(PeerTutorRegistration newPeerTutorRegistration) {
		LOG.debug("Adding a new peer tutor registration= {}", newPeerTutorRegistration);
        Response response = null;
        service.persistPeerTutorRegistration(newPeerTutorRegistration);
        response = Response.ok(newPeerTutorRegistration).build();
        return response;
    }

		

	@DELETE
	@RolesAllowed({ ADMIN_ROLE })
	@Path(RESOURCE_PATH_ID_PATH)
	public Response deletePeerTutor(@PathParam("studentId") int studentId, @PathParam("courseId") int courseId) {
		LOG.debug("Deleting a peer tutor registration with course id = {}, student id = {}", courseId, studentId);
		Response response = null;
		service.deletePeerTutorRegistrationById(studentId, courseId);
		response = Response.ok("Deleted a peer tutor registration with student id: " + studentId + ", course id: " + courseId).build();
		return response;
	}

}
