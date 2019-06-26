/**
 * Test Cases/test/com.kazurayam.katalon.download/DownloadingClientTestRunner
 */
import com.kazurayam.katalon.download.DownloadingClientTest
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

CustomKeywords.'com.kazurayam.junit4ks.JUnitCustomKeywords.runWithJUnitRunner'(DownloadingClientTest.class)

WebUI.comment("done")