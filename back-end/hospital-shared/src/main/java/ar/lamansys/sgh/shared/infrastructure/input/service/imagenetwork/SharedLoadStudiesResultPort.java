package ar.lamansys.sgh.shared.infrastructure.input.service.imagenetwork;

import java.util.Optional;

public interface SharedLoadStudiesResultPort {
	void updateStatusAndResult(Integer idMove, String status, String result);


	void updateSize(Integer idMove, Integer size, String imageId);

	Optional<Integer> findInstitutionId(Integer idMove);
}
