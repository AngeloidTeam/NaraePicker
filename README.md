# NaraePicker

## Usage

```
Intent intent = new Intent(this, AlbumActivity.class);
intent.putExtra("limit", 4);
startActivityForResult(intent, 727272);
```

and 

```
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
  if (requestCode == 727272 && resultCode == RESULT_OK && data != null) {
    ArrayList<String> images = data.getStringArrayListExtra("images");
  }
}
```

so Easy to Use!

## License

This source follows [MIT License](https://github.com/WindSekirun/NaraePicker/blob/master/license.md)

## Version

**v 1.0.0 (2015.07.18)**

1. Initial Commit
