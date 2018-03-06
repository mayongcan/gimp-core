package com.gimplatform.core.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gimplatform.core.entity.WeatherInfo;
import com.gimplatform.core.repository.WeatherInfoRepository;
import com.gimplatform.core.service.WeatherInfoService;
import com.gimplatform.core.utils.HttpUtils;
import com.gimplatform.core.utils.PinyinUtils;

/**
 * 天气信息服务类
 * @author zzd
 */
@Service
public class WeatherInfoServiceImpl implements WeatherInfoService {

    protected static final Logger logger = LogManager.getLogger(WeatherInfoServiceImpl.class);

    @Autowired
    private WeatherInfoRepository weatherInfoRepository;

    @Override
    public List<Map<String, Object>> getWeatherList(String city, String beginTime, String endTime, int days) {
        logger.info("城市名称转换======》" + city);
        city = city.replace("市", "");
        city = PinyinUtils.converterToSpell(city);
        logger.info("城市名称转换======》" + city);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("city", city);
        params.put("beginTime", beginTime);
        params.put("endTime", endTime);
        List<Map<String, Object>> list = weatherInfoRepository.getWeatherList(params);
        // 判断是否有数据，如果没有数据，则发起请求
        if (list == null || list.size() < days) {
            weatherInfoRepository.delByCityAndDate(city, beginTime, endTime);
            weatherInfoRepository.flush();
            logger.info("查找不到天气数据，联网发起请求");
            String url = "https://api.seniverse.com/v3/weather/daily.json";
            Map<String, String> urlParams = new HashMap<String, String>();
            urlParams.put("key", "bhcvbvzswzatuoir");
            urlParams.put("location", city);
            urlParams.put("language", "zh-Hans");
            urlParams.put("unit", "c");
            urlParams.put("start", beginTime);
            urlParams.put("days", days + "");
            String retStr = HttpUtils.get(url, urlParams, null);
            // String retStr = HttpUtils.get("https://api.seniverse.com/v3/weather/daily.json?key=bhcvbvzswzatuoir&location=" + city + "&language=zh-Hans&unit=c&start=0&days=3");
            logger.info("天气信息：" + retStr);
            JSONObject json = JSONObject.parseObject(retStr);
            JSONArray resultJson = json.getJSONArray("results");
            if (resultJson != null && resultJson.size() > 0) {
                JSONArray dailyArray = resultJson.getJSONObject(0).getJSONArray("daily");
                if (dailyArray != null && dailyArray.size() > 0) {
                    for (int i = 0; i < dailyArray.size(); i++) {
                        JSONObject tmpJson = dailyArray.getJSONObject(i);
                        WeatherInfo obj = new WeatherInfo();
                        obj.setCity(city);
                        obj.setWeatherDate(tmpJson.getDate("date"));
                        obj.setTextDay(tmpJson.getString("text_day"));
                        obj.setCodeDay(tmpJson.getString("code_day"));
                        obj.setTextNigth(tmpJson.getString("text_night"));
                        obj.setCodeNight(tmpJson.getString("code_night"));
                        obj.setHight(tmpJson.getString("high"));
                        obj.setLow(tmpJson.getString("low"));
                        obj.setPrecip(tmpJson.getString("precip"));
                        obj.setWindDirection(tmpJson.getString("wind_direction"));
                        obj.setWindDirectionDegree(tmpJson.getString("wind_direction_degree"));
                        obj.setWindSpeed(tmpJson.getString("wind_speed"));
                        obj.setWindScale(tmpJson.getString("wind_scale"));
                        weatherInfoRepository.save(obj);
                    }
                    weatherInfoRepository.flush();
                }
            }
            list = weatherInfoRepository.getWeatherList(params);
            return list;
        } else {
            logger.info("从数据库缓存中获取天气信息成功！");
            return list;
        }
    }
}
