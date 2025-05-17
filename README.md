[![](https://jitpack.io/v/hannesa2/panoramagl.svg)](https://jitpack.io/#hannesa2/panoramagl)


# PanoramaGL Android

PanoramaGL library was the first open source library in the world to see panoramic views on Android. This is a gradle build adaptation, along with other changes and updates. 
I am evolving the library to something easy to use any help is welcome.

### Video
![panoramaGL.gif](./readme/panoramaGL.gif)

### The supported features are:
* OpenGL ES 1.0 and 1.1.
* Support for spherical, cubic and cylindrical panoramic images.
* Scrolling and continuous scrolling.
* Inertia to stop continuous scrolling.
* Zoom in and zoom out (moving two fingers on the screen).
* Reset (placing three fingers on the screen or shaking the device).
* Scrolling left to right and from top to bottom using the accelerometer.
* Sensorial rotation (Only compatible for devices with Gyroscope or Accelerometer and Magnetometer).
* Full control of camera including field of view, zoom, rotation, rotation range, animations, etc.
* Hotspots with commands.
* Simple JSON protocol.
* Creation of virtual tours using the JSON protocol or with programming.
* Transitions between panoramas.
* Support for events.

## Usage

https://jitpack.io/#hannesa2/panoramagl
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

```	
dependencies {
    implementation 'com.github.hannesa2:panoramagl:$latestVersion'
}
```

Create a `PLManager`object and add the bindings to the activity lifecycle methods:
````
    private PLManager plManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        plManager = new PLManager(this);
        plManager.onCreate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        plManager.onResume();
    }

    @Override
    protected void onPause() {
        plManager.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        plManager.onDestroy();
        super.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return plManager.onTouchEvent(event);
    }
````
Next set the view, before calling `plManager.onCreate`:
````
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        plManager = new PLManager(this);
        plManager.setContentView(R.layout.activity_main);
        plManager.onCreate();
    }
````

Finally add the panorama you want, for example;
````
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        plManager = new PLManager(this);
        plManager.setContentView(R.layout.activity_main);
        plManager.onCreate();

        PLSphericalPanorama panorama = new PLSphericalPanorama();
        panorama.getCamera().lookAt(30.0f, 90.0f);

        panorama.setImage(new PLImage(PLUtils.getBitmap(this, R.raw.sighisoara_sphere), false));
        plManager.setPanorama(panorama);
    }
````
## License PanoramaGL Android
```
Copyright 2025

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
