package com.dachen.health.controller.system.handler;

import com.dachen.health.base.entity.po.MsgTemplate;
import com.dachen.util.GeneralExcelUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sharp on 2018/3/1.
 */
public class DonwMsgTemplateList extends GeneralExcelUtil<MsgTemplate> {

    public  DonwMsgTemplateList(String[] keys, String[] columes, List<MsgTemplate> list) {
        super(keys, columes, list);
    }

    @Override
    public List<Map<String, Object>> createExcelRecord() {
        List<Map<String, Object>> listmap = new ArrayList<Map<String, Object>>();
        if(this.getDataList() == null ||this.getDataList().size() == 0){
            return listmap;
        }
        for(int i=0; i < this.getDataList().size(); i++){
            MsgTemplate vo = this.getDataList().get(i);
            Map<String, Object> mapValue = new HashMap<String, Object>();
            mapValue.put("_id", vo.getId());
            mapValue.put("category", vo.getCategory());
            mapValue.put("content", vo.getContent());
            mapValue.put("paraNum", vo.getParaNum());
            mapValue.put("usage", vo.getUsage());
            listmap.add(mapValue);
        }
        return listmap;
    }
}
