package com.dachen.health.pack.pack.service.impl;

import com.dachen.careplan.api.client.CarePlanApiClientProxy;
import com.dachen.careplan.api.entity.CCarePlan;
import com.dachen.careplan.api.entity.CCarePlanEnum;
import com.dachen.commons.constants.Constants;
import com.dachen.commons.constants.UserSession;
import com.dachen.commons.exception.ServiceException;
import com.dachen.drug.api.client.DrugApiClientProxy;
import com.dachen.drug.api.entity.CGoodsView;
import com.dachen.health.base.constant.UserChangeTypeEnum;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.base.service.IBaseUserService;
import com.dachen.health.commons.constants.GroupEnum;
import com.dachen.health.commons.constants.GroupEnum.GroupSkipStatus;
import com.dachen.health.commons.constants.PackEnum;
import com.dachen.health.commons.constants.PackEnum.PackStatus;
import com.dachen.health.commons.constants.PackEnum.PackType;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.utils.ServiceItemUtil;
import com.dachen.health.commons.vo.CarePlanDoctorVO;
import com.dachen.health.commons.vo.User;
import com.dachen.health.group.common.util.GroupUtil;
import com.dachen.health.group.fee.entity.vo.FeeVO;
import com.dachen.health.group.fee.service.IFeeService;
import com.dachen.health.group.group.dao.IGroupDao;
import com.dachen.health.group.group.dao.IGroupDoctorDao;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.entity.po.GroupDoctor;
import com.dachen.health.group.group.entity.po.GroupServiceItem;
import com.dachen.health.group.group.entity.vo.DoctorInfoDetailsVO;
import com.dachen.health.group.group.service.IGroupSearchService;
import com.dachen.health.group.group.service.IGroupService;
import com.dachen.health.group.group.service.IGroupServiceItemService;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.health.pack.pack.entity.po.*;
import com.dachen.health.pack.pack.entity.vo.PackVO;
import com.dachen.health.pack.pack.mapper.PackDrugMapper;
import com.dachen.health.pack.pack.mapper.PackMapper;
import com.dachen.health.pack.pack.service.IPackDoctorService;
import com.dachen.health.pack.pack.service.IPackService;
import com.dachen.health.system.entity.param.DoctorCheckParam;
import com.dachen.im.server.data.ImgTextMsg;
import com.dachen.im.server.enums.SysGroupEnum;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mobsms.sdk.MobSmsSdk;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

@Service
public class PackServiceImpl implements IPackService {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	protected PackMapper packMapper;

	@Autowired
	protected IPackDoctorService packDoctorService;

	@Autowired
    protected PackDrugMapper packDrugMapper;

	@Autowired
    protected IFeeService feeService;

	@Resource
    protected MobSmsSdk modSmsSdk;

	@Autowired
    protected IGroupService groupService;

	@Autowired
    protected IGroupDao groupDao;

	@Autowired
    protected IBusinessServiceMsg businessServiceMsg;

	@Autowired
    protected IBaseUserService baseUserService;

	@Autowired
    protected IBaseDataService baseDataService;

	@Resource
	protected CarePlanApiClientProxy carePlanApiClientProxy;

	@Autowired
    protected IGroupServiceItemService groupItemService;

	@Autowired
    protected IGroupDoctorDao gdocDao;

	/**
	 * </p>
	 * 根据id查找套餐
	 * </p>
	 * 
	 * @param id
	 * @return
	 * @author qujunli
	 * @date 2015年8月11日
	 */
	public Pack getPack(Integer id) {
		Pack pack = packMapper.selectByPrimaryKey(id);
		if (pack == null) {
			throw new ServiceException("无此套餐！");
		}
		return pack;
	}

	public int countByDoctorIdAndCareTemlateId(Integer doctorId, String careTemplateId) {
		Pack queryPack = new Pack();
		queryPack.setDoctorId(doctorId);
		queryPack.setStatus(PackStatus.open.getIndex());
		queryPack.setCareTemplateId(careTemplateId);
		int count = packMapper.countCarePack(queryPack);
		return count;
	}

	public void addPackIfMessageNull(Integer userId) throws HttpApiException {
		Pack doctorPack = getDoctorPackByType(userId, PackEnum.PackType.message.getIndex());
		if (doctorPack == null) {
			doctorPack = new Pack();
			doctorPack.setName(PackEnum.PackType.message.getTitle());
			doctorPack.setPrice(3000L);
			doctorPack.setPackType(PackEnum.PackType.message.getIndex());
			doctorPack.setDoctorId(userId);
			this.addPack(doctorPack);
		}
	}

	/**
	 * 新增套餐 12-25 给医生关怀计划套餐单独生成二维码地址 pack.setQrcodePath("");
	 * @throws HttpApiException 
	 */
	@Override
	public Map<String, Object> addPack(Pack pack) throws HttpApiException {
		String tag = "addPack";
		if (logger.isInfoEnabled()) {
			logger.info("{}. careTemplateId={}, doctorId={}, groupId={}, packType={}", tag, pack.getCareTemplateId(),
					pack.getDoctorId(), pack.getGroupId(), pack.getPackType());
		}
		if (pack.getReplyCount() == null || pack.getReplyCount() == 0) {
			pack.setReplyCount(3);
		}
		if (pack.getPackType() == PackType.careTemplate.getIndex()) {
			// 检查医生是否添加该关怀计划
			int count = this.countByDoctorIdAndCareTemlateId(pack.getDoctorId(), pack.getCareTemplateId());
			if (count > 0) {
				throw new ServiceException("您已经添加了该关怀计划！");
			}
		}

		// 只能有一个开启的同类型套餐( 仅限图文资讯和电话资讯)
		if (pack.getPackType() == PackType.message.getIndex() || pack.getPackType() == PackType.phone.getIndex()) {
			Pack queryPack = new Pack();
			queryPack.setDoctorId(pack.getDoctorId());
			// queryPack.setStatus(PackStatus.open.getIndex());
			queryPack.setPackType(pack.getPackType());
			List<Pack> packList = packMapper.query(queryPack);
			if (packList != null && packList.size() > 0) {
				Collections.sort(packList, (o1, o2) -> {
					if (o1.getStatus() == PackStatus.open.getIndex() || o2.getStatus() == PackStatus.open.getIndex())
						throw new ServiceException("已存在开启状态的同类型套餐！");
					return o2.getId() - o1.getId();
				});
				if (pack.getPackType() == PackType.message.getIndex())
					pack.setReplyCount(packList.get(0).getReplyCount());
			}
		} else if (pack.getPackType() == PackType.careTemplate.getIndex()
				|| pack.getPackType() == PackType.followTemplate.getIndex()) {
			// 关怀Id和随访Id都保存在care_template_id中，废弃follow_template_id
			if (StringUtil.isBlank(pack.getCareTemplateId())) {
				throw new ServiceException("健康关怀Id为空");
			}

			// 创建健康关怀套餐
			try {
				CCarePlan carePlanCopy = carePlanApiClientProxy.copy(pack.getCareTemplateId());
				pack.setHelpTimes(carePlanCopy.getHelpTimes());
				pack.setReplyCount(carePlanCopy.getReplyCount()); // 赠送咨询次数，added
																	// by
																	// xiaowei,
																	// 161206
				pack.setName(carePlanCopy.getName());
				pack.setPrice(Long.valueOf(carePlanCopy.getPrice()));
				pack.setDescription(carePlanCopy.getDigest());
				pack.setGroupId(carePlanCopy.getGroupId());
				// 记录源健康关怀的ID，用以标记“已添加”
				pack.setFollowTemplateId(pack.getCareTemplateId());
				pack.setCareTemplateId(carePlanCopy.getId().toString()); // 以copy的carePlanId为准
			} catch (HttpApiException e) {
				logger.error(e.getMessage(), e);
				throw new HttpApiException("拷贝关怀计划失败，请稍候再试");
			}

		}

		// 图文资讯或者电话资讯，需要读取医生所属集团的套餐范围价格，设置的套餐价格超过范围自动取上下限
		long price = checkAndUpdatePrice(pack, pack.getPrice());
		pack.setPrice(price);

		if (pack.getName() == null) {
			throw new ServiceException("套餐必须设置名称！");
		}
		if (pack.getPackType() == null) {
			throw new ServiceException("套餐必须设置类型！");
		}
		if (pack.getPrice() == null) {
			throw new ServiceException("套餐必须设置价格！");
		}

		pack.setCreateTime(System.currentTimeMillis());
		if (pack.getStatus() == null) {
			pack.setStatus(PackStatus.open.getIndex()); // 默认套餐是开通状态
		}

		// 插入套餐数据
		packMapper.insert(pack);

		userRepository.updateDoctorServiceStatus(pack.getDoctorId(), UserEnum.ServiceStatus.open.getIndex());

		if (pack.getPackType() == PackType.careTemplate.getIndex()) {
			// 添加二维链接--------保存在t_qr_code表中
			// pack.setQrcodePath(qrCodeService.generateQr(pack.getId()+"",
			// "care"));//添加
			// packMapper.updateByPrimaryKey(pack);
			// 将模板中的药添加到套餐中-------先去掉PackDrug
			// List<String> drugIds =
			// careTemplateService.findCareDrug(pack.getCareTemplateId());
			// addPackDrug(pack.getId(),drugIds);

			// 套餐专家组
			PackDoctor record = new PackDoctor(pack);
			// PackDoctor record=new PackDoctor();
			// record.setPackId(pack.getId());
			// record.setDoctorId(pack.getDoctorId());
			// record.setSplitRatio(100);
			// record.setReceiveRemind(0); // 默认不接受提醒
			packDoctorService.insert(record);
		}

		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("packId", pack.getId());
		map.put("price", pack.getPrice()); // 增加返回 套餐价格
		map.put("replyCount", pack.getReplyCount());
		return map;
	}

	public Pack addFeeBillPack(Pack pack) {
		if (StringUtil.isBlank(pack.getServiceItemId())) {
			throw new ServiceException("服务项Id不能为空");
		}
		if (StringUtil.isBlank(pack.getGroupId())) {
			throw new ServiceException("集团Id不能为空");
		}
		pack.setPackType(PackType.feeBill.getIndex());
		List<String> serviceItemIds = ServiceItemUtil.toList(pack.getServiceItemId());
		Collections.sort(serviceItemIds);

		pack.setPrice(calcPrice(serviceItemIds, pack.getGroupId()));
		pack.setServiceItemId(ServiceItemUtil.toString(serviceItemIds));
		pack.setCreateTime(System.currentTimeMillis());
		pack.setStatus(PackStatus.close.getIndex());
		pack.setName(PackType.feeBill.getTitle());
		packMapper.insert(pack);
		return pack;
	}

	private long calcPrice(List<String> serviceItemIds, String groupId) {
		long totalAmt = 0;
		for (String serviceItemId : serviceItemIds) {
			int amt = 0;
			if (serviceItemId.indexOf("#") != -1) {
				String id = serviceItemId.split("#")[0];
				GroupServiceItem groupItem = groupItemService.getGroupServiceItem(groupId, null, id);
				amt = groupItem.getPrice() * Integer.valueOf(serviceItemId.split("#")[1]);
			} else {
				GroupServiceItem groupItem = groupItemService.getGroupServiceItem(groupId, null, serviceItemId);
				amt = groupItem.getPrice();
			}
			totalAmt = totalAmt + amt;
		}
		return totalAmt;
	}

	public void deletePackDrug(Integer packDrugId) {
		if (packDrugId == null) {
			throw new ServiceException("用药Id参数不能为空!");
		}
		packDrugMapper.deleteByPrimaryKey(packDrugId);
	}

	public void deletePackDrugById(Integer packId) {
		PackDrugExample packDrugExample = new PackDrugExample();
		packDrugExample.createCriteria().andPackIdEqualTo(packId);
		packDrugMapper.deleteByExample(packDrugExample);
	}

	public void addPackDrug(Integer packId, List<String> drugIds) {
		if (packId == null || drugIds == null) {
			throw new ServiceException("用药输入参数不能为空!");
		}
		deletePackDrugById(packId);
		for (String drugId : drugIds) {
			PackDrug packDrug = new PackDrug();
			packDrug.setDrugId(drugId);
			packDrug.setPackId(packId);
			packDrugMapper.insert(packDrug);
		}
	}

	@Autowired
	protected DrugApiClientProxy drugApiClientProxy;

	@Override
	public List<CGoodsView> findPackDrugView(Integer packId, String access_tonke) throws HttpApiException {
		List<CGoodsView> drugViews = new ArrayList<CGoodsView>();
		PackDrugExample packDrugExample = new PackDrugExample();
		packDrugExample.createCriteria().andPackIdEqualTo(packId);
		List<PackDrug> packDrugs = packDrugMapper.selectByExample(packDrugExample);
		for (PackDrug packDrug : packDrugs) {
			CGoodsView goodsViews = drugApiClientProxy.getDrugUsage(packDrug.getDrugId());
			if (goodsViews != null) {
				goodsViews.setId(packDrug.getDrugId());
				drugViews.add(goodsViews);
			}
		}
		return drugViews;
	}

	@Override
	public void deletePack(Integer id) {
		if (id == null) {
			throw new ServiceException("删除套餐必须设置id！");
		}
		Pack dbPack = this.getPack(id);
		dbPack.setStatus(PackStatus.close.getIndex());

		packMapper.updateByPrimaryKey(dbPack);
	}

	@Override
	public Map updatePack(Pack pack) {
		if (pack.getId() == null) {
			throw new ServiceException("修改套餐必须设置id！");
		}
		Pack dbPack = this.getPack(pack.getId());

		if (pack.getHelpTimes() != null)
			dbPack.setHelpTimes(pack.getHelpTimes());
		if (pack.getName() != null)
			dbPack.setName(pack.getName());
		if (pack.getStatus() != null)
			dbPack.setStatus(pack.getStatus());
		if (pack.getDescription() != null)
			dbPack.setDescription(pack.getDescription());
		if (pack.getTimeLimit() != null) {
			dbPack.setTimeLimit(pack.getTimeLimit());
		}
		if (pack.getIsSearched() != null) {
			dbPack.setIsSearched(pack.getIsSearched());
		}
		if (pack.getPrice() != null) {
			if (dbPack.getPackType() == PackEnum.PackType.careTemplate.getIndex()) {
				try {
					CCarePlan carePlan = carePlanApiClientProxy.findById(dbPack.getCareTemplateId());
					if (carePlan != null) {
						if (!feeService.checkFeeIsCarePlan(carePlan.getGroupId(), pack.getPrice() + "")) {
							throw new ServiceException("请设置集团范围内的套餐价格！");
						}
					}
				} catch (HttpApiException e) {
					logger.error(e.getMessage(), e);
				}
				dbPack.setPrice(pack.getPrice());
			} else if (dbPack.getPackType() == PackEnum.PackType.appointment.getIndex()) {
				if (!pack.getPrice().equals(dbPack.getPrice())) {
					checkAndUpdatePrice(dbPack, pack.getPrice());// 验证价格范围
					dbPack.setPrice(pack.getPrice());
					pack.setPrice(pack.getPrice());
				} else {
					dbPack.setPrice(pack.getPrice());
					pack.setPrice(pack.getPrice());
				}
			} else {
				// 图文资讯或者电话资讯，需要读取医生所属集团的套餐范围价格，设置的套餐价格超过范围自动取上下限
				long price = checkAndUpdatePrice(dbPack, pack.getPrice());
				dbPack.setPrice(price);
				pack.setPrice(price);
			}
		}

		// 是否开启答题留言
		// if (null != pack.getIfLeaveMessage()) {
		// if (dbPack.getPackType() ==
		// PackEnum.PackType.careTemplate.getIndex()) {
		// HealthCarePlan carePlan =
		// carePlanService.getCarePlanById(dbPack.getCareTemplateId());
		// carePlan.setIfLeaveMessage(pack.getIfLeaveMessage());
		// carePlanService.save(carePlan);
		// }
		// }

		/* 修改聊天次数 */
		if (pack.getReplyCount() != null)
			dbPack.setReplyCount(pack.getReplyCount());

		if (null != pack.getPrice() && 0 != pack.getPrice()) {
			dbPack.setReplyCount(null); // 当价格为收费时，可以无限次咨询，null表示无限次咨询
		}

		if (pack.getPoint() != null) {
			dbPack.setPoint(pack.getPoint());//修改积分
		}
		packMapper.updateByPrimaryKey(dbPack);

		Map map = new HashMap();
		// 增加返回 套餐价格
		map.put("price", pack.getPrice());
		return map;
	}

	/**
	 * 套餐查询
	 * 
	 * @param param
	 * @return
	 */
	@Override
	public List<Pack> queryPack(Pack param) {
		String tag = "queryPack";
		if (logger.isInfoEnabled()) {
			logger.info("{}. doctorId={}, packType={}, status={}", tag, param.getDoctorId(), param.getPackType(),
					param.getStatus());
		}

		// 过滤屏蔽的集团下的套餐 TODO: 操作数据量太大，需要优化
		List<String> skipGroupIds = groupDao.getIdListBySkipAndType(GroupSkipStatus.skip, GroupEnum.GroupType.group);
		if (logger.isInfoEnabled()) {
			logger.info("{}. skipGroupIds={}", tag, skipGroupIds);
		}
		if (!CollectionUtils.isEmpty(skipGroupIds)) {
			param.setGroupIds(skipGroupIds); // 传入需要过滤的集团id列表，用skipGroupIds名称会更准确
		}

		// 查询医生的套餐列表
		List<Pack> packList = packMapper.query(param);
		if (CollectionUtils.isEmpty(packList)) {
			logger.error("{}. 没找到医生的套餐列表.", tag);
			return null;
		}

		if (logger.isInfoEnabled()) {
			logger.info("{}. packList.size={}", tag, packList.size());
		}

		// 移除状态不对的套餐
		removeDisablePack(packList);

		// 设置套餐中关联的医生数量
		wrapCareTemplatePackDoctorCount(packList);

		if (logger.isInfoEnabled()) {
			logger.info("{}. ret packList.size={}", tag, packList.size());
		}

		// 健康关怀套餐进行排序
		Collections.sort(packList, (o1, o2) -> {
			long p1 = o1.getPrice() == null ? 0 : o1.getPrice(), p2 = o2.getPrice() == null ? 0 : o2.getPrice();
			int r = Long.valueOf(p1 - p2).intValue();
			return r == 0 ? o1.getId() - o2.getId() : r;
		});
		return packList;
	}

	/**
	 * 设置套餐中关联的医生数量
	 * 
	 * @param packList
	 */
	private void wrapCareTemplatePackDoctorCount(List<Pack> packList) {
		if (CollectionUtils.isEmpty(packList)) {
			return;
		}

		Set<Integer> carePackIdList = new HashSet<Integer>(packList.size());
		for (Pack pack : packList) {
			if (pack.getPackType() == PackType.careTemplate.getIndex()) {
				carePackIdList.add(pack.getId());
			}
		}

		if (CollectionUtils.isEmpty(carePackIdList)) {
			return;
		}

		List<PackDoctorGroup> packDoctorGroups = packDoctorService
				.groupByPackIdList(new ArrayList<Integer>(carePackIdList));

		if (CollectionUtils.isEmpty(packDoctorGroups)) {
			return;
		}

		for (Pack pack : packList) {
			if (pack.getPackType() != PackType.careTemplate.getIndex()) {
				continue;
			}
			for (PackDoctorGroup packDoctorGroup : packDoctorGroups) {
				if (pack.getId().equals(packDoctorGroup.getPackId())) {
					pack.setDoctorCount(packDoctorGroup.getCount());
					break;
				}
			}
		}
	}

	/**
	 * 移除非有效的订单
	 * 
	 * @param packList
	 */
	private void removeDisablePack(List<Pack> packList) {
		if (CollectionUtils.isEmpty(packList)) {
			return;
		}

		String groupId = groupDao.getAppointmentGroupId();

		UserSession userSession = ReqUtil.instance.getUser();

		Iterator<Pack> iter = packList.iterator();
		while (iter.hasNext()) {
			// 禁用关怀计划，患者看不到医生添加的关怀计划套餐 from：YHPT-9365 健康关怀与随访细节改动
			Pack pack = iter.next();
			if (!pack.ifValid()) { // 没开通就不需要返回
				iter.remove();
				continue;
			} else if (pack.getPackType() == PackType.careTemplate.getIndex()
					&& (userSession.getUserType() == UserType.patient.getIndex()
							|| userSession.getUserType() == UserType.guest.getIndex())) {

				if (!isValidCareTemplateForPatient(pack)) {
					iter.remove();
					continue;
				}

			}

			/**
			 * 检查集团的预约名医是否开启
			 */
			if (StringUtils.isBlank(groupId) && pack.getPackType() == PackType.appointment.getIndex()
					&& pack.ifValid()) {
				iter.remove();
				continue;
			}
		}
	}

	/**
	 * 该关怀计划套餐是否有效
	 */
	@Override
	@Deprecated
	public boolean isValidCare(Pack pack) {
		// 判断careTemplateId为ObjectId
		if (StringUtil.isNotBlank(pack.getCareTemplateId()) && pack.getCareTemplateId().length() == 24) {

			CCarePlan carePlan;
			try {
				carePlan = carePlanApiClientProxy.findById(pack.getCareTemplateId());
				if (carePlan.getStatus() == 1 && pack.getPrice() != null && pack.getPrice() != 0) {
					return true;
				}
			} catch (HttpApiException e) {
				logger.error(e.getMessage(), e);
			}
		}
		return false;
	}

	@Override
	public boolean isValidCareTemplateForPatient(Pack pack) {

		// update at 2016年9月22日17:00:32 价格为0的需要开放
		/*
		 * if (null == pack.getPrice() || 0 == pack.getPrice()) { // 免费的在患者端不能查看
		 * return false; }
		 */

		// sourcePlan被禁用在患者端也不能查看
		if (null != pack.getFollowTemplateId() && 24 == pack.getFollowTemplateId().length()) {

			CCarePlan sourcePlan;
			try {
				sourcePlan = carePlanApiClientProxy.findById(pack.getFollowTemplateId());
				if (null != sourcePlan && null != sourcePlan.getStatus()
						&& CCarePlanEnum.CarePlanStatus.enabled.getIndex() == sourcePlan.getStatus()) {
					return true;
				} else {
					return false;
				}
			} catch (HttpApiException e) {
				logger.error(e.getMessage(), e);
			}

		}

		// 默认在患者端都不能被查看（老的健康关怀套餐不能被查看）
		return false;
	}

	public List<PackVO> queryPack12(Pack pack) {
		pack.setPackType(PackType.message.getIndex());
		List<Pack> packList = packMapper.query(pack);

		pack.setPackType(PackType.phone.getIndex());
		packList.addAll(packMapper.query(pack));

		return convert(packList);
	}

	@Override
	public Integer queryPack12Point(Pack pack) {

		// 添加积分问诊
		pack.setPackType(PackType.integral.getIndex());
		List<Pack> packList = packMapper.query(pack);

		return packList.size() > 0 ? 1 : 0;
	}

	public List<PackVO> convert(List<Pack> packs) {
		List<PackVO> packvos = new ArrayList<PackVO>();
		for (Pack p : packs) {
			PackVO pvo = new PackVO();
			pvo.setId(p.getId());
			pvo.setName(p.getName());
			pvo.setDoctorId(p.getDoctorId());
			pvo.setImage(p.getImage());
			pvo.setPackType(p.getPackType());
			pvo.setPrice(p.getPrice());
			pvo.setTimeLimit(p.getTimeLimit());
			pvo.setStatus(p.getStatus());
			pvo.setReplyCount(p.getReplyCount());
			packvos.add(pvo);
		}
		return packvos;
	}

	private void sendNotitfyByPackDoctor(Pack pack, List<PackDoctor> updatePackDoctorList,
			List<PackDoctor> checkPackDoctorList) throws HttpApiException {

		User packUser = userRepository.getUser(pack.getDoctorId());

		List<PackDoctor> delDoc = new ArrayList<PackDoctor>();
		List<PackDoctor> newDoc = new ArrayList<PackDoctor>();
		for (PackDoctor packDoctor : updatePackDoctorList) {
			boolean bool = false;
			for (PackDoctor packDoctor1 : checkPackDoctorList) {
				if (packDoctor.getDoctorId().intValue() == packDoctor1.getDoctorId().intValue()) {
					bool = true;
					break;
				}
			}
			if (!bool) {
				newDoc.add(packDoctor);
			}
		}

		for (PackDoctor packDoctor : checkPackDoctorList) {
			boolean bool = false;
			for (PackDoctor packDoctor1 : updatePackDoctorList) {
				if (packDoctor.getDoctorId().intValue() == packDoctor1.getDoctorId().intValue()) {
					bool = true;
					break;
				}
			}
			if (!bool) {
				delDoc.add(packDoctor);
			}
		}

		if (newDoc.size() > 0) {

			for (PackDoctor packDoctor : newDoc) {
				User user = userRepository.getUser(packDoctor.getDoctorId());
				// String
				// content="尊敬的"+user.getName()+"医生，"+packUser.getName()+"医生邀请您参与“"+pack.getName()+"健康关怀计划”服务";
				final String content = baseDataService.toContent("0012", user.getName(), packUser.getName(),
						pack.getName());
				// 发送短信
				modSmsSdk.send(user.getTelephone(), content);
				// IM通知
				List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>();
				ImgTextMsg imgTextMsg = new ImgTextMsg();
				imgTextMsg.setStyle(6);
				imgTextMsg.setTime(System.currentTimeMillis());
				imgTextMsg.setPic(GroupUtil.getInviteMemberImage());
				imgTextMsg.setTitle(UserChangeTypeEnum.DOCOTR_JOIN_CARE_PLAN.getAlias());
				imgTextMsg.setContent(content);
				imgTextMsg.setUrl(null);
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("bizType", 11);
				param.put("bizId", pack.getId());
				imgTextMsg.setParam(param);
				mpt.add(imgTextMsg);
				businessServiceMsg.sendTextMsg(packDoctor.getDoctorId().toString(), SysGroupEnum.TODO_NOTIFY, mpt,null);
			}
		}

		if (delDoc.size() > 0) {
			for (PackDoctor packDoctor : delDoc) {
				User user = userRepository.getUser(packDoctor.getDoctorId());
				String content = "尊敬的" + user.getName() + "医生，" + packUser.getName() + "医生已将您移除出《" + pack.getName()
						+ "健康关怀计划》医生组成员";
				// 发送短信
				modSmsSdk.send(user.getTelephone(), content);
				// IM通知
				List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>();
				ImgTextMsg imgTextMsg = new ImgTextMsg();
				imgTextMsg.setStyle(6);
				imgTextMsg.setTime(System.currentTimeMillis());
				imgTextMsg.setPic(GroupUtil.getInviteMemberImage());
				imgTextMsg.setTitle(UserChangeTypeEnum.DOCOTR_OUT_CARE_PLAN.getAlias());
				imgTextMsg.setContent(content);
				imgTextMsg.setUrl(null);
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("templateId", pack.getCareTemplateId());
				imgTextMsg.setParam(param);
				mpt.add(imgTextMsg);
				businessServiceMsg.sendTextMsg(packDoctor.getDoctorId().toString(), SysGroupEnum.TODO_NOTIFY, mpt,
						null);
			}
		}
	}

	/**
	 * 保存套餐的医生分成设置,先删除之前的，再保存
	 */
	public void savePackDoctor(Integer packId, List<PackDoctor> packDoctorList) throws HttpApiException {
		int totalSplitRatio = 0;
		// boolean isExistRemind = false;
		for (PackDoctor packDoctor : packDoctorList) {
			totalSplitRatio = totalSplitRatio + packDoctor.getSplitRatio();
			if (packDoctor.ifReceiveRemind()) {
				// isExistRemind = true;
				break;
			}
		}
		if (totalSplitRatio != 100) {
			throw new ServiceException("分成比例之和必须等于100%！");
		}
		// if(!isExistRemind) {
		// throw new ServiceException("至少需要设置一位医生接收提醒！");
		// }

		// 将旧的数据删除
		List<PackDoctor> oldPackDotorList = packDoctorService.findAndDeleteByPackId(packId);

		for (PackDoctor packDoctor : packDoctorList) {
			packDoctor.setPackId(packId);
		}

		// 批量插入新的数据（放在同一个事务中处理）
		packDoctorService.insertBatch(packDoctorList);

		// TODO: 以下可以异步处理
		Pack pack = this.getPack(packId);
		// 发送短信和IM通知
		sendNotitfyByPackDoctor(pack, packDoctorList, oldPackDotorList);
	}

	public List<Pack> findPackByType(Integer docId, String groupId, Integer packType) {
		Pack pack = new Pack();
		pack.setDoctorId(docId);
		pack.setPackType(packType);
		pack.setStatus(PackStatus.open.getIndex());

		/* 过滤屏蔽的集团 */
		List<String> skipGroupIds = groupService.getSkipGroupIds();
		if (!CollectionUtils.isEmpty(skipGroupIds)) {
			pack.setGroupIds(skipGroupIds);
		}
		return packMapper.query(pack);
	}
	
	@Override
	public boolean ifAdded(Integer doctorId, String carePlanId, String carePlanSourceId) {
		List<Pack> packList = this.findByDoctorIdAndPackType(doctorId,
				PackType.careTemplate.getIndex());
		if (CollectionUtils.isEmpty(packList)) {
			return false;
		}
		
		for (Pack pack : packList) {
			if (pack.getFollowTemplateId() == null) {
                continue;
            }
			
			if (pack.getFollowTemplateId().equals(carePlanId)) {
				return true;
			}
			
			if (carePlanSourceId != null
					&& pack.getFollowTemplateId().equals(carePlanSourceId)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<Pack> findByDoctorIdAndPackType(Integer doctorId, Integer packType) {
		Pack queryPack = new Pack();
		queryPack.setDoctorId(doctorId);
		queryPack.setPackType(packType);
		queryPack.setStatus(PackStatus.open.getIndex());

		/* 过滤屏蔽的集团 */
		List<String> skipGroupIds = groupService.getSkipGroupIds();
		if (!CollectionUtils.isEmpty(skipGroupIds)) {
			queryPack.setGroupIds(skipGroupIds);
		}
		return packMapper.query(queryPack);
	}

	@Override
	public List<Pack> queryByUserIds(List<Integer> doctorUserIds) {
		// public PackParam(List<String> itmes) {
		List<String> itmes = new ArrayList<String>();
		for (Integer userId : doctorUserIds) {
			itmes.add(userId.toString());
		}
		PackParam param = new PackParam(itmes);
		return packMapper.queryByUserIds(param);
	}

	public Boolean checkPackBy(Integer doctorUserId, String followUpTemplateId) {

		Pack pack = new Pack();
		pack.setDoctorId(doctorUserId);
		pack.setPackType(PackType.followTemplate.getIndex());
		pack.setFollowTemplateId(followUpTemplateId);
		pack.setStatus(1);
		return packMapper.query(pack).size() > 0;
	}

	/**
	 * 找出有预约服务的医生的id
	 */
	@Override
	public Map getPack012Doctors(List<Integer> doctorUserIds) {
		Map doctor012Map = new HashMap();
		List<Pack> packList = packMapper.getPack012Doctors(doctorUserIds);
		for (Pack pack : packList) {
			doctor012Map.put(pack.getDoctorId(), pack.getDoctorId());
		}
		return doctor012Map;
	}

	/**
	 * 集团的收费设置被修改后，处理这个集团里面现有医生的图文套餐、电话套餐、关怀计划套餐 1、查询集团的收费设置，查询集团的医生
	 * 2、根据医生id集合、集团的收费设置，查询不在这个范围内的套餐 3、修改这些套餐的价格 4、推送一条通知给修改的医生
	 * 
	 * 如果不传doctorIds，则默认处理整个集团的
	 * 
	 * @param groupId
	 */
	public void executeFeeUpdate(String groupId, List<Integer> doctorIds) throws HttpApiException {
		if (doctorIds == null) {
			doctorIds = baseUserService.getDoctorIdByGroup(groupId);
		}
		FeeVO feeVO = feeService.get(groupId);

		Group group = groupService.getGroupById(groupId);
		if (group == null) {
			return;
		}
		if (feeVO == null)
			return;
		for (Integer doctorId : doctorIds) {
			boolean isNeedUpdate = false;
			StringBuffer msg = new StringBuffer();
			msg.append("您所在的" + group.getName() + "已设置收费价格范围");
			/**
			 * 您所在医生集团已设置集团的收费价格范围，您的图文咨询服务价格已修改为
			 * {修改后价格}元每次，电话咨询服务价格已修改为{修改后价格}元每次。
			 */
			PackParam2 packParam2 = new PackParam2();
			packParam2.setDoctorId(doctorId);
			packParam2.setTextMin(Long.valueOf(feeVO.getTextMin() != null ? feeVO.getTextMin() : 0));
			packParam2.setTextMax(Long.valueOf(feeVO.getTextMax() != null ? feeVO.getTextMax() : 99999999));
			packParam2.setPhoneMin(Long.valueOf(feeVO.getPhoneMin() != null ? feeVO.getPhoneMin() : 0));
			packParam2.setPhoneMax(Long.valueOf(feeVO.getPhoneMax() != null ? feeVO.getPhoneMax() : 99999999));
			// 暂时不处理关怀计划套餐
			packParam2.setCarePlanMin(Long.valueOf(0));
			packParam2.setCarePlanMax(Long.valueOf(99999999));
			// 找出这个医生所有需要修改的套餐
			List<Pack> packs = packMapper.getFeeUpdatePack(packParam2);
			for (Pack pack : packs) {
				if (pack.getPackType() == PackType.message.getIndex()) {
					if (pack.getPrice() < feeVO.getTextMin()) {
						isNeedUpdate = true;
						pack.setPrice(Long.valueOf(feeVO.getTextMin()));
						packMapper.updatePriceById(pack);
						msg.append("，您的图文咨询服务价格已修改为" + feeVO.getTextMin() / 100 + "元每次");
					} else if (pack.getPrice() > feeVO.getTextMax()) {
						isNeedUpdate = true;
						pack.setPrice(Long.valueOf(feeVO.getTextMax()));
						packMapper.updatePriceById(pack);
						msg.append("，您的图文咨询服务价格已修改为" + feeVO.getTextMax() / 100 + "元每次");
					}

				} else if (pack.getPackType() == PackType.phone.getIndex()) {
					if (pack.getPrice() < feeVO.getPhoneMin()) {
						isNeedUpdate = true;
						pack.setPrice(Long.valueOf(feeVO.getPhoneMin()));
						packMapper.updatePriceById(pack);
						msg.append("，您的电话咨询服务价格已修改为" + feeVO.getPhoneMin() / 100 + "元每次");
					} else if (pack.getPrice() > feeVO.getPhoneMax()) {
						isNeedUpdate = true;
						pack.setPrice(Long.valueOf(feeVO.getPhoneMax()));
						packMapper.updatePriceById(pack);
						msg.append("，您的电话咨询服务价格已修改为" + feeVO.getPhoneMax() / 100 + "元每次");
					}
				} else if (pack.getPackType() == PackType.careTemplate.getIndex()) {
					if (pack.getPrice() < feeVO.getCarePlanMin()) {
						isNeedUpdate = true;
						pack.setPrice(Long.valueOf(feeVO.getCarePlanMin()));
						packMapper.updatePriceById(pack);
						msg.append("，您的关怀计划咨询服务价格已修改为" + feeVO.getCarePlanMin() / 100 + "元每次");
					} else if (pack.getPrice() > feeVO.getCarePlanMax()) {
						isNeedUpdate = true;
						pack.setPrice(Long.valueOf(feeVO.getCarePlanMax()));
						packMapper.updatePriceById(pack);
						msg.append("，您的关怀计划咨询服务价格已修改为" + feeVO.getCarePlanMax() / 100 + "元每次");
					}
				} else if (pack.getPackType() == PackType.appointment.getIndex()) // 名医面对面
																					// add
																					// by
																					// tanyf
																					// 20160623
				{
					if (pack.getPrice() < feeVO.getAppointmentMin()) {
						isNeedUpdate = true;
						pack.setPrice(Long.valueOf(feeVO.getAppointmentMin()));
						packMapper.updatePriceById(pack);
						msg.append("，您的名医面对面服务价格已修改为" + feeVO.getAppointmentMin() / 100 + "元每次");
					} else if (pack.getPrice() > feeVO.getAppointmentMax()) {
						isNeedUpdate = true;
						pack.setPrice(Long.valueOf(feeVO.getAppointmentMax()));
						packMapper.updatePriceById(pack);
						msg.append("，您的名医面对面服务价格已修改为" + feeVO.getAppointmentMax() / 100 + "元每次");
					}
				}
			}
			if (isNeedUpdate) {
				List<ImgTextMsg> list = new ArrayList<ImgTextMsg>();
				ImgTextMsg imgTextMsg = new ImgTextMsg();
				imgTextMsg.setStyle(7);
				imgTextMsg.setTitle("套餐价格被修改");
				imgTextMsg.setTime(System.currentTimeMillis());
				imgTextMsg.setContent(msg.toString());
				list.add(imgTextMsg);
				businessServiceMsg.sendTextMsg(String.valueOf(doctorId), SysGroupEnum.TODO_NOTIFY, list, null);
			}

		}
	}

	/**
	 * 图文套餐和电话套餐，在新增和更新时，价格必须在医生所属集团的价格范围之内，超过就自动取集团范围的上/下限
	 * 
	 * @param pack
	 * @param price
	 *            设置价格
	 * @return
	 * @author wangqiao
	 * @date 2015年12月14日
	 */
	private long checkAndUpdatePrice(Pack pack, long price) {
		if (pack == null || pack.getDoctorId() == null) {
			return price;
		}
		// 只处理图文套餐和电话套餐
		if (pack.getPackType() != PackType.message.getIndex() && pack.getPackType() != PackType.phone.getIndex()
				&& pack.getPackType() != PackType.appointment.getIndex()) {// 增加预约（名医面对面）
			return price;
		}
		// 查询医生所属所有集团的套餐范围
		FeeVO feeVO = feeService.getByDoctorId(pack.getDoctorId());
		if (feeVO == null) {
			return price;
		}
		// 超过集团套餐范围，自动取上下限
		if (pack.getPackType() == PackType.message.getIndex()) {// 图文资讯
			if (feeVO.getTextMin() != null && feeVO.getTextMax() != null) {
				long minPrice = feeVO.getTextMin().longValue();
				long maxPrice = feeVO.getTextMax().longValue();
				if (minPrice > 0) {
					price = Math.max(price, minPrice);
				}
				if (maxPrice != 0) {
					price = Math.min(price, maxPrice);
				}
				return price;
			}
		} else if (pack.getPackType() == PackType.phone.getIndex()) {// 电话资讯
			if (feeVO.getPhoneMin() != null && feeVO.getPhoneMax() != null) {
				long minPrice = feeVO.getPhoneMin().longValue();
				long maxPrice = feeVO.getPhoneMax().longValue();
				if (minPrice > 0) {
					price = Math.max(price, minPrice);
				}
				if (maxPrice != 0) {
					price = Math.min(price, maxPrice);
				}
				return price;
			}
		} else if (pack.getPackType() == PackType.appointment.getIndex()) {// 预约（名医面对面）
			if ((feeVO.getAppointmentMax() != null && price > feeVO.getAppointmentMax())
					|| (feeVO.getAppointmentMin() != null && price < feeVO.getAppointmentMin())) {
				throw new ServiceException("价格设置超出范围！");
			}

			if (feeVO.getAppointmentMin() != null && feeVO.getAppointmentMax() != null) {
				long minPrice = feeVO.getAppointmentMin().longValue();
				long maxPrice = feeVO.getAppointmentMax().longValue();
				if (minPrice > 0) {
					price = Math.max(price, minPrice);
				}
				if (maxPrice != 0) {
					price = Math.min(price, maxPrice);
				}
				return price;
			}
		}

		return price;
	}

	@Override
	public Pack getDoctorPackByType(Integer doctorId, int packType) {
		Map<String, Integer> params = new HashMap<String, Integer>();
		params.put("doctorId", doctorId);
		params.put("packType", packType);
		return packMapper.queryByDoctorIdAndType(params);
	}

	@Override
	public void deleteConsultationPackByDoctorId(Integer doctorId) {
		if (doctorId != null) {
			packMapper.deleteConsultationPackByDoctorId(doctorId);
		}
	}

	public List<Pack> selectPackDortorList(Integer queryType) {
		int packType = PackType.phone.getIndex();
		if (queryType != null) {
			packType = PackType.appointment.getIndex();
		}
		return packMapper.selectPackDortorList(packType);
	}

	@Override
	public Set<Integer> getAllBeSearchDoctorIds(ArrayList<Integer> doctorIds) {
		List<Integer> userIds = packMapper.getAllBeSearchDoctorIds(doctorIds);
		if (userIds != null && userIds.size() > 0) {
			return new HashSet<Integer>(userIds);
		}
		return null;
	}

	@Override
	public void updateConsultationPackPrice(Integer doctorId, long price) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("doctorId", doctorId);
		params.put("price", price);
		packMapper.updateConsultationPackPrice(params);
	}

	@Override
	public List<Integer> getConsultationDoctorId(List<Integer> friendIds) {
		if (friendIds != null && friendIds.size() > 0) {
			return packMapper.getConsultationDoctorId(friendIds);
		}
		return null;
	}

	@Override
	public List<Integer> getConsultationDoctorIdNotInIds(List<Integer> friendIds) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (friendIds != null && friendIds.size() > 0) {
			map.put("friendIds", friendIds);
		}
		return packMapper.getConsultationDoctorIdNotInIds(map);
	}

	@Override
	public Pack getDoctorPackDBData(Integer doctorId, int packType) {
		Map<String, Integer> params = new HashMap<String, Integer>();
		params.put("doctorId", doctorId);
		params.put("packType", packType);
		return packMapper.getDoctorPackDBData(params);
	}

	@Override
	public Set<Integer> getAllConsultationDoctorIds() {
		List<Integer> list = packMapper.getAllConsultationDoctorIds();
		if (list != null) {
			return new HashSet<Integer>(list);
		}
		return null;
	}

	@Override
	public Object getDoctorAppointment(String groupId, Integer doctorId) throws HttpApiException {
		Map<String, Object> map = new HashMap<String, Object>();
		Group g = groupService.getGroupById(groupId);
		if (g != null && g.getConfig() != null && g.getConfig().isOpenAppointment()) {
			/**
			 * 该集团已开通预约套餐
			 */
			Pack pack = this.getDoctorPackDBData(doctorId, PackEnum.PackType.appointment.getIndex());
			if (pack == null) {
				/**
				 * 数据不一致最后的防线
				 */
				pack = new Pack();
				pack.setName(PackEnum.PackType.appointment.getTitle());
				pack.setIsSearched(0);
				FeeVO feevo = feeService.get(groupId);
				// pack.setPrice(Long.valueOf(feevo.getAppointmentMin()==null?0:feevo.getAppointmentMin()));
				pack.setPrice(Long.valueOf(feevo.getAppointmentDefault())); // 预约
																			// 默认价格
				pack.setPackType(PackEnum.PackType.appointment.getIndex());
				pack.setGroupId(groupId);
				pack.setDoctorId(doctorId);
				pack.setStatus(PackEnum.PackStatus.close.getIndex());
				Map<String, Object> rtnMap = this.addPack(pack);
				if (rtnMap.get("packId") != null) {
					pack.setId(Integer.valueOf(rtnMap.get("packId") + ""));
				}
			}
			map.put("pack", pack);
			map.put("isOpen", "1");
		} else {
			map.put("isOpen", "0");
		}
		return map;
	}

	@Override
	public List<Integer> getDocIdsByServiceType(String groupId, String packId) {

		Pack pack = new Pack();
		List<Integer> hasTypeIds = Lists.newArrayList(); // 所有有图文咨询，电话咨询，健康关怀的医生id
		List<Integer> noTypes = Lists.newArrayList(); // 没有服务的医生id

		if (packId.equalsIgnoreCase("Undefined")) {

			// 查找集团下的所有医生id
			List<GroupDoctor> groupDoctors = gdocDao.findDoctorsByGroupId(groupId);
			List<Integer> allDocList = Lists.newArrayList();
			for (GroupDoctor groupDoctor : groupDoctors) {
				allDocList.add(groupDoctor.getDoctorId());
			}

			// 查找集团所有有服务的医生id
			Pack packAll = new Pack();
			packAll.setGroupId(groupId);
			packAll.setStatus(PackStatus.open.getIndex());

			List<Pack> packAlls = queryByUserIds(allDocList);

			for (Pack pk : packAlls) {
				if (pk.getPackType() == 1 || pk.getPackType() == 2
						|| (pk.getPackType() == 3 && pk.getGroupId().equals(groupId))) {
					hasTypeIds.add(pk.getDoctorId());
				}
			}

			// 排除有服务的医生id
			for (Integer id : allDocList) {
				if (!hasTypeIds.contains(id)) {
					noTypes.add(id);
				}
			}

			return noTypes;
		} else {
			pack.setPackType(Integer.valueOf(packId));
			pack.setStatus(PackStatus.open.getIndex());
			List<Pack> packs = packMapper.query(pack);
			for (Pack pk : packs) {
				if (pk.getPackType() == 3 && groupId.equals(pk.getGroupId())) {
					hasTypeIds.add(pk.getDoctorId());
				} else if (pk.getPackType() != 3) {
					hasTypeIds.add(pk.getDoctorId());
				}
			}

			return hasTypeIds;
		}
	}

	@Override
	public void setPackForDoctorVO(DoctorInfoDetailsVO vo) {

		Set<String> result = Sets.newHashSet();

		Pack pack = new Pack();
		pack.setDoctorId(vo.getUser().getUserId());
		pack.setStatus(PackStatus.open.getIndex());

		List<Pack> packs = packMapper.query(pack);
		if (packs != null && packs.size() > 0) {
			for (Pack pk : packs) {
				if (pk.getPackType() == 3) {
					if (pk.getGroupId().equals(vo.getGroup().getId())
							|| pk.getGroupId().equals(Constants.Id.PLATFORM_ID)) {
						result.add("健康关怀");
					}
				} else {
					result.add(pk.getName());
				}
			}
		}

		vo.setPackName(result);
	}

	@Override
	public List<Pack> getByDoctorIdAndPacktype(Integer doctorId, Integer packType) {
		PackParam2 packParam = new PackParam2();
		packParam.setDoctorId(doctorId);
		packParam.setType(packType);
		/*
		 * if (packType.intValue() == 3) { //健康关怀的则获取价格大于0的健康关怀 return
		 * packMapper.getNoFreePackByDoctorIdAndPackType(packParam); }
		 */
		return packMapper.getPackByDoctorIdAndPackType(packParam);
	}

	@Override
	public List<Pack> withRemoveDisablePack(List<Pack> packList) {

		String groupId = groupDao.getAppointmentGroupId();

		UserSession userSession = ReqUtil.instance.getUser();

		Iterator<Pack> iter = packList.iterator();
		while (iter.hasNext()) {
			// 禁用关怀计划，患者看不到医生添加的关怀计划套餐 from：YHPT-9365 健康关怀与随访细节改动
			Pack pack = iter.next();
			if (!pack.ifValid()) { // 没开通就不需要返回
				iter.remove();
				continue;
			} else if (pack.getPackType() == PackType.careTemplate.getIndex()) {
				if (!isValidCareTemplateForPatient(pack)) {
					iter.remove();
					continue;
				}

			}

			/**
			 * 检查集团的预约名医是否开启
			 */
			if (StringUtils.isBlank(groupId) && pack.getPackType() == PackType.appointment.getIndex()
					&& pack.ifValid()) {
				iter.remove();
				continue;
			}
		}
		return packList;
	}

	@Override
	public void saveIntegralPack(Integer doctorId, String goodsGroupId, String goodsGroupName, Integer point,
			Integer status) {
		if (doctorId == null || doctorId == 0) {
			throw new ServiceException("参数doctorId不能为空");
		}
		if (StringUtil.isEmpty(goodsGroupId)) {
			throw new ServiceException("参数goodsGroupId不能为空");
		}
		if (point == null || point == 0) {
			throw new ServiceException("参数point不能为空");
		}
		if (status == null || status == 0) {
			throw new ServiceException("参数status不能为空");
		}
		if (status != PackEnum.PackStatus.open.getIndex() && status != PackEnum.PackStatus.close.getIndex()) {
			throw new ServiceException("参数status不正确");
		}
		Pack pack = getIntegralPack(doctorId, goodsGroupId);
		if (pack == null) {
			pack = new Pack();
			pack.setDoctorId(doctorId);
			pack.setGoodsGroupId(goodsGroupId);
			pack.setName(goodsGroupName);
			pack.setDescription(goodsGroupName);
			pack.setPoint(point);
			pack.setStatus(status);
			pack.setPackType(PackEnum.PackType.integral.getIndex());// 积分问诊
			pack.setCreateTime(System.currentTimeMillis());
			pack.setPrice(0l);
			packMapper.addIntegralPack(pack);
		} else {
			Pack _pack = new Pack();
			_pack.setId(pack.getId());
			_pack.setStatus(status);
			_pack.setPoint(point);
			updatePack(_pack);
		}
	}

	@Override
	public Pack getIntegralPack(Integer doctorId, String goodsGroupId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("doctorId", doctorId);
		map.put("goodsGroupId", goodsGroupId);
		return packMapper.getIntegralPack(map);
	}

	@Override
	public Set<String> getPackNameByDoctorId(Integer doctorId) {
		Set<String> result = new HashSet<>();
		List<Integer> doctors = new ArrayList<>();
		doctors.add(doctorId);
		List<Pack> packs = queryByUserIds(doctors);
		for (Pack pack : packs) {
			if (null != pack.getStatus() && pack.getStatus().intValue() == PackEnum.PackStatus.open.getIndex()) {
				result.add(PackEnum.PackTypeName.getTitle(pack.getPackType()));
			}
		}
		return result;
	}
	
	@Resource
	protected UserRepository userRepository;
	
	@Resource
	protected IGroupSearchService groupSearchService;

	@Override
	public List<CarePlanDoctorVO> findUserInfoByPack(Integer packId) {
		Pack pack = this.getPack(packId);
		List<Integer> packDoctorIdList = packDoctorService.findDoctorIdListByPackId(packId);
		
		List<CarePlanDoctorVO> list = this.userRepository.findDoctorInfoGroup(packDoctorIdList, pack.getDoctorId());
		
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		groupSearchService.wrapGroupNames(list);
		
		Collections.sort(list, new Comparator<CarePlanDoctorVO>() {
            @Override
            public int compare(CarePlanDoctorVO o1, CarePlanDoctorVO o2) {
                if (1 == o1.getGroupType()) {
                    return -1;
                } else if (1 == o2.getGroupType()) {
                    return 1;
                }
                return 0;
            }
        });
		
		return list;
	}

	/**
	 */
	@Override
	public List<String> findCarePlanIdListByDoctor(Integer doctorId) {
		List<Pack> packs = this.findByDoctorIdAndPackType(doctorId, PackType.careTemplate.getIndex());
		if (CollectionUtils.isEmpty(packs)) {
			return null;
		}
		
		Set<String> carePlanIdSet = new HashSet<String>(packs.size());
		for (Pack pack:packs) {
			if (null == pack.getCareTemplateId()) {
				continue;
			}
			carePlanIdSet.add(pack.getCareTemplateId());
			carePlanIdSet.add(pack.getFollowTemplateId());
		}

		return new ArrayList<String>(carePlanIdSet);
	}

	@Override
	public void deletePackByConsultationId(String consultationId) {
		if(StringUtil.isEmpty(consultationId)){
			throw new ServiceException("会诊套餐数据不能为空");
		}
		packMapper.delPackByConsultationId(consultationId);
		
	}

}
