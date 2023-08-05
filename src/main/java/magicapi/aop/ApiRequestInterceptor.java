package magicapi.aop;

import cn.dev33.satoken.stp.StpUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.ssssssss.magicapi.core.interceptor.RequestInterceptor;
import org.ssssssss.magicapi.core.model.ApiInfo;
import org.ssssssss.magicapi.core.model.JsonBean;
import org.ssssssss.magicapi.core.model.Options;
import org.ssssssss.magicapi.core.servlet.MagicHttpServletRequest;
import org.ssssssss.magicapi.core.servlet.MagicHttpServletResponse;
import org.ssssssss.script.MagicScriptContext;

import java.util.Enumeration;

@Component
public class ApiRequestInterceptor implements RequestInterceptor {
    @Value("${api.token:}")
    private String apiToken;

    @Override
    public Object preHandle(ApiInfo info, MagicScriptContext context, MagicHttpServletRequest request, MagicHttpServletResponse response) {
        // 未配置token，则说明不需要校验token
        if (StringUtils.isBlank(apiToken)) {
            return null;
        }
        // 接口选项配置了不需要登录
        if ("false".equals(info.getOptionValue(Options.REQUIRE_LOGIN))) {
            return null;
        }
        // 如果已登录，则不需要校验token
        if (StpUtil.isLogin()) {
            return null;
        }

        // 校验token
        Enumeration<String> headers = request.getHeaders("Magic-Api-Token");
        if (!headers.hasMoreElements()) {
            return new JsonBean<>(401, "用户未被授权");
        }
        String apiTokenOfHeaders = headers.nextElement();
        if (StringUtils.isBlank(apiTokenOfHeaders)) {
            return new JsonBean<>(401, "用户未被授权");
        }
        if (!apiToken.equals(apiTokenOfHeaders)) {
            return new JsonBean<>(401, "用户未被授权");
        }
        return null;
    }
}
