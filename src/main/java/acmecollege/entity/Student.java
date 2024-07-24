/********************************************************************************************************2*4*w*
 * File:  Student.java Course materials CST 8277
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
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The persistent class for the student database table.
 */
@SuppressWarnings("unused")
//TODO ST01 - Add the missing annotations.
//TODO ST02 - Do we need a mapped super class? If so, which one?
@Entity
@Table(name = "student")
@AttributeOverride(name = "id", column = @Column(name = "id"))
@Access(AccessType.FIELD)
@NamedQueries({
@NamedQuery(name = Student.ALL_STUDENTS_QUERY_NAME, query = "SELECT s FROM Student s LEFT JOIN FETCH s.membershipCards LEFT JOIN FETCH s.peerTutorRegistrations"),
@NamedQuery(name = Student.STUDENT_BY_ID_QUERY, query = "SELECT s FROM Student s LEFT JOIN FETCH s.membershipCards LEFT JOIN FETCH s.peerTutorRegistrations where s.id = :param1")
})

public class Student extends PojoBase implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String ALL_STUDENTS_QUERY_NAME = "Student.findAll";
	public static final String STUDENT_BY_ID_QUERY = "Student.findById";

	
    public Student() {
    	super();
    }

    // TODO ST03 - Add annotation
    @Column(name = "first_name", nullable = false, length = 50)
	private String firstName;

	// TODO ST04 - Add annotation
    @Column(name = "last_name", nullable = false, length = 50)
	private String lastName;

	// TODO ST05 - Add annotations for 1:M relation.  Changes should not cascade.
    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MembershipCard> membershipCards = new HashSet<>();

	// TODO ST06 - Add annotations for 1:M relation.  Changes should not cascade.
    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PeerTutorRegistration> peerTutorRegistrations = new HashSet<>();

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

    // Simplify JSON body, skip MembershipCards
    @JsonIgnore
    public Set<MembershipCard> getMembershipCards() {
		return membershipCards;
	}

	public void setMembershipCards(Set<MembershipCard> membershipCards) {
		this.membershipCards = membershipCards;
	}

    // Simplify JSON body, skip PeerTutorRegistrations
    @JsonIgnore
    public Set<PeerTutorRegistration> getPeerTutorRegistrations() {
		return peerTutorRegistrations;
	}

	public void setPeerTutorRegistrations(Set<PeerTutorRegistration> peerTutorRegistrations) {
		this.peerTutorRegistrations = peerTutorRegistrations;
	}

	public void setFullName(String firstName, String lastName) {
		setFirstName(firstName);
		setLastName(lastName);
	}
	
	//Inherited hashCode/equals is sufficient for this entity class

}
