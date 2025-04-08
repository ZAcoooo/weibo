package io.github.zacoooo.weiboapi.controller;

import io.github.zacoooo.weiboapi.entity.Weibo;
import io.github.zacoooo.weiboapi.service.WeiboService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 微博控制层
 */
@Slf4j
@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class WeiboController {

    @Resource
    private final WeiboService weiboService;

    /**
     * 分页获取指定用户的微博（从数据库中）
     *
     * @param weiboUid 微博用户唯一ID
     * @param count 每页微博数量（最大100，默认20）
     * @param page 页码（默认1）
     * @return 用户微博列表
     */
    @GetMapping("/{weiboUid}")
    public ResponseEntity<?> getRecentWeiboPosts(
            @PathVariable Long weiboUid,
            @RequestParam(defaultValue = "20") int count,
            @RequestParam(defaultValue = "1") int page) {
        try {
            List<Weibo> weibos = weiboService.getRecentWeiboPosts(weiboUid, count, page);
            return ResponseEntity.ok(weibos);
        } catch (Exception e) {
            log.error("获取微博列表失败", e);
            return ResponseEntity.status(500).body("获取微博列表失败: " + e.getMessage());
        }
    }

    /**
     * 从微博开放平台拉取指定用户的微博（不落库，仅展示）
     *
     * @param accessToken 微博授权 token
     * @param weiboUid 微博用户唯一ID
     * @param count 每页拉取数量（最大100，默认20）
     * @param page 页码（默认1）
     * @return 拉取的微博列表
     */
    @GetMapping("/fetch")
    public ResponseEntity<?> fetchWeiboPostsFromWeiboApi(
            @RequestParam String accessToken,
            @RequestParam Long weiboUid,
            @RequestParam(defaultValue = "20") int count,
            @RequestParam(defaultValue = "1") int page) {
        try {
            List<Weibo> list = weiboService.fetchRecentWeiboPosts(accessToken, weiboUid, count, page);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            log.error("从微博API拉取微博失败", e);
            return ResponseEntity.status(500).body("微博拉取失败: " + e.getMessage());
        }
    }
}