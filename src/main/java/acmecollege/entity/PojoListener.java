/********************************************************************************************************2*4*w*
 * File:  PojoListener.java Course materials CST 8277
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
package acmecollege.entity;

import java.time.LocalDateTime;
import static java.time.LocalDateTime.now;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

@SuppressWarnings("unused")

public class PojoListener {

	// TODO PL01 - What annotation is used when we want to do something just before object is INSERT'd in the database?
	@PrePersist
	public void setCreatedOnDate(PojoBase pojoBase) {
		LocalDateTime now = LocalDateTime.now();
		// TODO PL02 - What member field(s) do we wish to alter just before object is INSERT'd in the database?
		pojoBase.setCreated(now());
		pojoBase.setUpdated(now());
	}

	// TODO PL03 - What annotation is used when we want to do something just before object is UPDATE'd in the database?
	@PreUpdate
	public void setUpdatedDate(PojoBase pojoBase) {
		// TODO PL04 - What member field(s) do we wish to alter just before object is UPDATE'd in the database?
		pojoBase.setUpdated(now());
	}

}
