package com.dachen.health.document.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.http.FileUploadUtil;
import com.dachen.commons.page.PageVO;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.document.constant.DocumentEnum;
import com.dachen.health.document.dao.IContentTypeDao;
import com.dachen.health.document.dao.IDocumentDao;
import com.dachen.health.document.entity.param.DocumentParam;
import com.dachen.health.document.entity.po.Document;
import com.dachen.health.document.entity.po.DocumentType;
import com.dachen.health.document.entity.vo.ContentTypeVO;
import com.dachen.health.document.entity.vo.DocumentVO;
import com.dachen.health.document.service.IDocumentService;
import com.dachen.util.BeanUtil;
import com.dachen.util.DateUtil;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.QiniuUtil;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import com.google.common.collect.Maps;

@Service
public class DocumentServiceImpl implements IDocumentService {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IDocumentDao dao;
    @Autowired
    private IContentTypeDao contentTypeDao;
    @Resource
    private UserManager userManager;
    
    @Autowired
    FileUploadUtil fileUpload;

    protected InputStream getResourceFile(String filePath)  {
        ClassPathResource classPathResource = new ClassPathResource(filePath);
        if (classPathResource.exists()) {
            try {
                return classPathResource.getInputStream();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return null;
    }

    @Override
    public DocumentVO addDcoument(DocumentParam param) {

        boolean needTempFile = true;

        InputStream tempFile = null;
        String path = "";
        String author = "";
        String docName = "";

        if (param.getDocumentType().intValue() == DocumentEnum.DocumentType.adv.getIndex()) {
            tempFile = getResourceFile("/temp/adv/index.html");
            path = "adv";
        } else if (param.getDocumentType().intValue() == DocumentEnum.DocumentType.science.getIndex()) {
            tempFile = getResourceFile("/temp/science/index.html");
            path = "science";
        } else if (param.getDocumentType().intValue() == DocumentEnum.DocumentType.care.getIndex()) {
            tempFile = getResourceFile("/temp/care/index.html");
            path = "care";
            param.setIsShow(DocumentEnum.DocumentShowStatus.show.getIndex());
        } else if (param.getDocumentType().intValue() == DocumentEnum.DocumentType.groupIndex.getIndex()) {
            if (StringUtil.isEmpty(param.getGroupId())) {
                throw new ServiceException("集团Id不能为空");
            }
            tempFile = getResourceFile("/temp/group/index.html");
            path = "group";
            if (Objects.isNull(param.getIsShowImg())) {
                param.setIsShowImg(DocumentEnum.DocumentShowStatus.hide.getIndex());
            }
        } else if (param.getDocumentType().intValue() == DocumentEnum.DocumentType.doctorIndex.getIndex()) {
            if (StringUtils.isBlank(param.getRecommendDoctId())) {
                throw new ServiceException("recommendDoctId不能为空");
            }
            tempFile = getResourceFile("/temp/doctor/index.html");
            path = "doctor";
            param.setIsShowImg(DocumentEnum.DocumentShowStatus.hide.getIndex());
            if (Objects.isNull(param.getIsShow())) {
                param.setIsShow(DocumentEnum.DocumentShowStatus.hide.getIndex());
            }
            DocumentParam dparam = new DocumentParam();
            dparam.setRecommendDoctId(param.getRecommendDoctId());
            docName = dao.getDoctorRecommendInfo(dparam).get("doctName");
            docName = Objects.isNull(docName) ? "" : docName;
        } else {
            throw new ServiceException("暂不支持该类型文档");
        }

        if (Objects.nonNull(param.getExternalAd()) && param.getExternalAd()) {
            needTempFile = false;
        }

        if (Objects.nonNull(param.getIsShowImg()) && param.getIsShowImg().intValue() == DocumentEnum.DocumentShowStatus.show.getIndex()
                && StringUtil.isEmpty(param.getCopyPath())) {
            throw new ServiceException("没有上传封面图，不能设置显示在正文里");
        }

        if (needTempFile && Objects.isNull(tempFile)) {
            throw new ServiceException("没有找到对应的模板文件");
        }

        Date now = new Date();
        Document db = BeanUtil.copy(param, Document.class);
        if (db == null) {
            return null;
        }
        db.setEnabled(DocumentEnum.DocumentEnabledStatus.enabled.getIndex());
        db.setCreateTime(now.getTime());
        db.setLastUpdateTime(now.getTime());
        db.setIsTop(DocumentEnum.DocumentTopStatus.unTop.getIndex());
        db.setWeight(0);
        if (param.getDocumentType() == DocumentEnum.DocumentType.doctorIndex.getIndex()) {
            db.setRecommendDoctId(param.getRecommendDoctId());
        }
        DocumentVO vo = dao.addDcoument(db);

        if (Objects.nonNull(param.getExternalAd()) && param.getExternalAd()) {
            return vo;
        }

        //生成静态文件 ==》更新url
        StringBuffer sf = new StringBuffer();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(tempFile));
            Integer userId = ReqUtil.instance.getUserId();
            User user = userManager.getUser(userId);
            if (user == null) {
                throw new ServiceException("用户不存在！");
            }
            String str = "";
            while ((str = br.readLine()) != null) {
                str = replaceTemplate(str, docName, param.getTitle(), vo.getCreateTime(), param.getDescription(),
                                        author, param.getIsShowImg(), param.getCopyPath(), vo.getContent());
                sf.append(str);
            }
        } catch (IOException ex) {
            throw new ServiceException(ex.getMessage());
        } finally {
            IOUtils.closeQuietly(tempFile);
            IOUtils.closeQuietly(br);
        }
        final String upUrl = PropertiesUtil.getContextProperty("fileserver.upload") + "upload/UploadDocumentServlet";
        String s = "";
        try {
            s = new String(sf.toString().getBytes(), "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//        String url = FileUploadUtil.uploadToFileServer(upUrl, vo.getId() + ".html", path, sf.toString());
        String url = fileUpload.uploadToFileServer(vo.getId() + ".html", path, sf.toString());
        
        param.setId(vo.getId());
        param.setUrl(url);
        vo.setUrl(url);
        dao.updateDocument(param);
        if (vo.getDocumentType().intValue() == DocumentEnum.DocumentType.science.getIndex()) {
            //更新对应树节点类型
            contentTypeDao.updateContentTypeCountByDocumentType(param, "+");
        } else if (param.getDocumentType().intValue() == DocumentEnum.DocumentType.groupIndex.getIndex()) {
            //把集团的banner信息更新到集团里
            dao.updateGroupIndexDesc(param);
        }
        if (param.getDocumentType().intValue() != DocumentEnum.DocumentType.doctorIndex.getIndex()
                && param.getDocumentType().intValue() != DocumentEnum.DocumentType.groupIndex.getIndex()) {
            //更新浏览量
            param.setId(vo.getId());
            param.setDocumentType(vo.getDocumentType());
            param.setVisitor(String.valueOf(ReqUtil.instance.getUserId()));
            dao.updateVisitCount(param);
        }
        return vo;
    }

    @Override
    public Map<String, Object> delDocument(DocumentParam param) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtil.isEmpty(param.getId())) {
            throw new ServiceException("文档Id不能为空");
        }
        boolean result = false;
        //先查下==》再删除
        DocumentVO vo = dao.getDocumentDetail(param);
        if (vo != null) {
            result = true;
        }
        if (result) {
            if (vo.getDocumentType().intValue() == DocumentEnum.DocumentType.groupIndex.getIndex()) {
                if (StringUtil.isEmpty(param.getGroupId())) {
                    throw new ServiceException("集团Id不能为空");
                }
                param.setId("");
                dao.updateGroupIndexDesc(param);
            }
            result = dao.delDocumentById(param);
        }
        if (result) {
            param.setContentType(vo.getContentType());
            param.setDocumentType(vo.getDocumentType());
            contentTypeDao.updateContentTypeCountByDocumentType(param, "-");
            //删除对应静态文件
//			http://192.168.3.7:8081/adv/083938a3fde84dc79a099e049f20e760.html
            delDocumentByVO(vo);
        }

        map.put("status", result);
        return map;
    }

    @Override
    public Map<String, Object> updateDocument(DocumentParam param) {

        boolean needTempFile = true;

        if (StringUtil.isEmpty(param.getId())) {
            throw new ServiceException("文档ID不能为空");
        }
        Map<String, Object> map = Maps.newHashMap();
        DocumentVO vo = dao.getDocumentDetail(param);
        if (vo == null) {
            map.put("status", false);
            return map;
        }
        param.setDocumentType(vo.getDocumentType());
        InputStream tempFile = null;
        String path = "";
        String author = "";
        String docName = "";
        if (param.getDocumentType().intValue() == DocumentEnum.DocumentType.adv.getIndex()) {
            tempFile = getResourceFile("/temp/adv/index.html");
            path = "adv";
            if (param.getIsShow() != null) {
                param.setIsShow(param.getIsShow());
            } else {
                param.setIsShow(DocumentEnum.DocumentShowStatus.hide.getIndex());
            }
        } else if (param.getDocumentType().intValue() == DocumentEnum.DocumentType.science.getIndex()) {
            tempFile = getResourceFile("/temp/science/index.html");
            path = "science";
            param.setIsShow(DocumentEnum.DocumentShowStatus.show.getIndex());
        } else if (param.getDocumentType().intValue() == DocumentEnum.DocumentType.care.getIndex()) {
            tempFile = getResourceFile("/temp/care/index.html");
            path = "care";
            param.setIsShow(DocumentEnum.DocumentShowStatus.show.getIndex());
        } else if (param.getDocumentType().intValue() == DocumentEnum.DocumentType.groupIndex.getIndex()) {
            if (StringUtil.isEmpty(param.getGroupId())) {
                throw new ServiceException("集团Id不能为空");
            }
            tempFile = getResourceFile("/temp/group/index.html");
            path = "group";
            if (param.getIsShow() == null) {
                param.setIsShow(DocumentEnum.DocumentShowStatus.hide.getIndex());
            }
        } else if (param.getDocumentType().intValue() == DocumentEnum.DocumentType.doctorIndex.getIndex()) {
            if (StringUtil.isEmpty(param.getRecommendDoctId())) {
                throw new ServiceException("recommendDoctId不能为空");
            }
            tempFile = getResourceFile("/temp/doctor/index.html");
            path = "doctor";
            param.setIsShowImg(DocumentEnum.DocumentShowStatus.hide.getIndex());
            if (param.getIsShow() == null) {
                param.setIsShow(DocumentEnum.DocumentShowStatus.hide.getIndex());
            }
            DocumentParam dparam = new DocumentParam();
            dparam.setRecommendDoctId(param.getRecommendDoctId());
            docName = dao.getDoctorRecommendInfo(dparam).get("doctName");
            docName = docName == null ? "" : docName;
        } else {
            throw new ServiceException("暂不支持该类型文档");
        }

        if (Objects.nonNull(param.getExternalAd()) && param.getExternalAd()) {
            needTempFile = false;
        }

        if (needTempFile && Objects.isNull(tempFile)) {
            throw new ServiceException("没有找到对应的模板文件");
        }
        if (Objects.nonNull(param.getIsShowImg()) && param.getIsShowImg().intValue() == DocumentEnum.DocumentShowStatus.show.getIndex()
                && StringUtil.isEmpty(param.getCopyPath())) {
            throw new ServiceException("没有上传封面图，不能设置显示在正文里");
        }

        if (Objects.nonNull(param.getExternalAd()) && param.getExternalAd()) {
            param.setId(vo.getId());
            dao.updateDocument(param);

            map.put("status", true);
            return map;
        }

        //生成静态文件 ==》更新url
        StringBuffer sf = new StringBuffer();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(tempFile));
            String str = "";
            while ((str = br.readLine()) != null) {
                str = replaceTemplate(str, docName, param.getTitle(), vo.getCreateTime(), param.getDescription(),
                        author, param.getIsShowImg(), param.getCopyPath(), vo.getContent());
                sf.append(str);
            }
        } catch (IOException ex) {
            throw new ServiceException(ex.getMessage());
        } finally {
            IOUtils.closeQuietly(tempFile);
            IOUtils.closeQuietly(br);
        }
        delDocumentByVO(vo);
//        final String upUrl = PropertiesUtil.getContextProperty("fileserver.upload") + "upload/UploadDocumentServlet";
//        String url = FileUploadUtil.uploadToFileServer(upUrl, vo.getId() + ".html", path, sf.toString());
        String url = fileUpload.uploadToFileServer(vo.getId() + ".html", path, sf.toString());
        param.setId(vo.getId());
        param.setUrl(url);

        dao.updateDocument(param);
        if (vo.getDocumentType().intValue() == DocumentEnum.DocumentType.science.getIndex()) {
            String newType = param.getContentType().trim();
            if (!vo.getContentType().trim().equals(newType)) {
                param.setContentType(vo.getContentType());
                contentTypeDao.updateContentTypeCountByDocumentType(param, "-");
                param.setContentType(newType);
                contentTypeDao.updateContentTypeCountByDocumentType(param, "+");
            }
        }
        map.put("status", true);
        return map;
    }

    @Override
    public DocumentVO getDocumentDetail(DocumentParam param) {
        if (StringUtil.isEmpty(param.getId())) {
            throw new ServiceException("文档Id不能为空");
        }
        DocumentVO vo = dao.getDocumentDetail(param);

        return Objects.isNull(vo) ? new DocumentVO() : vo;
    }

    @Override
    public Document getById(String id) {
        if (StringUtil.isEmpty(id)) {
            throw new ServiceException("文档Id不能为空");
        }
        Document document = dao.getById(id);

        return document;
    }

    @Override
    public Document findByIdSimple(String id) {
        if (StringUtil.isEmpty(id)) {
            throw new ServiceException("文档Id不能为空");
        }
        Document document = dao.findByIdSimple(id);

        return document;
    }

    @Override
    public List<Document> getByIds(List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }

        List<Document> documentList = new ArrayList<Document>(ids.size());
        for (String id : ids) {
            Document document = this.getById(id);
            documentList.add(document);
        }

        return documentList;
    }

    @Override
    public List<Document> findByIdsSimple(List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }

        List<Document> documentList  = dao.findByIdsSimple(ids);
        return documentList;
    }

    @Override
    public DocumentVO getDocumentByGid(String groupId) {
        if (StringUtil.isEmpty(groupId)) {
            throw new ServiceException("集团Id不能为空");
        }
        String docId = dao.getGroupDcoIdByGId(groupId);
        if (StringUtil.isEmpty(docId)) {
            return null;
        }
        DocumentParam param = new DocumentParam();
        param.setId(docId);
        return getDocumentDetail(param);
    }

    @Override
    public Map<String, Object> viewDocument(DocumentParam param) {
        Map<String, Object> map = new HashMap<String, Object>();
        DocumentVO vo = getDocumentDetail(param);
        if (StringUtil.isEmpty(vo.getId())) {
            map.put("status", false);
            map.put("msg", "此文章不存在");
            return map;
        }
        //更新浏览量
        param.setId(vo.getId());
        param.setDocumentType(vo.getDocumentType());
        dao.updateVisitCount(param);
        map.put("status", true);
        map.put("url", vo.getUrl());
        return map;
    }


    @Override
    public PageVO getDocumentList(DocumentParam param) {
        PageVO page = new PageVO();
        if (param.getDocumentType() == null) {
            return page;
        }
        Map<String, Object> map = dao.getDocumentList(param);
        page.setPageData((List<ContentTypeVO>) map.get("list"));
        page.setPageIndex(param.getPageIndex());
        page.setPageSize(param.getPageSize());
        page.setTotal(Long.parseLong(map.get("count").toString()));
        return page;
    }

    @Override
    public PageVO getHealthSicenceDocumentList(String contentType, String kw, int pageIndex, int pageSize) {
        PageVO vo = dao.getHealthSicenceDocumentPage(contentType, kw, pageIndex, pageSize);
        return vo;
    }

    @Override
    public Map<String, Object> getAllTypeByDocumentType(DocumentParam param) {
        if (param.getDocumentType() == null) {
            throw new ServiceException("文档类型不能为空");
        }
        return contentTypeDao.getAllTypeByDocumentType(param);
    }

    @Override
    public List<DocumentType> getAllTypeByHealthSicenceDocument() {
        List<DocumentType> typeList = contentTypeDao.getAllTypeByHealthSicenceDocument();
        return typeList;
    }

    @Override
    public Map<String, Object> setADVShowStatus(DocumentParam param) {
//		如果不显示，则权重清零
        if (StringUtil.isEmpty(param.getId())) {
            throw new ServiceException("Id 不能为空");
        }
        if (param.getIsShow() == null) {
            throw new ServiceException("显示状态不能为空");
        }
        Map<String, Object> map = new HashMap<String, Object>();
        DocumentVO vo = getDocumentDetail(param);
        if (StringUtil.isEmpty(vo.getId())) {
            map.put("status", false);
            map.put("msg", "没有找到对应的文档");
            return map;
        }
        if (vo.getDocumentType() != DocumentEnum.DocumentType.adv.getIndex()) {
            map.put("status", false);
            map.put("msg", "该文章不是患者广告类文档，不能修改可见性");
            return map;
        }
        param.setDocumentType(vo.getDocumentType());
        param.setWeight(vo.getWeight());
        if (param.getIsShow() == DocumentEnum.DocumentShowStatus.hide.getIndex()) {
            param.setWeight(0);
        } else if (param.getIsShow() == DocumentEnum.DocumentShowStatus.show.getIndex()) {
            List<DocumentVO> list = getWeightByDocuType(param);
            if (list.isEmpty()) {
                param.setWeight(1);
            } else {
                if (list.size() == DocumentEnum.MAX_TOP) {
                    map.put("status", false);
                    map.put("msg", "显示文章超过上限");
                    return map;
                }
                param.setWeight(list.get(0).getWeight() + 1);
            }
        }
        map.put("status", dao.setShowOrTopStatus(param));
        return map;
    }

    @Override
    public Map<String, Object> getTopScienceList(DocumentParam param) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<DocumentVO> list = dao.getTopScienceList(param);
        map.put("count", list.size());
        map.put("list", list);
        return map;
    }

    @Override
    public Map<String, Object> setTopScience(DocumentParam param) {
        if (StringUtil.isEmpty(param.getId())) {
            throw new ServiceException("文档ID不能为空");
        }
        if (param.getIsTop() == null) {
            throw new ServiceException("置顶状态不能为空");
        }
        Map<String, Object> map = new HashMap<String, Object>();
        DocumentVO vo = getDocumentDetail(param);
        if (StringUtil.isEmpty(vo.getId())) {
            map.put("status", false);
            map.put("msg", "没有找到对应的文档");
            return map;
        }
        if (vo.getDocumentType() != DocumentEnum.DocumentType.science.getIndex()) {
            map.put("status", false);
            map.put("msg", "该文章不是科普类文档，不能修改置顶状态");
            return map;
        }
        param.setDocumentType(vo.getDocumentType());
        if (param.getIsTop() == DocumentEnum.DocumentTopStatus.unTop.getIndex()) {
            param.setWeight(0);
        } else if (param.getIsTop() == DocumentEnum.DocumentTopStatus.top.getIndex()) {
            List<DocumentVO> list = getWeightByDocuType(param);
            if (list.isEmpty()) {
                param.setWeight(1);
            } else {
                if (list.size() == DocumentEnum.MAX_TOP) {
                    map.put("status", false);
                    map.put("msg", "置顶文章超过上限");
                    return map;
                }
                param.setWeight(list.get(0).getWeight() + 1);
            }
        }
        map.put("status", dao.setShowOrTopStatus(param));
        return map;
    }

    /**
     * 升序权重排列
     *
     * @param param
     * @return
     */
    public List<DocumentVO> getWeightByDocuType(DocumentParam param) {
        if (param.getDocumentType() == null) {
            throw new ServiceException("文档类型不能为空");
        }
        return dao.getWeight(param);
    }

    @Override
    public Map<String, Object> changeWeight(DocumentParam param, String type) {
        if (StringUtil.isEmpty(type)) {
            throw new ServiceException("移动方向不明");
        }
        Map<String, Object> map = new HashMap<String, Object>();
        DocumentVO vo = getDocumentDetail(param);
        if (StringUtil.isEmpty(vo.getId())) {
            map.put("status", false);
            map.put("msg", "没有找到该文章");
            return map;
        }
        param.setDocumentType(vo.getDocumentType());
        List<DocumentVO> list = getWeightByDocuType(param);
        if (list.isEmpty()) {
            map.put("status", false);
            map.put("msg", "没有任何可已置顶或者显示的文章");
            return map;
        } else {
            Integer before = null, now = null, after = null;
            for (int i = 0; i < list.size(); i++) {
                DocumentVO temp = list.get(i);
                if (temp.getId().equals(vo.getId())) {
                    now = i;
                    break;
                }
            }
            if (now == null) {
                map.put("status", false);
                map.put("msg", "先设置可见或者置顶状态");
                return map;
            }

            before = now - 1;
            after = now + 1;
            int weight = vo.getWeight();
            DocumentVO dVO = null;
            if (type.trim().equals("+")) {
                if (before < 0) {
                    map.put("status", false);
                    map.put("msg", "已经在第一行，无法上移！");
                    return map;
                }
                dVO = list.get(before);
                param.setIsTop(DocumentEnum.DocumentTopStatus.top.getIndex());
                param.setIsShow(DocumentEnum.DocumentShowStatus.show.getIndex());
            } else if (type.trim().equals("-")) {
                if (after > list.size()) {
                    map.put("status", false);
                    map.put("msg", "已经在最后一行，无法下移！");
                    return map;
                }
                dVO = list.get(after);
                param.setIsTop(DocumentEnum.DocumentTopStatus.unTop.getIndex());
                param.setIsShow(DocumentEnum.DocumentShowStatus.hide.getIndex());
            }
            if (dVO == null) {
                map.put("status", false);
                map.put("msg", "未知原因失败");
                return map;
            }

            param.setWeight(dVO.getWeight());
            dao.setShowOrTopStatus(param);
            param.setId(dVO.getId());
            param.setWeight(weight);
            dao.setShowOrTopStatus(param);

            map.put("status", true);
        }
        return map;
    }

    public void delDocumentByVO(DocumentVO docu) {
        if (docu == null) {
            return;
        }
        String url = docu.getUrl();
        if (!StringUtil.isEmpty(url)) {
            String path = null;
            if (docu.getDocumentType() == DocumentEnum.DocumentType.adv.getIndex()) {
                path = "adv";
            } else if (docu.getDocumentType() == DocumentEnum.DocumentType.science.getIndex()) {
                path = "science";
            } else if (docu.getDocumentType() == DocumentEnum.DocumentType.care.getIndex()) {
                path = "care";
            } else if (docu.getDocumentType() == DocumentEnum.DocumentType.doctorIndex.getIndex()) {
                path = "doctor";
            } else if (docu.getDocumentType() == DocumentEnum.DocumentType.groupIndex.getIndex()) {
                path = "group";
            }
            if (path == null) {
                throw new ServiceException("类型不正确");
            }
            String fileName = url.substring(url.lastIndexOf("/"));
            //外部广告不不会生成文档，这里不做文件是否存在的检验
            //throw new ServiceException("文件名不正确");
            if (StringUtils.isNotBlank(fileName) && fileName.indexOf(".") != -1) {
//                final String upUrl = PropertiesUtil.getContextProperty("fileserver.upload") + "upload/delDocument";
//                FileUploadUtil.delDocument(upUrl, fileName, path);
                fileUpload.delDocument(fileName, path);
            }
        }
    }

    @Override
    public DocumentVO addGoupIndexDoc(DocumentParam param) {
        //图片
        //是否显示在正文
        //正文
        //文件类型

        return null;
    }

    @Override
    public Map<String, Object> updateRecommendDocument(DocumentParam param) {
        if (StringUtil.isEmpty(param.getRecommendDoctId())) {
            throw new ServiceException("recommendId 不能为空");
        }

        Document doc = dao.getDocumentByRecommendId(param.getRecommendDoctId());
        Map<String, Object> map = new HashMap<String, Object>();
        if (doc == null) {
            param.setDocumentType(DocumentEnum.DocumentType.doctorIndex.getIndex());
            addDcoument(param);
            map.put("result", true);
            map.put("msg", "操作成功");
            return map;
        }

        InputStream tempFile = getResourceFile("/temp/doctor/index.html");
        if (tempFile == null) {
            throw new ServiceException("没有找到对应的模板文件");
        }
        String path = "doctor";
        if (param.getIsShow() == null) {
            param.setIsShow(DocumentEnum.DocumentShowStatus.hide.getIndex());
        }

//			//生成静态文件 ==》更新url
        StringBuffer sf = new StringBuffer();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(tempFile));
                /*Integer userId = ReqUtil.instance.getUserId();
                User user = userManager.getUser(userId);*/
            String str = "";
            while ((str = br.readLine()) != null) {
                if (str.contains("{{date}}")) {
                    final String dateStr = DateUtil.formatDate2Str(doc.getCreateTime(),
                            DateUtil.FORMAT_YYYY_MM_DD);
                    str = str.replace("{{date}}", dateStr);
                }

                if (str.contains("{{content}}")) {
                    if (param.getContent() != null) {
                        str = str.replace("{{content}}", param.getContent());
                    } else {
                        str = str.replace("{{content}}", "");
                    }
                }

                if (str.contains("{{doctorName}}")) {
                    if (StringUtils.isNotEmpty(param.getDoctorName())) {
                        str = str.replace("{{doctorName}}", param.getDoctorName());
                    } else {
                        str = str.replace("{{doctorName}}", "");
                    }
                }

                sf.append(str);
            }
        } catch (IOException ex) {
            throw new ServiceException(ex.getMessage());
        } finally {
            IOUtils.closeQuietly(tempFile);
            IOUtils.closeQuietly(br);
        }
        DocumentVO vo = BeanUtil.copy(doc, DocumentVO.class);
        delDocumentByVO(vo);
//        final String upUrl = PropertiesUtil.getContextProperty("fileserver.upload") + "upload/UploadDocumentServlet";
//        String url = FileUploadUtil.uploadToFileServer(upUrl, vo.getId() + ".html", path, sf.toString());
        String url = fileUpload.uploadToFileServer(vo.getId() + ".html", path, sf.toString());
        param.setId(vo.getId());
        param.setUrl(url);
        param.setId(doc.getId().toString());
        dao.updateDocument(param);
        map.put("status", true);
        return map;
    }

    private String uploadQiniu(String fileSource, String fileNameDest) {
        String toUrl = QiniuUtil.upload(fileSource, fileNameDest, DocumentEnum.DEFAULT_BUCKET());//上传
        if (!StringUtil.isEmpty(toUrl)) {
            toUrl = "http://" + DocumentEnum.DEFALUT_DOMAIN() + "/" + toUrl;
            return toUrl;
        }
        return "";
    }

    String replaceTemplate(String str, String doctorName, String title, long createTime, String summary, String author, int showImg, String imgSrc, String content) {
        if (str.contains("{{doctorName}}")) {
            str = str.replace("{{title}}", doctorName);
        }
        if (str.contains("{{title}}")) {
            str = str.replace("{{title}}", title);
        }
        if (str.contains("{{date}}")) {
            final String dateStr = DateUtil.formatDate2Str(createTime, DateUtil.FORMAT_YYYY_MM_DD);
            str = str.replace("{{date}}", dateStr);
        }
        if (str.contains("{{summary}}")) {
            str = str.replace("{{summary}}", summary);
        }
        //jhj date：2016年3月31日14:33:50 模板新增用户名
        if (str.contains("{{author}}")) {
            str = str.replace("{{author}}", author);
        }
        if (str.contains("{{imgSrc}}")) {
            if (showImg == DocumentEnum.DocumentShowStatus.show.getIndex()) {
                str = str.replace("{{imgSrc}}", imgSrc);
            } else {
                str = str.replace("<img src=\"{{imgSrc}}\" class=\"font-img\">", "");
            }
        }
        if (str.contains("{{content}}")) {
            if (StringUtils.isNotBlank(content)) {
                str = str.replace("{{content}}", content);
            } else {
                str = str.replace("{{content}}", "");
            }
        }
        return str;
    }
}
