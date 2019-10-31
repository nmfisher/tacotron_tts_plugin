class TextConverter {
  static String _pad = '_';
  static String _punctuation = '!\'(),.:;? ';
  static String _special = '-';
  static String _letters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz';

  static List<String> _symbols = [_pad,  _special] + _punctuation.split("") + _letters.split("");
  static Map<int,String> _id_to_symbol = _symbols.asMap();
  static Map<String,int> _symbol_to_id = _symbols.asMap().map((k,v) => MapEntry(v,k));

  static List<String> _clean_text(String text) {
    return text.split("");
  }

  static bool _should_keep_symbol(String s) {
    return _symbol_to_id.containsKey(s) && s != '_' && s != '~';
  }

  static Iterable<int> text_to_sequence(text) sync* {
    for (var s in _clean_text(text))  {
      if(_should_keep_symbol(s)) {
        yield _symbol_to_id[s];
      }
    }
  }
}
