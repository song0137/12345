/********************************************************************************************************2*4*w*
 * File:  MembershipCardResource.java
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
import static acmecollege.utility.MyConstants.MEMBERSHIP_CARD_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_PATH;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;

import java.util.List;
import java.util.Map;

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
import javax.ws.rs.ForbiddenException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.soteria.WrappingCallerPrincipal;

import acmecollege.ejb.ACMECollegeService;
import acmecollege.entity.MembershipCard;
import acmecollege.entity.SecurityUser;
import acmecollege.entity.Student;

@Path(MEMBERSHIP_CARD_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MembershipCardResource {
    
    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMECollegeService service;

    @Inject
    protected SecurityContext sc;
    
    
    @GET
    @RolesAllowed({ADMIN_ROLE})
    public Response getAllMembershipCards() {
        LOG.debug("Retrieving all membership cards...");
        List<MembershipCard> membershipCards = service.getAllMembershipCards();
        LOG.debug("Membership cards found = {}", membershipCards);
        return Response.ok(membershipCards).build();
    }
    
    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response getMembershipCardById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int cardId) {
        LOG.debug("Retrieving membership card with id = {}", cardId);
       
        Response response = null;
        MembershipCard card = null;
        
        if (sc.isCallerInRole(ADMIN_ROLE)) {
            card = service.getMembershipCardById(cardId);
            response = Response.status(card == null ? Status.NOT_FOUND : Status.OK).entity(card).build();
        } else if (sc.isCallerInRole(USER_ROLE)) {
            WrappingCallerPrincipal wCallerPrincipal = (WrappingCallerPrincipal) sc.getCallerPrincipal();
            SecurityUser sUser = (SecurityUser) wCallerPrincipal.getWrapped();
            Student student = sUser.getStudent();
            card = service.getMembershipCardById(cardId);
            if (student != null && student.getId() == card.getOwner().getId()) {
                response = Response.status(Status.OK).entity(card).build();
            } else {
                throw new ForbiddenException("User trys to access resource, it does not own (wrong userid)");
            }
        } else {
            response = Response.status(Status.BAD_REQUEST).build();
        }
        return response;
    }

 
    
    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addMembershipCard(Map<String, Integer> request) {
    	
    	try {
    		int studentId = request.get("student_id").intValue();
    		Student student = service.getStudentById(studentId); 
    		if (student == null) {
    			return Response.status(Response.Status.NOT_FOUND)
		                        .entity("Student is not found")
		                        .build();
    		}
    		
    		else {
    			MembershipCard newCard = new MembershipCard();
    			newCard.setOwner(student);
    			newCard = service.persistMembershipCard(newCard);
    			return Response.status(Status.OK).entity(newCard).build();
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
    public Response updateMembershipCard(@PathParam(RESOURCE_PATH_ID_ELEMENT) int cardId, MembershipCard updatingMembershipCard) {
        LOG.debug("Updating a specific membership card with id = {}", cardId);
        MembershipCard updatingMC = service.updateMembershipCardById(cardId, updatingMembershipCard);
        return Response.ok(updatingMC).build();
    }
    
    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response deleteMembershipCard(@PathParam(RESOURCE_PATH_ID_ELEMENT) int cardId) {
        LOG.debug("Deleting membership card with id = {}", cardId);
        Response response = null;
        service.deleteMembershipCardById(cardId);
        response = Response.ok("Deleted Membership card with id: " + cardId).build();
        return response;
        
    }
}