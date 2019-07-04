import com.kazurayam.katalon.keyword.MailSenderTest
import com.kazurayam.katalon.keyword.ScrapingClientTest
import com.kazurayam.katalon.keyword.ScrapingKeywordTest
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

CustomKeywords.'com.kazurayam.junit4ks.JUnitCustomKeywords.runWithJUnitRunner'(MailSenderTest.class)
CustomKeywords.'com.kazurayam.junit4ks.JUnitCustomKeywords.runWithJUnitRunner'(ScrapingClientTest.class)
CustomKeywords.'com.kazurayam.junit4ks.JUnitCustomKeywords.runWithJUnitRunner'(ScrapingKeywordTest.class)

WebUI.comment("done")