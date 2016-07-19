# MyApp
v2.0.0

库合集，常用工具类收集；自定义ui控件；

包含的三方库合集：

	//--核心组件--
    //网络组件
    compile 'com.zhy:okhttputils:2.6.2'
    //异步消息框架
    compile 'org.greenrobot:eventbus:3.0.0'
    //图片加载框架
    compile 'com.github.bumptech.glide:glide:3.7.0'
    //相册选择
    compile 'cn.finalteam:galleryfinal:1.4.8.7'

    //--UI组件--
    compile 'com.github.johnpersano:supertoasts:1.3.4@aar'
    compile 'cn.finalteam.loadingviewfinal:loading-more-view:1.0.1'
    compile 'cn.finalteam.loadingviewfinal:ultra-pull-to-refresh:1.0.1'
    compile 'com.github.chrisbanes:PhotoView:1.2.6'
    compile 'homhomlin.lib:apsts:1.4.0'
    compile 'com.github.medyo:fancybuttons:1.8.1'
    compile 'de.hdodenhof:circleimageview:2.0.0'
    //from jitpack
    compile 'com.github.lonaever:ZrcListView:1.0'
    compile 'com.github.JakeWharton:ViewPagerIndicator:2.4.1'
    compile 'com.github.jjobes:SlideDateTimePicker:v1.0.4'

（注意其中的v7，v4包都是23的，如果jar包冲突，需要排除）

	{
		exclude module: 'support-v4'
	}

### 集成
Step 1. Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}
Step 2. Add the dependency

	dependencies {
	        compile 'com.github.lonaever:MyApp:2.0.0'
	}

Sample里面目前包含一些重要功能的测试：
### Http测试
1.新闻列表获取并通过ZrcListView进行展示。

2.下载mp3文件到应用扩展存储测试。

3.下载mp3文件到sd卡，需提前申请权限

4.头像列表获取，通过UPTR来进行下拉刷新。接口采用翔天科技规范，请求相当简单。
