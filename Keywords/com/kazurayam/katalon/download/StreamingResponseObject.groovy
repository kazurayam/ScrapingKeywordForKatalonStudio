package com.kazurayam.katalon.download

import com.kms.katalon.core.testobject.ResponseObject

public class StreamingResponseObject extends ResponseObject {

	private InputStream inputStream_ = null

	StreamingResponseObject() {
		super()
	}

	StreamingResponseObject(String responseText) {
		super(responseText)
	}

	void setInputStream(InputStream is) {
		this.inputStream_ = is
	}

	InputStream getInputStream() {
		return this.inputStream_
	}
}
