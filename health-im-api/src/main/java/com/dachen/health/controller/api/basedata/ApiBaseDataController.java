package com.dachen.health.controller.api.basedata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.base.entity.po.CheckSuggest;
import com.dachen.health.base.entity.vo.DiseaseTypeVO;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.group.group.dao.IGroupSearchDao;
import com.dachen.health.pack.stat.service.IDiseaseTypeService;
import com.dachen.util.JSONUtil;
import com.dachen.util.tree.ExtTreeNode;
import com.dachen.util.tree.ExtTreeUtil;
import com.dachen.web.ApiBaseController;

@RestController
@RequestMapping("/api/basedata")
public class ApiBaseDataController extends ApiBaseController {

	@Resource
	protected IBaseDataService baseDataService;

	@Resource
	protected IDiseaseTypeService diseaseTypeService;

	@Resource
	protected IGroupSearchDao groupSearchDao;

	@RequestMapping(value = "diseaseType/{id}", method = RequestMethod.GET)
	public JSONMessage getDiseaseType(@PathVariable String id) {
		String tag = "diseaseType/{id}";
		logger.info("{}. id={}", tag, id);

		List<DiseaseTypeVO> diseaseTypeList = baseDataService.getDiseaseType(Arrays.asList(id));
		if (CollectionUtils.isEmpty(diseaseTypeList)) {
			return JSONMessage.success();
		}
		return JSONMessage.success(null, diseaseTypeList.get(0));
	}

	@RequestMapping(value = "diseaseTypes", method = RequestMethod.GET)
	public JSONMessage getDiseaseType(String[] ids) {
		String tag = "diseaseTypes";
		logger.info("{}. ids={}", tag, ids);
		List<DiseaseTypeVO> diseaseTypeList = baseDataService.getDiseaseType(Arrays.asList(ids));
		if (CollectionUtils.isEmpty(diseaseTypeList)) {
			return JSONMessage.success();
		}
		return JSONMessage.success(null, diseaseTypeList);
	}
	
	@RequestMapping(value = "checkups", method = RequestMethod.GET)
	public JSONMessage checkups(String[] ids) {
		String tag = "checkups";
		logger.info("{}. ids={}", tag, ids);
		List<CheckSuggest> ret = baseDataService.getCheckSuggestByIds(ids);
		if (CollectionUtils.isEmpty(ret)) {
			return JSONMessage.success();
		}
		return JSONMessage.success(null, ret);
	}

	/**
	 * 获取一级病种树
	 * 
	 * @param ids
	 * @return
	 */
	@RequestMapping(value = "getLevel1DiseaseTypeTree", method = RequestMethod.GET)
	public JSONMessage getLevel1DiseaseTypeTree(String[] ids) {
		String tag = "getLevel1DiseaseTypeTree";
		logger.info("{}. ids={}", tag, ids);
		if (null == ids || 0 == ids.length) {
			return JSONMessage.failure("ids is empty!");
		}

		List<ExtTreeNode> ret = diseaseTypeService
				.getLevel1DiseaseTypeTree(new HashSet<String>(Arrays.asList(ids)));

        return JSONMessage.success(null, ret);
	}

	/**
	 * 通过病种ids获取以，号分隔病种name的字符串文本
	 * 
	 * @param diseaseIds
	 * @return
	 */
	@RequestMapping(value = "findDiseaseOnIds", method = RequestMethod.GET)
	public JSONMessage findDiseaseOnIds(String[] diseaseIds) {
		String tag = "findDiseaseOnIds";
		logger.info("{}. diseaseIds={}", tag, diseaseIds);

		String name = groupSearchDao.findDiseaseOnIds(Arrays.asList(diseaseIds));
		return JSONMessage.success(null, name);
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "toContent/{id}", method = RequestMethod.POST)
	public JSONMessage toContent(@PathVariable String id, String argsJson) {
		String tag = "toContent/{id}";
		logger.info("{}. id={}, argsJson={}", tag, id, argsJson);
		
		List list = null;
		if (StringUtils.isNotBlank(argsJson)) {
			list = JSONUtil.parseObject(List.class, argsJson);
		}

		String msg = baseDataService.toContent(id, list.toArray());

		return JSONMessage.success(null, msg);
	}
	
	
    @RequestMapping(value="getDiseaseTypeTree4CarePlan", method=RequestMethod.GET)
    public JSONMessage getDiseaseTypeTree4CarePlan(@RequestParam String groupId, @RequestParam Integer tmpType) {
    	String tag = "getDiseaseTypeTree4CarePlan";
		logger.info("{}. groupId={}, tmpType={}", tag, groupId, tmpType);
		
    	List<DiseaseTypeVO> voList = diseaseTypeService.getDiseaseTypeTree4Plan(groupId, tmpType);
    	if (CollectionUtils.isEmpty(voList)) {
    		logger.warn("{}. voList is empty!", tag, voList.size());
    		return JSONMessage.success();
    	}
    	
    	logger.info("{}. voList.size()={}", tag, voList.size());
    	
    	List<ExtTreeNode> nodeList = new ArrayList<ExtTreeNode>();
        for(DiseaseTypeVO vo : voList){
            ExtTreeNode node = new ExtTreeNode();
            node.setId(vo.getId());
            node.setName(vo.getName());
            node.setParentId(vo.getParent());
            nodeList.add(node);
        }
        
        return JSONMessage.success(null, ExtTreeUtil.buildTree(nodeList));
    }
    
    @RequestMapping(value="getDiseaseTypeTree", method=RequestMethod.GET)
    public JSONMessage getDiseaseTypeTree(@RequestParam String[] ids, @RequestParam boolean onlyShowParentNode) {
    	String tag = "getDiseaseTypeTree";
		logger.info("{}. ids={}, onlyShowParentNode={}", tag, ids, onlyShowParentNode);
		
    	List<DiseaseTypeVO> voList = diseaseTypeService.getDiseaseTypeList(new HashSet<String>(Arrays.asList(ids)), onlyShowParentNode);
    	if (CollectionUtils.isEmpty(voList)) {
    		logger.warn("{}. voList is empty!", tag, voList.size());
    		return JSONMessage.success();
    	}
    	
    	logger.info("{}. voList.size()={}", tag, voList.size());
    	
    	List<ExtTreeNode> nodeList = new ArrayList<ExtTreeNode>();
        for(DiseaseTypeVO vo : voList){
            ExtTreeNode node = new ExtTreeNode();
            node.setId(vo.getId());
            node.setName(vo.getName());
            node.setParentId(vo.getParent());
            nodeList.add(node);
        }
        
        return JSONMessage.success(null, ExtTreeUtil.buildTree(nodeList));
    }
}
