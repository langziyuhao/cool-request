package com.hxl.plugin.springboot.invoke.view.page;

import com.hxl.plugin.springboot.invoke.invoke.ControllerInvoke;
import com.hxl.plugin.springboot.invoke.net.MapRequest;
import com.hxl.plugin.springboot.invoke.utils.UrlUtils;
import com.hxl.plugin.springboot.invoke.view.BasicTableParamJPanel;
import com.intellij.ui.JBColor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;


public class UrlParamPage  extends BasicTableParamJPanel  implements MapRequest {
    public UrlParamPage() {
    }
    @Override
    public void configRequest(ControllerInvoke.ControllerRequestData controllerRequestData) {
        Map<String,Object> param =new HashMap<>();
        foreach(param::put);
        String url = controllerRequestData.getUrl();
        //asd?
        //asd?name=1
        //asd?name=a&
        //asd?&
        //asd?
        if ( url.indexOf('?')==-1  && !url.endsWith("?")) url =url.concat("?");
        if (!url.endsWith("&") && param.size()>0  && !url.endsWith("?")) url=url.concat("&");
        controllerRequestData.setUrl(url.concat(UrlUtils.mapToUrlParams(param)));
    }
}
