local key = KEYS[1]

local now = tonumber(ARGV[1])
local window = tonumber(ARGV[2])
local limit = tonumber(ARGV[3])
local requestId = ARGV[4]
local startTime = now - window

redis.call('ZREMRANGEBYSCORE', key, '-inf', startTime)

local count = redis.call('ZCARD', key)

if count >= limit then

    return 0
end


redis.call('ZADD', key, now, requestId)

redis.call('PEXPIRE', key, window)

return 1