# NaraePicker
Easy Choose Picture Activity, use Material Design! 

## Usage

```
Intent intent = new Intent(this, AlbumActivity.class);
intent.putExtra("limit", 4);
startActivityForResult(intent, 72);
```

and 

```
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
  if (requestCode == 72 && data != null) {
    ArrayList<String> images = data.getStringArrayListExtra("images");
  }
}
```

so Easy to Use!

## ScreenShots
![ScreenShot1](http://i.imgur.com/8d9EM68m.png) | ![ScreenShot2](http://i.imgur.com/K0PIcBsm.png)

## License

This source follows [MIT License](https://github.com/WindSekirun/NaraePicker/blob/master/license.md)

## Version

**v 1.0.0 (2015.07.18)**

1. Initial Commit

## 다른 나래 라이브러리
* **[NaraePreference](https://github.com/WindSekirun/NaraePreference)** - SharedPreference Wrapper
* **[NaraeAsynchronous](http://www.windsekirun.wo.tc/NaraeAsynchronous)** - Thread Executor
* **[NaraePicker](http://www.windsekirun.wo.tc/NaraePicker)** - Multi Image Select
* **[NaraeResizer](https://github.com/WindSekirun/NaraeResizer)** - Bitmap Resizer
* **[NaraeTextView](http://www.windsekirun.wo.tc/NaraeTextView)** - Clickable TextView
