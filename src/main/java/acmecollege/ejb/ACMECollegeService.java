/********************************************************************************************************2*4*w*
 * File:  ACMEColegeService.java
 * Course materials CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman
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

package acmecollege.ejb;

import static acmecollege.entity.SecurityRole.SECURITY_ROLE_BY_NAME;
import static acmecollege.entity.SecurityUser.SECURITY_USER_BY_STUDENT_ID_QUERY;
import static acmecollege.entity.StudentClub.ALL_STUDENT_CLUBS_QUERY_NAME;
import static acmecollege.entity.StudentClub.SPECIFIC_STUDENT_CLUB_QUERY_NAME;
import static acmecollege.entity.StudentClub.IS_DUPLICATE_QUERY_NAME;
import static acmecollege.entity.Student.ALL_STUDENTS_QUERY_NAME;
import static acmecollege.utility.MyConstants.DEFAULT_KEY_SIZE;
import static acmecollege.utility.MyConstants.DEFAULT_PROPERTY_ALGORITHM;
import static acmecollege.utility.MyConstants.DEFAULT_PROPERTY_ITERATIONS;
import static acmecollege.utility.MyConstants.DEFAULT_SALT_SIZE;
import static acmecollege.utility.MyConstants.DEFAULT_USER_PASSWORD;
import static acmecollege.utility.MyConstants.DEFAULT_USER_PREFIX;
import static acmecollege.utility.MyConstants.PARAM1;
import static acmecollege.utility.MyConstants.PROPERTY_ALGORITHM;
import static acmecollege.utility.MyConstants.PROPERTY_ITERATIONS;
import static acmecollege.utility.MyConstants.PROPERTY_KEY_SIZE;
import static acmecollege.utility.MyConstants.PROPERTY_SALT_SIZE;
import static acmecollege.utility.MyConstants.PU_NAME;
import static acmecollege.utility.MyConstants.USER_ROLE;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import acmecollege.entity.ClubMembership;
import acmecollege.entity.Course;
import acmecollege.entity.MembershipCard;
import acmecollege.entity.PeerTutor;
import acmecollege.entity.PeerTutorRegistration;
import acmecollege.entity.SecurityRole;
import acmecollege.entity.SecurityUser;
import acmecollege.entity.Student;
import acmecollege.entity.StudentClub;

@SuppressWarnings("unused")

/**
 * Stateless Singleton EJB Bean - ACMECollegeService
 */
@Singleton
public class ACMECollegeService implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private static final Logger LOG = LogManager.getLogger();
    
    @PersistenceContext(name = PU_NAME)
    protected EntityManager em;
    
    @Inject
    protected Pbkdf2PasswordHash pbAndjPasswordHash;

    /* CRUD For Student */
    public List<Student> getAllStudents() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Student> cq = cb.createQuery(Student.class);
        cq.select(cq.from(Student.class));
        return em.createQuery(cq).getResultList();
    }

    public Student getStudentById(int id) {
        return em.find(Student.class, id);
    }

    @Transactional
    public Student persistStudent(Student newStudent) {
        em.persist(newStudent);
        return newStudent;
    }

    @Transactional
    public void buildUserForNewStudent(Student newStudent) {
        SecurityUser userForNewStudent = new SecurityUser();
        userForNewStudent.setUsername(
            DEFAULT_USER_PREFIX + "_" + newStudent.getFirstName() + "." + newStudent.getLastName());
        Map<String, String> pbAndjProperties = new HashMap<>();
        pbAndjProperties.put(PROPERTY_ALGORITHM, DEFAULT_PROPERTY_ALGORITHM);
        pbAndjProperties.put(PROPERTY_ITERATIONS, DEFAULT_PROPERTY_ITERATIONS);
        pbAndjProperties.put(PROPERTY_SALT_SIZE, DEFAULT_SALT_SIZE);
        pbAndjProperties.put(PROPERTY_KEY_SIZE, DEFAULT_KEY_SIZE);
        pbAndjPasswordHash.initialize(pbAndjProperties);
        String pwHash = pbAndjPasswordHash.generate(DEFAULT_USER_PASSWORD.toCharArray());
        userForNewStudent.setPwHash(pwHash);
        userForNewStudent.setStudent(newStudent);
        //SecurityRole userRole = /* TODO ACMECS01 - Use NamedQuery on SecurityRole to find USER_ROLE */ null;
        TypedQuery<SecurityRole> findRole = em.createNamedQuery(SECURITY_ROLE_BY_NAME, SecurityRole.class);
        findRole.setParameter(PARAM1, USER_ROLE);
        SecurityRole userRole = findRole.getSingleResult();
        
        userForNewStudent.getRoles().add(userRole);
        userRole.getUsers().add(userForNewStudent);
        em.persist(userForNewStudent);
    }

    @Transactional
    public PeerTutor setPeerTutorForStudentCourse(int studentId, int courseId, PeerTutor newPeerTutor) {
        Student studentToBeUpdated = em.find(Student.class, studentId);
        if (studentToBeUpdated != null) { // Student exists
            Set<PeerTutorRegistration> peerTutorRegistrations = studentToBeUpdated.getPeerTutorRegistrations();
            peerTutorRegistrations.forEach(pt -> {
                if (pt.getCourse().getId() == courseId) {
                    if (pt.getPeerTutor() != null) { // PeerTutor exists
                        PeerTutor peer = em.find(PeerTutor.class, pt.getPeerTutor().getId());
                        peer.setPeerTutor(newPeerTutor.getFirstName(),
                        				  newPeerTutor.getLastName(),
                        				  newPeerTutor.getProgram());
                        em.merge(peer);
                    }
                    else { // PeerTutor does not exist
                        pt.setPeerTutor(newPeerTutor);
                        em.merge(studentToBeUpdated);
                    }
                }
            });
            return newPeerTutor;
        }
        else return null;  // Student doesn't exists
    }

    /**
     * To update a student
     * 
     * @param id - id of entity to update
     * @param studentWithUpdates - entity with updated information
     * @return Entity with updated information
     */
    @Transactional
    public Student updateStudentById(int id, Student studentWithUpdates) {
        Student studentToBeUpdated = getStudentById(id);
        if (studentToBeUpdated != null) {
            em.refresh(studentToBeUpdated);
            em.merge(studentWithUpdates);
            em.flush();
        }
        return studentToBeUpdated;
    }

    /**
     * To delete a student by id
     * 
     * @param id - student id to delete
     */
    @Transactional
    public void deleteStudentById(int id) {
        Student student = getStudentById(id);
        if (student != null) {
            em.refresh(student);
            TypedQuery<SecurityUser> findUser = em.createNamedQuery(SECURITY_USER_BY_STUDENT_ID_QUERY, SecurityUser.class);
            findUser.setParameter(PARAM1, id);

                /* TODO ACMECS02 - Use NamedQuery on SecurityRole to find this related Student
                   so that when we remove it, the relationship from SECURITY_USER table
                   is not dangling
                */ 
            		//null;
            		 

            SecurityUser sUser = findUser.getSingleResult();
            em.remove(sUser);
            em.remove(student);
        }
    }
    
    /* CRUD For StudentClub */
    public List<StudentClub> getAllStudentClubs() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<StudentClub> cq = cb.createQuery(StudentClub.class);
        cq.select(cq.from(StudentClub.class));
        return em.createQuery(cq).getResultList();
    }

    // Why not use the build-in em.find?  The named query SPECIFIC_STUDENT_CLUB_QUERY_NAME
    // includes JOIN FETCH that we cannot add to the above API
     public StudentClub getStudentClubById(int id) {
        TypedQuery<StudentClub> specificStudentClubQuery = em.createNamedQuery(SPECIFIC_STUDENT_CLUB_QUERY_NAME, StudentClub.class);
        specificStudentClubQuery.setParameter(PARAM1, id);
        try {
            return specificStudentClubQuery.getSingleResult();
        } catch (NoResultException e) {
            // Log the exception for debugging purposes if needed
            return null;
        }

    }

     public StudentClub findStudentClubById(int cid) {
         return em.find(StudentClub.class, cid);
     }
    
    // These methods are more generic.

    public <T> List<T> getAll(Class<T> entity, String namedQuery) {
        TypedQuery<T> allQuery = em.createNamedQuery(namedQuery, entity);
        return allQuery.getResultList();
    }
    
    public <T> T getById(Class<T> entity, String namedQuery, int id) {
        TypedQuery<T> allQuery = em.createNamedQuery(namedQuery, entity);
        allQuery.setParameter(PARAM1, id);
        return allQuery.getSingleResult();
    }

    @Transactional
    public StudentClub deleteStudentClub(int id) {
        //StudentClub sc = getStudentClubById(id);
    	StudentClub sc = getById(StudentClub.class, StudentClub.SPECIFIC_STUDENT_CLUB_QUERY_NAME, id);
        if (sc != null) {
            Set<ClubMembership> memberships = sc.getClubMemberships();
            List<ClubMembership> list = new LinkedList<>();
            memberships.forEach(list::add);
            list.forEach(m -> {
                if (m.getCard() != null) {
                    MembershipCard mc = getById(MembershipCard.class, MembershipCard.ID_CARD_QUERY_NAME, m.getCard().getId());
                    mc.setClubMembership(null);
                }
                m.setCard(null);
                em.merge(m);
            });
            em.remove(sc);
            return sc;
        }
        return null;
    }
    
    // Please study & use the methods below in your test suites
    
    public boolean isDuplicated(StudentClub newStudentClub) {
    	em.flush();
        TypedQuery<Long> allStudentClubsQuery = em.createNamedQuery(IS_DUPLICATE_QUERY_NAME, Long.class);
        allStudentClubsQuery.setParameter(PARAM1, newStudentClub.getName());
        System.out.println(allStudentClubsQuery.toString());
        return (allStudentClubsQuery.getSingleResult() >= 1);
    }

    @Transactional
    public StudentClub persistStudentClub(StudentClub newStudentClub) {
        em.persist(newStudentClub);
        return newStudentClub;
    }

    @Transactional
    public StudentClub updateStudentClub(int id, StudentClub updatingStudentClub) {
    	StudentClub studentClubToBeUpdated = getStudentClubById(id);
        if (studentClubToBeUpdated != null) {
            em.refresh(studentClubToBeUpdated);
            studentClubToBeUpdated.setName(updatingStudentClub.getName());
            em.merge(studentClubToBeUpdated);
            em.flush();
        }
        return studentClubToBeUpdated;
    }
    
    
    /* CRUD for ClubMembership */
    @Transactional
    public ClubMembership persistClubMembership(ClubMembership newClubMembership) {
        em.flush();
        em.clear();
        em.persist(newClubMembership);
        return newClubMembership;
    }

    public List<ClubMembership> getAllClubMemberships() {
		return getAll(ClubMembership.class, ClubMembership.FIND_ALL);
	}
    
    public ClubMembership getClubMembershipById(int cmId) {
        TypedQuery<ClubMembership> allClubMembershipQuery = em.createNamedQuery(ClubMembership.FIND_BY_ID, ClubMembership.class);
        allClubMembershipQuery.setParameter(PARAM1, cmId);
        return allClubMembershipQuery.getSingleResult();
    }

    @Transactional
    public ClubMembership updateClubMembership(int id, ClubMembership clubMembershipWithUpdates) {
    	ClubMembership clubMembershipToBeUpdated = getClubMembershipById(id);
        if (clubMembershipToBeUpdated != null) {
            em.refresh(clubMembershipToBeUpdated);
            em.merge(clubMembershipWithUpdates);
            em.flush();
        }
        return clubMembershipToBeUpdated;
    }
    
	@Transactional
	public void deleteClubMembershipById(int membershipId) {
		ClubMembership clubMembership = getById(ClubMembership.class, ClubMembership.FIND_BY_ID, membershipId);
    	if (clubMembership != null) {
    		em.refresh(clubMembership);
    		em.remove(clubMembership);
    		em.flush();
    	}
	}
	
	/* CRUD for MembershipCard */
    // TODO
	public List<MembershipCard> getAllMembershipCards() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
    	CriteriaQuery<MembershipCard> cq = cb.createQuery(MembershipCard.class);
    	cq.select(cq.from(MembershipCard.class));
    	return em.createQuery(cq).getResultList();
	}

	public MembershipCard getMembershipCardById(int cardId) {
		return em.find(MembershipCard.class, cardId);
	}

	@Transactional
	public MembershipCard persistMembershipCard(MembershipCard newMembershipCard) {
		em.persist(newMembershipCard);
    	return newMembershipCard;
	}
	
	@Transactional
	public MembershipCard updateMembershipCardById(int cardId, MembershipCard updatingMembershipCard) {
		MembershipCard updatingMCard = getMembershipCardById(cardId);
    	if (updatingMCard != null) {
    		em.refresh(updatingMCard);
    		updatingMCard.setOwner(updatingMembershipCard.getOwner());
    		updatingMCard.setClubMembership(updatingMembershipCard.getClubMembership());
    		em.merge(updatingMCard);
    		em.flush();
    	}
    	return updatingMCard;
	}

	@Transactional
	public void deleteMembershipCardById(int cardId) {
		MembershipCard deleteMCard = getMembershipCardById(cardId);
    	if (deleteMCard != null) {
    		em.refresh(deleteMCard);
    		em.remove(deleteMCard);
    	}
	}
	
	/* CRUD for Course */
	@Transactional
    public Course addCourse(Course newCourse) {
    	em.persist(newCourse);
    	return newCourse;
    }
	
	public List<Course> getAllCourses(){
    	CriteriaBuilder cb = em.getCriteriaBuilder();
    	CriteriaQuery<Course> cq = cb.createQuery(Course.class);
    	cq.select(cq.from(Course.class));
    	return em.createQuery(cq).getResultList();
    }
    
    public Course getCourseById(int id) {
    	return em.find(Course.class, id);
    }
    
    @Transactional
    public Course updateCourseById(int id, Course courseUpdate) {
    	Course updatedCourse = getCourseById(id);
    	if (updatedCourse != null) {
    		em.refresh(updatedCourse);
    		updatedCourse.setCourseCode(courseUpdate.getCourseCode());
    		updatedCourse.setCourseTitle(courseUpdate.getCourseTitle());
    		updatedCourse.setYear(courseUpdate.getYear());
    		updatedCourse.setSemester(courseUpdate.getSemester());
    		updatedCourse.setCreditUnits(courseUpdate.getCreditUnits());
    		updatedCourse.setOnline(courseUpdate.getOnline());	
    		
    		em.merge(updatedCourse);
    		em.flush();
    	}
    	return updatedCourse;
    }
    
    @Transactional
    public void deleteCourseById(int id) {
    	Course courseDeleted = getCourseById(id);
    	if (courseDeleted != null) {
    		em.refresh(courseDeleted);
    		em.remove(courseDeleted);
    	}
    }
    
    /* CRUD for PeerTutor */

	public List<PeerTutor> getAllPeerTutors() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PeerTutor> cq = cb.createQuery(PeerTutor.class);
		cq.select(cq.from(PeerTutor.class));
		return em.createQuery(cq).getResultList();

	}

	@Transactional
	public PeerTutor persistPeerTutor(PeerTutor newPeerTutor) {
		em.persist(newPeerTutor);
		return newPeerTutor;
	}

	public PeerTutor getPeerTutorById(int peerTutorId) {
		return em.find(PeerTutor.class, peerTutorId);
	}

	@Transactional
	public void deletePeerTutorById(int peerTutorId) {
		PeerTutor deletePTutor = getPeerTutorById(peerTutorId);
		if (deletePTutor != null) {
			em.refresh(deletePTutor);
			em.remove(deletePTutor);
		}
	}

	/* CRUD for PeerTutorRegistration */
	public List<PeerTutorRegistration> getAllRegistration() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PeerTutorRegistration> cq = cb.createQuery(PeerTutorRegistration.class);
		cq.select(cq.from(PeerTutorRegistration.class));
		return em.createQuery(cq).getResultList();

	}
	
	@Transactional
	public PeerTutorRegistration persistPeerTutorRegistration(PeerTutorRegistration newPeerTutorRegistration) {
		em.persist(newPeerTutorRegistration);
		return newPeerTutorRegistration;
	}

	public PeerTutorRegistration getPeerTutorRegistrationById(int studentId, int courseId) {
        TypedQuery<PeerTutorRegistration> allQuery = em.createNamedQuery("PeerTutorRegistration.findById", PeerTutorRegistration.class);
        allQuery.setParameter(PARAM1, studentId);
        allQuery.setParameter("param2", courseId);
        return allQuery.getSingleResult();
    }


	@Transactional
	public void deletePeerTutorRegistrationById(int studentId, int courseId) {
		PeerTutorRegistration deletePTutorRegistration = getPeerTutorRegistrationById(studentId, courseId);
		if (deletePTutorRegistration != null) {
			em.refresh(deletePTutorRegistration);
			em.remove(deletePTutorRegistration);
		}
	}

	public StudentClub mergeStudentClub(StudentClub sc) {
		return em.merge(sc);
	}

	
	
	}
    
    
    
    