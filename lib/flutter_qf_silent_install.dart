import 'dart:async';

import 'package:flutter/services.dart';

class FlutterQfSilentInstall {
  static const MethodChannel _channel =
      const MethodChannel('silentinstall');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
