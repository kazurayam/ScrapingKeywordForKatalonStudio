import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import javax.mail.MessagingException
import org.subethamail.wiser.Wiser
import org.subethamail.wiser.WiserMessage
import javax.mail.internet.MimeMessage

import com.kazurayam.katalon.keyword.MailSender
import com.kazurayam.ksbackyard.Assert as MyAssert

// we start Sub Etha SMTP server to receive SMTP mail
// see https://github.com/voodoodyne/subethasmtpa
String smtpHost = 'localhost'
String smtpPort = '2500'
Wiser wiser = new Wiser()
wiser.setHostname(smtpHost)
wiser.setPort(Integer.parseInt(smtpPort))
wiser.start()

// Mick Jagger sends a mail to Keith Richards
String senderAddress = 'mick.jagger@rollingstones.com'
String senderPassword = 'ThisIsAFakePassword'
MailSender sender = new MailSender.Builder(smtpHost, smtpPort)
	.senderAddress(senderAddress)
	.senderPassword(senderPassword)
	.build()
String receiverAddress = 'keith.richards@rollingstones.com'
String subject = "Test test"
String message = """<h1>Hello, world</h1>"""

// Now we send a Email
boolean success = sender.sendMail(receiverAddress, subject, message)

assert success == true

// cheat the message and verify the content
List<WiserMessage> messages = wiser.getMessages()
for (WiserMessage msg in messages) {
	MyAssert.assertEquals("msg.getEnvelopeSender()=${msg.getEnvelopeSender()}"
		+ " is not equal to senderAddress=${senderAddress}",
		msg.getEnvelopeSender(), senderAddress)
	MyAssert.assertEquals("msg.getEnvelopeReceiver()=${msg.getEnvelopeReceiver()}"
		+ " is not equal to receiverAddress=${receiverAddress}",
		msg.getEnvelopeReceiver(), receiverAddress)
	try {
		MyAssert.assertEquals("msg.getMimeMessage()=${msg.getMimeMessage()}"
			+ " is not equal to subject=${subject}",
			msg.getMimeMessage().getSubject(), subject)
		MimeMessage mimeMessage = msg.getMimeMessage()
		String expectedContentType = "text/html; charset=iso-2022-jp"
		MyAssert.assertEquals("mimeMessage.getContentType()=${mimeMessage.getContentType()}"
			+ " is not equal to expectedContentType=\"${expectedContentType}\"",
			mimeMessage.getContentType(), expectedContentType)
		if (mimeMessage.getContent() instanceof String) {
			String content = (String)mimeMessage.getContent()
			MyAssert.assertEquals("content=${content}"
				+ " is not equal to message=${message}",
				content.trim(), message.trim())
		}
		WebUI.comment("PASS")
	} catch (MessagingException e) {
		MyAssert.fail("Couldn't find a subject")
	}
}


// stop the Sub Etha SMTP server
wiser.stop()

