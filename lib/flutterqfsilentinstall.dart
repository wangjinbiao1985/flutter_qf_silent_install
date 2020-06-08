import 'dart:async';

import 'package:flutter/services.dart';

class FlutterQfSilentInstall {
  static const MethodChannel _channel =
      const MethodChannel('silent_install');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<bool> install(String path) async{
    return await _channel.invokeMethod('install', {'path': path});
  }
}
