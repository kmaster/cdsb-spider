package cn.kt.cdsbspider.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SbrecordMapper {


    @Insert("INSERT INTO sbrecord( name) VALUES( #{name})")
    int insert(@Param("name") String name);

}