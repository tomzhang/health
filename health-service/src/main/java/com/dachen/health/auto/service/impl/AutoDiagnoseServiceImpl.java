package com.dachen.health.auto.service.impl;

import com.dachen.commons.exception.ServiceException;
import com.dachen.health.auto.dao.AutoDiagnoseDao;
import com.dachen.health.auto.entity.po.Body;
import com.dachen.health.auto.entity.po.Disease;
import com.dachen.health.auto.entity.po.Symptoms;
import com.dachen.health.auto.entity.po.SymptomsDisease;
import com.dachen.health.auto.entity.vo.AutoExcelVo;
import com.dachen.health.auto.entity.vo.BodyDisease;
import com.dachen.health.auto.entity.vo.DiseaseVo;
import com.dachen.health.auto.service.AutoDiagnoseService;
import com.dachen.health.disease.dao.DiseaseTypeRepository;
import com.dachen.health.disease.entity.DiseaseType;
import com.dachen.util.StringUtil;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liming on 2016/11/10.
 */
@Service
public class AutoDiagnoseServiceImpl implements AutoDiagnoseService{
    @Autowired
    private AutoDiagnoseDao auto;
    @Autowired
    private DiseaseTypeRepository diseaseDao;

    @Override
    public List<BodyDisease> getBodyDisease(String sex) {
        List<BodyDisease> list=new ArrayList<>();
        //获取身体部位列表
        List<Body> bodyList=auto.getBodyParsts();

        //获取身体各个部位的编码
        BodyDisease bodyDisease;
        for(Body bodyParts:bodyList){
            bodyDisease=new BodyDisease();
            bodyDisease.setBodyCode(bodyParts.getCode());//部位编码
            bodyDisease.setName(bodyParts.getName());//部位名称
            //查询对应的集合
            List<Symptoms> diseaseList=auto.getDiseaseByBodyCode(bodyParts.getCode(),sex);
            if(diseaseList!=null){
                bodyDisease.setDiseases(diseaseList);
            }
            list.add(bodyDisease);
        }
        return list;
    }

    public DiseaseVo getDiseaseList(String symptomsCode){
        List<SymptomsDisease> sd=auto.getSymptomsDiseaseBySympCode(symptomsCode);
        DiseaseVo diseaseVo=new DiseaseVo();
        List<DiseaseType> list =new ArrayList<>();
        List<String> diseaseIds=new ArrayList<>();

        for(SymptomsDisease vo:sd){
            DiseaseType diseaseType=diseaseDao.get(vo.getDiseaseCode());
            list.add(diseaseType);
            diseaseIds.add(diseaseType.getId());
        }
        diseaseVo.setDisease(list);
        diseaseVo.setDiseaseIds(diseaseIds);
        return diseaseVo;
    }

    @Override
    public void excel(MultipartFile file) {
        if (file == null) {
            throw new ServiceException("文件为空");
        }

        if (file.isEmpty()) {
            throw new ServiceException("数据内容为空");
        }

        List<AutoExcelVo> autoExcelVos=new ArrayList<>();

        try {
         autoExcelVos=  excelAuto(file);
        }catch (IOException e){
            e.printStackTrace();
        }

        System.out.println(autoExcelVos.size());
        //处理病症信息
        //获取需要保存的病症（有冗余）
        List<Symptoms> symptomses=new ArrayList<>();
        for(AutoExcelVo vo:autoExcelVos){
            Symptoms sy=new Symptoms();
            if(StringUtil.isEmpty(vo.getSymptomsName())){
                continue;
            }
            //查询对应部位的id
            Body body=auto.getBodyByName(vo.getBodyName());
            if(vo.getSex().equals("女")){
                sy.setBodyCode(body.getCode());
                sy.setName(vo.getSymptomsName());
                sy.setSex("2");
            }else{
                sy.setBodyCode(body.getCode());
                sy.setName(vo.getSymptomsName());
                sy.setSex("1");
            }
            symptomses.add(sy);
        }

        //保存病症库
        int symptomesCode=200000;
        for(Symptoms syVo:symptomses){
            //先查询病症库是否存在
            Symptoms a=(Symptoms) auto.getSymptomesByNameAndSex(syVo.getName(),syVo.getSex());
            if(a==null){
                syVo.setCode(symptomesCode+"");
                auto.saveSymptomes(syVo);
                symptomesCode++;
            }


        }

        //处理基础疾病表
        int j=100000;
        for(AutoExcelVo vos:autoExcelVos){
            Disease disease=new Disease();
            if(StringUtil.isEmpty(vos.getDiseaseName())){
                continue;
            }
            List<DiseaseType> diseaseTypes=diseaseDao.findByName(vos.getDiseaseName());
            if(diseaseTypes.size()>0){
               // System.out.println(vos.getDiseaseName());
            }else{
                //判断在原来的疾病库里面是否存在
                //List<Disease> d=auto.getDiseaseByName(vos.getDiseaseName());
                //if(d.size()>0){
                //    continue;
                //}
                //System.out.println(vos.getDiseaseName());
                //disease.setName(vos.getDiseaseName());
                //disease.setContent(vos.getDiseaseContent());
                //disease.setCode(j+"");
                //auto.saveDisease(disease);
                //j++;

            }



        }


        //处理病症和疾病库之间的对应关系
        List<SymptomsDisease> sd=new ArrayList();
        for(AutoExcelVo vos:autoExcelVos){
            if(StringUtil.isEmpty(vos.getDiseaseName())){
                continue;
            }
            //需要判断是否在本地库存在
            List<DiseaseType> diseaseTypes=diseaseDao.findByName(vos.getDiseaseName());
            if(diseaseTypes.size()<1){
                continue;
            }
            SymptomsDisease sbVo=new SymptomsDisease();
            Symptoms symptoms=(Symptoms) auto.getSymptomesByNameAndSex(vos.getSymptomsName(),vos.getSex().equals("男")?"1":"2");
           // Symptoms symptoms=(Symptoms) auto.get(Symptoms.class,"name",vos.getSymptomsName());
            Disease disease=(Disease) auto.get(Disease.class,"name",vos.getDiseaseName());
            //有重复数据，需要处理
            SymptomsDisease symptomsDisease=auto.getSymptomsDiseaseBySympCodeAndDiseaseCode(symptoms.getCode(),diseaseTypes.get(0).getId());
            if(symptomsDisease!=null){
                continue;
            }
            sbVo.setSymptomsCode(symptoms.getCode());
            sbVo.setDiseaseCode(diseaseTypes.get(0).getId());

            auto.saveSymptomesDisease(sbVo);

        }






    }



    List<AutoExcelVo> excelAuto(MultipartFile file) throws IOException{
        HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream());
        List<AutoExcelVo> auto=new ArrayList<>();
        for(int numSheet = 0; numSheet < workbook.getNumberOfSheets();numSheet ++) {
            HSSFSheet sheet = workbook.getSheetAt(numSheet);

            if (sheet == null) {
                continue;
            }

            for (int rowNum = 2; rowNum <= sheet.getLastRowNum(); rowNum++) {
                HSSFRow row = sheet.getRow(rowNum);
                if (row != null) {
                   AutoExcelVo excelVo=new AutoExcelVo();
                    String sex=getStringCellValue(row.getCell(0));
                    String bodyName=getStringCellValue(row.getCell(1));
                    String symptomsName=getStringCellValue(row.getCell(2));
                    String diseaseName=getStringCellValue(row.getCell(4));
                    String diseaseContent=getStringCellValue(row.getCell(5));
                    int num=Integer.parseInt(StringUtil.isEmpty(getStringCellValue(row.getCell(3)))?"0":getStringCellValue(row.getCell(3)));

                    excelVo.setSex(sex);
                    excelVo.setBodyName(bodyName);
                    excelVo.setSymptomsName(symptomsName);
                    excelVo.setDiseaseName(diseaseName);
                    excelVo.setDiseaseContent(diseaseContent);
                    excelVo.setNum(num);
                    auto.add(excelVo);
                }
            }
        }


        return auto;
    }


    private String getStringCellValue(HSSFCell cell) {
        String strCell = "";
        if(cell==null){
            return "";
        }
        switch (cell.getCellType()) {
            case HSSFCell.CELL_TYPE_STRING:
                strCell = cell.getStringCellValue();
                break;
            case HSSFCell.CELL_TYPE_NUMERIC:
                strCell = String.valueOf((int)cell.getNumericCellValue());
                break;
            case HSSFCell.CELL_TYPE_BOOLEAN:
                strCell = String.valueOf(cell.getBooleanCellValue());
                break;
            case HSSFCell.CELL_TYPE_BLANK:
                strCell = "";
                break;
            default:
                strCell = "";
                break;
        }
        if (strCell.equals("") || strCell == null) {
            return "";
        }
        return strCell;
    }



}
