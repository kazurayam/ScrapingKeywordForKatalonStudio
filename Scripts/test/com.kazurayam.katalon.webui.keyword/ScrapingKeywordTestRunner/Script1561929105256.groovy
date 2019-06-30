/**
 * Test Cases/test/com.kazurayam.katalon.webui.keyword/ScrapingKeywordTestRunner
 */
import com.kazurayam.katalon.webui.keyword.ScrapingKeywordTest
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

CustomKeywords.'com.kazurayam.junit4ks.JUnitCustomKeywords.runWithJUnitRunner'(ScrapingKeywordTest.class)

WebUI.comment("done")