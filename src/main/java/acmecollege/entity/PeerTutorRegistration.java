/********************************************************************************************************2*4*w*
 * File:  PeerTutorRegistration.java Course materials CST 8277
 * 
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
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
package acmecollege.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@SuppressWarnings("unused")
/**
 * The persistent class for the peer_tutor_registration database table.
 */
@Entity
@Table(name = "peer_tutor_registration")
@Access(AccessType.FIELD)
@NamedQuery(name = "PeerTutorRegistration.findAll", query = "SELECT ptr FROM PeerTutorRegistration ptr")
public class PeerTutorRegistration extends PojoBaseCompositeKey<PeerTutorRegistrationPK> implements Serializable {
	private static final long serialVersionUID = 1L;

	// Hint - What annotation is used for a composite primary key type?
	@EmbeddedId
	private PeerTutorRegistrationPK id;

	// @MapsId is used to map a part of composite key to an entity.
	@MapsId("studentId")
	@ManyToOne(cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "student_id", referencedColumnName = "id", nullable = false)
	private Student student;

	//TODO PTR01 - Add missing annotations.  Similar to student, this field is a part of the composite key of this entity.  Changes to this class should cascade.  Reference to a course is not optional.
	@MapsId("courseId")
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "course_id", referencedColumnName = "course_id", nullable = false)
    private Course course;

	//TODO PTR02 - Add missing annotations.  Changes to this class should cascade.
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "peer_tutor_id", referencedColumnName = "peer_tutor_id")
	private PeerTutor peerTutor;

	@Column(name = "numeric_grade")
	private int numericGrade;

	@Column(length = 3, name = "letter_grade")
	private String letterGrade;


	public PeerTutorRegistration() {
		id = new PeerTutorRegistrationPK();
	}

	@Override
	public PeerTutorRegistrationPK getId() {
		return id;
	}

	@Override
	public void setId(PeerTutorRegistrationPK id) {
		this.id = id;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		id.setStudentId(student.id);
		this.student = student;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		id.setCourseId(course.id);
		this.course = course;
	}

	public PeerTutor getPeerTutor() {
		return peerTutor;
	}

	public void setPeerTutor(PeerTutor peerTutor) {
		this.peerTutor = peerTutor;
	}

	public int getNumericGrade() {
		return numericGrade;
	}
	
	public void setNumericGrade(int numericGrade) {
		this.numericGrade = numericGrade;
	}

	public String getLetterGrade() {
		return letterGrade;
	}

	public void setLetterGrade(String letterGrade) {
		this.letterGrade = letterGrade;
	}

	//Inherited hashCode/equals is sufficient for this entity class

}