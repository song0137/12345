/********************************************************************************************************2*4*w*
 * File:  NonAcademicStudentClub.java Course materials CST 8277
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

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

//TODO NASC01 - Add missing annotations, please see Week 9 slides page 15.  Value 1 is academic and value 0 is non-academic.
@Entity
@DiscriminatorValue(value = "0")
public class NonAcademicStudentClub extends StudentClub implements Serializable {
	private static final long serialVersionUID = 1L;

	public NonAcademicStudentClub() {
		super(false);

	}
}