<div align="center">
<h1>SpotDL Android</h1>
</div>
<div align="center">
<h4>A SpotDL (python library) Android wrapper built with Kotlin</h4>
</div>

## 📣 DOCS IN PROGRESS!!

## 🎉 ANNOUNCEMENT: FIRST RELEASE
The library has came out, go to the releases page and see!

[![](https://jitpack.io/v/BobbyESP/spotdl-android.svg)](https://jitpack.io/#BobbyESP/spotdl-android)

<div align="center"> 
<h2>🔨 HOW IT WORKS</h2>
</div>

## Installation

**In Gradle:**
- **Step 1:** Add the Jitpack repository to your build.gradle file at the project level.
```gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

- **Step 2:** Add the two library dependencies to your build.gradle file at app/module level.
```gradle
dependencies {
  implementation "com.github.BobbyESP.spotdl-android:spotdl-android-library:$spotdllib_version"  
  implementation "com.github.BobbyESP.spotdl-android:spotdl-android-ffmpeg:$spotdllib_version"
}
```
>**Note**
You can add an `extra` property in Gradle for more organization/readability. In this example, as you can see, the property for putting the version of the library in the dependency declaration is called `spotdllib_version` and it should look like this:
```gradle
 allprojects{
    extras{
      spotdllib_version = '0.1.0'
    }
  }
```
Jitpack sometimes works quite bad, so if the library downloading fails, please, change the version value to `-SNAPSHOT`

**Things that are mandatory:**

- You need to put the property `android:extractNativeLibs="true"` in your app's AndroidManifest.xml because the spotdl binary is already included in the library and Android can't open it without this permission.

## Usage

>**Note**
Python 3.8 is already bundled in the library

 - **Step 1:**
 The first thing you have to do is to initialize the library. It's highly recommended to do it in the `onCreate` function in your MainActivity or Application class.
 
 ```kotlin
 try {
  SpotDL.getInstance().init(this@App) //Replace @App by the class you're using to initiate the lib.
 } catch (e: Excpetion) {
  Log.e("SpotDLExample", "An error ocurreed while trying to initiate the library: $e")
 }
 ```
 
 
## FFMpeg

The library won't work without FFmpeg, so for using it, you have to add the second library that was told at the very top of the README

- You have to add to your dependecies declaration the next library:
```gradle
dependencies {
  implementation "com.github.BobbyESP.spotdl-android:spotdl-android-ffmpeg:$spotdllib_version"
}
```

Now, you have to add this code to the last try-catch example:
```kotlin
FFMpeg.getInstance.init(this@App) //Same as before
```

Getting a final try-catch like this:
 ```kotlin
 try {
  SpotDL.getInstance().init(this@App) //Replace @App by the class you're using to initiate the lib.
  FFMpeg.getInstance().init(this@App)
 } catch (e: Excpetion) {
  Log.e("SpotDLExample", "An error ocurreed while trying to initiate the library: $e")
 }
 ```

## 👷 CREDITS
- [SpotDL](https://github.com/spotDL) for it's amazing python library for downloading any song/album from Spotify!

- [xnetcat](https://github.com/xnetcat) for helping me understand some parts/commands of Linux (XD), fix some bugs of this wrapper and for giving us all the files and binaries needed for working!

- [JunkFood02](https://github.com/Junfood02) for helping me fix some bugs and explain me some code and functionality of the youtubedl-android library (in which this library is a little based on) and explaining me the storage framework of Android.

- [youtubedl-android](https://github.com/yausername/youtubedl-android) for some parts of the code of the library aswell as the inspiration for making this library (I really wanted to learn how to do one!)

## ⚖️ LICENSE
This project is under the MIT License, the same as SpotDL uses.
