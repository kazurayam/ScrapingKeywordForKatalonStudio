/**
 * Test Cases/test/com.kazurayam.katalon.webui.keyword/ScrapingClientTestRunner
 */
import com.kazurayam.katalon.webui.keyword.ScrapingClientTest
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

CustomKeywords.'com.kazurayam.junit4ks.JUnitCustomKeywords.runWithJUnitRunner'(ScrapingClientTest.class)

WebUI.comment("done")