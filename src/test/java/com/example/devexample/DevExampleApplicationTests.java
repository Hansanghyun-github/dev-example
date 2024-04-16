package com.example.devexample;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class DevExampleApplicationTests {

	@Test
	void contextLoads() {
		int badAnswer = 3;
		assertThat(sum(1, 1)).isEqualTo(badAnswer);
	}

	private static int sum(int a, int b) {
		return a + b;
	}

}
