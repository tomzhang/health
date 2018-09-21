package com.dachen.health.controller.pack.patient;

import com.dachen.sdk.exception.HttpApiException;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dachen.commons.JSONMessage;
import com.dachen.health.pack.patient.service.IBaseService;
/**
 * 
 * ProjectName： health-im-api<br>
 * ClassName： BaseController<br>
 * Description： <br>
 * @author 李淼淼
 * @createTime 2015年8月13日
 * @version 1.0.0
 */
public abstract class BaseController<T, PK> {

	public IBaseService<T, PK> getService() {
		throw new RuntimeException("must implement");
	}

	/**
	 * 
	 * 
	 * 
	 * @author 李淼淼
	 * @date 2015年8月12日
	 */
	@RequestMapping("create")
	public JSONMessage create(T intance) throws HttpApiException {
		int ret = getService().save(intance);
		if (ret > 0) {
			return JSONMessage.success("创建成功", intance);
		} else {
			return JSONMessage.failure("创建失败");
		}

	}

	/**
	 * 
	 * 
	 * 
	 * @author 李淼淼
	 * @date 2015年8月12日
	 */
	@RequestMapping("update")
	public JSONMessage update(T intance) throws HttpApiException {
		int ret = getService().update(intance);
		if (ret > 0) {
			return JSONMessage.success("修改成功", intance);
		} else {
			return JSONMessage.failure("修改失败");
		}

	}

	/**
	 * 

	 * @author 李淼淼
	 * @date 2015年8月12日
	 */
	@RequestMapping(value = "deleteByPk")
	public JSONMessage deleteByPk(PK id) {
		int ret = getService().deleteByPK(id);
		if (ret > 0) {
			return JSONMessage.success("删除成功");
		} else {
			return JSONMessage.failure("删除失败");
		}

	}

	/**
	 * 
	 * @author 李淼淼
	 * @date 2015年8月12日
	 */
	@RequestMapping(value = "findById")
	public JSONMessage findById(PK id) throws HttpApiException {
		T data = getService().findByPk(id);
		if (data == null) {
			return JSONMessage.failure("找不到对应记录");
		}
		return JSONMessage.success(null, data);
	}
}
