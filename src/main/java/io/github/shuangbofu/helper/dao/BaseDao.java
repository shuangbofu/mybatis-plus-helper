package io.github.shuangbofu.helper.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.shuangbofu.helper.entity.IdEntity;
import io.github.shuangbofu.helper.handler.QueryHandler;
import io.github.shuangbofu.helper.handler.UpdateHandler;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public interface BaseDao<ENTITY extends IdEntity, MAPPER extends BaseMapper<ENTITY>> {

    /**
     * 根据id集合查询多条记录
     *
     * @param ids ids
     * @return 多条记录
     */
    List<ENTITY> selectListInIds(List<Long> ids);

    /**
     * 根据条件查找单条记录
     *
     * @param queryHandler 查询条件处理
     * @return 单条记录
     */
    ENTITY selectOneBy(QueryHandler<ENTITY> queryHandler);

    /**
     * 根据id查找单条记录
     *
     * @param id id
     * @return 单条记录
     */
    ENTITY selectOneById(Long id);

    /**
     * 根据条件计算数量
     *
     * @param queryHandler 查询条件处理
     * @return 数量
     */
    long countBy(QueryHandler<ENTITY> queryHandler);

    /**
     * 计算总数
     *
     * @return 数量
     */
    long countAll();

    /**
     * 根据条件查找多条记录
     *
     * @param queryHandler 查询条件处理
     * @return 记录集合
     */
    List<ENTITY> selectListBy(QueryHandler<ENTITY> queryHandler);

    /**
     * 查找全部记录
     *
     * @return 记录集合
     */
    List<ENTITY> selectAll();


    /**
     * 根据条件查找值集合
     *
     * @param sFunction    支持序列化的lambda方法
     * @param queryHandler 查询条件处理
     * @param <VALUE>      值类型
     * @return 值的集合
     */
    <VALUE> List<VALUE> selectValueListBy(SFunction<ENTITY, VALUE> sFunction, QueryHandler<ENTITY> queryHandler);

    /**
     * 根据条件查找值
     *
     * @param sFunction    支持序列化的lambda方法
     * @param queryHandler 查询条件处理
     * @param <VALUE>      值类型
     * @return 值
     */
    <VALUE> VALUE selectValueBy(SFunction<ENTITY, VALUE> sFunction, QueryHandler<ENTITY> queryHandler, Supplier<VALUE> supplier);

    <VALUE> VALUE selectValueBy(SFunction<ENTITY, VALUE> sFunction, QueryHandler<ENTITY> queryHandler);

    /**
     * 根据id查找值
     *
     * @param sFunction 支持序列化的lambda方法
     * @param id        id
     * @param <VALUE>   值类型
     * @return 值
     */
    <VALUE> VALUE selectValueById(SFunction<ENTITY, VALUE> sFunction, Long id, Supplier<VALUE> supplier);

    <VALUE> VALUE selectValueById(SFunction<ENTITY, VALUE> sFunction, Long id);

    /**
     * 根据条件更新
     *
     * @param updateHandler 更新条件及set值的处理
     * @return 更新影响条数
     */
    int updateBy(UpdateHandler<ENTITY> updateHandler);

    /**
     * 根据id更新
     *
     * @param id            id
     * @param updateHandler 更新条件及set值的处理
     * @return 更新影响条数
     */
    int updateById(Long id, UpdateHandler<ENTITY> updateHandler);

    /**
     * 根据条件更新实体
     *
     * @param entity        实体
     * @param updateHandler 更新条件及set值的处理
     * @return 更新影响条数
     */
    int updateEntityBy(ENTITY entity, UpdateHandler<ENTITY> updateHandler);

    /**
     * 根据id更新实体
     *
     * @param entity 实体
     * @param id     id
     * @return 更新影响条数
     */
    int updateEntityById(ENTITY entity, Long id);

    /**
     * 根据id更新值
     *
     * @param id        id
     * @param sFunction 支持序列化的lambda方法
     * @param value     值
     * @param <VALUE>   值类型
     * @return 更新影响条数
     */
    <VALUE> int updateValueById(Long id, SFunction<ENTITY, VALUE> sFunction, VALUE value);

    /**
     * 根据条件删除
     *
     * @param handler 条件处理（使用update handler是为了逻辑删除可以用同一个接口，只要handler里不set值就没问题）
     * @return 删除影响条数
     */
    int deleteBy(UpdateHandler<ENTITY> handler);

    /**
     * 根据id删除
     *
     * @param id id
     * @return 删除影响条数
     */
    int deleteById(Long id);

    /**
     * 根据id批量删除
     *
     * @param ids id集合
     * @return 删除影响条数
     */
    int deleteBatchIds(List<Long> ids);

    /**
     * 获取dao对应的mybatis mapper（非特殊情况不建议使用，不带有默认条件）
     *
     * @return mybatis-plus的baseMapper
     */
    MAPPER getMapper();

    /**
     * 根据条件查询任意格式单条记录
     *
     * @param handler 查询条件处理
     * @return 单条原始sql行列数据
     */
    Map<String, Object> selectMap(QueryHandler<ENTITY> handler);

    /**
     * 根据条件查询任意格式多条记录
     *
     * @param handler 查询条件处理
     * @return 多条原始sql行列数据
     */
    List<Map<String, Object>> selectMapList(QueryHandler<ENTITY> handler);

    /**
     * 插入实体对象
     *
     * @param entity 实体
     * @return 插入影响条数
     */
    int insert(ENTITY entity);

    /**
     * 批量插入实体对象
     *
     * @param entities 实体列表
     * @return 插入影响条数
     */
    @Transactional
    int batchInsert(List<ENTITY> entities);

    /**
     * 根据条件分页查询
     *
     * @param pageNum  第n页
     * @param pageSize 页数
     * @param handler  查询条件处理
     * @return 分页对象
     */
    Page<ENTITY> selectPage(int pageNum, int pageSize, QueryHandler<ENTITY> handler);

    /**
     * 根据条件查询任意格式分页记录
     *
     * @param pageNum  第n页
     * @param pageSize 页数
     * @param handler  查询条件处理
     * @return 分页对象
     */
    Page<Map<String, Object>> selectMapPage(int pageNum, int pageSize, QueryHandler<ENTITY> handler);
}
