#### 1. 添加 jitpack 支持：项目级 build.gradle 中
```
maven { url "https://jitpack.io" }
```

#### 2. 添加 ZyFrame 框架依赖：应用级 build.gradle 中
```
implementation  com.github.Sunnyfor:ZyFrame:Tag
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
