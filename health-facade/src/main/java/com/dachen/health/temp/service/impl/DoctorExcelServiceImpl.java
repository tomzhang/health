package com.dachen.health.temp.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dachen.common.auth.data.AccessToken;
import com.dachen.commons.JSONMessage;
import com.dachen.health.commons.vo.User;
import com.dachen.util.Token;
import com.google.common.collect.Sets;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.*;

import java.util.stream.Collectors;
import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.dachen.commons.support.jedis.JedisTemplate;
import com.dachen.health.commons.constants.GroupEnum;
import com.dachen.health.commons.constants.OrderEnum;
import com.dachen.health.commons.constants.GroupEnum.GroupSkipStatus;
import com.dachen.health.group.group.dao.DoctroExcelDao;
import com.dachen.health.group.group.dao.IGroupDao;
import com.dachen.health.group.group.entity.vo.DoctorExcelVo;
import com.dachen.health.group.group.entity.vo.GroupDoctorReVo;
import com.dachen.health.group.group.entity.vo.GroupExcelVo;
import com.dachen.health.group.group.entity.vo.ItemAnswerInfo;
import com.dachen.health.group.group.entity.vo.OrderExcelVo;
import com.dachen.health.group.group.entity.vo.PatientExcelVo;
import com.dachen.health.group.group.entity.vo.UserExcelVo;
import com.dachen.health.pack.order.entity.param.CheckInParam;
import com.dachen.health.pack.order.entity.param.OrderParam;
import com.dachen.health.pack.order.entity.po.Order;
import com.dachen.health.pack.order.entity.vo.CheckInVO;
import com.dachen.health.pack.order.mapper.CheckInMapper;
import com.dachen.health.pack.order.mapper.OrderMapper;
import com.dachen.health.pack.pack.entity.po.Pack;
import com.dachen.health.pack.pack.entity.po.PackParam2;
import com.dachen.health.pack.pack.mapper.PackMapper;
import com.dachen.health.pack.pack.service.IPackService;
import com.dachen.health.pack.patient.mapper.PatientMapper;
import com.dachen.health.pack.patient.model.OrderSession;
import com.dachen.health.pack.patient.model.Patient;
import com.dachen.health.pack.patient.model.PatientExample;
import com.dachen.health.pack.patient.service.IOrderSessionService;
import com.dachen.health.pack.patient.service.impl.OrderSessionServiceImpl;
import com.dachen.health.temp.service.DoctorExcelService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.web.client.RestTemplate;

@Service
public class DoctorExcelServiceImpl implements DoctorExcelService {

	@Resource
	private DoctroExcelDao doctroExcelDao;

	@Resource
	private PackMapper packMapper;

	@Resource
	private OrderMapper orderMapper;

	@Resource
	private PatientMapper patientMapper;

	@Resource
	private CheckInMapper CheckInMapper;

	@Resource
	private IGroupDao groupDao;

	@Resource
	private IPackService packService;
	
	@Resource
	private IOrderSessionService orderSessionService;
	
	@Autowired
	JedisTemplate jedisTemplate;

	@Autowired
    private RestTemplate restTemplate;

	private static final String GET_USER_CIRCLES = "http://CIRCLE/inner/base/doctorAllCircle/{userId}";

	private Map<String, String> getCircleName(String userId) {
        Map<String, String> result = Maps.newHashMap();
        String url = GET_USER_CIRCLES.replace("{userId}", userId);
        JSONMessage jsonMessage = restTemplate.getForObject(url, JSONMessage.class);
        String str = JSON.toJSONString(jsonMessage.getData());
        if (StringUtils.isNotEmpty(str)) {
            List<SimpleCircleVO> circleVOS = JSONArray.parseArray(str, SimpleCircleVO.class);
            if (CollectionUtils.isEmpty(circleVOS)) {
                return result;
            }
            List<String> circleNames = Lists.newArrayList();
            for (SimpleCircleVO vo : circleVOS) {
                if (Objects.equals(vo.getType(), 2)) {
                    result.put("dept", vo.getName());
                } else {
                    circleNames.add(vo.getName());
                }
            }
            if (!CollectionUtils.isEmpty(circleNames)) {
                result.put("circle", String.join(",", circleNames));
            }
        }
	    return result;
    }

    public Map<Integer, String> getOpenIdMap(List<Integer> userIds) {
	    Map<Integer, String> openIdMap = Maps.newHashMap();
        Integer size = userIds.size();

        if (size < 100) {
            return getOpenIdMap1(userIds);
        }

        //大量使用分页查询
        int index = 0;
        int preSize = 100;

        while (size > index) {
            int last = Math.min(size, preSize + index);
            List<Integer> ids = userIds.subList(index, last);
            Map<Integer, String> tempMap = getOpenIdMap1(ids);
            if (!tempMap.isEmpty()) {
                openIdMap.putAll(tempMap);
            }
            index += preSize;
        }

        return openIdMap;
    }

    public Map<Integer, String> getOpenIdMap1(List<Integer> userIds) {
        Map<Integer, String> openIdMap = Maps.newHashMap();
        if (CollectionUtils.isEmpty(userIds)) {
            return openIdMap;
        }

        AuthParamPO po = new AuthParamPO();
        po.setUserIdList(userIds);

        JSONMessage jsonMessage  = restTemplate.postForObject("http://AUTH2/v2/getOpenIdList", po, JSONMessage.class);
        List<AccessToken> tokens = JSONArray.parseArray(JSON.toJSONString(jsonMessage.getData()), AccessToken.class);
        if (!CollectionUtils.isEmpty(tokens)) {
            for (AccessToken token : tokens) {
                if (StringUtils.isNotEmpty(token.getOpenId())) {
                    openIdMap.put(token.getUserId(), token.getOpenId());
                }
            }
        }
        return openIdMap;
    }

    public Map<String, Map<String, String>> getCircleNameMap(List<String> userIds) {
        Map<String, Map<String, String>> result = Maps.newHashMap();

        int size = userIds.size();
        if (size < 100) {
            getCircleNameMap1(userIds);
        }

        //大量使用分页查询
        int index = 0;
        int preSize = 100;

        while (size > index) {
            int last = Math.min(size, preSize + index);
            List<String> ids = userIds.subList(index, last);
            Map<String, Map<String, String>> tempMap = getCircleNameMap1(ids);
            if (!tempMap.isEmpty()) {
                result.putAll(tempMap);
            }
            index += preSize;
        }

        return result;
    }

    public Map<String, Map<String, String>> getCircleNameMap1(List<String> userIds) {
        Map<String, Map<String, String>> result = Maps.newHashMap();
        if (CollectionUtils.isEmpty(userIds)) {
            return result;
        }

        JSONMessage jsonMessage = restTemplate.postForObject("http://CIRCLE/inner/user/getCircleNameByUserIds", userIds, JSONMessage.class);
        Map<String, Map<String, List<String>>> response = JSON.parseObject(JSON.toJSONString(jsonMessage.getData()), Map.class);

        if (!response.isEmpty()) {

            for (Map.Entry<String, Map<String, List<String>>> entry : response.entrySet()) {
                Map<String, String> nameMap = Maps.newHashMap();
                Map<String, List<String>> value = entry.getValue();
                if (!value.isEmpty()) {
                    List<String> circleNames = value.get("circle");
                    if (!CollectionUtils.isEmpty(circleNames)) {
                        nameMap.put("circle", String.join(",", circleNames));
                    }

                    List<String> departmentNames = value.get("department");
                    if (!CollectionUtils.isEmpty(departmentNames)) {
                        nameMap.put("department", String.join(",", departmentNames));
                    }
                    result.put(entry.getKey(), nameMap);
                }
            }
        }

        return result;
    }

	@Override
	public List<DoctorExcelVo> getDoctorStatVos(Integer status, Long start, Long end) {
		// 1、查询医生的基本信息
		List<DoctorExcelVo> doctorExcelVos = doctroExcelDao.getDoctorExcel(status, start, end);
		// 2、取出doctorid，去c_group_doctor表里面查询group的id
		if (doctorExcelVos != null && doctorExcelVos.size() > 0) {
			List<Integer> doctorIds = new ArrayList<>();
            Set<Integer> inviteOpenIds = new HashSet<>();
            List<String> doctorStringIds = new ArrayList<>();
			for (DoctorExcelVo doctorExcelVo : doctorExcelVos) {
				String doctorId = doctorExcelVo.getId();
				if (StringUtils.isNotEmpty(doctorId)) {
					doctorIds.add(Integer.valueOf(doctorId));
					doctorStringIds.add(doctorId);
				}
				if (StringUtils.isNotEmpty(doctorExcelVo.getInviterId())) {
				    inviteOpenIds.add(Integer.valueOf(doctorExcelVo.getInviterId()));
                }
			}

			//用户openId
            Map<Integer, String> openIdMap = getOpenIdMap(doctorIds);

			//邀请人openId
            Map<Integer, String> inviteOpenIdMap = getOpenIdMap(Lists.newArrayList(inviteOpenIds));

            Map<String, Map<String, String>> circleNameMap = getCircleNameMap(doctorStringIds);

            for (DoctorExcelVo doctorExcelVo : doctorExcelVos) {
                doctorExcelVo.setOpenId(openIdMap.get(Integer.valueOf(doctorExcelVo.getId())));
                if (StringUtils.isNotEmpty(doctorExcelVo.getInviterId())) {
                    doctorExcelVo.setInviteOpenId(inviteOpenIdMap.get(Integer.valueOf(doctorExcelVo.getInviterId())));
                }
                Map<String, String> circleName = circleNameMap.get(doctorExcelVo.getId());
                if (Objects.nonNull(circleName) && !circleName.isEmpty()) {
                    doctorExcelVo.setCircleNames(circleName.get("circle"));
                    doctorExcelVo.setDeptNames(circleName.get("department"));
                }
            }

			if (doctorIds != null && doctorIds.size() > 0) {
				List<GroupDoctorReVo> groupDoctorReVos = doctroExcelDao.getGroupDoctorReVo(doctorIds);

				// 3、查询c_group表, 获取集团名称
				groupDoctorReVos = doctroExcelDao.setGroupName(groupDoctorReVos);

				// 4、查询user表，获取推荐人名称
				groupDoctorReVos = doctroExcelDao.setInviterName(groupDoctorReVos);

				for (DoctorExcelVo doctorExcelVo : doctorExcelVos) {
					String doctorId = doctorExcelVo.getId();

					List<String> groupName = new ArrayList<>();
					List<String> inviterName = new ArrayList<>();
					StringBuffer inviterIds = new StringBuffer();
					if (groupDoctorReVos != null) {
						for (GroupDoctorReVo groupDoctorReVo : groupDoctorReVos) {
							String tempDoctorIds = groupDoctorReVo.getDoctorId();
							if (StringUtils.equals(doctorId, tempDoctorIds)) {
								groupName.add(groupDoctorReVo.getGroupName());
								inviterName.add(groupDoctorReVo.getInviterName());
								inviterIds.append(groupDoctorReVo.getInviterId()).append(",");
							}
						}
					}
					if (inviterIds != null && inviterIds.length() > 0) {
						inviterIds.setLength(inviterIds.length() - 1);
					}
					doctorExcelVo.setGroups(groupName);
					doctorExcelVo.setInviterNames(inviterName);
					doctorExcelVo.setInviterId(inviterIds.toString());
				}
			}

		} else {
			return null;
		}
		return doctorExcelVos;
	}

	@Override
	public void setHasAdvise(List<DoctorExcelVo> doctorExcelVos, int adviseType) {
		if (doctorExcelVos != null && doctorExcelVos.size() > 0) {
			// 先获取所有的医生的id
			List<Integer> doctorIds = new ArrayList<>();

			for (DoctorExcelVo doctorExcelVo : doctorExcelVos) {
				String doctorId = doctorExcelVo.getId();
				if (StringUtils.isNotEmpty(doctorId)) {
					doctorIds.add(Integer.valueOf(doctorId));
				}
			}

			// 查询 p_pack表，获取有对应套餐的医生的id
			PackParam2 packParam = new PackParam2();
			packParam.setDoctorIds(doctorIds);
			packParam.setType(adviseType);
			List<Integer> tempDoctorIds = Lists.newArrayList();
			if (adviseType != 3) {
				tempDoctorIds = packMapper.getAdviseDoctorIds(packParam);
			} else {
				List<String> skipGroupIds = groupDao.getIdListBySkipAndType(GroupSkipStatus.skip,
						GroupEnum.GroupType.group);
				if (!CollectionUtils.isEmpty(skipGroupIds)) {
					packParam.setSkipGroupIds(skipGroupIds);
				}

				List<Pack> packs = packMapper.queryAdvise(packParam);
				packs = packService.withRemoveDisablePack(packs);

				if (packs != null && packs.size() > 0) {
					for (Pack pack : packs) {
						tempDoctorIds.add(pack.getDoctorId());
					}
				}
			}

			if (tempDoctorIds != null) {
				for (DoctorExcelVo doctorExcelVo : doctorExcelVos) {
					Integer doctorId = Integer.valueOf(doctorExcelVo.getId());
					boolean hasAdvise = false;

					for (Integer tempDoctorId : tempDoctorIds) {
						if (doctorId.equals(tempDoctorId)) {
							hasAdvise = true;
							break;
						}
					}

					switch (adviseType) {
					case 1:
						// 图文咨询
						if (hasAdvise) {
							doctorExcelVo.setHasPictureAdvise("是");
						} else {
							doctorExcelVo.setHasPictureAdvise("否");
						}
						break;
					case 2:
						if (hasAdvise) {
							doctorExcelVo.setHasPhoneAdvise("是");
						} else {
							doctorExcelVo.setHasPhoneAdvise("否");
						}
						break;
					case 3:
						if (hasAdvise) {
							doctorExcelVo.setHasCareAdvise("是");
						} else {
							doctorExcelVo.setHasCareAdvise("否");
						}
						break;
					default:
						break;
					}
				}
			}
		}

	}

	@Override
	public List<PatientExcelVo> getPatientExcelVo(Integer status, Long start, Long end) {
		return doctroExcelDao.getPatientExcelVo(status, start, end);
	}

	@Override
	public List<OrderExcelVo> getByPackTypeAndTime(Integer packType, Long start, Long end) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		OrderParam orderParam = new OrderParam();
		orderParam.setStartCreateTime(start);
		orderParam.setEndCreateTime(end);
		orderParam.setPackType(packType);
		List<Order> orders = orderMapper.getByPackTypeAndTime(orderParam);
		List<OrderExcelVo> orderExcelVos = new ArrayList<>();
		if (orders != null && orders.size() > 0) {
			for (Order order : orders) {
				OrderExcelVo orderExcelVo = new OrderExcelVo();
				orderExcelVo.setId(order.getId());
				orderExcelVo.setOrderNo(order.getOrderNo());
				Long createTime = order.getCreateTime();
				orderExcelVo.setCreateTime(createTime);
				if (createTime != null) {
					orderExcelVo.setCreateTimeStr(simpleDateFormat.format(new Date(createTime)));
				}
				orderExcelVo.setDoctorId(order.getDoctorId());
				orderExcelVo.setPatientId(order.getPatientId());
				Integer orderStatus = order.getOrderStatus();
				orderExcelVo.setOrderStatus(orderStatus);
				if (packType.intValue() == 3) {
					orderExcelVo.setCareName(order.getCareName());
					orderExcelVo.setCareTemplateId(order.getCareTemplateId());
				} else if (packType.intValue() == 1) {
					//图文咨询获取会话信息
					OrderSession orderSession = orderSessionService.findOneByOrderId(order.getId());
					String messageGroupId = null;
					String messageSerial = null;
					if(Objects.nonNull(orderSession)) {
						messageGroupId = orderSession.getMsgGroupId();
						messageSerial = jedisTemplate.hget(OrderSessionServiceImpl.MESSAGE_REPLY_ORDER_COUNT, messageGroupId);
					}

					Calendar calendar = Calendar.getInstance();
					calendar.set(Calendar.YEAR, 2016);
					calendar.set(Calendar.MONTH, Calendar.OCTOBER);
					calendar.set(Calendar.DAY_OF_MONTH, 15);
					calendar.set(Calendar.HOUR, 0);
					calendar.set(Calendar.MINUTE, 0);
					calendar.set(Calendar.SECOND, 0);
					calendar.set(Calendar.MILLISECOND, 0);
					
					if (order.getOrderStatus() != null && order.getOrderStatus().intValue() == OrderEnum.OrderStatus.已支付.getIndex()) {
						if (Objects.nonNull(orderSession) && orderSession.getCreateTime() != null && orderSession.getCreateTime().longValue() > calendar.getTimeInMillis()) {
							if(StringUtils.isBlank(messageSerial)) {
								orderExcelVo.setPicMessageStatus("等待患者提问");
							} else {
								char last = messageSerial.charAt(messageSerial.length()-1);
								if (last == '0') {
									//最后一句话是患者说的
									orderExcelVo.setPicMessageStatus("等待医生回复");
								} else {
									orderExcelVo.setPicMessageStatus("等待患者提问");
								}
							}
						} else {
							orderExcelVo.setPicMessageStatus("旧订单无法获取回答状态");
						}
					}
					
				}
				
				
				if (orderStatus != null) {
					switch (orderStatus) {
					case 1:
						orderExcelVo.setOrderStatusStr("待预约");
						break;
					case 2:
						orderExcelVo.setOrderStatusStr("待支付");
						break;
					case 3:
						orderExcelVo.setOrderStatusStr("已支付");
						break;
					case 4:
						orderExcelVo.setOrderStatusStr("已完成");
						break;
					case 5:
						orderExcelVo.setOrderStatusStr("已取消");
						break;
					case 6:
						orderExcelVo.setOrderStatusStr("进行中");
						break;
					case 7:
						orderExcelVo.setOrderStatusStr("待完善");
						break;
					case 10:
						orderExcelVo.setOrderStatusStr("预约成功");
						break;
					default:
						break;
					}
				}
				Long payTime = order.getPayTime();
				orderExcelVo.setPayTime(payTime);
				if (payTime != null) {
					orderExcelVo.setPayTimeStr(simpleDateFormat.format(new Date(payTime)));
					orderExcelVo.setHasPay("是");
				} else {
					orderExcelVo.setHasPay("否");
				}
				orderExcelVo.setPrice(order.getPrice() / 100);
				orderExcelVo.setGroupId(order.getGroupId());
				orderExcelVos.add(orderExcelVo);
			}
			// 设置医生的基本信息
			setDoctorInfo(orderExcelVos);
			// 设置患者的基本信息
			setPatientInfo(orderExcelVos);
			// 设置集团信息
			setGroupInfo(orderExcelVos);

			return orderExcelVos;
		}

		return null;
	}

	void setDoctorInfo(List<OrderExcelVo> orderExcelVos) {
		// 从user表查询（设置doctorName、doctorPhone字段）
		if (orderExcelVos != null && orderExcelVos.size() > 0) {
			List<Integer> doctorIds = new ArrayList<>();
			for (OrderExcelVo orderExcelVo : orderExcelVos) {
				Integer doctorId = orderExcelVo.getDoctorId();
				doctorIds.add(doctorId);
			}

			List<UserExcelVo> userExcelVos = doctroExcelDao.getByIds(doctorIds);
			if (userExcelVos != null && userExcelVos.size() > 0) {
				for (OrderExcelVo orderExcelVo : orderExcelVos) {
					String doctorId = String.valueOf(orderExcelVo.getDoctorId());
					for (UserExcelVo userExcelVo : userExcelVos) {
						String userId = userExcelVo.getId();
						if (doctorId.equals(userId)) {
							orderExcelVo.setDoctorName(userExcelVo.getName());
							orderExcelVo.setDoctorPhone(userExcelVo.getPhone());
						}
					}
				}
			}
		}
	}

	void setPatientInfo(List<OrderExcelVo> orderExcelVos) {
		// 从t_patient表查询，设置patientName， patientPhone，
		// 再获取到Userid，再从user表查询用户的姓名和电话
		if (orderExcelVos != null && orderExcelVos.size() > 0) {
			List<Integer> patientIds = new ArrayList<>();
			for (OrderExcelVo orderExcelVo : orderExcelVos) {
				Integer patientId = orderExcelVo.getPatientId();
				patientIds.add(patientId);
			}
			PatientExample example = new PatientExample();
			example.setIds(patientIds);
			List<Patient> patients = patientMapper.getByIds(example);

			if (patients != null && patientIds.size() > 0) {

				List<Integer> userIds = new ArrayList<>();

				for (OrderExcelVo orderExcelVo : orderExcelVos) {
					Integer patientId = orderExcelVo.getPatientId();

					for (Patient patient : patients) {
						Integer id = patient.getId();
						if (patientId.equals(id)) {
							orderExcelVo.setPatientName(patient.getUserName());
							orderExcelVo.setPatientPhone(patient.getTelephone());
							orderExcelVo.setPatientUserId(patient.getUserId());
							userIds.add(patient.getUserId());
						}
					}
				}

				List<UserExcelVo> userExcelVos = doctroExcelDao.getByIds(userIds);
				if (userExcelVos != null && userExcelVos.size() > 0) {
					for (OrderExcelVo orderExcelVo : orderExcelVos) {
						String patientUserId = String.valueOf(orderExcelVo.getPatientUserId());
						for (UserExcelVo userExcelVo : userExcelVos) {
							String userId = userExcelVo.getId();
							if (patientUserId.equals(userId)) {
								orderExcelVo.setUserName(userExcelVo.getName());
							}
						}
					}
				}
			}
		}
	}

	void setGroupInfo(List<OrderExcelVo> orderExcelVos) {
		if (orderExcelVos != null && orderExcelVos.size() > 0) {
			List<String> groupIds = new ArrayList<>();
			for (OrderExcelVo orderExcelVo : orderExcelVos) {
				String groupId = orderExcelVo.getGroupId();
				groupIds.add(groupId);
			}

			// 查询集团的信息
			List<GroupExcelVo> groupExcelVos = doctroExcelDao.getGroupByIds(groupIds);
			if (groupExcelVos != null && groupExcelVos.size() > 0) {
				for (OrderExcelVo orderExcelVo : orderExcelVos) {
					String groupId = orderExcelVo.getGroupId();
					for (GroupExcelVo groupExcelVo : groupExcelVos) {
						String tempGroupId = groupExcelVo.getGroupId();
						if (StringUtils.equals(groupId, tempGroupId)) {
							orderExcelVo.setGroupName(groupExcelVo.getGroupName());
						}
					}
				}
			}
		}
	}

	/**
	 * 设置患者的邀请信息
	 * 
	 * @param patientExcelVos
	 */
	public void setInviterInfo(List<PatientExcelVo> patientExcelVos) {
		if (patientExcelVos != null && patientExcelVos.size() > 0) {
			List<Integer> patientIds = new ArrayList<>();
			for (PatientExcelVo patientExcelVo : patientExcelVos) {
				String id = patientExcelVo.getId();
				patientIds.add(Integer.valueOf(id));
			}

			CheckInParam param = new CheckInParam();
			param.setUserIds(patientIds);
			// 根据患者的id，查询p_check_in表中患者和医生之间的关系（可能会存在一个患者对应多个医生）
			List<CheckInVO> checkInVOs = CheckInMapper.getCheckInByUserIds(param);
			if (checkInVOs != null && checkInVOs.size() > 0) {
				List<Integer> doctorIds = new ArrayList<>();
				for (CheckInVO checkInVO : checkInVOs) {
					Integer doctorId = checkInVO.getDoctorId();
					doctorIds.add(doctorId);
				}

				List<UserExcelVo> userExcelVos = doctroExcelDao.getByIds(doctorIds);
				if (userExcelVos != null && userExcelVos.size() > 0) {
					for (CheckInVO checkInVO : checkInVOs) {
						String doctorId = String.valueOf(checkInVO.getDoctorId());
						for (UserExcelVo userExcelVo : userExcelVos) {
							String tempDoctorId = userExcelVo.getId();
							if (StringUtils.equals(doctorId, tempDoctorId)) {
								checkInVO.setDoctorName(userExcelVo.getName());
								checkInVO.setDoctorPhone(userExcelVo.getPhone());
							}
						}
					}
				}

				for (PatientExcelVo patientExcelVo : patientExcelVos) {
					String patientId = patientExcelVo.getId();
					StringBuffer doctorNames = new StringBuffer();
					StringBuffer doctorPhones = new StringBuffer();

					for (CheckInVO checkInVO : checkInVOs) {
						String tempPatientId = String.valueOf(checkInVO.getUserId());
						if (StringUtils.equals(patientId, tempPatientId)) {
							doctorNames.append(checkInVO.getDoctorName()).append(",");
							doctorPhones.append(checkInVO.getDoctorPhone()).append(",");
						}
					}

					if (doctorNames != null && doctorNames.length() > 0) {
						doctorNames.setLength(doctorNames.length() - 1);
					}

					if (doctorPhones != null && doctorPhones.length() > 0) {
						doctorPhones.setLength(doctorPhones.length() - 1);
					}

					patientExcelVo.setInviterName(doctorNames.toString());
					patientExcelVo.setInviterPhone(doctorPhones.toString());
				}
			}
		}
	}

	@Override
	public int getAdviseDoctorCount(Integer type) {
		PackParam2 packParam = new PackParam2();
		packParam.setType(type);
		return packMapper.getAdviseDoctorCount(packParam);
	}

	@Override
	public List<DoctorExcelVo> getAdviseDoctor(Integer type) {
		// 先去p_pack表查询医生的id
		List<Integer> doctorIds = Lists.newArrayList();
		List<Pack> packs = Lists.newArrayList();

		PackParam2 packParam = new PackParam2();
		packParam.setType(type);
		List<String> skipGroupIds = groupDao.getIdListBySkipAndType(GroupSkipStatus.skip, GroupEnum.GroupType.group);
		if (!CollectionUtils.isEmpty(skipGroupIds)) {
			if (type.intValue() == 3) {
				packParam.setSkipGroupIds(skipGroupIds);
			}
		}
		packs = packMapper.getAdviseAdviseOpenedPacks(packParam);
		packs = packService.withRemoveDisablePack(packs);
		if (packs != null && packs.size() > 0) {
			for (Pack pack : packs) {
				doctorIds.add(pack.getDoctorId());
			}
		}

		List<DoctorExcelVo> doctorExcelVos = Lists.newArrayList();

		if (doctorIds != null && doctorIds.size() > 0) {
			// 获取医生的基本信息
			doctorExcelVos = doctroExcelDao.getDoctorExcelByIds(doctorIds);
			// 获取集团医生的基本信息
			List<GroupDoctorReVo> groupDoctorReVos = doctroExcelDao.getGroupDoctorReVo(doctorIds);

			// 3、查询c_group表, 设置集团名称
			groupDoctorReVos = doctroExcelDao.setGroupName(groupDoctorReVos);

			// 4、查询user表，设置推荐人名称
			groupDoctorReVos = doctroExcelDao.setInviterName(groupDoctorReVos);

			for (DoctorExcelVo doctorExcelVo : doctorExcelVos) {
				String doctorId = doctorExcelVo.getId();

				List<String> groupName = new ArrayList<>();
				List<String> inviterName = new ArrayList<>();
				if (groupDoctorReVos != null) {
					for (GroupDoctorReVo groupDoctorReVo : groupDoctorReVos) {
						String tempDoctorIds = groupDoctorReVo.getDoctorId();
						if (StringUtils.equals(doctorId, tempDoctorIds)) {
							groupName.add(groupDoctorReVo.getGroupName());
							inviterName.add(groupDoctorReVo.getInviterName());
						}
					}
				}
				doctorExcelVo.setGroups(groupName);
				doctorExcelVo.setInviterNames(inviterName);
			}
		}

		if (type.intValue() == 1 || type.intValue() == 2) {
			for (DoctorExcelVo doctorExcelVo : doctorExcelVos) {
				String doctorId = doctorExcelVo.getId();
				for (Pack pack : packs) {
					String tempDoctorId = String.valueOf(pack.getDoctorId());
					if (StringUtils.equals(doctorId, tempDoctorId)) {
						doctorExcelVo.setPrice(pack.getPrice());
					}
				}
			}
		}

		return doctorExcelVos;
	}

	@Override
	public List<OrderExcelVo> setCareLastAnswerTime(List<OrderExcelVo> orderExcelVos) {
		// 设置itemid
		orderExcelVos = doctroExcelDao.setCareItemIdAndAnswerTimes(orderExcelVos);

		if (orderExcelVos != null && orderExcelVos.size() > 0) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			orderExcelVos.forEach((orderExcelVo) -> {
				List<Long> answerTimes = orderExcelVo.getAnswerTimes();
				if (answerTimes != null && answerTimes.size() > 0) {
					Long lastAnswerTime = Collections.max(answerTimes);
					orderExcelVo.setLastAnswerTime(lastAnswerTime);
					orderExcelVo.setLastAnswerTimeStr(simpleDateFormat.format(lastAnswerTime));
					orderExcelVo.setAnswerCount(answerTimes.size());
				} else {
					orderExcelVo.setLastAnswerTime(null);
					orderExcelVo.setAnswerCount(0);
				}
			});
		}

		orderExcelVos = doctroExcelDao.setItemAnswerInfo(orderExcelVos);

		return orderExcelVos;
	}

	public Map<String, List<String>> getLongTitle(List<OrderExcelVo> orderExcelVos) {
		List<String> keys = Lists.newArrayList();
		List<String> titles = Lists.newArrayList();
		Map<String, List<String>> result = Maps.newHashMap();
		if (orderExcelVos != null && orderExcelVos.size() > 0) {
			// 获取最长的跟踪表、量表、调查表
			List<Integer> tracksCount = Lists.newArrayList();
			List<Integer> lifesCount = Lists.newArrayList();
			List<Integer> surveysCount = Lists.newArrayList();
			for (OrderExcelVo orderExcelVo : orderExcelVos) {
				List<ItemAnswerInfo> tracks = orderExcelVo.getTrack();
				List<ItemAnswerInfo> lifes = orderExcelVo.getLife();
				List<ItemAnswerInfo> survey = orderExcelVo.getSurvey();

				Integer trackCount = tracks == null ? 0 : tracks.size();
				Integer lifeCount = lifes == null ? 0 : lifes.size();
				Integer surveyCount = survey == null ? 0 : survey.size();
				
				tracksCount.add(trackCount);
				lifesCount.add(lifeCount);
				surveysCount.add(surveyCount);
			}
			
			Integer maxTrackCount = Collections.max(tracksCount);
			Integer maxLifeCount = Collections.max(lifesCount);
			Integer maxSurveyCount = Collections.max(surveysCount);
			
			for(int i = 0; i < maxTrackCount; i++) {
				keys.add("trackSendTime_" + i);
				titles.add("第" + (i + 1) + "次病情跟踪计划时间");
				keys.add("trackAnswerTime_" + i);
				titles.add("第" + (i + 1) + "次病情跟踪答题时间");
				keys.add("trackStatus_" + i);
				titles.add("第" + (i + 1) + "次病情跟踪医生是否查看");
			}
			
			for(int i = 0; i < maxLifeCount; i++) {
				keys.add("lifeSendTime_" + i);
				titles.add("第" + (i + 1) + "次生活量表计划时间");
				keys.add("lifeAnswerTime_" + i);
				titles.add("第" + (i + 1) + "次生活量表答题时间");
				keys.add("lifeStatus_" + i);
				titles.add("第" + (i + 1) + "次生活量表医生是否查看");
			}
			
			for(int i = 0; i < maxSurveyCount; i++) {
				keys.add("surveySendTime_" + i);
				titles.add("第" + (i + 1) + "次调查表计划时间");
				keys.add("surveyAnswerTime_" + i);
				titles.add("第" + (i + 1) + "次调查表答题时间");
				keys.add("surveyStatus_" + i);
				titles.add("第" + (i + 1) + "次调查表医生是否查看");
			}
		}
		result.put("keys", keys);
		result.put("title", titles);

		return result;
	}
}

class AuthParamPO {
    private List<Integer> userIdList;

    public List<Integer> getUserIdList() {
        return userIdList;
    }

    public void setUserIdList(List<Integer> userIdList) {
        this.userIdList = userIdList;
    }
}

class SimpleCircleVO {

    private String id;

    private String name;

    private Integer type;

    private String deptName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }
}
