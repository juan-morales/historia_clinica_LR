package net.pladema.user.infrastructure.output.notification;

import lombok.Builder;

@Builder
public class RestorePasswordNotificationArgs {

	public final String fullname;
	public final String link;

}
