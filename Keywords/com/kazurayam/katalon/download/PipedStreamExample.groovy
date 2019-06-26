package com.kazurayam.katalon.download

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable
import java.io.PipedInputStream
import java.io.PipedOutputStream

/**
 * http://www.kab-studio.biz/Programing/JavaA2Z/Word/00000896.html
 * 
 * @author kazurayam
 * 
 */
// Pipe Reader might be also threaded; see https://www.boraji.com/java-pipedinputstream-and-pipedoutputstream-example

public class PipedStreamExample {

	PipedOutputStream pos_ = null
	PipedInputStream pis_ = null

	public void execute() throws IOException, InterruptedException {

		pos_ = new PipedOutputStream()
		pis_ = new PipedInputStream(pos_)

		// This Thread writes data into pipe
		Thread pipeWriter = new PipeWriter(pos_)

		// This Thread reads data from pipe
		Thread pipeReader = new PipeReader(pis_)

		pipeWriter.start()
		pipeReader.start()

		pipeWriter.join()
		pipeReader.join()

		pis_.close()
		pos_.close()
		
		// I saw the following Exception raised:
		// java.io.IOException: Pipe broken
		//   at java.io.PipedInputStream.read(PipedInputStream.java:321)
		//   at com.kazurayam.katalon.download.PipeReader.run(PipedStreamExample.groovy:78)
		
		// See
		//   https://stackoverflow.com/questions/1866255/pipedinputstream-how-to-avoid-java-io-ioexception-pipe-broken
		//   > Use a java.util.concurrent.CountDownLatch, and do not end the first thread 
		//   > before the second one has signaled that is has finished reading from the pipe.
		
		
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		PipedStreamExample example = new PipedStreamExample()
		example.execute()
	}
}

/**
 * 
 */
class PipeReader extends Thread {
	private PipedInputStream pis_

	public PipeReader(PipedInputStream pis) {
		this.pis_ = pis
	}

	@Override
	public void run() {
		try {
			while (true) {
				int i = pis_.read()
				if (i == -1) {
					break
				}
				System.out.println "0x${Integer.toHexString(i)}(${i})"
			}
			System.out.println("PipeReader finished")
		} catch (IOException e) {
			e.printStackTrace()
		} finally {

		}
	}
}

/**
 * 
 */
class PipeWriter extends Thread {

	private PipedOutputStream pos_

	public PipeWriter(PipedOutputStream pos) {
		this.pos_ = pos
	}

	@Override
	public void run() {
		try {
			for (int c = 0; c <= 16; ++c) {
				pos_.write(c)
				// pause the tread for 1 second
				try {
					Thread.sleep(1 * 1000)
				} catch (InterruptedException e) {
					e.printStackTrace()
				}
			}
			System.out.println("PipeWriter finished")
		} catch (IOException e) {
			e.printStackTrace()
		}
	}


}