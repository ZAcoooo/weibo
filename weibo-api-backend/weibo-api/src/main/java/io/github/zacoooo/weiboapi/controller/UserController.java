package io.github.zacoooo.weiboapi.controller;

import io.github.zacoooo.weiboapi.entity.User;
import io.github.zacoooo.weiboapi.service.UserService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

/**
 * 用户管理控制层
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    @Resource
    private final UserService userService;

    /**
     * 根据 weiboUid 获取用户信息
     *
     * @param weiboUid 微博用户唯一ID
     * @return 用户实体
     */
    @GetMapping("/{weiboUid}")
    public ResponseEntity<?> getUserInfo(@PathVariable Long weiboUid) {
        User user = userService.getUserByWeiboUid(weiboUid);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", "用户不存在"));        }
        return ResponseEntity.ok(user);
    }
}
