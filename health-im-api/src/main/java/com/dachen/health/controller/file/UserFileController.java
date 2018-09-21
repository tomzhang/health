package com.dachen.health.controller.file;

import com.dachen.commons.JSONMessage;
import com.dachen.health.file.entity.param.UserAfterUploadParam;
import com.dachen.health.file.entity.param.UserFileParam;
import com.dachen.health.file.entity.param.UserSaveFileParam;
import com.dachen.health.file.entity.vo.UserFileVO;
import com.dachen.health.file.service.IUserFileService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xuhuanjie
 * @desc
 * @date 2018-03-05
 * @Copyright (c) 2017, DaChen All Rights Reserved.
 */
@RestController
@RequestMapping("/userFile")
public class UserFileController {

    @Autowired
    private IUserFileService iUserFileService;

    @ApiOperation(value = "Web上传保存文件元信息", notes = "成功返回{\"data\": {id:xxx},\"resultCode\": 1,\"resultMsg\": xx}", response = Map.class)
    @ApiImplicitParam(name = "param", value = "前端上传完成返回参数", required = true, dataType = "UserAfterUploadParam")
    @RequestMapping(value = "saveFileInfo", method = RequestMethod.POST)
    public JSONMessage saveUploadFileInfo(@RequestBody UserAfterUploadParam param) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", iUserFileService.saveUploadFileInfo(param));
        return JSONMessage.success(null, map);
    }

    @ApiOperation(value = "搜索文件(包括我上传的文件，我接收的文件）", notes = "成功返回{\"data\": UserFileVO,\"resultCode\": 1,\"resultMsg\": xx}", response = UserFileVO.class)
    @ApiImplicitParam(name = "param", value = "查询参数", required = true, dataType = "UserFileParam")
    @RequestMapping(value = "queryFile", method = RequestMethod.POST)
    public JSONMessage queryFile(@RequestBody UserFileParam param) {
        //登录用户id
        return JSONMessage.success(iUserFileService.searchFile(param));
    }

    @ApiOperation(value = "删除文件(包括我上传的文件，我接收的文件）", notes = "成功返回{\"data\":,\"resultCode\": 1,\"resultMsg\": xx}", response = JSONMessage.class)
    @ApiImplicitParam(name = "fileIds", value = "文件Id集合", required = true, dataType = "String")
    @RequestMapping(value = "deleteFile", method = RequestMethod.POST)
    public JSONMessage deleteFile(@RequestParam List<String> fileIds) {
        iUserFileService.deleteFile(fileIds);
        return JSONMessage.success();
    }

    @ApiOperation(value = " 修改文件名", notes = "成功返回{\"data\":,\"resultCode\": 1,\"resultMsg\": xx}", response = JSONMessage.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "文件Id", required = true, dataType = "String"),
            @ApiImplicitParam(name = "newName", value = "新文件名（必须包括后缀）", required = true, dataType = "String"),
            @ApiImplicitParam(name = "userId", value = "用户Id", required = true, dataType = "Integer")
    })
    @RequestMapping(value = "updateFileName", method = RequestMethod.GET)
    public JSONMessage updateFileName(String id, String newName, Integer userId) {
        iUserFileService.updateFileName(id, newName, userId);
        return JSONMessage.success(null);
    }

    @ApiOperation(value = "用户发送文件", notes = "成功返回{\"data\":,\"resultCode\": 1,\"resultMsg\": xx}", response = JSONMessage.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupId", value = "会话组Id", required = true, dataType = "String"),
            @ApiImplicitParam(name = "fileIds", value = "文件Id集合", required = true, dataType = "String")
    })
    @RequestMapping(value = "sendFile", method = RequestMethod.POST)
    public JSONMessage sendFile(@RequestParam String groupId, @RequestParam List<String> fileIds) {
        iUserFileService.sendFile(groupId, fileIds);
        return JSONMessage.success();
    }

    @ApiOperation(value = "用户保存文件", notes = "成功返回{\"data\": fileId,\"resultCode\": 1,\"resultMsg\": xx}", response = JSONMessage.class)
    @ApiImplicitParam(name = "param", value = "参数", required = true, dataType = "UserSaveFileParam")
    @RequestMapping(value = "saveFile", method = RequestMethod.POST)
    public JSONMessage saveFile(@RequestBody UserSaveFileParam param) {
        return JSONMessage.success(iUserFileService.saveFileInfo(param));
    }

    @ApiOperation(value = "返回文件是否在我的文件列表", notes = "成功返回{\"data\": true(存在)/false(不存在),\"resultCode\": 1,\"resultMsg\": xx}", response = JSONMessage.class)
    @ApiImplicitParam(name = "id", value = "文件Id", required = true, dataType = "String")
    @RequestMapping(value = "isInMyFileList/{id}", method = RequestMethod.GET)
    public JSONMessage isInMyFileList(@PathVariable String id) {
        return JSONMessage.success(iUserFileService.isInMyFileList(id));
    }

}
