package io.github.zacoooo.weiboapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.github.zacoooo.weiboapi.entity.User;

/**
 * 用户接口层
 */
public interface UserService extends IService<User> {

    /**
     * 根据微博用户唯一ID（weibo_uid）保存或更新用户信息。
     * 如果数据库中已存在该 weibo_uid，则执行更新操作；
     * 否则执行插入操作。
     *
     * @param user 微博用户信息实体
     */
    void saveOrUpdateByWeiboUid(User user);

    /**
     * 根据 weiboUid 获取用户信息
     *
     * @param weiboUid 微博用户唯一ID
     * @return 用户实体
     */
    User getUserByWeiboUid(Long weiboUid);
}
