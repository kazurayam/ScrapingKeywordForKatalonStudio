package com.kazurayam.ks.download

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
import com.kms.katalon.core.webservice.common.BasicRequestor
import internal.GlobalVariable
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.ResponseObject
import org.apache.commons.lang.StringUtils
import com.kms.katalon.core.webservice.constants.RequestHeaderConstants
import com.kms.katalon.core.webservice.common.RestfulClient
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import com.kms.katalon.constants.GlobalStringConstants
import com.kms.katalon.core.webservice.helper.RestRequestMethodHelper
import java.lang.reflect.Field
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.webservice.support.UrlEncoder
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import com.kms.katalon.core.webservice.helper.WebServiceCommonHelper

/**
 * <p>A modification of com.kms.katalon.core.webservice.common.RestfulClient class.</p>
 * <p>https://github.com/katalon-studio/katalon-studio-testing-framework/blob/master/Include/scripts/groovy/com/kms/katalon/core/webservice/common/RestfulClient.java</p>
 * 
 * <p>The send() method returns an instance of com.kazurayam.ks.download.DownloadableResponseObject 
 *     instead of com.kms.katalon.core.testobject.RequestObject. This is the sole difference from the RestfulClient.</p>
 * 
 * <p>DownloadableResponseObject offers the getInputStream() method.</p>
 * 
 */
public class WebResourceDownloader extends BasicRequestor {
	
	private static final String SSL = RequestHeaderConstants.SSL
	
	private static final String HTTPS = RequestHeaderConstants.HTTPS

	private static final String DEFAULT_USER_AGENT = GlobalStringConstants.APP_NAME

	private static final String HTTP_USER_AGENT = RequestHeaderConstants.USER_AGENT

	@Override
	public BufferedResponseObject send(RequestObject request) throws Exception {
		return sendRequest(request)
	}

	/** 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	private BufferedResponseObject sendRequest(RequestObject request) throws Exception {
		if (StringUtils.defaultString(request.getRestUrl()).toLowerCase().startsWith(HTTPS)) {
			SSLContext sc = SSLContext.getInstance(SSL);
			sc.init(null, getTrustManagers(), new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		}

		// If there are some parameters, they should be append after the Service URL
		processRequestParams(request);

		URL url = new URL(request.getRestUrl());
		HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection(getProxy());
		if (StringUtils.defaultString(request.getRestUrl()).toLowerCase().startsWith(HTTPS)) {
			((HttpsURLConnection) httpConnection).setHostnameVerifier(getHostnameVerifier());
		}

		String requestMethod = request.getRestRequestMethod();
		setRequestMethod(httpConnection, request.getRestRequestMethod());

		// Default if not set
		httpConnection.setRequestProperty(HTTP_USER_AGENT, DEFAULT_USER_AGENT);
		setHttpConnectionHeaders(httpConnection, request);

		if (isBodySupported(requestMethod) && request.getBodyContent() != null) {
			httpConnection.setDoOutput(true);

			// Send post request
			OutputStream os = httpConnection.getOutputStream();
			request.getBodyContent().writeTo(os);
			os.flush();
			os.close();
		}


		/* kazurayam hacked here.
		 * this is the sole difference from the com.kms.katalon.core.webservice.commonRestfulClient
		 */
		//return response(httpConnection)
		BufferedResponseObject bro = responseWithHack(httpConnection)
		return bro;
	}

	private boolean isBodySupported(String requestMethod) {
		return RestRequestMethodHelper.isBodySupported(requestMethod);
	}

	/**
	 * HttpURLConnection will throw ProtocolException when setting a request method which is not
	 * GET, POST, HEAD, OPTIONS, PUT, DELETE, or TRACE. Use this workaround for unsupported methods.
	 */
	private static void setRequestMethod(HttpURLConnection connection, String method)
	throws ProtocolException {
		try {
			connection.setRequestMethod(method);
		} catch (ProtocolException ex) {
			try {
				Field methodField = HttpURLConnection.class.getDeclaredField("method");
				methodField.setAccessible(true);
				if (connection instanceof HttpsURLConnection) {
					try {
						Field delegateField = connection.getClass().getDeclaredField("delegate");
						delegateField.setAccessible(true);

						Object delegateConnection = delegateField.get(connection);
						if (delegateConnection instanceof HttpURLConnection) {
							methodField.set(delegateConnection, method);
						}

						Field httpsURLConnectionField = delegateConnection.getClass()
						.getDeclaredField("httpsURLConnection");
						httpsURLConnectionField.setAccessible(true);
						HttpsURLConnection httpsURLConnection = (HttpsURLConnection) httpsURLConnectionField
						.get(delegateConnection);

						methodField.set(httpsURLConnection, method);
					} catch (Exception ignored) {

					}

				}

				methodField.set(connection, method);
			} catch (Exception e) {
				throw ex;
			}
		}
	}

	private static void processRequestParams(RequestObject request) throws MalformedURLException {
		StringBuilder paramString = new StringBuilder();
		for (TestObjectProperty property : request.getRestParameters()) {
			if (StringUtils.isEmpty(property.getName())) {
				continue;
			}
			if (!StringUtils.isEmpty(paramString.toString())) {
				paramString.append("&");
			}
			paramString.append(UrlEncoder.encode(property.getName()));
			paramString.append("=");
			paramString.append(UrlEncoder.encode(property.getValue()));
		}
		if (!StringUtils.isEmpty(paramString.toString())) {
			URL url = new URL(request.getRestUrl());
			request.setRestUrl(
			request.getRestUrl() + (StringUtils.isEmpty(url.getQuery()) ? "?" : "&") + paramString.toString());
		}
	}


	/**
	 * this method downloads the whole bytes of the resource from URL, save it into a temp file as buffer,
	 * returns a BufferedResponsePobject with an InputSteam for the buffering file.
	 * @author kazurayam
	 */
	private BufferedResponseObject responseWithHack(HttpURLConnection conn) throws Exception {
		if (conn == null) {
			return null
		}
		long startTime = System.currentTimeMillis()
		int statusCode = conn.getResponseCode()
		long waitingTime = System.currentTimeMillis() - startTime
		long contentDownloadTime = 0L

		char[] buffer = new char[1024]
		long bodyLength = 0L

		/*
		 * we use PipedOutputStream & PipedInputStream for effective buffering
		 */
		PipedOutputStream out = new PipedOutputStream()
		
		InputStream inputStream = null
		try {
			inputStream = ((statusCode >= 400) ? conn.getErrorStream() : conn.getInputStream())
			if (inputStream != null) {
				startTime = System.currentTimeMillis()
				int len = 0
				while (true) {
					len = inputStream.read(buffer)
					if (len == -1) {
						break
					}
					contentDownloadTime += System.currentTimeMillis() - startTime
					out.write(buffer, 0, len)
					bodyLength += len
					startTime = System.currentTimeMillis()

				}
			} else {
				throw new IOException("HttpURLConnection returned null as inputstream")
			}
		} catch (IOException e) {
			e.printStackTrace()
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close()
				} catch (IOException e) {
					e.printStackTrace()
				}
			}
		}
		
		PipedInputStream pipedInput = new PipedInputStream(out)

		long headerLength = WebServiceCommonHelper.calculateHeaderLength(conn)
		BufferedResponseObject responseObject = new BufferedResponseObject(sb.toString())
		responseObject.setInputStream(pipedInput)
		responseObject.setContentType(conn.getContentType());
		responseObject.setHeaderFields(conn.getHeaderFields());
		responseObject.setStatusCode(statusCode);
		responseObject.setResponseBodySize(bodyLength);
		responseObject.setResponseHeaderSize(headerLength);
		responseObject.setWaitingTime(waitingTime);
		responseObject.setContentDownloadTime(contentDownloadTime);
		
		conn.disconnect();

		return responseObject;
	}
}