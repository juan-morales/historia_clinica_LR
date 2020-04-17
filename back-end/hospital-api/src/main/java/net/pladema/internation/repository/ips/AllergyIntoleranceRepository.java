package net.pladema.internation.repository.ips;

import net.pladema.internation.repository.ips.entity.AllergyIntolerance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AllergyIntoleranceRepository extends JpaRepository<AllergyIntolerance, Integer> {

}
