package ar.lamansys.online;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackages = "ar.lamansys.online")
@EnableJpaRepositories(basePackages = {"ar.lamansys.online"})
@EntityScan(basePackages = {"ar.lamansys.online"})
@PropertySource(value = "classpath:booking.properties", ignoreResourceNotFound = true)
@Getter
@Setter
public class BookingAutoConfiguration {
    @Value("${booking-mail-from}")
    private String from;

    @Value("${booking-password}")
    private String password;

    @Value("${booking-host}")
    private String host;

    @Value("${booking-port}")
    private String port;

    @Value("${booking-starttls.enable}")
    private String enable;

    @Value("${booking-auth}")
    private String auth;

    @Value("${booking-protocol}")
    private String protocol;

    @Value("${booking-apibase}")
    private String apiBase;
}
