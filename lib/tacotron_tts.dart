import 'dart:async';
import 'dart:convert';
import 'dart:io';
import 'package:flutter/services.dart';
import 'package:path_provider/path_provider.dart';
import 'text.dart';

class TacotronTts {
  static const MethodChannel _channel =
      const MethodChannel('tacotron_tts');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static void initialize() async {
    HttpClient client = new HttpClient();
    var _downloadData = List<int>();
    Directory appDocDir = await getApplicationDocumentsDirectory();
    // var outFile = new File(appDocDir.path + '/tacotron2_statedict.pt');

    // if(outFile.existsSync()) {
      _channel.invokeMethod("load", {"tacotron":"tacotron2_statedict.pt", "vocoder":"waveglow_256channels.pt"});
    // } else {
    //   var sink = outFile.openWrite();
    //   HttpClientRequest request = await client.getUrl(Uri.parse("http://10.0.2.2/tacotron2_statedict.pt"));
    //   HttpClientResponse response = await request.close();
    //   print("closed request");
    //   response.listen((d) {
    //     sink.write(d);
    //   },
    //     onDone: () async {
    //       await sink.flush();
    //       await sink.close();
    //       print("written data to " + outFile.toString());
    //       _channel.invokeMethod("load", {"tacotron":"tacotron2_statedict.pt", "vocoder":"waveglow_256channels.pt"});
    //     }
    //   );
    // }
    
  }

  static void speak(String text) {
    var symbols = TextConverter.text_to_sequence(text); 
    _channel.invokeMethod("speak", symbols.toList());
  }

  
}
