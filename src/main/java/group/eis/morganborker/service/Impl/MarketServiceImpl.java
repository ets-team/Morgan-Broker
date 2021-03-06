package group.eis.morganborker.service.Impl;

import com.google.gson.Gson;
import group.eis.morganborker.service.MarketService;
import group.eis.morganborker.utils.RedisUtil;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("MarketService")
public class MarketServiceImpl implements MarketService {
    private final RedisUtil redisUtil;
    private final Gson gson;

    public MarketServiceImpl(RedisUtil redisUtil){
        this.redisUtil = redisUtil;
        gson = new Gson();
    }
    @Override
    public HashMap<String, HashMap<String, Integer>> getMarket(Long futureID, int count) {
        int inCount = count;
        Set<Object> buyKeySet = redisUtil.hashKeys(futureID.toString()+"_buy_list");
        Set<Object> sellKeySet = redisUtil.hashKeys(futureID.toString()+"_sell_list");
        HashMap<String, Integer> buyMap = new HashMap<>();
        HashMap<String, Integer> sellMap = new HashMap<>();
        List<Integer> tempList = new LinkedList<>();

        for(Object o : buyKeySet){
            tempList.add(Integer.valueOf((String)o));
        }
        Collections.sort(tempList);
        Collections.reverse(tempList);
        for(Integer i : tempList){
            buyMap.put(i.toString(), (Integer) redisUtil.hashGet(futureID.toString()+"_buy_list", i.toString()));
            if(inCount == -1){
                continue;
            }
            inCount--;
            if(inCount == 0){
                break;
            }
        }

        tempList.clear();
        inCount = count;
        for(Object o : sellKeySet){
            tempList.add(Integer.valueOf((String)o));
        }
        Collections.sort(tempList);
        for(Integer i : tempList){
            sellMap.put(i.toString(), (Integer) redisUtil.hashGet(futureID.toString()+"_sell_list", i.toString()));
            if(inCount == -1){
                continue;
            }
            inCount--;
            if(inCount == 0){
                break;
            }
        }


        HashMap<String, HashMap<String, Integer>> resultMap = new HashMap<>();

        HashMap<String, Integer> infoMap = new HashMap<>();

        infoMap.put("future_id", futureID.intValue());


        resultMap.put("buy_list", buyMap);
        resultMap.put("sell_list", sellMap);
        resultMap.put("info", infoMap);
        return resultMap;
    }
}
