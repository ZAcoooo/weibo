package io.github.zacoooo.weiboapi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 微博实体
 */
@Data
@Builder
@TableName("weibo")
@NoArgsConstructor
@AllArgsConstructor
public class Weibo {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 微博唯一ID
     */
    @TableField("weibo_id")
    private Long weiboId;

    /**
     * 微博发布者用户UID
     */
    @TableField("weibo_uid")
    private Long weiboUid;

    /**
     * 微博内容
     */
    @TableField("content")
    private String content;

    /**
     * 微博创建时间
     */
    @TableField("created_at")
    private Date createdAt;
}