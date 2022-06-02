# 框架地址
全库：<https://github.com/Sunnyfor/ZyFrame>  
基础库：<https://github.com/Sunnyfor/ZyBase>  
网络库：<https://github.com/Sunnyfor/ZyHttp>  

CSDN地址：<https://blog.csdn.net/Chenyongzuo/article/details/115629366?spm=1001.2014.3001.5501>


# 框架说明
### 1. ZyFrame  
*快速开发框架，直接在线依赖即可，包含：ZyBase/ZyHttp/蓝牙/Zxing/相机相册等各种工具类*
### 2. ZyBase
*基础库框架：管理整个项目的 Activity，Fragment，持久化存储，各种方便调用的工具类*
### 3. ZyHttp
*网络请求框架：OkHttp+Kotlin协程，支持kotlin / Java语言*


# 框架使用
#### 1. 添加 jitpack 支持：项目级 build.gradle 中
```
maven { url "https://jitpack.io" }
```

#### 2. 添加 ZyFrame 框架依赖：应用级 build.gradle 中
```
implementation ‘com.github.Sunnyfor:ZyFrame:Tag’
```
androidx 包相关的依赖，框架里已集成，可以删除

![例如](https://img-blog.csdnimg.cn/20210412165351309.png)

#### 3. 修改主题样式：AndroidManifest.xml 中
```
android:theme="@style/ZyTheme"
```

##### 配置主题的原因：

1. 统一全局页面主题样式

2. 通过配置修改状态栏、标题栏、颜色、字体 等等

3. 框架BaseAcitivty初始化布局依赖以下属性来实现沉浸式效果   
      
   ```
    <item name="windowActionBar">false</item>

    <item name="windowNoTitle">true</item>
   ```


#### 4. 在Application 中初始化 ZyFrameStore： 
```
    ZyFrameStore.init(this)
```
