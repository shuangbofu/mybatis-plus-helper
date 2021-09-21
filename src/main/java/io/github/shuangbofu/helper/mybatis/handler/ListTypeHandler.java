package io.github.shuangbofu.helper.mybatis.handler;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class ListTypeHandler<T> implements TypeHandler<List<T>> {

    protected String getSplitToken() {
        return ",";
    }

    @Override
    public void setParameter(PreparedStatement ps, int i,
                             List<T> parameter,
                             JdbcType jdbcType) throws SQLException {
        ps.setString(i, Optional.ofNullable(parameter)
                .filter(j -> !j.isEmpty())
                .map(this::list2Str)
                .orElse(""));
    }

    protected abstract List<T> str2List(String str);

    protected abstract String list2Str(List<T> list);

    @Override
    public List<T> getResult(ResultSet rs, String columnName) throws SQLException {
        return parseResult(rs.getString(columnName));
    }

    @Override
    public List<T> getResult(ResultSet rs, int columnIndex) throws SQLException {
        return parseResult(rs.getString(columnIndex));
    }

    @Override
    public List<T> getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parseResult(cs.getString(columnIndex));
    }

    private List<T> parseResult(String text) {
        return Optional.ofNullable(text)
                .map(this::str2List)
                .filter(i -> !i.isEmpty())
                .orElse(new ArrayList<>());
    }

    protected List<String> getStringList(String str) {
        return Arrays.stream(str.split(getSplitToken()))
                .filter(i -> !i.isEmpty())
                .collect(Collectors.toList());
    }

    protected String strListToString(List<String> list) {
        return list.stream()
                .filter(i -> !i.isEmpty())
                .collect(Collectors.joining(getSplitToken()));
    }
}
