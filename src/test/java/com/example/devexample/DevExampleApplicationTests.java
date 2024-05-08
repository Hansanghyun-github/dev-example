package com.example.devexample;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class DevExampleApplicationTests {

	@Test
	@DisplayName("profile test")
	void profile_test() throws Exception {
	    // given

	    // when

	    // then
	}
}
