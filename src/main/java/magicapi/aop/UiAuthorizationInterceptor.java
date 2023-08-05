package magicapi.aop;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.ssssssss.magicapi.core.context.MagicUser;
import org.ssssssss.magicapi.core.exception.MagicLoginException;
import org.ssssssss.magicapi.core.interceptor.AuthorizationInterceptor;
import org.ssssssss.magicapi.utils.MD5Utils;

import java.util.Objects;

@Component
public class UiAuthorizationInterceptor implements AuthorizationInterceptor {
    private final boolean requireLogin;
    private String validToken;

    public UiAuthorizationInterceptor(@Value("${magic-api.security.username}") String username,
                                      @Value("${magic-api.security.password}") String password) {
        this.requireLogin = StringUtils.isNoneBlank(username, password);
        if (requireLogin) {
            this.validToken = MD5Utils.encrypt(String.format("%s||%s", username, password));
        }
    }

    public boolean requireLogin() {
        return this.requireLogin;
    }

    public MagicUser getUserByToken(String token) throws MagicLoginException {
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        if (tokenInfo != null && tokenInfo.getLoginId() != null) {
            return new MagicUser(tokenInfo.getLoginId().toString(), tokenInfo.getLoginId().toString(), tokenInfo.getTokenValue());
        } else {
            throw new MagicLoginException("token无效");
        }
    }

    public MagicUser login(String username, String password) throws MagicLoginException {
        if (Objects.equals(MD5Utils.encrypt(String.format("%s||%s", username, MD5Utils.encrypt(password))), this.validToken)) {
            StpUtil.login(username);
            return new MagicUser(username, username, StpUtil.getTokenInfo().getTokenValue());
        } else {
            throw new MagicLoginException("用户名或密码不正确");
        }
    }

    @Override
    public void logout(String token) {
        StpUtil.logout();
    }
}
