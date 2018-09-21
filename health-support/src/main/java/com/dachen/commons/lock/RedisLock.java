package com.dachen.commons.lock;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dachen.commons.KeyBuilder;
import com.dachen.commons.constants.Constants;
import com.dachen.commons.support.jedis.JedisTemplate;
import com.dachen.commons.support.spring.SpringBeansUtils;
import com.dachen.util.StringUtil;

import redis.clients.jedis.Transaction;

/**
 * ProjectName： health-support<br>
 * ClassName： RedisLock<br>
 * Description：<br>
 * redis锁+事务处理并发流程:Object lock<br>
 *  1.检查key(get)是否存在，<br>
 *      a.如果存在则终止<br>
 *      b.如果不存则添加(setnx)<br>
 *      c.休眠10毫秒后再次获取key(get)值是否变化<br>
 *          d.如果值变化则终止<br>
 *          e.如果值未变化，开启事务(watch,multi)<br>
 *  2.删除key(del)<br>
 *  3.提交事务(exec)，如果事务失败，则mysql事务回滚，如果事务成功，则成功 <br>
 * 
 * @author 李淼淼
 * @createTime 2015年9月16日
 * @version 1.0.0
 */
public class RedisLock {

    private final static Logger logger=LoggerFactory.getLogger(RedisLock.class);
    
    private static JedisTemplate jedisTemplate = SpringBeansUtils.getBean("jedisTemplate");
    private  Transaction tx = null;

    /**
     * </p>添加锁</p>
     * 
     * @param key
     * @param expire
     * @param lockType
     * @return true or false
     * @author 李淼淼
     * @date 2015年9月165日
     */
    public boolean lock(String key, LockType lockType) {
        /*key = "lock_" + key;*/
        key=KeyBuilder.buildLockKey(lockType, key);

        String value = jedisTemplate.get(key);

        if (StringUtil.isNotBlank(value)) {
        	logger.info("LockType : "+lockType + " key : " + key+" has locked 1");
//            RedisUtil.returnJedis(jedis);
            return false;
        }

        try {
            value = System.nanoTime() + StringUtil.random4Code();
            boolean ok = jedisTemplate.setnx(key, value);
            //已存在，添加失败
            if(!ok){
            	logger.info("LockType : "+lockType + " key : " + key+" has locked 2");
//                RedisUtil.returnJedis(jedis);
                return false;
            }
            jedisTemplate.expire(key, Constants.Expire.MINUTE1);// 设置10分钟失效

            TimeUnit.MILLISECONDS.sleep(10);// 休眠10毫秒

            // 再次获取锁
            String newValue = jedisTemplate.get(key);

            // 判断锁中的值是否与设置的一致
            if (!StringUtil.equals(value, newValue)) {
            	logger.info("LockType : "+lockType + " key : " + key+" has locked 3");
//                RedisUtil.returnJedis(jedis);
                return false;
            }

            // 事务开始
            jedisTemplate.watch(key);
            tx = jedisTemplate.multi();

        } catch (Exception e) {
            logger.info("LockType : "+lockType + " key : " + key+"  lock fail"+e.getMessage());
            logger.error(e.toString());
            jedisTemplate.unwatch();
//            RedisUtil.returnJedis(jedis);
            return false;
        }
        return true;
    }

   
    /**
     * </p>删除锁</p>
     * @param key
     * @param lockType
     * @return true or false 为false的时候需事务回滚
     * @author 李淼淼
     * @date 2015年9月16日
     */
    public boolean unlock(String key, LockType lockType) {
        /*key = "lock_" + key;*/
        key=KeyBuilder.buildLockKey(lockType, key);
        try {
            logger.info("LockType : "+lockType + " key : " + key+" unlock");
            tx.del(key);
            List<Object> result = tx.exec();

            if (result == null || result.isEmpty()) {
                return false;
            }
        } catch (Exception e) {
            logger.info("LockType : "+lockType + " key : " + key+"  unlock fail");
            return false;
        } finally {
//            RedisUtil.returnJedis(jedis);
        }
        return true;
    }

    public enum LockType {
        recharge(1, "充值"), 
        refund(2, "退款"), 
        withdraw(3, "提现"), 
        consume(4, "消费"), 
        finish(5, "结算"),
        order(6, "订单"),
        ordercheckAndAutoClose(7, "订单自动关闭"),
        orderpay(8, "订单支付"),
    	outPatientOrderCancel(9,"门诊取消订单"),
    	customPatientMessage(10,"客服患者消息"),
    	messageReplyCount(11,"客服患者消息"),
    	neworder(12,"创建订单");

        private int index;

        private String title;

        private LockType(int index, String title) {
            this.index = index;
            this.title = title;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

    }

}
