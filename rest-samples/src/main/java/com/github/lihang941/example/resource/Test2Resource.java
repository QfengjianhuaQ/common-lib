package com.github.lihang941.example.resource;

import com.github.lihang941.common.bean.IdDto;
import com.github.lihang941.common.page.OffsetBean;
import com.github.lihang941.common.redis.CreateKey;
import com.github.lihang941.common.redis.JsonRedisTemplate;
import com.github.lihang941.common.vertx.RequestTool;
import com.github.lihang941.example.convert.Test2Convert;
import com.github.lihang941.example.dto.Test2Dto;
import com.github.lihang941.example.entity.Test2;
import com.github.lihang941.example.service.Test2Service;
import com.github.lihang941.vertx.rest.*;
import com.github.lihang941.web.autoconfigure.Controller;
import com.github.pagehelper.Page;
import io.vertx.core.MultiMap;
import io.vertx.core.http.CaseInsensitiveHeaders;
import io.vertx.core.http.HttpServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.BoundValueOperations;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * @author : lihang1329@gmail.com
 * @since : 2018-12-03 10:02
 */

@URL("test2")
@Controller
public class Test2Resource {

    @Autowired
    private Test2Service test2Service;

    private JsonRedisTemplate<Test2> jsonRedisTemplate;


    private BoundValueOperations<String, Test2> stringTest2BoundValueOperations;

    public Test2Resource(RedisConnectionFactory redisConnectionFactory) {
        jsonRedisTemplate = new JsonRedisTemplate<Test2>(redisConnectionFactory){};
        stringTest2BoundValueOperations = jsonRedisTemplate.boundValueOps(CreateKey.create("test", "redis"));
        stringTest2BoundValueOperations.set(new Test2().setId(123L).setTestName("hhh"));
    }

    @POST
    public IdDto add(@Body Test2Dto test2Dto) {
        Test2 test2 = Test2Convert.toTest2().apply(test2Dto);
        test2Service.insertSelective(test2);
        return new IdDto(test2.getId());
    }


    @URL(":id")
    @POST
    public void update(@Path("id") Long id, @Body Test2Dto test2Dto) {
        Test2 test2 = Test2Convert.toTest2().apply(test2Dto);
        test2.setId(id);
        test2Service.updateByPrimaryKeySelective(test2);
    }

    @URL(":id")
    @DELETE
    public void delete(@Path("id") Long id) {
        test2Service.deleteByPrimaryKey(id);
    }

    @GET
    @URL(":id")
    public Test2Dto get(@Path("id") Long id) {
        Test2 test2 = test2Service.selectByPrimaryKey(id);
        if (test2 == null) {
            throw new NoSuchElementException();
        }
        return Test2Convert.toTest2Dto().apply(test2);
    }


    @URL("list")
    @GET
    public void get(@HeaderMap CaseInsensitiveHeaders headers, @Context Serializer serializer, @Context HttpServerResponse response) {
        OffsetBean offsetBean = RequestTool.toOffsetBean(headers);
        Page<Test2> pageList = test2Service.offsetList(offsetBean);
        RequestTool.pageEnd(pageList.getTotal(),
                pageList.getResult()
                        .stream()
                        .map(Test2Convert.toTest2Dto())
                        .collect(Collectors.toList())
                , response, serializer);
    }

    @URL("redis")
    @GET
    public Test2 redisTest() {
        return stringTest2BoundValueOperations.get();
    }

}
