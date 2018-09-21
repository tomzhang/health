package com.dachen.health.controller.operationLog;

import com.dachen.commons.JSONMessage;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.health.activity.invite.util.MapUtil;
import com.dachen.health.operationLog.entity.param.OperationLogParam;
import com.dachen.health.operationLog.entity.po.OperationLog;
import com.dachen.health.operationLog.service.IOperationLogService;
import com.dachen.health.pack.income.util.ExcelUtil;
import com.dachen.util.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 钟良
 * @desc
 * @date:2017/9/29 16:49 Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Api(value = "操作日志", description = "操作日志", produces = MediaType.APPLICATION_JSON_VALUE, protocols = "http")
@RestController
@RequestMapping("operation/log")
public class OperationLogController {
    private static final Logger logger = LoggerFactory.getLogger(OperationLogController.class);

    @Autowired
    private IOperationLogService operationLogService;

    @ApiOperation(value = "查询操作日志列表", notes = "查询操作日志列表", response = OperationLog.class)
    @ApiImplicitParams(value = {
        @ApiImplicitParam(dataType = "String", paramType = "query", name = "keywords", value = "搜索关键字"),
        @ApiImplicitParam(dataType = "Integer", paramType = "query", name = "pageIndex", value = "页码，从0开始"),
        @ApiImplicitParam(dataType = "Integer", paramType = "query", name = "pageSize", value = "每页数量")
    })
    @RequestMapping(value = "", method = RequestMethod.GET)
    public JSONMessage list(OperationLogParam param) {
        return JSONMessage.success(operationLogService.list(param));
    }

    @ApiOperation(value = "导出操作日志列表", notes = "导出操作日志列表", response = OperationLog.class)
    @ApiImplicitParam(dataType = "String", paramType = "query", name = "keywords", value = "搜索关键字")
    @RequestMapping(value = "export", method = RequestMethod.GET)
    public JSONMessage log_export(OperationLogParam param, HttpServletResponse response) {
        String fileName = "用户操作日志";

        String[] columnNames = new String[]{"用户名", "模块", "操作内容", "时间"};//列名
        String[] keys = new String[]{"user", "operationType", "content", "date"};

        long total = operationLogService.listCount(param);
        if (total == 0L) {
            throw new ServiceException("没有查询到数据");
        } else {
            param.setPageSize(Long.valueOf(total).intValue());
        }
        PageVO pageVO = operationLogService.list(param);

        List<Map<String, Object>> list;
        try {
            list = createExcelRecord(pageVO.getPageData());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException("数据导出失败");
        }

        for (int i = 1; i < list.size(); i++) {
            Map<String, Object> map = list.get(i);
            Long date = (Long) map.get("date");
            map.put("date", DateUtil.formatDate2Str(date));
        }

        try {
            writeData(response, fileName, columnNames, keys, list);
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            return JSONMessage.error(ex);
        }
        return JSONMessage.success();
    }

    private List<Map<String, Object>> createExcelRecord(List<?> list) throws Exception {
        List<Map<String, Object>> listmap = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("sheetName", "sheet1");
        listmap.add(map);
        for (Object obj : list) {
            listmap.add(MapUtil.beanToMap(obj));
        }
        return listmap;
    }

    private void writeData(HttpServletResponse response, String fileName, String[] columnNames,
        String[] keys, List<Map<String, Object>> list) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ExcelUtil.createWorkBook(list, keys, columnNames).write(os);

        byte[] content = os.toByteArray();
        InputStream is = new ByteArrayInputStream(content);

        // 设置response参数，可以打开下载页面
        response.reset();
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition",
            "attachment;filename=" + new String((fileName + ".xls").getBytes(), "iso-8859-1"));
        ServletOutputStream out = response.getOutputStream();
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(is);
            bos = new BufferedOutputStream(out);
            byte[] buff = new byte[2048];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw e;
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                }catch (IOException e){
                    logger.error(e.getMessage(), e);
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                }catch (IOException e){
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }
}
