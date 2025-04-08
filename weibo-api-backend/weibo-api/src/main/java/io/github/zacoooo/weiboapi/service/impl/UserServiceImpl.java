package io.github.zacoooo.weiboapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.zacoooo.weiboapi.mapper.UserMapper;
import io.github.zacoooo.weiboapi.service.UserService;
import io.github.zacoooo.weiboapi.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 用户接口实现层
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Override
    public void saveOrUpdateByWeiboUid(User user) {
        LambdaQueryWrapper<User> queryWrapper = Wrappers.lambdaQuery(User.class)
                .eq(User::getWeiboUid, user.getWeiboUid());

        // 查询数据库中是否存在该用户 weibo_uid
        User existingUser = baseMapper.selectOne(queryWrapper);

        if (existingUser == null) {
            baseMapper.insert(user);
        } else {
            user.setId(existingUser.getId());
            user.setCreatedAt(existingUser.getCreatedAt());
            baseMapper.updateById(user);
        }
    }

    @Override
    public User getUserByWeiboUid(Long weiboUid) {
        // 使用 lambdaQueryWrapper 进行查询
        LambdaQueryWrapper<User> queryWrapper = Wrappers.lambdaQuery(User.class)
                .eq(User::getWeiboUid, weiboUid);
        return baseMapper.selectOne(queryWrapper); // 通过 baseMapper 执行查询
    }
}
