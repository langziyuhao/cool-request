package com.hxl.plugin.springboot.invoke.view.page;

import com.hxl.plugin.springboot.invoke.invoke.ControllerInvoke;
import com.hxl.plugin.springboot.invoke.net.MapRequest;
import com.hxl.plugin.springboot.invoke.view.BasicTableParamJPanel;
import com.intellij.ui.JBColor;


public class UrlParamPage  extends BasicTableParamJPanel  implements MapRequest {
    public UrlParamPage() {
        setBackground(JBColor.RED);
    }

    @Override
    public void configRequest(ControllerInvoke.ControllerRequestData controllerRequestData) {

    }
}