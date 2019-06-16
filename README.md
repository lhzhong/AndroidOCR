## 一、系统介绍
此APP是基于Tesseract开源OCR创建的一个Android OCR应用。可实现中英文文字识别，被识别的图片可从系统照相机或从系统相册中获取。系统界面如下：
![](https://github.com/lhzhong/AndroidOCR/blob/master/img/01.png)

## 二、实现的前期准备
### 1.Tesseract包的导入
因Tesseract是使用C++实现的，在Android中不能直接使用，需要封装JavaAPI才能在Android平台进行调用，github上有直接的封装集成项目TessTwo，
编译后将生成的包直接导入项目中即可使用。我是直接使用了其编译后生成的包，编译过程参考博客 
http://blog.csdn.net/duanbokan/article/details/50738711
导入的包如下：

![](https://github.com/lhzhong/AndroidOCR/blob/master/img/02.png)

### 2.数据包的导入
因为文字的识别首先有个训练的过程，训练数据可从github谷歌官方源码项目中下载。下载地址为： 
https://github.com/tesseract-ocr/tessdata 
下载后需要把数据存储到手机中，存储的路径为sdcard/tesseract/tessdata。
根据实现类的要求，程序会检测目录中是否有tessdata文件夹，不然就不能正常使用。以下截自类TessBaseAPI类下的init方法，该方法是用来进行文本训练的。

![](https://github.com/lhzhong/AndroidOCR/blob/master/img/03.png)

## 三、系统的实现
在参考网上已有程序的基础上，进行了界面的改动及功能的增补。实现过程主要依靠两部分组成，系统多媒体的调用（包括相机调用和相册调用），
以及外部导入开源包中核心类TessBaseAPI方法的调用。调用方法如下：
![](https://github.com/lhzhong/AndroidOCR/blob/master/img/04.png)

我把整个文字识别过程集成到doOcr方法中来实现。
其次，为了提高识别率，无论是相机拍摄的还是从相册选的照片，我都加入了裁剪，以便更好的对识别区域文字进行识别，提高了识别准群率。

## 四、效果展示
### 1.英文识别
![](https://github.com/lhzhong/AndroidOCR/blob/master/img/05.png)
### 2.中文识别
![](https://github.com/lhzhong/AndroidOCR/blob/master/img/06.png)


