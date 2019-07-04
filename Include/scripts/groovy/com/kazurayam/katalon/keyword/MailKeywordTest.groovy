package com.kazurayam.katalon.keyword

import static org.hamcrest.CoreMatchers.*
import static org.hamcrest.MatcherAssert.*
import static org.junit.Assert.*

import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

import com.kazurayam.junit4ks.IgnoreRestSupportRunner

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * TODO
 * ユニットテスト用のメールサーバsubethasmtpがあることを知りました
 * https://qiita.com/tchofu/items/79097b68cc9e22a1f401
 * 
 * 
 * @author qcq0264
 *
 */
@RunWith(IgnoreRestSupportRunner.class)
public class MailKeywordTest {

    static Path katalonPropsPath
    static Properties katalonProps
    
    @BeforeClass public static void onlyOnce() {
        katalonPropsPath = Paths.get('./katalon.properties')
        if (!Files.exists(katalonPropsPath)) {
            throw new IOException("${katalonPropsPath} is not found")
        }
        katalonProps = new Properties()
        katalonProps.load(new FileInputStream(katalonPropsPath.toFile()))
    }

    @Test
    void test_sendMail() {
        // setup:
        String host = katalonProps.getProperty('MailKeyword.host',
                                    "host is not defined in ${katalonPropsPath}")
        String port = katalonProps.getProperty('MailKeyword.port',
                                    "port is not defined in ${katalonPropsPath}")
        String senderAddress = katalonProps.getProperty('MailKeyword.senderAddress',
                                    "MailKeyword.senderAddress is not defined in ${katalonPropsPath}")
        String senderPassword = katalonProps.getProperty('MailKeyword.senderPassword',
                                    "MailKeyword.senderPassword is not defined in ${katalonPropsPath}")
        MailKeyword keyword = new MailKeyword.Builder(host, port)
                                    .senderAddress('kazuaki.matsuhashi@quick.jp')
                                    .senderPassword('Wakako1Akihioro2Hanae4')
                                    .build()
        // when:
        String receiverAddress = "kazuaki.matsuhashi@quick.jp"
        String subject = "Test test"
        String message = """<h1>Hello, world</h1>"""
        boolean success = keyword.sendMail(receiverAddress, subject, message);
        // then:
        assertThat(success, is(true));
    }
}
