package com.contract.wechat.server.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class BaseEntity {
    @TableId(value = "id",type = IdType.AUTO)
    private int id;
    private LocalDateTime createdAt;
    private LocalDateTime  updatedAt;
    private LocalDateTime  deletedAt;
}
