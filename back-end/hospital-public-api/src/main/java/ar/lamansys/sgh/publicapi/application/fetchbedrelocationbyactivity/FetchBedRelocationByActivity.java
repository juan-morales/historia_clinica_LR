package ar.lamansys.sgh.publicapi.application.fetchbedrelocationbyactivity;

import ar.lamansys.sgh.publicapi.application.port.out.ActivityInfoStorage;
import ar.lamansys.sgh.publicapi.domain.BedRelocationInfoBo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FetchBedRelocationByActivity {

    private final Logger logger;
    private final ActivityInfoStorage activityInfoStorage;

    public FetchBedRelocationByActivity(ActivityInfoStorage activityInfoStorage) {
        this.logger = LoggerFactory.getLogger(FetchBedRelocationByActivity.class);
        this.activityInfoStorage = activityInfoStorage;
    }

    public List<BedRelocationInfoBo> run(String refsetCode, String provinceCode, Long activityId) {
        logger.debug("Input parameters -> refsetCode {}, provinceCode {}, activityId {}", refsetCode, provinceCode, activityId);
        List<BedRelocationInfoBo> result = activityInfoStorage.getBedRelocationsByActivity(refsetCode, provinceCode, activityId);
        logger.debug("Output -> {}", result);
        return result;
    }
}
