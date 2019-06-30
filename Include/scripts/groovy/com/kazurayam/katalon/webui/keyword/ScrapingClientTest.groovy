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
import org.apache.commons.io.FileUtils

@RunWith(IgnoreRestSupportRunner.class)
class ScrapingClientTest {

	private static Path testOutputDir_
	private static Path fixtureDir_

	@BeforeClass public static void onlyOnce() {
		Path projectDir = Paths.get(RunConfiguration.getProjectDir())
		testOutputDir_ = projectDir.resolve(Paths.get("build/tmp/testOutput/${ScrapingClientTest.class.getSimpleName()}"))
		if (Files.exists(testOutputDir_)) {
			FileUtils.deleteDirectory(testOutputDir_.toFile())
		}
		Files.createDirectories(testOutputDir_)
		fixtureDir_ = projectDir.resolve(Paths.get('Include/resources/fixture'))
	}

	@Test
	void test_downloadAndSave_csv_MS932() {
		// when:
		String testCaseName = 'test_downloadAndSave_csv_MS932'
		// Japan Meteorological Agancy
		Map data = (Map)GlobalVariable.JMA_RAIN_DATA_URL
		String url = data.get('1h')   // 1hour volume of rain
		String fileName = '1h.csv'
		Path outFile = doDownloadAndSave(testCaseName, url, fileName)
		ScrapingClient.convertCharsetToUtf8(outFile)
		// then:
		assertThat(outFile.toString() + " should exist", Files.exists(outFile), is(equalTo(true)))
		assertTrue(outFile.toString() + " should not be empty", Files.size(outFile) > 0)
	}

	@Test
	void test_downloadAndSave_excel() {
		// when:
		String testCaseName = 'test_downloadAndSave_excel'
		// NISA excel
		Map data = (Map)GlobalVariable.FSA_NISA_DATA_URL
		String url = data.get('23')
		String fileName = "23.xlsx"
		Path outFile = doDownloadAndSave(testCaseName, url, fileName)
		// then:
		assertThat(outFile.toString() + " should exist", Files.exists(outFile), is(equalTo(true)))
		assertTrue(outFile.toString() + " should not be empty", Files.size(outFile) > 0)
	}

	@Test
	void test_downloadAndSave_pdf() {
		// when:
		String testCaseName = 'test_downloadAndSave_pdf'
		// NISA excel
		Map data = (Map)GlobalVariable.FSA_NISA_DATA_URL
		String url = data.get('24')
		String fileName = "24.pdf"
		Path outFile = doDownloadAndSave(testCaseName, url, fileName)
		// then:
		assertThat(outFile.toString() + " should exist", Files.exists(outFile), is(equalTo(true)))
		assertTrue(outFile.toString() + " should not be empty", Files.size(outFile) > 0)
	}

	@Test
	void test_downloadAndSave_image() {
		// when:
		String testCaseName = 'test_downloadAndSave_image'
		// NISA excel
		Map data = (Map)GlobalVariable.KATALON_DEMO_DATA_URL
		String url = data.get('HEADER_JPG')
		String fileName = "header.jpg"
		Path outFile = doDownloadAndSave(testCaseName, url, fileName)
		// then:
		assertThat(outFile.toString() + " should exist", Files.exists(outFile), is(equalTo(true)))
		assertTrue(outFile.toString() + " should not be empty", Files.size(outFile) > 0)
	}

	private Path doDownloadAndSave(String testCaseName, String url, String fileName) {
		Path caseOutputDir = testOutputDir_.resolve(Paths.get(testCaseName))
		Files.createDirectories(caseOutputDir)
		Path outFile = caseOutputDir.resolve(fileName)
		// when:
		String projectDir = RunConfiguration.getProjectDir()
		ProxyInformation proxyInformation = RunConfiguration.getProxyInformation()
		ScrapingClient client = new ScrapingClient(projectDir, proxyInformation)
		RequestObject request = new RequestObject()
		request.setRestRequestMethod('GET')
		request.setRestUrl(url)
		client.downloadAndSave(request, outFile)
		return outFile
	}

	@Test
	void test_convertCharsetToUtf8() {
		// setup:
		Path fixtureFile = fixtureDir_.resolve('1h_MS932.csv')
		Path caseOutputDir = testOutputDir_.resolve(Paths.get('test_convertCharsetToUtf8'))
		Files.createDirectories(caseOutputDir)
		Path dataFile = caseOutputDir.resolve('1h.csv')
		FileUtils.copyFile(fixtureFile.toFile(), dataFile.toFile())
		// when:
		ScrapingClient.convertCharsetToUtf8(dataFile)
		// then:
		String content = dataFile.toFile().getText('utf-8')
		//
		assertThat(content, containsString("観測所番号,都道府県,地点"));
	}
}
