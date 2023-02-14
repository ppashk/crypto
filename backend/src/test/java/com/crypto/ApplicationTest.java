package com.crypto;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {
        "spring.flyway.enabled=false", "spring.jpa.hibernate.ddl-auto=none"
})
@AutoConfigureTestDatabase
public class ApplicationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Disabled
    @Test
    public void should_ping_health_check_endpoint() {
        ResponseEntity<Void> response = restTemplate.getForEntity("/", Void.class);
        assertThat(response.getStatusCode(), equalTo(OK));
    }
}
