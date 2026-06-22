package magicapi.aop;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.ssssssss.magicapi.core.context.RequestEntity;
import org.ssssssss.magicapi.core.interceptor.RequestInterceptor;
import org.ssssssss.magicapi.core.interceptor.ResultProvider;
import org.ssssssss.magicapi.core.model.ApiInfo;
import org.ssssssss.magicapi.core.model.Options;
import org.ssssssss.magicapi.core.servlet.MagicHttpServletRequest;
import org.ssssssss.magicapi.core.servlet.MagicHttpServletResponse;
import org.ssssssss.script.MagicScriptContext;

import java.util.Enumeration;

@Component
public class ApiRequestInterceptor implements RequestInterceptor {
    @Value("${api.token-header:Magic-Api-Token}")
    private String apiTokenHeader;
    @Value("${api.token:}")
    private String apiToken;
    @Autowired
    private ResultProvider resultProvider;

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

        // 校验token
        Enumeration<String> headers = request.getHeaders(apiTokenHeader);
        if (!headers.hasMoreElements()) {
            RequestEntity requestEntity = RequestEntity.create();
            requestEntity.info(info);
            requestEntity.setMagicScriptContext(context);
            requestEntity.request(request);
            requestEntity.response(response);
            return resultProvider.buildResult(requestEntity, -1, "用户未被授权");
        }
        String apiTokenOfHeaders = headers.nextElement();
        if (StringUtils.isBlank(apiTokenOfHeaders)) {
            RequestEntity requestEntity = RequestEntity.create();
            requestEntity.info(info);
            requestEntity.setMagicScriptContext(context);
            requestEntity.request(request);
            requestEntity.response(response);
            return resultProvider.buildResult(requestEntity, -1, "用户未被授权");
        }
        if (!apiToken.equals(apiTokenOfHeaders)) {
            RequestEntity requestEntity = RequestEntity.create();
            requestEntity.info(info);
            requestEntity.setMagicScriptContext(context);
            requestEntity.request(request);
            requestEntity.response(response);
            return resultProvider.buildResult(requestEntity, -1, "用户未被授权");
        }
        return null;
    }
}
