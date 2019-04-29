package com.contract.wechat.sgin.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class BaseEntity {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    private LocalDateTime createdAt;
    private LocalDateTime  updatedAt;
    private LocalDateTime  deletedAt;
}
