package com.kazurayam.katalon.webui.keyword

import static org.hamcrest.CoreMatchers.*
import static org.hamcrest.MatcherAssert.*
import static org.junit.Assert.*

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import org.junit.BeforeClass
import org.junit.Test
import org.junit.Ignore
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import com.kazurayam.junit4ks.IgnoreRestSupportRunner
import com.kazurayam.junit4ks.IgnoreRest
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.network.ProxyInformation
import com.kms.katalon.core.testobject.RequestObject

import internal.GlobalVariable

@RunWith(IgnoreRestSupportRunner.class)
class ScrapingKeywordTest {

	@BeforeClass public static void onlyOnce() {
		Path projectDir = Paths.get(RunConfiguration.getProjectDir())
	}

	@Test
	void test_resolve() {
		// when:
		String base = 'https://s2.q4cdn.com/'
		String href = '470004039/files/doc_financials/2019/q2/10-Q-Q2-2019-(As-Filed).pdf'
		String expected = 'https://s2.q4cdn.com/470004039/files/doc_financials/2019/q2/10-Q-Q2-2019-(As-Filed).pdf'
		String actual = ScrapingKeyword.resolve(href, base)
		// then:
		assertEquals(expected, actual)
	}

	@Test
	void test_getFileName() {
		// when:
		String url = 'https://s2.q4cdn.com/470004039/files/doc_financials/2019/q2/10-Q-Q2-2019-(As-Filed).pdf'
		String expected = '10-Q-Q2-2019-(As-Filed).pdf'
		String actual = ScrapingKeyword.getFileName(url)
		// then:
		assertEquals(expected, actual)
	}
}
