# PanoramaGL Android

PanoramaGL library was the first open source library in the world to see panoramic views on Android. This is a gradle build adaptation, along with other changes and updates. I am evolving the library to something easy to use any help is welcome. The native code is used as a static library. 

Temporal docs about panorama object and json protocol the [original google code wiki](https://code.google.com/p/panoramagl-android/wiki/UserGuide#Introduction) and [here the new usage](#usage)

The supported features in version 0.2 beta are:
* SDK 2.0 to 5.+.
* Architectures ARM, x86 and MIPS.
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
*

##Usage

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




##ToDo
* Separate View, ViewController, TouchController and SensorController

##Done
* Activity removed as the base extended class. Now it can be used with AppCompatActivity or others.
