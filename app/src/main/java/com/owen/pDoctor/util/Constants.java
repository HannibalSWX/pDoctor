package com.owen.pDoctor.util;

/**
 * 常量类 用于存放项目中用到的常量
 * 
 * @author zq
 * 
 */
public class Constants {

	public static int APP_VERSION_CODE; // 版本信息
	public static String APP_VERSION_NAME; // 版本信息
	public static String APP_DOWNLOAD_URL = ""; // 新版本路径

	// 服务器地址
	public static final String SERVER_URL = "http://api.njwebseo.com/api.php";
	
//	// 服务器地址
//	 public static final String SERVER_URL = "http://api.fmsdw.com/";

	// 获取验证码
	public static final String GETCODE_URL = "user_sendCode";

	// 用户注册
	public static final String REGIST_URL = "user_reg";

	// 用户登录
	public static final String LOGIN_URL = "user_login";

	// 修改密码
	public static final String CHANGEPSW_URL = "user_resetPwd";
	
	// 修改资料
	public static final String MODIFYDATA_URL = "user_modifyData";
	
	// 退出登录
	public static final String LOGOUT_URL = "user_logout";
		
	// 忘记密码
	public static final String FORGOTPSW_URL = "CheckPwdServiceInterface";
	
	// 关于我们
	public static final String ABOUT_US = "baseData_aboutUs";
	
	// 用户反馈
	public static final String FEEDBACK = "baseData_feedback";

	// 忘记密码提交
	public static final String FORGOTPSW_SUBMIT_URL = "BackPwdServiceInterface";

	// 首页广告
	public static final String ADS_HOME_URL = "baseData_stat";
	
	// 首页搜索
	public static final String HOME_SEARCH_URL = "baseData_search";
	
	// 首页商品分类
	public static final String HOME_CATEGORY_URL = "baseData_listCategory";
	
	// 首页热门商品
	public static final String HOME_HEAT_URL = "product_list";
	
	// 商品详情
	public static final String HOME_DETAIL_URL = "product_view";

	// 获取客服电话
	public static final String SERVICE_PHONE = "baseData_servicePhone";
	
	// 设置客服电话
	public static String SERVICE_PHONE_NO = "service_phone_no";
		
	// 推广类型
	public static final String SPREAD_TYPE = "product_popularizeType";
		
	// 推广列表
	public static final String SPREAD_URL = "product_popularize";

	// 获取置顶价格
	public static final String TOP_PRICE_URL = "product_getAmount";
	
	// 提交订单
	public static final String ORDER_SUBMIT = "order_submit";
	
	// 付款
	public static final String ORDER_PAY = "order_pay";
	
	// 购买记录
	public static final String BUY_HIS= "baseData_buyRecord";

	// 举报
	public static final String REPORT_URL = "product_report";

	// 检查版本更新
	public static final String APP_UPDATE_URL = "baseData_update";

	// 项目文件
	public static final String BASE_DIR_PATH = "/mnt/sdcard/intelligent/";

	// 图片地址
	public static final String BASE_DIR_PIC_PATH = BASE_DIR_PATH + "pic/";

	// 语音地址
	public static final String BASE_DIR_VOICE_PATH = BASE_DIR_PATH + "voice/";

	// 添加收藏
	public static final String ADD_MY_FAVORITE = "collect_coll";
		
	// 我的收藏列表
	public static final String MY_FAVORITE_LIST = "collect_list";
	
	// 删除收藏
	public static final String DELETE_MY_FAVORITE = "collect_del";
	
	// 批量删除收藏
	public static final String DELETE_MY_FAVORITE_MULTI = "collect_delBatch";
		
	// 我的发布列表
	public static final String MY_FABU_LIST = "product_myRelease";
	
	// 我的发布修改
	public static final String MY_FABU_MODIFY = "product_modify";
	
	// 发布信息的上传图片URL
	public static final String UPLOAD_URL = "upData?action=upfile";
		
	// 发布信息和上传图片
	public static final String PHOTOUPLOAD = "product_release";

	public static final String COMMUNITY = "http://192.168.27.101:8080/zhsq/community.do";

	public static final String UPDATESTATUS = "http://192.168.27.101:8080/zhsq/updateStatus.do";

	public static final String JUDGE = "http://192.168.27.101:8080/zhsq/judge.do";

	public static final String SUCESS_CODE = "0";
	/**
	 * 图片上传成功
	 */
	public static final int SUCESS_FILEUPLOAD = 1;
	/**
	 * 图片上传失败
	 */
	public static final int FAIL_FILEUPLOAD = 2;

	public static int index = 0;
	
	public static final String SEND_BRAODCAST = "sendBraodcast";
	
	public static final String RESET_BRAODCAST = "resetBraodcast";
	
	public static final String REGSUC = "registsuccess";
	
	public static final String CHOOSE_DATE_BRAODCAST = "chooseDateBraodcast";
	
	// 选择分类后通知
	public static final String CHOOSE_BRAODCAST = "chooseBraodcast";

	// 数据库名称
	public static final String DATABASE_NAME = "electrictoy.db";
	// 数据库版本号
	public static final int DATABASE_VERSION = 1;
	public static final String PUBLIC_DBCREATE = "public_dbcreate";
	public static final String DB_ISCREATED = "dbiscreated";
	// 我的收藏表名
	public static final String TABLE_MYFAVORITE = "myfavorite";
	// 搜索历史表名
	public static final String TABLE_SEARCHHISTORY = "searchhistory";
	// 我的收藏字段名
	public static final String IDS = "id";
	public static final String PRODUCT_ID = "product_id";
	public static final String PRODUCT_NAME = "name";
	public static final String PRODUCT_TIME = "time";
	public static final String PRODUCT_PRICE = "price";
	public static final String PRODUCT_ADDRESS = "address";
	public static final String PRODUCT_DETAIL = "detail";
	public static final String PRODUCT_IMAGEPATH = "image_path";
	// 搜索历史字段名
	public static final String SEARCH_CONTENT = "search_content";
}
