package com.dachen.health.auto.service;


import com.dachen.health.auto.entity.vo.BodyDisease;
import com.dachen.health.auto.entity.vo.DiseaseVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AutoDiagnoseService {
    /**
     * 获取部位和症状列表
     * @param sex
     * @return
     */
    public List<BodyDisease> getBodyDisease(String sex);

    /**
     * 获取疾病列表
     * @param symptomsCode
     * @return
     */
    public DiseaseVo getDiseaseList(String symptomsCode);

    public void excel(MultipartFile file);
}
