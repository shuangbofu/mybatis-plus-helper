package io.github.shuangbofu.helper.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import io.github.shuangbofu.helper.entity.IdEntity;
import io.github.shuangbofu.helper.exception.DaoException;
import io.github.shuangbofu.helper.handler.QueryHandler;
import io.github.shuangbofu.helper.handler.UpdateHandler;
import io.github.shuangbofu.helper.hook.DaoHook;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class AbstractDao<ENTITY extends IdEntity, MAPPER extends BaseMapper<ENTITY>> implements BaseDao<ENTITY, MAPPER> {

    private static final String LIMIT_1 = " limit 1 ";
    protected final List<DaoHook<ENTITY>> daoHooks;
    private final MAPPER mapper;
    private final QueryHandler<ENTITY> QUERY_HANDLER_EMPTY = i -> {
    };

    public AbstractDao(List<DaoHook<ENTITY>> daoHooks, MAPPER mapper) {
        this.mapper = mapper;
        this.daoHooks = daoHooks;
    }

    protected QueryWrapper<ENTITY> wrapperByDefaultQuery(QueryHandler<ENTITY> queryHandler) {
        var qw = new QueryWrapper<ENTITY>();
        queryHandler.accept(qw);
        daoHooks.forEach(i -> i.defaultQuery(qw));
        return qw;
    }

    protected UpdateWrapper<ENTITY> wrapperByDefaultUpdate(UpdateHandler<ENTITY> updateHandler) {
        var uw = new UpdateWrapper<ENTITY>();
        updateHandler.accept(uw);
        daoHooks.forEach(i -> i.defaultUpdate(uw));
        return uw;
    }

    @Override
    public MAPPER getMapper() {
        return mapper;
    }

    @Override
    public long countBy(QueryHandler<ENTITY> queryHandler) {
        var qw = wrapperByDefaultQuery(queryHandler);
        return Optional.ofNullable(mapper.selectCount(qw))
                .orElse(0);
    }

    @Override
    public long countAll() {
        return countBy(QUERY_HANDLER_EMPTY);
    }

    @Override
    public List<ENTITY> selectAll() {
        return selectListBy(QUERY_HANDLER_EMPTY);
    }

    @Override
    public List<ENTITY> selectListBy(QueryHandler<ENTITY> queryHandler) {
        var qw = wrapperByDefaultQuery(queryHandler);
        return mapper.selectList(qw);
    }

    @Override
    public List<ENTITY> selectListInIds(List<Long> ids) {
        if (ids == null || ids.size() == 0) {
            return new ArrayList<>();
        }
        return selectListBy(i -> i.in(IdEntity.ID, ids));
    }

    @Override
    public ENTITY selectOneBy(QueryHandler<ENTITY> queryHandler) {
        var qw = wrapperByDefaultQuery(queryHandler);
        return mapper.selectOne(qw.last(LIMIT_1));
    }

    @Override
    public Optional<ENTITY> selectOneOptionalById(Long id) {
        return Optional.ofNullable(selectOneById(id));
    }

    @Override
    public <E extends RuntimeException> void existThrow(QueryHandler<ENTITY> queryHandler, Supplier<E> exceptionSupplier) throws DaoException {
        long count = countBy(queryHandler);
        Optional.of(count).filter(i -> count == 0).orElseThrow(exceptionSupplier);
    }

    @Override
    public <E extends RuntimeException> void notExistThrow(QueryHandler<ENTITY> queryHandler, Supplier<E> exceptionSupplier) throws DaoException {
        long count = countBy(queryHandler);
        Optional.of(count).filter(i -> count > 0).orElseThrow(exceptionSupplier);
    }

    @Override
    public ENTITY selectOneById(Long id) {
        return selectOneBy(q -> q.eq(IdEntity.ID, id));
    }

    @Override
    public <VALUE> VALUE selectValueBy(SFunction<ENTITY, VALUE> sFunction, QueryHandler<ENTITY> queryHandler, Supplier<VALUE> supplier) {
        ENTITY ENTITY = selectOneBy(q -> {
            queryHandler.accept(q);
            q.lambda().select(sFunction);
        });
        return Optional.ofNullable(ENTITY)
                .map(sFunction)
                .orElseGet(Optional.ofNullable(supplier).orElse(() -> null));
    }

    @Override
    public <VALUE> VALUE selectValueById(SFunction<ENTITY, VALUE> sFunction, Long id, Supplier<VALUE> supplier) {
        return selectValueBy(sFunction, q -> q.eq("id", id), supplier);
    }

    @Override
    public <VALUE> VALUE selectValueBy(SFunction<ENTITY, VALUE> sFunction, QueryHandler<ENTITY> queryHandler) {
        return selectValueBy(sFunction, queryHandler, () -> null);
    }

    @Override
    public <VALUE> VALUE selectValueById(SFunction<ENTITY, VALUE> sFunction, Long id) {
        return selectValueById(sFunction, id, () -> null);
    }

    @Override
    public <VALUE> List<VALUE> selectValueListBy(SFunction<ENTITY, VALUE> sFunction, QueryHandler<ENTITY> queryHandler) {
        List<ENTITY> ENTITIES = selectListBy(i -> {
            i.lambda().select(sFunction);
            queryHandler.accept(i);
        });
        if (ENTITIES != null) {
            return ENTITIES.stream()
                    .map(sFunction)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public int updateBy(UpdateHandler<ENTITY> updateHandler) {
        return updateEntityBy(null, updateHandler);
    }

    @Override
    public int updateById(Long id, UpdateHandler<ENTITY> updateHandler) {
        return updateBy(i -> {
            i.eq(IdEntity.ID, id);
            updateHandler.accept(i);
        });
    }

    @Override
    public int updateEntityBy(ENTITY entity, UpdateHandler<ENTITY> updateHandler) {
        var uw = wrapperByDefaultUpdate(updateHandler);
        return mapper.update(entity, uw);
    }

    @Override
    public int updateEntityById(ENTITY entity, Long id) {
        return updateEntityBy(entity, i -> i.eq(IdEntity.ID, id));
    }

    @Override
    public int updateEntityById(ENTITY entity) {
        if (entity == null) {
            return 0;
        }
        return updateEntityById(entity, entity.getId());
    }

    @Override
    public <V> int updateValueById(Long id, SFunction<ENTITY, V> sFunction, V value) {
        return updateById(id, i -> i.lambda().set(sFunction, value));
    }

    @Override
    public int deleteBy(UpdateHandler<ENTITY> updateHandler) {
        var uw = wrapperByDefaultUpdate(updateHandler);
        return mapper.delete(uw);
    }

    @Override
    public int deleteById(Long id) {
        return deleteBy(i -> i.eq(IdEntity.ID, id));
    }

    @Override
    public int deleteBatchIds(List<Long> ids) {
        if (ids == null || ids.size() == 0) {
            return 0;
        }
        return deleteBy(i -> i.in(IdEntity.ID, ids));
    }

    @Override
    public Map<String, Object> selectMap(QueryHandler<ENTITY> handler) {
        return Optional.ofNullable(selectMapList(i -> {
                    i.last(LIMIT_1);
                    handler.accept(i);
                })).filter(i -> i.size() > 0)
                .map(i -> i.get(0)).orElse(null);
    }

    @Override
    public List<Map<String, Object>> selectMapList(QueryHandler<ENTITY> queryHandler) {
        var qw = wrapperByDefaultQuery(queryHandler);
        return mapper.selectMaps(qw);
    }

    @Override
    public int insert(ENTITY entity) {
        daoHooks.forEach(i -> i.insert(entity));
        return mapper.insert(entity);
    }

    @Override
    @Transactional
    public int batchInsert(List<ENTITY> entities) {
        return entities.stream().map(this::insert)
                .reduce(0, Integer::sum);
    }

    @Override
    public Page<ENTITY> selectPage(int pageNum, int pageSize, QueryHandler<ENTITY> queryHandler) {
        var qw = wrapperByDefaultQuery(queryHandler);
        return mapper.selectPage(PageDTO.of(pageNum, pageSize), qw);
    }

    @Override
    public Page<Map<String, Object>> selectMapPage(int pageNum, int pageSize, QueryHandler<ENTITY> queryHandler) {
        var qw = wrapperByDefaultQuery(queryHandler);
        return mapper.selectMapsPage(PageDTO.of(pageNum, pageSize), qw);
    }
}
