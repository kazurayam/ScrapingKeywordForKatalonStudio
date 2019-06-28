/**
 * Test Cases/test/com.kazurayam.katalon.download/DownloadingKeywordsTestRunner
 */
import com.kazurayam.katalon.download.DownloaderKeywordsTest
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

CustomKeywords.'com.kazurayam.junit4ks.JUnitCustomKeywords.runWithJUnitRunner'(DownloaderKeywordsTest.class)

WebUI.comment("done")