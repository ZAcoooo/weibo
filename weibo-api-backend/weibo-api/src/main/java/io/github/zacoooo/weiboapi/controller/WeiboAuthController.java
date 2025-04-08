package io.github.zacoooo.weiboapi.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.github.zacoooo.weiboapi.entity.User;
import io.github.zacoooo.weiboapi.entity.Weibo;
import io.github.zacoooo.weiboapi.service.UserService;
import io.github.zacoooo.weiboapi.service.WeiboService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * 微博授权登录回调控制层
 */
@Slf4j
@RestController
public class WeiboAuthController {

    @Value("${weibo.client-id}")
    private String clientId;

    @Value("${weibo.client-secret}")
    private String clientSecret;

    @Value("${weibo.redirect-uri}")
    private String redirectUri;

    @Value("${weibo.front-redirect}")
    private String frontRedirect;

    @Value("${weibo.front-error}")
    private String frontError;

    @Resource
    private UserService userService;

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private WeiboService weiboService;

    /**
     * 微博登录回调处理
     * @param code 微博返回的授权码
     * @param response 重定向响应
     */
    @GetMapping("/oauth/weibo/callback")
    public void weiboCallback(@RequestParam("code") String code, HttpServletResponse response) throws IOException {
        log.info("收到微博授权code: {}", code);

        String tokenUrl = "https://api.weibo.com/oauth2/access_token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("grant_type", "authorization_code");
        params.add("code", code);
        params.add("redirect_uri", redirectUri);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> tokenResponse = restTemplate.postForEntity(tokenUrl, request, String.class);

        if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
            log.error("获取 access_token 失败: {}", tokenResponse.getBody());
            response.sendRedirect(frontError);
            return;
        }

        JSONObject tokenJson = JSONUtil.parseObj(tokenResponse.getBody());
        String accessToken = tokenJson.getStr("access_token");
        String uid = tokenJson.getStr("uid");
        Long expiresIn = tokenJson.getLong("expires_in");

        log.info("获取 access_token 成功: uid={}, token={}", uid, accessToken);

        String userInfoUrl = String.format("https://api.weibo.com/2/users/show.json?access_token=%s&uid=%s", accessToken, uid);
        String userInfoStr = restTemplate.getForObject(userInfoUrl, String.class);
        JSONObject userJson = JSONUtil.parseObj(userInfoStr);

        String screenName = userJson.getStr("screen_name");
        String avatarUrl = userJson.getStr("avatar_large");

        User user = User.builder()
                .weiboUid(Long.valueOf(uid))
                .accessToken(accessToken)
                .expiresIn(expiresIn)
                .tokenCreateTime(new Date())
                .screenName(screenName)
                .avatarUrl(avatarUrl)
                .build();

        userService.saveOrUpdateByWeiboUid(user);
        log.info("用户 {} 已保存或更新到数据库", screenName);

        try {
            List<Weibo> weiboList = weiboService.fetchRecentWeiboPosts(accessToken, Long.valueOf(uid), 20, 1);
            weiboService.saveWeiboListToDB(weiboList);
        } catch (Exception e) {
            log.warn("拉取微博失败: {}", e.getMessage());
        }

        String encodedName = URLEncoder.encode(screenName, StandardCharsets.UTF_8);
        String encodedAvatar = URLEncoder.encode(avatarUrl, StandardCharsets.UTF_8);
        String encodedToken = URLEncoder.encode(accessToken, StandardCharsets.UTF_8);
        String redirect = String.format("%s?screen_name=%s&avatar_url=%s&weibo_uid=%s&access_token=%s",
                frontRedirect,
                encodedName,
                encodedAvatar,
                uid,
                encodedToken);
        response.sendRedirect(redirect);
    }
}