package com.bbyopen.request.capture;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.INKFRequestReadOnly;
import org.netkernel.module.standard.endpoint.StandardAccessorImpl;

public class RequestCapture extends StandardAccessorImpl{

	@Override
	public void onSource(INKFRequestContext context) throws Exception{
		INKFRequestReadOnly initialRequest = context.source("arg:operand", INKFRequestReadOnly.class);
		
		// capture information from request and store it
		INKFRequest storeRequest = context.createRequest("active:requestStore");
		storeRequest.addArgumentByValue("url", context.getThisRequest().getIdentifier());
		context.issueAsyncRequest(storeRequest);
		
		INKFRequest clonedRequest = initialRequest.getIssuableClone();
		context.createResponseFrom(clonedRequest);
	}
}
