package cn.kt.cdsbspider.dao;

import cn.kt.cdsbspider.domain.Sbrecord;
import cn.kt.cdsbspider.domain.SbrecordExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SbrecordDao {
    long countByExample(SbrecordExample example);

    int deleteByExample(SbrecordExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Sbrecord record);

    int insertSelective(Sbrecord record);

    List<Sbrecord> selectByExample(SbrecordExample example);

    Sbrecord selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Sbrecord record, @Param("example") SbrecordExample example);

    int updateByExample(@Param("record") Sbrecord record, @Param("example") SbrecordExample example);

    int updateByPrimaryKeySelective(Sbrecord record);

    int updateByPrimaryKey(Sbrecord record);
}