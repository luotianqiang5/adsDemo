package com.common.android.iap_amazon;

/*
 *  商品类型。MANAGED表示这件商品受google play管理，用户购买后，应用从手机中删除，再次安装后所购买的商品能重新恢复
 *  UNMANAGED表示这件商品不受google play管理，用户购买后如果删除 应用，所购买的商品不能再恢复，一般是指消费类商品。比如生命药剂之类
 *  SUBSCRIPTION表示订阅类商品，比如书刊、杂志等【目前暂不支持订阅类商品】
 *  
 */
public enum Managed {
	MANAGED, UNMANAGED, SUBSCRIPTION
}
