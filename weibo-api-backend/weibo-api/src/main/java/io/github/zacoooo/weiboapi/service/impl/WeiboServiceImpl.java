package io.github.zacoooo.weiboapi.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.zacoooo.weiboapi.entity.Weibo;
import io.github.zacoooo.weiboapi.mapper.WeiboMapper;
import io.github.zacoooo.weiboapi.service.WeiboService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 微博接口实现层
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WeiboServiceImpl extends ServiceImpl<WeiboMapper, Weibo> implements WeiboService {

    @Resource
    private RestTemplate restTemplate;

    private static final SimpleDateFormat WEIBO_DATE_FORMAT =
            new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);

    private Date parseWeiboDate(String dateStr) {
        try {
            return WEIBO_DATE_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException("微博时间解析失败: " + dateStr, e);
        }
    }

    @Override
    public List<Weibo> fetchRecentWeiboPosts(String accessToken, Long weiboUid, int count, int page) {
        // 容错处理
        if (count <= 0 || count > 100) count = 20;
        if (page <= 0) page = 1;

        String url = String.format(
                "https://api.weibo.com/2/statuses/user_timeline.json?access_token=%s&count=%d&page=%d",
                accessToken, count, page
        );

        String response = restTemplate.getForObject(url, String.class);
        if (response == null) {
            throw new RuntimeException("获取微博数据失败");
        }

        JSONObject jsonResponse = JSONObject.parseObject(response);
        JSONArray statuses = jsonResponse.getJSONArray("statuses");

        List<Weibo> weiboList = new ArrayList<>();
        if (statuses != null) {
            for (Object item : statuses) {
                JSONObject weiboData = (JSONObject) item;
                Weibo weibo = new Weibo();
                weibo.setWeiboId(weiboData.getLong("id"));
                weibo.setContent(weiboData.getString("text"));
                weibo.setCreatedAt(parseWeiboDate(weiboData.getString("created_at")));
                weibo.setWeiboUid(weiboUid);
                weiboList.add(weibo);
            }
        }

        return weiboList;
    }

    @Override
    public void saveWeiboListToDB(List<Weibo> weiboList) {
        for (Weibo weibo : weiboList) {
            saveOrUpdate(weibo, new LambdaQueryWrapper<Weibo>()
                    .eq(Weibo::getWeiboId, weibo.getWeiboId()));
        }
    }

    @Override
    public List<Weibo> getRecentWeiboPosts(Long weiboUid, int count, int page) {
        if (count <= 0 || count > 100) count = 20;
        if (page <= 0) page = 1;
        int offset = (page - 1) * count;

        LambdaQueryWrapper<Weibo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Weibo::getWeiboUid, weiboUid)
                .orderByDesc(Weibo::getCreatedAt)
                .last("LIMIT " + offset + "," + count);

        return list(queryWrapper);
    }
}