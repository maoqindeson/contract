
package com.contract.wechat.sgin.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.contract.wechat.sgin.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017-03-23 15:22:06
 */
@Mapper
public interface UserDao extends BaseMapper<UserEntity> {

}
