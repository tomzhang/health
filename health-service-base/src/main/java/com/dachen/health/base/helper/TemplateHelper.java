package com.dachen.health.base.helper;

import java.text.MessageFormat;

import com.dachen.health.base.entity.po.MsgTemplate;

public class TemplateHelper {

	private String id;

	private Object[] args;

	// TODO 不能注入
	//		@Autowired
	//		private IBaseDataDao baseDataDao;

	// 	TODO 不能注入
	//	@Autowired 
	//	private IBaseDataService baseDataService;

	/**
	 * 格式化成title
	 * 
	 * @param id
	 * @param args
	 * @return
	 */
	@Deprecated
	public static String toTitle(String id, Object... args) {
		TemplateHelper helper = new TemplateHelper(id, args);
		return helper.formatTitle();
	}

	/**
	 * 格式化成Content
	 * 
	 * @param id
	 * @param args
	 * @return
	 */
	@Deprecated
	public static String toContent(String id, Object... args) {
		TemplateHelper helper = new TemplateHelper(id, args);
		return helper.formatContent();
	}

	public TemplateHelper(String id, Object... args) {
		this.id = id;
		this.args = args;
	}

	/**
	 * 从数据库由Id查到此数据
	 * 
	 * @param id
	 * @return
	 */
	@Deprecated
	public MsgTemplate getById(String id) {

		//		BaseDataDaoImpl sql = new BaseDataDaoImpl();
		//		Query<CopyWriterTemplate> query = baseDataService.queryCopyWriterTemplate(param);
		//		if (query == null) {
		//			return null;
		//		}
		//		List<CopyWriterTemplate> list = query.asList();

		//		CopyWriterTemplateParam param = new CopyWriterTemplateParam();
		//		param.setId(id);
		//		
		//		if (baseDataService == null) {
		//			System.out.println("baseDataService == null");
		//		}else{
		//			System.out.println("baseDataService != null");
		//		}
		//		List<CopyWriterTemplate> list = baseDataService.queryCopyWriterTemplate(param);
		//		if (list == null ||list.isEmpty()) {
		//			return null;
		//		}
		//		return list.get(0);
		return null;
	}

	@Deprecated
	public String formatTitle() {
		MsgTemplate object = getById(this.id);
		if (object == null) {
			return null;
		}
		return MessageFormat.format(object.getTitle(), this.args);
	}

	@Deprecated
	public String formatContent() {
		MsgTemplate object = getById(this.id);
		if (object == null) {
			return null;
		}
		return MessageFormat.format(object.getContent(), this.args);
	}

	public String formatText(String text) {
		if (text == null) {
			return null;
		}
		return MessageFormat.format(text, this.args);
	}

}
