package ar.lamansys.sgx.shared.notificationsinfrastructure.configuration.status;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.stereotype.Service;

import ar.lamansys.sgx.shared.strings.StringHelper;
import net.pladema.hsi.extensions.configuration.features.FeatureProperty;
import net.pladema.hsi.extensions.configuration.features.FeatureStatusService;

@Service
public class NotificationStatusService extends FeatureStatusService {

	public NotificationStatusService(
			MailProperties properties
	) {
		super(
				"spring.mail",
				listProperties(properties),
				fetchStatusData()
		);
	}

	private static Supplier<Map<String, Object>> fetchStatusData() {
		return Collections::emptyMap;
	}

	private static Supplier<List<FeatureProperty>> listProperties(MailProperties properties) {
		return () -> List.of(
				new FeatureProperty("host", properties.getHost()),
				new FeatureProperty("protocol", properties.getProtocol()),
				new FeatureProperty("port", StringHelper.toString(properties.getPort())),
				new FeatureProperty("username", properties.getUsername()),
				new FeatureProperty("password", properties.getPassword())
		);
	}

}
