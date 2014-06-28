package org.stagemonitor.requestmonitor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import org.stagemonitor.core.MeasurementSession;
import org.stagemonitor.requestmonitor.profiler.CallStackElement;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestTrace {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	static {
		MAPPER.registerModule(new AfterburnerModule());
	}

	@JsonIgnore
	private String id = UUID.randomUUID().toString();
	private String name;
	@JsonIgnore
	private CallStackElement callStack;
	private long executionTime;
	private long executionTimeCpu;
	private boolean error = false;
	@JsonProperty("@timestamp")
	private String timestamp;
	private String parameter;
	@JsonProperty("@application")
	private String application;
	@JsonProperty("@host")
	private String host;
	@JsonProperty("@instance")
	private String instance;
	private String exceptionMessage;
	private String exceptionClass;
	private String exceptionStackTrace;
	private String username;
	private String clientIp;

	public RequestTrace() {
		TimeZone tz = TimeZone.getDefault();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		df.setTimeZone(tz);
		this.timestamp = df.format(new Date());
	}

	public void setMeasurementSession(MeasurementSession measurementSession) {
		application = measurementSession.getApplicationName();
		host = measurementSession.getHostName();
		instance = measurementSession.getInstanceName();
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean failed) {
		this.error = failed;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public CallStackElement getCallStack() {
		return callStack;
	}

	public void setCallStack(CallStackElement callStack) {
		this.callStack = callStack;
	}

	@JsonProperty("callStack")
	public String getCallStackAscii() {
		if (callStack == null) {
			return null;
		}
		return callStack.toString(true);
	}

	public String getCallStackJson() throws JsonProcessingException {
		if (callStack == null) {
			return null;
		}
		return MAPPER.writeValueAsString(callStack);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(long executionTime) {
		this.executionTime = executionTime;
	}

	public long getExecutionTimeCpu() {
		return executionTimeCpu;
	}

	public void setExecutionTimeCpu(long executionTimeCpu) {
		this.executionTimeCpu = executionTimeCpu;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getInstance() {
		return instance;
	}

	public void setInstance(String instance) {
		this.instance = instance;
	}

	public String getExceptionMessage() {
		return exceptionMessage;
	}

	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}

	public String getExceptionStackTrace() {
		return exceptionStackTrace;
	}

	public void setExceptionStackTrace(String exceptionStackTrace) {
		this.exceptionStackTrace = exceptionStackTrace;
	}

	public String getExceptionClass() {
		return exceptionClass;
	}

	public void setExceptionClass(String exceptionClass) {
		this.exceptionClass = exceptionClass;
	}

	public void setException(Exception e) {
		error = e != null;
		if (e != null) {
			exceptionMessage = e.getMessage();
			exceptionClass = e.getClass().getCanonicalName();

			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw, true);
			e.printStackTrace(pw);
			exceptionStackTrace = sw.getBuffer().toString();
		}
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public String getClientIp() {
		return clientIp;
	}

	@Override
	public String toString() {
		return toString(false);
	}

	public String toString(boolean asciiArt) {
		StringBuilder sb = new StringBuilder(3000);
		sb.append("id:     ").append(id).append('\n');
		sb.append("name:   ").append(name).append('\n');
		if (getParameter() != null) {
			sb.append("params: ").append(getParameter()).append('\n');
		}
		appendCallStack(sb, asciiArt);
		return sb.toString();
	}

	protected void appendCallStack(StringBuilder sb, boolean asciiArt) {
		if (getCallStack() != null) {
			sb.append(getCallStack().toString(asciiArt));
		}
	}
}
