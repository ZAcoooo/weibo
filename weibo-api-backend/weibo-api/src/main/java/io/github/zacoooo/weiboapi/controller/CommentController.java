package io.github.zacoooo.weiboapi.controller;

import io.github.zacoooo.weiboapi.dto.req.CommentPostReqDTO;
import io.github.zacoooo.weiboapi.entity.Comment;
import io.github.zacoooo.weiboapi.service.CommentService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论管理控制层
 */
@Slf4j
@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {

    @Resource
    private CommentService commentService;

    /**
     * 获取指定微博的最新评论（分页）
     *
     * @param weiboId 微博唯一ID
     * @param count 每页评论数（默认10）
     * @param page 评论页码（默认1）
     * @return 评论列表
     */
    @GetMapping("/{weiboId}")
    public ResponseEntity<?> getComments(
            @PathVariable Long weiboId,
            @RequestParam(defaultValue = "10") int count,
            @RequestParam(defaultValue = "1") int page) {
        try {
            List<Comment> comments = commentService.getRecentWeiboComments(weiboId, count, page);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            log.error("获取微博评论失败", e);
            return ResponseEntity.status(500).body("获取评论失败: " + e.getMessage());
        }
    }

    /**
     * 从微博开放平台接口获取指定微博的评论列表（不保存，仅返回）
     *
     * @param accessToken 微博授权 token
     * @param weiboId 微博唯一ID
     * @param count 每页数量（默认10，最大200）
     * @param page 页码（默认1）
     * @return 评论列表
     */
    @GetMapping("/fetch")
    public ResponseEntity<?> fetchCommentsFromWeiboApi(
            @RequestParam String accessToken,
            @RequestParam Long weiboId,
            @RequestParam(defaultValue = "10") int count,
            @RequestParam(defaultValue = "1") int page) {
        try {
            List<Comment> comments = commentService.fetchCommentsFromWeibo(accessToken, weiboId, count, page);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            log.error("从微博API拉取评论失败", e);
            return ResponseEntity.status(500).body("拉取失败: " + e.getMessage());
        }
    }

    /**
     * 用户对指定微博发表评论
     *
     * @param req 评论请求参数，包括微博ID、评论内容、用户accessToken
     * @param request HttpServletRequest对象，用于获取客户端真实IP
     * @return 返回评论结果，成功返回200和消息，失败返回500和错误信息
     */
    @PostMapping("/create")
    public ResponseEntity<?> postComment(@RequestBody CommentPostReqDTO req, HttpServletRequest request) {
        try {
            commentService.postComment(req.getAccessToken(), req.getWeiboId(), req.getContent(), request);
            return ResponseEntity.ok("评论成功");
        } catch (Exception e) {
            log.error("发表评论失败", e);
            return ResponseEntity.status(500).body("评论失败: " + e.getMessage());
        }
    }
}