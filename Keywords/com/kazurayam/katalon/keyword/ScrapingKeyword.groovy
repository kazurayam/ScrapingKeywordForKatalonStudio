package com.kazurayam.katalon.keyword

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

class ScrapingKeyword {

    @Keyword
    public static Map<String, List<String>> downloadAndSave(String absoluteHref,
            Path outDir, String fileName,
            FailureHandling flowControl = RunConfiguration.getDefaultFailureHandling())
    throws IOException {
        Objects.requireNonNull(absoluteHref, "absoluteHref must not be null")
        Objects.requireNonNull(outDir, "outDir must not be null")
        Objects.requireNonNull(fileName, "fileName must not be null")
        if (!Files.exists(outDir)) {
            Files.createDirectories(outDir)
        }
        if (fileName.equals('')) {
            throw new IOException("fileName must not be empty")
        }
        String projectDir = RunConfiguration.getProjectDir()
        ProxyInformation  proxyInformation = RunConfiguration.getProxyInformation()
        ScrapingClient client = new ScrapingClient(projectDir, proxyInformation)
        RequestObject ro = new RequestObject()
        ro.setRestRequestMethod('GET')
        ro.setRestUrl(absoluteHref)
        Path outFile = outDir.resolve(fileName)
        // Now let's do the real business
        return client.downloadAndSave(ro, outFile)
    }

    @Keyword
    public static Map<String, List<String>> downloadAndSave(String absoluteHref,
            Path outFile,
            FailureHandling flowControl = RunConfiguration.getDefaultFailureHandling())
    throws IOException {
        Objects.requireNonNull(absoluteHref, "absoluteHref must not be null")
        Objects.requireNonNull(outFile, "outFile must not be null")
        Path outDir = outFile.getParent()
        if (!Files.exists(outDir)) {
            Files.createDirectories(outDir)
        }
        String fileName = getFileName(absoluteHref)
        if (fileName.equals('')) {
            throw new IllegalArgumentException("absoluteHref=${absoluteHref} does not have file name")
        }
        return downloadAndSave(absoluteHref, outDir, fileName, flowControl)
    }

    @Keyword
    public static Map<String, List<String>> downloadAndSave(
            String relativeHref, String baseUrl, Path outDir, String fileName)
    throws IOException {
        String absoluteHref = resolve(relativeHref, baseUrl)
        return downloadAndSave(absoluteHref, outDir, fileName)
    }

    @Keyword
    public static Map<String, List<String>> downloadAndSave(
            String relativeHref, String baseUrl, Path outFile)
    throws IOException {
        String absoluteHref = resolve(relativeHref, baseUrl)
        return downloadAndSave(absoluteHref, outFile)
    }

    /**
     * Scan a string of a URL to look up the file name.
     * url is primarilly supposed to be an absolute URL with 'http' or 'https' scheme.
     * url is also can be a relative URL.
     * If url is 'https://foo.bar/baz/pee.pdf' then returns 'pee.pdf', which is the last path component
     * If url is 'https://foo.bar/baz/' then returns ''.
     * If url is 'baz/pee.pdf' then returns 'pee.pdf'
     * If url is 'pee.pdf' then returns 'pee.pdf'
     * If url is 'https://foo.bar/baz/pee.pdf?' then returns 'pee.pdf', chomping off '?' and characters after that 
     * 
     * @param url
     * @return
     */
    @Keyword
    static String getFileName(String url) {
        Objects.requireNonNull(url, "url must not be null")

        int x = url.lastIndexOf('/')
        if (x > 0) {
            return url.substring(x + 1)
        } else {
            return ''
        }
    }

    @Keyword
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
    void convertCharsetToUtf8(Path textFile, String charset = 'MS932') {
        ScrapingClient.convertCharsetToUtf8(textFile, charset)
    }
}