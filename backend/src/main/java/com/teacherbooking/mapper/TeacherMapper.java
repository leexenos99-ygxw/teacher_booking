package com.teacherbooking.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teacherbooking.entity.Teacher;
import org.apache.ibatis.annotations.Mapper;

/**
 * 教师Mapper
 */
@Mapper
public interface TeacherMapper extends BaseMapper<Teacher> {
}
