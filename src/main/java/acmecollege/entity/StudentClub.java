/********************************************************************************************************2*4*w*
 * File:  StudentClub.java Course materials CST 8277
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

import static acmecollege.entity.StudentClub.ALL_STUDENT_CLUBS_QUERY_NAME;
import static acmecollege.entity.StudentClub.IS_DUPLICATE_QUERY_NAME;
import static acmecollege.entity.StudentClub.SPECIFIC_STUDENT_CLUB_QUERY_NAME;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.NamedQueries;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
/**
 * The persistent class for the student_club database table.
 */
//TODO SC01 - Add the missing annotations.
//TODO SC02 - StudentClub has subclasses AcademicStudentClub and NonAcademicStudentClub.  Look at week 9 slides for InheritanceType.
//TODO SC03 - Do we need a mapped super class?  If so, which one?
//TODO SC06 - Add in JSON annotations to indicate different sub-classes of StudentClub
@Entity
@Table(name = "student_club")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@AttributeOverride(name = "id", column = @Column(name = "club_id"))
@Access(AccessType.FIELD)
@DiscriminatorColumn(columnDefinition = "bit(1)", name = "academic", discriminatorType = DiscriminatorType.INTEGER)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(
use = JsonTypeInfo.Id.NAME,
include = JsonTypeInfo.As.PROPERTY,
property = "entity-type")
@JsonSubTypes({
@Type(value = AcademicStudentClub.class, name = "academic_student_club"),
@Type(value = NonAcademicStudentClub.class, name = "non_academic_student_club")
})
@NamedQueries({
@NamedQuery(name = ALL_STUDENT_CLUBS_QUERY_NAME, query = "SELECT distinct sc FROM StudentClub sc left JOIN FETCH sc.clubMemberships"),
@NamedQuery(name = SPECIFIC_STUDENT_CLUB_QUERY_NAME, query = "SELECT distinct sc FROM StudentClub sc left JOIN FETCH sc.clubMemberships where sc.id = :param1"),
@NamedQuery(name = IS_DUPLICATE_QUERY_NAME, query = "SELECT COUNT(sc) FROM StudentClub sc WHERE sc.name = :param1")
})
@JsonIgnoreProperties({"entity-type", "hibernateLazyInitializer"})

public abstract class StudentClub extends PojoBase implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String ALL_STUDENT_CLUBS_QUERY_NAME = "StudentClub.findAll";
	public static final String SPECIFIC_STUDENT_CLUB_QUERY_NAME = "StudentClub.findById";
	public static final String IS_DUPLICATE_QUERY_NAME = "StudentClub.isDuplicate";

	
	// TODO SC04 - Add the missing annotations.
	@Column(name = "name", length = 100, nullable = false)
	private String name;

	// TODO SC05 - Add the 1:M annotation.  This list should be effected by changes to this object (cascade).
	@OneToMany(mappedBy = "club", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private Set<ClubMembership> clubMemberships = new HashSet<>();

    @Transient
    private boolean isAcademic;

	public StudentClub() {
		super();
	}

    public StudentClub(boolean isAcademic) {
        this();
        this.isAcademic = isAcademic;
    }

    // Simplify Json body, skip ClubMemberships
    @JsonIgnore
	public Set<ClubMembership> getClubMemberships() {
		return clubMemberships;
	}

	public void setClubMembership(Set<ClubMembership> clubMemberships) {
		this.clubMemberships = clubMemberships;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	//Inherited hashCode/equals is NOT sufficient for this entity class
	
	/**
	 * Very important:  Use getter's for member variables because JPA sometimes needs to intercept those calls<br/>
	 * and go to the database to retrieve the value
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		// Only include member variables that really contribute to an object's identity
		// i.e. if variables like version/updated/name/etc. change throughout an object's lifecycle,
		// they shouldn't be part of the hashCode calculation
		
		// The database schema for the STUDENT_CLUB table has a UNIQUE constraint for the NAME column,
		// so we should include that in the hash/equals calculations
		
		return prime * result + Objects.hash(getId(), getName());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		
		if (obj instanceof StudentClub otherStudentClub) {
			// See comment (above) in hashCode():  Compare using only member variables that are
			// truly part of an object's identity
			return Objects.equals(this.getId(), otherStudentClub.getId()) &&
				Objects.equals(this.getName(), otherStudentClub.getName());
		}
		return false;
	}
}
