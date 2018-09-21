package com.dachen.health.pack.patient.service;


import com.dachen.sdk.exception.HttpApiException;

public interface IBaseService<T,PK> {
	/**
	 * 
	 * 保存
	 * @author 李淼淼
	 * @date 2015年8月12日
	 * @return int 影响行数
	 */
	public int save(T intance) throws HttpApiException;

	/**
	 * 
	 * 修改
	 * @author 李淼淼
	 * @date 2015年8月12日
	 * @return  int 影响行数
	 */
	public int update(T intance) throws HttpApiException;

	/**
	 * 
	 * 根据主键删除
	 * @author 李淼淼
	 * @date 2015年8月12日
	 * @return  int 影响行数
	 */
	public int deleteByPK(PK pk);

	/**
	 * 
	 * 根据主键查找
	 * @author 李淼淼
	 * @date 2015年8月12日
	 */
	public T findByPk(PK pk) throws HttpApiException;

}
