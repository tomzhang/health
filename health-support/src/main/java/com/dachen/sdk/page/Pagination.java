package com.dachen.sdk.page;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Pagination<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// 总页数
	protected int pageCount;
	
	/**
	 * 封装返回的业务数据
	 */
	protected List<T> pageData;
	
	/**
	 * 返回的页码
	 */
	protected int pageIndex = 0;
	
	/**
	 * 每页数据大小
	 */
	protected int pageSize = 15;
	
	protected int start;
	
	/**
	 * 总记录数
	 */
	protected Long total=0l;
	
	/**
	 * 排序对象key，字段，value 方式
	 */
	private Map sorter;

	public Pagination() {
		super();
	}

	public Pagination(List<T> pageData, Long total) {
		super();
		this.pageData = pageData;
		this.total = total;
	}

	public Pagination(List<T> pageData, Long total, int pageIndex,
			int pageSize) {
		super();
		this.pageIndex = pageIndex;
		this.pageSize = pageSize;
		this.pageData = pageData;
		this.total = total;
	}

	public int getPageCount() {
		return pageCount = (int) (total % pageSize == 0 ? total / pageSize : total / pageSize +1);
	}
	

	public List<T> getPageData() {
		return pageData;
	}

	public void setPageData(List<T> pageData) {
		this.pageData = pageData;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getStart() {
		return start = (pageIndex * pageSize);
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public Map getSorter() {
		return sorter;
	}

	public void setSorter(Map sorter) {
		this.sorter = sorter;
	}

}
