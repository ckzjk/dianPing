package com.hmdp.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.utils.RedisConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public List<ShopType> queryList() {

        String key = RedisConstants.CACHE_SHOP_TYPE_KEY;

        List<String> shopTypeJsonList = stringRedisTemplate.opsForList().range(key, 0, -1);

        if (CollUtil.isNotEmpty(shopTypeJsonList)) {
            return shopTypeJsonList.stream()
                    .map(json -> JSONUtil.toBean(json, ShopType.class))
                    .collect(Collectors.toList());
        }

        // 3. 查询数据库
        List<ShopType> shopTypes = query().orderByAsc("sort").list();

        // 4. 写入缓存
        if (CollUtil.isNotEmpty(shopTypes)) {
            List<String> cacheData = shopTypes.stream()
                    .map(JSONUtil::toJsonStr)
                    .collect(Collectors.toList());
            stringRedisTemplate.opsForList().rightPushAll(key, cacheData);
            stringRedisTemplate.expire(key, 30, TimeUnit.MINUTES);
        }

        return shopTypes;

    }
}
