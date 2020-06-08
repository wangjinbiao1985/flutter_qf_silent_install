import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutterqfsilentinstall/flutterqfsilentinstall.dart';

void main() {
  const MethodChannel channel = MethodChannel('flutterqfsilentinstall');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await Flutterqfsilentinstall.platformVersion, '42');
  });
}
