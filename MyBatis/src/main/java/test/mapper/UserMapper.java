package test.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author Shmily
 * @Description:
 * @CreateTime: 2024/1/4 下午3:12
 * @Site: shmily-qjj.top
 */

@Mapper
public interface UserMapper {
    Map<String, Object> dynamicColumnsSelect(@Param("id") String userId, @Param("columnList") String columnList);

    // 如果返回值类型是Map<String, Object> 则只能查询一条数据
    // 如果返回值类型是List<Map<String, Object>> 则可以查多条数据
    List<Map<String, Object>> dynamicValuesSelect(@Param("userIds") List<String> userIds, @Param("columnList") String columnList);
}
