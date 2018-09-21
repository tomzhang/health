package com.dachen.health.controller.api.document;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.document.entity.po.Document;
import com.dachen.health.document.service.IDocumentService;
import com.dachen.web.ApiBaseController;

@RestController
@RequestMapping("/api/document")
public class ApiDocumentController extends ApiBaseController {

	@Autowired
	protected IDocumentService documentService;

	@RequestMapping(value = "{id}", method = RequestMethod.GET)
	public JSONMessage findById(@PathVariable String id) {
		String tag = "{id}";
		logger.info("{}. id={}", tag, id);
		
		Document document = this.documentService.findByIdSimple(id);
		if (null == document) {
			return JSONMessage.success();
		}
		return JSONMessage.success(null, document);
	}
	
	@RequestMapping(value = "findByIds", method = RequestMethod.GET)
	public JSONMessage findByIds(@RequestParam String[] ids) {
		String tag = "findByIds";
		logger.info("{}. ids={}", tag, ids);
		
		List<Document> documentList = this.documentService.findByIdsSimple(Arrays.asList(ids));
		if (CollectionUtils.isEmpty(documentList)) {
			return JSONMessage.success();
		}
		return JSONMessage.success(null, documentList);
	}
	
}
