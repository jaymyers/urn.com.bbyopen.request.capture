package com.bbyopen.request.capture;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFRequestReadOnly;
import org.netkernel.layer0.representation.impl.HDSNodeImpl;
import org.netkernel.module.standard.endpoint.StandardAccessorImpl;

public class RequestCapture extends StandardAccessorImpl{

	@Override
	public void onSource(INKFRequestContext context) throws Exception{
		
		INKFRequestReadOnly initialRequest = context.source("arg:request", INKFRequestReadOnly.class);
		String thisrequest = context.source("httpRequest:/url", String.class);
		String method = context.source("httpRequest:/method", String.class);
		
		HDSNodeImpl header = context.source("httpRequest:/headers", HDSNodeImpl.class);
		
		String hostname = context.source("httpRequest:/remote-host", String.class);
		String qp = context.source("httpRequest:/query", String.class);
		String accept = (String) header.getFirstValue("//Accept");
		String acceptcharset = (String) header.getFirstValue("//Accept-Charset");
		String ims = (String) header.getFirstValue("//If-Modified-Since");
		String lang = (String) header.getFirstValue("//Accept-Language");
		String conn = (String) header.getFirstValue("//Connection");
		String cc = (String) header.getFirstValue("//Cache-Control");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss", Locale.ROOT);
		Date now = new Date();
		String timestamp = simpleDateFormat.format(now);
		
		JSONObject requestdata = new JSONObject();
		requestdata.put("timestamp",timestamp);
		requestdata.put("url",thisrequest);
		requestdata.put("method",method);
		requestdata.put("hostname",hostname);
		requestdata.put("qp",qp);
		requestdata.put("accept",accept);
		requestdata.put("acceptcharset",acceptcharset);
		requestdata.put("if-modified-since",ims);
		requestdata.put("lang",lang);
		requestdata.put("connection",conn);
		requestdata.put("cache-control",cc);
		
		String json = JSONValue.toJSONString(requestdata);

		// capture information from request and store it
		INKFRequest storeRequest = context.createRequest("active:requestStore");
		storeRequest.addArgumentByValue("data", json);
		context.issueAsyncRequest(storeRequest);
		
		INKFRequest clonedRequest = initialRequest.getIssuableClone();
		context.createResponseFrom(clonedRequest);
	}
}
