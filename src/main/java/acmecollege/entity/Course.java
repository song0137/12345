/********************************************************************************************************2*4*w*
 * File:  Course.java Course materials CST 8277
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

import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@SuppressWarnings("unused")

/**
 * The persistent class for the course database table.
 */
//TODO CO01 - Add the missing annotations.
//TODO CO02 - Do we need a mapped super class?  If so, which one?
@Entity(name = "Course")
@Table(name = "course")
@AttributeOverride(name = "id", column = @Column(name = "course_id"))
public class Course extends PojoBase implements Serializable {
	private static final long serialVersionUID = 1L;

	
	// TODO CO03 - Add missing annotations.
	@Column(name = "course_code", length = 7, nullable = false)
    private String courseCode;

	// TODO CO04 - Add missing annotations.
	@Column(name = "course_title", length = 100, nullable = false)
    private String courseTitle;

	// TODO CO05 - Add missing annotations.
	@Column(name = "year", nullable = false)
	private int year;

	// TODO CO06 - Add missing annotations.
	@Column(name = "semester", length = 6, nullable = false)
    private String semester;

	// TODO CO07 - Add missing annotations.
	@Column(name = "credit_units", nullable = false)
    private int creditUnits;

	// TODO CO08 - Add missing annotations.
	@Column (name = "online", nullable = false)
	private byte online;

	// TODO CO09 - Add annotations for 1:M relation.  Changes to this class should not cascade.
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private Set<PeerTutorRegistration> peerTutorRegistrations = new HashSet<>();

	public Course() {
		super();
	}

	public Course(String courseCode, String courseTitle, int year, String semester, int creditUnits, byte online) {
		this();
		this.courseCode = courseCode;
		this.courseTitle = courseTitle;
		this.year = year;
		this.semester = semester;
		this.creditUnits = creditUnits;
		this.online = online;
	}

	public Course setCourse(String courseCode, String courseTitle, int year, String semester, int creditUnits, byte online) {
		setCourseCode(courseCode);
		setCourseTitle(courseTitle);
		setYear(year);
		setSemester(semester);
		setCreditUnits(creditUnits);
		setOnline(online);
		return this;
	}

	public String getCourseCode() {
		return courseCode;
	}

	public void setCourseCode(String courseCode) {
		this.courseCode = courseCode;
	}

	public String getCourseTitle() {
		return courseTitle;
	}

	public void setCourseTitle(String courseTitle) {
		this.courseTitle = courseTitle;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getSemester() {
		return semester;
	}

	public void setSemester(String semester) {
		this.semester = semester;
	}

	public int getCreditUnits() {
		return creditUnits;
	}

	public void setCreditUnits(int creditUnits) {
		this.creditUnits = creditUnits;
	}

	public byte getOnline() {
		return online;
	}

	public void setOnline(byte online) {
		this.online = online;
	}
	
	public Set<PeerTutorRegistration> getPeerTutorRegistrations() {
		return peerTutorRegistrations;
	}

	public void setPeerTutorRegistrations(Set<PeerTutorRegistration> peerTutorRegistrations) {
		this.peerTutorRegistrations = peerTutorRegistrations;
	}

	//Inherited hashCode/equals is sufficient for this Entity class

}
