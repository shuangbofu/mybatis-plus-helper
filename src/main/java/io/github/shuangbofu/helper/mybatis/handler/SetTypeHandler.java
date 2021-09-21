package io.github.shuangbofu.helper.mybatis.handler;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by shuangbofu on 2021/9/18 5:55 下午
 */
public abstract class SetTypeHandler<T> implements TypeHandler<Set<T>> {

    protected abstract ListTypeHandler<T> getListTypeHandler();

    @Override
    public void setParameter(PreparedStatement ps, int i, Set<T> parameter, JdbcType jdbcType) throws SQLException {
        getListTypeHandler().setParameter(ps, i, toList(parameter), jdbcType);
    }

    private Set<T> newSet(List<T> list) {
        return new HashSet<>(list);
    }

    private List<T> toList(Set<T> set) {
        return new ArrayList<>(set);
    }

    @Override
    public Set<T> getResult(ResultSet rs, String columnName) throws SQLException {
        return newSet(getListTypeHandler().getResult(rs, columnName));
    }

    @Override
    public Set<T> getResult(ResultSet rs, int columnIndex) throws SQLException {
        return newSet(getListTypeHandler().getResult(rs, columnIndex));
    }

    @Override
    public Set<T> getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return newSet(getListTypeHandler().getResult(cs, columnIndex));
    }
}
