package com.kazurayam.katalon.download
import java.nio.file.Files
import java.nio.file.Path

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

class DownloaderKeywords {

	@Keyword
	public static void downloadAndSave(String absoluteHref, Path outDir, String fileName,
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
		client.downloadAndSave(ro, outFile)
		
	}

	@Keyword
	public static void downloadAndSave(String absoluteHref, Path outDir,
			FailureHandling flowControl = RunConfiguration.getDefaultFailureHandling())
	throws IOException {
		Objects.requireNonNull(absoluteHref, "absoluteHref must not be null")
		Objects.requireNonNull(outDir, "outDir must not be null")
		String fileName = getFileName(absoluteHref)
		if (fileName.equals('')) {
			throw new IllegalArgumentException("absoluteHref=${}")
		}
		Path outFile = outDir.resolve(fileName)
		downloadAndSave(absoluteHref, outDir, fileName, flowControl)
	}

	@Keyword
	public static void downloadAndSave(String relativeHref, String baseUrl, Path outDir)
	throws IOException {
		String absoluteHref = resolve(relativeHref, baseUrl)
		downloadAndSave(absoluteHref, outDir)
	}

	@Keyword
	public static void downloadAndSave(String relativeHref, String baseUrl, Path outDir, String fileName)
	throws IOException {
		String absoluteHref = resolve(relativeHref, baseUrl)
		downloadAndSave(absoluteHref, outDir, fileName)
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
				s = s.substring(0, s.lenth())
			}
			return s + href
		}
	}

	/**
	 * Refresh browser
	 */
	@Keyword
	def refreshBrowser() {
		KeywordUtil.logInfo("Refreshing")
		WebDriver webDriver = DriverFactory.getWebDriver()
		webDriver.navigate().refresh()
		KeywordUtil.markPassed("Refresh successfully")
	}

	/**
	 * Click element
	 * @param to Katalon test object
	 */
	@Keyword
	def clickElement(TestObject to) {
		try {
			WebElement element = WebUiBuiltInKeywords.findWebElement(to);
			KeywordUtil.logInfo("Clicking element")
			element.click()
			KeywordUtil.markPassed("Element has been clicked")
		} catch (WebElementNotFoundException e) {
			KeywordUtil.markFailed("Element not found")
		} catch (Exception e) {
			KeywordUtil.markFailed("Fail to click on element")
		}
	}

	/**
	 * Get all rows of HTML table
	 * @param table Katalon test object represent for HTML table
	 * @param outerTagName outer tag name of TR tag, usually is TBODY
	 * @return All rows inside HTML table
	 */
	@Keyword
	def List<WebElement> getHtmlTableRows(TestObject table, String outerTagName) {
		WebElement mailList = WebUiBuiltInKeywords.findWebElement(table)
		List<WebElement> selectedRows = mailList.findElements(By.xpath("./" + outerTagName + "/tr"))
		return selectedRows
	}
}