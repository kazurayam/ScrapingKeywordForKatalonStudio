package com.kazurayam.misc

import java.util.concurrent.CountDownLatch

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

    private static CountDownLatch latch = new CountDownLatch(1);

    public void execute() throws IOException, InterruptedException {

        pos_ = new PipedOutputStream()
        pis_ = new PipedInputStream(pos_)

        // This Thread writes data into pipe
        Thread pipeWriter = new PipeWriter(pos_, latch)

        // This Thread reads data from pipe
        Thread pipeReader = new PipeReader(pis_, latch)

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
class PipeWriter extends Thread {

    private PipedOutputStream pos_
    private CountDownLatch latch_

    public PipeWriter(PipedOutputStream pos, CountDownLatch latch) {
        this.pos_ = pos
        this.latch_ = latch
    }

    @Override
    public void run() {
        try {
            for (int c = 0; c <= 5; ++c) {
                pos_.write(c)
                // pause the tread for 1 second
                try {
                    Thread.sleep(1 * 1000)
                } catch (InterruptedException e) {
                    e.printStackTrace()
                }
            }
            // PipeWriter need to close the pipe explicitly
            // in order to notify PipeReader of the end of the stream
            pos_.close()
            latch_.await()
            System.out.println("PipeWriter finished")
        } catch (IOException e) {
            e.printStackTrace()
        }
    }

}


/**
 *
 */
class PipeReader extends Thread {

    private PipedInputStream pis_
    private CountDownLatch latch_

    public PipeReader(PipedInputStream pis, CountDownLatch latch) {
        this.pis_ = pis
        this.latch_ = latch
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
            latch_.countDown()
            System.out.println("PipeReader finished")
        } catch (IOException e) {
            e.printStackTrace()
        } finally {
            try {
                pis_.close()
            } catch (Exception e) {
                e.printStackTrace()
            }
        }
    }
}
