package org.example.chapter02.item05.factorymethod;


import org.example.chapter02.item05.DefaultDictionary;
import org.example.chapter02.item05.Dictionary;

public class DefaultDictionaryFactory implements DictionaryFactory {
    @Override
    public Dictionary getDictionary() {
        return new DefaultDictionary();
    }
}
