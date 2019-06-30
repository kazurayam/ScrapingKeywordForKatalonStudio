package com.kazurayam.katalon.download

import java.lang.reflect.Field
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.CountDownLatch

import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext

import org.apache.commons.lang.StringUtils
import org.apache.commons.lang3.time.StopWatch

import com.kms.katalon.constants.GlobalStringConstants
import com.kms.katalon.core.network.ProxyInformation
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.webservice.common.BasicRequestor
import com.kms.katalon.core.webservice.constants.RequestHeaderConstants
import com.kms.katalon.core.webservice.helper.RestRequestMethodHelper
import com.kms.katalon.core.webservice.helper.WebServiceCommonHelper
import com.kms.katalon.core.webservice.support.UrlEncoder
import org.apache.commons.io.FileUtils
/**
 * <p></p>
 * <p>DownloadingClient is a modification of com.kms.katalon.core.webservice.common.RestfulClient class.
 *     The following methods are 100% identical to those of RestfulClient:
 *     <ul>
 *     <li>- sendRequest()</li>
 *     <li>- isBodySupported()</li>
 *     <li>- setRequestMethod()</li>
 *     <li>- processRequestParams()</li>
 *     </ul>
 *     I did not wanted to but I had to copy the source code of those and paste, as those methods are
 *     marked "private". If those were marked "protected", then I would have happily extended the
 *     RestufulClient to create DownloadingClient.
 *     </p>
 * 
 * <p>You can find the source code of RestfulClient here: 
 *     https://github.com/katalon-studio/katalon-studio-testing-framework/blob/master/Include/scripts/groovy/com/kms/katalon/core/webservice/common/RestfulClient.java</p>
 * 
 * <p>The send() method here returns an instance of 
 *     com.kazurayam.ks.download.DownloadingClient.StreamingResponseObject 
 *     instead of 
 *     com.kms.katalon.core.testobject.RequestObject. 
 *     This is the sole difference from the RestfulClient.</p>
 * 
 * <p>StreamingResponseObject offers the getInputStream() method.</p>
 * 
 * @author kazurayam
 */
public class DownloaderClient extends BasicRequestor {

	private static final String SSL = RequestHeaderConstants.SSL

	private static final String HTTPS = RequestHeaderConstants.HTTPS

	private static final String DEFAULT_USER_AGENT = GlobalStringConstants.APP_NAME

	private static final String HTTP_USER_AGENT = RequestHeaderConstants.USER_AGENT


	DownloaderClient(String projectDir, ProxyInformation proxyInformation) {
		super(projectDir, proxyInformation)
		Objects.requireNonNull(projectDir, "projectDir must not be null")
		Objects.requireNonNull(proxyInformation, "proxyInformation must not be null")
		Path p = Paths.get(projectDir)
		if (! Files.exists(p)) {
			throw new IllegalArgumentException("projectDir ${p} does not exist")
		}
	}

	@Override
	public ResponseObject send(RequestObject request) throws Exception {
		//return sendRequest(request)
		throw new UnsupportedOperationException(
		"Do not use send(RequestObject) method." +
		" Use downloadAndSave(RequestObject, Path outFile) method instead.")
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
	 * 
	 */
	public Map<String, List<String>> downloadAndSave(RequestObject request, Path outFile) throws IOException, InterruptedException {

		HttpURLConnection connection = sendRequest(request)

		PipedOutputStream pos = new PipedOutputStream()
		PipedInputStream  pis = new PipedInputStream(pos)
		CountDownLatch  latch = new CountDownLatch(1)

		// This Thread download resource from URL and writes it into pipe
		Thread pipeWriter = new ResourceDownloadingPipeWriter(pos, latch, connection)

		// This Thread reads resource from pipe and save it into file
		Thread pipeReader  = new PipeReadingResourceSaver(pis, latch, outFile)

		pipeWriter.start()
		pipeReader.start()

		pipeWriter.join()
		pipeWriter.join()

		pis.close()
		pos.close()

		return connection.getHeaderFields()
	}

	/**
	 * 
	 */
	HttpURLConnection sendRequest(RequestObject request) {
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
		return httpConnection
	}

	/**
	 * Download a web resource from the specified URL,
	 * write the bytes into the pipe to pass to the PipeReader
	 * 
	 * @author kazurayam
	 */
	class ResourceDownloadingPipeWriter extends Thread {

		private PipedOutputStream pos_
		private HttpURLConnection conn_
		private CountDownLatch latch_
		private static final int BUFFER_SIZE = 1024 * 100

		private boolean DEBUG_MODE = false

		ResourceDownloadingPipeWriter(PipedOutputStream pos, CountDownLatch latch, HttpURLConnection conn) {
			Objects.requireNonNull(pos, "pos must not be null")
			Objects.requireNonNull(latch, "latch must not be null")
			Objects.requireNonNull(conn, "conn must not be null")
			this.pos_ = pos
			this.conn_ = conn
			this.latch_ = latch
		}

		@Override
		public void run() {
			int statusCode = conn_.getResponseCode()
			byte[] buffer = new byte[BUFFER_SIZE]
			InputStream input = null
			try {
				StopWatch stopWatch = new StopWatch()
				stopWatch.start()
				input = ((statusCode >= 400)) ? conn_.getErrorStream() : conn_.getInputStream()
				if (input != null) {
					int len = 0
					while (true) {
						len = input.read(buffer)
						if (len == -1) {
							break
						}
						pos_.write(buffer, 0, len)
					}
				}
				// PipeWriter needs to close the pipe in order to notify PipeReader of the end of the stream
				try {
					pos_.close()
				} catch (IOException e) {
					e.printStackTrace()
				}
				//
				stopWatch.stop()
				int contentLength = conn_.getContentLength()
				if (DEBUG_MODE) {
					System.out.println("ResourceDownloaderPipeWriter took ${stopWatch.getTime()} milliseconds for downloading ${contentLength} bytes")
				}
				// wait for the PipeReader to finish consuming the piped stream, then finish
				latch_.await()
				if (DEBUG_MODE) {
					System.out.println("ResourceDownloaderPipeWriter finished processing")
				}
			} catch (IOException e) {
				e.printStackTrace()
			} finally {
				if (input != null) {
					try {
						input.close()
					} catch (IOException e) {
						e.printStackTrace()
					}

				}
			}
		}
	}


	/**
	 * 
	 */
	class PipeReadingResourceSaver extends Thread {

		private PipedInputStream pis_
		private Path outFile_
		private CountDownLatch latch_
		private static final int BUFFER_SIZE = 4096

		private boolean DEBUG_MODE = false

		PipeReadingResourceSaver(PipedInputStream pis, CountDownLatch latch, Path outFile) {
			this.pis_     = pis
			this.outFile_ = outFile
			this.latch_   = latch
		}

		@Override
		public void run() {
			BufferedOutputStream bos = null
			int resourceSize = 0
			try {
				bos = new BufferedOutputStream(new FileOutputStream(outFile_.toFile()))
				byte[] buffer = new byte[BUFFER_SIZE]
				while (true) {
					int len = pis_.read(buffer)
					if (len == -1) {
						break
					}
					bos.write(buffer, 0, len)
					resourceSize += len
				}
				latch_.countDown()
				if (DEBUG_MODE) {
					System.out.println("PipeReadingResourceSaver finished saving the resource(${resourceSize} bytes) into ${outFile_}")
				}
			} catch (IOException e) {
				e.printStackTrace()
			} finally {
				try {
					pis_.close()
				} catch (Exception e) {
					e.printStackTrace()
				}
				try {
					bos.flush()
					bos.close()
				} catch (Exception e) {
					e.printStackTrace()
				}
			}
		}
	}

	/**
	 * Convert a text file in a given Charset to UTF-8.
	 * 
	 * While converting, the End-of-line to the one of the runtime environment.
	 * 
	 * This method overwrites the target text file.
	 * 
	 * @param textPath
	 * @param charset, optional, default to 'MS932'
	 */
	public static void convertCharsetToUtf8(Path textPath, String charset = 'MS932') {
		Objects.requireNonNull(textPath, "textFile must not be null")
		if (!Files.exists(textPath)) {
			throw new IllegalArgumentException("${textPath} does not exist")
		}
		File tmp = null
		try {
			tmp = Files.createTempFile(Paths.get('.'), 'converCharsetToUTF8', '.tmp').toFile()

			// read the text file as MS932, convert every line to utf-8 and write into the tmp file
			BufferedReader br = new BufferedReader(
					new InputStreamReader(
					new FileInputStream(textPath.toFile()),
					'MS932'))
			PrintWriter bw = new PrintWriter(
					new BufferedWriter(
					new OutputStreamWriter(
					new FileOutputStream(tmp),
					'utf-8')))
			String line
			while ((line = br.readLine()) != null) {
				bw.println(line)
			}
			bw.flush()
			bw.close()
			br.close()
			//
			FileUtils.copyFile(tmp, textPath.toFile())
		} finally {
			if (tmp != null && tmp.exists()) {
				tmp.delete()
			}
		}
	}

}
