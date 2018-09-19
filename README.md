# MediaDemo
媒体交互

## 遇到主要问题

* 预览图片角度问题
* 输出图片角度问题

#### 预览图角度问题

通过`camera.setDisplayOrientation(‘角度’)`即可修复：

```
public static void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {

    // 获得相机相关信息
    Camera.CameraInfo info = new Camera.CameraInfo();// 信息载体
    Camera.getCameraInfo(cameraId, info);// 取出信息
    // 获得当前屏幕角度竖屏一般为 0
    int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
    int degrees = 0;
    switch (rotation) {
        case Surface.ROTATION_0:
            degrees = 0;
            break;
        case Surface.ROTATION_90:
            degrees = 90;
            break;
        case Surface.ROTATION_180:
            degrees = 180;
            break;
        case Surface.ROTATION_270:
            degrees = 270;
            break;
    }
    // 竖屏后置摄像头拍照，result一般为90
    int result;
    if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
        result = (info.orientation + degrees) % 360;
        result = (360 - result) % 360;// 前置补偿镜
    } else {
        result = (info.orientation - degrees + 360) % 360;
    }
    camera.setDisplayOrientation(result);
}
```

#### 输出图片角度问题

修改预览图角度并不能修改输出图的角度，同样需要设置相关属性来修复，需要用到上面方法中算出的`result`来作为参数，但这只能设置响应的属性，并不能直观上的修改好：

```
// 获得当前Camera对象的Parameters属性
Camera.Parameters params = mCamera.getParameters();
params.setRotation(result); // 来自上一方法的结果，设置输出角度
mCamera.setParameters(params);// 设置到Camera上，使之生效
```

虽然在图片的属性中设置了正确的角度，但是打开看依然存在角度不正的问题。使用该方法可以查看图片属性中的角度：

```
private int readPictureDegree(String path) {
    int degree = 0;
    try {
        ExifInterface exif = new ExifInterface(path);
        // exif.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(ExifInterface.ORIENTATION_ROTATE_90)); // 该方法用来设置相关属性
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                degree = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                degree = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                degree = 270;
                break;
            default:
                degree = 0;
                break;
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    Log.e("TAG", "图片角度：" + degree + "°");
    return degree;
}
```

<font color=red>解决办法</font>
-------
在保存之前就对其进行修改，其中的`degrees`为`setCameraDisplayOrientation()`方法中的`result`：

```
@Override
public void onPictureTaken(byte[] data, Camera camera) {

    try {
        // 获得原始位图
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        // 根据角度旋转摆正
        bitmap = getZeroDegreeBitmap(bitmap, degrees);

        // 输出文件
        File photoFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "picture.jpg");
        filePath = photoFile.getAbsolutePath();

        // 输出文件流
        FileOutputStream out = new FileOutputStream(photoFile);
        // 设置响应的输出质量 quality 100%质量输出
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        out.flush();
        out.close();
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }

    // 必须重启预览
    camera.startPreview();
}
```


## 自定义视频采集

MediaRecorder 和 Camera 彼此合作创建视频采集 Activity。
同样存在预览与输出方向的问题，预览可以通过之前的方法`camera.setDisplayOrientation(角度)`输出则需要使用`MediaRecorder`对象的`setOrientationHint(角度)`方法来修改。

_该示例中并未进行6.0版本以上的适配，包括权限的获取和文件的访问等_

**具体实现思路：**
创建 Activity 时，让它得到设备上的摄像头实例，并将摄像头显示方向予以修正；但并未修正输出方向；当 Activity 可见时，会收到 surfaceCreated()回调，这时 Camera 开始发送预览数据。
开始录制，Camera 会被解锁并交给 MediaRecorder 使用。然后 MediaRecorder 会设置进行视频采集所需的参数，如数据源和数据格式，以及时间和文件大小限制。

也可是已使用官方推荐的[Camera2](https://github.com/googlesamples/android-Camera2Video/#readme)

## 播放音效

用 SoundPool 将音频文件缓冲加载到内存中，调用`mSoundPool.play()`来响应用户操作时的快速播放。在此之前，需要先调用`mSoundPool.load()`载入音频文件，并得到一个`soundId`，在`play()`时传入。
可以使用`unload()`来移除声音池中的声音。
使用完 SoundPool 之后，需要调用`release()`来释放资源
