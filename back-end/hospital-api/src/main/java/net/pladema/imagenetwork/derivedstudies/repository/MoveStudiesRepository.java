package net.pladema.imagenetwork.derivedstudies.repository;

import net.pladema.imagenetwork.derivedstudies.repository.entity.MoveStudies;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MoveStudiesRepository extends JpaRepository<MoveStudies, Integer> {

	@Transactional(readOnly = true)
	@Query("SELECT mo " +
			"FROM MoveStudies AS mo " +
			"WHERE mo.status != :status " +
			"ORDER BY mo.orchestratorId ")
	List<MoveStudies> getListMoveStudies(@Param("status") String status);

	@Transactional
	@Modifying
	@Query("UPDATE MoveStudies AS mo " +
			"SET mo.status = :status " +
			"WHERE mo.id = :idMove")
	void updateStatus(@Param("idMove") Integer idMove,
						 @Param("status") String status);

	@Transactional
	@Modifying
	@Query("UPDATE MoveStudies AS mo " +
			"SET mo.sizeImage = :size " +
			"WHERE mo.id = :idMove")
	void updateSize(@Param("idMove") Integer idMove,
					  @Param("size") Integer size);

	@Transactional
	@Modifying
	@Query("UPDATE MoveStudies AS mo " +
			"SET mo.status = :status, mo.result= :result " +
			"WHERE mo.id = :idMove")
	void updateStatusandResult(@Param("idMove") Integer idMove,
							   @Param("status") String status,
							   @Param("result") String result);

	@Transactional
	@Modifying
	@Query("UPDATE MoveStudies AS mo " +
			"SET mo.attempsNumber = :attempsNumbers " +
			"WHERE mo.id = :idMove")
	void updateAttemps(@Param("idMove") Integer idMove,
					@Param("attempsNumbers") Integer attempsNumbers);


}
