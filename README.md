# MyApp
v2.0.0

库合集；常用工具类收集；自定义ui控件；

包含的三方库合集：

	//--核心组件--
    //http
    compile 'com.squareup.okhttp3:okhttp:3.4.1'
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
	        compile 'com.github.lonaever:MyApp:2.0.3'
	}

Sample里面目前包含一些重要功能的测试：
### Http测试
1.新闻列表获取并通过ZrcListView进行展示。

2.下载mp3文件到应用扩展存储测试。

3.下载mp3文件到sd卡，需提前申请权限

4.头像列表获取，通过UPTR来进行下拉刷新。接口采用翔天科技规范，请求相当简单。


### 拍照相册测试

#####（都是需要提前进行权限申请的）

1.拍照或者相册选取一张图片，然后自动进入剪裁，适合用于头像。

	new GalleryHelper().openSingleCrop(context, 555, 555, new GalleryHelper.OnPickPhotoCallback() {
            @Override
            public void onPickSucc(PhotoInfo photoInfo) {
                headImg = new ImageData(photoInfo.getPhotoPath());
                headImg.displayImage(context, iv_pic);
            }

            @Override
            public void onPickFail(String errorMsg) {
                showErrorTip(errorMsg);
            }
        });


剪裁后返回的图片文件路径就是设定尺寸的图片大小。原图仍然保存在默认设置的app DATA里面。

2.拍照或者选多个图片，适合发布一组图片。
	
	new GalleryHelper().openMuti(context, 8, new GalleryHelper.OnPickMutiPhotoCallback() {
            @Override
            public void onPickSucc(List<PhotoInfo> photoInfoList) {
                for (PhotoInfo pi:photoInfoList) {
                    ImageData img=new ImageData(pi.getPhotoPath(),600,600, ImageData.ScaleType.SCALE_TYPE_FIT_CENTER);
                    imglist.add(img);
                }
                photoGridView.reload();
            }

            @Override
            public void onPickFail(String errorMsg) {
                showErrorTip(errorMsg);
            }
        });
        
 这里需要注意，这里返回的图片都是未经过处理的。图片的压缩大小是需要后期来进行的。我将压缩方法封装在了ImageData里面，在显示的同时进行压缩。压缩后的文件保存在App ext DATA里面。
 
### Eventbus在ViewPager中的使用

这里只是进行了一个简单试验，用于在activity中通知，ViewPager中的Fragment进行更新UI。

### 自定义字体测试

这里使用了三方库来方便进行UI中的字体改变：

	compile 'uk.co.chrisjenx:calligraphy:2.2.0'
	
文档中有好多的方式进行设置，我测试了两种，通过xml直接写FontPath配置字体文件。这种方式最简单，只是有点傻。另一种比较合理，就是在sytle中写一个TextAppearence的样式，然后在需要的控件上直接加上就可。

1、通过fontpath直接配置：

	<TextView
            android:id="@+id/tv_text"
            fontPath="MILT_RG.ttf"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="1.测试xml中直接配置字体文件"
            android:textColor="@color/text_black"
            android:textSize="20sp"
            tools:ignore="MissingPrefix" />
            
2、通过TextAppearence的Style配置

	<TextView
            android:id="@+id/tv_text3"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="40dp"
            android:text="3.测试通过TextAppearence配置小米兰亭字体文件"
            android:textAppearance="@style/TextAppearance.MILT"
            android:textColor="@color/text_black"
            android:textSize="20sp" />
            
3、代码中也测试了通过代码如何改变字体，这个就相对更不方便了。

4、配置actionbar中的字体，需要在style中进行，具体看style的xml配置文件。

### sqlite操作

##### 集成的是从xutils3上提取的db操作。

Sample中，只简单测试了下配置初始化，保存表，和查询。

model里面只能通过注解方式，制定表名。字段需要通过@Column来指定，否则字段会被忽略。是否生成set，get方法，我觉得不一定。

	DbManager.DaoConfig dbconfig = new DbManager.DaoConfig().setDbName("test.db").setDbDir(new File(FileUtil.getAppExtFilesPath()));
    DbManager dbManager;
    private void initDB() {
        dbManager = DbManagerImpl.getInstance(dbconfig);
    }            
 
 