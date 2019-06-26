package com.kazurayam.katalon.download

import com.kms.katalon.core.testobject.ResponseObject

public class BufferedResponseObject extends ResponseObject {

	private InputStream inputStream_ = null

	BufferedResponseObject() {
		super()
	}

	BufferedResponseObject(String responseText) {
		super(responseText)
	}

	void setInputStream(InputStream is) {
		this.inputStream_ = is
	}

	InputStream getInputStream() {
		return this.inputStream_
	}
}
