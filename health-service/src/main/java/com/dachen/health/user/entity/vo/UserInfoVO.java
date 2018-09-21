package com.dachen.health.user.entity.vo;

/**
 * 
 *  * @apiSuccess 		{int} 							userId 						用户id
	 * @apiSuccess 		{int} 							userType 					用户类型
	 * @apiSuccess 		{String} 						userName 					用户名称
	 * @apiSuccess 		{String} 						headPicFileName 			头像绝对地址
	 * @apiSuccess 		{String} 						doctorGroupName 			医生集团
	 * @apiSuccess 		{String} 						departmentsName 			科室名称
	 * 
 * @author lmc
 *
 */
public class UserInfoVO {
	public int userId;
	public int userType;
	public String name;
	public String headPicFileName;
	public String doctorGroupName;
	public String departmentsName;
	public String title;
	public int splitPercent;
	
	
}
