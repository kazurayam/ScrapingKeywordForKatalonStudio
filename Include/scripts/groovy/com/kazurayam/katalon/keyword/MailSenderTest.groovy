package com.kazurayam.katalon.keyword

import static org.hamcrest.CoreMatchers.*
import static org.hamcrest.MatcherAssert.*
import static org.junit.Assert.*

import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

import com.kazurayam.junit4ks.IgnoreRestSupportRunner

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import javax.mail.MessagingException
import org.subethamail.wiser.Wiser
import org.subethamail.wiser.WiserMessage
import javax.mail.internet.MimeMessage

/**
 * TODO
 * ユニットテスト用のメールサーバsubethasmtpがあることを知りました
 * https://qiita.com/tchofu/items/79097b68cc9e22a1f401
 * 
 * Here we use Subethamail at https://github.com/voodoodyne/subethasmtp to mock a STMP server
 * 
 * @author kazuarayam
 *
 */
@RunWith(IgnoreRestSupportRunner.class)
public class MailSenderTest {

    private static Wiser wiser
    private static String smtpHost
    private static String smtpPort
    private static Path katalonPropsPath
    private static Properties katalonProps

    @BeforeClass public static void onlyOnce() throws Exception {
        // start up a Mail server
        smtpHost = 'localhost'
        smtpPort = '2500'
        wiser = new Wiser()
        wiser.setHostname(smtpHost)
        wiser.setPort(Integer.parseInt(smtpPort))
        wiser.start()
    }
    
    @AfterClass public static void tearDownAfterClass() throws Exception {
        // stop the mail server
        wiser.stop()
    }

    @Test
    void test_sendMail() {
        // setup:
        String senderAddress  = 'mick.jagger@rollingstones.com'
        String senderPassword = 'ThisIsAFakePassword'
        MailSender sender = new MailSender.Builder(smtpHost, smtpPort)
                .senderAddress(senderAddress)
                .senderPassword(senderPassword)
                .build()
        // when:
        String receiverAddress = "keith.richards@rollingstones.com"
        String subject = "Test test"
        String message = """<h1>Hello, world</h1>"""
        boolean success = sender.sendMail(receiverAddress, subject, message);
        // then:
        assertThat(success, is(true));
        
        // verify the content of mail sent
        List<WiserMessage> messages = wiser.getMessages()
        for (WiserMessage msg: messages) {
            assertThat(msg.getEnvelopeSender(), is(senderAddress))
            assertThat(msg.getEnvelopeReceiver(), is(receiverAddress))
            try {
                assertThat(msg.getMimeMessage().getSubject(), is(subject))
                MimeMessage mimeMsg = msg.getMimeMessage()
                assertThat(mimeMsg.getContentType(), is('text/html; charset=iso-2022-jp'))
                if (mimeMsg.getContent() instanceof String) {
                    String content = (String)mimeMsg.getContent()
                    assertThat(content.trim(), is(message.trim()))
                }
            } catch(MessagingException e) {
                fail('Couldnt fine a Subject')
            }
        }
        
    }
}
