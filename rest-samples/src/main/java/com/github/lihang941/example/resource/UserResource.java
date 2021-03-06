package com.github.lihang941.example.resource;

import com.github.lihang941.common.bean.IdDto;
import com.github.lihang941.common.page.OffsetBean;
import com.github.lihang941.common.vertx.RequestTool;
import com.github.lihang941.example.convert.UserConvert;
import com.github.lihang941.example.dto.UserDto;
import com.github.lihang941.example.entity.User;
import com.github.lihang941.example.service.UserService;
import com.github.lihang941.vertx.rest.*;
import com.github.lihang941.web.autoconfigure.Controller;
import com.github.pagehelper.Page;
import io.vertx.core.http.CaseInsensitiveHeaders;
import io.vertx.core.http.HttpServerResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * @author : lihang1329@gmail.com
 * @since : 2018-12-03 10:02
 */
@URL("user")
@Controller
public class UserResource {

    @Autowired
    private UserService userService;

    @POST
    public IdDto add(@Body UserDto userDto) {
        User user= UserConvert.toUser().apply(userDto);
        userService.insertSelective(user);
        return new IdDto(user.getUserId());
    }

    @URL(":id")
    @POST
    public void update(@Path("id") String userId,@Body UserDto userDto) {
        User user= UserConvert.toUser().apply(userDto);
        user.setUserId(userId);
        userService.updateByPrimaryKeySelective(user);
    }

    @URL(":id")
    @DELETE
    public void delete(@Path("id") String userId) {
        userService.deleteByPrimaryKey(userId);
    }

    @GET
    @URL(":id")
    public UserDto get(@Path("id") String userId) {
        User user = userService.selectByPrimaryKey(userId);
        if(user == null){
            throw new NoSuchElementException();
        }
        return UserConvert.toUserDto().apply(user);
    }


    @URL("list")
    @GET
    public void get(@HeaderMap CaseInsensitiveHeaders headers, @Context Serializer serializer, @Context HttpServerResponse response) {
        OffsetBean offsetBean = RequestTool.toOffsetBean(headers);
        Page<User> pageList = userService.offsetList(offsetBean);
        RequestTool.pageEnd(pageList.getTotal(),
                        pageList.getResult()
                        .stream()
                        .map(UserConvert.toUserDto())
                        .collect(Collectors.toList())
                    , response, serializer);
    }

}
