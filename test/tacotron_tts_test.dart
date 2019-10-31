import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:tacotron_tts/tacotron_tts.dart';

void main() {
  const MethodChannel channel = MethodChannel('tacotron_tts');

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await TacotronTts.platformVersion, '42');
  });
}
