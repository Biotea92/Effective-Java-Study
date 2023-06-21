package org.example.chapter02.item05.dependencyinjection;


import org.example.chapter02.item05.DefaultDictionary;

public class DictionaryFactory {
    public static DefaultDictionary get() {
        return new DefaultDictionary();
    }
}
