import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

/**
 * A sample Test Case which downloads a EXCEL file from a Web page
 * and save it into file into the directory you want.
 * 
 * This script uses a custom keyword class 
 * <PRE>com.kazurayam.com.kazurayam.katalon.keyword.ScrapingKeyword#downloadAndSave(String href, Path dir, String fileName)</PRE>
 * 
 * This script targes to a extremely simple Web page.
 * In the page you can find an anchor tag
 * which contains the URL of the excel file as @href attribute.
 * <PRE>&lt;a href="url of excel file"&gt;EXCEL&lt;/a&gt;</PRE>
 * All you need to do is to scrape the anchor tag to get @href value, and pass it to
 * ScrapingKeyword#downloadAndSave() call.
 * 
 * However it is likely that target Web pages of your case are not as simple as this.
 * How to identify the URL of Excel file?
 * It is highly dependent on how the Web page are designed.
 * It is up to you. 
 */


// a utility function which creates a TestObject with XPath selector
TestObject createTestObjectWithXPath(String xpath) {
	TestObject to = new TestObject()
	to.addProperty('xpath', ConditionType.EQUALS, xpath)
	return to	
}

String targetPageURL = "https://www.fsa.go.jp/policy/nisa2/about/tsumitate/target/index.html"

TestObject anchorToEXCEL1 = createTestObjectWithXPath("//dt[contains(.,'つみたてNISA対象商品届出一覧（運用会社別）')]/following-sibling::dd[1]/a")

// open the page
WebUI.openBrowser('')
WebUI.navigateToUrl(targetPageURL)
WebUI.verifyElementPresent(anchorToEXCEL1, 10)

// select the <a href="...">EXCEL<a> tag to get the Href attribute from which we wan to download the Excel file
String hrefExcel = WebUI.getAttribute(anchorToEXCEL1, 'href')

WebUI.comment("hrefExcel is ${hrefExcel}")

// location where to save the file
Path projectDir = Paths.get(RunConfiguration.getProjectDir())
Path targetDir = projectDir.resolve('Data Files').resolve('demo').resolve('fsa.go.jp_nisa2_tsumitate')
Files.createDirectories(targetDir)
String excelFileName = 'data.xlsx'

// download the file at the URL into a file at the prefered location
CustomKeywords.'com.kazurayam.katalon.keyword.ScrapingKeyword.downloadAndSave'(hrefExcel, targetDir, excelFileName)

WebUI.comment("downloaded EXCEL file into \"${targetDir.resolve(excelFileName)}\"")

WebUI.closeBrowser()
