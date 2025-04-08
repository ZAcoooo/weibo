package io.github.zacoooo.weiboapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.github.zacoooo.weiboapi.entity.Weibo;

import java.util.List;

/**
 * 微博接口层
 */
public interface WeiboService extends IService<Weibo> {

    /**
     * 从微博开放平台获取指定用户的微博列表（分页）
     *
     * @param accessToken 微博用户授权 token
     * @param weiboUid 微博用户唯一ID
     * @param count 每页获取的微博数量（最大100）
     * @param page 微博列表的页码（从1开始）
     * @return 微博列表
     */
    List<Weibo> fetchRecentWeiboPosts(String accessToken, Long weiboUid, int count, int page);

    /**
     * 从数据库中分页获取指定用户的微博列表
     *
     * @param weiboUid 微博用户唯一ID
     * @param count 每页微博数量
     * @param page 页码
     * @return 微博列表
     */
    List<Weibo> getRecentWeiboPosts(Long weiboUid, int count, int page);

    /**
     * 批量保存微博列表到数据库，已存在的微博将自动更新
     *
     * @param weiboList 微博列表
     */
    void saveWeiboListToDB(List<Weibo> weiboList);
}