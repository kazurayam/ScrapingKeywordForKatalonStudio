package com.kazurayam.katalon.keyword
import static org.hamcrest.CoreMatchers.*
import static org.hamcrest.MatcherAssert.*
import static org.junit.Assert.*

import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

import com.kazurayam.junit4ks.IgnoreRestSupportRunner


/**
 * ユニットテスト用のメールサーバsubethasmtpがあることを知りました
 * https://qiita.com/tchofu/items/79097b68cc9e22a1f401
 * 
 * 
 * @author qcq0264
 *
 */
@RunWith(IgnoreRestSupportRunner.class)
public class MailKeywordTest {

    @BeforeClass public static void onlyOnce() {
    }

    @Test
    void test_sendMail() {
        // setup:
        MailKeyword keyword = new MailKeyword()
        // when:
        keyword.sendMail("Hello, world")
        // then:
        assertThat(true, is(false))
    }
}
