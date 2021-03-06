package com.github.lihang941.common.vertx;

import com.github.lihang941.common.page.OffsetBean;
import com.github.lihang941.common.page.PageNumBean;
import com.github.lihang941.vertx.rest.Serializer;
import com.github.pagehelper.Page;
import io.vertx.core.http.CaseInsensitiveHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static com.github.lihang941.common.page.PageConstants.PAGE_NUM;
import static com.github.lihang941.common.page.PageConstants.PAGE_SIZE;

/**
 * Vert.x Http 工具包
 *
 * @author : lihang1329@gmail.com
 * @since : 2018/9/4
 */
public abstract class RequestTool {

    /**
     * 获取真实IP
     *
     * @param request
     * @return
     */
    public static String getIpAddress(HttpServerRequest request) {
        String Xip = request.getHeader("X-Real-IP");
        String XFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotEmpty(XFor) && !"unKnown".equalsIgnoreCase(XFor)) {
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = XFor.indexOf(",");
            if (index != -1) {
                return XFor.substring(0, index);
            } else {
                return XFor;
            }
        }
        XFor = Xip;
        if (StringUtils.isNotEmpty(XFor) && !"unKnown".equalsIgnoreCase(XFor)) {
            return XFor;
        }
        if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.remoteAddress().host();
        }
        return XFor;
    }


    public static OffsetBean toOffsetBean(CaseInsensitiveHeaders map) {
        int offset;
        int size;
        try {
            String tempOffset = map.get("Offset");
            String tempSize = map.get("Size");
            offset = tempOffset == null ? 0 : Integer.parseInt(tempOffset);
            size = tempSize == null ? 0 : Integer.parseInt(tempSize);
        } catch (NumberFormatException e) {
            offset = 0;
            size = PAGE_SIZE;
        }
        OffsetBean offSetBean = new OffsetBean().setOffSet(offset).setSize(size);
        if (offSetBean.getOffSet() < 0) offSetBean.setOffSet(0);
        if (offSetBean.getSize() <= 0) {
            offSetBean.setSize(PAGE_SIZE);
        }
        return offSetBean;
    }


    public static PageNumBean toPageNumBean(CaseInsensitiveHeaders map) {
        int pageNum;
        int size;
        try {
            String tempPage = map.get("Page");
            String tempSize = map.get("Size");
            pageNum = tempPage == null ? 0 : Integer.parseInt(tempPage);
            size = tempSize == null ? 0 : Integer.parseInt(tempSize);
        } catch (NumberFormatException e) {
            pageNum = PAGE_NUM;
            size = PAGE_SIZE;
        }
        PageNumBean pageNumBean = new PageNumBean().setPageNum(pageNum).setPageSize(size);
        if (pageNumBean.getPageNum() <= 0) pageNumBean.setPageNum(1);
        if (pageNumBean.getPageSize() <= 0) {
            pageNumBean.setPageSize(PAGE_SIZE);
        }
        return pageNumBean;
    }


    public static HttpServerResponse pageEnd(Page page, HttpServerResponse httpServerResponse, Serializer serializer) {
        httpServerResponse.putHeader("TotalCount", Long.toString(page.getTotal()))
                .end(serializer.serialize(page.getResult()));
        return httpServerResponse;
    }

    public static HttpServerResponse pageEnd(long totalCount, List<?> list, HttpServerResponse httpServerResponse, Serializer serializer) {
        httpServerResponse.putHeader("TotalCount", Long.toString(totalCount)).end(serializer.serialize(list));
        return httpServerResponse;
    }


}
