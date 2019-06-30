package com.kazurayam.katalon.download

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.network.ProxyInformation
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.exception.WebElementNotFoundException
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords
import com.kms.katalon.core.testobject.RequestObject
import org.apache.commons.io.FileUtils

class DownloaderKeywords {

	@Keyword
	public static Map<String, List<String>> downloadAndSave(String absoluteHref, Path outDir, String fileName,
			FailureHandling flowControl = RunConfiguration.getDefaultFailureHandling()) throws IOException {
		Objects.requireNonNull(absoluteHref, "absoluteHref must not be null")
		Objects.requireNonNull(outDir, "outDir must not be null")
		Objects.requireNonNull(fileName, "fileName must not be null")
		if (!Files.exists(outDir)) {
			throw new IOException("${outDir} does not exist")
		}
		if (fileName.equals('')) {
			throw new IOException("fileName must not be empty")
		}
		String projectDir = RunConfiguration.getProjectDir()
		ProxyInformation  proxyInformation = RunConfiguration.getProxyInformation()
		DownloaderClient client = new DownloaderClient(projectDir, proxyInformation)
		RequestObject ro = new RequestObject()
		ro.setRestRequestMethod('GET')
		ro.setRestUrl(absoluteHref)
		Path outFile = outDir.resolve(fileName)
		// Now let's do the real business
		return client.downloadAndSave(ro, outFile)
	}

	@Keyword
	public static Map<String, List<String>> downloadAndSave(String absoluteHref, Path outDir,
			FailureHandling flowControl = RunConfiguration.getDefaultFailureHandling())
	throws IOException {
		Objects.requireNonNull(absoluteHref, "absoluteHref must not be null")
		Objects.requireNonNull(outDir, "outDir must not be null")
		String fileName = getFileName(absoluteHref)
		if (fileName.equals('')) {
			throw new IllegalArgumentException("absoluteHref=${}")
		}
		Path outFile = outDir.resolve(fileName)
		return downloadAndSave(absoluteHref, outDir, fileName, flowControl)
	}

	@Keyword
	public static Map<String, List<String>> downloadAndSave(String relativeHref, String baseUrl, Path outDir)
	throws IOException {
		String absoluteHref = resolve(relativeHref, baseUrl)
		return downloadAndSave(absoluteHref, outDir)
	}

	@Keyword
	public static Map<String, List<String>> downloadAndSave(String relativeHref, String baseUrl, Path outDir, String fileName)
	throws IOException {
		String absoluteHref = resolve(relativeHref, baseUrl)
		return downloadAndSave(absoluteHref, outDir, fileName)
	}

	static String getFileName(String url) {
		if (url.lastIndexOf('/') > 0) {
			return url.substring(url.lastIndexOf('/') + 1)
		} else {
			return ''
		}
	}

	static String resolve(String href, String base) {
		if (href.startsWith('http:') ||
		href.startsWith('https:') ||
		href.startsWith('file:')    ) {
			// href is already absolute
			return href
		} else {
			String s = base
			if (!s.endsWith('/')) {
				s = s.substring(0, s.length())
			}
			return s + href
		}
	}

	/**
	 * Convert a text file in Charset MS932 to UTF-8 
	 * while converting the End-of-line to the one of the runtime environment.
	 * This method overwrites the target text file.  
	 */
	@Keyword
	void convertCharsetToUtf8(Path textFile) {
		DownloaderClient.convertCharsetToUtf8(textFile)
	}
}