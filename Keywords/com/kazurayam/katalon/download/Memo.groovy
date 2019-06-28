/**
 * this method downloads the whole bytes of the resource from URL, save it into a temp file as buffer,
 * returns a BufferedResponsePobject with an InputSteam for the buffering file.
 * @author kazurayam
 *
 private StreamingResponseObject responseWithHack(HttpURLConnection conn) throws Exception {
 if (conn == null) {
 return null
 }
 long startTime = System.currentTimeMillis()
 int statusCode = conn.getResponseCode()
 long waitingTime = System.currentTimeMillis() - startTime
 long contentDownloadTime = 0L
 byte[] buffer = new byte[1024]
 long bodyLength = 0L
 PipedOutputStream pipedOutput = new PipedOutputStream()
 // enable buffering
 int BUFFER_SIZE = 1024 * 1000;
 OutputStream out = new BufferedOutputStream(pipedOutput, BUFFER_SIZE)
 PipedInputStream pipedInput = new PipedInputStream()
 pipedInput.connect(pipedOutput)
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
 if (out != null) {
 try {
 out.close()
 } catch (IOException e) {
 e.printStackTrace()
 }
 }
 }
 StreamingResponseObject responseObject = new StreamingResponseObject()
 responseObject.setInputStream(pipedInput)
 //
 responseObject.setContentType(conn.getContentType());
 responseObject.setHeaderFields(conn.getHeaderFields());
 responseObject.setStatusCode(statusCode);
 responseObject.setResponseBodySize(bodyLength);
 long headerLength = WebServiceCommonHelper.calculateHeaderLength(conn)
 responseObject.setResponseHeaderSize(headerLength);
 responseObject.setWaitingTime(waitingTime);
 responseObject.setContentDownloadTime(contentDownloadTime);
 conn.disconnect();
 return responseObject;
 }
 */
