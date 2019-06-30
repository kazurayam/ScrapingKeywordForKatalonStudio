package com.kazurayam.katalon.webui.keyword

import static org.hamcrest.CoreMatchers.*
import static org.hamcrest.MatcherAssert.*
import static org.junit.Assert.*

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4.class)
public class PipedStreamExampleTest {

	@Test
	void test_execute() {
		// when:
		PipedStreamExample instance = new PipedStreamExample()
		instance.execute()
		// then:
	}
}
