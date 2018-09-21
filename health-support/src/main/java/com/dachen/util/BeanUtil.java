package com.dachen.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;

public class BeanUtil {
	
//	public static void main(String[]args)
//	{
//		List<Room>roomList = new ArrayList<Room>();
//		for(int i=0;i<5;i++)
//		{
//			Room room = new Room();
//			room.setName("room"+i);
//			roomList.add(room);
//		}
//		
//		List<RoomVO> voList = copyList(roomList,RoomVO.class);
//	}
	 public static <T> T copy(Object poObj,final Class <T>voClass)
	 {
		 T voObj =null;
		 try {
             voObj = voClass.newInstance();
             BeanUtils.copyProperties(poObj, voObj);
             return voObj;
         } catch (InstantiationException | IllegalAccessException e) {
             e.printStackTrace();
         }
		 return null;
	 }
	 public static <T> List <T> copyList(List <? extends Object> poList ,final Class <T>voClass){
        
        List<T> voList=new ArrayList<T>();
         
        T voObj =null;
        for(Object poObj:poList){
            try {
                voObj = voClass.newInstance();
                BeanUtils.copyProperties(poObj, voObj);
                voList.add(voObj);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            System.out.println(voObj);
        }
        return voList;
    }
}
