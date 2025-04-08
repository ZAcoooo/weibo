package io.github.zacoooo.weiboapi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 微博用户实体
 */
@Data
@Builder
@TableName("user")
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 微博用户唯一ID
     */
    @TableField("weibo_uid")
    private Long weiboUid;

    /**
     * 微博昵称
     */
    @TableField("screen_name")
    private String screenName;

    /**
     * 用户头像地址
     */
    @TableField("avatar_url")
    private String avatarUrl;

    /**
     * 授权access_token
     */
    @TableField("access_token")
    private String accessToken;

    /**
     * access_token有效期(秒)
     */
    @TableField("expires_in")
    private Long expiresIn;

    /**
     * access_token获取时间
     */
    @TableField("token_create_time")
    private Date tokenCreateTime;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private Date createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;
}