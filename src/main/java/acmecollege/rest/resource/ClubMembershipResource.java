/********************************************************************************************************2*4*w*
 * File:  ClubMembershipResource.java
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
import static acmecollege.utility.MyConstants.USER_ROLE;
import static acmecollege.utility.MyConstants.CLUB_MEMBERSHIP_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_PATH;

import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import acmecollege.ejb.ACMECollegeService;
import acmecollege.entity.ClubMembership;
import acmecollege.entity.DurationAndStatus;
import acmecollege.entity.StudentClub;

@Path(CLUB_MEMBERSHIP_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ClubMembershipResource {
    
    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMECollegeService service;

    @Inject
    protected SecurityContext sc;
    
    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    public Response getAllClubMemberships() {
        LOG.debug("Retrieving all club memberships...");
        List<ClubMembership> clubMemberships = service.getAllClubMemberships();
        LOG.debug("Club memberships found = {}", clubMemberships);
        return Response.ok(clubMemberships).build();
    }
    
    @GET
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response getClubMembershipById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int membershipId) {
        LOG.debug("Retrieving club membership with id = {}", membershipId);
        	// For ADMIN_ROLE, retrieve any membership
            ClubMembership clubMembership = service.getClubMembershipById(membershipId);
            Response response = Response.status(clubMembership == null ? Status.NOT_FOUND : Status.OK).entity(clubMembership).build();
            return response;

    }

    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addClubMembership(Map<String, Integer> request) {

        try {
    		int cid = request.get("club_id").intValue();
    		StudentClub sc = service.getStudentClubById(cid);
    		if (sc == null) {
    			return Response.status(Response.Status.NOT_FOUND)
		                        .entity("This student club doesn't exist")
		                        .build();
    		}
    		
    		else {
    			ClubMembership cm = new ClubMembership();
    			cm.setStudentClub(sc);
    			DurationAndStatus das = new DurationAndStatus();
    			LocalDateTime current = LocalDateTime.now();
    			das.setStartDate(current);
    			das.setEndDate(current.plusYears(2));
    			cm.setDurationAndStatus(das);
    			cm = service.persistClubMembership(cm);
    			return Response.status(Status.OK).entity(cm).build();
    		}
    	}
    	catch(Exception e) {
    		return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Invalid Request. Internal error")
                    .build();
    	}
    }
    
    @PUT
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response updatelubMembership(@PathParam(RESOURCE_PATH_ID_ELEMENT) int updateMembershipId, ClubMembership updatedRecord) {
       	ClubMembership updateCM = service.updateClubMembership(updateMembershipId, updatedRecord);
    	return Response.ok(updateCM).build();
    	
    }
    
    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response deleteClubMembership(@PathParam(RESOURCE_PATH_ID_ELEMENT) int membershipId) {
        LOG.debug("Deleting club membership with id = {}", membershipId);
        Response response = null;
        service.deleteClubMembershipById(membershipId);
        response = Response.ok("Deleted club with id: " + membershipId).build();
        return response;
    }
    
    

}
