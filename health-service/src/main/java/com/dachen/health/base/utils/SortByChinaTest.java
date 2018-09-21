package com.dachen.health.base.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.dachen.health.commons.vo.User;

public class SortByChinaTest {

	public static void main(String[] args) {
		List<User> list = new ArrayList<User>();
		
		User user = new User();
		user.setName("张垠z");
		list.add(user);
		
		user = new User();
		user.setName("安吉利a");
		list.add(user);
		
		user = new User();
		user.setName("谢平x");
		list.add(user);
		
		user = new User();
		user.setName("谢佩x");
		list.add(user);
		
		user = new User();
		user.setName("王乐w");
		list.add(user);
		
		user = new User();
		user.setName("屈军利q");
		list.add(user);
		
		System.out.println("排序前：");
		for (User u : list) {
			System.out.print(u.getName()+", ");
		}
		
		Collections.sort(list, new SortByChina<User>("name"));
		
		System.out.println("\n排序后：");
		for (User u : list) {
			System.out.print(u.getName()+", ");
		}
	}
}
