package com.dachen.health.controller.api.article;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.knowledge.entity.po.MedicalKnowledge;
import com.dachen.health.knowledge.service.IMedicalKnowledgeService;
import com.dachen.web.ApiBaseController;

@RestController
@RequestMapping("/api/article")
public class ApiArticleController extends ApiBaseController {

	@Resource
	protected IMedicalKnowledgeService medicalKnowledgeService;

	@RequestMapping(value = "{id}", method = RequestMethod.GET)
	public JSONMessage findById(@PathVariable String id) {
		String tag = "{id}";
		logger.info("{}. id={}", tag, id);

		MedicalKnowledge knowledgeVo = medicalKnowledgeService.findByIdSimple(id);
		if (null == knowledgeVo) {
			return JSONMessage.success();
		}

		return JSONMessage.success(null, knowledgeVo);
	}
	
	@RequestMapping(value = "findByIds", method = RequestMethod.GET)
	public JSONMessage findByIds(@RequestParam String[] ids) {
		String tag = "findByIds";
		logger.info("{}. ids={}", tag, ids);

		List<MedicalKnowledge> knowledgeVoList = medicalKnowledgeService.findByIdsSimple(Arrays.asList(ids));
		if (CollectionUtils.isEmpty(knowledgeVoList)) {
			return JSONMessage.success();
		}
		return JSONMessage.success(null, knowledgeVoList);
	}

}
