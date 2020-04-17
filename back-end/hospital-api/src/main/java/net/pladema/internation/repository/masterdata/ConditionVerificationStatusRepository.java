package net.pladema.internation.repository.masterdata;

import net.pladema.internation.repository.masterdata.entity.ConditionVerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConditionVerificationStatusRepository extends JpaRepository<ConditionVerificationStatus, String> {

}
