package com.avinium.tacotron_tts;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.List;

/** TacotronTtsPlugin */
public class TacotronTtsPlugin implements MethodCallHandler {

  private Module _tacotron;
  private Module _vocoder;
  private Context _context;

  TacotronTtsPlugin(Context context) {
    _context = context;
  }

  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "tacotron_tts");
    channel.setMethodCallHandler(new TacotronTtsPlugin(registrar.activeContext()));
  }

  public String assetFilePath(String assetName) {
    
    File[] files = new File("/data/user/0/com.avinium.tacotron_tts_example/app_flutter").listFiles();

    for (int i = 0; i < files.length; i++) {
      if (files[i].isFile()) {
        System.out.println("File " + files[i].getName());
      } else if (files[i].isDirectory()) {
        System.out.println("Directory " + files[i].getName());
      }
    }
    System.out.println("Looking for asset : " + assetName);

    File file = new File("/data/user/0/com.avinium.tacotron_tts_example/app_flutter", assetName);
    if (file.exists()) {
      System.out.println("Got asset : " + file.toString());
      if(file.length() > 0) 
        return file.getAbsolutePath();
      else
        System.out.println("Empty file");
    }

    AssetManager am = _context.getAssets();

    // try {
    //   for(String item : am.list("flutter_assets")) {
    //     System.out.println(item);
    //   }
    // } catch(IOException e) {
    //   System.out.println("IOException");
    // }

    try (InputStream is = am.open(assetName)) {
      try (OutputStream os = new FileOutputStream(file)) {
        byte[] buffer = new byte[4 * 1024];
        int read;
        while ((read = is.read(buffer)) != -1) {
          os.write(buffer, 0, read);
        }
        os.flush();
      }
      return file.getAbsolutePath();
    } catch (IOException e) {
      //Log.e(Constants.TAG, "Error process asset " + assetName + " to file path");
    }
    return null;
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else if (call.method.equals("load")) {
      System.out.println("Checking for tacotron model at " + (String)call.argument("tacotron"));
      //String tacotronPath = "/data/user/0/com.avinium.tacotron_tts_example/app_flutter/tacotron2_statedict_traced.pt"; //assetFilePath((String)call.argument("tacotron"));
      String tacotronPath = "/data/user/0/com.avinium.tacotron_tts_example/app_flutter/wav2vec_traced.pt"; //assetFilePath((String)call.argument("tacotron"));
      if(tacotronPath == null) {
        String message = "Tacotron model file not found at " + (String)call.argument("tacotron");
        System.out.println(message);
        result.error("ERROR", message, null);
        return;
      }

      String message = "Loading tacotron model @ " + tacotronPath;

      System.out.println(message);
      
      _tacotron = Module.load(tacotronPath);
      // String vocoderPath = "/data/user/0/com.avinium.tacotron_tts_example/app_flutter/waveglow_256channels_ljs_v3.pt"; 
      // _vocoder = Module.load(vocoderPath);
      // System.out.println("Loading vocoder model at " + (String)call.argument("vocoder"));
    } else if (call.method.equals("speak")) {
      // List<Integer> indicesList = (List<Integer>)call.arguments;
      // long[] indices = new long[indicesList.size()];
      
      // for(int i = 0; i < indicesList.size(); i++)
      //   indices[i] = (long)indicesList.get(i);
      
      float[] indices = new float[10000];
      System.out.println("melOutputs.toString()");

      final long[] shape = new long[]{1, indices.length};
      final Tensor inputTensor = Tensor.fromBlob(indices, shape);
      System.out.println("got input tensor");
      IValue melOutputs = _tacotron.forward(IValue.from(inputTensor));//.toTuple();
      System.out.println("ran forwarrd");
      System.out.println(melOutputs.toString());
      // Tensor audioOutput = _vocoder.runMethod("inference", melOutputs[1]).toTensor();
      // float[] scores = audioOutput.getDataAsFloatArray();
      // result.success(scores);
    } 
    else {
      result.notImplemented();
    }
  }
}
