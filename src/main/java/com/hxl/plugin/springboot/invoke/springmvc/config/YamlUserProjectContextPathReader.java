package com.hxl.plugin.springboot.invoke.springmvc.config;

import com.hxl.plugin.springboot.invoke.springmvc.config.base.BaseYamlUserProjectConfigReader;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;

public class YamlUserProjectContextPathReader extends BaseYamlUserProjectConfigReader<String> {

    public YamlUserProjectContextPathReader(Project project, Module module) {
        super(project, module);
    }

    @Override
    public String read() {
        return doRead("application.yaml", SpringKey.KEY_CONTEXT_PATH, false);
    }
}