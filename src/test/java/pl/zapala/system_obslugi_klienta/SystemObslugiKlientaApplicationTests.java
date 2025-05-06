package pl.zapala.system_obslugi_klienta;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
		"spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.mail.username=dummy",
		"spring.mail.password=dummy",
		"spring.mail.host=localhost",
		"emails.sender_email=dummy@example.com"})
class SystemObslugiKlientaApplicationTests {

	@Test
	void contextLoads() {
	}

}
