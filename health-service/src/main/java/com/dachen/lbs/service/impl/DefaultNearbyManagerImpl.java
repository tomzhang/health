package com.dachen.lbs.service.impl;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.dachen.lbs.service.NearbyManager;
import com.dachen.lbs.vo.NearbyJob;
import com.dachen.lbs.vo.NearbyUser;
import com.dachen.util.DateUtil;
import com.dachen.util.HttpUtil;
import com.dachen.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Service(value = "DefaultNearbyManagerImpl")
public class DefaultNearbyManagerImpl implements NearbyManager {

	public static final String AK = "OuLCe9EHc0v6Ik5BiAE4oxfN";
	public static final String BEAN_ID = "DefaultNearbyManagerImpl";
	private static final int USER_TABLE_ID = 59906;
	private static final int IM_USER_TABLE_ID = 94416;



	@Override
	public void saveJob(NearbyJob poi) {
		String spec = "http://api.map.baidu.com/geodata/v3/poi/create";
		Map<String, Object> data = Maps.newHashMap();
		data.put("title", poi.getTitle());
		data.put("address", poi.getAddress());
		data.put("tags", poi.getTags());
		data.put("latitude", poi.getLatitude());
		data.put("longitude", poi.getLongitude());
		data.put("coord_type", 3);
		data.put("geotable_id", 75526);
		data.put("ak", AK);
		data.put("companyId", poi.getCompanyId());// 公司Id
		data.put("companyName", poi.getCompanyName());// 公司名称
		data.put("description", poi.getDescription());// 公司简介
		data.put("diploma", poi.getDiploma());// 学历
		data.put("jobId", poi.getJobId());// 职位Id
		data.put("jobName", poi.getJobName());// 职位名称
		data.put("salary", poi.getSalary());// 薪水
		data.put("workExp", poi.getWorkExp());// 工作经验
		data.put("workLocation", poi.getWorkLocation());// 工作地点

		try {
			HttpUtil.asBean(new HttpUtil.Request(data, spec), Response.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void updateJob(NearbyJob poi) {

	}

	@Override
	public List<NearbyJob> getJobList(NearbyJob poi) {
		List<NearbyJob> poiList = Lists.newArrayList();

		String spec = "http://api.map.baidu.com/geosearch/v3/nearby";
		Map<String, Object> data = Maps.newHashMap();
		data.put("ak", AK);
		data.put("geotable_id", 75526);
		data.put("q", "");
		data.put("location", poi.getLongitude() + "," + poi.getLatitude());
		data.put("coord_type", 3);
		data.put("radius", 1000);
		// data.put("tags", "");
		// data.put("sortby", "");
		// data.put("filter", "");
		data.put("page_index", poi.getPageIndex());
		data.put("page_size", poi.getPageSize());// 10-50
		// data.put("callback", "");
		// data.put("sn", "");

		try {
			String text = HttpUtil.get(new HttpUtil.Request(data, spec));
			Response result = JSON.parseObject(text, Response.class);
			if (0 == result.getStatus())
				poiList = JSON
						.parseArray(result.getContents(), NearbyJob.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return poiList;
	}

	@Override
	public void saveUser(NearbyUser poi) {
		String spec = "http://api.map.baidu.com/geodata/v3/poi/create";
		Map<String, Object> data = Maps.newHashMap();
		data.put("title", poi.getTitle());
		data.put("address", poi.getAddress());
		data.put("tags", poi.getTags());
		data.put("latitude", poi.getLatitude());
		data.put("longitude", poi.getLongitude());
		data.put("coord_type", 3);
		data.put("geotable_id", USER_TABLE_ID);
		data.put("ak", AK);
		data.put("birthday", poi.getBirthday());// 生日
		data.put("description", poi.getDescription());// 签名
		data.put("diploma", poi.getDiploma());// 学历
		data.put("name", poi.getName());// 姓名
		data.put("nickname", poi.getNickname());// 昵称
		data.put("salary", poi.getSalary());// 薪水
		data.put("sex", poi.getSex());// 性别
		data.put("userId", poi.getUserId());// 用户Id
		data.put("workExp", poi.getWorkExp());// 工作经验

		try {
			HttpUtil.asBean(new HttpUtil.Request(data, spec), Response.class);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void updateUser(NearbyUser poi) {
		String spec = "http://api.map.baidu.com/geodata/v3/poi/update";
		Map<String, Object> data = Maps.newHashMap();
		data.put("id", poi.getPoiId());
		data.put("title", poi.getTitle());
		data.put("address", poi.getAddress());
		data.put("tags", poi.getTags());
		data.put("latitude", poi.getLatitude());
		data.put("longitude", poi.getLongitude());
		data.put("coord_type", 3);
		data.put("geotable_id", USER_TABLE_ID);
		data.put("ak", AK);
		data.put("birthday", poi.getBirthday());// 生日
		data.put("description", poi.getDescription());// 签名
		data.put("diploma", poi.getDiploma());// 学历
		data.put("name", poi.getName());// 姓名
		data.put("nickname", poi.getNickname());// 昵称
		data.put("salary", poi.getSalary());// 薪水
		data.put("sex", poi.getSex());// 性别
		data.put("userId", poi.getUserId());// 用户Id
		data.put("workExp", poi.getWorkExp());// 工作经验

		try {
			HttpUtil.asBean(new HttpUtil.Request(data, spec), Response.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public List<NearbyUser> getTalentsList(NearbyUser poi) {
		return getUserList(poi);
	}

	@Override
	public List<NearbyUser> getUserList(NearbyUser poi) {
		List<NearbyUser> poiList = Lists.newArrayList();

		String spec = "http://api.map.baidu.com/geosearch/v3/nearby";
		Map<String, Object> data = Maps.newHashMap();
		data.put("ak", AK);
		data.put("geotable_id", USER_TABLE_ID);
		if (!StringUtil.isEmpty(poi.getNickname()))
			data.put("q", poi.getNickname());
		data.put("location", poi.getLongitude() + "," + poi.getLatitude());
		data.put("coord_type", 3);
		data.put("radius", 1000);
		// data.put("tags", "");
		// data.put("sortby", "");
		// data.put("filter", "");
		data.put("page_index", poi.getPageIndex());
		data.put("page_size", poi.getPageSize());// 10-50
		// data.put("callback", "");
		// data.put("sn", "");

		try {
			String text = HttpUtil.get(new HttpUtil.Request(data, spec));
			Response result = JSON.parseObject(text, Response.class);
			if (0 == result.getStatus())
				poiList = JSON.parseArray(result.getContents(),
						NearbyUser.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return poiList;
	}

	@Override
	public void saveIMUser(NearbyUser poi) {
		String spec = "http://api.map.baidu.com/geodata/v3/poi/create";
		Map<String, Object> data = Maps.newHashMap();
		data.put("title", poi.getTitle());
		data.put("address", poi.getAddress());
		data.put("tags", poi.getTags());
		data.put("latitude", poi.getLatitude());
		data.put("longitude", poi.getLongitude());
		data.put("coord_type", 3);
		data.put("geotable_id", IM_USER_TABLE_ID);
		data.put("ak", AK);
		data.put("userId", poi.getUserId());// 用户Id
		data.put("nickname", poi.getNickname());// 昵称
		data.put("sex", poi.getSex());// 性别
		data.put("birthday", poi.getBirthday());// 生日
		data.put("description", poi.getDescription());// 签名
		data.put("active", DateUtil.currentTimeSeconds());// 时间

		try {
			HttpUtil.asBean(new HttpUtil.Request(data, spec), Response.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateIMUser(NearbyUser poi) {
		String spec = "http://api.map.baidu.com/geodata/v3/poi/update";
		Map<String, Object> data = Maps.newHashMap();
		data.put("id", poi.getPoiId());
		data.put("title", poi.getTitle());
		data.put("address", poi.getAddress());
		data.put("tags", poi.getTags());
		data.put("latitude", poi.getLatitude());
		data.put("longitude", poi.getLongitude());
		data.put("coord_type", 3);
		data.put("geotable_id", IM_USER_TABLE_ID);
		data.put("ak", AK);
		data.put("userId", poi.getUserId());// 用户Id
		data.put("nickname", poi.getNickname());// 昵称
		data.put("sex", poi.getSex());// 性别
		data.put("birthday", poi.getBirthday());// 生日
		data.put("description", poi.getDescription());// 签名
		data.put("active", DateUtil.currentTimeSeconds());// 时间

		try {
			HttpUtil.asBean(new HttpUtil.Request(data, spec), Response.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	String getFilter(NearbyUser poi) {
		StringBuilder sb = new StringBuilder();

		if (null != poi.getSex())
			sb.append("sex:[" + poi.getSex() + "]|");
		if (null != poi.getActive()) {
			sb.append("active:"
					+ (DateUtil.currentTimeSeconds() - poi.getActive()) + ","
					+ DateUtil.currentTimeSeconds() + "|");
		}
		if (null != poi.getMinAge() && null != poi.getMaxAge()) {
			int year = Calendar.getInstance().get(Calendar.YEAR);
			long start = DateUtil
					.toSeconds((year - poi.getMinAge()) + "-01-01");
			long end = DateUtil.toSeconds((year - poi.getMaxAge()) + "-12-31");
			sb.append("birthday:" + start + "," + end + "|");
		}

		return sb.length() == 0 ? null : sb.substring(0, sb.length() - 1);
	}

	@Override
	public List<NearbyUser> getIMUserList(NearbyUser poi) {
		List<NearbyUser> poiList = Lists.newArrayList();

		String spec = "http://api.map.baidu.com/geosearch/v3/nearby";
		Map<String, Object> data = Maps.newHashMap();
		data.put("ak", AK);
		data.put("geotable_id", IM_USER_TABLE_ID);
		if (!StringUtil.isEmpty(poi.getNickname()))
			data.put("q", poi.getNickname());
		data.put("location", poi.getLongitude() + "," + poi.getLatitude());
		data.put("coord_type", 3);
		data.put("radius", 1000);

		// data.put("tags", "");
		// data.put("sortby", "");
		String filter = getFilter(poi);
		if (null != filter)
			data.put("filter", filter);
		data.put("page_index", poi.getPageIndex());
		data.put("page_size", poi.getPageSize());// 10-50
		// data.put("callback", "");
		// data.put("sn", "");

		try {
			String text = HttpUtil.get(new HttpUtil.Request(data, spec));
			Response result = JSON.parseObject(text, Response.class);
			if (0 == result.getStatus())
				poiList = JSON.parseArray(result.getContents(),
						NearbyUser.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return poiList;
	}

	public static class Response {
		private String contents;
		private int id;
		private String message;
		private int size;
		private int status;
		private int total;

		public String getContents() {
			return contents;
		}

		public int getId() {
			return id;
		}

		public String getMessage() {
			return message;
		}

		public int getSize() {
			return size;
		}

		public int getStatus() {
			return status;
		}

		public int getTotal() {
			return total;
		}

		public void setContents(String contents) {
			this.contents = contents;
		}

		public void setId(int id) {
			this.id = id;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public void setSize(int size) {
			this.size = size;
		}

		public void setStatus(int status) {
			this.status = status;
		}

		public void setTotal(int total) {
			this.total = total;
		}
	}

}
