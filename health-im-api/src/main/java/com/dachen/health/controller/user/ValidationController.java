package com.dachen.health.controller.user;

import com.dachen.commons.exception.ServiceException;
import com.dachen.health.common.controller.AbstractController;
import com.dachen.health.commons.vo.OperationPassword;
import com.dachen.health.user.service.IValidationService;
import com.dachen.util.Md5Util;
import com.dachen.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.dachen.commons.JSONMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/validation")
public class ValidationController extends AbstractController {

    @Autowired
    private IValidationService validationService;

    @RequestMapping(value = "/pwd")
    public JSONMessage validationPwd(String type, @RequestParam(required = true) String pwd) {

        OperationPassword operationPassword = validationService.getValidation(type);

        if (Objects.isNull(operationPassword)) {
            throw new ServiceException("找不到该类型");
        }

        Map<String, Boolean> result = new HashMap<>();
        if (StringUtils.equals(Md5Util.md5Hex(pwd), operationPassword.getPassword())) {
            result.put("validation", true);
        } else {
            result.put("validation", false);
        }

        return JSONMessage.success(result);
    }
}
