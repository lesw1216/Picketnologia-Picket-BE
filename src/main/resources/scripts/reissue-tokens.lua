local previousRefreshToken = KEYS[1]
local reissuedRefreshToken = KEYS[2]

local requestAccessToken = ARGV[1]
local reissuedAccessToken = ARGV[2]
local refreshTokenExpireMillis = ARGV[3]

-- 기존 refresh token이 존재하는지 확인
local storedAccessToken = redis.call('GET', previousRefreshToken)

-- 없다면 재발급 실패 return 0
if not storedAccessToken then
    return 0
end

-- redis에 저장된 access token과 쿠키로 넘어온 access token이 다른 경우 재발급 실패, redis에서 삭제 후 return -1
if storedAccessToken ~= requestAccessToken then
    redis.call('DEL', previousRefreshToken)
    return -1
end

-- 정상 로직, 이전 refresh 키 삭제, 새로운 refresh, access token 저장
redis.call('DEL', previousRefreshToken)
redis.call('SET', reissuedRefreshToken, reissuedAccessToken, 'PX', refreshTokenExpireMillis)

return 1
