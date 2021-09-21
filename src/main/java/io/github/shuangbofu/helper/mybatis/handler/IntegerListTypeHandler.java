package io.github.shuangbofu.helper.mybatis.handler;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.util.List;

/**
 * Created by shuangbofu on 2021/9/18 5:54 下午
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes({List.class})
public class IntegerListTypeHandler extends NumberListTypeHandler<Integer> {
    @Override
    protected Integer str2Num(String str) {
        return Integer.parseInt(str);
    }

    @Override
    protected String num2Str(Integer number) {
        return number.toString();
    }
}
