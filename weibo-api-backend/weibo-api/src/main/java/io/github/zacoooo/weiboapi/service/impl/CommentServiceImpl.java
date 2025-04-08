package io.github.zacoooo.weiboapi.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.zacoooo.weiboapi.entity.Comment;
import io.github.zacoooo.weiboapi.mapper.CommentMapper;
import io.github.zacoooo.weiboapi.service.CommentService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 评论接口实现层
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Resource
    private RestTemplate restTemplate;

    private static final SimpleDateFormat WEIBO_DATE_FORMAT =
            new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);

    private Date parseWeiboDate(String dateStr) {
        try {
            return WEIBO_DATE_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException("评论时间解析失败: " + dateStr, e);
        }
    }

    @Override
    public List<Comment> fetchCommentsFromWeibo(String accessToken, Long weiboId, int count, int page) {
        if (count <= 0 || count > 200) count = 20;
        if (page < 1) page = 1;

        String url = String.format(
                "https://api.weibo.com/2/comments/show.json?access_token=%s&id=%s&count=%d&page=%d",
                accessToken, weiboId, count, page
        );

        String response = restTemplate.getForObject(url, String.class);

        if (response == null) {
            log.warn("微博评论接口返回为空 weiboId: {}", weiboId);
            return Collections.emptyList();
        }

        JSONObject jsonResponse = JSONObject.parseObject(response);
        JSONArray commentsArray = jsonResponse.getJSONArray("comments");

        if (commentsArray == null || commentsArray.isEmpty()) {
            log.info("微博 {} 没有评论", weiboId);
            return Collections.emptyList();
        }

        List<Comment> result = new ArrayList<>();
        for (Object obj : commentsArray) {
            JSONObject commentData = (JSONObject) obj;
            JSONObject userObj = commentData.getJSONObject("user");

            Comment comment = new Comment();
            comment.setCommentId(commentData.getLong("id"));
            comment.setWeiboId(weiboId);
            comment.setContent(commentData.getString("text"));
            comment.setWeiboUid(userObj.getLong("id"));
            comment.setCreatedAt(parseWeiboDate(commentData.getString("created_at")));

            result.add(comment);
        }

        return result;
    }

    @Override
    public void saveCommentListToDB(List<Comment> commentList) {
        for (Comment comment : commentList) {
            saveOrUpdate(comment, new LambdaQueryWrapper<Comment>()
                    .eq(Comment::getCommentId, comment.getCommentId()));
        }
    }

    @Override
    public List<Comment> getRecentWeiboComments(Long weiboId, int count, int page) {
        if (count <= 0 || count > 100) count = 10;
        if (page < 1) page = 1;
        int offset = (page - 1) * count;

        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getWeiboId, weiboId)
                .orderByDesc(Comment::getCreatedAt)
                .last("LIMIT " + offset + "," + count);

        return list(queryWrapper);
    }

    @Override
    public void postComment(String accessToken, Long weiboId, String content, HttpServletRequest request) {
        String url = "https://api.weibo.com/2/comments/create.json";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String clientIp = getClientIp(request);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("access_token", accessToken);
        body.add("id", String.valueOf(weiboId));
        body.add("comment", URLEncoder.encode(content, StandardCharsets.UTF_8));
        body.add("rip", clientIp);

        HttpEntity<MultiValueMap<String, String>> httpRequest = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, httpRequest, String.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("微博评论发送失败");
        }

        JSONObject responseJson = JSONObject.parseObject(response.getBody());
        JSONObject userJson = responseJson.getJSONObject("user");

        Comment comment = new Comment();
        comment.setCommentId(responseJson.getLong("id"));
        comment.setWeiboId(weiboId);
        comment.setContent(responseJson.getString("text"));
        comment.setWeiboUid(userJson.getLong("id"));
        comment.setCreatedAt(parseWeiboDate(responseJson.getString("created_at")));

        save(comment);
        log.info("微博评论发送成功，评论ID: {}", comment.getCommentId());
    }

    private String getClientIp(HttpServletRequest request) {
        String[] headers = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP",
                "HTTP_X_FORWARDED_FOR"
        };

        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }
        return request.getRemoteAddr();
    }
}