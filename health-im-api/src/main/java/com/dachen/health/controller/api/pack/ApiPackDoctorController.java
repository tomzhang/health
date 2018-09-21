package com.dachen.health.controller.api.pack;

import java.util.List;

import javax.annotation.Resource;

import com.dachen.health.pack.order.entity.vo.DoctoreRatioVO;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.pack.pack.entity.po.PackDoctor;
import com.dachen.health.pack.pack.service.IPackDoctorService;
import com.dachen.web.ApiBaseController;

@RestController
@RequestMapping("/api/pack/doctor")
public class ApiPackDoctorController extends ApiBaseController {

	@Resource
	protected IPackDoctorService packDoctorService;

	@RequestMapping(value = "byPack/{packId}", method = RequestMethod.GET)
	public JSONMessage byPack(@PathVariable Integer packId) {
		String tag = "byPack/{packId}";
		logger.info("{}. packId={}", tag, packId);

		List<PackDoctor> packDoctores = packDoctorService.findByPackId(packId);
		if (CollectionUtils.isEmpty(packDoctores)) {
			return JSONMessage.success();
		}
		
		return JSONMessage.success(null, packDoctores);
	}

	@RequestMapping(value = "findDoctorIdListByPackId/{packId}", method = RequestMethod.GET)
	public JSONMessage findDoctorIdListByPackId(@PathVariable Integer packId) {
		String tag = "findDoctorIdListByPackId/{packId}";
		logger.info("{}. packId={}", tag, packId);

		List<Integer> packDoctorIdList = packDoctorService.findDoctorIdListByPackId(packId);
		if (CollectionUtils.isEmpty(packDoctorIdList)) {
			return JSONMessage.success();
		}
		
		return JSONMessage.success(null, packDoctorIdList);
	}

	@RequestMapping(value = "getDoctorRatiosByPack/{packId}", method = RequestMethod.GET)
	public JSONMessage getDoctorRatiosByPack(@PathVariable Integer packId,
			@RequestParam(required = true) Integer mainDoctorId) {
		String tag = "getDoctorRatiosByPack/{packId}";
		logger.info("{}. packId={}, mainDoctorId={}", tag, packId, mainDoctorId);

		List<DoctoreRatioVO> vos = packDoctorService.getDoctorRatiosByPack(packId, mainDoctorId);
		if (CollectionUtils.isEmpty(vos)) {
			return JSONMessage.success();
		}
		
		return JSONMessage.success(null, vos);
	}
}
