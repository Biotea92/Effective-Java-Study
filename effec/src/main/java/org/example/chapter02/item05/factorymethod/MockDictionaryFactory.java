package org.example.chapter02.item05.factorymethod;


import org.example.chapter02.item05.Dictionary;
import org.example.chapter02.item05.MockDictionary;

public class MockDictionaryFactory implements DictionaryFactory {
    @Override
    public Dictionary getDictionary() {
        return new MockDictionary();
    }
}
