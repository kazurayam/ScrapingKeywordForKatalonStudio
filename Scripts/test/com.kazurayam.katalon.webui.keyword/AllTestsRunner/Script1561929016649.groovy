import com.kazurayam.katalon.webui.keyword.PipedStreamExampleTest
import com.kazurayam.katalon.webui.keyword.ScrapingClientTest
import com.kazurayam.katalon.webui.keyword.ScrapingKeywordTest
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

CustomKeywords.'com.kazurayam.junit4ks.JUnitCustomKeywords.runWithJUnitRunner'(PipedStreamExampleTest.class)
CustomKeywords.'com.kazurayam.junit4ks.JUnitCustomKeywords.runWithJUnitRunner'(ScrapingClientTest.class)
CustomKeywords.'com.kazurayam.junit4ks.JUnitCustomKeywords.runWithJUnitRunner'(ScrapingKeywordTest.class)

WebUI.comment("done")