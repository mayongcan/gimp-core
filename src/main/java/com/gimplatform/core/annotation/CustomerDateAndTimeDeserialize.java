package com.gimplatform.core.annotation;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.gimplatform.core.utils.AddressUtils;

/**
 * 自定义日期序列化
 * @author zzd
 */
public class CustomerDateAndTimeDeserialize extends JsonDeserializer<Date> {

    protected static final Logger logger = LogManager.getLogger(AddressUtils.class);

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public Date deserialize(JsonParser paramJsonParser, DeserializationContext paramDeserializationContext) throws IOException, JsonProcessingException {
        String str = paramJsonParser.getText().trim();
        if (StringUtils.isBlank(str))
            return null;
        try {
            return dateFormat.parse(str);
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
        }
        return paramDeserializationContext.parseDate(str);
    }
}
