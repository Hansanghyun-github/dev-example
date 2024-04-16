package com.example.devexample;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class DevExampleApplicationTests {

	@Test
	void contextLoads() {
		int goodAnswer = 2;
		assertThat(sum(1, 1)).isEqualTo(goodAnswer);
	}

	private static int sum(int a, int b) {
		return a + b;
	}

}
