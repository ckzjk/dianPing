local voucherId = ARGV[1]

local userId = ARGV[2]

local stockKey ='seckill:stock:' .. voucherId

local orderKey ='seckill:order:' .. voucherId

if(tonumber(redis.call('get',stockKey))<=0) then
    return 1
end

if(redis.call('sismember',orderKey,userId)==1) then
    return 2
end

redis.call('incrby',stockKey,-1)
redis.call('sadd',orderKey,userId)
--redis.call('xadd', 'stream.orders', '*', 'userId', userId, 'voucherId', voucherId, 'id', orderId)
return 0

--[[local voucherId = ARGV[1]
local userId = ARGV[2]
local stockKey = 'seckill:stock:' .. voucherId
local orderKey = 'seckill:order:' .. voucherId

-- 获取库存，如果不存在则默认为0
local stock = redis.call('get', stockKey)
if stock == false or stock == nil then
    return 1  -- 库存不存在，返回失败
end

-- 转换为数字并判断
stock = tonumber(stock)
if stock <= 0 then
    return 1  -- 库存不足
end

-- 判断是否已购买
if redis.call('sismember', orderKey, userId) == 1 then
    return 2  -- 已购买
end

-- 扣减库存并记录订单
redis.call('incrby', stockKey, -1)
redis.call('sadd', orderKey, userId)
return 0  -- 成功]]
