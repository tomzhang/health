package com.dachen.health.controller.group.hospital;

import com.dachen.commons.JSONMessage;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.health.activity.invite.util.MapUtil;
import com.dachen.health.base.entity.po.Area;
import com.dachen.health.base.entity.vo.HospitalVO;
import com.dachen.health.group.group.entity.param.HospitalBaseParam;
import com.dachen.health.group.impl.HospitalBaseService;
import com.dachen.health.pack.income.util.ExcelUtil;
import com.dachen.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 
 * @author longjh
 *      date:2017/08/18
 */
@RestController
@RequestMapping("/group/hospital")
public class HospitalBaseInfoController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private HospitalBaseService hospitalBaseService;
    
    /**
     * @api {[get,post]} /group/hospital/hospitalLevel 获取医院所有等级信息
     * @apiVersion 1.0.0
     * @apiName hospitalLevel
     * @apiGroup 医院管理
     * @apiDescription 获取医院所有等级信息
     * @apiParam {String} access_token token
     * 
     * @apiSuccess {String} ResultCode 返回状态码
     * @apiSuccess {Object[]} HospitalLevel 
     * @apiSuccess {String} HospitalLevel.id
     * @apiSuccess {String} HospitalLevel.level
     * 
     * @apiAuthor longjh
     * @date 2017/08/18
     */
    @RequestMapping(value="/hospitalLevel")
    public JSONMessage getHospitalLevel(){
        return JSONMessage.success("success", hospitalBaseService.findAllHospitalLevel());
    }
    
    /**
     * @api {get} /group/hospital/area 获取行政区域信息
     * @apiVersion 1.0.0
     * @apiName getArea
     * @apiGroup 医院管理
     * @apiDescription 获取行政区域信息
     * @apiParam {String} access_token token
     * @apiParam {Integer} id 唯一id
     * @apiParam {String} code 编码
     * @apiParam {String} name 行政区域名称
     * @apiParam {String} pcode 父编码
     * @apiParam {String} lastUpdatorTime 最后写操作时间
     * 
     * @apiSuccess {String} ResultCode 返回状态码
     * @apiSuccess {Object[]} area     
     * @apiSuccess {String} area.id 唯一id
     * @apiSuccess {String} area.code 编码
     * @apiSuccess {String} area.name 行政区域名称
     * @apiSuccess {String} area.pcode 父编码
     * @apiSuccess {String} area.lastUpdatorTime  最后更新时间
     * 
     * @apiAuthor longjh
     * @date 2017/08/18
     */
    @RequestMapping(value="/area")
    public JSONMessage getArea(Area area){
        return JSONMessage.success("success", hospitalBaseService.findArea(area));
    }
    
    /**
     * @api {get} /group/hospital/searchHospital 搜索医院信息信息
     * @apiVersion 1.0.0
     * @apiName searchHospital
     * @apiGroup 医院管理
     * @apiDescription 获取行政区域信息
     * @apiParam {String} access_token token
     * @apiParam {String} id 唯一id
     * @apiParam {String} name 医院名称
     * @apiParam {String} level 医院等级中文名称
     * @apiParam {Integer} province 医院所在省
     * @apiParam {Integer} city 医院所在市
     * @apiParam {Integer} country 医院所在镇
     * @apiParam {Integer} status 医院当前状态
     * @apiParam {Integer} pageIndex
     * @apiParam {Integer} pageSize
     *
     * @apiSuccess {String} ResultCode 返回状态码
     * @apiSuccess {PageVO} PageVO
     * @apiSuccess {int} PageVO.pageCount 唯一id
     * @apiSuccess {List} PageVO.pageData 编码
     * @apiSuccess {String} PageVO.pageIndex 行政区域名称
     * @apiSuccess {String} PageVO.pageSize 父编码
     * @apiSuccess {String} PageVO.start  最后更新时间
     * @apiSuccess {String} PageVO.total  总记录数
     *
     * @apiAuthor longjh
     * @date 2017/08/18
     */
    @RequestMapping(value="/searchHospital")
    public JSONMessage searchHospital(HospitalBaseParam hospitalParam){
        return JSONMessage.success("success", hospitalBaseService.findHospital(hospitalParam));
    }

    /**
     * 医院数据导出
     * @param hospitalParam
     * @param response
     * @return
     */
    @RequestMapping(value = "/exportHospital",method = RequestMethod.GET)
    public JSONMessage exportHospital(HospitalBaseParam hospitalParam,HttpServletResponse response){
        List<HospitalVO> pageData = null;

       if (StringUtil.isNotEmpty(hospitalParam.getLevel()) || StringUtil.isNotEmpty(hospitalParam.getName())
               || Objects.nonNull(hospitalParam.getProvince()) || Objects.nonNull(hospitalParam.getCity()) || Objects.nonNull(hospitalParam.getCountry())){
           hospitalParam.setPageIndex(0);
           hospitalParam.setPageSize(Integer.MAX_VALUE);
           PageVO pageVO = hospitalBaseService.findHospital(hospitalParam);
           pageData = (List<HospitalVO>) pageVO.getPageData();
       }else {
           //没有搜索条件,分批查询
           pageData = hospitalBaseService.pageQueryHospital();
       }

        if (CollectionUtils.isEmpty(pageData)){
            return JSONMessage.failure("没有查询到数据");
        }

        List<Map<String, Object>> list;
        try {
            list = createExcelRecord(pageData);
            for (Map<String,Object> map:list){
                if (map.containsKey("status")){
                    Integer status = (Integer) map.get("status");
                    String value = "";
                    if (status.equals(1)){
                        value = "启用";
                    }else if (status.equals(2)){
                        value = "禁用";
                    }
                    map.put("status",value);
                }
                //时间转换
                convertToDate(map);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException("数据导出失败");
        }

        String fileName = "医院数据";
        String[] columnNames = {"医院ID", "医院名称", "医院等级", "所在省份", "所在城市", "所在区域", "联系地址", "医院经度", "医院维度", "状态", "创建时间", "修改时间"};
        String[] keys = {"id", "name", "level", "provinceName", "cityName", "countryName", "address", "lat", "lng", "status", "createTime", "lastUpdatorTime"};
        try {
            writeData(response, fileName, columnNames, keys, list);
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            return JSONMessage.error(ex);
        }
        return JSONMessage.success();
    }

    private void convertToDate(Map<String, Object> map) {
        Long time = (Long) map.get("lastUpdatorTime");
        if (Objects.nonNull(time)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            java.util.Date dt = new Date(time);
            String sDateTime = sdf.format(dt);
            map.put("lastUpdatorTime", sDateTime);
        }
        Long createTime = (Long) map.get("createTime");
        if (Objects.nonNull(createTime)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            java.util.Date dt = new Date(createTime);
            String sDateTime = sdf.format(dt);
            map.put("createTime", sDateTime);
        }
    }

    private void writeData(HttpServletResponse response, String fileName, String[] columnNames,
                           String[] keys, List<Map<String, Object>> list) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ExcelUtil.createWorkBook(list, keys, columnNames).write(os);
        byte[] content  = os.toByteArray();
        InputStream is = new ByteArrayInputStream(content);

        // 设置response参数，可以打开下载页面
        response.reset();
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + new String((fileName + ".xlsx").getBytes(), "iso-8859-1"));
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

    /**
     * 批量获取医院信息，导出广告调查表时用到
     * @param hospitalIds
     * @return
     */
    @RequestMapping(value = "/getHospitalByIds")
    public JSONMessage getHospitalByIds(@RequestParam("hospitalIds") ArrayList<String> hospitalIds){
        return JSONMessage.success(hospitalBaseService.getByIds(hospitalIds));
    }
}
