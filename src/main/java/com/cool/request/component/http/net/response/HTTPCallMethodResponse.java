package com.cool.request.component.http.net.response;

import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.common.model.ErrorHTTPResponseBody;
import com.cool.request.component.http.HTTPResponseListener;
import com.cool.request.component.http.HTTPResponseManager;
import com.cool.request.component.http.net.*;
import com.cool.request.utils.Base64Utils;
import com.cool.request.view.main.HTTPEventListener;
import com.intellij.openapi.project.Project;
import okhttp3.Headers;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HTTPCallMethodResponse implements HttpRequestCallMethod.SimpleCallback {
    private final Project project;
    private final Map<RequestContext, Thread> waitResponseThread;
    private final List<HTTPResponseListener> httpResponseListeners;
    private final RequestContext requestContext;

    public HTTPCallMethodResponse(Project project,
                                  Map<RequestContext, Thread> waitResponseThread,
                                  List<HTTPResponseListener> httpResponseListeners,
                                  RequestContext requestContext) {
        this.project = project;
        this.waitResponseThread = waitResponseThread;
        this.httpResponseListeners = httpResponseListeners;
        this.requestContext = requestContext;
    }

    @Override
    public void onResponse(String requestId, int code, Response response) {
        //可能被用户取消了，然后才响应成功，不做处理
        if (!waitResponseThread.containsKey(requestContext)) {
            return;
        }
        waitResponseThread.remove(requestContext);

        Headers okHttpHeaders = response.headers();
        List<Header> headers = new ArrayList<>();
        int headerCount = okHttpHeaders.size();
        for (int i = 0; i < headerCount; i++) {
            String headerName = okHttpHeaders.name(i);
            String headerValue = okHttpHeaders.value(i);
            headers.add(new Header(headerName, headerValue));
        }
        HTTPResponseBody httpResponseBody = new HTTPResponseBody();

        httpResponseBody.setBase64BodyData("");
        httpResponseBody.setCode(response.code());
        httpResponseBody.setId(requestId);
        httpResponseBody.setHeader(headers);
        httpResponseBody.setSize(0);
        if (response.body() != null) {
            try {
                byte[] bytes = response.body().bytes();
                httpResponseBody.setSize(bytes.length);
                bytes = HTTPResponseManager.getInstance(project).bodyConverter(bytes, new HTTPHeader(headers));
                httpResponseBody.setBase64BodyData(Base64Utils.encodeToString(bytes));
            } catch (IOException ignored) {
            }
        }

        for (HTTPEventListener httpEventListener : requestContext.getHttpEventListeners()) {
            httpEventListener.endSend(requestContext, httpResponseBody);
        }
        //通知全局的监听器
        HTTPResponseManager.getInstance(project).onHTTPResponse(httpResponseBody);
    }

    @Override
    public void onError(String requestId, IOException e) {
        ErrorHTTPResponseBody errorHTTPResponseBody = new ErrorHTTPResponseBody(e.getMessage().getBytes());
        for (HTTPEventListener httpEventListener : requestContext.getHttpEventListeners()) {
            httpEventListener.endSend(requestContext, errorHTTPResponseBody);
        }
        project.getMessageBus()
                .syncPublisher(CoolRequestIdeaTopic.HTTP_RESPONSE)
                .onResponseEvent(requestId, errorHTTPResponseBody, requestContext);
    }
}
