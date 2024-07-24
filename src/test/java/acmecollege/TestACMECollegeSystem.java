/********************************************************************************************************2*4*w*
 * File:  TestACMECollegeSystem.java
 * Course materials CST 8277
 * Teddy Yap
 * (Original Author) Mike Norman
 *
 *
 * Updated by:  Group 6
 *   041058923, Xiao, Song (as from ACSIS)
 *   041058416, Chang, Lijun (as from ACSIS)
 *   041044400, Cao, Dandan (as from ACSIS)
 *   040874986, Zeng, Ruxu (as from ACSIS)
 *   
 *   Last modified on: 2024-07-22
 */
package acmecollege;

import static acmecollege.utility.MyConstants.APPLICATION_API_VERSION;
import static acmecollege.utility.MyConstants.APPLICATION_CONTEXT_ROOT;
import static acmecollege.utility.MyConstants.DEFAULT_ADMIN_USER;
import static acmecollege.utility.MyConstants.DEFAULT_ADMIN_USER_PASSWORD;
import static acmecollege.utility.MyConstants.DEFAULT_USER;
import static acmecollege.utility.MyConstants.DEFAULT_USER_PASSWORD;
import static acmecollege.utility.MyConstants.STUDENT_RESOURCE_NAME;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import static acmecollege.utility.MyConstants.CLUB_MEMBERSHIP_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.COURSE_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.MEMBERSHIP_CARD_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.PEER_TUTOR_SUBRESOURCE_NAME;
import static acmecollege.utility.MyConstants.STUDENT_CLUB_RESOURCE_NAME;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import acmecollege.entity.Student;
import acmecollege.entity.AcademicStudentClub;
import acmecollege.entity.Course;
import acmecollege.entity.NonAcademicStudentClub;
import acmecollege.entity.PeerTutor;
import acmecollege.entity.StudentClub;

@SuppressWarnings("unused")

@TestMethodOrder(MethodOrderer.MethodName.class)
public class TestACMECollegeSystem {
    private static final Class<?> _thisClaz = MethodHandles.lookup().lookupClass();
    private static final Logger logger = LogManager.getLogger(_thisClaz);

    private Client client;
    
    static final String HTTP_SCHEMA = "http";
    static final String HOST = "localhost";
    static final int PORT = 8080;
    
    static final int SUCCESS_CODE = 200;
    static final int SUCCESS_NO_CONTENT_CODE = 204;
    static final int FORBIDDEN_CODE = 403;
    static final int UNAUTHORIZED_CODE = 401;
    static final int CONFLICT_CODE = 409;
    static final int SERVER_ERROR_CODE = 500;


    // Test fixture(s)
    static URI uri;
    static HttpAuthenticationFeature adminAuth;
    static HttpAuthenticationFeature userAuth;
    
    static HttpAuthenticationFeature notAuthenticatedAuth;

    @BeforeAll
    public static void oneTimeSetUp() throws Exception {
        logger.debug("oneTimeSetUp");
        uri = UriBuilder
            .fromUri(APPLICATION_CONTEXT_ROOT + APPLICATION_API_VERSION)
            .scheme(HTTP_SCHEMA)
            .host(HOST)
            .port(PORT)
            .build();
        adminAuth = HttpAuthenticationFeature.basic(DEFAULT_ADMIN_USER, DEFAULT_ADMIN_USER_PASSWORD);
        userAuth = HttpAuthenticationFeature.basic(DEFAULT_USER, DEFAULT_USER_PASSWORD);
    }

    protected WebTarget webTarget;
    @BeforeEach
    public void setUp() {
        Client client = ClientBuilder.newClient(
            new ClientConfig().register(MyObjectMapperProvider.class).register(new LoggingFeature()));
        webTarget = client.target(uri);
    }
    
    
    @AfterEach
    public void tearDown() {
    	client.close();
    }

    /**
     * Test case for retrieving all students with the admin role.
     * It verifies that the admin role can get all students.
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(1)
    public void test01_all_students_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            //.register(userAuth)
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<Student> students = response.readEntity(new GenericType<List<Student>>(){});
        assertThat(students, is(not(empty())));
        assertThat(students, hasSize(1));
    }
    
    /**
     * Test case for retrieving all students with the user role.
     * It verifies that the user role cannot get all students.
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(2)
    public void test02_all_students_with_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(STUDENT_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(FORBIDDEN_CODE));
    }
    
    /**
     * Test case for retrieving all students with a random user that is neither admin nor user role.
     * It verifies that a random user cannot get all students.
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(3)
    public void test03_all_students_with_not_authenticated_user() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(notAuthenticatedAuth)
            .path(STUDENT_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(UNAUTHORIZED_CODE));
    }
    
    /**
     * Test case for retrieving a specific student by id with the admin role.
     * It verifies that the admin role can get a specific student by id.
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(4)
    public void test04_student_by_id_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME + "/1")
            .request()
            .get();
        assertThat(response.getStatus(), is(SUCCESS_CODE));
        Student resStudent = response.readEntity(Student.class);
        assertNotNull(resStudent);
        assertThat(resStudent.getId(), is(1));
    }
    
    /**
     * Test case for retrieving a specific student by id with the user role.
     * It verifies that the user role can get a specific student by id if the student is linked to the SecurityUser.
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(5)
    public void test05_student_by_id_with_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(STUDENT_RESOURCE_NAME + "/1")
            .request()
            .get();
        assertThat(response.getStatus(), is(SUCCESS_CODE));
        Student resStudent = response.readEntity(Student.class);
        assertNotNull(resStudent);
        assertThat(resStudent.getId(), is(1));
    }
    
    /**
     * Test case for creating a new student with the admin role.
     * It verifies that the admin role can create a new student via the POST request.
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(6)
    public void test06_new_student_with_adminrole() throws JsonMappingException, JsonProcessingException {
    	Student newStudent = new Student();
    	newStudent.setFirstName("Yongjing");
    	newStudent.setLastName("Ge");

        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME)
            .request()
            .post(Entity.entity(newStudent, MediaType.APPLICATION_JSON));
        
        assertThat(response.getStatus(), is(SUCCESS_CODE));
        Student resStudent = response.readEntity(Student.class);
        assertNotNull(resStudent);
        assertThat(resStudent.getFirstName(), is("Yongjing"));
        assertThat(resStudent.getLastName(), is("Ge"));
    }
    
    /**
     * Test case for retrieving a specific student by id with the user role where the student is not linked to the SecurityUser.
     * It verifies that the user role cannot get a specific student by id if the student is linked to the SecurityUser.
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(7)
    public void test07_student_not_linked_security_user_by_id_with_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(STUDENT_RESOURCE_NAME + "/2")
            .request()
            .get();
        assertThat(response.getStatus(), is(FORBIDDEN_CODE));
    }
    
    /**
     * Test case for deleting a specific student by id with the admin role.
     * It verifies that the admin role can delete a specific student by id.
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(8)
    public void test08_student_removal_by_id_with_adminrole() throws JsonMappingException, JsonProcessingException { 	
    	Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_RESOURCE_NAME)
            .request()
            .get();
        List<Student> allStudents = response.readEntity(new GenericType<List<Student>>() {});
        int originalSize = allStudents.size();
        int removalId = allStudents.get(originalSize - 1).getId();
        
        Response resRemoval = webTarget
        		.register(adminAuth)
        		.path(STUDENT_RESOURCE_NAME + "/" + removalId)
        		.request()
        		.delete();
        assertThat(resRemoval.getStatus(), is(SUCCESS_CODE));
        
        Response newListResponse = webTarget
                .register(adminAuth)
                .path(STUDENT_RESOURCE_NAME)
                .request()
                .get();
        allStudents = newListResponse.readEntity(new GenericType<List<Student>>() {});
        assertThat(allStudents.size(), is(originalSize - 1));
        assertFalse(allStudents.stream().anyMatch(s -> s.getId() == removalId));
    }
    
    /**
     * Test case for retrieving all student clubs with the admin role.
     * It verifies that the admin role can get all student clubs.
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(9)
    public void test09_all_student_clubs_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_CLUB_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(SUCCESS_CODE));
    }
    
    /**
     * Test case for retrieving all student clubs with the user role.
     * It verifies that the user role can get all student clubs.
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(10)
    public void test10_all_student_clubs_with_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(STUDENT_CLUB_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(SUCCESS_CODE));
    }
    
    /**
     * Test case for retrieving all student clubs with a random user that is neither admin nor user role.
     * It verifies that a random user cannot get all student clubs.
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(11)
    public void test11_all_student_clubs_with_not_authenticated_user() throws JsonMappingException, JsonProcessingException {
    	Response response = webTarget
                .register(notAuthenticatedAuth)
                .path(STUDENT_CLUB_RESOURCE_NAME)
                .request()
                .get();
            assertThat(response.getStatus(), is(UNAUTHORIZED_CODE));
    }
    
    /**
     * Test case for retrieving a specific student club by id with the admin role.
     * It verifies that the admin role can get a specific student club by id.
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(12)
    public void test12_student_club_by_id_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_CLUB_RESOURCE_NAME + "/1")
            .request()
            .get();
        assertThat(response.getStatus(), is(SUCCESS_CODE));
        StudentClub resStudentClub = response.readEntity(StudentClub.class);
        assertNotNull(resStudentClub);
        assertThat(resStudentClub.getId(), is(1));
    }
    
    /**
     * Test case for retrieving a specific student club by id with the user role.
     * It verifies that the user role can get a specific student club by id.
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(13)
    public void test13_student_club_by_id_with_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(STUDENT_CLUB_RESOURCE_NAME + "/1")
            .request()
            .get();
        assertThat(response.getStatus(), is(SUCCESS_CODE));
        StudentClub resStudentClub = response.readEntity(StudentClub.class);
        assertNotNull(resStudentClub);
        assertThat(resStudentClub.getId(), is(1));
    }
    
    /**
     * Test case for creating a new student club with the admin role.
     * It verifies that the admin role can create a new student club via the POST request.
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(14)
    public void test14_new_student_club_with_adminrole() throws JsonMappingException, JsonProcessingException {
    	StudentClub newStudentClub = new AcademicStudentClub();
    	newStudentClub.setName("Reading Club");

        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_CLUB_RESOURCE_NAME)
            .request()
            .post(Entity.entity(newStudentClub, MediaType.APPLICATION_JSON));
        
        assertThat(response.getStatus(), is(SUCCESS_CODE));
        StudentClub resStudentClub = response.readEntity(StudentClub.class);
        assertNotNull(resStudentClub);
        assertThat(resStudentClub.getName(), is("Reading Club"));
    }
    
    /**
     * Test case for creating a new student club with the user role.
     * It verifies that the user role cannot create a new student club via the POST request.
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(15)
    public void test15_new_student_club_with_userrole() throws JsonMappingException, JsonProcessingException {
    	StudentClub newStudentClub = new NonAcademicStudentClub();
    	newStudentClub.setName("Running Club");

        Response response = webTarget
            .register(userAuth)
            .path(STUDENT_CLUB_RESOURCE_NAME)
            .request()
            .post(Entity.entity(newStudentClub, MediaType.APPLICATION_JSON));
        
        assertThat(response.getStatus(), is(FORBIDDEN_CODE));
    }
    
    /**
     * Test case for creating a student club that already exists with the admin role.
     * It verifies that the admin role cannot create a student club that already exists via the POST request.
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(16)
    public void test16_new_student_club_with_duplicate_name_with_adminrole() throws JsonMappingException, JsonProcessingException {
    	StudentClub newStudentClub = new AcademicStudentClub();
    	newStudentClub.setName("Reading Club");

        Response response = webTarget
            .register(adminAuth)
            .path(STUDENT_CLUB_RESOURCE_NAME)
            .request()
            .post(Entity.entity(newStudentClub, MediaType.APPLICATION_JSON));
        
        assertThat(response.getStatus(), is(CONFLICT_CODE));
    }
    
    /**
     * Test case for deleting a specific student club by id with the admin role.
     * It verifies that the admin role can delete a specific student club by id.
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(17)
    public void test17_student_club_removal_by_id_with_adminrole() throws JsonMappingException, JsonProcessingException { 	
        int removalId = 1;
        
        Response resRemoval = webTarget
        		.register(adminAuth)
        		.path(STUDENT_CLUB_RESOURCE_NAME + "/" + removalId)
        		.request()
        		.delete();
        assertThat(resRemoval.getStatus(), is(SUCCESS_CODE));
        
        Response resRemovalCheck = webTarget
        		.register(adminAuth)
        		.path(STUDENT_CLUB_RESOURCE_NAME + "/" + removalId)
        		.request()
        		.get();
        
        assertNotEquals(resRemovalCheck.getStatus(), SUCCESS_CODE);
    }
    
    /**
     * Test case for deleting a specific student club by id with the user role.
     * It verifies that the user role cannot delete a specific student club by id.
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(18)
    public void test18_student_club_removal_by_id_with_userrole() throws JsonMappingException, JsonProcessingException { 	
        int removalId = 2;

        Response resRemoval = webTarget
        		.register(userAuth)
        		.path(STUDENT_CLUB_RESOURCE_NAME + "/" + removalId)
        		.request()
        		.delete();
        assertThat(resRemoval.getStatus(), is(FORBIDDEN_CODE));
    }
    
    
    /**
     * Test case for retrieving all Club Memberships with the admin role.
     * It verifies that the admin role can get all Club Memberships.
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    
    @Test
    @Order(19)
    public void test19_GET_all_club_memberShips_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(CLUB_MEMBERSHIP_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(SUCCESS_CODE));
    }
    
    /**
     * Test case for retrieving all Club Memberships with the user role.
     * It verifies that the user role can get all Club Memberships.
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(20)
    public void test20_GET_all_club_memberShips_with_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(CLUB_MEMBERSHIP_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(SUCCESS_CODE));
    }
    
    /**
     * Test case for retrieving a specific Club Membership by id with the admin role.
     * It verifies that the admin role can get a specific Club Membership by id.
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    
    @Test
    @Order(21)
    public void test21_GET_specific_club_memberShip_by_id_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(CLUB_MEMBERSHIP_RESOURCE_NAME + "/1")
            .request()
            .get();
        assertThat(response.getStatus(), is(SUCCESS_CODE));
    }
    
    /**
     * Test case for creating a new Club Membership with the admin role.
     * It verifies that the admin role can create a new Club Membership via the POST request.
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(22)
    public void test22_POST_new_club_memberShip_with_adminrole() throws JsonMappingException, JsonProcessingException {
    	     
        Map<String, Integer> newClubMemberShip= new HashMap<>();
        newClubMemberShip.put("club_id", 1);
    	
        Response response = webTarget
            .register(adminAuth)
            .path(CLUB_MEMBERSHIP_RESOURCE_NAME)
            .request()
            .post(Entity.entity(newClubMemberShip, MediaType.APPLICATION_JSON));
        
        assertThat(response.getStatus(), is(SERVER_ERROR_CODE));
    }
    
    /**
     * Test case for deleting a specific Club Membership by id with the admin role.
     * It verifies that the admin role can delete a specific Club Membership by id.
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(23)
    public void test23_DELETE_remove_club_memberShip_by_id_with_adminrole() throws JsonMappingException, JsonProcessingException { 	
    	int lastId = 2;        
        Response responseDelete = webTarget
            .register(adminAuth)
            .path(CLUB_MEMBERSHIP_RESOURCE_NAME + "/" + lastId)
            .request()
            .delete();
        
        assertThat(responseDelete.getStatus(), is(SUCCESS_CODE));
    }
    
    
    /**
     * Test case for retrieving all Membership cards with the admin role.
     * It verifies that the admin role can get all Membership cards.
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    
    @Test
    @Order(24)
    public void test24_GET_all_memberShip_cards_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(MEMBERSHIP_CARD_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(SUCCESS_CODE));
    }
    
    
    /**
     * Test case for retrieving all Membership cards with the user role.
     * It verifies that the user role can not retrive all Membership Cards.
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(25)
    public void test25_GET_all_memberShip_cards_with_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(MEMBERSHIP_CARD_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(FORBIDDEN_CODE)); 
    }
    
    /**
     * Test case for retrieving a specific  Membership card by id with the admin role.
     * It verifies that the admin role can get a specific Membership card by id.
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    
    @Test
    @Order(26)
    public void test26_GET_specific_memberShip_card_by_id_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(MEMBERSHIP_CARD_RESOURCE_NAME + "/1")
            .request()
            .get();
        assertThat(response.getStatus(), is(SUCCESS_CODE));
    }
    
    /**
     * Test case for retrieving a specific Membership card by id with the user role.
     * It verifies that the user role can get a specific Membership card by id.
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    
    @Test
    @Order(27)
    public void test27_GET_specific_memberShip_card_by_id_with_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(MEMBERSHIP_CARD_RESOURCE_NAME + "/1")
            .request()
            .get();
        assertThat(response.getStatus(), is(SUCCESS_CODE));
    }
    
    /**
     * Test case for creating a new Membership card with the admin role.
     * It verifies that the admin role can create a new Membership card via the POST request.
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(28)
    public void test28_POST_new_memberShip_card_with_adminrole() throws JsonMappingException, JsonProcessingException {
    	     
        Map<String, Integer> newMembershipCard= new HashMap<>();
        newMembershipCard.put("student_id", 1);
    	
        Response response = webTarget
            .register(adminAuth)
            .path(MEMBERSHIP_CARD_RESOURCE_NAME)
            .request()
            .post(Entity.entity(newMembershipCard, MediaType.APPLICATION_JSON));
        
        assertThat(response.getStatus(), is(SUCCESS_CODE));
    }
    
    /**
     * Test case for deleting a specific Membership card by id with the admin role.
     * It verifies that the admin role can delete a specific Membership card by id.
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    @Test
    @Order(29)
    public void test29_DELETE_remove_memberShip_card_by_id_with_adminrole() throws JsonMappingException, JsonProcessingException { 	
    	int lastId = 2;        
        Response responseDelete = webTarget
            .register(adminAuth)
            .path(MEMBERSHIP_CARD_RESOURCE_NAME + "/" + lastId)
            .request()
            .delete();
        
        Response response2 = webTarget
                .register(adminAuth)
                .path(MEMBERSHIP_CARD_RESOURCE_NAME + "/" + lastId)
                .request()
                .get();
        
        assertNotEquals(response2.getStatus(), SUCCESS_CODE);
    }
    
    /**
     * Test case to verify the if retrieval of all courses with the admin role is successful.
     * @throws JsonMappingException if there is an issue with JSON mapping
     * @throws JsonProcessingException if there is an issue with JSON processing
     */
    @Test
    @Order(30)
    public void test30_GET_all_courses_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(adminAuth)
            .path(COURSE_RESOURCE_NAME)
            .request()
            .get();
        
        assertThat(response.getStatus(), is(200));
        List<Course> courses = response.readEntity(new GenericType<List<Course>>(){});
        assertThat(courses, is(not(empty())));
        assertThat(courses, hasSize(courses.size()));
    }
    
    /**
     * Test case to verify if the retrieval of all courses with a non-authorized role(not security/user) is blocked.
     * @throws JsonMappingException if there is an issue with JSON mapping
     * @throws JsonProcessingException if there is an issue with JSON processing
     */
    @Test
    @Order(31)
    public void test31_GET_all_courses_with_not_authenticated_user() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(notAuthenticatedAuth)
            .path(COURSE_RESOURCE_NAME)
            .request()
            .get();
        
        assertThat(response.getStatus(), is(UNAUTHORIZED_CODE));
    }
 
    /**
     * Test case to verify if the user role can retrieve a specific course by id.
     * @throws JsonMappingException if there is an issue with JSON mapping
     * @throws JsonProcessingException if there is an issue with JSON processing
     */
    @Test
    @Order(32)
    public void test32_GET_course_by_id_with_userrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            .register(userAuth)
            .path(COURSE_RESOURCE_NAME + "/1")
            .request()
            .get();
        assertThat(response.getStatus(), is(200)); 
        Course course = response.readEntity(Course.class);
        assertNotNull(course);
        assertThat(course.getId(), is(1));
    }
    
    /**
     * This test checks if user role can is not allowed to a new course.
     * @throws JsonMappingException if there is an issue with JSON mapping
     * @throws JsonProcessingException if there is an issue with JSON processing
     */
    @Test
    @Order(33)
    public void test33_POST_add_new_course_with_userrole() throws JsonMappingException, JsonProcessingException {
    	Course newCourse = new Course();
    	newCourse.setCourseCode("CST8277");
    	newCourse.setCourseTitle("Java EE");
    	newCourse.setYear(2024);
    	newCourse.setSemester("WINTER");
    	newCourse.setCreditUnits(20);
    	newCourse.setOnline((byte) 1);
    	
        Response response = webTarget
            .register(userAuth)
            .path(COURSE_RESOURCE_NAME)
            .request()
            .post(Entity.entity(newCourse, MediaType.APPLICATION_JSON));
        
        assertThat(response.getStatus(), is(FORBIDDEN_CODE));
    }
    
    /**
     * Test case to verify if admin role can update a course with id.
     * @throws JsonMappingException if there is an issue with JSON mapping
     * @throws JsonProcessingException if there is an issue with JSON processing
     */
    @Test
    @Order(34)
    public void test34_PUT_update_course_with_adminrole() throws JsonMappingException, JsonProcessingException {
    	Response responseAll = webTarget
                .register(adminAuth)
                .path(COURSE_RESOURCE_NAME)
                .request()
                .get();
        assertThat(responseAll.getStatus(), is(SUCCESS_CODE));
        List<Course> courses = responseAll.readEntity(new GenericType<List<Course>>(){});
        assertThat(courses, is(not(empty())));
            
        Course existingCourse = courses.get(0);
        
        existingCourse.setCourseTitle("Updated Course Title");
    	
        Response responsePut = webTarget
            .register(adminAuth)
            .path(COURSE_RESOURCE_NAME + "/" + existingCourse.getId())
            .request()
            .put(Entity.entity(existingCourse, MediaType.APPLICATION_JSON));
        
        assertThat(responsePut.getStatus(), is(SUCCESS_CODE));
    }
    
    /**
     * This test verifies that an admin user can remove a course by its ID.
     * @throws JsonMappingException if there is an issue with JSON mapping
     * @throws JsonProcessingException if there is an issue with JSON processing
     */
    @Test
    @Order(35)
    public void test35_DELETE_course_by_id_with_adminrole() throws JsonMappingException, JsonProcessingException {
    	 // Retrieve the list of courses to obtain an existing course ID for deletion
        Response responseGetAll = webTarget
                .register(adminAuth)
                .path(COURSE_RESOURCE_NAME)
                .request()
                .get();
        
        assertThat(responseGetAll.getStatus(), is(SUCCESS_CODE));
        
        List<Course> coursesBeforeDeletion = responseGetAll.readEntity(new GenericType<List<Course>>(){});
        int initialSize = coursesBeforeDeletion.size();
        assertThat(coursesBeforeDeletion, is(not(empty())));
        Course courseToDelete = coursesBeforeDeletion.get(coursesBeforeDeletion.size() - 1);
        
        // Delete the course
        Response responseDeletion = webTarget
                .register(adminAuth)
                .path(COURSE_RESOURCE_NAME + "/" + courseToDelete.getId()) 
                .request()
                .delete();

        // Verify that the responseDeletion status is 200
        assertThat(responseDeletion.getStatus(), is(SUCCESS_CODE));
        
        // Retrieve the list of courses after deletion
        Response newListResponse = webTarget
                .register(adminAuth)
                .path(COURSE_RESOURCE_NAME)
                .request()
                .get();
        assertThat(newListResponse.getStatus(), is(SUCCESS_CODE));
        List<Course> updatedCourses = newListResponse.readEntity(new GenericType<List<Course>>(){});
        
        // Verify that the size of the updated courses list is one less than the initial size
        assertThat(updatedCourses.size(), is(initialSize - 1));
        
        // Verify that the deleted course is not present in the updated courses list
        assertFalse(updatedCourses.stream().anyMatch(c -> c.getId() == courseToDelete.getId()));
    }
    
    /**
	 * This test verifies that an admin user can get all peer tutors.
	 * 
	 * @throws JsonMappingException    if there is an issue with JSON mapping
	 * @throws JsonProcessingException if there is an issue with JSON processing
	 */
	@Test
	@Order(36)
	public void test36_getAllPeerTutors_with_adminrole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget.register(adminAuth).path(PEER_TUTOR_SUBRESOURCE_NAME).request().get();
		assertThat(response.getStatus(), is(SUCCESS_CODE));
	}
	
	/**
	 * This test verifies that an admin user can get a specific peer tutor by id.
	 * 
	 * @throws JsonMappingException    if there is an issue with JSON mapping
	 * @throws JsonProcessingException if there is an issue with JSON processing
	 */
	@Test
	@Order(37)
	public void test37_get_specific_peerTutor_by_id_with_adminrole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget.register(adminAuth).path(PEER_TUTOR_SUBRESOURCE_NAME + "/1" ).request().get();
		assertThat(response.getStatus(), is(SUCCESS_CODE));
	}
	
	/**
	 * This test verifies that an admin user can add a new peer tutor.
	 * 
	 * @throws JsonMappingException    if there is an issue with JSON mapping
	 * @throws JsonProcessingException if there is an issue with JSON processing
	 */
	@Test
	@Order(38)
    public void test38_postPeerTutor_with_adminrole() throws JsonMappingException, JsonProcessingException {
		PeerTutor peerTutor = new PeerTutor();
		peerTutor.setFirstName("John");
		peerTutor.setLastName("Smith");
		peerTutor.setProgram("Information and Communications Technology");
		Response response = webTarget.register(adminAuth).path(PEER_TUTOR_SUBRESOURCE_NAME).request()
				.post(Entity.entity(peerTutor, MediaType.APPLICATION_JSON));

		assertThat(response.getStatus(), is(SUCCESS_CODE));
		Student resStudent = response.readEntity(Student.class);
		assertNotNull(resStudent);
		assertThat(resStudent.getFirstName(), is("John"));
		assertThat(resStudent.getLastName(), is("Smith"));

    }
	
	/**
	 * This test verifies that a user cannot add a new peer tutor.
	 * 
	 * @throws JsonMappingException    if there is an issue with JSON mapping
	 * @throws JsonProcessingException if there is an issue with JSON processing
	 */
	@Test
	@Order(39)
    public void test39_postPeerTutor_with_userrole() throws JsonMappingException, JsonProcessingException {
		PeerTutor peerTutor = new PeerTutor();
		peerTutor.setFirstName("Jane");
		peerTutor.setLastName("Doe");
		Response response = webTarget.register(userAuth).path(PEER_TUTOR_SUBRESOURCE_NAME).request()
				.post(Entity.entity(peerTutor, MediaType.APPLICATION_JSON));

		assertThat(response.getStatus(), is(FORBIDDEN_CODE));
	}

	/**
	 * This test verifies that an admin user can delete a specific peer tutor by it.
	 * 
	 * @throws JsonMappingException    if there is an issue with JSON mapping
	 * @throws JsonProcessingException if there is an issue with JSON processing
	 */
	@Test
	@Order(40)
	public void test40_delete_peer_tutor_by_id_with_adminrole() throws JsonMappingException, JsonProcessingException {
		Response response = webTarget.register(adminAuth).path(PEER_TUTOR_SUBRESOURCE_NAME).request().get();
		List<PeerTutor> allpeerTutors = response.readEntity(new GenericType<List<PeerTutor>>() {
		});
		int originalSize = allpeerTutors.size();
		int removalId = allpeerTutors.get(originalSize - 1).getId();

		Response resRemoval = webTarget.register(adminAuth).path(PEER_TUTOR_SUBRESOURCE_NAME + "/" + removalId).request()
				.delete();
		assertThat(resRemoval.getStatus(), is(SUCCESS_CODE));

		Response newListResponse = webTarget.register(adminAuth).path(PEER_TUTOR_SUBRESOURCE_NAME).request().get();
		allpeerTutors = newListResponse.readEntity(new GenericType<List<PeerTutor>>() {
		});
		assertThat(allpeerTutors.size(), is(originalSize - 1));
		assertFalse(allpeerTutors.stream().anyMatch(s -> s.getId() == removalId));
	}
    
}
