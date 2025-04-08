package io.github.zacoooo.weiboapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.github.zacoooo.weiboapi.entity.Comment;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 评论接口层
 */
public interface CommentService extends IService<Comment> {

    /**
     * 调用微博开放平台接口，获取指定微博的评论列表（分页）
     *
     * @param accessToken 微博授权token
     * @param weiboId 微博ID
     * @param count 评论数量（最大200）
     * @param page 页码（从1开始）
     * @return 评论列表
     */
    List<Comment> fetchCommentsFromWeibo(String accessToken, Long weiboId, int count, int page);

    /**
     * 批量保存评论到数据库，已存在的自动更新
     *
     * @param commentList 评论列表
     */
    void saveCommentListToDB(List<Comment> commentList);

    /**
     * 获取数据库中指定微博的评论（分页）
     *
     * @param weiboId 微博ID
     * @param count 每页数量
     * @param page 页码
     * @return 评论列表
     */
    List<Comment> getRecentWeiboComments(Long weiboId, int count, int page);

    /**
     * 对指定微博发表评论
     *
     * @param accessToken 微博授权token
     * @param weiboId 微博ID
     * @param content 评论内容
     * @param request 请求对象，用于获取客户端IP
     */
    void postComment(String accessToken, Long weiboId, String content, HttpServletRequest request);
}