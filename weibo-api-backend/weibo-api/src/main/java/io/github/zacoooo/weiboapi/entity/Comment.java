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
 * 评论实体
 */
@Data
@Builder
@TableName("comment")
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 评论唯一ID
     */
    @TableField("comment_id")
    private Long commentId;

    /**
     * 微博唯一ID
     */
    @TableField("weibo_id")
    private Long weiboId;

    /**
     * 评论用户的微博唯一ID
     */
    @TableField("weibo_uid")
    private Long weiboUid;
    /**
     * 评论内容
     */
    @TableField("content")
    private String content;

    /**
     * 评论创建时间
     */
    @TableField("created_at")
    private Date createdAt;
}