package com.potato.template.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserToken implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 用户ID
     */
    @TableId(value = "user_id")
    private String userId;

    /**
     * token
     */
    @TableField(value = "token")
    private String token;
}
