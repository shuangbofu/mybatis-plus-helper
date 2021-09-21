package io.github.shuangbofu.helper.mybatis.handler;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.util.List;

/**
 * Created by shuangbofu on 2021/9/18 5:40 下午
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes({List.class})
public class StringListTypeHandler extends ListTypeHandler<String> {

    @Override
    protected List<String> str2List(String str) {
        return getStringList(str);
    }

    @Override
    protected String list2Str(List<String> list) {
        return strListToString(list);
    }
}
