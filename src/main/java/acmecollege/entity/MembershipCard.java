/********************************************************************************************************2*4*w*
 * File:  MembershipCard.java Course materials CST 8277
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
import static acmecollege.entity.MembershipCard.ID_CARD_QUERY_NAME;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@SuppressWarnings("unused")

/**
 * The persistent class for the membership_card database table.
 */
//TODO MC01 - Add the missing annotations.
//TODO MC02 - Do we need a mapped super class?  If so, which one?
@Entity
@Table(name = "membership_card")
@AttributeOverride(name = "id", column = @Column(name = "card_id"))
@Access(AccessType.FIELD)
@NamedQuery(name = ID_CARD_QUERY_NAME, query = "SELECT mc FROM MembershipCard mc WHERE mc.id = :param1")

public class MembershipCard extends PojoBase implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String ID_CARD_QUERY_NAME = "MembershipCard.findById";
	
	// TODO MC03 - Add annotations for 1:1 mapping.  Changes here should cascade.
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "membership_id")
	private ClubMembership clubMembership;

	// TODO MC04 - Add annotations for M:1 mapping.  Changes here should not cascade.
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "student_id", referencedColumnName = "id", nullable = false)
	private Student owner;

	// TODO MC05 - Add annotations.
	@Column(name = "signed", nullable = false)
	private byte signed;

	public MembershipCard() {
		super();
	}
	
	public MembershipCard(ClubMembership clubMembership, Student owner, byte signed) {
		this();
		this.clubMembership = clubMembership;
		this.owner = owner;
		this.signed = signed;
	}

	public ClubMembership getClubMembership() {
		return clubMembership;
	}

	public void setClubMembership(ClubMembership clubMembership) {
		this.clubMembership = clubMembership;
	}

	public Student getOwner() {
		return owner;
	}

	public void setOwner(Student owner) {
		this.owner = owner;
	}

	public byte getSigned() {
		return signed;
	}

	public void setSigned(boolean signed) {
		this.signed = (byte) (signed ? 0b0001 : 0b0000);
	}
	
	//Inherited hashCode/equals is sufficient for this entity class

}