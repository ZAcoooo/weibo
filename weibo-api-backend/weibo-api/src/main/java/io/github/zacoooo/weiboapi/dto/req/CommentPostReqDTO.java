package io.github.zacoooo.weiboapi.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微博评论创建请求对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentPostReqDTO {

    /**
     * 微博唯一ID
     */
    private Long weiboId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 授权token
     */
    private String accessToken;
}
