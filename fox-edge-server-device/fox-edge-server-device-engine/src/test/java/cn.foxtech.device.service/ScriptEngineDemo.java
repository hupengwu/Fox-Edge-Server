/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ScriptEngineDemo {
    public static void main(String[] args) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        try {
            engine.eval("function greetEx(name) { return greet(name) + '1'; }");
            engine.eval("function greet(name) { return 'Hello, ' + name + '!'; }");

            String name = "Alice";
            String greeting1 = (String) engine.eval("greetEx('" + name + "');");

            for (int k = 0; k < 100000000; k++) {
                String greeting = "";
                for (int i = 0; i < 100000; i++) {
                    engine.eval("function greet(name) { return 'Hello, ' + name + '!'; }");
                    greeting = (String) engine.eval("greet('" + name + "');");

                }
                System.out.println(greeting + ":" + k);
            }


        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }
}

