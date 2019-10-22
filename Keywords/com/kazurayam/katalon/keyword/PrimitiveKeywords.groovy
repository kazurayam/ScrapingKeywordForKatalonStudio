package com.kazurayam.ksbackyard

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.util.KeywordUtil

public class PrimitiveKeywords {

	/**
	 * This keyword always fails. Into the Katalon log, it emits the message given as 1st argument.
	 * The FailureHandling object given as 2nd argument is respected.
	 * 
	 * @param message
	 * @param flowControl
	 * @throws StepFailedException
	 */
	@Keyword
	static void fail(String message, FailureHandling flowControl) throws StepFailedException {
		switch (flowControl) {
			case FailureHandling.OPTIONAL:
				KeywordUtil.logInfo(message)
				break
			case FailureHandling.CONTINUE_ON_FAILURE:
				KeywordUtil.markFailed(message)
				break
			case FailureHandling.STOP_ON_FAILURE:
				KeywordUtil.markFailedAndStop(message)
				break
		}
	}
}
