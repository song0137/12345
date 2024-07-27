/********************************************************************************************************2*4*w*
 * File:  CourseResource.java
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

import static acmecollege.utility.MyConstants.USER_ROLE;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_PATH;
import static acmecollege.utility.MyConstants.ADMIN_ROLE;
import static acmecollege.utility.MyConstants.COURSE_RESOURCE_NAME;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
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
import acmecollege.entity.Course;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path(COURSE_RESOURCE_NAME)
public class CourseResource {
	private static final Logger LOG = LogManager.getLogger();

	@EJB
	protected ACMECollegeService service;

	@Inject
	protected SecurityContext sc;

	@POST
	@RolesAllowed({ ADMIN_ROLE })
	public Response addCourse(Course newCourse) {
		LOG.debug("Adding a new course: ", newCourse);
		Response response = null;
		Course courseToBeAdded = null;

		if (!sc.isCallerInRole(ADMIN_ROLE)) {
	        throw new ForbiddenException("Unauthorized access: Admin role required");
	    }
		try {
			courseToBeAdded = service.addCourse(newCourse);
		} catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
				
		response = Response.ok(courseToBeAdded).build();

		return response;
	}

	@GET
	@RolesAllowed({ ADMIN_ROLE, USER_ROLE })
	public Response getAllCourses() {
		LOG.debug("Retrieving all students ...");
		Response response = Response.noContent().build();

		List<Course> courses = service.getAllCourses();
		response = Response.ok(courses).build();

		return response;
	}

	@GET
	@RolesAllowed({ ADMIN_ROLE, USER_ROLE })
	@Path(RESOURCE_PATH_ID_PATH)
	public Response getCourseById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
		LOG.debug("Retrieving course with id " + id);
		Response response = null;
		Course course = null;

		course = service.getCourseById(id);
		response = Response.status(course == null ? Status.NOT_FOUND : Status.OK).entity(course).build();

		return response;
	}

	@PUT
	@RolesAllowed({ ADMIN_ROLE })
	@Path(RESOURCE_PATH_ID_PATH)
	public Response updateCourse(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id, Course updatedCourse) {
		LOG.debug("Updating course with id " + id);

		Response response = null;
		Course courseToBeUpdated = null;
		courseToBeUpdated = service.updateCourseById(id, updatedCourse);
		response = Response.status(courseToBeUpdated == null ? Status.NOT_FOUND : Status.OK).entity(courseToBeUpdated)
				.build();

		return response;
	}

	@DELETE
	@RolesAllowed({ ADMIN_ROLE })
	@Path(RESOURCE_PATH_ID_PATH)
	public Response deleteCourseById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
		LOG.debug("Deleting course with id " + id);
		service.deleteCourseById(id);
		Response response = Response.ok("Course with id " + id + " is deleted").build();

		return response;
	}

}