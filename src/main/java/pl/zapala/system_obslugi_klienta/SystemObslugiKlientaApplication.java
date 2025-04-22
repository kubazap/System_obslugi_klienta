package pl.zapala.system_obslugi_klienta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SystemObslugiKlientaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SystemObslugiKlientaApplication.class, args);
	}

}
